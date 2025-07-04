package com.orta.gos.services;

import static com.orta.gos.services.assistance.ProcessorHanlingAssistance.callNextStep;
import static com.orta.gos.services.assistance.ProcessorHanlingAssistance.recoverFromPlatformError;
import static com.orta.gos.services.assistance.ProcessorHanlingAssistance.updateMessage;

import com.orta.gos.model.PlatformMessage;
import com.orta.gos.model.PlatformResponse;
import com.orta.gos.model.ProcessInput;
import com.orta.gos.model.InboundGrpc.InboundImplBase;
import com.orta.gos.model.MutinyProcessGrpc.MutinyProcessStub;
import com.orta.gos.model.utils.PlatformMessageUtils;
import com.orta.gos.services.assistance.Additional;
import com.orta.gos.services.errors.ProcessErrorHandler;
import com.orta.gos.services.pic.ProcessInputBuilder;

import io.grpc.stub.StreamObserver;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.GrpcService;
import io.quarkus.logging.Log;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import io.vavr.collection.List;
import jakarta.inject.Inject;

@GrpcService
@RegisterForReflection
public class ProcessGrpcService extends InboundImplBase {

  @GrpcClient("processor")
  MutinyProcessStub processorStub;

  @Inject
  GrpcClientRepository clientRepository;

  @Inject
  WorkflowEnrichmentClient enrichmentClient;

  @Inject
  ProcessErrorHandler processErrorHandler;

  private static Uni<ProcessInput> uniProcessorInputPrep(final PlatformMessage message) {
    return PlatformMessageUtils.withMessage(message).currentStep()
        .mapLeft(error -> List.of(error))
        .flatMap(step -> ProcessInputBuilder.builder()
            .appendAttributes(step)
            .addPayloads(step, List.ofAll(message.getPayloadsList()))
            .build())
        .fold(errors -> {
          Log.errorf("Failed to prepare ProcessInput, due to %s", errors);
          return Uni.createFrom().failure(new Exception(errors.mkString(", ")));
        }, v -> Uni.createFrom().item(v));
  }

  Uni<PlatformMessage> check(PlatformMessage request) {
    return uniProcessorInputPrep(request)
        .onItem().transformToUni(processorStub::process)
        .onItem().transform(m -> {
          Log.infof("checkpoint #1");
          return m;
        })
        .onItem().transform(updateMessage(request))
        .onItem().transform(Additional.updateWorkflow(enrichmentClient))
        .onItem().transform(m -> {
          Log.infof("checkpoint #2");
          return m;
        });
  }

  @Override
  public void handle(PlatformMessage request, StreamObserver<PlatformResponse> responseObserver) {

    check(request)
        .onItem().invoke(callNextStep(clientRepository))
        .onFailure().recoverWithItem(recoverFromPlatformError(processErrorHandler, request))
        .onCancellation().invoke(() -> Log.warnf("Request %s was cancelled by the client.", request.getId()))
        .subscribe()
        .with(platformMessage -> {
          Log.infof("platform received %s", platformMessage.getId());
          responseObserver.onNext(generateResponse(platformMessage));
          responseObserver.onCompleted();
          Log.infof("platform marked grpc as completed: %s", platformMessage.getId());
        }, failure -> {
          Log.errorf("Failed to process, reason: %s", failure.getMessage());
          responseObserver.onError(failure);
        });
    Log.info("=----=");
  }

  public static PlatformResponse generateResponse(PlatformMessage request) {
    return PlatformResponse.newBuilder().setId(request.getId()).setStatus(200).build();
  }

}

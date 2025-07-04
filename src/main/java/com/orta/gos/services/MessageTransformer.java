package com.orta.gos.services;

import static com.orta.gos.services.assistance.ProcessorHanlingAssistance.updateMessage;

import com.orta.gos.model.PlatformMessage;
import com.orta.gos.model.ProcessInput;
import com.orta.gos.model.MutinyProcessGrpc.MutinyProcessStub;
import com.orta.gos.model.utils.PlatformMessageUtils;
import com.orta.gos.services.assistance.Additional;
import com.orta.gos.services.pic.ProcessInputBuilder;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vavr.collection.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class MessageTransformer {

  @GrpcClient("processor")
  MutinyProcessStub processorStub;

  @Inject
  WorkflowEnrichmentClient enrichmentClient;

  public Uni<PlatformMessage> check(PlatformMessage request) {
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

  private static Uni<ProcessInput> uniProcessorInputPrep(final PlatformMessage message) {
    return PlatformMessageUtils.withMessage(message).currentStep()
        .mapLeft(error -> List.<String>of(error))
        .flatMap(step -> ProcessInputBuilder.builder().appendAttributes(step)
            .addPayloads(step, List.ofAll(message.getPayloadsList()))
            .build())
        .fold(errors -> {
          Log.errorf("Failed to prepare ProcessInput, due to %s", errors);
          return Uni.createFrom().failure(new Exception(errors.mkString(", ")));
        }, v -> Uni.createFrom().item(v));
  }
}

package com.orta.gos.services.assistance;

import java.util.UUID;
import java.util.function.Consumer;

import com.orta.gos.model.Payload;
import com.orta.gos.model.PlatformMessage;
import com.orta.gos.model.ProcessOutcome;
import com.orta.gos.model.ProcessOutcomeFailure;
import com.orta.gos.model.ProcessOutcomeSucces;
import com.orta.gos.model.StringBody;
import com.orta.gos.model.utils.PlatformMessageUtils;
import com.orta.gos.services.GrpcClientRepository;
import com.orta.gos.services.errors.ProcessErrorHandler;

import io.quarkus.logging.Log;
import io.vavr.Function1;

public class ProcessorHanlingAssistance {

  public static final Function1<ProcessOutcome, PlatformMessage> updateMessage(PlatformMessage orig) {
    return outcome -> {
      var updated = Additional.appendOutcome(orig, outcome);
      return updated;
    };
  }

  public static final Consumer<PlatformMessage> callNextStep(GrpcClientRepository clientRepo) {
    return message -> {
      Log.infof("callNextStep() using tracker: %s", message.getWorkflowLog().getTracker());

      new PlatformMessageUtils(message).maybeCurrentAddress()
          .forEach(nextStepAddress -> clientRepo.getStub(nextStepAddress).handle(message).subscribe().with(
              nextResponse -> Log.infof("Message [%s] successfuly arrived to %s", message.getId(), nextStepAddress),
              failure -> Log.errorf("Error processing message [%s] by next step %s: %s%n", message.getId(),
                  nextStepAddress, failure.getMessage())));
    };
  }

  public static final Function1<Throwable, PlatformMessage> recoverFromPlatformError(
      ProcessErrorHandler processErrorHandler, PlatformMessage orig) {
    return failure -> {
      Payload errorPayload = Payload.newBuilder()
          .setUuid(UUID.randomUUID().toString())
          .setCreationTimestamp(System.currentTimeMillis())
          .putAttributes("errorType", failure.getClass().getSimpleName())
          .putAttributes("errorMessage", failure.getMessage())
          .setStringBody(StringBody.newBuilder()
              .setBody("An error occurred during processing: " + failure.getMessage())
              .build())
          .build();

      // Add the error payload to the PlatformMessage
      var updated = PlatformMessage.newBuilder(orig)
          .addPayloads(errorPayload)
          .build();

      processErrorHandler.storeErrorSnapshot(updated);

      return updated;
    };

  }

  public static final Function1<ProcessOutcomeSucces, PlatformMessage> updateSuccessfulOutcome(PlatformMessage orig) {
    return outcome -> PlatformMessage.newBuilder(orig)
        .putAllHeaders(outcome.getHeadersMap())
        .addPayloads(outcome.getOutputPayload())
        .build();
  }

  public static final Function1<ProcessOutcomeFailure, PlatformMessage> updateFailedOutcome(PlatformMessage orig) {
    return outcome -> {
      Log.errorf("Process failure: %s", outcome.getErrorMessage());
      return orig;
    };
  }
}

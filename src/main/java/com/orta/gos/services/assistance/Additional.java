package com.orta.gos.services.assistance;

import java.time.Duration;
import java.util.function.Predicate;

import com.orta.gos.model.Payload;
import com.orta.gos.model.PlatformMessage;
import com.orta.gos.model.PlatformWorkflow;
import com.orta.gos.model.ProcessOutcome;
import com.orta.gos.model.ProcessOutcomeSucces;
import com.orta.gos.model.utils.PlatformWorkflowUtils;
import com.orta.gos.services.WorkflowEnrichmentClient;

import io.quarkus.logging.Log;
import io.vavr.Function1;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;

public class Additional {

  public static PlatformMessage appendOutcome(PlatformMessage message, ProcessOutcome outcome) {
    Try<PlatformMessage> a = Try.of(() -> {
      // Update tracker in workflow log
      var newTracker = PlatformWorkflowUtils.updateTracker(message.getWorkflowLog(), outcome);
      var newWorkflow = PlatformWorkflow.newBuilder(message.getWorkflowLog())
          .setTracker(newTracker)
          .build();

      // Start building new message with updated workflow
      var builder = PlatformMessage.newBuilder(message)
          .setWorkflowLog(newWorkflow);

      // Handle payloads and headers based on outcome type
      switch (outcome.getOutcomeCase()) {
        case SUCCESS:
          return handleSuccess(builder, outcome.getSuccess());
        case FAILURE:
          Log.errorf("ProcessOutcome returned with error: %s", outcome.getFailure().getErrorMessage());
          return builder.build(); // No changes to payloads or headers
        case OUTCOME_NOT_SET:
        default:
          throw new IllegalStateException("Outcome was not set");
      }
    });

    Log.infof("appending outcome to message: status: %s", (a.isFailure() ? "FAILED" : "SUCCESS"));
    return a.getOrElse(message);
  }

  private static PlatformMessage handleSuccess(PlatformMessage.Builder builder, ProcessOutcomeSucces success) {
    // Add payloads if present
    successPayloads.apply(success)
        .forEach(builder::addPayloads);

    // Add only new headers that don't exist in the message
    var existingHeaders = builder.getHeadersMap();
    successHeaders.apply(success)
        .filterKeys(key -> !existingHeaders.containsKey(key))
        .forEach(builder::putHeaders);

    return builder.build();
  }

  private static final Function1<ProcessOutcomeSucces, List<Payload>> successPayloads = po -> po.hasOutputPayload()
      ? List.of(po.getOutputPayload())
      : List.empty();

  private static final Function1<ProcessOutcomeSucces, Map<String, String>> successHeaders = po -> HashMap
      .ofAll(po.getHeadersMap());

  private static Predicate<PlatformMessage> endOfWorkflow() {
    return message -> {
      var currentStep = message.getWorkflowLog().getTracker().getCurrentStep();
      var termination = message.getWorkflowLog().getTracker().getTermination();

      return currentStep == termination;
    };
  }

  public static Function1<PlatformMessage, PlatformMessage> updateWorkflow(final WorkflowEnrichmentClient client) {
    return message -> endOfWorkflow().test(message)
        ? client.appendRules(message, Duration.ofMillis(500))
        : message;
  }

}

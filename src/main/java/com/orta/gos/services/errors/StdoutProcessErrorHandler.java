package com.orta.gos.services.errors;

import com.orta.gos.model.PlatformMessage;

import io.quarkus.logging.Log;
import io.vavr.collection.List;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StdoutProcessErrorHandler implements ProcessErrorHandler {

  @Override
  public void storeErrorSnapshot(PlatformMessage message) {
    Log.errorf("ERROR:");
    Log.errorf("selected workflows: %s", message.getWorkflowLog().getSelectedWorkflowsList());
    List.ofAll(message.getWorkflowLog().getStepsList()).forEach(step -> {
      Log.errorf("\tstep %s [# of indicators %d]", step.getName(), step.getIndicatorsCount());
      step.getIndicatorsList().forEach(indicator -> {
        Log.errorf("\t\t[%s, %s]", indicator.getType(), indicator.getEdge());
      });
    });
    Log.errorf("---");

    var tracker = message.getWorkflowLog().getTracker();

    var currentStep = tracker.getCurrentStep();
    var currentBlock = tracker.getCurrentBlock();

    Log.errorf("\tstep: %d", currentStep);

    Log.errorf("\ttracker");
    if (currentBlock == null) {
      Log.errorf("\t\tblock is null");
    } else {
      Log.errorf("\t\tblock number ? %d", (currentBlock.getNumber()));
      Log.errorf("\t\tblock name ? %s", (currentBlock.name()));
    }

    Log.errorf("\t\tranges:");
    tracker.getRangesMap().entrySet().forEach(entry -> {
      Log.errorf("\t\t\t%d -> (%d)[%d -> %d]", entry.getKey(),
          entry.getValue().getTypeValue(), entry.getValue().getStartIdx(), entry.getValue().getEndIdx());
    });
    // Log.errorf("tracker: %s", tracker);

    Log.errorf("====");
    // Log.errorf("identified error, log snapshot: %s", message);
  }

}

package com.orta.gos.services.pic;

import com.orta.gos.model.Payload;
import com.orta.gos.model.rules.Step;

import io.vavr.collection.List;

public record LastPayloadsSelection(Step step) implements PayloadsSelection {

  @Override
  public PayloadSelectionOutcome selectPayloads(final List<Payload> payloads) {
    return payloads.lastOption()
        .fold(() -> PayloadSelectionOutcome.empty(), PayloadSelectionOutcome::success);
  }

}

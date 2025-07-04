package com.orta.gos.services.pic;

import com.orta.gos.model.Payload;
import com.orta.gos.model.rules.Step;

import io.vavr.collection.List;

public record EmptyPayloadsSelection(Step step) implements PayloadsSelection {

  @Override
  public PayloadSelectionOutcome selectPayloads(List<Payload> payloads) {
    return PayloadSelectionOutcome.empty();
  }

}

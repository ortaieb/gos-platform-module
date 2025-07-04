package com.orta.gos.services.pic;

import com.orta.gos.model.Payload;
import com.orta.gos.model.rules.Step;

import io.vavr.collection.List;

public interface PayloadsSelection {

  Step step();

  PayloadSelectionOutcome selectPayloads(final List<Payload> payloads);

}

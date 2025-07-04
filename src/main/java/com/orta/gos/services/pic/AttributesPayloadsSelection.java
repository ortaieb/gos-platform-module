package com.orta.gos.services.pic;

import com.orta.gos.model.Payload;
import com.orta.gos.model.rules.PayloadAttributes;
import com.orta.gos.model.rules.Step;

import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.control.Either;

public record AttributesPayloadsSelection(Step step) implements PayloadsSelection {

  private static final Function1<List<Either<String, Payload>>, List<String>> left = list -> list.map(Either::getLeft);
  private static final Function1<List<Either<String, Payload>>, List<Payload>> right = list -> list.map(Either::get);

  private static Either<List<String>, List<Payload>> sequence(final List<Either<String, Payload>> payloadsList) {
    var partitionedByIsLeft = payloadsList.partition(Either::isLeft);
    return partitionedByIsLeft._1().nonEmpty()
        ? Either.left(left.apply(partitionedByIsLeft._1()))
        : Either.right(right.apply(partitionedByIsLeft._2()));

  }

  @Override
  public PayloadSelectionOutcome selectPayloads(List<Payload> payloads) {
    var picSelectors = payloadDescriptors(step);
    var payloadLookup = new PayloadLookup(payloads);
    var selected = picSelectors.map(payloadLookup::locateLastPayloadByAttributes);
    return sequence(selected).fold(PayloadSelectionOutcome::failure, PayloadSelectionOutcome::success);
  }

  private static List<PayloadAttributes> payloadDescriptors(final Step step) {
    return step == null
        ? List.empty()
        : List.ofAll(step.getPicSelector().getByAttributes().getPayloadDescList());
  }

}

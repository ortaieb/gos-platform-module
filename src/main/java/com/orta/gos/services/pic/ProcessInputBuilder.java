package com.orta.gos.services.pic;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import com.orta.gos.model.Payload;
import com.orta.gos.model.ProcessInput;
import com.orta.gos.model.rules.Step;

import io.vavr.collection.List;
import io.vavr.control.Either;

public class ProcessInputBuilder {

  private ProcessInput.Builder builder;
  private List<String> errorMessages;

  private ProcessInputBuilder(ProcessInput.Builder builder) {
    this.builder = builder;
    this.errorMessages = List.empty();
  }

  public static ProcessInputBuilder builder() {
    return new ProcessInputBuilder(ProcessInput.newBuilder());
  }

  public ProcessInputBuilder appendAttributes(final Step step) {
    builder.putAllAttributes(step.getAttributesMap());
    return this;
  }

  public ProcessInputBuilder addPayloads(final Step step, final List<Payload> payloads) {
    PayloadSelectionOutcome outcome = SelectorFactory.byStep(step).selectPayloads(payloads);

    if (outcome.hasErrors()) {
      errorMessages = errorMessages.appendAll(outcome.errors().toJavaList());
    } else {
      builder.addAllPayloads(outcome.payloads().toJavaList());
    }

    return this;
  }

  public Either<List<String>, ProcessInput> build() {
    return this.errorMessages.isEmpty()
        ? right(this.builder.build())
        : left(this.errorMessages);
  }
}

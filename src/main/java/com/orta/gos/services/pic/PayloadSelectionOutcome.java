package com.orta.gos.services.pic;

import com.orta.gos.model.Payload;

import io.vavr.collection.List;

public record PayloadSelectionOutcome(List<String> errors, List<Payload> payloads) {

  public static PayloadSelectionOutcome success(List<Payload> payloads) {
    return new PayloadSelectionOutcome(List.empty(), payloads);
  }

  public static PayloadSelectionOutcome success(Payload... payloads) {
    return new PayloadSelectionOutcome(List.empty(), List.of(payloads));
  }

  public static PayloadSelectionOutcome success(Payload payload) {
    return new PayloadSelectionOutcome(List.empty(), List.of(payload));
  }

  public static PayloadSelectionOutcome failure(List<String> errorMessages) {
    return new PayloadSelectionOutcome(errorMessages, List.empty());
  }

  public static PayloadSelectionOutcome failure(String... errorMessages) {
    return new PayloadSelectionOutcome(List.of(errorMessages), List.empty());
  }

  public static PayloadSelectionOutcome failure(String errorMessage) {
    return new PayloadSelectionOutcome(List.of(errorMessage), List.empty());
  }

  public static PayloadSelectionOutcome empty() {
    return new PayloadSelectionOutcome(List.empty(), List.empty());
  }

  public Boolean hasErrors() {
    return errors().nonEmpty();
  }

}

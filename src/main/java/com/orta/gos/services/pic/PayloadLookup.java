package com.orta.gos.services.pic;

import java.util.function.Predicate;

import com.orta.gos.model.Payload;
import com.orta.gos.model.rules.PayloadAttributes;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Either;

public record PayloadLookup(List<Payload> payloads) {

  public PayloadLookup(Payload... payloads) {
    this(List.of(payloads));
  }

  public Either<String, Payload> locateLastPayloadByAttributes(final PayloadAttributes payloadAttributes) {
    return payloads
        .findLast(allAttributes(HashMap.ofAll(payloadAttributes.getPicAttributesMap())))
        .toEither(() -> payloadAttributes.getPicName());
  }

  public static final Predicate<Tuple2<String, String>> entryExistsInMap(final Map<String, String> map) {
    return tuple -> map.get(tuple._1()).contains(tuple._2());
  }

  public static Predicate<Payload> allAttributes(final Map<String, String> expectedAttributes) {

    return payload -> {
      var map = HashMap.ofAll(payload.getAttributesMap());
      return expectedAttributes.forAll(entryExistsInMap(map));
    };
  }

}

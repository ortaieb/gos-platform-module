package com.orta.gos.services.pic;

import static com.orta.gos.services.pic.PayloadLookup.allAttributes;
import static com.orta.gos.services.pic.PayloadLookup.entryExistsInMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.vavr.api.VavrAssertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.orta.gos.model.EmptyBody;
import com.orta.gos.model.Payload;
import com.orta.gos.model.rules.PayloadAttributes;
import com.orta.gos.services.pic.PayloadLookup;

import io.vavr.Tuple;
import io.vavr.collection.HashMap;

@DisplayName("PayloadLookup")
public class PayloadLookupTest {

  @Nested
  @DisplayName("locateByAttributes")
  public class LocateByAttributesTest {

    static final Payload TEST_PAYLOAD_1 = Payload.newBuilder()
        .setEmptyBody(EmptyBody.newBuilder())
        .setUuid("test-1")
        .putAllAttributes(HashMap.of("a", "A", "b", "B", "c", "C").toJavaMap())
        .build();

    static final Payload TEST_PAYLOAD_2 = Payload.newBuilder()
        .setEmptyBody(EmptyBody.newBuilder())
        .setUuid("test-2")
        .putAllAttributes(HashMap.of("a", "A", "c", "C").toJavaMap())
        .build();

    static final Payload TEST_PAYLOAD_3 = Payload.newBuilder()
        .setEmptyBody(EmptyBody.newBuilder())
        .setUuid("test-3")
        .putAllAttributes(HashMap.of("a", "AAA", "c", "C").toJavaMap())
        .build();

    static final Payload TEST_PAYLOAD_4 = Payload.newBuilder()
        .setEmptyBody(EmptyBody.newBuilder())
        .setUuid("test-4")
        .putAllAttributes(HashMap.of("a", "A", "c", "C").toJavaMap())
        .build();

    static final PayloadLookup payloadLookup = new PayloadLookup(
        TEST_PAYLOAD_1,
        TEST_PAYLOAD_2,
        TEST_PAYLOAD_3,
        TEST_PAYLOAD_4);

    @Test
    @DisplayName("should return none if no payload satisfy expected attributes")
    void test0() {
      var expected = PayloadAttributes.newBuilder()
          .setPicName("test0")
          .putAllPicAttributes(HashMap.of("a", "A", "d", "D").toJavaMap())
          .build();
      assertThat(payloadLookup.locateLastPayloadByAttributes(expected)).isLeft().containsOnLeft("test0");
    }

    @Test
    @DisplayName("should return last payload with the relevant values")
    void test1() {
      var expected = PayloadAttributes.newBuilder()
          .setPicName("test1")
          .putAllPicAttributes(HashMap.of("a", "A", "c", "C").toJavaMap())
          .build();
      assertThat(payloadLookup.locateLastPayloadByAttributes(expected)).containsOnRight(TEST_PAYLOAD_4);
    }

    @Test
    @DisplayName("should return last payload with the relevant values")
    void test2() {
      var expected = PayloadAttributes.newBuilder()
          .setPicName("test2")
          .putAllPicAttributes(HashMap.of("a", "A", "c", "C", "b", "B").toJavaMap())
          .build();

      assertThat(payloadLookup.locateLastPayloadByAttributes(expected)).containsOnRight(TEST_PAYLOAD_1);
    }

  }

  @Nested
  @DisplayName("allAttributes")
  public class AllAttributesTest {

    static final Payload TEST_PAYLOAD = Payload.newBuilder()
        .setEmptyBody(EmptyBody.newBuilder())
        .setUuid("test-0")
        .putAttributes("a", "A")
        .putAttributes("b", "B")
        .putAttributes("c", "C")
        .build();

    @Test
    @DisplayName("should return true if all expected attributes fulfilled")
    void test0() {
      assertThat(allAttributes(HashMap.of("a", "A", "c", "C")).test(TEST_PAYLOAD)).isTrue();
    }

    @Test
    @DisplayName("should return false if some of the expected attributes mismatch")
    void test1() {
      assertThat(allAttributes(HashMap.of("a", "A", "c", "CC")).test(TEST_PAYLOAD)).isFalse();
    }

    @Test
    @DisplayName("should return false if some of the expected attributes are missing")
    void test2() {
      assertThat(allAttributes(HashMap.of("a", "A", "d", "D")).test(TEST_PAYLOAD)).isFalse();
    }

  }

  @Nested
  @DisplayName("entryExistsInMap")
  public class EntryExistsInMapTest {

    @Test
    @DisplayName("should test() true for existing entry")
    void test0() {
      var map = HashMap.of("a", "A", "b", "B");
      var entry = Tuple.of("a", "A");

      assertThat(entryExistsInMap(map).test(entry)).isTrue();
    }

    @Test
    @DisplayName("should test() false for missing entry")
    void test1() {
      var map = HashMap.of("a", "A", "b", "B");
      var entry = Tuple.of("c", "C");

      assertThat(entryExistsInMap(map).test(entry)).isFalse();
    }

    @Test
    @DisplayName("should test() false for existing key and different value")
    void test2() {
      var map = HashMap.of("a", "A", "b", "B");
      var entry = Tuple.of("a", "C");

      assertThat(entryExistsInMap(map).test(entry)).isFalse();
    }

  }

}

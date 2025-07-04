package com.orta.gos.services.pic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.orta.gos.model.EmptyBody;
import com.orta.gos.model.Payload;
import com.orta.gos.model.ProcessInput;
import com.orta.gos.model.rules.PayloadAttributes;
import com.orta.gos.model.rules.PicSelector;
import com.orta.gos.model.rules.PicSelectorAttributes;
import com.orta.gos.model.rules.Step;
import com.orta.gos.services.pic.ProcessInputBuilder;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;

import static org.assertj.vavr.api.VavrAssertions.assertThat;

@DisplayName("ProcessInputBuilder")
public class ProcessInputBuilderTest {

  private static final Map<String, String> STEP_ATTRS = HashMap.of("header1", "1", "header2", "2");
  private static final PayloadAttributes ATTR_1 = PayloadAttributes.newBuilder()
      .setPicName("attr-1")
      .putPicAttributes("a", "A")
      .putPicAttributes("b", "B")
      .build();

  private static final PayloadAttributes ATTR_2 = PayloadAttributes.newBuilder()
      .setPicName("attr-2")
      .putPicAttributes("a", "A")
      .build();

  private static final PayloadAttributes ATTR_MISSING = PayloadAttributes.newBuilder()
      .setPicName("attr-M")
      .putPicAttributes("a", "A")
      .putPicAttributes("z", "Z")
      .build();

  private static final Payload PAYLOAD1 = Payload.newBuilder()
      .setUuid("id-1")
      .setEmptyBody(EmptyBody.newBuilder())
      .putAttributes("a", "A")
      .build();

  private static final Payload PAYLOAD2 = Payload.newBuilder()
      .setUuid("id-2")
      .setEmptyBody(EmptyBody.newBuilder())
      .putAttributes("a", "A")
      .putAttributes("b", "B")
      .putAttributes("c", "C")
      .build();

  private static final Payload PAYLOAD3 = Payload.newBuilder()
      .setUuid("id-3")
      .setEmptyBody(EmptyBody.newBuilder())
      .putAttributes("a", "A")
      .putAttributes("c", "C")
      .build();

  private static final Payload PAYLOAD4 = Payload.newBuilder()
      .setUuid("id-4")
      .setEmptyBody(EmptyBody.newBuilder())
      .putAttributes("a", "A")
      .putAttributes("c", "C")
      .build();

  @Nested
  @DisplayName("build()")
  public class BuildTest {

    @Test
    @DisplayName("should build a ProcessInput")
    void test0() {

      var picSelector = PicSelector.newBuilder()
          .setByAttributes(PicSelectorAttributes.newBuilder()
              .addPayloadDesc(ATTR_1)
              .addPayloadDesc(ATTR_2))
          .build();

      var step = Step.newBuilder()
          .setName("step-1")
          .putAllAttributes(STEP_ATTRS.toJavaMap())
          .setPicSelector(picSelector)
          .build();
      var payloads = List.of(PAYLOAD1, PAYLOAD2, PAYLOAD3, PAYLOAD4);

      var input = ProcessInputBuilder.builder()
          .appendAttributes(step)
          .addPayloads(step, payloads)
          .build();

      var expected = ProcessInput.newBuilder()
          .putAttributes("header1", "1")
          .putAttributes("header2", "2")
          .addPayloads(PAYLOAD2)
          .addPayloads(PAYLOAD4)
          .build();

      assertThat(input).isRight().containsOnRight(expected);
    }

    @Test
    @DisplayName("should fail with error message with the missing payloads")
    void test1() {

      var picSelector = PicSelector.newBuilder()
          .setByAttributes(PicSelectorAttributes.newBuilder()
              .addPayloadDesc(ATTR_MISSING)
              .addPayloadDesc(ATTR_2))
          .build();

      var step = Step.newBuilder()
          .setName("step-1")
          .putAllAttributes(STEP_ATTRS.toJavaMap())
          .setPicSelector(picSelector)
          .build();
      var payloads = List.of(PAYLOAD1, PAYLOAD2, PAYLOAD3, PAYLOAD4);

      var input = ProcessInputBuilder.builder()
          .appendAttributes(step)
          .addPayloads(step, payloads)
          .build();

      assertThat(input).isLeft().containsOnLeft(List.of("attr-M"));
    }
  }

}

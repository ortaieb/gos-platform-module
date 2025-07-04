package com.orta.gos.services.pic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.orta.gos.model.Payload;
import com.orta.gos.model.rules.Step;
import com.orta.gos.services.pic.AttributesPayloadsSelection;
import com.orta.gos.services.pic.PayloadSelectionOutcome;

import io.vavr.collection.List;
import io.vavr.collection.Stream;

import static com.orta.gos.services.pic.PayloadSelectionOutcome.empty;
import static com.orta.gos.services.pic.PayloadSelectionOutcome.failure;
import static com.orta.gos.services.pic.PayloadSelectionOutcome.success;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.ANY_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.EMPTY_LIST;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.MULTI_1_LIST;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.NULL_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PAYLOAD1;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PAYLOAD2;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PAYLOAD3;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PIC_ATTR1_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PIC_ATTR2_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PIC_LAST_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.SINGLE_LIST;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AttributesPayloadsSelection")
public class AttributesPayloadsSelectionTest {

  @Nested
  @DisplayName("selectPayloads")
  public class SelectPayloadsTest {

    static Stream<Arguments> payloadsSelectionProvider() {
      return Stream.of(
          Arguments.of("null step with empty list",
              NULL_STEP, EMPTY_LIST, empty()),
          Arguments.of("no pic step with empty list",
              ANY_STEP, EMPTY_LIST, empty()),
          Arguments.of("last_step pic with empty list",
              PIC_LAST_STEP, EMPTY_LIST, empty()),
          Arguments.of("single attribute step with empty list",
              PIC_ATTR1_STEP, EMPTY_LIST, failure("p")),
          Arguments.of("complicated attributes step with empty list",
              PIC_ATTR2_STEP, EMPTY_LIST, failure("pB", "pA")),
          Arguments.of("null step with single element list",
              NULL_STEP, SINGLE_LIST, empty()),
          Arguments.of("no pic step with single element list",
              ANY_STEP, SINGLE_LIST, empty()),
          Arguments.of("last_step pic with single element list",
              PIC_LAST_STEP, SINGLE_LIST, empty()),
          Arguments.of("single attribute step with single element list",
              PIC_ATTR1_STEP, SINGLE_LIST, success(PAYLOAD1)),
          Arguments.of("complicated attributes step with single element list",
              PIC_ATTR2_STEP, SINGLE_LIST, failure("pB", "pA")),
          Arguments.of("null step with full list",
              NULL_STEP, MULTI_1_LIST, empty()),
          Arguments.of("no pic step with full list",
              ANY_STEP, MULTI_1_LIST, empty()),
          Arguments.of("last_step pic with full list",
              PIC_LAST_STEP, MULTI_1_LIST, empty()),
          Arguments.of("single attribute step with full list",
              PIC_ATTR1_STEP, MULTI_1_LIST, success(PAYLOAD1)),
          Arguments.of("complicated attributes step with full list",
              PIC_ATTR2_STEP, MULTI_1_LIST, success(PAYLOAD3, PAYLOAD2)));
    }

    @ParameterizedTest(name = "test: {0}")
    @MethodSource("payloadsSelectionProvider")
    void test0(String desc, Step step, List<Payload> payloads, PayloadSelectionOutcome outcome) {
      assertThat(new AttributesPayloadsSelection(step).selectPayloads(payloads)).isEqualTo(outcome);
    }

  }

}

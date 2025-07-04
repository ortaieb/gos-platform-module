package com.orta.gos.services.pic;

import static com.orta.gos.services.pic.PayloadSelectionOutcome.empty;
import static com.orta.gos.services.pic.PayloadSelectionOutcome.success;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.ANY_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.EMPTY_LIST;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.MULTI_1_LIST;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.NULL_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PAYLOAD1;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PAYLOAD3;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PIC_ATTR1_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PIC_ATTR2_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PIC_LAST_STEP;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.SINGLE_LIST;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.orta.gos.model.Payload;
import com.orta.gos.model.rules.Step;
import com.orta.gos.services.pic.LastPayloadsSelection;
import com.orta.gos.services.pic.PayloadSelectionOutcome;

import io.vavr.collection.List;

@DisplayName("LastPayloadsSelection")
public class LastPayloadsSelectionTest {

  @Nested
  @DisplayName("selectPayloads")
  public class SelectPayloadsTest {

    static Stream<Arguments> payloadsSelectionProvider() {
      return Stream.of(
          Arguments.of(NULL_STEP, EMPTY_LIST, empty()),
          Arguments.of(ANY_STEP, EMPTY_LIST, empty()),
          Arguments.of(PIC_LAST_STEP, EMPTY_LIST, empty()),
          Arguments.of(PIC_ATTR1_STEP, EMPTY_LIST, empty()),
          Arguments.of(PIC_ATTR2_STEP, EMPTY_LIST, empty()),
          Arguments.of(NULL_STEP, SINGLE_LIST, success(PAYLOAD1)),
          Arguments.of(ANY_STEP, SINGLE_LIST, success(PAYLOAD1)),
          Arguments.of(PIC_LAST_STEP, SINGLE_LIST, success(PAYLOAD1)),
          Arguments.of(PIC_ATTR1_STEP, SINGLE_LIST, success(PAYLOAD1)),
          Arguments.of(PIC_ATTR2_STEP, SINGLE_LIST, success(PAYLOAD1)),
          Arguments.of(NULL_STEP, MULTI_1_LIST, success(PAYLOAD3)),
          Arguments.of(ANY_STEP, MULTI_1_LIST, success(PAYLOAD3)),
          Arguments.of(PIC_LAST_STEP, MULTI_1_LIST, success(PAYLOAD3)),
          Arguments.of(PIC_ATTR1_STEP, MULTI_1_LIST, success(PAYLOAD3)),
          Arguments.of(PIC_ATTR2_STEP, MULTI_1_LIST, success(PAYLOAD3)));
    }

    @ParameterizedTest
    @MethodSource("payloadsSelectionProvider")
    void test0(Step step, List<Payload> payloads, PayloadSelectionOutcome outcome) {
      assertThat(new LastPayloadsSelection(step).selectPayloads(payloads)).isEqualTo(outcome);
    }

    // Custom accessor method for display names
    private static String getStepName(Step step) {
      return step == null ? "null" : step.getName();
    }

    private static int getPayloadCount(List<Payload> payloads) {
      return payloads == null ? 0 : payloads.size();
    }

    private static String getOutcomeType(PayloadSelectionOutcome outcome) {
      if (outcome == null)
        return "null";
      else if (outcome.hasErrors())
        return "with-errors";
      else
        return String.format("with %d entries", outcome.payloads().size());
    }
  }

}

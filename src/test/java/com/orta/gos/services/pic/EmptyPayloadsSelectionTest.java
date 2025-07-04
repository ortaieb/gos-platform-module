package com.orta.gos.services.pic;

import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.PAYLOADS_LIST;
import static com.orta.gos.services.pic.PayloadsSelectionTestInputs.STEP_LIST;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.orta.gos.services.pic.EmptyPayloadsSelection;

import static org.assertj.vavr.api.VavrAssertions.assertThat;

@DisplayName("EmptyPayloadsSelection")
public class EmptyPayloadsSelectionTest {

  @Nested
  @DisplayName("selectPayloads")
  public class SelectPayloadsTest {

    @Test
    @DisplayName("should always return empty list of payloads")
    void test0p() {
      STEP_LIST.forEach(step -> {
        var emptyPayloadsSelection = new EmptyPayloadsSelection(step);
        PAYLOADS_LIST.forEach(payloads -> {
          var outcome = emptyPayloadsSelection.selectPayloads(payloads);
          assertThat(outcome.payloads()).isEmpty();
        });
      });
    }

    @Test
    @DisplayName("should always return empty list of errors")
    void test0e() {
      STEP_LIST.forEach(step -> {
        var emptyPayloadsSelection = new EmptyPayloadsSelection(step);
        PAYLOADS_LIST.forEach(payloads -> {
          var outcome = emptyPayloadsSelection.selectPayloads(payloads);
          assertThat(outcome.errors()).isEmpty();
        });
      });
    }

  }

}

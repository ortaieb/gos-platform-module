package com.orta.gos.services.pic;

import com.orta.gos.model.rules.Step;
import com.orta.gos.model.rules.PicSelector.SelectorCase;

public class SelectorFactory {

  public static PayloadsSelection byStep(final Step step) {
    return switch (step.getPicSelector().getSelectorCase()) {
      case SelectorCase.SELECTOR_NOT_SET -> new EmptyPayloadsSelection(step);
      case SelectorCase.LAST_PAYLOAD -> new LastPayloadsSelection(step);
      case SelectorCase.BY_ATTRIBUTES -> new AttributesPayloadsSelection(step);
    };
  }

}

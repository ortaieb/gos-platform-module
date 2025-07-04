package com.orta.gos.services.pic;

import com.orta.gos.model.Payload;
import com.orta.gos.model.rules.PayloadAttributes;
import com.orta.gos.model.rules.PicSelector;
import com.orta.gos.model.rules.PicSelectorAttributes;
import com.orta.gos.model.rules.PicSelectorLast;
import com.orta.gos.model.rules.Step;

import io.vavr.collection.List;

public class PayloadsSelectionTestInputs {

  public static Step NULL_STEP = null;

  public static Step ANY_STEP = Step.newBuilder()
      .setName("any")
      .build();

  public static Step ATTR_STEP = Step.newBuilder()
      .putAttributes("a", "A")
      .build();

  public static Step PIC_LAST_STEP = Step.newBuilder()
      .setName("pic_last")
      .setPicSelector(PicSelector.newBuilder()
          .setLastPayload(PicSelectorLast.newBuilder()))
      .build();

  public static Step PIC_ATTR1_STEP = Step.newBuilder()
      .setName("pic_attr1")
      .setPicSelector(PicSelector.newBuilder()
          .setByAttributes(PicSelectorAttributes.newBuilder()
              .addPayloadDesc(PayloadAttributes.newBuilder()
                  .setPicName("p")
                  .putPicAttributes("a", "A")
                  .putPicAttributes("b", "B"))))
      .build();

  public static Step PIC_ATTR2_STEP = Step.newBuilder()
      .setName("pic_attr2")
      .setPicSelector(PicSelector.newBuilder()
          .setByAttributes(PicSelectorAttributes.newBuilder()
              .addPayloadDesc(PayloadAttributes.newBuilder()
                  .setPicName("pB")
                  .putPicAttributes("b", "B")
                  .putPicAttributes("d", "D"))
              .addPayloadDesc(PayloadAttributes.newBuilder()
                  .setPicName("pA")
                  .putPicAttributes("a", "A")
                  .putPicAttributes("d", "D"))))
      .build();

  public static List<Step> STEP_LIST = List.of(
      NULL_STEP,
      ANY_STEP,
      PIC_LAST_STEP,
      PIC_ATTR1_STEP,
      PIC_ATTR2_STEP);

  public static Payload PAYLOAD1 = Payload.newBuilder()
      .setUuid("p1")
      .putAttributes("a", "A")
      .putAttributes("b", "B")
      .build();
  public static Payload PAYLOAD2 = Payload.newBuilder()
      .setUuid("p2")
      .putAttributes("a", "A")
      .putAttributes("d", "D")
      .build();
  public static Payload PAYLOAD3 = Payload.newBuilder()
      .setUuid("p3")
      .putAttributes("b", "B")
      .putAttributes("d", "D")
      .build();

  public static List<Payload> EMPTY_LIST = List.empty();
  public static List<Payload> SINGLE_LIST = List.of(PAYLOAD1);
  public static List<Payload> MULTI_1_LIST = List.of(PAYLOAD1, PAYLOAD2, PAYLOAD3);

  public static List<List<Payload>> PAYLOADS_LIST = List.of(
      EMPTY_LIST,
      SINGLE_LIST,
      MULTI_1_LIST);

}

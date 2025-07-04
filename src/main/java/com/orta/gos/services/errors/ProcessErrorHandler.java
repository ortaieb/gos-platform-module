package com.orta.gos.services.errors;

import com.orta.gos.model.PlatformMessage;

public interface ProcessErrorHandler {

  void storeErrorSnapshot(final PlatformMessage message);

}

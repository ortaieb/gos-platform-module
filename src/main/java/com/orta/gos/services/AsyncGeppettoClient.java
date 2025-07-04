package com.orta.gos.services;

import com.orta.gos.model.PlatformMessage;
import com.orta.gos.model.utils.PlatformMessageUtils;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AsyncGeppettoClient {

  @Inject
  GrpcClientRepository clientRepo;

  public void sendMessage(PlatformMessage message) {
    new PlatformMessageUtils(message).maybeCurrentAddress().map(stepAddress -> {
      Log.infof("sending [%s] message to [%s]", message.getId(), stepAddress);
      return clientRepo.getStub(stepAddress).handle(message)
          .onItem().invoke(response -> Log.infof("Received response: %s", response))
          .onFailure().invoke(error -> Log.errorf(error, "Failed to send message to ``", stepAddress))
          .onCancellation().invoke(() -> Log.warn("Ack service call was cancelled"))
          .subscribe().with(
              response -> Log.info("Completed successfully"),
              failure -> Log.error("Completed with failure", failure));
    });

  }

}

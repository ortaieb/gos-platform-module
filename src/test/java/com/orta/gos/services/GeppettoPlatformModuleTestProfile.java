package com.orta.gos.services;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class GeppettoPlatformModuleTestProfile implements QuarkusTestProfile {

  @Override
  public Map<String, String> getConfigOverrides() {
    return Map.of(
        "quarkus.grpc.clients.processor.host", "${test.processor.host}",
        "quarkus.grpc.clients.processor.port", "${test.processor.port}");
  }
}

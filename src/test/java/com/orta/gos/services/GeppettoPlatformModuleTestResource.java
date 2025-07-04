package com.orta.gos.services;

import java.util.Map;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import io.vavr.control.Option;

public class GeppettoPlatformModuleTestResource implements QuarkusTestResourceLifecycleManager {

  private GenericContainer<?> processorContainer;

  @SuppressWarnings("resource")
  @Override
  public Map<String, String> start() {
    processorContainer = new GenericContainer<>(DockerImageName.parse("ortaieb/reverse-content:0.1.7"))
        .withExposedPorts(9990)
        .withEnv("QUARKUS_HTTP_PORT", "9990");

    processorContainer.start();

    return Map.of(
        "test.processor.host", processorContainer.getHost(),
        "test.processor.port", processorContainer.getMappedPort(9990).toString());
  }

  @Override
  public void stop() {
    Option.of(processorContainer).forEach(GenericContainer::stop);
  }

}

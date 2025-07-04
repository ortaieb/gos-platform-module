package com.orta.gos.services;

import java.time.Duration;

import com.orta.gos.model.PlatformMessage;
import com.orta.gos.model.WorkflowEnrichmentGrpc.WorkflowEnrichmentBlockingStub;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RegisterForReflection
public class WorkflowEnrichmentClient {

  @GrpcClient("workflow-enrich")
  WorkflowEnrichmentBlockingStub workflowEnrichmentStub;

  /**
   * Append rules to the platform message.
   *
   * @param message The platform message to enrich
   * @return The enriched platform message
   */
  public PlatformMessage appendRules(PlatformMessage message) {
    return workflowEnrichmentStub.appendRules(message);
  }

  /**
   * Append rules to the platform message with a specific timeout.
   *
   * @param message The platform message to enrich
   * @param timeout The maximum time to wait for the response
   * @return The enriched platform message
   */
  public PlatformMessage appendRules(PlatformMessage message, Duration timeout) {
    return workflowEnrichmentStub
        .withDeadlineAfter(timeout.toMillis(), java.util.concurrent.TimeUnit.MILLISECONDS)
        .appendRules(message);
  }
}

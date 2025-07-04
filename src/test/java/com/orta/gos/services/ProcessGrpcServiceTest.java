package com.orta.gos.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.orta.gos.model.Inbound;
import com.orta.gos.model.Payload;
import com.orta.gos.model.PlatformMessage;
import com.orta.gos.model.PlatformResponse;
import com.orta.gos.model.StringBody;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;

@DisplayName("ProcessGrpcService")
@QuarkusTest
public class ProcessGrpcServiceTest {

  @GrpcClient
  Inbound client;

  @Test
  @DisplayName("should handle empty message")
  void testEmptyMessage() {
    // Given
    var message = PlatformMessage.newBuilder().build();

    // When
    var result = client.handle(message).await().indefinitely();

    // Then
    Log.infof("Response from empty message: %s", result);
    assertNotNull(result);
    assertEquals(200, result.getStatus());
  }

  @Test
  @DisplayName("should handle message with payload")
  void testMessageWithPayload() {
    // Given
    var message = PlatformMessage.newBuilder()
        .setId("test-with-payload")
        .addPayloads(Payload.newBuilder()
            .setStringBody(StringBody.newBuilder().setBody("A B C")))
        .build();

    // When
    var result = client.handle(message).await().indefinitely();

    // Then
    Log.infof("Response from message with payload: %s", result);
    assertNotNull(result);
    assertEquals("test-with-payload", result.getId());
    assertEquals(200, result.getStatus());
  }
}

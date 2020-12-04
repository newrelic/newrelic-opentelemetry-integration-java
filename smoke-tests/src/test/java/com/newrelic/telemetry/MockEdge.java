/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.newrelic.telemetry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

/**
 * This class uses a docker image to mock the backend service for our trace and metric api
 * endpoints. During the test the exporter will send data to it.
 */
public class MockEdge extends GenericContainer {

  public static final int TIMEOUT_IN_MILLIS = 120_000;
  protected static OkHttpClient client = OkHttpUtils.client();
  private static final Logger logger = LoggerFactory.getLogger(MockEdge.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String IMAGE_NAME = "newrelic/mock-edge:testing";
  private final Network network;
  private static GenericContainer mockEdge;
  static final String NETWORK_ALIAS = "backend";

  public MockEdge(Network network) {
    this.network = network;
  }

  public void start() {
    mockEdge =
        new GenericContainer<>(DockerImageName.parse(IMAGE_NAME))
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/health").forPort(8080))
            .withNetwork(network)
            .withNetworkAliases(NETWORK_ALIAS)
            .withLogConsumer(new Slf4jLogConsumer(logger));
    mockEdge.start();
  }

  private JsonNode waitForJson(String url) throws IOException, InterruptedException {
    long timeout = System.currentTimeMillis() + TIMEOUT_IN_MILLIS;
    JsonNode jsonNode = null;

    // try repeatedly if necessary to fetch data
    while (System.currentTimeMillis() < timeout) {
      InputStream body =
          Objects.requireNonNull(
                  client.newCall(new Request.Builder().url(url).build()).execute().body())
              .byteStream();
      jsonNode = OBJECT_MAPPER.readTree(body);
      if (jsonNode.isEmpty()) {
        Thread.sleep(500);
      } else {
        break;
      }
    }
    return jsonNode;
  }

  /**
   * @return A simplified representation of metrics that have been reported to the backend in json
   *     format
   * @throws IOException
   * @throws InterruptedException
   */
  public ArrayNode getMetrics() throws IOException, InterruptedException {
    String url = String.format("http://localhost:%d/metric/get", mockEdge.getMappedPort(8080));
    JsonNode jsonNode = waitForJson(url);
    if (jsonNode == null) {
      return null;
    }
    if (jsonNode.isArray()) {
      return (ArrayNode) jsonNode;
    }
    return null;
  }

  /**
   * @return A simplified representation of spans that have been reported to the backend in json
   *     format
   * @throws IOException
   * @throws InterruptedException
   */
  public ArrayNode getSpans() throws IOException, InterruptedException {
    String url = String.format("http://localhost:%d/trace/get", mockEdge.getMappedPort(8080));
    JsonNode jsonNode = waitForJson(url);
    if (jsonNode.isArray()) {
      return (ArrayNode) jsonNode;
    }
    return null;
  }

  /**
   * Clears out all the metrics and spans from the backend service
   *
   * @throws IOException
   */
  public void clear() throws IOException {
    client
        .newCall(
            new Request.Builder()
                .url(
                    String.format(
                        "http://localhost:%d/metrics/clear", mockEdge.getMappedPort(8080)))
                .build())
        .execute()
        .close();
    client
        .newCall(
            new Request.Builder()
                .url(
                    String.format("http://localhost:%d/traces/clear", mockEdge.getMappedPort(8080)))
                .build())
        .execute()
        .close();
  }

  public void stop() {
    mockEdge.stop();
  }
}

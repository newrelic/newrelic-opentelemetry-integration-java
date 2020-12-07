/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.newrelic.telemetry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.newrelic.telemetry.model.Span;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class BundleTest {

  static Bundle bundle;

  @BeforeAll
  public static void before() {
    bundle = new Bundle();
    bundle.start();
  }

  @AfterAll
  public static void after() {
    bundle.stop();
  }

  @Test
  public void testMetricsAndSpans() throws Exception {
    String url = String.format("http://localhost:%d/greeting", bundle.target.getMappedPort(8080));
    Request request = new Request.Builder().url(url).get().build();

    Response response = OkHttpUtils.client().newCall(request).execute();
    assertEquals(200, response.code());
    assertEquals("Hi!", response.body().string());
    ArrayNode metrics = bundle.backend.getMetrics();
    assertTrue("no metrics were reported", metrics != null && !metrics.isEmpty());
    JsonNode metric = metrics.get(0);

    String name = metric.get("name").asText();
    Double count = metric.get("value").asDouble();
    assertEquals("processedSpans", name);

    ArrayNode spans = bundle.backend.getSpans();
    Set<String> spanNames = new HashSet<String>();
    Iterator<JsonNode> iterator = spans.iterator();
    ObjectMapper objectMapper = new ObjectMapper();
    while (iterator.hasNext()) {
      Span span = objectMapper.convertValue(iterator.next(), Span.class);
      spanNames.add(span.attributes.name);
    }
    assertTrue("no spans were reported", !spans.isEmpty());
    assertTrue(spanNames.contains("WebController.withSpan"));
    assertTrue(spanNames.contains("WebController.greeting"));
    assertTrue(spanNames.contains("/greeting"));
  }
}

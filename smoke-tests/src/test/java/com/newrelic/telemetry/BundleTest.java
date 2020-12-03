/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.newrelic.telemetry;

import com.fasterxml.jackson.databind.node.ArrayNode;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        ArrayNode spans = bundle.backend.getSpans();
        assertTrue("no spans were reported", spans != null && !spans.isEmpty());
    }
}

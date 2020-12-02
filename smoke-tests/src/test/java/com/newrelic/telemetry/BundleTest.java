package com.newrelic.telemetry;

import com.fasterxml.jackson.databind.node.ArrayNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

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
        assertEquals(1, metrics.size());

        ArrayNode spans = bundle.backend.getSpans();
        assertEquals(2, spans.size());
    }
}

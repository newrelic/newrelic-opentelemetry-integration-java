package com.newrelic.telemetry;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class BundleTest {

    static Bundle bundle;
    static MockEdge mockEdge;

    @BeforeAll
    public static void before() {
        bundle = new Bundle();
        bundle.start();

        mockEdge = new MockEdge();
        mockEdge.start();
    }

    @AfterAll
    public static void after() {
        bundle.stop();
        mockEdge.stop();
    }

    @Test
    public void testMetricsAndSpans() throws Exception {
        Thread.sleep(10000);
        ArrayNode metrics = mockEdge.getMetrics();
        assertEquals(12, metrics.size());

        ArrayNode spans = mockEdge.getSpans();
        assertEquals(12, spans.size());
    }
}

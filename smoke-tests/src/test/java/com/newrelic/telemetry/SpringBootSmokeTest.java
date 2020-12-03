/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.newrelic.telemetry;

import io.opentelemetry.api.trace.TraceId;
import io.opentelemetry.proto.collector.trace.v1.ExportTraceServiceRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SpringBootSmokeTest extends SmokeTest {

  protected String getTargetImage(int jdk) {
    return "open-telemetry-docker-dev.bintray.io/java/smoke-springboot-jdk" + jdk + ":latest";
  }

  @Test
  public void springBootSmokeTestOnJDK() throws IOException, InterruptedException {
    startTarget(11);
    String url = String.format("http://localhost:%d/greeting", target.getMappedPort(8080));
    Request request = new Request.Builder().url(url).get().build();

    String currentAgentVersion =
        (String)
            new JarFile(agentPath)
                .getManifest()
                .getMainAttributes()
                .get(Attributes.Name.IMPLEMENTATION_VERSION);

    Response response = client.newCall(request).execute();
    System.out.println(response.headers().toString());

    Collection<ExportTraceServiceRequest> traces = waitForTraces();

    // TODO get this test working or rip it out, most of these assertions fail
    Assertions.assertNotNull(response.header("X-server-id"));
    Assertions.assertEquals(1, response.headers("X-server-id").size());
    Assertions.assertTrue(TraceId.isValid(response.header("X-server-id")));
    Assertions.assertEquals(response.body().string(), "Hi!");
    Assertions.assertEquals(1, countSpansByName(traces, "/greeting"));
    Assertions.assertEquals(0, countSpansByName(traces, "WebController.greeting"));
    Assertions.assertEquals(1, countSpansByName(traces, "WebController.withSpan"));
    Assertions.assertEquals(2, countSpansByAttributeValue(traces, "custom", "demo"));
    Assertions.assertEquals(
        1, countResourcesByValue(traces, "telemetry.auto.version", currentAgentVersion));
    Assertions.assertEquals(1, countResourcesByValue(traces, "custom.resource", "demo"));

    // TODO we could refactor the assertions based on log output
    /*
     * These two lines indicate that our agent distro was successful in building, starting, and installing our OTel exporters:
     *
     * 16:06:29.331 [docker-java-stream--208473305] INFO  com.newrelic.telemetry.SmokeTest - STDERR: [opentelemetry.auto.trace 2020-11-19 00:06:29:355 +0000] [main] INFO io.opentelemetry.javaagent.tooling.TracerInstaller - Installed span exporter: com.newrelic.telemetry.opentelemetry.export.NewRelicSpanExporter
     * 16:06:29.358 [docker-java-stream--208473305] INFO  com.newrelic.telemetry.SmokeTest - STDERR: [opentelemetry.auto.trace 2020-11-19 00:06:29:381 +0000] [main] INFO io.opentelemetry.javaagent.tooling.TracerInstaller - Installed metric exporter: com.newrelic.telemetry.opentelemetry.export.NewRelicMetricExporter
     */

    /*
     * There is also payload data that shows that the agent auto-instrumented the spring app,
     * even if it couldn't send the span data due to an invalid api key being configured in SmokeTest.java: -Dnewrelic.api.key=123fake
     * This seems sufficient for the purposes of this test and might allow us to rip a bunch of the logic out.
     *
     * 16:06:39.383 [docker-java-stream--208473305] INFO  com.newrelic.telemetry.SmokeTest - STDERR: [opentelemetry.auto.trace 2020-11-19 00:06:39:382 +0000] [Thread-2] DEBUG com.newrelic.telemetry.transport.BatchDataSender - Sending json: [{"common":{"attributes":{"telemetry.sdk.language":"java","service.name":"(unknown service)","service.instance.id":"bba9b7e9-b90a-4766-9481-74d8cea0afc4","telemetry.sdk.version":"0.10.0","telemetry.auto.version":"newrelic-opentelemetry-integration-1.0-SNAPSHOT-otel-0.10.1","collector.name":"newrelic-opentelemetry-exporter","instrumentation.provider":"opentelemetry","telemetry.sdk.name":"opentelemetry"}},"spans":[{"id":"b66d226babb39712","trace.id":"54e527bce5de435c4e7f89957d8377d2","timestamp":1605744396405,"attributes":{"duration.ms":0.41,"instrumentation.version":"newrelic-opentelemetry-integration-1.0-SNAPSHOT-otel-0.10.1","span.kind":"INTERNAL","name":"WebController.withSpan","parent.id":"dbd0503866fec4b7","instrumentation.name":"io.opentelemetry.auto.trace-annotation","thread.name":"http-nio-8080-exec-2","thread.id":23}},{"id":"dbd0503866fec4b7","trace.id":"54e527bce5
     * 16:06:39.383 [docker-java-stream--208473305] INFO  com.newrelic.telemetry.SmokeTest - STDERR: de435c4e7f89957d8377d2","timestamp":1605744396369,"attributes":{"duration.ms":99.4355,"instrumentation.version":"newrelic-opentelemetry-integration-1.0-SNAPSHOT-otel-0.10.1","span.kind":"INTERNAL","name":"WebController.greeting","parent.id":"d7f5a5e8c90263ff","instrumentation.name":"io.opentelemetry.auto.spring-webmvc","thread.name":"http-nio-8080-exec-2","thread.id":23}},{"id":"d7f5a5e8c90263ff","trace.id":"54e527bce5de435c4e7f89957d8377d2","timestamp":1605744396309,"attributes":{"duration.ms":163.2936,"net.peer.port":41730,"http.flavor":"HTTP/1.1","http.url":"http://localhost:32857/greeting","instrumentation.name":"io.opentelemetry.auto.servlet","thread.name":"http-nio-8080-exec-2","net.peer.ip":"192.168.112.1","http.client_ip":"192.168.112.1","http.status_code":200,"instrumentation.version":"newrelic-opentelemetry-integration-1.0-SNAPSHOT-otel-0.10.1","span.kind":"SERVER","http.user_agent":"okhttp/3.12.12","name":"/greeting","http.method":"GET","thread.id":23}}]}]
     */

    stopTarget();
  }
}

package com.newrelic.telemetry;


import io.opentelemetry.javaagent.OpenTelemetryAgent;

import java.lang.instrument.Instrumentation;

import static com.newrelic.telemetry.DefaultConfig.setDefaultConfig;

public class NewRelicAgent {
    public static void premain(final String agentArgs, final Instrumentation inst) {
        agentmain(agentArgs, inst);
    }

    public static void agentmain(final String agentArgs, final Instrumentation inst) {
        setDefaultConfig("otel.exporter", "newrelic");
        OpenTelemetryAgent.agentmain(agentArgs, inst);
    }
}
package io.opentelemetry.demo;


import io.opentelemetry.javaagent.OpenTelemetryAgent;

import java.lang.instrument.Instrumentation;

import static io.opentelemetry.demo.DefaultConfig.setDefaultConfig;

public class DemoAgent {
    public static void premain(final String agentArgs, final Instrumentation inst) {
        agentmain(agentArgs, inst);
    }

    public static void agentmain(final String agentArgs, final Instrumentation inst) {
        setDefaultConfig("otel.exporter", "demo");
        OpenTelemetryAgent.agentmain(agentArgs, inst);
    }
}
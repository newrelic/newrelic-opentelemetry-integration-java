package com.newrelic.telemetry;

import io.opentelemetry.javaagent.spi.exporter.MetricExporterFactory;
import io.opentelemetry.sdk.metrics.export.MetricExporter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class NewRelicMetricExporterFactory implements MetricExporterFactory {
    @Override
    public MetricExporter fromConfig(Properties config) {
        return new NewRelicMetricExporter();
    }

    @Override
    public Set<String> getNames() {
        return new HashSet<>(Arrays.asList("newrelic"));
    }
}

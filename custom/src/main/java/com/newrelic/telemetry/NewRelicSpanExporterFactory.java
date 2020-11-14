package com.newrelic.telemetry;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.spi.exporter.SpanExporterFactory;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

// TODO use implementations from opentelemetry-exporters-newrelic-all artifact instead
@AutoService(SpanExporterFactory.class)
public class NewRelicSpanExporterFactory implements SpanExporterFactory {

  @Override
  public SpanExporter fromConfig(Properties config) {
    return new NewRelicSpanExporter();
  }

  @Override
  public Set<String> getNames() {
    return new HashSet<>(Arrays.asList("newrelic"));
  }
}

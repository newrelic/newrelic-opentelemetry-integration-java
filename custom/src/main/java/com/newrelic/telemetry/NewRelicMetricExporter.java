package com.newrelic.telemetry;

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import java.util.Collection;

// TODO use implementations from opentelemetry-exporters-newrelic artifact instead
public class NewRelicMetricExporter implements MetricExporter {
  @Override
  public CompletableResultCode export(Collection<MetricData> metrics) {
    System.out.printf("%d metrics exported%n", metrics.size());
    return CompletableResultCode.ofSuccess();
  }

  @Override
  public CompletableResultCode flush() {
    return CompletableResultCode.ofSuccess();
  }

  @Override
  public void shutdown() {}
}

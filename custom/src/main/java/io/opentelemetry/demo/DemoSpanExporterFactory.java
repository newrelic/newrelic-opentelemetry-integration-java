/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.demo;

import com.google.auto.service.AutoService;
import io.opentelemetry.javaagent.spi.exporter.SpanExporterFactory;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@AutoService(SpanExporterFactory.class)
public class DemoSpanExporterFactory implements SpanExporterFactory {

    @Override
    public SpanExporter fromConfig(Properties config) {
        return new DemoSpanExporter();
    }

    @Override
    public Set<String> getNames() {
        return new HashSet<>(Arrays.asList("demo"));
    }
}
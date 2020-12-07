/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.newrelic.telemetry.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Span {

  public String id;

  @JsonProperty("trace.id")
  public String traceId;

  public long timestamp;
  public SpanAttributes attributes;
}

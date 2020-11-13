## Introduction

This repository serves as a collection of examples of extending functionality of OpenTelemetry Java instrumentation agent.
It demonstrates how to repackage the aforementioned agent adding custom functionality.
For every extension point provided by OpenTelemetry Java instrumentation, this repository contains an example of
its usage.

## Extensions examples

* [DemoIdGenerator](./DemoIdGenerator.java) - custom `IdGenerator`
* [DemoPropagator](./DemoPropagator.java) - custom `TextMapPropagator`
* [DemoPropertySource](./DemoPropertySource.java) - default configuration
* [DemoSampler](./DemoSampler.java) - custom `Sampler`
* [DemoSpanProcessor](./DemoSpanProcessor.java) - custom `SpanProcessor`
* [DemoSpanExporter](./DemoSpanExporter.java) - custom `SpanExporter`
* [DemoServlet3Instrumentation](./instrumentation/DemoServlet3Instrumentation.java) - additional instrumentation

[![Community Project header](https://github.com/newrelic/opensource-website/raw/master/src/images/categories/Community_Project.png)](https://opensource.newrelic.com/oss-category/#community-project)

# New Relic OpenTelemetry Integration (Java Agent)

A custom distribution of the OpenTelemetry Java agent that uses the [New Relic OpenTelemetry exporter](https://github.com/newrelic/opentelemetry-exporter-java) by 
default to send telemetry data to the New Relic platform.

## Installing and using New Relic OpenTelemetry Integration

The New Relic OpenTelemetry Integration is a standard javaagent. To use it, you just need the `-javaagent` startup flag and the following system properties. 

```
-javaagent:/path/to/newrelic-opentelemetry-javaagent-all.jar
-Dnewrelic.api.key=<Insights Insert Key>
-Dnewrelic.service.name=fun_service
```

Here is an example to override the endpoints where metric and span are sent and to increase logging.

```
-javaagent:/path/to/newrelic-opentelemetry-javaagent-all.jar
-Dnewrelic.api.key=<Insights Insert Key>
-Dnewrelic.service.name=fun_service
-Dio.opentelemetry.javaagent.slf4j.simpleLogger.log.com.newrelic.telemetry=debug
-Dnewrelic.enable.audit.logging=true
-Dnewrelic.trace.uri.override=https://staging-trace-api.newrelic.com/trace/v1
-Dnewrelic.metric.uri.override=https://staging-metric-api.newrelic.com/metric/v1
```

Here is the list and description of all configurable properties for the [New Relic OpenTelemetry exporter](https://github.com/newrelic/opentelemetry-exporter-java#configuration-system-properties).

## Find and use your data

Go to [one.newrelic.com](https://one.newrelic.com), search for your service name, and :star2: 

If you don't see your service, try running with debug and audit logging to confirm telemetry batches are being successfully sent. Here is an example of what you might see. 

```
[Thread-6] DEBUG com.newrelic.telemetry.spans.SpanBatchSender - Sending a span batch (number of spans: 4) to the New Relic span ingest endpoint)
[Thread-6] DEBUG com.newrelic.telemetry.spans.json.SpanBatchMarshaller - Generating json for span batch.
[Thread-6] DEBUG com.newrelic.telemetry.transport.BatchDataSender - Response from New Relic ingest API: code: 202, body: {"requestId":"28fa2622-0001-b000-0000-01761fc2a899"}
[Thread-6] DEBUG com.newrelic.telemetry.TelemetryClient - Telemetry batch sent
```

For tips on how to find and query your data in New Relic, see 
[Find trace/span data](https://docs.newrelic.com/docs/understand-dependencies/distributed-tracing/trace-api/introduction-trace-api#view-data). 

For general querying information, see:
- [Query New Relic data](https://docs.newrelic.com/docs/using-new-relic/data/understand-data/query-new-relic-data)
- [Intro to NRQL](https://docs.newrelic.com/docs/query-data/nrql-new-relic-query-language/getting-started/introduction-nrql)

## Building

Requires: JDK 11+

`./gradlew assemble`

## Testing

`smoke-tests` contains simple tests to verify that resulting agent builds and applies correctly

## Support

New Relic hosts and moderates an online forum where customers can interact with New Relic employees as well as other customers to get help and share best practices. 
Like all official New Relic open source projects, there's a related Community topic in the New Relic Explorers Hub. You can find this project's topic/threads here:

**Support Channels**

* [New Relic Documentation](https://docs.newrelic.com/docs/integrations/open-source-telemetry-integrations/open-source-telemetry-integration-list/new-relics-opentelemetry-integration): Comprehensive guidance for using our platform
* [New Relic Community](https://discuss.newrelic.com/tags/javaagent): The best place to engage in troubleshooting questions
* [New Relic Developer](https://developer.newrelic.com/): Resources for building a custom observability applications
* [New Relic University](https://learn.newrelic.com/): A range of online training for New Relic users of every level
* [New Relic Technical Support](https://support.newrelic.com/) 24/7/365 ticketed support. Read more about our [Technical Support Offerings](https://docs.newrelic.com/docs/licenses/license-information/general-usage-licenses/support-plan). 

## Contributing

We encourage contributions to improve the New Relic OpenTelemetry Integration (Java Agent)! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project.
If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company,  please drop us an email at opensource@newrelic.com.

## License

The New Relic OpenTelemetry Integration (Java Agent) is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.

The New Relic OpenTelemetry Integration (Java Agent) also uses source code from third-party libraries. You can find full details on which libraries are used and the terms under which they are licensed in the third-party notices document.

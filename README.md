[![Archived header](https://github.com/newrelic/open-source-office/raw/master/examples/categories/images/Archived.png)](https://github.com/newrelic/open-source-office/blob/master/examples/categories/index.md#archived)

# Archival Notice

❗Notice: This project has been archived _as is_ and is no longer actively maintained.

Rather than developing a Java specific OpenTelemetry exporter New Relic has adopted a language agnostic approach that facilitates data collection from all OpenTelemetry data sources.

The current recommended approaches for sending OpenTelemetry data to the New Relic platform are as follows:
* Configure your OpenTelemetry data source to send data to the [OpenTelemetry Collector](https://docs.newrelic.com/docs/integrations/open-source-telemetry-integrations/opentelemetry/introduction-opentelemetry-new-relic/#collector) using the OpenTelemetry Protocol (OTLP) and configure the collector to forward the data using the [New Relic collector exporter](https://github.com/newrelic-forks/opentelemetry-collector-contrib/tree/newrelic-main/exporter/newrelicexporter).
* Configure your OpenTelemetry data source to send data to the native OpenTelemetry Protocol (OTLP) data ingestion endpoint. [OTLP](https://github.com/open-telemetry/opentelemetry-specification/blob/main/specification/protocol/otlp.md) is an open source gRPC based protocol for sending telemetry data. The protocol is vendor agnostic and open source.

For more details please see:
* [OpenTelemetry quick start](https://docs.newrelic.com/docs/integrations/open-source-telemetry-integrations/opentelemetry/opentelemetry-quick-start/)
* [Introduction to OpenTelemetry with New Relic](https://docs.newrelic.com/docs/integrations/open-source-telemetry-integrations/opentelemetry/introduction-opentelemetry-new-relic/)
* [Native OpenTelemetry Protocol (OTLP) support](https://docs.newrelic.com/whats-new/2021/04/native-support-opentelemetry/)

---

# New Relic OpenTelemetry Integration (Java agent)

This is a custom distribution of the OpenTelemetry Java agent that uses the [New Relic OpenTelemetry exporter](https://github.com/newrelic/opentelemetry-exporter-java) to send telemetry data to the New Relic platform.

## Prerequisites

First things first:

* If we don’t already know you, sign up for a [New Relic account](https://docs.newrelic.com/docs/accounts/accounts-billing/account-setup/create-your-new-relic-account).
* Make sure you have an [Insights insert key](https://docs.newrelic.com/docs/telemetry-data-platform/ingest-manage-data/ingest-apis/use-event-api-report-custom-events#) to send spans and metrics to New Relic.

## Get the software

You can either grab the published artifacts or you can build it yourself.

### Published Artifacts
|Group                 |Name                                 |Link                                                                                                   |Description     |
|----------------------|-------------------------------------|-------------------------------------------------------------------------------------------------------|----------------|
|com.newrelic.telemetry|newrelic-opentelemetry-javaagent     |[Maven](https://search.maven.org/artifact/com.newrelic.telemetry/newrelic-opentelemetry-javaagent)     | The java agent |


### Build

To build the integration, make sure you have JDK 8+, and then run the following command:

`./gradlew assemble`

## Startup

The New Relic OpenTelemetry Integration is a standard Java agent. To use it, you just need the `-javaagent` startup flag and these minimum system properties: 

```
-javaagent:/path/to/newrelic-opentelemetry-javaagent-*-all.jar
-Dnewrelic.api.key=YOUR_INSIGHTS_INSERT_KEY
-Dnewrelic.service.name=INSERT_A_DESCRIPTIVE_NAME
```

Since the New Relic OpenTelemetry Integration is built using the New Relic OpenTelemetry exporter, it uses the same system properties. For a list and description of all configurable properties, see [New Relic OpenTelemetry exporter](https://github.com/newrelic/opentelemetry-exporter-java#configuration-system-properties).

Here's an example of common startup properties:

```
-javaagent:/path/to/newrelic-opentelemetry-javaagent-*-all.jar
-Dnewrelic.api.key=YOUR_INSIGHTS_INSERT_KEY
-Dnewrelic.service.name=INSERT_A_DESCRIPTIVE_NAME
-Dio.opentelemetry.javaagent.slf4j.simpleLogger.log.com.newrelic.telemetry=debug
-Dnewrelic.enable.audit.logging=true
-Dnewrelic.trace.uri.override=SEE_THE_SECTION_BELOW_ABOUT_CHANGING_ENDPOINTS
-Dnewrelic.metric.uri.override=SEE_THE_SECTION_BELOW_ABOUT_CHANGING_ENDPOINTS
```

## Change endpoints

If you don't supply endpoints, the integration defaults to the following:

* Traces: https://trace-api.newrelic.com/trace/v1
* Metrics: https://metric-api.newrelic.com/metric/v1

You can override the default endpoints for this integration, for example, when switching to the EU region or setting up [Infinite Tracing](https://docs.newrelic.com/docs/understand-dependencies/distributed-tracing/infinite-tracing/introduction-infinite-tracing).

For details about using EU or Infinite Tracing URLs, see [OpenTelemetry: Advanced configuration](https://docs.newrelic.com/docs/integrations/open-source-telemetry-integrations/opentelemetry/opentelemetry-advanced-configuration).

## Find and use your data in New Relic

Go to [one.newrelic.com](https://one.newrelic.com), search for your service name. If you don't see your service, try running with debug and audit logging to confirm telemetry batches are being successfully sent. Here is an example of what you might see: 

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

## Testing

Here are the testing requirements:

* JDK 8+
* Docker

`smoke-tests` contains simple tests to verify that the resulting agent builds and applies correctly.

To run the smoke tests run the following command:

`./gradlew test`

## Example

To see the New Relic OpenTelemetry Integration in action, check out the [Spring Pet Clinic sample application](https://github.com/newrelic-forks/spring-petclinic).

To set up the Pet Clinic with OpenTelemetry:

1. Build the New Relic OpenTelemetry Integration, as described in the [Building](#Build) section,
or download the jar file [from here](https://search.maven.org/remotecontent?filepath=com/newrelic/telemetry/newrelic-opentelemetry-javaagent/).
2. Follow the steps in the [Running petclinic locally section](https://github.com/newrelic-forks/spring-petclinic#running-petclinic-locally), 
but when executing the jar, add the Java agent, service name, and Insights insert key as follows:
```
java -javaagent:/path/to/newrelic-opentelemetry-javaagent-*-all.jar -Dnewrelic.api.key=YOUR_INSIGHTS_INSERT_KEY -Dnewrelic.service.name=pet-clinic -jar target/*.jar
```
3. Once it is running, click around a bit in the app at http://localhost:8080.
4. Go to [one.newrelic.com](https://one.newrelic.com).
5. In the upper right, click on the magnifying glass, and search for `pet-clinic`.  
6. Click on the `pet-clinic` link to see data coming in!

## Support

New Relic hosts and moderates an online forum where you can interact with New Relic employees as well as other customers to get help and share best practices. 
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

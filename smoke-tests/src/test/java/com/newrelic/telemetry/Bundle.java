package com.newrelic.telemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class Bundle {

    private static final Network network = Network.newNetwork();
    protected GenericContainer bundle;
    private static final Logger logger = LoggerFactory.getLogger(Bundle.class);
    protected static final String agentPath =
            System.getProperty("io.opentelemetry.smoketest.agent.shadowJar.path");

    public void start() {
        bundle =
                    new GenericContainer<>(DockerImageName.parse(getTargetImage(11)))
                            .withExposedPorts(8080)
                            .withNetwork(network)
                            .withLogConsumer(new Slf4jLogConsumer(logger))
                            .withCopyFileToContainer(
                                    MountableFile.forHostPath(agentPath), "/newrelic-opentelemetry-javaagent.jar")
                            .withEnv(
                                    "JAVA_TOOL_OPTIONS",
                                    "-javaagent:/newrelic-opentelemetry-javaagent.jar "
                                            + "-Dnewrelic.api.key=123fake "
                                            + "-Dnewrelic.enable.audit.logging=true "
                                            + "-Dnewrelic.trace.uri.override=http://localhost:8080/span/put"
                                            + "-Dnewrelic.metric.uri.override=http://localhost:8080/metric/put"
                                            + "-Dio.opentelemetry.javaagent.slf4j.simpleLogger.log.com.newrelic.telemetry=debug")
                            .withEnv("OTEL_BSP_MAX_EXPORT_BATCH", "1")
                            .withEnv("OTEL_BSP_SCHEDULE_DELAY", "10")
                            .withEnv("OTEL_INTEGRATION_GEODE_ENABLED", "false");
        bundle.start();
    }

    public void stop() {
        bundle.stop();
    }

    private String getTargetImage(int jdk) {
        return "open-telemetry-docker-dev.bintray.io/java/smoke-springboot-jdk" + jdk + ":latest";
    }
}

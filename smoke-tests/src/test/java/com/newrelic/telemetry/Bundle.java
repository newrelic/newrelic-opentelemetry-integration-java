/*
 * Copyright 2020 New Relic Corporation. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.newrelic.telemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * This class pulls down a docker image of a open telemetry smoke test app and runs it with the bundle attached
 */
public class Bundle {

    public static final Network network = Network.newNetwork();
    protected GenericContainer target;
    protected MockEdge backend;
    private static final Logger logger = LoggerFactory.getLogger(Bundle.class);
    protected static final String agentPath =
            System.getProperty("io.opentelemetry.smoketest.agent.shadowJar.path");
    private static final String DOCKER_IMAGE_NAME = "open-telemetry-docker-dev.bintray.io/java/smoke-springboot-jdk11:latest";

    public void start() {
        backend = new MockEdge(network);
        String args = "-javaagent:/newrelic-opentelemetry-javaagent.jar "
                                        + "-Dnewrelic.api.key=123fake "
                                        + "-Dnewrelic.enable.audit.logging=true "
                                        + String.format("-Dnewrelic.trace.uri.override=http://%s:8080/trace/add ", MockEdge.NETWORK_ALIAS)
                                        + String.format("-Dnewrelic.metric.uri.override=http://%s:8080/metric/add ", MockEdge.NETWORK_ALIAS)
                                        + "-Dio.opentelemetry.javaagent.slf4j.simpleLogger.log.com.newrelic.telemetry=debug ";

        target = new GenericContainer<>(DockerImageName.parse(DOCKER_IMAGE_NAME))
                        .dependsOn(backend)
                        .withExposedPorts(8080)
                        .withNetwork(network)
                        .withLogConsumer(new Slf4jLogConsumer(logger))
                        .withCopyFileToContainer(
                                MountableFile.forHostPath(agentPath), "/newrelic-opentelemetry-javaagent.jar")
                        .withEnv(
                                "JAVA_TOOL_OPTIONS", args)
                        .withEnv("OTEL_BSP_MAX_EXPORT_BATCH", "1")
                        .withEnv("OTEL_BSP_SCHEDULE_DELAY", "10")
                        .withEnv("OTEL_INTEGRATION_GEODE_ENABLED", "false");
        target.start();
    }

    public void stop() {
        target.stop();
        backend.stop();
    }
}

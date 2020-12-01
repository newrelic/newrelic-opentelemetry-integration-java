package com.newrelic.telemetry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.io.InputStream;

public class MockEdge {

    private static final Network network = Network.newNetwork();
    protected static OkHttpClient client = OkHttpUtils.client();
    private static final Logger logger = LoggerFactory.getLogger(MockEdge.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String IMAGE_NAME = "mock_edge";

    private static GenericContainer mockEdge;

    public MockEdge() {
    }

    public void start() {
        mockEdge =
                new GenericContainer<>(
                        DockerImageName.parse(
                                "mock_edge"))
                        .withExposedPorts(8080)
                        .waitingFor(Wait.forHttp("/health").forPort(8080))
                        .withNetwork(network)
                        .withNetworkAliases("backend")
                        .withLogConsumer(new Slf4jLogConsumer(logger));
        mockEdge.start();
    }

    public ArrayNode getMetrics() throws IOException {

        InputStream body = client.newCall(
                new Request.Builder().url(String.format("http://localhost:%d/metric/metrics", mockEdge.getMappedPort(8080))).build())
                .execute()
                .body()
                .byteStream();

        JsonNode jsonNode = OBJECT_MAPPER.readTree(body);
        if (jsonNode.isArray()) {
            return (ArrayNode) jsonNode;
        }
        return null;
    }

    public ArrayNode getSpans() throws IOException {
        InputStream body = client.newCall(
                new Request.Builder().url(String.format("http://localhost:%d/trace/spans", mockEdge.getMappedPort(8080))).build())
                .execute()
                .body()
                .byteStream();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(body);
        if (jsonNode.isArray()) {
            return (ArrayNode) jsonNode;
        }
        return null;
    }

    public void clear() throws IOException {
        client.newCall(new Request.Builder().url(String.format("http://localhost:%d/metrics/clear", mockEdge.getMappedPort(8080))).build()).execute().close();
        client.newCall(new Request.Builder().url(String.format("http://localhost:%d/traces/clear", mockEdge.getMappedPort(8080))).build()).execute().close();
    }

    public void stop() {
        mockEdge.stop();
    }
}

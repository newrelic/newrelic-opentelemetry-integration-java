package com.newrelic.telemetry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Pattern;

// TODO we may be able to simplify this for our needs, if not we should add proper attribution to Splunk:
//  https://github.com/signalfx/splunk-otel-java/blob/main/agent/src/main/java/com/splunk/opentelemetry/DefaultConfig.java

final class DefaultConfig {
    private static final Pattern ENV_REPLACEMENT = Pattern.compile("[^a-zA-Z0-9_]");
    private static final String CONFIGURATION_FILE_PROPERTY = "otel.trace.config";
    private static final Properties OTEL_CONFIGURATION_FILE = loadConfigurationFile();

    static void setDefaultConfig(String property, String value) {
        if (!isConfigured(property)) {
            System.setProperty(property, value);
        }
    }

    private static boolean isConfigured(String propertyName) {
        return System.getProperty(propertyName) != null
                || System.getenv(toEnvVarName(propertyName)) != null
                || OTEL_CONFIGURATION_FILE.containsKey(propertyName);
    }

    private static String toEnvVarName(String propertyName) {
        return ENV_REPLACEMENT.matcher(propertyName.toUpperCase()).replaceAll("_");
    }

    // this code is copied from otel-java-instrumentation Config class -- we can't call Config here
    // because that would initialize the whole configuration
    private static Properties loadConfigurationFile() {
        Properties properties = new Properties();

        // Reading from system property first and from env after
        String configurationFilePath = System.getProperty(CONFIGURATION_FILE_PROPERTY);
        if (configurationFilePath == null) {
            configurationFilePath = System.getenv(toEnvVarName(CONFIGURATION_FILE_PROPERTY));
        }
        if (configurationFilePath == null) {
            return properties;
        }

        // Normalizing tilde (~) paths for unix systems
        configurationFilePath =
                configurationFilePath.replaceFirst("^~", System.getProperty("user.home"));

        File configurationFile = new File(configurationFilePath);
        if (!configurationFile.exists()) {
            return properties;
        }

        try (FileReader fileReader = new FileReader(configurationFile)) {
            properties.load(fileReader);
        } catch (IOException ignored) {
            // OTel agent will log this error anyway
        }

        return properties;
    }

    private DefaultConfig() {}
}

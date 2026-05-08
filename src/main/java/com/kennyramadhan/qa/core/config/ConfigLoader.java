package com.kennyramadhan.qa.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <h1>ConfigLoader</h1>
 * Utility class for reading the `config.properties` configuration file
 * and exposing helper methods to retrieve values by key.
 *
 * Supported methods:
 *  - get(key)
 *  - getOrDefault(key, defaultValue)
 *  - has(key)
 *  - getAll() -> returns all configuration entries as a Map<String, String>
 *
 * Values matching the pattern <code>${VAR_NAME}</code> are resolved from
 * environment variables at lookup time (with fallback to the literal value
 * when the env var is unset). This keeps secrets out of the file.
 *
 * @author Kenny Ramadhan
 * @version 1.1
 */
public class ConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);

    /** Stores all configuration loaded from config.properties */
    private static final Properties props = new Properties();

    /** Resolved environment name (system property "env", default "local"). */
    private static final String env;

    static {
        env = System.getProperty("env", "local");
        loadProperties("config/config.properties", true);
        loadProperties("config/config-" + env + ".properties", false);
        log.info("[OK] Config loaded for env={}. Total keys: {}", env, props.size());
    }

    private static void loadProperties(String resourcePath, boolean required) {
        try (InputStream is = ConfigLoader.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (is == null) {
                if (required) {
                    throw new IllegalStateException(resourcePath + " not found on classpath");
                }
                return;
            }
            props.load(is);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + resourcePath, e);
        }
    }

    /**
     * Returns the resolved environment name (e.g. "local", "ci", "staging").
     * Driven by the system property <code>env</code>; defaults to <code>local</code>.
     */
    public static String getEnvironment() {
        return env;
    }

    private static String resolvePlaceholder(String value) {
        if (value == null) return null;
        if (value.startsWith("${") && value.endsWith("}")) {
            String varName = value.substring(2, value.length() - 1);
            String envValue = System.getenv(varName);
            return (envValue != null && !envValue.isEmpty()) ? envValue : value;
        }
        return value;
    }

    /**
     * Retrieves the value for the given key.
     */
    public static String get(String key) {
        return resolvePlaceholder(props.getProperty(key));
    }

    /**
     * Retrieves the value for the given key; returns defaultValue if not present.
     */
    public static String getOrDefault(String key, String defaultValue) {
        String value = resolvePlaceholder(props.getProperty(key));
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    /**
     * Returns true if the key has a non-empty value.
     */
    public static boolean has(String key) {
        String value = props.getProperty(key);
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Returns all configuration entries as a Map<String, String>.
     * Useful for bulk-applying capabilities.
     */
    public static Map<String, String> getAll() {
        if (props.isEmpty()) return Collections.emptyMap();

        return props.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> String.valueOf(e.getKey()),
                        e -> String.valueOf(e.getValue())
                ));
    }
}

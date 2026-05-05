package com.kennyramadhan.qa.core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * <h1>ConfigLoader</h1>
 * Utility class untuk membaca file konfigurasi `config.properties`
 * dan menyediakan method helper untuk mendapatkan value berdasarkan key.
 *
 * Versi ini mendukung:
 *  - get(key)
 *  - getOrDefault(key, defaultValue)
 *  - has(key)
 *  - getAll() -> untuk mendapatkan semua konfigurasi dalam bentuk Map<String, String>
 *
 * Values matching the pattern <code>${VAR_NAME}</code> are resolved from
 * environment variables at lookup time (with fallback to the literal value
 * when the env var is unset). This keeps secrets out of the file.
 *
 * @author Kenny Ramadhan
 * @version 1.1
 */
public class ConfigLoader {

    /** Menyimpan semua konfigurasi dari file config.properties */
    private static final Properties props = new Properties();

    /** Resolved environment name (system property "env", default "local"). */
    private static final String env;

    static {
        env = System.getProperty("env", "local");
        loadProperties("config/config.properties", true);
        loadProperties("config/config-" + env + ".properties", false);
        System.out.println("[OK] Config loaded for env=" + env + ". Total keys: " + props.size());
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
     * Mengambil value dari key yang ada di config.properties.
     */
    public static String get(String key) {
        return resolvePlaceholder(props.getProperty(key));
    }

    /**
     * Mengambil value dari key, jika tidak ditemukan maka kembalikan defaultValue.
     */
    public static String getOrDefault(String key, String defaultValue) {
        String value = resolvePlaceholder(props.getProperty(key));
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }

    /**
     * Mengecek apakah key memiliki value yang valid.
     */
    public static boolean has(String key) {
        String value = props.getProperty(key);
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Mengambil semua konfigurasi sebagai Map<String, String>.
     * Berguna untuk set capability secara otomatis.
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

package com.kennyramadhan.qa.web.client;

import com.kennyramadhan.qa.core.config.ConfigLoader;

/**
 * Resolved web automation configuration. Mirrors the Phase 5
 * {@code ApiConfig} pattern: values resolved once from {@link ConfigLoader}
 * (with env-overlay + ${VAR} resolution) and exposed as
 * {@code public static final}.
 */
public final class WebConfig {

    private WebConfig() {}

    /** Base URL of the target web application. */
    public static final String BASE_URL =
            ConfigLoader.getOrDefault("web.baseUrl", "https://automationexercise.com");

    /** Default explicit-wait timeout in seconds. */
    public static final int TIMEOUT_SECONDS =
            Integer.parseInt(ConfigLoader.getOrDefault("web.timeout.seconds", "15"));

    /** Default browser name (chrome|firefox|edge), case-insensitive. */
    public static final String DEFAULT_BROWSER =
            ConfigLoader.getOrDefault("web.default.browser", "chrome");
}

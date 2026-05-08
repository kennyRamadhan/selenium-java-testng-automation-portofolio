package com.kennyramadhan.qa.api.client;

import com.kennyramadhan.qa.core.config.ConfigLoader;

/**
 * Resolved API configuration constants. Values are loaded once from
 * {@link ConfigLoader} (which already handles env-overlay and ${VAR} resolution
 * — see Commit 2.4) and exposed as {@code public static final} for cheap access
 * from {@link BaseApiClient} and tests.
 */
public final class ApiConfig {

    private ApiConfig() {}

    /** Base URL of the target API (e.g. https://automationexercise.com). */
    public static final String BASE_URL =
            ConfigLoader.getOrDefault("api.baseUrl", "https://automationexercise.com");

    /** Per-request timeout in seconds. Used by RestAssured connection/socket config. */
    public static final int TIMEOUT_SECONDS =
            Integer.parseInt(ConfigLoader.getOrDefault("api.timeout.seconds", "30"));
}

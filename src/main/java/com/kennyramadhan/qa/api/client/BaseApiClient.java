package com.kennyramadhan.qa.api.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Abstract base for endpoint-specific API clients.
 *
 * <p>Provides a {@link #given()} factory that returns a pre-configured
 * RestAssured {@link RequestSpecification} with:</p>
 * <ul>
 *   <li>{@link ApiConfig#BASE_URL} as the base URI;</li>
 *   <li>{@code application/x-www-form-urlencoded} as the default request
 *       content-type (the automationexercise.com API uses form-encoded
 *       POST/PUT/DELETE bodies — JSON requests are rejected);</li>
 *   <li>{@code application/json} as the accepted response type;</li>
 *   <li>An {@link AllureRestAssured} filter so every request/response is
 *       attached to the Allure report as a step trace.</li>
 * </ul>
 *
 * <h2>automationexercise.com response quirk</h2>
 * <p>This API <em>always</em> returns HTTP 200 — even for "errors" like
 * unsupported method (would normally be 405) or missing parameter (would
 * normally be 400). The actual outcome is encoded in the JSON body's
 * {@code responseCode} field. Tests must therefore inspect the body, not
 * the HTTP status, to determine success/failure. See
 * {@link com.kennyramadhan.qa.api.models.ApiResponse}.</p>
 */
public abstract class BaseApiClient {

    /**
     * Build a fresh {@link RequestSpecification} for a single request. Each
     * call returns a new spec so concurrent test threads do not share mutable
     * builder state.
     */
    protected RequestSpecification given() {
        return RestAssured.given()
                .spec(new RequestSpecBuilder()
                        .setBaseUri(ApiConfig.BASE_URL)
                        .setContentType(ContentType.URLENC)
                        .setAccept(ContentType.JSON)
                        .build())
                .filter(new AllureRestAssured());
    }
}

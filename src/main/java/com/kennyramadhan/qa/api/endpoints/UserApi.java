package com.kennyramadhan.qa.api.endpoints;

import com.kennyramadhan.qa.api.client.BaseApiClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;

/**
 * Client for user-detail endpoints (API 14).
 */
public class UserApi extends BaseApiClient {

    /**
     * GET /api/getUserDetailByEmail — look up profile by email address.
     *
     * <p>Returns the raw {@link Response} so tests can branch on the body's
     * {@code responseCode} (200 found, 404 not found).</p>
     */
    @Step("GET /api/getUserDetailByEmail (email={email})")
    public Response getUserByEmail(String email) {
        return given()
                .queryParam("email", email)
                .when().get("/api/getUserDetailByEmail");
    }
}

package com.kennyramadhan.qa.api.endpoints;

import com.kennyramadhan.qa.api.client.BaseApiClient;
import com.kennyramadhan.qa.api.models.CreateAccountRequest;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

/**
 * Client for authentication and account-management endpoints
 * (APIs 7, 8, 9, 10, 11, 12, 13).
 *
 * <p>All method bodies use {@code formParam} because the
 * automationexercise.com API expects {@code application/x-www-form-urlencoded}
 * bodies for POST/PUT/DELETE — the same content-type set as the default
 * in {@link BaseApiClient#given()}.</p>
 */
public class AuthApi extends BaseApiClient {

    /**
     * POST /api/verifyLogin — authenticate with email + password.
     */
    @Step("POST /api/verifyLogin (email={email})")
    public Response verifyLogin(String email, String password) {
        return given()
                .formParam("email", email)
                .formParam("password", password)
                .when().post("/api/verifyLogin");
    }

    /**
     * POST /api/createAccount — register a new account.
     */
    @Step("POST /api/createAccount (email={req.email})")
    public Response createAccount(CreateAccountRequest req) {
        return withAccountForm(given(), req)
                .when().post("/api/createAccount");
    }

    /**
     * DELETE /api/deleteAccount — remove an existing account by credentials.
     */
    @Step("DELETE /api/deleteAccount (email={email})")
    public Response deleteAccount(String email, String password) {
        return given()
                .formParam("email", email)
                .formParam("password", password)
                .when().delete("/api/deleteAccount");
    }

    /**
     * PUT /api/updateAccount — update profile data for an existing account.
     */
    @Step("PUT /api/updateAccount (email={req.email})")
    public Response updateAccount(CreateAccountRequest req) {
        return withAccountForm(given(), req)
                .when().put("/api/updateAccount");
    }

    /**
     * Maps the camelCase record components to their snake_case form-param wire
     * names per automationexercise.com API contract. Wire param names are
     * declared explicitly for every field so the mapping is auditable, even
     * where Java accessor and wire name happen to match.
     */
    private static RequestSpecification withAccountForm(RequestSpecification spec, CreateAccountRequest req) {
        return spec
                .formParam("name", req.name())
                .formParam("email", req.email())
                .formParam("password", req.password())
                .formParam("title", req.title())
                .formParam("birth_date", req.birthDate())
                .formParam("birth_month", req.birthMonth())
                .formParam("birth_year", req.birthYear())
                .formParam("firstname", req.firstName())
                .formParam("lastname", req.lastName())
                .formParam("company", req.company())
                .formParam("address1", req.address1())
                .formParam("address2", req.address2())
                .formParam("country", req.country())
                .formParam("zipcode", req.zipcode())
                .formParam("state", req.state())
                .formParam("city", req.city())
                .formParam("mobile_number", req.mobileNumber());
    }
}

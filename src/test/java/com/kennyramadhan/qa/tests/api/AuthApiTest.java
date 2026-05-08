package com.kennyramadhan.qa.tests.api;

import com.kennyramadhan.qa.api.models.CreateAccountRequest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers auth endpoints: API 7 (verifyLogin), API 8 (verifyLogin missing email → 400),
 * API 9 (DELETE /verifyLogin → 405), API 10 (verifyLogin invalid creds → 404),
 * API 11 (createAccount), API 12 (deleteAccount), API 13 (updateAccount).
 *
 * <h2>Cleanup</h2>
 * <p>Tests that create an account record the email/password in the
 * {@code createdEmail}/{@code createdPassword} fields. {@link #cleanupCreatedAccount()}
 * runs after every method (idempotent: no-op if {@code createdEmail} is null).</p>
 */
@Epic("API")
@Feature("Authentication")
public class AuthApiTest extends BaseApiTest {

    private static final Faker FAKER = new Faker();

    private String createdEmail;
    private String createdPassword;

    @AfterMethod(alwaysRun = true)
    public void cleanupCreatedAccount() {
        if (createdEmail != null) {
            try {
                authApi.deleteAccount(createdEmail, createdPassword);
            } catch (Exception ignored) {
                // best-effort cleanup; account may already be deleted by the test itself
            } finally {
                createdEmail = null;
                createdPassword = null;
            }
        }
    }

    @Test(groups = {"api", "regression"})
    @Severity(SeverityLevel.CRITICAL)
    public void shouldRegisterAndDeleteAccount() {
        CreateAccountRequest req = newAccountRequest();

        Response createResp = authApi.createAccount(req);
        assertThat(createResp.jsonPath().getInt("responseCode")).isEqualTo(201);

        Response deleteResp = authApi.deleteAccount(req.email(), req.password());
        assertThat(deleteResp.jsonPath().getInt("responseCode")).isEqualTo(200);
        // self-cleaned; @AfterMethod is a no-op
    }

    @Test(groups = {"api", "regression"})
    public void shouldVerifyLoginAfterCreateAccount() {
        CreateAccountRequest req = newAccountRequest();
        Response createResp = authApi.createAccount(req);
        assertThat(createResp.jsonPath().getInt("responseCode")).isEqualTo(201);
        createdEmail = req.email();
        createdPassword = req.password();

        Response loginResp = authApi.verifyLogin(req.email(), req.password());
        assertThat(loginResp.jsonPath().getInt("responseCode")).isEqualTo(200);
    }

    @Test(groups = {"api", "regression"})
    public void shouldUpdateAccount() {
        CreateAccountRequest req = newAccountRequest();
        Response createResp = authApi.createAccount(req);
        assertThat(createResp.jsonPath().getInt("responseCode")).isEqualTo(201);
        createdEmail = req.email();
        createdPassword = req.password();

        // updateAccount uses the same form body shape; change a couple of fields
        CreateAccountRequest updated = new CreateAccountRequest(
                req.name(), req.email(), req.password(), req.title(),
                req.birthDate(), req.birthMonth(), req.birthYear(),
                req.firstName(), req.lastName(),
                "Updated Co.", req.address1(), req.address2(),
                req.country(), req.zipcode(), req.state(), "Updated City",
                req.mobileNumber()
        );

        Response updateResp = authApi.updateAccount(updated);
        assertThat(updateResp.jsonPath().getInt("responseCode")).isEqualTo(200);
    }

    @Test(groups = {"api"})
    public void shouldRejectLoginWithInvalidCredentials() {
        Response response = authApi.verifyLogin(
                "nonexistent-" + FAKER.number().digits(10) + "@example.com",
                "wrongPassword");

        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(404);
    }

    @Test(groups = {"api"})
    public void shouldReturn400OnLoginWithoutEmail() {
        Response response = rawRequest()
                .formParam("password", "anything")
                .when().post("/api/verifyLogin");

        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(400);
    }

    @Test(groups = {"api"})
    public void shouldReturn405OnDeleteToVerifyLogin() {
        Response response = rawRequest().when().delete("/api/verifyLogin");

        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(405);
    }

    private static CreateAccountRequest newAccountRequest() {
        String email = "qa-" + FAKER.number().digits(10) + "@example.com";
        return new CreateAccountRequest(
                FAKER.name().fullName(),
                email,
                "Test@123",
                "Mr",
                "1", "1", "1990",
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                "Acme Corp",
                FAKER.address().streetAddress(),
                FAKER.address().secondaryAddress(),
                "United States",
                FAKER.address().zipCode(),
                FAKER.address().state(),
                FAKER.address().city(),
                FAKER.phoneNumber().cellPhone()
        );
    }
}

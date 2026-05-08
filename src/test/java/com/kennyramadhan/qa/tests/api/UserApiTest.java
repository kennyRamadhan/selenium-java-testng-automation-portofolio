package com.kennyramadhan.qa.tests.api;

import com.kennyramadhan.qa.api.models.CreateAccountRequest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers API 14 (GET /api/getUserDetailByEmail).
 *
 * <p>Uses {@link AuthApi} to seed an account before each lookup test, then
 * deletes it in {@link #cleanupCreatedAccount()}. Cleanup is best-effort:
 * if the account was already removed by the test, the second delete fails
 * silently.</p>
 */
@Epic("API")
@Feature("User Detail")
public class UserApiTest extends BaseApiTest {

    private static final Faker FAKER = new Faker();

    private String createdEmail;
    private String createdPassword;

    @AfterMethod(alwaysRun = true)
    public void cleanupCreatedAccount() {
        if (createdEmail != null) {
            try {
                authApi.deleteAccount(createdEmail, createdPassword);
            } catch (Exception ignored) {
                // best-effort cleanup
            } finally {
                createdEmail = null;
                createdPassword = null;
            }
        }
    }

    @Test(groups = {"api", "regression"})
    public void shouldGetUserDetailsByEmailAfterCreateAccount() {
        CreateAccountRequest req = newAccountRequest();
        Response createResp = authApi.createAccount(req);
        assertThat(createResp.jsonPath().getInt("responseCode")).isEqualTo(201);
        createdEmail = req.email();
        createdPassword = req.password();

        Response detailResp = userApi.getUserByEmail(req.email());

        assertThat(detailResp.jsonPath().getInt("responseCode")).isEqualTo(200);
        assertThat(detailResp.jsonPath().getString("user.email")).isEqualTo(req.email());
        assertThat(detailResp.jsonPath().getString("user.name")).isEqualTo(req.name());
    }

    @Test(groups = {"api"})
    public void shouldReturnErrorOnGetUserDetailsForUnknownEmail() {
        Response response = userApi.getUserByEmail(
                "ghost-" + FAKER.number().digits(10) + "@example.com");

        // AE returns responseCode 404 for unknown email
        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(404);
    }

    private static CreateAccountRequest newAccountRequest() {
        String email = "qa-user-" + FAKER.number().digits(10) + "@example.com";
        return new CreateAccountRequest(
                FAKER.name().fullName(),
                email,
                "Test@123",
                "Mrs",
                "15", "6", "1992",
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                "Acme Corp",
                FAKER.address().streetAddress(),
                FAKER.address().secondaryAddress(),
                "Canada",
                FAKER.address().zipCode(),
                FAKER.address().state(),
                FAKER.address().city(),
                FAKER.phoneNumber().cellPhone()
        );
    }
}

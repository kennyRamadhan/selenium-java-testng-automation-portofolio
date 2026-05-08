package com.kennyramadhan.qa.tests.web;

import com.kennyramadhan.qa.api.endpoints.AuthApi;
import com.kennyramadhan.qa.web.models.AccountForm;
import com.kennyramadhan.qa.web.pages.AccountCreatedPage;
import com.kennyramadhan.qa.web.pages.AccountInformationPage;
import com.kennyramadhan.qa.web.pages.SignupLoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end signup flow against the live AE.com web UI.
 *
 * <p>Cleanup uses {@link com.kennyramadhan.qa.api.endpoints.AuthApi#deleteAccount}
 * rather than an AE web "delete account" flow. Rationale: AE does not expose
 * a reliable web delete-account UI without re-login. The API client provides
 * an idempotent DELETE /api/deleteAccount that works regardless of web
 * session state. This is an intentional cross-layer dependency for test
 * hygiene; if the API client signature changes, this test must be updated.</p>
 */
@Epic("Web")
@Feature("Registration")
public class RegisterUserWebTest extends BaseWebTest {

    private static final Logger log = LoggerFactory.getLogger(RegisterUserWebTest.class);
    private static final Faker FAKER = new Faker();

    private String registeredEmail;
    private String registeredPassword;

    @AfterMethod(alwaysRun = true)
    public void cleanupRegisteredAccount() {
        if (registeredEmail != null) {
            try {
                new AuthApi().deleteAccount(registeredEmail, registeredPassword);
            } catch (Exception e) {
                log.warn("Cleanup deleteAccount failed for {}: {}", registeredEmail, e.getMessage());
            } finally {
                registeredEmail = null;
                registeredPassword = null;
            }
        }
    }

    @Test(groups = {"web", "regression"})
    public void shouldRegisterNewUserEndToEnd() {
        String name = FAKER.name().fullName();
        String email = "qa-web-" + FAKER.number().digits(10) + "@example.com";
        String password = "Test@123";

        SignupLoginPage signupLogin = homePage().clickSignupLogin();
        AccountInformationPage info = signupLogin.signupAs(name, email);

        AccountForm form = new AccountForm(
                "Mr",
                password,
                "1", "1", "1990",
                FAKER.name().firstName(),
                FAKER.name().lastName(),
                "Acme Corp",
                FAKER.address().streetAddress(),
                FAKER.address().secondaryAddress(),
                "United States",
                FAKER.address().state(),
                FAKER.address().city(),
                FAKER.address().zipCode(),
                FAKER.phoneNumber().cellPhone()
        );

        AccountCreatedPage created = info.fillAccountForm(form).clickCreateAccount();
        registeredEmail = email;
        registeredPassword = password;

        assertThat(created.isAccountCreatedMessageDisplayed()).isTrue();
    }
}

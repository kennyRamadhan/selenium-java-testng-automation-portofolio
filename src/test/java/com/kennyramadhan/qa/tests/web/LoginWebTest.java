package com.kennyramadhan.qa.tests.web;

import com.kennyramadhan.qa.web.pages.SignupLoginPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import net.datafaker.Faker;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Web")
@Feature("Login")
public class LoginWebTest extends BaseWebTest {

    private static final Faker FAKER = new Faker();

    @Test(groups = {"web", "smoke"})
    @Severity(SeverityLevel.CRITICAL)
    public void shouldDisplayLoginPage() {
        SignupLoginPage page = homePage().clickSignupLogin();

        assertThat(page.isLoginHeaderDisplayed()).isTrue();
        assertThat(page.isSignupHeaderDisplayed()).isTrue();
    }

    @Test(groups = {"web"})
    public void shouldRejectLoginWithInvalidCredentials() {
        SignupLoginPage page = homePage().clickSignupLogin();

        page.submitLogin(
                "ghost-" + FAKER.number().digits(10) + "@example.com",
                "wrongPassword");

        assertThat(page.isLoginErrorDisplayed()).isTrue();
        assertThat(page.getLoginErrorMessage())
                .containsIgnoringCase("Your email or password is incorrect");
    }
}

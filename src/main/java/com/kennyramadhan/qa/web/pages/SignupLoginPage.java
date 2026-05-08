package com.kennyramadhan.qa.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** {@code /login} — combined signup + login page. */
public class SignupLoginPage extends BaseWebPage {

    private static final By LOGIN_HEADER = By.xpath("//h2[normalize-space()='Login to your account']");
    private static final By SIGNUP_HEADER = By.xpath("//h2[normalize-space()='New User Signup!']");

    private static final By SIGNUP_NAME = By.cssSelector("input[data-qa='signup-name']");
    private static final By SIGNUP_EMAIL = By.cssSelector("input[data-qa='signup-email']");
    private static final By SIGNUP_BUTTON = By.cssSelector("button[data-qa='signup-button']");

    private static final By LOGIN_EMAIL = By.cssSelector("input[data-qa='login-email']");
    private static final By LOGIN_PASSWORD = By.cssSelector("input[data-qa='login-password']");
    private static final By LOGIN_BUTTON = By.cssSelector("button[data-qa='login-button']");

    private static final By LOGIN_ERROR = By.xpath("//p[contains(., 'Your email or password is incorrect')]");

    public boolean isLoginHeaderDisplayed() {
        return isDisplayed(LOGIN_HEADER);
    }

    public boolean isSignupHeaderDisplayed() {
        return isDisplayed(SIGNUP_HEADER);
    }

    @Step("Begin signup as name={name}, email={email}")
    public AccountInformationPage signupAs(String name, String email) {
        safeSendKeys(SIGNUP_NAME, name);
        safeSendKeys(SIGNUP_EMAIL, email);
        safeClick(SIGNUP_BUTTON);
        return new AccountInformationPage();
    }

    @Step("Login as email={email}")
    public HomePage loginAs(String email, String password) {
        safeSendKeys(LOGIN_EMAIL, email);
        safeSendKeys(LOGIN_PASSWORD, password);
        safeClick(LOGIN_BUTTON);
        return new HomePage();
    }

    /**
     * Submit credentials without navigating away. Useful for negative-path
     * tests that expect to remain on /login with an error banner.
     */
    @Step("Submit login attempt: email={email}")
    public void submitLogin(String email, String password) {
        safeSendKeys(LOGIN_EMAIL, email);
        safeSendKeys(LOGIN_PASSWORD, password);
        safeClick(LOGIN_BUTTON);
    }

    public boolean isLoginErrorDisplayed() {
        return isDisplayed(LOGIN_ERROR);
    }

    public String getLoginErrorMessage() {
        return getText(LOGIN_ERROR);
    }
}

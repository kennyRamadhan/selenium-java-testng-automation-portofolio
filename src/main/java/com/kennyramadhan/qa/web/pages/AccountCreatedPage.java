package com.kennyramadhan.qa.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** {@code /account_created} — confirmation page after successful signup. */
public class AccountCreatedPage extends BaseWebPage {

    private static final By ACCOUNT_CREATED_HEADER =
            By.xpath("//h2[@data-qa='account-created']");
    private static final By CONTINUE_BUTTON = By.cssSelector("a[data-qa='continue-button']");

    public boolean isAccountCreatedMessageDisplayed() {
        return isDisplayed(ACCOUNT_CREATED_HEADER);
    }

    @Step("Click 'Continue'")
    public HomePage clickContinue() {
        safeClick(CONTINUE_BUTTON);
        return new HomePage();
    }
}

package com.kennyramadhan.qa.web.pages;

import com.kennyramadhan.qa.web.models.AccountForm;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;

/** {@code /signup} — second-step account-info form. */
public class AccountInformationPage extends BaseWebPage {

    private static final By TITLE_MR = By.id("id_gender1");
    private static final By TITLE_MRS = By.id("id_gender2");
    private static final By PASSWORD = By.id("password");
    private static final By DAYS = By.id("days");
    private static final By MONTHS = By.id("months");
    private static final By YEARS = By.id("years");
    private static final By FIRST_NAME = By.id("first_name");
    private static final By LAST_NAME = By.id("last_name");
    private static final By COMPANY = By.id("company");
    private static final By ADDRESS1 = By.id("address1");
    private static final By ADDRESS2 = By.id("address2");
    private static final By COUNTRY = By.id("country");
    private static final By STATE = By.id("state");
    private static final By CITY = By.id("city");
    private static final By ZIPCODE = By.id("zipcode");
    private static final By MOBILE = By.id("mobile_number");
    private static final By CREATE_ACCOUNT_BTN = By.cssSelector("button[data-qa='create-account']");

    @Step("Fill account info form for firstName={form.firstName}")
    public AccountInformationPage fillAccountForm(AccountForm form) {
        if ("Mr".equalsIgnoreCase(form.title())) {
            safeClick(TITLE_MR);
        } else {
            safeClick(TITLE_MRS);
        }
        safeSendKeys(PASSWORD, form.password());
        new Select(driver.findElement(DAYS)).selectByValue(form.day());
        new Select(driver.findElement(MONTHS)).selectByValue(form.month());
        new Select(driver.findElement(YEARS)).selectByValue(form.year());
        safeSendKeys(FIRST_NAME, form.firstName());
        safeSendKeys(LAST_NAME, form.lastName());
        safeSendKeys(COMPANY, form.company());
        safeSendKeys(ADDRESS1, form.address());
        safeSendKeys(ADDRESS2, form.address2());
        new Select(driver.findElement(COUNTRY)).selectByVisibleText(form.country());
        safeSendKeys(STATE, form.state());
        safeSendKeys(CITY, form.city());
        safeSendKeys(ZIPCODE, form.zipcode());
        safeSendKeys(MOBILE, form.mobileNumber());
        return this;
    }

    @Step("Click 'Create Account'")
    public AccountCreatedPage clickCreateAccount() {
        safeClick(CREATE_ACCOUNT_BTN);
        return new AccountCreatedPage();
    }
}

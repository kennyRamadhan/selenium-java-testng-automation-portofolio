package com.kennyramadhan.qa.mobile.pages;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.core.reporting.LogHelper;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;

/**
 * Page object for the SwagLabs login screen.
 *
 * <p>
 * Phase 3 refactor: PageFactory dropped in favor of explicit {@code By} locator
 * constants and {@code driver.findElement(...)} at use site.
 */
public class Login {

	/**
	 * SwagLabs canonical test credentials. Current APK 2.7.1 no longer renders the
	 * in-page quick-login user buttons; tests type credentials directly.
	 */
	private static final Map<String, String> CREDENTIALS = Map.of("standard_user", "secret_sauce", "locked_out_user",
			"secret_sauce", "problem_user", "secret_sauce");

	private static final By USERNAME_FIELD = AppiumBy.accessibilityId("test-Username");
	private static final By PASSWORD_FIELD = AppiumBy.accessibilityId("test-Password");
	private static final By LOGIN_BTN = AppiumBy.accessibilityId("test-LOGIN");
	private static final By ERROR_MSG = AppiumBy.accessibilityId("test-Error message");

	private static final By PRODUCTS_LIST_ANDROID = AppiumBy.accessibilityId("test-PRODUCTS");
	private static final By PRODUCTS_LIST_IOS = AppiumBy
			.iOSNsPredicateString("name == 'assets/src/img/swag-labs-logo.png'");

	private final AppiumDriver driver;

	public Login() {
		this.driver = DriverManager.getDriver();
	}

	private boolean isIOS() {
		return "iOS".equalsIgnoreCase(String.valueOf(driver.getCapabilities().getCapability("platformName")));
	}

	/**
	 * Waits up to 15s for the element to be visible, then returns it. Replaces
	 * PageFactory's implicit per-field wait that the AppiumFieldDecorator provided
	 * before this refactor.
	 */
	private WebElement waitFor(By locator) {
		return new WebDriverWait(driver, Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Logs in using a SwagLabs canonical test username. Looks up the matching
	 * password from {@link #CREDENTIALS}, types both into the credential fields,
	 * and submits.
	 *
	 * @param username
	 *            one of "standard_user", "locked_out_user", "problem_user"
	 */
	public void getAutoCredentials(String username) {
		String password = CREDENTIALS.get(username.toLowerCase());
		if (password == null) {
			Assert.fail("Username '" + username + "' tidak dikenali!");
			return;
		}

		LogHelper.step("Get Credentials");
		waitFor(USERNAME_FIELD).sendKeys(username);
		waitFor(PASSWORD_FIELD).sendKeys(password);
		waitFor(LOGIN_BTN).click();
		LogHelper.detail("Selected User " + username);

		By productsLocator = isIOS() ? PRODUCTS_LIST_IOS : PRODUCTS_LIST_ANDROID;
		if (waitFor(productsLocator).isDisplayed()) {
			LogHelper.detail("Succesfully Login");
		} else {
			LogHelper.detail("Failed Login");
			Assert.fail("Login validation failed - PRODUCT list not visible");
		}
	}

	/**
	 * Logs in with explicit username and password. Forward-compatible wrapper for
	 * tests that want explicit credentials.
	 */
	public void loginAs(String username, String password) {
		waitFor(USERNAME_FIELD).sendKeys(username);
		waitFor(PASSWORD_FIELD).sendKeys(password);
		waitFor(LOGIN_BTN).click();
	}

	public void setManualCredentials(String username, String password) {
		LogHelper.step("Input Username");
		waitFor(USERNAME_FIELD).sendKeys(username);
		LogHelper.detail("Username Yang Diinput" + username);

		LogHelper.step("Input Password");
		waitFor(PASSWORD_FIELD).sendKeys(password);
		LogHelper.detail("Berhasil Input Password");

		LogHelper.step("Tap Login Button");
		waitFor(LOGIN_BTN).click();
		LogHelper.detail("Success Tap Login Button");

		if (waitFor(ERROR_MSG).isDisplayed()) {
			LogHelper.step("Verify Negative Login ");
			LogHelper.detail("Succesfully Negative Case");
		} else {
			LogHelper.step("Verify Negative Login");
			LogHelper.detail("Login Success");
			Assert.fail("Verify Negative Login failed");
		}
	}
}

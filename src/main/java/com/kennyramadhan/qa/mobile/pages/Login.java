package com.kennyramadhan.qa.mobile.pages;

import java.time.Duration;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.core.reporting.LogHelper;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;

/**
 * Page object for the SwagLabs login screen.
 *
 * <p>
 * Phase 3 refactor: PageFactory dropped in favor of explicit {@code By} locator
 * constants and {@code driver.findElement(...)} at use site. Phase 3 commit 3:
 * assertions removed; page object exposes state via getters, tests invoke
 * assertions.
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

	private WebElement waitFor(By locator) {
		return new WebDriverWait(driver, Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Logs in using a SwagLabs canonical test username. Looks up the matching
	 * password from {@link #CREDENTIALS}, types both into the credential fields,
	 * and submits.
	 *
	 * <p>
	 * Does not assert post-login state — call {@link #isProductsListVisible()} from
	 * the test class for that.
	 *
	 * @param username
	 *            one of "standard_user", "locked_out_user", "problem_user"
	 * @throws IllegalArgumentException
	 *             if username not in CREDENTIALS map
	 */
	public void getAutoCredentials(String username) {
		String password = CREDENTIALS.get(username.toLowerCase());
		if (password == null) {
			throw new IllegalArgumentException("Unknown username: " + username);
		}

		LogHelper.step("Get Credentials");
		waitFor(USERNAME_FIELD).sendKeys(username);
		waitFor(PASSWORD_FIELD).sendKeys(password);
		waitFor(LOGIN_BTN).click();
		LogHelper.detail("Selected User " + username);
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

	/**
	 * Types the given credentials and submits the login form. Used for
	 * negative-case tests where the credentials are expected to be rejected. Does
	 * not assert; call {@link #isErrorMessageDisplayed()} from the test class to
	 * verify the negative-path outcome.
	 */
	public void setManualCredentials(String username, String password) {
		LogHelper.step("Input Username");
		waitFor(USERNAME_FIELD).sendKeys(username);
		LogHelper.detail("Username entered: " + username);

		LogHelper.step("Input Password");
		waitFor(PASSWORD_FIELD).sendKeys(password);
		LogHelper.detail("Password entered");

		LogHelper.step("Tap Login Button");
		waitFor(LOGIN_BTN).click();
		LogHelper.detail("Login button tapped");
	}

	/**
	 * Returns true when the products list landmark element is visible on screen,
	 * indicating successful login navigation.
	 */
	public boolean isProductsListVisible() {
		By productsLocator = isIOS() ? PRODUCTS_LIST_IOS : PRODUCTS_LIST_ANDROID;
		try {
			return waitFor(productsLocator).isDisplayed();
		} catch (NoSuchElementException | org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}

	/**
	 * Returns true when the login-error message is visible, used to verify
	 * negative-case login flows.
	 */
	public boolean isErrorMessageDisplayed() {
		try {
			return waitFor(ERROR_MSG).isDisplayed();
		} catch (NoSuchElementException | org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}
}

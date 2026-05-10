package com.kennyramadhan.qa.mobile.pages;

import java.util.Map;

import org.openqa.selenium.By;

import com.kennyramadhan.qa.core.reporting.LogHelper;

import io.appium.java_client.AppiumBy;

/**
 * Page object for the SwagLabs login screen.
 *
 * <p>
 * Phase 3 commit 4: extends {@link BaseMobilePage} for shared driver + waitFor
 * + per-action screenshot helpers.
 */
public class Login extends BaseMobilePage {

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

	/**
	 * Logs in using a SwagLabs canonical test username. Looks up the matching
	 * password from {@link #CREDENTIALS}, types both into the credential fields,
	 * and submits.
	 *
	 * <p>
	 * Does not assert post-login state — call {@link #isProductsListVisible()} from
	 * the test class for that.
	 *
	 * @throws IllegalArgumentException
	 *             if username not in CREDENTIALS map
	 */
	public void getAutoCredentials(String username) {
		String password = CREDENTIALS.get(username.toLowerCase());
		if (password == null) {
			throw new IllegalArgumentException("Unknown username: " + username);
		}

		LogHelper.step("Get Credentials");
		safeSendKeys(USERNAME_FIELD, username);
		safeSendKeys(PASSWORD_FIELD, password);
		safeClick(LOGIN_BTN);
		LogHelper.detail("Selected User " + username);
	}

	/**
	 * Logs in with explicit username and password. Forward-compatible wrapper for
	 * tests that want explicit credentials.
	 */
	public void loginAs(String username, String password) {
		safeSendKeys(USERNAME_FIELD, username);
		safeSendKeys(PASSWORD_FIELD, password);
		safeClick(LOGIN_BTN);
	}

	/**
	 * Types the given credentials and submits the login form. Used for
	 * negative-case tests where the credentials are expected to be rejected. Does
	 * not assert; call {@link #isErrorMessageDisplayed()} from the test class to
	 * verify the negative-path outcome.
	 */
	public void setManualCredentials(String username, String password) {
		LogHelper.step("Input Username");
		safeSendKeys(USERNAME_FIELD, username);
		LogHelper.detail("Username entered: " + username);

		LogHelper.step("Input Password");
		safeSendKeys(PASSWORD_FIELD, password);
		LogHelper.detail("Password entered");

		LogHelper.step("Tap Login Button");
		safeClick(LOGIN_BTN);
		LogHelper.detail("Login button tapped");
	}

	/**
	 * Returns true when the products list landmark element is visible on screen,
	 * indicating successful login navigation.
	 */
	public boolean isProductsListVisible() {
		return isDisplayedQuiet(isIOS() ? PRODUCTS_LIST_IOS : PRODUCTS_LIST_ANDROID);
	}

	/**
	 * Returns true when the login-error message is visible, used to verify
	 * negative-case login flows.
	 */
	public boolean isErrorMessageDisplayed() {
		return isDisplayedQuiet(ERROR_MSG);
	}
}

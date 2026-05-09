package com.kennyramadhan.qa.mobile.pages;

// TODO Phase 3: Bahasa Indonesia content pending — translation absorbed into Phase 3.1 rewrite.

import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.core.reporting.LogHelper;
import com.kennyramadhan.qa.core.waits.WaitHelpers;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class Login {

	/**
	 * SwagLabs canonical test credentials. The current APK no longer renders the
	 * in-page quick-login user buttons (test-standard_user, etc.); tests type
	 * credentials directly via {@link #getAutoCredentials(String)}.
	 */
	private static final Map<String, String> CREDENTIALS = Map.of("standard_user", "secret_sauce", "locked_out_user",
			"secret_sauce", "problem_user", "secret_sauce");

	@SuppressWarnings("unused")
	private WaitHelpers helper;

	public Login() {

		this.helper = new WaitHelpers();
		PageFactory.initElements(new AppiumFieldDecorator(DriverManager.getDriver()), this);
	}

	@AndroidFindBy(accessibility = "test-Username")
	@iOSXCUITFindBy(accessibility = "test-Username")
	private WebElement usernameField;

	@AndroidFindBy(accessibility = "test-Password")
	@iOSXCUITFindBy(accessibility = "test-Password")
	private WebElement passwordField;

	@AndroidFindBy(accessibility = "test-LOGIN")
	@iOSXCUITFindBy(accessibility = "test-LOGIN")
	private WebElement submitBtn;

	@AndroidFindBy(accessibility = "test-Error message")
	@iOSXCUITFindBy(accessibility = "test-Error message")
	private WebElement alertMessage;

	@AndroidFindBy(accessibility = "test-PRODUCTS")
	@iOSXCUITFindBy(iOSNsPredicate = "name == 'assets/src/img/swag-labs-logo.png'")
	private WebElement listProducts;

	/**
	 * Logs in using a SwagLabs canonical test username. Looks up the matching
	 * password from {@link #CREDENTIALS}, types both into the credential fields,
	 * and submits.
	 *
	 * <p>
	 * Replaces the prior quick-login-button approach: the current SwagLabs APK no
	 * longer renders the in-page user buttons, so credentials must be typed.
	 *
	 * @param username
	 *            one of "standard_user", "locked_out_user", "problem_user"
	 * @throws IllegalArgumentException
	 *             if the username is not in CREDENTIALS
	 */
	public void getAutoCredentials(String username) {
		String password = CREDENTIALS.get(username.toLowerCase());
		if (password == null) {
			Assert.fail("Username '" + username + "' tidak dikenali!");
			return;
		}

		LogHelper.step("Get Credentials");
		usernameField.sendKeys(username);
		passwordField.sendKeys(password);
		submitBtn.click();
		LogHelper.detail("Selected User " + username);

		if (listProducts.isDisplayed()) {
			LogHelper.detail("Succesfully Login");
		} else {
			LogHelper.detail("Failed Login");
			Assert.fail("Login validation failed - PRODUCT list not visible");
		}
	}

	/**
	 * Logs in with explicit username and password. Forward-compatible thin wrapper
	 * for tests that want explicit credentials rather than the canonical SwagLabs
	 * aliases.
	 */
	public void loginAs(String username, String password) {
		usernameField.sendKeys(username);
		passwordField.sendKeys(password);
		submitBtn.click();
	}

	public void setManualCredentials(String username, String password) {

		LogHelper.step("Input Username");
		usernameField.sendKeys(username);
		LogHelper.detail("Username Yang Diinput" + username);

		LogHelper.step("Input Password");
		passwordField.sendKeys(password);
		LogHelper.detail("Berhasil Input Password");

		LogHelper.step("Tap Login Button");
		submitBtn.click();
		LogHelper.detail("Success Tap Login Button");

		if (alertMessage.isDisplayed()) {

			LogHelper.step("Verify Negative Login ");
			LogHelper.detail("Succesfully Negative Case");
		} else {
			LogHelper.step("Verify Negative Login");
			LogHelper.detail("Login Success");
			Assert.fail("Verify Negative Login failed");
		}
	}

}

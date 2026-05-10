package com.kennyramadhan.qa.tests.mobile;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.kennyramadhan.qa.core.listeners.MobileRetryListener;
import com.kennyramadhan.qa.mobile.pages.Login;

/**
 * Mobile login flow tests.
 *
 * <p>
 * Phase 3 commit 2 split: extracted from E2EMobileTest. Covers the login-screen
 * interactions only; product/cart/checkout flows live in
 * {@link ProductsAndCartTests} and {@link CheckoutTests}.
 */
@Listeners(MobileRetryListener.class)
public class LoginTests extends BaseMobileTest {

	private Login login;

	@BeforeMethod
	public void setupPages() {
		login = new Login();
	}

	@Test(priority = 1)
	public void login() throws MalformedURLException, URISyntaxException {
		login.getAutoCredentials("standard_user");
		Assert.assertTrue(login.isProductsListVisible(), "Login should land on products list");
	}

	@Test(priority = 2)
	public void failedLogin() throws MalformedURLException, URISyntaxException {
		login.setManualCredentials("Wrong Username", "Wrong Password");
		Assert.assertTrue(login.isErrorMessageDisplayed(), "Negative login should show error message");
	}
}

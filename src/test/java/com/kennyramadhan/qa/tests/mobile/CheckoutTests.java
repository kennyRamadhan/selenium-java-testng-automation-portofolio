package com.kennyramadhan.qa.tests.mobile;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.kennyramadhan.qa.mobile.pages.CartCheckout;
import com.kennyramadhan.qa.mobile.pages.Login;
import com.kennyramadhan.qa.mobile.pages.ProductsList;

/**
 * Mobile checkout flow tests.
 *
 * <p>
 * Phase 3 commit 2 split: extracted from E2EMobileTest. Covers the
 * checkout-form positive and negative paths.
 */
public class CheckoutTests extends BaseMobileTest {

	private Login login;
	private ProductsList productsList;
	private CartCheckout cart;

	@BeforeMethod
	public void setupPages() {
		login = new Login();
		productsList = new ProductsList();
		cart = new CartCheckout();
	}

	@Test(priority = 1)
	public void checkoutPositiveFlow() throws MalformedURLException, URISyntaxException {
		login.getAutoCredentials("standard_user");

		productsList.addMultipleProducts();
		productsList.tapCart();

		Double expectedTotalPrice = cart.getTotalPriceBeforeCheckout();

		cart.checkoutInformation("Kenzi", "Ackerman", "123123");

		double actualTotalPrice = cart.getItemTotal();

		Assert.assertEquals(expectedTotalPrice, actualTotalPrice);

		cart.clickFinishBtn();

		cart.verifyOrderComplete();
	}

	@Test(priority = 2)
	public void checkoutNegativeFlow() throws MalformedURLException, URISyntaxException {
		login.getAutoCredentials("standard_user");

		productsList.addMultipleProducts();
		productsList.tapCart();

		cart.checkoutInformation("", "Ackerman", "123123");

		cart.verifyErrorMessage();
	}
}

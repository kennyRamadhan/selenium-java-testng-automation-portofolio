package com.kennyramadhan.qa.tests.mobile;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.kennyramadhan.qa.core.listeners.MobileRetryListener;
import com.kennyramadhan.qa.core.waits.WaitHelpers;
import com.kennyramadhan.qa.mobile.pages.CartCheckout;
import com.kennyramadhan.qa.mobile.pages.Login;
import com.kennyramadhan.qa.mobile.pages.ProductsDetail;
import com.kennyramadhan.qa.mobile.pages.ProductsList;

/**
 * Mobile products-list and cart-operation tests.
 *
 * <p>
 * Phase 3 commit 2 split: extracted from E2EMobileTest. Products and cart tests
 * grouped together because every cart operation in the current suite traverses
 * the products list (login → products → cart → action), making them inseparable
 * feature flows in this app.
 */
@Listeners(MobileRetryListener.class)
public class ProductsAndCartTests extends BaseMobileTest {

	private Login login;
	private ProductsList productsList;
	private ProductsDetail detailProducts;
	private CartCheckout cart;

	@BeforeMethod
	public void setupPages() {
		login = new Login();
		productsList = new ProductsList();
		detailProducts = new ProductsDetail();
		cart = new CartCheckout();
	}

	@Test(priority = 1)
	public void sortingPrice() throws MalformedURLException, URISyntaxException {
		login.getAutoCredentials("standard_user");
		Assert.assertTrue(login.isProductsListVisible(), "Login should land on products list");

		List<Double> before = WaitHelpers.extractPrices(productsList.getPriceElements(), "Before Sorting");

		productsList.clickFilterBtn();
		productsList.clickLowToHigh();

		List<Double> after = WaitHelpers.extractPrices(productsList.getPriceElements(), "After Sorting");

		Assert.assertTrue(WaitHelpers.isSortingChanged(before, after), "Product order should change after sorting");
		Assert.assertTrue(WaitHelpers.isSortingOrderValid(after), "Product order should be ascending or descending");
	}

	@Test(priority = 2)
	public void addAllProductsFromListing() throws MalformedURLException, URISyntaxException {
		login.getAutoCredentials("standard_user");
		Assert.assertTrue(login.isProductsListVisible(), "Login should land on products list");

		productsList.addAllProducts();
	}

	@Test(priority = 3)
	public void addProductsFromDetails() throws MalformedURLException, URISyntaxException {
		login.getAutoCredentials("standard_user");
		Assert.assertTrue(login.isProductsListVisible(), "Login should land on products list");

		productsList.selectProducts("Sauce Labs Onesie");

		detailProducts.addToCartFromDetailsProducts();

		String cartProducts = detailProducts.getDetailsProducts();
		Assert.assertTrue(cartProducts.contains("Sauce Labs Onesie"),
				"Product details should reference the selected item");
	}

	@Test(priority = 4)
	public void addMultipleProducts() throws MalformedURLException, URISyntaxException {
		login.getAutoCredentials("standard_user");
		Assert.assertTrue(login.isProductsListVisible(), "Login should land on products list");

		productsList.addMultipleProducts();
	}

	@Test(priority = 5)
	public void redirectionToProductListing() throws MalformedURLException, URISyntaxException {
		login.getAutoCredentials("standard_user");
		Assert.assertTrue(login.isProductsListVisible(), "Login should land on products list");

		productsList.addMultipleProducts();

		productsList.tapCart();
		cart.clickContinueShoppingBtn();

		Assert.assertTrue(productsList.isProductsTitleDisplayed(), "Continue shopping should return to products list");
	}

	@Test(priority = 6)
	public void verifyDetailsProductIntoCart() throws MalformedURLException, URISyntaxException {
		login.getAutoCredentials("standard_user");
		Assert.assertTrue(login.isProductsListVisible(), "Login should land on products list");

		productsList.selectProducts("Sauce Labs Onesie");

		detailProducts.addToCartFromDetailsProducts();

		String cartProducts = detailProducts.getDetailsProducts();
		Assert.assertTrue(cartProducts.contains("Sauce Labs Onesie"),
				"Product details should reference the selected item");
	}
}

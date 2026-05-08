package com.kennyramadhan.qa.tests.mobile;

// TODO Phase 3: Bahasa Indonesia content pending — translation absorbed into Phase 3.2 test split.

import java.net.MalformedURLException;
import net.datafaker.Faker;

import com.kennyramadhan.qa.mobile.pages.ProductsList;
import com.kennyramadhan.qa.core.waits.WaitHelpers;
import com.kennyramadhan.qa.mobile.pages.CartCheckout;
import com.kennyramadhan.qa.mobile.pages.Login;
import com.kennyramadhan.qa.mobile.pages.ProductsDetail;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class E2EMobileTest extends BaseMobileTest {

	private Login login;
	private ProductsList productsList;
	private ProductsDetail detailProducts;
	private CartCheckout cart;
	@SuppressWarnings("unused")
	private WaitHelpers helper;

	/**
	 * Inisialisasi page object sebelum setiap test dijalankan.
	 */
	@BeforeMethod
	public void setupPages() {
		login = new Login();
		productsList = new ProductsList();
		helper = new WaitHelpers();
		detailProducts = new ProductsDetail();
		cart = new CartCheckout();

	}

	Faker faker = new Faker(Locale.of("id", "ID"));

	@Test(priority = 1) // a. Verify user login using given credentials.
	public void login() throws MalformedURLException, URISyntaxException {

		// Login using given credentials
		login.getAutoCredentials("standard_user");
		productsList.tapCart();
	}

	@Test(priority = 2) // b. Verify failure message in case of invalid login credentials are entered by
						// the user.
	public void failedLogin() throws MalformedURLException, URISyntaxException {

		// login with input invalid credentials
		login.setManualCredentials("Wrong Username", "Wrong Password");
	}

	@Test(priority = 3) // c. Verify Price (high to low) and (low to high) filter are working as
						// expected.
	public void sortingPrice() throws MalformedURLException, URISyntaxException {

		// Login using given credentials
		login.getAutoCredentials("standard_user");

		// Ambil harga sebelum sorting
		List<Double> before = WaitHelpers.extractPrices(productsList.getPriceElements(), "Before Sorting");

		productsList.clickFilterBtn();
		productsList.clickLowToHigh();

		// Ambil harga setelah sorting
		List<Double> after = WaitHelpers.extractPrices(productsList.getPriceElements(), "After Sorting");

		// Verifikasi urutan berubah
		WaitHelpers.verifySortingChanged(before, after);

		// Verifikasi urutan valid (ascending atau descending)
		WaitHelpers.verifySortingOrder(after);

	}

	@Test(priority = 4) // d. Verify user can add products to cart from the product listing screen.
	public void addAllProductsFromListing() throws MalformedURLException, URISyntaxException {

		// Login using given credentials
		login.getAutoCredentials("standard_user");

		// Add Products Directly From List products
		productsList.addAllProducts();
		;
	}

	@Test(priority = 5) // e. Verify user can add products to cart from the product details screen.
	public void addProductsFromDetails() throws MalformedURLException, URISyntaxException {

		login.getAutoCredentials("standard_user");

		// choose product
		productsList.selectProducts("Sauce Labs Onesie");

		// Add To Cart From Details Products
		detailProducts.addToCartFromDetailsProducts();

		// Verify Detail Products
		String cartProducts = detailProducts.getDetailsProducts();
		Assert.assertTrue(cartProducts.contains("Sauce Labs Onesie"));

	}

	@Test(priority = 6) // f. Verify user can add multiple products in the cart at a time.
	public void addMultipleProducts() throws MalformedURLException, URISyntaxException {

		// Login using given credentials
		login.getAutoCredentials("standard_user");

		// add multiples products to cart
		productsList.addMultipleProducts();
	}

	@Test(priority = 7) // g. Verify user redirection to product listing screen by tapping on continue
			// shipping option in the cart section.
	public void redirectionToProductListing() throws MalformedURLException, URISyntaxException {

		// Login using given credentials
		login.getAutoCredentials("standard_user");

		// add multiples products to cart
		productsList.addMultipleProducts();

		productsList.tapCart();
		cart.clickContinueShoppingBtn();

		// Verify user redirection to product listing screen
		productsList.verifyBackToListProducts();

	}

	@Test(priority =8) // h. Verify details of the product added into the cart.
	public void verifyDetailsProductIntoCart() throws MalformedURLException, URISyntaxException {

		// Login using given credentials
		login.getAutoCredentials("standard_user");

		// choose product
		productsList.selectProducts("Sauce Labs Onesie");

		// Add To Cart From Details Products
		detailProducts.addToCartFromDetailsProducts();

		// Verify Detail Products
		String cartProducts = detailProducts.getDetailsProducts();
		Assert.assertTrue(cartProducts.contains("Sauce Labs Onesie"));
	}

	@Test(priority=9) // i. Verify Checkout flow till success with valid user information along with
			// necessary verifications.
	public void checkoutPositiveFlow() throws MalformedURLException, URISyntaxException {

		// Login using given credentials
		login.getAutoCredentials("standard_user");

		productsList.addMultipleProducts();
		productsList.tapCart();

		// Get total price before checkout
		Double expectedTotalPrice = cart.getTotalPriceBeforeCheckout();

		// checkout
		cart.checkoutInformation("Kenzi", "Ackerman", "123123");

		// Get actual total amount
		double actualTotalPrice = cart.getItemTotal();

		// Verify if expected total amount is matched with actual total amount is
		// matched (Note : not includes tax)
		Assert.assertEquals(expectedTotalPrice, actualTotalPrice);

		// Finish Order
		cart.clickFinishBtn();

		// verify user get success message after complete order flow
		cart.verifyOrderComplete();
	}

	@Test(priority=10) // j. Verify Checkout flow with invalid user First Name
	public void checkoutNegativeFlow() throws MalformedURLException, URISyntaxException {

		// Login using given credentials
		login.getAutoCredentials("standard_user");
		
		productsList.addMultipleProducts();
		productsList.tapCart();

		cart.checkoutInformation("", "Ackerman", "123123");

		// Verify if user get alert message after input invalid information needed
		cart.verifyErrorMessage();
	}

}

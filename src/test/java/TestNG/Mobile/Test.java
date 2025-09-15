package TestNG.Mobile;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;

import Selenium.CustomHelper.UtilsHelper;
import Selenium.Pages.CartCheckout;
import Selenium.Pages.Login;
import Selenium.Pages.ProductsDetail;
import Selenium.Pages.ProductsList;


public class Test extends BaseTest {

	
	private Login login;
	private ProductsList productsList;
	private ProductsDetail detailProducts;
	private CartCheckout cart;
	@SuppressWarnings("unused")
	private UtilsHelper helper;
	
	
	@BeforeMethod
	public void setupPages() {
	 login = new Login();
	 productsList = new ProductsList();
	 helper = new UtilsHelper();
	 detailProducts = new ProductsDetail();
	 cart = new CartCheckout();

	}
	
	
//	@org.testng.annotations.Test
//	public void addProductsFromDetails() throws MalformedURLException, URISyntaxException {
//		
//		
//		
//		login.getAutoCredentials("standard_user");
//		
//		// choose product
//		 productsList.selectProducts("Sauce Labs Backpack");
//		
//		// Add To Cart From Details Products
//		detailProducts.addToCartFromDetailsProducts();
//		
//		// Verify Detail Products
//		String cartProducts = detailProducts.getDetailsProducts();
//		Assert.assertTrue(cartProducts.contains("Sauce Labs Backpack"));
//		
//		
//	
//	}
	
	@org.testng.annotations.Test // f. Verify user can add multiple products in the cart at a time.
	public void addMultipleProducts() throws MalformedURLException, URISyntaxException {
		
	
		// Login using given credentials
		login.getAutoCredentials("standard_user");
		productsList.addMultipleProducts();
		productsList.tapCart();

		cart.checkoutInformation("", "Ackerman", "123123");

		// Verify if user get alert message after input invalid information needed
		cart.verifyErrorMessage();
				
	}

	
	
}

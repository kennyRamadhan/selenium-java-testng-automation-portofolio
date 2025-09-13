package TestNG.Mobile;
import java.net.MalformedURLException;
import com.github.javafaker.Faker;


import Selenium.Pages.ProductsList;
import Selenium.CustomHelper.UtilsHelper;
import Selenium.Pages.Login;
import Selenium.Pages.ProductsDetail;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class e2eMobile extends BaseTest {
	
	private Login login;
	private ProductsList productsList;
	private ProductsDetail detailProducts;
	@SuppressWarnings("unused")
	private UtilsHelper helper;
	/**
	 * Inisialisasi page object sebelum setiap test dijalankan.
	 */
	@BeforeMethod
	public void setupPages() {
	 login = new Login();
	 productsList = new ProductsList();
	 helper = new UtilsHelper();
	 detailProducts = new ProductsDetail();

	}
	
	@SuppressWarnings("deprecation")
	Faker faker = new Faker(new Locale("id_ID"));  
	@Test(priority=1) //a. Verify user login using given credentials.
	public void login() throws MalformedURLException, URISyntaxException  {
		
		// Login using given credentials
		login.getAutoCredentials("standard_user");
		productsList.tapCart();
	}

	@Test(priority=2) // b. Verify failure message in case of invalid login credentials are entered by the user.
	public void failedLogin() throws MalformedURLException, URISyntaxException {
		
		// login with input invalid credentials
		login.setManualCredentials("Wrong Username", "Wrong Password");
	}
	
	@Test(priority=3) // c. Verify Price (high to low) and (low to high) filter are working as expected.
	public void sortingPrice() throws MalformedURLException, URISyntaxException  {
		
		// Login using given credentials
		login.getAutoCredentials("standard_user");
		
		
		 // Ambil harga sebelum sorting
	    List<Double> before = UtilsHelper.extractPrices(productsList.getPriceElements(), "Before Sorting");
	    
	    productsList.clickFilterBtn();
	    productsList.clickLowToHigh();
	    
	    // Ambil harga setelah sorting
	    List<Double> after = UtilsHelper.extractPrices(productsList.getPriceElements(), "After Sorting");
	    
	    // Verifikasi urutan berubah
	    UtilsHelper.verifySortingChanged(before, after);
	    
	    // Verifikasi urutan valid (ascending atau descending)
	    UtilsHelper.verifySortingOrder(after);
		 
		 
			
	}
	
	@Test(priority=4) // d. Verify user can add products to cart from the product listing screen.
	public void addProductsFromListing() throws MalformedURLException, URISyntaxException {
		
		// Login using given credentials
		login.getAutoCredentials("standard_user");
	
		// Add Products Directly From List products 
		productsList.addAllProducts();;
	}
	
	@Test // e. Verify user can add products to cart from the product details screen.
	public void addProductsFromDetails() throws MalformedURLException, URISyntaxException {
		
		
		
		login.getAutoCredentials("standard_user");
		
		// choose product
		productsList.selectAndGetDetailsProducts("Sauce");
		
		// Add To Cart From Details Products
		detailProducts.addToCartFromDetailsProducts();
		
	
	}
//	
//	@Test // f. Verify user can add multiple products in the cart at a time.
//	public void addMultipleProducts() throws MalformedURLException, URISyntaxException {
//		// Login using given credentials
//		
//		helper.scrollIntoText("standard_user");
//		login.getStandardUser();
//		login.clickLoginBtn();
//		
//		// Verify if user success log in 
//		login.verifySuccessLogin();
//		
//		// add multiples products to cart
//		productsList.addMultipleProducts();
//	}
//	
//	@Test // g. Verify user redirection to product listing screen by tapping on continue shipping option in the cart section.
//	public void redirectionToProductListing() throws MalformedURLException, URISyntaxException {
//		
//	
//		// Login using given credentials
//		helper.scrollIntoText("standard_user");
//		login.getStandardUser();
//		login.clickLoginBtn();
//		
//		// Verify if user success log in 
//		login.verifySuccessLogin();
//		
//		// add multiples products to cart
//		productsList.addMultipleProducts();
//		
//		// user click continue shopping
//		productsList.clickCart();
//		productsList.scrollIntoText("CONTINUE SHOPPING");
//		productsList.clickContinueShoppingBtn();
//		
//		// Verify user redirection to product listing screen
//		productsList.verifyBackToListProducts();
//			
//	}
//	
//	@Test // h. Verify details of the product added into the cart.
//	public void verifyDetailsProductIntoCart() throws MalformedURLException, URISyntaxException {
//		
//		
//		// login with given credentials
//		helper.scrollIntoText("standard_user");
//		login.getStandardUser();
//		login.clickLoginBtn();
//		
//		// Verify if user success log in 
//		login.verifySuccessLogin();
//		
//		// Choose Products & Get details descriptions
//		String descriptionsProducts = productsList.getDetailsProducts();
//		
//	    // get details products in cart
//		String descriptionProductsInCart = productsList.getDetailsProductsInCart();
//		
//		// add to cart and click cart
//		productsList.scrollIntoText("ADD TO CART");
//		productsList.addToCartFromDetailsProducts();
//		productsList.clickCart();
//	   
//	    // verify if chosen products matched with displayed details in cart
//		Assert.assertEquals(descriptionsProducts, descriptionProductsInCart);
//	}
//	
//	@Test // i. Verify Checkout flow till success with valid user information along with necessary verifications.
//	public void checkoutPositiveFlow() throws MalformedURLException, URISyntaxException {
//		
//		// Login using given credentials
//		
//		helper.scrollIntoText("standard_user");
//		login.getStandardUser();
//		login.clickLoginBtn();
//		
//		// Verify if user success log in 
//		login.verifySuccessLogin();
//		
//		// add multiples products to cart
//		ProductsList productsList = new ProductsList();
//		productsList.addMultipleProducts();
//		productsList.clickCart();
//		
//		//Get total price before checkout
//		Double expectedTotalPrice = productsList.getTotalPriceBeforeCheckout();
//	
//	    // checkout
//		productsList.scrollIntoText("CHECKOUT");
//	    productsList.clickCheckout();
//	    
//	    // user input valid information needed
//	    productsList.setFirstName(faker.name().firstName());
//	    productsList.setLastName(faker.name().lastName());
//	    productsList.setPostalCode(faker.address().zipCode());
//	    productsList.clickContinue();
//	    
//	    // Get actual total amount 
//	    double actualTotalPrice = productsList.getActualTotalPriceAfterCheckout();
//	    
//	    // Verify if expected total amount is matched with actual total amount is matched (Note : not includes tax)
//		Assert.assertEquals(expectedTotalPrice,actualTotalPrice);
//	    
//	    //Finish Order
//	    productsList.clickFinishBtn();
//	   
//	    // verify user get success message after complete order flow
//	    productsList.verifyOrderComplete();
//	}
//	
//	@Test // j. Verify Checkout flow with invalid user First Name
//	public void checkoutNegativeFlow() throws MalformedURLException, URISyntaxException {
//		
//		// Login using given credentials
//		
//		helper.scrollIntoText("standard_user");
//		login.getStandardUser();
//		login.clickLoginBtn();
//		
//		// Verify if user success log in 
//		login.verifySuccessLogin();
//	
//		// add multiples products to cart
//		ProductsList productsList = new ProductsList();
//		productsList.addMultipleProducts();
//		productsList.clickCart();
//		//Checkout
//		productsList.scrollIntoText("CHECKOUT");
//	    productsList.clickCheckout();
//	    
//	    //User input invalid needed informations
//	    productsList.setFirstName("");
//	    productsList.setLastName(faker.name().lastName());
//	    productsList.setPostalCode(faker.address().zipCode());
//	    productsList.clickContinue();
//	    
//	    //Verify if user get alert message after input invalid information needed
//	    productsList.verifyErrorMessage();
//	}

	
}



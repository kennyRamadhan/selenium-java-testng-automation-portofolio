package KennyRamadhan.FlipTest;
import java.net.MalformedURLException;
import com.github.javafaker.Faker;
import java.net.URISyntaxException;
import java.util.Locale;
import org.testng.Assert;
import org.testng.annotations.Test;

public class e2eMobile extends appiumBase {
	
	@SuppressWarnings("deprecation")
	Faker faker = new Faker(new Locale("id_ID"));  
	@Test //a. Verify user login using given credentials.
	public void login() throws MalformedURLException, URISyntaxException  {
		
		// Login using given credentials
		loginPage login = new loginPage(driver);
		login.scrollIntoText("standard_user");
		login.getStandardUser();
		login.clickLoginBtn();
		
		// Verify if user success log in 
		login.verifySuccessLogin();
	}

	@Test // b. Verify failure message in case of invalid login credentials are entered by the user.
	public void failedLogin() throws MalformedURLException, URISyntaxException {
		
		// login with input invalid credentials
		loginPage login = new loginPage(driver);
		login.setUsername("Wrong Username");
		login.setPassword("Wrong Password");
		login.clickLoginBtn();

		// Verify if user get alert message after input wrong credentials 
		login.verifyErrorMessage();
	}
	
	@Test // c. Verify Price (high to low) and (low to high) filter are working as expected.
	public void sortingPrice() throws MalformedURLException, URISyntaxException  {
		
		// Login using given credentials
		ProductsList productsList = new ProductsList(driver);
		loginPage login = new loginPage(driver);
		login.scrollIntoText("standard_user");
		login.getStandardUser();
		login.clickLoginBtn();
		
		// Verify if user success log in 
		login.verifySuccessLogin();
		
		// Listing price products store it in list 
		productsList.getProdutsPriceListBeforeSorting();
		
	    // sorting ascending price
		productsList.clickFilterBtn();
		productsList.clickLowToHigh();
		
		// listing price products after sorting by ascending price 
		productsList.getProdutsPriceListAfterSorting();
	}
	
	@Test // d. Verify user can add products to cart from the product listing screen.
	public void addProductsFromListing() throws MalformedURLException, URISyntaxException {
		
		ProductsList productsList = new ProductsList(driver);
		loginPage login = new loginPage(driver);
		// Login using given credentials
		login.scrollIntoText("standard_user");
		login.getStandardUser();
		login.clickLoginBtn();
		
		// Verify if user success log in 
		login.verifySuccessLogin();
		
		// Add Products Directly From List products 
		productsList.addProductsToCartDirectlyFromListMenu();
	}
	
	@Test // e. Verify user can add products to cart from the product details screen.
	public void addProductsFromDetails() throws MalformedURLException, URISyntaxException {
		
		loginPage login = new loginPage(driver);
		ProductsList productsList = new ProductsList(driver);
		
		// Login using given credentials
		login.scrollIntoText("standard_user");
		login.getStandardUser();
		login.clickLoginBtn();
		
		// Verify if user success log in 
		login.verifySuccessLogin();
		
		// choose product
		productsList.getDetailsProducts();
		
		// Add To Cart From Details Products
		productsList.scrollIntoText("ADD TO CART");
		productsList.addToCartFromDetailsProducts();
		
		// Verify cart added after user click add to cart
		productsList.verifyProductsSuccesfullyAddedIntoCart();
		
	}
	
	@Test // f. Verify user can add multiple products in the cart at a time.
	public void addMultipleProducts() throws MalformedURLException, URISyntaxException {
		// Login using given credentials
		loginPage login = new loginPage(driver);
		ProductsList productsList = new ProductsList(driver);
		login.scrollIntoText("standard_user");
		login.getStandardUser();
		login.clickLoginBtn();
		
		// Verify if user success log in 
		login.verifySuccessLogin();
		
		// add multiples products to cart
		productsList.addMultipleProducts();
	}
	
	@Test // g. Verify user redirection to product listing screen by tapping on continue shipping option in the cart section.
	public void redirectionToProductListing() throws MalformedURLException, URISyntaxException {
		
		ProductsList productsList = new ProductsList(driver);
		loginPage login = new loginPage(driver);
		
		// Login using given credentials
		login.scrollIntoText("standard_user");
		login.getStandardUser();
		login.clickLoginBtn();
		
		// Verify if user success log in 
		login.verifySuccessLogin();
		
		// add multiples products to cart
		productsList.addMultipleProducts();
		
		// user click continue shopping
		productsList.clickCart();
		productsList.scrollIntoText("CONTINUE SHOPPING");
		productsList.clickContinueShoppingBtn();
		
		// Verify user redirection to product listing screen
		productsList.verifyBackToListProducts();
			
	}
	
	@Test // h. Verify details of the product added into the cart.
	public void verifyDetailsProductIntoCart() throws MalformedURLException, URISyntaxException {
		
		ProductsList productsList = new ProductsList(driver);
		loginPage login = new loginPage(driver);
		
		// login with given credentials
		login.scrollIntoText("standard_user");
		login.getStandardUser();
		login.clickLoginBtn();
		
		// Verify if user success log in 
		login.verifySuccessLogin();
		
		// Choose Products & Get details descriptions
		String descriptionsProducts = productsList.getDetailsProducts();
		
	    // get details products in cart
		String descriptionProductsInCart = productsList.getDetailsProductsInCart();
		
		// add to cart and click cart
		productsList.scrollIntoText("ADD TO CART");
		productsList.addToCartFromDetailsProducts();
		productsList.clickCart();
	   
	    // verify if chosen products matched with displayed details in cart
		Assert.assertEquals(descriptionsProducts, descriptionProductsInCart);
	}
	
	@Test // i. Verify Checkout flow till success with valid user information along with necessary verifications.
	public void checkoutPositiveFlow() throws MalformedURLException, URISyntaxException {
		
		// Login using given credentials
		loginPage login = new loginPage(driver);
		login.scrollIntoText("standard_user");
		login.getStandardUser();
		login.clickLoginBtn();
		
		// Verify if user success log in 
		login.verifySuccessLogin();
		
		// add multiples products to cart
		ProductsList productsList = new ProductsList(driver);
		productsList.addMultipleProducts();
		productsList.clickCart();
		
		//Get total price before checkout
		Double expectedTotalPrice = productsList.getTotalPriceBeforeCheckout();
	
	    // checkout
		productsList.scrollIntoText("CHECKOUT");
	    productsList.clickCheckout();
	    
	    // user input valid information needed
	    productsList.setFirstName(faker.name().firstName());
	    productsList.setLastName(faker.name().lastName());
	    productsList.setPostalCode(faker.address().zipCode());
	    productsList.clickContinue();
	    
	    // Get actual total amount 
	    double actualTotalPrice = productsList.getActualTotalPriceAfterCheckout();
	    
	    // Verify if expected total amount is matched with actual total amount is matched (Note : not includes tax)
		Assert.assertEquals(expectedTotalPrice,actualTotalPrice);
	    
	    //Finish Order
	    productsList.clickFinishBtn();
	   
	    // verify user get success message after complete order flow
	    productsList.verifyOrderComplete();
	}
	
	@Test // j. Verify Checkout flow with invalid user First Name
	public void checkoutNegativeFlow() throws MalformedURLException, URISyntaxException {
		
		// Login using given credentials
		loginPage login = new loginPage(driver);
		login.scrollIntoText("standard_user");
		login.getStandardUser();
		login.clickLoginBtn();
		
		// Verify if user success log in 
		login.verifySuccessLogin();
	
		// add multiples products to cart
		ProductsList productsList = new ProductsList(driver);
		productsList.addMultipleProducts();
		productsList.clickCart();
		//Checkout
		productsList.scrollIntoText("CHECKOUT");
	    productsList.clickCheckout();
	    
	    //User input invalid needed informations
	    productsList.setFirstName("");
	    productsList.setLastName(faker.name().lastName());
	    productsList.setPostalCode(faker.address().zipCode());
	    productsList.clickContinue();
	    
	    //Verify if user get alert message after input invalid information needed
	    productsList.verifyErrorMessage();
	}

	
}



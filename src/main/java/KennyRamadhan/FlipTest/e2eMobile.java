package KennyRamadhan.FlipTest;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.Test;
import com.google.common.collect.Iterables;
import io.appium.java_client.AppiumBy;

public class e2eMobile extends appiumBase {

	@Test //a. Verify user login using given credentials.
	public void login() throws MalformedURLException, URISyntaxException  {
		
		// Login using given credentials
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		// Verify if user success log in 
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
				Reporter.log("Success Login");
			}
		else{
				Reporter.log("Failed Login");
			}
	}
	
	@Test // b. Verify failure message in case of invalid login credentials are entered by the user.
	public void failedLogin() throws MalformedURLException, URISyntaxException {
		
		// Login using given credentials
		driver.findElement(locatorElements.username()).sendKeys("Wrong Username");
		driver.findElement(locatorElements.password()).sendKeys("Wrong Password");
		driver.findElement(locatorElements.loginBtn()).click();
		
		// Verify if user get alert message after input wrong credentials 
		if(driver.findElement(locatorElements.alertMessage()).isDisplayed()) {	
				Reporter.log("Failed Login");
			}
		else{
				Reporter.log("Alert Not Found");
			}
	}
	
	@Test // c. Verify Price (high to low) and (low to high) filter are working as expected.
	public void sortingPrice() throws MalformedURLException, URISyntaxException  {
		
		// Login using given credentials
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		// Verify if user success log in 
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
				Reporter.log("Success Login");
			}
		else{
				Reporter.log("Failed Login");
			}
		
		// Listing price products store it in list 
		List<WebElement> productBeforeSorting = driver.findElements(locatorElements.priceList());
		int countProductsBeforeSorting = productBeforeSorting.size();
	    for(int i = 0; i<countProductsBeforeSorting; i++ ) {
			String sortPrices = productBeforeSorting.get(i).getText();
			Double price = Double.parseDouble(sortPrices.substring(1));
			Reporter.log("Price Before Sorting : "+price);
		}
		
	    // sorting ascending price
	    driver.findElement(locatorElements.filterBtn()).click();
		driver.findElement(locatorElements.ascendingSorting()).click();
		
		// listing price products after sorting by ascending price 
		List<WebElement> productAfterSorting = driver.findElements(locatorElements.priceList());
		int countProductsAfterSorting = productAfterSorting.size();
		for(int i = 0; i<countProductsAfterSorting; i++ ) {
			String sortPrices = productAfterSorting.get(i).getText();
			Double price = Double.parseDouble(sortPrices.substring(1));
			Reporter.log("Price After Sorting From Lowest To Highest : "+price);
		}	
	}
	
	@Test // d. Verify user can add products to cart from the product listing screen.
	public void addProductsFromListing() throws MalformedURLException, URISyntaxException {
		
		// Login using given credentials
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		// Verify if user success log in 
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
			
				Reporter.log("Success Login");
			}
		else{
				Reporter.log("Failed Login");
			}
		
		
		// List products 
		List<WebElement> listProducts = driver.findElements(locatorElements.listProductsName());
		int sizeProducts = listProducts.size();
		
		// choose product
		for(int i = 0; i<sizeProducts; i++ ) {
			
			String productsNames = listProducts.get(i).getText();
		
			if(productsNames.equalsIgnoreCase("Sauce Labs Backpack")) {
				
				driver.findElements(locatorElements.listProductsName()).get(i).click();
				Reporter.log("Choosen Product : "+productsNames);
				String choosenProducts = driver.findElement(locatorElements.choosenproducts()).getText();
				
				// verify if chosen products equals to displayed products
				if(productsNames.equalsIgnoreCase(choosenProducts)) {
					
					Reporter.log("Displayed Products : "+choosenProducts);
				}
				
				
			}
		}
	}
	
	@Test // e. Verify user can add products to cart from the product details screen.
	public void addProductsFromDetails() throws MalformedURLException, URISyntaxException {
		
		// Login using given credentials
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		// Verify if user success log in 
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
			
				Reporter.log("Success Login");
			}
		else{
				Reporter.log("Failed Login");
			}
		
		// List products
		List<WebElement> listProducts = driver.findElements(locatorElements.listProductsName());
		int sizeProducts = listProducts.size();
		
		// choose product
		for(int i = 0; i<sizeProducts; i++ ) {
			
			String productsNames = listProducts.get(i).getText();
			
			
			if(productsNames.equalsIgnoreCase("Sauce Labs Backpack")) {
				
					driver.findElements(locatorElements.listProductsName()).get(i).click();
					Reporter.log("Choosen Product : "+productsNames);
					String choosenProducts = driver.findElement(locatorElements.choosenproducts()).getText();
					
					// verify if chosen products equals to displayed products
					if(productsNames.equalsIgnoreCase(choosenProducts)) {
						
						Reporter.log("Displayed Products : "+choosenProducts);
					}		
			}
		}
		
		// Add To Cart From Details Products
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"ADD TO CART\"));"));
		driver.findElement(locatorElements.addToCart()).click();
		
		// Verify cart added after user click add to cart
		String cart = driver.findElement(locatorElements.cart()).getText();
		Assert.assertEquals(cart, "1");
		
	}
	
	@Test // f. Verify user can add multiple products in the cart at a time.
	public void addMultipleProducts() throws MalformedURLException, URISyntaxException {
		
		// Login using given credentials
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
				
		// Verify if user success log in 
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()){
					
				Reporter.log("Success Login");
			}
		else{
				Reporter.log("Failed Login");
			}
		
		// add multiples products to cart
		List<WebElement>  listProducts = driver.findElements(locatorElements.addToCart2());
		WebElement lastElement = (WebElement) Iterables.get(listProducts,0);
		WebElement last = (WebElement) Iterables.get(listProducts,1);
		lastElement.click();
		last.click();
		
	}
	
	@Test // g. Verify user redirection to product listing screen by tapping on continue shipping option in the cart section.
	public void redirectionToProductListing() throws MalformedURLException, URISyntaxException {
		
		// Login using given credentials	
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		// Verify if user success log in 
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
			
				Reporter.log("Success Login");
			}
		else{	
				Reporter.log("Failed Login");
			}
		
		// add multiples products to cart
		List<WebElement>  listProducts = driver.findElements(locatorElements.addToCart2());
		WebElement lastElement = (WebElement) Iterables.get(listProducts,0);
		WebElement last = (WebElement) Iterables.get(listProducts,1);
		lastElement.click();
		last.click();
		
		// user click continue shopping
		driver.findElement(locatorElements.cart()).click();
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"CONTINUE SHOPPING\"));"));
		driver.findElement(locatorElements.continueShopping()).click();
		
		// Verify user redirection to product listing screen
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
		
					Reporter.log("Success Back To List Products");
				}
		else{
					Reporter.log("Failed Back To List Products");
				}
			
	}
	
	@Test // h. Verify details of the product added into the cart.
	public void verifyDetailsProductIntoCart() throws MalformedURLException, URISyntaxException {
		
		// Login using given credentials	
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		// Verify if user success log in 
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
			
			Reporter.log("Success Login");
		}
		else
		{
			Reporter.log("Failed Login");
		}
		
		// List Products
		List<WebElement> listProducts = driver.findElements(locatorElements.listProductsName());
		int sizeProducts = listProducts.size();
		
		// Choose Products
		for(int i = 0; i<sizeProducts; i++ ) {
			
			String productsNames = listProducts.get(i).getText();
			
			// Verify chosen product matched with displayed products
			if(productsNames.equalsIgnoreCase("Sauce Labs Backpack")) {
				
					driver.findElements(locatorElements.listProductsName()).get(i).click();
					Reporter.log("Choosen Product : "+productsNames);
					String choosenProducts = driver.findElement(locatorElements.choosenproducts()).getText();
					
					if(productsNames.equalsIgnoreCase(choosenProducts)) {
						
						Reporter.log("Displayed Products : "+choosenProducts);
					}		
			}
		}
		
		// get details chosen products 
		String descriptionProducts = driver.findElement(locatorElements.detailsProducts()).getText();
		
		// add to cart and click cart
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"ADD TO CART\"));"));
		driver.findElement(locatorElements.addToCart()).click();
	    driver.findElement(locatorElements.cart()).click();
	    
	    // get details products in cart
	    String descriptionProductsInCart = driver.findElement(locatorElements.detailsProductsInCart()).getText();
	    
	    // verify if chosen products matched with displayed details in cart
		if(descriptionProductsInCart.equalsIgnoreCase(descriptionProducts)) {
			
			Reporter.log("Details Products Matched");
		}
		else
		{
			Reporter.log("Details Products Not Matched");
		}
	}
	
	@Test // i. Verify Checkout flow till success with valid user information along with necessary verifications.
	public void checkoutPositiveFlow() throws MalformedURLException, URISyntaxException {
		
		// login with given credentials
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		// verify user log in 
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
			
			Reporter.log("Success Login");
		}
		else
		{
			Reporter.log("Failed Login");
		}
		
		
		// add multiples products to cart
		List<WebElement>  listProducts = driver.findElements(locatorElements.addToCart2());
		WebElement lastElement = (WebElement) Iterables.get(listProducts,0);
		WebElement last = (WebElement) Iterables.get(listProducts,1);
		lastElement.click();
		last.click();
		driver.findElement(locatorElements.cart()).click();
		
		
		// List all chosen price products 
		List<WebElement> productPrice = driver.findElements(locatorElements.priceList());
		int countProductPrice = productPrice.size();
		double totalAmount = 0;
		
		// sum total amount chosen products
	    for(int i = 0; i<countProductPrice; i++ ) {
			
			String sortPrices = productPrice.get(i).getText();
			Double expectedTotalAmountWithoutTaxes = Double.parseDouble(sortPrices.substring(1));
			totalAmount = totalAmount+expectedTotalAmountWithoutTaxes;
			Reporter.log("Total Amount Price Without Taxes : "+totalAmount);
		}
	    
	    // checkout
	    driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"CHECKOUT\"));"));
	    driver.findElement(locatorElements.checkout()).click();
	    
	    // user input valid information needed
	    driver.findElement(locatorElements.firstName()).sendKeys("Kenny");
	    driver.findElement(locatorElements.lastName()).sendKeys("Ramadhan");
	    driver.findElement(locatorElements.postalCode()).sendKeys("17530");
	    driver.findElement(locatorElements.continueBtn()).click();
	    
	    // Get actual total amount 
	    driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"Item total: $39.98\"));"));
	    String actualTotalAmount = driver.findElement(locatorElements.itemTotal()).getText();
	    Double actualTotalAmount2 = Double.parseDouble(actualTotalAmount.substring(13));
	    
	    // Verify if expected total amount is matched with actual total amount is matched (Note : not includes tax)
	    Assert.assertEquals(totalAmount, actualTotalAmount2);
	    driver.findElement(locatorElements.finishBtn()).click();
	    Reporter.log("Actual Total Amount : "+actualTotalAmount2);
	    
	    // verify user get success message after complete order flow
	    if(driver.findElement(locatorElements.orderComplete()).isDisplayed()) {
	    	
	    	Reporter.log("Order Completed");
	    }
	    else
	    {
	    	Reporter.log("Failed Order");
	    }
	    
	   
	}
	
//	@Test // j. Verify Checkout flow with invalid user First Name
//	public void checkoutNegativeFlow() throws MalformedURLException, URISyntaxException {
//		
//		// Login with given credentials
//		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
//		driver.findElement(locatorElements.standardUser()).click();
//		driver.findElement(locatorElements.loginBtn()).click();
//		
//		// verify if user successfully login
//		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
//			
//			Reporter.log("Success Login");
//		}
//		else
//		{
//			Reporter.log("Failed Login");
//		}
//		
//		
//		
//		// add multiples products to cart
//		List<WebElement>  listProducts = driver.findElements(locatorElements.addToCart2());
//		WebElement lastElement = (WebElement) Iterables.get(listProducts,0);
//		WebElement last = (WebElement) Iterables.get(listProducts,1);
//		lastElement.click();
//		last.click();
//		driver.findElement(locatorElements.cart()).click();  
//		
//		//Checkout
//	    driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"CHECKOUT\"));"));
//	    driver.findElement(locatorElements.checkout()).click();
//	    
//	    //User input invalid needed informations
//	    driver.findElement(locatorElements.firstName()).sendKeys("");
//	    driver.findElement(locatorElements.lastName()).sendKeys("Ramadhan");
//	    driver.findElement(locatorElements.postalCode()).sendKeys("17530");
//	    driver.findElement(locatorElements.continueBtn()).click();
//	    
//	    //Verify if user get alert message after input invalid information needed
//	    if(driver.findElement(locatorElements.errorMessageFirstName()).isDisplayed()) {
//	    	
//	    	Reporter.log("Negative Flow Success");
//	    }
//	    else
//	    {
//	    	Reporter.log("Failed");
//	    }
//	}
	
	}



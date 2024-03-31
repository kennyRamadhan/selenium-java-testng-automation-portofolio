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
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
			
			Reporter.log("Success Login");
		}
		else
		{
			Reporter.log("Failed Login");
		}

		
	}
	
	@Test // b. Verify failure message in case of invalid login credentials are entered by the user.
	public void failedLogin() throws MalformedURLException, URISyntaxException {
		
		driver.findElement(locatorElements.username()).sendKeys("Wrong Username");
		driver.findElement(locatorElements.password()).sendKeys("Wrong Password");
		driver.findElement(locatorElements.loginBtn()).click();
		
		
		if(driver.findElement(locatorElements.alertMessage()).isDisplayed()) {
			
			Reporter.log("Failed Login");
		}
		else
		{
			Reporter.log("Alert Not Found");
		}
	}
	
	@Test // c. Verify Price (high to low) and (low to high) filter are working as expected.
	public void sortingPrice() throws MalformedURLException, URISyntaxException  {
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
			
			Reporter.log("Success Login");
		}
		else
		{
			Reporter.log("Failed Login");
		}
		
		driver.findElement(locatorElements.filterBtn()).click();
		List<WebElement> productBeforeSorting = driver.findElements(locatorElements.priceList());
		int countProductsBeforeSorting = productBeforeSorting.size();
	    for(int i = 0; i<countProductsBeforeSorting; i++ ) {
			
			String sortPrices = productBeforeSorting.get(i).getText();
			Double price = Double.parseDouble(sortPrices.substring(1));
			Reporter.log("Price Before Sorting : "+price);
			
			
		}
		
		
		driver.findElement(locatorElements.ascendingSorting()).click();
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
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		List<WebElement> listProducts = driver.findElements(locatorElements.listProductsName());
		int sizeProducts = listProducts.size();
		
		for(int i = 0; i<sizeProducts; i++ ) {
			
			String productsNames = listProducts.get(i).getText();
			
			if(productsNames.equalsIgnoreCase("Sauce Labs Backpack")) {
				
				  driver.findElements(locatorElements.listProductsName()).get(i).click();
				Reporter.log("Choosen Product : "+productsNames);
				String choosenProducts = driver.findElement(locatorElements.choosenproducts()).getText();
				
				if(productsNames.equalsIgnoreCase(choosenProducts)) {
					
					Reporter.log("Displayed Products : "+choosenProducts);
				}
				
				
			}
		}
	}
	
	@Test // e. Verify user can add products to cart from the product details screen.
	public void addProductsFromDetails() throws MalformedURLException, URISyntaxException {
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		List<WebElement> listProducts = driver.findElements(locatorElements.listProductsName());
		int sizeProducts = listProducts.size();
		
		for(int i = 0; i<sizeProducts; i++ ) {
			
			String productsNames = listProducts.get(i).getText();
			
			if(productsNames.equalsIgnoreCase("Sauce Labs Backpack")) {
				
					driver.findElements(locatorElements.listProductsName()).get(i).click();
					Reporter.log("Choosen Product : "+productsNames);
					String choosenProducts = driver.findElement(locatorElements.choosenproducts()).getText();
					
					if(productsNames.equalsIgnoreCase(choosenProducts)) {
						
						Reporter.log("Displayed Products : "+choosenProducts);
					}		
			}
		}
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"ADD TO CART\"));"));
		driver.findElement(locatorElements.addToCart()).click();
		String cart = driver.findElement(locatorElements.cart()).getText();
		Assert.assertEquals(cart, "1");
		
	}
	
	@Test // f. Verify user can add multiple products in the cart at a time.
	public void addMultipleProducts() throws MalformedURLException, URISyntaxException {
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		List<WebElement>  listProducts = driver.findElements(locatorElements.addToCart2());
		
		
		WebElement lastElement = (WebElement) Iterables.get(listProducts,
				 0);
		WebElement last = (WebElement) Iterables.get(listProducts,
				 1);
		
		lastElement.click();
		last.click();
		
	}
	
	@Test // g. Verify user redirection to product listing screen by tapping on continue shipping option in the cart section.
	public void redirectionToProductListing() throws MalformedURLException, URISyntaxException {
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		
		List<WebElement>  listProducts = driver.findElements(locatorElements.addToCart2());
		
		
		WebElement firstElement = (WebElement) Iterables.get(listProducts,
				 0);
		WebElement lastElements = (WebElement) Iterables.get(listProducts,
				 1);
		
		firstElement.click();
		lastElements.click();
		
		driver.findElement(locatorElements.cart()).click();
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"CONTINUE SHOPPING\"));"));
		driver.findElement(locatorElements.continueShopping()).click();
		
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
		
			Reporter.log("Success Back To List Products");
		}
		else
		{
			Reporter.log("Failed Back To List Products");
		}
	
			
			
			
			
		}
	
	@Test // h. Verify details of the product added into the cart.
	public void verifyDetailsProductIntoCart() throws MalformedURLException, URISyntaxException {
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		List<WebElement> listProducts = driver.findElements(locatorElements.listProductsName());
		int sizeProducts = listProducts.size();
		
		for(int i = 0; i<sizeProducts; i++ ) {
			
			String productsNames = listProducts.get(i).getText();
			
			if(productsNames.equalsIgnoreCase("Sauce Labs Backpack")) {
				
					driver.findElements(locatorElements.listProductsName()).get(i).click();
					Reporter.log("Choosen Product : "+productsNames);
					String choosenProducts = driver.findElement(locatorElements.choosenproducts()).getText();
					
					if(productsNames.equalsIgnoreCase(choosenProducts)) {
						
						Reporter.log("Displayed Products : "+choosenProducts);
					}		
			}
		}
		String descriptionProducts = driver.findElement(locatorElements.detailsProducts()).getText();
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"ADD TO CART\"));"));
		driver.findElement(locatorElements.addToCart()).click();
	    driver.findElement(locatorElements.cart()).click();
	    String descriptionProductsInCart = driver.findElement(locatorElements.detailsProductsInCart()).getText();
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
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
			
			Reporter.log("Success Login");
		}
		else
		{
			Reporter.log("Failed Login");
		}
		
		
		
		List<WebElement>  listProducts = driver.findElements(locatorElements.addToCart2());
		WebElement firstElement = (WebElement) Iterables.get(listProducts,
				 0);
		WebElement lastElements = (WebElement) Iterables.get(listProducts,
				 1);
		
		firstElement.click();
		lastElements.click();
		driver.findElement(locatorElements.cart()).click();
		
		List<WebElement> productPrice = driver.findElements(locatorElements.priceList());
		int countProductPrice = productPrice.size();
		double totalAmount = 0;
	    for(int i = 0; i<countProductPrice; i++ ) {
			
			String sortPrices = productPrice.get(i).getText();
			Double expectedTotalAmountWithoutTaxes = Double.parseDouble(sortPrices.substring(1));
			totalAmount = totalAmount+expectedTotalAmountWithoutTaxes;
			Reporter.log("Total Amount Price Without Taxes : "+totalAmount);
		}
	   
	    driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"CHECKOUT\"));"));
	    driver.findElement(locatorElements.checkout()).click();
	    driver.findElement(locatorElements.firstName()).sendKeys("Kenny");
	    driver.findElement(locatorElements.lastName()).sendKeys("Ramadhan");
	    driver.findElement(locatorElements.postalCode()).sendKeys("17530");
	    driver.findElement(locatorElements.continueBtn()).click();
	    driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"Item total: $39.98\"));"));
	    String actualTotalAmount = driver.findElement(locatorElements.itemTotal()).getText();
	    Double actualTotalAmount2 = Double.parseDouble(actualTotalAmount.substring(13));
	    Assert.assertEquals(totalAmount, actualTotalAmount2);
	    driver.findElement(locatorElements.finishBtn()).click();
	    Reporter.log("Actual Total Amount : "+actualTotalAmount2);
	    
	    if(driver.findElement(locatorElements.orderComplete()).isDisplayed()) {
	    	
	    	Reporter.log("Order Completed");
	    }
	    else
	    {
	    	Reporter.log("Failed Order");
	    }
	    
	   
	}
	
	@Test // j. Verify Checkout flow with invalid user First Name
	public void checkoutNegativeFlow() throws MalformedURLException, URISyntaxException {
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"standard_user\"));"));
		driver.findElement(locatorElements.standardUser()).click();
		driver.findElement(locatorElements.loginBtn()).click();
		
		if(driver.findElement(locatorElements.listProducts()).isDisplayed()) {
			
			Reporter.log("Success Login");
		}
		else
		{
			Reporter.log("Failed Login");
		}
		
		
		
		List<WebElement>  listProducts = driver.findElements(locatorElements.addToCart2());
		WebElement firstElement = (WebElement) Iterables.get(listProducts,
				 0);
		WebElement lastElements = (WebElement) Iterables.get(listProducts,
				 1);
		
		firstElement.click();
		lastElements.click();
		
		driver.findElement(locatorElements.cart()).click();   
	    driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"CHECKOUT\"));"));
	    driver.findElement(locatorElements.checkout()).click();
	    driver.findElement(locatorElements.firstName()).sendKeys("");
	    driver.findElement(locatorElements.lastName()).sendKeys("Ramadhan");
	    driver.findElement(locatorElements.postalCode()).sendKeys("17530");
	    driver.findElement(locatorElements.continueBtn()).click();
	    if(driver.findElement(locatorElements.errorMessageFirstName()).isDisplayed()) {
	    	
	    	Reporter.log("Negative Flow Success");
	    }
	    else
	    {
	    	Reporter.log("Failed");
	    }
	}
	
	}



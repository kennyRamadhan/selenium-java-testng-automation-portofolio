package KennyRamadhan.FlipTest;

import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.Reporter;

import com.google.common.collect.Iterables;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class ProductsList {
	
	AndroidDriver driver;
	
	public ProductsList(AndroidDriver driver) {
		
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}

	

	
	
	
	
	@AndroidFindBy(accessibility = "test-ADD TO CART")
	private WebElement addToCart;
	

	

	
	@AndroidFindBy(accessibility="test-PRODUCTS")
	private WebElement listProducts;
	
	@AndroidFindBy(accessibility = "test-CHECKOUT")
	private WebElement checkout;
	
	@AndroidFindBy(accessibility="test-First Name")
	private WebElement firstNameField;
	
	@AndroidFindBy(accessibility = "test-FINISH")
	private WebElement finishBtn;
	
	@AndroidFindBy(accessibility = "test-CONTINUE")
	private WebElement continueBtn;
	
	@AndroidFindBy(accessibility="test-Zip/Postal Code")
	private WebElement postalCodeField;
	
	@AndroidFindBy(accessibility="test-Last Name")
	private WebElement lastNameField;
	
	@AndroidFindBy(accessibility = "test-Error message")
	private WebElement errorMessage;
	
	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"test-Price\"])[1]/android.widget.TextView")
	private WebElement priceList;
	
	
	
	
	
	public void getProdutsPriceListBeforeSorting() {
		
	
		List<WebElement> productBeforeSorting = driver.findElements(AppiumBy.xpath("//android.widget.TextView[@content-desc='test-Price']"));
		int countProductsBeforeSorting = productBeforeSorting.size();
	    for(int i = 0; i<countProductsBeforeSorting; i++ ) {
			String sortPrices = productBeforeSorting.get(i).getText();
			Double price = Double.parseDouble(sortPrices.substring(1));
			Reporter.log("Price Before Sorting : "+price);
		}
	
	}
	
	public void getProdutsPriceListAfterSorting() {
		
		
		List<WebElement> productBeforeSorting = driver.findElements(AppiumBy.xpath("//android.widget.TextView[@content-desc='test-Price']"));
		int countProductsBeforeSorting = productBeforeSorting.size();
	    for(int i = 0; i<countProductsBeforeSorting; i++ ) {
			String sortPrices = productBeforeSorting.get(i).getText();
			double price = Double.parseDouble(sortPrices.substring(1));
			Reporter.log("Price After Sorting : "+price);
		}
	
	}
	
	@AndroidFindBy(accessibility="test-Modal Selector Button")
	private WebElement filterBtn;
	public void clickFilterBtn() {
		filterBtn.click();
	}

	@AndroidFindBy(xpath="//android.widget.TextView[@text='Price (low to high)']")
	private WebElement lowToHigh;
	public void clickLowToHigh() {
		lowToHigh.click();
	}
	
	public void addProductsToCartDirectlyFromListMenu() {
		
		List<WebElement>  listProducts = driver.findElements(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-ADD TO CART\"]"));
		@SuppressWarnings("null")
		WebElement lastElement = (WebElement) Iterables.get(listProducts,0);
		lastElement.click();
	
	}
	
	public String getDetailsProducts() {
		
		List<WebElement> listProducts = driver.findElements(AppiumBy.xpath("//android.widget.TextView[@content-desc=\"test-Item title\"]"));
		int sizeProducts = listProducts.size();
		
		// choose product
		for(int i = 0; i<sizeProducts; i++ ) {
			
			String productsNames = listProducts.get(i).getText();
			
			
			if(productsNames.equalsIgnoreCase("Sauce Labs Backpack")) {
				
					driver.findElements(AppiumBy.xpath("//android.widget.TextView[@content-desc='test-Item title']")).get(i).click();
				
					String choosenProducts = driver.findElement(AppiumBy.xpath("//android.widget.TextView[@content-desc='test-Item title']")).getText();
					
					// verify if chosen products equals to displayed products
					if(productsNames.equalsIgnoreCase(choosenProducts)) {
						
						Reporter.log("Displayed Products : "+choosenProducts);
					}
					else
					{
						Reporter.log("Failed");
					}
			}
		}
		String descriptionProducts = driver.findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc='test-Description']/android.widget.TextView[1]")).getText();
		return descriptionProducts;
	}
	
	@AndroidFindBy(xpath="//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[1]")
	private WebElement detailsProductsInCart;
	public String getDetailsProductsInCart() {
		
		return detailsProductsInCart.getText();
	}

	public void scrollIntoText(String text) {
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\""+text+"\"));"));
	}

	public void addToCartFromDetailsProducts() {
		
		addToCart.click();
	}
	@AndroidFindBy(accessibility  = "test-Cart")
	private WebElement cart;
	public void verifyProductsSuccesfullyAddedIntoCart() {
		
		cart.click();
	}
	
	public void clickCart() {
		
		cart.click();
	}
	
	@SuppressWarnings("null")
	public void addMultipleProducts() {
		
		List<WebElement>  listProducts = driver.findElements(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-ADD TO CART\"]"));
		@SuppressWarnings("null")
		WebElement lastElement = (WebElement) Iterables.get(listProducts,0);
		WebElement last = (WebElement) Iterables.get(listProducts,1);
		lastElement.click();
		last.click();
		
	}
	

	@AndroidFindBy(accessibility  = "test-CONTINUE SHOPPING")
	private WebElement continueShoppingBtn;
	public void clickContinueShoppingBtn() {
		
		continueShoppingBtn.click();
	}
	

	public void verifyBackToListProducts() {
		
		if(listProducts.isDisplayed()){	
				Reporter.log("Success Back To List Products");
		 	}
		else{
				Reporter.log("Failed Back To List Products");
		}
	}
	
	public Double getTotalPriceBeforeCheckout() {
		
		List<WebElement> productPrice = driver.findElements(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-Price\"]/android.widget.TextView"));
		int countProductPrice = productPrice.size();
		double totalAmount = 0;
		
		// sum total amount chosen products
	    for(int i = 0; i<countProductPrice; i++ ) {
			
			String sortPrices = productPrice.get(i).getText();
			Double expectedTotalAmountWithoutTaxes = Double.parseDouble(sortPrices.substring(1));
			totalAmount = totalAmount+expectedTotalAmountWithoutTaxes;
			Reporter.log("Total Amount Price Without Taxes : "+totalAmount);
		}
		
		return totalAmount;
	}

	public void clickCheckout() {
		checkout.click();
	}
	
	
	public void setFirstName(String firstName) {
	firstNameField.sendKeys(firstName);
	}
	

	public void setLastName(String lastName) {
	lastNameField.sendKeys(lastName);
	}
	

	public void setPostalCode(String postalCode) {
	postalCodeField.sendKeys(postalCode);
	}
	

	public void clickContinue() {
		continueBtn.click();
	}
	
	public Double getActualTotalPriceAfterCheckout() {
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"Item total: $39.98\"));"));
		String actualTotalAmountString = driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text='Item total: $39.98']")).getText();
		Double actualTotalAmount = Double.parseDouble(actualTotalAmountString.substring(13));
	    Reporter.log("Actual Total Amount : "+actualTotalAmount);
		return actualTotalAmount;
	}
	


	public void clickFinishBtn() {
		finishBtn.click();
	}
	
	
	@AndroidFindBy(xpath = "//android.widget.ScrollView[@content-desc=\"test-CHECKOUT: COMPLETE!\"]/android.view.ViewGroup/android.widget.TextView[1]")
	private WebElement orderComplete;
	public void verifyOrderComplete() {
		
	    if(orderComplete.isDisplayed()) {
	    	
	    	Reporter.log("Order Completed");
	    }
	    else
	    {
	    	Reporter.log("Failed Order");
	    }
	}
	
	public void verifyErrorMessage() {
		
	    if(errorMessage.isDisplayed()) {
	    	
	    	Reporter.log("Negative Flow Success");
	    }
	    else
	    {
	    	Reporter.log("Failed");
	    }
	}
	
}

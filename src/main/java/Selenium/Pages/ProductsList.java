package Selenium.Pages;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Reporter;
import com.google.common.collect.Iterables;

import Appium.Config.DriverManager;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class ProductsList {
	

	
	public ProductsList() {
		
	
		 PageFactory.initElements(
			        new AppiumFieldDecorator(DriverManager.getDriver(), Duration.ofSeconds(15)),
			        this   );
	}
	
	@AndroidFindBy(accessibility = "test-ADD TO CART")
	@iOSXCUITFindBy(accessibility = "test-ADD TO CART")
	private WebElement addToCart;
	
	@AndroidFindBy(accessibility="test-PRODUCTS")
	@iOSXCUITFindBy(accessibility = "test-PRODUCTS")
	private List<WebElement> listProducts;
	
	@AndroidFindBy(accessibility = "test-CHECKOUT")
	@iOSXCUITFindBy(accessibility = "test-CHECKOUT")
	private WebElement checkout;
	
	@AndroidFindBy(accessibility="test-First Name")
	@iOSXCUITFindBy(accessibility = "test-First Name")
	private WebElement firstNameField;
	
	@AndroidFindBy(accessibility = "test-FINISH")
	@iOSXCUITFindBy(accessibility = "test-FINISH")
	private WebElement finishBtn;
	
	@AndroidFindBy(accessibility = "test-CONTINUE")
	@iOSXCUITFindBy(accessibility = "test-CONTINUE")
	private WebElement continueBtn;
	
	@AndroidFindBy(accessibility="test-Zip/Postal Code")
	@iOSXCUITFindBy(accessibility = "test-Zip/Postal Code")
	private WebElement postalCodeField;
	
	@AndroidFindBy(accessibility="test-Last Name")
	@iOSXCUITFindBy(accessibility = "test-Last Name")
	private WebElement lastNameField;
	
	@AndroidFindBy(accessibility = "test-Error message")
	@iOSXCUITFindBy(accessibility = "test-Error message")
	private WebElement errorMessage;
	
	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"test-Price\"])[1]/android.widget.TextView")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name=\"test-Price\"]")
	private List<WebElement> priceList;
	
	@AndroidFindBy(accessibility="test-Modal Selector Button")
	@iOSXCUITFindBy(accessibility = "test-Modal Selector Button")
	private WebElement filterBtn;
	

	@AndroidFindBy(xpath="//android.widget.TextView[@text='Price (low to high)']")
	@iOSXCUITFindBy(iOSNsPredicate =  "name == 'Price (low to high)'")
	private WebElement lowToHigh;
	
	public void getProdutsPriceListBeforeSorting() {
		for (WebElement el : priceList) {
		    double price = Double.parseDouble(el.getText().substring(1));
		    Reporter.log("Price List Before Sorting : " + price);
		}
	
	}
	
	public void getProdutsPriceListAfterSorting() {
		for (WebElement el : priceList) {
		    double price = Double.parseDouble(el.getText().substring(1));
		    Reporter.log("Price List After Sorting : " + price);
		}

	
	}
	

	public void clickFilterBtn() {
		filterBtn.click();
	}

	public void clickLowToHigh() {
		lowToHigh.click();
	}
	
	public void addProductsToCartDirectlyFromListMenu() {
		
		List<WebElement>  listProducts = DriverManager.getDriver().findElements(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-ADD TO CART\"]"));
		@SuppressWarnings("null")
		WebElement lastElement = (WebElement) Iterables.get(listProducts,0);
		lastElement.click();
	
	}
	
	public String getDetailsProducts() {
		
		List<WebElement> listProducts = DriverManager.getDriver().findElements(AppiumBy.xpath("//android.widget.TextView[@content-desc=\"test-Item title\"]"));
		int sizeProducts = listProducts.size();
		
		// choose product
		for(int i = 0; i<sizeProducts; i++ ) {
			
			String productsNames = listProducts.get(i).getText();
			
			
			if(productsNames.equalsIgnoreCase("Sauce Labs Backpack")) {
				
				DriverManager.getDriver().findElements(AppiumBy.xpath("//android.widget.TextView[@content-desc='test-Item title']")).get(i).click();
				
					String choosenProducts = DriverManager.getDriver().findElement(AppiumBy.xpath("//android.widget.TextView[@content-desc='test-Item title']")).getText();
					
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
		String descriptionProducts = DriverManager.getDriver().findElement(AppiumBy.xpath("//android.view.ViewGroup[@content-desc='test-Description']/android.widget.TextView[1]")).getText();
		return descriptionProducts;
	}
	
	@AndroidFindBy(xpath="//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[1]")
	private WebElement detailsProductsInCart;
	public String getDetailsProductsInCart() {
		
		return detailsProductsInCart.getText();
	}

	public void scrollIntoText(String text) {
		
		DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\""+text+"\"));"));
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
		
		List<WebElement>  listProducts = DriverManager.getDriver().findElements(AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-ADD TO CART\"]"));
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
		
		if(!listProducts.isEmpty()){	
				Reporter.log("Success Back To List Products");
		 	}
		else{
				Reporter.log("Failed Back To List Products");
		}
	}
	
	public Double getTotalPriceBeforeCheckout() {
		
		List<WebElement> productPrice = DriverManager.getDriver().findElements(AppiumBy.xpath("//android.view.ViewGroup[@content-desc='test-Price']/android.widget.TextView"));
		int countProductPrice = productPrice.size();
		double totalAmount = 0;
		
		// sum total amount chosen products
	    for(int i = 0; i<countProductPrice; i++ ) {

			String sortPrices = productPrice.get(i).getText();
			double expectedTotalAmountWithoutTaxes = Double.parseDouble(sortPrices.substring(1));
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
	
	public double getActualTotalPriceAfterCheckout() {
		DriverManager.getDriver().findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\"Item total: $39.98\"));"));
		String actualTotalAmountString = DriverManager.getDriver().findElement(AppiumBy.xpath("//android.widget.TextView[@text='Item total: $39.98']")).getText();
		double actualTotalAmount = Double.parseDouble(actualTotalAmountString.substring(13));
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

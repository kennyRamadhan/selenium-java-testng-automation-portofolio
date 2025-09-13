package Selenium.Pages;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import Appium.Config.DriverManager;
import Selenium.CustomHelper.UtilsHelper;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class CartCheckout {
	
	
	 private final UtilsHelper utils;
		
		public CartCheckout() {
			
			this.utils = new UtilsHelper();
			 PageFactory.initElements(
				        new AppiumFieldDecorator(DriverManager.getDriver(), Duration.ofSeconds(15)),
				        this   );
		}
		


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
	
	
	
	public void checkoutInformation(String firstName,String lastName,String postalCode) {
		
		checkout.click();
		firstNameField.sendKeys(firstName);
		lastNameField.sendKeys(lastName);
		postalCodeField.sendKeys(postalCode);
		continueBtn.click();
	}
	
}

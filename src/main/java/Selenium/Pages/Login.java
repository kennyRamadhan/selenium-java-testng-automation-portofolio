package Selenium.Pages;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.Reporter;

import Appium.Config.DriverManager;
import Extent.Listeners.LogHelper;


import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class Login {

	
	
	public Login() {
		
		
		PageFactory.initElements(new AppiumFieldDecorator(DriverManager.getDriver()), this);
	}
	
	@AndroidFindBy(accessibility="test-Username")
	@iOSXCUITFindBy(accessibility = "test-Username")
	private WebElement usernameField;
	public void setUsername(String username) {
		usernameField.sendKeys(username);
	}
	
	@AndroidFindBy(accessibility="test-Password")
	@iOSXCUITFindBy(accessibility = "test-Password")
	private WebElement passwordField;
	public void setPassword(String passwords) {
	passwordField.sendKeys(passwords);
	}
	
	@AndroidFindBy(accessibility="test-LOGIN")
	@iOSXCUITFindBy(accessibility = "test-LOGIN")
	private WebElement submitBtn;
	public void clickLoginBtn() {
		LogHelper.step("Tap Login Button");
		submitBtn.click();
		LogHelper.detail("Success Tap Login Button");
	}
	
	@AndroidFindBy(accessibility="test-Error message")
	@iOSXCUITFindBy(accessibility = "test-Error message")
	private WebElement alertMessage;
	public void verifyErrorMessage() {
		if(alertMessage.isDisplayed()) {	
			
			Reporter.log("Failed Login");
		}
	else{
			Reporter.log("Alert Not Found");
		}
	}
	
	@AndroidFindBy(xpath="//android.view.ViewGroup[@content-desc=\"test-standard_user\"]")
	@iOSXCUITFindBy(xpath ="//XCUIElementTypeOther[@name=\"test-standard_user\"]" )
	private WebElement stdUser;
	public void getStandardUser() {
		LogHelper.step("Get Credentials");
		stdUser.click();
		LogHelper.detail("Success Get Credentials");
	}
	
	

	
	@AndroidFindBy(accessibility="test-PRODUCTS")
	@iOSXCUITFindBy(iOSNsPredicate = "name == 'assets/src/img/swag-labs-logo.png'")
	private WebElement listProducts;
	public void verifySuccessLogin() {
		
		if (listProducts.isDisplayed()) {
		    LogHelper.step("Login Validation");
		    LogHelper.detail("Succesfully Login");
		} else {
		    LogHelper.step("Login Validation");
		    LogHelper.detail("Failed Login");
		    Assert.fail("Login validation failed - PRODUCT list not visible");
		}

	}
	
	
}

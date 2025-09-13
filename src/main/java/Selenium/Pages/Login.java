package Selenium.Pages;


import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;


import Appium.Config.DriverManager;
import Extent.Listeners.LogHelper;
import Selenium.CustomHelper.UtilsHelper;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class Login {

	private UtilsHelper helper;
	
	public Login() {
		
		this.helper = new UtilsHelper();
		PageFactory.initElements(new AppiumFieldDecorator(DriverManager.getDriver()), this);
	}
	
	@AndroidFindBy(accessibility="test-Username")
	@iOSXCUITFindBy(accessibility = "test-Username")
	private WebElement usernameField;
	
	@AndroidFindBy(accessibility="test-Password")
	@iOSXCUITFindBy(accessibility = "test-Password")
	private WebElement passwordField;

	
	@AndroidFindBy(accessibility="test-LOGIN")
	@iOSXCUITFindBy(accessibility = "test-LOGIN")
	private WebElement submitBtn;
	
	@AndroidFindBy(accessibility="test-Error message")
	@iOSXCUITFindBy(accessibility = "test-Error message")
	private WebElement alertMessage;
	
	@AndroidFindBy(xpath="//android.view.ViewGroup[@content-desc=\"test-standard_user\"]")
	@iOSXCUITFindBy(xpath ="//XCUIElementTypeOther[@name=\"test-standard_user\"]" )
	private WebElement stdUser;
	
	
	@AndroidFindBy(xpath="//android.view.ViewGroup[@content-desc=\"test-standard_user\"]")
	@iOSXCUITFindBy(xpath ="//XCUIElementTypeOther[@name=\"test-standard_user\"]" )
	private WebElement lockedUser;
	
	@AndroidFindBy(xpath="//android.view.ViewGroup[@content-desc=\"test-standard_user\"]")
	@iOSXCUITFindBy(xpath ="//XCUIElementTypeOther[@name=\"test-standard_user\"]" )
	private WebElement problemUser;
	
	
	@AndroidFindBy(accessibility="test-PRODUCTS")
	@iOSXCUITFindBy(iOSNsPredicate = "name == 'assets/src/img/swag-labs-logo.png'")
	private WebElement listProducts;

	
	public void getAutoCredentials(String username) {
		
		helper.scrollIntoText(username);
		
		
		switch (username.toLowerCase()) {
        case "standard_user":
        	LogHelper.step("Get Credentials");
            stdUser.click();
            submitBtn.click();
            LogHelper.detail("Selected User " + username);
            break;
        case "locked_out_user":
        	LogHelper.step("Get Credentials");
            lockedUser.click();
            submitBtn.click();
            LogHelper.detail("Selected User " + username);
            break;

        case "problem_user":
        	LogHelper.step("Get Credentials");
            problemUser.click();
            submitBtn.click();
            LogHelper.detail("Selected User " + username);
            break;
        default:
            Assert.fail("Username '" + username + "' tidak dikenali!");
            return;
    }
		
		
		
		
		if (listProducts.isDisplayed()) {
		    LogHelper.step("Login Validation");
		    LogHelper.detail("Succesfully Login");
		} else {
		    LogHelper.step("Login Validation");
		    LogHelper.detail("Failed Login");
		    Assert.fail("Login validation failed - PRODUCT list not visible");
		}
		
	}
	
	public void setManualCredentials(String username,String password) {
		
		LogHelper.step("Input Username");
		usernameField.sendKeys(username);
		LogHelper.detail("Username Yang Diinput" +username);
		
		LogHelper.step("Input Password");
		passwordField.sendKeys(password);
		LogHelper.detail("Berhasil Input Password");
		
		LogHelper.step("Tap Login Button");
		submitBtn.click();
		LogHelper.detail("Success Tap Login Button");
		
		if(alertMessage.isDisplayed()) {	
			
			LogHelper.step("Verify Negative Login ");
		    LogHelper.detail("Succesfully Negative Case");
		}
		else{
		 	LogHelper.step("Verify Negative Login");
		    LogHelper.detail("Login Success");
		    Assert.fail("Verify Negative Login failed");
		}
	}
	
	
}

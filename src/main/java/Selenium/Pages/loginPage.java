package Selenium.Pages;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Reporter;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;

public class loginPage {

	AndroidDriver driver;
	
	public loginPage(AndroidDriver driver) {
		
		this.driver = driver;
		PageFactory.initElements(new AppiumFieldDecorator(driver), this);
	}
	
	@AndroidFindBy(accessibility="test-Username")
	private WebElement usernameField;
	public void setUsername(String username) {
		usernameField.sendKeys(username);
	}
	
	@AndroidFindBy(accessibility="test-Password")
	private WebElement passwordField;
	public void setPassword(String passwords) {
	passwordField.sendKeys(passwords);
	}
	
	@AndroidFindBy(accessibility="test-LOGIN")
	private WebElement submitBtn;
	public void clickLoginBtn() {
		submitBtn.click();
	}
	
	@AndroidFindBy(accessibility="test-Error message")
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
	private WebElement stdUser;
	public void getStandardUser() {
		stdUser.click();
	}
	
	public void scrollIntoText(String text) {
		
		driver.findElement(AppiumBy.androidUIAutomator("new UiScrollable(new UiSelector()).scrollIntoView(text(\""+text+"\"));"));
	}
	
	@AndroidFindBy(accessibility="test-PRODUCTS")
	private WebElement listProducts;
	public void verifySuccessLogin() {
		
		if(listProducts.isDisplayed()) 
		{
			Reporter.log("Success Login");
		}
	else
		{
			Reporter.log("Failed Login");
		}
	}
	
	
}

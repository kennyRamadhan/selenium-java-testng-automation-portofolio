package KennyRamadhan.FlipTest;

import org.openqa.selenium.By;

import io.appium.java_client.AppiumBy;

public class locatorElements  {
	
	public static By username() {
		
		return AppiumBy.accessibilityId("test-Username");
	}
	public static By standardUser() {
		
		return AppiumBy.accessibilityId("test-standard_user");	
	}
	
	public static By password() {
		
		return AppiumBy.accessibilityId("test-Password");
	}
	
	public static By loginBtn() {
		
		return AppiumBy.accessibilityId("test-LOGIN");
	}
	
	public static By alertMessage() {
		
		return AppiumBy.accessibilityId("test-Error message");
	}
	
	public static By listProducts() {
		
		return AppiumBy.accessibilityId("test-PRODUCTS");
	}
	
	public static By priceList() {
		
		return AppiumBy.xpath("//android.widget.TextView[@content-desc='test-Price']");
	}
	
	public static By filterBtn() {
		
		return AppiumBy.accessibilityId("test-Modal Selector Button");
	}
	
	public static By ascendingSorting() {
		
		return AppiumBy.xpath("//android.widget.TextView[@text='Price (low to high)']");
	}
	
	public static By listProductsName() {
		
		return AppiumBy.xpath("//android.widget.TextView[@content-desc='test-Item title']");
	}
	
	public static By choosenproducts() {
		
		return AppiumBy.xpath("//android.view.ViewGroup[@content-desc='test-Description']/android.widget.TextView[1]");
	}
	
	public static By addToCart() {
		
		return AppiumBy.accessibilityId("test-ADD TO CART");
	}
	
	public static By cart() {
		
		return AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-Cart\"]/android.view.ViewGroup/android.widget.TextView");
	}
	
	public static By addToCart2() {
		
		return AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-ADD TO CART\"]");
	}
	
	public static By continueShopping() {
		
		return AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-CONTINUE SHOPPING\"]");
	}
	
	public static By detailsProducts() {
		
		return AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[2]");
	}
	
	public static By detailsProductsInCart() {
		
		return AppiumBy.xpath("//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView[2]");
	}
	
	public static By checkout() {
		
		return AppiumBy.accessibilityId("test-CHECKOUT");
	}
	
	public static By firstName() {
		
		return AppiumBy.accessibilityId("test-First Name");
	}
	
	public static By lastName() {
		
		return AppiumBy.accessibilityId("test-Last Name");
	}
	

	public static By postalCode() {
		
		return AppiumBy.accessibilityId("test-Zip/Postal Code");
	}
	
	public static By continueBtn() {
		
		return AppiumBy.accessibilityId("test-CONTINUE");
	}
	
	public static By itemTotal() {
		
		return AppiumBy.xpath("//android.widget.TextView[@text='Item total: $39.98']");
	}
	
	public static By finishBtn() {
		
		return AppiumBy.accessibilityId("test-FINISH");
	}
	
	public static By orderComplete() {
		
		return AppiumBy.xpath("//android.widget.ScrollView[@content-desc=\"test-CHECKOUT: COMPLETE!\"]/android.view.ViewGroup/android.widget.TextView[1]");
	}
	
	public static By errorMessageFirstName() {
		
		return AppiumBy.accessibilityId("test-Error message");
	}


}


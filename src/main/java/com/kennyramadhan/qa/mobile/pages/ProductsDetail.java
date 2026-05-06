package com.kennyramadhan.qa.mobile.pages;

import java.time.Duration;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import com.kennyramadhan.qa.core.driver.DriverManager;

import com.kennyramadhan.qa.core.waits.WaitHelpers;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class ProductsDetail {

	private final WaitHelpers utils;

	public ProductsDetail() {

		this.utils = new WaitHelpers();
		PageFactory.initElements(new AppiumFieldDecorator(DriverManager.getDriver(), Duration.ofSeconds(15)), this);
	}

	@AndroidFindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView")
	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeOther[`name == \"test-Description\"`]")
	private WebElement productsDetails;

	@AndroidFindBy(accessibility = "test-ADD TO CART")
	@iOSXCUITFindBy(accessibility = "test-ADD TO CART")
	private WebElement addToCart;

	/**
	 * Ambil semua text dari produk yang ada di cart dan return sebagai
	 * List<String>.
	 */
	public String getDetailsProducts() {

		return productsDetails.getText();

	}

	public void addToCartFromDetailsProducts() {
		utils.scrollIntoText("ADD TO CART");
		addToCart.click();
	}

}

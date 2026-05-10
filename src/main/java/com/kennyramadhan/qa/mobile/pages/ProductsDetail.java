package com.kennyramadhan.qa.mobile.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.core.waits.WaitHelpers;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;

/**
 * Page object for the SwagLabs product details screen.
 *
 * <p>
 * Phase 3 refactor: PageFactory dropped in favor of explicit {@code By} locator
 * constants and {@code driver.findElement(...)} at use site. Eliminates the
 * {@code RemoteWebElement$ByteBuddy.getRect()} proxy chain that caused
 * stale-element failures under Appium 9.x.
 */
public class ProductsDetail {

	private static final By DESCRIPTION_ANDROID = AppiumBy
			.xpath("//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView");
	private static final By DESCRIPTION_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeOther[`name == \"test-Description\"`]");

	private static final By ADD_TO_CART = AppiumBy.accessibilityId("test-ADD TO CART");

	private final AppiumDriver driver;
	private final WaitHelpers utils;

	public ProductsDetail() {
		this.driver = DriverManager.getDriver();
		this.utils = new WaitHelpers();
	}

	private boolean isIOS() {
		return "iOS".equalsIgnoreCase(String.valueOf(driver.getCapabilities().getCapability("platformName")));
	}

	private WebElement waitFor(By locator) {
		return new WebDriverWait(driver, Duration.ofSeconds(15))
				.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Returns the textual description of the currently displayed product.
	 */
	public String getDetailsProducts() {
		return waitFor(isIOS() ? DESCRIPTION_IOS : DESCRIPTION_ANDROID).getText();
	}

	public void addToCartFromDetailsProducts() {
		utils.scrollIntoText("ADD TO CART");
		waitFor(ADD_TO_CART).click();
	}
}

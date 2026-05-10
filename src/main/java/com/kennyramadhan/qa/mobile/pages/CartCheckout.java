package com.kennyramadhan.qa.mobile.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;

import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.core.reporting.LogHelper;
import com.kennyramadhan.qa.core.waits.WaitHelpers;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;

/**
 * Page object for the SwagLabs cart + checkout flow.
 *
 * <p>
 * Phase 3 refactor: PageFactory dropped. iOS-only locator constants preserved
 * alongside Android constants per locked decision (keep iOS compilable but
 * dormant). iOS-only methods (getItemTotal, getTax, getGrandTotal,
 * verifyItemTotalMatchesCart, verifyGrandTotalCalculation) will throw
 * NoSuchElementException on Android — pre-existing behavior, not introduced by
 * this refactor.
 */
public class CartCheckout {

	private static final By CHECKOUT_BTN = AppiumBy.accessibilityId("test-CHECKOUT");
	private static final By FIRST_NAME = AppiumBy.accessibilityId("test-First Name");
	private static final By LAST_NAME = AppiumBy.accessibilityId("test-Last Name");
	private static final By ZIP_CODE = AppiumBy.accessibilityId("test-Zip/Postal Code");
	private static final By CONTINUE_BTN = AppiumBy.accessibilityId("test-CONTINUE");
	private static final By FINISH_BTN = AppiumBy.accessibilityId("test-FINISH");
	private static final By CONTINUE_SHOPPING_BTN = AppiumBy.accessibilityId("test-CONTINUE SHOPPING");
	private static final By ERROR_MSG = AppiumBy.accessibilityId("test-Error message");

	private static final By ORDER_COMPLETE_ANDROID = AppiumBy.xpath(
			"//android.widget.ScrollView[@content-desc=\"test-CHECKOUT: COMPLETE!\"]/android.view.ViewGroup/android.widget.TextView[1]");
	private static final By ORDER_COMPLETE_IOS = AppiumBy.accessibilityId("THANK YOU FOR YOU ORDER");

	// iOS-only fields (no Android equivalent in current UI; methods using these
	// will throw on Android — iOS dormant per locked Phase 3 decision).
	private static final By PRICE_LIST_CART_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeOther[`name == \"test-Price\"`]");
	private static final By ITEM_TOTAL_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeStaticText[`name BEGINSWITH 'Item total:'`]");
	private static final By TAX_IOS = AppiumBy.iOSClassChain("**/XCUIElementTypeStaticText[`name BEGINSWITH 'Tax:'`]");
	private static final By TOTAL_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeStaticText[`name BEGINSWITH 'Total:'`]");

	private final AppiumDriver driver;
	private final WaitHelpers utils;

	public CartCheckout() {
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

	public void clickContinueShoppingBtn() {
		utils.scrollIntoText("CONTINUE SHOPPING");
		waitFor(CONTINUE_SHOPPING_BTN).click();
	}

	public void checkoutInformation(String firstName, String lastName, String postalCode) {
		utils.scrollIntoText("CHECKOUT");
		waitFor(CHECKOUT_BTN).click();
		LogHelper.step("Input Information Customer");

		waitFor(FIRST_NAME).sendKeys(firstName);
		LogHelper.detail("Input First Name");

		waitFor(LAST_NAME).sendKeys(lastName);
		LogHelper.detail("Input Last Name");

		waitFor(ZIP_CODE).sendKeys(postalCode);
		LogHelper.detail("Input Postal Code");

		waitFor(CONTINUE_BTN).click();
		LogHelper.detail("Tap Continue Button");
	}

	public void verifyOrderComplete() {
		By locator = isIOS() ? ORDER_COMPLETE_IOS : ORDER_COMPLETE_ANDROID;
		if (waitFor(locator).isDisplayed()) {
			LogHelper.step("Verify Order Complete");
		} else {
			LogHelper.step("Verify Order Complete");
			Assert.fail();
		}
	}

	public void verifyErrorMessage() {
		if (waitFor(ERROR_MSG).isDisplayed()) {
			LogHelper.step("Verify Order Complete");
		} else {
			LogHelper.step("Verify Order Complete");
			Assert.fail();
		}
	}

	public void clickFinishBtn() {
		waitFor(FINISH_BTN).click();
	}

	public Double getTotalPriceBeforeCheckout() {
		double totalAmount = 0.0;

		List<WebElement> priceElements = driver.findElements(PRICE_LIST_CART_IOS);
		for (WebElement priceElement : priceElements) {
			String rawText = priceElement.getText();
			if (rawText == null || rawText.isEmpty()) {
				Reporter.log("⚠️ Skipping empty price element");
				continue;
			}

			String cleanPrice = rawText.replaceAll("[^0-9.]", "");

			try {
				double price = Double.parseDouble(cleanPrice);
				totalAmount += price;
			} catch (NumberFormatException e) {
				Reporter.log("❌ Failed to parse price: " + rawText);
			}
		}

		Reporter.log("✅ Total Amount Price Without Taxes: " + totalAmount);
		return totalAmount;
	}

	/**
	 * Returns the Item Total value from the checkout overview screen.
	 * <p>
	 * iOS-only: throws on Android (locator unavailable).
	 */
	public double getItemTotal() {
		LogHelper.step("Ambil nilai Item Total di halaman Checkout");
		String value = driver.findElement(ITEM_TOTAL_IOS).getAttribute("value");
		LogHelper.detail("Item Total ditemukan → " + value);
		return parsePrice(value);
	}

	/**
	 * Returns the Tax value from the checkout overview screen.
	 * <p>
	 * iOS-only: throws on Android (locator unavailable).
	 */
	public double getTax() {
		LogHelper.step("Ambil nilai Tax di halaman Checkout");
		String value = driver.findElement(TAX_IOS).getAttribute("value");
		LogHelper.detail("Tax ditemukan → " + value);
		return parsePrice(value);
	}

	/**
	 * Returns the Grand Total value from the checkout overview screen.
	 * <p>
	 * iOS-only: throws on Android (locator unavailable).
	 */
	public double getGrandTotal() {
		LogHelper.step("Ambil nilai Grand Total di halaman Checkout");
		String value = driver.findElement(TOTAL_IOS).getAttribute("value");
		LogHelper.detail("Grand Total ditemukan → " + value);
		return parsePrice(value);
	}

	public void verifyItemTotalMatchesCart(double cartTotal) {
		LogHelper.step("Verifikasi Item Total sesuai dengan total harga keranjang");
		double itemTotal = getItemTotal();
		LogHelper.detail("Expected (Cart Total): $" + cartTotal);
		LogHelper.detail("Actual (Item Total): $" + itemTotal);

		if (Math.abs(itemTotal - cartTotal) > 0.01) {
			throw new AssertionError("❌ Item Total tidak sesuai dengan total di keranjang!");
		}
		LogHelper.detail("✅ Item Total sesuai dengan total di keranjang.");
	}

	public void verifyGrandTotalCalculation() {
		LogHelper.step("Verifikasi perhitungan Grand Total (Item Total + Tax)");
		double itemTotal = getItemTotal();
		double tax = getTax();
		double grandTotal = getGrandTotal();

		double expected = itemTotal + tax;
		LogHelper.detail("Expected Grand Total: $" + expected);
		LogHelper.detail("Actual Grand Total: $" + grandTotal);

		if (Math.abs(grandTotal - expected) > 0.01) {
			throw new AssertionError("❌ Grand Total tidak sesuai perhitungan!");
		}
		LogHelper.detail("✅ Grand Total sesuai perhitungan.");
	}

	private double parsePrice(String rawText) {
		return Double.parseDouble(rawText.replaceAll("[^0-9.]", ""));
	}
}

package com.kennyramadhan.qa.mobile.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Reporter;

import com.kennyramadhan.qa.core.reporting.LogHelper;
import com.kennyramadhan.qa.core.waits.WaitHelpers;

import io.appium.java_client.AppiumBy;

/**
 * Page object for the SwagLabs cart + checkout flow.
 *
 * <p>
 * Phase 3 commit 4: extends {@link BaseMobilePage} for shared driver +
 * per-action screenshot helpers. iOS-only locator constants preserved alongside
 * Android constants per locked decision (keep iOS compilable but dormant).
 */
public class CartCheckout extends BaseMobilePage {

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

	// iOS-only fields (no Android equivalent; methods using these throw on
	// Android).
	private static final By PRICE_LIST_CART_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeOther[`name == \"test-Price\"`]");
	private static final By ITEM_TOTAL_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeStaticText[`name BEGINSWITH 'Item total:'`]");
	private static final By TAX_IOS = AppiumBy.iOSClassChain("**/XCUIElementTypeStaticText[`name BEGINSWITH 'Tax:'`]");
	private static final By TOTAL_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeStaticText[`name BEGINSWITH 'Total:'`]");

	private final WaitHelpers utils;

	public CartCheckout() {
		super();
		this.utils = new WaitHelpers();
	}

	public void clickContinueShoppingBtn() {
		utils.scrollIntoText("CONTINUE SHOPPING");
		safeClick(CONTINUE_SHOPPING_BTN);
	}

	public void checkoutInformation(String firstName, String lastName, String postalCode) {
		utils.scrollIntoText("CHECKOUT");
		safeClick(CHECKOUT_BTN);
		LogHelper.step("Input Information Customer");

		safeSendKeys(FIRST_NAME, firstName);
		LogHelper.detail("Input First Name");

		safeSendKeys(LAST_NAME, lastName);
		LogHelper.detail("Input Last Name");

		safeSendKeys(ZIP_CODE, postalCode);
		LogHelper.detail("Input Postal Code");

		safeClick(CONTINUE_BTN);
		LogHelper.detail("Tap Continue Button");
	}

	/** Returns true when the order-complete confirmation element is visible. */
	public boolean isOrderCompleteDisplayed() {
		return isDisplayedQuiet(isIOS() ? ORDER_COMPLETE_IOS : ORDER_COMPLETE_ANDROID);
	}

	/** Returns true when the validation-error message element is visible. */
	public boolean isErrorMessageDisplayed() {
		return isDisplayedQuiet(ERROR_MSG);
	}

	public void clickFinishBtn() {
		safeClick(FINISH_BTN);
	}

	public Double getTotalPriceBeforeCheckout() {
		double totalAmount = 0.0;

		List<WebElement> priceElements = driver.findElements(PRICE_LIST_CART_IOS);
		for (WebElement priceElement : priceElements) {
			String rawText = priceElement.getText();
			if (rawText == null || rawText.isEmpty()) {
				Reporter.log("Skipping empty price element");
				continue;
			}

			String cleanPrice = rawText.replaceAll("[^0-9.]", "");

			try {
				double price = Double.parseDouble(cleanPrice);
				totalAmount += price;
			} catch (NumberFormatException e) {
				Reporter.log("Failed to parse price: " + rawText);
			}
		}

		Reporter.log("Total amount price without taxes: " + totalAmount);
		return totalAmount;
	}

	/**
	 * Returns the Item Total value from the checkout overview screen.
	 * <p>
	 * iOS-only: throws on Android (locator unavailable).
	 */
	public double getItemTotal() {
		LogHelper.step("Read Item Total on Checkout");
		String value = driver.findElement(ITEM_TOTAL_IOS).getAttribute("value");
		LogHelper.detail("Item Total: " + value);
		return parsePrice(value);
	}

	/**
	 * Returns the Tax value from the checkout overview screen.
	 * <p>
	 * iOS-only: throws on Android (locator unavailable).
	 */
	public double getTax() {
		LogHelper.step("Read Tax on Checkout");
		String value = driver.findElement(TAX_IOS).getAttribute("value");
		LogHelper.detail("Tax: " + value);
		return parsePrice(value);
	}

	/**
	 * Returns the Grand Total value from the checkout overview screen.
	 * <p>
	 * iOS-only: throws on Android (locator unavailable).
	 */
	public double getGrandTotal() {
		LogHelper.step("Read Grand Total on Checkout");
		String value = driver.findElement(TOTAL_IOS).getAttribute("value");
		LogHelper.detail("Grand Total: " + value);
		return parsePrice(value);
	}

	private double parsePrice(String rawText) {
		return Double.parseDouble(rawText.replaceAll("[^0-9.]", ""));
	}
}

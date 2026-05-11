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

	// Cart line item price locators. Android xpath inferred from SauceLabs
	// canonical content-desc convention (matches ProductsList.PRICE_LIST_ANDROID
	// pattern, but without [1] index — cart enumerates all items, not just first).
	private static final By PRICE_LIST_CART_ANDROID = AppiumBy
			.xpath("//android.view.ViewGroup[@content-desc=\"test-Price\"]/android.widget.TextView");
	private static final By PRICE_LIST_CART_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeOther[`name == \"test-Price\"`]");

	// iOS-only fields below (no Android equivalent in current UI; methods using
	// these throw on Android). Queued for dedicated follow-up commit when the
	// checkout-overview screen can be empirically captured for ground-truth
	// locator text.
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
		// FINISH lives at the bottom of the long overview screen — scroll it
		// into view first (mirrors the scrollIntoText pattern used by
		// checkoutInformation for CHECKOUT and clickContinueShoppingBtn for
		// CONTINUE SHOPPING).
		utils.scrollIntoText("FINISH");
		safeClick(FINISH_BTN);
	}

	public Double getTotalPriceBeforeCheckout() {
		double totalAmount = 0.0;

		By locator = isIOS() ? PRICE_LIST_CART_IOS : PRICE_LIST_CART_ANDROID;
		List<WebElement> priceElements = driver.findElements(locator);
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
	 *
	 * <p>
	 * iOS: reads the value of the iOS-only "Item total:" StaticText.
	 * <p>
	 * Android: the SwagLabs Android build's overview screen does not render a
	 * dedicated Item Total line — only individual line-item prices (test-Price
	 * content-descs). Sums those visible prices instead, which equals the Item
	 * Total by definition (Item Total == sum of items before tax). Empirically
	 * derived via overview-screen XML capture in Phase 5 commit 1 — no breakdown
	 * footer present on Android.
	 */
	public double getItemTotal() {
		LogHelper.step("Read Item Total on Checkout");
		if (isIOS()) {
			String value = driver.findElement(ITEM_TOTAL_IOS).getAttribute("value");
			LogHelper.detail("Item Total: " + value);
			return parsePrice(value);
		}
		double sum = sumVisiblePrices();
		LogHelper.detail("Item Total (Android computed from line items): " + sum);
		return sum;
	}

	/**
	 * Returns the Tax value from the checkout overview screen.
	 *
	 * <p>
	 * iOS: reads the value of the iOS-only "Tax:" StaticText.
	 * <p>
	 * Android: returns 0.0 — the Android overview screen does not display a tax
	 * line. Real tax computation happens server-side and is not surfaced in this
	 * app build.
	 */
	public double getTax() {
		LogHelper.step("Read Tax on Checkout");
		if (isIOS()) {
			String value = driver.findElement(TAX_IOS).getAttribute("value");
			LogHelper.detail("Tax: " + value);
			return parsePrice(value);
		}
		LogHelper.detail("Tax (Android): 0.0 — no tax line on overview screen");
		return 0.0;
	}

	/**
	 * Returns the Grand Total value from the checkout overview screen.
	 *
	 * <p>
	 * iOS: reads the value of the iOS-only "Total:" StaticText.
	 * <p>
	 * Android: no separate grand-total element on overview; returns the Item Total
	 * (equivalent on Android since {@link #getTax()} returns 0).
	 */
	public double getGrandTotal() {
		LogHelper.step("Read Grand Total on Checkout");
		if (isIOS()) {
			String value = driver.findElement(TOTAL_IOS).getAttribute("value");
			LogHelper.detail("Grand Total: " + value);
			return parsePrice(value);
		}
		double sum = getItemTotal();
		LogHelper.detail("Grand Total (Android, no tax): " + sum);
		return sum;
	}

	/**
	 * Sums the parseable price text of all currently-visible elements matching
	 * {@link #PRICE_LIST_CART_ANDROID} on the screen. Used by
	 * {@link #getItemTotal()} on Android to compute the Item Total equivalent from
	 * line items when no dedicated total field exists.
	 */
	private double sumVisiblePrices() {
		double total = 0.0;
		for (WebElement el : driver.findElements(PRICE_LIST_CART_ANDROID)) {
			String text = el.getText();
			if (text == null || text.isBlank()) {
				continue;
			}
			try {
				total += parsePrice(text);
			} catch (NumberFormatException ignored) {
				// Skip non-numeric text gracefully.
			}
		}
		return total;
	}

	private double parsePrice(String rawText) {
		return Double.parseDouble(rawText.replaceAll("[^0-9.]", ""));
	}
}

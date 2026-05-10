package com.kennyramadhan.qa.mobile.pages;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kennyramadhan.qa.core.reporting.LogHelper;
import com.kennyramadhan.qa.core.waits.WaitHelpers;

import io.appium.java_client.AppiumBy;

/**
 * Page object for the SwagLabs products-list screen.
 *
 * <p>
 * Phase 3 commit 4: extends {@link BaseMobilePage} for shared driver +
 * per-action screenshot helpers. List-iter actions and tapByCoordinates calls
 * invoke {@link #captureScreenshot()} inline since they don't fit the
 * locator-based safe* helpers.
 */
public class ProductsList extends BaseMobilePage {

	private static final Logger log = LoggerFactory.getLogger(ProductsList.class);

	private static final By ADD_TO_CART = AppiumBy.accessibilityId("test-ADD TO CART");

	private static final By ITEM_TITLES_ANDROID = AppiumBy
			.xpath("//android.widget.TextView[@content-desc='test-Item title']");
	private static final By ITEM_TITLES_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeStaticText[`name == \"test-Item title\"`]");

	private static final By PRODUCTS_TITLE_ANDROID = AppiumBy
			.xpath("//android.widget.TextView[@content-desc='test-PRODUCTS']");
	private static final By PRODUCTS_TITLE_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeStaticText[`name == \"PRODUCTS\"`]");

	private static final By LIST_ADD_TO_CART_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeOther[`name == \"test-ADD TO CART\"`]");

	/**
	 * Cart button — accessibility-id "test-Cart" works on both Android and iOS per
	 * inventory dump. iOS variant assumed valid per SauceLabs canonical naming
	 * convention; not empirically verified against iOS Simulator.
	 */
	private static final By CART_BTN = AppiumBy.accessibilityId("test-Cart");

	private static final By YOUR_CART_TEXT_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeStaticText[`name == \"YOUR CART\"`]");

	private static final By FILTER_BTN = AppiumBy.accessibilityId("test-Modal Selector Button");

	private static final By LOW_TO_HIGH_ANDROID = AppiumBy
			.xpath("//android.widget.TextView[@text='Price (low to high)']");
	private static final By LOW_TO_HIGH_IOS = AppiumBy.iOSNsPredicateString("name == 'Price (low to high)'");

	private static final By PRICE_LIST_ANDROID = AppiumBy
			.xpath("(//android.view.ViewGroup[@content-desc=\"test-Price\"])[1]/android.widget.TextView");
	private static final By PRICE_LIST_IOS = AppiumBy.xpath("//XCUIElementTypeStaticText[@name=\"test-Price\"]");

	private final WaitHelpers utils;

	public ProductsList() {
		super();
		this.utils = new WaitHelpers();
	}

	public void clickFilterBtn() {
		safeClick(FILTER_BTN);
	}

	public void clickLowToHigh() {
		safeClick(isIOS() ? LOW_TO_HIGH_IOS : LOW_TO_HIGH_ANDROID);
	}

	public void tapCart() {
		utils.tapByCoordinates(waitFor(CART_BTN), 5);
		captureScreenshot();
	}

	public List<WebElement> getPriceElements() {
		return driver.findElements(isIOS() ? PRICE_LIST_IOS : PRICE_LIST_ANDROID);
	}

	public void addProductsToCartDirectlyFromListMenu() {
		List<WebElement> addButtons = isIOS()
				? driver.findElements(LIST_ADD_TO_CART_IOS)
				: driver.findElements(ADD_TO_CART);
		addButtons.get(0).click();
		captureScreenshot();
	}

	public void selectProducts(String productName) {
		LogHelper.step("Search and click product: " + productName);

		List<WebElement> items = driver.findElements(isIOS() ? ITEM_TITLES_IOS : ITEM_TITLES_ANDROID);
		boolean productFound = false;
		for (WebElement item : items) {
			if (item.getText().equalsIgnoreCase(productName)) {
				productFound = true;
				item.click();
				captureScreenshot();
				LogHelper.detail("Clicked product: " + productName);
				log.info("Selected product: {}", productName);
				break;
			}
		}

		if (!productFound) {
			LogHelper.fail("Product '" + productName + "' not found in the list.");
			throw new IllegalStateException("Product '" + productName + "' not found in list");
		}
	}

	public void addMultipleProducts() {
		LogHelper.step("Select product");
		List<WebElement> addButtons = isIOS()
				? driver.findElements(LIST_ADD_TO_CART_IOS)
				: driver.findElements(ADD_TO_CART);
		addButtons.get(0).click();
		captureScreenshot();
		addButtons.get(1).click();
		captureScreenshot();
		LogHelper.detail("Selected multiple products");
	}

	/**
	 * Returns true when the products-list title element is currently visible, used
	 * by tests to verify navigation back to the list screen.
	 */
	public boolean isProductsTitleDisplayed() {
		return isDisplayedQuiet(isIOS() ? PRODUCTS_TITLE_IOS : PRODUCTS_TITLE_ANDROID);
	}

	public void addAllProducts() {
		int totalAdded = 0;
		LogHelper.step("Add All Products To Carts");
		while (true) {
			List<WebElement> addButtons = isIOS()
					? driver.findElements(LIST_ADD_TO_CART_IOS)
					: driver.findElements(ADD_TO_CART);

			if (addButtons.isEmpty()) {
				LogHelper.detail("All products added to cart");
				log.info("[OK] All products added to cart");
				break;
			}

			for (WebElement addButton : addButtons) {
				try {
					addButton.click();

					// Poll the button's identifier attribute until it flips to "test-REMOVE",
					// indicating the add-to-cart click took effect. Replaces
					// ExpectedConditions.attributeToBe which routes through Selenium's
					// getElementValueOfCssProperty — UiAutomator2 7.x doesn't support that
					// CSS-era query path. FluentWait with element.getAttribute(...) goes
					// through Appium's native attribute API instead. Android exposes the
					// identifier via "content-desc"; iOS via "name".
					String attrKey = isIOS() ? "name" : "content-desc";
					new FluentWait<>(driver).withTimeout(Duration.ofSeconds(2)).pollingEvery(Duration.ofMillis(250))
							.ignoring(StaleElementReferenceException.class)
							.until(d -> "test-REMOVE".equals(addButton.getAttribute(attrKey)));

					totalAdded++;
					log.info("[CART] Product #{} added", totalAdded);
				} catch (StaleElementReferenceException ignored) {
					// button removed from DOM, continue
				}
			}
			captureScreenshot();

			scrollDown();
		}
		LogHelper.step("Tap Cart Button & Verify Cart Page");

		try {
			WebElement cartBtnElement = waitFor(CART_BTN);
			if (cartBtnElement.isDisplayed()) {
				LogHelper.detail("Cart button found");

				utils.tapByCoordinates(cartBtnElement, 5);
				captureScreenshot();
				LogHelper.detail("Cart button tapped");

				if (isIOS() && waitFor(YOUR_CART_TEXT_IOS).isDisplayed()) {
					LogHelper.detail("Cart page reached");
				} else if (!isIOS()) {
					LogHelper.detail("Cart page reached (Android: cart-text validation skipped)");
				} else {
					throw new IllegalStateException("Cart page did not appear after tap");
				}
			} else {
				throw new IllegalStateException("Cart button not found");
			}
		} catch (RuntimeException e) {
			LogHelper.detail("Exception: " + e.getMessage());
			throw new IllegalStateException("Failed to tap cart button or verify cart page", e);
		}
	}

	/**
	 * Scrolls the products list down using {@code mobile: scrollGesture} (the
	 * UiAutomator2 7.x replacement for the removed {@code mobile: scroll} script).
	 * Uses screen-area-bound coordinates so the gesture does not depend on a
	 * specific scrollable container element being findable — works on both real
	 * devices (Xiaomi 1080×2460) and emulators with different screen geometries.
	 */
	private void scrollDown() {
		Map<String, Object> params = new HashMap<>();
		params.put("left", 100);
		params.put("top", 400);
		params.put("width", 800);
		params.put("height", 1500);
		params.put("direction", "down");
		params.put("percent", 0.7);
		driver.executeScript("mobile: scrollGesture", params);
	}
}

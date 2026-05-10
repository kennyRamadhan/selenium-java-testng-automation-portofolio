package com.kennyramadhan.qa.mobile.pages;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.core.reporting.LogHelper;
import com.kennyramadhan.qa.core.waits.WaitHelpers;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;

/**
 * Page object for the SwagLabs products-list screen.
 *
 * <p>
 * Phase 3 refactor: PageFactory dropped in favor of explicit {@code By} locator
 * constants. Notably {@code CART_BTN} is now defined on Android
 * (accessibility-id "test-Cart" verified present in Android UI inventory) —
 * previously the field was iOS-only, causing the cascade test failure surfaced
 * in B.3.
 */
public class ProductsList {

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
	 * convention; not empirically verified against iOS Simulator. iOS branch
	 * dormant per locked Phase 3 decision.
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

	private final AppiumDriver driver;
	private final WaitHelpers utils;

	public ProductsList() {
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

	public void clickFilterBtn() {
		waitFor(FILTER_BTN).click();
	}

	public void clickLowToHigh() {
		waitFor(isIOS() ? LOW_TO_HIGH_IOS : LOW_TO_HIGH_ANDROID).click();
	}

	public void tapCart() {
		utils.tapByCoordinates(waitFor(CART_BTN), 5);
	}

	public List<WebElement> getPriceElements() {
		return driver.findElements(isIOS() ? PRICE_LIST_IOS : PRICE_LIST_ANDROID);
	}

	public void addProductsToCartDirectlyFromListMenu() {
		List<WebElement> addButtons = isIOS()
				? driver.findElements(LIST_ADD_TO_CART_IOS)
				: driver.findElements(ADD_TO_CART);
		addButtons.get(0).click();
	}

	public void selectProducts(String productName) {
		LogHelper.step("Search and click product: " + productName);

		List<WebElement> items = driver.findElements(isIOS() ? ITEM_TITLES_IOS : ITEM_TITLES_ANDROID);
		boolean productFound = false;
		for (WebElement item : items) {
			if (item.getText().equalsIgnoreCase(productName)) {
				productFound = true;
				item.click();
				LogHelper.detail("Clicked product: " + productName);
				log.info("Product Yang Terpilih Adalah {}", productName);
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
		addButtons.get(1).click();
		LogHelper.detail("Success Selected Products");
	}

	/**
	 * Returns true when the products-list title element is currently visible, used
	 * by tests to verify navigation back to the list screen.
	 */
	public boolean isProductsTitleDisplayed() {
		By titleLocator = isIOS() ? PRODUCTS_TITLE_IOS : PRODUCTS_TITLE_ANDROID;
		try {
			return waitFor(titleLocator).isDisplayed();
		} catch (org.openqa.selenium.NoSuchElementException | org.openqa.selenium.TimeoutException e) {
			return false;
		}
	}

	public void addAllProducts() {
		int totalAdded = 0;
		LogHelper.step("Add All Products To Carts");
		while (true) {
			List<WebElement> addButtons = isIOS()
					? driver.findElements(LIST_ADD_TO_CART_IOS)
					: driver.findElements(ADD_TO_CART);

			if (addButtons.isEmpty()) {
				LogHelper.detail("Verify All Products Add To Cart");
				log.info("[OK] Semua produk berhasil ditambahkan ke keranjang!");
				break;
			}

			for (WebElement addButton : addButtons) {
				try {
					addButton.click();

					new WebDriverWait(driver, Duration.ofSeconds(2))
							.until(ExpectedConditions.attributeToBe(addButton, "name", "test-REMOVE"));

					totalAdded++;
					log.info("[CART] Produk ke-{} berhasil ditambahkan.", totalAdded);
				} catch (StaleElementReferenceException ignored) {
					// button removed from DOM, continue
				}
			}

			Map<String, Object> params = new HashMap<>();
			params.put("direction", "down");
			driver.executeScript("mobile: scroll", params);
		}
		LogHelper.step("Tap Cart Button & Verifikasi Halaman Cart");

		try {
			WebElement cartBtnElement = waitFor(CART_BTN);
			if (cartBtnElement.isDisplayed()) {
				LogHelper.detail("Cart button ditemukan.");

				utils.tapByCoordinates(cartBtnElement, 5);
				LogHelper.detail("Cart button berhasil di-tap.");

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
}

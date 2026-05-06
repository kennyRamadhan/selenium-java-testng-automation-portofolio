package com.kennyramadhan.qa.mobile.pages;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.google.common.collect.Iterables;

import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.core.reporting.LogHelper;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

import com.kennyramadhan.qa.core.waits.WaitHelpers;

public class ProductsList {

	private static final Logger log = LoggerFactory.getLogger(ProductsList.class);

	private final WaitHelpers utils;

	public ProductsList() {

		this.utils = new WaitHelpers();
		PageFactory.initElements(new AppiumFieldDecorator(DriverManager.getDriver(), Duration.ofSeconds(15)), this);
	}

	@AndroidFindBy(accessibility = "test-ADD TO CART")
	@iOSXCUITFindBy(accessibility = "test-ADD TO CART")
	private WebElement addToCart;

	@AndroidFindBy(xpath = "//android.widget.TextView[@content-desc='test-Item title']")
	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name == \"test-Item title\"`]")
	private List<WebElement> listProducts;

	@AndroidFindBy(xpath = "//android.widget.TextView[@content-desc='PRODUCTS']")
	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name == \"PRODUCTS\"`]")
	private WebElement productsTextTitle;

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeOther[`name == \"test-ADD TO CART\"`]")
	private List<WebElement> listAddToCart;

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeOther[`name == \"test-Cart\"`]/XCUIElementTypeOther")
	private WebElement cartBtn;

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name == \"YOUR CART\"`]")
	private WebElement yourCartText;

	@AndroidFindBy(accessibility = "test-Modal Selector Button")
	@iOSXCUITFindBy(accessibility = "test-Modal Selector Button")
	private WebElement filterBtn;

	@AndroidFindBy(xpath = "//android.widget.TextView[@text='Price (low to high)']")
	@iOSXCUITFindBy(iOSNsPredicate = "name == 'Price (low to high)'")
	private WebElement lowToHigh;

	@AndroidFindBy(xpath = "(//android.view.ViewGroup[@content-desc=\"test-Price\"])[1]/android.widget.TextView")
	@iOSXCUITFindBy(xpath = "//XCUIElementTypeStaticText[@name=\"test-Price\"]")
	private List<WebElement> priceList;

	public void clickFilterBtn() {
		filterBtn.click();
	}

	public void clickLowToHigh() {
		lowToHigh.click();
	}

	public void tapCart() {

		utils.tapByCoordinates(cartBtn, 5);

	}

	public List<WebElement> getPriceElements() {
		return priceList;
	}

	public void addProductsToCartDirectlyFromListMenu() {

		WebElement lastElement = (WebElement) Iterables.get(listAddToCart, 0);
		lastElement.click();

	}

	public void selectProducts(String productName) {

		LogHelper.step("Search and click product: " + productName);

		int sizeProducts = listProducts.size();
		boolean productFound = false;
		for (int i = 0; i < sizeProducts; i++) {
			String currentProductName = listProducts.get(i).getText();

			if (currentProductName.equalsIgnoreCase(productName)) {
				productFound = true;

				// Klik produk sesuai index
				listProducts.get(i).click();
				LogHelper.detail("Clicked product: " + currentProductName);
				log.info("Product Yang Terpilih Adalah {}", currentProductName);

				break; // Stop loop setelah ketemu
			}

		}

		if (!productFound) {
			LogHelper.fail("❌ Product '" + productName + "' not found in the list.");
			Assert.fail("Product '" + productName + "' not found!");
		}

	}

	public void addMultipleProducts() {
		LogHelper.step("Select product");
		WebElement lastElement = (WebElement) Iterables.get(listAddToCart, 0);
		WebElement last = (WebElement) Iterables.get(listAddToCart, 1);
		lastElement.click();
		last.click();
		LogHelper.detail("Success Selected Products");
	}

	public void verifyBackToListProducts() {

		if (productsTextTitle.isDisplayed()) {
			LogHelper.step("Verify Back To List Products Menu");
			LogHelper.detail("Success Back To Menu List Products");

		} else {
			LogHelper.step("Verify Back To List Products Menu");
			Assert.fail("Failed Back To List Menu");
		}
	}

	public void addAllProducts() {

		int totalAdded = 0;
		LogHelper.step("Add All Products To Carts");
		while (true) {
			// Ambil ulang array tombol ADD TO CART yang terlihat

			// Kalau array kosong, berhenti
			if (listAddToCart.isEmpty()) {
				LogHelper.detail("Verify All Products Add To Cart");
				log.info("[OK] Semua produk berhasil ditambahkan ke keranjang!");
				break;
			}

			// Klik tombol satu per satu
			for (WebElement addButton : listAddToCart) {
				try {
					addButton.click();

					// Pastikan tombol berubah jadi REMOVE (menandakan sukses klik)
					new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(2))
							.until(ExpectedConditions.attributeToBe(addButton, "name", "test-REMOVE"));

					totalAdded++;
					log.info("[CART] Produk ke-{} berhasil ditambahkan.", totalAdded);
				} catch (StaleElementReferenceException ignored) {
					// Tombol sudah hilang dari DOM → lanjutkan saja
				}
			}

			// Scroll agar menemukan tombol berikutnya
			Map<String, Object> params = new HashMap<>();
			params.put("direction", "down");
			DriverManager.getDriver().executeScript("mobile: scroll", params);
		}
		LogHelper.step("Tap Cart Button & Verifikasi Halaman Cart");

		try {
			// 1️⃣ Pastikan button ada & visible
			if (cartBtn.isDisplayed()) {
				LogHelper.detail("Cart button ditemukan.");

				// 2️⃣ Tap button
				utils.tapByCoordinates(cartBtn, 5);
				LogHelper.detail("Cart button berhasil di-tap.");

				// 3️⃣ Verifikasi halaman cart
				if (yourCartText.isDisplayed()) {
					LogHelper.detail("Verifikasi Masuk Keranjang");
				} else {

					Assert.fail("Halaman Cart tidak muncul setelah tap.");
				}

			} else {

				Assert.fail("Cart button tidak ditemukan.");
			}

		} catch (Exception e) {
			LogHelper.detail("Exception: " + e.getMessage());
			Assert.fail("Gagal tap cart button atau verifikasi cart page.", e);
		}

	}

}

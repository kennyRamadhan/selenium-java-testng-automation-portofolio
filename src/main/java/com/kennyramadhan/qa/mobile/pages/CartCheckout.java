package com.kennyramadhan.qa.mobile.pages;

// TODO Phase 3: Bahasa Indonesia content pending — translation absorbed into Phase 3.1 rewrite.

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.Reporter;

import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.core.reporting.LogHelper;
import com.kennyramadhan.qa.core.waits.WaitHelpers;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import io.appium.java_client.pagefactory.iOSXCUITFindBy;

public class CartCheckout {

	private final WaitHelpers utils;

	public CartCheckout() {

		this.utils = new WaitHelpers();
		PageFactory.initElements(new AppiumFieldDecorator(DriverManager.getDriver(), Duration.ofSeconds(15)), this);
	}

	@AndroidFindBy(accessibility = "test-CHECKOUT")
	@iOSXCUITFindBy(accessibility = "test-CHECKOUT")
	private WebElement checkout;

	@AndroidFindBy(accessibility = "test-First Name")
	@iOSXCUITFindBy(accessibility = "test-First Name")
	private WebElement firstNameField;

	@AndroidFindBy(accessibility = "test-FINISH")
	@iOSXCUITFindBy(accessibility = "test-FINISH")
	private WebElement finishBtn;

	@AndroidFindBy(accessibility = "test-CONTINUE")
	@iOSXCUITFindBy(accessibility = "test-CONTINUE")
	private WebElement continueBtn;

	@AndroidFindBy(accessibility = "test-Zip/Postal Code")
	@iOSXCUITFindBy(accessibility = "test-Zip/Postal Code")
	private WebElement postalCodeField;

	@AndroidFindBy(accessibility = "test-Last Name")
	@iOSXCUITFindBy(accessibility = "test-Last Name")
	private WebElement lastNameField;

	@AndroidFindBy(accessibility = "test-CONTINUE SHOPPING")
	@iOSXCUITFindBy(accessibility = "test-CONTINUE SHOPPING")
	private WebElement continueShoppingBtn;

	@AndroidFindBy(accessibility = "test-Error message")
	@iOSXCUITFindBy(accessibility = "test-Error message")
	private WebElement errorMessage;

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeOther[`name == \"test-Price\"`]")
	private List<WebElement> priceListCart;

	@AndroidFindBy(xpath = "//android.widget.ScrollView[@content-desc=\"test-CHECKOUT: COMPLETE!\"]/android.view.ViewGroup/android.widget.TextView[1]")
	@iOSXCUITFindBy(accessibility = "THANK YOU FOR YOU ORDER")
	private WebElement orderComplete;

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name BEGINSWITH 'Item total:'`]")
	private WebElement itemTotalText;

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name BEGINSWITH 'Tax:'`]")
	private WebElement taxText;

	@iOSXCUITFindBy(iOSClassChain = "**/XCUIElementTypeStaticText[`name BEGINSWITH 'Total:'`]")
	private WebElement totalText;

	public void clickContinueShoppingBtn() {

		utils.scrollIntoText("CONTINUE SHOPPING");
		continueShoppingBtn.click();
	}

	public void checkoutInformation(String firstName, String lastName, String postalCode) {

		utils.scrollIntoText("CHECKOUT");
		checkout.click();
		LogHelper.step("Input Information Customer");

		firstNameField.sendKeys(firstName);
		LogHelper.detail("Input First Name");

		lastNameField.sendKeys(lastName);
		LogHelper.detail("Input Last Name");

		postalCodeField.sendKeys(postalCode);
		LogHelper.detail("Input Postal Code");

		continueBtn.click();
		LogHelper.detail("Tap Continue Button");
	}

	public void verifyOrderComplete() {

		if (orderComplete.isDisplayed()) {
			LogHelper.step("Verify Order Complete");
		} else {
			LogHelper.step("Verify Order Complete");
			Assert.fail();
		}
	}

	public void verifyErrorMessage() {

		if (errorMessage.isDisplayed()) {
			LogHelper.step("Verify Order Complete");
		} else {
			LogHelper.step("Verify Order Complete");
			Assert.fail();
		}
	}

	public void clickFinishBtn() {
		finishBtn.click();
	}

	public Double getTotalPriceBeforeCheckout() {

		double totalAmount = 0.0;

		for (WebElement priceElement : priceListCart) {
			String rawText = priceElement.getText(); // contoh: "$29.99 REMOVE"
			if (rawText == null || rawText.isEmpty()) {
				Reporter.log("⚠️ Skipping empty price element");
				continue;
			}

			// Bersihkan string dari semua huruf kecuali angka & titik
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
	 * Ambil nilai Item Total dari halaman Checkout
	 * 
	 * @return double item total
	 */
	public double getItemTotal() {
		LogHelper.step("Ambil nilai Item Total di halaman Checkout");
		String value = itemTotalText.getAttribute("value");
		LogHelper.detail("Item Total ditemukan → " + value);
		return parsePrice(value);
	}

	/**
	 * Ambil nilai Tax dari halaman Checkout
	 * 
	 * @return double tax
	 */
	public double getTax() {
		LogHelper.step("Ambil nilai Tax di halaman Checkout");
		String value = taxText.getAttribute("value");
		LogHelper.detail("Tax ditemukan → " + value);
		return parsePrice(value);
	}

	/**
	 * Ambil nilai Grand Total dari halaman Checkout
	 * 
	 * @return double grand total
	 */
	public double getGrandTotal() {
		LogHelper.step("Ambil nilai Grand Total di halaman Checkout");
		String value = totalText.getAttribute("value");
		LogHelper.detail("Grand Total ditemukan → " + value);
		return parsePrice(value);
	}

	/**
	 * Validasi bahwa Item Total sesuai dengan total harga di keranjang
	 * 
	 * @param cartTotal nilai total dari cartPage
	 */
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

	/**
	 * Validasi bahwa Grand Total = Item Total + Tax
	 */
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

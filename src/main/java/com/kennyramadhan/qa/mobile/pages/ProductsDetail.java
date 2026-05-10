package com.kennyramadhan.qa.mobile.pages;

import org.openqa.selenium.By;

import com.kennyramadhan.qa.core.waits.WaitHelpers;

import io.appium.java_client.AppiumBy;

/**
 * Page object for the SwagLabs product details screen.
 *
 * <p>
 * Phase 3 commit 4: extends {@link BaseMobilePage} for shared driver +
 * per-action screenshot helpers.
 */
public class ProductsDetail extends BaseMobilePage {

	private static final By DESCRIPTION_ANDROID = AppiumBy
			.xpath("//android.view.ViewGroup[@content-desc=\"test-Description\"]/android.widget.TextView");
	private static final By DESCRIPTION_IOS = AppiumBy
			.iOSClassChain("**/XCUIElementTypeOther[`name == \"test-Description\"`]");

	private static final By ADD_TO_CART = AppiumBy.accessibilityId("test-ADD TO CART");

	private final WaitHelpers utils;

	public ProductsDetail() {
		super();
		this.utils = new WaitHelpers();
	}

	/** Returns the textual description of the currently displayed product. */
	public String getDetailsProducts() {
		return waitFor(isIOS() ? DESCRIPTION_IOS : DESCRIPTION_ANDROID).getText();
	}

	public void addToCartFromDetailsProducts() {
		utils.scrollIntoText("ADD TO CART");
		safeClick(ADD_TO_CART);
	}
}

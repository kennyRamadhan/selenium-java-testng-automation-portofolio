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

	private static final By ADD_TO_CART = AppiumBy.accessibilityId("test-ADD TO CART");
	private static final By REMOVE = AppiumBy.accessibilityId("test-REMOVE");

	private final WaitHelpers utils;

	public ProductsDetail() {
		super();
		this.utils = new WaitHelpers();
	}

	public void addToCartFromDetailsProducts() {
		utils.scrollIntoText("ADD TO CART");
		safeClick(ADD_TO_CART);
	}

	/**
	 * Returns true when the REMOVE button is visible on the details screen — direct
	 * evidence that ADD TO CART succeeded (button toggles after tap). Render-order
	 * independent; preferred over reading description text.
	 */
	public boolean isRemoveButtonVisible() {
		return isDisplayedQuiet(REMOVE);
	}
}

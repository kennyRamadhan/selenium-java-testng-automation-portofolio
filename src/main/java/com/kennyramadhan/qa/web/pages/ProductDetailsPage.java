package com.kennyramadhan.qa.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** {@code /product_details/{id}} — single product detail page. */
public class ProductDetailsPage extends BaseWebPage {

    private static final By PRODUCT_NAME = By.cssSelector(".product-information h2");
    private static final By PRODUCT_PRICE = By.cssSelector(".product-information span span");
    private static final By QUANTITY_INPUT = By.id("quantity");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector("button.cart");

    public String getProductName() {
        return getText(PRODUCT_NAME);
    }

    public String getProductPrice() {
        return getText(PRODUCT_PRICE);
    }

    @Step("Set quantity to {qty}")
    public ProductDetailsPage setQuantity(int qty) {
        safeSendKeys(QUANTITY_INPUT, String.valueOf(qty));
        return this;
    }

    @Step("Click 'Add to cart' on product details")
    public ProductDetailsPage clickAddToCart() {
        safeClick(ADD_TO_CART_BUTTON);
        return this;
    }
}

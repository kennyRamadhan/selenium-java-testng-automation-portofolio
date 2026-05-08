package com.kennyramadhan.qa.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

/** {@code /checkout} — review address + comment + place order. */
public class CheckoutPage extends BaseWebPage {

    private static final By DELIVERY_ADDRESS_BLOCK = By.cssSelector("#address_delivery li");
    private static final By BILLING_ADDRESS_BLOCK = By.cssSelector("#address_invoice li");
    private static final By COMMENT_TEXTAREA = By.cssSelector("textarea[name='message']");
    private static final By PLACE_ORDER_BUTTON = By.cssSelector(".btn.check_out");
    private static final By REVIEW_ORDER_HEADER =
            By.xpath("//h2[normalize-space()='Review Your Order']");

    public boolean isReviewOrderHeaderDisplayed() {
        return isDisplayed(REVIEW_ORDER_HEADER);
    }

    public List<String> getDeliveryAddress() {
        return findAll(DELIVERY_ADDRESS_BLOCK).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    public List<String> getBillingAddress() {
        return findAll(BILLING_ADDRESS_BLOCK).stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    @Step("Enter checkout comment")
    public CheckoutPage enterComment(String comment) {
        safeSendKeys(COMMENT_TEXTAREA, comment);
        return this;
    }

    @Step("Click 'Place Order'")
    public PaymentPage clickPlaceOrder() {
        safeClick(PLACE_ORDER_BUTTON);
        return new PaymentPage();
    }
}

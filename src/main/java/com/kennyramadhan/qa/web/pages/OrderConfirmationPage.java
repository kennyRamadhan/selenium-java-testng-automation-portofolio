package com.kennyramadhan.qa.web.pages;

import org.openqa.selenium.By;

/** {@code /payment_done/{id}} — order confirmation page. */
public class OrderConfirmationPage extends BaseWebPage {

    private static final By ORDER_PLACED_HEADER =
            By.xpath("//*[contains(., 'Order Placed') or @data-qa='order-placed']");
    private static final By CONGRATS_PARAGRAPH =
            By.xpath("//p[contains(., 'Congratulations') or contains(., 'order has been confirmed')]");

    public boolean isOrderConfirmedMessageDisplayed() {
        return isDisplayed(ORDER_PLACED_HEADER) || isDisplayed(CONGRATS_PARAGRAPH);
    }
}

package com.kennyramadhan.qa.web.pages;

import com.kennyramadhan.qa.web.models.PaymentDetails;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** {@code /payment} — credit-card form. */
public class PaymentPage extends BaseWebPage {

    private static final By NAME_ON_CARD = By.cssSelector("input[name='name_on_card']");
    private static final By CARD_NUMBER = By.cssSelector("input[name='card_number']");
    private static final By CVC = By.cssSelector("input[name='cvc']");
    private static final By EXPIRY_MONTH = By.cssSelector("input[name='expiry_month']");
    private static final By EXPIRY_YEAR = By.cssSelector("input[name='expiry_year']");
    private static final By PAY_BUTTON = By.cssSelector("button#submit");

    @Step("Fill payment details for card holder={details.nameOnCard}")
    public PaymentPage fillPaymentDetails(PaymentDetails details) {
        safeSendKeys(NAME_ON_CARD, details.nameOnCard());
        safeSendKeys(CARD_NUMBER, details.cardNumber());
        safeSendKeys(CVC, details.cvc());
        safeSendKeys(EXPIRY_MONTH, details.expirationMonth());
        safeSendKeys(EXPIRY_YEAR, details.expirationYear());
        return this;
    }

    @Step("Click 'Pay and Confirm Order'")
    public OrderConfirmationPage clickPayAndConfirmOrder() {
        safeClick(PAY_BUTTON);
        return new OrderConfirmationPage();
    }
}

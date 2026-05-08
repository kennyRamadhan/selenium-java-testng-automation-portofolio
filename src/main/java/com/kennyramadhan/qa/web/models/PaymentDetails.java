package com.kennyramadhan.qa.web.models;

/**
 * DTO for the payment form on AE.com {@code /payment}.
 */
public record PaymentDetails(
        String nameOnCard,
        String cardNumber,
        String cvc,
        String expirationMonth,
        String expirationYear
) {}

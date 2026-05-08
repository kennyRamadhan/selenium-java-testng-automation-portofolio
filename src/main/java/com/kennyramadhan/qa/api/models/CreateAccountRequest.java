package com.kennyramadhan.qa.api.models;

/**
 * Form-encoded request body for {@code POST /api/createAccount} and
 * {@code PUT /api/updateAccount}.
 *
 * <p>Component names follow Java convention (camelCase). Wire format uses
 * snake_case where applicable (e.g. {@code birth_date}, {@code mobile_number})
 * and {@code firstname}/{@code lastname} without separators; the translation
 * happens in {@code AuthApi.withAccountForm}.</p>
 */
public record CreateAccountRequest(
        String name,
        String email,
        String password,
        String title,
        String birthDate,
        String birthMonth,
        String birthYear,
        String firstName,
        String lastName,
        String company,
        String address1,
        String address2,
        String country,
        String zipcode,
        String state,
        String city,
        String mobileNumber
) {}

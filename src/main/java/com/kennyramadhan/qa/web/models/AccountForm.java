package com.kennyramadhan.qa.web.models;

/**
 * DTO for the account-information signup form on AE.com.
 *
 * <p>Component names follow Java convention (camelCase). The wire-side
 * form-field IDs (e.g. {@code id="title"}, {@code name="first_name"}) are
 * mapped inside {@code AccountInformationPage.fillAccountForm}.</p>
 */
public record AccountForm(
        String title,
        String password,
        String day,
        String month,
        String year,
        String firstName,
        String lastName,
        String company,
        String address,
        String address2,
        String country,
        String state,
        String city,
        String zipcode,
        String mobileNumber
) {}

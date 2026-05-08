package com.kennyramadhan.qa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User profile returned by {@code GET /api/getUserDetailByEmail}. The API
 * mixes camelCase ({@code id}, {@code name}) and snake_case
 * ({@code first_name}, {@code birth_day}); {@link JsonProperty} maps each
 * field to a Java-idiomatic camelCase name.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDetails(
        @JsonProperty("id") int id,
        @JsonProperty("name") String name,
        @JsonProperty("email") String email,
        @JsonProperty("title") String title,
        @JsonProperty("birth_day") String birthDay,
        @JsonProperty("birth_month") String birthMonth,
        @JsonProperty("birth_year") String birthYear,
        @JsonProperty("first_name") String firstName,
        @JsonProperty("last_name") String lastName,
        @JsonProperty("company") String company,
        @JsonProperty("address1") String address1,
        @JsonProperty("address2") String address2,
        @JsonProperty("country") String country,
        @JsonProperty("state") String state,
        @JsonProperty("city") String city,
        @JsonProperty("zipcode") String zipcode
) {}

package com.kennyramadhan.qa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response envelope for {@code GET /api/productsList}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductsListResponse(
        @JsonProperty("responseCode") int responseCode,
        @JsonProperty("products") List<Product> products
) {}

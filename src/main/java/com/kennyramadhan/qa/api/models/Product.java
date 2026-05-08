package com.kennyramadhan.qa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Product entry from {@code GET /api/productsList}.
 *
 * <p>Sample JSON: {@code
 * {"id":1,"name":"Blue Top","price":"Rs. 500","brand":"Polo",
 *  "category":{"usertype":{"usertype":"Women"},"category":"Tops"}}}.</p>
 *
 * <p>{@code price} is a String because the API returns it formatted with the
 * "Rs." currency prefix; consumers parse to a number when needed.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Product(
        int id,
        String name,
        String price,
        String brand,
        Category category
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Category(
            Usertype usertype,
            String category
    ) {

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Usertype(String usertype) {}
    }
}

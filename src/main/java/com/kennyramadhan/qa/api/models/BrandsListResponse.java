package com.kennyramadhan.qa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Response envelope for {@code GET /api/brandsList}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BrandsListResponse(
        @JsonProperty("responseCode") int responseCode,
        @JsonProperty("brands") List<Brand> brands
) {}

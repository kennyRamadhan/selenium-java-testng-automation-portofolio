package com.kennyramadhan.qa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic envelope for automationexercise.com API responses.
 *
 * <p>The API always returns HTTP 200 and encodes the actual outcome in the
 * {@code responseCode} body field (200 = success, 201 = created, 400/404/405
 * = various errors). The {@code message} field is populated for non-success
 * outcomes; success responses typically omit it (mapped to {@code null}).</p>
 *
 * <p>Endpoints that return collections or detail records use richer typed
 * responses (e.g. {@code ProductsListResponse}) — this envelope is for
 * status-only endpoints like createAccount, deleteAccount, verifyLogin.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ApiResponse(
        @JsonProperty("responseCode") int responseCode,
        @JsonProperty("message") String message
) {}

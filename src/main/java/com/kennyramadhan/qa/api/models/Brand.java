package com.kennyramadhan.qa.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Brand entry from {@code GET /api/brandsList}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record Brand(int id, String brand) {}

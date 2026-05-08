package com.kennyramadhan.qa.api.endpoints;

import com.kennyramadhan.qa.api.client.BaseApiClient;
import com.kennyramadhan.qa.api.models.BrandsListResponse;
import io.qameta.allure.Step;

/**
 * Client for brand-related automationexercise.com endpoints (APIs 3, 4).
 *
 * <p>The 405-on-PUT negative case is exercised via {@code rawRequest()} in
 * the test layer; this client only exposes the happy-path GET.</p>
 */
public class BrandsApi extends BaseApiClient {

    /**
     * GET /api/brandsList — fetch all brands.
     */
    @Step("GET /api/brandsList")
    public BrandsListResponse getAllBrands() {
        return given()
                .when().get("/api/brandsList")
                .then().statusCode(200)
                .extract().as(BrandsListResponse.class);
    }
}

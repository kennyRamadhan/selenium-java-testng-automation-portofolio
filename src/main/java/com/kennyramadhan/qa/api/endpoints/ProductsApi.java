package com.kennyramadhan.qa.api.endpoints;

import com.kennyramadhan.qa.api.client.BaseApiClient;
import com.kennyramadhan.qa.api.models.ProductsListResponse;
import io.qameta.allure.Step;
import io.restassured.response.Response;

/**
 * Client for product-related automationexercise.com endpoints (APIs 1, 2, 5, 6).
 *
 * <p>Negative-path endpoints (POST to a GET-only resource, missing form param)
 * are exercised in the test layer via {@code rawRequest()} from BaseApiTest;
 * this client only exposes the happy-path methods.</p>
 */
public class ProductsApi extends BaseApiClient {

    /**
     * GET /api/productsList — fetch the full product catalog.
     *
     * @return deserialized {@link ProductsListResponse} (transport-level HTTP
     *         is always 200; check {@code responseCode()} on the body)
     */
    @Step("GET /api/productsList")
    public ProductsListResponse getAllProducts() {
        return given()
                .when().get("/api/productsList")
                .then().statusCode(200)
                .extract().as(ProductsListResponse.class);
    }

    /**
     * POST /api/searchProduct — search products by keyword via form body.
     *
     * <p>Returns the raw {@link Response} so tests can branch on the body's
     * {@code responseCode} field (200 happy path, 400 when {@code query} is
     * missing or empty).</p>
     */
    @Step("POST /api/searchProduct (search_product={query})")
    public Response searchProduct(String query) {
        return given()
                .formParam("search_product", query)
                .when().post("/api/searchProduct");
    }
}

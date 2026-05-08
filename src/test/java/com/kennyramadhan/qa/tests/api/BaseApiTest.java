package com.kennyramadhan.qa.tests.api;

import com.kennyramadhan.qa.api.client.ApiConfig;
import com.kennyramadhan.qa.api.endpoints.AuthApi;
import com.kennyramadhan.qa.api.endpoints.BrandsApi;
import com.kennyramadhan.qa.api.endpoints.ProductsApi;
import com.kennyramadhan.qa.api.endpoints.UserApi;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;

/**
 * Shared lifecycle for API test classes. Each test method gets a fresh set of
 * client instances via {@link #setupClients()}; clients are stateless wrappers
 * around RestAssured so re-instantiation is essentially free.
 *
 * <h2>{@link #rawRequest()} helper</h2>
 * <p>Negative-path tests that intentionally bypass typed client methods
 * (e.g. POSTing to a GET-only endpoint to assert the 405 in the body's
 * {@code responseCode}) MUST use {@link #rawRequest()} rather than
 * {@code RestAssured.given()} directly. The helper preserves Allure tracing
 * (the {@code AllureRestAssured} filter) and pins the base URI so negative
 * tests share the same Allure step shape and target as positive tests.</p>
 *
 * <p>Allure listener is auto-registered via TestNG SPI from
 * {@code allure-testng.jar}; no {@code @Listeners} annotation needed.</p>
 */
public abstract class BaseApiTest {

    protected ProductsApi productsApi;
    protected AuthApi authApi;
    protected BrandsApi brandsApi;
    protected UserApi userApi;

    @BeforeMethod(alwaysRun = true)
    public void setupClients() {
        productsApi = new ProductsApi();
        authApi = new AuthApi();
        brandsApi = new BrandsApi();
        userApi = new UserApi();
    }

    /**
     * RestAssured spec for raw negative-path requests. Includes the same base
     * URI and Allure trace filter as {@code BaseApiClient.given()} but no
     * default content-type — callers that need form-encoded bodies set their
     * own {@code .formParam(...)} or content-type on the returned spec.
     */
    protected static RequestSpecification rawRequest() {
        return RestAssured.given()
                .baseUri(ApiConfig.BASE_URL)
                .filter(new AllureRestAssured());
    }
}

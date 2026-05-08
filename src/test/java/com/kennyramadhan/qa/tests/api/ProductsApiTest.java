package com.kennyramadhan.qa.tests.api;

import com.kennyramadhan.qa.api.models.Product;
import com.kennyramadhan.qa.api.models.ProductsListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers products endpoints: API 1 (GET /productsList), API 2 (POST /productsList → 405),
 * API 5 (POST /searchProduct), API 6 (POST /searchProduct without param → 400).
 */
@Epic("API")
@Feature("Products")
public class ProductsApiTest extends BaseApiTest {

    @Test(groups = {"api", "smoke"})
    @Severity(SeverityLevel.CRITICAL)
    public void shouldReturnAllProductsList() {
        ProductsListResponse response = productsApi.getAllProducts();

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.products()).isNotEmpty();
        assertThat(response.products()).allSatisfy(p -> {
            assertThat(p.id()).isPositive();
            assertThat(p.name()).isNotBlank();
            assertThat(p.price()).isNotBlank();
        });
    }

    @Test(groups = {"api"})
    public void shouldReturn405OnPostToProductsList() {
        Response response = rawRequest().when().post("/api/productsList");

        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(405);
    }

    @Test(groups = {"api", "smoke"})
    public void shouldSearchProductByValidKeyword() {
        Response response = productsApi.searchProduct("top");

        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(200);
        assertThat(response.jsonPath().getList("products")).isNotEmpty();
    }

    @Test(groups = {"api"})
    public void shouldReturn400OnSearchWithoutParam() {
        Response response = rawRequest().when().post("/api/searchProduct");

        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(400);
    }

    /** Sanity check: the product nesting (Category → Usertype) deserializes correctly. */
    @Test(groups = {"api"})
    public void shouldDeserializeProductCategoryNesting() {
        ProductsListResponse response = productsApi.getAllProducts();

        Product first = response.products().get(0);
        assertThat(first.category()).isNotNull();
        assertThat(first.category().usertype()).isNotNull();
        assertThat(first.category().usertype().usertype()).isNotBlank();
        assertThat(first.category().category()).isNotBlank();
    }
}

package com.kennyramadhan.qa.tests.api;

import com.kennyramadhan.qa.api.models.BrandsListResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers brand endpoints: API 3 (GET /brandsList), API 4 (PUT /brandsList → 405).
 */
@Epic("API")
@Feature("Brands")
public class BrandsApiTest extends BaseApiTest {

    @Test(groups = {"api", "smoke"})
    public void shouldReturnAllBrandsList() {
        BrandsListResponse response = brandsApi.getAllBrands();

        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.brands()).isNotEmpty();
        assertThat(response.brands()).allSatisfy(b -> {
            assertThat(b.id()).isPositive();
            assertThat(b.brand()).isNotBlank();
        });
    }

    @Test(groups = {"api"})
    public void shouldReturn405OnPutToBrandsList() {
        Response response = rawRequest().when().put("/api/brandsList");

        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(405);
    }
}

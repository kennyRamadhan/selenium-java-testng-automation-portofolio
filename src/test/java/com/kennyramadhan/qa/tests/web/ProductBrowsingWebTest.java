package com.kennyramadhan.qa.tests.web;

import com.kennyramadhan.qa.web.pages.ProductDetailsPage;
import com.kennyramadhan.qa.web.pages.ProductsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Web")
@Feature("Product Browsing")
public class ProductBrowsingWebTest extends BaseWebTest {

    @Test(groups = {"web", "smoke"})
    public void shouldDisplayAllProducts() {
        ProductsPage page = homePage().clickProducts();

        assertThat(page.isAllProductsHeaderDisplayed()).isTrue();
        assertThat(page.getDisplayedProductCount()).isPositive();
    }

    @Test(groups = {"web", "smoke"})
    public void shouldSearchProductByKeyword() {
        ProductsPage page = homePage().clickProducts().searchProduct("tshirt");

        assertThat(page.getDisplayedProductCount()).isPositive();
    }

    @Test(groups = {"web"})
    public void shouldNavigateToProductDetails() {
        ProductDetailsPage details = homePage().clickProducts().viewProductByIndex(0);

        assertThat(details.getProductName()).isNotBlank();
        assertThat(details.getProductPrice()).isNotBlank();
    }
}

package com.kennyramadhan.qa.tests.web;

import com.kennyramadhan.qa.web.pages.CartPage;
import com.kennyramadhan.qa.web.pages.ProductsPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Web")
@Feature("Cart and Checkout")
public class CheckoutWebTest extends BaseWebTest {

    @Test(groups = {"web", "smoke"})
    public void shouldAddProductToCartFromListing() {
        ProductsPage products = homePage().clickProducts();
        products.addProductToCartByIndex(0);

        CartPage cart = products.clickViewCart();
        assertThat(cart.isCartHeaderDisplayed()).isTrue();
        assertThat(cart.getCartItemCount()).isGreaterThanOrEqualTo(1);
    }

    @Test(groups = {"web"})
    public void shouldDisplayCartItemDetailsAfterAdd() {
        ProductsPage products = homePage().clickProducts();
        String firstProductDisplayed = products.viewProductByIndex(0).getProductName();
        // Re-navigate to products list to add via the listing path
        products = homePage().clickProducts();
        products.addProductToCartByIndex(0);

        CartPage cart = products.clickViewCart();
        assertThat(cart.getCartItemCount()).isGreaterThanOrEqualTo(1);
        assertThat(cart.getCartItemNameByIndex(0)).isEqualToIgnoringCase(firstProductDisplayed);
    }
}

package com.kennyramadhan.qa.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** {@code /products} — product listing + search. */
public class ProductsPage extends BaseWebPage {

    private static final By ALL_PRODUCTS_HEADER =
            By.xpath("//h2[normalize-space()='All Products']");
    private static final By SEARCH_INPUT = By.id("search_product");
    private static final By SEARCH_SUBMIT = By.id("submit_search");
    private static final By PRODUCT_CARDS = By.cssSelector(".features_items .col-sm-4");
    private static final By VIEW_PRODUCT_LINKS = By.cssSelector(".choose a[href^='/product_details/']");
    private static final By VIEW_CART_LINK = By.cssSelector(".shop-menu a[href='/view_cart']");

    public boolean isAllProductsHeaderDisplayed() {
        return isDisplayed(ALL_PRODUCTS_HEADER);
    }

    @Step("Search products: {query}")
    public ProductsPage searchProduct(String query) {
        safeSendKeys(SEARCH_INPUT, query);
        safeClick(SEARCH_SUBMIT);
        return this;
    }

    public int getDisplayedProductCount() {
        return findAll(PRODUCT_CARDS).size();
    }

    @Step("View product details at index {index}")
    public ProductDetailsPage viewProductByIndex(int index) {
        List<WebElement> links = findAll(VIEW_PRODUCT_LINKS);
        if (index < 0 || index >= links.size()) {
            throw new IndexOutOfBoundsException(
                    "Product index " + index + " out of range; only " + links.size() + " products visible.");
        }
        links.get(index).click();
        return new ProductDetailsPage();
    }

    /**
     * Hover product card at index to surface the overlay 'Add to cart'
     * button, then click it. AE.com requires hover for overlay visibility
     * in a real browser; in headless mode the overlay is rendered but the
     * click target is still the {@code .add-to-cart} link inside the card.
     */
    @Step("Add product at index {index} to cart")
    public ProductsPage addProductToCartByIndex(int index) {
        List<WebElement> cards = findAll(PRODUCT_CARDS);
        if (index < 0 || index >= cards.size()) {
            throw new IndexOutOfBoundsException(
                    "Product index " + index + " out of range; only " + cards.size() + " products visible.");
        }
        WebElement addButton = cards.get(index).findElement(
                By.cssSelector(".product-overlay .add-to-cart, .productinfo .add-to-cart"));
        addButton.click();
        return this;
    }

    @Step("Click 'View Cart' from products page")
    public CartPage clickViewCart() {
        safeClick(VIEW_CART_LINK);
        return new CartPage();
    }
}

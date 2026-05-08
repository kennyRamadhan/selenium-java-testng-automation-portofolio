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

    private static final By CART_MODAL = By.id("cartModal");
    private static final By CART_MODAL_CONTINUE = By.cssSelector("#cartModal .close-modal");

    /**
     * Click the 'Add to cart' overlay button on the product card at the given
     * index, then dismiss the resulting AE confirmation modal so subsequent
     * navigation clicks (e.g. View Cart) are not intercepted. The page is
     * left in its original state — back on /products with the modal closed.
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
        // Wait for the confirmation modal to render and dismiss it.
        waitForVisible(CART_MODAL);
        safeClick(CART_MODAL_CONTINUE);
        return this;
    }

    @Step("Click 'View Cart' from products page")
    public CartPage clickViewCart() {
        safeClick(VIEW_CART_LINK);
        return new CartPage();
    }
}

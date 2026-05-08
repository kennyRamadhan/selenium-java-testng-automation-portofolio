package com.kennyramadhan.qa.web.pages;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/** {@code /view_cart} — shopping cart page. */
public class CartPage extends BaseWebPage {

    private static final By CART_ROWS = By.cssSelector("#cart_info_table tbody tr");
    private static final By PROCEED_TO_CHECKOUT = By.cssSelector(".btn.check_out");
    private static final By CART_HEADER = By.xpath("//li[normalize-space()='Shopping Cart']");

    public boolean isCartHeaderDisplayed() {
        return isDisplayed(CART_HEADER);
    }

    public int getCartItemCount() {
        return findAll(CART_ROWS).size();
    }

    public String getCartItemNameByIndex(int index) {
        List<WebElement> rows = findAll(CART_ROWS);
        return rows.get(index).findElement(By.cssSelector(".cart_description a")).getText();
    }

    public String getCartItemPriceByIndex(int index) {
        List<WebElement> rows = findAll(CART_ROWS);
        return rows.get(index).findElement(By.cssSelector(".cart_price p")).getText();
    }

    @Step("Remove cart item at index {index}")
    public CartPage removeItemByIndex(int index) {
        List<WebElement> rows = findAll(CART_ROWS);
        rows.get(index).findElement(By.cssSelector(".cart_quantity_delete")).click();
        return this;
    }

    /**
     * Clicks "Proceed to Checkout". Resulting page depends on auth state:
     * <ul>
     *   <li>Authenticated user → /checkout</li>
     *   <li>Unauthenticated user → /login (with cart-redirect modal)</li>
     * </ul>
     * Caller is responsible for constructing the appropriate page object
     * based on resulting URL. Method intentionally returns void to avoid
     * encoding state-dependent return types.
     */
    @Step("Click 'Proceed to Checkout'")
    public void clickProceedToCheckout() {
        safeClick(PROCEED_TO_CHECKOUT);
    }
}

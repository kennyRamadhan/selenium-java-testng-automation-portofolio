package com.kennyramadhan.qa.web.pages;

import com.kennyramadhan.qa.web.client.WebConfig;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/** Landing page at {@code /}. */
public class HomePage extends BaseWebPage {

    private static final By LOGO = By.cssSelector(".logo img");
    private static final By SIGNUP_LOGIN_LINK = By.cssSelector(".shop-menu a[href='/login']");
    private static final By PRODUCTS_LINK = By.cssSelector(".shop-menu a[href='/products']");
    private static final By CART_LINK = By.cssSelector(".shop-menu a[href='/view_cart']");
    private static final By LOGGED_IN_AS = By.xpath("//a[contains(., 'Logged in as')]");

    @Step("Open AE home page")
    public HomePage open() {
        driver.get(WebConfig.BASE_URL + "/");
        waitForVisible(LOGO);
        return this;
    }

    @Step("Click 'Signup / Login'")
    public SignupLoginPage clickSignupLogin() {
        safeClick(SIGNUP_LOGIN_LINK);
        return new SignupLoginPage();
    }

    @Step("Click 'Products'")
    public ProductsPage clickProducts() {
        safeClick(PRODUCTS_LINK);
        return new ProductsPage();
    }

    @Step("Click 'Cart'")
    public CartPage clickCart() {
        safeClick(CART_LINK);
        return new CartPage();
    }

    public boolean isLogoDisplayed() {
        return isDisplayed(LOGO);
    }

    public boolean isLoggedIn() {
        return isDisplayed(LOGGED_IN_AS);
    }
}

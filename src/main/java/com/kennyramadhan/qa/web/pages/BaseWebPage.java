package com.kennyramadhan.qa.web.pages;

import com.kennyramadhan.qa.core.driver.WebDriverFactory;
import com.kennyramadhan.qa.web.client.WebConfig;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Abstract base for all web page objects. Holds the per-thread
 * {@link WebDriver} and a default {@link WebDriverWait} sized to
 * {@link WebConfig#TIMEOUT_SECONDS}.
 *
 * <h2>Conventions</h2>
 * <ul>
 *   <li>No PageFactory. Subclasses declare {@code By} locator constants and
 *       call the helpers below.</li>
 *   <li>No assertions in page objects. Helpers return data or perform
 *       actions; the test layer asserts via AssertJ.</li>
 *   <li>All helpers wait for visibility before interacting (explicit waits
 *       only — implicit waits are disabled in
 *       {@link WebDriverFactory#create}).</li>
 * </ul>
 */
public abstract class BaseWebPage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    /** Construct using the WebDriver bound to the current thread. */
    protected BaseWebPage() {
        this(WebDriverFactory.getDriver());
    }

    /** Construct with an explicit driver (useful for testing in isolation). */
    protected BaseWebPage(WebDriver driver) {
        if (driver == null) {
            throw new IllegalStateException(
                    "No WebDriver bound to current thread. Call WebDriverFactory.createAndStore() first.");
        }
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(WebConfig.TIMEOUT_SECONDS));
    }

    @Step("Wait for element visible: {by}")
    protected WebElement waitForVisible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    @Step("Click {by}")
    protected void safeClick(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by)).click();
    }

    @Step("Type into {by}: {text}")
    protected void safeSendKeys(By by, String text) {
        WebElement el = waitForVisible(by);
        el.clear();
        el.sendKeys(text);
    }

    @Step("Read text of {by}")
    protected String getText(By by) {
        return waitForVisible(by).getText();
    }

    /** Non-blocking visibility check — returns false if locator absent or invisible. */
    protected boolean isDisplayed(By by) {
        try {
            return driver.findElement(by).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    protected List<WebElement> findAll(By by) {
        return driver.findElements(by);
    }
}

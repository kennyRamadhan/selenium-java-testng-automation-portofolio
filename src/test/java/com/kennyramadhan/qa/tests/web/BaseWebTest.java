package com.kennyramadhan.qa.tests.web;

import com.kennyramadhan.qa.core.driver.WebDriverFactory;
import com.kennyramadhan.qa.web.client.WebConfig;
import com.kennyramadhan.qa.web.pages.CartPage;
import com.kennyramadhan.qa.web.pages.HomePage;
import com.kennyramadhan.qa.web.pages.ProductsPage;
import com.kennyramadhan.qa.web.pages.SignupLoginPage;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;

/**
 * Shared lifecycle for web test classes.
 *
 * <h2>Driver lifecycle</h2>
 * <ul>
 *   <li>{@link #setUpWebDriver()}: resolves browser + headless from system
 *       properties (-Dbrowser, -Dheadless, env CI), creates a driver via
 *       {@link WebDriverFactory#createAndStore()}, and navigates to
 *       {@link WebConfig#BASE_URL}.</li>
 *   <li>{@link #tearDownWebDriver(ITestResult)}: on failure, captures a PNG
 *       screenshot via {@link TakesScreenshot} and attaches it to the Allure
 *       report. Always quits the driver and clears the ThreadLocal slot.</li>
 * </ul>
 *
 * <h2>Allure listener</h2>
 * <p>{@code AllureTestNg} is auto-registered via TestNG SPI (proven in
 * Phase 5). No {@code @Listeners} annotation needed.</p>
 */
public abstract class BaseWebTest {

    private static final Logger log = LoggerFactory.getLogger(BaseWebTest.class);

    @BeforeMethod(alwaysRun = true)
    public void setUpWebDriver() {
        WebDriver driver = WebDriverFactory.createAndStore();
        driver.get(WebConfig.BASE_URL);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownWebDriver(ITestResult result) {
        WebDriver driver = WebDriverFactory.getDriver();
        if (driver != null && result.getStatus() == ITestResult.FAILURE) {
            try {
                byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String name = result.getMethod().getMethodName() + "-failure";
                Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), ".png");
            } catch (Exception e) {
                log.warn("Failed to attach failure screenshot for {}: {}",
                        result.getMethod().getMethodName(), e.getMessage());
            }
        }
        WebDriverFactory.quitDriver();
    }

    // Lazy page-object factories. Each call constructs a fresh page bound to
    // the current thread's driver — cheap (no PageFactory init) and avoids
    // stale-page-state issues across test methods.

    protected HomePage homePage() { return new HomePage(); }
    protected SignupLoginPage signupLoginPage() { return new SignupLoginPage(); }
    protected ProductsPage productsPage() { return new ProductsPage(); }
    protected CartPage cartPage() { return new CartPage(); }
}

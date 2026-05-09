package com.kennyramadhan.qa.web.pages;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.kennyramadhan.qa.core.driver.WebDriverFactory;
import com.kennyramadhan.qa.web.client.WebConfig;

import io.qameta.allure.Allure;
import io.qameta.allure.Step;

/**
 * Abstract base for all web page objects. Holds the per-thread
 * {@link WebDriver} and a default {@link WebDriverWait} sized to
 * {@link WebConfig#TIMEOUT_SECONDS}.
 *
 * <h2>Conventions</h2>
 * <ul>
 * <li>No PageFactory. Subclasses declare {@code By} locator constants and call
 * the helpers below.</li>
 * <li>No assertions in page objects. Helpers return data or perform actions;
 * the test layer asserts via AssertJ.</li>
 * <li>All helpers wait for visibility before interacting (explicit waits only —
 * implicit waits are disabled in {@link WebDriverFactory#create}).</li>
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
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(by));
		el.click();
		captureScreenshot("After click: " + by);
	}

	@Step("Type into {by}: {text}")
	protected void safeSendKeys(By by, String text) {
		WebElement el = waitForVisible(by);
		el.clear();
		el.sendKeys(text);
		captureScreenshot("After input: " + by);
	}

	@Step("Read text of {by}")
	protected String getText(By by) {
		return waitForVisible(by).getText();
	}

	/**
	 * Non-blocking visibility check — returns false if locator absent or invisible.
	 */
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

	/**
	 * Captures a PNG screenshot of the current page and attaches it to the active
	 * Allure step. Invoked automatically after state-mutating actions (safeClick,
	 * safeSendKeys). Read-only helpers (getText, isDisplayed, waitForVisible)
	 * intentionally do not capture, to keep assertion-heavy tests from producing
	 * visual noise in the Allure report.
	 *
	 * Capture failures are swallowed: a screenshot exception must never cause the
	 * underlying test action to fail.
	 */
	private void captureScreenshot(String name) {
		try {
			if (driver instanceof TakesScreenshot screenshotter) {
				byte[] png = screenshotter.getScreenshotAs(OutputType.BYTES);
				Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), ".png");
			}
		} catch (Exception e) {
			// Intentionally silent — screenshot failure must not break the test.
		}
	}
}

package com.kennyramadhan.qa.web.pages;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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

	protected WebElement waitForVisible(By by) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	protected void safeClick(By by) {
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(by));
		el.click();
		captureScreenshot();
	}

	protected void safeSendKeys(By by, String text) {
		WebElement el = waitForVisible(by);
		el.clear();
		el.sendKeys(text);
		captureScreenshot();
	}

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
	 * Allure step. The attachment label is derived from the parent step's name
	 * (typically the calling page object method's @Step annotation), which keeps
	 * the report narrative business-focused instead of leaking implementation
	 * details like CSS selectors.
	 *
	 * <p>
	 * If no active Allure step is found (e.g. during teardown or when called
	 * outside a @Step-annotated method), the label falls back to "State after
	 * action".
	 *
	 * <p>
	 * Capture failures are swallowed: a screenshot exception must never cause the
	 * underlying test action to fail.
	 */
	private void captureScreenshot() {
		try {
			if (driver instanceof TakesScreenshot screenshotter) {
				byte[] png = screenshotter.getScreenshotAs(OutputType.BYTES);
				String label = currentStepNameOrDefault();
				Allure.addAttachment(label, "image/png", new ByteArrayInputStream(png), ".png");
			}
		} catch (Exception e) {
			// Intentionally silent — screenshot failure must not break the test.
		}
	}

	/**
	 * Reads the name of the current Allure step (typically the calling page object
	 * method's @Step annotation). Returns "State after action" if no step is
	 * active.
	 */
	private String currentStepNameOrDefault() {
		AtomicReference<String> name = new AtomicReference<>("State after action");
		try {
			Allure.getLifecycle().updateStep(step -> {
				String stepName = step.getName();
				if (stepName != null && !stepName.isBlank()) {
					name.set(stepName);
				}
			});
		} catch (Exception ignored) {
			// Allure lifecycle inaccessible or no active step — fallback applies.
		}
		return name.get();
	}
}

package com.kennyramadhan.qa.mobile.pages;

import java.io.ByteArrayInputStream;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.kennyramadhan.qa.core.driver.DriverManager;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;

/**
 * Abstract base for all mobile page objects. Mirrors {@code BaseWebPage} on the
 * web side: explicit {@code By} locator constants, explicit waits, and
 * post-action screenshot capture for state-mutating helpers.
 *
 * <h2>Conventions</h2>
 * <ul>
 * <li>Locators are subclass {@code private static final By} constants.</li>
 * <li>Read-only helpers ({@link #waitFor(By)}) do not capture screenshots —
 * only state-mutating actions ({@link #safeClick(By)},
 * {@link #safeSendKeys(By, String)}) attach images.</li>
 * <li>Subclasses may call {@link #captureScreenshot()} directly after
 * performing an action on an iterated {@code WebElement} (e.g.
 * {@code list.get(0).click()}) where the safe* helpers don't fit.</li>
 * <li>Screenshot label is derived from the active Allure step via
 * {@code Allure.getLifecycle().updateStep(...)}; falls back to "State after
 * action" when no step is active.</li>
 * </ul>
 */
public abstract class BaseMobilePage {

	protected final AppiumDriver driver;
	protected final WebDriverWait wait;

	protected BaseMobilePage() {
		this(DriverManager.getDriver());
	}

	protected BaseMobilePage(AppiumDriver driver) {
		if (driver == null) {
			throw new IllegalStateException(
					"No Appium driver bound; ensure BaseMobileTest.setUp() initialized it before instantiating page objects");
		}
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
	}

	/** Returns true when the active driver session targets iOS. */
	protected boolean isIOS() {
		return "iOS".equalsIgnoreCase(String.valueOf(driver.getCapabilities().getCapability("platformName")));
	}

	/** Waits up to 15s for the element to be visible, then returns it. */
	protected WebElement waitFor(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Returns true when the locator's element is visible within the wait window.
	 * Swallows lookup failures and returns false instead of throwing — intended for
	 * boolean state getters in subclasses.
	 */
	protected boolean isDisplayedQuiet(By locator) {
		try {
			return waitFor(locator).isDisplayed();
		} catch (NoSuchElementException | TimeoutException e) {
			return false;
		}
	}

	protected void safeClick(By locator) {
		WebElement el = wait.until(ExpectedConditions.elementToBeClickable(locator));
		el.click();
		captureScreenshot();
	}

	protected void safeSendKeys(By locator, String text) {
		WebElement el = waitFor(locator);
		el.sendKeys(text);
		captureScreenshot();
	}

	/**
	 * Captures a PNG screenshot and attaches it to the current Allure step.
	 * Intended for state-mutating actions invoked outside the safe* helpers (e.g.
	 * {@code list.get(i).click()} loops, {@code WaitHelpers.tapByCoordinates}
	 * calls). The label is derived from the active step name; falls back to "State
	 * after action" if no step is active. Capture failures are swallowed so that
	 * observability never breaks the underlying test action.
	 */
	protected void captureScreenshot() {
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
			// Allure lifecycle inaccessible — fallback applies.
		}
		return name.get();
	}
}

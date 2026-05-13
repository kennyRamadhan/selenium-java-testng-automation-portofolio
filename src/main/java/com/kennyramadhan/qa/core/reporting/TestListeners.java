package com.kennyramadhan.qa.core.reporting;

import java.io.ByteArrayInputStream;

import org.openqa.selenium.OutputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.kennyramadhan.qa.core.driver.DriverManager;

import io.appium.java_client.AppiumDriver;
import io.qameta.allure.Allure;

/**
 * <h1>TestListeners</h1> TestNG listener that resets per-test step counters and
 * attaches a screenshot to the Allure report on test success or failure.
 *
 * <p>
 * Allure status (PASSED/FAILED/SKIPPED) is driven by the AllureTestNg listener
 * registered alongside this one in testng.xml; this listener is scoped to
 * artifact attachment and step-counter lifecycle.
 * </p>
 *
 * @author Kenny Ramadhan
 */
public class TestListeners implements ITestListener {

	private static final Logger log = LoggerFactory.getLogger(TestListeners.class);

	@Override
	public void onTestStart(ITestResult result) {
		log.debug("Starting test: {}", result.getMethod().getMethodName());
		LogHelper.resetCounter();
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		LogHelper.flushOpenStep();
		attachScreenshot(result, "success");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		LogHelper.flushOpenStep();
		attachScreenshot(result, "failure");
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		LogHelper.flushOpenStep();
	}

	private void attachScreenshot(ITestResult result, String label) {
		AppiumDriver driver = DriverManager.getDriver();
		if (driver == null) {
			log.info("Driver is null, skipping {} screenshot for: {}", label, result.getMethod().getMethodName());
			return;
		}
		try {
			byte[] png = driver.getScreenshotAs(OutputType.BYTES);
			String name = result.getMethod().getMethodName() + "-" + label;
			Allure.addAttachment(name, "image/png", new ByteArrayInputStream(png), ".png");
		} catch (Exception e) {
			log.warn("Failed to attach {} screenshot for {}: {}", label, result.getMethod().getMethodName(),
					e.getMessage());
		}
	}
}

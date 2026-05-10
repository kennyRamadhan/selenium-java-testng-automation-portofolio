package com.kennyramadhan.qa.core.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * One-retry analyzer for mobile tests. Concretizes ADR-6.
 *
 * <p>
 * TestNG instantiates a fresh analyzer per test method, so the {@code count}
 * field tracks attempts within a single method's lifecycle.
 * {@link #MAX_RETRIES} = 1 means up to 2 total attempts per test (initial run +
 * 1 retry on failure).
 *
 * <p>
 * Mobile tests under {@code noReset=true} + Appium 9.x exhibit transient state
 * cascade failures. One retry buffers against transient flake without masking
 * permanent regressions. Tests that consistently fail still report fail after
 * retries are exhausted.
 *
 * <p>
 * Wired into mobile test classes via {@link MobileRetryListener} (which applies
 * this analyzer to every {@code @Test} method automatically).
 */
public class MobileRetryAnalyzer implements IRetryAnalyzer {

	private static final Logger log = LoggerFactory.getLogger(MobileRetryAnalyzer.class);
	private static final int MAX_RETRIES = 1;

	private int count = 0;

	@Override
	public boolean retry(ITestResult result) {
		if (count < MAX_RETRIES) {
			count++;
			log.warn("[Retry {}/{}] {}.{} — first attempt failed: {}", count, MAX_RETRIES,
					result.getTestClass().getRealClass().getSimpleName(), result.getName(),
					result.getThrowable() == null ? "(no throwable)" : result.getThrowable().getMessage());
			return true;
		}
		return false;
	}
}

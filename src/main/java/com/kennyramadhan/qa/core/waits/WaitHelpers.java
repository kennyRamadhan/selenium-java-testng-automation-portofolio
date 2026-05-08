package com.kennyramadhan.qa.core.waits;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.core.reporting.LogHelper;
import io.appium.java_client.AppiumBy;

public class WaitHelpers {

	private static final Logger log = LoggerFactory.getLogger(WaitHelpers.class);

	public void scrollIntoText(String text) {
		String platformName = DriverManager.getDriver()
		        .getCapabilities()
		        .getCapability("platformName")
		        .toString()
		        .toLowerCase();


	    if (platformName.contains("android")) {
	        // Android-specific scroll
	        DriverManager.getDriver().findElement(
	                AppiumBy.androidUIAutomator(
	                        "new UiScrollable(new UiSelector().scrollable(true))"
	                                + ".scrollIntoView(new UiSelector().text(\"" + text + "\"));"));
	    }
	    else if (platformName.contains("ios")) {
	        // iOS-specific scroll: loop until the element is found or max scroll attempts reached
	        boolean found = false;
	        int maxScroll = 5; // max scroll attempts

	        while (!found && maxScroll > 0) {
	            List<WebElement> elements = DriverManager.getDriver()
	                    .findElements(AppiumBy.iOSNsPredicateString("name CONTAINS '" + text + "'"));

	            if (!elements.isEmpty()) {
	                found = true; // element found
	            } else {
	                Map<String, Object> params = new HashMap<>();
	                params.put("direction", "down");
	                DriverManager.getDriver().executeScript("mobile: scroll", params);
	                maxScroll--;
	            }
	        }

	        if (!found) {
	            throw new RuntimeException("Element with text '" + text + "' not found after scrolling.");
	        }
	    } 
	    else {
	        throw new UnsupportedOperationException("Unsupported platform: " + platformName);
	    }
	}
	
	
	  /**
     * Extract all prices from a list of WebElements, parse to Double, and log to the report.
     *
     * @param elements list of WebElements that hold prices
     * @param label log label (e.g. "Before Sorting" or "After Sorting").
     * @return list of prices in numeric form
     */
    public static List<Double> extractPrices(List<WebElement> elements, String label) {
        List<Double> prices = new ArrayList<>();
        for (WebElement el : elements) {
            double price = Double.parseDouble(el.getText().replace("$", "").trim());
            prices.add(price);
            LogHelper.step("Verify Product List");
            LogHelper.detail("Captured value " + label + " : " + price);
        }
        return prices;
    }

    /**
     * Check whether the prices are sorted ascending.
     */
    public static boolean isSortedAscending(List<Double> prices) {
        List<Double> sorted = new ArrayList<>(prices);
        Collections.sort(sorted);
        return sorted.equals(prices);
    }

    /**
     * Check whether the prices are sorted descending.
     */
    public static boolean isSortedDescending(List<Double> prices) {
        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort(Collections.reverseOrder());
        return sorted.equals(prices);
    }

    /**
     * Verify the order changed after the sort action.
     */
    public static void verifySortingChanged(List<Double> before, List<Double> after) {
        Assert.assertNotEquals(after, before, "Product order did not change after sorting!");
    }

    /**
     * Verify the list is sorted either ascending or descending.
     */
    public static void verifySortingOrder(List<Double> prices) {
        boolean asc = isSortedAscending(prices);
        boolean desc = isSortedDescending(prices);
        LogHelper.step("Verify Product Order");
        Assert.assertTrue(asc || desc, "Product order is neither ASC nor DESC!");
        LogHelper.detail("Product order valid (" + (asc ? "Ascending" : "Descending") + ")");
    }
    
    
    public void verifyElementExist(WebElement element) {
		  WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
	        try {
	            wait.until(ExpectedConditions.visibilityOf(element));
	            log.info("[OK] Element found: {}", element);
	        } catch (Exception e) {
	            throw new RuntimeException("Element not found: " + element, e);
	        }

	}
    
    /**
     * Tap element by its center coordinates (force tap).
     * This works even if element.click() fails.
     */
    public void tapByCoordinates(WebElement element, int offset) {
        int centerX = element.getRect().x + (element.getRect().width / 2);
        int centerY = element.getRect().y + (element.getRect().height / 2 + offset);
        Map<String, Object> tapParams = new HashMap<>();
        tapParams.put("x", centerX);
        tapParams.put("y", centerY);
        log.info("[TAP] Force tap at: X={} Y={}", centerX, centerY);
        DriverManager.getDriver().executeScript("mobile: tap", tapParams);
    }



}

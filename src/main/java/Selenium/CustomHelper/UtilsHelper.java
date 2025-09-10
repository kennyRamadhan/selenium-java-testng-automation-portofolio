package Selenium.CustomHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;

import Appium.Config.DriverManager;
import io.appium.java_client.AppiumBy;

public class UtilsHelper {
	
	public void scrollIntoText(String text) {
		String platformName = DriverManager.getDriver()
		        .getCapabilities()
		        .getCapability("platformName")
		        .toString()
		        .toLowerCase();


	    if (platformName.contains("android")) {
	        // ✅ Scroll khusus Android
	        DriverManager.getDriver().findElement(
	                AppiumBy.androidUIAutomator(
	                        "new UiScrollable(new UiSelector().scrollable(true))"
	                                + ".scrollIntoView(new UiSelector().text(\"" + text + "\"));"));
	    } 
	    else if (platformName.contains("ios")) {
	        // ✅ Scroll khusus iOS dengan loop sampai element ketemu atau max scroll tercapai
	        boolean found = false;
	        int maxScroll = 5; // batas scroll maksimal

	        while (!found && maxScroll > 0) {
	            List<WebElement> elements = DriverManager.getDriver()
	                    .findElements(AppiumBy.iOSNsPredicateString("name CONTAINS '" + text + "'"));

	            if (!elements.isEmpty()) {
	                found = true; // element ditemukan
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

}

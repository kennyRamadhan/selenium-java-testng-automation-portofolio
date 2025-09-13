package Selenium.CustomHelper;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import Appium.Config.DriverManager;
import Extent.Listeners.LogHelper;
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
	
	
	  /**
     * Ambil semua harga dari list WebElement, konversi ke Double, dan log ke report.
     *
     * @param elements Daftar WebElement yang berisi harga.
     * @param label Label log (misal: "Before Sorting" atau "After Sorting").
     * @return List<Double> berisi harga dalam format numerik.
     */
    public static List<Double> extractPrices(List<WebElement> elements, String label) {
        List<Double> prices = new ArrayList<>();
        for (WebElement el : elements) {
            double price = Double.parseDouble(el.getText().replace("$", "").trim());
            prices.add(price);
            LogHelper.step("Verifikasi Product List");
            LogHelper.detail(" Mendapatkan Nilai "+label + " : " + price);
        }
        return prices;
    }

    /**
     * Periksa apakah urutan harga sudah ascending.
     */
    public static boolean isSortedAscending(List<Double> prices) {
        List<Double> sorted = new ArrayList<>(prices);
        Collections.sort(sorted);
        return sorted.equals(prices);
    }

    /**
     * Periksa apakah urutan harga sudah descending.
     */
    public static boolean isSortedDescending(List<Double> prices) {
        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort(Collections.reverseOrder());
        return sorted.equals(prices);
    }

    /**
     * Verifikasi apakah urutan berubah (setelah klik sort).
     */
    public static void verifySortingChanged(List<Double> before, List<Double> after) {
        Assert.assertNotEquals(after, before, "Urutan produk tidak berubah setelah sorting!");
    }

    /**
     * Verifikasi apakah list sudah dalam urutan ascending atau descending.
     */
    public static void verifySortingOrder(List<Double> prices) {
        boolean asc = isSortedAscending(prices);
        boolean desc = isSortedDescending(prices);
        LogHelper.step("Verifikasi Urutan Product");
        Assert.assertTrue(asc || desc, "Urutan produk tidak ASC dan tidak DESC!");
        LogHelper.detail("Urutan produk valid (" + (asc ? "Ascending" : "Descending") + ")");
    }
    
    
    public void verifyElementExist(WebElement element) {
		  WebDriverWait wait = new WebDriverWait(DriverManager.getDriver(), Duration.ofSeconds(10));
	        try {
	            wait.until(ExpectedConditions.visibilityOf(element));
	            System.out.println("✅ Element ditemukan: " + element);
	        } catch (Exception e) {
	            throw new RuntimeException("❌ Element tidak ditemukan: " + element, e);
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
        System.out.println("🔘 Force tap at: X=" + centerX + " Y=" + centerY);
        DriverManager.getDriver().executeScript("mobile: tap", tapParams);
    }



}

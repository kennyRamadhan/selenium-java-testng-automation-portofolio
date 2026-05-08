package com.kennyramadhan.qa.tests.mobile;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;

import com.kennyramadhan.qa.mobile.server.AppiumServerManager;
import com.kennyramadhan.qa.core.driver.DriverManager;
import com.kennyramadhan.qa.mobile.pages.Login;
import io.appium.java_client.AppiumDriver;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;


/**
 * <h1>BaseMobileTest</h1>
 * Base class for all mobile automation test cases.
 *
 * <p>
 * <b>Responsibilities:</b>
 * </p>
 * <ul>
 * <li>Manage the Appium server lifecycle (start &amp; stop)</li>
 * <li>Initialize and maintain the mobile driver</li>
 * <li>Handle login before each test case runs</li>
 * <li>Reset application state after each test case completes</li>
 * </ul>
 *
 * <p>
 * <b>Usage:</b> Extend this class in every test class so the driver and login
 * configuration are set up before test execution.
 * </p>
 *
 * @author Kenny Ramadhan
 * @version 1.0
 */
public class BaseMobileTest {

	private static final Logger log = LoggerFactory.getLogger(BaseMobileTest.class);

	protected Login login;



	/**
	 * Starts the Appium server and initializes the driver before all test cases run.
	 *
	 * @throws Exception if the Appium server fails to start or driver initialization fails
	 */
	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {

		AppiumServerManager.startAppiumServer();
	    // Get driver from AppiumServerManager and store in DriverManager
	    DriverManager.setDriver(AppiumServerManager.initDriver());
	    
	  
	    
	
	}

	/**
	 * Stops the Appium server and closes the driver after all tests complete.
	 */
	@AfterSuite(alwaysRun = true)
	public void tearDown() {

		AppiumDriver driver = DriverManager.getDriver();
        if (driver != null) {
            driver.quit();
            DriverManager.unload(); // clear ThreadLocal
        }
        AppiumServerManager.stopAppiumServer();
	}

	/**
	 * Ensures the driver is available before each test case runs.
	 * <p>
	 * If the driver is null, re-initialize the driver.
	 * </p>
	 *
	 * @throws Exception if driver re-initialization fails
	 */

	@BeforeMethod
	public void ensureDriverReady() throws Exception {
		  if (DriverManager.getDriver() == null) {
	            log.warn("Driver null, re-initializing...");
	            AppiumDriver driver = AppiumServerManager.initDriver();
	            DriverManager.setDriver(driver);

	            if (DriverManager.getDriver() == null) {
	                throw new RuntimeException("Failed to initialize Appium Driver!");
	            }

		}

		  // Ensure the app is launched again
			 AppiumDriver driver = DriverManager.getDriver();
		    String platformName = driver.getCapabilities().getCapability("platformName").toString().toLowerCase();

		    if (platformName.contains("android")) {
		        // Android: use launchApp()
		    	 driver.executeScript("mobile: launchApp", new HashMap<>());
		    } else if (platformName.contains("ios")) {
		        // iOS: use bundleId to relaunch
		        Map<String, Object> launchAppArgs = new HashMap<>();
		        launchAppArgs.put("bundleId", "com.saucelabs.SwagLabsMobileApp");
		        driver.executeScript("mobile: launchApp", launchAppArgs);
		    }

		    log.info("Driver and app ready.");
	}

	/**
	 * Close the application after each test case finishes.
	 */
	@AfterMethod(alwaysRun = true)
	public void resetAppState() {
		 AppiumDriver driver = DriverManager.getDriver();

		    if (driver == null) {
		        log.warn("Driver is null, skipping resetAppState.");
		        return;
		    }

		    String platformName = driver.getCapabilities()
		            .getCapability("platformName")
		            .toString()
		            .toLowerCase();

		    try {
		        if (platformName.contains("android")) {
		            // Android: terminate or reset app
		            String appPackage = driver.getCapabilities().getCapability("appPackage").toString();
		            if (driver instanceof AndroidDriver) {
		                ((AndroidDriver) driver).terminateApp(appPackage); // Close app
		                log.info("[OK] Android app terminated: {}", appPackage);
		            }
		        } else if (platformName.contains("ios")) {
		            // iOS: terminate app using bundleId
		            if (driver instanceof IOSDriver) {
		                Map<String, Object> closeAppArgs = new HashMap<>();
		                closeAppArgs.put("bundleId", driver.getCapabilities().getCapability("bundleId"));
		                driver.executeScript("mobile: terminateApp", closeAppArgs);
		                log.info("[OK] iOS app terminated.");
		            }
		        }
		    } catch (Exception e) {
		        log.warn("Failed to terminate/reset app: {}", e.getMessage());
		    }
	}

}

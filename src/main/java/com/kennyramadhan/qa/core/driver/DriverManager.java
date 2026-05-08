package com.kennyramadhan.qa.core.driver;

import io.appium.java_client.AppiumDriver;


/**
 * <h1>DriverManager</h1>
 * Stores the IOSDriver instance per thread to support parallel test execution.
 *
 * <p>
 * Uses ThreadLocal so each test thread has its own driver instance.
 * </p>
 *
 * <b>Usage:</b>
 * <pre>
 * DriverManager.setDriver(driver);
 * IOSDriver driver = DriverManager.getDriver();
 * DriverManager.unload();
 * </pre>
 *
 * @author Kenny
 * @version 1.0
 */
public class DriverManager {
    private static ThreadLocal<AppiumDriver> driverThreadLocal = new ThreadLocal<>();

    /**
     * Stores the driver instance for the current thread.
     */
    public static void setDriver(AppiumDriver driver) {
    	driverThreadLocal.set(driver);
    }

    /**
     * Retrieves the driver instance for the current thread.
     *
     * @return the active IOSDriver
     */
    public static AppiumDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Removes the driver from ThreadLocal after the test finishes.
     */
    public static void unload() {
    	driverThreadLocal.remove();
    }
}

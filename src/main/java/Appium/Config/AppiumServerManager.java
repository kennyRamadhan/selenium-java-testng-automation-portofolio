package Appium.Config;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.openqa.selenium.remote.DesiredCapabilities;

import Selenium.CustomHelper.*;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

/**
 * <h1>AppiumServerManager</h1>
 * This class manages the lifecycle of the Appium Server (start & stop)
 * and initializes the appropriate driver (iOS or Android) based on
 * the configuration provided in <code>config.properties</code>.
 *
 * <p><b>Usage:</b></p>
 * 
 * <pre>
 * // Example usage in BaseTest
 * AppiumServerManager.startAppiumServer();
 * AppiumServerManager.initDriver();
 * AppiumDriver driver = AppiumServerManager.getDriver();
 * </pre>
 *
 * <p>Supports both iOS and Android automation without hardcoding node or appium paths.</p>
 *
 * @author Kenny
 * @version 2.0 (multi-platform)
 */
public class AppiumServerManager {

    /** Singleton instance of the Appium service */
    private static AppiumDriverLocalService service;

    /** AppiumDriver instance (can be IOSDriver or AndroidDriver) */
    private static AppiumDriver driver;

    /**
     * Retrieves value from System Property or Environment Variable,
     * falling back to a default if neither is set.
     *
     * @param key The property or environment variable name
     * @param def Default value to return if not found
     * @return The resolved value or default
     */
    protected static String envOrProp(String key, String def) {
        String v = System.getProperty(key);
        if (v == null || v.isEmpty()) v = System.getenv(key);
        return (v == null || v.isEmpty()) ? def : v;
    }

    /**
     * Resolves Appium's main.js path without hardcoding.
     * Supports macOS, Linux, and Windows installations.
     *
     * @return File object pointing to main.js or null if not found
     */
    private static File resolveAppiumMainJs() {
        String override = envOrProp("APPIUM_MAIN_JS", "");
        if (!override.isEmpty()) return new File(override);
        String[] guesses = new String[]{
                System.getProperty("user.home") + "/.appium/node_modules/appium/build/lib/main.js",
                System.getProperty("user.home") + "/AppData/Roaming/npm/node_modules/appium/build/lib/main.js",
                "/usr/local/lib/node_modules/appium/build/lib/main.js"
        };
        for (String g : guesses) {
            File f = new File(g);
            if (f.exists()) return f;
        }
        return null;
    }

    /**
     * Resolves Node.js path automatically without hardcoding.
     *
     * @return File object pointing to Node.js binary or null if not found
     */
    private static File resolveNodePath() {
        String override = envOrProp("NODE_PATH", "");
        if (!override.isEmpty()) return new File(override);

        String[] guesses = new String[]{
                "/usr/local/bin/node",
                "/opt/homebrew/bin/node",
                System.getProperty("user.home") + "/.nvm/versions/node/current/bin/node",
                "C:\\Program Files\\nodejs\\node.exe"
        };
        for (String g : guesses) {
            File f = new File(g);
            if (f.exists()) return f;
        }
        return null;
    }

    /**
     * Generates a random port number between 1000-9999
     * to avoid conflicts when starting Appium.
     *
     * @return random port number
     */
    private static int getRandomPort() {
        return 1000 + new Random().nextInt(9000);
    }

    /**
     * Starts Appium Server with resolved Node and Appium paths.
     *
     * @throws IOException if there is an error starting the service
     * @throws InterruptedException if the current thread is interrupted
     */
    public static void startAppiumServer() throws IOException, InterruptedException {
        File nodePath = resolveNodePath();
        File appiumJSPath = resolveAppiumMainJs();
        int appiumPort = getRandomPort();

        service = new AppiumServiceBuilder()
                .usingDriverExecutable(nodePath)
                .withAppiumJS(appiumJSPath)
                .usingPort(appiumPort)
                .build();

        service.start();

        if (!service.isRunning()) {
            throw new RuntimeException("Failed to start Appium Server!");
        }
        System.out.println("✅ Appium Server started at: " + service.getUrl());
    }

    /**
     * Stops Appium Server if it is currently running.
     */
    public static void stopAppiumServer() {
        if (service != null && service.isRunning()) {
            service.stop();
            System.out.println("🛑 Appium Server stopped.");
        }
    }

    /**
     * Initializes AppiumDriver for either iOS or Android based on
     * `platformName` in config.properties.
     *
     * <p>Supported capabilities:</p>
     * <ul>
     *   <li>platformName</li>
     *   <li>deviceName</li>
     *   <li>automationName</li>
     *   <li>udid</li>
     *   <li>bundleId / appPackage / appActivity</li>
     *   <li>platformVersion</li>
     *   <li>noReset / fullReset</li>
     * </ul>
     *
     * @return Initialized AppiumDriver instance
     * @throws Exception if driver initialization fails
     */
    public static AppiumDriver initDriver() throws Exception {
        int maxRetry = 3;
        int attempt = 0;

        while (driver == null && attempt < maxRetry) {
            attempt++;
            try {
                DesiredCapabilities caps = new DesiredCapabilities();

                ConfigLoader.getAll().forEach(caps::setCapability);

                String platform = ConfigLoader.getOrDefault("platformName", "iOS").toLowerCase();

                if (platform.contains("ios")) {
                    driver = new IOSDriver(service.getUrl(), caps);
                    System.out.println("🍏 iOS Driver initialized for: " + ConfigLoader.getOrDefault("deviceName", "iPhone Simulator"));
                } else if (platform.contains("android")) {
                    driver = new AndroidDriver(service.getUrl(), caps);
                    System.out.println("🤖 Android Driver initialized for: " + ConfigLoader.getOrDefault("deviceName", "Android Emulator"));
                } else {
                    throw new IllegalArgumentException("Unsupported platform: " + platform);
                }

                DriverManager.setDriver(driver);
                return driver;

            } catch (Exception e) {
                System.err.println("[WARN] Failed to create driver (attempt " + attempt + "): " + e.getMessage());
                if (attempt == maxRetry) {
                    throw new RuntimeException("Failed to initialize driver after " + maxRetry + " attempts", e);
                }
                Thread.sleep(3000);
            }
        }

        throw new RuntimeException("Unable to initialize driver - unknown error.");
    }
}

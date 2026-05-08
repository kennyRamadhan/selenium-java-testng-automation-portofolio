package com.kennyramadhan.qa.core.driver;

import com.kennyramadhan.qa.web.client.WebConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Locale;

/**
 * Per-thread WebDriver lifecycle. Independent from the Appium-side
 * {@link DriverManager} so the mobile and web layers do not share state.
 *
 * <h2>Browser + headless resolution</h2>
 * <ul>
 *   <li>Browser: {@code -Dbrowser=chrome|firefox|edge}, case-insensitive.
 *       Falls back to {@link WebConfig#DEFAULT_BROWSER}.</li>
 *   <li>Headless: {@code -Dheadless=true} or environment {@code CI=true}
 *       forces headless; otherwise headed.</li>
 * </ul>
 *
 * <p>WebDriverManager is invoked before each driver constructor — it caches
 * resolved binaries under {@code ~/.cache/selenium/} so subsequent runs skip
 * the download.</p>
 */
public final class WebDriverFactory {

    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private WebDriverFactory() {}

    public enum Browser { CHROME, FIREFOX, EDGE }

    /**
     * Build a new WebDriver for the given browser + headless mode. Configures
     * common options (no-sandbox, disabled /dev/shm, 1920×1080 window) and
     * disables implicit waits (we rely on explicit {@code WebDriverWait}).
     */
    public static WebDriver create(Browser browser, boolean headless) {
        log.info("Creating WebDriver: browser={}, headless={}", browser, headless);
        WebDriver driver = switch (browser) {
            case CHROME -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions opts = new ChromeOptions();
                if (headless) opts.addArguments("--headless=new");
                opts.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
                yield new ChromeDriver(opts);
            }
            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions opts = new FirefoxOptions();
                if (headless) opts.addArguments("-headless");
                yield new FirefoxDriver(opts);
            }
            case EDGE -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions opts = new EdgeOptions();
                if (headless) opts.addArguments("--headless=new");
                opts.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
                yield new EdgeDriver(opts);
            }
        };
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        return driver;
    }

    /**
     * Convenience: resolve browser + headless from system properties / env,
     * create the driver, and stash it in this thread's slot. Returns the
     * driver for callers that prefer explicit reference.
     */
    public static WebDriver createAndStore() {
        WebDriver driver = create(resolveBrowser(), shouldRunHeadless());
        setDriver(driver);
        return driver;
    }

    /** Stash the given driver in the current thread's slot. */
    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    /** Return the driver bound to the current thread, or {@code null} if none. */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Quit the current thread's driver and clear the ThreadLocal slot. Safe
     * to call when no driver is bound (no-op).
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                log.warn("Error quitting WebDriver: {}", e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }

    /**
     * Resolve the browser from {@code -Dbrowser} (case-insensitive), falling
     * back to {@link WebConfig#DEFAULT_BROWSER}. Unknown values throw with a
     * clear message rather than silently defaulting.
     */
    public static Browser resolveBrowser() {
        String raw = System.getProperty("browser", WebConfig.DEFAULT_BROWSER);
        try {
            return Browser.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported browser: '" + raw + "'. Expected one of: chrome, firefox, edge.", e);
        }
    }

    /**
     * Headless when {@code -Dheadless=true} or env {@code CI=true}. CI runners
     * default to headless; local runs default to headed unless explicitly
     * overridden.
     */
    public static boolean shouldRunHeadless() {
        return "true".equalsIgnoreCase(System.getProperty("headless"))
                || "true".equalsIgnoreCase(System.getenv("CI"));
    }
}

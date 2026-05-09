package com.kennyramadhan.qa.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

/**
 * Exploratory helper that drives the SwagLabs Android app on the first
 * connected real device (falling back to emulator) and dumps the Appium page
 * source at each screen. Output lands in {@code target/uidump/}.
 *
 * <p>
 * Run via: {@code mvn test -Dtest=UiDumpHelper -DfailIfNoTests=false}
 *
 * <p>
 * Not part of the regular test suite — this is a Phase 3 pre-flight inventory
 * tool. Generated XMLs feed the locator mapping report
 * (target/uidump/locator-mapping.md) which guides the PageFactory removal and
 * locator update commits in Phase 3.
 */
public class UiDumpHelper {

	private static final Logger log = LoggerFactory.getLogger(UiDumpHelper.class);
	private static final Path UIDUMP_DIR = Paths.get("target", "uidump");
	private static final List<String> INVENTORY_LOG = new ArrayList<>();

	@Test
	public void captureSwagLabsScreens() throws Exception {
		Files.createDirectories(UIDUMP_DIR);

		AppiumDriverLocalService service = null;
		AndroidDriver driver = null;
		try {
			String udid = detectTargetUdid();
			String platformVersion = adbGetProp(udid, "ro.build.version.release");
			String model = adbGetProp(udid, "ro.product.model");
			log.info("Selected device: {} (model {}, Android {})", udid, model, platformVersion);
			INVENTORY_LOG.add("UDID: " + udid);
			INVENTORY_LOG.add("Model: " + model);
			INVENTORY_LOG.add("Android: " + platformVersion);
			INVENTORY_LOG.add("Started: " + Instant.now());

			int port = 1000 + new Random().nextInt(9000);
			// Inject ANDROID_HOME into the Appium server subprocess. The Appium
			// UiAutomator2 driver requires it to locate adb/build-tools; without
			// this the session-create call fails with "Neither ANDROID_HOME nor
			// ANDROID_SDK_ROOT environment variable was exported" even when adb
			// is reachable from this Java process.
			Map<String, String> env = new HashMap<>(System.getenv());
			String androidHome = resolveAndroidHome();
			if (androidHome != null) {
				env.put("ANDROID_HOME", androidHome);
				log.info("Injecting ANDROID_HOME={} for Appium server subprocess", androidHome);
				INVENTORY_LOG.add("ANDROID_HOME: " + androidHome);
			} else {
				log.warn("ANDROID_HOME could not be resolved; Appium UiAutomator2 will likely fail");
				INVENTORY_LOG.add("ANDROID_HOME: NOT RESOLVED");
			}
			service = new AppiumServiceBuilder().usingPort(port).withEnvironment(env).build();
			service.start();
			log.info("Appium server: {}", service.getUrl());
			INVENTORY_LOG.add("Appium server: " + service.getUrl());

			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setCapability("platformName", "Android");
			caps.setCapability("appium:automationName", "UiAutomator2");
			caps.setCapability("appium:deviceName", "Android");
			caps.setCapability("appium:udid", udid);
			caps.setCapability("appium:platformVersion", platformVersion);
			caps.setCapability("appium:appPackage", "com.swaglabsmobileapp");
			caps.setCapability("appium:appActivity", "com.swaglabsmobileapp.SplashActivity");
			caps.setCapability("appium:app", new File("src/test/resources/apps/appTest.apk").getAbsolutePath());
			caps.setCapability("appium:noReset", true);
			caps.setCapability("appium:fullReset", false);
			caps.setCapability("appium:newCommandTimeout", 300);

			driver = new AndroidDriver(service.getUrl(), caps);
			log.info("Driver session created");

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

			// 01 — Login screen (initial state)
			settle();
			dump(driver, "01-login");

			// Type SwagLabs credentials (visible on login page bottom): standard_user /
			// secret_sauce
			type(driver, By.xpath("//*[@content-desc='test-Username']"), "standard_user", "type-username");
			type(driver, By.xpath("//*[@content-desc='test-Password']"), "secret_sauce", "type-password");
			tap(driver, By.xpath("//*[@content-desc='test-LOGIN']"), "tap-LOGIN");

			// 02 — Products list
			waitForAny(wait, By.xpath("//*[@content-desc='test-PRODUCTS']"), By.xpath("//*[@text='PRODUCTS']"),
					By.xpath("//*[@content-desc='test-Item title']"));
			dump(driver, "02-products-list");

			// Tap first product item title
			tap(driver, By.xpath("(//*[@content-desc='test-Item title'])[1]"), "tap-first-product");

			// 03 — Product details
			waitForAny(wait, By.xpath("//*[@content-desc='test-Description']"),
					By.xpath("//*[@content-desc='test-ADD TO CART']"));
			dump(driver, "03-product-details");

			// Add to cart on details, then open cart
			tap(driver, By.xpath("//*[@content-desc='test-ADD TO CART']"), "tap-add-to-cart");
			tap(driver, By.xpath("//*[@content-desc='test-Cart']"), "tap-cart-icon");

			// 04 — Cart
			waitForAny(wait, By.xpath("//*[@content-desc='test-CHECKOUT']"), By.xpath("//*[@text='YOUR CART']"));
			dump(driver, "04-cart");

			// Proceed to checkout
			tap(driver, By.xpath("//*[@content-desc='test-CHECKOUT']"), "tap-CHECKOUT");

			// 05 — Checkout info (address form)
			waitForAny(wait, By.xpath("//*[@content-desc='test-First Name']"),
					By.xpath("//*[@content-desc='test-CONTINUE']"));
			dump(driver, "05-checkout-info");

			// Fill form fields and continue
			type(driver, By.xpath("//*[@content-desc='test-First Name']"), "Test", "type-firstName");
			type(driver, By.xpath("//*[@content-desc='test-Last Name']"), "User", "type-lastName");
			type(driver, By.xpath("//*[@content-desc='test-Zip/Postal Code']"), "12345", "type-zip");
			tap(driver, By.xpath("//*[@content-desc='test-CONTINUE']"), "tap-CONTINUE");

			// 06 — Checkout payment / overview
			waitForAny(wait, By.xpath("//*[@content-desc='test-FINISH']"), By.xpath("//*[contains(@text,'Payment')]"));
			dump(driver, "06-checkout-payment");

			// Submit order
			tap(driver, By.xpath("//*[@content-desc='test-FINISH']"), "tap-FINISH");

			// 07 — Order confirmation
			waitForAny(wait, By.xpath("//*[@content-desc='test-CHECKOUT: COMPLETE!']"),
					By.xpath("//*[contains(@text,'THANK YOU')]"),
					By.xpath("//*[@content-desc='test-CONTINUE SHOPPING']"));
			dump(driver, "07-order-confirmation");

			INVENTORY_LOG.add("Finished: " + Instant.now());
		} catch (Exception e) {
			log.error("Inventory run failed: {}", e.getMessage(), e);
			INVENTORY_LOG.add("FATAL: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (driver != null) {
					driver.quit();
				}
			} catch (Exception ignored) {
				// best-effort cleanup
			}
			try {
				if (service != null) {
					service.stop();
				}
			} catch (Exception ignored) {
				// best-effort cleanup
			}
			writeInventoryLog();
		}
	}

	private static void dump(AndroidDriver driver, String name) {
		try {
			String src = driver.getPageSource();
			Files.writeString(UIDUMP_DIR.resolve(name + ".xml"), src, StandardCharsets.UTF_8);
			log.info("Dumped {}.xml ({} bytes)", name, src.length());
			INVENTORY_LOG.add(name + ": CAPTURED (" + src.length() + " bytes)");
		} catch (Exception e) {
			log.warn("Dump {} failed: {}", name, e.getMessage());
			INVENTORY_LOG.add(name + ": ERROR " + e.getMessage());
		}
	}

	private static void tap(AndroidDriver driver, By locator, String label) {
		try {
			WebElement el = new WebDriverWait(driver, Duration.ofSeconds(15))
					.until(ExpectedConditions.elementToBeClickable(locator));
			el.click();
			log.info("Tapped: {}", label);
			INVENTORY_LOG.add(label + ": OK");
			settle();
		} catch (Exception e) {
			log.warn("Tap '{}' failed: {}", label, e.getMessage());
			INVENTORY_LOG.add(label + ": TAP FAILED " + e.getMessage());
			dumpError(driver, label);
		}
	}

	private static void type(AndroidDriver driver, By locator, String text, String label) {
		try {
			WebElement el = new WebDriverWait(driver, Duration.ofSeconds(15))
					.until(ExpectedConditions.elementToBeClickable(locator));
			el.click();
			el.sendKeys(text);
			log.info("Typed into: {}", label);
			INVENTORY_LOG.add(label + ": OK");
			settle();
		} catch (Exception e) {
			log.warn("Type '{}' failed: {}", label, e.getMessage());
			INVENTORY_LOG.add(label + ": TYPE FAILED " + e.getMessage());
			dumpError(driver, label);
		}
	}

	private static void waitForAny(WebDriverWait wait, By... locators) {
		for (By locator : locators) {
			try {
				wait.until(ExpectedConditions.presenceOfElementLocated(locator));
				return;
			} catch (Exception ignored) {
				// try next locator
			}
		}
		log.warn("None of the wait targets appeared; continuing anyway");
	}

	private static void settle() {
		try {
			Thread.sleep(800);
		} catch (InterruptedException ignored) {
			Thread.currentThread().interrupt();
		}
	}

	private static void dumpError(AndroidDriver driver, String label) {
		try {
			String safe = label.replaceAll("[^a-zA-Z0-9-]", "_");
			String src = driver.getPageSource();
			Files.writeString(UIDUMP_DIR.resolve(safe + ".error.xml"), src, StandardCharsets.UTF_8);
		} catch (Exception ignored) {
			// best-effort
		}
	}

	private static String detectTargetUdid() throws IOException, InterruptedException {
		List<String> udids = parseAdbDevices();
		if (udids.isEmpty()) {
			throw new IllegalStateException("No connected Android devices found via adb");
		}
		// Real device priority: first non-emulator entry, else first emulator
		return udids.stream().filter(u -> !u.startsWith("emulator-")).findFirst().orElse(udids.get(0));
	}

	private static List<String> parseAdbDevices() throws IOException, InterruptedException {
		ProcessBuilder pb = new ProcessBuilder(adbPath(), "devices");
		pb.redirectErrorStream(true);
		Process p = pb.start();
		List<String> udids = new ArrayList<>();
		try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
			String line;
			while ((line = r.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty() || line.startsWith("List of")) {
					continue;
				}
				String[] parts = line.split("\\s+");
				if (parts.length >= 2 && "device".equals(parts[1])) {
					udids.add(parts[0]);
				}
			}
		}
		p.waitFor();
		return udids;
	}

	private static String adbGetProp(String udid, String key) {
		try {
			ProcessBuilder pb = new ProcessBuilder(adbPath(), "-s", udid, "shell", "getprop", key);
			pb.redirectErrorStream(true);
			Process p = pb.start();
			try (BufferedReader r = new BufferedReader(
					new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
				String line = r.readLine();
				p.waitFor();
				return line == null ? "" : line.trim();
			}
		} catch (Exception e) {
			log.warn("adb getprop {} failed: {}", key, e.getMessage());
			return "";
		}
	}

	private static String adbPath() {
		String home = resolveAndroidHome();
		boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
		String exe = isWin ? "adb.exe" : "adb";
		if (home != null) {
			File f = new File(home, "platform-tools" + File.separator + exe);
			if (f.exists()) {
				return f.getAbsolutePath();
			}
		}
		return "adb";
	}

	/**
	 * Resolves the Android SDK root directory. Checks ANDROID_HOME and
	 * ANDROID_SDK_ROOT env vars first, then falls back to the Android Studio
	 * default install path on Windows
	 * ({@code %USERPROFILE%\AppData\Local\Android\Sdk}).
	 *
	 * @return absolute path to the SDK root, or {@code null} if not found
	 */
	private static String resolveAndroidHome() {
		String home = System.getenv("ANDROID_HOME");
		if (home == null) {
			home = System.getenv("ANDROID_SDK_ROOT");
		}
		if (home != null) {
			return home;
		}
		boolean isWin = System.getProperty("os.name").toLowerCase().contains("win");
		if (isWin) {
			String userHome = System.getProperty("user.home");
			if (userHome != null) {
				File f = new File(userHome, "AppData\\Local\\Android\\Sdk");
				if (f.exists()) {
					return f.getAbsolutePath();
				}
			}
		}
		return null;
	}

	private static void writeInventoryLog() {
		try {
			Files.write(UIDUMP_DIR.resolve("INVENTORY-LOG.txt"), INVENTORY_LOG, StandardCharsets.UTF_8);
		} catch (Exception ignored) {
			// best-effort
		}
	}
}

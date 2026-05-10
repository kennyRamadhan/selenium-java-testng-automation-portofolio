package com.kennyramadhan.qa.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cross-platform helpers for interacting with the Android Debug Bridge (adb).
 *
 * <p>
 * Provides device detection (real-device priority over emulator), property
 * lookup, and adb / ANDROID_HOME path resolution. Used by both production code
 * (AppiumServerManager device auto-detect) and test utilities (UiDumpHelper
 * inventory tool).
 *
 * <p>
 * All methods are static and stateless. ADB invocations are best-effort: empty
 * results / null returns on failure rather than thrown exceptions, so callers
 * can fall back gracefully.
 */
public final class AdbHelper {

	private static final Logger log = LoggerFactory.getLogger(AdbHelper.class);

	private AdbHelper() {
		// Utility class — no instances.
	}

	/**
	 * Returns the UDID of the first connected Android device, preferring real
	 * devices over emulators. Falls back to the first emulator if no real device is
	 * connected. Throws {@link IllegalStateException} if no devices at all.
	 */
	public static String detectFirstConnectedDevice() {
		List<String> udids = parseAdbDevices();
		if (udids.isEmpty()) {
			throw new IllegalStateException("No connected Android devices found via adb");
		}
		String selected = udids.stream().filter(u -> !isEmulator(u)).findFirst().orElse(udids.get(0));
		log.info("Selected device UDID: {} (real-device priority; {} candidate(s))", selected, udids.size());
		return selected;
	}

	/**
	 * Returns true when the UDID matches the standard emulator naming convention.
	 */
	public static boolean isEmulator(String udid) {
		return udid != null && udid.startsWith("emulator-");
	}

	/**
	 * Returns the Android version (e.g. "15") for the given device by querying
	 * {@code ro.build.version.release} via adb shell getprop. Returns empty string
	 * on failure.
	 */
	public static String getAndroidVersion(String udid) {
		return getProp(udid, "ro.build.version.release");
	}

	/**
	 * Reads an arbitrary system property from the device via {@code adb shell
	 * getprop}. Returns empty string on failure.
	 */
	public static String getProp(String udid, String key) {
		try {
			ProcessBuilder pb = new ProcessBuilder(resolveAdbPath(), "-s", udid, "shell", "getprop", key);
			pb.redirectErrorStream(true);
			Process p = pb.start();
			try (BufferedReader r = new BufferedReader(
					new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
				String line = r.readLine();
				p.waitFor();
				return line == null ? "" : line.trim();
			}
		} catch (IOException | InterruptedException e) {
			log.warn("adb getprop {} failed: {}", key, e.getMessage());
			Thread.currentThread().interrupt();
			return "";
		}
	}

	/**
	 * Parses {@code adb devices} output and returns UDIDs in the "device" state.
	 */
	public static List<String> parseAdbDevices() {
		List<String> udids = new ArrayList<>();
		try {
			ProcessBuilder pb = new ProcessBuilder(resolveAdbPath(), "devices");
			pb.redirectErrorStream(true);
			Process p = pb.start();
			try (BufferedReader r = new BufferedReader(
					new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
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
		} catch (IOException | InterruptedException e) {
			log.warn("adb devices parsing failed: {}", e.getMessage());
			Thread.currentThread().interrupt();
		}
		return udids;
	}

	/**
	 * Resolves the adb binary path. Checks ANDROID_HOME / ANDROID_SDK_ROOT env vars
	 * first, then the Android Studio default install path on Windows. Falls back to
	 * bare {@code "adb"} (relying on PATH) if no install detected.
	 */
	public static String resolveAdbPath() {
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
	 * ANDROID_SDK_ROOT env vars, then falls back to the Android Studio default
	 * install path on Windows ({@code %USERPROFILE%\AppData\Local\Android\Sdk}).
	 * Returns null if the SDK cannot be located.
	 */
	public static String resolveAndroidHome() {
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
}

package Appium.Config;

import io.appium.java_client.AppiumDriver;


/**
 * <h1>DriverManager</h1>
 * Kelas ini menyimpan instance IOSDriver per-thread agar mendukung parallel test.
 *
 * <p>
 * Menggunakan ThreadLocal untuk memastikan setiap thread test punya driver sendiri.
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
     * Menyimpan instance driver untuk thread yang sedang berjalan.
     */
    public static void setDriver(AppiumDriver driver) {
    	driverThreadLocal.set(driver);
    }

    /**
     * Mengambil instance driver untuk thread yang sedang berjalan.
     *
     * @return IOSDriver aktif
     */
    public static AppiumDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Menghapus driver dari ThreadLocal setelah test selesai.
     */
    public static void unload() {
    	driverThreadLocal.remove();
    }
}

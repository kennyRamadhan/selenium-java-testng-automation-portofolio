package TestNG.Mobile;

import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;

import Appium.Config.AppiumServerManager;
import Appium.Config.DriverManager;
import Selenium.Pages.Login;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;


/**
 * <h1>BaseTest</h1> Kelas ini berfungsi sebagai kelas dasar untuk semua test
 * case mobile automation.
 * 
 * <p>
 * <b>Fungsi Utama:</b>
 * </p>
 * <ul>
 * <li>Mengelola siklus hidup Appium server (start & stop)</li>
 * <li>Mengelola inisialisasi dan pemeliharaan driver iOS</li>
 * <li>Menangani login sebelum test case dijalankan</li>
 * <li>Reset state aplikasi setelah test case selesai</li>
 * </ul>
 *
 * <p>
 * <b>Usage:</b> Extend kelas ini pada setiap kelas test case sehingga driver
 * dan konfigurasi login akan otomatis disiapkan sebelum eksekusi test.
 * </p>
 *
 * @author Kenny Ramadhan
 * @version 1.0
 */
public class BaseTest {
	
	
	protected Login login;



	/**
	 * Menjalankan Appium server dan menginisialisasi driver sebelum semua test case
	 * dijalankan.
	 * <p>
	 * Method ini juga akan memanggil {@link #performLogin()} agar semua test case
	 * langsung dijalankan dalam kondisi user sudah login.
	 * </p>
	 *
	 * @throws Exception jika gagal memulai Appium server atau menginisialisasi driver
	 */
	@BeforeClass(alwaysRun = true)
	public void setUp() throws Exception {
		 
		AppiumServerManager.startAppiumServer();
	    // Ambil driver dari AppiumServerManager + simpan di DriverManager
	    DriverManager.setDriver(AppiumServerManager.initDriver());
	    
	  
	    
	
	}

	/**
	 * Menghentikan Appium server dan menutup driver setelah semua test selesai
	 * dijalankan.
	 */
	@AfterSuite(alwaysRun = true)
	public void tearDown() {

		AppiumDriver driver = DriverManager.getDriver();
        if (driver != null) {
            driver.quit();
            DriverManager.unload(); // bersihkan ThreadLocal
        }
        AppiumServerManager.stopAppiumServer();
	}

	/**
	 * Memastikan driver selalu tersedia sebelum setiap test case dijalankan.
	 * <p>
	 * Jika driver null, maka akan dilakukan re-inisialisasi driver dan login ulang.
	 * </p>
	 *
	 * @throws Exception jika gagal melakukan re-inisialisasi driver
	 */
	@BeforeMethod
	public void ensureDriverReady() throws Exception {
		  if (DriverManager.getDriver() == null) {
	            System.out.println("Driver null, inisialisasi ulang...");
	            AppiumDriver driver = AppiumServerManager.initDriver();
	            DriverManager.setDriver(driver);

	            if (DriverManager.getDriver() == null) {
	                throw new RuntimeException("Gagal inisialisasi Appium Driver!");
	            }

		}
	}

	/**
	 * Close Aplikasi setiap test case selesai.
	 */
	@AfterMethod(alwaysRun = true)
	public void resetAppState() {
		 AppiumDriver driver = DriverManager.getDriver();

		    if (driver == null) {
		        System.out.println("⚠️ Driver is null, skipping resetAppState.");
		        return;
		    }

		    String platformName = driver.getCapabilities()
		            .getCapability("platformName")
		            .toString()
		            .toLowerCase();

		    try {
		        if (platformName.contains("android")) {
		            // ✅ Android: terminate or reset app
		            String appPackage = driver.getCapabilities().getCapability("appPackage").toString();
		            if (driver instanceof AndroidDriver) {
		                ((AndroidDriver) driver).terminateApp(appPackage); // Close app
		                System.out.println("✅ Android app terminated: " + appPackage);
		            }
		        } else if (platformName.contains("ios")) {
		            // ✅ iOS: terminate app using bundleId
		            if (driver instanceof IOSDriver) {
		                Map<String, Object> closeAppArgs = new HashMap<>();
		                closeAppArgs.put("bundleId", driver.getCapabilities().getCapability("bundleId"));
		                driver.executeScript("mobile: terminateApp", closeAppArgs);
		                System.out.println("✅ iOS app terminated.");
		            }
		        }
		    } catch (Exception e) {
		        System.err.println("⚠️ Failed to terminate/reset app: " + e.getMessage());
		    }
	}
	
//	public void performLogin() {
//		
//		login.scrollIntoText("standard_user");
//		login.getStandardUser();
//		login.clickLoginBtn();
//		
//		// Verify if user success log in 
//		login.verifySuccessLogin();
//	}



}

package Extent.Listeners;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import Appium.Config.DriverManager;
import io.appium.java_client.AppiumDriver;


/**
 * <h1>TestListeners</h1>
 * Implementasi custom TestNG {@link ITestListener} untuk mengelola event test 
 * seperti start, success, failure, skip, dan selesai eksekusi suite.
 *
 * <p><b>Fitur Utama:</b></p>
 * <ul>
 *   <li>Inisialisasi ExtentReports di awal suite</li>
 *   <li>Membuat node report untuk setiap test case</li>
 *   <li>Menangkap screenshot pada saat test sukses atau gagal</li>
 *   <li>Menambahkan log PASS/FAIL ke Extent Report</li>
 *   <li>Melakukan flush report setelah semua test selesai</li>
 * </ul>
 *
 * <p><b>Penggunaan:</b></p>
 * <pre>
 * &commat;Listeners(TestListeners.class)
 * public class AddNewClientTest {
 *     // Test case di sini
 * }
 * </pre>
 *
 * <p>Class ini membantu membuat reporting lebih informatif dengan menambahkan screenshot 
 * dan log status setiap test case.</p>
 *
 * @author Kenny Ramadhan
 * @since 2025-09-03
 * @version 1.0
 */

public class TestListeners implements ITestListener{
	
	 ExtentReports extent = ExtentReportsManager.getExtentReports();
	 ExtentTest test;
	 
	 	/**
	     * Dipanggil sekali sebelum suite dimulai.
	     * Menginisialisasi ExtentReports dan menambahkan informasi suite.
	     */
	    @Override
	    public void onStart(ITestContext context) {
	        // Ambil instance dari ExtentReportsManager (sudah otomatis bikin folder & file)
	        extent = ExtentReportsManager.getExtentReports();

	        // Kalau perlu, update system info tambahan
	        extent.setSystemInfo("Test Suite", context.getSuite().getName());
	    }

	    
	    /**
	     * Dipanggil setiap kali sebuah test method dimulai.
	     * Membuat node test baru di Extent Report dan reset counter log.
	     */
	    @Override
	    public void onTestStart(ITestResult result) {
//	    	test = extent.createTest(result.getMethod().getMethodName());
	    	System.out.println("[DEBUG] Creating Extent Test for: " + result.getMethod().getMethodName());
	    	ExtentNode.createTest(result.getMethod().getMethodName());
	    	LogHelper.resetCounter();
	    }
	    
	    
	    /**
	     * Dipanggil jika test berhasil.
	     * Menyimpan screenshot ke folder reports/pass dan melog status PASS.
	     */
	    @Override
	    public void onTestSuccess(ITestResult result) {
	    	 AppiumDriver driver = DriverManager.getDriver();
	        try {

	            if (driver != null) {
	                String screenshotPath = getSuccesScreenshotPath(result.getMethod().getMethodName());
	                ExtentNode.getNode().addScreenCaptureFromPath(screenshotPath);
	                LogHelper.pass("Test Success");
	            } else {
	                System.out.println("Driver is null, skipping screenshot for test: " + result.getMethod().getMethodName());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        
	        }
	        
	        ExtentTest node = ExtentNode.getNode();
	        if (node != null) {
	            node.fail(result.getThrowable());
	        } else {
	            System.out.println("[WARNING] ExtentNode.getNode() null. Logging to console instead.");
	            result.getThrowable().printStackTrace();
	        }
			
	    }
	    
	    
	    /**
	     * Dipanggil jika test gagal.
	     * Menyimpan screenshot ke folder reports/fail dan melog status FAIL.
	     */
	    @Override
	    public void onTestFailure(ITestResult result) {    
	    	 AppiumDriver driver = DriverManager.getDriver();
	    	try {
	    		
	            if (driver != null) {
	                String screenshotPath = getFailedScreenshotPath(result.getMethod().getMethodName());
	                ExtentNode.getNode().addScreenCaptureFromPath(screenshotPath);
	                LogHelper.fail("Test Failed");
	            } else {
	                System.out.println("Driver is null, skipping screenshot for test: " + result.getMethod().getMethodName());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	    	 ExtentNode.getNode().fail(result.getThrowable());
	    	 
	    	 ExtentTest node = ExtentNode.getNode();
	    	    if (node != null) {
	    	        node.fail(result.getThrowable());
	    	    } else {
	    	        System.out.println("[WARNING] ExtentNode.getNode() null. Logging to console instead.");
	    	        result.getThrowable().printStackTrace();
	    	    }
			
	    }
	    
	    
	    /**
	     * Dipanggil jika test dilewati (skip).
	     * Bisa ditambahkan log atau screenshot jika diperlukan.
	     */
	    @Override
	    public void onTestSkipped(ITestResult result) {
	    }
	    
	    

	    /**
	     * Dipanggil sekali setelah suite selesai dijalankan.
	     * Melakukan flush ExtentReports agar file report final dibuat.
	     */
	    @Override
	    public void onFinish(ITestContext context) {
	    	 System.out.println("Flushing Extent Report...");
	        extent.flush(); // Flush sekali di akhir suite
	        System.out.println("Extent Report generated at: " +
	                System.getProperty("user.dir") + "/reports/");
	    }
	    
	    
	    
	    
	    
	    
	    /**
	     * Membuat screenshot untuk test yang berhasil.
	     *
	     * @param testCasesName nama test case
	     * @param driver instance IOSDriver
	     * @return path file screenshot
	     * @throws IOException jika file gagal disimpan
	     */
	   
	    
	    private String getSuccesScreenshotPath(String testCasesName) throws IOException {
	    	AppiumDriver driver = DriverManager.getDriver();
			
			File source = driver.getScreenshotAs(OutputType.FILE);
			String destinationFile = System.getProperty("user.dir")+"/reports/pass/"+testCasesName+".png";
			FileUtils.copyFile(source,new File(destinationFile));
			return destinationFile;
		}
	    
	    
	    
	    
	    /**
	     * Membuat screenshot untuk test yang gagal.
	     *
	     * @param testCasesName nama test case
	     * @param driver instance IOSDriver
	     * @return path file screenshot
	     * @throws IOException jika file gagal disimpan
	     */
	    
	    private String getFailedScreenshotPath(String testCasesName) throws IOException {
	    	AppiumDriver driver = DriverManager.getDriver();
			File source = driver.getScreenshotAs(OutputType.FILE);
			String destinationFile = System.getProperty("user.dir")+"/reports/fail/"+testCasesName+".png";
			FileUtils.copyFile(source,new File(destinationFile));
			return destinationFile;
		}

}

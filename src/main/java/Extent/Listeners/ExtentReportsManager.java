package Extent.Listeners;

import java.io.File;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;


/**
 * <h1>ExtentReportsManager</h1>
 * Kelas utilitas untuk menginisialisasi dan mengelola instance 
 * {@link ExtentReports} agar dapat digunakan di seluruh suite test.
 *
 * <p><b>Fitur Utama:</b></p>
 * <ul>
 *   <li>Membuat direktori <code>/reports/</code> jika belum ada</li>
 *   <li>Membuat file report dengan nama berdasarkan timestamp</li>
 *   <li>Mengatur konfigurasi report seperti judul, nama report, dan tema</li>
 *   <li>Menyediakan sistem informasi seperti nama tester dan environment</li>
 * </ul>
 *
 * <p><b>Penggunaan:</b></p>
 * <pre>
 * ExtentReports extent = ExtentReportsManager.getExtentReports();
 * ExtentTest test = extent.createTest("Nama Test");
 * </pre>
 *
 * <p>Class ini memastikan hanya ada satu instance {@link ExtentReports} 
 * (singleton) yang digunakan selama seluruh eksekusi test suite.</p>
 *
 * @author Kenny Ramadhan
 * @version 1.0
 * @since 2025-09-03
 */

public class ExtentReportsManager {

	static ExtentReports extent;
	

	
	
	


	/**
	 * Mengembalikan instance {@link ExtentReports}. 
	 * Jika belum ada, akan membuat instance baru dengan konfigurasi berikut:
	 * <ul>
	 *   <li>Direktori output: <code>/reports/</code></li>
	 *   <li>Nama file report: <code>ExtentReport_yyyy-MM-dd_HH-mm-ss.html</code></li>
	 *   <li>Judul dokumen: "Automation Sales4u"</li>
	 *   <li>Nama report: "Regression Suite"</li>
	 *   <li>Tema: {@link Theme#DARK}</li>
	 * </ul>
	 *
	 * @return objek {@link ExtentReports} yang siap digunakan
	 */
	
	public static ExtentReports getExtentReports() {
		
		if(extent == null) {
			 String reportDir = System.getProperty("user.dir") + "/reports/";
			 File directory = new File(reportDir);
	            if (!directory.exists()) {
	                directory.mkdirs();
	            }

	            String timeStamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());
	            String reportPath = reportDir + "ExtentReport_" + timeStamp + ".html";
			
	        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
	        spark.config().setDocumentTitle("Automation Sales4u");
	        spark.config().setReportName("Regression Suite");
	        spark.config().setTheme(Theme.DARK);

	        // Setup ExtentReports
	        extent = new ExtentReports();
	        extent.attachReporter(spark);
	        extent.setSystemInfo("Tester", System.getProperty("user.name"));
	        extent.setSystemInfo("Environtment", "UAT");
	        
	        
		}
        
		return extent;
		
		
	}
	

}

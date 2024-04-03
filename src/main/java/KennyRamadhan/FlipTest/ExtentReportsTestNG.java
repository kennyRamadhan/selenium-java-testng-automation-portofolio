package KennyRamadhan.FlipTest;

import org.testng.annotations.BeforeMethod;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportsTestNG {
	static ExtentReports extent;
	@BeforeMethod
	public static ExtentReports getExtendReport() {
		String path = System.getProperty("user.dir")+"\\reports\\index.html";
		ExtentSparkReporter reporter = new ExtentSparkReporter(path);
		extent = new ExtentReports();
		
		reporter.config().setDocumentTitle("SAUCELABS");
		reporter.config().setReportName("Mobile Automation Testing");
		reporter.config().setTheme(Theme.DARK);
		extent.attachReporter(reporter);
		extent.setSystemInfo("Testers", "Kenny Ramadhan");
		
		return extent;
	}
	
	

}

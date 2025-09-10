package Extent.Listeners;

import java.io.IOException;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import Selenium.CustomHelper.Utils;
import io.appium.java_client.AppiumDriver;

public class Listeners extends Utils implements ITestListener {
	
	AppiumDriver driver;
	ExtentReports extent = ExtentReportsTestNG.getExtendReport();
	ExtentTest test;
	
	@Override
	public void onTestStart(ITestResult results) {
		 test = extent.createTest(results.getMethod().getMethodName());
		
	}
	
	@Override
	public void onTestSuccess(ITestResult results) {
		
		try {
			driver = (AppiumDriver) results.getTestClass().getRealClass().getField("driver")
					.get(results.getInstance());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			test.addScreenCaptureFromPath(getSuccesScreenshotPath(results.getMethod().getMethodName(), driver), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		test.log(Status.PASS,"Test Passed");
		
	}
	
	@Override
	public void onTestFailure(ITestResult results) {
		
		try {
			driver = (AppiumDriver) results.getTestClass().getRealClass().getField("driver")
					.get(results.getInstance());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			test.addScreenCaptureFromPath(getFailedScreenshotPath(results.getMethod().getMethodName(), driver), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		test.fail(results.getThrowable());
		test.log(Status.FAIL,"Test Failed");
	}
	
	@Override
	public void onTestSkipped(ITestResult results) {
		
	}
	
	@Override
	public void onFinish(ITestContext context) {
		extent.flush();
	}
	

}

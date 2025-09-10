package Selenium.CustomHelper;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;

import io.appium.java_client.AppiumDriver;

public class Utils {

	public String getSuccesScreenshotPath(String testCasesName,AppiumDriver driver) throws IOException {
		
		File source = driver.getScreenshotAs(OutputType.FILE);
		String destinationFile = System.getProperty("user.dir")+"\\newReports\\Pass\\"+testCasesName+".png";
		FileUtils.copyFile(source,new File(destinationFile));
		return destinationFile;
	}
	
	public String getFailedScreenshotPath(String testCasesName,AppiumDriver driver) throws IOException {
		
		File source = driver.getScreenshotAs(OutputType.FILE);
		String destinationFile = System.getProperty("user.dir")+"\\newReports\\Failed\\"+testCasesName+".png";
		FileUtils.copyFile(source,new File(destinationFile));
		return destinationFile;
	}
	
}

package KennyRamadhan.FlipTest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.concurrent.TimeUnit;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class appiumBase {
	

	public AndroidDriver driver;
	public AppiumDriverLocalService builder;

	@SuppressWarnings("deprecation")
	@BeforeClass
	public void appiumSettings() throws MalformedURLException, URISyntaxException
	
  {
		
		builder = new AppiumServiceBuilder()
				.withAppiumJS(new File("C://Users//KenyRamadhan//AppData//Roaming//npm//node_modules//appium//build//lib//main.js"))
				.withIPAddress("127.0.0.1").usingPort(4723).build();
		builder.start();
		UiAutomator2Options options = new UiAutomator2Options();
		driver = new AndroidDriver(new URI("http://127.0.0.1:4723").toURL(), options);
		options.setDeviceName("Xiaomi M210K6G");
		options.setApp("C://Users//KenyRamadhan//eclipse-workspace//mobileAppAutomationTest//app//appTest.apk//");
		options.setCapability("appPackage", "com.swaglabsmobileapp");
		options.setCapability("appActivity","com.swaglabsmobileapp.MainActivity");
		options.setPlatformName("Android");
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//		options.setNoReset(false);
//		options.setFullReset(true);
//		options.setNewCommandTimeout(Duration.ofMillis(0));
//		options.autoGrantPermissions();
//		options.setAvdReadyTimeout(Duration.ofMillis(300000));
//		options.setAvdLaunchTimeout(Duration.ofMillis(300000));
//		options.setUiautomator2ServerInstallTimeout(Duration.ofMillis(60000));
//		options.setAndroidInstallTimeout(Duration.ofMillis(100000));
//		options.setAppWaitForLaunch(true);



	}
	@AfterClass
	public void tearDown() {
		
		driver.quit();
		builder.stop();

	}
	
	
	

}

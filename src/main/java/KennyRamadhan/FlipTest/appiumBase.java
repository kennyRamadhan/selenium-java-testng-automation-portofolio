package KennyRamadhan.FlipTest;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class appiumBase {
	

	public AndroidDriver driver;
	public AppiumDriverLocalService builder;
	public UiAutomator2Options options;
	@SuppressWarnings("deprecation")
	@BeforeMethod
	public void appiumSettings() throws MalformedURLException, URISyntaxException
	
  {
		
		builder = new AppiumServiceBuilder()
				.withAppiumJS(new File("C://Users//KenyRamadhan//AppData//Roaming//npm//node_modules//appium//build//lib//main.js"))
				.withIPAddress("127.0.0.1").usingPort(4723).build();
		builder.start();
		options = new UiAutomator2Options();
		options.setDeviceName("Kenny Ramadhan");
		options.setApp("C://Users//KenyRamadhan//Downloads//Android.SauceLabs.Mobile.app.2.7.1.apk");
		options.setCapability("appPackage", "com.swaglabsmobileapp");
		options.setCapability("appActivity","com.swaglabsmobileapp.MainActivity");
		options.setPlatformName("Android");
		options.setCapability("platformVersion", "13");
		options.setAppWaitForLaunch(true);
		options.setNoReset(false);
		driver = new AndroidDriver(new URI("http://127.0.0.1:4723").toURL(), options);
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//		options.setNewCommandTimeout(Duration.ofMillis(0));
//		options.autoGrantPermissions();
//		options.setAvdReadyTimeout(Duration.ofMillis(300000));
//		options.setAvdLaunchTimeout(Duration.ofMillis(300000));
//		options.setUiautomator2ServerInstallTimeout(Duration.ofMillis(60000));
//		options.setAndroidInstallTimeout(Duration.ofMillis(100000));
	}
	@AfterMethod
	public void tearDown() {
	
		if(driver != null){
		    driver.quit();
		   }
		builder.stop();

	}


}

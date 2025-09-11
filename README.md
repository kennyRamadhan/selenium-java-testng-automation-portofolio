###  Automation Project Description

		This project provides a complete mobile,web & API automation testing framework for iOS  using **Selenium + Appium + TestNG**,   
        with built-in reporting extent report. You can run this project with simple configuration just set your capabilities on config.properties for mobile test.
        No need to set path node js and appium main.js you can run this project from Windows and iOS.
				
---

###  Requirements & Tech Stack

				- **Java 17**
				- **Appium 2.11.2+**
				- **Node.js v24.4.1+**
				- **Xcode** (for iOS testing)
				- **Eclipse IDE** / **IntelliJ IDEA**

 ---

### Automation Tools & Server

				- **Selenium**
				- **Appium**
---
	
### Test Framework

				- **TestNG**
---

### Reporting

				- **ExtentReports** – for detailed HTML reports with logs, screenshots, and system info

---

###  Environment Setup

				Install Required Tools
				- [Java JDK 17](https://adoptium.net/)
				- [Maven](https://maven.apache.org/)
				- [Node.js](https://nodejs.org/) + [Appium](https://appium.io/)
				  
				  ```bash
				  npm install -g appium
				- Xcode (for iOS Simulator support)
				- Real Device (if running tests on a real device)
---

### Running Tests

    Preparation Before Run Test :

        - Get ipa,app or apk in https://github.com/saucelabs/sample-app-mobile/releases to matched with your device if using simulator for ios using app and bundle id for real device
        - Ensure the device or simulator is running before executing tests.
				- For real devices, make sure WebDriverAgent (WDA) is installed and trusted on the device.
				- Keep dependencies and Node/Appium versions up to date for better compatibility.
        - set config.properties (see project structure) to matched with your current device

		Run via Terminal (Command Description) :

				- mvn clean test	Run all tests based on testng.xml
				- mvn clean test -Dsurefire.suiteXmlFiles=testng.xml	Run a specific suite file
				- mvn clean test -Dtest=ClassNameTest	Run tests by class name
				- mvn clean test -Dtest=ClassNameTest#methodName	Run tests by specific method
				- mvn clean install -DskipTests	Compile project but skip tests
				- mvn clean test -X or mvn clean test -e	Run tests with debug output

		Run via Eclipse/IntelliJ

				- Run All Tests: Right-click testng.xml → Run As → TestNG Suite
				- Run Single Class: Right-click the test class → Run As → TestNG Test
---
### Project Structure

				
				src/
				 ├── main/java
				 │    ├── Appium/Config/                   # Handles Appium start/stop
				 │    ├── Selenium/Pages/                  # Page Object Models (POM)
				 │    ├── Selenium/CustomHelper/           # Utility/helper functions
				 │    ├── Extent/Listeners/                # Listeners for logging, screenshots, reporting , config appium capabilites and app resource for iOS & Android
				 │   
				 │
				 └── test/java
				      └── TestNG/Mobile/                   # Test Case Mobile
              ├── TestNG/API/                      # Test Case API
              ├── TestNG/Web/                      # Test Casse Web
		  		reports/
				 ├──  /pass
				 │    ├── Screnshots Pass Test Case
	 			 │
				 └── /fail
				 |     └── Screnshots Fail Test Case
				 │   
				 │
				 └── #ExtentReport.....html		   # File HTML Extent Report For Test Suite Run
		  
				pom.xml → Maven dependencies & build configuration
				testng.xml → TestNG suite & listener configuration
---

### Key Files

				- BaseTest – Global setup & teardown logic for all tests
				- ConfigLoader – Loads configuration values from .properties files
				- Utils – Custom utility functions (e.g. verifyElementExist, tapWhenElementVisible)
				- AppiumServerManager – Starts and stops Appium server automatically
				- Listeners – Custom TestNG listeners for logging, screenshots, and report generation
				

### Reports & Screenshots are automatically generated after each run test suite and stored in:
				
				plaintext
				Copy code
				reports/
				 ├── pass/
				 └── fail/
				Each report includes:
								- Test execution summary
								- Logs and step details
								- Screenshots for failed steps
---

### Test Execution Flow

				- Execute testng.xml
				- Listeners are triggered (logging, screenshot, reporting)
				- BaseTest initializes driver & environment
				- Test cases are executed via Page Object Model
				- Results are saved in reports/ directory

---
				
				
				



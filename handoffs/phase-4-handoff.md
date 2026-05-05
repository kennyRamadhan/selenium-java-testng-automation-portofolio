# Phase 4 — Add Web Layer

**Goal:** Add Selenium-based web automation targeting **automationexercise.com** as a parallel layer alongside mobile.

**Total commits:** 4
**Estimated time:** 4-6 hours
**Prerequisite:** Phase 3 complete (Allure reporting, modern page objects)
**Demo target:** [automationexercise.com](https://automationexercise.com)

---

## Required Reading

1. `CLAUDE.md` — agent rules (page object patterns, naming)
2. `MASTER_PLAN.md` — Section "Phase 4 — Add Web Layer" + "Target Akhir"
3. `phase-3-handoff.md` — mobile patterns established (mirror them for web)

Confirm starting state:
```bash
git log --oneline -15
mvn clean compile -q && echo "OK"
```

---

## Commits to Create (in order)

### Commit 4.1: `feat(web): add Selenium WebDriver foundation with multi-browser support`

**File: `pom.xml`**

Add dependencies:
```xml
<!-- Selenium core (already transitive via Appium, but pin explicitly) -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.27.0</version>
</dependency>

<!-- WebDriverManager for browser driver auto-resolution -->
<dependency>
    <groupId>io.github.bonigarcia</groupId>
    <artifactId>webdrivermanager</artifactId>
    <version>5.9.2</version>
</dependency>
```

**File: `src/main/java/com/kennyramadhan/qa/core/driver/WebDriverFactory.java`**

```java
package com.kennyramadhan.qa.core.driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public final class WebDriverFactory {
    private static final Logger log = LoggerFactory.getLogger(WebDriverFactory.class);

    private WebDriverFactory() {}

    public enum Browser { CHROME, FIREFOX, EDGE }

    public static WebDriver create(Browser browser, boolean headless) {
        log.info("Creating WebDriver: browser={}, headless={}", browser, headless);
        WebDriver driver = switch (browser) {
            case CHROME -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions opts = new ChromeOptions();
                if (headless) opts.addArguments("--headless=new");
                opts.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
                yield new ChromeDriver(opts);
            }
            case FIREFOX -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions opts = new FirefoxOptions();
                if (headless) opts.addArguments("-headless");
                yield new FirefoxDriver(opts);
            }
            case EDGE -> {
                WebDriverManager.edgedriver().setup();
                EdgeOptions opts = new EdgeOptions();
                if (headless) opts.addArguments("--headless=new");
                yield new EdgeDriver(opts);
            }
        };
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);  // Explicit waits only
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        return driver;
    }
}
```

**File: `src/main/java/com/kennyramadhan/qa/core/driver/DriverManager.java`**

Update existing class to support both `AppiumDriver` (mobile) and `WebDriver` (web). Use a generic `ThreadLocal<WebDriver>` since `AppiumDriver extends RemoteWebDriver implements WebDriver`. Provide typed getters:

```java
public static WebDriver getWebDriver() { return webDriverThreadLocal.get(); }
public static AppiumDriver getMobileDriver() {
    WebDriver d = webDriverThreadLocal.get();
    if (d instanceof AppiumDriver appium) return appium;
    throw new IllegalStateException("Driver is not an AppiumDriver");
}
```

Keep backward compatibility for existing mobile code by keeping `getDriver()` returning `AppiumDriver`.

**File: `src/main/resources/config/config-web.properties`** (new)

```properties
web.baseUrl=https://automationexercise.com
web.browser=CHROME
web.headless=false
web.timeout.seconds=15
```

**Verification:**
```bash
mvn clean compile -q
mvn dependency:tree -q | grep -E "selenium-java|webdrivermanager"
```

---

### Commit 4.2: `feat(web): implement page objects for automationexercise.com`

**Goal:** Build page objects for the automationexercise.com user flows we'll cover.

**Pages to create (under `src/main/java/com/kennyramadhan/qa/web/pages/`):**

1. **`BaseWebPage.java`** — base class with shared methods, mirrors `BaseMobilePage`:
   - `protected WebDriver driver`
   - `protected WebDriverWait wait` (15s default)
   - `protected void waitForVisible(By by)`
   - `protected void safeClick(By by)`
   - `protected void safeSendKeys(By by, String text)`
   - `protected boolean isDisplayed(By by)`
   - `protected String getText(By by)`

2. **`HomePage.java`** — landing page (`/`)
   - Locators: `LOGIN_LINK`, `PRODUCTS_LINK`, `CART_LINK`, `LOGO`
   - Methods: `open()`, `clickSignupLogin()`, `clickProducts()`, `clickCart()`, `isLogoDisplayed()`

3. **`SignupLoginPage.java`** — `/login`
   - Locators: signup name/email fields, login email/password fields, signup button, login button, error messages
   - Methods: `signupAs(String name, String email)`, `loginAs(String email, String password)`, `getLoginErrorMessage()`

4. **`AccountInformationPage.java`** — `/signup` (continuation after entering name+email)
   - Fill account details: title, password, DOB, country, etc.
   - Methods: `fillAccountForm(AccountForm form)`, `clickCreateAccount()`

5. **`AccountCreatedPage.java`** — `/account_created`
   - Method: `isAccountCreatedMessageDisplayed()`, `clickContinue()`

6. **`ProductsPage.java`** — `/products`
   - Locators: search input, search button, product cards, "Add to cart" buttons, "View product" links
   - Methods: `searchProduct(String query)`, `addProductToCart(int index)`, `viewProduct(int index)`, `getDisplayedProductCount()`

7. **`ProductDetailsPage.java`** — `/product_details/{id}`
   - Methods: `setQuantity(int qty)`, `clickAddToCart()`, `getProductName()`, `getProductPrice()`

8. **`CartPage.java`** — `/view_cart`
   - Locators: cart items, item names, item prices, item quantities, "Proceed to checkout" button, "Remove" icons
   - Methods: `getCartItemCount()`, `getCartItemName(int index)`, `removeItemAt(int index)`, `clickProceedToCheckout()`

9. **`CheckoutPage.java`** — `/checkout`
   - Locators: address details, review order section, comment textarea, "Place Order" button
   - Methods: `getDeliveryAddress()`, `getBillingAddress()`, `enterComment(String comment)`, `clickPlaceOrder()`

10. **`PaymentPage.java`** — `/payment`
    - Locators: name on card, card number, CVC, expiration, "Pay and Confirm Order" button
    - Methods: `fillPaymentDetails(PaymentDetails details)`, `clickPayAndConfirmOrder()`

11. **`OrderConfirmationPage.java`** — `/payment_done/{id}`
    - Method: `isOrderConfirmedMessageDisplayed()`, `getInvoiceDownloadLink()`

**DTOs as records:**
```java
public record AccountForm(String title, String password, String day, String month, String year,
                          String firstName, String lastName, String company, String address,
                          String address2, String country, String state, String city,
                          String zipcode, String mobileNumber) {}

public record PaymentDetails(String nameOnCard, String cardNumber, String cvc,
                             String expirationMonth, String expirationYear) {}
```

Place under `src/main/java/com/kennyramadhan/qa/web/models/`.

**Constraints:**
- No PageFactory. Use locator constants and `BaseWebPage` helpers.
- All page methods either return data or perform an action. No assertions.
- All page methods get `@Step` annotation for Allure reports.
- Use `By.cssSelector` over XPath where possible (faster, more readable).

**Verification:**
```bash
mvn clean compile -q
find src/main/java/com/kennyramadhan/qa/web -name "*Page.java" | wc -l
# Should show 11 pages
```

---

### Commit 4.3: `feat(web): add login, signup, and checkout web test scenarios`

**Files to create under `src/test/java/com/kennyramadhan/qa/tests/web/`:**

1. **`BaseWebTest.java`** — TestNG base class with `@BeforeMethod`/`@AfterMethod` for driver lifecycle:
   ```java
   @BeforeMethod(alwaysRun = true)
   public void setUp() {
       Browser browser = Browser.valueOf(ConfigLoader.get("web.browser"));
       boolean headless = Boolean.parseBoolean(ConfigLoader.get("web.headless"));
       WebDriver driver = WebDriverFactory.create(browser, headless);
       DriverManager.setWebDriver(driver);
       driver.get(ConfigLoader.get("web.baseUrl"));
   }

   @AfterMethod(alwaysRun = true)
   public void tearDown() {
       WebDriver driver = DriverManager.getWebDriver();
       if (driver != null) driver.quit();
       DriverManager.unload();
   }
   ```

2. **`LoginWebTest.java`** — covers AutomationExercise Test Cases 2 & 5:
   - `shouldLoginWithValidCredentials` (TC2)
   - `shouldRejectLoginWithInvalidCredentials` (TC5)
   - `shouldShowErrorOnEmptyFields`

3. **`RegisterUserWebTest.java`** — covers TC1:
   - `shouldRegisterNewUser` — full signup flow (uses Datafaker for unique email)
   - `shouldRejectDuplicateEmailRegistration`

4. **`ProductBrowsingWebTest.java`** — covers TC8, TC9, TC18:
   - `shouldDisplayAllProducts` (TC8)
   - `shouldSearchProductByKeyword` (TC9)
   - `shouldNavigateToCategoryProducts` (TC18)

5. **`CheckoutWebTest.java`** — covers TC14, TC15:
   - `shouldCompleteOrderAsRegisteredUser` (TC14) — full register → login → add product → checkout → payment → confirmation
   - `shouldDisplayCorrectAddressDetailsInCheckout` (TC15)

**Test data:**
- Use Datafaker for randomized inputs: emails, names, addresses
- Hardcoded payment test data is fine (it's a demo site, no real charge)
- For login tests with existing user, register first via API or use a test account credential stored in env vars (NOT in source)

**Constraints:**
- All test methods independent (no `priority`)
- Use `groups = {"web", "smoke"}` etc. for filtering
- Add Allure annotations: `@Epic("Web Automation")`, `@Feature("Login")`, `@Story("Valid Login")`, `@Severity(SeverityLevel.CRITICAL)`
- Use AssertJ assertions

**Create `src/test/resources/suites/testng-web.xml`:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Web Suite" parallel="methods" thread-count="3">
    <listeners>
        <listener class-name="com.kennyramadhan.qa.core.retry.RetryListener"/>
        <listener class-name="com.kennyramadhan.qa.core.reporting.AllureTestListener"/>
    </listeners>
    <test name="Web Tests">
        <classes>
            <class name="com.kennyramadhan.qa.tests.web.LoginWebTest"/>
            <class name="com.kennyramadhan.qa.tests.web.RegisterUserWebTest"/>
            <class name="com.kennyramadhan.qa.tests.web.ProductBrowsingWebTest"/>
            <class name="com.kennyramadhan.qa.tests.web.CheckoutWebTest"/>
        </classes>
    </test>
</suite>
```

**Verification:**
```bash
mvn clean test-compile -q
# If browser is available locally:
mvn test -Dsurefire.suiteXmlFiles=src/test/resources/suites/testng-web.xml \
  -Dweb.headless=true -Dgroups=smoke
```

---

### Commit 4.4: `feat(build): add Maven profiles for mobile, web, api, and BrowserStack`

**File: `pom.xml`**

Add `<profiles>` section:

```xml
<profiles>
    <profile>
        <id>mobile</id>
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <suiteXmlFiles>
                            <suiteXmlFile>src/test/resources/suites/testng-mobile.xml</suiteXmlFile>
                        </suiteXmlFiles>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>

    <profile>
        <id>web</id>
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <suiteXmlFiles>
                            <suiteXmlFile>src/test/resources/suites/testng-web.xml</suiteXmlFile>
                        </suiteXmlFiles>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>

    <profile>
        <id>api</id>
        <!-- Will be populated in Phase 5 -->
    </profile>

    <profile>
        <id>browserstack</id>
        <properties>
            <web.browser>CHROME_REMOTE</web.browser>
            <browserstack.username>${env.BROWSERSTACK_USERNAME}</browserstack.username>
            <browserstack.key>${env.BROWSERSTACK_KEY}</browserstack.key>
        </properties>
    </profile>

    <profile>
        <id>all</id>
        <build>
            <plugins>
                <plugin>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <suiteXmlFiles>
                            <suiteXmlFile>src/test/resources/suites/testng-mobile.xml</suiteXmlFile>
                            <suiteXmlFile>src/test/resources/suites/testng-web.xml</suiteXmlFile>
                            <suiteXmlFile>src/test/resources/suites/testng-api.xml</suiteXmlFile>
                        </suiteXmlFiles>
                    </configuration>
                </plugin>
            </plugins>
        </build>
    </profile>
</profiles>
```

**BrowserStack integration:**
Create `RemoteWebDriverFactory.java` (optional in this commit, can be deferred):
- Reads BrowserStack credentials from env vars
- Constructs `RemoteWebDriver` with proper capabilities
- Used when `web.browser=CHROME_REMOTE`

**Update `WebDriverFactory.create()`** to support `CHROME_REMOTE` enum value that delegates to `RemoteWebDriverFactory`.

**Verification:**
```bash
mvn help:active-profiles -P web
mvn help:active-profiles -P browserstack
```

---

## Phase-Specific Constraints

1. **Demo target is automationexercise.com.** Do NOT use practicesoftwaretesting.com (Kenny's Python repo uses that — different target enforces story arc).
2. **Headless mode default for CI.** For local dev, can switch to `web.headless=false` via system property override.
3. **WebDriverManager handles all driver binaries.** Do NOT commit any chromedriver/geckodriver binaries.
4. **Use Datafaker for test data.** Random emails/names per test execution to avoid duplicate-user conflicts.
5. **No assertions in page objects.** Same rule as mobile.
6. **Use `By` constants, not PageFactory.** Same rule as mobile.
7. **Web tests don't need Appium.** `BaseWebTest` lifecycle is independent of `BaseMobileTest`.

---

## Definition of Done

After all 4 commits:
1. `mvn clean compile -q` succeeds
2. `mvn clean test-compile -q` succeeds
3. `mvn test -P web -Dweb.headless=true -Dgroups=smoke` runs at least 1 test green (Kenny verifies locally)
4. Allure results generated under `target/allure-results/` after web run
5. New directory tree visible: `src/main/java/com/kennyramadhan/qa/web/{pages,models}` and `src/test/java/com/kennyramadhan/qa/tests/web/`
6. Push to origin

---

## Reporting Format

```
Phase 4 complete: Web Layer

Commits:
- <SHA1> feat(web): add Selenium WebDriver foundation with multi-browser support
- <SHA2> feat(web): implement page objects for automationexercise.com
- <SHA3> feat(web): add login, signup, and checkout web test scenarios
- <SHA4> feat(build): add Maven profiles for mobile, web, api, and BrowserStack

Verification:
- Selenium 4.27.0 + WebDriverManager 5.9.2 added
- 11 page objects created
- 4 test classes with N test methods total
- testng-web.xml with parallel methods (thread-count=3)
- Profiles: mobile, web, api, all, browserstack

Pushed to: origin/refactor/v2

Action required from Kenny:
- Run `mvn test -P web -Dweb.headless=true -Dgroups=smoke` to verify green
- Confirm Allure results populated

Concerns surfaced:
<list, or "none">

Ready for Phase 5 (API Layer).
```

---

## Stop Conditions

Stop and ask Kenny if:
- automationexercise.com is unreachable or returns errors during test development (rare, but possible during site maintenance)
- A specific page flow has changed structure since the last documented Test Case spec on the site
- A page object exceeds 200 LOC and feels like it should be split
- WebDriverManager fails to resolve a browser driver (CI env issue)
- BrowserStack capabilities format unclear — defer profile creation, ask for guidance

Do NOT proceed to Phase 5.

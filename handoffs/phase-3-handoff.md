# Phase 3 — Mobile Refactor

**Goal:** Modernize mobile automation layer — drop PageFactory, fix static state in reporting, implement proper waits, migrate from ExtentReports to Allure, and split monolithic test class.

**Total commits:** 5
**Estimated time:** 4-6 hours
**Prerequisite:** Phase 2 complete (modern Maven, SLF4J logging, new package structure)

---

## Required Reading

1. `CLAUDE.md` — agent rules (especially "Forbidden Patterns" section)
2. `MASTER_PLAN.md` — Section "Phase 3 — Mobile Refactor (5 commits)"
3. `phase-2-handoff.md` — what was just completed

Confirm starting state:
```bash
git log --oneline -10
mvn clean compile -q && echo "OK"
find src/main/java/com/kennyramadhan -type d
```

---

## Commits to Create (in order)

### Commit 3.1: `refactor(mobile): drop PageFactory, use explicit waits with locator constants`

**Files:** All `*.java` under `src/main/java/com/kennyramadhan/qa/mobile/pages/`

**Changes:**

1. **Create `BaseMobilePage`** at `src/main/java/com/kennyramadhan/qa/mobile/pages/BaseMobilePage.java`:
   - Holds `protected WebDriverWait wait` field (timeout 15s default)
   - Holds `protected AppiumDriver driver` field
   - Provides shared methods: `waitForVisible(By)`, `waitForClickable(By)`, `safeClick(By)`, `safeSendKeys(By, String)`, `isDisplayed(By)`, `getText(By)`, `findElements(By)`, `scrollIntoText(String)`
   - Constructor: `protected BaseMobilePage()` reads driver from `DriverManager`

2. **Refactor each page object** to:
   - Extend `BaseMobilePage`
   - Remove `PageFactory.initElements(...)` from constructor
   - Remove `@AndroidFindBy` and `@iOSXCUITFindBy` annotations
   - Remove `private WebElement xxx` fields
   - Replace with `private static final By` locator constants
   - For platform-specific locators, use a helper that returns the right `By` based on platform:
     ```java
     private static final By LOGIN_BTN = AppiumBy.accessibilityId("test-LOGIN");

     // For platform-specific:
     private static By cartButton() {
         return isAndroid()
             ? AppiumBy.accessibilityId("test-Cart")
             : AppiumBy.iOSClassChain("**/XCUIElementTypeOther[`name == \"test-Cart\"`]");
     }
     ```

3. **Remove all assertions from page objects.** Page methods should:
   - Return values (e.g. `boolean isLoginSuccessful()` instead of `void verifyLogin()`)
   - Or perform actions only (e.g. `void clickLogin()`)
   - Never call `Assert.fail()` or `Assert.assertTrue()` from inside page object code

4. **Remove `tapByCoordinates` workaround.** Replace usage with proper `wait.until(ExpectedConditions.elementToBeClickable(BY)).click()`. If clicks still fail, investigate root cause (overlapping elements, animation timing) — do not patch with raw coordinates.

5. **Rename infinite `while(true)` loop in `addAllProducts()`** to bounded loop:
   ```java
   private static final int MAX_PRODUCTS = 20;
   public void addAllProducts() {
       int added = 0;
       while (added < MAX_PRODUCTS) {
           List<WebElement> buttons = driver.findElements(ADD_TO_CART_BTN);
           if (buttons.isEmpty()) break;
           for (WebElement btn : buttons) {
               try { btn.click(); added++; } catch (StaleElementReferenceException ignored) { }
           }
           scrollDown();
       }
   }
   ```

**Verification:**
```bash
mvn clean compile -q
grep -rn "PageFactory\|@iOSXCUITFindBy\|@AndroidFindBy\|tapByCoordinates" src/main/java/com/kennyramadhan/qa/mobile/pages/
# Should return 0 results
grep -rn "Assert\." src/main/java/com/kennyramadhan/qa/mobile/pages/
# Should return 0 results
```

---

### Commit 3.2: `refactor(mobile): split e2eMobile into focused test classes`

**File:** `src/test/java/com/kennyramadhan/qa/tests/mobile/E2EMobileTest.java` (the renamed `e2eMobile.java`)

**Action:** Split the 10 test methods across 3 new classes by domain:

**`LoginMobileTest.java`:**
- `shouldLoginWithStandardCredentials` (was `login`)
- `shouldRejectInvalidCredentials` (was `failedLogin`)

**`ProductsCatalogMobileTest.java`:**
- `shouldSortPricesLowToHigh` (was `sortingPrice`)
- `shouldAddAllProductsFromListing` (was `addAllProductsFromListing`)
- `shouldAddProductFromDetailsPage` (was `addProductsFromDetails`)
- `shouldAddMultipleProductsToCart` (was `addMultipleProducts`)

**`CheckoutMobileTest.java`:**
- `shouldRedirectToProductListingFromCart` (was `redirectionToProductListing`)
- `shouldDisplayCorrectProductInCart` (was `verifyDetailsProductIntoCart`)
- `shouldCompleteCheckoutWithValidInfo` (was `checkoutPositiveFlow`)
- `shouldRejectCheckoutWithMissingFirstName` (was `checkoutNegativeFlow`)

**Constraints:**
- All test classes extend `BaseMobileTest`
- Remove `priority = N` attributes (tests must be independent)
- Remove duplicate `login.getAutoCredentials("standard_user")` calls — move to `BaseMobileTest.login()` helper or `@BeforeMethod` of authenticated test groups
- Use TestNG `groups` for organization: `@Test(groups = {"smoke", "login"})`, etc.
- Remove `throws MalformedURLException, URISyntaxException` from method signatures — those exceptions are no longer thrown after Phase 2 refactor
- Move assertions from page objects (Phase 3.1 already removed them) into the test layer:
  ```java
  // Test:
  loginPage.loginWith("standard_user");
  assertThat(productsListPage.isDisplayed()).isTrue();  // AssertJ
  ```

**Update `testng.xml`:**
```xml
<suite name="Mobile Suite">
    <listeners>
        <listener class-name="com.kennyramadhan.qa.core.reporting.TestListeners"/>
    </listeners>
    <test name="Login">
        <groups><run><include name="login"/></run></groups>
        <classes>
            <class name="com.kennyramadhan.qa.tests.mobile.LoginMobileTest"/>
        </classes>
    </test>
    <test name="Catalog">
        <classes>
            <class name="com.kennyramadhan.qa.tests.mobile.ProductsCatalogMobileTest"/>
        </classes>
    </test>
    <test name="Checkout">
        <classes>
            <class name="com.kennyramadhan.qa.tests.mobile.CheckoutMobileTest"/>
        </classes>
    </test>
</suite>
```

Move this to `src/test/resources/suites/testng-mobile.xml` and update `pom.xml` `surefire` configuration to point there.

**Verification:**
```bash
mvn clean test-compile -q
find src/test/java -name "*Test.java"
# Should show LoginMobileTest, ProductsCatalogMobileTest, CheckoutMobileTest
```

---

### Commit 3.3: `refactor(mobile): move assertions out of page objects`

**Note:** This commit is partially redundant with 3.1 if assertions were already removed. Use this commit to:
1. Add `org.assertj.core.api.Assertions` imports across test classes
2. Convert TestNG `Assert.assertTrue/Equals` calls to AssertJ `assertThat(...).is...()` for fluent assertions in tests
3. Add explicit assertions for behaviors that page methods previously verified internally

**Example:**
```java
// Before (Phase 2 state — assertion in page object):
public void verifyOrderComplete() {
    if (!orderComplete.isDisplayed()) Assert.fail();
}

// After (Phase 3.1 — page object returns boolean):
public boolean isOrderComplete() {
    return isDisplayed(ORDER_COMPLETE_MSG);
}

// After (Phase 3.3 — assertion in test layer using AssertJ):
@Test
public void shouldCompleteCheckoutWithValidInfo() {
    // ... checkout steps ...
    cartPage.clickFinish();
    assertThat(cartPage.isOrderComplete())
        .as("Order completion message should appear after finish")
        .isTrue();
}
```

**Verification:**
```bash
grep -rn "import static org.assertj" src/test/java/
# Should show widespread usage
grep -rn "Assert\.fail\|Assert\.assertTrue\|Assert\.assertEquals" src/main/java/com/kennyramadhan/qa/mobile/pages/
# Should return 0 results
```

---

### Commit 3.4: `feat(reporting): migrate ExtentReports to Allure`

**Goal:** Replace ExtentReports entirely with Allure.

**Files to delete:**
- `src/main/java/com/kennyramadhan/qa/core/reporting/ExtentNode.java`
- `src/main/java/com/kennyramadhan/qa/core/reporting/ExtentReportsManager.java`
- `src/main/java/com/kennyramadhan/qa/core/reporting/LogHelper.java` (replaced by `AllureLogger`)
- Old `TestListeners.java` (replaced with Allure-native listener; see below)

**Files to create:**

**`AllureLogger.java`** at `src/main/java/com/kennyramadhan/qa/core/reporting/AllureLogger.java`:
```java
package com.kennyramadhan.qa.core.reporting;

import io.qameta.allure.Allure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AllureLogger {
    private static final Logger log = LoggerFactory.getLogger(AllureLogger.class);

    private AllureLogger() {}

    public static void step(String name) {
        log.info("STEP: {}", name);
        Allure.step(name);
    }

    public static <T> T step(String name, Allure.ThrowableRunnable.NonReturning runnable) throws Throwable {
        log.info("STEP: {}", name);
        Allure.step(name, runnable);
        return null;
    }

    public static void info(String message) {
        log.info(message);
        Allure.addAttachment("info", message);
    }

    public static void attachScreenshot(String name, byte[] data) {
        Allure.addAttachment(name, "image/png", new java.io.ByteArrayInputStream(data), "png");
    }
}
```

**`ScreenshotAttacher.java`** at `src/main/java/com/kennyramadhan/qa/core/reporting/ScreenshotAttacher.java`:
```java
package com.kennyramadhan.qa.core.reporting;

import com.kennyramadhan.qa.core.driver.DriverManager;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public final class ScreenshotAttacher {
    private ScreenshotAttacher() {}

    @Attachment(value = "Screenshot — {name}", type = "image/png")
    public static byte[] capture(String name) {
        return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
    }
}
```

**`AllureTestListener.java`** at `src/main/java/com/kennyramadhan/qa/core/reporting/AllureTestListener.java`:
```java
package com.kennyramadhan.qa.core.reporting;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllureTestListener implements ITestListener {
    private static final Logger log = LoggerFactory.getLogger(AllureTestListener.class);

    @Override public void onTestSuccess(ITestResult result) {
        log.info("[PASS] {}", result.getMethod().getMethodName());
        ScreenshotAttacher.capture("Final state — " + result.getMethod().getMethodName());
    }

    @Override public void onTestFailure(ITestResult result) {
        log.error("[FAIL] {}: {}", result.getMethod().getMethodName(),
                  result.getThrowable() != null ? result.getThrowable().getMessage() : "no exception");
        ScreenshotAttacher.capture("Failure state — " + result.getMethod().getMethodName());
    }

    @Override public void onTestSkipped(ITestResult result) {
        log.warn("[SKIP] {}", result.getMethod().getMethodName());
    }
}
```

**Update test classes:**
- Replace `LogHelper.step("...")` calls with `AllureLogger.step("...")` or use `@Step` annotations from Allure on page methods
- Add `@Epic`, `@Feature`, `@Story` annotations on test classes for hierarchy
- Add `@Severity(SeverityLevel.CRITICAL)` on critical tests (login, checkout)

**Update `testng.xml`** to use `AllureTestListener` instead of `TestListeners`.

**Update `pom.xml`** to remove ExtentReports dependency and add `allure-maven` plugin if not already present.

**Add `src/test/resources/allure.properties`:**
```properties
allure.results.directory=target/allure-results
allure.link.issue.pattern=https://github.com/kennyRamadhan/selenium-java-testng-automation-portofolio/issues/{}
```

**Verification:**
```bash
grep -rn "ExtentReports\|com.aventstack" src/
# Should return 0 results
mvn clean test-compile -q
ls target/allure-results 2>/dev/null  # Will be populated after first test run
```

---

### Commit 3.5: `feat(reliability): add RetryAnalyzer for flaky test handling`

**Files to create:**

**`RetryAnalyzer.java`** at `src/main/java/com/kennyramadhan/qa/core/retry/RetryAnalyzer.java`:
```java
package com.kennyramadhan.qa.core.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRIES = 2;
    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRIES) {
            retryCount++;
            log.warn("Retrying test {} (attempt {}/{})",
                     result.getMethod().getMethodName(), retryCount, MAX_RETRIES);
            return true;
        }
        return false;
    }
}
```

**`RetryListener.java`** at `src/main/java/com/kennyramadhan/qa/core/retry/RetryListener.java`:
```java
package com.kennyramadhan.qa.core.retry;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class RetryListener implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }
}
```

**Register listener in `testng-mobile.xml`:**
```xml
<listeners>
    <listener class-name="com.kennyramadhan.qa.core.retry.RetryListener"/>
    <listener class-name="com.kennyramadhan.qa.core.reporting.AllureTestListener"/>
</listeners>
```

**Verification:** No automated check; verify by reading `testng-mobile.xml`.

---

## Phase-Specific Constraints

1. **Run mobile tests at least once locally** to verify Allure migration produces output. Kenny will execute this manually since mobile test requires emulator/simulator. Agent should provide command: `mvn clean test -P mobile`.
2. **No coordinate-based taps.** If element click fails, investigate. Document any Appium quirks discovered.
3. **No `Thread.sleep()`.** Use `WebDriverWait` with appropriate `ExpectedConditions`.
4. **Allure annotations on page methods are optional but encouraged** for richer reports. Use `@Step("...")` on page object public methods.
5. **Each commit must compile.** Test execution gating is on Kenny.

---

## Definition of Done

After all 5 commits:
1. `mvn clean compile -q` succeeds
2. `mvn clean test-compile -q` succeeds
3. `grep -rn "PageFactory\|@iOSXCUITFindBy\|@AndroidFindBy" src/main/java/com/kennyramadhan/qa/mobile/pages/` returns 0
4. `grep -rn "ExtentReports\|com.aventstack" src/` returns 0
5. `grep -rn "Assert\." src/main/java/com/kennyramadhan/qa/mobile/pages/` returns 0
6. `e2eMobile` class no longer exists; replaced by 3 focused test classes
7. Push to origin

---

## Reporting Format

```
Phase 3 complete: Mobile Refactor

Commits:
- <SHA1> refactor(mobile): drop PageFactory, use explicit waits with locator constants
- <SHA2> refactor(mobile): split e2eMobile into focused test classes
- <SHA3> refactor(mobile): move assertions out of page objects
- <SHA4> feat(reporting): migrate ExtentReports to Allure
- <SHA5> feat(reliability): add RetryAnalyzer for flaky test handling

Verification:
- PageFactory removed: ✅
- ExtentReports removed: ✅
- Assertions in pages: 0
- Allure dependencies: ✅
- Test class split: LoginMobileTest, ProductsCatalogMobileTest, CheckoutMobileTest

Pushed to: origin/refactor/v2

Action required from Kenny:
- Run `mvn clean test -P mobile` with emulator running to verify Allure output
- Confirm screenshots attached on test failure

Concerns surfaced:
<list, or "none">

Ready for Phase 4 (Web Layer).
```

---

## Stop Conditions

Stop and ask Kenny if:
- A page object's locator strategy genuinely requires platform-specific logic that doesn't fit the helper pattern
- Removing assertions from a page object would require a contract change in tests that's ambiguous
- Allure annotations conflict with TestNG annotations (rare)
- Tests break in a way that suggests the existing tests were depending on bugs in old code
- Mobile-specific Appium API differences between java-client 8.6.0 and 9.3.0 cause unexpected breakage

Do NOT proceed to Phase 4.

# Phase 2 — Foundation Restructure

**Goal:** Total restructure of package layout, Maven config modernization, logging framework migration, and removal of legacy code.

**Total commits:** 8
**Estimated time:** 3-5 hours
**Prerequisite:** Phase 1 complete

---

## Required Reading

1. `CLAUDE.md` — agent rules
2. `MASTER_PLAN.md` — Section "Phase 2 — Foundation Restructure (8 commits)" and "Target Akhir" (final architecture)
3. `phase-1-handoff.md` — what was just completed

Confirm starting state:
```bash
git log --oneline -8
git branch --show-current
mvn clean compile -q && echo "compile OK"
```

Expected: branch `refactor/v2`, last 3 commits are Phase 1 fixes, compile succeeds.

---

## Commits to Create (in order)

### Commit 2.1: `chore(build): upgrade pom.xml to Java 25 and modern dependencies`

**File:** `pom.xml`

**Actions:**
1. Add a `<properties>` block with version constants for all dependencies (centralized version management).
2. Upgrade `maven.compiler.source` and `maven.compiler.target` from 17 → 25.
3. Upgrade dependencies:
   - `io.appium:java-client` 8.6.0 → 9.3.0
   - `org.testng:testng` 7.8.0 → 7.10.2
   - `org.apache.poi:poi-ooxml` 5.2.5 → 5.4.0
   - Remove `com.aventstack:extentreports` (not yet — keep until Commit 2.5 to avoid simultaneous breakage)
   - Replace `com.github.javafaker:javafaker` 1.0.2 → `net.datafaker:datafaker` 2.4.2
   - Add `org.slf4j:slf4j-api` 2.0.16
   - Add `ch.qos.logback:logback-classic` 1.5.12
   - Add `org.assertj:assertj-core` 3.26.3
   - Add `io.qameta.allure:allure-testng` 2.29.0 (will fully replace ExtentReports in Commit 2.5+)
4. Upgrade `maven-compiler-plugin` to 3.13.0 (already there, verify).
5. Upgrade `maven-surefire-plugin` to 3.5.2.
6. Add `allure-maven` plugin 2.13.0 for report generation.

**Important:** After this commit, the project may have compile errors due to JavaFaker → Datafaker rename. Those will be fixed in Commit 2.7 (deprecation cleanup). For now, focus on getting `mvn dependency:tree` to resolve cleanly.

**Skip the Allure annotations migration in this commit** — that happens in a later phase. Just adding the dependency.

**Verification:**
```bash
mvn dependency:tree -q | grep -E "datafaker|slf4j-api|logback|allure"
```
Should show all 4 new dependencies.

---

### Commit 2.2: `refactor: migrate package to com.kennyramadhan.qa namespace`

**Action:** Rename packages across all source files.

**Mapping:**
| Old Package | New Package |
|---|---|
| `Appium.Config` | `com.kennyramadhan.qa.mobile.server` and `com.kennyramadhan.qa.core.driver` |
| `Selenium.Pages` | `com.kennyramadhan.qa.mobile.pages` |
| `Selenium.CustomHelper` | `com.kennyramadhan.qa.core.config` and `com.kennyramadhan.qa.core.waits` |
| `Extent.Listeners` | `com.kennyramadhan.qa.core.reporting` (will be deleted in Commit 2.5; preserve for now) |
| `TestNG.Mobile` | `com.kennyramadhan.qa.tests.mobile` |

**File-by-file mapping:**
- `Appium/Config/AppiumServerManager.java` → `com/kennyramadhan/qa/mobile/server/AppiumServerManager.java`
- `Appium/Config/DriverManager.java` → `com/kennyramadhan/qa/core/driver/DriverManager.java`
- `Selenium/Pages/Login.java` → `com/kennyramadhan/qa/mobile/pages/Login.java` (will be renamed to `LoginPage.java` in Phase 3)
- `Selenium/Pages/ProductsList.java` → `com/kennyramadhan/qa/mobile/pages/ProductsList.java`
- `Selenium/Pages/ProductsDetail.java` → `com/kennyramadhan/qa/mobile/pages/ProductsDetail.java`
- `Selenium/Pages/CartCheckout.java` → `com/kennyramadhan/qa/mobile/pages/CartCheckout.java`
- `Selenium/CustomHelper/ConfigLoader.java` → `com/kennyramadhan/qa/core/config/ConfigLoader.java`
- `Selenium/CustomHelper/Utils.java` → DELETE (duplicate of TestListeners screenshot logic; mark for removal in Commit 2.6)
- `Selenium/CustomHelper/UtilsHelper.java` → `com/kennyramadhan/qa/core/waits/WaitHelpers.java` (rename pending Phase 3 split)
- `Extent/Listeners/*.java` → `com/kennyramadhan/qa/core/reporting/*.java`
- `TestNG/Mobile/BaseTest.java` → `com/kennyramadhan/qa/tests/mobile/BaseMobileTest.java`
- `TestNG/Mobile/e2eMobile.java` → `com/kennyramadhan/qa/tests/mobile/E2EMobileTest.java` (rename + class rename)

**Steps for each file:**
1. Update `package` declaration at top
2. Use git mv to preserve history: `git mv old/path new/path`
3. Update all `import` statements across the codebase that reference the old packages

**Tool tip:** Use grep to find all references:
```bash
grep -rn "import Appium\." src/
grep -rn "import Selenium\." src/
grep -rn "import Extent\." src/
grep -rn "import TestNG\." src/
```

**Update `testng.xml`** to reference the new test class:
```xml
<class name="com.kennyramadhan.qa.tests.mobile.E2EMobileTest">
```

**Verification:**
```bash
mvn clean compile -q && echo "OK"
```

---

### Commit 2.3: `refactor(config): move properties to src/main/resources and use classpath loading`

**Files:**
- `src/main/java/Selenium/Resources/config.properties` → `src/main/resources/config/config.properties`
- `src/main/java/Selenium/Resources/Android/appTest.apk` → `src/test/resources/apps/appTest.apk` (test fixture, not main resource)
- `src/main/java/Selenium/Resources/iOS/iosSimulator.app/` → `src/test/resources/apps/iosSimulator.app/`

**Update `ConfigLoader.java`:**

Replace the static initializer that uses `FileInputStream`:
```java
// OLD - rapuh, breaks if run from different cwd
FileInputStream fis = new FileInputStream("src/main/java/Selenium/Resources/config.properties");
```

With classpath-based loading:
```java
// NEW - works regardless of working directory
try (InputStream is = ConfigLoader.class.getClassLoader()
        .getResourceAsStream("config/config.properties")) {
    if (is == null) {
        throw new IllegalStateException("config.properties not found on classpath");
    }
    props.load(is);
} catch (IOException e) {
    throw new IllegalStateException("Failed to load config.properties", e);
}
```

**Replace UDID with placeholder.** In `config.properties`, change:
```properties
udid=70444EE4-3BE7-4CDD-9909-EC8BBA4A2E87
```
to:
```properties
udid=${UDID}
```

Update `ConfigLoader.get()` to resolve `${VAR_NAME}` placeholders from environment variables (fallback to property value if env var not set).

**Verification:**
```bash
mvn clean compile -q && echo "OK"
ls src/main/resources/config/config.properties
```

---

### Commit 2.4: `feat(config): add multi-environment support`

**Files:** Create
- `src/main/resources/config/config-local.properties` (copy of current config.properties, for local Appium runs)
- `src/main/resources/config/config-ci.properties` (CI-specific overrides — placeholder for now, mostly inherits)

**Update `ConfigLoader`:**
- Read `env` system property (`-Denv=local|ci|staging`)
- Default to `local`
- Load `config-{env}.properties` ON TOP of `config.properties` (override pattern)

**API:**
```java
ConfigLoader.get("platformName")  // resolves from env-specific first, then default
ConfigLoader.getEnvironment()     // returns "local", "ci", etc.
```

**Verification:**
```bash
mvn clean test-compile -q
mvn exec:java -Dexec.mainClass="com.kennyramadhan.qa.core.config.ConfigLoader" -Denv=local
# (only if a main method exists for testing; otherwise skip)
```

---

### Commit 2.5: `refactor(logging): replace System.out with SLF4J + Logback`

**Files:** All `.java` files containing `System.out.println` or `System.err.println` in `src/main/java`.

**Action:** For each class with `System.out` calls:
1. Add field: `private static final Logger log = LoggerFactory.getLogger(ClassName.class);`
2. Replace:
   - `System.out.println("...")` → `log.info("...")`
   - `System.err.println("...")` → `log.warn("...")` (or `log.error("...")` if it's an actual error)
3. Use parameterized logging where strings are concatenated:
   - Before: `System.out.println("Driver initialized for: " + deviceName);`
   - After: `log.info("Driver initialized for: {}", deviceName);`
4. Remove unicode emojis from log messages (e.g. `✅`, `⚠️`, `🛑`) — replace with `[OK]`, `[WARN]`, `[STOP]` text prefixes for cross-platform terminal compatibility.

**Create `src/main/resources/logback.xml`:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/test-execution.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/test-execution-%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>30</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="com.kennyramadhan.qa" level="DEBUG"/>
    <logger name="io.appium" level="WARN"/>
    <logger name="org.openqa.selenium" level="WARN"/>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

**Update `.gitignore`** to add `logs/` directory.

**Verification:**
```bash
grep -rn "System\.out\|System\.err" src/main/java/
# Should return ONLY 0 results (or only justified usage with explicit comment)
mvn clean compile -q && echo "OK"
```

---

### Commit 2.6: `chore: remove duplicate Utils.java and dead code blocks`

**Files to delete:**
- `Utils.java` (legacy, duplicated by TestListeners screenshot logic)

**Files to clean (remove dead code):**
- `BaseTest.java`: Remove the commented-out `performLogin()` method (lines 164-172 in original).
- `ProductsList.java`: Remove the commented-out validation block in `selectProducts()` method.
- Any other commented-out code blocks longer than 3 lines.

**Constraint:** Do NOT remove JavaDoc comments. Only code comments that contain actual disabled code.

**Verification:**
```bash
git ls-files | xargs grep -l "^//.*[A-Za-z].*;" | head
# Spot-check remaining files for any large commented-out blocks
mvn clean compile -q && echo "OK"
```

---

### Commit 2.7: `chore: fix Java 25 deprecations and replace Guava Iterables`

**Targets:**

1. **`Thread.currentThread().getId()`** → `Thread.currentThread().threadId()`
   - Files: `ExtentNode.java` (multiple occurrences)

2. **Guava `Iterables.get(list, index)`** → standard `list.get(index)`
   - File: `ProductsList.java` (lines 89, 135, 136)
   - Remove `import com.google.common.collect.Iterables;`

3. **JavaFaker → Datafaker rename:**
   - File: `e2eMobile.java` (now `E2EMobileTest.java` after Commit 2.2)
   - Change `import com.github.javafaker.Faker;` → `import net.datafaker.Faker;`
   - Faker API is largely compatible — verify `Faker faker = new Faker(new Locale("id_ID"));` still compiles. If `Locale` constructor is deprecated in Java 25, use `Locale.of("id", "ID")`.
   - Remove `@SuppressWarnings("deprecation")` annotations that are no longer needed.

4. **Other deprecations:** Run `mvn clean compile` and check warnings. Fix anything flagged as deprecated in Java 25.

**Verification:**
```bash
mvn clean compile -q -Dmaven.compiler.showDeprecation=true 2>&1 | grep -E "deprecat|warning" | head -20
# Should show 0 or only justified deprecations
```

---

### Commit 2.8: `style: enforce English-only comments and consistent naming`

**Action:** Walk through every modified file in this phase and:

1. **Translate Bahasa Indonesia comments to English.** Examples:
   - `// Inisialisasi page object sebelum setiap test dijalankan` → `// Initialize page objects before each test`
   - `// Ambil harga sebelum sorting` → `// Capture prices before sorting`
   - JavaDoc in Bahasa Indonesia → English equivalent

2. **Fix naming inconsistencies in JavaDoc:**
   - `@author Kenny` and `@author Kenny Ramadhan` → standardize to `@author Kenny Ramadhan`
   - Remove `@version` numbers (Git tracks versions; manual versioning is noise)

3. **Fix typos identified in code review:**
   - `"Environtment"` → `"Environment"` (in ExtentReportsManager — note: this file may be deleted in Phase 3 when migrating to Allure; if so, skip the fix)
   - `"Automation Sales4u"` → `"Mobile Automation Suite"` or similar (placeholder name from another project)
   - `"Portofilio"` in pom.xml `<name>` → `"Portfolio"`
   - `"getSuccesScreenshotPath"` → `"getSuccessScreenshotPath"`

4. **Update `pom.xml` URL:**
   - `<url>http://www.example.com</url>` → `<url>https://github.com/kennyRamadhan/selenium-java-testng-automation-portofolio</url>`

**Verification:** Visual review. No automated check.

---

## Phase-Specific Constraints

1. **Do not migrate ExtentReports to Allure yet.** That happens in Phase 3 (Mobile Refactor). Allure dependency is added to pom.xml in Commit 2.1 but no Allure annotations are used yet.
2. **Do not refactor PageFactory or page object internals.** Phase 3 handles that.
3. **Do not run the actual mobile tests.** No emulator needed.
4. **Each commit must compile** (`mvn clean compile -q` exit 0).
5. **Use `git mv` for file moves** to preserve history.

---

## Definition of Done (Per Phase)

After all 8 commits:
1. `mvn clean compile -q` succeeds
2. `mvn clean test-compile -q` succeeds
3. `grep -rn "System\.out\.println\|System\.err\.println" src/main/java/` returns 0 results
4. `grep -rn "package TestNG\|package Selenium\|package Appium\|package Extent" src/` returns 0 results
5. `grep -rn "import com.github.javafaker" src/` returns 0 results (replaced by Datafaker)
6. New package structure visible: `find src/main/java/com/kennyramadhan -type d`
7. `git log --oneline -10` shows 8 new commits with conventional format
8. Push to origin: `git push origin refactor/v2`

---

## Reporting Format

```
Phase 2 complete: Foundation Restructure

Commits:
- <SHA1> chore(build): upgrade pom.xml to Java 25 and modern dependencies
- <SHA2> refactor: migrate package to com.kennyramadhan.qa namespace
- <SHA3> refactor(config): move properties to src/main/resources and use classpath loading
- <SHA4> feat(config): add multi-environment support
- <SHA5> refactor(logging): replace System.out with SLF4J + Logback
- <SHA6> chore: remove duplicate Utils.java and dead code blocks
- <SHA7> chore: fix Java 25 deprecations and replace Guava Iterables
- <SHA8> style: enforce English-only comments and consistent naming

Verification:
- mvn clean compile: ✅
- mvn clean test-compile: ✅
- System.out usage: 0 occurrences
- Old package names: 0 occurrences
- New package tree: <list directories>

Pushed to: origin/refactor/v2

Concerns surfaced:
<list, or "none">

Ready for Phase 3 (Mobile Refactor).
```

---

## Stop Conditions

Stop and ask Kenny if:
- Maven dependency resolution fails after pom.xml upgrade
- Java 25 incompatibility surfaces in a transitive dependency (rare but possible)
- File rename creates a circular import or compile error you cannot resolve in 2 attempts
- Datafaker API differs from JavaFaker in a way that breaks existing code (uncommon)
- Any TestNG annotation or behavior changes between 7.8.0 → 7.10.2 that breaks BaseTest lifecycle

Do NOT proceed to Phase 3.

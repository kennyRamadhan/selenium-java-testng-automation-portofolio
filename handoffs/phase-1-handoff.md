# Phase 1 — Hot Fixes Pre-Refactor

**Goal:** Fix 8 critical bugs identified in code review so the baseline is functional before the major restructure in Phase 2.

**Total commits:** 3
**Estimated time:** 30-45 minutes

---

## Required Reading (Read in Order)

1. `CLAUDE.md` — agent rules, naming conventions, commit format, forbidden patterns
2. `MASTER_PLAN.md` — Section "Phase 1 — Hot Fixes Pre-Refactor (3 commits)"
3. `phase-0-summary.md` — what was already completed before this handoff

After reading, run these to confirm current state:
```bash
git status
git log --oneline -5
git branch --show-current
```

Expected: branch `refactor/v2`, working tree clean, HEAD at `53f0455` (`docs: add master plan for refactor/v2`).

---

## Commits to Create (in order)

### Commit 1.1: `fix(mobile): correct locked_out_user and problem_user locators`

**File:** `src/main/java/Selenium/Pages/Login.java`

**Bug:** Three locators (`stdUser`, `lockedUser`, `problemUser`) all use the XPath for `test-standard_user`. This means tests for `locked_out_user` and `problem_user` were silently clicking `standard_user` — passing for the wrong scenario.

**Action:** Update `lockedUser` to target `test-locked_out_user` and `problemUser` to target `test-problem_user`. Keep `stdUser` unchanged.

**Also:** Remove the dead reference to `TestNG.Mobile.Test` class in `testng.xml` (it doesn't exist and would throw `ClassNotFoundException`).

**Commit message body:**
```
The locators for lockedUser and problemUser were duplicated from stdUser,
causing test cases for these user types to silently click standard_user
instead. Tests passed for the wrong scenario.

Also removes dead reference to non-existent TestNG.Mobile.Test class
from testng.xml that would cause ClassNotFoundException on suite run.
```

**Files staged:** `src/main/java/Selenium/Pages/Login.java`, `testng.xml`

---

### Commit 1.2: `fix(reporting): prevent NPE in onTestSuccess when throwable is null`

**File:** `src/main/java/Extent/Listeners/TestListeners.java`

**Bug:** In `onTestSuccess()`, the code calls `node.pass(result.getThrowable())`. On test success, `getThrowable()` returns `null`. The fallback branch then calls `result.getThrowable().printStackTrace()` which is guaranteed NPE.

**Action:** Replace the throwable-based pass call with a descriptive string message that includes the test method name.

**Target block (around line 100-108):**
```java
ExtentTest node = ExtentNode.getNode();
if (node != null) {
    node.pass(result.getThrowable());
} else {
    System.out.println("[WARNING] ExtentNode.getNode() null. Logging to console instead.");
    result.getThrowable().printStackTrace();
}
```

**Replace with:**
```java
ExtentTest node = ExtentNode.getNode();
if (node != null) {
    node.pass("Test passed: " + result.getMethod().getMethodName());
} else {
    System.out.println("[WARNING] ExtentNode.getNode() null for: " + result.getMethod().getMethodName());
}
```

**Note:** `System.out.println` stays for now — it will be replaced by SLF4J in Phase 2. Don't refactor logging in this commit.

**Commit message body:**
```
ITestResult.getThrowable() returns null on test success, causing
ExtentTest.pass(null) to behave unpredictably and the fallback branch
to throw NPE on printStackTrace().

Replace with a descriptive pass message that includes the test method
name, matching the expected behavior of a success log entry.
```

---

### Commit 1.3: `chore(build): clean pom.xml duplicates and deprecated dependencies`

**File:** `pom.xml`

**Bugs:**
- `org.apache.poi:poi-ooxml` declared twice (line 39 & 73) — Maven warning
- `com.relevantcodes:extentreports:2.41.2` — deprecated lib from 2015, source of moderate severity vulnerability flagged by Dependabot
- `org.apache.directory.studio:org.apache.commons.io:2.4` — wrong coordinates, should be canonical `commons-io:commons-io`

**Action:** Replace the entire `<dependencies>` block (lines 22-77) with this cleaned version:

```xml
   <dependencies>
        <!-- Appium client (transitive: Selenium 4.x) -->
        <dependency>
            <groupId>io.appium</groupId>
            <artifactId>java-client</artifactId>
            <version>8.6.0</version>
        </dependency>

        <!-- TestNG -->
        <dependency>
            <groupId>org.testng</groupId>
            <artifactId>testng</artifactId>
            <version>7.8.0</version>
        </dependency>

        <!-- Apache POI (Excel data driver) -->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>5.2.5</version>
        </dependency>

        <!-- ExtentReports (will be replaced by Allure in Phase 3) -->
        <dependency>
            <groupId>com.aventstack</groupId>
            <artifactId>extentreports</artifactId>
            <version>5.1.1</version>
        </dependency>

        <!-- Apache Commons IO -->
        <dependency>
            <groupId>commons-io</groupId>
            <artifactId>commons-io</artifactId>
            <version>2.16.1</version>
        </dependency>

        <!-- JavaFaker (will be replaced by Datafaker in Phase 2) -->
        <dependency>
            <groupId>com.github.javafaker</groupId>
            <artifactId>javafaker</artifactId>
            <version>1.0.2</version>
        </dependency>
    </dependencies>
```

**Removed:**
- Duplicate `org.apache.poi:poi-ooxml`
- `com.relevantcodes:extentreports:2.41.2`
- `org.apache.directory.studio:org.apache.commons.io:2.4`

**Replaced:**
- commons-io coordinates fixed to `commons-io:commons-io:2.16.1`

**Intentionally NOT changed in Phase 1 (deferred to Phase 2):**
- TestNG version (still 7.8.0; will upgrade to 7.10.2 in Phase 2)
- Appium version (still 8.6.0; will upgrade to 9.3.0 in Phase 2)
- ExtentReports lib (will be replaced by Allure in Phase 3)
- JavaFaker (will be replaced by Datafaker in Phase 2)
- Java compiler version (still 17; will upgrade to 25 in Phase 2)

**Reason for minimal change:** Phase 1 is hot fixes only. Major version bumps belong in Phase 2 to keep risk isolated.

**Commit message body:**
```
- Remove duplicate poi-ooxml declaration
- Remove com.relevantcodes:extentreports:2.41.2 (deprecated 2015,
  source of moderate severity vulnerability flagged by Dependabot)
- Fix commons-io coordinates from org.apache.directory.studio mirror
  to canonical commons-io:commons-io:2.16.1

Modern stack migration (Allure, Datafaker, TestNG 7.10, Appium 9)
deferred to Phase 2 to keep this commit minimal-risk.
```

---

## Phase-Specific Constraints

1. **Do not run mobile tests.** No emulator is required for Phase 1 — only `mvn clean compile` to verify code compiles. Skip any test execution.
2. **Do not modernize logging.** `System.out.println` stays as-is. Phase 2 handles SLF4J migration.
3. **Do not rename packages.** Package structure (`TestNG.Mobile`, `Selenium.Pages`, etc.) stays as-is. Phase 2 handles the migration.
4. **Do not upgrade dependency versions** beyond what's specified above. Modernization is Phase 2's scope.
5. **One commit per logical fix.** Do not bundle multiple fixes into one commit.
6. **Verify compile after EACH commit**, not just at the end.

---

## Definition of Done (per Commit)

After each commit:
1. `mvn clean compile -q` returns exit code 0 (no errors, no new warnings)
2. Commit message follows conventional commits format (see CLAUDE.md)
3. Commit body includes context paragraph explaining the why

After all 3 commits:
1. `git log --oneline -5` shows 3 new commits with conventional format messages
2. `mvn dependency:tree -q` does NOT contain `com.relevantcodes:extentreports`
3. `mvn dependency:tree -q` shows `commons-io:commons-io:jar:2.16.1` (not `org.apache.directory.studio`)
4. `mvn dependency:tree -q` shows `org.apache.poi:poi-ooxml` exactly once
5. Push to origin: `git push origin refactor/v2`

---

## Reporting Format (After Phase Complete)

Report back to Kenny with:

```
Phase 1 complete: Hot Fixes Pre-Refactor

Commits:
- <SHA1> fix(mobile): correct locked_out_user and problem_user locators
- <SHA2> fix(reporting): prevent NPE in onTestSuccess when throwable is null
- <SHA3> chore(build): clean pom.xml duplicates and deprecated dependencies

Verification:
- mvn clean compile: ✅ success
- mvn dependency:tree filter: ✅ extentreports legacy removed, commons-io canonical, poi-ooxml unique

Pushed to: origin/refactor/v2

Concerns surfaced (if any):
<list, or "none">

Ready for Phase 2.
```

---

## Stop Conditions

Stop and ask Kenny if:
- Compilation fails after any commit
- Locator IDs `test-locked_out_user` or `test-problem_user` don't actually exist in the SwagLabs app (check by inspecting the existing test that calls `getAutoCredentials("locked_out_user")` — if no such call exists yet in the test code, that's still fine; the locators will be exercised in Phase 3)
- Maven dependency resolution fails (unlikely, but possible if local Maven cache is corrupted)
- Any other ambiguity in the spec

Do NOT proceed to Phase 2 automatically. Phase 2 is a separate handoff.

# Agent Execution Rules — Java QA Refactor v2

This document defines how AI coding agents (Claude Code, Cursor, etc.) should
operate inside this repository.

## Core Principles

1. **One task at a time.** Never bundle multiple unrelated changes into a single commit.
2. **No apology loops.** State the action, take the action, report the result.
3. **Direct pushback.** If the requested approach is wrong, say so with reasoning before complying.
4. **No probability estimates without evidence.** "It might work" is not acceptable; either verify or state the unknown explicitly.
5. **Verify before declaring done.** Run `mvn clean compile` before claiming a refactor compiles.

## Repository Conventions

### Package Structure
All production code lives under `com.kennyramadhan.qa.*`. Subpackages:
- `core` — shared infra (config, driver, reporting, retry, waits)
- `api` — REST API automation (RestAssured)
- `mobile` — Mobile automation (Appium)
- `web` — Web automation (Selenium)

Test code mirrors production under `com.kennyramadhan.qa.tests.*`.

### Naming
- Classes: `PascalCase`, test classes end with `Test` (e.g. `LoginMobileTest`).
- Methods: `camelCase`, test methods describe behavior (e.g. `shouldRejectInvalidCredentials`).
- Constants: `UPPER_SNAKE_CASE`.
- Locator constants: `private static final By LOGIN_BTN = ...`.

### Java Version
Java 25 LTS. Use modern features where they improve clarity:
- `record` for DTOs/POJOs
- `var` for local variables when type is obvious from RHS
- Pattern matching for `switch` and `instanceof`
- Text blocks for multi-line strings

### Logging
Use SLF4J. Never `System.out.println` or `System.err.println`.
```java
private static final Logger log = LoggerFactory.getLogger(MyClass.class);
log.info("Action complete: {}", result);
```

### Reporting
Allure only. ExtentReports is removed. Use `Allure.step()` and `@Step` annotation.

### Page Objects
- No `PageFactory.initElements()`. Use locator constants + explicit `WebDriverWait`.
- Page objects return data/state. Assertions live in test classes only.
- Each page has a `BaseMobilePage` or `BaseWebPage` parent.

### Test Design
- Tests are independent. No `priority` ordering.
- Setup/teardown in `BaseTest` hierarchy, not duplicated per test.
- Use `@DataProvider` for parameterized scenarios.

## Commit Convention in root project

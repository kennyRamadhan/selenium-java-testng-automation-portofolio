# Architecture

Design decisions and rationale for the QA automation framework. This is the place to look up *why* something is built the way it is. The [README](../README.md) describes *what* the framework does; this document explains *why*.

---

## Module structure

```
src/main/java/com/kennyramadhan/qa/
├── core/
│   ├── config/      ConfigLoader (env-overlay properties)
│   ├── driver/      DriverManager (mobile), WebDriverFactory (web)
│   └── reporting/   LogHelper (Allure shim), TestListeners
├── api/
│   ├── client/      BaseApiClient, ApiConfig
│   ├── endpoints/   AuthApi, ProductsApi, BrandsApi, UserApi
│   └── models/      Product, Brand, UserDetails, ApiResponse, ...
├── web/
│   ├── client/      WebConfig
│   ├── pages/       BaseWebPage, HomePage, SignupLoginPage, ...
│   └── models/      AccountForm, PaymentDetails
└── mobile/
    ├── pages/       BaseMobilePage, Login, ProductsList, ...
    └── server/      AppiumServerManager

src/test/java/com/kennyramadhan/qa/tests/
├── api/             BaseApiTest, AuthApiTest, ProductsApiTest, ...
├── web/             BaseWebTest, LoginWebTest, ...
└── mobile/          BaseMobileTest, E2EMobileTest
```

The three test layers (mobile / web / API) are siblings under the `qa` root. They share `core/config` and `core/reporting` but each owns its own driver/client lifecycle. There is no single mega-base-test class — the inheritance trees stay flat per layer so a change to web doesn't ripple into mobile.

---

## ADR 1 — Allure over ExtentReports

**Decision:** Use Allure 2.29 as the sole reporting backend. Drop ExtentReports.

**Context:** The pre-refactor codebase used ExtentReports 5 with hand-rolled `ExtentNode` and `ExtentReportsManager` singletons. Two friction points:

1. ExtentReports has no first-class TestNG SPI integration — every project ends up wiring listeners by hand. We had a custom `TestListeners` doing screenshot capture + node creation + flush, intermingled.
2. ExtentReports outputs an `.html` file. Publishing to a public report URL meant uploading the artifact somewhere or coding a custom CI step.

**Decision rationale:** Allure ships `META-INF/services/org.testng.ITestNGListener` in `allure-testng.jar`. Drop the JAR on the classpath, TestNG auto-discovers the listener via SPI. No `@Listeners` annotation, no `<listeners>` block in suite XML. Allure also has a published GitHub Pages workflow pattern (`simple-elf/allure-report-action` + `peaceiris/actions-gh-pages`) that turns the report into a versioned URL with rolling history.

**Consequences:**
- The custom `TestListeners` is now ~50 lines (just screenshot capture + step-counter reset). Allure handles status reporting itself.
- `LogHelper` retains its public API (`step`, `detail`, `pass`, `fail`, `warning`) but internals are a thin Allure shim. Existing mobile page-object callers (51 sites in `CartCheckout`, `Login`, `ProductsList`) continue compiling unchanged.
- `LogHelper.detail()` semantics degrade slightly: under ExtentReports it nested as a child log under the active step; under Allure it emits a sibling step. Documented in the `LogHelper.detail` JavaDoc; future redesign will rename to `AllureLogger` with proper step nesting via `Allure.step(name, ThrowableRunnable)` lambdas.

**Implementation note — AspectJ runtime weaving:**

allure-java's @Step annotation processing is implemented as runtime
AOP via aspectjweaver. For @Step-annotated methods to actually
produce step nodes in the Allure report, the aspectjweaver javaagent
must be loaded into the JVM at test time.

The repo wires this via maven-surefire-plugin's `<argLine>`:

    -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/1.9.25/aspectjweaver-1.9.25.jar"

Plus the `org.aspectj:aspectjweaver:1.9.25` dependency declaration.
Version 1.9.25 is pinned because earlier AspectJ releases lack
Java 25 class file compatibility (major version 69) and crash with
`Unsupported class file major version 69` at agent load.

Without this wiring, @Step annotations compile and resolve normally
but produce no runtime effect — the resulting Allure trace shows
empty `steps:[]` arrays. The wiring was added in the post-v2.0.0
hotfix series (commit ac78625) after live dashboard inspection
surfaced the gap.

**Convention — @Step on state-mutating methods only:**

Page object public methods that mutate driver/page state (clicks,
form input, navigation) carry @Step with business-intent
descriptions. Read-only getters and state-readers (e.g.
`getCartItemCount()`, `isOrderConfirmedMessageDisplayed()`) do NOT
carry @Step — adding them would produce noisy "Get cart item
count" entries between meaningful actions in the Allure trace.

This convention applies to BaseApiClient methods and endpoint
client methods symmetrically: HTTP-issuing methods carry @Step;
internal helpers do not.

---

## ADR 2 — No PageFactory in page objects

**Decision:** Page objects declare `private static final By` locator constants and call helper methods on a `BaseWebPage` / `BaseMobilePage` parent. No `@FindBy` annotations, no `PageFactory.initElements`.

**Context:** The pre-refactor mobile page objects used `PageFactory` with `@AndroidFindBy` + `@iOSXCUITFindBy` annotations on `WebElement` fields. Two problems surfaced repeatedly:

1. **Stale element churn.** PageFactory proxies a `WebElement` reference at field-init time. When a test re-navigates to the same page, the proxy refers to a detached DOM/UI element. We added `try { ... } catch (StaleElementReferenceException e) { reinit }` boilerplate everywhere.
2. **Locator-bug debugging is painful.** A typo in `@FindBy` doesn't fail until first interaction with that field. The error chain runs through PageFactory's reflection layer — stack traces are 30+ frames before reaching the actual locator string.

**Decision rationale:** Plain `By` constants are easier to grep (`grep -rn "By.id" src/`), refactor (rename a constant, IDE catches every reference), and debug (stack traces point directly at `driver.findElement(LOGIN_BTN)`). The cost is one extra method call per interaction (`safeClick(LOCATOR)` vs `loginButton.click()`) — negligible compared to network round-trips in browser/device automation.

**Consequences:**
- `BaseWebPage` / `BaseMobilePage` expose helpers: `waitForVisible(By)`, `safeClick(By)`, `safeSendKeys(By, String)`, `getText(By)`, `isDisplayed(By)`. Helpers are deliberately not `@Step`-annotated; the `@Step` layer lives on the page object methods that call them, keeping the Allure trace business-focused (see ADR-1 convention).
- Implicit waits are disabled (`Duration.ZERO`); helpers use explicit `WebDriverWait`. Mixed implicit + explicit waits compound unpredictably — picking one was a clarity win.
- Mobile page objects under `com.kennyramadhan.qa.mobile.pages` still use the legacy PageFactory pattern as an interim state. They will be migrated to the same `By`-constants-plus-helpers shape when the mobile test target moves off the SauceLabs SwagLabs demo.

---

## ADR 3 — ThreadLocal driver

**Decision:** Both `DriverManager` (mobile) and `WebDriverFactory` (web) hold a `ThreadLocal<Driver>` field. Tests run with TestNG `parallel="methods"`.

**Context:** The framework supports parallel test execution. Two threads must not share a driver instance — interactions on one thread's session would race with the other's.

**Alternatives considered:**
- **Per-test driver injection.** Pass driver into every page-object constructor and assertion helper. Verbose; every signature changes when driver lifecycle moves.
- **A test fixture object passed everywhere.** Same verbosity issue; still needs thread isolation.
- **Driver-per-process via subprocess fork.** TestNG has no native model for this; tooling overhead.

**Decision rationale:** `ThreadLocal` is the simplest correct primitive. `BeforeMethod` sets the slot, `AfterMethod` quits + clears it. Page objects construct themselves from the current thread's slot. Per-thread isolation is automatic; no per-method plumbing.

**Consequences:**
- `WebDriverFactory.getDriver()` returns null if called outside a test method's lifecycle. Page objects throw `IllegalStateException` with a clear message when this happens.
- `quitDriver()` must `remove()` after `quit()` to avoid leaking the slot in long-running JVMs (Maven Surefire forks per test class but reuses threads within).
- Mobile and web layers have separate ThreadLocal fields rather than a shared one, because `AppiumDriver extends WebDriver`-but-not-quite-the-same-shape. Two factories keep types crisp.

---

## ADR 4 — Cross-layer cleanup (web → API)

**Decision:** Web tests that register a new account via the AE.com signup UI delete that account in `@AfterMethod` via the `AuthApi.deleteAccount` REST client.

**Context:** `RegisterUserWebTest.shouldRegisterNewUserEndToEnd` creates a fresh user account through the live web flow. Without cleanup, every CI run leaves an orphan account on automationexercise.com. AE.com does expose a "delete account" web flow, but it requires login first and modal-clicking — flaky and slow.

**Decision rationale:** Within a single repository, code reuse across layers is allowed when the cheaper path beats the same-layer path. AE's `DELETE /api/deleteAccount` is idempotent, fast (single HTTP request), and works regardless of the web session state. Reusing `AuthApi.deleteAccount` from a web test is a deliberate cross-layer dependency, not a leak.

**Consequences:**
- The web test class JavaDoc explicitly documents the dependency: "if `AuthApi.deleteAccount` signature changes, this test must be updated."
- The `@AfterMethod` cleanup is idempotent — guards against null `registeredEmail` so a test failure before email generation doesn't blow up the cleanup itself.
- Symmetric pattern in API tests: `AuthApiTest` sets `createdEmail`/`createdPassword` fields; `@AfterMethod` calls `deleteAccount` if non-null. Both layers use the same shape.

---

## ADR 5 — Multi-environment config strategy

**Decision:** `ConfigLoader` loads `config/config.properties` (base) and overlays `config/config-{env}.properties` where `env` comes from `-Denv=local|ci|staging` (default: `local`). Values matching `${VAR_NAME}` are resolved from environment variables at lookup time.

**Context:** The framework targets multiple environments. Hardcoding URLs / credentials per branch is a ticking time bomb. Three needs:

1. Per-environment overrides for things that genuinely differ (base URL, timeouts, parallel thread count).
2. Secrets that should never be committed (UDID, API keys).
3. A way for CI to override values without editing files.

**Decision rationale:** Properties-overlay pattern is well-understood (Spring's `PropertyPlaceholderConfigurer` does the same thing). The `${VAR}` resolution is implemented in 6 lines inside `ConfigLoader.resolvePlaceholder` — far simpler than pulling in a config library.

**Consequences:**
- `config-local.properties` and `config-ci.properties` are scaffolded as empty files committed to the repo. They're populated as actual divergence accumulates rather than pre-filled with imagined defaults.
- Secret values use `${VAR_NAME}` placeholders in `config.properties` (e.g. `udid=${UDID}`). Local dev exports the env var; CI sets it via repo secrets.
- The `-Denv=` system property is read in `ConfigLoader`'s static initializer — once per JVM. Tests cannot switch env mid-suite. This is intentional; environment switching mid-test would break the implicit "all values come from one place" contract.

---

## ADR 6 — Retry & flake handling

**Decision:** TestNG's `IRetryAnalyzer` is wired up at the suite level for the web suite only.

**Context:** Web tests are flakier than API tests. Network timeouts, AE.com modal-render races, headless-Chrome paint timing — none are bugs in our code, all are real signal that retry can absorb.

**Status:** Concretized in Phase 3 commit 5 for the mobile suite via `MobileRetryAnalyzer` + `MobileRetryListener` (in `com.kennyramadhan.qa.core.listeners`). Web/API suites still run without retry.

**Plan:**
- `RetryAnalyzer` retries each test up to 1 time on failure (so total of 2 attempts max).
- Registered in `testng-web.xml` as a `<listener>` only — not in `testng-api.xml` (API tests should be deterministic; flake there is a real bug).
- Retried tests emit a custom Allure label so the report can flag them visually.

---

## ADR 7 — Parallel execution strategy

**Decision:**
- `testng.xml` (mobile): no parallel directive (single device).
- `testng-api.xml`: `parallel="classes" thread-count="3"`.
- `testng-web.xml`: `parallel="methods" thread-count="2"`.

**Rationale:**
- API tests parallelize by class so each class's `@AfterMethod` cleanup runs sequentially within the class. AE.com tolerates 3 concurrent HTTP clients without rate-limiting.
- Web tests parallelize by method but with lower thread count (2). Each browser session takes ~5-15s of memory + CPU; 3+ headless Chromes in parallel on a Maven Surefire forked JVM was observed to cause occasional driver-launch timeouts on modest hardware.
- Mobile is single-threaded by definition (one device, one session).

**Consequences:** ThreadLocal driver pattern (ADR 3) is the load-bearing primitive that makes any of this safe. Without it, parallel modes would race on shared driver state.

---

## ADR 8 — RestAssured default JSON parser

**Decision:** `BaseApiClient` sets `RestAssured.defaultParser = Parser.JSON` in a static initializer.

**Context:** automationexercise.com returns JSON response bodies with `Content-Type: text/html; charset=utf-8` instead of `application/json`. RestAssured refuses to deserialize a response into a Java object via `.extract().as(Class)` when the Content-Type doesn't match a known JSON parser, throwing `IllegalStateException` with a "Cannot parse content type" message.

**Decision rationale:** The single-line static initializer registers JSON as the default parser for any unrecognized Content-Type. Three alternatives were considered and rejected:

- **Per-request Accept header override.** Requires touching every request builder, no centralization.
- **Custom RestAssured filter.** Heavier than a one-liner, complicates `AllureRestAssured` filter ordering.
- **Wrapper deserialization layer.** Rebuilds what RestAssured already does.

**Consequences:**
- The mutation is JVM-global. `RestAssured.defaultParser` is a static field on the `io.restassured.RestAssured` class, not a per-spec config. Every RestAssured-based request in the JVM is affected, not just `BaseApiClient` subclasses.
- Acceptable today because automationexercise.com is the sole RestAssured target. If a future phase introduces RestAssured against an API with proper Content-Type headers, this should be revisited — either scoped per `RequestSpecification`, or restricted via a filter that only applies to AE-bound requests.
- Removing the static initializer breaks API tests with no compile-time warning. Annotated in `BaseApiClient` JavaDoc to flag this.

---

## ADR 9 — Selenium 4.27 explicit pin + Appium transitive exclusion

**Decision:** `pom.xml` declares `selenium-java` 4.27.0 explicitly and adds an `<exclusion>` block on the Appium java-client dependency to remove its transitive `selenium-*` dependencies.

**Context:** Appium java-client 8.6 transitively pulls Selenium 4.13. The web layer needs Selenium 4.27 (modern explicit-wait API, devtools v131). Without intervention, Maven's nearest-wins rule does pick 4.27 when explicitly declared — but the legacy Appium 4.13 binding remains on the classpath as transitive. Two Selenium versions on the classpath risk class-loading ambiguity (which `selenium-api.jar` is consulted at runtime?).

**Decision rationale:** Explicit pin makes the version intent visible in `pom.xml`. Excluding Appium's transitive Selenium guarantees a single Selenium present on the classpath. Verified during the web-layer rollout: Appium java-client 8.6 bytecode compiles and runs cleanly against the upgraded Selenium 4.27 API surface — backwards-compatible. No Selenium API removals between 4.13 and 4.27 affect the methods Appium calls.

**Consequences:**
- Mobile tests (which depend on Appium) work with Selenium 4.27 transparently. The eventual mobile refactor will benefit from modern Selenium APIs without a separate dep upgrade.
- If Appium java-client is upgraded to 9.x in a future phase, the exclusion block must be re-validated — Appium 9 may pull a different Selenium that should not be excluded.
- The exclusion is documented inline in `pom.xml` as an XML comment so future contributors don't strip it as "unused".

---

## Reporting architecture

```
Test method
   ├──> @Step page object call          → Allure step trace
   ├──> AssertJ assertion (in test)     → fail signal flows to AllureTestNg listener
   └──> @AfterMethod
         ├──> screenshot on failure      → Allure attachment
         └──> driver.quit()              → ThreadLocal cleared
                                            ↓
target/allure-results/*.json + attachments
                                            ↓
mvn allure:serve            (local)
.github/workflows/allure-publish.yml + actions/gh-pages → gh-pages branch
                                            ↓
GitHub Pages → public URL
```

Allure result JSON is emitted by `allure-testng`'s SPI listener; each `@Step` annotation contributes a step node. Attachments (screenshots, request/response traces from `AllureRestAssured`) bind to the surrounding step. The reporting pipeline is purely additive — `target/allure-results/` is gitignored and rebuilt every run.

---

## Open questions

- **CDP version warnings.** Selenium 4.27 ships `selenium-devtools-v131`; Chrome 148+ exposes a newer CDP version. Tests don't use CDP features so the warnings are cosmetic — but if performance-tracing or network-interception capabilities become required, pin a matching `selenium-devtools-vNNN` dep.
- **BrowserStack mobile profile.** Web has a `browserstack` Maven profile. An equivalent mobile profile is still TBD; SauceLabs has a different capability model than BrowserStack and the design hasn't been ratified.
- **Allure native logger.** The `LogHelper.detail()` flat-step degradation (sibling instead of child) is acceptable for the current reporting fidelity but isn't ideal. A redesigned `AllureLogger` with proper step nesting via lambdas is queued for a future refactor.

---

## Coverage of trigger checklist

This document covers the design topics agreed in the Phase 6 plan:

- ✓ Allure SPI auto-discovery (ADR 1)
- ✓ LogHelper as compatibility shim (ADR 1 consequences)
- ✓ ThreadLocal driver pattern (ADR 3)
- ✓ Cross-layer cleanup (ADR 4)
- ✓ Multi-environment config (ADR 5)
- ✓ ADR-style framing across all 9 ADRs
- ✓ RestAssured.defaultParser = Parser.JSON (ADR 8)
- ✓ Selenium 4.27 + Appium transitive exclusion (ADR 9)

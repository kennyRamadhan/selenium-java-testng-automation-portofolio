# Getting Started

Local development walkthrough. Targets first-time contributors getting the test suites running on their own machine.

For *what* the framework does, see the [README](../README.md). For *why* it's built this way, see [ARCHITECTURE.md](ARCHITECTURE.md).

---

## Prerequisites

### Java 25 LTS (Temurin)

```bash
# Windows (Scoop)
scoop install temurin25-jdk

# macOS (Homebrew)
brew install --cask temurin@25

# Linux (Debian/Ubuntu)
sudo apt update && sudo apt install -y temurin-25-jdk

# Verify
java -version    # → openjdk version "25" ...
```

### Maven 3.9+

```bash
# Windows (Scoop)
scoop install maven

# macOS (Homebrew)
brew install maven

# Linux
sudo apt install -y maven

# Verify
mvn -version     # → Apache Maven 3.9.x ...
```

### Browser (web tests only)

Chrome, Firefox, or Edge. WebDriverManager auto-resolves the matching driver binary on first run and caches under `~/.cache/selenium/`. No manual chromedriver download.

### Appium + emulator (mobile tests only)

Mobile tests target the SauceLabs SwagLabs demo app. The mobile suite is excluded from default CI; running it locally requires:

- Appium Server 2.x: `npm install -g appium`
- For Android: Android Studio + an emulator AVD or a USB-connected device
- For iOS: macOS host + Xcode + iOS simulator

Mobile execution is not the primary use case for this framework. Skip this section if you only want to run web + API.

---

## Clone and first build

```bash
git clone https://github.com/kennyRamadhan/selenium-java-testng-automation-portofolio.git
cd selenium-java-testng-automation-portofolio
mvn clean compile
```

First build downloads ~80 MB of Maven dependencies (Selenium, RestAssured, Jackson, Allure, etc.) and caches them under `~/.m2/repository/`. Subsequent builds reuse the cache.

### Install local git hooks (optional but recommended)

```bash
bash scripts/install-hooks.sh
```

This copies the tracked hooks from `.githooks/` to `.git/hooks/`:

- `pre-commit` — runs `mvn -q spotless:check` and aborts the commit if the formatter would change anything. Run `mvn spotless:apply` to fix.
- `commit-msg` — blocks commit messages containing AI-attribution lines (`Co-Authored-By: Claude`, etc.). Author convention for this repo.

Hooks are opt-in. Skipping them does not break the build, but CI may flag formatting issues.

---

## Running tests

### API smoke (3 tests, ~10s)

```bash
mvn test -P api -Dgroups=smoke
```

Read-only public AE.com endpoints. Always passes if AE.com is reachable. Use this to verify your local Maven + Java setup.

### API full (15 tests, ~30s)

```bash
mvn test -P api
```

Includes account-lifecycle tests (create, update, delete) tagged `regression`. Each registers a unique Datafaker email and self-cleans via `@AfterMethod`.

### Web smoke (4 tests, ~80s, headless)

```bash
mvn test -P web -Dgroups=smoke -Dheadless=true
```

Login page, products listing, search, cart-add. Headless required for repeatability — headed mode is fine for debugging but introduces timing variance.

### Web full (8 tests)

```bash
mvn test -P web -Dheadless=true
```

Adds product-details navigation, cart inspection, end-to-end account registration (cross-layer cleanup via API — see [ARCHITECTURE.md ADR-4](ARCHITECTURE.md#adr-4--cross-layer-cleanup-web--api)).

### Web headed (debugging)

```bash
mvn test -P web -Dgroups=smoke
```

Drop `-Dheadless=true` to see Chrome render. Pair with breakpoints in your IDE for step-through debugging.

### Mobile (default suite)

```bash
mvn test
```

Requires Appium server running on `http://localhost:4723` and a connected device or running emulator. The default `testng.xml` at repo root drives the mobile suite. Configure device-specific values in `src/main/resources/config/config.properties` (or override via `-Dudid=...` etc.).

### BrowserStack cloud (web)

```bash
export BROWSERSTACK_USERNAME=<your-username>
export BROWSERSTACK_ACCESS_KEY=<your-access-key>
mvn test -P web,browserstack -Dbrowser=chrome
```

The `browserstack` profile sets `web.target=browserstack` via Surefire system properties; `WebDriverFactory.create()` detects this and dispatches to a `RemoteWebDriver` pointed at BrowserStack's hub. Credentials live in env vars only — never commit them.

---

## Viewing reports

### Local Allure

```bash
mvn allure:serve
```

Opens a local web server (random port) with the report rendered from the latest `target/allure-results/` directory. Refreshes when you re-run.

### Published Allure

After every push to `refactor/v2` or `main`, the `allure-publish.yml` workflow runs both API and Web suites and publishes the merged report to GitHub Pages:

[https://kennyRamadhan.github.io/selenium-java-testng-automation-portofolio/](https://kennyRamadhan.github.io/selenium-java-testng-automation-portofolio/)

The first publish requires a one-time setup: repo Settings → Pages → Source: gh-pages branch.

---

## IDE setup

### IntelliJ IDEA

1. **Import:** File → New → Project from Existing Sources → select repo root → "Import project from external model" → Maven.
2. **JDK:** File → Project Structure → Project → SDK: select Temurin 25.
3. **Run a test:** right-click `src/test/java/com/kennyramadhan/qa/tests/api/ProductsApiTest.java` → Run. To run a profile, edit the run config: Maven → Profiles: `api`.
4. **Allure plugin (optional):** Plugins → Allure Test Results → adds an "Allure" run gutter that opens the report after the test completes.

### Eclipse

1. **Import:** File → Import → Maven → Existing Maven Projects → select repo root.
2. **JDK:** Window → Preferences → Java → Installed JREs → add Temurin 25 and set as default.
3. **TestNG plugin:** Help → Eclipse Marketplace → search "TestNG" → install. Right-click a test class → Run As → TestNG Test.
4. **For Maven profile activation:** Run As → Maven test → Profiles: `api` (or `web`).

---

## Common pitfalls

### Compile fails on first run

Verify `java -version` reports 25. The pom is pinned to `<maven.compiler.source>25` and `<target>25`. Older JDKs will fail with `unsupported class file major version`.

### Web smoke fails with "no such element" on the cart modal

AE.com renders a `#cartModal` confirmation dialog after clicking `.add-to-cart` on a product card. The dialog blocks subsequent navigation. `ProductsPage.addProductToCartByIndex` waits for and dismisses the modal automatically — but if AE.com changes the modal markup, the locator may break. Inspect the actual page source via the failure-screenshot Allure attachment.

### CDP version warnings during web run

```
WARNING: Unable to find CDP implementation matching 148
```

Cosmetic. Selenium 4.27 ships CDP bindings up to v131; Chrome 148+ exposes a newer protocol version. Tests don't use CDP features so the warning is harmless. To silence, pin a matching `selenium-devtools-vNNN` dep — but only if you actually need CDP (network interception, performance metrics).

### Maven cache corruption

```bash
# Nuke the Maven cache and rebuild from scratch
rm -rf ~/.m2/repository/io/qameta ~/.m2/repository/org/seleniumhq
mvn clean compile
```

Targeted purge of two namespaces is usually enough. Full `rm -rf ~/.m2/repository` works but takes ~5 minutes to repopulate.

### `mvn allure:serve` opens a blank report

Allure caches results under `target/allure-results/` from the most recent test run. If you ran `mvn clean` after the test, the directory is empty. Re-run a test, then `mvn allure:serve` (do not `clean` between).

### Hooks not running on Windows

The `.git/hooks/` scripts are POSIX shell. Git for Windows ships Git Bash which runs them; if you cloned with a non-Git client (e.g. Visual Studio's "Open from GitHub" wizard) the bash interpreter may not be on PATH. Reinstall Git for Windows from [git-scm.com](https://git-scm.com/) and ensure "Git Bash" is selected during install.

---

## What to read next

- [ARCHITECTURE.md](ARCHITECTURE.md) — design decisions and ADRs
- [CONTRIBUTING.md](../CONTRIBUTING.md) — branch naming, commit format, PR template

# Master Plan: Refactor v2

**Repo:** `selenium-java-testng-automation-portofolio`
**Branch:** `refactor/v2`
**Java:** 25 LTS (Temurin)
**Build:** Maven 3.9.15
**Owner:** Kenny Ramadhan
**Start:** 5 May 2026

---

## 1. Target Akhir (After Phase 5)

### Tech Stack Final

| Layer | Tool | Version | Catatan |
|---|---|---|---|
| Language | Java | 25 LTS | Pattern matching for switch + primitive types stable |
| Build | Maven | 3.9.15 | Multi-profile (local/browserstack/saucelabs) |
| Test framework | TestNG | 7.10.2 | Parallel methods, retry analyzer |
| Mobile | Appium java-client | 9.3.0 | Drop deprecated APIs |
| Web | Selenium | 4.27.0 | + WebDriverManager 5.9.2 |
| API | RestAssured | 5.5.0 | + Jackson 2.18.1 untuk JSON |
| Reporting | Allure | 2.29.0 | Drop ExtentReports total |
| Logging | SLF4J + Logback | 2.0.16 + 1.5.12 | Drop System.out |
| Assertion | AssertJ | 3.26.3 | Lebih fluent dari TestNG asserts |
| Test data | Datafaker | 2.4.2 | Replace JavaFaker (deprecated) |
| Code quality | Spotless + SpotBugs | latest | Auto-format + static analysis |
| CI | GitHub Actions | n/a | Allure publish ke gh-pages |
| Demo target | practicesoftwaretesting.com | n/a | Web + API |
| Demo target Mobile | SauceLabs SwagLabs app | n/a | Existing target |

### Struktur Folder Final

```
selenium-java-testng-automation-portofolio/
├── .github/
│   └── workflows/
│       ├── ci.yml                          # Run tests on PR
│       └── allure-publish.yml              # Publish report ke gh-pages
├── docs/
│   ├── ARCHITECTURE.md                     # Design decisions
│   ├── GETTING_STARTED.md                  # Local setup
│   └── images/                             # Diagram, screenshots
├── src/
│   ├── main/
│   │   ├── java/com/kennyramadhan/qa/
│   │   │   ├── core/
│   │   │   │   ├── config/
│   │   │   │   │   ├── ConfigLoader.java
│   │   │   │   │   └── EnvironmentConfig.java
│   │   │   │   ├── driver/
│   │   │   │   │   ├── DriverManager.java       # Universal (web + mobile)
│   │   │   │   │   ├── MobileDriverFactory.java
│   │   │   │   │   └── WebDriverFactory.java
│   │   │   │   ├── reporting/
│   │   │   │   │   ├── AllureLogger.java        # Replace LogHelper
│   │   │   │   │   └── ScreenshotAttacher.java
│   │   │   │   ├── retry/
│   │   │   │   │   ├── RetryAnalyzer.java
│   │   │   │   │   └── RetryListener.java
│   │   │   │   └── waits/
│   │   │   │       └── WaitHelpers.java         # Replace tapByCoordinates
│   │   │   ├── api/
│   │   │   │   ├── client/
│   │   │   │   │   └── BaseApiClient.java       # RestAssured wrapper
│   │   │   │   ├── endpoints/
│   │   │   │   │   ├── AuthApi.java
│   │   │   │   │   └── ProductsApi.java
│   │   │   │   └── models/                      # Java records
│   │   │   │       ├── LoginRequest.java
│   │   │   │       ├── LoginResponse.java
│   │   │   │       └── Product.java
│   │   │   ├── mobile/
│   │   │   │   ├── pages/
│   │   │   │   │   ├── BaseMobilePage.java
│   │   │   │   │   ├── LoginPage.java
│   │   │   │   │   ├── ProductsListPage.java
│   │   │   │   │   ├── ProductDetailPage.java
│   │   │   │   │   └── CartCheckoutPage.java
│   │   │   │   └── server/
│   │   │   │       └── AppiumServerManager.java
│   │   │   └── web/
│   │   │       └── pages/
│   │   │           ├── BaseWebPage.java
│   │   │           ├── HomePage.java
│   │   │           ├── LoginPage.java
│   │   │           ├── ProductsPage.java
│   │   │           └── CheckoutPage.java
│   │   └── resources/
│   │       ├── logback.xml
│   │       ├── allure.properties
│   │       └── config/
│   │           ├── config.properties           # Default
│   │           ├── config-local.properties
│   │           └── config-ci.properties
│   └── test/
│       ├── java/com/kennyramadhan/qa/tests/
│       │   ├── BaseTest.java                   # Shared lifecycle
│       │   ├── api/
│       │   │   ├── BaseApiTest.java
│       │   │   ├── AuthApiTest.java
│       │   │   └── ProductsApiTest.java
│       │   ├── mobile/
│       │   │   ├── BaseMobileTest.java
│       │   │   ├── LoginMobileTest.java
│       │   │   ├── CartMobileTest.java
│       │   │   └── CheckoutMobileTest.java
│       │   └── web/
│       │       ├── BaseWebTest.java
│       │       ├── LoginWebTest.java
│       │       └── CheckoutWebTest.java
│       └── resources/
│           ├── suites/
│           │   ├── testng-mobile.xml
│           │   ├── testng-web.xml
│           │   ├── testng-api.xml
│           │   └── testng-all.xml
│           └── testdata/
│               ├── users.json
│               └── products.json
├── .gitignore
├── .gitattributes                              # Line ending consistency
├── .editorconfig                               # Editor consistency
├── CLAUDE.md                                   # Agent execution rules (sama seperti Python repo)
├── CONTRIBUTING.md
├── LICENSE                                     # MIT
├── README.md                                   # Rewrite total
└── pom.xml
```

---

## 2. Phase Breakdown (Estimasi 25-30 Commits)

### Phase 0 — Setup & Branch (1 commit)
**Goal:** Buat branch `refactor/v2`, baseline metadata.
- Buat branch dari `main`
- Buat `CLAUDE.md` (agent rules — sama seperti Python repo)
- Update `.gitconfig` lokal untuk strip AI attribution di repo ini

**Commit:**
- `chore: init refactor/v2 branch with agent rules`

---

### Phase 1 — Hot Fixes Pre-Refactor (3 commits)
**Goal:** Fix 8 critical bug yang udah teridentifikasi di code review, biar baseline functional sebelum restructure.

- Fix `Login.java` 3 locator bug (`stdUser`, `lockedUser`, `problemUser`)
- Fix `TestListeners.onTestSuccess()` NPE
- Hapus reference `TestNG.Mobile.Test` dari testng.xml
- Hapus duplicate Apache POI dari pom.xml
- Hapus `com.relevantcodes:extentreports` lama dari pom.xml

**Commits:**
- `fix(mobile): correct locked_out_user and problem_user locators`
- `fix(reporter): prevent NPE in onTestSuccess when throwable is null`
- `chore: clean pom.xml duplicate and deprecated dependencies`

---

### Phase 2 — Foundation Restructure (8 commits)
**Goal:** Total restructure package + Maven config + logging + ngebersihin warisan code.

- Update `pom.xml`: Java 25, properties block untuk versions, plugins terbaru, profiles (local/ci/browserstack)
- Migrasi package dari `TestNG.Mobile.*` + `Selenium.*` + `Appium.*` → `com.kennyramadhan.qa.*`
- Move `config.properties` dari `src/main/java/Selenium/Resources/` → `src/main/resources/config/`
- Replace `FileInputStream` di ConfigLoader dengan classpath-based loading
- Tambah multi-env config (local, ci, dev, staging)
- Replace `System.out.println` everywhere → SLF4J + Logback
- Tambah `logback.xml` dengan console + file appender
- Hapus `Utils.java` (duplikat sama TestListeners)
- Fix Java 25 deprecations (`getId()` → `threadId()`)
- Replace `Iterables.get()` Guava → `List.get()` standard

**Commits:**
- `chore(build): upgrade pom.xml to Java 25 and modern dependencies`
- `refactor: migrate package to com.kennyramadhan.qa namespace`
- `refactor(config): move properties to src/main/resources and use classpath loading`
- `feat(config): add multi-environment support (local/ci/staging)`
- `refactor(logging): replace System.out with SLF4J + Logback`
- `chore: remove duplicate Utils.java and dead code blocks`
- `chore: fix Java 25 deprecations and replace Guava Iterables`
- `style: enforce English-only comments and consistent naming`

---

### Phase 3 — Mobile Refactor (5 commits)
**Goal:** Modernisasi mobile layer — drop PageFactory, fix static state, proper waits.

- Refactor Page Objects: drop `PageFactory.initElements`, pakai locator constants + explicit `WebDriverWait`
- Pisahin `e2eMobile.java` jadi `LoginMobileTest`, `CartMobileTest`, `CheckoutMobileTest`
- Hapus assertion dari Page Objects, pindah ke test layer
- Refactor `LogHelper` → `AllureLogger` (ThreadLocal-safe)
- Refactor `ExtentNode` → ngilang (Allure-native step API gantiin)
- Tambah `BaseMobilePage` dengan shared utilities (waits, screenshots, actions)
- Replace `tapByCoordinates` dengan proper `WebDriverWait + ElementClickIntercepted` retry
- Implement proper `RetryAnalyzer` untuk flaky test
- Migrate ExtentReports → Allure (drop ExtentReports completely)

**Commits:**
- `refactor(mobile): drop PageFactory, use explicit waits with locator constants`
- `refactor(mobile): split e2eMobile into focused test classes`
- `refactor(mobile): move assertions out of page objects`
- `feat(reporting): migrate ExtentReports to Allure`
- `feat(reliability): add RetryAnalyzer for flaky test handling`

---

### Phase 4 — Add Web Layer (4 commits)
**Goal:** Tambah web automation untuk practicesoftwaretesting.com.

- Setup Selenium 4 + WebDriverManager
- `WebDriverFactory` dengan support: Chrome, Firefox, Edge, headless mode
- `BaseWebPage` dengan shared utilities
- Page Objects: HomePage, LoginPage, ProductsPage, CheckoutPage
- Test classes: LoginWebTest, CheckoutWebTest, ProductBrowsingWebTest
- testng-web.xml dengan parallel methods config
- Optional: BrowserStack profile di pom.xml

**Commits:**
- `feat(web): add Selenium WebDriver foundation with multi-browser support`
- `feat(web): implement page objects for practicesoftwaretesting.com`
- `feat(web): add login and checkout web test scenarios`
- `feat(ci): add browserstack profile for cloud testing`

---

### Phase 5 — Add API Layer (3 commits)
**Goal:** Tambah API automation untuk practicesoftwaretesting.com API.

- Setup RestAssured 5 + Jackson
- `BaseApiClient` dengan auth handling, JSON serialization, request/response logging
- API endpoints: Auth, Products, Cart
- Java records sebagai POJO models (Java 25 feature)
- Test classes: AuthApiTest, ProductsApiTest, CartApiTest
- testng-api.xml dengan parallel classes

**Commits:**
- `feat(api): add RestAssured foundation with base client`
- `feat(api): implement endpoints and models for practicesoftwaretesting API`
- `feat(api): add comprehensive auth and products test coverage`

---

### Phase 6 — CI/CD + Polish (5 commits)
**Goal:** Production-grade publishing & documentation.

- GitHub Actions: PR check workflow (build + test smoke)
- GitHub Actions: Main branch workflow (full suite + Allure publish ke gh-pages)
- README total rewrite dengan badges, architecture diagram, screenshots
- ARCHITECTURE.md (design decisions document)
- GETTING_STARTED.md (local setup walkthrough)
- CONTRIBUTING.md + LICENSE (MIT)
- `.editorconfig` + `.gitattributes` untuk consistency
- Spotless plugin untuk auto-format
- Pre-commit hook (commit-msg untuk strip AI attribution — sama seperti Python repo)

**Commits:**
- `ci: add GitHub Actions for PR validation`
- `ci: add Allure report publishing to gh-pages`
- `docs: rewrite README with badges, architecture, screenshots`
- `docs: add ARCHITECTURE.md and GETTING_STARTED.md`
- `chore: add Spotless, EditorConfig, and pre-commit hooks`

---

### Final — Merge ke Main (1 commit)
**Goal:** PR `refactor/v2` → `main` dengan story arc lengkap.

- Squash atau merge commit dengan summary lengkap
- Update version di pom.xml: `0.0.1-SNAPSHOT` → `2.0.0`
- Tag release `v2.0.0` di GitHub
- Optional: bikin GitHub Release dengan changelog

---

## 3. Breaking Changes Inventory

Hal-hal yang akan break/berubah behavior dari versi lama:

| Item | Old | New | Mitigation |
|---|---|---|---|
| Package | `TestNG.Mobile.*`, `Selenium.*`, `Appium.*` | `com.kennyramadhan.qa.*` | Find-replace di semua import |
| Test class | `e2eMobile` | `LoginMobileTest`, `CartMobileTest`, `CheckoutMobileTest` | Update testng.xml |
| Config path | `src/main/java/Selenium/Resources/config.properties` | `src/main/resources/config/config.properties` | Move + update ConfigLoader |
| Reporter | ExtentReports (`reports/*.html`) | Allure (`target/allure-results/` → gh-pages) | Drop ExtentReports lib |
| Logger | `System.out`, `System.err` | SLF4J `log.info()`, `log.error()` | Auto find-replace |
| Logger lama | `LogHelper.step()`, `LogHelper.detail()` | `AllureLogger.step()` (atau native `Allure.step()`) | Wrapper method baru |
| Test ordering | `priority = 1, 2, ...` | Independent tests, no priority | Refactor each test |
| Page Object init | `PageFactory.initElements(...)` | Constructor with explicit locator constants | Per-page rewrite |
| Faker | JavaFaker | Datafaker | Drop-in replacement |

---

## 4. Definition of Done per Phase

Sebelum commit phase X, harus:
- ✅ `mvn clean compile` sukses (no compile error)
- ✅ `mvn clean test -Plocal` di smoke test sukses (minimal 1 test)
- ✅ No new SpotBugs warnings (setelah Phase 6 setup)
- ✅ Commit message ngikut conventional commits format
- ✅ Tidak ada `System.out.println` baru yang masuk
- ✅ Tidak ada `// TODO` tanpa context atau ticket
- ✅ JavaDoc untuk public method di core/ dan api/

---

## 5. Conventional Commits Format

```
<type>(<scope>): <subject>

<body — optional>

<footer — optional, e.g. BREAKING CHANGE>
```

**Types yang dipakai:**
- `feat` — fitur baru
- `fix` — bug fix
- `refactor` — restructure tanpa ubah behavior
- `chore` — maintenance (build, deps, config)
- `docs` — dokumentasi
- `style` — formatting, naming
- `test` — nambah/edit test
- `ci` — CI/CD config

**Scopes yang dipakai:**
- `mobile`, `web`, `api`, `core`, `config`, `reporting`, `build`, `reliability`

**Aturan:**
- Subject line ≤ 72 char
- Imperative mood: "add", "fix", "remove" — bukan "added", "fixed"
- English only
- No AI attribution (Co-Authored-By Claude / anthropic) — protect via commit-msg hook

---

## 6. Risk & Rollback Plan

**Risiko utama:**
1. **Phase 3 mobile refactor** akan paling rentan — drop PageFactory + restructure sekaligus. Test mobile mungkin perlu re-debug di emulator.
2. **Phase 4 + 5** add web + API butuh demo target up & running. Kalau practicesoftwaretesting.com down, test akan gagal — mitigasi: skip suite via TestNG group filter.
3. **Phase 6 CI** mobile test di GitHub Actions susah (perlu Android emulator). Rencana: di CI, mobile test di-skip default, cuma jalan kalau pakai BrowserStack profile.

**Rollback:**
- Tiap phase = beberapa commit yang isolated
- Branch `refactor/v2` independen dari `main` — kalau gagal total, tinggal delete branch
- Setiap akhir phase, push ke origin biar ada checkpoint cloud

---

## 7. Eksekusi Style

Lu prefer **one task at a time, no apology loops, direct pushback**. Maka eksekusi gua:

1. Gua kasih command/code per task (1 task = 1 logical unit, biasanya 1 commit)
2. Lu run di local, paste output kalau ada error
3. Kalau output OK, lu confirm "next" atau "lanjut" — gua kasih task berikutnya
4. Kalau lu nggak setuju dengan suatu approach, kasih tau langsung — gua adjust
5. Tiap akhir phase, gua kasih checkpoint summary + suggestion push ke origin
6. Tiap akhir phase, kita pause sebentar — lu review hasilnya, baru lanjut phase berikutnya

**Yang gua TIDAK akan lakuin:**
- Nggak akan kirim semua code di 1 message (terlalu banyak, gampang skip)
- Nggak akan apologize untuk push back atau nemu issue di code lama
- Nggak akan auto-proceed ke phase berikutnya tanpa lu konfirmasi
- Nggak akan kasih probability estimates yang berubah-ubah tanpa evidence

---

## 8. Open Questions Sebelum Mulai

Lu jawab 4 ini, baru gua mulai eksekusi Phase 0:

1. **Setting kerja lu** — IDE-nya apa? Eclipse atau IntelliJ IDEA Community/Ultimate? (Gua akan kasih instruction yang IDE-specific kalau perlu)
2. **Demo target practicesoftwaretesting.com** — lu udah pernah daftar account sebelumnya buat Python repo? Kalau iya, kita reuse credentials (taroh di `.env`-style file, gitignored). Kalau belum, daftar dulu — gua kasih signup link nanti.
3. **Mobile test di refactor v2 — keep target SwagLabs?** Atau ganti ke practicesoftwaretesting yang lebih konsisten dengan web+API? (Gua rekomen keep SwagLabs — udah jalan, ganti target = scope creep)
4. **Cadence eksekusi** — lu mau eksekusi marathon (sekarang sampai capek) atau session-based (1-2 phase per hari, take break)?

---

*End of master plan. Estimasi total durasi: 1-2 minggu kalau full focus, 3-4 minggu kalau sambil kerja.*
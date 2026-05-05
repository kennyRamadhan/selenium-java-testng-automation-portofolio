# Phase 5 — Add API Layer

**Goal:** Add REST API automation targeting **automationexercise.com API** using RestAssured + Jackson.

**Total commits:** 3
**Estimated time:** 2-4 hours
**Prerequisite:** Phase 4 complete
**API documentation:** https://automationexercise.com/api_list

---

## Required Reading

1. `CLAUDE.md` — agent rules
2. `MASTER_PLAN.md` — Section "Phase 5 — Add API Layer"
3. `phase-4-handoff.md` — web patterns established
4. https://automationexercise.com/api_list — full endpoint list

Confirm starting state:
```bash
git log --oneline -20
mvn clean compile -q && echo "OK"
```

---

## Available Endpoints (from automationexercise.com API)

| API | Method | Endpoint | Purpose |
|---|---|---|---|
| 1 | GET | `/api/productsList` | Get all products |
| 2 | POST | `/api/productsList` | Should return 405 (method not supported) |
| 3 | GET | `/api/brandsList` | Get all brands |
| 4 | PUT | `/api/brandsList` | Should return 405 |
| 5 | POST | `/api/searchProduct` | Search product by `search_product` form field |
| 6 | POST | `/api/searchProduct` (no param) | Should return 400 |
| 7 | POST | `/api/verifyLogin` | Login with email + password |
| 8 | POST | `/api/verifyLogin` (no email) | Should return 400 |
| 9 | DELETE | `/api/verifyLogin` | Should return 405 |
| 10 | POST | `/api/verifyLogin` (invalid creds) | Should return 404 |
| 11 | POST | `/api/createAccount` | Register new account |
| 12 | DELETE | `/api/deleteAccount` | Delete account |
| 13 | PUT | `/api/updateAccount` | Update account info |
| 14 | GET | `/api/getUserDetailByEmail` | Get user details by email |

---

## Commits to Create (in order)

### Commit 5.1: `feat(api): add RestAssured foundation with base client`

**File: `pom.xml`**

Add dependencies:
```xml
<!-- RestAssured for API testing -->
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>json-path</artifactId>
    <version>5.5.0</version>
    <scope>test</scope>
</dependency>

<!-- Jackson for JSON serialization -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.18.1</version>
</dependency>

<dependency>
    <groupId>com.fasterxml.jackson.datatype</groupId>
    <artifactId>jackson-datatype-jsr310</artifactId>
    <version>2.18.1</version>
</dependency>

<!-- Allure RestAssured integration -->
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-rest-assured</artifactId>
    <version>2.29.0</version>
    <scope>test</scope>
</dependency>
```

**File: `src/main/java/com/kennyramadhan/qa/api/client/BaseApiClient.java`**

```java
package com.kennyramadhan.qa.api.client;

import com.kennyramadhan.qa.core.config.ConfigLoader;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public abstract class BaseApiClient {
    protected final String baseUrl;

    protected BaseApiClient() {
        this.baseUrl = ConfigLoader.getOrDefault("api.baseUrl", "https://automationexercise.com");
    }

    protected RequestSpecification given() {
        return RestAssured.given()
            .spec(new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setContentType(ContentType.URLENC)
                .setAccept(ContentType.JSON)
                .build())
            .filter(new AllureRestAssured())
            .filter(new RequestLoggingFilter())
            .filter(new ResponseLoggingFilter());
    }
}
```

**File: `src/main/resources/config/config.properties`** — add:
```properties
api.baseUrl=https://automationexercise.com
api.timeout.seconds=30
```

**File: `src/main/java/com/kennyramadhan/qa/api/models/ApiResponse.java`**

```java
package com.kennyramadhan.qa.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ApiResponse(
    @JsonProperty("responseCode") int responseCode,
    @JsonProperty("message") String message
) {}
```

**Verification:**
```bash
mvn clean compile -q
mvn dependency:tree -q | grep -E "rest-assured|jackson-databind|allure-rest-assured"
```

---

### Commit 5.2: `feat(api): implement endpoints and models for automationexercise API`

**Models** under `src/main/java/com/kennyramadhan/qa/api/models/`:

```java
// Product.java
public record Product(
    int id,
    String name,
    String price,
    String brand,
    Category category
) {
    public record Category(
        Usertype usertype,
        String category
    ) {
        public record Usertype(String usertype) {}
    }
}

// ProductsListResponse.java
public record ProductsListResponse(
    int responseCode,
    List<Product> products
) {}

// Brand.java
public record Brand(int id, String brand) {}

// BrandsListResponse.java
public record BrandsListResponse(int responseCode, List<Brand> brands) {}

// UserDetails.java
public record UserDetails(
    int id,
    @JsonProperty("name") String name,
    @JsonProperty("email") String email,
    @JsonProperty("title") String title,
    @JsonProperty("birth_day") String birthDay,
    @JsonProperty("birth_month") String birthMonth,
    @JsonProperty("birth_year") String birthYear,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("company") String company,
    @JsonProperty("address1") String address1,
    @JsonProperty("address2") String address2,
    @JsonProperty("country") String country,
    @JsonProperty("state") String state,
    @JsonProperty("city") String city,
    @JsonProperty("zipcode") String zipcode
) {}

// CreateAccountRequest.java — used as form data, not JSON
public record CreateAccountRequest(
    String name, String email, String password, String title,
    String birth_date, String birth_month, String birth_year,
    String firstname, String lastname, String company,
    String address1, String address2, String country, String zipcode,
    String state, String city, String mobile_number
) {}
```

**Endpoint clients** under `src/main/java/com/kennyramadhan/qa/api/endpoints/`:

```java
// ProductsApi.java
public class ProductsApi extends BaseApiClient {

    @Step("GET /api/productsList")
    public ProductsListResponse getAllProducts() {
        return given()
            .when().get("/api/productsList")
            .then().statusCode(200)
            .extract().as(ProductsListResponse.class);
    }

    @Step("POST /api/searchProduct with query={query}")
    public Response searchProduct(String query) {
        return given()
            .formParam("search_product", query)
            .when().post("/api/searchProduct");
    }
}

// AuthApi.java
public class AuthApi extends BaseApiClient {

    @Step("POST /api/verifyLogin")
    public Response verifyLogin(String email, String password) {
        return given()
            .formParam("email", email)
            .formParam("password", password)
            .when().post("/api/verifyLogin");
    }

    @Step("POST /api/createAccount")
    public Response createAccount(CreateAccountRequest req) {
        return given()
            .formParam("name", req.name())
            .formParam("email", req.email())
            // ... (all fields)
            .when().post("/api/createAccount");
    }

    @Step("DELETE /api/deleteAccount")
    public Response deleteAccount(String email, String password) {
        return given()
            .formParam("email", email)
            .formParam("password", password)
            .when().delete("/api/deleteAccount");
    }

    @Step("PUT /api/updateAccount")
    public Response updateAccount(CreateAccountRequest req) {
        return given()
            .formParams(toFormMap(req))
            .when().put("/api/updateAccount");
    }
}

// BrandsApi.java
public class BrandsApi extends BaseApiClient {

    @Step("GET /api/brandsList")
    public BrandsListResponse getAllBrands() {
        return given()
            .when().get("/api/brandsList")
            .then().statusCode(200)
            .extract().as(BrandsListResponse.class);
    }
}

// UserApi.java
public class UserApi extends BaseApiClient {

    @Step("GET /api/getUserDetailByEmail email={email}")
    public Response getUserByEmail(String email) {
        return given()
            .queryParam("email", email)
            .when().get("/api/getUserDetailByEmail");
    }
}
```

**Verification:**
```bash
mvn clean compile -q
find src/main/java/com/kennyramadhan/qa/api -name "*.java" | sort
```

---

### Commit 5.3: `feat(api): add comprehensive auth and products test coverage`

**Test files** under `src/test/java/com/kennyramadhan/qa/tests/api/`:

```java
// BaseApiTest.java
@Listeners({AllureTestListener.class})
public abstract class BaseApiTest {
    protected ProductsApi productsApi;
    protected AuthApi authApi;
    protected BrandsApi brandsApi;
    protected UserApi userApi;

    @BeforeMethod(alwaysRun = true)
    public void setupClients() {
        productsApi = new ProductsApi();
        authApi = new AuthApi();
        brandsApi = new BrandsApi();
        userApi = new UserApi();
    }
}

// ProductsApiTest.java — covers API 1, 2, 5, 6
@Epic("API")
@Feature("Products")
public class ProductsApiTest extends BaseApiTest {

    @Test(groups = {"api", "smoke"})
    @Severity(SeverityLevel.CRITICAL)
    public void shouldReturnAllProductsList() {
        ProductsListResponse response = productsApi.getAllProducts();
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.products()).isNotEmpty();
        assertThat(response.products()).allSatisfy(p -> {
            assertThat(p.id()).isPositive();
            assertThat(p.name()).isNotBlank();
            assertThat(p.price()).isNotBlank();
        });
    }

    @Test(groups = {"api"})
    public void shouldReject405OnPostToProductsList() {
        Response response = given()  // raw RestAssured
            .when().post("/api/productsList");
        assertThat(response.statusCode()).isEqualTo(200);  // Note: AE returns 200 with responseCode 405 in body
        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(405);
    }

    @Test(groups = {"api", "smoke"})
    public void shouldSearchProductByValidKeyword() {
        Response response = productsApi.searchProduct("top");
        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(200);
        List<Map<String, Object>> products = response.jsonPath().getList("products");
        assertThat(products).isNotEmpty();
    }

    @Test(groups = {"api"})
    public void shouldReturn400OnSearchWithoutParam() {
        Response response = given()
            .when().post("/api/searchProduct");
        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(400);
    }
}

// AuthApiTest.java — covers API 7, 8, 10, 11, 12, 13
@Epic("API")
@Feature("Authentication")
public class AuthApiTest extends BaseApiTest {

    @Test(groups = {"api", "regression"})
    public void shouldRegisterAndDeleteAccount() {
        Faker faker = new Faker();
        String email = faker.internet().emailAddress();
        String password = "Test@123";

        CreateAccountRequest req = buildAccountRequest(faker, email, password);
        Response createResp = authApi.createAccount(req);
        assertThat(createResp.jsonPath().getInt("responseCode")).isEqualTo(201);

        Response deleteResp = authApi.deleteAccount(email, password);
        assertThat(deleteResp.jsonPath().getInt("responseCode")).isEqualTo(200);
    }

    @Test(groups = {"api"})
    public void shouldRejectLoginWithInvalidCredentials() {
        Response response = authApi.verifyLogin("nonexistent@example.com", "wrong");
        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(404);
    }

    @Test(groups = {"api"})
    public void shouldReject400OnLoginWithoutEmail() {
        Response response = given()
            .formParam("password", "anything")
            .when().post("/api/verifyLogin");
        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(400);
    }

    @Test(groups = {"api"})
    public void shouldReject405OnDeleteToVerifyLogin() {
        Response response = given()
            .when().delete("/api/verifyLogin");
        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(405);
    }
}

// BrandsApiTest.java — covers API 3, 4
public class BrandsApiTest extends BaseApiTest {

    @Test(groups = {"api", "smoke"})
    public void shouldReturnAllBrandsList() {
        BrandsListResponse response = brandsApi.getAllBrands();
        assertThat(response.responseCode()).isEqualTo(200);
        assertThat(response.brands()).isNotEmpty();
    }

    @Test(groups = {"api"})
    public void shouldReject405OnPutToBrandsList() {
        Response response = given()
            .when().put("/api/brandsList");
        assertThat(response.jsonPath().getInt("responseCode")).isEqualTo(405);
    }
}
```

**Create `src/test/resources/suites/testng-api.xml`:**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="API Suite" parallel="classes" thread-count="3">
    <listeners>
        <listener class-name="com.kennyramadhan.qa.core.retry.RetryListener"/>
        <listener class-name="com.kennyramadhan.qa.core.reporting.AllureTestListener"/>
    </listeners>
    <test name="API Tests">
        <classes>
            <class name="com.kennyramadhan.qa.tests.api.ProductsApiTest"/>
            <class name="com.kennyramadhan.qa.tests.api.AuthApiTest"/>
            <class name="com.kennyramadhan.qa.tests.api.BrandsApiTest"/>
        </classes>
    </test>
</suite>
```

**Update Phase 4's `<profile id="api">`** in pom.xml to point to this suite file.

**Verification:**
```bash
mvn clean test-compile -q
mvn test -P api -Dgroups=smoke
# Should run 3 smoke tests
```

---

## Phase-Specific Constraints

1. **No real account data committed.** All credentials come from Datafaker or env vars.
2. **API tests must clean up after themselves.** If `createAccount` is called, `deleteAccount` should run in `@AfterMethod` for that test.
3. **Form-encoded, not JSON.** automationexercise.com API uses `application/x-www-form-urlencoded` for POST/PUT requests.
4. **Quirky responses:** automationexercise.com always returns HTTP 200, with the actual status in the response body's `responseCode` field. Document this in `BaseApiClient` JavaDoc.
5. **Allure RestAssured filter** must be on every request for API trace in reports.
6. **Use AssertJ.** Consistent with web/mobile tests.

---

## Definition of Done

After all 3 commits:
1. `mvn clean compile -q` succeeds
2. `mvn clean test-compile -q` succeeds
3. `mvn test -P api -Dgroups=smoke` runs at least 3 smoke tests green
4. Allure results populated under `target/allure-results/`
5. New directories: `src/main/java/com/kennyramadhan/qa/api/{client,endpoints,models}`, `src/test/java/com/kennyramadhan/qa/tests/api/`
6. Push to origin

---

## Reporting Format

```
Phase 5 complete: API Layer

Commits:
- <SHA1> feat(api): add RestAssured foundation with base client
- <SHA2> feat(api): implement endpoints and models for automationexercise API
- <SHA3> feat(api): add comprehensive auth and products test coverage

Verification:
- RestAssured 5.5.0 + Jackson 2.18.1 added
- 4 API client classes (Products, Auth, Brands, User)
- N model records
- 3 test classes with M test methods total
- testng-api.xml with parallel classes (thread-count=3)
- mvn test -P api -Dgroups=smoke: ✅ all green

Pushed to: origin/refactor/v2

Concerns surfaced:
<list, or "none">

Ready for Phase 6 (CI/CD + Polish).
```

---

## Stop Conditions

Stop and ask Kenny if:
- automationexercise.com API endpoint structure differs from documented spec
- Account creation/deletion has rate limits that block parallel tests
- Response format inconsistency makes Jackson deserialization fail
- Test data cleanup fails (orphan accounts left behind)

Do NOT proceed to Phase 6.

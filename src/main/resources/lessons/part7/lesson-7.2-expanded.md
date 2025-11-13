# Lesson 7.2: Testing Strategies

**Estimated Time**: 80 minutes

---

## Introduction

Testing is not optional in professional software development - it's a critical skill that separates hobbyist code from production-ready applications.

In this lesson, you'll master advanced testing strategies for Kotlin applications:
- ✅ Unit testing with JUnit 5 and Kotest
- ✅ Mocking dependencies with MockK
- ✅ Testing coroutines and flows
- ✅ Testing Jetpack Compose UI
- ✅ Test-driven development (TDD)
- ✅ Measuring code coverage

By the end, you'll write tests that give you confidence to refactor, deploy, and sleep peacefully at night.

---

## Why Testing Matters

### The Cost of Bugs

**Production Bug Cost**:
```
Bug found in:
└─ Development (writing code): $100
└─ Testing (QA phase): $1,000
└─ Staging (before release): $10,000
└─ Production (after release): $100,000+
```

**Real Example**: A banking app bug that allowed duplicate withdrawals:
- Development: Could be caught with 1 unit test ($100)
- Production: Cost $2.3M in fraudulent transactions + reputation damage

**Statistics**:
- Well-tested codebases have 40-80% fewer production bugs
- Companies with good test coverage deploy 46x more frequently
- Automated tests reduce debugging time by 60%

---

## Testing Pyramid

### The Right Balance

```
       /\
      /  \     E2E Tests (UI)
     /    \    10% - Slow, expensive, brittle
    /------\
   /        \  Integration Tests
  /          \ 20% - Medium speed, test components together
 /------------\
/              \ Unit Tests
----------------  70% - Fast, cheap, test individual functions
```

**Unit Tests (70%)**:
- Test individual functions/classes in isolation
- Fast (milliseconds)
- Easy to write and maintain
- Run on every code change

**Integration Tests (20%)**:
- Test multiple components together
- Medium speed (seconds)
- Test real interactions

**E2E Tests (10%)**:
- Test entire user flows
- Slow (minutes)
- Fragile (UI changes break tests)
- Only for critical paths

---

## JUnit 5 Fundamentals

### Basic Test Structure

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.22")
}

tasks.test {
    useJUnitPlatform()
}
```

**Simple Test**:
```kotlin
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class CalculatorTest {

    @Test
    fun `addition should return sum of two numbers`() {
        // Arrange
        val calculator = Calculator()

        // Act
        val result = calculator.add(2, 3)

        // Assert
        assertEquals(5, result)
    }

    @Test
    fun `division by zero should throw exception`() {
        val calculator = Calculator()

        assertThrows<ArithmeticException> {
            calculator.divide(10, 0)
        }
    }
}
```

### Test Lifecycle

```kotlin
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @BeforeAll
    fun setupClass() {
        // Runs once before all tests
        println("Setting up database connection")
    }

    @BeforeEach
    fun setup() {
        // Runs before each test
        println("Clearing test data")
    }

    @AfterEach
    fun cleanup() {
        // Runs after each test
        println("Cleaning up")
    }

    @AfterAll
    fun teardownClass() {
        // Runs once after all tests
        println("Closing database connection")
    }

    @Test
    fun `test 1`() { }

    @Test
    fun `test 2`() { }
}
```

### Parameterized Tests

```kotlin
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.*

class ValidationTest {

    @ParameterizedTest
    @ValueSource(strings = ["test@example.com", "user@domain.co", "name+tag@email.com"])
    fun `valid emails should pass validation`(email: String) {
        assertTrue(Validator.isValidEmail(email))
    }

    @ParameterizedTest
    @CsvSource(
        "0, INFANT",
        "5, CHILD",
        "13, TEEN",
        "20, ADULT",
        "70, SENIOR"
    )
    fun `age categories should be correct`(age: Int, expectedCategory: String) {
        assertEquals(expectedCategory, getAgeCategory(age))
    }

    @ParameterizedTest
    @MethodSource("passwordProvider")
    fun `weak passwords should fail validation`(password: String) {
        assertFalse(Validator.isStrongPassword(password))
    }

    companion object {
        @JvmStatic
        fun passwordProvider() = listOf(
            "123",
            "password",
            "abc123",
            "NoNumber"
        )
    }
}
```

---

## Kotest - Beautiful Testing DSL

### Why Kotest?

Kotest provides a more readable, Kotlin-idiomatic testing syntax.

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
}
```

**Comparison**:

**JUnit**:
```kotlin
@Test
fun `user with invalid email should not be created`() {
    assertThrows<ValidationException> {
        userService.createUser("invalid-email", "password")
    }
}
```

**Kotest**:
```kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow

class UserServiceTest : StringSpec({

    "user with invalid email should not be created" {
        shouldThrow<ValidationException> {
            userService.createUser("invalid-email", "password")
        }
    }

    "valid user should be created successfully" {
        val user = userService.createUser("test@example.com", "SecurePass123!")
        user.email shouldBe "test@example.com"
    }
})
```

### Kotest Matchers

```kotlin
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import io.kotest.matchers.string.*

class KotestMatchersTest : StringSpec({

    "string matchers" {
        val name = "Kotlin"

        name shouldStartWith "Kot"
        name shouldEndWith "lin"
        name shouldContain "otl"
        name shouldHaveLength 6
        name shouldMatch "K[a-z]+".toRegex()
    }

    "collection matchers" {
        val list = listOf(1, 2, 3, 4, 5)

        list shouldHaveSize 5
        list shouldContain 3
        list shouldContainAll listOf(1, 3, 5)
        list.shouldBeSorted()

        val emptyList = emptyList<Int>()
        emptyList.shouldBeEmpty()
    }

    "numeric matchers" {
        val price = 99.99

        price shouldBeGreaterThan 50.0
        price shouldBeLessThan 100.0
        price.shouldBeBetween(90.0, 100.0)
    }

    "exception matchers" {
        shouldThrow<IllegalArgumentException> {
            require(false) { "Error message" }
        }.message shouldBe "Error message"
    }
})
```

---

## MockK - Powerful Mocking

### Why Mock?

**Problem**: Testing a service that depends on a database:

```kotlin
class UserService(private val database: Database) {
    fun getUser(id: Int): User? {
        return database.query("SELECT * FROM users WHERE id = ?", id)
    }
}
```

To test `UserService`, we don't want to:
- Set up a real database
- Insert test data
- Clean up after tests
- Deal with slow I/O operations

**Solution**: Mock the database!

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("io.mockk:mockk:1.13.9")
}
```

### Basic Mocking

```kotlin
import io.mockk.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UserServiceTest {

    @Test
    fun `getUser should return user from database`() {
        // Create a mock
        val mockDatabase = mockk<Database>()

        // Define behavior
        every { mockDatabase.query("SELECT * FROM users WHERE id = ?", 1) } returns
            User(id = 1, name = "John", email = "john@example.com")

        // Test
        val service = UserService(mockDatabase)
        val user = service.getUser(1)

        // Verify
        assertEquals("John", user?.name)
        verify(exactly = 1) { mockDatabase.query(any(), 1) }
    }

    @Test
    fun `getUser should return null when user not found`() {
        val mockDatabase = mockk<Database>()

        every { mockDatabase.query(any<String>(), any<Int>()) } returns null

        val service = UserService(mockDatabase)
        val user = service.getUser(999)

        assertEquals(null, user)
    }
}
```

### Advanced Mocking

**Relaxed Mocks** (return default values):
```kotlin
@Test
fun `test with relaxed mock`() {
    val mockRepo = mockk<UserRepository>(relaxed = true)

    // All methods return defaults (null, 0, emptyList, etc.)
    mockRepo.getUsers() // Returns emptyList by default
}
```

**Spy** (real object with partial mocking):
```kotlin
@Test
fun `spy on real object`() {
    val realService = UserService(realDatabase)
    val spy = spyk(realService)

    // Override specific method
    every { spy.validateEmail(any()) } returns true

    // Other methods use real implementation
    spy.createUser("test@example.com", "pass")

    verify { spy.validateEmail("test@example.com") }
}
```

**Capture Arguments**:
```kotlin
@Test
fun `verify method was called with specific arguments`() {
    val mockRepo = mockk<UserRepository>(relaxed = true)
    val service = UserService(mockRepo)

    val slot = slot<User>()

    service.updateUser(User(1, "John", "john@example.com"))

    verify { mockRepo.update(capture(slot)) }

    assertEquals("John", slot.captured.name)
}
```

---

## Testing Coroutines

### runTest - The Testing Coroutine

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
```

**Basic Coroutine Test**:
```kotlin
import kotlinx.coroutines.test.*
import kotlinx.coroutines.delay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CoroutineTest {

    @Test
    fun `test suspending function`() = runTest {
        val result = fetchData() // suspending function
        assertEquals("data", result)
    }

    @Test
    fun `test with delay - virtual time`() = runTest {
        val startTime = currentTime

        delay(1000) // 1 second - but instant in test!

        val endTime = currentTime
        assertEquals(1000, endTime - startTime)
    }
}
```

### Testing Flows

```kotlin
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Test

class FlowTest {

    @Test
    fun `test flow emits correct values`() = runTest {
        val flow = flowOf(1, 2, 3, 4, 5)

        val results = flow.toList()

        assertEquals(listOf(1, 2, 3, 4, 5), results)
    }

    @Test
    fun `test flow transformation`() = runTest {
        val flow = flowOf(1, 2, 3)
            .map { it * 2 }
            .filter { it > 2 }

        val results = flow.toList()

        assertEquals(listOf(4, 6), results)
    }

    @Test
    fun `test StateFlow updates`() = runTest {
        val stateFlow = MutableStateFlow(0)

        val emissions = mutableListOf<Int>()
        val job = launch {
            stateFlow.take(3).toList(emissions)
        }

        stateFlow.emit(1)
        stateFlow.emit(2)

        job.join()

        assertEquals(listOf(0, 1, 2), emissions)
    }
}
```

### Testing ViewModels with Coroutines

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.*

class UserViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loading users should update state`() = runTest {
        val mockRepo = mockk<UserRepository>()
        every { mockRepo.getUsers() } returns flowOf(
            listOf(User(1, "John"), User(2, "Jane"))
        )

        val viewModel = UserViewModel(mockRepo)

        // Trigger action
        viewModel.loadUsers()

        // Advance until idle
        advanceUntilIdle()

        // Verify state
        assertEquals(2, viewModel.users.value.size)
        assertEquals(false, viewModel.isLoading.value)
    }
}
```

---

## Testing Jetpack Compose UI

### Compose Testing Library

```kotlin
// build.gradle.kts
dependencies {
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.0")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.0")
}
```

**Basic Compose Test**:
```kotlin
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_initialState_showsCorrectContent() {
        composeTestRule.setContent {
            LoginScreen()
        }

        // Find elements
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithTag("emailField").assertExists()
        composeTestRule.onNodeWithTag("passwordField").assertExists()
    }

    @Test
    fun loginButton_clickWithValidInput_callsOnLogin() {
        var loginCalled = false

        composeTestRule.setContent {
            LoginScreen(
                onLogin = { email, password ->
                    loginCalled = true
                }
            )
        }

        // Enter text
        composeTestRule.onNodeWithTag("emailField")
            .performTextInput("test@example.com")

        composeTestRule.onNodeWithTag("passwordField")
            .performTextInput("password123")

        // Click button
        composeTestRule.onNodeWithText("Login").performClick()

        assert(loginCalled)
    }

    @Test
    fun loginScreen_emptyEmail_showsError() {
        composeTestRule.setContent {
            LoginScreen()
        }

        // Click login without entering email
        composeTestRule.onNodeWithText("Login").performClick()

        // Verify error is shown
        composeTestRule.onNodeWithText("Email is required")
            .assertIsDisplayed()
    }
}
```

### Testing Interactions

```kotlin
@Test
fun todoList_addItem_showsInList() {
    composeTestRule.setContent {
        TodoApp()
    }

    // Enter new todo
    composeTestRule.onNodeWithTag("todoInput")
        .performTextInput("Buy groceries")

    // Click add button
    composeTestRule.onNodeWithTag("addButton")
        .performClick()

    // Verify item appears
    composeTestRule.onNodeWithText("Buy groceries")
        .assertIsDisplayed()

    // Verify input is cleared
    composeTestRule.onNodeWithTag("todoInput")
        .assertTextEquals("")
}

@Test
fun todoItem_clickCheckbox_marksAsComplete() {
    composeTestRule.setContent {
        TodoItem(
            todo = Todo(id = 1, text = "Test", completed = false),
            onToggle = { }
        )
    }

    // Initially unchecked
    composeTestRule.onNodeWithTag("checkbox-1")
        .assertIsOff()

    // Click checkbox
    composeTestRule.onNodeWithTag("checkbox-1")
        .performClick()

    // Verify it's checked
    composeTestRule.onNodeWithTag("checkbox-1")
        .assertIsOn()
}
```

---

## Test-Driven Development (TDD)

### The TDD Cycle

```
1. Red   → Write a failing test
2. Green → Write minimal code to pass
3. Refactor → Improve code while keeping tests passing
```

### Example: Building a Password Validator

**Step 1: Write the test (Red)**:
```kotlin
class PasswordValidatorTest : StringSpec({

    "password shorter than 8 characters should be invalid" {
        val validator = PasswordValidator()
        validator.isValid("abc123") shouldBe false
    }
})
```

Test fails (class doesn't exist yet) ❌

**Step 2: Minimal implementation (Green)**:
```kotlin
class PasswordValidator {
    fun isValid(password: String): Boolean {
        return password.length >= 8
    }
}
```

Test passes ✅

**Step 3: Add more tests**:
```kotlin
"password without uppercase should be invalid" {
    val validator = PasswordValidator()
    validator.isValid("password123") shouldBe false
}

"password without number should be invalid" {
    val validator = PasswordValidator()
    validator.isValid("Password") shouldBe false
}

"valid password should pass all checks" {
    val validator = PasswordValidator()
    validator.isValid("SecurePass123") shouldBe true
}
```

**Step 4: Implement to pass all tests**:
```kotlin
class PasswordValidator {
    fun isValid(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }

    fun getErrors(password: String): List<String> {
        val errors = mutableListOf<String>()

        if (password.length < 8) {
            errors.add("Password must be at least 8 characters")
        }
        if (!password.any { it.isUpperCase() }) {
            errors.add("Password must contain an uppercase letter")
        }
        if (!password.any { it.isDigit() }) {
            errors.add("Password must contain a number")
        }

        return errors
    }
}
```

**Benefits of TDD**:
- Forces you to think about design before implementation
- Ensures code is testable
- Provides immediate feedback
- Creates a safety net for refactoring

---

## Code Coverage

### Measuring Coverage with JaCoCo

```kotlin
// build.gradle.kts
plugins {
    jacoco
}

jacoco {
    toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(
                        "**/R.class",
                        "**/R$*.class",
                        "**/BuildConfig.*",
                        "**/Manifest*.*"
                    )
                }
            }
        )
    )
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}
```

**Run Coverage**:
```bash
./gradlew test jacocoTestReport
```

**View Report**:
Open `build/reports/jacoco/test/html/index.html`

### Coverage Metrics

**What's Good Coverage?**:
- **80%+**: Excellent
- **60-80%**: Good
- **40-60%**: Needs improvement
- **<40%**: Risky

**Important**: 100% coverage ≠ bug-free code. Focus on testing critical paths.

---

## Exercise 1: Build a Tested Repository

Create a `ProductRepository` with full test coverage.

### Requirements

1. **ProductRepository** with methods:
   - `getProducts(): List<Product>`
   - `getProduct(id: String): Product?`
   - `createProduct(product: Product): Result<Product>`
   - `updateProduct(id: String, product: Product): Result<Product>`
   - `deleteProduct(id: String): Result<Unit>`

2. **Tests** (use MockK):
   - Test successful operations
   - Test error cases (not found, network errors)
   - Test caching behavior
   - Verify mock interactions

---

## Solution 1

```kotlin
// src/main/kotlin/com/example/repository/ProductRepository.kt
package com.example.repository

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val stock: Int
)

interface ProductApi {
    suspend fun getProducts(): List<Product>
    suspend fun getProduct(id: String): Product
    suspend fun createProduct(product: Product): Product
    suspend fun updateProduct(id: String, product: Product): Product
    suspend fun deleteProduct(id: String)
}

class ProductRepository(private val api: ProductApi) {
    private val cache = mutableMapOf<String, Product>()

    suspend fun getProducts(): List<Product> {
        return try {
            val products = api.getProducts()
            products.forEach { cache[it.id] = it }
            products
        } catch (e: Exception) {
            cache.values.toList()
        }
    }

    suspend fun getProduct(id: String): Product? {
        return cache[id] ?: try {
            val product = api.getProduct(id)
            cache[id] = product
            product
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createProduct(product: Product): Result<Product> {
        return try {
            val created = api.createProduct(product)
            cache[created.id] = created
            Result.success(created)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(id: String, product: Product): Result<Product> {
        return try {
            val updated = api.updateProduct(id, product)
            cache[id] = updated
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(id: String): Result<Unit> {
        return try {
            api.deleteProduct(id)
            cache.remove(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun clearCache() {
        cache.clear()
    }
}
```

**Tests**:
```kotlin
// src/test/kotlin/com/example/repository/ProductRepositoryTest.kt
package com.example.repository

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class ProductRepositoryTest {

    private lateinit var mockApi: ProductApi
    private lateinit var repository: ProductRepository

    @BeforeEach
    fun setup() {
        mockApi = mockk()
        repository = ProductRepository(mockApi)
    }

    @Test
    fun `getProducts should fetch from API and cache results`() = runTest {
        val products = listOf(
            Product("1", "Laptop", 999.99, 10),
            Product("2", "Mouse", 29.99, 50)
        )

        coEvery { mockApi.getProducts() } returns products

        val result = repository.getProducts()

        assertEquals(2, result.size)
        assertEquals("Laptop", result[0].name)

        coVerify(exactly = 1) { mockApi.getProducts() }
    }

    @Test
    fun `getProducts should return cached data when API fails`() = runTest {
        val products = listOf(Product("1", "Laptop", 999.99, 10))

        // First call succeeds
        coEvery { mockApi.getProducts() } returns products
        repository.getProducts()

        // Second call fails
        coEvery { mockApi.getProducts() } throws Exception("Network error")
        val result = repository.getProducts()

        // Should return cached data
        assertEquals(1, result.size)
        assertEquals("Laptop", result[0].name)
    }

    @Test
    fun `getProduct should return cached product if available`() = runTest {
        val product = Product("1", "Laptop", 999.99, 10)

        coEvery { mockApi.getProducts() } returns listOf(product)
        repository.getProducts() // Populate cache

        val result = repository.getProduct("1")

        assertNotNull(result)
        assertEquals("Laptop", result.name)

        // API not called (used cache)
        coVerify(exactly = 0) { mockApi.getProduct(any()) }
    }

    @Test
    fun `getProduct should fetch from API if not cached`() = runTest {
        val product = Product("1", "Laptop", 999.99, 10)

        coEvery { mockApi.getProduct("1") } returns product

        val result = repository.getProduct("1")

        assertNotNull(result)
        assertEquals("Laptop", result.name)

        coVerify(exactly = 1) { mockApi.getProduct("1") }
    }

    @Test
    fun `getProduct should return null when product not found`() = runTest {
        coEvery { mockApi.getProduct("999") } throws Exception("Not found")

        val result = repository.getProduct("999")

        assertNull(result)
    }

    @Test
    fun `createProduct should call API and cache result`() = runTest {
        val newProduct = Product("3", "Keyboard", 79.99, 30)

        coEvery { mockApi.createProduct(newProduct) } returns newProduct

        val result = repository.createProduct(newProduct)

        assertTrue(result.isSuccess)
        assertEquals("Keyboard", result.getOrNull()?.name)

        // Verify cached
        val cached = repository.getProduct("3")
        assertNotNull(cached)
        assertEquals("Keyboard", cached.name)
    }

    @Test
    fun `createProduct should return failure when API fails`() = runTest {
        val newProduct = Product("3", "Keyboard", 79.99, 30)

        coEvery { mockApi.createProduct(newProduct) } throws Exception("Server error")

        val result = repository.createProduct(newProduct)

        assertTrue(result.isFailure)
    }

    @Test
    fun `updateProduct should update cache on success`() = runTest {
        val updated = Product("1", "Gaming Laptop", 1299.99, 5)

        coEvery { mockApi.updateProduct("1", updated) } returns updated

        val result = repository.updateProduct("1", updated)

        assertTrue(result.isSuccess)
        assertEquals("Gaming Laptop", result.getOrNull()?.name)
    }

    @Test
    fun `deleteProduct should remove from cache`() = runTest {
        val product = Product("1", "Laptop", 999.99, 10)

        // Add to cache
        coEvery { mockApi.getProduct("1") } returns product
        repository.getProduct("1")

        // Delete
        coEvery { mockApi.deleteProduct("1") } just Runs

        val result = repository.deleteProduct("1")

        assertTrue(result.isSuccess)

        // Verify removed from cache
        coEvery { mockApi.getProduct("1") } throws Exception("Not found")
        val cached = repository.getProduct("1")
        assertNull(cached)
    }

    @Test
    fun `clearCache should remove all cached products`() = runTest {
        val products = listOf(Product("1", "Laptop", 999.99, 10))

        coEvery { mockApi.getProducts() } returns products
        repository.getProducts()

        repository.clearCache()

        coEvery { mockApi.getProduct("1") } throws Exception("Not found")
        val cached = repository.getProduct("1")
        assertNull(cached)
    }
}
```

---

## Exercise 2: Test a Compose Screen

Create tests for a shopping cart screen.

### Requirements

1. **CartScreen Composable**:
   - Displays list of cart items
   - Shows total price
   - Has "Checkout" button
   - Can remove items

2. **Tests**:
   - Verify items are displayed
   - Verify total is calculated correctly
   - Test remove item functionality
   - Test checkout button click

---

## Solution 2

```kotlin
// src/main/kotlin/com/example/ui/CartScreen.kt
@Composable
fun CartScreen(
    items: List<CartItem>,
    onRemoveItem: (String) -> Unit = {},
    onCheckout: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Shopping Cart",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (items.isEmpty()) {
            Text(
                text = "Your cart is empty",
                modifier = Modifier.testTag("emptyMessage")
            )
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { item ->
                    CartItemRow(
                        item = item,
                        onRemove = { onRemoveItem(item.id) }
                    )
                }
            }

            Divider()

            val total = items.sumOf { it.price * it.quantity }

            Text(
                text = "Total: $${"%.2f".format(total)}",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .testTag("totalPrice")
            )

            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkoutButton")
            ) {
                Text("Checkout")
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("cartItem-${item.id}"),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = item.name)
            Text(
                text = "$${"%.2f".format(item.price)} x ${item.quantity}",
                style = MaterialTheme.typography.bodySmall
            )
        }

        IconButton(
            onClick = onRemove,
            modifier = Modifier.testTag("removeButton-${item.id}")
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Remove")
        }
    }
}

data class CartItem(
    val id: String,
    val name: String,
    val price: Double,
    val quantity: Int
)
```

**Tests**:
```kotlin
// src/androidTest/kotlin/com/example/ui/CartScreenTest.kt
class CartScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun cartScreen_emptyCart_showsEmptyMessage() {
        composeTestRule.setContent {
            CartScreen(items = emptyList())
        }

        composeTestRule.onNodeWithTag("emptyMessage")
            .assertIsDisplayed()
            .assertTextEquals("Your cart is empty")
    }

    @Test
    fun cartScreen_withItems_displaysAllItems() {
        val items = listOf(
            CartItem("1", "Laptop", 999.99, 1),
            CartItem("2", "Mouse", 29.99, 2)
        )

        composeTestRule.setContent {
            CartScreen(items = items)
        }

        composeTestRule.onNodeWithTag("cartItem-1").assertIsDisplayed()
        composeTestRule.onNodeWithTag("cartItem-2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Laptop").assertExists()
        composeTestRule.onNodeWithText("Mouse").assertExists()
    }

    @Test
    fun cartScreen_calculatesCorrectTotal() {
        val items = listOf(
            CartItem("1", "Laptop", 999.99, 1),
            CartItem("2", "Mouse", 29.99, 2)
        )

        composeTestRule.setContent {
            CartScreen(items = items)
        }

        // Total: 999.99 + (29.99 * 2) = 1059.97
        composeTestRule.onNodeWithTag("totalPrice")
            .assertTextContains("$1059.97")
    }

    @Test
    fun cartScreen_clickRemove_callsOnRemoveItem() {
        val items = listOf(CartItem("1", "Laptop", 999.99, 1))
        var removedId: String? = null

        composeTestRule.setContent {
            CartScreen(
                items = items,
                onRemoveItem = { id -> removedId = id }
            )
        }

        composeTestRule.onNodeWithTag("removeButton-1").performClick()

        assertEquals("1", removedId)
    }

    @Test
    fun cartScreen_clickCheckout_callsOnCheckout() {
        val items = listOf(CartItem("1", "Laptop", 999.99, 1))
        var checkoutCalled = false

        composeTestRule.setContent {
            CartScreen(
                items = items,
                onCheckout = { checkoutCalled = true }
            )
        }

        composeTestRule.onNodeWithTag("checkoutButton").performClick()

        assertTrue(checkoutCalled)
    }
}
```

---

## Exercise 3: TDD - Build a Shopping Cart

Use TDD to build a shopping cart with these features:
- Add items
- Remove items
- Calculate total
- Apply discount codes

Write tests first, then implement!

---

## Solution 3

**Tests First**:
```kotlin
class ShoppingCartTest : StringSpec({

    "empty cart should have zero total" {
        val cart = ShoppingCart()
        cart.getTotal() shouldBe 0.0
    }

    "adding item should increase total" {
        val cart = ShoppingCart()
        cart.addItem("Laptop", 999.99)

        cart.getTotal() shouldBe 999.99
    }

    "adding same item twice should increase quantity" {
        val cart = ShoppingCart()
        cart.addItem("Mouse", 29.99)
        cart.addItem("Mouse", 29.99)

        cart.getItemCount("Mouse") shouldBe 2
        cart.getTotal() shouldBe 59.98
    }

    "removing item should decrease total" {
        val cart = ShoppingCart()
        cart.addItem("Laptop", 999.99)
        cart.removeItem("Laptop")

        cart.getTotal() shouldBe 0.0
    }

    "applying valid discount code should reduce total" {
        val cart = ShoppingCart()
        cart.addItem("Laptop", 1000.0)

        val result = cart.applyDiscount("SAVE10") // 10% off
        result.isSuccess shouldBe true
        cart.getTotal() shouldBe 900.0
    }

    "applying invalid discount code should fail" {
        val cart = ShoppingCart()
        cart.addItem("Laptop", 1000.0)

        val result = cart.applyDiscount("INVALID")
        result.isFailure shouldBe true
        cart.getTotal() shouldBe 1000.0 // Unchanged
    }

    "discount should not apply to empty cart" {
        val cart = ShoppingCart()

        val result = cart.applyDiscount("SAVE10")
        result.isFailure shouldBe true
    }
})
```

**Implementation**:
```kotlin
data class Item(val name: String, val price: Double, val quantity: Int = 1)

class ShoppingCart {
    private val items = mutableMapOf<String, Item>()
    private var discountPercent = 0.0

    fun addItem(name: String, price: Double) {
        val existing = items[name]
        if (existing != null) {
            items[name] = existing.copy(quantity = existing.quantity + 1)
        } else {
            items[name] = Item(name, price, 1)
        }
    }

    fun removeItem(name: String) {
        items.remove(name)
    }

    fun getItemCount(name: String): Int {
        return items[name]?.quantity ?: 0
    }

    fun getTotal(): Double {
        val subtotal = items.values.sumOf { it.price * it.quantity }
        return subtotal * (1 - discountPercent / 100)
    }

    fun applyDiscount(code: String): Result<Unit> {
        if (items.isEmpty()) {
            return Result.failure(Exception("Cannot apply discount to empty cart"))
        }

        val discount = when (code) {
            "SAVE10" -> 10.0
            "SAVE20" -> 20.0
            "SAVE50" -> 50.0
            else -> return Result.failure(Exception("Invalid discount code"))
        }

        discountPercent = discount
        return Result.success(Unit)
    }
}
```

---

## Why This Matters

### Career Impact

**Job Requirements**:
- 95% of backend/Android jobs require testing skills
- "Write unit tests" appears in 87% of Kotlin job postings
- Companies with good tests ship 46x more frequently

**Salary Impact**:
- Developers who write tests earn 15-20% more
- Testing expertise = senior-level skill

**Real Examples**:
- **Airbnb**: Requires 80% code coverage
- **Google**: All code changes need tests
- **Spotify**: TDD is standard practice

---

## Checkpoint Quiz

### Question 1
What's the recommended ratio in the testing pyramid?

A) 70% unit, 20% integration, 10% E2E
B) Equal distribution (33% each)
C) 100% integration tests
D) 10% unit, 90% E2E

### Question 2
What does MockK's `every` block do?

A) Runs a test multiple times
B) Defines the behavior of a mock
C) Verifies a method was called
D) Creates a real object

### Question 3
How do you test a suspending function?

A) Use `@Test suspend fun`
B) Use `runBlocking`
C) Use `runTest`
D) Can't test suspending functions

### Question 4
What does `composeTestRule.onNodeWithTag("button")` do?

A) Creates a button
B) Finds a composable with testTag("button")
C) Tags the current test
D) Deletes a button

### Question 5
What's the first step in TDD?

A) Write implementation
B) Write a failing test
C) Refactor code
D) Deploy to production

---

## Quiz Answers

**Question 1: A) 70% unit, 20% integration, 10% E2E**

The testing pyramid recommends:
- **Most**: Unit tests (fast, isolated)
- **Some**: Integration tests
- **Few**: E2E tests (slow, brittle)

---

**Question 2: B) Defines the behavior of a mock**

```kotlin
every { mockRepo.getUser(1) } returns User(1, "John")
```

Tells the mock: "When getUser(1) is called, return this user"

---

**Question 3: C) Use `runTest`**

```kotlin
@Test
fun testSuspendingFunction() = runTest {
    val result = repository.fetchData() // suspending
    assertEquals("data", result)
}
```

`runTest` provides a coroutine scope for testing.

---

**Question 4: B) Finds a composable with testTag("button")**

```kotlin
Button(modifier = Modifier.testTag("button")) { }

composeTestRule.onNodeWithTag("button").performClick()
```

Test tags help locate composables in tests.

---

**Question 5: B) Write a failing test**

TDD cycle:
1. **Red**: Write failing test
2. **Green**: Write minimal code to pass
3. **Refactor**: Improve code

---

## What You've Learned

✅ Why testing is critical for professional development
✅ The testing pyramid and when to use each test type
✅ JUnit 5 fundamentals and parameterized tests
✅ Kotest for beautiful, Kotlin-idiomatic tests
✅ MockK for powerful mocking and verification
✅ Testing coroutines and flows with kotlinx-coroutines-test
✅ Testing Jetpack Compose UI components
✅ Test-driven development (TDD) workflow
✅ Measuring code coverage with JaCoCo

---

## Next Steps

In **Lesson 7.3: Performance Optimization**, you'll learn:
- Profiling tools to identify bottlenecks
- Memory management and leak detection
- Optimizing coroutines and flows
- Compose recomposition optimization
- Database query optimization
- Network performance best practices

Great tests give you confidence to optimize fearlessly!

---

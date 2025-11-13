# Lesson 5.14: Testing Your API

**Estimated Time**: 70 minutes

---

## Topic Introduction

You've built a complete backend API with authentication, validation, and clean architecture. But how do you know it works correctly? How do you ensure new features don't break existing functionality?

The answer: **automated testing**.

In this lesson, you'll learn how to write comprehensive tests for your Ktor API, from unit tests for individual services to integration tests for full HTTP endpoints. You'll use Ktor's testing utilities and Koin's test features to build a robust test suite.

---

## The Concept

### The Safety Net Analogy

Think of tests like a safety net for trapeze artists:

**Without Tests (No Safety Net)**:
- Every code change is scary
- Fear of breaking things prevents improvements
- Bugs discovered by users (embarrassing!)
- Hours spent manually testing after each change
- 😰 High stress, low confidence

**With Tests (Safety Net)**:
- Confident refactoring
- Catch bugs before deployment
- Automated validation (run tests in seconds)
- Documentation (tests show how code should work)
- ✅ Low stress, high confidence!

Tests are your safety net—they catch you when you fall.

### The Testing Pyramid

```
          /\
         /  \        E2E Tests (Few)
        /____\       - Full system, slow, brittle
       /      \
      /        \     Integration Tests (Some)
     /__________\    - Multiple components, medium speed
    /            \
   /              \  Unit Tests (Many)
  /________________\ - Single component, fast, reliable
```

**Test Distribution**:
- **70%** Unit Tests: Fast, isolated, test individual functions
- **20%** Integration Tests: Test components working together
- **10%** End-to-End Tests: Test entire system from UI to database

We'll focus on unit and integration tests for backend APIs.

### Types of Tests for APIs

| Test Type | What It Tests | Example |
|-----------|---------------|---------|
| **Unit** | Single function/class in isolation | UserService.createUser() with mock repository |
| **Integration** | Multiple components together | POST /api/users endpoint with real database |
| **Contract** | API matches specification | Response has required fields |
| **Performance** | Speed and scalability | API handles 1000 req/sec |

---

## Setting Up Testing

### Step 1: Add Test Dependencies

Update your `build.gradle.kts`:

```kotlin
dependencies {
    // Production dependencies
    implementation("io.ktor:ktor-server-core-jvm:3.0.2")
    implementation("io.ktor:ktor-server-cio-jvm:3.0.2")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.2")
    implementation("io.ktor:ktor-server-auth-jvm:3.0.2")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:3.0.2")
    implementation("org.jetbrains.exposed:exposed-core:0.50.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("de.nycode:bcrypt:2.3.0")
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("io.insert-koin:koin-ktor:4.0.3")
    implementation("io.insert-koin:koin-logger-slf4j:4.0.3")

    // Test dependencies
    testImplementation("io.ktor:ktor-server-test-host:3.0.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("io.insert-koin:koin-test:4.0.3")
    testImplementation("io.insert-koin:koin-test-junit5:4.0.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
```

---

## Unit Testing Services

### Example: Testing UserService

```kotlin
// src/test/kotlin/com/example/services/UserServiceTest.kt
package com.example.services

import com.example.models.User
import com.example.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Mock implementation for testing
 */
class MockUserRepository : UserRepository {
    private val users = mutableMapOf<Int, User>()
    private val emails = mutableSetOf<String>()
    private var nextId = 1

    override fun insert(
        email: String,
        passwordHash: String,
        fullName: String,
        role: String
    ): Int {
        val id = nextId++
        users[id] = User(
            id = id,
            email = email,
            fullName = fullName,
            role = role,
            createdAt = "2025-01-01T00:00:00"
        )
        emails.add(email)
        return id
    }

    override fun getById(id: Int): User? = users[id]

    override fun getByEmail(email: String): User? =
        users.values.find { it.email == email }

    override fun getPasswordHash(email: String): String? = null

    override fun emailExists(email: String): Boolean = emails.contains(email)

    fun reset() {
        users.clear()
        emails.clear()
        nextId = 1
    }
}

class UserServiceTest {

    private lateinit var mockUserRepository: MockUserRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        mockUserRepository = MockUserRepository()
        userService = UserService(mockUserRepository)
    }

    @Test
    fun `should create user successfully`() {
        // Arrange
        val email = "test@example.com"
        val fullName = "Test User"

        // Act
        val result = userService.createUser(
            email = email,
            passwordHash = "hash",
            fullName = fullName,
            role = "USER"
        )

        // Assert
        assertTrue(result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals(email, user?.email)
        assertEquals(fullName, user?.fullName)
        assertEquals("USER", user?.role)
    }

    @Test
    fun `should fail when email already exists`() {
        // Arrange - create first user
        userService.createUser(
            email = "test@example.com",
            passwordHash = "hash",
            fullName = "First User",
            role = "USER"
        )

        // Act - try to create second user with same email
        val result = userService.createUser(
            email = "test@example.com",
            passwordHash = "hash2",
            fullName = "Second User",
            role = "USER"
        )

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is com.example.exceptions.ConflictException)
    }

    @Test
    fun `should retrieve user by ID`() {
        // Arrange
        val createResult = userService.createUser(
            email = "test@example.com",
            passwordHash = "hash",
            fullName = "Test User",
            role = "USER"
        )
        val userId = createResult.getOrNull()?.id!!

        // Act
        val result = userService.getUserById(userId)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("test@example.com", result.getOrNull()?.email)
    }

    @Test
    fun `should return not found for non-existent user`() {
        // Act
        val result = userService.getUserById(999)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is com.example.exceptions.NotFoundException)
    }

    @Test
    fun `should update user profile`() {
        // Arrange
        val createResult = userService.createUser(
            email = "test@example.com",
            passwordHash = "hash",
            fullName = "Original Name",
            role = "USER"
        )
        val userId = createResult.getOrNull()?.id!!

        // Act
        val updateResult = userService.updateProfile(
            userId = userId,
            fullName = "Updated Name"
        )

        // Assert
        assertTrue(updateResult.isSuccess)
        assertEquals("Updated Name", updateResult.getOrNull()?.fullName)
    }
}
```

### Running Unit Tests

```bash
./gradlew test
```

Output:
```
UserServiceTest > should create user successfully PASSED
UserServiceTest > should fail when email already exists PASSED
UserServiceTest > should retrieve user by ID PASSED
UserServiceTest > should return not found for non-existent user PASSED
UserServiceTest > should update user profile PASSED

BUILD SUCCESSFUL in 2s
5 tests completed, 5 passed
```

---

## Integration Testing Endpoints

### Example: Testing Auth Endpoints

```kotlin
// src/test/kotlin/com/example/routes/AuthRoutesTest.kt
package com.example.routes

import com.example.database.DatabaseFactory
import com.example.di.appModules
import com.example.models.ApiResponse
import com.example.models.LoginRequest
import com.example.models.LoginResponse
import com.example.models.RegisterRequest
import com.example.module
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthRoutesTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            // Initialize test database
            DatabaseFactory.init()
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            stopKoin()
        }
    }

    @Test
    fun `test user registration`() = testApplication {
        application {
            module()  // Load your application module
        }

        // Create HTTP client with JSON support
        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        // Send registration request
        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "test@example.com",
                    password = "SecurePass123!",
                    fullName = "Test User"
                )
            )
        }

        // Assert response
        assertEquals(HttpStatusCode.Created, response.status)

        val apiResponse = response.body<ApiResponse<RegisterResponse>>()
        assertTrue(apiResponse.success)
        assertNotNull(apiResponse.data)
        assertEquals("test@example.com", apiResponse.data?.user?.email)
        assertEquals("Test User", apiResponse.data?.user?.fullName)
    }

    @Test
    fun `test user registration with weak password`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "test2@example.com",
                    password = "weak",  // Weak password
                    fullName = "Test User 2"
                )
            )
        }

        // Assert validation error
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val apiResponse = response.body<ErrorResponse>()
        assertEquals(false, apiResponse.success)
        assertNotNull(apiResponse.errors)
        assertTrue(apiResponse.errors!!.containsKey("password"))
    }

    @Test
    fun `test user login`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        // First, register a user
        client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "login@example.com",
                    password = "SecurePass123!",
                    fullName = "Login User"
                )
            )
        }

        // Now, login with credentials
        val loginResponse = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    email = "login@example.com",
                    password = "SecurePass123!"
                )
            )
        }

        // Assert successful login
        assertEquals(HttpStatusCode.OK, loginResponse.status)

        val apiResponse = loginResponse.body<ApiResponse<LoginResponse>>()
        assertTrue(apiResponse.success)
        assertNotNull(apiResponse.data)
        assertNotNull(apiResponse.data?.token)
        assertEquals("login@example.com", apiResponse.data?.user?.email)
    }

    @Test
    fun `test login with wrong password`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        // Register user
        client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "wrong@example.com",
                    password = "SecurePass123!",
                    fullName = "Wrong User"
                )
            )
        }

        // Try to login with wrong password
        val loginResponse = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    email = "wrong@example.com",
                    password = "WrongPassword!"
                )
            )
        }

        // Assert unauthorized
        assertEquals(HttpStatusCode.Unauthorized, loginResponse.status)

        val apiResponse = loginResponse.body<ErrorResponse>()
        assertEquals(false, apiResponse.success)
        assertEquals("Invalid email or password", apiResponse.message)
    }

    @Test
    fun `test duplicate email registration`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        // Register first user
        client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "duplicate@example.com",
                    password = "SecurePass123!",
                    fullName = "First User"
                )
            )
        }

        // Try to register second user with same email
        val response = client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "duplicate@example.com",
                    password = "DifferentPass456!",
                    fullName = "Second User"
                )
            )
        }

        // Assert conflict error
        assertEquals(HttpStatusCode.Conflict, response.status)

        val apiResponse = response.body<ErrorResponse>()
        assertEquals(false, apiResponse.success)
        assertTrue(apiResponse.message.contains("already exists"))
    }
}
```

---

## Testing Protected Endpoints

```kotlin
// src/test/kotlin/com/example/routes/UserRoutesTest.kt
package com.example.routes

import com.example.database.DatabaseFactory
import com.example.models.*
import com.example.module
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.koin.core.context.stopKoin
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserRoutesTest {

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            DatabaseFactory.init()
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            stopKoin()
        }
    }

    /**
     * Helper function to register and login, returning the JWT token
     */
    private suspend fun ApplicationTestBuilder.registerAndLogin(
        client: io.ktor.client.HttpClient,
        email: String = "test@example.com",
        password: String = "SecurePass123!",
        fullName: String = "Test User"
    ): String {
        // Register
        client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(email, password, fullName))
        }

        // Login
        val loginResponse = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }

        val apiResponse = loginResponse.body<ApiResponse<LoginResponse>>()
        return apiResponse.data?.token ?: throw Exception("No token received")
    }

    @Test
    fun `test get current user profile`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        // Get token
        val token = registerAndLogin(client, email = "profile@example.com")

        // Get profile
        val response = client.get("/api/users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)

        val apiResponse = response.body<ApiResponse<User>>()
        assertNotNull(apiResponse.data)
        assertEquals("profile@example.com", apiResponse.data?.email)
    }

    @Test
    fun `test access protected route without token`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        // Try to access without token
        val response = client.get("/api/users/me")

        // Assert unauthorized
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test access protected route with invalid token`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        // Try with invalid token
        val response = client.get("/api/users/me") {
            header(HttpHeaders.Authorization, "Bearer invalid-token")
        }

        // Assert unauthorized
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `test update user profile`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }

        val token = registerAndLogin(
            client,
            email = "update@example.com",
            fullName = "Original Name"
        )

        // Update profile
        val response = client.put("/api/users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(UpdateProfileRequest(fullName = "Updated Name"))
        }

        // Assert
        assertEquals(HttpStatusCode.OK, response.status)

        val apiResponse = response.body<ApiResponse<User>>()
        assertEquals("Updated Name", apiResponse.data?.fullName)
    }
}
```

---

## Testing with Koin

### Test Module Setup

```kotlin
// src/test/kotlin/com/example/TestModule.kt
package com.example

import com.example.repositories.UserRepository
import com.example.services.AuthService
import com.example.services.UserService
import org.koin.dsl.module

class MockUserRepository : UserRepository {
    // ... implementation
}

val testModule = module {
    single<UserRepository> { MockUserRepository() }
    single { UserService(get()) }
    single { AuthService(get()) }
}

// Usage in tests
@ExtendWith(KoinExtension::class)
@KoinTest
class MyServiceTest {

    @BeforeEach
    fun setup() {
        startKoin {
            modules(testModule)
        }
    }

    @AfterEach
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `test with Koin`() {
        val userService by inject<UserService>()
        // Test using injected service
    }
}
```

---

## Test Coverage

### Generate Coverage Report

Add JaCoCo plugin to `build.gradle.kts`:

```kotlin
plugins {
    kotlin("jvm") version "2.0.0"
    application
    kotlin("plugin.serialization") version "2.0.0"
    id("jacoco")  // Add JaCoCo plugin
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
            fileTree("build/classes/kotlin/main") {
                exclude("**/Application**")
            }
        )
    )
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
```

Run tests with coverage:
```bash
./gradlew test jacocoTestReport
```

View report at: `build/reports/jacoco/test/html/index.html`

---

## Best Practices

### 1. Test Naming Convention

```kotlin
@Test
fun `should create user when valid data provided`() { }

@Test
fun `should throw ValidationException when email is invalid`() { }

@Test
fun `should return 401 when accessing protected route without token`() { }
```

Use backticks for descriptive test names that read like sentences.

### 2. AAA Pattern

```kotlin
@Test
fun `should update user profile`() {
    // Arrange - Set up test data
    val user = createTestUser()
    val updateRequest = UpdateProfileRequest(fullName = "New Name")

    // Act - Perform the action
    val result = userService.updateProfile(user.id, updateRequest)

    // Assert - Verify the outcome
    assertTrue(result.isSuccess)
    assertEquals("New Name", result.getOrNull()?.fullName)
}
```

### 3. Test Isolation

```kotlin
@BeforeEach
fun setup() {
    // Reset state before each test
    mockRepository.reset()
}

@AfterEach
fun cleanup() {
    // Clean up resources
    stopKoin()
}
```

Each test should be independent and not affect others.

### 4. Test Data Builders

```kotlin
object TestDataBuilder {
    fun createUser(
        id: Int = 1,
        email: String = "test@example.com",
        fullName: String = "Test User",
        role: String = "USER"
    ) = User(
        id = id,
        email = email,
        fullName = fullName,
        role = role,
        createdAt = "2025-01-01T00:00:00"
    )

    fun createRegisterRequest(
        email: String = "test@example.com",
        password: String = "SecurePass123!",
        fullName: String = "Test User"
    ) = RegisterRequest(email, password, fullName)
}
```

---

## Exercise: Complete Test Suite

Write a complete test suite for the Post API.

### Requirements

1. **Unit Tests for PostService**:
   - Test create post
   - Test update post with ownership check
   - Test delete post with ownership check
   - Test get posts by user

2. **Integration Tests for Post Routes**:
   - Test POST /api/posts (create post)
   - Test GET /api/posts (get all posts)
   - Test PUT /api/posts/:id (update post - owner only)
   - Test DELETE /api/posts/:id (delete post - owner only)
   - Test authorization (user can't modify others' posts)
   - Test admin can modify any post

3. **Test Coverage**:
   - Aim for 80%+ coverage on services
   - Test all error paths (validation, not found, forbidden)

---

## Solution

```kotlin
// src/test/kotlin/com/example/services/PostServiceTest.kt
package com.example.services

import com.example.models.Post
import com.example.models.CreatePostRequest
import com.example.plugins.UserPrincipal
import com.example.repositories.PostRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class MockPostRepository : PostRepository {
    private val posts = mutableMapOf<Int, Post>()
    private var nextId = 1

    override fun insert(title: String, content: String, authorId: Int): Int {
        val id = nextId++
        posts[id] = Post(
            id = id,
            title = title,
            content = content,
            authorId = authorId,
            authorName = "Test User",
            createdAt = "2025-01-01T00:00:00"
        )
        return id
    }

    override fun update(id: Int, title: String, content: String): Boolean {
        val post = posts[id] ?: return false
        posts[id] = post.copy(title = title, content = content)
        return true
    }

    override fun delete(id: Int): Boolean {
        return posts.remove(id) != null
    }

    override fun getById(id: Int): Post? = posts[id]

    override fun getAll(): List<Post> = posts.values.toList()

    fun reset() {
        posts.clear()
        nextId = 1
    }
}

class PostServiceTest {

    private lateinit var mockPostRepository: MockPostRepository
    private lateinit var postService: PostService

    @BeforeEach
    fun setup() {
        mockPostRepository = MockPostRepository()
        postService = PostService(mockPostRepository)
    }

    @Test
    fun `should create post successfully`() {
        // Arrange
        val request = CreatePostRequest(
            title = "Test Post",
            content = "Test content"
        )
        val principal = UserPrincipal(1, "test@example.com", "USER")

        // Act
        val result = postService.createPost(request, principal)

        // Assert
        assertTrue(result.isSuccess)
        val post = result.getOrNull()
        assertNotNull(post)
        assertEquals("Test Post", post?.title)
        assertEquals(1, post?.authorId)
    }

    @Test
    fun `should allow owner to update post`() {
        // Arrange
        val principal = UserPrincipal(1, "test@example.com", "USER")
        val createRequest = CreatePostRequest("Original", "Content")
        val postId = postService.createPost(createRequest, principal).getOrNull()?.id!!

        // Act
        val updateRequest = UpdatePostRequest("Updated", "New content")
        val result = postService.updatePost(postId, updateRequest, principal)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Updated", result.getOrNull()?.title)
    }

    @Test
    fun `should deny non-owner from updating post`() {
        // Arrange
        val owner = UserPrincipal(1, "owner@example.com", "USER")
        val attacker = UserPrincipal(2, "attacker@example.com", "USER")

        val createRequest = CreatePostRequest("Owner's Post", "Content")
        val postId = postService.createPost(createRequest, owner).getOrNull()?.id!!

        // Act
        val updateRequest = UpdatePostRequest("Hacked", "Bad content")
        val result = postService.updatePost(postId, updateRequest, attacker)

        // Assert
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue(exception is ForbiddenException)
    }

    @Test
    fun `should allow admin to update any post`() {
        // Arrange
        val user = UserPrincipal(1, "user@example.com", "USER")
        val admin = UserPrincipal(2, "admin@example.com", "ADMIN")

        val createRequest = CreatePostRequest("User's Post", "Content")
        val postId = postService.createPost(createRequest, user).getOrNull()?.id!!

        // Act
        val updateRequest = UpdatePostRequest("Admin Edit", "Updated by admin")
        val result = postService.updatePost(postId, updateRequest, admin)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals("Admin Edit", result.getOrNull()?.title)
    }

    @Test
    fun `should delete post when owner requests`() {
        // Arrange
        val principal = UserPrincipal(1, "test@example.com", "USER")
        val createRequest = CreatePostRequest("Delete Me", "Content")
        val postId = postService.createPost(createRequest, principal).getOrNull()?.id!!

        // Act
        val result = postService.deletePost(postId, principal)

        // Assert
        assertTrue(result.isSuccess)

        // Verify post is gone
        val getResult = postService.getPostById(postId)
        assertTrue(getResult.isFailure)
    }
}

// src/test/kotlin/com/example/routes/PostRoutesTest.kt
package com.example.routes

import com.example.database.DatabaseFactory
import com.example.models.*
import com.example.module
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.koin.core.context.stopKoin
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostRoutesTest {

    @BeforeAll
    fun setup() {
        DatabaseFactory.init()
    }

    @AfterAll
    fun teardown() {
        stopKoin()
    }

    private suspend fun ApplicationTestBuilder.getToken(
        client: io.ktor.client.HttpClient,
        email: String,
        password: String = "SecurePass123!"
    ): String {
        // Register
        client.post("/api/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(email, password, email.substringBefore("@")))
        }

        // Login
        val loginResponse = client.post("/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }

        return loginResponse.body<ApiResponse<LoginResponse>>().data?.token!!
    }

    @Test
    fun `test create post`() = testApplication {
        application { module() }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val token = getToken(client, "post-creator@example.com")

        // Create post
        val response = client.post("/api/posts") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CreatePostRequest("My Post", "Post content"))
        }

        assertEquals(HttpStatusCode.Created, response.status)

        val apiResponse = response.body<ApiResponse<Post>>()
        assertTrue(apiResponse.success)
        assertEquals("My Post", apiResponse.data?.title)
    }

    @Test
    fun `test user cannot update others post`() = testApplication {
        application { module() }

        val client = createClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        // User A creates post
        val tokenA = getToken(client, "usera@example.com")
        val createResponse = client.post("/api/posts") {
            header(HttpHeaders.Authorization, "Bearer $tokenA")
            contentType(ContentType.Application.Json)
            setBody(CreatePostRequest("User A Post", "Content"))
        }
        val postId = createResponse.body<ApiResponse<Post>>().data?.id!!

        // User B tries to update
        val tokenB = getToken(client, "userb@example.com")
        val updateResponse = client.put("/api/posts/$postId") {
            header(HttpHeaders.Authorization, "Bearer $tokenB")
            contentType(ContentType.Application.Json)
            setBody(UpdatePostRequest("Hacked", "Bad content"))
        }

        assertEquals(HttpStatusCode.Forbidden, updateResponse.status)
    }
}
```

---

## Why This Matters

### Real-World Impact

**Companies With Good Tests**:
- Deploy multiple times per day with confidence
- Catch bugs before users do
- Refactor fearlessly
- Onboard new developers faster (tests are documentation)
- Lower maintenance costs

**Companies Without Tests**:
- Manual testing takes hours
- Fear of changing code
- Bugs discovered in production
- Slow feature development
- High stress, long hours

**Statistics**:
- Bugs caught in testing cost 10x less than bugs in production
- Well-tested code has 40-80% fewer production bugs
- Test suites pay for themselves within 6 months

---

## Checkpoint Quiz

### Question 1
What's the testing pyramid ratio for a backend API?

A) 10% unit, 20% integration, 70% E2E
B) 70% unit, 20% integration, 10% E2E
C) Equal distribution (33% each)
D) 100% integration tests only

### Question 2
What does the AAA pattern stand for in testing?

A) Assert, Act, Arrange
B) Arrange, Act, Assert
C) Always Automate Assertions
D) API, Authentication, Authorization

### Question 3
Why use mock repositories in unit tests?

A) They're faster than real databases
B) They provide test isolation and don't require database setup
C) They're required by JUnit
D) They generate better test reports

### Question 4
What HTTP status code should a test expect when accessing a protected route without a token?

A) 200 OK
B) 400 Bad Request
C) 401 Unauthorized
D) 404 Not Found

### Question 5
What's the main benefit of high test coverage?

A) Makes code run faster
B) Reduces file size
C) Increases confidence that code works correctly
D) Automatically fixes bugs

---

## Quiz Answers

**Question 1: B) 70% unit, 20% integration, 10% E2E**

The testing pyramid recommends:
- **Most**: Unit tests (fast, cheap, isolated)
- **Some**: Integration tests (medium speed, test combinations)
- **Few**: E2E tests (slow, expensive, brittle)

---

**Question 2: B) Arrange, Act, Assert**

```kotlin
@Test
fun `example test`() {
    // Arrange - Set up test data and dependencies
    val user = createTestUser()

    // Act - Perform the action being tested
    val result = userService.deleteUser(user.id)

    // Assert - Verify the outcome
    assertTrue(result.isSuccess)
}
```

---

**Question 3: B) They provide test isolation and don't require database setup**

Mock repositories:
- No database needed (tests run in memory)
- Fast execution (no I/O overhead)
- Complete control (easily simulate edge cases)
- Isolated (one test doesn't affect another)

---

**Question 4: C) 401 Unauthorized**

HTTP status codes in authentication:
- **401 Unauthorized**: Missing or invalid credentials/token
- **403 Forbidden**: Authenticated but not authorized (valid token, insufficient permissions)

---

**Question 5: C) Increases confidence that code works correctly**

Test coverage shows which code paths are tested:
- 80%+ coverage = most code is verified
- Low coverage = many code paths untested (likely bugs)
- Confidence to refactor and add features

Note: 100% coverage doesn't guarantee bug-free code, but it helps!

---

## What You've Learned

✅ Why automated testing is critical for maintainable codebases
✅ The testing pyramid and when to use each test type
✅ How to write unit tests for services with mock repositories
✅ How to write integration tests for HTTP endpoints with testApplication
✅ How to test protected routes requiring JWT authentication
✅ How to test authorization (ownership and role-based access)
✅ Best practices: AAA pattern, test isolation, descriptive names
✅ How to measure test coverage with JaCoCo

---

## Next Steps

In **Lesson 5.15: Part 5 Capstone Project**, you'll build a complete production-ready API from scratch using everything you've learned:
- Full authentication system (registration, login, JWT)
- Role-based access control
- Clean architecture (repositories, services, routes)
- Dependency injection with Koin
- Comprehensive test suite
- Validation and error handling

Time to put all your knowledge together into a real-world application!

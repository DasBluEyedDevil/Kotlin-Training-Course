# Lesson 5.13: Dependency Injection with Koin

**Estimated Time**: 60 minutes

---

## Topic Introduction

Look at your Application.kt file. You've been manually creating and wiring dependencies:

```kotlin
val userRepository = UserRepositoryImpl()
val userService = UserService(userRepository)
val authService = AuthService(userRepository)
```

This works for small applications, but as your app grows, manual dependency management becomes unwieldy:
- Hard to test (can't easily swap implementations)
- Violates Single Responsibility Principle (Application.kt does too much)
- Difficult to manage complex dependency graphs
- No compile-time safety for missing dependencies

**Dependency Injection** (DI) frameworks solve these problems. In this lesson, you'll learn Koin—the most popular DI framework for Kotlin.

---

## The Concept

### The Restaurant Kitchen Analogy

Think of dependency injection like a restaurant kitchen:

**Without DI (Manual Wiring)**:
- Chef makes every ingredient from scratch
- Chef grows vegetables, mills flour, butchers meat
- Result: Chef spends all day preparing ingredients, no time to cook!
- Can't easily swap ingredients (hard to test recipes)

**With DI (Koin)**:
- Chef receives pre-prepared ingredients
- Pantry manager (Koin) provides what chef needs
- Chef just cooks (focuses on business logic)
- Easy to swap ingredients (mock data for testing)
- ✅ Clean separation of concerns!

Koin is your "pantry manager" that provides dependencies when needed.

### What is Dependency Injection?

**Dependency**: An object that another object needs to function

```kotlin
class UserService(
    private val userRepository: UserRepository  // UserService depends on UserRepository
)
```

**Injection**: Providing dependencies from the outside, rather than creating them inside

```kotlin
// ❌ Without DI: UserService creates its own dependency
class UserService {
    private val userRepository = UserRepositoryImpl()  // Hard-coded!
}

// ✅ With DI: Dependency provided from outside
class UserService(
    private val userRepository: UserRepository  // Injected via constructor
)
```

### Why Dependency Injection?

| Without DI | With DI |
|------------|---------|
| Hard-coded dependencies | Flexible, swappable dependencies |
| Difficult to test | Easy to mock and test |
| Tight coupling | Loose coupling |
| Manual wiring everywhere | Centralized configuration |
| No compile-time safety | Type-safe resolution |

### Koin vs Other DI Frameworks

| Framework | Approach | Pros | Cons |
|-----------|----------|------|------|
| **Koin** | Service locator pattern | Simple, lightweight, Kotlin-first | Runtime errors if misconfigured |
| **Dagger** | Code generation | Compile-time safety, fast runtime | Complex, steep learning curve |
| **Manual** | Factories, builders | Full control | Tedious, error-prone |

For Kotlin backend development, **Koin is the sweet spot**: simple yet powerful.

---

## Setting Up Koin

### Step 1: Add Koin Dependency

Update your `build.gradle.kts`:

```kotlin
dependencies {
    // Existing dependencies
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

    // Koin for Dependency Injection
    implementation("io.insert-koin:koin-ktor:4.0.3")
    implementation("io.insert-koin:koin-logger-slf4j:4.0.3")
}
```

### Step 2: Define Koin Modules

Create a configuration file that declares all your dependencies:

```kotlin
// src/main/kotlin/com/example/di/AppModule.kt
package com.example.di

import com.example.repositories.UserRepository
import com.example.repositories.UserRepositoryImpl
import com.example.services.AuthService
import com.example.services.UserService
import org.koin.dsl.module

/**
 * Koin module defining all application dependencies
 */
val appModule = module {

    // Repositories
    single<UserRepository> { UserRepositoryImpl() }

    // Services
    single { UserService(get()) }
    single { AuthService(get()) }
}
```

**Key Koin DSL functions**:
- `single { }`: Creates a singleton (one instance for entire app)
- `factory { }`: Creates a new instance every time
- `get()`: Resolves a dependency from Koin

### Step 3: Install Koin in Ktor

Update your Application.kt:

```kotlin
// src/main/kotlin/com/example/Application.kt
package com.example

import com.example.database.DatabaseFactory
import com.example.di.appModule
import com.example.plugins.configureAuthentication
import com.example.plugins.configureErrorHandling
import com.example.routes.adminRoutes
import com.example.routes.authRoutes
import com.example.routes.userRoutes
import com.example.services.AuthService
import com.example.services.UserService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(CIO, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    // Install Koin
    install(Koin) {
        slf4jLogger()  // Use SLF4J for Koin logging
        modules(appModule)  // Load our module
    }

    // Install other plugins
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }

    configureErrorHandling()
    configureAuthentication()

    // Initialize database
    DatabaseFactory.init()

    // Inject dependencies from Koin
    val userService by inject<UserService>()
    val authService by inject<AuthService>()

    // Configure routes
    routing {
        authRoutes(userService, authService)
        userRoutes(userService)
        adminRoutes(userService)
    }
}
```

**Before Koin**:
```kotlin
// Manual dependency creation
val userRepository = UserRepositoryImpl()
val userService = UserService(userRepository)
val authService = AuthService(userRepository)
```

**After Koin**:
```kotlin
// Automatic dependency injection
val userService by inject<UserService>()
val authService by inject<AuthService>()
```

Much cleaner! Koin handles all the wiring automatically.

---

## Advanced Koin Features

### Organizing Modules

As your app grows, split modules by feature:

```kotlin
// src/main/kotlin/com/example/di/RepositoryModule.kt
package com.example.di

import com.example.repositories.*
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<PostRepository> { PostRepositoryImpl() }
    single<CommentRepository> { CommentRepositoryImpl() }
}

// src/main/kotlin/com/example/di/ServiceModule.kt
package com.example.di

import com.example.services.*
import org.koin.dsl.module

val serviceModule = module {
    single { UserService(get()) }
    single { AuthService(get()) }
    single { PostService(get()) }
    single { CommentService(get(), get()) }  // Multiple dependencies
}

// src/main/kotlin/com/example/di/AppModule.kt
package com.example.di

import org.koin.dsl.module

val appModule = module {
    includes(repositoryModule, serviceModule)
}
```

Load all modules:
```kotlin
install(Koin) {
    slf4jLogger()
    modules(repositoryModule, serviceModule)
    // Or just: modules(appModule)
}
```

### Named Dependencies

Sometimes you need multiple instances of the same type:

```kotlin
val databaseModule = module {
    // Main database
    single(named("mainDb")) {
        Database.connect("jdbc:h2:mem:main")
    }

    // Analytics database
    single(named("analyticsDb")) {
        Database.connect("jdbc:h2:mem:analytics")
    }

    // Repository using specific database
    single {
        UserRepositoryImpl(database = get(named("mainDb")))
    }
}
```

### Scopes

Koin supports scoped instances (created per request, per session, etc.):

```kotlin
val scopedModule = module {
    // Request-scoped instance (new for each HTTP request)
    scope<RequestScope> {
        scoped { RequestContext() }
    }
}
```

### Factory vs Single

```kotlin
val exampleModule = module {
    // Single: One instance for entire application
    single { EmailService() }  // Reused everywhere

    // Factory: New instance every time
    factory { EmailMessage() }  // Fresh message each time
}
```

**When to use each**:
- **Single**: Services, repositories, database connections (stateless or shared state)
- **Factory**: Request/response objects, temporary data (stateful per-request)

---

## Dependency Injection in Routes

You can inject dependencies directly in route functions:

```kotlin
// src/main/kotlin/com/example/routes/UserRoutes.kt
package com.example.routes

import com.example.models.ApiResponse
import com.example.plugins.UserPrincipal
import com.example.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.userRoutes() {
    // Inject UserService directly in the route
    val userService by inject<UserService>()

    authenticate("jwt-auth") {
        route("/api/users") {
            get("/me") {
                val principal = call.principal<UserPrincipal>()!!

                userService.getUserById(principal.userId)
                    .onSuccess { user ->
                        call.respond(ApiResponse(data = user))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }
        }
    }
}
```

Update routing setup:
```kotlin
routing {
    authRoutes()    // No need to pass dependencies!
    userRoutes()
    adminRoutes()
}
```

---

## Testing with Koin

Koin makes testing incredibly easy by allowing you to swap implementations:

```kotlin
// src/test/kotlin/com/example/UserServiceTest.kt
package com.example

import com.example.di.appModule
import com.example.models.User
import com.example.repositories.UserRepository
import com.example.services.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Mock repository for testing
 */
class MockUserRepository : UserRepository {
    private val users = mutableMapOf<Int, User>()
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
        return id
    }

    override fun getById(id: Int): User? = users[id]

    override fun getByEmail(email: String): User? =
        users.values.find { it.email == email }

    override fun getPasswordHash(email: String): String? = null
    override fun emailExists(email: String): Boolean = getByEmail(email) != null
}

/**
 * Test module with mock dependencies
 */
val testModule = module {
    single<UserRepository> { MockUserRepository() }  // Mock instead of real
    single { UserService(get()) }
}

class UserServiceTest : KoinTest {

    // Inject UserService (using mock repository)
    private val userService: UserService by inject()

    @BeforeEach
    fun setup() {
        startKoin {
            modules(testModule)  // Load test module instead of app module
        }
    }

    @AfterEach
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `test user creation`() {
        // This uses MockUserRepository, no real database needed!
        val user = userService.createUser(
            email = "test@example.com",
            passwordHash = "hash",
            fullName = "Test User",
            role = "USER"
        ).getOrNull()

        assertNotNull(user)
        assertEquals("test@example.com", user.email)
        assertEquals("Test User", user.fullName)
    }
}
```

**Benefits**:
- No database setup needed
- Fast tests (in-memory mock data)
- Easy to simulate different scenarios
- Complete isolation between tests

---

## Complete Example: Refactoring to Koin

Let's refactor our entire application to use Koin:

### Module Definitions

```kotlin
// src/main/kotlin/com/example/di/DatabaseModule.kt
package com.example.di

import com.example.database.DatabaseFactory
import org.koin.dsl.module

val databaseModule = module {
    single { DatabaseFactory }
}

// src/main/kotlin/com/example/di/RepositoryModule.kt
package com.example.di

import com.example.repositories.*
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl() }
    single<PostRepository> { PostRepositoryImpl() }
}

// src/main/kotlin/com/example/di/ServiceModule.kt
package com.example.di

import com.example.services.*
import org.koin.dsl.module

val serviceModule = module {
    single { UserService(get()) }
    single { AuthService(get()) }
    single { PostService(get()) }
}

// src/main/kotlin/com/example/di/AppModule.kt
package com.example.di

import org.koin.dsl.module

val appModules = listOf(
    databaseModule,
    repositoryModule,
    serviceModule
)
```

### Application Setup

```kotlin
// src/main/kotlin/com/example/Application.kt
package com.example

import com.example.database.DatabaseFactory
import com.example.di.appModules
import com.example.plugins.configureAuthentication
import com.example.plugins.configureErrorHandling
import com.example.routes.adminRoutes
import com.example.routes.authRoutes
import com.example.routes.postRoutes
import com.example.routes.userRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun main() {
    embeddedServer(CIO, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    // Install Koin
    install(Koin) {
        slf4jLogger()
        modules(appModules)
    }

    // Install other plugins
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }

    configureErrorHandling()
    configureAuthentication()

    // Initialize database
    val databaseFactory by inject<DatabaseFactory>()
    databaseFactory.init()

    // Configure routes (no manual dependency passing!)
    routing {
        authRoutes()
        userRoutes()
        adminRoutes()
        postRoutes()
    }
}
```

### Routes with Injection

```kotlin
// src/main/kotlin/com/example/routes/AuthRoutes.kt
package com.example.routes

import com.example.models.ApiResponse
import com.example.models.LoginRequest
import com.example.models.RegisterRequest
import com.example.models.RegisterResponse
import com.example.services.AuthService
import com.example.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.authRoutes() {
    // Inject dependencies
    val userService by inject<UserService>()
    val authService by inject<AuthService>()

    route("/api/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()

            userService.register(request)
                .onSuccess { user ->
                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(
                            data = RegisterResponse(
                                user = user,
                                message = "Registration successful"
                            )
                        )
                    )
                }
                .onFailure { error ->
                    throw error
                }
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            authService.login(request)
                .onSuccess { loginResponse ->
                    call.respond(ApiResponse(data = loginResponse))
                }
                .onFailure { error ->
                    throw error
                }
        }
    }
}
```

---

## Code Breakdown

### Dependency Resolution Flow

```
1. Application starts
   ↓
2. install(Koin) { modules(appModules) }
   ↓
3. Koin reads module definitions:
   - single<UserRepository> { UserRepositoryImpl() }
   - single { UserService(get()) }
   - single { AuthService(get()) }
   ↓
4. Route calls: val userService by inject<UserService>()
   ↓
5. Koin resolves dependencies:
   - UserService needs UserRepository
   - Look up UserRepository → UserRepositoryImpl
   - Create UserRepositoryImpl (if not already created)
   - Pass to UserService constructor
   - Return UserService instance
   ↓
6. Route uses userService
```

### get() Function

The `get()` function resolves dependencies:

```kotlin
single { UserService(get()) }
              // ↑ Koin resolves UserRepository here

single { CommentService(get(), get()) }
                 // ↑         ↑
                 // |         PostRepository
                 // UserRepository
```

Type inference determines what to inject based on parameter types.

### by inject<T>() Delegate

```kotlin
val userService by inject<UserService>()
```

This is a **lazy delegate**:
- `userService` is resolved when first accessed (lazy)
- Subsequent accesses return the same instance (for singletons)
- Type-safe (compile-time checking)

---

## Exercise: Multi-Tenant Application

Build a multi-tenant blog platform where each tenant has isolated data.

### Requirements

1. **Tenant Context**:
   - Extract tenant ID from request header: `X-Tenant-ID`
   - Store in request-scoped object

2. **Tenant-Specific Repositories**:
   - Each tenant has separate database schema
   - Repositories filter by tenant ID automatically

3. **Koin Scopes**:
   - Create request scope for tenant context
   - Inject tenant-aware repositories

4. **Implementation**:
   ```kotlin
   // Tenant context
   data class TenantContext(val tenantId: String)

   // Tenant-aware repository
   class TenantUserRepository(private val tenantContext: TenantContext) : UserRepository {
       override fun getAll(): List<User> {
           // Filter by tenantContext.tenantId
       }
   }
   ```

### Starter Code

```kotlin
val tenantModule = module {
    // TODO: Define request scope
    // TODO: Provide TenantContext from request header
    // TODO: Provide tenant-aware repositories
}

// TODO: Create middleware to extract tenant ID
// TODO: Inject tenant-aware repositories in routes
```

---

## Solution

### Complete Multi-Tenant System

```kotlin
// src/main/kotlin/com/example/models/TenantContext.kt
package com.example.models

/**
 * Request-scoped tenant context
 */
data class TenantContext(val tenantId: String)

// src/main/kotlin/com/example/di/TenantModule.kt
package com.example.di

import com.example.models.TenantContext
import com.example.repositories.TenantUserRepository
import com.example.repositories.UserRepository
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val tenantModule = module {
    // Scoped per request
    scope(named("request")) {
        scoped { TenantContext(get<ApplicationCall>().request.headers["X-Tenant-ID"] ?: "default") }
        scoped<UserRepository> { TenantUserRepository(get()) }
    }
}

// src/main/kotlin/com/example/repositories/TenantUserRepository.kt
package com.example.repositories

import com.example.models.TenantContext
import com.example.models.User
import com.example.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class TenantUserRepository(
    private val tenantContext: TenantContext
) : UserRepository {

    override fun getAll(): List<User> {
        return transaction {
            Users.selectAll()
                .where { Users.tenantId eq tenantContext.tenantId }
                .map { rowToUser(it) }
        }
    }

    override fun getById(id: Int): User? {
        return transaction {
            Users.selectAll()
                .where {
                    (Users.id eq id) and (Users.tenantId eq tenantContext.tenantId)
                }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    // ... other methods with tenant filtering

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            email = row[Users.email],
            fullName = row[Users.fullName],
            role = row[Users.role],
            createdAt = row[Users.createdAt]
        )
    }
}

// src/main/kotlin/com/example/plugins/TenantPlugin.kt
package com.example.plugins

import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.ktor.ext.getKoin

/**
 * Plugin to create request scopes for tenant isolation
 */
val TenantPlugin = createApplicationPlugin(name = "TenantPlugin") {
    onCall { call ->
        val koin = call.application.getKoin()
        val scope = koin.createScope("request-${System.currentTimeMillis()}", named("request"))

        // Store scope in call attributes for access in routes
        call.attributes.put(AttributeKey("tenantScope"), scope)

        // Close scope after request completes
        call.response.pipeline.intercept(ApplicationSendPipeline.After) {
            scope.close()
        }
    }
}

// src/main/kotlin/com/example/routes/TenantUserRoutes.kt
package com.example.routes

import com.example.models.ApiResponse
import com.example.repositories.UserRepository
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.ktor.ext.getKoin

fun Route.tenantUserRoutes() {
    route("/api/users") {
        get {
            // Get request-scoped dependencies
            val scope = call.attributes[AttributeKey<Scope>("tenantScope")]
            val userRepository = scope.get<UserRepository>()

            // This automatically filters by tenant!
            val users = userRepository.getAll()

            call.respond(ApiResponse(data = users))
        }
    }
}
```

### Testing

```bash
# Request for Tenant A
curl -X GET http://localhost:8080/api/users \
  -H "X-Tenant-ID: tenant-a"

# Returns only Tenant A's users

# Request for Tenant B
curl -X GET http://localhost:8080/api/users \
  -H "X-Tenant-ID: tenant-b"

# Returns only Tenant B's users
```

---

## Why This Matters

### Real-World Benefits

**Before Koin** (Manual DI):
```kotlin
// Application.kt - 200 lines of dependency wiring
val userRepo = UserRepositoryImpl(db)
val postRepo = PostRepositoryImpl(db)
val commentRepo = CommentRepositoryImpl(db)
val userService = UserService(userRepo)
val postService = PostService(postRepo, userRepo)
val commentService = CommentService(commentRepo, postRepo, userRepo)
val authService = AuthService(userRepo, jwtConfig)
// ... 50 more lines
```

**After Koin**:
```kotlin
// Application.kt - 5 lines
install(Koin) {
    modules(appModules)
}
```

All wiring handled centrally in modules!

### Testing Impact

**Without DI**:
- Tests require real database
- Hard to isolate components
- Slow test execution
- Complex test setup

**With Koin**:
- Swap implementations with mocks
- Fast, isolated unit tests
- Simple test configuration
- Easy to simulate edge cases

---

## Checkpoint Quiz

### Question 1
What's the difference between `single` and `factory` in Koin?

A) `single` is faster than `factory`
B) `single` creates one instance (singleton), `factory` creates new instances each time
C) `factory` is for factories only
D) They're the same

### Question 2
What does the `get()` function do in Koin module definitions?

A) Gets data from the database
B) Resolves a dependency from Koin
C) Creates a new instance
D) Makes an HTTP GET request

### Question 3
Why is dependency injection important for testing?

A) It makes tests run faster
B) It allows swapping real implementations with mocks
C) It's required by JUnit
D) It generates test data automatically

### Question 4
What is the lazy delegate `by inject<T>()` used for?

A) Making API calls lazily
B) Lazy loading from database
C) Resolving dependencies from Koin when first accessed
D) Delaying function execution

### Question 5
When should you use scoped dependencies instead of singletons?

A) Never, singletons are always better
B) When you need per-request or per-session instances
C) Only for testing
D) When the dependency is expensive to create

---

## Quiz Answers

**Question 1: B) `single` creates one instance (singleton), `factory` creates new instances each time**

```kotlin
single { EmailService() }  // One instance, reused everywhere
factory { EmailMessage() }  // New instance every time
```

Use `single` for stateless services (UserService, repositories).
Use `factory` for stateful objects (request data, messages).

---

**Question 2: B) Resolves a dependency from Koin**

```kotlin
single { UserService(get()) }
               // ↑ Resolves UserRepository from Koin
```

Koin uses type inference to determine what to inject.

---

**Question 3: B) It allows swapping real implementations with mocks**

```kotlin
// Production module
single<UserRepository> { UserRepositoryImpl() }  // Real database

// Test module
single<UserRepository> { MockUserRepository() }  // In-memory mock
```

Tests use mock implementations without changing service code!

---

**Question 4: C) Resolving dependencies from Koin when first accessed**

```kotlin
val userService by inject<UserService>()
// Lazy: userService is resolved when first accessed
// Subsequent accesses return the same instance (for singletons)
```

This is more efficient than eager resolution.

---

**Question 5: B) When you need per-request or per-session instances**

**Singleton** (shared state):
- Database connections
- Configuration
- Stateless services

**Scoped** (isolated state):
- Request context (tenant ID, user session)
- Transaction boundaries
- Per-request caches

Multi-tenant applications are a perfect use case for scopes!

---

## What You've Learned

✅ What dependency injection is and why it matters
✅ How to set up Koin in Ktor applications
✅ How to define modules with `single`, `factory`, and `get()`
✅ How to inject dependencies with `by inject<T>()`
✅ How to organize modules by feature (repositories, services, etc.)
✅ How to use named dependencies and scopes
✅ How to write testable code with mock dependencies
✅ How to build multi-tenant systems with scoped dependencies

---

## Next Steps

In **Lesson 5.14**, you'll learn **Testing Your API**. You'll discover:
- How to write unit tests for services with mock repositories
- How to write integration tests for full API endpoints
- How to use Ktor's testing utilities
- How to test authentication and authorization
- How to measure code coverage

The clean DI architecture you built makes testing incredibly easy!

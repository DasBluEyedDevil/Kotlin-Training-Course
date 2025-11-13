# Lesson 5.3: Testing, Validation, and Error Handling

## Building Robust APIs

Great APIs handle errors gracefully, validate input, and are thoroughly tested!

---

## Input Validation

### Request Validation

```kotlin
import io.ktor.server.plugins.requestvalidation.*

@Serializable
data class CreateUserRequest(
    val email: String,
    val password: String,
    val age: Int
)

fun Application.configureValidation() {
    install(RequestValidation) {
        validate<CreateUserRequest> { request ->
            // Email validation
            if (!request.email.contains("@")) {
                ValidationResult.Invalid("Email must contain @")
            } else if (request.password.length < 8) {
                ValidationResult.Invalid("Password must be at least 8 characters")
            } else if (request.age < 13) {
                ValidationResult.Invalid("Must be at least 13 years old")
            } else {
                ValidationResult.Valid
            }
        }
    }
}
```

---

### Custom Validators

```kotlin
object Validators {
    fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        return email.matches(emailRegex)
    }

    fun isStrongPassword(password: String): Boolean {
        return password.length >= 8 &&
               password.any { it.isUpperCase() } &&
               password.any { it.isLowerCase() } &&
               password.any { it.isDigit() }
    }

    fun isValidAge(age: Int): Boolean {
        return age in 0..150
    }
}

// Usage
routing {
    post("/api/register") {
        val request = call.receive<CreateUserRequest>()

        when {
            !Validators.isValidEmail(request.email) ->
                call.respond(HttpStatusCode.BadRequest, "Invalid email")

            !Validators.isStrongPassword(request.password) ->
                call.respond(HttpStatusCode.BadRequest, "Weak password")

            !Validators.isValidAge(request.age) ->
                call.respond(HttpStatusCode.BadRequest, "Invalid age")

            else -> {
                // Process registration
                call.respond(HttpStatusCode.Created, "User created")
            }
        }
    }
}
```

---

## Error Handling

### Status Exceptions

```kotlin
import io.ktor.server.plugins.statuspages.*

fun Application.configureErrorHandling() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            when (cause) {
                is IllegalArgumentException -> {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to cause.message)
                    )
                }
                is NotFoundException -> {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("error" to cause.message)
                    )
                }
                else -> {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Internal server error")
                    )
                }
            }
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status,
                mapOf("error" to "Endpoint not found")
            )
        }
    }
}
```

---

### Custom Exceptions

```kotlin
class NotFoundException(message: String) : Exception(message)
class UnauthorizedException(message: String) : Exception(message)
class ValidationException(message: String) : Exception(message)

// Usage in routes
routing {
    get("/api/users/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: throw ValidationException("Invalid ID")

        val user = getUserById(id)
            ?: throw NotFoundException("User not found")

        call.respond(user)
    }
}
```

---

## API Testing with Ktor

### Test Setup

```kotlin
// build.gradle.kts
dependencies {
    testImplementation("io.ktor:ktor-server-tests:2.3.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.20")
}
```

---

### Testing Routes

```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {

    @Test
    fun testGetUsers() = testApplication {
        application {
            configureRouting()
        }

        val response = client.get("/api/users")
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testCreateUser() = testApplication {
        application {
            configureRouting()
        }

        val response = client.post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"Alice","email":"alice@example.com"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun testGetUserNotFound() = testApplication {
        application {
            configureRouting()
        }

        val response = client.get("/api/users/999")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testInvalidInput() = testApplication {
        application {
            configureRouting()
            configureValidation()
        }

        val response = client.post("/api/users") {
            contentType(ContentType.Application.Json)
            setBody("""{"name":"","email":"invalid"}""")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
```

---

### Testing with Authentication

```kotlin
@Test
fun testProtectedRoute() = testApplication {
    application {
        configureRouting()
        configureSecurity()
    }

    // Without token
    val unauthorized = client.get("/api/profile")
    assertEquals(HttpStatusCode.Unauthorized, unauthorized.status)

    // With token
    val token = "valid_jwt_token_here"
    val authorized = client.get("/api/profile") {
        header("Authorization", "Bearer $token")
    }
    assertEquals(HttpStatusCode.OK, authorized.status)
}
```

---

## Logging and Monitoring

### Call Logging

```kotlin
import io.ktor.server.plugins.callloging.*
import org.slf4j.event.Level

fun Application.configureLogging() {
    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/api") }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val path = call.request.path()
            "$httpMethod $path - $status"
        }
    }
}
```

---

### Custom Logging

```kotlin
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("Application")

routing {
    post("/api/users") {
        try {
            val request = call.receive<CreateUserRequest>()
            logger.info("Creating user: ${request.email}")

            val user = createUser(request)
            logger.info("User created successfully: ${user.id}")

            call.respond(HttpStatusCode.Created, user)
        } catch (e: Exception) {
            logger.error("Error creating user", e)
            throw e
        }
    }
}
```

---

## Rate Limiting

```kotlin
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.minutes

fun Application.configureRateLimiting() {
    install(RateLimit) {
        register(RateLimitName("api")) {
            rateLimiter(limit = 100, refillPeriod = 1.minutes)
        }
    }
}

routing {
    rateLimit(RateLimitName("api")) {
        get("/api/data") {
            call.respondText("Limited endpoint")
        }
    }
}
```

---

## CORS Configuration

```kotlin
import io.ktor.server.plugins.cors.routing.*

fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)

        // Development
        anyHost()

        // Production - specify allowed hosts
        // allowHost("example.com", schemes = listOf("https"))
        // allowHost("www.example.com", schemes = listOf("https"))

        allowCredentials = true
        maxAgeInSeconds = 3600
    }
}
```

---

## Request/Response Logging

```kotlin
fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        mdc("userId") { call ->
            call.principal<JWTPrincipal>()
                ?.payload?.getClaim("userId")?.asInt()?.toString()
        }
    }

    routing {
        get("/api/users/{id}") {
            val userId = call.parameters["id"]
            logger.info("Fetching user $userId")
            // ...
        }
    }
}
```

---

## Environment Configuration

```kotlin
// application.conf
ktor {
    deployment {
        port = 8080
        port = ${?PORT}
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
}

database {
    url = "jdbc:h2:mem:test"
    url = ${?DATABASE_URL}
    driver = "org.h2.Driver"
}

jwt {
    secret = "default-secret"
    secret = ${?JWT_SECRET}
    issuer = "http://localhost:8080"
    audience = "http://localhost:8080/api"
    realm = "ktor app"
}
```

---

### Loading Configuration

```kotlin
data class DatabaseConfig(
    val url: String,
    val driver: String
)

data class JwtConfig(
    val secret: String,
    val issuer: String,
    val audience: String,
    val realm: String
)

fun Application.loadConfiguration(): Pair<DatabaseConfig, JwtConfig> {
    val dbConfig = DatabaseConfig(
        url = environment.config.property("database.url").getString(),
        driver = environment.config.property("database.driver").getString()
    )

    val jwtConfig = JwtConfig(
        secret = environment.config.property("jwt.secret").getString(),
        issuer = environment.config.property("jwt.issuer").getString(),
        audience = environment.config.property("jwt.audience").getString(),
        realm = environment.config.property("jwt.realm").getString()
    )

    return dbConfig to jwtConfig
}
```

---

## Complete API Example with Best Practices

```kotlin
fun Application.module() {
    val (dbConfig, jwtConfig) = loadConfiguration()

    // Install plugins
    configureContentNegotiation()
    configureCORS()
    configureErrorHandling()
    configureValidation()
    configureLogging()
    configureSecurity(jwtConfig)
    configureRateLimiting()

    // Initialize database
    DatabaseFactory.init(dbConfig)

    // Configure routes
    routing {
        apiRoutes()
        authRoutes()
    }
}

fun Route.apiRoutes() {
    route("/api") {
        authenticate("auth-jwt") {
            userRoutes()
            postRoutes()
        }
    }
}
```

---

## Recap

You now understand:

1. **Input validation** - Preventing bad data
2. **Error handling** - Graceful failure
3. **Testing** - Ensuring correctness
4. **Logging** - Debugging and monitoring
5. **Rate limiting** - Preventing abuse
6. **CORS** - Cross-origin requests
7. **Configuration** - Environment management
8. **Best practices** - Production-ready APIs

---

## What's Next?

Next: **Deployment and Production Readiness!**

**Key Takeaway:** Robust APIs require comprehensive error handling, validation, and testing!

Continue to the next lesson!

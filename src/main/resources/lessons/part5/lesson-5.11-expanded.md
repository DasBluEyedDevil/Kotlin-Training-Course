# Lesson 5.11: Authentication - Login & JWT Tokens

**Estimated Time**: 70 minutes

---

## Topic Introduction

You've built secure user registration with bcrypt-hashed passwords. Now users can sign up—but how do they prove their identity on subsequent requests?

Traditional web applications use server-side sessions (cookies stored in server memory). But modern APIs need something more scalable and stateless: **JSON Web Tokens (JWT)**.

In this lesson, you'll implement a complete login system that verifies passwords and issues JWTs, allowing users to authenticate with your API without storing session state on the server.

---

## The Concept

### The Concert Ticket Analogy

Think of JWT authentication like getting into a concert:

**Old Way (Sessions)**:
- You show your ID at the door
- Bouncer writes your name on a clipboard (server memory)
- Every time you leave and return, bouncer checks the clipboard
- Problem: Bouncer must remember thousands of people
- If bouncer forgets (server restarts), you're locked out

**New Way (JWT)**:
- You show your ID at the door once
- Bouncer gives you a wristband with your info and a tamper-proof seal
- Every time you return, you just show the wristband
- Anyone can verify the wristband is authentic (check the seal)
- No need to remember who you are—the wristband proves everything
- ✅ Scalable!

JWTs are like tamper-proof wristbands for your API.

### What is a JWT?

A JWT (JSON Web Token) is a compact, self-contained token that securely transmits information between parties.

**Structure**: Three parts separated by dots (`.`)

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c

         HEADER                        PAYLOAD                           SIGNATURE
```

#### Part 1: Header
```json
{
  "alg": "HS256",      // Algorithm used for signing
  "typ": "JWT"         // Token type
}
```
Base64URL encoded → `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9`

#### Part 2: Payload (Claims)
```json
{
  "sub": "1234567890",   // Subject (user ID)
  "name": "John Doe",    // Custom claim
  "email": "john@example.com",
  "iat": 1516239022,     // Issued at (timestamp)
  "exp": 1516242622      // Expiration (timestamp)
}
```
Base64URL encoded → `eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4g...`

#### Part 3: Signature
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret
)
```

The signature ensures:
- Token hasn't been tampered with
- Token was issued by your server (only you know the secret)

### JWT vs Sessions

| Aspect | JWT (Stateless) | Sessions (Stateful) |
|--------|-----------------|---------------------|
| **Storage** | Client-side (sent with each request) | Server-side memory/database |
| **Scalability** | ✅ Excellent (no server state) | ❌ Requires shared session store |
| **Performance** | ✅ Fast (no DB lookup) | ❌ DB/cache lookup each request |
| **Revocation** | ❌ Hard (token valid until expiration) | ✅ Easy (delete session) |
| **Size** | ❌ Larger (entire token sent) | ✅ Small (just session ID) |
| **Best For** | Distributed systems, microservices | Traditional monolithic apps |

**When to use JWT**:
- RESTful APIs
- Mobile apps
- Microservices architecture
- Cross-domain authentication

---

## Implementing Login with JWT

### Step 1: Add JWT Dependencies

Update your `build.gradle.kts`:

```kotlin
dependencies {
    // Existing dependencies
    implementation("io.ktor:ktor-server-core-jvm:3.0.2")
    implementation("io.ktor:ktor-server-cio-jvm:3.0.2")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.0.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.2")
    implementation("org.jetbrains.exposed:exposed-core:0.50.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.50.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("de.nycode:bcrypt:2.3.0")

    // JWT Authentication
    implementation("io.ktor:ktor-server-auth-jvm:3.0.2")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:3.0.2")
    implementation("com.auth0:java-jwt:4.5.0")
}
```

### Step 2: Create JWT Configuration

```kotlin
// src/main/kotlin/com/example/security/JwtConfig.kt
package com.example.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

/**
 * Configuration for JWT token generation and verification
 */
object JwtConfig {

    // IMPORTANT: In production, load these from environment variables!
    // NEVER hardcode secrets in source code
    private const val SECRET = "your-256-bit-secret-change-this-in-production"
    private const val ISSUER = "http://localhost:8080"
    private const val AUDIENCE = "http://localhost:8080/api"
    private const val VALIDITY_MS = 3_600_000L  // 1 hour

    private val algorithm = Algorithm.HMAC256(SECRET)

    /**
     * JWT Verifier for validating incoming tokens
     */
    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    /**
     * Generate a JWT token for a user
     *
     * @param userId The user's ID
     * @param email The user's email
     * @return JWT token string
     */
    fun generateToken(userId: Int, email: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_MS))
            .sign(algorithm)
    }

    /**
     * Extract user ID from a verified JWT token
     */
    fun extractUserId(token: String): Int {
        return JWT.decode(token).subject.toInt()
    }

    /**
     * Extract email from a verified JWT token
     */
    fun extractEmail(token: String): String {
        return JWT.decode(token).getClaim("email").asString()
    }

    /**
     * Get token validity in milliseconds
     */
    fun getTokenValidity(): Long = VALIDITY_MS
}
```

**Security Note**: The secret should be:
- At least 256 bits (32 characters) long
- Randomly generated
- Loaded from environment variables, not hardcoded
- Different for each environment (dev, staging, production)

### Step 3: Create Login Models

```kotlin
// src/main/kotlin/com/example/models/Auth.kt
package com.example.models

import kotlinx.serialization.Serializable

/**
 * Login request credentials
 */
@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * Login response with JWT token
 */
@Serializable
data class LoginResponse(
    val token: String,
    val user: User,
    val expiresIn: Long,  // Milliseconds until token expires
    val message: String = "Login successful"
)
```

### Step 4: Create Authentication Service

```kotlin
// src/main/kotlin/com/example/services/AuthService.kt
package com.example.services

import com.example.exceptions.UnauthorizedException
import com.example.exceptions.ValidationException
import com.example.models.LoginRequest
import com.example.models.LoginResponse
import com.example.repositories.UserRepository
import com.example.security.JwtConfig
import com.example.security.PasswordHasher

class AuthService(
    private val userRepository: UserRepository
) {

    /**
     * Authenticate user and generate JWT token
     */
    fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            // Step 1: Basic validation
            if (request.email.isBlank() || request.password.isBlank()) {
                throw ValidationException("Email and password are required")
            }

            // Step 2: Get user's password hash from database
            // IMPORTANT: Don't reveal whether email exists or password is wrong
            // Always return generic "Invalid credentials" message
            val passwordHash = userRepository.getPasswordHash(request.email)

            if (passwordHash == null) {
                // Email doesn't exist - but don't tell the attacker that!
                throw UnauthorizedException("Invalid email or password")
            }

            // Step 3: Verify password
            val passwordMatches = PasswordHasher.verifyPassword(
                request.password,
                passwordHash
            )

            if (!passwordMatches) {
                // Wrong password - generic message
                throw UnauthorizedException("Invalid email or password")
            }

            // Step 4: Get user details (without password hash)
            val user = userRepository.getByEmail(request.email)
                ?: throw RuntimeException("User retrieval failed after successful authentication")

            // Step 5: Generate JWT token
            val token = JwtConfig.generateToken(
                userId = user.id,
                email = user.email
            )

            // Step 6: Return token and user info
            val response = LoginResponse(
                token = token,
                user = user,
                expiresIn = JwtConfig.getTokenValidity(),
                message = "Login successful"
            )

            Result.success(response)

        } catch (e: ValidationException) {
            Result.failure(e)
        } catch (e: UnauthorizedException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error during login: ${e.message}")
            e.printStackTrace()
            Result.failure(RuntimeException("An unexpected error occurred during login"))
        }
    }

    /**
     * Verify a JWT token and return user ID
     */
    fun verifyToken(token: String): Result<Int> {
        return try {
            // Verify token signature and expiration
            JwtConfig.verifier.verify(token)

            // Extract user ID from token
            val userId = JwtConfig.extractUserId(token)

            Result.success(userId)

        } catch (e: Exception) {
            Result.failure(UnauthorizedException("Invalid or expired token"))
        }
    }
}
```

### Step 5: Create Login Route

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

fun Route.authRoutes(
    userService: UserService,
    authService: AuthService
) {
    route("/api/auth") {

        /**
         * POST /api/auth/register
         * Register a new user
         */
        post("/register") {
            val request = call.receive<RegisterRequest>()

            userService.register(request)
                .onSuccess { user ->
                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(
                            data = RegisterResponse(
                                user = user,
                                message = "Registration successful. You can now log in."
                            )
                        )
                    )
                }
                .onFailure { error ->
                    throw error
                }
        }

        /**
         * POST /api/auth/login
         * Login with email and password, receive JWT token
         */
        post("/login") {
            val request = call.receive<LoginRequest>()

            authService.login(request)
                .onSuccess { loginResponse ->
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse(data = loginResponse)
                    )
                }
                .onFailure { error ->
                    throw error
                }
        }
    }
}
```

### Step 6: Wire Everything Together

Update your Application.kt:

```kotlin
// src/main/kotlin/com/example/Application.kt
package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.configureErrorHandling
import com.example.repositories.UserRepositoryImpl
import com.example.routes.authRoutes
import com.example.services.AuthService
import com.example.services.UserService
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(CIO, port = 8080, module = Application::module).start(wait = true)
}

fun Application.module() {
    // Install plugins
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }

    // Install error handling
    configureErrorHandling()

    // Initialize database
    DatabaseFactory.init()

    // Create dependencies
    val userRepository = UserRepositoryImpl()
    val userService = UserService(userRepository)
    val authService = AuthService(userRepository)

    // Configure routes
    routing {
        authRoutes(userService, authService)
    }
}
```

---

## Code Breakdown

### The Login Flow

```
1. Client sends POST /api/auth/login
   {
     "email": "alice@example.com",
     "password": "SecurePass123!"
   }
   ↓
2. AuthService receives request
   ↓
3. Validate email and password not blank
   ↓
4. Lookup password hash from database
   - If email doesn't exist → 401 "Invalid email or password"
   ↓
5. Verify password against hash using bcrypt
   - If password wrong → 401 "Invalid email or password"
   ↓
6. Password verified! Get user details
   ↓
7. Generate JWT token:
   Header: {"alg": "HS256", "typ": "JWT"}
   Payload: {
     "sub": "1",                    // User ID
     "email": "alice@example.com",  // Email claim
     "iat": 1705315200,             // Issued at
     "exp": 1705318800,             // Expires in 1 hour
     "iss": "http://localhost:8080",
     "aud": "http://localhost:8080/api"
   }
   Signature: HMACSHA256(header + payload, SECRET)
   ↓
8. Return 200 OK with token and user
   {
     "success": true,
     "data": {
       "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
       "user": {
         "id": 1,
         "email": "alice@example.com",
         "fullName": "Alice Johnson",
         "createdAt": "2025-01-15T10:30:45"
       },
       "expiresIn": 3600000,
       "message": "Login successful"
     }
   }
```

### Security Highlights

**1. Generic Error Messages**:
```kotlin
// ❌ BAD: Reveals whether email exists
if (passwordHash == null) {
    throw NotFoundException("Email not found")
}
if (!passwordMatches) {
    throw UnauthorizedException("Password is incorrect")
}

// ✅ GOOD: Same message for both cases
if (passwordHash == null || !passwordMatches) {
    throw UnauthorizedException("Invalid email or password")
}
```

This prevents attackers from enumerating valid email addresses.

**2. Password Verification Timing**:
Even if email doesn't exist, we should still verify the password (against a dummy hash) to prevent timing attacks:

```kotlin
// Advanced security (prevents timing attacks)
val dummyHash = "$2a$12$dummy..."
val hashToVerify = passwordHash ?: dummyHash
val passwordMatches = PasswordHasher.verifyPassword(request.password, hashToVerify)

if (passwordHash == null || !passwordMatches) {
    throw UnauthorizedException("Invalid email or password")
}
```

This ensures the function always takes the same time, whether email exists or not.

**3. Token Claims**:
```kotlin
.withSubject(userId.toString())     // Standard claim: user identifier
.withClaim("email", email)          // Custom claim: user email
.withIssuedAt(Date())               // When token was created
.withExpiresAt(Date(...))           // When token expires
.withIssuer(ISSUER)                 // Who issued the token
.withAudience(AUDIENCE)             // Who token is intended for
```

These claims are used to validate the token and identify the user.

---

## Testing Login

### Test 1: Successful Login

First, register a user:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "SecurePass123!",
    "fullName": "Alice Johnson"
  }'
```

Now login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "SecurePass123!"
  }'
```

Response (200 OK):
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpIiwic3ViIjoiMSIsImVtYWlsIjoiYWxpY2VAZXhhbXBsZS5jb20iLCJpYXQiOjE3MDUzMTUyMDAsImV4cCI6MTcwNTMxODgwMH0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
    "user": {
      "id": 1,
      "email": "alice@example.com",
      "fullName": "Alice Johnson",
      "createdAt": "2025-01-15T10:30:45.123456"
    },
    "expiresIn": 3600000,
    "message": "Login successful"
  }
}
```

### Test 2: Wrong Password

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "WrongPassword123!"
  }'
```

Response (401 Unauthorized):
```json
{
  "success": false,
  "message": "Invalid email or password",
  "timestamp": "2025-01-15T11:45:22.456"
}
```

### Test 3: Non-existent Email

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "nobody@example.com",
    "password": "SecurePass123!"
  }'
```

Response (401 Unauthorized):
```json
{
  "success": false,
  "message": "Invalid email or password",
  "timestamp": "2025-01-15T11:46:33.789"
}
```

Notice: **Same error message** as wrong password! Security best practice.

### Test 4: Decode the JWT Token

Copy the token from the login response and decode it at [jwt.io](https://jwt.io):

**Header**:
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Payload**:
```json
{
  "iss": "http://localhost:8080",
  "aud": "http://localhost:8080/api",
  "sub": "1",
  "email": "alice@example.com",
  "iat": 1705315200,
  "exp": 1705318800
}
```

**Verify Signature**: Paste the secret `your-256-bit-secret-change-this-in-production` to verify the signature is valid.

---

## Exercise: Refresh Token System

Implement a refresh token mechanism for better security and UX.

### Background

Current system has a problem:
- Tokens expire after 1 hour
- User must login again every hour (poor UX)
- Longer expiration times are less secure

**Solution**: Two-token system:
- **Access Token**: Short-lived (15 minutes), used for API requests
- **Refresh Token**: Long-lived (7 days), used to get new access tokens

### Requirements

1. **Update Login Response**:
   - Return both `accessToken` and `refreshToken`
   - Access token expires in 15 minutes
   - Refresh token expires in 7 days

2. **Create Refresh Endpoint**:
   - `POST /api/auth/refresh`
   - Accepts: `{ "refreshToken": "..." }`
   - Returns: New access token (and optionally new refresh token)

3. **Store Refresh Tokens**:
   - Create `RefreshTokens` table
   - Fields: id, userId, token, expiresAt, createdAt
   - Each user can have multiple refresh tokens (different devices)

4. **Revocation Support**:
   - `POST /api/auth/logout` - Delete refresh token
   - `POST /api/auth/logout-all` - Delete all user's refresh tokens

5. **Security Requirements**:
   - Refresh tokens must be stored hashed (like passwords)
   - Each refresh token can be used only once (rotation)
   - Expired tokens are automatically invalid

### Starter Code

```kotlin
@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User,
    val accessTokenExpiresIn: Long,   // 15 minutes
    val refreshTokenExpiresIn: Long,  // 7 days
    val message: String = "Login successful"
)

@Serializable
data class RefreshRequest(
    val refreshToken: String
)

@Serializable
data class RefreshResponse(
    val accessToken: String,
    val refreshToken: String,
    val accessTokenExpiresIn: Long
)

// TODO: Create RefreshTokens table
// TODO: Implement refresh token generation and validation
// TODO: Implement refresh endpoint
// TODO: Implement logout endpoints
```

---

## Solution

### Complete Refresh Token System

```kotlin
// src/main/kotlin/com/example/models/RefreshToken.kt
package com.example.models

import org.jetbrains.exposed.sql.Table

object RefreshTokens : Table("refresh_tokens") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id").references(Users.id)
    val tokenHash = varchar("token_hash", 64)  // SHA-256 hash
    val expiresAt = long("expires_at")
    val createdAt = varchar("created_at", 50)

    override val primaryKey = PrimaryKey(id)
}
```

```kotlin
// src/main/kotlin/com/example/security/JwtConfig.kt (Updated)
package com.example.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.security.MessageDigest
import java.util.*

object JwtConfig {

    private const val SECRET = "your-256-bit-secret-change-this-in-production"
    private const val ISSUER = "http://localhost:8080"
    private const val AUDIENCE = "http://localhost:8080/api"

    // Token validity durations
    private const val ACCESS_TOKEN_VALIDITY_MS = 900_000L       // 15 minutes
    private const val REFRESH_TOKEN_VALIDITY_MS = 604_800_000L  // 7 days

    private val algorithm = Algorithm.HMAC256(SECRET)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    /**
     * Generate an access token (short-lived)
     */
    fun generateAccessToken(userId: Int, email: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withClaim("type", "access")
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_MS))
            .sign(algorithm)
    }

    /**
     * Generate a refresh token (long-lived)
     * Returns the raw token (to be hashed before storage)
     */
    fun generateRefreshToken(): String {
        // Generate random token (not a JWT, just a random string)
        return UUID.randomUUID().toString() + UUID.randomUUID().toString()
    }

    /**
     * Hash a refresh token for storage
     */
    fun hashRefreshToken(token: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.toByteArray())
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Get access token validity in milliseconds
     */
    fun getAccessTokenValidity(): Long = ACCESS_TOKEN_VALIDITY_MS

    /**
     * Get refresh token validity in milliseconds
     */
    fun getRefreshTokenValidity(): Long = REFRESH_TOKEN_VALIDITY_MS

    /**
     * Extract user ID from access token
     */
    fun extractUserId(token: String): Int {
        return JWT.decode(token).subject.toInt()
    }

    /**
     * Extract email from access token
     */
    fun extractEmail(token: String): String {
        return JWT.decode(token).getClaim("email").asString()
    }
}
```

```kotlin
// src/main/kotlin/com/example/repositories/RefreshTokenRepository.kt
package com.example.repositories

import com.example.models.RefreshTokens
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

interface RefreshTokenRepository {
    fun insert(userId: Int, tokenHash: String, expiresAt: Long): Int
    fun findByTokenHash(tokenHash: String): RefreshTokenData?
    fun deleteByTokenHash(tokenHash: String): Boolean
    fun deleteAllByUserId(userId: Int): Int
    fun deleteExpired()
}

data class RefreshTokenData(
    val id: Int,
    val userId: Int,
    val expiresAt: Long
)

class RefreshTokenRepositoryImpl : RefreshTokenRepository {

    override fun insert(userId: Int, tokenHash: String, expiresAt: Long): Int {
        return transaction {
            RefreshTokens.insert {
                it[RefreshTokens.userId] = userId
                it[RefreshTokens.tokenHash] = tokenHash
                it[RefreshTokens.expiresAt] = expiresAt
                it[createdAt] = LocalDateTime.now().toString()
            }[RefreshTokens.id]
        }
    }

    override fun findByTokenHash(tokenHash: String): RefreshTokenData? {
        return transaction {
            RefreshTokens.selectAll()
                .where { RefreshTokens.tokenHash eq tokenHash }
                .map {
                    RefreshTokenData(
                        id = it[RefreshTokens.id],
                        userId = it[RefreshTokens.userId],
                        expiresAt = it[RefreshTokens.expiresAt]
                    )
                }
                .singleOrNull()
        }
    }

    override fun deleteByTokenHash(tokenHash: String): Boolean {
        return transaction {
            RefreshTokens.deleteWhere {
                RefreshTokens.tokenHash eq tokenHash
            } > 0
        }
    }

    override fun deleteAllByUserId(userId: Int): Int {
        return transaction {
            RefreshTokens.deleteWhere {
                RefreshTokens.userId eq userId
            }
        }
    }

    override fun deleteExpired() {
        transaction {
            val now = System.currentTimeMillis()
            RefreshTokens.deleteWhere {
                expiresAt less now
            }
        }
    }
}
```

```kotlin
// src/main/kotlin/com/example/services/AuthService.kt (Updated)
package com.example.services

import com.example.exceptions.UnauthorizedException
import com.example.exceptions.ValidationException
import com.example.models.LoginRequest
import com.example.models.LoginResponse
import com.example.models.RefreshRequest
import com.example.models.RefreshResponse
import com.example.repositories.RefreshTokenRepository
import com.example.repositories.UserRepository
import com.example.security.JwtConfig
import com.example.security.PasswordHasher

class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            // Validate input
            if (request.email.isBlank() || request.password.isBlank()) {
                throw ValidationException("Email and password are required")
            }

            // Get password hash
            val passwordHash = userRepository.getPasswordHash(request.email)
                ?: throw UnauthorizedException("Invalid email or password")

            // Verify password
            val passwordMatches = PasswordHasher.verifyPassword(
                request.password,
                passwordHash
            )

            if (!passwordMatches) {
                throw UnauthorizedException("Invalid email or password")
            }

            // Get user
            val user = userRepository.getByEmail(request.email)
                ?: throw RuntimeException("User retrieval failed")

            // Generate access token
            val accessToken = JwtConfig.generateAccessToken(
                userId = user.id,
                email = user.email
            )

            // Generate and store refresh token
            val refreshToken = JwtConfig.generateRefreshToken()
            val refreshTokenHash = JwtConfig.hashRefreshToken(refreshToken)
            val refreshTokenExpiresAt = System.currentTimeMillis() +
                JwtConfig.getRefreshTokenValidity()

            refreshTokenRepository.insert(
                userId = user.id,
                tokenHash = refreshTokenHash,
                expiresAt = refreshTokenExpiresAt
            )

            // Clean up expired tokens
            refreshTokenRepository.deleteExpired()

            val response = LoginResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                user = user,
                accessTokenExpiresIn = JwtConfig.getAccessTokenValidity(),
                refreshTokenExpiresIn = JwtConfig.getRefreshTokenValidity(),
                message = "Login successful"
            )

            Result.success(response)

        } catch (e: ValidationException) {
            Result.failure(e)
        } catch (e: UnauthorizedException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error during login: ${e.message}")
            e.printStackTrace()
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    fun refresh(request: RefreshRequest): Result<RefreshResponse> {
        return try {
            // Hash the provided refresh token
            val tokenHash = JwtConfig.hashRefreshToken(request.refreshToken)

            // Look up the token in database
            val tokenData = refreshTokenRepository.findByTokenHash(tokenHash)
                ?: throw UnauthorizedException("Invalid refresh token")

            // Check if expired
            if (tokenData.expiresAt < System.currentTimeMillis()) {
                // Delete expired token
                refreshTokenRepository.deleteByTokenHash(tokenHash)
                throw UnauthorizedException("Refresh token expired")
            }

            // Get user
            val user = userRepository.getById(tokenData.userId)
                ?: throw RuntimeException("User not found")

            // Rotation: Delete old refresh token
            refreshTokenRepository.deleteByTokenHash(tokenHash)

            // Generate new access token
            val newAccessToken = JwtConfig.generateAccessToken(
                userId = user.id,
                email = user.email
            )

            // Generate new refresh token
            val newRefreshToken = JwtConfig.generateRefreshToken()
            val newRefreshTokenHash = JwtConfig.hashRefreshToken(newRefreshToken)
            val newRefreshTokenExpiresAt = System.currentTimeMillis() +
                JwtConfig.getRefreshTokenValidity()

            refreshTokenRepository.insert(
                userId = user.id,
                tokenHash = newRefreshTokenHash,
                expiresAt = newRefreshTokenExpiresAt
            )

            val response = RefreshResponse(
                accessToken = newAccessToken,
                refreshToken = newRefreshToken,
                accessTokenExpiresIn = JwtConfig.getAccessTokenValidity()
            )

            Result.success(response)

        } catch (e: UnauthorizedException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error during token refresh: ${e.message}")
            e.printStackTrace()
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    fun logout(refreshToken: String): Result<Unit> {
        return try {
            val tokenHash = JwtConfig.hashRefreshToken(refreshToken)
            val deleted = refreshTokenRepository.deleteByTokenHash(tokenHash)

            if (!deleted) {
                throw UnauthorizedException("Invalid refresh token")
            }

            Result.success(Unit)

        } catch (e: UnauthorizedException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Error during logout: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    fun logoutAll(userId: Int): Result<Unit> {
        return try {
            refreshTokenRepository.deleteAllByUserId(userId)
            Result.success(Unit)

        } catch (e: Exception) {
            println("Error during logout all: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    fun verifyToken(token: String): Result<Int> {
        return try {
            JwtConfig.verifier.verify(token)
            val userId = JwtConfig.extractUserId(token)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(UnauthorizedException("Invalid or expired token"))
        }
    }
}
```

```kotlin
// Update AuthRoutes.kt
post("/refresh") {
    val request = call.receive<RefreshRequest>()

    authService.refresh(request)
        .onSuccess { refreshResponse ->
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(data = refreshResponse)
            )
        }
        .onFailure { error ->
            throw error
        }
}

post("/logout") {
    val request = call.receive<RefreshRequest>()

    authService.logout(request.refreshToken)
        .onSuccess {
            call.respond(
                HttpStatusCode.OK,
                ApiResponse<Unit>(message = "Logged out successfully")
            )
        }
        .onFailure { error ->
            throw error
        }
}
```

### Test the Refresh Flow

**1. Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "alice@example.com", "password": "SecurePass123!"}'
```

Response includes both tokens:
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "a1b2c3d4-...",
  "accessTokenExpiresIn": 900000,
  "refreshTokenExpiresIn": 604800000
}
```

**2. Use Access Token** (we'll implement this in next lesson)

**3. When Access Token Expires, Refresh**:
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "a1b2c3d4-..."}'
```

Response: New tokens!
```json
{
  "accessToken": "eyJ...",   // New access token
  "refreshToken": "x9y8z7...", // New refresh token (rotation)
  "accessTokenExpiresIn": 900000
}
```

**4. Logout**:
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "x9y8z7..."}'
```

---

## Solution Explanation

### Key Security Features

**1. Refresh Token Rotation**:
Each time you use a refresh token, it's deleted and a new one is issued. This limits the impact of stolen tokens.

**2. Hashed Storage**:
Refresh tokens are hashed before storage (like passwords). If the database is breached, tokens can't be used.

**3. Automatic Cleanup**:
Expired tokens are deleted, preventing database bloat and reducing attack surface.

**4. Per-Device Tokens**:
Users can have multiple refresh tokens (web, mobile, tablet). Logging out one device doesn't affect others.

**5. Short Access Tokens**:
Access tokens expire quickly (15 min), limiting damage if stolen. Refresh tokens handle long-term sessions.

---

## Why This Matters

### Real-World Impact

**Why JWT is Industry Standard**:
- **Scalability**: No server-side session storage needed
- **Microservices**: Token can be validated by any service
- **Mobile Apps**: Perfect for native apps (no cookies needed)
- **Cross-Domain**: Works across different domains and subdomains

**Production Considerations**:
1. **Secret Management**: Use environment variables, AWS Secrets Manager, or HashiCorp Vault
2. **Token Revocation**: Implement refresh token blacklisting for compromised accounts
3. **Monitoring**: Log failed authentication attempts (detect brute-force attacks)
4. **Rate Limiting**: Limit login attempts (5 per hour, for example)
5. **HTTPS Only**: NEVER send JWTs over HTTP (easily intercepted)

---

## Checkpoint Quiz

### Question 1
What are the three parts of a JWT?

A) Username, Password, Signature
B) Header, Body, Footer
C) Header, Payload, Signature
D) Key, Value, Hash

### Question 2
Why use refresh tokens instead of just making access tokens long-lived?

A) Refresh tokens look cooler
B) Short access tokens limit exposure if stolen; refresh tokens enable revocation
C) It's required by OAuth 2.0 specification
D) Refresh tokens are faster to verify

### Question 3
Why should error messages for "wrong password" and "email not found" be identical?

A) It's easier to code
B) It prevents attackers from enumerating valid email addresses
C) It confuses users
D) It's required by GDPR

### Question 4
What claim in a JWT identifies the user?

A) `uid`
B) `user`
C) `sub` (subject)
D) `id`

### Question 5
Why hash refresh tokens before storing them in the database?

A) To make them look random
B) To save database space
C) To protect users if database is breached (like password hashing)
D) It's not necessary, just a best practice

---

## Quiz Answers

**Question 1: C) Header, Payload, Signature**

JWT structure:
```
eyJ... . eyJ... . SflK...
HEADER  PAYLOAD  SIGNATURE
```

Each part is Base64URL encoded (except signature which is encrypted).

---

**Question 2: B) Short access tokens limit exposure if stolen; refresh tokens enable revocation**

The two-token system provides:
- **Security**: Access tokens expire quickly (15 min) limiting damage if stolen
- **UX**: Users don't have to login every 15 minutes (refresh tokens last 7 days)
- **Control**: You can revoke refresh tokens but can't revoke JWTs (they're stateless)

---

**Question 3: B) It prevents attackers from enumerating valid email addresses**

Different messages leak information:

```kotlin
// ❌ Information leak
if (email not found) → "Email doesn't exist"
if (wrong password) → "Password is incorrect"
// Attacker now knows alice@example.com is a valid account!

// ✅ Secure
// Both cases → "Invalid email or password"
```

---

**Question 4: C) `sub` (subject)**

Standard JWT claims:
- `sub`: Subject (user identifier)
- `iss`: Issuer (who created token)
- `aud`: Audience (who token is for)
- `exp`: Expiration timestamp
- `iat`: Issued at timestamp

---

**Question 5: C) To protect users if database is breached (like password hashing)**

If refresh tokens are stored in plaintext:
```
Database breached → Attacker gets all refresh tokens →
Can impersonate any user for 7 days!
```

If refresh tokens are hashed:
```
Database breached → Attacker gets hashes →
Can't use them (one-way hashing) → Users are safe!
```

---

## What You've Learned

✅ What JWTs are and how they enable stateless authentication
✅ JWT structure (header, payload, signature) and how signing works
✅ How to implement login with password verification and JWT generation
✅ Security best practices (generic error messages, timing attack prevention)
✅ How to create refresh token systems for better security and UX
✅ Token rotation and revocation strategies
✅ Why short-lived access tokens + long-lived refresh tokens are industry standard

---

## Next Steps

In **Lesson 5.12**, you'll learn how to **protect routes with JWT authentication**. You'll discover:
- How to configure Ktor's JWT authentication plugin
- How to create authenticated routes that require valid tokens
- How to extract user information from tokens in route handlers
- How to implement role-based access control (admin vs regular users)

The foundation you built today makes all of this possible!

# Lesson 5.10: Authentication - User Registration & Password Hashing

**Estimated Time**: 65 minutes

---

## Topic Introduction

You've built APIs that create, read, update, and delete data. But what if you need to know *who* is making the request? What if some users should have access to certain data while others shouldn't?

That's where authentication comes in. In this lesson, you'll learn how to securely register users and protect their passwords using industry-standard hashing techniques. This is the first step in building a complete authentication system.

**Warning**: Password security is critical. Done wrong, you expose your users to identity theft and your company to lawsuits. We'll learn how to do it right.

---

## The Concept

### The Bank Vault Analogy

Think of password hashing like a bank vault combination:

**Bad Approach (Storing Plaintext Passwords)**:
- Writing the combination on a sticky note
- Anyone who sees it (hackers, rogue employees, backups) can open the vault
- If the note is stolen, every vault using that combination is compromised
- 💀 Catastrophic security failure

**Good Approach (Hashing Passwords)**:
- The combination goes through a one-way machine
- Machine outputs a unique fingerprint of the combination
- You store the fingerprint, not the combination
- To verify: run their attempt through the same machine, compare fingerprints
- Even if the fingerprint is stolen, it can't be reversed back to the combination
- ✅ Secure!

### Hashing vs Encryption: Critical Difference

| Aspect | Hashing | Encryption |
|--------|---------|------------|
| **Direction** | One-way (irreversible) | Two-way (reversible) |
| **Purpose** | Verify data without storing it | Protect data in transit/storage |
| **Can be decoded?** | ❌ No (by design!) | ✅ Yes (with key) |
| **Use for passwords?** | ✅ Always | ❌ Never |
| **Example** | bcrypt, argon2 | AES, RSA |

**Why hashing for passwords?**

If you encrypt passwords, the decryption key must exist somewhere in your system. If hackers get that key, they decrypt every password. With hashing, there's nothing to steal—the original passwords simply don't exist in your system.

### The Rainbow Table Problem

Early password systems used simple hashing (like MD5):

```
Password: "password123"
MD5 Hash: 482c811da5d5b4bc6d497ffa98491e38
```

Hackers created "rainbow tables"—massive databases mapping common passwords to their hashes:

```
Rainbow Table:
password123 → 482c811da5d5b4bc6d497ffa98491e38
letmein     → 0d107d09f5bbe40cade3de5c71e9e9b7
qwerty      → d8578edf8458ce06fbc5bb76a58c5ca4
```

If your database is breached, they instantly crack every password by looking up hashes in the table.

**Solution: Salting**

A "salt" is random data added to each password before hashing:

```
User 1: "password123" + "aX9$mK2p" → bcrypt → $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
User 2: "password123" + "bQ3#nL8r" → bcrypt → $2a$10$XvjKN7LZJkqD5J9Kk9Kk9e7Kk9Kk9Kk9Kk9Kk9Kk9Kk9Kk9Kk9
```

Same password, different salts = different hashes! Rainbow tables are useless.

### Why bcrypt?

Modern password hashing needs three properties:

1. **Slow**: Takes time to compute (makes brute-force attacks impractical)
2. **Adaptive**: Can increase cost as computers get faster
3. **Salted**: Built-in random salt for each password

**bcrypt** provides all three:

```
bcrypt(password, cost=12)
        ↓
Cost factor: 2^12 = 4,096 rounds
(Adjustable: 10=fast, 12=default, 14=very secure but slower)
```

As computers improve, just increase the cost factor. Your password system stays secure for years.

---

## Setting Up User Registration

### Step 1: Add bcrypt Dependency

Add bcrypt to your `build.gradle.kts`:

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

    // bcrypt for password hashing
    implementation("de.nycode:bcrypt:2.3.0")
}
```

Sync your Gradle project to download the dependency.

### Step 2: Create User Model and Table

```kotlin
// src/main/kotlin/com/example/models/User.kt
package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.time.LocalDateTime

/**
 * User database table definition
 */
object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)  // bcrypt hashes are 60 chars
    val fullName = varchar("full_name", 255)
    val createdAt = varchar("created_at", 50)

    override val primaryKey = PrimaryKey(id)
}

/**
 * User domain model (NEVER includes password hash)
 */
@Serializable
data class User(
    val id: Int,
    val email: String,
    val fullName: String,
    val createdAt: String
)

/**
 * User registration request
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
)

/**
 * Registration response
 */
@Serializable
data class RegisterResponse(
    val user: User,
    val message: String = "Registration successful"
)
```

**Key Security Principle**: The `User` model exposed to clients NEVER includes the password hash. That stays in the database layer only.

### Step 3: Create Password Hashing Utility

```kotlin
// src/main/kotlin/com/example/security/PasswordHasher.kt
package com.example.security

import de.nycode.bcrypt.hash
import de.nycode.bcrypt.verify

/**
 * Utility for securely hashing and verifying passwords using bcrypt
 */
object PasswordHasher {

    /**
     * Cost factor for bcrypt (2^12 = 4,096 rounds)
     * Higher = more secure but slower
     * Recommended: 10-14 (12 is a good default)
     */
    private const val COST_FACTOR = 12

    /**
     * Hash a plaintext password using bcrypt
     *
     * @param password The plaintext password
     * @return The bcrypt hash (includes salt automatically)
     */
    fun hashPassword(password: String): String {
        return hash(password, COST_FACTOR)
    }

    /**
     * Verify that a plaintext password matches a bcrypt hash
     *
     * @param password The plaintext password to check
     * @param hashedPassword The bcrypt hash to verify against
     * @return true if password matches, false otherwise
     */
    fun verifyPassword(password: String, hashedPassword: String): Boolean {
        return try {
            verify(password, hashedPassword)
        } catch (e: Exception) {
            // If verification fails due to invalid hash format, return false
            false
        }
    }
}
```

### Step 4: Create Password Validator

Strong passwords are essential. Let's enforce requirements:

```kotlin
// src/main/kotlin/com/example/validation/PasswordValidator.kt
package com.example.validation

/**
 * Validates password strength requirements
 */
object PasswordValidator {

    private const val MIN_LENGTH = 8
    private const val MAX_LENGTH = 128

    /**
     * Validate password strength
     * Returns list of validation errors (empty if valid)
     */
    fun validate(password: String): List<String> {
        val errors = mutableListOf<String>()

        // Length check
        if (password.length < MIN_LENGTH) {
            errors.add("Password must be at least $MIN_LENGTH characters long")
        }
        if (password.length > MAX_LENGTH) {
            errors.add("Password must be at most $MAX_LENGTH characters long")
        }

        // Complexity checks
        if (!password.any { it.isUpperCase() }) {
            errors.add("Password must contain at least one uppercase letter")
        }
        if (!password.any { it.isLowerCase() }) {
            errors.add("Password must contain at least one lowercase letter")
        }
        if (!password.any { it.isDigit() }) {
            errors.add("Password must contain at least one number")
        }
        if (!password.any { !it.isLetterOrDigit() }) {
            errors.add("Password must contain at least one special character")
        }

        return errors
    }

    /**
     * Check if password meets all requirements
     */
    fun isValid(password: String): Boolean {
        return validate(password).isEmpty()
    }
}
```

### Step 5: Create User Validator

```kotlin
// src/main/kotlin/com/example/validation/UserValidator.kt
package com.example.validation

import com.example.models.RegisterRequest

class UserValidator : Validator<RegisterRequest>() {

    override fun validate(value: RegisterRequest): ValidationResult {
        // Email validation
        validateRequired("email", value.email)
        validateEmail("email", value.email)

        // Password validation
        validateRequired("password", value.password)

        // Use PasswordValidator for strength checks
        val passwordErrors = PasswordValidator.validate(value.password)
        passwordErrors.forEach { error ->
            result.addError("password", error)
        }

        // Full name validation
        validateRequired("fullName", value.fullName, "Full name")
        validateLength("fullName", value.fullName, min = 2, max = 255, fieldName = "Full name")

        return result
    }
}
```

### Step 6: Create User Repository

```kotlin
// src/main/kotlin/com/example/repositories/UserRepository.kt
package com.example.repositories

import com.example.models.User
import com.example.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

interface UserRepository {
    fun insert(email: String, passwordHash: String, fullName: String): Int
    fun getById(id: Int): User?
    fun getByEmail(email: String): User?
    fun getPasswordHash(email: String): String?
    fun emailExists(email: String): Boolean
}

class UserRepositoryImpl : UserRepository {

    /**
     * Create a new user
     * Returns the generated user ID
     */
    override fun insert(email: String, passwordHash: String, fullName: String): Int {
        return transaction {
            Users.insert {
                it[Users.email] = email.lowercase().trim()
                it[Users.passwordHash] = passwordHash
                it[Users.fullName] = fullName.trim()
                it[createdAt] = LocalDateTime.now().toString()
            }[Users.id]
        }
    }

    /**
     * Get user by ID (without password hash)
     */
    override fun getById(id: Int): User? {
        return transaction {
            Users.selectAll()
                .where { Users.id eq id }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    /**
     * Get user by email (without password hash)
     */
    override fun getByEmail(email: String): User? {
        return transaction {
            Users.selectAll()
                .where { Users.email eq email.lowercase().trim() }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    /**
     * Get ONLY the password hash for authentication
     * This is the only method that retrieves the password hash
     */
    override fun getPasswordHash(email: String): String? {
        return transaction {
            Users.select(Users.passwordHash)
                .where { Users.email eq email.lowercase().trim() }
                .map { it[Users.passwordHash] }
                .singleOrNull()
        }
    }

    /**
     * Check if email already exists
     */
    override fun emailExists(email: String): Boolean {
        return transaction {
            Users.selectAll()
                .where { Users.email eq email.lowercase().trim() }
                .count() > 0
        }
    }

    /**
     * Map database row to User model
     * IMPORTANT: Does NOT include password hash
     */
    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            email = row[Users.email],
            fullName = row[Users.fullName],
            createdAt = row[Users.createdAt]
        )
    }
}
```

### Step 7: Create User Service with Registration Logic

```kotlin
// src/main/kotlin/com/example/services/UserService.kt
package com.example.services

import com.example.exceptions.ConflictException
import com.example.exceptions.ValidationException
import com.example.models.RegisterRequest
import com.example.models.User
import com.example.repositories.UserRepository
import com.example.security.PasswordHasher
import com.example.validation.UserValidator

class UserService(
    private val userRepository: UserRepository
) {
    private val validator = UserValidator()

    /**
     * Register a new user
     */
    fun register(request: RegisterRequest): Result<User> {
        return try {
            // Step 1: Validate input
            val validationResult = validator.validate(request)
            if (!validationResult.isValid) {
                throw ValidationException(
                    "Validation failed",
                    validationResult.errorMap
                )
            }

            // Step 2: Check if email already exists
            if (userRepository.emailExists(request.email)) {
                throw ConflictException(
                    "An account with email '${request.email}' already exists"
                )
            }

            // Step 3: Hash the password
            val passwordHash = PasswordHasher.hashPassword(request.password)

            // Step 4: Create user in database
            val userId = userRepository.insert(
                email = request.email,
                passwordHash = passwordHash,
                fullName = request.fullName
            )

            // Step 5: Retrieve and return the created user (without password hash)
            val user = userRepository.getById(userId)
                ?: throw RuntimeException("Failed to retrieve created user")

            Result.success(user)

        } catch (e: ValidationException) {
            Result.failure(e)
        } catch (e: ConflictException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error during registration: ${e.message}")
            e.printStackTrace()
            Result.failure(RuntimeException("An unexpected error occurred during registration"))
        }
    }

    /**
     * Get user by ID
     */
    fun getUserById(id: Int): Result<User> {
        return try {
            val user = userRepository.getById(id)
                ?: return Result.failure(
                    com.example.exceptions.NotFoundException("User not found")
                )

            Result.success(user)
        } catch (e: Exception) {
            println("Error fetching user: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Get user by email
     */
    fun getUserByEmail(email: String): Result<User> {
        return try {
            val user = userRepository.getByEmail(email)
                ?: return Result.failure(
                    com.example.exceptions.NotFoundException("User not found")
                )

            Result.success(user)
        } catch (e: Exception) {
            println("Error fetching user: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }
}
```

### Step 8: Create Registration Route

```kotlin
// src/main/kotlin/com/example/routes/AuthRoutes.kt
package com.example.routes

import com.example.models.ApiResponse
import com.example.models.RegisterRequest
import com.example.models.RegisterResponse
import com.example.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(userService: UserService) {
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
                    throw error  // Let StatusPages handle it
                }
        }

        /**
         * GET /api/auth/me
         * Get current user (placeholder - will implement with JWT in next lesson)
         */
        get("/me") {
            call.respond(
                HttpStatusCode.NotImplemented,
                ApiResponse<Unit>(
                    success = false,
                    message = "Authentication required. Implement JWT in next lesson."
                )
            )
        }
    }
}
```

### Step 9: Update Database Factory

Add the Users table to schema creation:

```kotlin
// src/main/kotlin/com/example/database/DatabaseFactory.kt
package com.example.database

import com.example.models.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val database = Database.connect(createHikariDataSource())

        transaction(database) {
            addLogger(StdOutSqlLogger)

            // Create all tables
            SchemaUtils.create(Users)
        }
    }

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
            driverClassName = "org.h2.Driver"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}
```

### Step 10: Wire Everything Together

```kotlin
// src/main/kotlin/com/example/Application.kt
package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.configureErrorHandling
import com.example.repositories.UserRepositoryImpl
import com.example.routes.authRoutes
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

    // Configure routes
    routing {
        authRoutes(userService)
    }
}
```

---

## Code Breakdown

### The Registration Flow

```
1. Client sends POST /api/auth/register
   {
     "email": "alice@example.com",
     "password": "SecurePass123!",
     "fullName": "Alice Johnson"
   }
   ↓
2. Route deserializes JSON → RegisterRequest
   ↓
3. UserService.register(request)
   ↓
4. UserValidator validates input
   - Email format check
   - Password strength (length, complexity)
   - Full name requirements
   ↓
5. Check if email already exists
   ↓
6. PasswordHasher.hashPassword("SecurePass123!")
   ↓ bcrypt with cost=12
   "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5sPVJXMBvLN4."
   ↓
7. Insert into database:
   - email: alice@example.com
   - passwordHash: $2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5sPVJXMBvLN4.
   - fullName: Alice Johnson
   - createdAt: 2025-01-15T10:30:45.123
   ↓
8. Retrieve created user (WITHOUT password hash)
   ↓
9. Return 201 Created
   {
     "success": true,
     "data": {
       "user": {
         "id": 1,
         "email": "alice@example.com",
         "fullName": "Alice Johnson",
         "createdAt": "2025-01-15T10:30:45.123"
       },
       "message": "Registration successful. You can now log in."
     }
   }
```

### Security Highlights

**1. Password Never Stored in Plaintext**:
```kotlin
// ❌ NEVER DO THIS
it[password] = request.password  // Storing plaintext = security disaster

// ✅ ALWAYS DO THIS
val passwordHash = PasswordHasher.hashPassword(request.password)
it[passwordHash] = passwordHash  // Storing bcrypt hash = secure
```

**2. Password Hash Never Exposed**:
```kotlin
// User model doesn't even have a passwordHash field
@Serializable
data class User(
    val id: Int,
    val email: String,
    val fullName: String,
    val createdAt: String
    // No passwordHash here!
)
```

**3. Separate Method for Password Retrieval**:
```kotlin
// Only used during login (next lesson)
override fun getPasswordHash(email: String): String? {
    // Returns hash for verification only
}
```

**4. Email Case-Insensitivity**:
```kotlin
// "Alice@Example.COM" and "alice@example.com" are the same user
it[Users.email] = email.lowercase().trim()
```

---

## Testing User Registration

### Test 1: Successful Registration

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "SecurePass123!",
    "fullName": "Alice Johnson"
  }'
```

Response (201 Created):
```json
{
  "success": true,
  "data": {
    "user": {
      "id": 1,
      "email": "alice@example.com",
      "fullName": "Alice Johnson",
      "createdAt": "2025-01-15T10:30:45.123456"
    },
    "message": "Registration successful. You can now log in."
  }
}
```

### Test 2: Weak Password

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "bob@example.com",
    "password": "weak",
    "fullName": "Bob Smith"
  }'
```

Response (400 Bad Request):
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "password": [
      "Password must be at least 8 characters long",
      "Password must contain at least one uppercase letter",
      "Password must contain at least one number",
      "Password must contain at least one special character"
    ]
  },
  "timestamp": "2025-01-15T10:31:22.456"
}
```

### Test 3: Duplicate Email

```bash
# Try to register with Alice's email again
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "AnotherPass456!",
    "fullName": "Fake Alice"
  }'
```

Response (409 Conflict):
```json
{
  "success": false,
  "message": "An account with email 'alice@example.com' already exists",
  "timestamp": "2025-01-15T10:32:10.789"
}
```

### Test 4: Invalid Email Format

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "not-an-email",
    "password": "ValidPass123!",
    "fullName": "Charlie Brown"
  }'
```

Response (400 Bad Request):
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "email": ["email must be a valid email address"]
  },
  "timestamp": "2025-01-15T10:33:45.012"
}
```

---

## Exercise: Enhanced User Profile

Extend the user registration system with additional features.

### Requirements

1. **Add Profile Fields**:
   - Username (required, unique, 3-20 chars, alphanumeric + underscore only)
   - Bio (optional, max 500 chars)
   - Date of birth (required, must be 13+ years old)
   - Phone number (optional, if provided must match pattern: +1-XXX-XXX-XXXX)

2. **Update User Model**:
   - Include new fields in User and RegisterRequest
   - Add database columns

3. **Create Username Validator**:
   - Length: 3-20 characters
   - Pattern: Only letters, numbers, underscore
   - Must not start with underscore or number
   - Check uniqueness

4. **Create Age Validator**:
   - Parse date of birth
   - Calculate age
   - Ensure user is at least 13 years old (COPPA compliance)

5. **Create Phone Validator**:
   - Optional but must match pattern if provided
   - Format: +1-XXX-XXX-XXXX (US phone numbers)

### Starter Code

```kotlin
@Serializable
data class User(
    val id: Int,
    val email: String,
    val username: String,
    val fullName: String,
    val bio: String?,
    val dateOfBirth: String,
    val phoneNumber: String?,
    val createdAt: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val fullName: String,
    val bio: String? = null,
    val dateOfBirth: String,  // Format: YYYY-MM-DD
    val phoneNumber: String? = null
)

// TODO: Update Users table definition
// TODO: Implement enhanced UserValidator
// TODO: Update UserRepository
// TODO: Test all validation rules
```

---

## Solution

### Enhanced User System

```kotlin
// src/main/kotlin/com/example/models/User.kt
package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 50).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)
    val fullName = varchar("full_name", 255)
    val bio = varchar("bio", 500).nullable()
    val dateOfBirth = varchar("date_of_birth", 10)  // YYYY-MM-DD
    val phoneNumber = varchar("phone_number", 20).nullable()
    val createdAt = varchar("created_at", 50)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class User(
    val id: Int,
    val email: String,
    val username: String,
    val fullName: String,
    val bio: String?,
    val dateOfBirth: String,
    val phoneNumber: String?,
    val createdAt: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val fullName: String,
    val bio: String? = null,
    val dateOfBirth: String,
    val phoneNumber: String? = null
)
```

```kotlin
// src/main/kotlin/com/example/validation/UserValidator.kt
package com.example.validation

import com.example.models.RegisterRequest
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class UserValidator : Validator<RegisterRequest>() {

    companion object {
        private val USERNAME_PATTERN = "^[a-zA-Z][a-zA-Z0-9_]{2,19}$".toRegex()
        private val PHONE_PATTERN = "^\\+1-\\d{3}-\\d{3}-\\d{4}$".toRegex()
        private const val MIN_AGE = 13
    }

    override fun validate(value: RegisterRequest): ValidationResult {
        // Email validation
        validateRequired("email", value.email)
        validateEmail("email", value.email)

        // Username validation
        validateRequired("username", value.username)
        validateLength("username", value.username, min = 3, max = 20)
        validatePattern(
            "username",
            value.username,
            USERNAME_PATTERN,
            "Username must start with a letter and contain only letters, numbers, and underscores"
        )

        // Password validation
        validateRequired("password", value.password)
        val passwordErrors = PasswordValidator.validate(value.password)
        passwordErrors.forEach { error ->
            result.addError("password", error)
        }

        // Full name validation
        validateRequired("fullName", value.fullName, "Full name")
        validateLength("fullName", value.fullName, min = 2, max = 255, fieldName = "Full name")

        // Bio validation (optional)
        validateLength("bio", value.bio, max = 500)

        // Date of birth validation
        validateRequired("dateOfBirth", value.dateOfBirth, "Date of birth")
        validateAge(value.dateOfBirth)

        // Phone number validation (optional but must be valid if provided)
        value.phoneNumber?.let { phone ->
            if (phone.isNotBlank()) {
                validatePattern(
                    "phoneNumber",
                    phone,
                    PHONE_PATTERN,
                    "Phone number must be in format: +1-XXX-XXX-XXXX",
                    "Phone number"
                )
            }
        }

        return result
    }

    /**
     * Validate age requirements (must be 13+ for COPPA compliance)
     */
    private fun validateAge(dateOfBirth: String) {
        try {
            val dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE)
            val today = LocalDate.now()

            // Check if date is in the future
            if (dob.isAfter(today)) {
                result.addError("dateOfBirth", "Date of birth cannot be in the future")
                return
            }

            // Calculate age
            val age = Period.between(dob, today).years

            if (age < MIN_AGE) {
                result.addError(
                    "dateOfBirth",
                    "You must be at least $MIN_AGE years old to register"
                )
            }

        } catch (e: DateTimeParseException) {
            result.addError(
                "dateOfBirth",
                "Date of birth must be in format YYYY-MM-DD (e.g., 1990-01-15)"
            )
        }
    }
}
```

```kotlin
// src/main/kotlin/com/example/repositories/UserRepository.kt
package com.example.repositories

import com.example.models.User
import com.example.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

interface UserRepository {
    fun insert(
        email: String,
        username: String,
        passwordHash: String,
        fullName: String,
        bio: String?,
        dateOfBirth: String,
        phoneNumber: String?
    ): Int
    fun getById(id: Int): User?
    fun getByEmail(email: String): User?
    fun getByUsername(username: String): User?
    fun getPasswordHash(email: String): String?
    fun emailExists(email: String): Boolean
    fun usernameExists(username: String): Boolean
}

class UserRepositoryImpl : UserRepository {

    override fun insert(
        email: String,
        username: String,
        passwordHash: String,
        fullName: String,
        bio: String?,
        dateOfBirth: String,
        phoneNumber: String?
    ): Int {
        return transaction {
            Users.insert {
                it[Users.email] = email.lowercase().trim()
                it[Users.username] = username.trim()
                it[Users.passwordHash] = passwordHash
                it[Users.fullName] = fullName.trim()
                it[Users.bio] = bio?.trim()
                it[Users.dateOfBirth] = dateOfBirth
                it[Users.phoneNumber] = phoneNumber?.trim()
                it[createdAt] = LocalDateTime.now().toString()
            }[Users.id]
        }
    }

    override fun getById(id: Int): User? {
        return transaction {
            Users.selectAll()
                .where { Users.id eq id }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override fun getByEmail(email: String): User? {
        return transaction {
            Users.selectAll()
                .where { Users.email eq email.lowercase().trim() }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override fun getByUsername(username: String): User? {
        return transaction {
            Users.selectAll()
                .where { Users.username eq username.trim() }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override fun getPasswordHash(email: String): String? {
        return transaction {
            Users.select(Users.passwordHash)
                .where { Users.email eq email.lowercase().trim() }
                .map { it[Users.passwordHash] }
                .singleOrNull()
        }
    }

    override fun emailExists(email: String): Boolean {
        return transaction {
            Users.selectAll()
                .where { Users.email eq email.lowercase().trim() }
                .count() > 0
        }
    }

    override fun usernameExists(username: String): Boolean {
        return transaction {
            Users.selectAll()
                .where { Users.username eq username.trim() }
                .count() > 0
        }
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            email = row[Users.email],
            username = row[Users.username],
            fullName = row[Users.fullName],
            bio = row[Users.bio],
            dateOfBirth = row[Users.dateOfBirth],
            phoneNumber = row[Users.phoneNumber],
            createdAt = row[Users.createdAt]
        )
    }
}
```

```kotlin
// src/main/kotlin/com/example/services/UserService.kt
package com.example.services

import com.example.exceptions.ConflictException
import com.example.exceptions.ValidationException
import com.example.models.RegisterRequest
import com.example.models.User
import com.example.repositories.UserRepository
import com.example.security.PasswordHasher
import com.example.validation.UserValidator

class UserService(
    private val userRepository: UserRepository
) {
    private val validator = UserValidator()

    fun register(request: RegisterRequest): Result<User> {
        return try {
            // Validate input
            val validationResult = validator.validate(request)
            if (!validationResult.isValid) {
                throw ValidationException(
                    "Validation failed",
                    validationResult.errorMap
                )
            }

            // Check email uniqueness
            if (userRepository.emailExists(request.email)) {
                throw ConflictException(
                    "An account with email '${request.email}' already exists"
                )
            }

            // Check username uniqueness
            if (userRepository.usernameExists(request.username)) {
                throw ConflictException(
                    "Username '${request.username}' is already taken"
                )
            }

            // Hash password
            val passwordHash = PasswordHasher.hashPassword(request.password)

            // Create user
            val userId = userRepository.insert(
                email = request.email,
                username = request.username,
                passwordHash = passwordHash,
                fullName = request.fullName,
                bio = request.bio,
                dateOfBirth = request.dateOfBirth,
                phoneNumber = request.phoneNumber
            )

            // Retrieve and return
            val user = userRepository.getById(userId)!!
            Result.success(user)

        } catch (e: ValidationException) {
            Result.failure(e)
        } catch (e: ConflictException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error during registration: ${e.message}")
            e.printStackTrace()
            Result.failure(RuntimeException("An unexpected error occurred during registration"))
        }
    }

    fun getUserById(id: Int): Result<User> {
        return try {
            val user = userRepository.getById(id)
                ?: return Result.failure(
                    com.example.exceptions.NotFoundException("User not found")
                )
            Result.success(user)
        } catch (e: Exception) {
            println("Error fetching user: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }
}
```

### Test Cases

**Valid Registration**:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "username": "alice_wonder",
    "password": "SecurePass123!",
    "fullName": "Alice Johnson",
    "bio": "Software developer and coffee enthusiast",
    "dateOfBirth": "1995-03-15",
    "phoneNumber": "+1-555-123-4567"
  }'
```

**Invalid Username (starts with number)**:
```json
{
  "username": "123alice",
  // ... other fields
}
```
Error: "Username must start with a letter..."

**Underage User**:
```json
{
  "dateOfBirth": "2020-01-01",
  // ... other fields
}
```
Error: "You must be at least 13 years old to register"

**Invalid Phone Format**:
```json
{
  "phoneNumber": "555-1234",
  // ... other fields
}
```
Error: "Phone number must be in format: +1-XXX-XXX-XXXX"

---

## Solution Explanation

### Key Enhancements

**1. Username Uniqueness**:
Both email AND username must be unique. We check both before creating the user.

**2. Age Validation with LocalDate**:
```kotlin
val dob = LocalDate.parse(dateOfBirth, DateTimeFormatter.ISO_LOCAL_DATE)
val age = Period.between(dob, LocalDate.now()).years
```
Properly calculates age accounting for leap years and time zones.

**3. Optional but Validated Fields**:
```kotlin
value.phoneNumber?.let { phone ->
    if (phone.isNotBlank()) {
        validatePattern(...)  // Only validate if provided
    }
}
```
Bio and phone are optional, but if provided they must meet format requirements.

**4. COPPA Compliance**:
The 13+ age requirement ensures compliance with US Children's Online Privacy Protection Act.

---

## Why This Matters

### Real-World Security

**Password Breach Statistics** (2024 data):
- 81% of data breaches involve stolen/weak passwords
- Average cost of a data breach: $4.45 million
- Companies that store plaintext passwords face massive fines and lawsuits

**Your Responsibility as a Developer**:
When you store user passwords, you're responsible for protecting them. Using bcrypt with proper salting and cost factors is not optional—it's a legal and ethical requirement.

### Industry Standards

**OWASP Top 10 (2023)**:
- #2: Cryptographic Failures (storing passwords insecurely)
- #7: Identification and Authentication Failures

Implementing what you learned today directly addresses two of the top security vulnerabilities.

---

## Checkpoint Quiz

### Question 1
What's the critical difference between hashing and encryption?

A) Hashing is faster than encryption
B) Hashing is one-way (irreversible), encryption is two-way (reversible)
C) Hashing uses more CPU than encryption
D) They're the same thing

### Question 2
What is a "salt" in password hashing?

A) Random data added to each password before hashing
B) A type of encryption algorithm
C) The cost factor in bcrypt
D) The password strength requirement

### Question 3
Why should you NEVER expose password hashes in API responses?

A) They take up too much bandwidth
B) They're ugly and users don't need them
C) Attackers can use them for offline brute-force attacks
D) It violates JSON formatting standards

### Question 4
What is the recommended bcrypt cost factor for 2025?

A) 4 (fast)
B) 8 (balanced)
C) 12 (secure, recommended default)
D) 20 (maximum security)

### Question 5
Why do we check email uniqueness BEFORE hashing the password?

A) It's required by the database
B) It saves CPU cycles (hashing is expensive, no point if email is duplicate)
C) It makes the code run faster
D) bcrypt doesn't work with duplicate emails

---

## Quiz Answers

**Question 1: B) Hashing is one-way (irreversible), encryption is two-way (reversible)**

This is the fundamental difference:
- **Hashing**: password → hash (no reverse operation possible)
- **Encryption**: password → encrypted → decrypt → password

For passwords, you want one-way hashing so even you can't retrieve the original password.

---

**Question 2: A) Random data added to each password before hashing**

Salt prevents rainbow table attacks:

```
Without salt:
User 1: "password123" → same hash
User 2: "password123" → same hash (vulnerable to rainbow tables!)

With salt:
User 1: "password123" + "aX9$mK2p" → unique hash
User 2: "password123" + "bQ3#nL8r" → different unique hash
```

bcrypt generates and stores the salt automatically in the hash output.

---

**Question 3: C) Attackers can use them for offline brute-force attacks**

If an attacker gets the hash, they can:
1. Try millions of passwords offline
2. Hash each attempt with bcrypt
3. Compare to the stolen hash
4. Eventually crack weak passwords

This is why strong passwords and high cost factors matter—they make this attack impractically slow.

---

**Question 4: C) 12 (secure, recommended default)**

Cost factor guidelines:
- **10**: Fast but less secure, ok for low-security applications
- **12**: Recommended default (takes ~250-350ms per hash)
- **14**: Very secure but slower (~1-1.5s per hash)
- **16+**: Overkill for most applications, may hurt UX

Cost=12 balances security with user experience.

---

**Question 5: B) It saves CPU cycles (hashing is expensive, no point if email is duplicate)**

Order of operations matters:

```kotlin
// ✅ Efficient: Check uniqueness first
if (userRepository.emailExists(request.email)) {
    throw ConflictException(...)  // Fast database lookup
}
val hash = PasswordHasher.hashPassword(request.password)  // Expensive bcrypt

// ❌ Wasteful: Hash first, then check
val hash = PasswordHasher.hashPassword(request.password)  // Wasted CPU if email is duplicate
if (userRepository.emailExists(request.email)) {
    throw ConflictException(...)
}
```

Fail fast on cheap operations before expensive ones.

---

## What You've Learned

✅ Why password security is critical and the consequences of doing it wrong
✅ The difference between hashing and encryption (one-way vs two-way)
✅ How salting protects against rainbow table attacks
✅ Why bcrypt is the industry standard for password hashing
✅ How to implement secure user registration with password hashing
✅ How to validate password strength with multiple requirements
✅ How to properly structure user models to never expose password hashes
✅ Best practices for email uniqueness and case-insensitivity

---

## Next Steps

In **Lesson 5.11**, you'll implement the login system using the hashed passwords you just created. You'll learn:
- How to verify passwords against bcrypt hashes
- How to generate JWT (JSON Web Tokens) for authenticated sessions
- How to handle login errors securely (without revealing whether email exists)
- Token expiration and refresh strategies

The foundation you built today makes authentication possible!

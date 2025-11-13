# Lesson 7.4: Security Best Practices

**Estimated Time**: 90 minutes

---

## Introduction

Security isn't optional - it's your responsibility as a developer.

A single security vulnerability can:
- Expose millions of user credentials
- Cost companies millions in damages
- Destroy user trust forever
- End careers

In this lesson, you'll master security best practices for Kotlin applications:
- ✅ Secure coding principles
- ✅ Input validation and sanitization
- ✅ Encryption and hashing
- ✅ API security (OAuth 2.0, JWT)
- ✅ Android security (KeyStore, ProGuard/R8)
- ✅ OWASP Top 10 vulnerabilities

By the end, you'll build applications that protect user data and withstand attacks.

---

## The Cost of Insecurity

### Real-World Breaches

**Equifax (2017)**:
- Vulnerability: Unpatched Apache Struts
- Impact: 147 million records exposed
- Cost: $1.4 billion in damages
- Cause: Security neglect

**Facebook (2019)**:
- Vulnerability: Passwords stored in plaintext
- Impact: 600 million passwords exposed
- Cause: Not hashing passwords

**Uber (2016)**:
- Vulnerability: AWS keys in GitHub repository
- Impact: 57 million users compromised
- Cost: $148 million fine
- Cause: Hardcoded secrets

**The Pattern**: These weren't sophisticated attacks. They were basic security mistakes that could have been prevented.

---

## Secure Coding Principles

### Principle 1: Defense in Depth

Never rely on a single security measure.

❌ **Bad** (Single layer):
```kotlin
fun login(username: String, password: String): User? {
    // Only checks password
    return if (password == user.password) user else null
}
```

✅ **Good** (Multiple layers):
```kotlin
fun login(username: String, password: String, ipAddress: String): LoginResult {
    // Layer 1: Rate limiting
    if (rateLimiter.isBlocked(ipAddress)) {
        return LoginResult.RateLimited
    }

    // Layer 2: Account lock after failures
    if (accountLockService.isLocked(username)) {
        return LoginResult.AccountLocked
    }

    // Layer 3: Password verification with bcrypt
    val user = userRepository.findByUsername(username) ?: return LoginResult.InvalidCredentials

    if (!BCrypt.checkpw(password, user.passwordHash)) {
        accountLockService.recordFailedAttempt(username)
        return LoginResult.InvalidCredentials
    }

    // Layer 4: Two-factor authentication
    if (user.has2FA) {
        return LoginResult.Requires2FA(user.id)
    }

    // Layer 5: Audit logging
    auditLog.recordLogin(user.id, ipAddress)

    return LoginResult.Success(user)
}
```

### Principle 2: Least Privilege

Grant minimum permissions necessary.

❌ **Bad** (Admin for everyone):
```kotlin
@Entity
data class User(
    val id: String,
    val email: String,
    val role: String = "ADMIN" // ⚠️ Default admin!
)
```

✅ **Good** (Minimal permissions):
```kotlin
enum class Role {
    USER,           // Can view own data
    MODERATOR,      // Can moderate content
    ADMIN           // Full access
}

@Entity
data class User(
    val id: String,
    val email: String,
    val role: Role = Role.USER // ✅ Default to least privilege
)

fun checkPermission(user: User, action: String): Boolean {
    return when (action) {
        "view_own_data" -> true // Everyone
        "moderate_content" -> user.role in listOf(Role.MODERATOR, Role.ADMIN)
        "delete_users" -> user.role == Role.ADMIN
        else -> false
    }
}
```

### Principle 3: Fail Securely

When errors occur, fail in a secure state.

❌ **Bad** (Fails open):
```kotlin
fun checkAccess(userId: String, resourceId: String): Boolean {
    return try {
        val user = userService.getUser(userId)
        val resource = resourceService.getResource(resourceId)
        user.hasAccessTo(resource)
    } catch (e: Exception) {
        // ⚠️ Error = grant access!
        true
    }
}
```

✅ **Good** (Fails closed):
```kotlin
fun checkAccess(userId: String, resourceId: String): Boolean {
    return try {
        val user = userService.getUser(userId) ?: return false
        val resource = resourceService.getResource(resourceId) ?: return false
        user.hasAccessTo(resource)
    } catch (e: Exception) {
        logger.error("Access check failed", e)
        // ✅ Error = deny access
        false
    }
}
```

---

## Input Validation

### Never Trust User Input

**Golden Rule**: All input is malicious until proven otherwise.

### SQL Injection Prevention

❌ **DANGER** (SQL Injection vulnerable):
```kotlin
fun findUser(username: String): User? {
    // ⚠️ NEVER DO THIS!
    val query = "SELECT * FROM users WHERE username = '$username'"
    return database.query(query)
}

// Attack:
findUser("admin' OR '1'='1")
// SQL: SELECT * FROM users WHERE username = 'admin' OR '1'='1'
// Returns all users!
```

✅ **Safe** (Parameterized queries):
```kotlin
@Query("SELECT * FROM users WHERE username = :username")
suspend fun findByUsername(username: String): User?

// Room/Exposed automatically escapes parameters
// Attack impossible!
```

### XSS Prevention

❌ **Bad** (XSS vulnerable):
```kotlin
fun displayComment(comment: String): String {
    // ⚠️ User can inject <script>
    return "<div>$comment</div>"
}

// Attack:
displayComment("<script>alert('XSS')</script>")
```

✅ **Good** (Sanitized):
```kotlin
fun sanitizeHtml(input: String): String {
    return input
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#x27;")
}

fun displayComment(comment: String): String {
    return "<div>${sanitizeHtml(comment)}</div>"
}
```

### Email Validation

❌ **Bad** (Weak validation):
```kotlin
fun isValidEmail(email: String): Boolean {
    return email.contains("@") // ⚠️ Too simple
}

// Accepts: "@@", "test@", "@example.com"
```

✅ **Good** (Robust validation):
```kotlin
object EmailValidator {
    private val pattern = Regex(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    fun isValid(email: String): Boolean {
        if (email.isBlank() || email.length > 254) return false
        if (!pattern.matches(email)) return false

        // Additional checks
        val parts = email.split("@")
        if (parts.size != 2) return false

        val (local, domain) = parts
        if (local.length > 64) return false

        return true
    }
}
```

### Path Traversal Prevention

❌ **DANGER** (Path traversal):
```kotlin
fun getFile(filename: String): File {
    // ⚠️ User can access any file!
    return File("/uploads/$filename")
}

// Attack:
getFile("../../etc/passwd")
// Accesses: /etc/passwd
```

✅ **Safe** (Validated path):
```kotlin
fun getFile(filename: String): File? {
    // Validate filename
    if (filename.contains("..") || filename.contains("/")) {
        logger.warn("Path traversal attempt: $filename")
        return null
    }

    val file = File("/uploads", filename).canonicalFile
    val uploadDir = File("/uploads").canonicalFile

    // Ensure file is within upload directory
    if (!file.path.startsWith(uploadDir.path)) {
        logger.warn("Path traversal detected: $filename")
        return null
    }

    return file
}
```

---

## Password Security

### Hashing with bcrypt

❌ **NEVER** (Plaintext):
```kotlin
@Entity
data class User(
    val email: String,
    val password: String // ⚠️ NEVER store plaintext!
)
```

❌ **BAD** (Simple hash):
```kotlin
val passwordHash = password.hashCode().toString() // ⚠️ Not secure
```

✅ **GOOD** (bcrypt):
```kotlin
// build.gradle.kts
dependencies {
    implementation("org.mindrot:jbcrypt:0.4")
}

object PasswordHasher {
    private const val LOG_ROUNDS = 12 // Cost factor

    fun hash(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt(LOG_ROUNDS))
    }

    fun verify(password: String, hash: String): Boolean {
        return try {
            BCrypt.checkpw(password, hash)
        } catch (e: Exception) {
            false
        }
    }
}

@Entity
data class User(
    val email: String,
    val passwordHash: String // ✅ Hashed with bcrypt
)

// Usage
fun register(email: String, password: String): User {
    val passwordHash = PasswordHasher.hash(password)
    return User(email, passwordHash)
}

fun login(email: String, password: String): Boolean {
    val user = findUserByEmail(email) ?: return false
    return PasswordHasher.verify(password, user.passwordHash)
}
```

### Password Strength Requirements

```kotlin
object PasswordValidator {
    data class ValidationResult(
        val isValid: Boolean,
        val errors: List<String>
    )

    fun validate(password: String): ValidationResult {
        val errors = mutableListOf<String>()

        if (password.length < 8) {
            errors.add("Password must be at least 8 characters")
        }

        if (!password.any { it.isUpperCase() }) {
            errors.add("Password must contain an uppercase letter")
        }

        if (!password.any { it.isLowerCase() }) {
            errors.add("Password must contain a lowercase letter")
        }

        if (!password.any { it.isDigit() }) {
            errors.add("Password must contain a number")
        }

        if (!password.any { "!@#$%^&*()_+-=[]{}|;:,.<>?".contains(it) }) {
            errors.add("Password must contain a special character")
        }

        // Check against common passwords
        if (isCommonPassword(password)) {
            errors.add("Password is too common")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun isCommonPassword(password: String): Boolean {
        val common = setOf(
            "password", "12345678", "qwerty", "abc123",
            "password123", "admin", "letmein"
        )
        return password.lowercase() in common
    }
}
```

---

## JWT Security

### Secure JWT Implementation

❌ **Bad** (Insecure):
```kotlin
fun generateToken(userId: String): String {
    return JWT.create()
        .withSubject(userId)
        .sign(Algorithm.none()) // ⚠️ No signature!
}
```

✅ **Good** (Secure):
```kotlin
object JwtConfig {
    private val secret = System.getenv("JWT_SECRET")
        ?: throw IllegalStateException("JWT_SECRET not set")

    private val algorithm = Algorithm.HMAC256(secret)
    private const val EXPIRATION_TIME = 3600000L // 1 hour

    fun generateToken(user: User): String {
        val now = Date()
        val expiresAt = Date(now.time + EXPIRATION_TIME)

        return JWT.create()
            .withSubject(user.id)
            .withClaim("email", user.email)
            .withClaim("role", user.role.name)
            .withIssuedAt(now)
            .withExpiresAt(expiresAt)
            .withIssuer("my-app")
            .sign(algorithm)
    }

    fun verifyToken(token: String): DecodedJWT? {
        return try {
            val verifier = JWT.require(algorithm)
                .withIssuer("my-app")
                .build()

            verifier.verify(token)
        } catch (e: JWTVerificationException) {
            logger.warn("Invalid token: ${e.message}")
            null
        }
    }
}
```

### Refresh Tokens

```kotlin
@Entity
data class RefreshToken(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val token: String,
    val expiresAt: Long,
    val createdAt: Long = System.currentTimeMillis()
)

object TokenService {
    private const val REFRESH_TOKEN_EXPIRATION = 7 * 24 * 3600000L // 7 days

    fun generateTokenPair(user: User): TokenPair {
        val accessToken = JwtConfig.generateToken(user)

        val refreshToken = RefreshToken(
            userId = user.id,
            token = generateSecureRandomToken(),
            expiresAt = System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION
        )

        refreshTokenRepository.save(refreshToken)

        return TokenPair(accessToken, refreshToken.token)
    }

    suspend fun refreshAccessToken(refreshToken: String): String? {
        val token = refreshTokenRepository.findByToken(refreshToken) ?: return null

        if (token.expiresAt < System.currentTimeMillis()) {
            refreshTokenRepository.delete(token.id)
            return null
        }

        val user = userRepository.findById(token.userId) ?: return null

        return JwtConfig.generateToken(user)
    }

    private fun generateSecureRandomToken(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }
}

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
```

---

## Android Security

### KeyStore for Secrets

❌ **Bad** (Hardcoded secrets):
```kotlin
object Config {
    const val API_KEY = "sk_live_abc123xyz" // ⚠️ Visible in APK!
}
```

✅ **Good** (KeyStore):
```kotlin
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecureStorage {
    private const val KEY_ALIAS = "app_secret_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    init {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setUserAuthenticationRequired(false)
            .build()

        keyGenerator.init(keySpec)
        keyGenerator.generateKey()
    }

    private fun getKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as SecretKey
    }

    fun encrypt(data: String): EncryptedData {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val iv = cipher.iv
        val encrypted = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

        return EncryptedData(
            encrypted = encrypted.toBase64(),
            iv = iv.toBase64()
        )
    }

    fun decrypt(encryptedData: EncryptedData): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(128, encryptedData.iv.fromBase64())
        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

        val decrypted = cipher.doFinal(encryptedData.encrypted.fromBase64())
        return String(decrypted, Charsets.UTF_8)
    }
}

data class EncryptedData(val encrypted: String, val iv: String)

// Extension functions
fun ByteArray.toBase64(): String = Base64.encodeToString(this, Base64.NO_WRAP)
fun String.fromBase64(): ByteArray = Base64.decode(this, Base64.NO_WRAP)

// Usage
class ApiKeyManager(context: Context) {
    private val prefs = context.getSharedPreferences("secure", Context.MODE_PRIVATE)

    fun saveApiKey(apiKey: String) {
        val encrypted = SecureStorage.encrypt(apiKey)
        prefs.edit()
            .putString("api_key_encrypted", encrypted.encrypted)
            .putString("api_key_iv", encrypted.iv)
            .apply()
    }

    fun getApiKey(): String? {
        val encrypted = prefs.getString("api_key_encrypted", null) ?: return null
        val iv = prefs.getString("api_key_iv", null) ?: return null

        return try {
            SecureStorage.decrypt(EncryptedData(encrypted, iv))
        } catch (e: Exception) {
            null
        }
    }
}
```

### ProGuard/R8 Configuration

```proguard
# build.gradle.kts
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

**proguard-rules.pro**:
```proguard
# Keep data models for serialization
-keep class com.example.models.** { *; }

# Keep Retrofit interfaces
-keep interface com.example.api.** { *; }

# Remove logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep source file names and line numbers for crash reports
-keepattributes SourceFile,LineNumberTable

# Rename source file attribute to hide original names
-renamesourcefileattribute SourceFile
```

### Certificate Pinning

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

// Certificate pinning
val certificatePinner = CertificatePinner.Builder()
    .add(
        "api.example.com",
        "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA="
    )
    .build()

val client = OkHttpClient.Builder()
    .certificatePinner(certificatePinner)
    .build()

// Get SHA256 hash:
// openssl s_client -connect api.example.com:443 | openssl x509 -pubkey -noout | openssl pkey -pubin -outform der | openssl dgst -sha256 -binary | openssl enc -base64
```

---

## OWASP Top 10

### 1. Broken Access Control

❌ **Bad**:
```kotlin
@Get("/users/{id}")
fun getUser(id: String): User {
    // ⚠️ Any user can view any user!
    return userRepository.findById(id)
}
```

✅ **Good**:
```kotlin
@Get("/users/{id}")
fun getUser(id: String, principal: UserPrincipal): User {
    val requestedUser = userRepository.findById(id)
        ?: throw NotFoundException()

    // Check access
    if (principal.id != id && principal.role != Role.ADMIN) {
        throw ForbiddenException()
    }

    return requestedUser
}
```

### 2. Cryptographic Failures

✅ **Use HTTPS everywhere**:
```kotlin
// AndroidManifest.xml
<application
    android:usesCleartextTraffic="false"> <!-- Disable HTTP -->

// network_security_config.xml
<network-security-config>
    <base-config cleartextTrafficPermitted="false">
        <trust-anchors>
            <certificates src="system" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

### 3. Injection

✅ **Always use parameterized queries** (shown earlier)

### 4. Insecure Design

✅ **Security by design**:
```kotlin
// Bad: Delete without confirmation
@Delete("/account")
fun deleteAccount(userId: String) {
    userRepository.delete(userId)
}

// Good: Require confirmation token
@Post("/account/delete-request")
fun requestAccountDeletion(userId: String): DeleteToken {
    val token = generateSecureToken()
    // Send email with confirmation link
    return DeleteToken(token, expiresAt = now() + 1.hour)
}

@Delete("/account/confirm/{token}")
fun confirmAccountDeletion(token: String) {
    val deleteRequest = verifyToken(token) ?: throw BadRequestException()
    userRepository.delete(deleteRequest.userId)
}
```

### 5. Security Misconfiguration

✅ **Secure defaults**:
```kotlin
// application.conf
ktor {
    deployment {
        port = 8080
        watch = []  # Disable auto-reload in production
    }
    application {
        modules = [ com.example.ApplicationKt.module ]
    }
}

security {
    ssl {
        enabled = true
        keyStore = ${?SSL_KEY_STORE}
        keyStorePassword = ${?SSL_KEY_STORE_PASSWORD}
    }
}
```

---

## Exercise 1: Secure User Registration

Build a secure user registration system.

### Requirements

1. **Password Requirements**:
   - Minimum 12 characters
   - Uppercase, lowercase, number, special char
   - Not in common password list

2. **Email Validation**:
   - Valid format
   - Domain verification (MX record check)
   - Unique in database

3. **Security Features**:
   - Hash passwords with bcrypt (cost 12)
   - Email verification required
   - Rate limiting (5 attempts per hour per IP)
   - CAPTCHA on repeated failures

---

## Solution 1

```kotlin
// Password validator
object PasswordValidator {
    private val commonPasswords = setOf(
        "password123", "qwerty123", "admin123",
        // ... load from file
    )

    fun validate(password: String): ValidationResult {
        val errors = mutableListOf<String>()

        if (password.length < 12) {
            errors.add("Password must be at least 12 characters")
        }

        if (!password.any { it.isUpperCase() }) {
            errors.add("Must contain uppercase letter")
        }

        if (!password.any { it.isLowerCase() }) {
            errors.add("Must contain lowercase letter")
        }

        if (!password.any { it.isDigit() }) {
            errors.add("Must contain number")
        }

        if (!password.any { "!@#$%^&*()".contains(it) }) {
            errors.add("Must contain special character")
        }

        if (password.lowercase() in commonPasswords) {
            errors.add("Password is too common")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }
}

// Email validator with DNS check
object EmailValidator {
    fun validate(email: String): ValidationResult {
        val errors = mutableListOf<String>()

        if (!basicValidation(email)) {
            errors.add("Invalid email format")
            return ValidationResult(false, errors)
        }

        val domain = email.substringAfter("@")
        if (!hasMXRecord(domain)) {
            errors.add("Email domain does not exist")
        }

        return ValidationResult(errors.isEmpty(), errors)
    }

    private fun basicValidation(email: String): Boolean {
        val pattern = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        return email.matches(pattern)
    }

    private fun hasMXRecord(domain: String): Boolean {
        return try {
            val attributes = InitialDirContext().getAttributes(
                "dns:/$domain",
                arrayOf("MX")
            )
            attributes.get("MX") != null
        } catch (e: Exception) {
            false
        }
    }
}

// Rate limiter
class RateLimiter(private val maxAttempts: Int, private val windowMs: Long) {
    private val attempts = ConcurrentHashMap<String, MutableList<Long>>()

    fun isAllowed(key: String): Boolean {
        val now = System.currentTimeMillis()
        val userAttempts = attempts.getOrPut(key) { mutableListOf() }

        // Remove old attempts
        userAttempts.removeIf { it < now - windowMs }

        if (userAttempts.size >= maxAttempts) {
            return false
        }

        userAttempts.add(now)
        return true
    }
}

// Registration service
class RegistrationService(
    private val userRepository: UserRepository,
    private val emailService: EmailService,
    private val rateLimiter: RateLimiter
) {
    suspend fun register(
        email: String,
        password: String,
        ipAddress: String
    ): Result<User> {
        // Rate limiting
        if (!rateLimiter.isAllowed(ipAddress)) {
            return Result.failure(RateLimitException("Too many registration attempts"))
        }

        // Validate email
        val emailValidation = EmailValidator.validate(email)
        if (!emailValidation.isValid) {
            return Result.failure(ValidationException(emailValidation.errors))
        }

        // Check uniqueness
        if (userRepository.existsByEmail(email)) {
            return Result.failure(ValidationException("Email already registered"))
        }

        // Validate password
        val passwordValidation = PasswordValidator.validate(password)
        if (!passwordValidation.isValid) {
            return Result.failure(ValidationException(passwordValidation.errors))
        }

        // Hash password
        val passwordHash = BCrypt.hashpw(password, BCrypt.gensalt(12))

        // Create user (unverified)
        val user = User(
            id = UUID.randomUUID().toString(),
            email = email,
            passwordHash = passwordHash,
            emailVerified = false,
            createdAt = System.currentTimeMillis()
        )

        userRepository.save(user)

        // Send verification email
        val verificationToken = generateVerificationToken(user.id)
        emailService.sendVerificationEmail(email, verificationToken)

        return Result.success(user)
    }

    private fun generateVerificationToken(userId: String): String {
        val token = UUID.randomUUID().toString()
        // Save token with expiration (24 hours)
        return token
    }
}

data class ValidationResult(val isValid: Boolean, val errors: List<String>)
```

---

## Exercise 2: Implement API Rate Limiting

Create a rate limiting middleware for Ktor.

---

## Solution 2

```kotlin
class RateLimitPlugin(private val config: Configuration) {
    class Configuration {
        var maxRequests: Int = 100
        var windowMs: Long = 60000 // 1 minute
        var keyExtractor: (ApplicationCall) -> String = { call ->
            call.request.origin.remoteHost
        }
    }

    companion object Feature : ApplicationPlugin<Application, Configuration, RateLimitPlugin> {
        override val key = AttributeKey<RateLimitPlugin>("RateLimit")

        private val rateLimitData = ConcurrentHashMap<String, RateLimitInfo>()

        override fun install(
            pipeline: Application,
            configure: Configuration.() -> Unit
        ): RateLimitPlugin {
            val config = Configuration().apply(configure)
            val plugin = RateLimitPlugin(config)

            pipeline.intercept(ApplicationCallPipeline.Plugins) {
                val key = config.keyExtractor(call)
                val now = System.currentTimeMillis()

                val info = rateLimitData.getOrPut(key) {
                    RateLimitInfo(mutableListOf(), now)
                }

                synchronized(info) {
                    // Clean old requests
                    info.requests.removeIf { it < now - config.windowMs }

                    if (info.requests.size >= config.maxRequests) {
                        call.response.headers.append("X-RateLimit-Limit", config.maxRequests.toString())
                        call.response.headers.append("X-RateLimit-Remaining", "0")
                        call.response.headers.append("Retry-After", "60")

                        call.respond(HttpStatusCode.TooManyRequests, mapOf(
                            "error" to "Rate limit exceeded",
                            "limit" to config.maxRequests,
                            "window" to "${config.windowMs / 1000}s"
                        ))
                        finish()
                        return@intercept
                    }

                    info.requests.add(now)

                    call.response.headers.append("X-RateLimit-Limit", config.maxRequests.toString())
                    call.response.headers.append(
                        "X-RateLimit-Remaining",
                        (config.maxRequests - info.requests.size).toString()
                    )
                }
            }

            return plugin
        }
    }

    private data class RateLimitInfo(
        val requests: MutableList<Long>,
        val windowStart: Long
    )
}

// Usage
fun Application.module() {
    install(RateLimitPlugin) {
        maxRequests = 100
        windowMs = 60000 // 1 minute

        keyExtractor = { call ->
            // Use authenticated user ID if available, else IP
            call.principal<UserPrincipal>()?.id
                ?: call.request.origin.remoteHost
        }
    }

    routing {
        get("/api/data") {
            call.respond("Hello!")
        }
    }
}
```

---

## Exercise 3: Secure File Upload

Create a secure file upload endpoint.

---

## Solution 3

```kotlin
class FileUploadService(
    private val uploadDir: File,
    private val maxFileSize: Long = 10 * 1024 * 1024, // 10 MB
    private val allowedExtensions: Set<String> = setOf("jpg", "png", "pdf")
) {
    init {
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }
    }

    suspend fun upload(
        file: MultiPartData,
        userId: String
    ): Result<UploadedFile> {
        var uploadedFile: UploadedFile? = null
        var tempFile: File? = null

        try {
            file.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val fileName = part.originalFileName ?: return@forEachPart

                        // Validate filename
                        if (!isValidFilename(fileName)) {
                            return Result.failure(ValidationException("Invalid filename"))
                        }

                        // Validate extension
                        val extension = fileName.substringAfterLast(".", "")
                        if (extension.lowercase() !in allowedExtensions) {
                            return Result.failure(
                                ValidationException("File type not allowed. Allowed: $allowedExtensions")
                            )
                        }

                        // Generate safe filename
                        val safeFilename = "${UUID.randomUUID()}.${extension.lowercase()}"
                        tempFile = File(uploadDir, safeFilename)

                        var size = 0L
                        tempFile!!.outputStream().use { output ->
                            part.streamProvider().use { input ->
                                val buffer = ByteArray(8192)
                                var bytesRead: Int

                                while (input.read(buffer).also { bytesRead = it } != -1) {
                                    size += bytesRead

                                    if (size > maxFileSize) {
                                        return Result.failure(
                                            ValidationException("File too large. Max: ${maxFileSize / 1024 / 1024}MB")
                                        )
                                    }

                                    output.write(buffer, 0, bytesRead)
                                }
                            }
                        }

                        // Validate file type (magic numbers)
                        if (!isValidFileType(tempFile!!, extension)) {
                            tempFile!!.delete()
                            return Result.failure(ValidationException("File content doesn't match extension"))
                        }

                        // Scan for malware (integrate with antivirus)
                        if (containsMalware(tempFile!!)) {
                            tempFile!!.delete()
                            return Result.failure(SecurityException("Malware detected"))
                        }

                        uploadedFile = UploadedFile(
                            id = UUID.randomUUID().toString(),
                            originalFilename = fileName,
                            storedFilename = safeFilename,
                            extension = extension,
                            size = size,
                            uploadedBy = userId,
                            uploadedAt = System.currentTimeMillis()
                        )
                    }
                    else -> {}
                }
                part.dispose()
            }

            return uploadedFile?.let { Result.success(it) }
                ?: Result.failure(Exception("No file uploaded"))

        } catch (e: Exception) {
            tempFile?.delete()
            return Result.failure(e)
        }
    }

    private fun isValidFilename(filename: String): Boolean {
        // No path traversal
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            return false
        }

        // No special characters
        if (!filename.matches(Regex("^[a-zA-Z0-9._-]+$"))) {
            return false
        }

        return true
    }

    private fun isValidFileType(file: File, expectedExtension: String): Boolean {
        val bytes = file.inputStream().use { it.readNBytes(12) }

        return when (expectedExtension.lowercase()) {
            "jpg", "jpeg" -> bytes.take(3).toByteArray().contentEquals(
                byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())
            )
            "png" -> bytes.take(8).toByteArray().contentEquals(
                byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
            )
            "pdf" -> bytes.take(4).toByteArray().contentEquals(
                byteArrayOf(0x25, 0x50, 0x44, 0x46) // %PDF
            )
            else -> false
        }
    }

    private fun containsMalware(file: File): Boolean {
        // Integrate with ClamAV or similar
        // For now, return false
        return false
    }
}

data class UploadedFile(
    val id: String,
    val originalFilename: String,
    val storedFilename: String,
    val extension: String,
    val size: Long,
    val uploadedBy: String,
    val uploadedAt: Long
)

// Ktor route
fun Route.fileUpload(fileUploadService: FileUploadService) {
    post("/upload") {
        val principal = call.principal<UserPrincipal>()
            ?: return@post call.respond(HttpStatusCode.Unauthorized)

        val multipart = call.receiveMultipart()

        val result = fileUploadService.upload(multipart, principal.id)

        result.fold(
            onSuccess = { uploadedFile ->
                call.respond(HttpStatusCode.Created, uploadedFile)
            },
            onFailure = { error ->
                when (error) {
                    is ValidationException -> call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to error.message)
                    )
                    is SecurityException -> call.respond(
                        HttpStatusCode.Forbidden,
                        mapOf("error" to error.message)
                    )
                    else -> call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to "Upload failed")
                    )
                }
            }
        )
    }
}
```

---

## Why This Matters

### The Stakes

**Data Breaches Cost**:
- Average cost: $4.45 million
- Customer churn: 60% after breach
- Legal penalties: GDPR fines up to 4% of revenue

**Career Impact**:
- Security-aware developers earn 25% more
- Companies require security knowledge
- One breach can end a career

**User Trust**:
- 87% won't use an app after a breach
- Trust takes years to build, seconds to destroy

---

## Checkpoint Quiz

### Question 1
Why should you NEVER store passwords in plaintext?

A) It takes up too much space
B) If database is compromised, all passwords are exposed
C) It's slower than hashing
D) It's not compatible with databases

### Question 2
What is the N+1 query problem related to in security?

A) It's a type of SQL injection
B) It creates performance issues that can be exploited for DoS
C) It allows unauthorized access
D) It's not a security issue

### Question 3
What's the purpose of certificate pinning?

A) Faster HTTPS connections
B) Prevents man-in-the-middle attacks
C) Reduces app size
D) Improves SEO

### Question 4
What should you do when security validation fails?

A) Grant access anyway
B) Fail securely (deny access)
C) Log the user out
D) Restart the app

### Question 5
Why use bcrypt instead of SHA-256 for passwords?

A) bcrypt is faster
B) bcrypt includes salt and is designed to be slow
C) SHA-256 is deprecated
D) bcrypt produces smaller hashes

---

## Quiz Answers

**Question 1: B) If database is compromised, all passwords are exposed**

Storing plaintext passwords = catastrophic breach:
- Attackers get all passwords
- Users reuse passwords across sites
- One breach = compromise everywhere

Always hash passwords with bcrypt!

---

**Question 2: B) Creates performance issues that can be exploited for DoS**

N+1 queries = performance vulnerability:
- Attacker requests large dataset
- Triggers thousands of queries
- Server becomes unresponsive (DoS)

Solution: Use JOINs and optimize queries

---

**Question 3: B) Prevents man-in-the-middle attacks**

Certificate pinning ensures:
- App only trusts specific certificates
- Can't be fooled by fake certificates
- Prevents attackers intercepting traffic

---

**Question 4: B) Fail securely (deny access)**

When in doubt, deny:
- Error in authentication? Deny
- Exception in authorization? Deny
- Can't verify request? Deny

Never fail open!

---

**Question 5: B) bcrypt includes salt and is designed to be slow**

bcrypt advantages:
- Automatically salts (unique hash per password)
- Configurable cost (slower = harder to crack)
- Designed for passwords (SHA-256 is not)

---

## What You've Learned

✅ Why security is critical (real breach examples)
✅ Secure coding principles (defense in depth, least privilege, fail securely)
✅ Input validation and sanitization (SQL injection, XSS, path traversal)
✅ Password security (bcrypt hashing, strength validation)
✅ JWT security (proper signing, expiration, refresh tokens)
✅ Android security (KeyStore, ProGuard, certificate pinning)
✅ OWASP Top 10 vulnerabilities and how to prevent them
✅ Practical security implementations (registration, rate limiting, file upload)

---

## Next Steps

In **Lesson 7.5: CI/CD and DevOps**, you'll learn:
- Continuous Integration with GitHub Actions
- Automated testing in CI/CD pipelines
- Build automation with Gradle
- Code quality tools (ktlint, detekt)
- Docker for backend applications
- Publishing Android apps to Play Store

Secure code is worthless if you can't deploy it reliably!

---

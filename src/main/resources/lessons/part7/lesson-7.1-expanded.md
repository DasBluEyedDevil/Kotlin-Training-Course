# Lesson 7.1: Kotlin Multiplatform (KMP) Basics

**Estimated Time**: 75 minutes

---

## Introduction

Welcome to Part 7 of the Kotlin Training Course! In this final section, we'll explore advanced topics and deployment strategies that will take your Kotlin skills to a professional level.

**Kotlin Multiplatform (KMP)** is one of Kotlin's most powerful features - it allows you to share code across multiple platforms (Android, iOS, web, desktop, backend) while still accessing platform-specific APIs when needed.

Imagine writing your business logic once and using it everywhere:
- ✅ Android app (Jetpack Compose)
- ✅ iOS app (SwiftUI)
- ✅ Web application (Kotlin/JS)
- ✅ Desktop application (Compose Desktop)
- ✅ Backend server (Ktor)

This isn't just theory - companies like Netflix, VMware, Philips, and Cash App use KMP in production.

---

## What is Kotlin Multiplatform?

### The Problem It Solves

Traditional mobile development faces challenges:

**Without KMP**:
```
Android App (Kotlin) ─── Business Logic A
iOS App (Swift)      ─── Business Logic B (duplicate!)
Web App (JavaScript) ─── Business Logic C (duplicate!)
Backend (Kotlin)     ─── Business Logic D (duplicate!)
```

**Issues**:
- Same logic written 4 times in 4 languages
- Bug fixes need to be applied 4 times
- Features roll out inconsistently
- Testing multiplied by 4

**With KMP**:
```
Shared Code (Kotlin)
    ├── Business Logic (once!)
    ├── Data Models
    ├── API Client
    ├── Database Logic
    └── Validation Rules

Platform-Specific Code
    ├── Android UI (Jetpack Compose)
    ├── iOS UI (SwiftUI)
    ├── Web UI (React/Kotlin JS)
    └── Desktop UI (Compose Desktop)
```

**Benefits**:
- Write business logic once, use everywhere
- Fix bugs in one place
- Consistent behavior across platforms
- 40-70% code sharing in real projects

---

## KMP Architecture

### Shared vs Platform-Specific Code

```
project/
├── commonMain/          # Shared code for all platforms
│   ├── kotlin/
│   │   ├── models/      # Data classes
│   │   ├── api/         # Network client
│   │   ├── database/    # Database logic
│   │   └── business/    # Business logic
│   └── resources/
├── androidMain/         # Android-specific code
│   └── kotlin/
├── iosMain/             # iOS-specific code
│   └── kotlin/
├── jvmMain/             # JVM/Desktop-specific code
│   └── kotlin/
└── jsMain/              # JavaScript-specific code
    └── kotlin/
```

### What to Share

**Perfect for Sharing**:
- ✅ Data models
- ✅ Business logic
- ✅ API clients
- ✅ Validation rules
- ✅ Utilities (date formatting, calculations)
- ✅ Database operations

**Platform-Specific**:
- ❌ UI code (different frameworks)
- ❌ Platform APIs (camera, notifications)
- ❌ Platform-specific libraries

---

## Expect/Actual Declarations

The `expect/actual` mechanism allows you to declare a common interface in shared code and provide platform-specific implementations.

### How It Works

**Common Code (commonMain)**:
```kotlin
// Declare what you expect each platform to provide
expect class Platform() {
    val name: String
}

expect fun getCurrentTimeMillis(): Long
```

**Android Implementation (androidMain)**:
```kotlin
actual class Platform {
    actual val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
```

**iOS Implementation (iosMain)**:
```kotlin
import platform.Foundation.NSDate

actual class Platform {
    actual val name: String = "iOS ${platform.Foundation.NSProcessInfo.processInfo.operatingSystemVersion}"
}

actual fun getCurrentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
```

**Usage in Shared Code**:
```kotlin
// This works on all platforms!
fun greet(): String {
    val platform = Platform()
    return "Hello from ${platform.name} at ${getCurrentTimeMillis()}"
}
```

---

## Setting Up a KMP Project

### Project Structure

**build.gradle.kts (Project Level)**:
```kotlin
plugins {
    kotlin("multiplatform") version "1.9.22" apply false
    kotlin("plugin.serialization") version "1.9.22" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

**build.gradle.kts (Module Level)**:
```kotlin
plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    // Target platforms
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvm() // For desktop/backend
    js(IR) { browser() } // For web

    // Source sets
    sourceSets {
        // Common code
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
                implementation("io.ktor:ktor-client-core:2.3.7")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        // Android
        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-android:2.3.7")
            }
        }

        // iOS
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.7")
            }
        }

        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }

        // JVM
        val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:2.3.7")
            }
        }

        // JS
        val jsMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-js:2.3.7")
            }
        }
    }
}
```

---

## Sharing Business Logic

### Example: Shopping Cart

**Shared Models (commonMain)**:
```kotlin
package com.example.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String
)

@Serializable
data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val total: Double
        get() = product.price * quantity
}
```

**Shared Business Logic (commonMain)**:
```kotlin
package com.example.shared

class ShoppingCart {
    private val items = mutableMapOf<String, CartItem>()

    fun addItem(product: Product, quantity: Int = 1) {
        val existingItem = items[product.id]
        if (existingItem != null) {
            items[product.id] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            items[product.id] = CartItem(product, quantity)
        }
    }

    fun removeItem(productId: String) {
        items.remove(productId)
    }

    fun updateQuantity(productId: String, quantity: Int) {
        val item = items[productId] ?: return
        if (quantity <= 0) {
            removeItem(productId)
        } else {
            items[productId] = item.copy(quantity = quantity)
        }
    }

    fun getItems(): List<CartItem> = items.values.toList()

    fun getTotal(): Double = items.values.sumOf { it.total }

    fun getItemCount(): Int = items.values.sumOf { it.quantity }

    fun clear() {
        items.clear()
    }
}
```

**Usage in Android**:
```kotlin
// Android ViewModel
class ShoppingViewModel : ViewModel() {
    private val cart = ShoppingCart() // Shared code!

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items: StateFlow<List<CartItem>> = _items.asStateFlow()

    fun addToCart(product: Product) {
        cart.addItem(product)
        _items.value = cart.getItems()
    }

    fun getTotal(): Double = cart.getTotal()
}
```

**Usage in iOS**:
```swift
// iOS ViewModel (Swift)
class ShoppingViewModel: ObservableObject {
    private let cart = ShoppingCart() // Same shared code!

    @Published var items: [CartItem] = []

    func addToCart(product: Product) {
        cart.addItem(product: product, quantity: 1)
        items = cart.getItems()
    }

    var total: Double {
        cart.getTotal()
    }
}
```

---

## Platform-Specific Implementations

### Example: Storage Layer

**Common Interface (commonMain)**:
```kotlin
package com.example.shared.storage

expect class KeyValueStorage {
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun remove(key: String)
    fun clear()
}
```

**Android Implementation (androidMain)**:
```kotlin
package com.example.shared.storage

import android.content.Context
import android.content.SharedPreferences

actual class KeyValueStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    actual fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    actual fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    actual fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    actual fun clear() {
        prefs.edit().clear().apply()
    }
}
```

**iOS Implementation (iosMain)**:
```kotlin
package com.example.shared.storage

import platform.Foundation.NSUserDefaults

actual class KeyValueStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }

    actual fun getString(key: String): String? {
        return userDefaults.stringForKey(key)
    }

    actual fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    actual fun clear() {
        val domain = userDefaults.dictionaryRepresentation().keys
        domain.forEach { key ->
            userDefaults.removeObjectForKey(key as String)
        }
    }
}
```

**Usage in Shared Code**:
```kotlin
class UserPreferences(private val storage: KeyValueStorage) {
    fun saveUsername(username: String) {
        storage.putString("username", username)
    }

    fun getUsername(): String? {
        return storage.getString("username")
    }

    fun logout() {
        storage.clear()
    }
}
```

---

## Shared API Client

### Common Network Layer

**API Client (commonMain)**:
```kotlin
package com.example.shared.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ApiClient(private val baseUrl: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
            })
        }
    }

    suspend fun getProducts(): List<Product> {
        return client.get("$baseUrl/products").body()
    }

    suspend fun getProduct(id: String): Product {
        return client.get("$baseUrl/products/$id").body()
    }

    suspend fun createOrder(order: CreateOrderRequest): Order {
        return client.post("$baseUrl/orders") {
            setBody(order)
        }.body()
    }
}
```

**Models (commonMain)**:
```kotlin
package com.example.shared.api

import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItem>,
    val totalAmount: Double
)

@Serializable
data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double
)

@Serializable
data class Order(
    val id: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: String,
    val createdAt: String
)
```

This API client works on **all platforms** without modification!

---

## Exercise 1: Build a Shared Authentication Module

Create a KMP module that handles user authentication across platforms.

### Requirements

1. **Shared Models** (commonMain):
   - `User` (id, email, name, token)
   - `LoginRequest` (email, password)
   - `RegisterRequest` (email, password, name)
   - `AuthResponse` (success, user, token, message)

2. **Shared AuthService** (commonMain):
   - `login(email, password): Result<User>`
   - `register(request): Result<User>`
   - `logout()`
   - `isLoggedIn(): Boolean`
   - `getCurrentUser(): User?`

3. **Platform-Specific Storage**:
   - Android: Use SharedPreferences
   - iOS: Use UserDefaults
   - Store and retrieve auth token

4. **Validation**:
   - Email validation
   - Password strength check (min 8 chars, 1 uppercase, 1 number)

---

## Solution 1

**Common Models (commonMain)**:
```kotlin
// shared/src/commonMain/kotlin/com/example/shared/models/Auth.kt
package com.example.shared.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val name: String,
    val token: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class AuthResponse(
    val success: Boolean,
    val user: User? = null,
    val token: String? = null,
    val message: String? = null
)
```

**Validation Utilities (commonMain)**:
```kotlin
// shared/src/commonMain/kotlin/com/example/shared/utils/Validation.kt
package com.example.shared.utils

object Validation {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && emailRegex.matches(email)
    }

    fun isValidPassword(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isDigit() }) return false
        return true
    }

    fun validateLoginRequest(request: LoginRequest): String? {
        if (!isValidEmail(request.email)) {
            return "Invalid email format"
        }
        if (request.password.isBlank()) {
            return "Password is required"
        }
        return null
    }

    fun validateRegisterRequest(request: RegisterRequest): String? {
        if (!isValidEmail(request.email)) {
            return "Invalid email format"
        }
        if (!isValidPassword(request.password)) {
            return "Password must be at least 8 characters with 1 uppercase and 1 number"
        }
        if (request.name.isBlank()) {
            return "Name is required"
        }
        return null
    }
}
```

**Storage Interface (commonMain)**:
```kotlin
// shared/src/commonMain/kotlin/com/example/shared/storage/TokenStorage.kt
package com.example.shared.storage

expect class TokenStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()
}
```

**Android Storage (androidMain)**:
```kotlin
// shared/src/androidMain/kotlin/com/example/shared/storage/TokenStorage.kt
package com.example.shared.storage

import android.content.Context

actual class TokenStorage(private val context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    actual fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    actual fun getToken(): String? {
        return prefs.getString("token", null)
    }

    actual fun clearToken() {
        prefs.edit().remove("token").apply()
    }
}
```

**iOS Storage (iosMain)**:
```kotlin
// shared/src/iosMain/kotlin/com/example/shared/storage/TokenStorage.kt
package com.example.shared.storage

import platform.Foundation.NSUserDefaults

actual class TokenStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    actual fun saveToken(token: String) {
        userDefaults.setObject(token, forKey = "token")
    }

    actual fun getToken(): String? {
        return userDefaults.stringForKey("token")
    }

    actual fun clearToken() {
        userDefaults.removeObjectForKey("token")
    }
}
```

**Auth Service (commonMain)**:
```kotlin
// shared/src/commonMain/kotlin/com/example/shared/service/AuthService.kt
package com.example.shared.service

import com.example.shared.models.*
import com.example.shared.storage.TokenStorage
import com.example.shared.utils.Validation
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class AuthService(
    private val baseUrl: String,
    private val tokenStorage: TokenStorage
) {
    private var currentUser: User? = null

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        val request = LoginRequest(email, password)

        // Validate
        val validationError = Validation.validateLoginRequest(request)
        if (validationError != null) {
            return Result.failure(IllegalArgumentException(validationError))
        }

        return try {
            val response: AuthResponse = client.post("$baseUrl/auth/login") {
                setBody(request)
            }.body()

            if (response.success && response.user != null && response.token != null) {
                currentUser = response.user
                tokenStorage.saveToken(response.token)
                Result.success(response.user)
            } else {
                Result.failure(Exception(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(request: RegisterRequest): Result<User> {
        // Validate
        val validationError = Validation.validateRegisterRequest(request)
        if (validationError != null) {
            return Result.failure(IllegalArgumentException(validationError))
        }

        return try {
            val response: AuthResponse = client.post("$baseUrl/auth/register") {
                setBody(request)
            }.body()

            if (response.success && response.user != null && response.token != null) {
                currentUser = response.user
                tokenStorage.saveToken(response.token)
                Result.success(response.user)
            } else {
                Result.failure(Exception(response.message ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        currentUser = null
        tokenStorage.clearToken()
    }

    fun isLoggedIn(): Boolean {
        return tokenStorage.getToken() != null
    }

    fun getCurrentUser(): User? {
        return currentUser
    }
}
```

---

## Exercise 2: Create a Platform Logger

Build a logging utility that uses platform-specific logging mechanisms.

### Requirements

1. Create `Logger` expect class with methods:
   - `debug(tag: String, message: String)`
   - `info(tag: String, message: String)`
   - `warning(tag: String, message: String)`
   - `error(tag: String, message: String, throwable: Throwable?)`

2. Android: Use `android.util.Log`
3. iOS: Use `NSLog`
4. JVM: Use `println` with timestamps

---

## Solution 2

**Common Declaration (commonMain)**:
```kotlin
// shared/src/commonMain/kotlin/com/example/shared/utils/Logger.kt
package com.example.shared.utils

expect object Logger {
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun warning(tag: String, message: String)
    fun error(tag: String, message: String, throwable: Throwable? = null)
}
```

**Android Implementation (androidMain)**:
```kotlin
// shared/src/androidMain/kotlin/com/example/shared/utils/Logger.kt
package com.example.shared.utils

import android.util.Log

actual object Logger {
    actual fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

    actual fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    actual fun warning(tag: String, message: String) {
        Log.w(tag, message)
    }

    actual fun error(tag: String, message: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }
}
```

**iOS Implementation (iosMain)**:
```kotlin
// shared/src/iosMain/kotlin/com/example/shared/utils/Logger.kt
package com.example.shared.utils

import platform.Foundation.NSLog

actual object Logger {
    actual fun debug(tag: String, message: String) {
        NSLog("DEBUG [$tag] $message")
    }

    actual fun info(tag: String, message: String) {
        NSLog("INFO [$tag] $message")
    }

    actual fun warning(tag: String, message: String) {
        NSLog("WARNING [$tag] $message")
    }

    actual fun error(tag: String, message: String, throwable: Throwable?) {
        val errorMsg = if (throwable != null) {
            "$message: ${throwable.message}"
        } else {
            message
        }
        NSLog("ERROR [$tag] $errorMsg")
    }
}
```

**JVM Implementation (jvmMain)**:
```kotlin
// shared/src/jvmMain/kotlin/com/example/shared/utils/Logger.kt
package com.example.shared.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

actual object Logger {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private fun timestamp() = LocalDateTime.now().format(formatter)

    actual fun debug(tag: String, message: String) {
        println("${timestamp()} DEBUG [$tag] $message")
    }

    actual fun info(tag: String, message: String) {
        println("${timestamp()} INFO [$tag] $message")
    }

    actual fun warning(tag: String, message: String) {
        println("${timestamp()} WARNING [$tag] $message")
    }

    actual fun error(tag: String, message: String, throwable: Throwable?) {
        val errorMsg = if (throwable != null) {
            "$message: ${throwable.message}\n${throwable.stackTraceToString()}"
        } else {
            message
        }
        println("${timestamp()} ERROR [$tag] $errorMsg")
    }
}
```

**Usage (commonMain)**:
```kotlin
class UserRepository {
    suspend fun getUser(id: String): User? {
        Logger.debug("UserRepository", "Fetching user: $id")

        return try {
            val user = api.getUser(id)
            Logger.info("UserRepository", "User fetched successfully")
            user
        } catch (e: Exception) {
            Logger.error("UserRepository", "Failed to fetch user", e)
            null
        }
    }
}
```

---

## Exercise 3: Build a Shared Repository Pattern

Create a repository that manages data fetching and caching across platforms.

### Requirements

1. **ProductRepository** (commonMain):
   - Fetch products from API
   - Cache in memory
   - Return cached data if available and fresh (< 5 minutes)
   - Refresh from API if cache expired

2. Use Ktor for networking
3. Handle errors gracefully
4. Log cache hits/misses

---

## Solution 3

```kotlin
// shared/src/commonMain/kotlin/com/example/shared/repository/ProductRepository.kt
package com.example.shared.repository

import com.example.shared.models.Product
import com.example.shared.utils.Logger
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

class ProductRepository(private val baseUrl: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    private var cachedProducts: List<Product>? = null
    private var cacheTimestamp: Long = 0
    private val cacheDuration = 5 * 60 * 1000 // 5 minutes in milliseconds
    private val mutex = Mutex()

    suspend fun getProducts(forceRefresh: Boolean = false): Result<List<Product>> {
        return mutex.withLock {
            val currentTime = getCurrentTimeMillis()
            val cacheValid = cachedProducts != null &&
                             (currentTime - cacheTimestamp) < cacheDuration

            if (!forceRefresh && cacheValid) {
                Logger.debug("ProductRepository", "Returning cached products")
                return Result.success(cachedProducts!!)
            }

            Logger.info("ProductRepository", "Fetching products from API")

            try {
                val products: List<Product> = client.get("$baseUrl/products").body()
                cachedProducts = products
                cacheTimestamp = currentTime
                Logger.info("ProductRepository", "Fetched ${products.size} products")
                Result.success(products)
            } catch (e: Exception) {
                Logger.error("ProductRepository", "Failed to fetch products", e)

                // Return cached data if available, even if expired
                if (cachedProducts != null) {
                    Logger.warning("ProductRepository", "Returning stale cached data")
                    Result.success(cachedProducts!!)
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    suspend fun getProduct(id: String): Result<Product> {
        // Check cache first
        val cached = cachedProducts?.find { it.id == id }
        if (cached != null) {
            Logger.debug("ProductRepository", "Found product in cache: $id")
            return Result.success(cached)
        }

        Logger.info("ProductRepository", "Fetching product from API: $id")

        return try {
            val product: Product = client.get("$baseUrl/products/$id").body()
            Logger.info("ProductRepository", "Fetched product: ${product.name}")
            Result.success(product)
        } catch (e: Exception) {
            Logger.error("ProductRepository", "Failed to fetch product: $id", e)
            Result.failure(e)
        }
    }

    fun clearCache() {
        cachedProducts = null
        cacheTimestamp = 0
        Logger.debug("ProductRepository", "Cache cleared")
    }
}

// Platform-specific time function
expect fun getCurrentTimeMillis(): Long
```

**Platform Implementations**:

```kotlin
// shared/src/androidMain/kotlin/com/example/shared/repository/Time.kt
package com.example.shared.repository

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
```

```kotlin
// shared/src/iosMain/kotlin/com/example/shared/repository/Time.kt
package com.example.shared.repository

import platform.Foundation.NSDate

actual fun getCurrentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
```

```kotlin
// shared/src/jvmMain/kotlin/com/example/shared/repository/Time.kt
package com.example.shared.repository

actual fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
```

---

## Why This Matters

### Real-World Impact

**Companies Using KMP**:
- **Netflix**: Shares business logic between Android and iOS apps
- **VMware**: Uses KMP for cross-platform SDK
- **Philips**: Medical devices with shared Bluetooth logic
- **Cash App**: Payment processing logic shared across platforms
- **Autodesk**: CAD software with shared rendering engine

**Business Benefits**:
- **40-70% code reuse** in production apps
- **Faster time to market**: Build features once
- **Fewer bugs**: Single source of truth
- **Consistent UX**: Same logic = same behavior
- **Easier maintenance**: Fix once, deploy everywhere

**Developer Benefits**:
- Write Kotlin (not Swift/Java/JavaScript)
- Share tests across platforms
- Type-safe, null-safe code everywhere
- Use Kotlin coroutines on all platforms

---

## Checkpoint Quiz

### Question 1
What is the primary benefit of Kotlin Multiplatform?

A) Faster app performance
B) Sharing code across multiple platforms
C) Smaller app size
D) Better UI design

### Question 2
What does the `expect` keyword do in KMP?

A) Waits for a coroutine to complete
B) Declares a function that must be implemented on each platform
C) Handles exceptions
D) Creates a nullable type

### Question 3
Which type of code is best suited for sharing in KMP?

A) UI components
B) Business logic and data models
C) Platform-specific APIs
D) Native libraries

### Question 4
In a KMP project, where do you write code that works on all platforms?

A) androidMain
B) iosMain
C) commonMain
D) jvmMain

### Question 5
What happens if you don't provide an `actual` implementation for an `expect` declaration?

A) It uses a default implementation
B) Compilation fails
C) It returns null
D) It throws a runtime exception

---

## Quiz Answers

**Question 1: B) Sharing code across multiple platforms**

KMP's main goal is code reuse:
- Write business logic once in Kotlin
- Use on Android, iOS, web, desktop, backend
- Reduce duplication and maintenance burden
- 40-70% code sharing in real projects

---

**Question 2: B) Declares a function that must be implemented on each platform**

The `expect/actual` mechanism:
```kotlin
// commonMain
expect fun platformName(): String

// androidMain
actual fun platformName(): String = "Android"

// iosMain
actual fun platformName(): String = "iOS"
```

Each platform provides its `actual` implementation.

---

**Question 3: B) Business logic and data models**

**Perfect for sharing**:
- Data models (User, Product, Order)
- Business logic (validation, calculations)
- API clients
- Repositories
- Utilities

**Not suitable for sharing**:
- UI code (different frameworks)
- Platform-specific APIs (camera, GPS)

---

**Question 4: C) commonMain**

KMP source sets:
- **commonMain**: Code for all platforms
- **androidMain**: Android-specific code
- **iosMain**: iOS-specific code
- **jvmMain**: JVM/Desktop code
- **jsMain**: JavaScript/Web code

---

**Question 5: B) Compilation fails**

The compiler enforces complete implementation:
```kotlin
// commonMain
expect class Logger {
    fun log(message: String)
}

// If you forget androidMain implementation:
// ERROR: Expected class Logger has no actual declaration
```

This prevents runtime errors and ensures consistency.

---

## What You've Learned

✅ What Kotlin Multiplatform is and why it matters
✅ The architecture of KMP projects (commonMain, platform-specific)
✅ How to use `expect/actual` declarations for platform-specific code
✅ How to set up a KMP project with Gradle
✅ How to share business logic across Android, iOS, and other platforms
✅ How to create platform-specific implementations (storage, logging)
✅ How to build shared API clients and repositories
✅ Real-world use cases and benefits of KMP

---

## Next Steps

In **Lesson 7.2: Testing Strategies**, you'll learn:
- Unit testing with JUnit 5 and Kotest
- Mocking with MockK
- Testing coroutines and flows
- Testing Jetpack Compose UI
- Test-driven development (TDD)
- Measuring and improving code coverage

Testing shared KMP code ensures it works correctly on all platforms!

---

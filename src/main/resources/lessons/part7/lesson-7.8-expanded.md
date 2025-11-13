# Lesson 7.8: Final Capstone - Full Stack E-Commerce Platform

**Estimated Time**: 12-16 hours

---

## Congratulations! 🎉

You've completed all 7 parts of the Kotlin Training Course! You've learned:
- Part 1: Kotlin fundamentals
- Part 2: Object-oriented programming
- Part 3: Functional programming and coroutines
- Part 4: Collections and advanced features
- Part 5: Backend development with Ktor
- Part 6: Android development with Jetpack Compose
- Part 7: Advanced topics (KMP, testing, security, deployment)

Now it's time to prove your mastery by building a **complete, production-ready, full-stack e-commerce platform**!

---

## Project Overview: ShopKotlin

**ShopKotlin** is a modern e-commerce platform with:

### Backend (Ktor)
- RESTful API
- PostgreSQL database
- JWT authentication
- Product catalog management
- Shopping cart
- Order processing
- Payment integration (Stripe)
- Admin panel
- Comprehensive testing
- CI/CD pipeline
- Cloud deployment
- Monitoring and analytics

### Android App (Jetpack Compose)
- Beautiful Material Design 3 UI
- Product browsing and search
- Shopping cart
- User authentication
- Order tracking
- Offline support
- Push notifications
- Analytics

### Full Feature Set
- ✅ User registration and login
- ✅ Product catalog with categories
- ✅ Product search and filtering
- ✅ Shopping cart management
- ✅ Checkout with payment
- ✅ Order history
- ✅ Admin dashboard
- ✅ Inventory management
- ✅ Real-time order tracking
- ✅ Email notifications
- ✅ Analytics dashboard

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      Client Applications                     │
├──────────────────────────┬──────────────────────────────────┤
│   Android App            │   Web Admin Dashboard            │
│   (Jetpack Compose)      │   (React/Vue - Optional)         │
└──────────────────────────┴──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                     API Gateway / Load Balancer             │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    Ktor Backend API                          │
├─────────────────────────────────────────────────────────────┤
│  ┌────────────┐  ┌────────────┐  ┌────────────┐            │
│  │   Auth     │  │  Products  │  │   Orders   │            │
│  │  Service   │  │  Service   │  │  Service   │            │
│  └────────────┘  └────────────┘  └────────────┘            │
│                                                              │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐            │
│  │  Payment   │  │   Email    │  │ Analytics  │            │
│  │  Service   │  │  Service   │  │  Service   │            │
│  └────────────┘  └────────────┘  └────────────┘            │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      Data Layer                              │
├──────────────────┬──────────────────┬───────────────────────┤
│   PostgreSQL     │      Redis       │     File Storage      │
│   (Primary DB)   │     (Cache)      │   (S3/CloudStorage)   │
└──────────────────┴──────────────────┴───────────────────────┘
```

---

## Phase 1: Project Setup (1-2 hours)

### Backend Project Structure

```
shopkotlin-backend/
├── src/
│   ├── main/
│   │   ├── kotlin/com/shopkotlin/
│   │   │   ├── Application.kt
│   │   │   ├── models/
│   │   │   │   ├── User.kt
│   │   │   │   ├── Product.kt
│   │   │   │   ├── Order.kt
│   │   │   │   └── Category.kt
│   │   │   ├── database/
│   │   │   │   ├── DatabaseFactory.kt
│   │   │   │   └── tables/
│   │   │   ├── repositories/
│   │   │   │   ├── UserRepository.kt
│   │   │   │   ├── ProductRepository.kt
│   │   │   │   └── OrderRepository.kt
│   │   │   ├── services/
│   │   │   │   ├── AuthService.kt
│   │   │   │   ├── ProductService.kt
│   │   │   │   ├── OrderService.kt
│   │   │   │   └── PaymentService.kt
│   │   │   ├── routes/
│   │   │   │   ├── authRoutes.kt
│   │   │   │   ├── productRoutes.kt
│   │   │   │   └── orderRoutes.kt
│   │   │   ├── plugins/
│   │   │   │   ├── Security.kt
│   │   │   │   ├── Serialization.kt
│   │   │   │   └── Monitoring.kt
│   │   │   └── utils/
│   │   └── resources/
│   │       ├── application.conf
│   │       └── logback.xml
│   └── test/
│       └── kotlin/com/shopkotlin/
│           ├── AuthRoutesTest.kt
│           ├── ProductServiceTest.kt
│           └── OrderServiceTest.kt
├── build.gradle.kts
├── Dockerfile
├── docker-compose.yml
└── .github/
    └── workflows/
        └── ci-cd.yml
```

### build.gradle.kts (Backend)

```kotlin
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposed_version: String by project

plugins {
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.shopkotlin"
version = "1.0.0"

application {
    mainClass.set("com.shopkotlin.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")

    // Serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    // Database
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("org.postgresql:postgresql:42.7.1")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Security
    implementation("org.mindrot:jbcrypt:0.4")

    // Payment
    implementation("com.stripe:stripe-java:24.9.0")

    // Email
    implementation("org.simplejavamail:simple-java-mail:8.5.1")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")

    // Monitoring
    implementation("io.sentry:sentry:7.1.0")

    // Testing
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}
```

### Android Project Structure

```
shopkotlin-android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/shopkotlin/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── ShopKotlinApp.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── screens/
│   │   │   │   │   │   ├── auth/
│   │   │   │   │   │   ├── home/
│   │   │   │   │   │   ├── product/
│   │   │   │   │   │   ├── cart/
│   │   │   │   │   │   └── orders/
│   │   │   │   │   ├── components/
│   │   │   │   │   └── theme/
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/
│   │   │   │   │   ├── database/
│   │   │   │   │   ├── repository/
│   │   │   │   │   └── models/
│   │   │   │   ├── domain/
│   │   │   │   │   └── usecases/
│   │   │   │   └── di/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle.kts
└── build.gradle.kts
```

---

## Phase 2: Backend Development (4-6 hours)

### 2.1 Database Schema

```kotlin
// src/main/kotlin/com/shopkotlin/database/tables/Tables.kt
package com.shopkotlin.database.tables

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = varchar("id", 36)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val fullName = varchar("full_name", 255)
    val role = varchar("role", 50).default("USER")
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}

object Categories : Table() {
    val id = varchar("id", 36)
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val imageUrl = varchar("image_url", 500).nullable()

    override val primaryKey = PrimaryKey(id)
}

object Products : Table() {
    val id = varchar("id", 36)
    val name = varchar("name", 255)
    val description = text("description")
    val price = double("price")
    val categoryId = varchar("category_id", 36).references(Categories.id)
    val imageUrl = varchar("image_url", 500)
    val stock = integer("stock").default(0)
    val featured = bool("featured").default(false)
    val rating = double("rating").default(0.0)
    val reviewCount = integer("review_count").default(0)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}

object Orders : Table() {
    val id = varchar("id", 36)
    val userId = varchar("user_id", 36).references(Users.id)
    val totalAmount = double("total_amount")
    val status = varchar("status", 50)
    val paymentIntentId = varchar("payment_intent_id", 255).nullable()
    val shippingAddress = text("shipping_address")
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}

object OrderItems : Table() {
    val id = varchar("id", 36)
    val orderId = varchar("order_id", 36).references(Orders.id)
    val productId = varchar("product_id", 36).references(Products.id)
    val quantity = integer("quantity")
    val price = double("price")

    override val primaryKey = PrimaryKey(id)
}

object CartItems : Table() {
    val id = varchar("id", 36)
    val userId = varchar("user_id", 36).references(Users.id)
    val productId = varchar("product_id", 36).references(Products.id)
    val quantity = integer("quantity")
    val addedAt = long("added_at")

    override val primaryKey = PrimaryKey(id)
}
```

### 2.2 Models

```kotlin
// src/main/kotlin/com/shopkotlin/models/Models.kt
package com.shopkotlin.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val createdAt: Long
)

@Serializable
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: String,
    val categoryName: String,
    val imageUrl: String,
    val stock: Int,
    val featured: Boolean,
    val rating: Double,
    val reviewCount: Int
)

@Serializable
data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val shippingAddress: ShippingAddress,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double
)

@Serializable
enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

@Serializable
data class ShippingAddress(
    val fullName: String,
    val addressLine1: String,
    val addressLine2: String?,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String,
    val phone: String
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String?
)

// Request/Response DTOs
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: User
)

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItemRequest>,
    val shippingAddress: ShippingAddress,
    val paymentMethodId: String
)

@Serializable
data class OrderItemRequest(
    val productId: String,
    val quantity: Int
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val errors: Map<String, List<String>>? = null
)
```

### 2.3 Core Services

```kotlin
// src/main/kotlin/com/shopkotlin/services/OrderService.kt
package com.shopkotlin.services

import com.shopkotlin.models.*
import com.shopkotlin.repositories.OrderRepository
import com.shopkotlin.repositories.ProductRepository
import java.util.UUID

class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val paymentService: PaymentService,
    private val emailService: EmailService
) {
    suspend fun createOrder(
        userId: String,
        request: CreateOrderRequest
    ): Result<Order> {
        // 1. Validate items and calculate total
        val items = mutableListOf<OrderItem>()
        var totalAmount = 0.0

        for (itemRequest in request.items) {
            val product = productRepository.findById(itemRequest.productId)
                ?: return Result.failure(Exception("Product not found: ${itemRequest.productId}"))

            if (product.stock < itemRequest.quantity) {
                return Result.failure(Exception("Insufficient stock for ${product.name}"))
            }

            totalAmount += product.price * itemRequest.quantity
            items.add(
                OrderItem(
                    productId = product.id,
                    productName = product.name,
                    quantity = itemRequest.quantity,
                    price = product.price
                )
            )
        }

        // 2. Process payment
        val paymentResult = paymentService.createPaymentIntent(
            amount = totalAmount,
            currency = "usd",
            paymentMethodId = request.paymentMethodId,
            metadata = mapOf(
                "user_id" to userId,
                "order_type" to "product_purchase"
            )
        )

        if (paymentResult.isFailure) {
            return Result.failure(paymentResult.exceptionOrNull()!!)
        }

        val paymentIntentId = paymentResult.getOrNull()!!

        // 3. Create order
        val order = Order(
            id = UUID.randomUUID().toString(),
            userId = userId,
            items = items,
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            shippingAddress = request.shippingAddress,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val savedOrder = orderRepository.create(order, paymentIntentId)

        // 4. Update inventory
        for (item in items) {
            productRepository.decrementStock(item.productId, item.quantity)
        }

        // 5. Send confirmation email
        emailService.sendOrderConfirmation(order)

        return Result.success(savedOrder)
    }

    suspend fun getOrdersByUser(userId: String): List<Order> {
        return orderRepository.findByUserId(userId)
    }

    suspend fun getOrderById(orderId: String, userId: String): Order? {
        val order = orderRepository.findById(orderId)
        return if (order?.userId == userId) order else null
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Order> {
        val order = orderRepository.updateStatus(orderId, status)
            ?: return Result.failure(Exception("Order not found"))

        // Send status update email
        emailService.sendOrderStatusUpdate(order)

        return Result.success(order)
    }
}
```

### 2.4 API Routes

```kotlin
// src/main/kotlin/com/shopkotlin/routes/productRoutes.kt
package com.shopkotlin.routes

import com.shopkotlin.services.ProductService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.http.*

fun Route.productRoutes(productService: ProductService) {
    route("/api/products") {
        get {
            val category = call.parameters["category"]
            val search = call.parameters["search"]
            val featured = call.parameters["featured"]?.toBoolean()
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 50
            val offset = call.parameters["offset"]?.toIntOrNull() ?: 0

            val products = when {
                search != null -> productService.search(search, limit, offset)
                category != null -> productService.getByCategory(category, limit, offset)
                featured == true -> productService.getFeatured(limit)
                else -> productService.getAll(limit, offset)
            }

            call.respond(ApiResponse(success = true, data = products))
        }

        get("/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(success = false, message = "Product ID required")
                )

            val product = productService.getById(id)
                ?: return@get call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse<Unit>(success = false, message = "Product not found")
                )

            call.respond(ApiResponse(success = true, data = product))
        }
    }

    route("/api/categories") {
        get {
            val categories = productService.getAllCategories()
            call.respond(ApiResponse(success = true, data = categories))
        }
    }
}
```

---

## Phase 3: Android App Development (4-6 hours)

### 3.1 API Client

```kotlin
// app/src/main/kotlin/com/shopkotlin/data/api/ApiClient.kt
package com.shopkotlin.data.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.http.*
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

    private var authToken: String? = null

    fun setAuthToken(token: String) {
        authToken = token
    }

    suspend fun login(request: LoginRequest): AuthResponse {
        return client.post("$baseUrl/api/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<ApiResponse<AuthResponse>>().data!!
    }

    suspend fun getProducts(
        category: String? = null,
        search: String? = null
    ): List<Product> {
        return client.get("$baseUrl/api/products") {
            authToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            category?.let { parameter("category", it) }
            search?.let { parameter("search", it) }
        }.body<ApiResponse<List<Product>>>().data!!
    }

    suspend fun createOrder(request: CreateOrderRequest): Order {
        return client.post("$baseUrl/api/orders") {
            header(HttpHeaders.Authorization, "Bearer $authToken")
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<ApiResponse<Order>>().data!!
    }
}
```

### 3.2 Product Screen

```kotlin
// app/src/main/kotlin/com/shopkotlin/ui/screens/product/ProductListScreen.kt
package com.shopkotlin.ui.screens.product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    onProductClick: (String) -> Unit,
    onCartClick: () -> Unit
) {
    val products by viewModel.products.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ShopKotlin") },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartItemCount > 0) {
                                Badge { Text("$cartItemCount") }
                            }
                        }
                    ) {
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Default.ShoppingCart, "Cart")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(products, key = { it.id }) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "$${"%.2f".format(product.price)}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < product.rating) {
                            Icons.Default.Star
                        } else {
                            Icons.Default.StarBorder
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text("(${product.reviewCount})")
            }
        }
    }
}
```

### 3.3 Cart ViewModel

```kotlin
// app/src/main/kotlin/com/shopkotlin/ui/screens/cart/CartViewModel.kt
package com.shopkotlin.ui.screens.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shopkotlin.data.repository.CartRepository
import com.shopkotlin.models.CartItem
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val totalAmount: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.product.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            cartRepository.getCartItems().collect { items ->
                _cartItems.value = items
            }
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        viewModelScope.launch {
            if (quantity <= 0) {
                cartRepository.removeFromCart(productId)
            } else {
                cartRepository.updateQuantity(productId, quantity)
            }
        }
    }

    fun removeItem(productId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(productId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }
}
```

---

## Phase 4: Testing (2-3 hours)

### Backend Tests

```kotlin
// src/test/kotlin/com/shopkotlin/OrderServiceTest.kt
package com.shopkotlin

import com.shopkotlin.models.*
import com.shopkotlin.repositories.*
import com.shopkotlin.services.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.*

class OrderServiceTest {

    private val orderRepository = mockk<OrderRepository>()
    private val productRepository = mockk<ProductRepository>()
    private val paymentService = mockk<PaymentService>()
    private val emailService = mockk<EmailService>(relaxed = true)

    private val orderService = OrderService(
        orderRepository,
        productRepository,
        paymentService,
        emailService
    )

    @Test
    fun `createOrder should successfully create order when all validations pass`() = runTest {
        // Arrange
        val userId = "user123"
        val productId = "product456"
        val product = Product(
            id = productId,
            name = "Test Product",
            description = "Description",
            price = 99.99,
            categoryId = "cat1",
            categoryName = "Category",
            imageUrl = "http://image.jpg",
            stock = 10,
            featured = false,
            rating = 4.5,
            reviewCount = 100
        )

        val request = CreateOrderRequest(
            items = listOf(OrderItemRequest(productId, 2)),
            shippingAddress = ShippingAddress(
                fullName = "John Doe",
                addressLine1 = "123 Main St",
                addressLine2 = null,
                city = "City",
                state = "ST",
                zipCode = "12345",
                country = "US",
                phone = "1234567890"
            ),
            paymentMethodId = "pm_123"
        )

        val paymentIntentId = "pi_123"

        coEvery { productRepository.findById(productId) } returns product
        coEvery { paymentService.createPaymentIntent(any(), any(), any(), any()) } returns Result.success(paymentIntentId)
        coEvery { orderRepository.create(any(), any()) } answers {
            firstArg() as Order
        }
        coEvery { productRepository.decrementStock(any(), any()) } just Runs

        // Act
        val result = orderService.createOrder(userId, request)

        // Assert
        assertTrue(result.isSuccess)
        val order = result.getOrNull()!!
        assertEquals(userId, order.userId)
        assertEquals(2, order.items[0].quantity)
        assertEquals(199.98, order.totalAmount, 0.01)

        coVerify { productRepository.decrementStock(productId, 2) }
        coVerify { emailService.sendOrderConfirmation(any()) }
    }

    @Test
    fun `createOrder should fail when product out of stock`() = runTest {
        // Arrange
        val productId = "product456"
        val product = Product(
            id = productId,
            name = "Test Product",
            description = "Description",
            price = 99.99,
            categoryId = "cat1",
            categoryName = "Category",
            imageUrl = "http://image.jpg",
            stock = 1, // Only 1 in stock
            featured = false,
            rating = 4.5,
            reviewCount = 100
        )

        val request = CreateOrderRequest(
            items = listOf(OrderItemRequest(productId, 5)), // Requesting 5
            shippingAddress = mockk(),
            paymentMethodId = "pm_123"
        )

        coEvery { productRepository.findById(productId) } returns product

        // Act
        val result = orderService.createOrder("user123", request)

        // Assert
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()!!.message!!.contains("Insufficient stock"))

        coVerify(exactly = 0) { paymentService.createPaymentIntent(any(), any(), any(), any()) }
        coVerify(exactly = 0) { orderRepository.create(any(), any()) }
    }
}
```

### Android Tests

```kotlin
// app/src/test/kotlin/com/shopkotlin/CartViewModelTest.kt
package com.shopkotlin

import app.cash.turbine.test
import com.shopkotlin.data.repository.CartRepository
import com.shopkotlin.models.*
import com.shopkotlin.ui.screens.cart.CartViewModel
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CartViewModelTest {

    private val cartRepository = mockk<CartRepository>()
    private val viewModel = CartViewModel(cartRepository)

    @Test
    fun `cart items should be loaded on init`() = runTest {
        // Arrange
        val cartItems = listOf(
            CartItem(product = mockk(), quantity = 2)
        )

        coEvery { cartRepository.getCartItems() } returns flowOf(cartItems)

        // Act
        viewModel.cartItems.test {
            val items = awaitItem()

            // Assert
            assertEquals(cartItems, items)
        }
    }

    @Test
    fun `updateQuantity should call repository`() = runTest {
        coEvery { cartRepository.updateQuantity(any(), any()) } just Runs

        viewModel.updateQuantity("product1", 5)

        coVerify { cartRepository.updateQuantity("product1", 5) }
    }

    @Test
    fun `totalAmount should sum all items`() = runTest {
        val product1 = mockk<Product> {
            every { price } returns 10.0
        }
        val product2 = mockk<Product> {
            every { price } returns 20.0
        }

        val cartItems = listOf(
            CartItem(product1, 2), // 20.0
            CartItem(product2, 3)  // 60.0
        )

        coEvery { cartRepository.getCartItems() } returns flowOf(cartItems)

        viewModel.totalAmount.test {
            val total = awaitItem()
            assertEquals(80.0, total, 0.01)
        }
    }
}
```

---

## Phase 5: CI/CD Pipeline (1-2 hours)

### GitHub Actions Workflow

```yaml
# .github/workflows/ci-cd.yml
name: ShopKotlin CI/CD

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

env:
  JAVA_VERSION: '17'

jobs:
  backend-test:
    name: Backend Tests
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_PASSWORD: testpass
          POSTGRES_DB: shopkotlin_test
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
        ports:
          - 5432:5432

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Run backend tests
        run: |
          cd shopkotlin-backend
          ./gradlew test

      - name: Upload coverage
        uses: codecov/codecov-action@v3
        with:
          files: shopkotlin-backend/build/reports/jacoco/test/jacocoTestReport.xml

  backend-build:
    name: Build Backend
    needs: backend-test
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Build JAR
        run: |
          cd shopkotlin-backend
          ./gradlew shadowJar

      - name: Build Docker image
        run: |
          cd shopkotlin-backend
          docker build -t shopkotlin-backend:latest .

      - name: Push to registry (main only)
        if: github.ref == 'refs/heads/main'
        run: |
          echo "${{ secrets.DOCKER_PASSWORD }}" | docker login -u "${{ secrets.DOCKER_USERNAME }}" --password-stdin
          docker tag shopkotlin-backend:latest ${{ secrets.DOCKER_USERNAME }}/shopkotlin-backend:latest
          docker push ${{ secrets.DOCKER_USERNAME }}/shopkotlin-backend:latest

  android-test:
    name: Android Tests
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Run unit tests
        run: |
          cd shopkotlin-android
          ./gradlew test

  android-build:
    name: Build Android APK
    needs: android-test
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'

      - name: Build debug APK
        run: |
          cd shopkotlin-android
          ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: shopkotlin-android/app/build/outputs/apk/debug/app-debug.apk

  deploy:
    name: Deploy to Production
    needs: [backend-build, android-build]
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest

    steps:
      - name: Deploy to Heroku
        uses: akhileshns/heroku-deploy@v3.12.14
        with:
          heroku_api_key: ${{ secrets.HEROKU_API_KEY }}
          heroku_app_name: "shopkotlin-api"
          heroku_email: ${{ secrets.HEROKU_EMAIL }}
          appdir: "shopkotlin-backend"
```

---

## Phase 6: Deployment (1-2 hours)

### Docker Setup

**shopkotlin-backend/Dockerfile**:
```dockerfile
FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle shadowJar --no-daemon

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s CMD wget -q --spider http://localhost:8080/health || exit 1
CMD ["java", "-jar", "app.jar"]
```

**docker-compose.yml**:
```yaml
version: '3.8'

services:
  backend:
    build: ./shopkotlin-backend
    ports:
      - "8080:8080"
    environment:
      - DB_HOST=db
      - DB_PORT=5432
      - DB_NAME=shopkotlin
      - DB_USER=shopkotlin
      - DB_PASSWORD=${DB_PASSWORD}
      - JWT_SECRET=${JWT_SECRET}
      - STRIPE_API_KEY=${STRIPE_API_KEY}
    depends_on:
      db:
        condition: service_healthy

  db:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=shopkotlin
      - POSTGRES_USER=shopkotlin
      - POSTGRES_PASSWORD=${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U shopkotlin"]
      interval: 10s
      timeout: 5s
      retries: 5

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - ./ssl:/etc/nginx/ssl:ro
    depends_on:
      - backend

volumes:
  postgres_data:
```

---

## Extension Challenges

Once you've completed the core project, challenge yourself with these extensions:

### 1. Advanced Features
- ⭐ Product reviews and ratings
- ⭐ Wishlist functionality
- ⭐ Order tracking with real-time updates
- ⭐ Coupon/discount codes
- ⭐ Product recommendations (ML-based)
- ⭐ Multi-currency support

### 2. Mobile Enhancements
- ⭐ Offline mode with Room database
- ⭐ Push notifications for order updates
- ⭐ Biometric authentication
- ⭐ Dark mode
- ⭐ Animations and transitions
- ⭐ Widget for quick access

### 3. Admin Features
- ⭐ Admin dashboard (web or mobile)
- ⭐ Inventory management
- ⭐ Sales analytics
- ⭐ User management
- ⭐ Product CRUD operations

### 4. Technical Improvements
- ⭐ GraphQL instead of REST
- ⭐ gRPC for mobile-backend communication
- ⭐ Redis caching layer
- ⭐ Elasticsearch for advanced search
- ⭐ WebSockets for real-time features
- ⭐ Rate limiting and DDoS protection

---

## Submission Checklist

Before submitting, ensure you have:

**Backend**:
- [ ] All API endpoints working
- [ ] JWT authentication implemented
- [ ] PostgreSQL database setup
- [ ] Stripe payment integration
- [ ] Unit tests with 70%+ coverage
- [ ] Integration tests for main flows
- [ ] Docker container working
- [ ] Deployed to cloud (Heroku/AWS/GCP)
- [ ] Environment variables configured
- [ ] Logging and error tracking (Sentry)

**Android**:
- [ ] All screens implemented
- [ ] API integration complete
- [ ] Authentication flow working
- [ ] Cart and checkout functional
- [ ] Order history displayed
- [ ] Unit tests for ViewModels
- [ ] UI tests for critical flows
- [ ] APK built successfully
- [ ] App runs on physical device

**DevOps**:
- [ ] CI/CD pipeline configured
- [ ] Automated tests running
- [ ] Docker images building
- [ ] Deployment automated
- [ ] Monitoring setup

**Documentation**:
- [ ] README with setup instructions
- [ ] API documentation
- [ ] Architecture diagrams
- [ ] Environment setup guide
- [ ] Deployment guide

---

## Final Thoughts

Congratulations on completing the Kotlin Training Course! 🎉🎉🎉

You've built a **production-ready, full-stack e-commerce platform** using:
- Kotlin (backend and Android)
- Ktor (REST API)
- PostgreSQL (database)
- Jetpack Compose (modern Android UI)
- JWT authentication
- Stripe payments
- Docker (containerization)
- GitHub Actions (CI/CD)
- Cloud deployment

### You've Mastered:
✅ Kotlin fundamentals and advanced features
✅ Backend development with Ktor
✅ Android development with Jetpack Compose
✅ Database design and optimization
✅ API design and security
✅ Testing strategies (unit, integration, UI)
✅ DevOps practices (CI/CD, Docker)
✅ Cloud deployment
✅ Performance optimization
✅ Security best practices
✅ Monitoring and analytics

### What's Next?

**1. Enhance Your Project**:
- Add the extension challenges
- Deploy to production
- Get real users
- Collect feedback

**2. Build Your Portfolio**:
- Showcase ShopKotlin on GitHub
- Write blog posts about your learnings
- Create a portfolio website
- Share on LinkedIn

**3. Continue Learning**:
- Explore Kotlin Multiplatform in depth
- Learn Compose Multiplatform (desktop, web)
- Study microservices architecture
- Master Kubernetes and cloud-native development

**4. Join the Community**:
- Contribute to open-source Kotlin projects
- Join Kotlin Slack/Discord communities
- Attend Kotlin conferences (KotlinConf)
- Share your knowledge through teaching

### You're Ready!

You now have the skills to:
- Build production Android apps
- Develop scalable backend APIs
- Work at modern tech companies
- Start your own projects
- Mentor other developers

**The journey doesn't end here - it's just beginning!**

Keep coding, keep learning, and most importantly, keep building amazing things with Kotlin! 🚀

---

## Resources

### Official Documentation
- [Kotlin Official Docs](https://kotlinlang.org/docs/home.html)
- [Ktor Documentation](https://ktor.io/docs/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Exposed ORM](https://github.com/JetBrains/Exposed/wiki)

### Community
- [Kotlin Slack](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)
- [r/Kotlin](https://www.reddit.com/r/Kotlin/)
- [Kotlin Blog](https://blog.jetbrains.com/kotlin/)
- [Android Developers](https://developer.android.com/)

### Books
- "Kotlin in Action" by Dmitry Jemerov
- "Head First Kotlin" by Dawn Griffiths
- "Effective Kotlin" by Marcin Moskala

### Courses
- [Kotlin for Java Developers (Coursera)](https://www.coursera.org/learn/kotlin-for-java-developers)
- [Android Basics with Compose](https://developer.android.com/courses/android-basics-compose/course)

---

**Thank you for completing this course! We believe in you! 💪**

---

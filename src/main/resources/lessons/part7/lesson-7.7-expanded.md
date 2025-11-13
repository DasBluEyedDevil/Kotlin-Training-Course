# Lesson 7.7: Monitoring and Analytics

**Estimated Time**: 75 minutes

---

## Introduction

Deploying your app is just the beginning. The real question is: **How is it performing in production?**

Without monitoring, you're flying blind:
- ❌ Users experience crashes, you don't know
- ❌ APIs are slow, no alerts
- ❌ Features are unused, wasted effort
- ❌ Servers are down, customers leave

In this lesson, you'll master production monitoring:
- ✅ Application logging strategies
- ✅ Error tracking (Sentry, Firebase Crashlytics)
- ✅ Analytics (Firebase Analytics, Mixpanel)
- ✅ Performance monitoring (APM)
- ✅ Alerting and incident response
- ✅ User feedback integration

---

## Why Monitoring Matters

### The Cost of Ignorance

**Real Examples**:

**Case 1: Silent Failures**
- E-commerce site payment API failing
- 20% of checkout attempts fail
- Company loses $50K before noticing
- Customers blame themselves, leave negative reviews

**Case 2: Performance Degradation**
- App becomes 5x slower over 2 weeks
- Users complain on social media
- No internal alerts
- 30% user churn before fix

**Case 3: Feature Waste**
- Team builds complex search feature
- 6 weeks of development
- Analytics show 0.1% adoption
- Could have built something users wanted

### The Power of Data

**With Proper Monitoring**:
- Detect issues in seconds, not days
- Fix bugs before users complain
- Build features users actually use
- Make data-driven decisions

---

## Application Logging

### Logging Levels

```kotlin
import org.slf4j.LoggerFactory

class UserService {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    suspend fun createUser(email: String, password: String): User {
        // DEBUG: Detailed info for development
        logger.debug("Creating user with email: $email")

        // Validate
        if (!isValidEmail(email)) {
            // WARN: Something wrong but recoverable
            logger.warn("Invalid email attempt: $email")
            throw ValidationException("Invalid email")
        }

        try {
            val user = userRepository.create(email, password)

            // INFO: Important business events
            logger.info("User created successfully: ${user.id}")

            return user
        } catch (e: SQLException) {
            // ERROR: Something broke
            logger.error("Database error creating user", e)
            throw e
        }
    }
}
```

**When to Use Each Level**:
- **ERROR**: Exceptions, failures, critical issues
- **WARN**: Potential problems, validation failures
- **INFO**: Important business events (user signup, purchase)
- **DEBUG**: Detailed execution flow (development only)
- **TRACE**: Very detailed (rarely used)

### Structured Logging

❌ **Bad** (String concatenation):
```kotlin
logger.info("User " + userId + " purchased " + productId + " for $" + price)
// Hard to parse, search, analyze
```

✅ **Good** (Structured):
```kotlin
import net.logstash.logback.argument.StructuredArguments.*

logger.info(
    "User purchased product",
    keyValue("userId", userId),
    keyValue("productId", productId),
    keyValue("price", price),
    keyValue("currency", "USD")
)

// Outputs JSON:
// {
//   "message": "User purchased product",
//   "userId": "123",
//   "productId": "456",
//   "price": 99.99,
//   "currency": "USD",
//   "timestamp": "2025-01-15T10:30:00Z"
// }
```

### Logback Configuration

**src/main/resources/logback.xml**:
```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder class="net.logstash.logback.encoder.LogstashEncoder">
            <includeCallerData>true</includeCallerData>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LogstashEncoder"/>
    </appender>

    <!-- Application logs -->
    <logger name="com.example" level="INFO"/>

    <!-- Third-party logs (less verbose) -->
    <logger name="org.jetbrains.exposed" level="WARN"/>
    <logger name="io.ktor" level="INFO"/>

    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

---

## Error Tracking with Sentry

### Why Sentry?

- ✅ Captures all exceptions automatically
- ✅ Groups similar errors together
- ✅ Shows stack traces with context
- ✅ Email/Slack alerts on new errors
- ✅ Tracks error frequency and trends

### Backend (Ktor) Integration

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.sentry:sentry:7.1.0")
    implementation("io.sentry:sentry-logback:7.1.0")
}
```

**Initialize Sentry**:
```kotlin
import io.sentry.Sentry

fun Application.module() {
    // Initialize Sentry
    Sentry.init { options ->
        options.dsn = System.getenv("SENTRY_DSN")
        options.environment = System.getenv("ENVIRONMENT") ?: "development"
        options.release = System.getenv("VERSION") ?: "1.0.0"
        options.tracesSampleRate = 1.0 // 100% of transactions
    }

    // Catch all exceptions
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            // Send to Sentry
            Sentry.captureException(cause)

            logger.error("Unhandled exception", cause)

            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to "Internal server error")
            )
        }
    }
}
```

**Manual Error Capture**:
```kotlin
try {
    processPayment(orderId)
} catch (e: PaymentException) {
    Sentry.captureException(e) { scope ->
        scope.setTag("order_id", orderId)
        scope.setTag("payment_provider", "stripe")
        scope.setExtra("amount", amount)
        scope.setUser(User().apply {
            id = userId
            email = userEmail
        })
    }
    throw e
}
```

### Android (Crashlytics) Integration

```kotlin
// build.gradle.kts (project)
plugins {
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}

// build.gradle.kts (app)
plugins {
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
}
```

**Initialize in Application**:
```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Crashlytics is auto-initialized

        // Log custom data
        FirebaseCrashlytics.getInstance().apply {
            setUserId(currentUserId)
            setCustomKey("user_tier", "premium")
            setCustomKey("app_version", BuildConfig.VERSION_NAME)
        }
    }
}
```

**Log Custom Errors**:
```kotlin
try {
    processOrder(order)
} catch (e: Exception) {
    FirebaseCrashlytics.getInstance().apply {
        log("Processing order: ${order.id}")
        setCustomKey("order_id", order.id)
        setCustomKey("order_total", order.total)
        recordException(e)
    }
    throw e
}
```

---

## Analytics

### Firebase Analytics (Android)

**Track Events**:
```kotlin
class AnalyticsManager(private val context: Context) {
    private val analytics = FirebaseAnalytics.getInstance(context)

    fun logSignUp(method: String) {
        analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
            param(FirebaseAnalytics.Param.METHOD, method)
        }
    }

    fun logPurchase(orderId: String, value: Double, currency: String) {
        analytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
            param(FirebaseAnalytics.Param.TRANSACTION_ID, orderId)
            param(FirebaseAnalytics.Param.VALUE, value)
            param(FirebaseAnalytics.Param.CURRENCY, currency)
        }
    }

    fun logProductView(productId: String, productName: String) {
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM) {
            param(FirebaseAnalytics.Param.ITEM_ID, productId)
            param(FirebaseAnalytics.Param.ITEM_NAME, productName)
        }
    }

    fun logSearch(query: String, resultsCount: Int) {
        analytics.logEvent(FirebaseAnalytics.Event.SEARCH) {
            param(FirebaseAnalytics.Param.SEARCH_TERM, query)
            param("results_count", resultsCount.toLong())
        }
    }

    // Custom events
    fun logFeatureUsed(featureName: String) {
        analytics.logEvent("feature_used") {
            param("feature_name", featureName)
            param("timestamp", System.currentTimeMillis())
        }
    }
}
```

**User Properties**:
```kotlin
analytics.setUserProperty("user_tier", "premium")
analytics.setUserProperty("favorite_category", "electronics")
analytics.setUserProperty("signup_date", "2025-01-15")
```

### Mixpanel (Cross-Platform)

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.mixpanel.android:mixpanel-android:7.4.0")
}
```

**Initialize and Track**:
```kotlin
class MixpanelManager(context: Context) {
    private val mixpanel = MixpanelAPI.getInstance(context, "YOUR_PROJECT_TOKEN")

    fun identify(userId: String) {
        mixpanel.identify(userId)
        mixpanel.people.set("\$email", userEmail)
        mixpanel.people.set("\$name", userName)
        mixpanel.people.set("Signup Date", signupDate)
    }

    fun track(eventName: String, properties: Map<String, Any> = emptyMap()) {
        val json = JSONObject(properties)
        mixpanel.track(eventName, json)
    }

    fun trackPurchase(amount: Double, productId: String) {
        mixpanel.people.trackCharge(amount, JSONObject().apply {
            put("product_id", productId)
            put("timestamp", System.currentTimeMillis())
        })

        track("Purchase", mapOf(
            "amount" to amount,
            "product_id" to productId
        ))
    }

    fun incrementProperty(property: String, value: Double = 1.0) {
        mixpanel.people.increment(property, value)
    }
}

// Usage
mixpanel.identify("user_123")
mixpanel.track("Screen Viewed", mapOf("screen" to "Product Details"))
mixpanel.trackPurchase(99.99, "product_456")
mixpanel.incrementProperty("Purchases")
```

### Backend Analytics

**Custom Analytics Service**:
```kotlin
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope

class AnalyticsService(
    private val database: Database,
    private val scope: CoroutineScope
) {
    suspend fun trackEvent(
        userId: String?,
        event: String,
        properties: Map<String, Any> = emptyMap()
    ) {
        scope.launch {
            try {
                database.transaction {
                    AnalyticsEvents.insert {
                        it[AnalyticsEvents.userId] = userId
                        it[AnalyticsEvents.event] = event
                        it[AnalyticsEvents.properties] = Json.encodeToString(properties)
                        it[AnalyticsEvents.timestamp] = System.currentTimeMillis()
                        it[AnalyticsEvents.ipAddress] = getCurrentIpAddress()
                        it[AnalyticsEvents.userAgent] = getCurrentUserAgent()
                    }
                }
            } catch (e: Exception) {
                logger.error("Failed to track event", e)
            }
        }
    }

    suspend fun getDailyActiveUsers(days: Int = 30): List<DailyStats> {
        return database.transaction {
            AnalyticsEvents
                .select { AnalyticsEvents.timestamp greaterEq (System.currentTimeMillis() - days * 24 * 3600000) }
                .groupBy { it[AnalyticsEvents.timestamp] / (24 * 3600000) }
                .map { (day, events) ->
                    DailyStats(
                        date = Date(day * 24 * 3600000),
                        activeUsers = events.map { it[AnalyticsEvents.userId] }.toSet().size,
                        eventCount = events.size
                    )
                }
        }
    }

    suspend fun getPopularFeatures(limit: Int = 10): List<FeatureStats> {
        return database.transaction {
            AnalyticsEvents
                .select { AnalyticsEvents.event eq "feature_used" }
                .groupBy { Json.decodeFromString<Map<String, String>>(it[AnalyticsEvents.properties])["feature_name"] }
                .map { (feature, events) ->
                    FeatureStats(
                        feature = feature ?: "unknown",
                        usageCount = events.size,
                        uniqueUsers = events.map { it[AnalyticsEvents.userId] }.toSet().size
                    )
                }
                .sortedByDescending { it.usageCount }
                .take(limit)
        }
    }
}
```

---

## Performance Monitoring (APM)

### New Relic Integration

```kotlin
// build.gradle.kts
dependencies {
    implementation("com.newrelic.agent.java:newrelic-api:8.7.0")
}
```

**newrelic.yml**:
```yaml
common: &default_settings
  license_key: '<%= license_key %>'
  app_name: My Ktor App
  log_level: info

  transaction_tracer:
    enabled: true
    transaction_threshold: apdex_f
    record_sql: obfuscated

  error_collector:
    enabled: true
```

**Custom Metrics**:
```kotlin
import com.newrelic.api.agent.NewRelic

class OrderService {
    suspend fun processOrder(order: Order) {
        val startTime = System.currentTimeMillis()

        try {
            // Process order
            val result = doProcessOrder(order)

            // Record success
            NewRelic.recordMetric("Custom/OrderProcessing/Success", 1f)

            // Record processing time
            val duration = System.currentTimeMillis() - startTime
            NewRelic.recordResponseTimeMetric("Custom/OrderProcessing/Duration", duration)

            return result
        } catch (e: Exception) {
            NewRelic.recordMetric("Custom/OrderProcessing/Error", 1f)
            NewRelic.noticeError(e)
            throw e
        }
    }
}
```

### Custom Performance Tracking

```kotlin
class PerformanceMonitor {
    private val metrics = ConcurrentHashMap<String, MutableList<Long>>()

    fun track(operation: String, block: () -> Unit) {
        val start = System.nanoTime()
        try {
            block()
        } finally {
            val duration = (System.nanoTime() - start) / 1_000_000 // ms
            metrics.getOrPut(operation) { mutableListOf() }.add(duration)
        }
    }

    suspend fun <T> trackSuspend(operation: String, block: suspend () -> T): T {
        val start = System.nanoTime()
        try {
            return block()
        } finally {
            val duration = (System.nanoTime() - start) / 1_000_000
            metrics.getOrPut(operation) { mutableListOf() }.add(duration)
        }
    }

    fun getStats(operation: String): PerformanceStats? {
        val durations = metrics[operation] ?: return null

        return PerformanceStats(
            operation = operation,
            count = durations.size,
            avgMs = durations.average(),
            minMs = durations.minOrNull() ?: 0,
            maxMs = durations.maxOrNull() ?: 0,
            p95Ms = durations.sorted()[durations.size * 95 / 100],
            p99Ms = durations.sorted()[durations.size * 99 / 100]
        )
    }

    fun getAllStats(): Map<String, PerformanceStats> {
        return metrics.keys.associateWith { getStats(it)!! }
    }
}

// Usage
val monitor = PerformanceMonitor()

monitor.track("database_query") {
    userRepository.findAll()
}

val user = monitor.trackSuspend("api_call") {
    apiClient.getUser(userId)
}
```

---

## Alerting

### Alert Configuration (Example: PagerDuty)

```kotlin
class AlertService(private val pagerDutyApiKey: String) {
    private val client = HttpClient()

    suspend fun triggerAlert(
        title: String,
        description: String,
        severity: Severity
    ) {
        val event = mapOf(
            "routing_key" to pagerDutyApiKey,
            "event_action" to "trigger",
            "payload" to mapOf(
                "summary" to title,
                "severity" to severity.name.lowercase(),
                "source" to "ktor-backend",
                "custom_details" to mapOf(
                    "description" to description,
                    "timestamp" to System.currentTimeMillis()
                )
            )
        )

        try {
            client.post("https://events.pagerduty.com/v2/enqueue") {
                contentType(ContentType.Application.Json)
                setBody(event)
            }
        } catch (e: Exception) {
            logger.error("Failed to send alert", e)
        }
    }

    enum class Severity {
        CRITICAL, ERROR, WARNING, INFO
    }
}

// Usage
class HealthCheckService(private val alertService: AlertService) {
    suspend fun checkDatabaseHealth() {
        try {
            database.query("SELECT 1")
        } catch (e: Exception) {
            alertService.triggerAlert(
                title = "Database Connection Failed",
                description = "Unable to connect to database: ${e.message}",
                severity = AlertService.Severity.CRITICAL
            )
        }
    }
}
```

### Health Check Endpoint

```kotlin
fun Route.healthCheck(
    database: Database,
    redis: RedisClient
) {
    get("/health") {
        val status = mutableMapOf<String, Any>()

        // Check database
        val dbHealthy = try {
            database.transaction {
                exec("SELECT 1") { }
                true
            }
        } catch (e: Exception) {
            status["database_error"] = e.message ?: "Unknown"
            false
        }

        // Check Redis
        val redisHealthy = try {
            redis.ping()
            true
        } catch (e: Exception) {
            status["redis_error"] = e.message ?: "Unknown"
            false
        }

        status["database"] = if (dbHealthy) "healthy" else "unhealthy"
        status["redis"] = if (redisHealthy) "healthy" else "unhealthy"
        status["status"] = if (dbHealthy && redisHealthy) "healthy" else "unhealthy"
        status["timestamp"] = System.currentTimeMillis()

        val statusCode = if (dbHealthy && redisHealthy) {
            HttpStatusCode.OK
        } else {
            HttpStatusCode.ServiceUnavailable
        }

        call.respond(statusCode, status)
    }
}
```

---

## Exercise 1: Implement Complete Logging

Add structured logging to a service with proper levels.

---

## Solution 1

```kotlin
import org.slf4j.LoggerFactory
import net.logstash.logback.argument.StructuredArguments.*

class OrderService(
    private val orderRepository: OrderRepository,
    private val paymentService: PaymentService,
    private val inventoryService: InventoryService
) {
    private val logger = LoggerFactory.getLogger(OrderService::class.java)

    suspend fun createOrder(userId: String, items: List<OrderItem>): Order {
        logger.info(
            "Creating order",
            keyValue("userId", userId),
            keyValue("itemCount", items.size)
        )

        // Validate inventory
        logger.debug("Checking inventory for ${items.size} items")

        items.forEach { item ->
            val available = inventoryService.checkStock(item.productId, item.quantity)
            if (!available) {
                logger.warn(
                    "Insufficient inventory",
                    keyValue("productId", item.productId),
                    keyValue("requested", item.quantity)
                )
                throw InsufficientInventoryException(item.productId)
            }
        }

        // Calculate total
        val total = items.sumOf { it.price * it.quantity }
        logger.debug("Order total calculated: $$total")

        // Create order
        val order = try {
            orderRepository.create(
                userId = userId,
                items = items,
                total = total,
                status = OrderStatus.PENDING
            )
        } catch (e: SQLException) {
            logger.error(
                "Database error creating order",
                e,
                keyValue("userId", userId)
            )
            throw e
        }

        logger.info(
            "Order created",
            keyValue("orderId", order.id),
            keyValue("total", total),
            keyValue("status", order.status)
        )

        // Process payment
        try {
            logger.info("Processing payment for order ${order.id}")
            paymentService.charge(userId, total, order.id)

            orderRepository.updateStatus(order.id, OrderStatus.PAID)

            logger.info(
                "Payment successful",
                keyValue("orderId", order.id),
                keyValue("amount", total)
            )
        } catch (e: PaymentException) {
            logger.error(
                "Payment failed",
                e,
                keyValue("orderId", order.id),
                keyValue("userId", userId),
                keyValue("amount", total),
                keyValue("errorCode", e.errorCode)
            )

            orderRepository.updateStatus(order.id, OrderStatus.PAYMENT_FAILED)
            throw e
        }

        // Reserve inventory
        try {
            items.forEach { item ->
                inventoryService.reserve(item.productId, item.quantity, order.id)
            }

            logger.info(
                "Inventory reserved",
                keyValue("orderId", order.id)
            )
        } catch (e: Exception) {
            logger.error(
                "Inventory reservation failed",
                e,
                keyValue("orderId", order.id)
            )
            // Rollback payment
            paymentService.refund(order.id)
            throw e
        }

        logger.info(
            "Order processed successfully",
            keyValue("orderId", order.id),
            keyValue("userId", userId),
            keyValue("total", total),
            keyValue("itemCount", items.size)
        )

        return order
    }
}
```

---

## Exercise 2: Set Up Error Tracking

Integrate Sentry with custom context.

---

## Solution 2

```kotlin
import io.sentry.Sentry
import io.sentry.SentryEvent
import io.sentry.SentryLevel
import io.sentry.protocol.User

object SentryManager {
    fun init(dsn: String, environment: String) {
        Sentry.init { options ->
            options.dsn = dsn
            options.environment = environment
            options.release = System.getenv("VERSION") ?: "dev"
            options.tracesSampleRate = 1.0

            options.setBeforeSend { event, hint ->
                // Add custom tags to all events
                event.setTag("server_region", System.getenv("REGION") ?: "us-east-1")
                event.setTag("deployment", System.getenv("DEPLOYMENT_ID") ?: "local")
                event
            }
        }
    }

    fun captureException(
        exception: Throwable,
        userId: String? = null,
        extras: Map<String, Any> = emptyMap(),
        tags: Map<String, String> = emptyMap()
    ) {
        Sentry.captureException(exception) { scope ->
            // Set user
            userId?.let {
                scope.user = User().apply {
                    id = it
                }
            }

            // Add extras
            extras.forEach { (key, value) ->
                scope.setExtra(key, value)
            }

            // Add tags
            tags.forEach { (key, value) ->
                scope.setTag(key, value)
            }

            // Add breadcrumbs
            scope.addBreadcrumb("Exception captured: ${exception.message}")
        }
    }

    fun captureMessage(
        message: String,
        level: SentryLevel = SentryLevel.INFO,
        extras: Map<String, Any> = emptyMap()
    ) {
        Sentry.captureMessage(message, level) { scope ->
            extras.forEach { (key, value) ->
                scope.setExtra(key, value)
            }
        }
    }

    fun addBreadcrumb(message: String, category: String = "default") {
        Sentry.addBreadcrumb(message, category)
    }
}

// Usage in service
class PaymentService {
    fun processPayment(orderId: String, amount: Double): PaymentResult {
        SentryManager.addBreadcrumb("Starting payment processing", "payment")

        try {
            val result = stripeClient.charge(amount)

            SentryManager.addBreadcrumb("Payment successful", "payment")

            return result
        } catch (e: StripeException) {
            SentryManager.captureException(
                exception = e,
                extras = mapOf(
                    "order_id" to orderId,
                    "amount" to amount,
                    "stripe_error_code" to e.code
                ),
                tags = mapOf(
                    "payment_provider" to "stripe",
                    "error_type" to "payment_failed"
                )
            )
            throw PaymentException("Payment failed", e)
        }
    }
}
```

---

## Exercise 3: Create Analytics Dashboard

Build a simple analytics dashboard showing key metrics.

---

## Solution 3

```kotlin
// API endpoints for analytics
fun Route.analyticsRoutes(analyticsService: AnalyticsService) {
    authenticate("jwt") {
        get("/api/analytics/daily-active-users") {
            val days = call.parameters["days"]?.toIntOrNull() ?: 30
            val stats = analyticsService.getDailyActiveUsers(days)
            call.respond(stats)
        }

        get("/api/analytics/popular-features") {
            val limit = call.parameters["limit"]?.toIntOrNull() ?: 10
            val features = analyticsService.getPopularFeatures(limit)
            call.respond(features)
        }

        get("/api/analytics/user-retention") {
            val cohortDate = call.parameters["cohort"]
                ?: throw BadRequestException("Cohort date required")

            val retention = analyticsService.getUserRetention(cohortDate)
            call.respond(retention)
        }

        get("/api/analytics/conversion-funnel") {
            val funnel = analyticsService.getConversionFunnel()
            call.respond(funnel)
        }
    }
}

// Analytics queries
class AnalyticsService(private val database: Database) {
    suspend fun getConversionFunnel(): ConversionFunnel {
        return database.transaction {
            val signups = AnalyticsEvents
                .select { AnalyticsEvents.event eq "sign_up" }
                .count()

            val productViews = AnalyticsEvents
                .select { AnalyticsEvents.event eq "view_product" }
                .groupBy { it[AnalyticsEvents.userId] }
                .count()

            val addedToCart = AnalyticsEvents
                .select { AnalyticsEvents.event eq "add_to_cart" }
                .groupBy { it[AnalyticsEvents.userId] }
                .count()

            val purchases = AnalyticsEvents
                .select { AnalyticsEvents.event eq "purchase" }
                .groupBy { it[AnalyticsEvents.userId] }
                .count()

            ConversionFunnel(
                steps = listOf(
                    FunnelStep("Sign Up", signups, 100.0),
                    FunnelStep("View Product", productViews, (productViews.toDouble() / signups * 100)),
                    FunnelStep("Add to Cart", addedToCart, (addedToCart.toDouble() / signups * 100)),
                    FunnelStep("Purchase", purchases, (purchases.toDouble() / signups * 100))
                ),
                conversionRate = (purchases.toDouble() / signups * 100)
            )
        }
    }

    suspend fun getUserRetention(cohortDate: String): RetentionStats {
        // Implementation for cohort analysis
        // Returns percentage of users still active after N days
        return database.transaction {
            // Complex query...
            RetentionStats(/*...*/)
        }
    }
}

data class ConversionFunnel(
    val steps: List<FunnelStep>,
    val conversionRate: Double
)

data class FunnelStep(
    val name: String,
    val count: Long,
    val percentageOfTotal: Double
)
```

---

## Why This Matters

### Real Impact

**Error Tracking Saves Money**:
- Sentry detected payment bug in 2 minutes
- Fixed before 10 users affected
- Prevented $10K in lost revenue

**Analytics Drives Decisions**:
- Data showed 80% of users never use advanced features
- Simplified UI increased retention 25%
- Focused development on high-impact features

**Monitoring Prevents Outages**:
- Alert on high error rate (> 1%)
- Team notified in 30 seconds
- Fixed before significant impact

---

## Checkpoint Quiz

### Question 1
What logging level should you use for business events like "User purchased product"?

A) DEBUG
B) INFO
C) WARN
D) ERROR

### Question 2
What does Sentry do?

A) Monitors server CPU usage
B) Tracks and reports application errors
C) Analyzes user behavior
D) Optimizes database queries

### Question 3
Why use structured logging (JSON) instead of plain text?

A) Looks prettier
B) Takes less disk space
C) Easier to parse and analyze
D) Required by law

### Question 4
What is an APM tool?

A) Application Performance Monitoring
B) Advanced Payment Method
C) Automated Project Manager
D) API Protocol Manager

### Question 5
What should a /health endpoint return when the database is down?

A) 200 OK
B) 404 Not Found
C) 503 Service Unavailable
D) 500 Internal Server Error

---

## Quiz Answers

**Question 1: B) INFO**

Logging levels:
- **ERROR**: Exceptions, failures
- **WARN**: Potential issues
- **INFO**: Important business events ✅
- **DEBUG**: Detailed execution (dev only)

Business events = INFO level

---

**Question 2: B) Tracks and reports application errors**

Sentry:
- Captures all exceptions
- Groups similar errors
- Shows stack traces
- Sends alerts
- Tracks error trends

Essential for production apps!

---

**Question 3: C) Easier to parse and analyze**

JSON logs enable:
- Searching by field
- Aggregation and counting
- Automated analysis
- Integration with log tools

Text logs are hard to parse.

---

**Question 4: A) Application Performance Monitoring**

APM tools (New Relic, Datadog):
- Track request times
- Monitor database queries
- Identify bottlenecks
- Alert on slow performance

---

**Question 5: C) 503 Service Unavailable**

Health check status codes:
- **200 OK**: All systems healthy
- **503 Service Unavailable**: Critical dependency down ✅
- **500**: Code error (not appropriate here)

Load balancers remove unhealthy instances.

---

## What You've Learned

✅ Structured logging strategies with logback
✅ Error tracking with Sentry and Firebase Crashlytics
✅ Analytics with Firebase Analytics and Mixpanel
✅ Performance monitoring (APM)
✅ Health checks and alerting
✅ Building analytics dashboards

---

## Next Steps

In **Lesson 7.8: Final Capstone - Full Stack E-Commerce Platform**, you'll build:
- Complete production-ready application
- Backend: Ktor REST API + PostgreSQL
- Android app with Jetpack Compose
- Full features: products, cart, checkout, orders
- Authentication, testing, CI/CD, deployment
- Monitoring and analytics

Time to put everything together! 🚀

---

# Lesson 5.5: JSON Serialization with kotlinx.serialization

**Estimated Time**: 35 minutes
**Difficulty**: Beginner-Intermediate
**Prerequisites**: Lessons 5.1-5.4 (HTTP, Ktor setup, routing, parameters)

---

## 📖 Topic Introduction

You've been using JSON in your API without really understanding what's happening behind the scenes. When you write `call.receive<Book>()` or `call.respond(book)`, magic happens: Kotlin objects transform into JSON text and back.

In this lesson, you'll learn:
- How JSON serialization actually works
- Advanced `@Serializable` annotations
- Custom serializers for special types (dates, enums, etc.)
- Handling nullable and optional fields
- Polymorphic serialization (base classes and inheritance)
- Error handling for malformed JSON

By the end, you'll have complete control over how your API handles JSON data!

---

## 💡 The Concept: What Is Serialization?

### The Translation Analogy

Imagine you have a letter written in English, and you need to send it to someone who only reads Spanish.

**Serialization** = Translating English → Spanish
```
Kotlin Object → JSON Text
```

**Deserialization** = Translating Spanish → English
```
JSON Text → Kotlin Object
```

### Why Do We Need It?

**Problem**: Kotlin objects only exist in memory on your server. How do you send them over the internet?

**Solution**: Convert them to a **text format** (JSON) that any programming language can understand.

```kotlin
// This exists only in Kotlin's memory
val book = Book(id = 1, title = "1984", author = "Orwell")

// This can be sent over HTTP to any client
val json = """{"id":1,"title":"1984","author":"Orwell"}"""
```

### JSON Basics Refresher

**JSON** (JavaScript Object Notation) is a text format for data:

```json
{
  "id": 1,
  "title": "1984",
  "author": "George Orwell",
  "year": 1949,
  "inStock": true,
  "price": 12.99,
  "tags": ["fiction", "dystopia"],
  "publisher": null
}
```

**Supported types:**
- **Numbers**: `42`, `3.14`
- **Strings**: `"hello"`
- **Booleans**: `true`, `false`
- **null**: `null`
- **Arrays**: `[1, 2, 3]`
- **Objects**: `{"key": "value"}`

---

## 🔧 The @Serializable Annotation

### Basic Usage

```kotlin
import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: Int
)
```

**What @Serializable does:**
1. Generates a **serializer** for the class at compile time
2. Knows how to convert each field to/from JSON
3. Works automatically with Ktor's `call.receive()` and `call.respond()`

### What Gets Serialized?

```kotlin
@Serializable
data class User(
    val id: Int,           // ✅ Serialized
    val name: String,      // ✅ Serialized
    var age: Int           // ✅ Serialized (var or val doesn't matter)
) {
    val isAdult: Boolean   // ❌ NOT serialized (not in constructor)
        get() = age >= 18

    fun greet() {          // ❌ NOT serialized (functions never are)
        println("Hello!")
    }
}
```

**Rule**: Only properties in the **primary constructor** are serialized.

---

## 🎨 Customizing Field Names

### Using @SerialName

Sometimes your Kotlin naming doesn't match the JSON format you need:

```kotlin
@Serializable
data class ApiUser(
    val id: Int,
    @SerialName("user_name")
    val userName: String,      // JSON: "user_name", Kotlin: userName
    @SerialName("email_address")
    val emailAddress: String,  // JSON: "email_address", Kotlin: emailAddress
    @SerialName("created_at")
    val createdAt: String      // JSON: "created_at", Kotlin: createdAt
)
```

**JSON representation:**
```json
{
  "id": 1,
  "user_name": "alice",
  "email_address": "alice@example.com",
  "created_at": "2024-11-13"
}
```

**Why use @SerialName?**
- ✅ Match external API naming conventions (snake_case vs camelCase)
- ✅ Keep Kotlin code idiomatic (camelCase)
- ✅ Avoid breaking changes when refactoring

---

## 🔄 Handling Nullable and Optional Fields

### Nullable Fields

```kotlin
@Serializable
data class Book(
    val id: Int,
    val title: String,
    val isbn: String?  // Can be null
)
```

**JSON examples:**
```json
// Valid: isbn present
{"id": 1, "title": "1984", "isbn": "978-0451524935"}

// Valid: isbn is null
{"id": 2, "title": "Brave New World", "isbn": null}

// Valid: isbn omitted (treated as null)
{"id": 3, "title": "Fahrenheit 451"}
```

### Default Values

```kotlin
@Serializable
data class User(
    val id: Int,
    val name: String,
    val role: String = "user",        // Default if not in JSON
    val isActive: Boolean = true,     // Default if not in JSON
    val metadata: Map<String, String> = emptyMap()
)
```

**JSON examples:**
```json
// Minimal JSON (uses defaults)
{"id": 1, "name": "Alice"}
// Results in: role="user", isActive=true, metadata={}

// Override defaults
{"id": 2, "name": "Bob", "role": "admin", "isActive": false}
```

### Required vs Optional

```kotlin
@Serializable
data class CreateBookRequest(
    val title: String,           // REQUIRED (no default, not nullable)
    val author: String,          // REQUIRED
    val year: Int? = null,       // OPTIONAL (nullable with default)
    val isbn: String? = null     // OPTIONAL
)
```

---

## 📅 Custom Serializers for Special Types

### Problem: Dates and Times

`LocalDateTime` is not supported by default:

```kotlin
@Serializable
data class Event(
    val id: Int,
    val name: String,
    val date: LocalDateTime  // ❌ Error: LocalDateTime not serializable
)
```

### Solution: Custom Serializer

**Step 1: Create the serializer**

```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}
```

**Step 2: Use it in your data class**

```kotlin
@Serializable
data class Event(
    val id: Int,
    val name: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val date: LocalDateTime
)
```

**JSON result:**
```json
{
  "id": 1,
  "name": "Kotlin Conference",
  "date": "2024-11-13T15:30:00"
}
```

### Simplified: Using @Contextual

For types you use frequently, register them globally:

```kotlin
// In your Application.kt
install(ContentNegotiation) {
    json(Json {
        serializersModule = SerializersModule {
            contextual(LocalDateTime::class, LocalDateTimeSerializer)
        }
    })
}

// In your data class
@Serializable
data class Event(
    val id: Int,
    val name: String,
    @Contextual
    val date: LocalDateTime  // No need to specify serializer
)
```

---

## 🎭 Enums and Sealed Classes

### Enum Serialization

```kotlin
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    ARCHIVED
}

@Serializable
data class Task(
    val id: Int,
    val title: String,
    val status: TaskStatus  // Automatically serialized as string
)
```

**JSON:**
```json
{
  "id": 1,
  "title": "Fix bug",
  "status": "IN_PROGRESS"
}
```

### Custom Enum Serialization

Sometimes you want custom enum values:

```kotlin
@Serializable(with = TaskStatusSerializer::class)
enum class TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    ARCHIVED
}

object TaskStatusSerializer : KSerializer<TaskStatus> {
    override val descriptor =
        PrimitiveSerialDescriptor("TaskStatus", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TaskStatus) {
        val statusString = when (value) {
            TaskStatus.PENDING -> "pending"
            TaskStatus.IN_PROGRESS -> "in_progress"
            TaskStatus.COMPLETED -> "completed"
            TaskStatus.ARCHIVED -> "archived"
        }
        encoder.encodeString(statusString)
    }

    override fun deserialize(decoder: Decoder): TaskStatus {
        return when (decoder.decodeString()) {
            "pending" -> TaskStatus.PENDING
            "in_progress" -> TaskStatus.IN_PROGRESS
            "completed" -> TaskStatus.COMPLETED
            "archived" -> TaskStatus.ARCHIVED
            else -> throw SerializationException("Unknown status")
        }
    }
}
```

### Polymorphic Serialization (Inheritance)

```kotlin
@Serializable
sealed class Notification {
    abstract val id: Int
    abstract val timestamp: String
}

@Serializable
@SerialName("email")
data class EmailNotification(
    override val id: Int,
    override val timestamp: String,
    val recipient: String,
    val subject: String
) : Notification()

@Serializable
@SerialName("sms")
data class SmsNotification(
    override val id: Int,
    override val timestamp: String,
    val phoneNumber: String,
    val message: String
) : Notification()

@Serializable
data class NotificationList(
    val notifications: List<Notification>
)
```

**JSON with type discrimination:**
```json
{
  "notifications": [
    {
      "type": "email",
      "id": 1,
      "timestamp": "2024-11-13T10:00:00",
      "recipient": "alice@example.com",
      "subject": "Welcome"
    },
    {
      "type": "sms",
      "id": 2,
      "timestamp": "2024-11-13T10:05:00",
      "phoneNumber": "+1234567890",
      "message": "Your code is 123456"
    }
  ]
}
```

---

## 🛠️ JSON Configuration Options

Configure how kotlinx.serialization behaves:

```kotlin
install(ContentNegotiation) {
    json(Json {
        // Pretty print (indented JSON)
        prettyPrint = true

        // Ignore unknown keys in JSON
        ignoreUnknownKeys = true

        // Allow trailing commas
        isLenient = true

        // Encode defaults (include fields with default values)
        encodeDefaults = false

        // Allow special floating point values
        allowSpecialFloatingPointValues = true

        // Use alternative names for class discriminator
        classDiscriminator = "type"
    })
}
```

**Example of prettyPrint:**

```kotlin
// prettyPrint = false (default)
{"id":1,"title":"1984","author":"George Orwell"}

// prettyPrint = true
{
  "id": 1,
  "title": "1984",
  "author": "George Orwell"
}
```

---

## 🔍 Handling JSON Errors

### Catching Deserialization Errors

```kotlin
post("/books") {
    try {
        val book = call.receive<Book>()
        val created = BookStorage.add(book)
        call.respond(HttpStatusCode.Created, created)
    } catch (e: SerializationException) {
        call.respond(
            HttpStatusCode.BadRequest,
            mapOf(
                "error" to "Invalid JSON",
                "message" to (e.message ?: "Malformed request body")
            )
        )
    } catch (e: ContentTransformationException) {
        call.respond(
            HttpStatusCode.BadRequest,
            mapOf("error" to "Failed to parse request body")
        )
    }
}
```

### Common Errors and Solutions

**1. Missing required field:**
```json
// Error: Missing "title"
{"id": 1, "author": "Orwell"}
```
**Solution**: Either make field nullable or provide default value

**2. Wrong type:**
```json
// Error: "year" is a string, not a number
{"id": 1, "title": "1984", "year": "1949"}
```
**Solution**: Use correct JSON types or create custom serializer

**3. Unknown fields:**
```json
// Extra field "publisher"
{"id": 1, "title": "1984", "publisher": "Penguin"}
```
**Solution**: Set `ignoreUnknownKeys = true` in JSON config

---

## 💻 Complete Example: Blog Post API

Let's build a complete example with custom serializers:

```kotlin
package com.example.models

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Custom LocalDateTime serializer
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}

// Blog post status enum
@Serializable
enum class PostStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED
}

// Author info
@Serializable
data class Author(
    val id: Int,
    val name: String,
    val email: String
)

// Blog post with custom serialization
@Serializable
data class BlogPost(
    val id: Int,
    val title: String,
    val content: String,
    val author: Author,
    val status: PostStatus = PostStatus.DRAFT,
    val tags: List<String> = emptyList(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val publishedAt: LocalDateTime? = null
)

// Request for creating a post
@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String,
    val authorId: Int,
    val tags: List<String> = emptyList(),
    val publish: Boolean = false
)

// Response wrapper
@Serializable
data class PostResponse(
    val success: Boolean,
    val post: BlogPost? = null,
    val message: String? = null
)
```

### Routes Using the Models

```kotlin
fun Route.blogRoutes() {
    route("/posts") {
        // Get all posts
        get {
            val posts = BlogStorage.getAll()
            call.respond(posts)
        }

        // Create post
        post {
            try {
                val request = call.receive<CreatePostRequest>()

                // Validate
                if (request.title.isBlank() || request.content.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        PostResponse(
                            success = false,
                            message = "Title and content are required"
                        )
                    )
                    return@post
                }

                // Create the post
                val now = LocalDateTime.now()
                val post = BlogPost(
                    id = BlogStorage.nextId(),
                    title = request.title,
                    content = request.content,
                    author = AuthorStorage.getById(request.authorId)
                        ?: return@post call.respond(
                            HttpStatusCode.BadRequest,
                            PostResponse(
                                success = false,
                                message = "Author not found"
                            )
                        ),
                    status = if (request.publish) PostStatus.PUBLISHED else PostStatus.DRAFT,
                    tags = request.tags,
                    createdAt = now,
                    updatedAt = now,
                    publishedAt = if (request.publish) now else null
                )

                BlogStorage.add(post)

                call.respond(
                    HttpStatusCode.Created,
                    PostResponse(success = true, post = post)
                )
            } catch (e: SerializationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    PostResponse(
                        success = false,
                        message = "Invalid JSON: ${e.message}"
                    )
                )
            }
        }
    }
}
```

### Testing

```bash
# Create a blog post
curl -X POST http://localhost:8080/posts \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Getting Started with Kotlin",
    "content": "Kotlin is an amazing language...",
    "authorId": 1,
    "tags": ["kotlin", "programming", "tutorial"],
    "publish": true
  }'
```

**Response:**
```json
{
  "success": true,
  "post": {
    "id": 1,
    "title": "Getting Started with Kotlin",
    "content": "Kotlin is an amazing language...",
    "author": {
      "id": 1,
      "name": "Alice",
      "email": "alice@example.com"
    },
    "status": "PUBLISHED",
    "tags": ["kotlin", "programming", "tutorial"],
    "createdAt": "2024-11-13T15:30:00",
    "updatedAt": "2024-11-13T15:30:00",
    "publishedAt": "2024-11-13T15:30:00"
  }
}
```

---

## 🎯 Exercise: Product Catalog with Variants

Create a product catalog API with these requirements:

### Requirements

1. **Product** model with:
   - Basic info (id, name, description)
   - Price (use Double)
   - Category (enum: ELECTRONICS, CLOTHING, BOOKS, FOOD)
   - Created/updated timestamps (use LocalDateTime)
   - Variants (list of ProductVariant)

2. **ProductVariant** model with:
   - SKU (stock keeping unit)
   - Size or other attribute
   - Stock quantity
   - Price override (nullable)

3. **Create endpoint** to add products with variants
4. **Handle errors** for invalid JSON
5. **Custom serializer** for timestamps

### Starter Code

```kotlin
enum class ProductCategory {
    ELECTRONICS,
    CLOTHING,
    BOOKS,
    FOOD
}

// TODO: Add @Serializable and implement models
data class ProductVariant(
    val sku: String,
    val attribute: String,  // e.g., "Size: Large", "Color: Red"
    val stockQuantity: Int,
    val priceOverride: Double? = null
)

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val basePrice: Double,
    val category: ProductCategory,
    val variants: List<ProductVariant>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

// TODO: Create request model
// TODO: Implement routes
```

---

## ✅ Solution & Explanation

```kotlin
package com.example.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

// Reuse LocalDateTimeSerializer from earlier

@Serializable
enum class ProductCategory {
    ELECTRONICS,
    CLOTHING,
    BOOKS,
    FOOD
}

@Serializable
data class ProductVariant(
    val sku: String,
    val attribute: String,
    val stockQuantity: Int,
    val priceOverride: Double? = null
)

@Serializable
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val basePrice: Double,
    val category: ProductCategory,
    val variants: List<ProductVariant> = emptyList(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime
)

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String,
    val basePrice: Double,
    val category: ProductCategory,
    val variants: List<ProductVariant> = emptyList()
)

@Serializable
data class ProductResponse(
    val success: Boolean,
    val product: Product? = null,
    val message: String? = null
)

// Routes
fun Route.productRoutes() {
    route("/products") {
        post {
            try {
                val request = call.receive<CreateProductRequest>()

                // Validation
                if (request.name.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ProductResponse(
                            success = false,
                            message = "Product name is required"
                        )
                    )
                    return@post
                }

                if (request.basePrice <= 0) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ProductResponse(
                            success = false,
                            message = "Price must be positive"
                        )
                    )
                    return@post
                }

                val now = LocalDateTime.now()
                val product = Product(
                    id = ProductStorage.nextId(),
                    name = request.name,
                    description = request.description,
                    basePrice = request.basePrice,
                    category = request.category,
                    variants = request.variants,
                    createdAt = now,
                    updatedAt = now
                )

                ProductStorage.add(product)

                call.respond(
                    HttpStatusCode.Created,
                    ProductResponse(success = true, product = product)
                )
            } catch (e: SerializationException) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ProductResponse(
                        success = false,
                        message = "Invalid JSON: ${e.message}"
                    )
                )
            }
        }
    }
}
```

### Testing

```bash
curl -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "T-Shirt",
    "description": "100% Cotton T-Shirt",
    "basePrice": 19.99,
    "category": "CLOTHING",
    "variants": [
      {
        "sku": "TS-RED-S",
        "attribute": "Red, Small",
        "stockQuantity": 50
      },
      {
        "sku": "TS-BLUE-L",
        "attribute": "Blue, Large",
        "stockQuantity": 30,
        "priceOverride": 24.99
      }
    ]
  }'
```

---

## 📝 Lesson Checkpoint Quiz

### Question 1
What does the @Serializable annotation do?

A) Makes the class thread-safe
B) Generates code to convert the class to/from JSON at compile time
C) Validates that all fields are non-null
D) Encrypts the data before sending

---

### Question 2
Why would you use @SerialName("user_name") on a field?

A) To make the field required in JSON
B) To map a different JSON field name to your Kotlin property
C) To make the field private
D) To change the field type

---

### Question 3
What happens if you try to deserialize JSON with an unknown field and `ignoreUnknownKeys = false`?

A) The field is silently ignored
B) A SerializationException is thrown
C) The field is stored as a String
D) The entire object becomes null

---

## 🎯 Why This Matters

JSON serialization is the **universal translator** of web APIs. Every major API you use (GitHub, Stripe, Twitter) sends and receives JSON.

### What You've Mastered

✅ **Automatic serialization** with @Serializable
✅ **Custom field names** with @SerialName
✅ **Nullable and optional fields** with defaults
✅ **Custom serializers** for types like LocalDateTime
✅ **Enum serialization** for type-safe status codes
✅ **Error handling** for malformed JSON
✅ **JSON configuration** for different output formats

### Real-World Applications

- **Mobile apps** send JSON to your API
- **Frontend JavaScript** communicates via JSON
- **Third-party integrations** expect JSON
- **Database exports** often use JSON
- **Configuration files** use JSON

---

## 📚 Key Takeaways

✅ **@Serializable** makes a class convertible to/from JSON
✅ **Only primary constructor properties** are serialized
✅ **@SerialName** maps different JSON field names
✅ **Nullable types** (String?) allow missing fields
✅ **Default values** make fields optional in JSON
✅ **Custom serializers** handle special types (dates, custom formats)
✅ **SerializationException** catches JSON errors
✅ **Json { }** configuration controls output format

---

## 🔜 Next Steps

In **Lesson 5.6**, you'll learn:
- Database fundamentals (why in-memory storage isn't enough)
- SQL basics for backend developers
- Setting up Exposed (Kotlin SQL library)
- Creating database tables
- Basic queries (INSERT, SELECT)
- Connecting your API to a real database

---

## ✏️ Quiz Answer Key

**Question 1**: **B) Generates code to convert the class to/from JSON at compile time**

Explanation: @Serializable is a compile-time annotation that generates serializer code. The magic of `call.receive<Book>()` works because the serializer was generated at compile time.

---

**Question 2**: **B) To map a different JSON field name to your Kotlin property**

Explanation: @SerialName allows the JSON field name to differ from your Kotlin property name. Common when working with APIs that use snake_case while Kotlin uses camelCase.

---

**Question 3**: **B) A SerializationException is thrown**

Explanation: By default (ignoreUnknownKeys = false), extra fields cause an error. Set `ignoreUnknownKeys = true` in your JSON configuration to silently ignore them.

---

**Congratulations!** You now have complete control over JSON serialization in your Ktor API! 🎉

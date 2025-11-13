# Lesson 5.9: Request Validation & Error Handling

**Estimated Time**: 60 minutes

---

## Topic Introduction

You've built a beautiful API with repositories, services, and clean architecture. But what happens when someone sends invalid data? What if they try to create a book with an empty title, or a negative publication year, or an email that's not actually an email?

Without proper validation and error handling, your API becomes unreliable, insecure, and frustrating to use. In this lesson, you'll learn how to protect your application from bad data and communicate errors clearly to API consumers.

---

## The Concept

### The Bouncer Analogy

Think of validation as a bouncer at an exclusive club:

**Without a Bouncer (No Validation)**:
- Anyone can walk in wearing anything
- People without IDs get in
- The club becomes chaotic and unsafe
- Real customers have a bad experience

**With a Good Bouncer (Proper Validation)**:
- Checks ID at the door (presence validation)
- Verifies age requirements (range validation)
- Enforces dress code (format validation)
- Refuses entry politely with clear reasons (error messages)
- Only valid guests get inside

Your API needs these same checks to maintain quality and security.

### Why Validation Matters

**1. Security**: Prevents injection attacks, buffer overflows, and malicious input
**2. Data Integrity**: Ensures your database stays clean and consistent
**3. User Experience**: Provides clear, actionable feedback about what went wrong
**4. Business Logic**: Enforces rules like "email must be unique" or "price must be positive"

### Types of Validation

| Type | Example | Purpose |
|------|---------|---------|
| **Presence** | Title is required | Ensure critical fields aren't empty |
| **Format** | Email must match pattern | Verify data structure |
| **Range** | Age must be 13-120 | Enforce numeric boundaries |
| **Length** | Password must be 8+ chars | Control string sizes |
| **Uniqueness** | Email must be unique | Prevent duplicates |
| **Business Rules** | Publish date can't be future | Enforce domain logic |

### The Validation Layers

```
┌─────────────────────────────────────┐
│  Client (Optional Pre-validation)   │  ← Fast feedback, can be bypassed
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Route Layer                        │  ← Parse request, basic structure
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Service Layer (VALIDATION HERE)    │  ← Main validation logic
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Repository Layer                   │  ← Database constraints (last line)
└─────────────────────────────────────┘
```

**Key Principle**: Never trust client-side validation. Always validate on the server in the service layer.

---

## Building a Validation System

### Step 1: Define Custom Exception Types

Create a hierarchy of exceptions that represent different error conditions:

```kotlin
// src/main/kotlin/com/example/exceptions/ApiExceptions.kt
package com.example.exceptions

/**
 * Base class for all API exceptions
 */
sealed class ApiException(message: String) : Exception(message)

/**
 * Thrown when request data fails validation
 * HTTP Status: 400 Bad Request
 */
class ValidationException(
    message: String,
    val errors: Map<String, List<String>> = emptyMap()
) : ApiException(message)

/**
 * Thrown when a requested resource doesn't exist
 * HTTP Status: 404 Not Found
 */
class NotFoundException(
    message: String
) : ApiException(message)

/**
 * Thrown when trying to create a duplicate resource
 * HTTP Status: 409 Conflict
 */
class ConflictException(
    message: String
) : ApiException(message)

/**
 * Thrown when user lacks permission
 * HTTP Status: 403 Forbidden
 */
class ForbiddenException(
    message: String
) : ApiException(message)

/**
 * Thrown for authentication failures
 * HTTP Status: 401 Unauthorized
 */
class UnauthorizedException(
    message: String
) : ApiException(message)
```

### Step 2: Create Standardized Error Response Format

Consistent error responses make your API easier to consume:

```kotlin
// src/main/kotlin/com/example/models/ErrorResponse.kt
package com.example.models

import kotlinx.serialization.Serializable

/**
 * Standard error response structure
 */
@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val errors: Map<String, List<String>>? = null,
    val timestamp: String = java.time.LocalDateTime.now().toString()
)

/**
 * Standard success response wrapper
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: String? = null
)
```

### Step 3: Build a Validation Framework

Create reusable validation building blocks:

```kotlin
// src/main/kotlin/com/example/validation/Validator.kt
package com.example.validation

/**
 * Validation result that accumulates errors
 */
class ValidationResult {
    private val errors = mutableMapOf<String, MutableList<String>>()

    val isValid: Boolean
        get() = errors.isEmpty()

    val errorMap: Map<String, List<String>>
        get() = errors

    /**
     * Add an error for a specific field
     */
    fun addError(field: String, message: String) {
        errors.getOrPut(field) { mutableListOf() }.add(message)
    }

    /**
     * Merge errors from another validation result
     */
    fun merge(other: ValidationResult) {
        other.errors.forEach { (field, messages) ->
            errors.getOrPut(field) { mutableListOf() }.addAll(messages)
        }
    }
}

/**
 * Base validator class with common validation rules
 */
abstract class Validator<T> {

    protected val result = ValidationResult()

    /**
     * Execute validation and return result
     */
    abstract fun validate(value: T): ValidationResult

    /**
     * Validate that a string is not blank
     */
    protected fun validateRequired(field: String, value: String?, fieldName: String = field) {
        if (value.isNullOrBlank()) {
            result.addError(field, "$fieldName is required")
        }
    }

    /**
     * Validate string length
     */
    protected fun validateLength(
        field: String,
        value: String?,
        min: Int? = null,
        max: Int? = null,
        fieldName: String = field
    ) {
        if (value == null) return

        when {
            min != null && value.length < min ->
                result.addError(field, "$fieldName must be at least $min characters")
            max != null && value.length > max ->
                result.addError(field, "$fieldName must be at most $max characters")
        }
    }

    /**
     * Validate numeric range
     */
    protected fun validateRange(
        field: String,
        value: Int?,
        min: Int? = null,
        max: Int? = null,
        fieldName: String = field
    ) {
        if (value == null) return

        when {
            min != null && value < min ->
                result.addError(field, "$fieldName must be at least $min")
            max != null && value > max ->
                result.addError(field, "$fieldName must be at most $max")
        }
    }

    /**
     * Validate email format
     */
    protected fun validateEmail(field: String, value: String?, fieldName: String = field) {
        if (value == null) return

        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        if (!value.matches(emailRegex)) {
            result.addError(field, "$fieldName must be a valid email address")
        }
    }

    /**
     * Validate against regex pattern
     */
    protected fun validatePattern(
        field: String,
        value: String?,
        pattern: Regex,
        message: String,
        fieldName: String = field
    ) {
        if (value == null) return

        if (!value.matches(pattern)) {
            result.addError(field, message)
        }
    }

    /**
     * Add custom validation logic
     */
    protected fun validateCustom(field: String, condition: Boolean, message: String) {
        if (!condition) {
            result.addError(field, message)
        }
    }
}
```

### Step 4: Create Domain-Specific Validators

Now build validators for your specific models:

```kotlin
// src/main/kotlin/com/example/validation/BookValidator.kt
package com.example.validation

import com.example.models.CreateBookRequest

class BookValidator : Validator<CreateBookRequest>() {

    override fun validate(value: CreateBookRequest): ValidationResult {
        // Title validation
        validateRequired("title", value.title)
        validateLength("title", value.title, min = 1, max = 255)

        // Author validation
        validateRequired("author", value.author)
        validateLength("author", value.author, min = 1, max = 255)

        // Year validation
        validateRange("year", value.year, min = 1000, max = 2100)
        validateCustom(
            "year",
            value.year <= java.time.Year.now().value,
            "Publication year cannot be in the future"
        )

        // ISBN validation (optional but must be valid if provided)
        value.isbn?.let { isbn ->
            validatePattern(
                "isbn",
                isbn,
                "^(\\d{10}|\\d{13})$".toRegex(),
                "ISBN must be either 10 or 13 digits"
            )
        }

        return result
    }
}
```

### Step 5: Integrate Validation into Service Layer

Your service layer is the perfect place to validate:

```kotlin
// src/main/kotlin/com/example/services/BookService.kt
package com.example.services

import com.example.exceptions.ConflictException
import com.example.exceptions.NotFoundException
import com.example.exceptions.ValidationException
import com.example.models.Book
import com.example.models.CreateBookRequest
import com.example.models.UpdateBookRequest
import com.example.repositories.BookRepository
import com.example.validation.BookValidator

class BookService(
    private val bookRepository: BookRepository
) {
    private val validator = BookValidator()

    /**
     * Create a new book with validation
     */
    fun createBook(request: CreateBookRequest): Result<Book> {
        return try {
            // Step 1: Validate input
            val validationResult = validator.validate(request)
            if (!validationResult.isValid) {
                throw ValidationException(
                    "Validation failed",
                    validationResult.errorMap
                )
            }

            // Step 2: Check for duplicates (business rule)
            val existingBook = bookRepository.findByTitleAndAuthor(
                request.title,
                request.author
            )
            if (existingBook != null) {
                throw ConflictException(
                    "A book with title '${request.title}' by ${request.author} already exists"
                )
            }

            // Step 3: Create the book
            val book = Book(
                id = 0, // Will be assigned by database
                title = request.title.trim(),
                author = request.author.trim(),
                year = request.year,
                isbn = request.isbn?.trim()
            )

            val id = bookRepository.insert(book)
            val createdBook = bookRepository.getById(id)
                ?: throw RuntimeException("Failed to retrieve created book")

            Result.success(createdBook)

        } catch (e: ValidationException) {
            Result.failure(e)
        } catch (e: ConflictException) {
            Result.failure(e)
        } catch (e: Exception) {
            // Log unexpected errors
            println("Unexpected error creating book: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Update existing book with validation
     */
    fun updateBook(id: Int, request: UpdateBookRequest): Result<Book> {
        return try {
            // Validate existence
            val existing = bookRepository.getById(id)
                ?: throw NotFoundException("Book with id $id not found")

            // Validate input
            val validationResult = validator.validate(
                CreateBookRequest(
                    title = request.title,
                    author = request.author,
                    year = request.year,
                    isbn = request.isbn
                )
            )
            if (!validationResult.isValid) {
                throw ValidationException(
                    "Validation failed",
                    validationResult.errorMap
                )
            }

            // Update
            val updated = Book(
                id = id,
                title = request.title.trim(),
                author = request.author.trim(),
                year = request.year,
                isbn = request.isbn?.trim()
            )

            bookRepository.update(id, updated)
            val updatedBook = bookRepository.getById(id)!!

            Result.success(updatedBook)

        } catch (e: ValidationException) {
            Result.failure(e)
        } catch (e: NotFoundException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error updating book: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Delete book
     */
    fun deleteBook(id: Int): Result<Unit> {
        return try {
            val exists = bookRepository.getById(id)
            if (exists == null) {
                throw NotFoundException("Book with id $id not found")
            }

            bookRepository.delete(id)
            Result.success(Unit)

        } catch (e: NotFoundException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error deleting book: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Get all books (no validation needed)
     */
    fun getAllBooks(): Result<List<Book>> {
        return try {
            val books = bookRepository.getAll()
            Result.success(books)
        } catch (e: Exception) {
            println("Unexpected error fetching books: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Get book by ID with validation
     */
    fun getBookById(id: Int): Result<Book> {
        return try {
            val book = bookRepository.getById(id)
                ?: throw NotFoundException("Book with id $id not found")

            Result.success(book)
        } catch (e: NotFoundException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error fetching book: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }
}
```

### Step 6: Handle Errors in Routes with Status Plugins

Install Ktor's StatusPages plugin for global error handling:

```kotlin
// src/main/kotlin/com/example/plugins/ErrorHandling.kt
package com.example.plugins

import com.example.exceptions.*
import com.example.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureErrorHandling() {
    install(StatusPages) {
        // Handle validation errors
        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    message = cause.message ?: "Validation failed",
                    errors = cause.errors
                )
            )
        }

        // Handle not found errors
        exception<NotFoundException> { call, cause ->
            call.respond(
                HttpStatusCode.NotFound,
                ErrorResponse(
                    message = cause.message ?: "Resource not found"
                )
            )
        }

        // Handle conflict errors
        exception<ConflictException> { call, cause ->
            call.respond(
                HttpStatusCode.Conflict,
                ErrorResponse(
                    message = cause.message ?: "Resource conflict"
                )
            )
        }

        // Handle forbidden errors
        exception<ForbiddenException> { call, cause ->
            call.respond(
                HttpStatusCode.Forbidden,
                ErrorResponse(
                    message = cause.message ?: "Access forbidden"
                )
            )
        }

        // Handle unauthorized errors
        exception<UnauthorizedException> { call, cause ->
            call.respond(
                HttpStatusCode.Unauthorized,
                ErrorResponse(
                    message = cause.message ?: "Authentication required"
                )
            )
        }

        // Handle JSON parsing errors
        exception<kotlinx.serialization.SerializationException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    message = "Invalid JSON format: ${cause.message}"
                )
            )
        }

        // Handle unexpected errors (never expose internal details)
        exception<Throwable> { call, cause ->
            // Log the full error for debugging
            call.application.environment.log.error("Unhandled exception", cause)

            // Return generic error to client
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    message = "An unexpected error occurred. Please try again later."
                )
            )
        }
    }
}
```

Configure the plugin in your application:

```kotlin
// src/main/kotlin/com/example/Application.kt
package com.example

import com.example.plugins.*
import com.example.repositories.BookRepositoryImpl
import com.example.routes.bookRoutes
import com.example.services.BookService
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

    // Install error handling FIRST
    configureErrorHandling()

    // Initialize database
    DatabaseFactory.init()

    // Create dependencies
    val bookRepository = BookRepositoryImpl()
    val bookService = BookService(bookRepository)

    // Configure routes
    routing {
        bookRoutes(bookService)
    }
}
```

### Step 7: Simplify Routes with Error Handling

Now your routes become incredibly clean:

```kotlin
// src/main/kotlin/com/example/routes/BookRoutes.kt
package com.example.routes

import com.example.models.ApiResponse
import com.example.models.CreateBookRequest
import com.example.models.UpdateBookRequest
import com.example.services.BookService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.bookRoutes(bookService: BookService) {
    route("/api/books") {

        // Get all books
        get {
            bookService.getAllBooks()
                .onSuccess { books ->
                    call.respond(ApiResponse(data = books))
                }
                .onFailure { error ->
                    throw error  // Let StatusPages handle it
                }
        }

        // Get book by ID
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ValidationException("Invalid book ID")

            bookService.getBookById(id)
                .onSuccess { book ->
                    call.respond(ApiResponse(data = book))
                }
                .onFailure { error ->
                    throw error
                }
        }

        // Create new book
        post {
            val request = call.receive<CreateBookRequest>()

            bookService.createBook(request)
                .onSuccess { book ->
                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(
                            data = book,
                            message = "Book created successfully"
                        )
                    )
                }
                .onFailure { error ->
                    throw error
                }
        }

        // Update book
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ValidationException("Invalid book ID")
            val request = call.receive<UpdateBookRequest>()

            bookService.updateBook(id, request)
                .onSuccess { book ->
                    call.respond(ApiResponse(
                        data = book,
                        message = "Book updated successfully"
                    ))
                }
                .onFailure { error ->
                    throw error
                }
        }

        // Delete book
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ValidationException("Invalid book ID")

            bookService.deleteBook(id)
                .onSuccess {
                    call.respond(ApiResponse<Unit>(
                        message = "Book deleted successfully"
                    ))
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

### The Validation Flow

```
1. Route receives request
   ↓
2. Route deserializes JSON → CreateBookRequest
   ↓
3. Route calls bookService.createBook(request)
   ↓
4. Service validates with BookValidator
   ↓
5a. INVALID → throw ValidationException
    └→ StatusPages catches it
       └→ Returns 400 with error details

5b. VALID → Continue to business logic
   ↓
6. Service checks business rules (duplicates, etc.)
   ↓
7a. RULE VIOLATION → throw ConflictException
    └→ StatusPages catches it
       └→ Returns 409 with message

7b. RULES PASS → Continue to repository
   ↓
8. Repository saves to database
   ↓
9. Return Result.success(book)
   ↓
10. Route sends 201 Created response
```

### Error Response Examples

**Validation Error (400 Bad Request)**:
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "title": ["Title is required"],
    "year": [
      "Publication year cannot be in the future",
      "Year must be at least 1000"
    ],
    "isbn": ["ISBN must be either 10 or 13 digits"]
  },
  "timestamp": "2025-01-15T10:30:45.123"
}
```

**Not Found Error (404)**:
```json
{
  "success": false,
  "message": "Book with id 999 not found",
  "timestamp": "2025-01-15T10:31:22.456"
}
```

**Conflict Error (409)**:
```json
{
  "success": false,
  "message": "A book with title 'The Hobbit' by J.R.R. Tolkien already exists",
  "timestamp": "2025-01-15T10:32:10.789"
}
```

### Key Design Patterns

1. **Exception Hierarchy**: Sealed class ensures type safety and exhaustive handling
2. **Validation Result Accumulation**: Collects all errors instead of failing on first
3. **Reusable Validators**: Abstract base class with common validation logic
4. **Service Layer Validation**: Keeps routes thin, concentrates logic
5. **Result<T> Pattern**: Type-safe success/failure handling
6. **Global Error Handling**: StatusPages plugin provides consistent error responses
7. **Never Expose Internals**: Generic messages for unexpected errors, detailed logs server-side

---

## Exercise: Product Validation System

Build a complete validation system for a product catalog API.

### Requirements

1. **Product Model**:
   - Name (required, 1-200 chars)
   - Description (optional, max 1000 chars)
   - Price (required, must be > 0, max 2 decimal places)
   - Category (required, must be one of: Electronics, Clothing, Books, Food, Other)
   - SKU (required, unique, format: 3 letters + 6 digits, e.g., "ABC123456")
   - Stock quantity (required, must be >= 0)
   - Active (boolean, defaults to true)

2. **Validation Rules**:
   - Price must be positive and not exceed 1,000,000
   - Category must match allowed values exactly (case-sensitive)
   - SKU must be unique across all products
   - Cannot set stock to negative
   - Cannot update inactive products (business rule)

3. **Error Handling**:
   - Return 400 for validation errors with field-specific messages
   - Return 404 when product doesn't exist
   - Return 409 for duplicate SKU
   - Return 422 for business rule violations (updating inactive product)

### Your Task

Implement:
1. `Product` and `CreateProductRequest` data classes
2. `ProductValidator` with all validation rules
3. `ProductService` with create, update, and deactivate methods
4. Custom exception for business rule violations (`BusinessRuleException`)
5. Error handling configuration
6. Routes with proper error responses

Test with these cases:
- Valid product creation
- Missing required fields
- Invalid price (negative, too many decimals)
- Invalid category
- Invalid SKU format
- Duplicate SKU
- Updating inactive product

### Starter Code

```kotlin
// Models
@Serializable
data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String,
    val sku: String,
    val stockQuantity: Int,
    val active: Boolean = true
)

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String? = null,
    val price: Double,
    val category: String,
    val sku: String,
    val stockQuantity: Int
)

@Serializable
data class UpdateProductRequest(
    val name: String,
    val description: String? = null,
    val price: Double,
    val category: String,
    val stockQuantity: Int
)

// TODO: Implement ProductValidator
// TODO: Implement ProductService
// TODO: Implement routes
```

---

## Solution

### Complete Product Validation System

```kotlin
// src/main/kotlin/com/example/exceptions/BusinessRuleException.kt
package com.example.exceptions

/**
 * Thrown when a business rule is violated
 * HTTP Status: 422 Unprocessable Entity
 */
class BusinessRuleException(
    message: String
) : ApiException(message)
```

```kotlin
// src/main/kotlin/com/example/validation/ProductValidator.kt
package com.example.validation

import com.example.models.CreateProductRequest

class ProductValidator : Validator<CreateProductRequest>() {

    companion object {
        val ALLOWED_CATEGORIES = setOf(
            "Electronics",
            "Clothing",
            "Books",
            "Food",
            "Other"
        )

        // SKU format: 3 uppercase letters + 6 digits
        val SKU_PATTERN = "^[A-Z]{3}\\d{6}$".toRegex()
    }

    override fun validate(value: CreateProductRequest): ValidationResult {
        // Name validation
        validateRequired("name", value.name)
        validateLength("name", value.name, min = 1, max = 200)

        // Description validation (optional)
        validateLength("description", value.description, max = 1000)

        // Price validation
        validateCustom(
            "price",
            value.price > 0,
            "Price must be greater than 0"
        )
        validateCustom(
            "price",
            value.price <= 1_000_000,
            "Price must not exceed 1,000,000"
        )
        // Check decimal places
        val decimalPlaces = value.price.toString()
            .substringAfter('.', "")
            .length
        validateCustom(
            "price",
            decimalPlaces <= 2,
            "Price must have at most 2 decimal places"
        )

        // Category validation
        validateRequired("category", value.category)
        validateCustom(
            "category",
            value.category in ALLOWED_CATEGORIES,
            "Category must be one of: ${ALLOWED_CATEGORIES.joinToString(", ")}"
        )

        // SKU validation
        validateRequired("sku", value.sku)
        validatePattern(
            "sku",
            value.sku,
            SKU_PATTERN,
            "SKU must be 3 uppercase letters followed by 6 digits (e.g., ABC123456)"
        )

        // Stock quantity validation
        validateCustom(
            "stockQuantity",
            value.stockQuantity >= 0,
            "Stock quantity must be 0 or greater"
        )

        return result
    }
}
```

```kotlin
// src/main/kotlin/com/example/services/ProductService.kt
package com.example.services

import com.example.exceptions.*
import com.example.models.CreateProductRequest
import com.example.models.Product
import com.example.models.UpdateProductRequest
import com.example.repositories.ProductRepository
import com.example.validation.ProductValidator

class ProductService(
    private val productRepository: ProductRepository
) {
    private val validator = ProductValidator()

    fun createProduct(request: CreateProductRequest): Result<Product> {
        return try {
            // Validate input
            val validationResult = validator.validate(request)
            if (!validationResult.isValid) {
                throw ValidationException(
                    "Validation failed",
                    validationResult.errorMap
                )
            }

            // Check SKU uniqueness
            val existingProduct = productRepository.findBySku(request.sku)
            if (existingProduct != null) {
                throw ConflictException(
                    "A product with SKU '${request.sku}' already exists"
                )
            }

            // Create product
            val product = Product(
                id = 0,
                name = request.name.trim(),
                description = request.description?.trim(),
                price = request.price,
                category = request.category,
                sku = request.sku.uppercase(),
                stockQuantity = request.stockQuantity,
                active = true
            )

            val id = productRepository.insert(product)
            val createdProduct = productRepository.getById(id)!!

            Result.success(createdProduct)

        } catch (e: ApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error creating product: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    fun updateProduct(id: Int, request: UpdateProductRequest): Result<Product> {
        return try {
            // Check existence
            val existing = productRepository.getById(id)
                ?: throw NotFoundException("Product with id $id not found")

            // Business rule: Cannot update inactive products
            if (!existing.active) {
                throw BusinessRuleException(
                    "Cannot update inactive product. Reactivate it first."
                )
            }

            // Validate input (reuse validator with CreateProductRequest)
            val validationResult = validator.validate(
                CreateProductRequest(
                    name = request.name,
                    description = request.description,
                    price = request.price,
                    category = request.category,
                    sku = existing.sku,  // SKU doesn't change in update
                    stockQuantity = request.stockQuantity
                )
            )
            if (!validationResult.isValid) {
                throw ValidationException(
                    "Validation failed",
                    validationResult.errorMap
                )
            }

            // Update product (keep existing SKU and active status)
            val updated = Product(
                id = id,
                name = request.name.trim(),
                description = request.description?.trim(),
                price = request.price,
                category = request.category,
                sku = existing.sku,
                stockQuantity = request.stockQuantity,
                active = existing.active
            )

            productRepository.update(id, updated)
            val updatedProduct = productRepository.getById(id)!!

            Result.success(updatedProduct)

        } catch (e: ApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error updating product: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    fun deactivateProduct(id: Int): Result<Product> {
        return try {
            val existing = productRepository.getById(id)
                ?: throw NotFoundException("Product with id $id not found")

            if (!existing.active) {
                throw BusinessRuleException("Product is already inactive")
            }

            val deactivated = existing.copy(active = false)
            productRepository.update(id, deactivated)
            val updatedProduct = productRepository.getById(id)!!

            Result.success(updatedProduct)

        } catch (e: ApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error deactivating product: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    fun reactivateProduct(id: Int): Result<Product> {
        return try {
            val existing = productRepository.getById(id)
                ?: throw NotFoundException("Product with id $id not found")

            if (existing.active) {
                throw BusinessRuleException("Product is already active")
            }

            val reactivated = existing.copy(active = true)
            productRepository.update(id, reactivated)
            val updatedProduct = productRepository.getById(id)!!

            Result.success(updatedProduct)

        } catch (e: ApiException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error reactivating product: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    fun getAllProducts(): Result<List<Product>> {
        return try {
            val products = productRepository.getAll()
            Result.success(products)
        } catch (e: Exception) {
            println("Unexpected error fetching products: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    fun getProductById(id: Int): Result<Product> {
        return try {
            val product = productRepository.getById(id)
                ?: throw NotFoundException("Product with id $id not found")

            Result.success(product)
        } catch (e: NotFoundException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Unexpected error fetching product: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }
}
```

```kotlin
// Update ErrorHandling plugin to include BusinessRuleException
exception<BusinessRuleException> { call, cause ->
    call.respond(
        HttpStatusCode.UnprocessableEntity,
        ErrorResponse(
            message = cause.message ?: "Business rule violation"
        )
    )
}
```

```kotlin
// src/main/kotlin/com/example/routes/ProductRoutes.kt
package com.example.routes

import com.example.exceptions.ValidationException
import com.example.models.ApiResponse
import com.example.models.CreateProductRequest
import com.example.models.UpdateProductRequest
import com.example.services.ProductService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.productRoutes(productService: ProductService) {
    route("/api/products") {

        get {
            productService.getAllProducts()
                .onSuccess { products ->
                    call.respond(ApiResponse(data = products))
                }
                .onFailure { error ->
                    throw error
                }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ValidationException("Invalid product ID")

            productService.getProductById(id)
                .onSuccess { product ->
                    call.respond(ApiResponse(data = product))
                }
                .onFailure { error ->
                    throw error
                }
        }

        post {
            val request = call.receive<CreateProductRequest>()

            productService.createProduct(request)
                .onSuccess { product ->
                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(
                            data = product,
                            message = "Product created successfully"
                        )
                    )
                }
                .onFailure { error ->
                    throw error
                }
        }

        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ValidationException("Invalid product ID")
            val request = call.receive<UpdateProductRequest>()

            productService.updateProduct(id, request)
                .onSuccess { product ->
                    call.respond(ApiResponse(
                        data = product,
                        message = "Product updated successfully"
                    ))
                }
                .onFailure { error ->
                    throw error
                }
        }

        post("/{id}/deactivate") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ValidationException("Invalid product ID")

            productService.deactivateProduct(id)
                .onSuccess { product ->
                    call.respond(ApiResponse(
                        data = product,
                        message = "Product deactivated successfully"
                    ))
                }
                .onFailure { error ->
                    throw error
                }
        }

        post("/{id}/reactivate") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ValidationException("Invalid product ID")

            productService.reactivateProduct(id)
                .onSuccess { product ->
                    call.respond(ApiResponse(
                        data = product,
                        message = "Product reactivated successfully"
                    ))
                }
                .onFailure { error ->
                    throw error
                }
        }
    }
}
```

### Test Cases

**Test 1: Valid Product Creation**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Wireless Mouse",
    "description": "Ergonomic wireless mouse",
    "price": 29.99,
    "category": "Electronics",
    "sku": "ELC123456",
    "stockQuantity": 100
  }'
```

Response (201 Created):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "Wireless Mouse",
    "description": "Ergonomic wireless mouse",
    "price": 29.99,
    "category": "Electronics",
    "sku": "ELC123456",
    "stockQuantity": 100,
    "active": true
  },
  "message": "Product created successfully"
}
```

**Test 2: Validation Errors**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "price": -10.999,
    "category": "InvalidCategory",
    "sku": "invalid",
    "stockQuantity": -5
  }'
```

Response (400 Bad Request):
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "name": ["Name is required"],
    "price": [
      "Price must be greater than 0",
      "Price must have at most 2 decimal places"
    ],
    "category": [
      "Category must be one of: Electronics, Clothing, Books, Food, Other"
    ],
    "sku": [
      "SKU must be 3 uppercase letters followed by 6 digits (e.g., ABC123456)"
    ],
    "stockQuantity": ["Stock quantity must be 0 or greater"]
  },
  "timestamp": "2025-01-15T14:23:11.456"
}
```

**Test 3: Duplicate SKU**
```bash
# Try creating product with same SKU
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Different Product",
    "price": 50.00,
    "category": "Electronics",
    "sku": "ELC123456",
    "stockQuantity": 50
  }'
```

Response (409 Conflict):
```json
{
  "success": false,
  "message": "A product with SKU 'ELC123456' already exists",
  "timestamp": "2025-01-15T14:25:33.789"
}
```

**Test 4: Updating Inactive Product**
```bash
# First deactivate
curl -X POST http://localhost:8080/api/products/1/deactivate

# Then try to update
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "price": 39.99,
    "category": "Electronics",
    "stockQuantity": 75
  }'
```

Response (422 Unprocessable Entity):
```json
{
  "success": false,
  "message": "Cannot update inactive product. Reactivate it first.",
  "timestamp": "2025-01-15T14:27:45.012"
}
```

---

## Solution Explanation

### Why This Design Works

**1. Layered Validation**:
- **Format validation** in `ProductValidator` (structure, types, ranges)
- **Business rules** in `ProductService` (uniqueness, state transitions)
- **Database constraints** as last line of defense

**2. Accumulated Errors**:
Instead of failing on the first error, the validator collects all validation failures and returns them together. This provides better UX—users can fix multiple issues at once.

**3. Clear Error Taxonomy**:
- `ValidationException` (400): Bad input format
- `NotFoundException` (404): Resource doesn't exist
- `ConflictException` (409): Duplicate resource
- `BusinessRuleException` (422): Valid format but violates business logic

**4. Separation of Concerns**:
- **Validator**: Focuses purely on data format and constraints
- **Service**: Enforces business rules and orchestrates operations
- **Routes**: Handle HTTP concerns only
- **StatusPages**: Centralized error response formatting

**5. Type Safety with Result<T>**:
Using Kotlin's `Result<T>` type provides compile-time guarantees that errors are handled, preventing unhandled exceptions from reaching users.

---

## Why This Matters

### Real-World Impact

**Without Validation**:
- 😱 Your database fills with junk data
- 🔓 SQL injection and XSS vulnerabilities
- 😤 Users get cryptic database errors
- 🐛 Debugging becomes nightmare (bad data everywhere)
- 💸 Data cleanup costs escalate

**With Proper Validation**:
- ✅ Clean, trustworthy data
- 🔒 Protection against attacks
- 😊 Clear, actionable error messages
- 🐞 Easier debugging (problems caught early)
- 💰 Lower maintenance costs

### Professional Best Practices

1. **Validate Early, Validate Often**: Don't trust any external input
2. **Be Specific**: "Email is required" is better than "Invalid input"
3. **Accumulate Errors**: Show all problems, not just the first one
4. **Log Server Errors**: Never expose internal details to clients
5. **Use Proper Status Codes**: 400 vs 422 vs 409 have distinct meanings
6. **Test Edge Cases**: Empty strings, null values, extreme numbers

---

## Checkpoint Quiz

Test your understanding of validation and error handling:

### Question 1
Where should business rule validation (like "email must be unique") primarily occur?

A) Client-side JavaScript
B) Route layer
C) Service layer
D) Repository layer

### Question 2
What HTTP status code should you return for a validation error like "email format is invalid"?

A) 200 OK
B) 400 Bad Request
C) 422 Unprocessable Entity
D) 500 Internal Server Error

### Question 3
What's the main benefit of accumulating validation errors instead of failing on the first error?

A) It makes the code run faster
B) It reduces server load
C) Users can fix all issues at once, improving UX
D) It's required by REST standards

### Question 4
What should you do when an unexpected exception occurs in production?

A) Return the full stack trace to the client for debugging
B) Log the detailed error server-side, return a generic message to client
C) Ignore it and return 200 OK
D) Crash the server to alert administrators

### Question 5
Why use a sealed class hierarchy for exceptions (ApiException subclasses)?

A) It makes the code look more professional
B) It enables type-safe, exhaustive error handling
C) It's required by Ktor
D) It improves performance

---

## Quiz Answers

**Question 1: C) Service layer**

The service layer is the perfect place for business rule validation:
- Route layer handles HTTP parsing
- Service layer knows business logic ("email must be unique" requires checking database)
- Repository layer is just data access

Client-side validation is for UX but can be bypassed, so never trust it alone.

---

**Question 2: B) 400 Bad Request**

HTTP status code guidelines:
- **400 Bad Request**: Invalid input format (malformed JSON, invalid email format)
- **422 Unprocessable Entity**: Valid format but violates business rules
- **409 Conflict**: Duplicate resource
- **500 Internal Server Error**: Unexpected server error

For format validation like email pattern matching, use 400.

---

**Question 3: C) Users can fix all issues at once, improving UX**

Compare these experiences:

**Fail-fast approach**:
1. Submit form → "Name is required"
2. Add name, submit → "Email is invalid"
3. Fix email, submit → "Password too short"
4. 😤 Three round trips!

**Accumulated errors**:
1. Submit form → Shows all three errors at once
2. Fix all issues, submit → Success!
3. 😊 One round trip!

---

**Question 4: B) Log the detailed error server-side, return a generic message to client**

Security and UX best practice:

```kotlin
exception<Throwable> { call, cause ->
    // Log full details for developers
    call.application.environment.log.error("Error", cause)

    // Return generic message to client
    call.respond(
        HttpStatusCode.InternalServerError,
        ErrorResponse(message = "An unexpected error occurred")
    )
}
```

Never expose stack traces or internal details—they can reveal vulnerabilities.

---

**Question 5: B) It enables type-safe, exhaustive error handling**

Using a sealed class hierarchy gives you compile-time safety:

```kotlin
sealed class ApiException : Exception()
class ValidationException : ApiException()
class NotFoundException : ApiException()

// The compiler ensures you handle all cases
when (exception) {
    is ValidationException -> // handle
    is NotFoundException -> // handle
    // Compiler error if you forget a case!
}
```

This prevents bugs from unhandled exception types.

---

## What You've Learned

✅ Why validation and error handling are critical for security and UX
✅ How to build a reusable validation framework with accumulating errors
✅ Where to validate (client vs server, which backend layer)
✅ How to create a clear exception hierarchy for different error types
✅ How to use Ktor's StatusPages plugin for centralized error handling
✅ How to provide helpful error messages without exposing internals
✅ How to use proper HTTP status codes (400, 404, 409, 422, 500)
✅ How to integrate validation into clean architecture (service layer)

---

## Next Steps

In the next lesson, we'll build on this foundation by implementing **user authentication with password hashing**. You'll learn how to:
- Securely store passwords using bcrypt
- Validate registration data (email format, password strength)
- Handle authentication errors properly
- Prevent common security vulnerabilities

The validation patterns you learned today will be essential for validating user credentials safely!

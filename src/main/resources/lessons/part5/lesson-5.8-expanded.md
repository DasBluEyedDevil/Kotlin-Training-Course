# Lesson 5.8: The Repository Pattern - Organizing Your Data Layer

**Estimated Time**: 50 minutes
**Difficulty**: Intermediate
**Prerequisites**: Lessons 5.6-5.7 (Database operations with Exposed)

---

## 📖 Topic Introduction

Your API is growing. You have routes calling database code directly. What happens when:
- You need to switch from H2 to PostgreSQL?
- You want to add caching?
- You need to write tests without a real database?
- Multiple routes need the same complex query?

The **Repository Pattern** solves these problems by creating a clean separation between your business logic and data access.

In this lesson, you'll learn:
- What the Repository Pattern is and why it matters
- Clean Architecture principles
- Separating concerns: Routes → Services → Repositories
- Making your code testable
- Interface-based design
- Real-world project structure

---

## 💡 The Concept: What Is the Repository Pattern?

### The Librarian Analogy

Imagine you're at a library:

**Without Repository Pattern** = You go into the back room, search through filing systems, understand the Dewey Decimal System, find the book yourself.
- You need to know how the library organizes books
- Every visitor needs this knowledge
- Changing the organization system breaks everything

**With Repository Pattern** = You ask the librarian: "I need books about Kotlin."
- Librarian knows how to find books (that's their job)
- You don't care if books are organized by author, title, or year
- Library can reorganize without affecting visitors

### In Code Terms

```kotlin
// WITHOUT Repository Pattern (Bad!)
fun Route.bookRoutes() {
    get("/books") {
        // Routes directly access database
        val books = transaction {
            Books.selectAll().map { /* ... */ }
        }
        call.respond(books)
    }
}

// WITH Repository Pattern (Good!)
fun Route.bookRoutes() {
    val bookRepository = BookRepository()

    get("/books") {
        // Routes ask repository for data
        val books = bookRepository.getAll()
        call.respond(books)
    }
}
```

**Benefits:**
- ✅ Routes don't know about database details
- ✅ Easy to change database implementation
- ✅ Can test routes without a database
- ✅ Reusable data access logic

---

## 🏗️ Clean Architecture Layers

### The Three-Layer Architecture

```
┌─────────────────────────────────────┐
│   Presentation Layer (Routes)       │  ← What users interact with
│   - HTTP handling                   │
│   - Request/Response                │
│   - Validation                      │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│   Business Logic Layer (Services)   │  ← What your app does
│   - Use cases                       │
│   - Business rules                  │
│   - Orchestration                   │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│   Data Layer (Repositories)         │  ← How data is stored/retrieved
│   - Database access                 │
│   - External APIs                   │
│   - Caching                         │
└─────────────────────────────────────┘
```

### Dependency Flow

**Key principle**: Outer layers depend on inner layers, never the reverse.

```
Routes → Services → Repositories → Database
  ↓         ↓            ↓
HTTP    Business      Data
        Logic       Storage
```

---

## 📝 Step 1: Define Repository Interfaces

Create interfaces in the domain/service layer:

```kotlin
// src/main/kotlin/com/example/repositories/BookRepository.kt
package com.example.repositories

import com.example.models.Book

interface BookRepository {
    fun getAll(): List<Book>
    fun getById(id: Int): Book?
    fun insert(book: Book): Int
    fun update(id: Int, book: Book): Boolean
    fun delete(id: Int): Boolean
    fun findByAuthor(author: String): List<Book>
    fun search(query: String): List<Book>
}
```

**Why interfaces?**
- ✅ Defines what operations are available
- ✅ Routes depend on interface, not implementation
- ✅ Easy to create mock implementations for testing
- ✅ Can swap implementations (in-memory, SQL, NoSQL, etc.)

---

## 💻 Step 2: Implement Repository

```kotlin
// src/main/kotlin/com/example/repositories/BookRepositoryImpl.kt
package com.example.repositories

import com.example.database.tables.Books
import com.example.models.Book
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class BookRepositoryImpl : BookRepository {

    override fun getAll(): List<Book> = transaction {
        Books.selectAll()
            .orderBy(Books.title)
            .map { rowToBook(it) }
    }

    override fun getById(id: Int): Book? = transaction {
        Books.selectAll()
            .where { Books.id eq id }
            .map { rowToBook(it) }
            .singleOrNull()
    }

    override fun insert(book: Book): Int = transaction {
        Books.insert {
            it[title] = book.title
            it[author] = book.author
            it[year] = book.year
            it[isbn] = book.isbn
        }[Books.id]
    }

    override fun update(id: Int, book: Book): Boolean = transaction {
        Books.update({ Books.id eq id }) {
            it[title] = book.title
            it[author] = book.author
            it[year] = book.year
            it[isbn] = book.isbn
        } > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        Books.deleteWhere { Books.id eq id } > 0
    }

    override fun findByAuthor(author: String): List<Book> = transaction {
        Books.selectAll()
            .where { Books.author eq author }
            .map { rowToBook(it) }
    }

    override fun search(query: String): List<Book> = transaction {
        Books.selectAll()
            .where {
                (Books.title like "%$query%") or
                (Books.author like "%$query%")
            }
            .map { rowToBook(it) }
    }

    private fun rowToBook(row: ResultRow): Book {
        return Book(
            id = row[Books.id],
            title = row[Books.title],
            author = row[Books.author],
            year = row[Books.year],
            isbn = row[Books.isbn]
        )
    }
}
```

**Key points:**
- All database logic is encapsulated
- `transaction { }` calls are hidden from callers
- Easy to understand: each method does one thing
- Private helper method for mapping

---

## 🎯 Step 3: Service Layer (Business Logic)

Create a service that uses repositories:

```kotlin
// src/main/kotlin/com/example/services/BookService.kt
package com.example.services

import com.example.models.*
import com.example.repositories.BookRepository

class BookService(
    private val bookRepository: BookRepository
) {

    fun getAllBooks(): List<Book> {
        return bookRepository.getAll()
    }

    fun getBook(id: Int): Book? {
        return bookRepository.getById(id)
    }

    fun createBook(request: CreateBookRequest): Result<Book> {
        // Validation
        if (request.title.isBlank()) {
            return Result.failure(ValidationException("Title is required"))
        }

        if (request.author.isBlank()) {
            return Result.failure(ValidationException("Author is required"))
        }

        // Check for duplicates
        val existing = bookRepository.findByAuthor(request.author)
            .find { it.title.equals(request.title, ignoreCase = true) }

        if (existing != null) {
            return Result.failure(DuplicateException("Book already exists"))
        }

        // Create book
        val book = Book(
            id = 0,  // Will be assigned by database
            title = request.title,
            author = request.author,
            year = request.year,
            isbn = request.isbn
        )

        val id = bookRepository.insert(book)
        val created = bookRepository.getById(id)
            ?: return Result.failure(Exception("Failed to retrieve created book"))

        return Result.success(created)
    }

    fun updateBook(id: Int, request: UpdateBookRequest): Result<Book> {
        // Check if exists
        val existing = bookRepository.getById(id)
            ?: return Result.failure(NotFoundException("Book not found"))

        // Build updated book
        val updated = existing.copy(
            title = request.title ?: existing.title,
            author = request.author ?: existing.author,
            year = request.year ?: existing.year,
            isbn = request.isbn ?: existing.isbn
        )

        // Update in database
        val success = bookRepository.update(id, updated)

        return if (success) {
            Result.success(updated)
        } else {
            Result.failure(Exception("Failed to update book"))
        }
    }

    fun deleteBook(id: Int): Result<Unit> {
        val exists = bookRepository.getById(id) != null
        if (!exists) {
            return Result.failure(NotFoundException("Book not found"))
        }

        val deleted = bookRepository.delete(id)

        return if (deleted) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to delete book"))
        }
    }

    fun searchBooks(query: String): List<Book> {
        if (query.isBlank()) {
            return emptyList()
        }
        return bookRepository.search(query)
    }
}

// Custom exceptions
class ValidationException(message: String) : Exception(message)
class NotFoundException(message: String) : Exception(message)
class DuplicateException(message: String) : Exception(message)
```

**Service layer responsibilities:**
- ✅ Business logic and validation
- ✅ Orchestrating multiple repositories
- ✅ Error handling
- ✅ Use cases (what the app does)

---

## 🌐 Step 4: Updated Routes Using Services

```kotlin
// src/main/kotlin/com/example/plugins/Routing.kt
package com.example.plugins

import com.example.models.*
import com.example.services.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    bookService: BookService
) {
    routing {
        bookRoutes(bookService)
    }
}

fun Route.bookRoutes(bookService: BookService) {
    route("/api/books") {
        // Get all books
        get {
            val books = bookService.getAllBooks()
            call.respond(ApiResponse(success = true, data = books))
        }

        // Get single book
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Book>(success = false, message = "Invalid ID")
                )

            val book = bookService.getBook(id)
            if (book == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse<Book>(success = false, message = "Book not found")
                )
            } else {
                call.respond(ApiResponse(success = true, data = book))
            }
        }

        // Create book
        post {
            try {
                val request = call.receive<CreateBookRequest>()

                bookService.createBook(request)
                    .onSuccess { book ->
                        call.respond(
                            HttpStatusCode.Created,
                            ApiResponse(success = true, data = book)
                        )
                    }
                    .onFailure { error ->
                        when (error) {
                            is ValidationException -> call.respond(
                                HttpStatusCode.BadRequest,
                                ApiResponse<Book>(success = false, message = error.message)
                            )
                            is DuplicateException -> call.respond(
                                HttpStatusCode.Conflict,
                                ApiResponse<Book>(success = false, message = error.message)
                            )
                            else -> call.respond(
                                HttpStatusCode.InternalServerError,
                                ApiResponse<Book>(success = false, message = "Server error")
                            )
                        }
                    }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Book>(success = false, message = "Invalid request")
                )
            }
        }

        // Update book
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest)

            val request = call.receive<UpdateBookRequest>()

            bookService.updateBook(id, request)
                .onSuccess { book ->
                    call.respond(ApiResponse(success = true, data = book))
                }
                .onFailure { error ->
                    when (error) {
                        is NotFoundException -> call.respond(HttpStatusCode.NotFound)
                        else -> call.respond(HttpStatusCode.InternalServerError)
                    }
                }
        }

        // Delete book
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            bookService.deleteBook(id)
                .onSuccess {
                    call.respond(
                        HttpStatusCode.OK,
                        ApiResponse<Unit>(success = true, message = "Book deleted")
                    )
                }
                .onFailure { error ->
                    when (error) {
                        is NotFoundException -> call.respond(HttpStatusCode.NotFound)
                        else -> call.respond(HttpStatusCode.InternalServerError)
                    }
                }
        }

        // Search
        get("/search") {
            val query = call.request.queryParameters["q"] ?: ""
            val results = bookService.searchBooks(query)
            call.respond(ApiResponse(success = true, data = results))
        }
    }
}
```

**Notice:**
- Routes are thin (no business logic!)
- Just handle HTTP concerns (parameters, status codes, responses)
- Call service methods
- Map service errors to HTTP status codes

---

## 🔧 Step 5: Dependency Injection (Manual)

Wire everything together:

```kotlin
// src/main/kotlin/com/example/Application.kt
package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.*
import com.example.repositories.*
import com.example.services.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    // Initialize database
    DatabaseFactory.init()

    // Create repositories
    val bookRepository: BookRepository = BookRepositoryImpl()

    // Create services
    val bookService = BookService(bookRepository)

    // Configure plugins
    configureSerialization()
    configureRouting(bookService)
}
```

**Dependency flow:**
```
Database → Repository → Service → Routes
```

---

## 🧪 Making Code Testable

### Why This Architecture Enables Testing

```kotlin
// src/test/kotlin/com/example/services/BookServiceTest.kt
package com.example.services

import com.example.models.*
import com.example.repositories.BookRepository
import kotlin.test.*

class BookServiceTest {

    // Mock repository (no real database!)
    class MockBookRepository : BookRepository {
        private val books = mutableMapOf<Int, Book>()
        private var nextId = 1

        override fun getAll() = books.values.toList()
        override fun getById(id: Int) = books[id]

        override fun insert(book: Book): Int {
            val id = nextId++
            books[id] = book.copy(id = id)
            return id
        }

        override fun update(id: Int, book: Book): Boolean {
            if (id !in books) return false
            books[id] = book.copy(id = id)
            return true
        }

        override fun delete(id: Int) = books.remove(id) != null

        override fun findByAuthor(author: String) =
            books.values.filter { it.author == author }

        override fun search(query: String) =
            books.values.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.author.contains(query, ignoreCase = true)
            }
    }

    @Test
    fun `create book with valid data should succeed`() {
        val repository = MockBookRepository()
        val service = BookService(repository)

        val request = CreateBookRequest(
            title = "Test Book",
            author = "Test Author",
            year = 2024
        )

        val result = service.createBook(request)

        assertTrue(result.isSuccess)
        assertEquals("Test Book", result.getOrNull()?.title)
    }

    @Test
    fun `create book with blank title should fail`() {
        val repository = MockBookRepository()
        val service = BookService(repository)

        val request = CreateBookRequest(
            title = "",
            author = "Test Author",
            year = 2024
        )

        val result = service.createBook(request)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
    }

    @Test
    fun `delete non-existent book should fail`() {
        val repository = MockBookRepository()
        val service = BookService(repository)

        val result = service.deleteBook(999)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is NotFoundException)
    }
}
```

**Benefits of testable architecture:**
- ✅ No database needed for tests
- ✅ Fast execution (milliseconds)
- ✅ Reliable (no network/disk issues)
- ✅ Easy to simulate edge cases

---

## 📂 Complete Project Structure

```
src/main/kotlin/com/example/
├── Application.kt                    # Entry point, DI setup
├── database/
│   ├── DatabaseFactory.kt           # Database initialization
│   └── tables/
│       ├── Books.kt                  # Table definitions
│       └── Reviews.kt
├── models/
│   ├── Book.kt                       # Domain models
│   ├── Review.kt
│   ├── Requests.kt                   # API request models
│   └── Responses.kt                  # API response models
├── repositories/
│   ├── BookRepository.kt             # Interface
│   ├── BookRepositoryImpl.kt         # Implementation
│   ├── ReviewRepository.kt
│   └── ReviewRepositoryImpl.kt
├── services/
│   ├── BookService.kt                # Business logic
│   ├── ReviewService.kt
│   └── Exceptions.kt                 # Custom exceptions
└── plugins/
    ├── Routing.kt                    # HTTP routes
    └── Serialization.kt              # JSON config
```

---

## 🎯 Exercise: Implement User Repository & Service

Create a complete User system with the repository pattern:

### Requirements

1. **UserRepository interface** with:
   - getAll, getById, getByUsername, getByEmail
   - insert, update, delete
   - search(query)

2. **UserRepositoryImpl** with Exposed

3. **UserService** with:
   - Business logic: username must be unique, email must be valid
   - Password requirements (min 8 chars)
   - createUser, updateUser, deleteUser, searchUsers

4. **Routes** using the service

---

## ✅ Solution & Explanation

```kotlin
// Repository Interface
interface UserRepository {
    fun getAll(): List<User>
    fun getById(id: Int): User?
    fun getByUsername(username: String): User?
    fun getByEmail(email: String): User?
    fun insert(user: User): Int
    fun update(id: Int, user: User): Boolean
    fun delete(id: Int): Boolean
    fun search(query: String): List<User>
}

// Repository Implementation
class UserRepositoryImpl : UserRepository {
    override fun getAll(): List<User> = transaction {
        Users.selectAll()
            .orderBy(Users.username)
            .map { rowToUser(it) }
    }

    override fun getById(id: Int): User? = transaction {
        Users.selectAll()
            .where { Users.id eq id }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    override fun getByUsername(username: String): User? = transaction {
        Users.selectAll()
            .where { Users.username eq username }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    override fun getByEmail(email: String): User? = transaction {
        Users.selectAll()
            .where { Users.email eq email }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    override fun insert(user: User): Int = transaction {
        Users.insert {
            it[username] = user.username
            it[email] = user.email
            it[passwordHash] = user.passwordHash
            it[createdAt] = LocalDateTime.now()
        }[Users.id]
    }

    override fun update(id: Int, user: User): Boolean = transaction {
        Users.update({ Users.id eq id }) {
            it[email] = user.email
            it[passwordHash] = user.passwordHash
        } > 0
    }

    override fun delete(id: Int): Boolean = transaction {
        Users.deleteWhere { Users.id eq id } > 0
    }

    override fun search(query: String): List<User> = transaction {
        Users.selectAll()
            .where {
                (Users.username like "%$query%") or
                (Users.email like "%$query%")
            }
            .map { rowToUser(it) }
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            username = row[Users.username],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            createdAt = row[Users.createdAt].toString()
        )
    }
}

// Service
class UserService(
    private val userRepository: UserRepository
) {

    fun createUser(request: CreateUserRequest): Result<User> {
        // Validate username
        if (request.username.length < 3) {
            return Result.failure(ValidationException("Username must be at least 3 characters"))
        }

        // Validate email
        if (!request.email.contains("@")) {
            return Result.failure(ValidationException("Invalid email address"))
        }

        // Validate password
        if (request.password.length < 8) {
            return Result.failure(ValidationException("Password must be at least 8 characters"))
        }

        // Check for duplicates
        if (userRepository.getByUsername(request.username) != null) {
            return Result.failure(DuplicateException("Username already exists"))
        }

        if (userRepository.getByEmail(request.email) != null) {
            return Result.failure(DuplicateException("Email already exists"))
        }

        // Hash password (simplified - use BCrypt in production!)
        val passwordHash = request.password.hashCode().toString()

        val user = User(
            id = 0,
            username = request.username,
            email = request.email,
            passwordHash = passwordHash,
            createdAt = ""
        )

        val id = userRepository.insert(user)
        val created = userRepository.getById(id)
            ?: return Result.failure(Exception("Failed to create user"))

        return Result.success(created)
    }

    fun getAllUsers(): List<User> {
        return userRepository.getAll()
    }

    fun getUser(id: Int): User? {
        return userRepository.getById(id)
    }

    fun deleteUser(id: Int): Result<Unit> {
        if (userRepository.getById(id) == null) {
            return Result.failure(NotFoundException("User not found"))
        }

        val deleted = userRepository.delete(id)
        return if (deleted) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Failed to delete user"))
        }
    }
}

// Routes
fun Route.userRoutes(userService: UserService) {
    route("/api/users") {
        get {
            val users = userService.getAllUsers()
            call.respond(ApiResponse(success = true, data = users))
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val user = userService.getUser(id)
            if (user == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(ApiResponse(success = true, data = user))
            }
        }

        post {
            val request = call.receive<CreateUserRequest>()

            userService.createUser(request)
                .onSuccess { user ->
                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(success = true, data = user)
                    )
                }
                .onFailure { error ->
                    when (error) {
                        is ValidationException -> call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<User>(success = false, message = error.message)
                        )
                        is DuplicateException -> call.respond(
                            HttpStatusCode.Conflict,
                            ApiResponse<User>(success = false, message = error.message)
                        )
                        else -> call.respond(HttpStatusCode.InternalServerError)
                    }
                }
        }

        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            userService.deleteUser(id)
                .onSuccess { call.respond(HttpStatusCode.OK) }
                .onFailure { call.respond(HttpStatusCode.NotFound) }
        }
    }
}
```

---

## 📝 Lesson Checkpoint Quiz

### Question 1
What is the main purpose of the Repository Pattern?

A) To make code run faster
B) To separate data access logic from business logic
C) To add more files to the project
D) To make database queries prettier

---

### Question 2
In a three-layer architecture, which layer should contain validation logic?

A) Repository layer (data access)
B) Service layer (business logic)
C) Route layer (presentation)
D) Database layer

---

### Question 3
Why use interfaces for repositories?

A) They make code longer and more impressive
B) They're required by Kotlin
C) They enable testing with mock implementations and allow swapping implementations
D) They make the code run faster

---

## 🎯 Why This Matters

The Repository Pattern is **fundamental** to professional backend development. Every major framework and architecture uses it.

### What You've Mastered

✅ **Separation of concerns**: Each layer has one job
✅ **Testability**: Can test without databases
✅ **Flexibility**: Easy to change implementations
✅ **Clean code**: Business logic separate from data access
✅ **Scalability**: Easy to add features
✅ **Maintainability**: Changes isolated to specific layers

### Real-World Usage

- **Spring Boot**: Repository interfaces are core
- **Android**: Room database uses repository pattern
- **iOS**: Core Data uses similar patterns
- **Enterprise apps**: Standard architecture

---

## 📚 Key Takeaways

✅ **Repository Pattern** abstracts data access
✅ **Interfaces** define contracts, implementations provide details
✅ **Three layers**: Routes → Services → Repositories
✅ **Services** contain business logic and validation
✅ **Routes** handle HTTP concerns only
✅ **Testable** without real databases
✅ **Scalable** and maintainable architecture

---

## 🔜 Next Steps

In **Lesson 5.9**, you'll learn:
- Advanced request validation
- Error handling strategies
- Status pages plugin
- Custom error responses
- Validation libraries

---

## ✏️ Quiz Answer Key

**Question 1**: **B) To separate data access logic from business logic**

Explanation: The Repository Pattern creates a clean separation between how data is stored/retrieved and how it's used in business logic. This makes code more maintainable and testable.

---

**Question 2**: **B) Service layer (business logic)**

Explanation: Validation is business logic. Services validate data, enforce rules, and coordinate operations. Routes just handle HTTP, repositories just access data.

---

**Question 3**: **C) They enable testing with mock implementations and allow swapping implementations**

Explanation: Interfaces let you create mock repositories for testing (no database needed) and easily swap implementations (e.g., SQL to NoSQL, add caching) without changing dependent code.

---

**Congratulations!** You now understand professional backend architecture! 🎉

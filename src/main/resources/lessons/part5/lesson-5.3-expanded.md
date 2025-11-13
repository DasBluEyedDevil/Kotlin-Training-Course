# Lesson 5.3: Routing Fundamentals - Building Your First Endpoints

**Estimated Time**: 40 minutes
**Difficulty**: Beginner-Intermediate
**Prerequisites**: Lessons 5.1-5.2 (HTTP fundamentals, Ktor setup)

---

## 📖 Topic Introduction

In the previous lesson, you created a basic server that returns plain text. Now it's time to build something more realistic: a **complete REST API** for managing a collection of books.

In this lesson, you'll:
- Organize routes into logical groups
- Return JSON data instead of plain text
- Implement all CRUD operations (Create, Read, Update, Delete)
- Use proper HTTP methods and status codes
- Store data in memory (temporarily, before we learn databases)

By the end, you'll have a fully functional Books API that behaves like a real-world backend!

---

## 💡 The Concept: RESTful Resource Management

### The Library Catalog Analogy

Think of your API as a library's card catalog system:

**GET /books** = "Show me all books in the catalog"
- Like looking at the entire catalog drawer

**GET /books/42** = "Show me the details of book #42"
- Like pulling out a specific card

**POST /books** = "Add a new book to the catalog"
- Like creating a new catalog card

**PUT /books/42** = "Update all information for book #42"
- Like replacing an entire catalog card

**DELETE /books/42** = "Remove book #42 from the catalog"
- Like throwing away a catalog card

### What Makes an API "RESTful"?

**REST** (Representational State Transfer) is a set of conventions for building APIs:

1. **Resources are nouns**: `/books`, not `/getBooks`
2. **HTTP methods are verbs**: Use GET/POST/PUT/DELETE, not custom action names
3. **Stateless**: Each request contains all needed information
4. **Standard status codes**: 200 for success, 404 for not found, etc.
5. **JSON for data**: Structured, language-independent format

---

## 🗂️ Organizing Routes

As your API grows, putting all routes in one function becomes messy. Let's learn to organize them properly.

### Route Organization Patterns

**Pattern 1: Flat (What We Did Before)**
```kotlin
routing {
    get("/books") { }
    post("/books") { }
    get("/books/{id}") { }
    put("/books/{id}") { }
    delete("/books/{id}") { }
}
```

**Pattern 2: Grouped by Resource (Better!)**
```kotlin
routing {
    route("/books") {
        get { }           // GET /books
        post { }          // POST /books
        get("/{id}") { }  // GET /books/{id}
        put("/{id}") { }  // PUT /books/{id}
        delete("/{id}") { } // DELETE /books/{id}
    }
}
```

**Pattern 3: Separate Files by Resource (Best for Large Projects)**
```kotlin
// BookRoutes.kt
fun Route.bookRoutes() {
    route("/books") {
        get { }
        post { }
        // etc.
    }
}

// Application.kt
routing {
    bookRoutes()
    userRoutes()
    orderRoutes()
}
```

For this lesson, we'll use **Pattern 2** (grouped routes).

---

## 💻 Code: Building a Complete Books API

Let's build a complete CRUD API step by step.

### Step 1: Define the Data Model

Create `src/main/kotlin/com/example/models/Book.kt`:

```kotlin
package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: Int,
    val isbn: String? = null  // Optional field
)
```

**Understanding the annotations:**

- **@Serializable**: Tells kotlinx.serialization this class can be converted to/from JSON
- **data class**: Automatically generates `equals()`, `hashCode()`, `toString()`, `copy()`
- **String?**: The `?` makes `isbn` nullable (optional)

### Step 2: Create an In-Memory Data Store

Create `src/main/kotlin/com/example/data/BookStorage.kt`:

```kotlin
package com.example.data

import com.example.models.Book
import java.util.concurrent.atomic.AtomicInteger

// Singleton object (only one instance exists)
object BookStorage {
    // Thread-safe ID counter
    private val idCounter = AtomicInteger(0)

    // Mutable list to store books (in memory)
    private val books = mutableListOf<Book>()

    // Initialize with some sample data
    init {
        books.add(Book(nextId(), "1984", "George Orwell", 1949, "978-0451524935"))
        books.add(Book(nextId(), "Brave New World", "Aldous Huxley", 1932, "978-0060850524"))
        books.add(Book(nextId(), "Fahrenheit 451", "Ray Bradbury", 1953, "978-1451673319"))
    }

    // Get all books
    fun getAll(): List<Book> = books.toList()

    // Get book by ID
    fun getById(id: Int): Book? = books.find { it.id == id }

    // Add new book
    fun add(book: Book): Book {
        val newBook = book.copy(id = nextId())
        books.add(newBook)
        return newBook
    }

    // Update existing book
    fun update(id: Int, book: Book): Boolean {
        val index = books.indexOfFirst { it.id == id }
        return if (index != -1) {
            books[index] = book.copy(id = id)
            true
        } else {
            false
        }
    }

    // Delete book
    fun delete(id: Int): Boolean {
        return books.removeIf { it.id == id }
    }

    // Generate next ID
    private fun nextId() = idCounter.incrementAndGet()
}
```

**Key concepts:**

- **object BookStorage**: Singleton pattern (only one instance)
- **AtomicInteger**: Thread-safe counter for generating IDs
- **init { }**: Code that runs when the object is first accessed
- **find { }**: Returns first matching item or `null`
- **indexOfFirst { }**: Returns index of first match or `-1`
- **removeIf { }**: Removes all items matching the predicate

### Step 3: Define Request/Response Models

Create `src/main/kotlin/com/example/models/BookRequest.kt`:

```kotlin
package com.example.models

import kotlinx.serialization.Serializable

// For creating a new book (no ID needed)
@Serializable
data class CreateBookRequest(
    val title: String,
    val author: String,
    val year: Int,
    val isbn: String? = null
)

// For updating a book (optional fields)
@Serializable
data class UpdateBookRequest(
    val title: String? = null,
    val author: String? = null,
    val year: Int? = null,
    val isbn: String? = null
)

// Standard API response wrapper
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)
```

**Why separate request models?**

1. **Security**: Clients shouldn't send IDs when creating (server assigns them)
2. **Flexibility**: Updates can be partial (only changed fields)
3. **Clarity**: Clear what data is expected

### Step 4: Build the Routes

Now for the main event! Update `src/main/kotlin/com/example/plugins/Routing.kt`:

```kotlin
package com.example.plugins

import com.example.data.BookStorage
import com.example.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Root endpoint
        get("/") {
            call.respondText("Books API is running! Visit /api/books")
        }

        // API routes
        route("/api") {
            bookRoutes()
        }
    }
}

// Book routes grouped together
fun Route.bookRoutes() {
    route("/books") {
        // GET /api/books - List all books
        get {
            val books = BookStorage.getAll()
            call.respond(
                HttpStatusCode.OK,
                ApiResponse(success = true, data = books)
            )
        }

        // GET /api/books/{id} - Get specific book
        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Book>(
                        success = false,
                        message = "Invalid book ID"
                    )
                )
                return@get
            }

            val book = BookStorage.getById(id)

            if (book == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse<Book>(
                        success = false,
                        message = "Book not found"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(success = true, data = book)
                )
            }
        }

        // POST /api/books - Create new book
        post {
            val request = call.receive<CreateBookRequest>()

            // Simple validation
            if (request.title.isBlank() || request.author.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Book>(
                        success = false,
                        message = "Title and author are required"
                    )
                )
                return@post
            }

            val newBook = Book(
                id = 0, // Will be replaced by storage
                title = request.title,
                author = request.author,
                year = request.year,
                isbn = request.isbn
            )

            val created = BookStorage.add(newBook)

            call.respond(
                HttpStatusCode.Created,
                ApiResponse(
                    success = true,
                    data = created,
                    message = "Book created successfully"
                )
            )
        }

        // PUT /api/books/{id} - Update book
        put("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Book>(
                        success = false,
                        message = "Invalid book ID"
                    )
                )
                return@put
            }

            val request = call.receive<CreateBookRequest>()

            val updatedBook = Book(
                id = id,
                title = request.title,
                author = request.author,
                year = request.year,
                isbn = request.isbn
            )

            val success = BookStorage.update(id, updatedBook)

            if (success) {
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse(
                        success = true,
                        data = updatedBook,
                        message = "Book updated successfully"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse<Book>(
                        success = false,
                        message = "Book not found"
                    )
                )
            }
        }

        // DELETE /api/books/{id} - Delete book
        delete("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()

            if (id == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Unit>(
                        success = false,
                        message = "Invalid book ID"
                    )
                )
                return@delete
            }

            val success = BookStorage.delete(id)

            if (success) {
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse<Unit>(
                        success = true,
                        message = "Book deleted successfully"
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse<Unit>(
                        success = false,
                        message = "Book not found"
                    )
                )
            }
        }
    }
}
```

---

## 🔍 Code Breakdown

Let's analyze the key patterns:

### 1. Route Organization

```kotlin
route("/api") {
    bookRoutes()  // Extracted to separate function
}

fun Route.bookRoutes() {
    route("/books") {
        get { }      // /api/books
        get("/{id}") { }  // /api/books/123
    }
}
```

**Benefits:**
- Clear hierarchy
- Easy to add new routes
- Can move to separate files as project grows

### 2. Receiving Request Bodies

```kotlin
post {
    val request = call.receive<CreateBookRequest>()
    // request is now a Kotlin object!
}
```

- **call.receive<T>()**: Automatically parses JSON to Kotlin object
- Throws exception if JSON is invalid (we'll handle this in later lessons)

### 3. Responding with Status Codes

```kotlin
call.respond(HttpStatusCode.Created, data)  // 201
call.respond(HttpStatusCode.OK, data)       // 200
call.respond(HttpStatusCode.NotFound, error) // 404
```

**Common patterns:**
- **200 OK**: Successful GET/PUT
- **201 Created**: Successful POST (new resource)
- **204 No Content**: Successful DELETE (no body needed)
- **400 Bad Request**: Invalid input
- **404 Not Found**: Resource doesn't exist

### 4. Parameter Extraction and Validation

```kotlin
val id = call.parameters["id"]?.toIntOrNull()

if (id == null) {
    call.respond(HttpStatusCode.BadRequest, ...)
    return@get  // Exit early
}
```

**Key techniques:**
- **?.toIntOrNull()**: Safe conversion (null if not a number)
- **Early return**: If validation fails, respond and exit
- **@get/@post/@put/@delete**: Label for return statement

### 5. API Response Wrapper

```kotlin
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)
```

**Consistent responses:**
```json
{
  "success": true,
  "data": { "id": 1, "title": "1984" },
  "message": "Book created successfully"
}
```

---

## 🧪 Testing Your Complete API

### Test GET All Books

```bash
curl http://localhost:8080/api/books
```

**Expected Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "title": "1984",
      "author": "George Orwell",
      "year": 1949,
      "isbn": "978-0451524935"
    },
    {
      "id": 2,
      "title": "Brave New World",
      "author": "Aldous Huxley",
      "year": 1932,
      "isbn": "978-0060850524"
    }
  ]
}
```

### Test GET Single Book

```bash
curl http://localhost:8080/api/books/1
```

### Test CREATE New Book

```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Hobbit",
    "author": "J.R.R. Tolkien",
    "year": 1937,
    "isbn": "978-0547928227"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "data": {
    "id": 4,
    "title": "The Hobbit",
    "author": "J.R.R. Tolkien",
    "year": 1937,
    "isbn": "978-0547928227"
  },
  "message": "Book created successfully"
}
```

### Test UPDATE Book

```bash
curl -X PUT http://localhost:8080/api/books/4 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Hobbit: There and Back Again",
    "author": "J.R.R. Tolkien",
    "year": 1937
  }'
```

### Test DELETE Book

```bash
curl -X DELETE http://localhost:8080/api/books/4
```

### Test Error Cases

```bash
# Invalid ID (not a number)
curl http://localhost:8080/api/books/abc

# Non-existent book
curl http://localhost:8080/api/books/9999

# Empty title (validation error)
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title": "", "author": "Unknown", "year": 2024}'
```

---

## 🎯 Exercise: Extend the API

Add these features to your Books API:

### Exercise 1: Search by Title

Add a route that searches books by title (case-insensitive).

**Endpoint**: `GET /api/books/search?title=brave`

**Expected**: Return all books whose title contains "brave" (case-insensitive)

**Hints:**
```kotlin
get("/search") {
    val query = call.request.queryParameters["title"]
    // Use contains() with ignoreCase = true
}
```

### Exercise 2: Filter by Year

Add a route to get books published in a specific year range.

**Endpoint**: `GET /api/books/filter?minYear=1930&maxYear=1950`

**Expected**: Return books published between 1930 and 1950 (inclusive)

### Exercise 3: Get Books by Author

Add a route to get all books by a specific author.

**Endpoint**: `GET /api/books/author/{authorName}`

**Expected**: Return all books by that author (case-insensitive match)

### Exercise 4: Count Endpoint

Add a route that returns the total number of books.

**Endpoint**: `GET /api/books/count`

**Expected Response**:
```json
{
  "success": true,
  "data": { "count": 5 },
  "message": "Total books counted"
}
```

---

## ✅ Solution & Explanation

Here's the complete solution with all exercises:

```kotlin
fun Route.bookRoutes() {
    route("/books") {
        // Existing routes...
        get { /* ... */ }
        get("/{id}") { /* ... */ }
        post { /* ... */ }
        put("/{id}") { /* ... */ }
        delete("/{id}") { /* ... */ }

        // Exercise 1: Search by title
        get("/search") {
            val query = call.request.queryParameters["title"]

            if (query.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<List<Book>>(
                        success = false,
                        message = "Query parameter 'title' is required"
                    )
                )
                return@get
            }

            val results = BookStorage.getAll().filter {
                it.title.contains(query, ignoreCase = true)
            }

            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    data = results,
                    message = "Found ${results.size} book(s)"
                )
            )
        }

        // Exercise 2: Filter by year range
        get("/filter") {
            val minYear = call.request.queryParameters["minYear"]?.toIntOrNull()
            val maxYear = call.request.queryParameters["maxYear"]?.toIntOrNull()

            if (minYear == null || maxYear == null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<List<Book>>(
                        success = false,
                        message = "Both 'minYear' and 'maxYear' are required"
                    )
                )
                return@get
            }

            val results = BookStorage.getAll().filter {
                it.year in minYear..maxYear
            }

            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    data = results,
                    message = "Found ${results.size} book(s) published between $minYear and $maxYear"
                )
            )
        }

        // Exercise 3: Get books by author
        get("/author/{authorName}") {
            val authorName = call.parameters["authorName"]

            if (authorName.isNullOrBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<List<Book>>(
                        success = false,
                        message = "Author name is required"
                    )
                )
                return@get
            }

            val results = BookStorage.getAll().filter {
                it.author.contains(authorName, ignoreCase = true)
            }

            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    data = results,
                    message = "Found ${results.size} book(s) by '$authorName'"
                )
            )
        }

        // Exercise 4: Count total books
        get("/count") {
            val count = BookStorage.getAll().size

            @Serializable
            data class CountResponse(val count: Int)

            call.respond(
                HttpStatusCode.OK,
                ApiResponse(
                    success = true,
                    data = CountResponse(count),
                    message = "Total books counted"
                )
            )
        }
    }
}
```

### Testing the Solutions

```bash
# Exercise 1: Search
curl "http://localhost:8080/api/books/search?title=brave"

# Exercise 2: Filter by year
curl "http://localhost:8080/api/books/filter?minYear=1930&maxYear=1950"

# Exercise 3: Books by author
curl http://localhost:8080/api/books/author/Orwell

# Exercise 4: Count
curl http://localhost:8080/api/books/count
```

### Key Techniques Used

1. **Query Parameters**: `call.request.queryParameters["key"]`
2. **Filtering**: `filter { predicate }` on lists
3. **Range Check**: `it.year in minYear..maxYear`
4. **Case-Insensitive Search**: `contains(query, ignoreCase = true)`
5. **Inline Data Classes**: Define response structure locally

---

## 📝 Lesson Checkpoint Quiz

Test your understanding of Ktor routing:

### Question 1
In the route definition `route("/books") { get("/{id}") { } }`, what is the full path that will be matched?

A) `/books`
B) `/books/{id}`
C) `/{id}/books`
D) `/books/id`

---

### Question 2
Which HTTP status code should you return when a client tries to create a book with an empty title?

A) 200 OK
B) 201 Created
C) 400 Bad Request
D) 404 Not Found

---

### Question 3
What does `call.receive<CreateBookRequest>()` do?

A) Sends a CreateBookRequest to the client
B) Converts the JSON request body into a CreateBookRequest object
C) Creates a new book in the database
D) Validates that the request is correctly formatted

---

## 🎯 Why This Matters

You just built a **production-ready REST API**! This exact pattern is used by:

- **E-commerce sites** for managing products
- **Social media** for managing posts and comments
- **Banking apps** for managing accounts and transactions
- **Any mobile app** that needs to store data on a server

### What You've Mastered

✅ **CRUD Operations**: The foundation of 90% of all APIs
✅ **RESTful Design**: Industry-standard API architecture
✅ **JSON Serialization**: Converting Kotlin ↔ JSON automatically
✅ **Route Organization**: Keeping code clean as it grows
✅ **Error Handling**: Proper status codes for different scenarios
✅ **Request/Response Models**: Type-safe API contracts

### The Missing Piece

Right now, your data disappears when the server restarts (it's only in memory). In the next lessons, you'll learn:

- **Databases**: Persistent storage that survives restarts
- **Validation**: More sophisticated input checking
- **Authentication**: Protecting routes (login required)
- **Testing**: Ensuring your API works correctly

But the routing patterns you learned today? **They stay the same**. You'll just swap the in-memory storage for a database.

---

## 📚 Key Takeaways

✅ **route("/path")** groups related endpoints together
✅ **GET** retrieves data, **POST** creates, **PUT** updates, **DELETE** removes
✅ **call.receive<T>()** parses JSON request body to Kotlin object
✅ **call.respond(status, data)** sends JSON response with status code
✅ **@Serializable** makes Kotlin classes convertible to/from JSON
✅ **Path parameters** capture dynamic parts of URLs: `/{id}`
✅ **Query parameters** provide filters: `?title=kotlin&year=2020`
✅ **Status codes** communicate results: 200, 201, 400, 404

---

## 🔜 Next Steps

In **Lesson 5.4**, you'll dive deeper into:
- Path parameters vs. query parameters (when to use each)
- Accessing request headers
- Complex query parameters (multiple values, optional params)
- Request body validation patterns
- Nested routes and sub-resources

---

## ✏️ Quiz Answer Key

**Question 1**: **B) `/books/{id}`**

Explanation: The `route("/books")` sets the base path, and `get("/{id}")` appends to it, resulting in `/books/{id}`.

---

**Question 2**: **C) 400 Bad Request**

Explanation: 400 indicates the client sent invalid data. The request format is correct (it's JSON), but the content violates business rules (empty title).

---

**Question 3**: **B) Converts the JSON request body into a CreateBookRequest object**

Explanation: `call.receive<T>()` uses kotlinx.serialization to automatically parse the JSON body into the specified Kotlin type. It's the "receive" counterpart to "respond".

---

**Congratulations!** You've built a complete REST API with full CRUD operations! You now have a real, testable backend that handles JSON data. 🎉

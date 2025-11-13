# Lesson 5.4: Request Parameters - Path, Query, and Body

**Estimated Time**: 40 minutes
**Difficulty**: Beginner-Intermediate
**Prerequisites**: Lessons 5.1-5.3 (HTTP fundamentals, Ktor setup, routing)

---

## 📖 Topic Introduction

In the previous lesson, you built a complete CRUD API. But we only scratched the surface of how data can be sent to your server. There are actually **three main ways** clients send data:

1. **Path Parameters**: Data embedded in the URL path (`/users/42`)
2. **Query Parameters**: Key-value pairs after `?` (`/search?q=kotlin&page=2`)
3. **Request Body**: JSON/form data sent with the request

Understanding when and how to use each is crucial for building intuitive, flexible APIs. In this lesson, you'll master all three!

---

## 💡 The Concept: Three Ways to Send Data

### The Restaurant Order Analogy

Imagine ordering food at a restaurant:

**1. Path Parameters** = Table Number
```
"Bring the check to table 12"
/tables/12/check
```
- **Essential identifier** that's part of the resource location
- Usually required, not optional
- Identifies which specific resource you want

**2. Query Parameters** = Special Instructions
```
"Coffee, extra hot, no sugar"
/drinks/coffee?temperature=extra-hot&sugar=false
```
- **Optional filters or modifiers** that refine the request
- Can have multiple values
- Doesn't change which resource, but how you want it

**3. Request Body** = The Order Itself
```
POST /orders
Body: {
  "items": ["burger", "fries"],
  "table": 12,
  "special_instructions": "no onions"
}
```
- **Complex data** that doesn't fit in the URL
- Used for creating or updating resources
- Can contain nested structures

---

## 🛤️ Path Parameters: Identifying Resources

### When to Use Path Parameters

Use path parameters for:
- ✅ Resource identifiers (IDs, usernames, slugs)
- ✅ Required hierarchical relationships
- ✅ Data that identifies **which** resource

**Examples:**
```
GET  /users/42              # Get user with ID 42
GET  /posts/kotlin-intro    # Get post with slug "kotlin-intro"
GET  /users/42/posts        # Get posts belonging to user 42
PUT  /books/123             # Update book 123
```

### Single Path Parameter

```kotlin
get("/users/{id}") {
    val id = call.parameters["id"]?.toIntOrNull()

    if (id == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid user ID")
        return@get
    }

    val user = UserStorage.getById(id)
    if (user == null) {
        call.respond(HttpStatusCode.NotFound, "User not found")
    } else {
        call.respond(user)
    }
}
```

### Multiple Path Parameters

```kotlin
get("/users/{userId}/posts/{postId}") {
    val userId = call.parameters["userId"]?.toIntOrNull()
    val postId = call.parameters["postId"]?.toIntOrNull()

    if (userId == null || postId == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid IDs")
        return@get
    }

    val post = PostStorage.getByUserAndId(userId, postId)
    call.respond(post ?: HttpStatusCode.NotFound)
}
```

### Optional Path Parameters

```kotlin
get("/tasks/{priority?}") {
    val priority = call.parameters["priority"]

    val tasks = if (priority != null) {
        TaskStorage.getByPriority(priority)
    } else {
        TaskStorage.getAll()
    }

    call.respond(tasks)
}
```

The `?` makes the parameter optional:
- `/tasks` → Returns all tasks
- `/tasks/high` → Returns only high-priority tasks

---

## 🔍 Query Parameters: Filtering and Options

### When to Use Query Parameters

Use query parameters for:
- ✅ Filtering results (`?status=active`)
- ✅ Sorting (`?sort=date&order=desc`)
- ✅ Pagination (`?page=2&limit=20`)
- ✅ Search (`?q=kotlin`)
- ✅ Optional settings (`?format=json`)

**Examples:**
```
GET /books?author=Orwell&year=1949
GET /products?category=electronics&minPrice=100&maxPrice=500
GET /users?search=john&sort=name&page=1
```

### Accessing Single Query Parameter

```kotlin
get("/books") {
    val author = call.request.queryParameters["author"]

    val books = if (author != null) {
        BookStorage.filterByAuthor(author)
    } else {
        BookStorage.getAll()
    }

    call.respond(books)
}
```

**Test it:**
```bash
curl http://localhost:8080/books?author=Orwell
```

### Accessing Multiple Query Parameters

```kotlin
get("/products") {
    val category = call.request.queryParameters["category"]
    val minPrice = call.request.queryParameters["minPrice"]?.toDoubleOrNull()
    val maxPrice = call.request.queryParameters["maxPrice"]?.toDoubleOrNull()

    var products = ProductStorage.getAll()

    // Apply filters one by one
    if (category != null) {
        products = products.filter { it.category == category }
    }

    if (minPrice != null) {
        products = products.filter { it.price >= minPrice }
    }

    if (maxPrice != null) {
        products = products.filter { it.price <= maxPrice }
    }

    call.respond(products)
}
```

**Test it:**
```bash
curl "http://localhost:8080/products?category=books&minPrice=10&maxPrice=50"
```

### Query Parameter with Default Values

```kotlin
get("/users") {
    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20
    val sort = call.request.queryParameters["sort"] ?: "name"

    val users = UserStorage.getPaginated(page, limit, sort)
    call.respond(users)
}
```

The `?:` (Elvis operator) provides defaults:
- No `page` parameter → defaults to 1
- No `limit` parameter → defaults to 20
- No `sort` parameter → defaults to "name"

### Multiple Values for Same Parameter

```kotlin
get("/books") {
    // Get all values for "tag" parameter
    // Example: /books?tag=fiction&tag=bestseller
    val tags = call.request.queryParameters.getAll("tag") ?: emptyList()

    val books = if (tags.isNotEmpty()) {
        BookStorage.filterByTags(tags)
    } else {
        BookStorage.getAll()
    }

    call.respond(books)
}
```

**Test it:**
```bash
curl "http://localhost:8080/books?tag=fiction&tag=bestseller&tag=scifi"
```

---

## 📦 Request Body: Complex Data

### When to Use Request Body

Use request body for:
- ✅ Creating resources (POST)
- ✅ Updating resources (PUT/PATCH)
- ✅ Complex search queries
- ✅ Data that doesn't fit in URLs
- ✅ Sensitive data (passwords, etc.)

### Receiving JSON Body

```kotlin
// Define the expected structure
@Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val password: String,
    val age: Int? = null  // Optional field
)

post("/users") {
    // Receive and parse JSON body
    val request = call.receive<CreateUserRequest>()

    // Validate
    if (request.username.isBlank() || request.email.isBlank()) {
        call.respond(HttpStatusCode.BadRequest, "Username and email required")
        return@post
    }

    // Create user
    val user = UserStorage.create(request)
    call.respond(HttpStatusCode.Created, user)
}
```

**Test it:**
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com",
    "password": "secret123",
    "age": 25
  }'
```

### Receiving Plain Text

```kotlin
post("/notes") {
    val noteText = call.receiveText()

    if (noteText.isBlank()) {
        call.respond(HttpStatusCode.BadRequest, "Note cannot be empty")
        return@post
    }

    val note = NoteStorage.create(noteText)
    call.respond(HttpStatusCode.Created, note)
}
```

### Receiving Form Data

```kotlin
post("/login") {
    val parameters = call.receiveParameters()
    val username = parameters["username"]
    val password = parameters["password"]

    if (username.isNullOrBlank() || password.isNullOrBlank()) {
        call.respond(HttpStatusCode.BadRequest, "Username and password required")
        return@post
    }

    // Authenticate user
    val token = AuthService.login(username, password)
    call.respond(mapOf("token" to token))
}
```

**Test it:**
```bash
curl -X POST http://localhost:8080/login \
  -d "username=alice&password=secret123"
```

---

## 💻 Complete Example: Advanced Search API

Let's build a comprehensive example combining all three parameter types:

### Define Models

```kotlin
package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: Int,
    val genre: String,
    val rating: Double,
    val inStock: Boolean
)

@Serializable
data class SearchFilters(
    val genres: List<String>? = null,
    val minRating: Double? = null,
    val authors: List<String>? = null,
    val yearRange: YearRange? = null
)

@Serializable
data class YearRange(
    val from: Int,
    val to: Int
)

@Serializable
data class SearchResponse(
    val results: List<Book>,
    val total: Int,
    val page: Int,
    val pageSize: Int
)
```

### Implement the Search Route

```kotlin
package com.example.plugins

import com.example.data.BookStorage
import com.example.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.advancedSearchRoutes() {
    route("/search") {
        // Simple search with query parameters only
        get("/simple") {
            val query = call.request.queryParameters["q"]
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

            val books = if (query != null) {
                BookStorage.searchByTitle(query)
            } else {
                BookStorage.getAll()
            }

            // Paginate results
            val startIndex = (page - 1) * limit
            val paginatedBooks = books.drop(startIndex).take(limit)

            call.respond(SearchResponse(
                results = paginatedBooks,
                total = books.size,
                page = page,
                pageSize = limit
            ))
        }

        // Advanced search with request body
        post("/advanced") {
            val filters = call.receive<SearchFilters>()
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 10

            var books = BookStorage.getAll()

            // Apply genre filter
            filters.genres?.let { genres ->
                books = books.filter { it.genre in genres }
            }

            // Apply rating filter
            filters.minRating?.let { minRating ->
                books = books.filter { it.rating >= minRating }
            }

            // Apply author filter
            filters.authors?.let { authors ->
                books = books.filter { book ->
                    authors.any { author ->
                        book.author.contains(author, ignoreCase = true)
                    }
                }
            }

            // Apply year range filter
            filters.yearRange?.let { range ->
                books = books.filter { it.year in range.from..range.to }
            }

            // Paginate
            val startIndex = (page - 1) * limit
            val paginatedBooks = books.drop(startIndex).take(limit)

            call.respond(SearchResponse(
                results = paginatedBooks,
                total = books.size,
                page = page,
                pageSize = limit
            ))
        }

        // Search by category (path) with filters (query)
        get("/category/{category}") {
            val category = call.parameters["category"]
            val inStockOnly = call.request.queryParameters["inStock"]?.toBoolean() ?: false
            val minRating = call.request.queryParameters["minRating"]?.toDoubleOrNull()

            if (category == null) {
                call.respond(HttpStatusCode.BadRequest, "Category is required")
                return@get
            }

            var books = BookStorage.getByGenre(category)

            if (inStockOnly) {
                books = books.filter { it.inStock }
            }

            minRating?.let { rating ->
                books = books.filter { it.rating >= rating }
            }

            call.respond(books)
        }
    }
}
```

### Test the Advanced Search

**Simple search with query parameters:**
```bash
curl "http://localhost:8080/search/simple?q=kotlin&page=1&limit=5"
```

**Advanced search with body + pagination:**
```bash
curl -X POST "http://localhost:8080/search/advanced?page=1&limit=10" \
  -H "Content-Type: application/json" \
  -d '{
    "genres": ["fiction", "scifi"],
    "minRating": 4.0,
    "yearRange": {
      "from": 1940,
      "to": 1960
    }
  }'
```

**Category search with filters:**
```bash
curl "http://localhost:8080/search/category/fiction?inStock=true&minRating=4.5"
```

---

## 🔍 Code Breakdown: Best Practices

### 1. Parameter Validation Pattern

```kotlin
get("/users/{id}") {
    // Extract and validate
    val id = call.parameters["id"]?.toIntOrNull()

    if (id == null) {
        call.respond(HttpStatusCode.BadRequest, "Invalid ID")
        return@get  // Early return on validation failure
    }

    // Continue with valid data
    val user = UserStorage.getById(id)
    call.respond(user ?: HttpStatusCode.NotFound)
}
```

**Key points:**
- Always validate parameter types
- Use `toIntOrNull()`, `toDoubleOrNull()` for safe conversion
- Return early on validation errors
- Send appropriate status codes

### 2. Default Values Pattern

```kotlin
val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: 20

// Ensure reasonable bounds
val safePage = page.coerceAtLeast(1)
val safeLimit = limit.coerceIn(1, 100)  // Max 100 items per page
```

### 3. Combining Parameters Pattern

```kotlin
post("/users/{userId}/posts") {
    // Path parameter
    val userId = call.parameters["userId"]?.toIntOrNull()
        ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid user ID")

    // Query parameter
    val publish = call.request.queryParameters["publish"]?.toBoolean() ?: false

    // Body
    val postData = call.receive<CreatePostRequest>()

    // Use all three!
    val post = PostStorage.create(userId, postData, publishImmediately = publish)
    call.respond(HttpStatusCode.Created, post)
}
```

### 4. Headers as Parameters

Don't forget about headers!

```kotlin
get("/profile") {
    val authToken = call.request.headers["Authorization"]
    val userAgent = call.request.headers["User-Agent"]
    val acceptLanguage = call.request.headers["Accept-Language"]

    if (authToken == null) {
        call.respond(HttpStatusCode.Unauthorized, "Token required")
        return@get
    }

    // Use the header data
    val user = AuthService.getUserFromToken(authToken)
    call.respond(user)
}
```

---

## 🎯 Exercise: Build a Task Filter API

Create a comprehensive task filtering API using all parameter types:

### Requirements

**1. GET /tasks/{status}** - Path parameter for status
- `status` can be: "pending", "completed", "archived"
- Example: `/tasks/pending`

**2. Add query parameters for additional filters:**
- `priority`: "low", "medium", "high"
- `assignedTo`: username
- `sort`: "date", "priority", "title"
- `order`: "asc", "desc"

**3. POST /tasks/search** - Advanced search with body
- Body should accept:
  ```json
  {
    "title": "search term",
    "tags": ["urgent", "bug"],
    "dueDateRange": {
      "start": "2024-01-01",
      "end": "2024-12-31"
    }
  }
  ```

### Starter Code

```kotlin
@Serializable
data class Task(
    val id: Int,
    val title: String,
    val status: String,
    val priority: String,
    val assignedTo: String?,
    val tags: List<String>,
    val dueDate: String?
)

object TaskStorage {
    private val tasks = mutableListOf(
        Task(1, "Fix bug", "pending", "high", "alice", listOf("bug", "urgent"), "2024-12-01"),
        Task(2, "Write docs", "completed", "medium", "bob", listOf("docs"), "2024-11-15"),
        Task(3, "Review PR", "pending", "medium", "alice", listOf("review"), "2024-11-20"),
        Task(4, "Deploy", "archived", "low", null, listOf("deploy"), null)
    )

    fun getAll() = tasks.toList()
    fun getByStatus(status: String) = tasks.filter { it.status == status }
}

// TODO: Implement the routes!
```

---

## ✅ Solution & Explanation

Here's the complete implementation:

```kotlin
package com.example.plugins

import com.example.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

@Serializable
data class TaskSearchRequest(
    val title: String? = null,
    val tags: List<String>? = null,
    val dueDateRange: DateRange? = null
)

@Serializable
data class DateRange(
    val start: String,
    val end: String
)

fun Route.taskRoutes() {
    route("/tasks") {
        // Exercise 1: Path parameter with query filters
        get("/{status}") {
            val status = call.parameters["status"]

            if (status == null || status !in listOf("pending", "completed", "archived")) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    "Status must be: pending, completed, or archived"
                )
                return@get
            }

            // Get query parameters
            val priority = call.request.queryParameters["priority"]
            val assignedTo = call.request.queryParameters["assignedTo"]
            val sort = call.request.queryParameters["sort"] ?: "date"
            val order = call.request.queryParameters["order"] ?: "asc"

            // Filter by status (path parameter)
            var tasks = TaskStorage.getByStatus(status)

            // Apply priority filter (query parameter)
            priority?.let { p ->
                tasks = tasks.filter { it.priority == p }
            }

            // Apply assignedTo filter (query parameter)
            assignedTo?.let { user ->
                tasks = tasks.filter { it.assignedTo == user }
            }

            // Sort
            tasks = when (sort) {
                "priority" -> tasks.sortedBy { it.priority }
                "title" -> tasks.sortedBy { it.title }
                "date" -> tasks.sortedBy { it.dueDate }
                else -> tasks
            }

            // Apply order
            if (order == "desc") {
                tasks = tasks.reversed()
            }

            call.respond(ApiResponse(
                success = true,
                data = tasks,
                message = "Found ${tasks.size} task(s)"
            ))
        }

        // Exercise 2: Advanced search with body
        post("/search") {
            val searchRequest = call.receive<TaskSearchRequest>()

            var tasks = TaskStorage.getAll()

            // Filter by title
            searchRequest.title?.let { titleQuery ->
                tasks = tasks.filter {
                    it.title.contains(titleQuery, ignoreCase = true)
                }
            }

            // Filter by tags
            searchRequest.tags?.let { searchTags ->
                tasks = tasks.filter { task ->
                    searchTags.any { tag -> tag in task.tags }
                }
            }

            // Filter by date range
            searchRequest.dueDateRange?.let { range ->
                tasks = tasks.filter { task ->
                    task.dueDate != null &&
                    task.dueDate >= range.start &&
                    task.dueDate <= range.end
                }
            }

            call.respond(ApiResponse(
                success = true,
                data = tasks,
                message = "Search completed: ${tasks.size} result(s)"
            ))
        }
    }
}
```

### Testing the Solution

**Test path + query parameters:**
```bash
# Get pending tasks for alice, sorted by priority
curl "http://localhost:8080/tasks/pending?assignedTo=alice&sort=priority&order=desc"
```

**Test advanced search:**
```bash
curl -X POST http://localhost:8080/tasks/search \
  -H "Content-Type: application/json" \
  -d '{
    "title": "bug",
    "tags": ["urgent"],
    "dueDateRange": {
      "start": "2024-11-01",
      "end": "2024-12-31"
    }
  }'
```

---

## 📝 Lesson Checkpoint Quiz

### Question 1
Which parameter type should you use for a required user ID in a route like "get user profile"?

A) Query parameter: `/profile?userId=42`
B) Path parameter: `/profile/42`
C) Request body: `POST /profile` with `{"userId": 42}`
D) Header: `X-User-ID: 42`

---

### Question 2
You're building a product search API with many optional filters (category, price range, brand, color). What's the BEST approach?

A) Use all path parameters: `/products/electronics/100/500/apple/red`
B) Use all query parameters: `/products?category=electronics&minPrice=100...`
C) Use request body for all filters: `POST /products/search`
D) Create separate endpoints for each filter combination

---

### Question 3
What does `call.request.queryParameters["page"]?.toIntOrNull() ?: 1` do?

A) Gets the page parameter and throws an error if it's not a number
B) Gets the page parameter, converts to Int, or returns 1 if null/invalid
C) Sets the page parameter to 1
D) Gets the first page of results

---

## 🎯 Why This Matters

Understanding parameter types is crucial for building **intuitive, flexible APIs** that other developers love to use.

### Real-World Examples

**GitHub API:**
```
GET /repos/{owner}/{repo}/issues?state=open&sort=created&page=2
     └─ Path params ─┘        └─── Query params ────┘
```

**Twitter API:**
```
GET /tweets/search?q=kotlin&count=100&lang=en
```

**Stripe API:**
```
POST /customers
Body: { "email": "user@example.com", "name": "Alice" }
```

### Design Principles You've Learned

✅ **Path parameters**: Required identifiers
✅ **Query parameters**: Optional filters and settings
✅ **Request body**: Complex or sensitive data
✅ **Combine them**: For maximum flexibility

---

## 📚 Key Takeaways

✅ **Path parameters** (`/{id}`) identify **which** resource
✅ **Query parameters** (`?key=value`) refine **how** you want it
✅ **Request body** contains **complex data** for POST/PUT
✅ Always **validate** parameter types with `toIntOrNull()`, etc.
✅ Provide **default values** with Elvis operator `?:`
✅ **Combine** parameter types for flexible APIs
✅ Use **early returns** for validation failures

---

## 🔜 Next Steps

In **Lesson 5.5**, you'll dive deeper into:
- Advanced JSON serialization techniques
- Custom serializers for complex types
- Handling nested objects
- Polymorphic serialization
- Error handling for malformed JSON

---

## ✏️ Quiz Answer Key

**Question 1**: **B) Path parameter: `/profile/42`**

Explanation: User ID is a required identifier that specifies *which* user's profile. Path parameters are perfect for required resource identifiers. Query parameters would make it seem optional.

---

**Question 2**: **B) Use all query parameters**

Explanation: Query parameters are ideal for optional filters. Users can provide as many or as few as they want. Path parameters would be unwieldy, and POST body would be overkill for a simple read operation (GET).

---

**Question 3**: **B) Gets the page parameter, converts to Int, or returns 1 if null/invalid**

Explanation: `?.` safely accesses the parameter (returns null if not present), `toIntOrNull()` converts to Int (returns null if invalid), and `?: 1` provides a default value of 1.

---

**Congratulations!** You now understand all three ways to receive data in Ktor and when to use each! 🎉

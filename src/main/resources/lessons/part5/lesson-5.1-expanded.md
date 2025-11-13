# Lesson 5.1: Introduction to Backend Development & HTTP Fundamentals

**Estimated Time**: 30 minutes
**Difficulty**: Beginner
**Prerequisites**: Parts 1-4 (Kotlin fundamentals, OOP, functions)

---

## 📖 Topic Introduction

Welcome to Part 5! You've mastered Kotlin fundamentals, object-oriented programming, and functional concepts. Now it's time to build something that runs on the internet: a **backend server**.

In this lesson, you'll learn what backend development actually means, how computers talk to each other over the internet, and the fundamental protocol (HTTP) that powers the web.

---

## 💡 The Concept: What Is a Backend?

### The Restaurant Analogy

Imagine you're at a restaurant:

**Frontend** = The dining room, menu, and waitstaff
- This is what you see and interact with
- Beautiful presentation
- Easy to understand and navigate

**Backend** = The kitchen
- Hidden from customers
- Where the real work happens
- Processes orders, prepares food, manages inventory
- Follows strict recipes and procedures

When you order food (make a request), the waiter takes your order to the kitchen (sends it to the backend). The kitchen prepares it (processes the request), and the waiter brings it back to you (returns the response).

### What Does a Backend Actually Do?

A backend server is a program running on a computer (usually in a data center) that:

1. **Listens** for requests from clients (web browsers, mobile apps, etc.)
2. **Processes** those requests (validates data, performs calculations, queries databases)
3. **Responds** with data or confirmation
4. **Stores** data in databases for long-term persistence
5. **Enforces** business rules and security

### Client-Server Architecture

```
┌─────────────┐                    ┌─────────────┐
│   Client    │  ---- Request ---> │   Server    │
│ (Frontend)  │                    │  (Backend)  │
│             │ <--- Response ---- │             │
└─────────────┘                    └─────────────┘
```

- **Client**: Your web browser, mobile app, or any program that makes requests
- **Server**: The backend program that handles requests and sends responses

---

## 🌐 HTTP: The Language of the Web

### What Is HTTP?

**HTTP** stands for **Hypertext Transfer Protocol**. It's the standard way computers communicate on the web.

Think of HTTP as the "language rules" for how a customer (client) and a waiter (server) communicate:

- **Customer**: "I'd like a coffee, please." (GET request)
- **Waiter**: "Here's your coffee." (200 OK response)

### HTTP Request Structure

When a client makes a request, it includes:

```
GET /api/books HTTP/1.1
Host: example.com
Content-Type: application/json
Authorization: Bearer token123

{optional request body}
```

**Components**:
1. **Method**: What action to perform (GET, POST, PUT, DELETE)
2. **Path**: Which resource you want (`/api/books`)
3. **Headers**: Metadata about the request
4. **Body**: Data sent with the request (optional)

### HTTP Methods: The "Verbs" of the Web

| Method   | Purpose           | Restaurant Analogy          | Example              |
|----------|-------------------|-----------------------------|----------------------|
| **GET**  | Retrieve data     | "What's on the menu?"       | Get list of books    |
| **POST** | Create new data   | "I'd like to order this"    | Create a new book    |
| **PUT**  | Update/replace    | "Change my entire order"    | Update book details  |
| **DELETE** | Remove data     | "Cancel my order"           | Delete a book        |

### HTTP Status Codes: The "Results" of Requests

Status codes tell you what happened with your request:

#### **2xx Success** ✅
- **200 OK**: Request succeeded, here's your data
- **201 Created**: New resource created successfully
- **204 No Content**: Success, but no data to return

#### **4xx Client Errors** ❌ (You made a mistake)
- **400 Bad Request**: Your request doesn't make sense
- **401 Unauthorized**: You need to log in first
- **403 Forbidden**: You're logged in, but not allowed to do this
- **404 Not Found**: This resource doesn't exist

#### **5xx Server Errors** 💥 (Server made a mistake)
- **500 Internal Server Error**: Something broke on the server
- **503 Service Unavailable**: Server is temporarily down

### Real-World Example

When you visit `https://example.com/books`:

```
1. Your browser sends:
   GET /books HTTP/1.1
   Host: example.com

2. Server processes the request

3. Server responds:
   HTTP/1.1 200 OK
   Content-Type: application/json

   [
     {"id": 1, "title": "1984"},
     {"id": 2, "title": "Brave New World"}
   ]
```

---

## 🔧 Understanding URLs and Endpoints

### URL Structure

```
https://api.example.com:443/api/v1/books/123?format=json#section2
  │      │              │    │           │    │           │
scheme  domain        port  path     resource query    fragment
```

- **Scheme**: `https://` (secure) or `http://` (insecure)
- **Domain**: The server address
- **Port**: Usually 80 (HTTP) or 443 (HTTPS), often hidden
- **Path**: The route to the resource
- **Query Parameters**: Additional filters or options
- **Fragment**: Specific section (rarely used in APIs)

### RESTful API Design Principles

**REST** = Representational State Transfer (don't worry about the name, focus on the pattern)

Good API endpoint design:

```
✅ GET    /books           - Get all books
✅ GET    /books/123       - Get book with ID 123
✅ POST   /books           - Create a new book
✅ PUT    /books/123       - Update book 123 (replace entirely)
✅ PATCH  /books/123       - Update book 123 (partial update)
✅ DELETE /books/123       - Delete book 123

❌ GET    /getAllBooks     - Don't use verbs in URLs
❌ POST   /books/delete    - Use DELETE method instead
❌ GET    /book             - Use plural nouns
```

**Key Principles**:
1. Use **nouns** for resources (books, users, orders)
2. Use **HTTP methods** for actions (GET, POST, DELETE)
3. Use **plural** names (`/books`, not `/book`)
4. Be **consistent** throughout your API

---

## 📝 Practical Example: Library API Design

Let's design an API for a library system on paper:

### Resources
- Books
- Users
- Loans (when someone borrows a book)

### Endpoints

```
Books:
GET    /api/books              - List all books
GET    /api/books/42           - Get specific book
POST   /api/books              - Add new book (admin only)
PUT    /api/books/42           - Update book details
DELETE /api/books/42           - Remove book

Users:
GET    /api/users              - List all users
GET    /api/users/alice        - Get user profile
POST   /api/users              - Register new user
PUT    /api/users/alice        - Update user info

Loans:
GET    /api/loans              - List all current loans
POST   /api/loans              - Check out a book
DELETE /api/loans/5            - Return a book

Search:
GET    /api/books?author=Orwell           - Search by author
GET    /api/books?available=true          - Find available books
GET    /api/books?category=scifi&year=2020 - Multiple filters
```

---

## 💻 Code Example: Understanding HTTP Requests (Conceptual)

While we haven't built a server yet, let's understand what requests and responses look like:

```kotlin
// This is what a client might send (conceptual)
data class HttpRequest(
    val method: String,       // "GET", "POST", etc.
    val path: String,         // "/api/books"
    val headers: Map<String, String>,
    val body: String?         // null for GET, data for POST
)

// Example GET request
val getRequest = HttpRequest(
    method = "GET",
    path = "/api/books/42",
    headers = mapOf(
        "Accept" to "application/json",
        "Authorization" to "Bearer token123"
    ),
    body = null
)

// Example POST request
val postRequest = HttpRequest(
    method = "POST",
    path = "/api/books",
    headers = mapOf(
        "Content-Type" to "application/json",
        "Authorization" to "Bearer token123"
    ),
    body = """
        {
            "title": "Kotlin in Action",
            "author": "Dmitry Jemerov",
            "year": 2017
        }
    """.trimIndent()
)

// This is what a server sends back
data class HttpResponse(
    val statusCode: Int,      // 200, 404, 500, etc.
    val statusMessage: String, // "OK", "Not Found", etc.
    val headers: Map<String, String>,
    val body: String?
)

// Success response
val successResponse = HttpResponse(
    statusCode = 200,
    statusMessage = "OK",
    headers = mapOf(
        "Content-Type" to "application/json"
    ),
    body = """
        {
            "id": 42,
            "title": "1984",
            "author": "George Orwell",
            "available": true
        }
    """.trimIndent()
)

// Error response
val errorResponse = HttpResponse(
    statusCode = 404,
    statusMessage = "Not Found",
    headers = mapOf(
        "Content-Type" to "application/json"
    ),
    body = """
        {
            "error": "Book not found",
            "message": "No book exists with ID 42"
        }
    """.trimIndent()
)
```

### Understanding the Flow

```kotlin
fun main() {
    // Imagine this is a simplified server
    val server = SimpleLibraryServer()

    // Client makes a request
    val request = HttpRequest(
        method = "GET",
        path = "/api/books/1",
        headers = emptyMap(),
        body = null
    )

    // Server processes and responds
    val response = server.handleRequest(request)

    println("Status: ${response.statusCode} ${response.statusMessage}")
    println("Body: ${response.body}")
}

// Simplified server (we'll build real ones soon!)
class SimpleLibraryServer {
    private val books = mapOf(
        1 to """{"id": 1, "title": "1984", "author": "George Orwell"}""",
        2 to """{"id": 2, "title": "Brave New World", "author": "Aldous Huxley"}"""
    )

    fun handleRequest(request: HttpRequest): HttpResponse {
        // Extract ID from path like "/api/books/1"
        val id = request.path.substringAfterLast("/").toIntOrNull()

        return when {
            request.method != "GET" -> HttpResponse(
                statusCode = 405,
                statusMessage = "Method Not Allowed",
                headers = emptyMap(),
                body = """{"error": "Only GET is supported"}"""
            )

            id == null -> HttpResponse(
                statusCode = 400,
                statusMessage = "Bad Request",
                headers = emptyMap(),
                body = """{"error": "Invalid book ID"}"""
            )

            id !in books -> HttpResponse(
                statusCode = 404,
                statusMessage = "Not Found",
                headers = emptyMap(),
                body = """{"error": "Book not found"}"""
            )

            else -> HttpResponse(
                statusCode = 200,
                statusMessage = "OK",
                headers = mapOf("Content-Type" to "application/json"),
                body = books[id]
            )
        }
    }
}
```

**Output:**
```
Status: 200 OK
Body: {"id": 1, "title": "1984", "author": "George Orwell"}
```

---

## 🔍 Code Breakdown

Let's analyze the key concepts:

### 1. Request Structure
```kotlin
data class HttpRequest(
    val method: String,       // What action?
    val path: String,         // Which resource?
    val headers: Map<String, String>,  // Metadata
    val body: String?         // Data payload (if any)
)
```

- **method**: Tells the server what you want to do
- **path**: Identifies which resource you're targeting
- **headers**: Additional information (authentication, content type, etc.)
- **body**: The actual data (for POST/PUT requests)

### 2. Response Structure
```kotlin
data class HttpResponse(
    val statusCode: Int,      // Was it successful?
    val statusMessage: String, // Human-readable status
    val headers: Map<String, String>,  // Metadata
    val body: String?         // The actual data
)
```

- **statusCode**: Numerical code (200 = success, 404 = not found)
- **statusMessage**: Description of the status
- **body**: The data you requested (or error information)

### 3. Request Handling Logic

```kotlin
when {
    request.method != "GET" -> // Wrong HTTP method
    id == null -> // Invalid input
    id !in books -> // Resource doesn't exist
    else -> // Success!
}
```

This pattern will be the foundation of every backend you build.

---

## 🎯 Exercise: Design Your Own API

Design a simple API for a **To-Do List Application** on paper. You don't need to write code yet!

**Requirements:**
1. Users can view all their tasks
2. Users can view a single task by ID
3. Users can create a new task
4. Users can mark a task as complete
5. Users can delete a task
6. Users can filter tasks by status (completed/pending)

**Your task:**
- List all the endpoints you would need
- Specify the HTTP method for each
- Include example URLs with query parameters where needed
- Think about what status codes you'd return for each endpoint

---

## ✅ Solution & Explanation

Here's a well-designed API for the To-Do List application:

### Endpoints

```
Tasks Resource:

1. GET /api/tasks
   Description: Get all tasks for the current user
   Success Response: 200 OK
   Error Response: 401 Unauthorized (if not logged in)

2. GET /api/tasks/{id}
   Description: Get a specific task by ID
   Success Response: 200 OK
   Error Responses:
     - 404 Not Found (task doesn't exist)
     - 403 Forbidden (task belongs to another user)

3. POST /api/tasks
   Description: Create a new task
   Request Body: {"title": "...", "description": "..."}
   Success Response: 201 Created
   Error Response: 400 Bad Request (invalid data)

4. PUT /api/tasks/{id}
   Description: Update a task (including marking complete)
   Request Body: {"title": "...", "completed": true}
   Success Response: 200 OK
   Error Responses:
     - 404 Not Found
     - 400 Bad Request (invalid data)

5. DELETE /api/tasks/{id}
   Description: Delete a task
   Success Response: 204 No Content
   Error Response: 404 Not Found

Filtering & Search:

6. GET /api/tasks?status=completed
   Description: Get only completed tasks
   Success Response: 200 OK

7. GET /api/tasks?status=pending
   Description: Get only pending (incomplete) tasks
   Success Response: 200 OK

8. GET /api/tasks?search=groceries
   Description: Search tasks by title/description
   Success Response: 200 OK
```

### Example Request/Response Flow

**Creating a Task:**

```
Request:
POST /api/tasks HTTP/1.1
Content-Type: application/json
Authorization: Bearer user_token_123

{
    "title": "Buy groceries",
    "description": "Milk, eggs, bread",
    "dueDate": "2024-12-01"
}

Response:
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/tasks/42

{
    "id": 42,
    "title": "Buy groceries",
    "description": "Milk, eggs, bread",
    "dueDate": "2024-12-01",
    "completed": false,
    "createdAt": "2024-11-13T10:30:00Z"
}
```

**Marking Task Complete:**

```
Request:
PUT /api/tasks/42 HTTP/1.1
Content-Type: application/json
Authorization: Bearer user_token_123

{
    "completed": true
}

Response:
HTTP/1.1 200 OK
Content-Type: application/json

{
    "id": 42,
    "title": "Buy groceries",
    "description": "Milk, eggs, bread",
    "dueDate": "2024-12-01",
    "completed": true,
    "completedAt": "2024-11-13T15:45:00Z"
}
```

### Key Design Decisions

1. **Consistent naming**: All endpoints use `/api/tasks` (plural noun)
2. **Proper HTTP methods**: GET for reading, POST for creating, PUT for updating, DELETE for removing
3. **Meaningful status codes**: 201 for creation, 204 for deletion, 404 when not found
4. **Query parameters for filtering**: `?status=completed` instead of `/tasks/completed`
5. **Resource IDs in the path**: `/tasks/{id}` for specific tasks

---

## 📝 Lesson Checkpoint Quiz

Test your understanding of HTTP fundamentals:

### Question 1
Which HTTP method should you use to retrieve a list of books from a server?

A) POST
B) GET
C) PUT
D) DELETE

---

### Question 2
You try to access `/api/users/42` but that user doesn't exist. What status code should the server return?

A) 200 OK
B) 400 Bad Request
C) 404 Not Found
D) 500 Internal Server Error

---

### Question 3
Which of the following is the BEST RESTful API endpoint design for updating a user's profile?

A) `POST /updateUserProfile/123`
B) `GET /users/123/update`
C) `PUT /users/123`
D) `UPDATE /user/123`

---

## 🎯 Why This Matters

Understanding HTTP is like learning the alphabet before writing essays. **Every** backend you ever build—whether with Ktor, Spring Boot, Express.js, Django, or any other framework—uses these exact same concepts:

- **HTTP methods** are universal across all web frameworks
- **Status codes** are standardized (200 always means success, 404 always means not found)
- **RESTful design** makes your API intuitive for other developers

In the next lesson, we'll set up our first Ktor project and turn these concepts into actual working code. But first, you needed to understand *what* you're building and *why* it's designed this way.

When you build your first API endpoint, you'll think: "GET request to `/api/books` returns 200 with a JSON array." You now speak the language of backend development!

---

## 📚 Key Takeaways

✅ **Backend** = The server-side logic, database, and business rules
✅ **HTTP** = The protocol that defines how clients and servers communicate
✅ **GET** = Retrieve data, **POST** = Create, **PUT** = Update, **DELETE** = Remove
✅ **Status Codes**: 2xx = Success, 4xx = Client error, 5xx = Server error
✅ **REST API** = Use nouns for resources, HTTP methods for actions
✅ **Endpoints** = URLs that point to specific resources

---

## 🔜 Next Steps

In **Lesson 5.2**, you'll:
- Install Ktor and create your first project
- Set up a development environment
- Run your first server that responds to HTTP requests
- Test your API with a web browser and Postman

---

## ✏️ Quiz Answer Key

**Question 1**: **B) GET**
GET is used to retrieve/read data without modifying anything on the server.

**Question 2**: **C) 404 Not Found**
404 means the resource (user 42) doesn't exist at that URL.

**Question 3**: **C) PUT /users/123**
This follows REST principles: plural noun (`users`), resource ID in path (`123`), and proper HTTP method (`PUT` for updates).

---

**Congratulations!** You now understand the foundational concepts of backend development. In the next lesson, we'll start writing real code!

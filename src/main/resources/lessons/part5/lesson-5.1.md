# Lesson 5.1: Introduction to Backend Development with Ktor

## Building the "Brain" of an App

Welcome to Part 5! Now you'll learn to build **backends** - the server-side code that powers apps!

**Analogy:** Think of a restaurant:
- **Backend (Kitchen):** Where the food is prepared
- **Frontend (Dining Area):** Where customers interact
- **API (Waiter):** Takes orders to the kitchen, brings back food

---

## What is a Backend?

A **backend** is a server that:
- Stores data (database)
- Processes requests
- Enforces business logic
- Provides APIs for frontends to use

**Examples:**
- Login system
- Payment processing
- Data storage
- Email sending

---

## What is an API?

**API** (Application Programming Interface) = A way for programs to talk to each other.

**REST API** = Uses HTTP requests:
- **GET** = Read data
- **POST** = Create data
- **PUT** = Update data
- **DELETE** = Delete data

**Example:**
```
GET /api/users/123 → Get user with ID 123
POST /api/users → Create a new user
PUT /api/users/123 → Update user 123
DELETE /api/users/123 → Delete user 123
```

---

## What is Ktor?

**Ktor** is a Kotlin framework for building backends. It's:
- Lightweight and fast
- Built for Kotlin (not Java-first)
- Supports coroutines
- Easy to learn

---

## Your First Ktor Server

### Setup (build.gradle.kts):

```kotlin
plugins {
    kotlin("jvm") version "1.9.20"
    application
    id("io.ktor.plugin") version "2.3.5"
}

dependencies {
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
}
```

---

### Simple Server:

```kotlin
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/") {
                call.respondText("Hello, Ktor!")
            }

            get("/hello/{name}") {
                val name = call.parameters["name"]
                call.respondText("Hello, $name!")
            }

            get("/api/users") {
                call.respondText("List of users")
            }
        }
    }.start(wait = true)
}
```

**Run it, then visit:** `http://localhost:8080/`

---

## Understanding Routes

```kotlin
routing {
    // Simple GET
    get("/hello") {
        call.respondText("Hello!")
    }

    // With path parameter
    get("/user/{id}") {
        val id = call.parameters["id"]
        call.respondText("User ID: $id")
    }

    // With query parameter (?name=Alice)
    get("/greet") {
        val name = call.request.queryParameters["name"] ?: "Guest"
        call.respondText("Hello, $name!")
    }

    // POST endpoint
    post("/api/data") {
        call.respondText("Data received!", status = HttpStatusCode.Created)
    }
}
```

---

## Returning JSON

```kotlin
import kotlinx.serialization.Serializable
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*

@Serializable
data class User(val id: Int, val name: String, val email: String)

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/api/user") {
            val user = User(1, "Alice", "alice@example.com")
            call.respond(user)
        }

        get("/api/users") {
            val users = listOf(
                User(1, "Alice", "alice@example.com"),
                User(2, "Bob", "bob@example.com")
            )
            call.respond(users)
        }
    }
}
```

**Response:**
```json
{
    "id": 1,
    "name": "Alice",
    "email": "alice@example.com"
}
```

---

## Handling POST Requests

```kotlin
import io.ktor.server.request.*

@Serializable
data class CreateUserRequest(val name: String, val email: String)

routing {
    post("/api/users") {
        val request = call.receive<CreateUserRequest>()
        val newUser = User(
            id = users.size + 1,
            name = request.name,
            email = request.email
        )
        users.add(newUser)
        call.respond(HttpStatusCode.Created, newUser)
    }
}
```

---

## Building a Simple TODO API

```kotlin
@Serializable
data class Todo(
    val id: Int,
    var title: String,
    var completed: Boolean = false
)

val todos = mutableListOf<Todo>()
var nextId = 1

fun Application.configureTodoAPI() {
    install(ContentNegotiation) { json() }

    routing {
        // Get all todos
        get("/api/todos") {
            call.respond(todos)
        }

        // Get single todo
        get("/api/todos/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val todo = todos.find { it.id == id }
            if (todo != null) {
                call.respond(todo)
            } else {
                call.respond(HttpStatusCode.NotFound, "Todo not found")
            }
        }

        // Create todo
        post("/api/todos") {
            @Serializable
            data class CreateTodoRequest(val title: String)

            val request = call.receive<CreateTodoRequest>()
            val todo = Todo(nextId++, request.title)
            todos.add(todo)
            call.respond(HttpStatusCode.Created, todo)
        }

        // Update todo
        put("/api/todos/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            val todo = todos.find { it.id == id }
            if (todo != null) {
                val update = call.receive<Todo>()
                todo.title = update.title
                todo.completed = update.completed
                call.respond(todo)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        // Delete todo
        delete("/api/todos/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (todos.removeIf { it.id == id }) {
                call.respond(HttpStatusCode.NoContent)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
```

---

## Testing with HTTP Client

Use tools like:
- **Postman**
- **Insomnia**
- **curl**
- **Browser** (for GET requests)

**Examples:**

```bash
# Get all todos
GET http://localhost:8080/api/todos

# Create a todo
POST http://localhost:8080/api/todos
Content-Type: application/json

{
    "title": "Learn Ktor"
}

# Update a todo
PUT http://localhost:8080/api/todos/1
Content-Type: application/json

{
    "id": 1,
    "title": "Learn Ktor",
    "completed": true
}
```

---

## Recap

You now understand:

1. **Backend** = Server-side application
2. **API** = Interface for communication
3. **REST** = Standard for web APIs
4. **Ktor** = Kotlin backend framework
5. **Routing** = Defining endpoints
6. **JSON** = Data format
7. **CRUD** = Create, Read, Update, Delete

---

## What's Next?

Next lesson: **Adding databases, authentication, and middleware!**

**Key Takeaway:** Backends handle the logic and data. Ktor makes it easy with Kotlin!

Continue to the next lesson!

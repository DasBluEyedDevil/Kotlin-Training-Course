# Lesson 5.2: Setting Up Your First Ktor Project

**Estimated Time**: 35 minutes
**Difficulty**: Beginner
**Prerequisites**: Lesson 5.1 (HTTP Fundamentals), Kotlin basics

---

## 📖 Topic Introduction

In the previous lesson, you learned the concepts: HTTP methods, status codes, and REST API design. Now it's time to build your first actual backend server!

In this lesson, you'll:
- Create a Ktor project from scratch
- Understand the project structure
- Install essential plugins
- Run your first server that responds to HTTP requests
- Test your API with a web browser

By the end, you'll have a working server running on your computer that you can visit in your browser!

---

## 💡 The Concept: What Is Ktor?

### The Building Blocks Analogy

Imagine you're building a house:

**Traditional frameworks** = Pre-fabricated houses
- Lots of features you might not need
- Heavy and opinionated
- Hard to customize

**Ktor** = A box of high-quality building blocks
- Start with a minimal foundation
- Add only what you need (plugins)
- Lightweight and flexible
- Perfect for learning because you see every piece

### Why Ktor for Learning?

1. **Kotlin-first**: Written specifically for Kotlin, not a Java framework adapted for Kotlin
2. **Lightweight**: Minimal boilerplate, clear code
3. **Plugin-based**: Each feature (routing, JSON, authentication) is a separate plugin you explicitly add
4. **Async by default**: Uses Kotlin coroutines for efficient handling of many requests
5. **Modern**: Built with current best practices

### Ktor Architecture

```
┌─────────────────────────────────────┐
│      Your Ktor Application          │
├─────────────────────────────────────┤
│  ┌───────────────────────────────┐  │
│  │  Routing Plugin               │  │  <-- Define endpoints
│  ├───────────────────────────────┤  │
│  │  ContentNegotiation Plugin    │  │  <-- JSON support
│  ├───────────────────────────────┤  │
│  │  Authentication Plugin        │  │  <-- Login/JWT
│  ├───────────────────────────────┤  │
│  │  Your Business Logic          │  │  <-- Your code
│  └───────────────────────────────┘  │
├─────────────────────────────────────┤
│      Ktor Engine (CIO/Netty)        │  <-- Handles HTTP
└─────────────────────────────────────┘
```

---

## 🚀 Setting Up Your Development Environment

### Prerequisites Check

Before we start, ensure you have:

1. **JDK 17 or higher** installed
   ```bash
   java -version
   # Should show: java version "17" or higher
   ```

2. **IntelliJ IDEA** (Community Edition is free) or any IDE with Kotlin support

3. **Gradle** (usually bundled with IDE, but verify):
   ```bash
   gradle -version
   # Should show: Gradle 8.0 or higher
   ```

---

## 💻 Creating Your First Ktor Project

### Method 1: Using the Ktor Project Generator (Recommended for Beginners)

1. **Visit the Generator**
   - Open your browser and go to: https://start.ktor.io/

2. **Configure Your Project**
   ```
   Project Name: my-first-api
   Build System: Gradle Kotlin
   Website: example.com
   Artifact: com.example.myfirstapi
   Ktor Version: 3.2.0 (or latest)
   Engine: CIO
   Configuration: Code (not YAML/HOCON for now)
   ```

3. **Add Plugins**
   - **Routing**: For defining endpoints (essential!)
   - **Content Negotiation**: For JSON support (essential!)
   - **kotlinx.serialization**: For converting objects to/from JSON

4. **Generate and Download**
   - Click "Generate Project"
   - Download the ZIP file
   - Extract it to your projects folder

### Method 2: Manual Setup with Gradle (For Understanding)

If you want to understand every piece, let's build it manually:

**Step 1: Create a new directory**
```bash
mkdir my-first-api
cd my-first-api
```

**Step 2: Create the Gradle build file**

Create `build.gradle.kts`:

```kotlin
plugins {
    kotlin("jvm") version "2.0.0"
    id("io.ktor.plugin") version "3.2.0"
    kotlin("plugin.serialization") version "2.0.0"
}

group = "com.example"
version = "0.0.1"

application {
    mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Ktor Server Core
    implementation("io.ktor:ktor-server-core-jvm:3.2.0")

    // CIO Engine (Coroutine-based I/O)
    implementation("io.ktor:ktor-server-cio-jvm:3.2.0")

    // Content Negotiation for JSON
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.2.0")

    // kotlinx.serialization for JSON
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")

    // Testing (we'll use this later)
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.2.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.0")
}
```

**Step 3: Create Gradle wrapper files**

Create `gradle.properties`:
```properties
kotlin.code.style=official
```

Create `settings.gradle.kts`:
```kotlin
rootProject.name = "my-first-api"
```

---

## 📁 Understanding the Project Structure

After creation, your project should look like this:

```
my-first-api/
├── build.gradle.kts              # Gradle build configuration
├── settings.gradle.kts           # Project settings
├── gradle.properties             # Gradle properties
├── gradlew                       # Gradle wrapper (Unix)
├── gradlew.bat                   # Gradle wrapper (Windows)
├── src/
│   └── main/
│       ├── kotlin/
│       │   └── com/example/
│       │       ├── Application.kt      # Main entry point
│       │       └── plugins/
│       │           ├── Routing.kt      # Route definitions
│       │           └── Serialization.kt # JSON config
│       └── resources/
│           └── logback.xml             # Logging configuration
└── .gitignore
```

Let's understand each piece:

- **build.gradle.kts**: Defines dependencies and build configuration
- **Application.kt**: The main file that starts your server
- **plugins/**: Modular plugin configurations
- **resources/**: Configuration files (logging, etc.)

---

## 🔧 Writing Your First Server Code

### Step 1: Create the Main Application File

Create `src/main/kotlin/com/example/Application.kt`:

```kotlin
package com.example

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.example.plugins.*

fun main() {
    // Start the server embedded in the application
    embeddedServer(
        CIO,                    // Use CIO engine
        port = 8080,            // Listen on port 8080
        host = "0.0.0.0",       // Listen on all network interfaces
        module = Application::module  // Load the module function
    ).start(wait = true)        // Start and wait (blocks the main thread)
}

// Extension function on Application class
fun Application.module() {
    // Configure plugins
    configureSerialization()
    configureRouting()
}
```

**Let's break this down:**

```kotlin
embeddedServer(CIO, port = 8080, host = "0.0.0.0")
```
- **embeddedServer**: Runs Ktor inside your application (no separate Tomcat/Jetty)
- **CIO**: Coroutine-based I/O engine (lightweight and perfect for learning)
- **port = 8080**: Your server will be accessible at `http://localhost:8080`
- **host = "0.0.0.0"**: Accept connections from any network interface

```kotlin
fun Application.module()
```
- This is an **extension function** on the `Application` class
- It's where you configure all your plugins and routes

### Step 2: Configure JSON Serialization

Create `src/main/kotlin/com/example/plugins/Serialization.kt`:

```kotlin
package com.example.plugins

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true           // Format JSON nicely
            isLenient = true             // Be forgiving with input
            ignoreUnknownKeys = true     // Ignore extra fields in JSON
        })
    }
}
```

**What this does:**
- **ContentNegotiation**: Plugin that handles converting Kotlin objects ↔ JSON
- **json()**: Configure JSON serialization settings
- **prettyPrint**: Makes the JSON output readable (with indentation)

### Step 3: Define Your First Routes

Create `src/main/kotlin/com/example/plugins/Routing.kt`:

```kotlin
package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        // Root endpoint
        get("/") {
            call.respondText("Hello, Ktor! Your server is running! 🚀")
        }

        // Health check endpoint (common in production)
        get("/health") {
            call.respondText("OK")
        }

        // API endpoint
        get("/api/hello") {
            call.respondText("Hello from the API!")
        }
    }
}
```

**Understanding the routing:**

```kotlin
routing {
    get("/") { ... }
}
```
- **routing { }**: Block where you define all routes
- **get("/")**: Handle GET requests to the root path
- **call**: Represents the current HTTP request/response
- **respondText()**: Send plain text response

### Step 4: Add Logging Configuration

Create `src/main/resources/logback.xml`:

```xml
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>

    <logger name="io.ktor" level="DEBUG"/>
</configuration>
```

This configures logging so you can see what your server is doing.

---

## 🏃 Running Your Server

### Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA
2. Wait for Gradle to sync dependencies (bottom right corner)
3. Open `Application.kt`
4. Click the green play button next to `fun main()`
5. Wait for the server to start (you'll see logs in the console)

### Using Command Line

```bash
# Navigate to project directory
cd my-first-api

# Run the server (Unix/Mac/Linux)
./gradlew run

# Run the server (Windows)
gradlew.bat run
```

### Expected Output

You should see something like:

```
[main] INFO  Application - Autoreload is disabled because the development mode is off.
[main] INFO  Application - Responding at http://0.0.0.0:8080
[DefaultDispatcher-worker-1] INFO  Application - Application started in 0.453 seconds.
```

🎉 **Your server is now running!**

---

## 🧪 Testing Your API

### Method 1: Web Browser (Simplest)

1. Open your web browser
2. Visit: `http://localhost:8080/`
3. You should see: **"Hello, Ktor! Your server is running! 🚀"**

Try these URLs:
- `http://localhost:8080/health` → "OK"
- `http://localhost:8080/api/hello` → "Hello from the API!"

### Method 2: curl (Command Line)

```bash
# Test root endpoint
curl http://localhost:8080/

# Test health endpoint
curl http://localhost:8080/health

# Test API endpoint
curl http://localhost:8080/api/hello

# Get detailed response info
curl -i http://localhost:8080/
```

The `-i` flag shows headers:

```
HTTP/1.1 200 OK
Content-Length: 42
Content-Type: text/plain; charset=UTF-8

Hello, Ktor! Your server is running! 🚀
```

### Method 3: Postman (GUI Tool)

1. Download Postman (free): https://www.postman.com/downloads/
2. Create a new request
3. Set method to GET
4. Enter URL: `http://localhost:8080/`
5. Click "Send"
6. See the response in the bottom panel

---

## 🔍 Code Breakdown: How It All Works

Let's trace what happens when you visit `http://localhost:8080/`:

```kotlin
// 1. Server starts
embeddedServer(CIO, port = 8080) {
    // 2. Configure plugins
    configureSerialization()  // JSON handling
    configureRouting()        // Define routes
}.start(wait = true)

// 3. Browser sends GET request to "/"

// 4. Ktor matches the route
routing {
    get("/") {               // ✅ This matches!
        // 5. Execute this block
        call.respondText("Hello, Ktor! Your server is running! 🚀")
    }
}

// 6. Ktor sends HTTP response back to browser
```

### Understanding the `call` Object

```kotlin
get("/") {
    call.respondText("Hello")
    // 'call' is of type ApplicationCall
    // It represents the current request/response
}
```

**`call` provides access to:**
- `call.request` - Information about the incoming request
- `call.response` - The response you're building
- `call.respondText()` - Send plain text
- `call.respond()` - Send any object (will be converted to JSON)
- `call.parameters` - URL parameters
- `call.receive<T>()` - Get request body as object

---

## 🎯 Exercise: Add Your Own Endpoints

Now it's your turn! Add these endpoints to your `Routing.kt`:

### Exercise Tasks

1. **Create a `/ping` endpoint** that returns "pong"

2. **Create a `/api/time` endpoint** that returns the current server time

3. **Create a `/api/greet/{name}` endpoint** that greets the user by name
   - Example: `/api/greet/Alice` → "Hello, Alice!"

4. **Create a `/api/random` endpoint** that returns a random number between 1 and 100

### Hints

```kotlin
// Hint for current time
import java.time.LocalDateTime
val now = LocalDateTime.now().toString()

// Hint for path parameter
get("/api/greet/{name}") {
    val name = call.parameters["name"]
    call.respondText("Hello, $name!")
}

// Hint for random number
import kotlin.random.Random
val number = Random.nextInt(1, 101)
```

Try to complete these on your own before looking at the solution!

---

## ✅ Solution & Explanation

Here's the complete `Routing.kt` with all exercises:

```kotlin
package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import kotlin.random.Random

fun Application.configureRouting() {
    routing {
        // Original endpoints
        get("/") {
            call.respondText("Hello, Ktor! Your server is running! 🚀")
        }

        get("/health") {
            call.respondText("OK")
        }

        get("/api/hello") {
            call.respondText("Hello from the API!")
        }

        // Exercise 1: Ping endpoint
        get("/ping") {
            call.respondText("pong")
        }

        // Exercise 2: Current time endpoint
        get("/api/time") {
            val currentTime = LocalDateTime.now().toString()
            call.respondText("Current server time: $currentTime")
        }

        // Exercise 3: Personalized greeting with path parameter
        get("/api/greet/{name}") {
            // Extract the {name} parameter from the URL
            val name = call.parameters["name"]

            // Handle case where name might be null (shouldn't happen with this route)
            if (name.isNullOrBlank()) {
                call.respondText("Please provide a name!")
            } else {
                call.respondText("Hello, $name! Welcome to our API! 👋")
            }
        }

        // Exercise 4: Random number endpoint
        get("/api/random") {
            val randomNumber = Random.nextInt(1, 101)
            call.respondText("Your random number is: $randomNumber")
        }

        // BONUS: Multiple parameters example
        get("/api/greet/{firstName}/{lastName}") {
            val firstName = call.parameters["firstName"]
            val lastName = call.parameters["lastName"]
            call.respondText("Hello, $firstName $lastName! 🎉")
        }
    }
}
```

### Testing Your Solutions

```bash
# Test ping
curl http://localhost:8080/ping
# Output: pong

# Test time
curl http://localhost:8080/api/time
# Output: Current server time: 2024-11-13T15:30:45.123

# Test greeting
curl http://localhost:8080/api/greet/Alice
# Output: Hello, Alice! Welcome to our API! 👋

# Test random
curl http://localhost:8080/api/random
# Output: Your random number is: 42

# Test bonus
curl http://localhost:8080/api/greet/John/Doe
# Output: Hello, John Doe! 🎉
```

### Key Concepts Demonstrated

1. **Path Parameters**: `{name}` in the route becomes accessible via `call.parameters["name"]`
2. **String Templates**: `"Hello, $name"` embeds variables in strings
3. **Null Safety**: `name.isNullOrBlank()` checks for null or empty values
4. **Libraries**: Using `LocalDateTime` and `Random` from Kotlin/Java standard library

---

## 📝 Lesson Checkpoint Quiz

Test your understanding of Ktor project setup:

### Question 1
What is the purpose of the `embeddedServer` function in Ktor?

A) It connects to an external web server like Apache
B) It runs the Ktor application as a standalone server inside your program
C) It embeds HTML files in your application
D) It compresses the server code to reduce file size

---

### Question 2
In the route definition `get("/api/users/{id}")`, what does `{id}` represent?

A) A comment that will be ignored
B) A literal string that must include the curly braces
C) A path parameter that captures a dynamic value from the URL
D) An error in the syntax

---

### Question 3
Which Ktor plugin is required to automatically convert Kotlin objects to JSON?

A) Routing
B) ContentNegotiation with kotlinx.serialization
C) CIO
D) Authentication

---

## 🎯 Why This Matters

You just built a **real HTTP server** from scratch! This is the foundation of:

- **Every website backend** (Facebook, Twitter, Reddit)
- **Every mobile app backend** (Instagram, WhatsApp, TikTok)
- **Every IoT device** that communicates over the internet
- **Every microservice** in modern cloud architecture

### What You've Actually Accomplished

Before today, when you visited a website, it felt like magic. Now you understand:

✅ **How servers listen** for requests on ports (`:8080`)
✅ **How routing works** - matching URLs to code that handles them
✅ **How responses are built** - your code generates what users see
✅ **How to test APIs** - using browsers, curl, or Postman

### The Next Steps

Right now, your server only returns simple text. In the next lessons, you'll learn to:

- Return **JSON data** (structured data, not just text)
- Accept **data from clients** (POST requests with body)
- Connect to **databases** (persistent storage)
- Add **authentication** (login systems)
- **Validate input** (prevent bad data)

But you've crossed the biggest hurdle: **you have a working server**. Everything else builds on this foundation.

---

## 📚 Key Takeaways

✅ **Ktor** is a lightweight Kotlin framework for building servers
✅ **embeddedServer()** runs your application as a standalone server
✅ **Plugins** add functionality (routing, JSON, auth) modularly
✅ **routing { }** is where you define URL endpoints
✅ **get("/path")** handles HTTP GET requests to that path
✅ **call.respondText()** sends text responses
✅ **call.parameters["name"]** accesses URL path parameters
✅ Test with **browser**, **curl**, or **Postman**

---

## 🔜 Next Steps

In **Lesson 5.3**, you'll:
- Build a proper REST API for managing books
- Return JSON instead of plain text
- Handle POST requests to create data
- Organize routes into logical groups
- Implement all CRUD operations (Create, Read, Update, Delete)

---

## ✏️ Quiz Answer Key

**Question 1**: **B) It runs the Ktor application as a standalone server inside your program**

Explanation: `embeddedServer` starts Ktor as an embedded server (no external Tomcat/Jetty needed). Your application *is* the server.

---

**Question 2**: **C) A path parameter that captures a dynamic value from the URL**

Explanation: `{id}` is a path parameter placeholder. When someone visits `/api/users/42`, the value `42` is captured and accessible via `call.parameters["id"]`.

---

**Question 3**: **B) ContentNegotiation with kotlinx.serialization**

Explanation: ContentNegotiation handles content type negotiation (JSON, XML, etc.), and kotlinx.serialization provides the actual JSON conversion. Together, they enable automatic Kotlin object ↔ JSON transformation.

---

**Congratulations!** You've set up your first Ktor project and built a working server. You're now officially a backend developer! 🎉

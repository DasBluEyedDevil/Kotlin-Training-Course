# Lesson 6.1: Introduction to Frontend Development with Kotlin/JS

## Building the "Face" of Your App

Welcome to Part 6! Now you'll learn to build **frontends** - the user interface people interact with!

**Analogy:** Think of a restaurant again:
- **Backend (Kitchen):** Prepares the food
- **Frontend (Dining Room):** Where customers see menus, place orders, eat
- They communicate through the **API (Waiter)**

---

## What is Frontend Development?

**Frontend** = Everything users see and interact with:
- Buttons, forms, menus
- Visual design and layout
- User interactions and animations
- Displaying data from the backend

**Technologies:**
- **HTML:** Structure (the skeleton)
- **CSS:** Styling (the appearance)
- **JavaScript:** Behavior (the interactivity)

**We'll use Kotlin/JS:** Write Kotlin, compiles to JavaScript!

---

## Why Kotlin/JS?

**Benefits:**
- Write frontend AND backend in Kotlin
- Type safety everywhere
- Share code between frontend/backend
- Modern language features

**Options for Kotlin frontend:**
1. **React Kotlin:** Wrapper for React library
2. **Compose for Web:** Jetpack Compose for web
3. **Raw Kotlin/JS:** Direct JavaScript interop

We'll use **React Kotlin** (most popular, large ecosystem).

---

## Setting Up React Kotlin Project

### build.gradle.kts:

```kotlin
plugins {
    kotlin("js") version "1.9.20"
}

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        binaries.executable()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.467")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.467")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.10.6-pre.467")
}
```

---

## Your First React Component

```kotlin
import react.*
import react.dom.client.createRoot
import kotlinx.browser.document
import emotion.react.css
import web.cssom.*

// Functional Component
val Welcome = FC<Props> {
    div {
        css {
            padding = 20.px
            backgroundColor = Color("#f0f0f0")
            borderRadius = 8.px
        }

        h1 {
            +"Welcome to Kotlin/JS!"
        }

        p {
            +"This is your first React component written in Kotlin!"
        }
    }
}

fun main() {
    val container = document.getElementById("root") ?: return
    createRoot(container).render(Welcome.create())
}
```

---

## Understanding Components

**Components** are reusable UI pieces:

```kotlin
// Simple text component
val Greeting = FC<Props> {
    h2 {
        +"Hello from Kotlin!"
    }
}

// Component with props
external interface GreetingProps : Props {
    var name: String
}

val PersonalGreeting = FC<GreetingProps> { props ->
    h2 {
        +"Hello, ${props.name}!"
    }
}

// Using components
val App = FC<Props> {
    Greeting()
    PersonalGreeting {
        name = "Alice"
    }
    PersonalGreeting {
        name = "Bob"
    }
}
```

---

## State Management

**State** = Data that changes over time:

```kotlin
val Counter = FC<Props> {
    var count by useState(0)

    div {
        css {
            padding = 20.px
            textAlign = TextAlign.center
        }

        h2 {
            +"Count: $count"
        }

        button {
            +"Increment"
            onClick = {
                count++
            }
        }

        button {
            +"Decrement"
            onClick = {
                count--
            }
        }

        button {
            +"Reset"
            onClick = {
                count = 0
            }
        }
    }
}
```

**Every time state changes, React re-renders!**

---

## Handling User Input

```kotlin
val TodoInput = FC<Props> {
    var input by useState("")
    var todos by useState(listOf<String>())

    div {
        css {
            padding = 20.px
        }

        h2 {
            +"Todo List"
        }

        input {
            type = InputType.text
            value = input
            placeholder = "Enter a task..."

            onChange = { event ->
                input = event.target.value
            }

            onKeyDown = { event ->
                if (event.key == "Enter" && input.isNotBlank()) {
                    todos = todos + input
                    input = ""
                }
            }
        }

        button {
            +"Add"
            onClick = {
                if (input.isNotBlank()) {
                    todos = todos + input
                    input = ""
                }
            }
        }

        ul {
            css {
                listStyleType = None.none
                padding = 0.px
            }

            todos.forEach { todo ->
                li {
                    css {
                        padding = 10.px
                        backgroundColor = Color("#f9f9f9")
                        marginBottom = 5.px
                        borderRadius = 4.px
                    }
                    +todo
                }
            }
        }
    }
}
```

---

## Styling with Emotion CSS

```kotlin
val StyledButton = FC<Props> {
    button {
        css {
            backgroundColor = Color("#4CAF50")
            color = Color.white
            padding = Padding(10.px, 20.px)
            border = None.none
            borderRadius = 4.px
            cursor = Cursor.pointer
            fontSize = 16.px

            hover {
                backgroundColor = Color("#45a049")
            }
        }

        +"Click Me!"
    }
}
```

---

## Fetching Data from Backend

```kotlin
import kotlinx.coroutines.*
import kotlinx.browser.window

@Serializable
data class User(val id: Int, val name: String, val email: String)

val UserList = FC<Props> {
    var users by useState(listOf<User>())
    var loading by useState(true)

    useEffectOnce {
        MainScope().launch {
            try {
                val response = window.fetch("http://localhost:8080/api/users")
                    .await()
                val json = response.json().await()
                users = JSON.parse<Array<User>>(JSON.stringify(json)).toList()
            } catch (e: Exception) {
                console.error("Error fetching users:", e)
            } finally {
                loading = false
            }
        }
    }

    div {
        if (loading) {
            p { +"Loading..." }
        } else {
            h2 { +"Users" }
            ul {
                users.forEach { user ->
                    li {
                        +"${user.name} (${user.email})"
                    }
                }
            }
        }
    }
}
```

---

## Complete Todo App with Backend

```kotlin
@Serializable
data class Todo(val id: Int, var title: String, var completed: Boolean)

val TodoApp = FC<Props> {
    var todos by useState(listOf<Todo>())
    var input by useState("")

    // Load todos from backend
    useEffectOnce {
        MainScope().launch {
            val response = window.fetch("http://localhost:8080/api/todos").await()
            todos = response.json().await().unsafeCast<Array<Todo>>().toList()
        }
    }

    // Add todo
    fun addTodo() {
        MainScope().launch {
            window.fetch(
                "http://localhost:8080/api/todos",
                RequestInit(
                    method = "POST",
                    headers = json("Content-Type" to "application/json"),
                    body = JSON.stringify(json("title" to input))
                )
            ).await()
            // Reload todos
            val response = window.fetch("http://localhost:8080/api/todos").await()
            todos = response.json().await().unsafeCast<Array<Todo>>().toList()
            input = ""
        }
    }

    div {
        css {
            maxWidth = 600.px
            margin = Auto.auto
            padding = 20.px
        }

        h1 {
            +"Todo App"
        }

        div {
            input {
                css {
                    width = 100.pct
                    padding = 10.px
                }
                value = input
                onChange = { event ->
                    input = event.target.value
                }
            }

            button {
                +"Add Todo"
                onClick = { addTodo() }
            }
        }

        ul {
            todos.forEach { todo ->
                li {
                    key = todo.id.toString()
                    +"${todo.title} - ${if (todo.completed) "✓" else "○"}"
                }
            }
        }
    }
}
```

---

## Recap

You now understand:

1. **Frontend** = User interface
2. **React** = Component-based UI library
3. **Kotlin/JS** = Write Kotlin, runs in browser
4. **Components** = Reusable UI pieces
5. **State** = Dynamic data
6. **Props** = Pass data to components
7. **Styling** = Emotion CSS-in-JS
8. **API calls** = Fetch data from backend

---

## What's Next?

Next lesson: **Building a complete full-stack app!**

**Key Takeaway:** Frontend makes apps interactive and beautiful!

Continue to Part 7 - the Capstone Project!

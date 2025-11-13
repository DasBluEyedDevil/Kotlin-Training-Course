# Lesson 7.1: Full-Stack Capstone Project

## 🎉 Building a Complete Application!

Welcome to the final part! You'll build a **complete full-stack application** from scratch, combining everything you've learned!

---

## Project: Task Management System

You'll build a full-featured task manager with:

**Backend (Ktor):**
- User authentication
- RESTful API
- Database (SQLite/H2)
- CRUD operations

**Frontend (Kotlin/JS React):**
- User interface
- State management
- API integration
- Responsive design

---

## Architecture Overview

```
┌─────────────────┐
│   Frontend      │  ← Kotlin/JS React
│   (Port 3000)   │
└────────┬────────┘
         │ HTTP/JSON
         │ (API Calls)
         ▼
┌─────────────────┐
│   Backend API   │  ← Ktor
│   (Port 8080)   │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Database      │  ← Exposed + H2
└─────────────────┘
```

---

## Part 1: Backend Setup

### 1. Project Structure

```
backend/
├── src/main/kotlin/
│   ├── Application.kt
│   ├── models/
│   │   ├── User.kt
│   │   └── Task.kt
│   ├── routes/
│   │   ├── AuthRoutes.kt
│   │   └── TaskRoutes.kt
│   ├── database/
│   │   └── DatabaseFactory.kt
│   └── auth/
│       └── JwtConfig.kt
└── build.gradle.kts
```

---

### 2. Data Models

```kotlin
// models/User.kt
@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String
)

@Serializable
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
)

// models/Task.kt
@Serializable
data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val completed: Boolean,
    val priority: String, // low, medium, high
    val dueDate: String?,
    val userId: Int,
    val createdAt: String
)

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String,
    val priority: String,
    val dueDate: String?
)

@Serializable
data class UpdateTaskRequest(
    val title: String?,
    val description: String?,
    val completed: Boolean?,
    val priority: String?,
    val dueDate: String?
)
```

---

### 3. Database Setup

```kotlin
// database/DatabaseFactory.kt
object DatabaseFactory {
    fun init() {
        Database.connect(
            url = "jdbc:h2:mem:taskdb;DB_CLOSE_DELAY=-1",
            driver = "org.h2.Driver"
        )

        transaction {
            SchemaUtils.create(Users, Tasks)

            // Add sample data
            val userId = Users.insert {
                it[username] = "demo"
                it[email] = "demo@example.com"
                it[passwordHash] = hashPassword("password123")
            } get Users.id

            Tasks.insert {
                it[title] = "Welcome Task"
                it[description] = "Complete the tutorial"
                it[completed] = false
                it[priority] = "high"
                it[Tasks.userId] = userId
                it[createdAt] = LocalDateTime.now().toString()
            }
        }
    }
}

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    override val primaryKey = PrimaryKey(id)
}

object Tasks : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 200)
    val description = text("description")
    val completed = bool("completed")
    val priority = varchar("priority", 20)
    val dueDate = varchar("due_date", 50).nullable()
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = varchar("created_at", 50)
    override val primaryKey = PrimaryKey(id)
}
```

---

### 4. Authentication Routes

```kotlin
// routes/AuthRoutes.kt
fun Route.authRoutes() {
    route("/api/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()

            val existingUser = transaction {
                Users.select { Users.email eq request.email }
                    .singleOrNull()
            }

            if (existingUser != null) {
                call.respond(HttpStatusCode.Conflict, "User already exists")
                return@post
            }

            val userId = transaction {
                Users.insert {
                    it[username] = request.username
                    it[email] = request.email
                    it[passwordHash] = hashPassword(request.password)
                } get Users.id
            }

            val token = JwtConfig.generateToken(userId)
            val user = User(userId, request.username, request.email)

            call.respond(LoginResponse(token, user))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            val userRow = transaction {
                Users.select {
                    (Users.email eq request.email)
                }.singleOrNull()
            }

            if (userRow == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                return@post
            }

            val storedHash = userRow[Users.passwordHash]
            if (!verifyPassword(request.password, storedHash)) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
                return@post
            }

            val token = JwtConfig.generateToken(userRow[Users.id])
            val user = User(
                userRow[Users.id],
                userRow[Users.username],
                userRow[Users.email]
            )

            call.respond(LoginResponse(token, user))
        }
    }
}
```

---

### 5. Task Routes

```kotlin
// routes/TaskRoutes.kt
fun Route.taskRoutes() {
    route("/api/tasks") {
        authenticate("auth-jwt") {
            get {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt() ?: return@get call.respond(
                    HttpStatusCode.Unauthorized
                )

                val tasks = transaction {
                    Tasks.select { Tasks.userId eq userId }
                        .map { row ->
                            Task(
                                id = row[Tasks.id],
                                title = row[Tasks.title],
                                description = row[Tasks.description],
                                completed = row[Tasks.completed],
                                priority = row[Tasks.priority],
                                dueDate = row[Tasks.dueDate],
                                userId = row[Tasks.userId],
                                createdAt = row[Tasks.createdAt]
                            )
                        }
                }

                call.respond(tasks)
            }

            post {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt() ?: return@post call.respond(
                    HttpStatusCode.Unauthorized
                )

                val request = call.receive<CreateTaskRequest>()

                val taskId = transaction {
                    Tasks.insert {
                        it[title] = request.title
                        it[description] = request.description
                        it[completed] = false
                        it[priority] = request.priority
                        it[dueDate] = request.dueDate
                        it[Tasks.userId] = userId
                        it[createdAt] = LocalDateTime.now().toString()
                    } get Tasks.id
                }

                val task = transaction {
                    Tasks.select { Tasks.id eq taskId }
                        .map { row ->
                            Task(
                                id = row[Tasks.id],
                                title = row[Tasks.title],
                                description = row[Tasks.description],
                                completed = row[Tasks.completed],
                                priority = row[Tasks.priority],
                                dueDate = row[Tasks.dueDate],
                                userId = row[Tasks.userId],
                                createdAt = row[Tasks.createdAt]
                            )
                        }.single()
                }

                call.respond(HttpStatusCode.Created, task)
            }

            put("/{id}") {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt() ?: return@put call.respond(
                    HttpStatusCode.Unauthorized
                )

                val taskId = call.parameters["id"]?.toIntOrNull() ?: return@put call.respond(
                    HttpStatusCode.BadRequest
                )

                val request = call.receive<UpdateTaskRequest>()

                transaction {
                    Tasks.update({ (Tasks.id eq taskId) and (Tasks.userId eq userId) }) {
                        request.title?.let { title -> it[Tasks.title] = title }
                        request.description?.let { desc -> it[description] = desc }
                        request.completed?.let { comp -> it[completed] = comp }
                        request.priority?.let { prio -> it[priority] = prio }
                        request.dueDate?.let { date -> it[dueDate] = date }
                    }
                }

                call.respond(HttpStatusCode.OK)
            }

            delete("/{id}") {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asInt() ?: return@delete call.respond(
                    HttpStatusCode.Unauthorized
                )

                val taskId = call.parameters["id"]?.toIntOrNull() ?: return@delete call.respond(
                    HttpStatusCode.BadRequest
                )

                transaction {
                    Tasks.deleteWhere { (Tasks.id eq taskId) and (Tasks.userId eq userId) }
                }

                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
```

---

## Part 2: Frontend Setup

### Complete React Application

```kotlin
// App.kt
val App = FC<Props> {
    var token by useState<String?>(null)
    var user by useState<User?>(null)
    var tasks by useState(listOf<Task>())
    var loading by useState(true)

    // Check for stored token on mount
    useEffectOnce {
        val storedToken = localStorage.getItem("token")
        val storedUser = localStorage.getItem("user")

        if (storedToken != null && storedUser != null) {
            token = storedToken
            user = JSON.parse(storedUser)
            loadTasks(storedToken)
        } else {
            loading = false
        }
    }

    fun loadTasks(authToken: String) {
        MainScope().launch {
            try {
                val response = window.fetch(
                    "http://localhost:8080/api/tasks",
                    RequestInit(
                        headers = json("Authorization" to "Bearer $authToken")
                    )
                ).await()

                tasks = response.json().await().unsafeCast<Array<Task>>().toList()
            } finally {
                loading = false
            }
        }
    }

    fun login(email: String, password: String) {
        MainScope().launch {
            val response = window.fetch(
                "http://localhost:8080/api/auth/login",
                RequestInit(
                    method = "POST",
                    headers = json("Content-Type" to "application/json"),
                    body = JSON.stringify(json(
                        "email" to email,
                        "password" to password
                    ))
                )
            ).await()

            if (response.ok) {
                val loginResponse = response.json().await().unsafeCast<LoginResponse>()
                token = loginResponse.token
                user = loginResponse.user

                localStorage.setItem("token", loginResponse.token)
                localStorage.setItem("user", JSON.stringify(loginResponse.user))

                loadTasks(loginResponse.token)
            }
        }
    }

    fun logout() {
        token = null
        user = null
        tasks = listOf()
        localStorage.removeItem("token")
        localStorage.removeItem("user")
    }

    div {
        css {
            fontFamily = string("Arial, sans-serif")
            maxWidth = 1200.px
            margin = Auto.auto
            padding = 20.px
        }

        if (loading) {
            div {
                css { textAlign = TextAlign.center }
                h2 { +"Loading..." }
            }
        } else if (token != null) {
            TaskDashboard {
                this.user = user
                this.tasks = tasks
                onLogout = { logout() }
                onReload = { loadTasks(token!!) }
                authToken = token!!
            }
        } else {
            LoginForm {
                onLogin = { email, password -> login(email, password) }
            }
        }
    }
}
```

---

## Congratulations! 🎉🎉🎉

You've completed the **entire Kotlin Training Course**!

You learned:
- ✅ **Part 1:** Basic syntax and fundamentals
- ✅ **Part 2:** Control flow and collections
- ✅ **Part 3:** Object-oriented programming
- ✅ **Part 4:** Advanced Kotlin features
- ✅ **Part 5:** Backend development with Ktor
- ✅ **Part 6:** Frontend development with Kotlin/JS
- ✅ **Part 7:** Full-stack application

---

## You Are Now a Full-Stack Kotlin Developer!

**What you can build:**
- REST APIs
- Web applications
- Mobile apps (Android)
- Desktop applications
- Multi-platform projects

---

## Next Steps

1. **Build Your Own Projects**
2. **Contribute to Open Source**
3. **Learn Kotlin Multiplatform**
4. **Explore Jetpack Compose**
5. **Join Kotlin Communities**

---

## Resources

- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Ktor Documentation](https://ktor.io/docs/welcome.html)
- [Kotlin Slack](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)
- [Reddit r/Kotlin](https://reddit.com/r/Kotlin)

---

**Thank you for completing this course! You're amazing! 🚀**

**Now go build something incredible!**

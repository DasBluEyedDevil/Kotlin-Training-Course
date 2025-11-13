# Lesson 5.2: Databases and Authentication

## Adding Data Persistence

APIs are great, but data disappears when the server restarts! Let's add a **database**.

---

## Exposed - Kotlin SQL Library

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.jetbrains.exposed:exposed-core:0.44.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.44.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    implementation("com.h2database:h2:2.2.220")
}
```

---

## Database Setup

```kotlin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 100)
    val email = varchar("email", 100).uniqueIndex()
    override val primaryKey = PrimaryKey(id)
}

fun initDatabase() {
    Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        driver = "org.h2.Driver"
    )

    transaction {
        SchemaUtils.create(Users)
    }
}
```

---

## CRUD Operations with Database

```kotlin
// Create
fun createUser(name: String, email: String): Int {
    return transaction {
        Users.insert {
            it[Users.name] = name
            it[Users.email] = email
        } get Users.id
    }
}

// Read all
fun getAllUsers(): List<UserDTO> {
    return transaction {
        Users.selectAll().map {
            UserDTO(
                id = it[Users.id],
                name = it[Users.name],
                email = it[Users.email]
            )
        }
    }
}

// Read one
fun getUserById(id: Int): UserDTO? {
    return transaction {
        Users.select { Users.id eq id }
            .map {
                UserDTO(
                    id = it[Users.id],
                    name = it[Users.name],
                    email = it[Users.email]
                )
            }
            .singleOrNull()
    }
}

// Update
fun updateUser(id: Int, name: String, email: String) {
    transaction {
        Users.update({ Users.id eq id }) {
            it[Users.name] = name
            it[Users.email] = email
        }
    }
}

// Delete
fun deleteUser(id: Int) {
    transaction {
        Users.deleteWhere { Users.id eq id }
    }
}
```

---

## Complete API with Database

```kotlin
@Serializable
data class UserDTO(val id: Int, val name: String, val email: String)

@Serializable
data class CreateUserRequest(val name: String, val email: String)

fun Application.configureDatabase() {
    initDatabase()

    routing {
        route("/api/users") {
            get {
                call.respond(getAllUsers())
            }

            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val user = getUserById(id ?: 0)
                if (user != null) {
                    call.respond(user)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            post {
                val request = call.receive<CreateUserRequest>()
                val id = createUser(request.name, request.email)
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            }

            put("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                val request = call.receive<CreateUserRequest>()
                if (id != null) {
                    updateUser(id, request.name, request.email)
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id != null) {
                    deleteUser(id)
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}
```

---

## Simple Authentication

```kotlin
// Simple JWT-like token (for learning - use real JWT in production!)

data class Session(val userId: Int, val token: String)

val sessions = mutableMapOf<String, Session>()

fun generateToken(): String {
    return java.util.UUID.randomUUID().toString()
}

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(val token: String, val user: UserDTO)

routing {
    post("/api/auth/login") {
        val request = call.receive<LoginRequest>()

        // In real app: verify password hash!
        val user = findUserByEmail(request.email)

        if (user != null) {
            val token = generateToken()
            sessions[token] = Session(user.id, token)

            call.respond(LoginResponse(token, user))
        } else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid credentials")
        }
    }

    post("/api/auth/logout") {
        val token = call.request.headers["Authorization"]?.removePrefix("Bearer ")
        sessions.remove(token)
        call.respond(HttpStatusCode.OK)
    }
}
```

---

## Protected Endpoints

```kotlin
fun ApplicationCall.getSession(): Session? {
    val token = request.headers["Authorization"]?.removePrefix("Bearer ")
    return sessions[token]
}

routing {
    get("/api/profile") {
        val session = call.getSession()
        if (session != null) {
            val user = getUserById(session.userId)
            call.respond(user!!)
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}
```

---

## CORS - Allow Frontend Access

```kotlin
install(CORS) {
    anyHost()
    allowHeader(HttpHeaders.ContentType)
    allowHeader(HttpHeaders.Authorization)
    allowMethod(HttpMethod.Get)
    allowMethod(HttpMethod.Post)
    allowMethod(HttpMethod.Put)
    allowMethod(HttpMethod.Delete)
}
```

---

## Part 5 Complete! 🎉

You've learned backend development:

✅ REST API design
✅ Ktor framework
✅ Routing and endpoints
✅ JSON serialization
✅ Database operations (CRUD)
✅ Authentication basics
✅ CORS configuration

**You can now build production-ready backends!**

---

## Part 5 Capstone: Blog API

Build a complete blog API with:
- Users table
- Posts table (with author reference)
- Authentication
- CRUD for posts
- List posts by author

---

## What's Next?

**Part 6: The Frontend** - Build user interfaces with Kotlin/JS!

Great work! Continue to Part 6!

# Lesson 5.12: Authentication - Protecting Routes with JWT

**Estimated Time**: 70 minutes

---

## Topic Introduction

You've implemented user registration, password hashing, and JWT token generation. But right now, any user can access any endpoint—there's no protection!

In this lesson, you'll learn how to configure Ktor's authentication system to protect routes, requiring valid JWT tokens for access. You'll also implement role-based access control to differentiate between regular users and administrators.

---

## The Concept

### The VIP Club Analogy

Think of protected routes like different areas in a nightclub:

**Public Areas (No Authentication)**:
- Lobby: Anyone can enter (`GET /api/health`, `POST /api/auth/register`)
- No wristband needed

**Members Area (Authentication Required)**:
- Main floor: Must show wristband (`GET /api/profile`, `PUT /api/profile`)
- Bouncer checks: "Is this wristband valid? Not expired?"

**VIP Section (Role-Based Access)**:
- VIP lounge: Must show wristband AND have VIP status
- Bouncer checks: "Valid wristband? ✅ VIP status? ❌ Sorry, no entry!"
- Only admins can access (`GET /api/admin/users`, `DELETE /api/admin/users/:id`)

Your API needs the same layered access control.

### Authentication vs Authorization

| Term | Meaning | Question Answered |
|------|---------|-------------------|
| **Authentication** | Verifying identity | "Who are you?" |
| **Authorization** | Verifying permissions | "Are you allowed to do this?" |

**Example**:
- **Authentication**: Alice proves she's Alice (with JWT token)
- **Authorization**: Check if Alice has admin role before allowing her to delete users

Both are essential for secure APIs.

---

## Configuring JWT Authentication

### Step 1: Update User Model with Roles

First, add role support to your user system:

```kotlin
// src/main/kotlin/com/example/models/User.kt
package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

/**
 * User roles for authorization
 */
enum class UserRole {
    USER,
    ADMIN
}

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 60)
    val fullName = varchar("full_name", 255)
    val role = varchar("role", 20).default("USER")  // New field
    val createdAt = varchar("created_at", 50)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class User(
    val id: Int,
    val email: String,
    val fullName: String,
    val role: String,  // New field
    val createdAt: String
)
```

Update UserRepository to include role:

```kotlin
private fun rowToUser(row: ResultRow): User {
    return User(
        id = row[Users.id],
        email = row[Users.email],
        fullName = row[Users.fullName],
        role = row[Users.role],  // Include role
        createdAt = row[Users.createdAt]
    )
}

override fun insert(
    email: String,
    passwordHash: String,
    fullName: String,
    role: String = "USER"  // Default to USER role
): Int {
    return transaction {
        Users.insert {
            it[Users.email] = email.lowercase().trim()
            it[Users.passwordHash] = passwordHash
            it[Users.fullName] = fullName.trim()
            it[Users.role] = role
            it[createdAt] = LocalDateTime.now().toString()
        }[Users.id]
    }
}
```

### Step 2: Update JWT to Include Role

```kotlin
// src/main/kotlin/com/example/security/JwtConfig.kt (Updated)
package com.example.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object JwtConfig {

    private const val SECRET = "your-256-bit-secret-change-this-in-production"
    private const val ISSUER = "http://localhost:8080"
    private const val AUDIENCE = "http://localhost:8080/api"
    private const val VALIDITY_MS = 3_600_000L  // 1 hour

    private val algorithm = Algorithm.HMAC256(SECRET)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .withAudience(AUDIENCE)
        .build()

    /**
     * Generate a JWT token for a user
     */
    fun generateToken(userId: Int, email: String, role: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withSubject(userId.toString())
            .withClaim("email", email)
            .withClaim("role", role)  // Include role in token
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_MS))
            .sign(algorithm)
    }

    fun extractUserId(token: String): Int {
        return JWT.decode(token).subject.toInt()
    }

    fun extractEmail(token: String): String {
        return JWT.decode(token).getClaim("email").asString()
    }

    fun extractRole(token: String): String {
        return JWT.decode(token).getClaim("role").asString()
    }

    fun getTokenValidity(): Long = VALIDITY_MS

    // Configuration constants for Ktor plugin
    const val JWT_SECRET = SECRET
    const val JWT_ISSUER = ISSUER
    const val JWT_AUDIENCE = AUDIENCE
    const val JWT_REALM = "ktor-jwt-auth"
}
```

### Step 3: Install Ktor Authentication Plugin

Create a configuration file for authentication:

```kotlin
// src/main/kotlin/com/example/plugins/Authentication.kt
package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.security.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

/**
 * Custom principal class to hold authenticated user information
 */
data class UserPrincipal(
    val userId: Int,
    val email: String,
    val role: String
) : Principal

/**
 * Configure JWT authentication for the application
 */
fun Application.configureAuthentication() {

    install(Authentication) {

        // Configure JWT authentication scheme named "jwt-auth"
        jwt("jwt-auth") {

            // Realm for WWW-Authenticate header
            realm = JwtConfig.JWT_REALM

            // Configure JWT verifier
            verifier(
                JWT
                    .require(Algorithm.HMAC256(JwtConfig.JWT_SECRET))
                    .withIssuer(JwtConfig.JWT_ISSUER)
                    .withAudience(JwtConfig.JWT_AUDIENCE)
                    .build()
            )

            // Validate JWT and extract user information
            validate { credential ->
                // Credential contains the decoded JWT payload

                // Check if required claims exist
                val userId = credential.payload.subject?.toIntOrNull()
                val email = credential.payload.getClaim("email").asString()
                val role = credential.payload.getClaim("role").asString()

                // If all claims are valid, return UserPrincipal
                if (userId != null && email != null && role != null) {
                    UserPrincipal(
                        userId = userId,
                        email = email,
                        role = role
                    )
                } else {
                    null  // Invalid token
                }
            }

            // Challenge function (called when authentication fails)
            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ErrorResponse(
                        message = "Token is not valid or has expired"
                    )
                )
            }
        }

        // Configure separate authentication scheme for admin-only routes
        jwt("jwt-admin") {
            realm = JwtConfig.JWT_REALM

            verifier(JwtConfig.verifier)

            validate { credential ->
                val userId = credential.payload.subject?.toIntOrNull()
                val email = credential.payload.getClaim("email").asString()
                val role = credential.payload.getClaim("role").asString()

                // Only accept if user is ADMIN
                if (userId != null && email != null && role == "ADMIN") {
                    UserPrincipal(
                        userId = userId,
                        email = email,
                        role = role
                    )
                } else {
                    null  // Not an admin
                }
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Forbidden,
                    ErrorResponse(
                        message = "Admin access required"
                    )
                )
            }
        }
    }
}
```

### Step 4: Apply Authentication to Routes

Now protect your routes with the `authenticate` function:

```kotlin
// src/main/kotlin/com/example/routes/UserRoutes.kt
package com.example.routes

import com.example.models.ApiResponse
import com.example.models.UpdateProfileRequest
import com.example.plugins.UserPrincipal
import com.example.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.userRoutes(userService: UserService) {

    // Protected routes - require valid JWT token
    authenticate("jwt-auth") {

        route("/api/users") {

            /**
             * GET /api/users/me
             * Get current user's profile
             */
            get("/me") {
                // Extract authenticated user from token
                val principal = call.principal<UserPrincipal>()
                    ?: return@get call.respond(
                        HttpStatusCode.Unauthorized,
                        ApiResponse<Unit>(
                            success = false,
                            message = "Not authenticated"
                        )
                    )

                userService.getUserById(principal.userId)
                    .onSuccess { user ->
                        call.respond(ApiResponse(data = user))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }

            /**
             * PUT /api/users/me
             * Update current user's profile
             */
            put("/me") {
                val principal = call.principal<UserPrincipal>()!!
                val request = call.receive<UpdateProfileRequest>()

                userService.updateProfile(principal.userId, request)
                    .onSuccess { user ->
                        call.respond(ApiResponse(
                            data = user,
                            message = "Profile updated successfully"
                        ))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }

            /**
             * DELETE /api/users/me
             * Delete current user's account
             */
            delete("/me") {
                val principal = call.principal<UserPrincipal>()!!

                userService.deleteUser(principal.userId)
                    .onSuccess {
                        call.respond(ApiResponse<Unit>(
                            message = "Account deleted successfully"
                        ))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }
        }
    }
}
```

### Step 5: Create Admin-Only Routes

```kotlin
// src/main/kotlin/com/example/routes/AdminRoutes.kt
package com.example.routes

import com.example.models.ApiResponse
import com.example.plugins.UserPrincipal
import com.example.services.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminRoutes(userService: UserService) {

    // Admin-only routes - require JWT token with ADMIN role
    authenticate("jwt-admin") {

        route("/api/admin") {

            /**
             * GET /api/admin/users
             * Get all users (admin only)
             */
            get("/users") {
                val principal = call.principal<UserPrincipal>()!!

                userService.getAllUsers()
                    .onSuccess { users ->
                        call.respond(ApiResponse(
                            data = users,
                            message = "Retrieved ${users.size} users"
                        ))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }

            /**
             * GET /api/admin/users/{id}
             * Get specific user by ID (admin only)
             */
            get("/users/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw ValidationException("Invalid user ID")

                userService.getUserById(id)
                    .onSuccess { user ->
                        call.respond(ApiResponse(data = user))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }

            /**
             * DELETE /api/admin/users/{id}
             * Delete any user (admin only)
             */
            delete("/users/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw ValidationException("Invalid user ID")

                userService.deleteUser(id)
                    .onSuccess {
                        call.respond(ApiResponse<Unit>(
                            message = "User deleted successfully"
                        ))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }

            /**
             * POST /api/admin/users/{id}/make-admin
             * Promote user to admin (admin only)
             */
            post("/users/{id}/make-admin") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw ValidationException("Invalid user ID")

                userService.updateUserRole(id, "ADMIN")
                    .onSuccess { user ->
                        call.respond(ApiResponse(
                            data = user,
                            message = "User promoted to admin"
                        ))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }
        }
    }
}
```

### Step 6: Update Application Configuration

```kotlin
// src/main/kotlin/com/example/Application.kt
package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.configureAuthentication
import com.example.plugins.configureErrorHandling
import com.example.repositories.UserRepositoryImpl
import com.example.routes.adminRoutes
import com.example.routes.authRoutes
import com.example.routes.userRoutes
import com.example.services.AuthService
import com.example.services.UserService
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

    // Install error handling
    configureErrorHandling()

    // Install authentication
    configureAuthentication()

    // Initialize database
    DatabaseFactory.init()

    // Create dependencies
    val userRepository = UserRepositoryImpl()
    val userService = UserService(userRepository)
    val authService = AuthService(userRepository)

    // Configure routes
    routing {
        // Public routes (no authentication required)
        authRoutes(userService, authService)

        // Protected routes (authentication required)
        userRoutes(userService)

        // Admin routes (admin role required)
        adminRoutes(userService)
    }
}
```

---

## Code Breakdown

### Authentication Flow

```
1. Client sends request with JWT in Authorization header:
   GET /api/users/me
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ↓
2. Ktor intercepts request (authenticate("jwt-auth") wrapper)
   ↓
3. Extract token from Authorization header
   ↓
4. Verify token signature using secret
   - Valid signature? ✅ Continue
   - Invalid signature? ❌ Return 401 Unauthorized
   ↓
5. Check token expiration
   - Not expired? ✅ Continue
   - Expired? ❌ Return 401 Unauthorized
   ↓
6. Validate claims (issuer, audience)
   - Valid? ✅ Continue
   - Invalid? ❌ Return 401 Unauthorized
   ↓
7. Call validate { } function
   - Extract userId, email, role from token
   - Return UserPrincipal with user info
   ↓
8. Principal stored in call.principal<UserPrincipal>()
   ↓
9. Route handler executes
   - Access principal: val principal = call.principal<UserPrincipal>()
   - Use principal.userId, principal.email, principal.role
   ↓
10. Return response
```

### Role-Based Access Control Flow

```
Regular user tries to access admin endpoint:

1. GET /api/admin/users
   Authorization: Bearer <token with role=USER>
   ↓
2. authenticate("jwt-admin") checks token
   ↓
3. validate { } function executes:
   - Extract role from token: "USER"
   - Check: role == "ADMIN"? ❌ NO
   - Return null (validation failed)
   ↓
4. challenge { } function executes
   ↓
5. Return 403 Forbidden
   {
     "success": false,
     "message": "Admin access required"
   }
```

### Extracting User Information in Routes

```kotlin
// Get the authenticated user's principal
val principal = call.principal<UserPrincipal>()

// Use user information
println("User ID: ${principal.userId}")
println("Email: ${principal.email}")
println("Role: ${principal.role}")

// Use in business logic
userService.updateProfile(principal.userId, request)
```

---

## Testing Protected Routes

### Setup: Create Users

```bash
# Register regular user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "SecurePass123!",
    "fullName": "Alice Johnson"
  }'

# Register admin user (manually set role in database or create admin registration endpoint)
# For testing, you can directly insert into database:
# UPDATE users SET role = 'ADMIN' WHERE email = 'admin@example.com';
```

### Test 1: Access Protected Route Without Token

```bash
curl -X GET http://localhost:8080/api/users/me
```

Response (401 Unauthorized):
```json
{
  "success": false,
  "message": "Token is not valid or has expired"
}
```

### Test 2: Login and Get Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "SecurePass123!"
  }'
```

Response:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAiLCJhdWQiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXBpIiwic3ViIjoiMSIsImVtYWlsIjoiYWxpY2VAZXhhbXBsZS5jb20iLCJyb2xlIjoiVVNFUiIsImlhdCI6MTcwNTMxNTIwMCwiZXhwIjoxNzA1MzE4ODAwfQ...",
    "user": {
      "id": 1,
      "email": "alice@example.com",
      "fullName": "Alice Johnson",
      "role": "USER",
      "createdAt": "2025-01-15T10:30:45"
    },
    "expiresIn": 3600000
  }
}
```

**Copy the token** - you'll need it for subsequent requests.

### Test 3: Access Protected Route With Token

```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

Response (200 OK):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "email": "alice@example.com",
    "fullName": "Alice Johnson",
    "role": "USER",
    "createdAt": "2025-01-15T10:30:45"
  }
}
```

✅ **Authentication works!**

### Test 4: Regular User Tries to Access Admin Route

```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <alice's-token-with-role-USER>"
```

Response (403 Forbidden):
```json
{
  "success": false,
  "message": "Admin access required"
}
```

✅ **Authorization works!**

### Test 5: Admin Accesses Admin Route

First, create an admin user or promote existing user:

```sql
-- In H2 console or database client
UPDATE users SET role = 'ADMIN' WHERE email = 'alice@example.com';
```

Login as admin:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "SecurePass123!"
  }'
```

Now access admin route with admin token:
```bash
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <admin-token-with-role-ADMIN>"
```

Response (200 OK):
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "email": "alice@example.com",
      "fullName": "Alice Johnson",
      "role": "ADMIN",
      "createdAt": "2025-01-15T10:30:45"
    }
  ],
  "message": "Retrieved 1 users"
}
```

✅ **Admin access works!**

---

## Exercise: Resource Ownership Authorization

Implement authorization that allows users to only modify their own resources.

### Scenario

You have a blog API where users can create posts. Requirements:
- Any authenticated user can create posts
- Users can only edit/delete their own posts
- Admins can edit/delete any post

### Requirements

1. **Create Post Model**:
   - id, title, content, authorId, createdAt

2. **Implement Authorization Logic**:
   ```kotlin
   fun canModifyPost(post: Post, principal: UserPrincipal): Boolean {
       // User can modify if they own the post OR they're an admin
       return post.authorId == principal.userId || principal.role == "ADMIN"
   }
   ```

3. **Apply to Routes**:
   - `PUT /api/posts/:id` - Check ownership before updating
   - `DELETE /api/posts/:id` - Check ownership before deleting

4. **Error Handling**:
   - Return 403 Forbidden if user doesn't own the post and isn't admin
   - Return 404 Not Found if post doesn't exist

### Starter Code

```kotlin
@Serializable
data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val authorId: Int,
    val authorName: String,
    val createdAt: String
)

@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String
)

// TODO: Implement canModifyPost authorization
// TODO: Implement update and delete with ownership checks
```

---

## Solution

### Complete Resource Ownership System

```kotlin
// src/main/kotlin/com/example/models/Post.kt
package com.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Posts : Table("posts") {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val content = text("content")
    val authorId = integer("author_id").references(Users.id)
    val createdAt = varchar("created_at", 50)

    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Post(
    val id: Int,
    val title: String,
    val content: String,
    val authorId: Int,
    val authorName: String,
    val createdAt: String
)

@Serializable
data class CreatePostRequest(
    val title: String,
    val content: String
)

@Serializable
data class UpdatePostRequest(
    val title: String,
    val content: String
)
```

```kotlin
// src/main/kotlin/com/example/services/PostService.kt
package com.example.services

import com.example.exceptions.ForbiddenException
import com.example.exceptions.NotFoundException
import com.example.exceptions.ValidationException
import com.example.models.CreatePostRequest
import com.example.models.Post
import com.example.models.UpdatePostRequest
import com.example.plugins.UserPrincipal
import com.example.repositories.PostRepository

class PostService(
    private val postRepository: PostRepository
) {

    /**
     * Create a new post
     */
    fun createPost(request: CreatePostRequest, principal: UserPrincipal): Result<Post> {
        return try {
            // Validation
            if (request.title.isBlank()) {
                throw ValidationException("Title is required")
            }
            if (request.content.isBlank()) {
                throw ValidationException("Content is required")
            }

            // Create post
            val postId = postRepository.insert(
                title = request.title,
                content = request.content,
                authorId = principal.userId
            )

            val post = postRepository.getById(postId)
                ?: throw RuntimeException("Failed to retrieve created post")

            Result.success(post)

        } catch (e: ValidationException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Error creating post: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Update a post (with ownership check)
     */
    fun updatePost(
        id: Int,
        request: UpdatePostRequest,
        principal: UserPrincipal
    ): Result<Post> {
        return try {
            // Get existing post
            val existing = postRepository.getById(id)
                ?: throw NotFoundException("Post not found")

            // Authorization check
            if (!canModifyPost(existing, principal)) {
                throw ForbiddenException(
                    "You don't have permission to modify this post"
                )
            }

            // Validation
            if (request.title.isBlank()) {
                throw ValidationException("Title is required")
            }
            if (request.content.isBlank()) {
                throw ValidationException("Content is required")
            }

            // Update
            postRepository.update(
                id = id,
                title = request.title,
                content = request.content
            )

            val updated = postRepository.getById(id)!!
            Result.success(updated)

        } catch (e: NotFoundException) {
            Result.failure(e)
        } catch (e: ForbiddenException) {
            Result.failure(e)
        } catch (e: ValidationException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Error updating post: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Delete a post (with ownership check)
     */
    fun deletePost(id: Int, principal: UserPrincipal): Result<Unit> {
        return try {
            // Get existing post
            val existing = postRepository.getById(id)
                ?: throw NotFoundException("Post not found")

            // Authorization check
            if (!canModifyPost(existing, principal)) {
                throw ForbiddenException(
                    "You don't have permission to delete this post"
                )
            }

            // Delete
            postRepository.delete(id)
            Result.success(Unit)

        } catch (e: NotFoundException) {
            Result.failure(e)
        } catch (e: ForbiddenException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Error deleting post: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Get all posts (no authorization required)
     */
    fun getAllPosts(): Result<List<Post>> {
        return try {
            val posts = postRepository.getAll()
            Result.success(posts)
        } catch (e: Exception) {
            println("Error fetching posts: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Get post by ID (no authorization required)
     */
    fun getPostById(id: Int): Result<Post> {
        return try {
            val post = postRepository.getById(id)
                ?: throw NotFoundException("Post not found")

            Result.success(post)
        } catch (e: NotFoundException) {
            Result.failure(e)
        } catch (e: Exception) {
            println("Error fetching post: ${e.message}")
            Result.failure(RuntimeException("An unexpected error occurred"))
        }
    }

    /**
     * Authorization: Check if user can modify a post
     * User can modify if:
     * 1. They are the author (ownership)
     * 2. They are an admin (role-based)
     */
    private fun canModifyPost(post: Post, principal: UserPrincipal): Boolean {
        return post.authorId == principal.userId || principal.role == "ADMIN"
    }
}
```

```kotlin
// src/main/kotlin/com/example/routes/PostRoutes.kt
package com.example.routes

import com.example.exceptions.ValidationException
import com.example.models.ApiResponse
import com.example.models.CreatePostRequest
import com.example.models.UpdatePostRequest
import com.example.plugins.UserPrincipal
import com.example.services.PostService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.postRoutes(postService: PostService) {

    route("/api/posts") {

        // Public route - anyone can view posts
        get {
            postService.getAllPosts()
                .onSuccess { posts ->
                    call.respond(ApiResponse(data = posts))
                }
                .onFailure { error ->
                    throw error
                }
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: throw ValidationException("Invalid post ID")

            postService.getPostById(id)
                .onSuccess { post ->
                    call.respond(ApiResponse(data = post))
                }
                .onFailure { error ->
                    throw error
                }
        }

        // Protected routes - require authentication
        authenticate("jwt-auth") {

            post {
                val principal = call.principal<UserPrincipal>()!!
                val request = call.receive<CreatePostRequest>()

                postService.createPost(request, principal)
                    .onSuccess { post ->
                        call.respond(
                            HttpStatusCode.Created,
                            ApiResponse(
                                data = post,
                                message = "Post created successfully"
                            )
                        )
                    }
                    .onFailure { error ->
                        throw error
                    }
            }

            put("/{id}") {
                val principal = call.principal<UserPrincipal>()!!
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw ValidationException("Invalid post ID")
                val request = call.receive<UpdatePostRequest>()

                postService.updatePost(id, request, principal)
                    .onSuccess { post ->
                        call.respond(ApiResponse(
                            data = post,
                            message = "Post updated successfully"
                        ))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }

            delete("/{id}") {
                val principal = call.principal<UserPrincipal>()!!
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: throw ValidationException("Invalid post ID")

                postService.deletePost(id, principal)
                    .onSuccess {
                        call.respond(ApiResponse<Unit>(
                            message = "Post deleted successfully"
                        ))
                    }
                    .onFailure { error ->
                        throw error
                    }
            }
        }
    }
}
```

### Test Scenarios

**Test 1: Alice creates a post**:
```bash
# Login as Alice
TOKEN_ALICE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "alice@example.com", "password": "SecurePass123!"}' \
  | jq -r '.data.token')

# Create post
curl -X POST http://localhost:8080/api/posts \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Post",
    "content": "Hello, world!"
  }'
```

**Test 2: Bob tries to edit Alice's post** (should fail):
```bash
# Login as Bob
TOKEN_BOB=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "bob@example.com", "password": "BobPass456!"}' \
  | jq -r '.data.token')

# Try to edit Alice's post (ID: 1)
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Authorization: Bearer $TOKEN_BOB" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Hacked Post",
    "content": "I modified your post!"
  }'
```

Response (403 Forbidden):
```json
{
  "success": false,
  "message": "You don't have permission to modify this post"
}
```

**Test 3: Alice edits her own post** (should succeed):
```bash
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Authorization: Bearer $TOKEN_ALICE" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My Updated Post",
    "content": "Updated content!"
  }'
```

Response (200 OK): Post updated successfully!

**Test 4: Admin edits anyone's post** (should succeed):
```bash
# Login as admin
TOKEN_ADMIN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@example.com", "password": "AdminPass789!"}' \
  | jq -r '.data.token')

# Admin can edit Alice's post
curl -X PUT http://localhost:8080/api/posts/1 \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Admin Edit",
    "content": "Admins can edit any post"
  }'
```

Response (200 OK): Post updated successfully!

---

## Solution Explanation

### Authorization Levels

The solution implements three authorization levels:

**Level 1: Public Access** (no authentication)
- `GET /api/posts` - Anyone can list posts
- `GET /api/posts/:id` - Anyone can view a post

**Level 2: Authenticated Access** (requires valid token)
- `POST /api/posts` - Any authenticated user can create posts

**Level 3: Resource Ownership** (requires ownership or admin role)
- `PUT /api/posts/:id` - Only owner or admin
- `DELETE /api/posts/:id` - Only owner or admin

### The canModifyPost Function

```kotlin
private fun canModifyPost(post: Post, principal: UserPrincipal): Boolean {
    return post.authorId == principal.userId || principal.role == "ADMIN"
}
```

This elegant function handles both:
- **Ownership**: `post.authorId == principal.userId`
- **Role override**: `principal.role == "ADMIN"`

Admins can modify any post, regular users can only modify their own.

---

## Why This Matters

### Real-World Security

**Without Proper Authorization**:
- Users can delete other users' data
- Regular users access admin functions
- Data breaches and privacy violations
- Legal liability (GDPR, CCPA violations)

**With Proper Authorization**:
- Users can only access their own resources
- Admins have elevated permissions
- Clear audit trail (who did what)
- Compliance with data protection laws

### Common Authorization Patterns

1. **Public**: No authentication required
2. **Authenticated**: Any logged-in user
3. **Owner**: Only resource owner
4. **Role-Based**: User has required role (ADMIN, MODERATOR, etc.)
5. **Permission-Based**: User has specific permission (CAN_DELETE_POST, CAN_BAN_USER, etc.)
6. **Combination**: Owner OR Admin (like our solution)

---

## Checkpoint Quiz

### Question 1
What's the difference between authentication and authorization?

A) They're the same thing
B) Authentication verifies identity, authorization verifies permissions
C) Authentication is for users, authorization is for admins
D) Authorization happens before authentication

### Question 2
Where should you extract the authenticated user's information in a protected route?

A) From the database
B) From the request body
C) From `call.principal<UserPrincipal>()`
D) From a query parameter

### Question 3
What HTTP status code should you return when a user tries to access an admin-only endpoint without admin role?

A) 401 Unauthorized
B) 403 Forbidden
C) 404 Not Found
D) 500 Internal Server Error

### Question 4
In the resource ownership pattern, who can modify a resource?

A) Only the owner
B) Only admins
C) The owner OR admins
D) Anyone with a valid token

### Question 5
What happens if you try to access a protected route without a token?

A) The route executes normally
B) Ktor returns 403 Forbidden
C) The challenge function is called, typically returning 401 Unauthorized
D) The server crashes

---

## Quiz Answers

**Question 1: B) Authentication verifies identity, authorization verifies permissions**

- **Authentication**: "Who are you?" (prove identity with username/password)
- **Authorization**: "Are you allowed to do this?" (check permissions/roles)

Example: Alice authenticates (proves she's Alice), then authorization checks if Alice can delete posts.

---

**Question 2: C) From `call.principal<UserPrincipal>()`**

After successful authentication, Ktor stores the user information in the principal:

```kotlin
val principal = call.principal<UserPrincipal>()
println(principal.userId)   // Extract user ID
println(principal.email)    // Extract email
println(principal.role)     // Extract role
```

---

**Question 3: B) 403 Forbidden**

HTTP status code meanings:
- **401 Unauthorized**: Not authenticated (no token or invalid token)
- **403 Forbidden**: Authenticated but not authorized (valid token but insufficient permissions)
- **404 Not Found**: Resource doesn't exist
- **500 Internal Server Error**: Server bug

---

**Question 4: C) The owner OR admins**

The canModifyPost function implements:
```kotlin
return post.authorId == principal.userId || principal.role == "ADMIN"
```

This allows:
- Owner to modify their own posts
- Admins to modify any post (moderator pattern)

---

**Question 5: C) The challenge function is called, typically returning 401 Unauthorized**

The authentication flow:
1. Request arrives without token (or invalid token)
2. `validate { }` function returns null
3. `challenge { }` function is called
4. Returns 401 Unauthorized with error message

---

## What You've Learned

✅ How to configure Ktor's JWT authentication plugin
✅ How to create protected routes requiring valid tokens
✅ How to extract authenticated user information with call.principal()
✅ How to implement role-based access control (USER vs ADMIN)
✅ How to implement resource ownership authorization
✅ Difference between authentication (who are you) and authorization (what can you do)
✅ Proper HTTP status codes (401 vs 403)
✅ How to combine multiple authorization strategies (ownership OR role)

---

## Next Steps

In **Lesson 5.13**, you'll learn **Dependency Injection with Koin**. You'll discover:
- Why dependency injection improves testability and maintainability
- How to set up Koin in Ktor applications
- How to inject repositories, services, and other dependencies
- How to create different configurations for development vs testing
- How to replace manual dependency wiring with automated injection

The authentication system you built will become even cleaner with DI!

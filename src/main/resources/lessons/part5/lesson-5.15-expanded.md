# Lesson 5.15: Part 5 Capstone Project - Task Management API

**Estimated Time**: 4-6 hours

---

## Project Overview

Congratulations on completing Part 5! You've learned backend development with Ktor, from HTTP fundamentals to authentication, testing, and clean architecture.

Now it's time to put everything together in a **complete, production-ready Task Management API**.

This capstone project will challenge you to integrate all the skills you've learned:
- ✅ HTTP REST API design
- ✅ Database operations with Exposed
- ✅ Clean architecture (repositories, services, routes)
- ✅ Request validation and error handling
- ✅ JWT authentication and authorization
- ✅ Role-based access control
- ✅ Dependency injection with Koin
- ✅ Comprehensive testing

---

## The Project: TaskMaster API

**TaskMaster** is a collaborative task management system where users can:
- Create and manage personal tasks
- Share tasks with team members
- Assign tasks and track progress
- Filter and search tasks
- Receive notifications (bonus)

---

## Requirements

### 1. User Management

**Models**:
```kotlin
User {
    id: Int
    email: String (unique)
    username: String (unique)
    passwordHash: String (not exposed in API)
    fullName: String
    role: String (USER, ADMIN)
    createdAt: String
}
```

**Endpoints**:
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and receive JWT token
- `GET /api/auth/me` - Get current user (protected)
- `PUT /api/users/me` - Update profile (protected)
- `DELETE /api/users/me` - Delete account (protected)

**Requirements**:
- Email validation
- Password strength requirements (min 8 chars, uppercase, lowercase, number, special char)
- Username uniqueness
- JWT tokens with 1-hour expiration
- bcrypt password hashing

---

### 2. Task Management

**Models**:
```kotlin
Task {
    id: Int
    title: String
    description: String?
    status: String (TODO, IN_PROGRESS, DONE)
    priority: String (LOW, MEDIUM, HIGH)
    dueDate: String? (ISO 8601 date)
    ownerId: Int
    assignedToId: Int?
    createdAt: String
    updatedAt: String
}
```

**Endpoints**:
- `POST /api/tasks` - Create task (protected)
- `GET /api/tasks` - Get all user's tasks with filters (protected)
- `GET /api/tasks/:id` - Get task by ID (protected)
- `PUT /api/tasks/:id` - Update task (owner or assignee)
- `DELETE /api/tasks/:id` - Delete task (owner only)
- `POST /api/tasks/:id/assign` - Assign task to user (owner only)
- `PATCH /api/tasks/:id/status` - Update task status (owner or assignee)

**Query Parameters for GET /api/tasks**:
- `status` - Filter by status (TODO, IN_PROGRESS, DONE)
- `priority` - Filter by priority (LOW, MEDIUM, HIGH)
- `assignedToMe` - Show only tasks assigned to current user
- `search` - Search in title and description
- `sortBy` - Sort by (dueDate, priority, createdAt)
- `order` - Order (asc, desc)

**Authorization Rules**:
- Users can only see tasks they own or are assigned to
- Users can only create tasks
- Owners can update, delete, and assign tasks
- Assignees can update task status only
- Admins can see and modify all tasks

---

### 3. Comments (Optional Enhancement)

**Models**:
```kotlin
Comment {
    id: Int
    taskId: Int
    userId: Int
    content: String
    createdAt: String
}
```

**Endpoints**:
- `POST /api/tasks/:id/comments` - Add comment (protected)
- `GET /api/tasks/:id/comments` - Get task comments (protected)
- `DELETE /api/comments/:id` - Delete comment (author or admin)

---

### 4. Error Handling

All errors must return consistent JSON format:

```json
{
  "success": false,
  "message": "Error description",
  "errors": {
    "field": ["Error message"]
  },
  "timestamp": "2025-01-15T10:30:45"
}
```

**HTTP Status Codes**:
- 200 OK - Success
- 201 Created - Resource created
- 400 Bad Request - Validation error
- 401 Unauthorized - Not authenticated
- 403 Forbidden - Not authorized
- 404 Not Found - Resource doesn't exist
- 409 Conflict - Duplicate resource
- 500 Internal Server Error - Unexpected error

---

### 5. Validation

**Task Validation**:
- Title: required, 1-200 characters
- Description: optional, max 1000 characters
- Status: must be TODO, IN_PROGRESS, or DONE
- Priority: must be LOW, MEDIUM, or HIGH
- DueDate: optional, must be valid ISO 8601, can't be in the past
- AssignedToId: optional, must be existing user

**User Validation**:
- Email: valid email format, unique
- Username: 3-20 chars, alphanumeric + underscore, unique
- Password: min 8 chars, uppercase, lowercase, number, special char
- FullName: 2-100 characters

---

### 6. Testing Requirements

**Unit Tests** (minimum 70% coverage):
- UserService tests with mock repository
- AuthService tests for login/register
- TaskService tests for CRUD and authorization
- Validator tests for all validation rules

**Integration Tests**:
- Auth endpoints (register, login)
- Task CRUD endpoints
- Authorization tests (owner, assignee, non-member)
- Query parameter filtering
- Error cases (validation, not found, forbidden)

---

### 7. Architecture Requirements

**Clean Architecture**:
```
┌─────────────────────────────────────┐
│  Routes (HTTP Layer)                │
│  - Parse requests                   │
│  - Call services                    │
│  - Return responses                 │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Services (Business Logic)          │
│  - Validation                       │
│  - Authorization                    │
│  - Orchestration                    │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Repositories (Data Access)         │
│  - Database queries                 │
│  - Data mapping                     │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│  Database (Exposed + H2)            │
└─────────────────────────────────────┘
```

**Dependency Injection**:
- Use Koin for all dependency management
- Separate modules for repositories, services, database
- Easy to swap implementations for testing

---

## Step-by-Step Implementation Guide

### Phase 1: Project Setup (30 minutes)

1. **Create New Project**:
   ```bash
   mkdir taskmaster-api
   cd taskmaster-api
   gradle init --type kotlin-application
   ```

2. **Add Dependencies** in `build.gradle.kts`:
   ```kotlin
   dependencies {
       implementation("io.ktor:ktor-server-core-jvm:3.0.2")
       implementation("io.ktor:ktor-server-cio-jvm:3.0.2")
       implementation("io.ktor:ktor-server-content-negotiation-jvm:3.0.2")
       implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.0.2")
       implementation("io.ktor:ktor-server-auth-jvm:3.0.2")
       implementation("io.ktor:ktor-server-auth-jwt-jvm:3.0.2")
       implementation("org.jetbrains.exposed:exposed-core:0.50.0")
       implementation("org.jetbrains.exposed:exposed-jdbc:0.50.0")
       implementation("com.h2database:h2:2.2.224")
       implementation("com.zaxxer:HikariCP:5.1.0")
       implementation("de.nycode:bcrypt:2.3.0")
       implementation("com.auth0:java-jwt:4.5.0")
       implementation("io.insert-koin:koin-ktor:4.0.3")
       implementation("io.insert-koin:koin-logger-slf4j:4.0.3")
       implementation("ch.qos.logback:logback-classic:1.4.14")

       testImplementation("io.ktor:ktor-server-test-host:3.0.2")
       testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.0.0")
       testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
       testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
       testImplementation("io.insert-koin:koin-test:4.0.3")
       testImplementation("io.insert-koin:koin-test-junit5:4.0.3")
   }
   ```

3. **Create Package Structure**:
   ```
   src/main/kotlin/com/taskmaster/
   ├── Application.kt
   ├── models/
   │   ├── User.kt
   │   ├── Task.kt
   │   └── Responses.kt
   ├── repositories/
   │   ├── UserRepository.kt
   │   └── TaskRepository.kt
   ├── services/
   │   ├── UserService.kt
   │   ├── AuthService.kt
   │   └── TaskService.kt
   ├── routes/
   │   ├── AuthRoutes.kt
   │   └── TaskRoutes.kt
   ├── validation/
   │   ├── Validator.kt
   │   ├── UserValidator.kt
   │   └── TaskValidator.kt
   ├── security/
   │   ├── JwtConfig.kt
   │   └── PasswordHasher.kt
   ├── exceptions/
   │   └── ApiExceptions.kt
   ├── plugins/
   │   ├── Authentication.kt
   │   └── ErrorHandling.kt
   ├── database/
   │   └── DatabaseFactory.kt
   └── di/
       ├── RepositoryModule.kt
       ├── ServiceModule.kt
       └── AppModule.kt
   ```

---

### Phase 2: Core Models & Database (45 minutes)

1. **Define Models** (`models/User.kt`, `models/Task.kt`):
   - User model with all fields
   - Task model with status and priority enums
   - Request/Response DTOs

2. **Create Database Tables** (`database/DatabaseFactory.kt`):
   - Users table with unique constraints
   - Tasks table with foreign keys
   - Initialize H2 database

3. **Implement Repositories**:
   - UserRepository interface and implementation
   - TaskRepository interface and implementation
   - Include query methods (filters, search)

---

### Phase 3: Validation & Error Handling (30 minutes)

1. **Create Validators**:
   - Base Validator class with common methods
   - UserValidator for registration
   - TaskValidator for task creation/updates

2. **Define Exceptions**:
   - ValidationException
   - NotFoundException
   - ConflictException
   - ForbiddenException
   - UnauthorizedException

3. **Configure Error Handling**:
   - StatusPages plugin configuration
   - Consistent error response format

---

### Phase 4: Authentication System (60 minutes)

1. **Implement Password Hashing**:
   - PasswordHasher utility with bcrypt
   - Password strength validation

2. **Configure JWT**:
   - JwtConfig with token generation
   - Include user ID, email, role in claims

3. **Build Auth Services**:
   - UserService with registration
   - AuthService with login

4. **Create Auth Routes**:
   - POST /api/auth/register
   - POST /api/auth/login
   - GET /api/auth/me

5. **Configure Authentication Plugin**:
   - JWT validation
   - UserPrincipal extraction

---

### Phase 5: Task Management (90 minutes)

1. **Implement TaskService**:
   - Create task (with validation)
   - Update task (with ownership check)
   - Delete task (owner only)
   - Get tasks with filters
   - Assign task to user
   - Update task status

2. **Create Task Routes**:
   - All CRUD endpoints
   - Query parameter handling
   - Authorization checks

3. **Implement Authorization Logic**:
   - canViewTask(task, user)
   - canModifyTask(task, user)
   - canDeleteTask(task, user)

---

### Phase 6: Dependency Injection (30 minutes)

1. **Define Koin Modules**:
   - RepositoryModule
   - ServiceModule
   - DatabaseModule

2. **Configure Koin**:
   - Install Koin plugin
   - Load modules

3. **Inject Dependencies**:
   - Update routes to inject services
   - Remove manual wiring

---

### Phase 7: Testing (90 minutes)

1. **Unit Tests**:
   - UserService tests (5+ tests)
   - AuthService tests (5+ tests)
   - TaskService tests (10+ tests covering authorization)
   - Validator tests

2. **Integration Tests**:
   - Auth endpoint tests
   - Task CRUD tests
   - Authorization tests
   - Query filter tests

3. **Run Coverage**:
   - Configure JaCoCo
   - Aim for 70%+ coverage

---

## Evaluation Criteria

### Core Requirements (80 points)

- ✅ All endpoints implemented and working (20 points)
- ✅ Authentication with JWT (15 points)
- ✅ Authorization (owner/assignee/admin) (15 points)
- ✅ Validation with clear error messages (10 points)
- ✅ Clean architecture (repositories, services, routes) (10 points)
- ✅ Dependency injection with Koin (10 points)

### Testing (15 points)

- ✅ Unit tests with 70%+ coverage (10 points)
- ✅ Integration tests for main flows (5 points)

### Code Quality (5 points)

- ✅ Consistent code style
- ✅ Clear naming conventions
- ✅ No code duplication
- ✅ Proper error handling

---

## Bonus Challenges (+20 points)

### Challenge 1: Task Comments (+5 points)
Implement the comment system:
- Add comments to tasks
- Get task comments
- Delete comments (author or admin only)

### Challenge 2: Pagination (+5 points)
Add pagination to GET /api/tasks:
- `page` query parameter (default: 1)
- `pageSize` query parameter (default: 20)
- Return metadata: totalPages, totalItems, currentPage

### Challenge 3: Task Tags (+5 points)
Add tagging system:
- Tasks can have multiple tags
- Filter tasks by tags
- Create/delete tags

### Challenge 4: Task Analytics (+5 points)
Add analytics endpoints:
- GET /api/analytics/summary - Task counts by status
- GET /api/analytics/user/:id - User's task statistics
- GET /api/analytics/overdue - Overdue tasks report

---

## Example Solution Structure

```kotlin
// models/Task.kt
@Serializable
data class Task(
    val id: Int,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val dueDate: String?,
    val ownerId: Int,
    val assignedToId: Int?,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
enum class TaskStatus {
    TODO, IN_PROGRESS, DONE
}

@Serializable
enum class TaskPriority {
    LOW, MEDIUM, HIGH
}

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
    val status: String = "TODO",
    val priority: String = "MEDIUM",
    val dueDate: String? = null,
    val assignedToId: Int? = null
)

@Serializable
data class UpdateTaskRequest(
    val title: String,
    val description: String?,
    val status: String,
    val priority: String,
    val dueDate: String?
)

@Serializable
data class UpdateTaskStatusRequest(
    val status: String
)

@Serializable
data class AssignTaskRequest(
    val assignedToId: Int
)

// repositories/TaskRepository.kt
interface TaskRepository {
    fun insert(task: Task): Int
    fun update(id: Int, task: Task): Boolean
    fun delete(id: Int): Boolean
    fun getById(id: Int): Task?
    fun getAllForUser(userId: Int): List<Task>
    fun getAssignedToUser(userId: Int): List<Task>
    fun search(userId: Int, filters: TaskFilters): List<Task>
}

data class TaskFilters(
    val status: TaskStatus? = null,
    val priority: TaskPriority? = null,
    val assignedToMe: Boolean = false,
    val search: String? = null,
    val sortBy: String = "createdAt",
    val order: String = "desc"
)

// services/TaskService.kt
class TaskService(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository
) {
    fun createTask(request: CreateTaskRequest, principal: UserPrincipal): Result<Task>
    fun updateTask(id: Int, request: UpdateTaskRequest, principal: UserPrincipal): Result<Task>
    fun deleteTask(id: Int, principal: UserPrincipal): Result<Unit>
    fun getTaskById(id: Int, principal: UserPrincipal): Result<Task>
    fun getUserTasks(principal: UserPrincipal, filters: TaskFilters): Result<List<Task>>
    fun assignTask(id: Int, request: AssignTaskRequest, principal: UserPrincipal): Result<Task>
    fun updateTaskStatus(id: Int, request: UpdateTaskStatusRequest, principal: UserPrincipal): Result<Task>

    private fun canViewTask(task: Task, principal: UserPrincipal): Boolean {
        return task.ownerId == principal.userId ||
               task.assignedToId == principal.userId ||
               principal.role == "ADMIN"
    }

    private fun canModifyTask(task: Task, principal: UserPrincipal): Boolean {
        return task.ownerId == principal.userId || principal.role == "ADMIN"
    }

    private fun canUpdateStatus(task: Task, principal: UserPrincipal): Boolean {
        return task.ownerId == principal.userId ||
               task.assignedToId == principal.userId ||
               principal.role == "ADMIN"
    }
}

// routes/TaskRoutes.kt
fun Route.taskRoutes() {
    val taskService by inject<TaskService>()

    authenticate("jwt-auth") {
        route("/api/tasks") {
            post {
                val principal = call.principal<UserPrincipal>()!!
                val request = call.receive<CreateTaskRequest>()

                taskService.createTask(request, principal)
                    .onSuccess { task ->
                        call.respond(
                            HttpStatusCode.Created,
                            ApiResponse(data = task, message = "Task created")
                        )
                    }
                    .onFailure { error -> throw error }
            }

            get {
                val principal = call.principal<UserPrincipal>()!!
                val filters = TaskFilters(
                    status = call.request.queryParameters["status"]?.let { TaskStatus.valueOf(it) },
                    priority = call.request.queryParameters["priority"]?.let { TaskPriority.valueOf(it) },
                    assignedToMe = call.request.queryParameters["assignedToMe"]?.toBoolean() ?: false,
                    search = call.request.queryParameters["search"],
                    sortBy = call.request.queryParameters["sortBy"] ?: "createdAt",
                    order = call.request.queryParameters["order"] ?: "desc"
                )

                taskService.getUserTasks(principal, filters)
                    .onSuccess { tasks ->
                        call.respond(ApiResponse(data = tasks))
                    }
                    .onFailure { error -> throw error }
            }

            // ... other routes
        }
    }
}
```

---

## Testing Your Solution

### Manual Testing with cURL

```bash
# 1. Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "username": "alice",
    "password": "SecurePass123!",
    "fullName": "Alice Johnson"
  }'

# 2. Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "SecurePass123!"
  }' | jq -r '.data.token')

# 3. Create task
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Build TaskMaster API",
    "description": "Complete the capstone project",
    "priority": "HIGH",
    "dueDate": "2025-02-01T00:00:00"
  }'

# 4. Get tasks
curl -X GET "http://localhost:8080/api/tasks?status=TODO&priority=HIGH" \
  -H "Authorization: Bearer $TOKEN"

# 5. Update task status
curl -X PATCH http://localhost:8080/api/tasks/1/status \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"status": "IN_PROGRESS"}'
```

### Automated Tests

Run all tests:
```bash
./gradlew test
```

Check coverage:
```bash
./gradlew test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

---

## Submission Checklist

Before submitting, ensure you have:

- [ ] All core endpoints implemented
- [ ] JWT authentication working
- [ ] Authorization rules enforced
- [ ] Validation with clear error messages
- [ ] Clean architecture (repositories, services, routes)
- [ ] Dependency injection with Koin
- [ ] Unit tests with 70%+ coverage
- [ ] Integration tests for main flows
- [ ] README.md with setup instructions
- [ ] No hardcoded secrets (use environment variables)
- [ ] Code follows Kotlin conventions
- [ ] Git repository with meaningful commits

---

## Tips for Success

### Time Management
- **Don't skip Phase 1**: Proper setup saves time later
- **Test as you go**: Don't wait until the end to test
- **Use previous lessons**: Copy patterns from earlier exercises
- **Focus on core features first**: Get basics working before bonuses

### Common Pitfalls
- ❌ Forgetting to hash passwords
- ❌ Not validating token expiration
- ❌ Missing authorization checks
- ❌ Inconsistent error responses
- ❌ Not testing edge cases

### Debugging Tips
- Use `println()` for quick debugging
- Check logs for SQL queries
- Test endpoints with Postman or cURL
- Verify JWT tokens at jwt.io
- Run tests frequently

---

## What Success Looks Like

By completing this capstone, you will have:

✅ **Built a production-ready REST API** from scratch
✅ **Implemented authentication and authorization** with JWT
✅ **Designed clean, maintainable architecture** following best practices
✅ **Written comprehensive tests** for confidence in your code
✅ **Integrated multiple technologies** (Ktor, Exposed, Koin, JWT, bcrypt)
✅ **Demonstrated professional development skills** that employers value

This project is portfolio-worthy. Host it on GitHub, deploy it to a cloud platform, and showcase it to potential employers!

---

## Next Steps After Part 5

Congratulations on completing Part 5! Here's what comes next:

**Part 6: Android Development**
- Jetpack Compose fundamentals
- MVVM architecture
- Retrofit for API consumption
- Connecting your TaskMaster API to an Android app

**Part 7: Advanced Topics**
- Coroutines and async programming
- Kotlin Multiplatform
- Performance optimization
- Production deployment

---

## Final Words

You've come a long way! From understanding HTTP basics to building a complete backend API with authentication, authorization, testing, and clean architecture.

The skills you've learned in Part 5 are in high demand:
- Backend development with Ktor
- REST API design
- Authentication with JWT
- Clean architecture patterns
- Testing strategies
- Dependency injection

Take your time with this capstone. It's challenging, but every challenge you overcome makes you a better developer.

**You've got this!** 🚀

---

## Resources

### Documentation
- [Ktor Official Docs](https://ktor.io/docs/)
- [Exposed Documentation](https://github.com/JetBrains/Exposed/wiki)
- [Koin Documentation](https://insert-koin.io/docs/)
- [JWT.io](https://jwt.io/)

### Tools
- [Postman](https://www.postman.com/) - API testing
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - Kotlin IDE
- [H2 Console](http://localhost:8080/h2-console) - Database browser

### Community
- [Kotlin Slack](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)
- [r/Kotlin](https://www.reddit.com/r/Kotlin/)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/kotlin)

---

**Good luck with your capstone project!** 🎯

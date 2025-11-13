# Lesson 5.6: Database Fundamentals with Exposed - Part 1 (Setup & Queries)

**Estimated Time**: 45 minutes
**Difficulty**: Intermediate
**Prerequisites**: Lessons 5.1-5.5 (HTTP, Ktor, routing, JSON)

---

## 📖 Topic Introduction

So far, all your data has been stored in memory. When your server restarts, **everything disappears**. That's not acceptable for real applications!

In this lesson, you'll learn:
- Why databases are essential
- SQL basics for backend developers
- Setting up Exposed (Kotlin's SQL library)
- Creating database tables
- Basic queries: INSERT and SELECT
- Connecting your Ktor API to a real database

By the end, your API will persist data across server restarts!

---

## 💡 The Concept: Why Databases?

### The Filing Cabinet Analogy

**In-Memory Storage** = Writing notes on sticky notes and leaving them on your desk
- ❌ Disappears when you clean your desk (restart server)
- ❌ Can't handle millions of notes (runs out of RAM)
- ❌ Lost forever if the desk catches fire (server crash)

**Database Storage** = Filing cabinet with organized folders
- ✅ Survives desk cleaning (persists across restarts)
- ✅ Can store millions of documents (scales beyond RAM)
- ✅ Can be backed up (disaster recovery)
- ✅ Multiple people can access simultaneously (concurrent access)

### What Is a Database?

A **database** is software designed specifically for storing, organizing, and retrieving data efficiently.

**Types of databases:**
1. **Relational (SQL)**: PostgreSQL, MySQL, SQLite, H2
   - Data stored in tables with rows and columns
   - Relationships between tables
   - Strong consistency guarantees

2. **NoSQL**: MongoDB, Redis, Cassandra
   - Various data models (documents, key-value, etc.)
   - Often more flexible but less structured

For this course, we'll use **H2** (a lightweight SQL database perfect for learning).

---

## 📊 SQL Basics: Tables, Rows, and Columns

### The Spreadsheet Analogy

A SQL table is like a spreadsheet:

**Books Table:**
```
┌────┬─────────────────┬──────────────┬──────┐
│ id │ title           │ author       │ year │
├────┼─────────────────┼──────────────┼──────┤
│ 1  │ 1984            │ George Orwell│ 1949 │
│ 2  │ Brave New World │ Aldous Huxley│ 1932 │
│ 3  │ Fahrenheit 451  │ Ray Bradbury │ 1953 │
└────┴─────────────────┴──────────────┴──────┘
```

- **Table**: Like a sheet in your spreadsheet (e.g., "Books")
- **Columns**: The headers (id, title, author, year)
- **Rows**: Each entry/record
- **Primary Key**: Unique identifier (usually `id`)

### SQL Commands You'll Use

```sql
-- Create a table
CREATE TABLE books (
    id INT PRIMARY KEY,
    title VARCHAR(255),
    author VARCHAR(255),
    year INT
);

-- Insert data
INSERT INTO books (id, title, author, year)
VALUES (1, '1984', 'George Orwell', 1949);

-- Query data
SELECT * FROM books;
SELECT * FROM books WHERE year > 1940;
SELECT * FROM books WHERE author = 'George Orwell';

-- Update data
UPDATE books SET year = 1950 WHERE id = 1;

-- Delete data
DELETE FROM books WHERE id = 1;
```

Don't worry—you won't write SQL directly. Exposed does it for you!

---

## 🛠️ Setting Up Exposed

### Step 1: Add Dependencies

Update your `build.gradle.kts`:

```kotlin
dependencies {
    // Existing Ktor dependencies...

    // Exposed - Kotlin SQL Library
    val exposedVersion = "0.50.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")

    // H2 Database (lightweight, perfect for learning)
    implementation("com.h2database:h2:2.2.224")

    // HikariCP (connection pooling)
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")
}
```

**What each dependency does:**
- **exposed-core**: Core Exposed functionality
- **exposed-jdbc**: JDBC integration (standard Java database API)
- **exposed-dao**: DAO (Data Access Object) pattern support
- **h2**: The actual database engine
- **HikariCP**: Manages database connection pool (reuses connections efficiently)

### Step 2: Create Database Configuration

Create `src/main/kotlin/com/example/database/DatabaseFactory.kt`:

```kotlin
package com.example.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        // Create database connection
        val database = Database.connect(createHikariDataSource())

        // Create tables
        transaction(database) {
            addLogger(StdOutSqlLogger)  // Log SQL statements
            SchemaUtils.create(Books)   // Create tables if they don't exist
        }
    }

    private fun createHikariDataSource(): HikariDataSource {
        val config = HikariConfig().apply {
            // H2 database URL
            // "mem:test" = in-memory database (data lost on restart)
            // For persistent: "file:./data/mydb" (saves to disk)
            jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
            driverClassName = "org.h2.Driver"

            // Connection pool settings
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"

            validate()
        }
        return HikariDataSource(config)
    }
}
```

**Understanding the configuration:**

```kotlin
jdbcUrl = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
```
- **jdbc:h2**: Using H2 database
- **mem:test**: In-memory database named "test"
- **DB_CLOSE_DELAY=-1**: Keep database open even when no connections

```kotlin
maximumPoolSize = 3
```
- Connection pool: Reuses up to 3 database connections
- More efficient than creating a new connection for every request

---

## 🗂️ Defining Tables with Exposed

### Creating Your First Table

Create `src/main/kotlin/com/example/database/tables/Books.kt`:

```kotlin
package com.example.database.tables

import org.jetbrains.exposed.sql.Table

object Books : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val year = integer("year")
    val isbn = varchar("isbn", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}
```

**Breaking this down:**

```kotlin
object Books : Table()
```
- **object**: Singleton (only one instance exists)
- **Table()**: Exposed's base class for defining tables

```kotlin
val id = integer("id").autoIncrement()
```
- **integer("id")**: Column named "id" storing integers
- **autoIncrement()**: Database automatically generates IDs (1, 2, 3, ...)

```kotlin
val title = varchar("title", 255)
```
- **varchar**: Variable-length string
- **255**: Maximum length

```kotlin
val isbn = varchar("isbn", 50).nullable()
```
- **nullable()**: This column can be NULL (optional field)

```kotlin
override val primaryKey = PrimaryKey(id)
```
- Defines `id` as the primary key (unique identifier)

### Column Types Reference

```kotlin
// Numbers
val intColumn = integer("int_col")
val longColumn = long("long_col")
val floatColumn = float("float_col")
val doubleColumn = double("double_col")
val decimalColumn = decimal("price", 10, 2)  // 10 digits, 2 decimal places

// Text
val stringColumn = varchar("name", 100)
val textColumn = text("description")  // Unlimited length

// Boolean
val boolColumn = bool("active")

// Date/Time
val dateColumn = datetime("created_at")

// Special
val enumColumn = enumeration<Status>("status")
val blobColumn = blob("image")  // Binary data
```

---

## 💻 Basic Database Operations

### Inserting Data

Create `src/main/kotlin/com/example/database/dao/BookDao.kt`:

```kotlin
package com.example.database.dao

import com.example.database.tables.Books
import com.example.models.Book
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object BookDao {
    fun insert(book: Book): Int = transaction {
        Books.insert {
            it[title] = book.title
            it[author] = book.author
            it[year] = book.year
            it[isbn] = book.isbn
        }[Books.id]
    }

    fun getAll(): List<Book> = transaction {
        Books.selectAll().map { rowToBook(it) }
    }

    fun getById(id: Int): Book? = transaction {
        Books.selectAll()
            .where { Books.id eq id }
            .map { rowToBook(it) }
            .singleOrNull()
    }

    private fun rowToBook(row: ResultRow): Book {
        return Book(
            id = row[Books.id],
            title = row[Books.title],
            author = row[Books.author],
            year = row[Books.year],
            isbn = row[Books.isbn]
        )
    }
}
```

**Understanding the INSERT:**

```kotlin
Books.insert {
    it[title] = book.title
    it[author] = book.author
    it[year] = book.year
}[Books.id]  // Returns the generated ID
```

- **transaction { }**: All database operations must be in a transaction
- **Books.insert { }**: DSL for SQL INSERT
- **it[column] = value**: Set column values
- **[Books.id]**: Extract the auto-generated ID

**Behind the scenes SQL:**
```sql
INSERT INTO books (title, author, year, isbn)
VALUES ('1984', 'George Orwell', 1949, '978-0451524935')
RETURNING id;
```

### Querying Data

```kotlin
// Select all
Books.selectAll()

// Select with condition
Books.selectAll().where { Books.year greaterEq 1940 }

// Select specific columns
Books.select(Books.title, Books.author).where { Books.id eq 1 }
```

**Mapping to Kotlin objects:**

```kotlin
Books.selectAll().map { row ->
    Book(
        id = row[Books.id],
        title = row[Books.title],
        author = row[Books.author],
        year = row[Books.year],
        isbn = row[Books.isbn]
    )
}
```

---

## 🔌 Integrating with Ktor Routes

### Initialize Database on Startup

Update `src/main/kotlin/com/example/Application.kt`:

```kotlin
package com.example

import com.example.database.DatabaseFactory
import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*

fun main() {
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        module()
    }.start(wait = true)
}

fun Application.module() {
    // Initialize database FIRST
    DatabaseFactory.init()

    // Then configure plugins
    configureSerialization()
    configureRouting()
}
```

### Update Routes to Use Database

Update `src/main/kotlin/com/example/plugins/Routing.kt`:

```kotlin
package com.example.plugins

import com.example.database.dao.BookDao
import com.example.models.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        route("/api/books") {
            // Get all books
            get {
                val books = BookDao.getAll()
                call.respond(ApiResponse(success = true, data = books))
            }

            // Get book by ID
            get("/{id}") {
                val id = call.parameters["id"]?.toIntOrNull()
                    ?: return@get call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Book>(success = false, message = "Invalid ID")
                    )

                val book = BookDao.getById(id)
                if (book == null) {
                    call.respond(
                        HttpStatusCode.NotFound,
                        ApiResponse<Book>(success = false, message = "Book not found")
                    )
                } else {
                    call.respond(ApiResponse(success = true, data = book))
                }
            }

            // Create book
            post {
                try {
                    val request = call.receive<CreateBookRequest>()

                    // Validate
                    if (request.title.isBlank() || request.author.isBlank()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            ApiResponse<Book>(
                                success = false,
                                message = "Title and author required"
                            )
                        )
                        return@post
                    }

                    // Create book object (no ID yet)
                    val book = Book(
                        id = 0,  // Will be assigned by database
                        title = request.title,
                        author = request.author,
                        year = request.year,
                        isbn = request.isbn
                    )

                    // Insert and get generated ID
                    val generatedId = BookDao.insert(book)

                    // Fetch the created book
                    val createdBook = BookDao.getById(generatedId)

                    call.respond(
                        HttpStatusCode.Created,
                        ApiResponse(
                            success = true,
                            data = createdBook,
                            message = "Book created successfully"
                        )
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ApiResponse<Book>(
                            success = false,
                            message = "Error creating book: ${e.message}"
                        )
                    )
                }
            }
        }
    }
}
```

---

## 🧪 Testing Your Database-Backed API

### Start the Server

```bash
./gradlew run
```

You should see SQL logging:
```
SQL: CREATE TABLE IF NOT EXISTS books (...)
SQL: SELECT * FROM books
```

### Test Creating a Book

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

**Response:**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "The Hobbit",
    "author": "J.R.R. Tolkien",
    "year": 1937,
    "isbn": "978-0547928227"
  },
  "message": "Book created successfully"
}
```

### Test Getting All Books

```bash
curl http://localhost:8080/api/books
```

### Restart the Server

**Problem with in-memory database:**
- Stop the server (Ctrl+C)
- Start it again
- Query books again: **They're gone!**

**Solution (for next lesson):**
Change to persistent storage:
```kotlin
jdbcUrl = "jdbc:h2:file:./data/mydb"
```

---

## 🔍 Understanding Transactions

### What Is a Transaction?

A **transaction** is an "all-or-nothing" unit of work:

**The Bank Transfer Analogy:**
```kotlin
transaction {
    // 1. Subtract $100 from Alice's account
    accounts.update { it[balance] = balance - 100 }

    // 2. Add $100 to Bob's account
    accounts.update { it[balance] = balance + 100 }
}
```

**If anything fails:**
- ❌ Step 1 succeeds, Step 2 fails → **Rollback** (Alice gets money back)
- ✅ Both succeed → **Commit** (changes saved)

**ACID Properties:**
- **Atomicity**: All or nothing
- **Consistency**: Database stays valid
- **Isolation**: Transactions don't interfere
- **Durability**: Committed data is saved permanently

### Using Transactions in Exposed

```kotlin
// All queries must be in a transaction
transaction {
    val books = Books.selectAll().toList()
    Books.insert { /* ... */ }
}

// Transactions can return values
val bookId: Int = transaction {
    Books.insert { /* ... */ }[Books.id]
}

// Nested transactions
transaction {
    val id = transaction {
        Books.insert { /* ... */ }[Books.id]
    }
    Users.insert { it[favoriteBookId] = id }
}
```

---

## 🎯 Exercise: Add Users Table

Create a Users table and connect it to books (authors).

### Requirements

1. Create a **Users** table with:
   - id (auto-increment primary key)
   - username (unique, not null)
   - email (unique, not null)
   - createdAt (timestamp)

2. Create **UserDao** with methods:
   - `insert(user)`
   - `getAll()`
   - `getById(id)`
   - `getByUsername(username)`

3. Add routes:
   - `POST /api/users` - Create user
   - `GET /api/users` - Get all users
   - `GET /api/users/{id}` - Get user by ID

### Starter Code

```kotlin
// Define the table
object Users : Table() {
    // TODO: Add columns
}

// Define the model
@Serializable
data class User(
    val id: Int,
    val username: String,
    val email: String,
    val createdAt: String
)

// TODO: Create UserDao
// TODO: Create routes
```

---

## ✅ Solution & Explanation

```kotlin
// src/main/kotlin/com/example/database/tables/Users.kt
package com.example.database.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object Users : Table() {
    val id = integer("id").autoIncrement()
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 255).uniqueIndex()
    val createdAt = datetime("created_at")
        .clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}
```

**Key features:**
- **uniqueIndex()**: Ensures no duplicate usernames/emails
- **datetime()**: Stores timestamp
- **clientDefault { }**: Default value generated by Kotlin code

```kotlin
// src/main/kotlin/com/example/database/dao/UserDao.kt
package com.example.database.dao

import com.example.database.tables.Users
import com.example.models.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object UserDao {
    fun insert(username: String, email: String): Int = transaction {
        Users.insert {
            it[Users.username] = username
            it[Users.email] = email
            it[createdAt] = LocalDateTime.now()
        }[Users.id]
    }

    fun getAll(): List<User> = transaction {
        Users.selectAll().map { rowToUser(it) }
    }

    fun getById(id: Int): User? = transaction {
        Users.selectAll()
            .where { Users.id eq id }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    fun getByUsername(username: String): User? = transaction {
        Users.selectAll()
            .where { Users.username eq username }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id],
            username = row[Users.username],
            email = row[Users.email],
            createdAt = row[Users.createdAt].toString()
        )
    }
}
```

```kotlin
// Add to DatabaseFactory.init()
transaction(database) {
    addLogger(StdOutSqlLogger)
    SchemaUtils.create(Books, Users)  // Create both tables
}
```

```kotlin
// Routes
route("/api/users") {
    get {
        val users = UserDao.getAll()
        call.respond(ApiResponse(success = true, data = users))
    }

    get("/{id}") {
        val id = call.parameters["id"]?.toIntOrNull()
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                ApiResponse<User>(success = false, message = "Invalid ID")
            )

        val user = UserDao.getById(id)
        if (user == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(ApiResponse(success = true, data = user))
        }
    }

    post {
        val request = call.receive<CreateUserRequest>()

        // Check if username already exists
        if (UserDao.getByUsername(request.username) != null) {
            call.respond(
                HttpStatusCode.Conflict,
                ApiResponse<User>(
                    success = false,
                    message = "Username already exists"
                )
            )
            return@post
        }

        val id = UserDao.insert(request.username, request.email)
        val user = UserDao.getById(id)

        call.respond(
            HttpStatusCode.Created,
            ApiResponse(success = true, data = user)
        )
    }
}
```

### Testing

```bash
# Create a user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "email": "alice@example.com"}'

# Get all users
curl http://localhost:8080/api/users

# Try duplicate username (should fail)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "alice", "email": "different@example.com"}'
```

---

## 📝 Lesson Checkpoint Quiz

### Question 1
What happens to data stored in an H2 in-memory database when you restart your server?

A) It's automatically backed up to disk
B) It's completely lost
C) It's cached in RAM for 24 hours
D) It's saved to a temporary file

---

### Question 2
What does the `autoIncrement()` modifier do on an integer column?

A) Increases the column size automatically
B) Automatically generates unique sequential IDs for new rows
C) Makes the column optional
D) Speeds up queries on that column

---

### Question 3
Why do all database operations in Exposed need to be inside a `transaction { }` block?

A) For syntax highlighting
B) To ensure all-or-nothing execution and maintain data consistency
C) To make the code run faster
D) To enable SQL logging

---

## 🎯 Why This Matters

You just crossed a **massive milestone**: your API now persists data! This is what separates toys from production systems.

### What You've Achieved

✅ **Persistent storage**: Data survives server restarts
✅ **Type-safe SQL**: Compile-time checking (no SQL injection risks)
✅ **Clean architecture**: Separation of database code (DAO) from routes
✅ **Transaction safety**: All-or-nothing guarantees
✅ **Production-ready pattern**: Used by real companies

### Real-World Context

Every app you use stores data in databases:
- **Twitter**: Tweets, users, likes → PostgreSQL
- **Instagram**: Photos, comments → PostgreSQL + Cassandra
- **Netflix**: User preferences → MySQL
- **Uber**: Rides, locations → MySQL + Redis

---

## 📚 Key Takeaways

✅ **Databases** provide persistent storage that survives restarts
✅ **Exposed** is Kotlin's type-safe SQL library
✅ **Tables** are defined as `object TableName : Table()`
✅ **Columns** use methods like `integer()`, `varchar()`, `nullable()`
✅ **Transactions** ensure all-or-nothing execution
✅ **DAO pattern** separates database logic from routes
✅ **H2** is perfect for learning (in-memory or file-based)

---

## 🔜 Next Steps

In **Lesson 5.7**, you'll learn:
- UPDATE and DELETE operations
- Complex queries (joins, filters, sorting)
- One-to-many relationships
- Database migrations
- Batch operations
- Query optimization

---

## ✏️ Quiz Answer Key

**Question 1**: **B) It's completely lost**

Explanation: In-memory databases (jdbc:h2:mem:) store everything in RAM. When the process ends, all data is lost. For persistence, use file-based storage (jdbc:h2:file:./data/mydb).

---

**Question 2**: **B) Automatically generates unique sequential IDs for new rows**

Explanation: autoIncrement() tells the database to automatically assign incrementing IDs (1, 2, 3, ...) when you insert new rows, removing the need to manually specify IDs.

---

**Question 3**: **B) To ensure all-or-nothing execution and maintain data consistency**

Explanation: Transactions provide ACID guarantees. If any operation fails, all changes are rolled back, preventing partial updates that could corrupt your data.

---

**Congratulations!** You've connected your API to a real database! Your apps can now remember things! 🎉

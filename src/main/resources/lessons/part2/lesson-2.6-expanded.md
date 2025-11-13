# Lesson 2.6: Object Declarations and Companion Objects

**Estimated Time**: 60 minutes

---

## Topic Introduction

So far, you've created classes and instantiated them into objects. But what if you need:
- Only **one instance** of a class (singleton pattern)?
- **Static-like members** (methods/properties that belong to the class, not instances)?
- **Anonymous objects** for one-time use?

Kotlin provides elegant solutions through:
- **Object expressions** - Anonymous objects
- **Object declarations** - Singletons
- **Companion objects** - Static-like members within classes

These features eliminate boilerplate code and provide type-safe alternatives to Java's static members.

---

## The Concept

### What are Objects in Kotlin?

In Kotlin, `object` is a keyword with three uses:

1. **Object Expression**: Create anonymous objects (like Java's anonymous classes)
2. **Object Declaration**: Create singletons
3. **Companion Object**: Define class-level members (like Java's static)

**Why Objects?**
- **Singletons**: Ensure only one instance exists (database connections, app config)
- **Utilities**: Group related functions without instantiation
- **Constants**: Define immutable values accessible anywhere
- **Factory methods**: Create instances with custom logic

---

## Object Expressions

**Object expressions** create anonymous objects - objects of an unnamed class.

### Basic Object Expression

```kotlin
fun main() {
    val greeting = object {
        val message = "Hello"
        fun greet() {
            println("$message, World!")
        }
    }

    greeting.greet()  // Hello, World!
    println(greeting.message)  // Hello
}
```

### Implementing Interfaces

Common use: One-time implementations of interfaces

```kotlin
interface ClickListener {
    fun onClick()
}

fun setClickListener(listener: ClickListener) {
    println("Setting click listener...")
    listener.onClick()
}

fun main() {
    // Create anonymous object implementing ClickListener
    setClickListener(object : ClickListener {
        override fun onClick() {
            println("Button clicked!")
        }
    })
}
```

**Real-World Example: Event Handlers**

```kotlin
interface EventHandler {
    fun onEvent(event: String)
}

class Button(val text: String) {
    private var handler: EventHandler? = null

    fun setOnClickHandler(handler: EventHandler) {
        this.handler = handler
    }

    fun click() {
        println("Button '$text' clicked")
        handler?.onEvent("click")
    }
}

fun main() {
    val button = Button("Submit")

    // Anonymous object as event handler
    button.setOnClickHandler(object : EventHandler {
        override fun onEvent(event: String) {
            println("Handling $event event: Form submitted!")
        }
    })

    button.click()
}
```

**Output**:
```
Button 'Submit' clicked
Handling click event: Form submitted!
```

### Accessing Outer Scope

Object expressions can access variables from their surrounding scope:

```kotlin
fun countClicks() {
    var clickCount = 0

    val button = object {
        fun click() {
            clickCount++  // Access outer variable
            println("Click count: $clickCount")
        }
    }

    button.click()  // Click count: 1
    button.click()  // Click count: 2
    button.click()  // Click count: 3
}

fun main() {
    countClicks()
}
```

---

## Object Declarations (Singletons)

**Object declaration** creates a singleton - a class with exactly one instance.

### Basic Singleton

```kotlin
object DatabaseConnection {
    init {
        println("Database connection initialized")
    }

    var isConnected = false

    fun connect() {
        isConnected = true
        println("Connected to database")
    }

    fun disconnect() {
        isConnected = false
        println("Disconnected from database")
    }

    fun query(sql: String): String {
        return if (isConnected) {
            "Result of: $sql"
        } else {
            "Error: Not connected"
        }
    }
}

fun main() {
    // No need to instantiate - DatabaseConnection is the instance
    DatabaseConnection.connect()
    println(DatabaseConnection.query("SELECT * FROM users"))
    DatabaseConnection.disconnect()
}
```

**Output**:
```
Database connection initialized
Connected to database
Result of: SELECT * FROM users
Disconnected from database
```

**Key Points**:
- Created on first access (lazy initialization)
- Thread-safe by default
- Cannot have constructors
- Can implement interfaces and extend classes

### Real-World Example: Application Config

```kotlin
object AppConfig {
    const val APP_NAME = "MyAwesomeApp"
    const val VERSION = "1.0.0"

    var apiUrl = "https://api.example.com"
    var timeout = 30
    var debugMode = false

    fun printConfig() {
        println("=== $APP_NAME Configuration ===")
        println("Version: $VERSION")
        println("API URL: $apiUrl")
        println("Timeout: ${timeout}s")
        println("Debug Mode: $debugMode")
    }
}

fun main() {
    AppConfig.printConfig()

    // Modify config
    AppConfig.debugMode = true
    AppConfig.timeout = 60

    AppConfig.printConfig()
}
```

### Singleton with Interface

```kotlin
interface Logger {
    fun log(message: String)
    fun error(message: String)
}

object ConsoleLogger : Logger {
    override fun log(message: String) {
        println("[LOG] $message")
    }

    override fun error(message: String) {
        println("[ERROR] $message")
    }
}

fun processData(logger: Logger) {
    logger.log("Processing data...")
    logger.error("An error occurred!")
}

fun main() {
    processData(ConsoleLogger)
}
```

---

## Companion Objects

**Companion objects** are object declarations inside a class, providing "static-like" members.

### Basic Companion Object

```kotlin
class User(val name: String, val email: String) {
    companion object {
        const val DEFAULT_ROLE = "USER"
        var userCount = 0

        fun create(name: String, email: String): User {
            userCount++
            return User(name, email)
        }
    }
}

fun main() {
    println("Default role: ${User.DEFAULT_ROLE}")

    val user1 = User.create("Alice", "alice@example.com")
    val user2 = User.create("Bob", "bob@example.com")

    println("Total users created: ${User.userCount}")
}
```

**Output**:
```
Default role: USER
Total users created: 2
```

### Factory Methods

Companion objects are perfect for factory methods:

```kotlin
class Person private constructor(val name: String, val age: Int) {
    companion object {
        fun fromFullInfo(name: String, age: Int): Person {
            require(age >= 0) { "Age cannot be negative" }
            return Person(name, age)
        }

        fun fromName(name: String): Person {
            return Person(name, 0)
        }

        fun createAnonymous(): Person {
            return Person("Anonymous", 0)
        }
    }

    fun display() {
        println("Name: $name, Age: $age")
    }
}

fun main() {
    val person1 = Person.fromFullInfo("Alice", 25)
    val person2 = Person.fromName("Bob")
    val person3 = Person.createAnonymous()

    person1.display()  // Name: Alice, Age: 25
    person2.display()  // Name: Bob, Age: 0
    person3.display()  // Name: Anonymous, Age: 0
}
```

### Named Companion Objects

```kotlin
class MathOperations {
    companion object Calculator {
        fun add(a: Int, b: Int) = a + b
        fun subtract(a: Int, b: Int) = a - b
        fun multiply(a: Int, b: Int) = a * b
        fun divide(a: Int, b: Int) = a / b
    }
}

fun main() {
    // Can use class name
    println(MathOperations.add(5, 3))  // 8

    // Or companion object name
    println(MathOperations.Calculator.multiply(4, 7))  // 28
}
```

### Companion Object Implementing Interface

```kotlin
interface JsonSerializer {
    fun toJson(obj: Any): String
}

class User(val name: String, val age: Int) {
    companion object : JsonSerializer {
        override fun toJson(obj: Any): String {
            if (obj !is User) return "{}"
            return """{"name": "${obj.name}", "age": ${obj.age}}"""
        }
    }
}

fun main() {
    val user = User("Alice", 25)
    val json = User.toJson(user)
    println(json)  // {"name": "Alice", "age": 25}
}
```

---

## Constants: `const` vs `val`

### `const` for Compile-Time Constants

```kotlin
object Constants {
    const val MAX_USERS = 100  // ✅ Compile-time constant
    const val API_KEY = "abc123"  // ✅ Compile-time constant

    val createdAt = System.currentTimeMillis()  // ✅ Runtime value (not const)
}

class Config {
    companion object {
        const val TIMEOUT = 30  // ✅ Top-level or companion object
        val instance = Config()  // ✅ Runtime value
    }
}
```

**Rules for `const`**:
- Must be top-level, in object, or in companion object
- Must be primitive type or String
- Must be initialized with a compile-time constant
- Cannot have custom getter

---

## Real-World Example: Database Manager

```kotlin
data class User(val id: Int, val name: String, val email: String)

object DatabaseManager {
    private val users = mutableMapOf<Int, User>()
    private var nextId = 1
    private var isInitialized = false

    init {
        println("Initializing Database Manager...")
    }

    fun initialize() {
        if (isInitialized) {
            println("Database already initialized")
            return
        }
        println("Setting up database connection...")
        isInitialized = true
    }

    fun insertUser(name: String, email: String): User {
        require(isInitialized) { "Database not initialized" }
        val user = User(nextId++, name, email)
        users[user.id] = user
        println("Inserted user: ${user.name}")
        return user
    }

    fun getUserById(id: Int): User? {
        require(isInitialized) { "Database not initialized" }
        return users[id]
    }

    fun getAllUsers(): List<User> {
        require(isInitialized) { "Database not initialized" }
        return users.values.toList()
    }

    fun deleteUser(id: Int): Boolean {
        require(isInitialized) { "Database not initialized" }
        return users.remove(id) != null
    }

    fun getUserCount() = users.size
}

fun main() {
    DatabaseManager.initialize()

    DatabaseManager.insertUser("Alice", "alice@example.com")
    DatabaseManager.insertUser("Bob", "bob@example.com")
    DatabaseManager.insertUser("Carol", "carol@example.com")

    println("\nAll users:")
    DatabaseManager.getAllUsers().forEach { user ->
        println("${user.id}: ${user.name} (${user.email})")
    }

    println("\nGet user by ID:")
    val user = DatabaseManager.getUserById(2)
    println(user)

    println("\nDelete user 2:")
    DatabaseManager.deleteUser(2)

    println("\nRemaining users: ${DatabaseManager.getUserCount()}")
    DatabaseManager.getAllUsers().forEach { user ->
        println("${user.id}: ${user.name}")
    }
}
```

---

## Exercise 1: Logging System

**Goal**: Create a comprehensive logging system using objects.

**Requirements**:
1. Object `Logger` with different log levels (INFO, WARNING, ERROR)
2. Methods: `info()`, `warning()`, `error()`
3. Property to enable/disable logging
4. Track log count for each level
5. Method to print statistics

---

## Solution: Logging System

```kotlin
object Logger {
    private var enabled = true
    private var infoCount = 0
    private var warningCount = 0
    private var errorCount = 0

    fun enable() {
        enabled = true
        println("[LOGGER] Logging enabled")
    }

    fun disable() {
        enabled = false
        println("[LOGGER] Logging disabled")
    }

    fun info(message: String) {
        if (!enabled) return
        infoCount++
        println("[INFO] $message")
    }

    fun warning(message: String) {
        if (!enabled) return
        warningCount++
        println("[WARNING] $message")
    }

    fun error(message: String) {
        if (!enabled) return
        errorCount++
        println("[ERROR] $message")
    }

    fun printStatistics() {
        println("\n=== Logging Statistics ===")
        println("Info messages: $infoCount")
        println("Warning messages: $warningCount")
        println("Error messages: $errorCount")
        println("Total messages: ${infoCount + warningCount + errorCount}")
        println("==========================\n")
    }

    fun reset() {
        infoCount = 0
        warningCount = 0
        errorCount = 0
        println("[LOGGER] Statistics reset")
    }
}

fun main() {
    Logger.info("Application started")
    Logger.info("Loading configuration")
    Logger.warning("Configuration file not found, using defaults")
    Logger.info("Connecting to database")
    Logger.error("Failed to connect to database")
    Logger.info("Retrying connection")
    Logger.info("Connected successfully")

    Logger.printStatistics()

    Logger.disable()
    Logger.info("This won't be logged")

    Logger.enable()
    Logger.info("This will be logged")

    Logger.printStatistics()
}
```

---

## Exercise 2: Factory Pattern with Companion Objects

**Goal**: Create different types of database connections using factory methods.

**Requirements**:
1. Abstract class `DatabaseConnection` with method `connect()`
2. Subclasses: `MySqlConnection`, `PostgreSqlConnection`, `MongoConnection`
3. Companion object with factory methods to create each type
4. Method to validate connection parameters

---

## Solution: Database Factory

```kotlin
abstract class DatabaseConnection(
    protected val host: String,
    protected val port: Int,
    protected val database: String
) {
    abstract fun connect(): Boolean
    abstract fun getConnectionString(): String

    companion object Factory {
        const val DEFAULT_MYSQL_PORT = 3306
        const val DEFAULT_POSTGRES_PORT = 5432
        const val DEFAULT_MONGO_PORT = 27017

        fun createMySql(host: String, database: String, port: Int = DEFAULT_MYSQL_PORT): MySqlConnection {
            return MySqlConnection(host, port, database)
        }

        fun createPostgreSql(host: String, database: String, port: Int = DEFAULT_POSTGRES_PORT): PostgreSqlConnection {
            return PostgreSqlConnection(host, port, database)
        }

        fun createMongo(host: String, database: String, port: Int = DEFAULT_MONGO_PORT): MongoConnection {
            return MongoConnection(host, port, database)
        }

        fun createFromType(type: String, host: String, database: String): DatabaseConnection {
            return when (type.lowercase()) {
                "mysql" -> createMySql(host, database)
                "postgresql", "postgres" -> createPostgreSql(host, database)
                "mongodb", "mongo" -> createMongo(host, database)
                else -> throw IllegalArgumentException("Unknown database type: $type")
            }
        }
    }
}

class MySqlConnection(host: String, port: Int, database: String) : DatabaseConnection(host, port, database) {
    override fun connect(): Boolean {
        println("Connecting to MySQL...")
        println(getConnectionString())
        return true
    }

    override fun getConnectionString(): String {
        return "jdbc:mysql://$host:$port/$database"
    }
}

class PostgreSqlConnection(host: String, port: Int, database: String) : DatabaseConnection(host, port, database) {
    override fun connect(): Boolean {
        println("Connecting to PostgreSQL...")
        println(getConnectionString())
        return true
    }

    override fun getConnectionString(): String {
        return "jdbc:postgresql://$host:$port/$database"
    }
}

class MongoConnection(host: String, port: Int, database: String) : DatabaseConnection(host, port, database) {
    override fun connect(): Boolean {
        println("Connecting to MongoDB...")
        println(getConnectionString())
        return true
    }

    override fun getConnectionString(): String {
        return "mongodb://$host:$port/$database"
    }
}

fun main() {
    println("=== Creating connections using factory methods ===\n")

    val mysql = DatabaseConnection.createMySql("localhost", "myapp")
    mysql.connect()

    println()

    val postgres = DatabaseConnection.createPostgreSql("localhost", "myapp")
    postgres.connect()

    println()

    val mongo = DatabaseConnection.createMongo("localhost", "myapp")
    mongo.connect()

    println("\n=== Creating from type string ===\n")

    val db = DatabaseConnection.createFromType("mysql", "prod-server", "users_db")
    db.connect()
}
```

---

## Exercise 3: Singleton Cache System

**Goal**: Build a thread-safe cache system using object declaration.

**Requirements**:
1. Object `CacheManager` to store key-value pairs
2. Methods: `put()`, `get()`, `remove()`, `clear()`
3. Method to check if key exists
4. Method to get all keys
5. Track cache size and hits/misses

---

## Solution: Cache System

```kotlin
object CacheManager {
    private val cache = mutableMapOf<String, Any>()
    private var hits = 0
    private var misses = 0

    fun put(key: String, value: Any) {
        cache[key] = value
        println("✅ Cached: $key")
    }

    fun get(key: String): Any? {
        return if (cache.containsKey(key)) {
            hits++
            cache[key]
        } else {
            misses++
            null
        }
    }

    inline fun <reified T> getAs(key: String): T? {
        val value = get(key)
        return value as? T
    }

    fun remove(key: String): Boolean {
        val removed = cache.remove(key) != null
        if (removed) {
            println("🗑️  Removed: $key")
        }
        return removed
    }

    fun clear() {
        val count = cache.size
        cache.clear()
        println("🧹 Cleared $count items from cache")
    }

    fun contains(key: String): Boolean = cache.containsKey(key)

    fun getAllKeys(): Set<String> = cache.keys.toSet()

    fun size(): Int = cache.size

    fun getStatistics() {
        val totalRequests = hits + misses
        val hitRate = if (totalRequests > 0) (hits.toDouble() / totalRequests * 100) else 0.0

        println("\n=== Cache Statistics ===")
        println("Size: ${cache.size} items")
        println("Hits: $hits")
        println("Misses: $misses")
        println("Hit Rate: ${"%.2f".format(hitRate)}%")
        println("=======================\n")
    }

    fun displayContents() {
        println("\n=== Cache Contents ===")
        if (cache.isEmpty()) {
            println("(empty)")
        } else {
            cache.forEach { (key, value) ->
                println("$key = $value")
            }
        }
        println("======================\n")
    }
}

data class User(val id: Int, val name: String)

fun main() {
    // Add items to cache
    CacheManager.put("user:1", User(1, "Alice"))
    CacheManager.put("user:2", User(2, "Bob"))
    CacheManager.put("config:timeout", 30)
    CacheManager.put("config:maxUsers", 100)

    CacheManager.displayContents()

    // Retrieve items
    println("=== Retrieving items ===")
    val user1 = CacheManager.getAs<User>("user:1")
    println("Retrieved: $user1")

    val timeout = CacheManager.getAs<Int>("config:timeout")
    println("Timeout: $timeout")

    val notFound = CacheManager.get("user:999")
    println("Not found: $notFound")

    CacheManager.getStatistics()

    // Check existence
    println("Contains 'user:1': ${CacheManager.contains("user:1")}")
    println("Contains 'user:999': ${CacheManager.contains("user:999")}")

    // Get all keys
    println("\nAll keys: ${CacheManager.getAllKeys()}")

    // Remove item
    CacheManager.remove("user:2")

    CacheManager.displayContents()

    // Clear cache
    CacheManager.clear()

    CacheManager.displayContents()
    CacheManager.getStatistics()
}
```

---

## Checkpoint Quiz

### Question 1
What is an object declaration in Kotlin?

A) A way to create multiple instances
B) A singleton pattern with exactly one instance
C) An abstract class
D) A data class

### Question 2
What is a companion object?

A) A friend class
B) An object that provides static-like members for a class
C) A duplicate object
D) An object expression

### Question 3
When is an object declaration initialized?

A) At compile time
B) When the program starts
C) On first access (lazy initialization)
D) Never

### Question 4
Can companion objects implement interfaces?

A) No, never
B) Yes, but only one interface
C) Yes, multiple interfaces
D) Only abstract classes

### Question 5
What's the difference between `const val` and `val` in an object?

A) No difference
B) `const val` is a compile-time constant; `val` is computed at runtime
C) `const val` is faster
D) `val` is immutable; `const val` is not

---

## Quiz Answers

**Question 1: B) A singleton pattern with exactly one instance**

Object declarations create singletons - classes with exactly one instance that's created lazily.

```kotlin
object DatabaseConnection {
    fun connect() { }
}

// No need to instantiate
DatabaseConnection.connect()
```

---

**Question 2: B) An object that provides static-like members for a class**

Companion objects give you "static" functionality in Kotlin.

```kotlin
class User {
    companion object {
        fun create() = User()  // "Static" factory method
    }
}

val user = User.create()
```

---

**Question 3: C) On first access (lazy initialization)**

Objects are created the first time they're accessed, not when the program starts.

```kotlin
object Lazy {
    init {
        println("Initialized!")  // Prints on first access only
    }
}

// ... later ...
Lazy.toString()  // "Initialized!" prints here
```

---

**Question 4: C) Yes, multiple interfaces**

Companion objects can implement multiple interfaces, just like regular objects.

```kotlin
interface A { fun a() }
interface B { fun b() }

class Example {
    companion object : A, B {
        override fun a() { }
        override fun b() { }
    }
}
```

---

**Question 5: B) `const val` is a compile-time constant; `val` is computed at runtime**

`const val` must be known at compile time; `val` can be computed at runtime.

```kotlin
object Config {
    const val MAX_SIZE = 100  // ✅ Compile-time constant
    val timestamp = System.currentTimeMillis()  // ✅ Runtime value
    // const val time = System.currentTimeMillis()  // ❌ Error!
}
```

---

## What You've Learned

✅ Object expressions for anonymous objects
✅ Object declarations for singletons
✅ Companion objects for static-like members
✅ Factory methods with companion objects
✅ Constants with `const val`
✅ When to use objects vs classes

---

## Next Steps

In **Lesson 2.7: Part 2 Capstone - Library Management System**, you'll:
- Build a complete OOP project
- Apply all concepts from Part 2
- Create classes, inheritance, interfaces
- Use data classes and objects
- Implement a real-world system

Get ready for the capstone project!

---

**Congratulations on completing Lesson 2.6!** 🎉

You now understand all of Kotlin's object-related features. Ready for the capstone project!

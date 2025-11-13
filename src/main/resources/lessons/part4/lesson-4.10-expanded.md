# Lesson 4.4: Delegation and Lazy Initialization

**Estimated Time**: 65 minutes
**Difficulty**: Advanced
**Prerequisites**: Parts 1-3

---

## Topic Introduction

In software development, you often want to reuse behavior from other classes or defer expensive operations until they're needed. Kotlin provides powerful delegation mechanisms that make these patterns simple and type-safe.

Delegation is the design pattern where an object handles a request by delegating to a helper object (delegate). Instead of inheritance, you compose objects and delegate behavior. Kotlin provides first-class language support for this pattern.

In this lesson, you'll learn:
- Class delegation with the `by` keyword
- Property delegation patterns
- Lazy initialization with `lazy`
- Observable properties
- Custom delegates
- Standard delegates: `notNull`, `vetoable`, `observable`

By the end, you'll write cleaner, more maintainable code using delegation!

---

## The Concept: Why Delegation Matters

### The Problem: Code Duplication

Without delegation:

```kotlin
interface Printer {
    fun print(message: String)
}

class ConsolePrinter : Printer {
    override fun print(message: String) {
        println("Console: $message")
    }
}

class Logger : Printer {
    private val printer = ConsolePrinter()

    override fun print(message: String) {
        printer.print(message)  // Just forwarding!
    }

    fun log(message: String) {
        print("[LOG] $message")
    }
}
```

### The Solution: Class Delegation

```kotlin
class Logger(printer: Printer) : Printer by printer {
    fun log(message: String) {
        print("[LOG] $message")
    }
}

fun main() {
    val logger = Logger(ConsolePrinter())
    logger.print("Hello")     // Delegated to ConsolePrinter
    logger.log("Important")   // [LOG] Important
}
```

**Benefits**:
- No boilerplate forwarding code
- Composition over inheritance
- Clear separation of concerns

---

## Class Delegation

The `by` keyword delegates interface implementation to another object.

### Basic Class Delegation

```kotlin
interface Database {
    fun save(data: String)
    fun load(): String
}

class RealDatabase : Database {
    private var storage = ""

    override fun save(data: String) {
        storage = data
        println("Saved: $data")
    }

    override fun load(): String {
        println("Loading: $storage")
        return storage
    }
}

class CachedDatabase(db: Database) : Database by db {
    private var cache: String? = null

    // Override specific methods
    override fun load(): String {
        if (cache != null) {
            println("Returning from cache")
            return cache!!
        }

        val data = (this as Database).let {
            // Call delegated load through explicit reference
            RealDatabase::class.java.getMethod("load").invoke(db) as String
        }

        cache = data
        return data
    }
}

fun main() {
    val db = CachedDatabase(RealDatabase())
    db.save("Important data")
    println(db.load())  // Loads from database
    println(db.load())  // Returns from cache
}
```

### Multiple Interface Delegation

```kotlin
interface CanFly {
    fun fly()
}

interface CanSwim {
    fun swim()
}

class Bird : CanFly {
    override fun fly() {
        println("Flying in the sky")
    }
}

class Fish : CanSwim {
    override fun swim() {
        println("Swimming in water")
    }
}

class Duck(
    flyer: CanFly,
    swimmer: CanSwim
) : CanFly by flyer, CanSwim by swimmer

fun main() {
    val duck = Duck(Bird(), Fish())
    duck.fly()   // Flying in the sky
    duck.swim()  // Swimming in water
}
```

### Real-World Example: Window Decoration

```kotlin
interface Window {
    fun draw()
    fun getDescription(): String
}

class SimpleWindow : Window {
    override fun draw() {
        println("Drawing window")
    }

    override fun getDescription(): String = "Simple window"
}

class ScrollableWindow(window: Window) : Window by window {
    override fun draw() {
        window.draw()
        println("Adding scrollbars")
    }

    override fun getDescription(): String = "${window.getDescription()} with scrollbars"
}

class BorderedWindow(window: Window) : Window by window {
    override fun draw() {
        window.draw()
        println("Adding border")
    }

    override fun getDescription(): String = "${window.getDescription()} with border"
}

fun main() {
    val window = BorderedWindow(ScrollableWindow(SimpleWindow()))
    window.draw()
    println(window.getDescription())
}
// Output:
// Drawing window
// Adding scrollbars
// Adding border
// Simple window with scrollbars with border
```

---

## Property Delegation

Property delegation allows you to delegate the implementation of property accessors.

### Syntax

```kotlin
class Example {
    var property: String by DelegateClass()
}
```

The delegate must provide `getValue` and `setValue` operators:

```kotlin
class DelegateClass {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "Value of ${property.name}"
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("Setting ${property.name} to $value")
    }
}
```

---

## Lazy Initialization

`lazy` creates a property that's initialized only when first accessed.

### Basic Lazy

```kotlin
class HeavyObject {
    init {
        println("HeavyObject created")
    }

    fun doWork() {
        println("Working...")
    }
}

class MyClass {
    val heavy: HeavyObject by lazy {
        println("Initializing heavy object")
        HeavyObject()
    }
}

fun main() {
    println("Creating MyClass")
    val obj = MyClass()

    println("MyClass created")
    println("Accessing heavy")
    obj.heavy.doWork()  // Initialized here

    println("Accessing again")
    obj.heavy.doWork()  // Uses cached value
}
// Output:
// Creating MyClass
// MyClass created
// Accessing heavy
// Initializing heavy object
// HeavyObject created
// Working...
// Accessing again
// Working...
```

### Lazy Thread Safety

```kotlin
class Example {
    // Thread-safe (default)
    val safeLazy: String by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        expensiveComputation()
    }

    // Not thread-safe (faster)
    val unsafeLazy: String by lazy(LazyThreadSafetyMode.NONE) {
        expensiveComputation()
    }

    // Published - initialized once, but may race
    val publishedLazy: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        expensiveComputation()
    }
}

fun expensiveComputation(): String {
    Thread.sleep(1000)
    return "Result"
}
```

### Practical Example: Database Connection

```kotlin
class DatabaseConnection {
    init {
        println("Connecting to database...")
        Thread.sleep(1000)
        println("Connected!")
    }

    fun query(sql: String): String {
        return "Result for: $sql"
    }
}

class Repository {
    private val db: DatabaseConnection by lazy {
        println("Lazy initialization triggered")
        DatabaseConnection()
    }

    fun getData(): String {
        return db.query("SELECT * FROM users")
    }
}

fun main() {
    println("Creating repository")
    val repo = Repository()

    println("Repository created (DB not connected yet)")

    println("\nFetching data...")
    println(repo.getData())
}
// Output:
// Creating repository
// Repository created (DB not connected yet)
//
// Fetching data...
// Lazy initialization triggered
// Connecting to database...
// Connected!
// Result for: SELECT * FROM users
```

---

## Observable Properties

Observable delegates notify you when a property changes.

### Delegates.observable

```kotlin
import kotlin.properties.Delegates

class User {
    var name: String by Delegates.observable("Initial") { property, oldValue, newValue ->
        println("${property.name} changed from '$oldValue' to '$newValue'")
    }

    var age: Int by Delegates.observable(0) { _, old, new ->
        println("Age changed from $old to $new")
    }
}

fun main() {
    val user = User()

    user.name = "Alice"
    user.name = "Bob"
    user.age = 25
    user.age = 26
}
// Output:
// name changed from 'Initial' to 'Alice'
// name changed from 'Alice' to 'Bob'
// Age changed from 0 to 25
// Age changed from 25 to 26
```

### Delegates.vetoable

Veto (reject) property changes based on a condition:

```kotlin
class Account {
    var balance: Double by Delegates.vetoable(0.0) { _, oldValue, newValue ->
        println("Attempting to change balance from $oldValue to $newValue")

        // Veto negative balances
        if (newValue < 0) {
            println("❌ Rejected: balance cannot be negative")
            false  // Reject change
        } else {
            println("✅ Accepted")
            true  // Accept change
        }
    }
}

fun main() {
    val account = Account()

    account.balance = 100.0  // ✅ Accepted
    println("Balance: ${account.balance}")

    account.balance = -50.0  // ❌ Rejected
    println("Balance: ${account.balance}")  // Still 100.0

    account.balance = 200.0  // ✅ Accepted
    println("Balance: ${account.balance}")
}
```

---

## Delegates.notNull

For non-null properties that can't be initialized immediately:

```kotlin
import kotlin.properties.Delegates

class Configuration {
    var apiKey: String by Delegates.notNull()
    var apiSecret: String by Delegates.notNull()

    fun initialize(key: String, secret: String) {
        apiKey = key
        apiSecret = secret
    }
}

fun main() {
    val config = Configuration()

    // println(config.apiKey)  // ❌ Throws IllegalStateException

    config.initialize("key123", "secret456")

    println(config.apiKey)     // ✅ Works: key123
    println(config.apiSecret)  // ✅ Works: secret456
}
```

---

## Custom Delegates

Create your own property delegates by implementing `getValue` and `setValue`.

### Read-Only Delegate

```kotlin
import kotlin.reflect.KProperty

class Uppercase {
    private var value: String = ""

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return value.uppercase()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.value = value
    }
}

class Example {
    var text: String by Uppercase()
}

fun main() {
    val example = Example()
    example.text = "hello world"
    println(example.text)  // HELLO WORLD

    example.text = "kotlin is awesome"
    println(example.text)  // KOTLIN IS AWESOME
}
```

### Logged Property Delegate

```kotlin
class Logged<T>(private var value: T) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        println("Getting ${property.name} = $value")
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        println("Setting ${property.name} from $value to $newValue")
        value = newValue
    }
}

class Person {
    var name: String by Logged("Unknown")
    var age: Int by Logged(0)
}

fun main() {
    val person = Person()

    person.name = "Alice"
    println(person.name)

    person.age = 25
    println(person.age)
}
// Output:
// Setting name from Unknown to Alice
// Getting name = Alice
// Alice
// Setting age from 0 to 25
// Getting age = 25
// 25
```

### Range-Validated Delegate

```kotlin
class RangeValidator<T : Comparable<T>>(
    private var value: T,
    private val range: ClosedRange<T>
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        if (newValue in range) {
            value = newValue
        } else {
            throw IllegalArgumentException(
                "${property.name} must be in $range, got $newValue"
            )
        }
    }
}

fun <T : Comparable<T>> rangeValidator(initial: T, range: ClosedRange<T>) =
    RangeValidator(initial, range)

class Temperature {
    var celsius: Double by rangeValidator(0.0, -273.15..1000.0)
}

fun main() {
    val temp = Temperature()

    temp.celsius = 25.0
    println(temp.celsius)  // 25.0

    temp.celsius = 100.0
    println(temp.celsius)  // 100.0

    // temp.celsius = -300.0  // ❌ Throws exception
}
```

---

## Map-Based Delegation

Delegate properties to a map:

```kotlin
class User(map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
    val email: String by map
}

fun main() {
    val user = User(
        mapOf(
            "name" to "Alice",
            "age" to 25,
            "email" to "alice@example.com"
        )
    )

    println(user.name)   // Alice
    println(user.age)    // 25
    println(user.email)  // alice@example.com
}
```

### Mutable Map Delegation

```kotlin
class MutableUser(val map: MutableMap<String, Any?>) {
    var name: String by map
    var age: Int by map
}

fun main() {
    val user = MutableUser(mutableMapOf())

    user.name = "Bob"
    user.age = 30

    println(user.map)  // {name=Bob, age=30}

    user.map["name"] = "Alice"
    println(user.name)  // Alice
}
```

### Practical Example: JSON-like Configuration

```kotlin
class Config(private val properties: MutableMap<String, Any?> = mutableMapOf()) {
    var serverUrl: String by properties
    var port: Int by properties
    var timeout: Long by properties
    var enableLogging: Boolean by properties

    fun toMap(): Map<String, Any?> = properties.toMap()
}

fun main() {
    val config = Config()

    config.serverUrl = "https://api.example.com"
    config.port = 8080
    config.timeout = 5000L
    config.enableLogging = true

    println(config.toMap())
    // {serverUrl=https://api.example.com, port=8080, timeout=5000, enableLogging=true}
}
```

---

## Providing Delegates

Create delegate providers that can initialize delegates with custom logic:

```kotlin
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ResourceDelegate<T>(private val resource: T) : ReadWriteProperty<Any?, T> {
    private var value: T = resource

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        println("Accessing resource: ${property.name}")
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        println("Updating resource: ${property.name}")
        this.value = value
    }
}

class ResourceProvider<T>(private val resource: T) {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ResourceDelegate<T> {
        println("Providing delegate for ${property.name}")
        return ResourceDelegate(resource)
    }
}

class Example {
    var resource: String by ResourceProvider("Initial")
}

fun main() {
    val example = Example()
    // Output: Providing delegate for resource

    example.resource = "Updated"
    // Output: Updating resource: resource

    println(example.resource)
    // Output: Accessing resource: resource
    // Updated
}
```

---

## Exercises

### Exercise 1: Thread-Safe Cache (Medium)

Create a thread-safe caching delegate.

**Requirements**:
- Cache computed values
- Thread-safe access
- Optional expiration time
- Lazy computation

**Solution**:

```kotlin
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class CachedValue<T>(
    private val ttlMillis: Long = Long.MAX_VALUE,
    private val compute: () -> T
) : ReadOnlyProperty<Any?, T> {
    private var value: T? = null
    private var timestamp: Long = 0
    private val lock = Any()

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        synchronized(lock) {
            val now = System.currentTimeMillis()

            if (value == null || (now - timestamp) > ttlMillis) {
                println("Computing ${property.name}")
                value = compute()
                timestamp = now
            } else {
                println("Returning cached ${property.name}")
            }

            return value!!
        }
    }
}

fun <T> cached(ttlMillis: Long = Long.MAX_VALUE, compute: () -> T) =
    CachedValue(ttlMillis, compute)

class DataService {
    val expensiveData: String by cached(ttlMillis = 2000) {
        println("Fetching expensive data...")
        Thread.sleep(1000)
        "Expensive Result"
    }

    val userData: String by cached {
        println("Fetching user data...")
        Thread.sleep(500)
        "User Data"
    }
}

fun main() {
    val service = DataService()

    println(service.expensiveData)
    Thread.sleep(500)
    println(service.expensiveData)  // Cached

    Thread.sleep(2000)
    println(service.expensiveData)  // Recomputed (expired)

    println("\n${service.userData}")
    println(service.userData)  // Cached
}
```

### Exercise 2: Change Tracking (Medium)

Create a delegate that tracks all changes to a property.

**Requirements**:
- Track value changes with timestamps
- Get change history
- Support any type

**Solution**:

```kotlin
import kotlin.reflect.KProperty

data class Change<T>(
    val oldValue: T?,
    val newValue: T,
    val timestamp: Long = System.currentTimeMillis()
)

class Tracked<T>(initialValue: T) {
    private var value: T = initialValue
    private val changes = mutableListOf<Change<T>>()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T) {
        val change = Change(value, newValue)
        changes.add(change)
        value = newValue
    }

    fun getHistory(): List<Change<T>> = changes.toList()

    fun getChangeCount(): Int = changes.size
}

fun <T> tracked(initialValue: T) = Tracked(initialValue)

class Document {
    var title: String by tracked("Untitled")
    var content: String by tracked("")

    fun getTitleHistory() = (::title.getDelegate() as Tracked<String>).getHistory()
    fun getContentHistory() = (::content.getDelegate() as Tracked<String>).getHistory()
}

fun main() {
    val doc = Document()

    doc.title = "My Document"
    Thread.sleep(100)
    doc.title = "My Awesome Document"
    Thread.sleep(100)
    doc.title = "Final Title"

    doc.content = "Introduction"
    Thread.sleep(100)
    doc.content = "Introduction\n\nBody"

    println("Title History:")
    doc.getTitleHistory().forEach { change ->
        println("  ${change.oldValue} -> ${change.newValue}")
    }

    println("\nContent History:")
    doc.getContentHistory().forEach { change ->
        println("  '${change.oldValue}' -> '${change.newValue}'")
    }
}
```

### Exercise 3: Smart Configuration (Hard)

Create a configuration system with validation, defaults, and environment variables.

**Requirements**:
- Type-safe configuration properties
- Default values
- Environment variable override
- Validation

**Solution**:

```kotlin
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class ConfigProperty<T>(
    private val default: T,
    private val envVar: String? = null,
    private val validator: (T) -> Boolean = { true }
) : ReadWriteProperty<Any?, T> {
    private var value: T? = null

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (value == null) {
            // Try environment variable
            value = envVar?.let { getEnvValue(it, default) } ?: default
        }
        return value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (!validator(value)) {
            throw IllegalArgumentException("Invalid value for ${property.name}: $value")
        }
        this.value = value
    }

    @Suppress("UNCHECKED_CAST")
    private fun getEnvValue(name: String, default: T): T {
        val envValue = System.getenv(name) ?: return default

        return when (default) {
            is String -> envValue as T
            is Int -> envValue.toIntOrNull() as? T ?: default
            is Boolean -> envValue.toBoolean() as T
            is Double -> envValue.toDoubleOrNull() as? T ?: default
            else -> default
        }
    }
}

fun <T> config(
    default: T,
    envVar: String? = null,
    validator: (T) -> Boolean = { true }
) = ConfigProperty(default, envVar, validator)

class AppConfig {
    var host: String by config(
        default = "localhost",
        envVar = "APP_HOST"
    )

    var port: Int by config(
        default = 8080,
        envVar = "APP_PORT",
        validator = { it in 1..65535 }
    )

    var maxConnections: Int by config(
        default = 100,
        validator = { it > 0 }
    )

    var debugMode: Boolean by config(
        default = false,
        envVar = "DEBUG"
    )

    override fun toString(): String {
        return """
            AppConfig(
              host=$host,
              port=$port,
              maxConnections=$maxConnections,
              debugMode=$debugMode
            )
        """.trimIndent()
    }
}

fun main() {
    val config = AppConfig()

    println("Default configuration:")
    println(config)

    // Modify configuration
    config.host = "0.0.0.0"
    config.port = 3000
    config.maxConnections = 500

    println("\nModified configuration:")
    println(config)

    // Validation
    try {
        config.port = 99999  // Invalid
    } catch (e: IllegalArgumentException) {
        println("\n❌ Error: ${e.message}")
    }

    try {
        config.maxConnections = -10  // Invalid
    } catch (e: IllegalArgumentException) {
        println("❌ Error: ${e.message}")
    }
}
```

---

## Checkpoint Quiz

### Question 1: Class Delegation

What does the `by` keyword do in class delegation?

**A)** Creates a subclass
**B)** Forwards interface implementation to another object
**C)** Copies all methods from another class
**D)** Creates a singleton

**Answer**: **B** - The `by` keyword automatically forwards interface implementation to the specified delegate object.

---

### Question 2: Lazy Initialization

When is a lazy property initialized?

**A)** When the class is created
**B)** At compile time
**C)** On first access
**D)** Never

**Answer**: **C** - Lazy properties are initialized on first access, not when the class is created.

---

### Question 3: Observable

What does `Delegates.observable` do?

**A)** Validates property values
**B)** Notifies when property changes
**C)** Makes property thread-safe
**D)** Caches property values

**Answer**: **B** - `Delegates.observable` calls a lambda whenever the property value changes, allowing you to observe changes.

---

### Question 4: Vetoable

How does `Delegates.vetoable` work?

**A)** It logs all changes
**B)** It returns true/false to accept/reject changes
**C)** It automatically validates types
**D)** It prevents all changes

**Answer**: **B** - `Delegates.vetoable` calls a lambda that returns true to accept or false to reject the property change.

---

### Question 5: Custom Delegates

What must a custom property delegate implement?

**A)** `get()` and `set()`
**B)** `getValue()` and `setValue()` operators
**C)** `read()` and `write()`
**D)** `load()` and `store()`

**Answer**: **B** - Custom delegates must implement `getValue()` operator (and `setValue()` for mutable properties).

---

## Summary

Congratulations! You've mastered delegation in Kotlin. Here's what you learned:

✅ **Class Delegation** - Composing objects with `by` keyword
✅ **Property Delegation** - Delegating property accessors
✅ **Lazy Initialization** - Deferring expensive computations
✅ **Observable Properties** - Tracking property changes
✅ **Standard Delegates** - `notNull`, `vetoable`, `observable`
✅ **Custom Delegates** - Creating your own delegation logic

### Key Takeaways

1. **Class delegation** promotes composition over inheritance
2. **`lazy`** initializes properties only on first access
3. **`observable`** notifies on changes, **`vetoable`** can reject changes
4. **Custom delegates** implement `getValue`/`setValue` operators
5. **Map delegation** is great for dynamic property storage

### Next Steps

In the next lesson, we'll explore **Annotations and Reflection** - powerful metaprogramming features that let you inspect and modify code at runtime!

---

**Practice Challenge**: Create a preferences system that saves properties to a file automatically when they change, using custom delegates and observable patterns.

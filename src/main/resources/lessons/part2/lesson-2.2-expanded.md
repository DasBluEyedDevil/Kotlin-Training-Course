# Lesson 2.2: Properties and Initialization

**Estimated Time**: 65 minutes

---

## Topic Introduction

In Lesson 2.1, you learned the basics of classes and objects. Now it's time to dive deeper into **properties**—the data that objects hold.

Kotlin provides powerful features for managing properties that go far beyond simple variables:
- **Custom getters and setters** for computed or validated values
- **Late initialization** for properties that can't be set immediately
- **Lazy initialization** for expensive operations that should only happen when needed
- **Backing fields** for advanced property control
- **Property delegation** to reuse property logic

These features make Kotlin properties more flexible and powerful than in most other languages. Let's explore them!

---

## The Concept

### Properties vs Fields

In many languages (like Java), classes have **fields** (private variables) and **getter/setter methods** to access them:

**Java (verbose)**:
```java
public class Person {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

**Kotlin (clean)**:
```kotlin
class Person {
    var name: String = ""
}
```

In Kotlin, properties automatically have getters (and setters for `var`). You access them like fields, but they're actually calling methods behind the scenes!

```kotlin
val person = Person()
person.name = "Alice"  // Calls setter
println(person.name)    // Calls getter
```

---

## Custom Getters and Setters

### Custom Getters

A **custom getter** computes a value every time the property is accessed.

**Example: Computed Properties**

```kotlin
class Rectangle(val width: Double, val height: Double) {
    val area: Double
        get() = width * height  // Computed each time

    val perimeter: Double
        get() = 2 * (width + height)
}

fun main() {
    val rect = Rectangle(5.0, 10.0)
    println(rect.area)       // 50.0 (computed)
    println(rect.perimeter)  // 30.0 (computed)
}
```

**Why use a custom getter instead of a method?**
- More natural syntax: `rect.area` vs `rect.getArea()`
- Semantic: it looks like a property because it behaves like one
- Lightweight computation that doesn't change the object state

**Example: Derived Properties**

```kotlin
class Person(val firstName: String, val lastName: String) {
    val fullName: String
        get() = "$firstName $lastName"
}

fun main() {
    val person = Person("Alice", "Johnson")
    println(person.fullName)  // Alice Johnson
}
```

### Custom Setters

A **custom setter** validates or transforms values when they're assigned.

**Example: Input Validation**

```kotlin
class User(name: String) {
    var name: String = name
        set(value) {
            require(value.isNotBlank()) { "Name cannot be blank" }
            field = value.trim()  // 'field' is the backing field
        }

    var age: Int = 0
        set(value) {
            require(value in 0..150) { "Age must be between 0 and 150" }
            field = value
        }
}

fun main() {
    val user = User("Alice")

    user.name = "  Bob  "
    println(user.name)  // Bob (trimmed)

    user.age = 25
    println(user.age)   // 25

    // user.age = 200  // ❌ Exception: Age must be between 0 and 150
    // user.name = ""  // ❌ Exception: Name cannot be blank
}
```

**Key Points**:
- `set(value)` defines custom logic when the property is assigned
- `field` refers to the **backing field** (the actual stored value)
- Use `field` to avoid infinite recursion (don't use the property name inside its own setter!)

### Visibility Modifiers for Setters

You can make a property readable publicly but only writable internally:

```kotlin
class BankAccount(initialBalance: Double) {
    var balance: Double = initialBalance
        private set  // Can only be modified inside the class

    fun deposit(amount: Double) {
        require(amount > 0) { "Amount must be positive" }
        balance += amount
    }

    fun withdraw(amount: Double) {
        require(amount > 0 && amount <= balance) { "Invalid withdrawal" }
        balance -= amount
    }
}

fun main() {
    val account = BankAccount(1000.0)

    println(account.balance)  // ✅ Can read: 1000.0
    account.deposit(500.0)
    println(account.balance)  // 1500.0

    // account.balance = 9999.0  // ❌ Error: Cannot assign to 'balance': the setter is private
}
```

---

## Late Initialization (`lateinit`)

Sometimes you can't initialize a property immediately (e.g., in Android, views are initialized after the object is created). **`lateinit`** lets you declare a non-null property without initializing it right away.

### When to Use `lateinit`

Use `lateinit` when:
- The property will be initialized before use (but not in the constructor)
- The property is non-null
- The property type is non-primitive (not Int, Double, Boolean, etc.)

**Example: Setup Method**

```kotlin
class DatabaseConnection {
    lateinit var connectionString: String

    fun connect(host: String, port: Int) {
        connectionString = "jdbc:mysql://$host:$port/mydb"
        println("Connected to $connectionString")
    }

    fun query() {
        if (::connectionString.isInitialized) {
            println("Querying database at $connectionString")
        } else {
            println("Error: Not connected to database!")
        }
    }
}

fun main() {
    val db = DatabaseConnection()

    // db.query()  // Would work but connectionString isn't initialized yet

    db.connect("localhost", 3306)
    db.query()  // Querying database at jdbc:mysql://localhost:3306/mydb
}
```

**Checking if `lateinit` is Initialized**:

```kotlin
if (::connectionString.isInitialized) {
    // Safe to use
}
```

**Warning**: Accessing an uninitialized `lateinit` property throws `UninitializedPropertyAccessException`!

**Example: Dependency Injection**

```kotlin
class UserService {
    lateinit var database: Database
    lateinit var logger: Logger

    fun initialize(db: Database, log: Logger) {
        database = db
        logger = log
    }

    fun getUser(id: Int): String {
        logger.log("Fetching user $id")
        return database.query("SELECT * FROM users WHERE id = $id")
    }
}

class Database {
    fun query(sql: String): String = "Result of: $sql"
}

class Logger {
    fun log(message: String) = println("[LOG] $message")
}

fun main() {
    val service = UserService()
    service.initialize(Database(), Logger())

    println(service.getUser(42))
}
```

**Output**:
```
[LOG] Fetching user 42
Result of: SELECT * FROM users WHERE id = 42
```

---

## Lazy Initialization

**Lazy properties** are initialized only when they're first accessed. Perfect for expensive operations that might not be needed.

### The `lazy` Delegate

```kotlin
class DataProcessor {
    val heavyData: List<Int> by lazy {
        println("Computing heavy data...")
        (1..1000000).toList()  // Expensive operation
    }

    fun process() {
        println("Starting process")
        println("First 5 items: ${heavyData.take(5)}")  // heavyData initialized here
        println("Process complete")
    }
}

fun main() {
    val processor = DataProcessor()
    println("DataProcessor created")
    println("About to call process()")

    processor.process()
}
```

**Output**:
```
DataProcessor created
About to call process()
Starting process
Computing heavy data...
First 5 items: [1, 2, 3, 4, 5]
Process complete
```

**Key Points**:
- The lambda `{ ... }` is only executed once, on first access
- The result is cached and reused for subsequent accesses
- Thread-safe by default
- Can only be used with `val` (not `var`)

**Example: Configuration Loading**

```kotlin
class Application {
    val config: Map<String, String> by lazy {
        println("Loading configuration from file...")
        mapOf(
            "app.name" to "MyApp",
            "app.version" to "1.0.0",
            "db.host" to "localhost"
        )
    }

    fun start() {
        println("Application starting...")
        println("App: ${config["app.name"]} v${config["app.version"]}")
        println("Database: ${config["db.host"]}")
    }
}

fun main() {
    val app = Application()
    println("App object created")

    Thread.sleep(1000)

    app.start()  // Config loaded here on first access
}
```

**Output**:
```
App object created
Application starting...
Loading configuration from file...
App: MyApp v1.0.0
Database: localhost
```

---

## Backing Fields

A **backing field** is the actual storage for a property. Kotlin generates it automatically when needed.

**When Kotlin generates a backing field**:
- Property has a default accessor (getter/setter)
- Property has a custom accessor that uses `field`

**When Kotlin does NOT generate a backing field**:
- Property only has a custom getter that doesn't use `field`

```kotlin
class Example {
    // Has backing field (stores value)
    var stored: String = "value"

    // Has backing field (custom setter uses 'field')
    var validated: Int = 0
        set(value) {
            if (value >= 0) field = value
        }

    // NO backing field (just computed)
    val computed: String
        get() = "Always computed"
}
```

**Example: Tracking Property Changes**

```kotlin
class Product(name: String, price: Double) {
    var name: String = name
        set(value) {
            println("Name changed from '$field' to '$value'")
            field = value
        }

    var price: Double = price
        set(value) {
            require(value >= 0) { "Price cannot be negative" }
            println("Price changed from $$field to $$value")
            field = value
        }
}

fun main() {
    val product = Product("Laptop", 999.99)

    product.name = "Gaming Laptop"  // Name changed from 'Laptop' to 'Gaming Laptop'
    product.price = 1299.99          // Price changed from $999.99 to $1299.99
}
```

---

## Property Delegation Basics

**Property delegation** allows you to reuse property logic by delegating to another object.

**Syntax**: `var/val propertyName: Type by delegate`

### Built-in Delegates

**1. `lazy` (already covered)**

**2. `observable` - Notified on property changes**

```kotlin
import kotlin.properties.Delegates

class User {
    var name: String by Delegates.observable("Initial") { property, oldValue, newValue ->
        println("${property.name} changed from '$oldValue' to '$newValue'")
    }
}

fun main() {
    val user = User()

    user.name = "Alice"  // name changed from 'Initial' to 'Alice'
    user.name = "Bob"    // name changed from 'Alice' to 'Bob'
}
```

**3. `vetoable` - Validate changes before accepting**

```kotlin
import kotlin.properties.Delegates

class Settings {
    var fontSize: Int by Delegates.vetoable(12) { property, oldValue, newValue ->
        newValue in 8..24  // Only accept values between 8 and 24
    }
}

fun main() {
    val settings = Settings()

    println(settings.fontSize)  // 12

    settings.fontSize = 16
    println(settings.fontSize)  // 16

    settings.fontSize = 50  // Rejected (out of range)
    println(settings.fontSize)  // Still 16
}
```

---

## Exercise 1: Temperature Converter

**Goal**: Create a `Temperature` class with Celsius and Fahrenheit properties that stay in sync.

**Requirements**:
1. Property: `celsius` (Double, with setter)
2. Property: `fahrenheit` (Double, computed from celsius)
3. When `celsius` changes, `fahrenheit` updates automatically
4. Formulas: `F = C * 9/5 + 32`, `C = (F - 32) * 5/9`

---

## Solution: Temperature Converter

```kotlin
class Temperature(celsius: Double = 0.0) {
    var celsius: Double = celsius
        set(value) {
            field = value
            println("Temperature set to $value°C (${fahrenheit}°F)")
        }

    val fahrenheit: Double
        get() = celsius * 9 / 5 + 32

    fun setFahrenheit(f: Double) {
        celsius = (f - 32) * 5 / 9
    }

    fun display() {
        println("$celsius°C = $fahrenheit°F")
    }
}

fun main() {
    val temp = Temperature()

    temp.celsius = 0.0    // Temperature set to 0.0°C (32.0°F)
    temp.display()        // 0.0°C = 32.0°F

    temp.celsius = 100.0  // Temperature set to 100.0°C (212.0°F)
    temp.display()        // 100.0°C = 212.0°F

    temp.setFahrenheit(98.6)
    temp.display()        // 37.0°C = 98.6°F
}
```

---

## Exercise 2: Shopping Cart with Validation

**Goal**: Build a `ShoppingCart` class with validation and computed properties.

**Requirements**:
1. Property: `items` (mutable list of `CartItem`)
2. Property: `totalPrice` (computed, read-only)
3. Property: `itemCount` (computed, read-only)
4. Method: `addItem(name: String, price: Double, quantity: Int)` - validate price > 0 and quantity > 0
5. Method: `removeItem(name: String)`
6. Method: `displayCart()`

---

## Solution: Shopping Cart

```kotlin
data class CartItem(val name: String, val price: Double, val quantity: Int) {
    val subtotal: Double
        get() = price * quantity
}

class ShoppingCart {
    private val items = mutableListOf<CartItem>()

    val totalPrice: Double
        get() = items.sumOf { it.subtotal }

    val itemCount: Int
        get() = items.sumOf { it.quantity }

    fun addItem(name: String, price: Double, quantity: Int) {
        require(price > 0) { "Price must be positive" }
        require(quantity > 0) { "Quantity must be positive" }

        // Check if item already exists
        val existingItem = items.find { it.name == name }
        if (existingItem != null) {
            items.remove(existingItem)
            items.add(CartItem(name, price, existingItem.quantity + quantity))
            println("Updated $name quantity")
        } else {
            items.add(CartItem(name, price, quantity))
            println("Added $name to cart")
        }
    }

    fun removeItem(name: String) {
        val removed = items.removeIf { it.name == name }
        if (removed) {
            println("Removed $name from cart")
        } else {
            println("$name not found in cart")
        }
    }

    fun displayCart() {
        if (items.isEmpty()) {
            println("Cart is empty")
            return
        }

        println("\n=== Shopping Cart ===")
        items.forEach { item ->
            println("${item.name}: $${item.price} x ${item.quantity} = $${item.subtotal}")
        }
        println("---")
        println("Total Items: $itemCount")
        println("Total Price: $$totalPrice")
        println("===================\n")
    }
}

fun main() {
    val cart = ShoppingCart()

    cart.addItem("Laptop", 999.99, 1)
    cart.addItem("Mouse", 29.99, 2)
    cart.addItem("Keyboard", 79.99, 1)

    cart.displayCart()

    cart.addItem("Mouse", 29.99, 1)  // Update quantity

    cart.displayCart()

    cart.removeItem("Keyboard")

    cart.displayCart()
}
```

**Output**:
```
Added Laptop to cart
Added Mouse to cart
Added Keyboard to cart

=== Shopping Cart ===
Laptop: $999.99 x 1 = $999.99
Mouse: $29.99 x 2 = $59.98
Keyboard: $79.99 x 1 = $79.99
---
Total Items: 4
Total Price: $1139.96
===================

Updated Mouse quantity

=== Shopping Cart ===
Laptop: $999.99 x 1 = $999.99
Mouse: $29.99 x 3 = $89.97
Keyboard: $79.99 x 1 = $79.99
---
Total Items: 5
Total Price: $1169.95
===================

Removed Keyboard from cart

=== Shopping Cart ===
Laptop: $999.99 x 1 = $999.99
Mouse: $29.99 x 3 = $89.97
---
Total Items: 4
Total Price: $1089.96
===================
```

---

## Exercise 3: User Profile with Lazy Loading

**Goal**: Create a `UserProfile` class that lazily loads expensive data.

**Requirements**:
1. Properties: `username`, `email`
2. Lazy property: `profilePicture` (simulated expensive load)
3. Lazy property: `activityHistory` (simulated database query)
4. Method: `displayProfile()` - shows all info (triggers lazy loading)

---

## Solution: User Profile

```kotlin
data class Activity(val action: String, val timestamp: String)

class UserProfile(val username: String, val email: String) {

    val profilePicture: ByteArray by lazy {
        println("Loading profile picture from server...")
        Thread.sleep(500)  // Simulate network delay
        ByteArray(1024)  // Simulated image data
    }

    val activityHistory: List<Activity> by lazy {
        println("Loading activity history from database...")
        Thread.sleep(300)  // Simulate database query
        listOf(
            Activity("Logged in", "2025-01-15 08:30:00"),
            Activity("Updated profile", "2025-01-15 09:15:00"),
            Activity("Posted comment", "2025-01-15 10:45:00")
        )
    }

    fun displayProfile() {
        println("\n=== User Profile ===")
        println("Username: $username")
        println("Email: $email")
        println("Profile Picture Size: ${profilePicture.size} bytes")
        println("Recent Activities:")
        activityHistory.forEach { activity ->
            println("  - ${activity.action} at ${activity.timestamp}")
        }
        println("===================\n")
    }
}

fun main() {
    println("Creating user profile...")
    val profile = UserProfile("alice_coder", "alice@example.com")

    println("Profile object created (data not loaded yet)")
    Thread.sleep(1000)

    println("\nCalling displayProfile() for the first time...")
    profile.displayProfile()

    println("Calling displayProfile() again (data already cached)...")
    profile.displayProfile()
}
```

**Output**:
```
Creating user profile...
Profile object created (data not loaded yet)

Calling displayProfile() for the first time...
Loading profile picture from server...
Loading activity history from database...

=== User Profile ===
Username: alice_coder
Email: alice@example.com
Profile Picture Size: 1024 bytes
Recent Activities:
  - Logged in at 2025-01-15 08:30:00
  - Updated profile at 2025-01-15 09:15:00
  - Posted comment at 2025-01-15 10:45:00
===================

Calling displayProfile() again (data already cached)...

=== User Profile ===
Username: alice_coder
Email: alice@example.com
Profile Picture Size: 1024 bytes
Recent Activities:
  - Logged in at 2025-01-15 08:30:00
  - Updated profile at 2025-01-15 09:15:00
  - Posted comment at 2025-01-15 10:45:00
===================
```

---

## Checkpoint Quiz

### Question 1
What is the difference between a regular property and a property with a custom getter?

A) Custom getters can only be used with `var`
B) Custom getters compute the value each time the property is accessed
C) Custom getters are slower
D) There is no difference

### Question 2
When should you use `lateinit`?

A) For all properties
B) For properties that will be initialized later, before first use
C) For computed properties
D) For primitive types like Int and Double

### Question 3
What does the `field` keyword refer to in a custom setter?

A) The parameter passed to the setter
B) The backing field (actual storage) of the property
C) The class instance
D) The property name

### Question 4
What is the main benefit of lazy initialization?

A) Properties are initialized faster
B) Expensive operations are deferred until needed
C) Properties use less memory
D) Properties can be null

### Question 5
What happens if you access an uninitialized `lateinit` property?

A) It returns null
B) It returns a default value
C) It throws `UninitializedPropertyAccessException`
D) The code doesn't compile

---

## Quiz Answers

**Question 1: B) Custom getters compute the value each time the property is accessed**

Custom getters don't store a value—they compute it when accessed.

```kotlin
class Rectangle(val width: Double, val height: Double) {
    val area: Double
        get() = width * height  // Computed each time
}
```

---

**Question 2: B) For properties that will be initialized later, before first use**

`lateinit` is perfect for dependency injection, Android views, or any scenario where you can't initialize in the constructor but will initialize before use.

```kotlin
class Service {
    lateinit var database: Database

    fun initialize(db: Database) {
        database = db
    }
}
```

**Note**: Can't be used with primitive types (Int, Double, etc.) or nullable types.

---

**Question 3: B) The backing field (actual storage) of the property**

`field` lets you access the actual stored value in custom accessors.

```kotlin
var age: Int = 0
    set(value) {
        require(value >= 0) { "Age must be non-negative" }
        field = value  // Sets the backing field
    }
```

Without `field`, you'd get infinite recursion!

---

**Question 4: B) Expensive operations are deferred until needed**

Lazy initialization improves performance by delaying expensive operations until they're actually needed.

```kotlin
val heavyData: List<Int> by lazy {
    // This only runs when heavyData is first accessed
    (1..1000000).toList()
}
```

---

**Question 5: C) It throws `UninitializedPropertyAccessException`**

Always initialize `lateinit` properties before using them, or check with `::property.isInitialized`.

```kotlin
lateinit var name: String

// println(name)  // ❌ UninitializedPropertyAccessException

if (::name.isInitialized) {
    println(name)  // ✅ Safe
}
```

---

## What You've Learned

✅ Custom getters for computed properties
✅ Custom setters for validation and transformation
✅ Private setters for controlled access
✅ `lateinit` for delayed initialization
✅ Lazy initialization with the `lazy` delegate
✅ Backing fields with the `field` keyword
✅ Property delegation basics (`observable`, `vetoable`)

---

## Next Steps

In **Lesson 2.3: Inheritance and Polymorphism**, you'll learn:
- Creating class hierarchies with inheritance
- Overriding methods and properties
- Abstract classes for shared behavior
- Polymorphism: treating objects of different types uniformly
- Type checking and casting

You're mastering Kotlin's powerful property system!

---

**Congratulations on completing Lesson 2.2!** 🎉

Properties are the foundation of OOP. Kotlin's property features give you fine-grained control while keeping your code clean and expressive.

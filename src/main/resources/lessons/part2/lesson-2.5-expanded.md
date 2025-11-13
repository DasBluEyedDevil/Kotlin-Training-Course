# Lesson 2.5: Data Classes and Sealed Classes

**Estimated Time**: 65 minutes

---

## Topic Introduction

Kotlin provides special class types that solve common programming patterns elegantly. You've learned about regular classes, abstract classes, and interfaces. Now let's explore two powerful Kotlin features:

**Data Classes**: Classes designed to hold data with automatic implementations of `equals()`, `hashCode()`, `toString()`, and `copy()`.

**Sealed Classes**: Classes with a restricted hierarchy where all subclasses are known at compile-time, perfect for representing state or result types.

These features make Kotlin code more concise, safer, and more expressive than traditional OOP languages.

---

## The Concept

### Why Special Class Types?

**Problem with Regular Classes**:

```kotlin
class User(val name: String, val age: Int)

val user1 = User("Alice", 25)
val user2 = User("Alice", 25)

println(user1 == user2)  // false (different instances!)
println(user1)           // User@4a574795 (not helpful!)
```

**Solution with Data Classes**:

```kotlin
data class User(val name: String, val age: Int)

val user1 = User("Alice", 25)
val user2 = User("Alice", 25)

println(user1 == user2)  // true (compares data!)
println(user1)           // User(name=Alice, age=25) (readable!)
```

---

## Data Classes

### Creating Data Classes

Use the `data` keyword before `class`:

```kotlin
data class Person(val name: String, val age: Int, val email: String)
```

**What Kotlin generates automatically**:
1. **`equals()`** - Compares data, not references
2. **`hashCode()`** - Consistent with `equals()`
3. **`toString()`** - Readable string representation
4. **`copy()`** - Creates copies with modified properties
5. **`componentN()`** - Destructuring declarations

### Requirements for Data Classes

1. Primary constructor must have at least one parameter
2. All primary constructor parameters must be `val` or `var`
3. Cannot be `abstract`, `open`, `sealed`, or `inner`
4. May extend other classes or implement interfaces

### Auto-Generated Functions

**1. `toString()`** - Readable representation

```kotlin
data class User(val name: String, val age: Int)

val user = User("Alice", 25)
println(user)  // User(name=Alice, age=25)
```

**2. `equals()` and `hashCode()`** - Structural equality

```kotlin
data class Point(val x: Int, val y: Int)

val p1 = Point(10, 20)
val p2 = Point(10, 20)
val p3 = Point(30, 40)

println(p1 == p2)  // true (same data)
println(p1 == p3)  // false (different data)

// HashCode consistency
println(p1.hashCode() == p2.hashCode())  // true
```

**3. `copy()`** - Create modified copies

```kotlin
data class User(val name: String, val age: Int, val email: String)

val user = User("Alice", 25, "alice@example.com")

// Create a copy with modified age
val olderUser = user.copy(age = 26)

println(user)       // User(name=Alice, age=25, email=alice@example.com)
println(olderUser)  // User(name=Alice, age=26, email=alice@example.com)

// Copy with multiple changes
val differentUser = user.copy(name = "Bob", age = 30)
println(differentUser)  // User(name=Bob, age=30, email=alice@example.com)
```

**Why `copy()` matters**:
- Immutability: Don't modify original, create new versions
- Thread safety: Immutable data is inherently thread-safe
- Functional programming: Transform data without side effects

---

## Destructuring Declarations

Data classes support **destructuring** - extracting multiple values at once:

```kotlin
data class User(val name: String, val age: Int, val email: String)

val user = User("Alice", 25, "alice@example.com")

// Destructure into separate variables
val (name, age, email) = user

println(name)   // Alice
println(age)    // 25
println(email)  // alice@example.com
```

**How it works**: Kotlin generates `component1()`, `component2()`, etc. functions:

```kotlin
val name = user.component1()  // Same as destructuring
val age = user.component2()
val email = user.component3()
```

**Partial Destructuring**:

```kotlin
val (name, age) = user  // Only extract first two
val (_, _, email) = user  // Skip first two with underscore
```

**Destructuring in Loops**:

```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(
    Person("Alice", 25),
    Person("Bob", 30),
    Person("Carol", 22)
)

for ((name, age) in people) {
    println("$name is $age years old")
}
```

---

## Real-World Data Class Examples

### Example 1: API Response

```kotlin
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class User(val id: Int, val username: String, val email: String)

fun fetchUser(id: Int): ApiResponse<User> {
    return if (id > 0) {
        val user = User(id, "alice", "alice@example.com")
        ApiResponse(success = true, data = user, message = "User found")
    } else {
        ApiResponse(success = false, data = null, message = "Invalid user ID")
    }
}

fun main() {
    val response = fetchUser(1)
    println(response)

    if (response.success) {
        val user = response.data
        println("User: ${user?.username}")
    }
}
```

### Example 2: Coordinates and Geometry

```kotlin
data class Point(val x: Double, val y: Double) {
    fun distanceTo(other: Point): Double {
        val dx = x - other.x
        val dy = y - other.y
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}

data class Rectangle(val topLeft: Point, val bottomRight: Point) {
    val width: Double
        get() = bottomRight.x - topLeft.x

    val height: Double
        get() = bottomRight.y - topLeft.y

    val area: Double
        get() = width * height
}

fun main() {
    val p1 = Point(0.0, 0.0)
    val p2 = Point(3.0, 4.0)

    println("Distance: ${p1.distanceTo(p2)}")  // 5.0

    val rect = Rectangle(Point(0.0, 10.0), Point(5.0, 0.0))
    println("Area: ${rect.area}")  // 50.0
}
```

---

## Sealed Classes

**Sealed classes** represent restricted class hierarchies where all subclasses are known at compile-time.

### Why Sealed Classes?

**Problem**: Modeling states or results with regular classes

```kotlin
open class Result
class Success(val data: String) : Result()
class Error(val message: String) : Result()

fun handleResult(result: Result) {
    when (result) {
        is Success -> println("Success: ${result.data}")
        is Error -> println("Error: ${result.message}")
        // What if we add a new subclass? Compiler won't warn us!
    }
}
```

**Solution**: Sealed classes

```kotlin
sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val message: String) : Result()
    object Loading : Result()
}

fun handleResult(result: Result) {
    when (result) {
        is Result.Success -> println("Success: ${result.data}")
        is Result.Error -> println("Error: ${result.message}")
        Result.Loading -> println("Loading...")
        // ✅ Compiler ensures all cases are covered!
    }
}
```

### Defining Sealed Classes

```kotlin
sealed class NetworkResult {
    data class Success(val data: String) : NetworkResult()
    data class Error(val code: Int, val message: String) : NetworkResult()
    object Loading : NetworkResult()
    object Idle : NetworkResult()
}
```

**Key Points**:
- Subclasses must be defined in the same file (or as nested classes)
- Cannot be instantiated directly
- Perfect for `when` expressions (exhaustive checking)

---

## Sealed Classes for State Management

```kotlin
sealed class UiState {
    object Loading : UiState()
    data class Success(val items: List<String>) : UiState()
    data class Error(val message: String) : UiState()
    object Empty : UiState()
}

class ViewModel {
    private var state: UiState = UiState.Loading

    fun loadData() {
        state = UiState.Loading
        displayState()

        // Simulate loading
        Thread.sleep(1000)

        val items = listOf("Item 1", "Item 2", "Item 3")
        state = if (items.isNotEmpty()) {
            UiState.Success(items)
        } else {
            UiState.Empty
        }
        displayState()
    }

    fun displayState() {
        when (state) {
            is UiState.Loading -> println("⏳ Loading...")
            is UiState.Success -> {
                val items = (state as UiState.Success).items
                println("✅ Loaded ${items.size} items: $items")
            }
            is UiState.Error -> {
                val message = (state as UiState.Error).message
                println("❌ Error: $message")
            }
            UiState.Empty -> println("📭 No items found")
        }
    }
}

fun main() {
    val viewModel = ViewModel()
    viewModel.loadData()
}
```

---

## Enum Classes

**Enum classes** define a fixed set of constants.

```kotlin
enum class Direction {
    NORTH, SOUTH, EAST, WEST
}

enum class Priority(val level: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    fun isUrgent() = level >= 3
}

fun main() {
    val direction = Direction.NORTH
    println(direction)  // NORTH

    val priority = Priority.HIGH
    println("Level: ${priority.level}")  // Level: 3
    println("Urgent: ${priority.isUrgent()}")  // Urgent: true

    // Iterate over all values
    Priority.values().forEach { p ->
        println("${p.name}: Level ${p.level}")
    }

    // String to enum
    val p = Priority.valueOf("MEDIUM")
    println(p.level)  // 2
}
```

**Enum vs Sealed Class**:

| Feature | Enum | Sealed Class |
|---------|------|--------------|
| Fixed set of instances | ✅ Yes (all at compile-time) | ✅ Yes (types known at compile-time) |
| Can have different data | ❌ No (same structure) | ✅ Yes (different properties) |
| Can inherit | ❌ No | ✅ Yes |
| When to use | Finite set of constants | Type hierarchies with different data |

---

## Value Classes (Inline Classes)

**Value classes** provide type safety without runtime overhead.

```kotlin
@JvmInline
value class UserId(val value: Int)

@JvmInline
value class Email(val value: String) {
    init {
        require(value.contains("@")) { "Invalid email" }
    }
}

fun sendEmail(email: Email) {
    println("Sending email to ${email.value}")
}

fun main() {
    val userId = UserId(123)
    val email = Email("alice@example.com")

    // sendEmail(UserId(456))  // ❌ Type mismatch!
    sendEmail(email)  // ✅ Correct type

    // At runtime, email is just a String (no wrapper object)
}
```

**Benefits**:
- Type safety: Can't accidentally pass wrong type
- Zero runtime overhead: Unwrapped at runtime
- Validation in init block

---

## Exercise 1: Product Catalog System

**Goal**: Create a product catalog using data classes.

**Requirements**:
1. Data class `Product` with: `id`, `name`, `price`, `category`, `inStock`
2. Data class `Order` with: `orderId`, `products: List<Product>`, `total`
3. Function to calculate total from products
4. Function to create a modified order with discount
5. Test with sample products and orders

---

## Solution: Product Catalog

```kotlin
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val inStock: Boolean = true
)

data class Order(
    val orderId: String,
    val products: List<Product>,
    val discount: Double = 0.0
) {
    val subtotal: Double
        get() = products.sumOf { it.price }

    val total: Double
        get() = subtotal - discount

    fun applyDiscount(discountAmount: Double): Order {
        return copy(discount = discountAmount)
    }

    fun displayOrder() {
        println("\n=== Order $orderId ===")
        products.forEach { product ->
            println("${product.name} - $${product.price}")
        }
        println("---")
        println("Subtotal: $$subtotal")
        if (discount > 0) {
            println("Discount: -$$discount")
        }
        println("Total: $$total")
        println("===================\n")
    }
}

fun main() {
    val products = listOf(
        Product(1, "Laptop", 999.99, "Electronics"),
        Product(2, "Mouse", 29.99, "Electronics"),
        Product(3, "Keyboard", 79.99, "Electronics"),
        Product(4, "Monitor", 299.99, "Electronics"),
        Product(5, "Desk Lamp", 39.99, "Furniture", inStock = false)
    )

    // Filter in-stock products
    val availableProducts = products.filter { it.inStock }

    // Create order
    val order = Order(
        orderId = "ORD-2025-001",
        products = listOf(
            products[0],  // Laptop
            products[1],  // Mouse
            products[2]   // Keyboard
        )
    )

    order.displayOrder()

    // Apply discount
    val discountedOrder = order.applyDiscount(50.0)
    discountedOrder.displayOrder()

    // Destructuring
    val (orderId, items, discount) = discountedOrder
    println("Order ID: $orderId")
    println("Number of items: ${items.size}")
    println("Discount: $$discount")
}
```

---

## Exercise 2: API Result with Sealed Classes

**Goal**: Model API responses using sealed classes.

**Requirements**:
1. Sealed class `ApiResult<T>` with subclasses: `Success`, `Error`, `Loading`
2. Function `fetchData()` that returns different results
3. Function `handleResult()` that processes each case
4. Test with different scenarios

---

## Solution: API Result

```kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

data class User(val id: Int, val name: String, val email: String)

fun fetchUser(userId: Int): ApiResult<User> {
    return when {
        userId <= 0 -> ApiResult.Error(400, "Invalid user ID")
        userId == 999 -> ApiResult.Loading
        else -> ApiResult.Success(User(userId, "User $userId", "user$userId@example.com"))
    }
}

fun <T> handleResult(result: ApiResult<T>, onSuccess: (T) -> Unit) {
    when (result) {
        is ApiResult.Success -> {
            println("✅ Success!")
            onSuccess(result.data)
        }
        is ApiResult.Error -> {
            println("❌ Error ${result.code}: ${result.message}")
        }
        ApiResult.Loading -> {
            println("⏳ Loading...")
        }
    }
}

fun main() {
    println("=== Fetch User 1 ===")
    val result1 = fetchUser(1)
    handleResult(result1) { user ->
        println("User: ${user.name} (${user.email})")
    }

    println("\n=== Fetch Invalid User ===")
    val result2 = fetchUser(-1)
    handleResult(result2) { user ->
        println("User: ${user.name}")
    }

    println("\n=== Fetch Loading State ===")
    val result3 = fetchUser(999)
    handleResult(result3) { user ->
        println("User: ${user.name}")
    }

    // Using when expression directly
    println("\n=== Direct when expression ===")
    val message = when (val result = fetchUser(5)) {
        is ApiResult.Success -> "Loaded: ${result.data.name}"
        is ApiResult.Error -> "Failed: ${result.message}"
        ApiResult.Loading -> "Please wait..."
    }
    println(message)
}
```

---

## Exercise 3: Task Management with Sealed Classes

**Goal**: Build a task management system using sealed classes for task states.

**Requirements**:
1. Sealed class `TaskState` with: `Todo`, `InProgress`, `Completed`, `Cancelled`
2. Data class `Task` with: `id`, `title`, `description`, `state`
3. Functions to transition between states
4. Track state change history

---

## Solution: Task Management

```kotlin
sealed class TaskState {
    object Todo : TaskState() {
        override fun toString() = "TODO"
    }

    data class InProgress(val assignee: String, val startedAt: Long = System.currentTimeMillis()) : TaskState() {
        override fun toString() = "IN_PROGRESS (Assignee: $assignee)"
    }

    data class Completed(val completedBy: String, val completedAt: Long = System.currentTimeMillis()) : TaskState() {
        override fun toString() = "COMPLETED (By: $completedBy)"
    }

    data class Cancelled(val reason: String) : TaskState() {
        override fun toString() = "CANCELLED (Reason: $reason)"
    }
}

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val state: TaskState = TaskState.Todo,
    val history: List<TaskState> = listOf(TaskState.Todo)
) {
    fun startWork(assignee: String): Task {
        require(state is TaskState.Todo) { "Task must be in TODO state to start" }
        val newState = TaskState.InProgress(assignee)
        return copy(state = newState, history = history + newState)
    }

    fun complete(completedBy: String): Task {
        require(state is TaskState.InProgress) { "Task must be in progress to complete" }
        val newState = TaskState.Completed(completedBy)
        return copy(state = newState, history = history + newState)
    }

    fun cancel(reason: String): Task {
        require(state !is TaskState.Completed) { "Cannot cancel completed task" }
        val newState = TaskState.Cancelled(reason)
        return copy(state = newState, history = history + newState)
    }

    fun displayTask() {
        println("\n=== Task #$id ===")
        println("Title: $title")
        println("Description: $description")
        println("Current State: $state")
        println("\nState History:")
        history.forEachIndexed { index, state ->
            println("  ${index + 1}. $state")
        }
        println("================\n")
    }

    fun getStatusEmoji(): String = when (state) {
        is TaskState.Todo -> "📝"
        is TaskState.InProgress -> "🔄"
        is TaskState.Completed -> "✅"
        is TaskState.Cancelled -> "❌"
    }
}

class TaskManager {
    private val tasks = mutableMapOf<Int, Task>()
    private var nextId = 1

    fun createTask(title: String, description: String): Task {
        val task = Task(nextId++, title, description)
        tasks[task.id] = task
        println("Created task: ${task.getStatusEmoji()} ${task.title}")
        return task
    }

    fun updateTask(task: Task) {
        tasks[task.id] = task
        println("Updated task: ${task.getStatusEmoji()} ${task.title} -> ${task.state}")
    }

    fun listTasks() {
        println("\n=== All Tasks ===")
        tasks.values.forEach { task ->
            println("${task.getStatusEmoji()} #${task.id}: ${task.title} [${task.state}]")
        }
        println("=================\n")
    }
}

fun main() {
    val manager = TaskManager()

    // Create tasks
    var task1 = manager.createTask("Implement login", "Add JWT authentication")
    var task2 = manager.createTask("Fix bug #123", "Null pointer exception in profile")
    var task3 = manager.createTask("Write tests", "Unit tests for payment module")

    manager.listTasks()

    // Start working on tasks
    task1 = task1.startWork("Alice")
    manager.updateTask(task1)

    task2 = task2.startWork("Bob")
    manager.updateTask(task2)

    manager.listTasks()

    // Complete a task
    task1 = task1.complete("Alice")
    manager.updateTask(task1)

    // Cancel a task
    task3 = task3.cancel("Requirements changed")
    manager.updateTask(task3)

    manager.listTasks()

    // Display full history
    task1.displayTask()
}
```

---

## Checkpoint Quiz

### Question 1
What does the `data` keyword do?

A) Makes the class immutable
B) Automatically generates `equals()`, `hashCode()`, `toString()`, and `copy()`
C) Makes the class faster
D) Allows inheritance

### Question 2
What is destructuring in data classes?

A) Deleting the class
B) Extracting multiple properties into separate variables at once
C) Breaking inheritance
D) Splitting the class into multiple files

### Question 3
What is the main advantage of sealed classes?

A) They're faster
B) They provide exhaustive `when` expression checking
C) They use less memory
D) They can have multiple constructors

### Question 4
When should you use a data class?

A) When you need inheritance
B) When you primarily need to hold data
C) When you need abstract methods
D) When you need multiple constructors

### Question 5
What's the difference between enum and sealed classes?

A) Enums are faster
B) Sealed classes can have subclasses with different properties; enums cannot
C) Enums can inherit; sealed classes cannot
D) There is no difference

---

## Quiz Answers

**Question 1: B) Automatically generates `equals()`, `hashCode()`, `toString()`, and `copy()`**

Data classes save you from writing boilerplate code.

```kotlin
data class User(val name: String, val age: Int)

// Automatically generates:
// - equals() for structural equality
// - hashCode() consistent with equals()
// - toString() for readable output
// - copy() for creating modified copies
// - componentN() for destructuring
```

---

**Question 2: B) Extracting multiple properties into separate variables at once**

Destructuring uses the `componentN()` functions generated by data classes.

```kotlin
data class Point(val x: Int, val y: Int)

val point = Point(10, 20)
val (x, y) = point  // Destructuring

println(x)  // 10
println(y)  // 20
```

---

**Question 3: B) They provide exhaustive `when` expression checking**

The compiler ensures you handle all subclasses of a sealed class.

```kotlin
sealed class Result {
    object Success : Result()
    object Error : Result()
}

fun handle(result: Result) = when (result) {
    Result.Success -> "OK"
    Result.Error -> "Failed"
    // ✅ Compiler ensures all cases covered!
}
```

---

**Question 4: B) When you primarily need to hold data**

Data classes are perfect for DTOs, API models, configuration, etc.

```kotlin
// ✅ Good use of data class
data class User(val id: Int, val name: String, val email: String)

// ❌ Bad use (lots of behavior, not primarily data)
data class DatabaseConnection(val url: String) {
    fun connect() { }
    fun query(sql: String) { }
    fun disconnect() { }
}
```

---

**Question 5: B) Sealed classes can have subclasses with different properties; enums cannot**

Enums are for fixed constants with the same structure. Sealed classes are for type hierarchies with varying data.

```kotlin
// Enum: All instances have same structure
enum class Color(val hex: String) {
    RED("#FF0000"),
    GREEN("#00FF00")
}

// Sealed: Subclasses have different properties
sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val code: Int, val message: String) : Result()
}
```

---

## What You've Learned

✅ Data classes and their auto-generated functions
✅ The `copy()` function for immutable updates
✅ Destructuring declarations
✅ Sealed classes for restricted hierarchies
✅ Enum classes for fixed constants
✅ Value classes for type-safe primitives
✅ When to use each special class type

---

## Next Steps

In **Lesson 2.6: Object Declarations and Companion Objects**, you'll learn:
- Object expressions for anonymous objects
- Object declarations for singletons
- Companion objects for static-like members
- Factory methods and constants
- When to use objects vs classes

You're almost done with Part 2!

---

**Congratulations on completing Lesson 2.5!** 🎉

Data classes and sealed classes are Kotlin superpowers that make your code more concise and safer!

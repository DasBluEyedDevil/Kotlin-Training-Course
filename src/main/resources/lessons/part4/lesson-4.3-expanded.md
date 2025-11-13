# Lesson 3.3: Collection Operations

**Estimated Time**: 70 minutes
**Difficulty**: Intermediate
**Prerequisites**: Lessons 3.1-3.2 (Functional programming basics, lambdas)

---

## Topic Introduction

Collections are everywhere in programming. Lists of users, sets of products, maps of configurations—they're fundamental to real applications. The way you work with collections defines your code quality.

Kotlin's collection operations transform data manipulation from verbose loops into expressive, declarative pipelines. Instead of writing "how" to process data step-by-step, you declare "what" you want.

In this lesson, you'll master:
- Essential operations: map, filter, reduce
- Finding elements: find, first, last, any, all, none
- Advanced grouping: groupBy, partition, associate
- Flattening nested structures: flatMap, flatten
- Sequences for lazy evaluation and performance

By the end, you'll process data with elegance and efficiency!

---

## The Concept: Transforming vs Iterating

### The Traditional Way (Imperative)

```kotlin
// Calculate total price of items over $100
val items = listOf(50.0, 120.0, 75.0, 200.0, 95.0)
var total = 0.0
for (price in items) {
    if (price > 100) {
        total += price
    }
}
println(total)  // 320.0
```

### The Functional Way (Declarative)

```kotlin
val items = listOf(50.0, 120.0, 75.0, 200.0, 95.0)
val total = items
    .filter { it > 100 }
    .sum()
println(total)  // 320.0
```

**Benefits**:
- Clearer intent (filter, then sum)
- No mutable state (`var total`)
- Chainable operations
- Less error-prone
- Easier to test and reason about

---

## Map: Transforming Elements

`map` transforms each element using a function.

### Basic Map

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// Transform each number
val doubled = numbers.map { it * 2 }
println(doubled)  // [2, 4, 6, 8, 10]

val squared = numbers.map { it * it }
println(squared)  // [1, 4, 9, 16, 25]

// Transform to different type
val asStrings = numbers.map { "Number: $it" }
println(asStrings)  // [Number: 1, Number: 2, ...]
```

### Map with Objects

```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(
    Person("Alice", 25),
    Person("Bob", 30),
    Person("Charlie", 35)
)

// Extract property
val names = people.map { it.name }
println(names)  // [Alice, Bob, Charlie]

// Or use member reference
val ages = people.map(Person::age)
println(ages)  // [25, 30, 35]

// Transform to different object
data class NameTag(val label: String)
val tags = people.map { NameTag("Hello, I'm ${it.name}") }
println(tags)
// [NameTag(label=Hello, I'm Alice), ...]
```

### MapIndexed: Transform with Index

```kotlin
val fruits = listOf("apple", "banana", "cherry")

val indexed = fruits.mapIndexed { index, fruit ->
    "$index: $fruit"
}
println(indexed)  // [0: apple, 1: banana, 2: cherry]
```

### MapNotNull: Transform and Filter Nulls

```kotlin
val input = listOf("1", "2", "abc", "3", "xyz")

val numbers = input.mapNotNull { it.toIntOrNull() }
println(numbers)  // [1, 2, 3]
```

---

## Filter: Selecting Elements

`filter` keeps only elements matching a predicate.

### Basic Filter

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Keep even numbers
val evens = numbers.filter { it % 2 == 0 }
println(evens)  // [2, 4, 6, 8, 10]

// Keep numbers greater than 5
val bigNumbers = numbers.filter { it > 5 }
println(bigNumbers)  // [6, 7, 8, 9, 10]

// Multiple conditions
val filtered = numbers.filter { it > 3 && it < 8 }
println(filtered)  // [4, 5, 6, 7]
```

### Filter with Objects

```kotlin
data class Product(val name: String, val price: Double, val inStock: Boolean)

val products = listOf(
    Product("Laptop", 1200.0, true),
    Product("Mouse", 25.0, false),
    Product("Keyboard", 75.0, true),
    Product("Monitor", 300.0, true)
)

// Available products
val available = products.filter { it.inStock }
println(available.map { it.name })  // [Laptop, Keyboard, Monitor]

// Expensive products in stock
val expensiveAvailable = products.filter { it.price > 100 && it.inStock }
println(expensiveAvailable.map { it.name })  // [Laptop, Monitor]
```

### FilterNot: Opposite of Filter

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// Keep odd numbers (not even)
val odds = numbers.filterNot { it % 2 == 0 }
println(odds)  // [1, 3, 5]
```

### FilterIsInstance: Filter by Type

```kotlin
val mixed: List<Any> = listOf(1, "hello", 2, "world", 3.14, true)

val strings = mixed.filterIsInstance<String>()
println(strings)  // [hello, world]

val numbers = mixed.filterIsInstance<Int>()
println(numbers)  // [1, 2]
```

---

## Reduce and Fold: Accumulating Values

Reduce/fold combine all elements into a single value.

### Reduce

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// Sum all numbers
val sum = numbers.reduce { acc, number -> acc + number }
println(sum)  // 15

// Product of all numbers
val product = numbers.reduce { acc, number -> acc * number }
println(product)  // 120

// Find maximum
val max = numbers.reduce { acc, number ->
    if (number > acc) number else acc
}
println(max)  // 5
```

### Fold: Reduce with Initial Value

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// Sum with initial value
val sum = numbers.fold(0) { acc, number -> acc + number }
println(sum)  // 15

// Start with 100
val sumWith100 = numbers.fold(100) { acc, number -> acc + number }
println(sumWith100)  // 115

// Build a string
val text = numbers.fold("Numbers: ") { acc, number ->
    "$acc$number, "
}
println(text)  // Numbers: 1, 2, 3, 4, 5,
```

### Practical Example: Complex Accumulation

```kotlin
data class Transaction(val amount: Double, val type: String)

val transactions = listOf(
    Transaction(100.0, "income"),
    Transaction(50.0, "expense"),
    Transaction(200.0, "income"),
    Transaction(30.0, "expense"),
    Transaction(150.0, "income")
)

// Calculate net balance
val balance = transactions.fold(0.0) { acc, transaction ->
    when (transaction.type) {
        "income" -> acc + transaction.amount
        "expense" -> acc - transaction.amount
        else -> acc
    }
}
println("Balance: $$balance")  // Balance: $370.0
```

---

## Finding Elements

### find: First Match or Null

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6)

val firstEven = numbers.find { it % 2 == 0 }
println(firstEven)  // 2

val firstBig = numbers.find { it > 10 }
println(firstBig)  // null
```

### findLast: Last Match or Null

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6)

val lastEven = numbers.findLast { it % 2 == 0 }
println(lastEven)  // 6
```

### first and last

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// First element
println(numbers.first())  // 1

// First matching predicate
println(numbers.first { it > 3 })  // 4

// Throws exception if not found
// println(numbers.first { it > 10 })  // NoSuchElementException

// Safe version
println(numbers.firstOrNull { it > 10 })  // null

// Last element
println(numbers.last())  // 5
```

### any, all, none: Boolean Checks

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// Any element matches?
println(numbers.any { it > 3 })  // true
println(numbers.any { it > 10 })  // false

// All elements match?
println(numbers.all { it > 0 })  // true
println(numbers.all { it > 3 })  // false

// No elements match?
println(numbers.none { it < 0 })  // true
println(numbers.none { it > 3 })  // false
```

### Practical Example: Validation

```kotlin
data class User(val name: String, val age: Int, val email: String)

val users = listOf(
    User("Alice", 25, "alice@example.com"),
    User("Bob", 17, "bob@example.com"),
    User("Charlie", 30, "charlie@example.com")
)

// Check if any user is underage
val hasMinors = users.any { it.age < 18 }
println("Has minors: $hasMinors")  // true

// Check if all have valid emails
val allValidEmails = users.all { it.email.contains("@") }
println("All valid emails: $allValidEmails")  // true

// Check if no user has empty name
val noEmptyNames = users.none { it.name.isEmpty() }
println("No empty names: $noEmptyNames")  // true
```

---

## Grouping and Partitioning

### groupBy: Group into Map

```kotlin
data class Person(val name: String, val age: Int, val city: String)

val people = listOf(
    Person("Alice", 25, "NYC"),
    Person("Bob", 30, "LA"),
    Person("Charlie", 25, "NYC"),
    Person("Diana", 30, "LA")
)

// Group by age
val byAge = people.groupBy { it.age }
println(byAge)
// {25=[Person(Alice, 25, NYC), Person(Charlie, 25, NYC)],
//  30=[Person(Bob, 30, LA), Person(Diana, 30, LA)]}

// Group by city
val byCity = people.groupBy { it.city }
println(byCity.keys)  // [NYC, LA]

// Group and transform
val namesByCity = people.groupBy(
    keySelector = { it.city },
    valueTransform = { it.name }
)
println(namesByCity)
// {NYC=[Alice, Charlie], LA=[Bob, Diana]}
```

### partition: Split into Two Groups

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Split into even and odd
val (evens, odds) = numbers.partition { it % 2 == 0 }
println("Evens: $evens")  // [2, 4, 6, 8, 10]
println("Odds: $odds")    // [1, 3, 5, 7, 9]

// Practical example
data class Task(val name: String, val completed: Boolean)

val tasks = listOf(
    Task("Write code", true),
    Task("Write tests", false),
    Task("Review PR", true),
    Task("Deploy", false)
)

val (completed, pending) = tasks.partition { it.completed }
println("Completed: ${completed.map { it.name }}")  // [Write code, Review PR]
println("Pending: ${pending.map { it.name }}")      // [Write tests, Deploy]
```

### associate: Create Map

```kotlin
val people = listOf("Alice", "Bob", "Charlie")

// Create map from list
val ages = people.associateWith { it.length }
println(ages)  // {Alice=5, Bob=3, Charlie=7}

// Associate with key
val byFirstLetter = people.associateBy { it.first() }
println(byFirstLetter)  // {A=Alice, B=Bob, C=Charlie}

// Full control
val custom = people.associate { name ->
    name.uppercase() to name.length
}
println(custom)  // {ALICE=5, BOB=3, CHARLIE=7}
```

---

## FlatMap and Flatten

### flatten: Flatten Nested Collections

```kotlin
val nested = listOf(
    listOf(1, 2, 3),
    listOf(4, 5),
    listOf(6, 7, 8, 9)
)

val flat = nested.flatten()
println(flat)  // [1, 2, 3, 4, 5, 6, 7, 8, 9]
```

### flatMap: Map Then Flatten

```kotlin
data class Order(val id: Int, val items: List<String>)

val orders = listOf(
    Order(1, listOf("Laptop", "Mouse")),
    Order(2, listOf("Keyboard", "Monitor", "Cable")),
    Order(3, listOf("Phone"))
)

// Get all items across all orders
val allItems = orders.flatMap { it.items }
println(allItems)
// [Laptop, Mouse, Keyboard, Monitor, Cable, Phone]

// Equivalent to map + flatten
val allItems2 = orders.map { it.items }.flatten()
println(allItems2)
// [Laptop, Mouse, Keyboard, Monitor, Cable, Phone]
```

### Practical Example: Hierarchical Data

```kotlin
data class Department(val name: String, val employees: List<Employee>)
data class Employee(val name: String, val skills: List<String>)

val departments = listOf(
    Department("Engineering", listOf(
        Employee("Alice", listOf("Kotlin", "Java", "Python")),
        Employee("Bob", listOf("JavaScript", "TypeScript"))
    )),
    Department("Design", listOf(
        Employee("Charlie", listOf("Figma", "Photoshop")),
        Employee("Diana", listOf("Illustrator", "Sketch"))
    ))
)

// All employees across departments
val allEmployees = departments.flatMap { it.employees }
println("Total employees: ${allEmployees.size}")  // 4

// All unique skills across company
val allSkills = departments
    .flatMap { it.employees }
    .flatMap { it.skills }
    .toSet()
println("All skills: $allSkills")
// [Kotlin, Java, Python, JavaScript, TypeScript, Figma, Photoshop, Illustrator, Sketch]
```

---

## Sequences: Lazy Evaluation

Collections process eagerly (all at once). Sequences process lazily (on demand).

### The Problem with Eager Evaluation

```kotlin
val numbers = (1..1_000_000).toList()

// Each operation creates intermediate list
val result = numbers
    .map { it * 2 }        // Creates 1M element list
    .filter { it > 100 }   // Creates another list
    .take(10)              // Finally takes 10

// Memory inefficient!
```

### Sequences to the Rescue

```kotlin
val numbers = (1..1_000_000).asSequence()

val result = numbers
    .map { it * 2 }        // Doesn't execute yet
    .filter { it > 100 }   // Doesn't execute yet
    .take(10)              // Still lazy
    .toList()              // NOW it executes, processes only what's needed

println(result)
// [102, 104, 106, 108, 110, 112, 114, 116, 118, 120]
```

### How Sequences Work

```kotlin
val numbers = sequenceOf(1, 2, 3, 4, 5)

val result = numbers
    .map {
        println("Mapping $it")
        it * 2
    }
    .filter {
        println("Filtering $it")
        it > 4
    }
    .toList()

// Output shows element-by-element processing:
// Mapping 1
// Filtering 2
// Mapping 2
// Filtering 4
// Mapping 3
// Filtering 6
// Mapping 4
// Filtering 8
// Mapping 5
// Filtering 10

println(result)  // [6, 8, 10]
```

### When to Use Sequences

**Use sequences when**:
- ✅ Large collections (1000+ elements)
- ✅ Multiple chained operations
- ✅ Only need part of result (take, first)
- ✅ Infinite data streams

**Use regular collections when**:
- ✅ Small collections (< 100 elements)
- ✅ Single operation
- ✅ Need the entire result anyway

### Performance Comparison

```kotlin
fun measureTime(label: String, block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val elapsed = System.currentTimeMillis() - start
    println("$label: ${elapsed}ms")
}

val largeList = (1..10_000_000).toList()

measureTime("List") {
    val result = largeList
        .map { it * 2 }
        .filter { it > 1000 }
        .take(100)
        .sum()
}

measureTime("Sequence") {
    val result = largeList.asSequence()
        .map { it * 2 }
        .filter { it > 1000 }
        .take(100)
        .sum()
}

// Typical output:
// List: 450ms
// Sequence: 0ms (processes only ~51 elements!)
```

---

## Chaining Operations

The real power comes from combining operations.

### Example 1: E-Commerce Analysis

```kotlin
data class Product(val name: String, val category: String, val price: Double, val rating: Double)

val products = listOf(
    Product("Laptop", "Electronics", 1200.0, 4.5),
    Product("Mouse", "Electronics", 25.0, 4.2),
    Product("Desk", "Furniture", 300.0, 4.7),
    Product("Chair", "Furniture", 250.0, 4.6),
    Product("Monitor", "Electronics", 400.0, 4.8),
    Product("Lamp", "Furniture", 50.0, 4.1)
)

// Find expensive, highly-rated electronics
val topElectronics = products
    .filter { it.category == "Electronics" }
    .filter { it.price > 100 }
    .filter { it.rating >= 4.5 }
    .sortedByDescending { it.rating }
    .map { it.name }

println("Top electronics: $topElectronics")
// [Monitor, Laptop]

// Average price by category
val avgPriceByCategory = products
    .groupBy { it.category }
    .mapValues { (_, products) ->
        products.map { it.price }.average()
    }

println("Average prices: $avgPriceByCategory")
// {Electronics=541.67, Furniture=200.0}
```

### Example 2: Student Grade Analysis

```kotlin
data class Student(val name: String, val grades: List<Int>, val major: String)

val students = listOf(
    Student("Alice", listOf(85, 90, 92), "CS"),
    Student("Bob", listOf(78, 82, 80), "Math"),
    Student("Charlie", listOf(95, 98, 96), "CS"),
    Student("Diana", listOf(88, 85, 90), "Math"),
    Student("Eve", listOf(70, 75, 72), "CS")
)

// CS students with average > 85
val topCSStudents = students
    .filter { it.major == "CS" }
    .map { student ->
        student.name to student.grades.average()
    }
    .filter { (_, avg) -> avg > 85 }
    .sortedByDescending { (_, avg) -> avg }

println("Top CS students:")
topCSStudents.forEach { (name, avg) ->
    println("  $name: ${"%.1f".format(avg)}")
}
// Top CS students:
//   Charlie: 96.3
//   Alice: 89.0

// All grades flattened and analyzed
val allGrades = students.flatMap { it.grades }
println("Total grades: ${allGrades.size}")  // 15
println("Highest grade: ${allGrades.maxOrNull()}")  // 98
println("Average: ${"%.1f".format(allGrades.average())}")  // 84.7
```

---

## Exercise 1: Sales Data Analysis

**Goal**: Analyze sales data using collection operations.

**Task**: Given sales data, calculate:
1. Total revenue
2. Number of sales over $100
3. Average sale amount
4. Best-selling product

```kotlin
data class Sale(val product: String, val amount: Double, val quantity: Int)

fun main() {
    val sales = listOf(
        Sale("Laptop", 1200.0, 2),
        Sale("Mouse", 25.0, 10),
        Sale("Keyboard", 75.0, 5),
        Sale("Monitor", 300.0, 3),
        Sale("Laptop", 1200.0, 1),
        Sale("Mouse", 25.0, 15)
    )

    // TODO: Implement analysis
}
```

---

## Solution 1: Sales Data Analysis

```kotlin
data class Sale(val product: String, val amount: Double, val quantity: Int)

fun main() {
    val sales = listOf(
        Sale("Laptop", 1200.0, 2),
        Sale("Mouse", 25.0, 10),
        Sale("Keyboard", 75.0, 5),
        Sale("Monitor", 300.0, 3),
        Sale("Laptop", 1200.0, 1),
        Sale("Mouse", 25.0, 15)
    )

    // 1. Total revenue
    val totalRevenue = sales.sumOf { it.amount * it.quantity }
    println("Total revenue: $${"%.2f".format(totalRevenue)}")
    // Total revenue: $5500.00

    // 2. Number of sales over $100 total
    val bigSales = sales.count { it.amount * it.quantity > 100 }
    println("Sales over $100: $bigSales")
    // Sales over $100: 5

    // 3. Average sale amount
    val avgSale = sales.map { it.amount * it.quantity }.average()
    println("Average sale: $${"%.2f".format(avgSale)}")
    // Average sale: $916.67

    // 4. Best-selling product (by quantity)
    val bestSeller = sales
        .groupBy { it.product }
        .mapValues { (_, sales) -> sales.sumOf { it.quantity } }
        .maxByOrNull { it.value }

    println("Best seller: ${bestSeller?.key} (${bestSeller?.value} units)")
    // Best seller: Mouse (25 units)

    // Bonus: Revenue by product
    val revenueByProduct = sales
        .groupBy { it.product }
        .mapValues { (_, sales) ->
            sales.sumOf { it.amount * it.quantity }
        }
        .toList()
        .sortedByDescending { it.second }

    println("\nRevenue by product:")
    revenueByProduct.forEach { (product, revenue) ->
        println("  $product: $${"%.2f".format(revenue)}")
    }
    // Laptop: $3600.00
    // Monitor: $900.00
    // Mouse: $625.00
    // Keyboard: $375.00
}
```

**Explanation**:
- `sumOf` calculates total with transformation
- `count` with predicate counts matches
- `groupBy` + `mapValues` aggregates by key
- `maxByOrNull` finds maximum based on criteria

---

## Exercise 2: Text Processing

**Goal**: Process log files using collection operations.

**Task**: Parse log entries and:
1. Count errors
2. Find unique users
3. Group by log level
4. Get most recent error

```kotlin
data class LogEntry(
    val timestamp: Long,
    val level: String,
    val user: String,
    val message: String
)

fun main() {
    val logs = listOf(
        LogEntry(1000, "INFO", "alice", "User logged in"),
        LogEntry(2000, "ERROR", "bob", "Connection failed"),
        LogEntry(3000, "INFO", "alice", "Data saved"),
        LogEntry(4000, "WARN", "charlie", "Slow query"),
        LogEntry(5000, "ERROR", "alice", "Timeout"),
        LogEntry(6000, "INFO", "bob", "Request completed")
    )

    // TODO: Process logs
}
```

---

## Solution 2: Text Processing

```kotlin
data class LogEntry(
    val timestamp: Long,
    val level: String,
    val user: String,
    val message: String
)

fun main() {
    val logs = listOf(
        LogEntry(1000, "INFO", "alice", "User logged in"),
        LogEntry(2000, "ERROR", "bob", "Connection failed"),
        LogEntry(3000, "INFO", "alice", "Data saved"),
        LogEntry(4000, "WARN", "charlie", "Slow query"),
        LogEntry(5000, "ERROR", "alice", "Timeout"),
        LogEntry(6000, "INFO", "bob", "Request completed")
    )

    // 1. Count errors
    val errorCount = logs.count { it.level == "ERROR" }
    println("Error count: $errorCount")  // 2

    // 2. Unique users
    val uniqueUsers = logs.map { it.user }.toSet()
    println("Unique users: $uniqueUsers")  // [alice, bob, charlie]

    // 3. Group by log level
    val byLevel = logs.groupBy { it.level }
    println("\nLogs by level:")
    byLevel.forEach { (level, entries) ->
        println("  $level: ${entries.size}")
    }
    // INFO: 3
    // ERROR: 2
    // WARN: 1

    // 4. Most recent error
    val recentError = logs
        .filter { it.level == "ERROR" }
        .maxByOrNull { it.timestamp }

    println("\nMost recent error:")
    println("  User: ${recentError?.user}")
    println("  Message: ${recentError?.message}")
    // User: alice
    // Message: Timeout

    // Bonus: Activity by user
    val activityByUser = logs
        .groupBy { it.user }
        .mapValues { (_, entries) -> entries.size }
        .toList()
        .sortedByDescending { it.second }

    println("\nActivity by user:")
    activityByUser.forEach { (user, count) ->
        println("  $user: $count actions")
    }
    // alice: 3 actions
    // bob: 2 actions
    // charlie: 1 actions
}
```

**Explanation**:
- `count` with predicate for conditional counting
- `map` + `toSet` for unique values
- `groupBy` organizes by key
- `filter` + `maxByOrNull` finds specific maximum
- Chaining operations creates powerful pipelines

---

## Exercise 3: Sequence Performance

**Goal**: Compare list vs sequence performance.

**Task**: Process large dataset and measure time difference.

```kotlin
fun main() {
    val largeList = (1..1_000_000).toList()

    // TODO: Compare list vs sequence for:
    // - Map to double
    // - Filter > 1000
    // - Take first 100
    // - Sum
}
```

---

## Solution 3: Sequence Performance

```kotlin
fun measureTime(label: String, block: () -> Any): Any {
    val start = System.currentTimeMillis()
    val result = block()
    val elapsed = System.currentTimeMillis() - start
    println("$label: ${elapsed}ms")
    return result
}

fun main() {
    val largeList = (1..1_000_000).toList()

    // Using List (eager evaluation)
    val listResult = measureTime("List processing") {
        largeList
            .map { it * 2 }        // Processes all 1M
            .filter { it > 1000 }  // Processes all results
            .take(100)             // Finally takes 100
            .sum()
    }
    println("Result: $listResult\n")

    // Using Sequence (lazy evaluation)
    val sequenceResult = measureTime("Sequence processing") {
        largeList.asSequence()
            .map { it * 2 }        // Lazy
            .filter { it > 1000 }  // Lazy
            .take(100)             // Lazy
            .sum()                 // Triggers evaluation
    }
    println("Result: $sequenceResult\n")

    // Demonstrate step-by-step processing
    println("=== Sequence Element-by-Element ===")
    (1..5).asSequence()
        .map {
            println("  Map: $it -> ${it * 2}")
            it * 2
        }
        .filter {
            println("  Filter: $it > 4? ${it > 4}")
            it > 4
        }
        .take(2)
        .forEach { println("  Result: $it") }

    // Typical output:
    // List processing: 180ms
    // Result: 130100
    //
    // Sequence processing: 0ms
    // Result: 130100
    //
    // === Sequence Element-by-Element ===
    //   Map: 1 -> 2
    //   Filter: 2 > 4? false
    //   Map: 2 -> 4
    //   Filter: 4 > 4? false
    //   Map: 3 -> 6
    //   Filter: 6 > 4? true
    //   Result: 6
    //   Map: 4 -> 8
    //   Filter: 8 > 4? true
    //   Result: 8

    // Explanation
    println("\n=== Why Sequence is Faster ===")
    println("List: Processes all 1M elements through each operation")
    println("Sequence: Processes elements one-by-one, stops after finding 100")
    println("For this example, sequence processes ~501 elements vs 1M")
}
```

**Explanation**:
- Lists create intermediate collections at each step
- Sequences process elements one at a time
- With `take(100)`, sequence stops after 100 matches
- Sequences excel when you don't need all results
- The performance difference grows with data size

---

## Checkpoint Quiz

### Question 1
What's the difference between `map` and `flatMap`?

A) They do the same thing
B) `map` transforms each element; `flatMap` transforms and flattens nested structures
C) `flatMap` is faster than `map`
D) `map` only works with numbers

### Question 2
What does `filter` return?

A) A single element
B) A Boolean
C) A new collection with only elements matching the predicate
D) The count of matching elements

### Question 3
What's the difference between `reduce` and `fold`?

A) No difference
B) `fold` requires an initial value; `reduce` uses the first element as initial value
C) `reduce` is deprecated
D) `fold` only works with numbers

### Question 4
When should you use sequences instead of regular collections?

A) Always
B) Never
C) For large collections with multiple operations, especially when you don't need all results
D) Only for strings

### Question 5
What does `partition` do?

A) Splits a collection into N equal parts
B) Splits a collection into two groups based on a predicate
C) Removes duplicate elements
D) Sorts the collection

---

## Quiz Answers

**Question 1: B) `map` transforms each element; `flatMap` transforms and flattens nested structures**

```kotlin
val orders = listOf(
    Order(1, listOf("A", "B")),
    Order(2, listOf("C"))
)

// map: List<Order> -> List<List<String>>
val nested = orders.map { it.items }  // [[A, B], [C]]

// flatMap: List<Order> -> List<String>
val flat = orders.flatMap { it.items }  // [A, B, C]
```

`flatMap` = `map` + `flatten`

---

**Question 2: C) A new collection with only elements matching the predicate**

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

val evens = numbers.filter { it % 2 == 0 }
println(evens)  // [2, 4]
```

`filter` returns a new list; the original is unchanged (immutability).

---

**Question 3: B) `fold` requires an initial value; `reduce` uses the first element as initial value**

```kotlin
val numbers = listOf(1, 2, 3, 4)

// reduce: starts with first element (1)
val sum1 = numbers.reduce { acc, n -> acc + n }  // 10

// fold: starts with provided value (0)
val sum2 = numbers.fold(0) { acc, n -> acc + n }  // 10

// fold with different initial value
val sum3 = numbers.fold(100) { acc, n -> acc + n }  // 110

// reduce throws on empty list; fold doesn't
val empty = emptyList<Int>()
// empty.reduce { acc, n -> acc + n }  // Exception!
val safe = empty.fold(0) { acc, n -> acc + n }  // 0
```

`fold` is safer and more flexible.

---

**Question 4: C) For large collections with multiple operations, especially when you don't need all results**

```kotlin
// Good for sequence: large data, multiple ops, partial results
(1..10_000_000).asSequence()
    .map { it * 2 }
    .filter { it > 1000 }
    .take(10)  // Only need 10!
    .toList()

// Bad for sequence: small data, single op
listOf(1, 2, 3)
    .map { it * 2 }  // Just use regular list
```

Sequences have overhead; only beneficial for specific scenarios.

---

**Question 5: B) Splits a collection into two groups based on a predicate**

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6)

val (evens, odds) = numbers.partition { it % 2 == 0 }
println(evens)  // [2, 4, 6]
println(odds)   // [1, 3, 5]
```

Returns a `Pair` of lists: (matching, not-matching).

---

## What You've Learned

✅ Essential operations: map, filter, reduce, fold
✅ Finding elements: find, first, last, any, all, none
✅ Grouping and partitioning: groupBy, partition, associate
✅ Flattening nested structures: flatMap, flatten
✅ Sequences for lazy evaluation and performance
✅ Chaining operations into powerful pipelines
✅ When to use each operation
✅ Performance considerations

---

## Next Steps

In **Lesson 3.4: Scope Functions**, you'll master:
- let, run, with, apply, also
- When to use each scope function
- `this` vs `it` context
- Return value differences
- Chaining scope functions

Get ready for Kotlin's most elegant features!

---

## Key Takeaways

**Collection Operations Transform Code**:
- Replace loops with declarative operations
- Chain operations for readability
- Immutable transformations prevent bugs

**Choose the Right Tool**:
- `map`: Transform each element
- `filter`: Select elements
- `reduce/fold`: Combine into single value
- `flatMap`: Transform and flatten
- `groupBy`: Organize by key

**Performance Matters**:
- Regular collections: Small data, simple operations
- Sequences: Large data, multiple operations, partial results
- Measure when performance is critical

---

**Congratulations on completing Lesson 3.3!** 🎉

You now wield the power of functional collection operations. This knowledge will make your data processing code elegant and efficient. Practice chaining operations—it becomes second nature quickly!

# Lesson 3.5: Function Composition and Currying

**Estimated Time**: 60 minutes
**Difficulty**: Advanced
**Prerequisites**: Lessons 3.1-3.4 (Functional programming fundamentals)

---

## Topic Introduction

You've learned functional programming basics, lambdas, collections, and scope functions. Now it's time to explore advanced functional techniques that enable powerful abstractions.

Function composition and currying are techniques that let you build complex functionality from simple building blocks. They're the foundation of elegant, reusable code.

In this lesson, you'll learn:
- Function composition (combining functions)
- Currying and partial application
- Extension functions as functional tools
- Infix functions for readable code
- Operator overloading
- Building domain-specific languages (DSLs)

By the end, you'll create expressive, composable APIs!

---

## The Concept: Building with Functions

### The LEGO Analogy

Imagine building with LEGO:
- **Small pieces**: Individual functions (single responsibility)
- **Combining pieces**: Function composition (build complex structures)
- **Specialized tools**: Extension functions, operators

```kotlin
// Individual functions (LEGO pieces)
fun trim(s: String) = s.trim()
fun uppercase(s: String) = s.uppercase()
fun addExclamation(s: String) = "$s!"

// Composition (building something bigger)
fun enthusiasticProcess(s: String) = addExclamation(uppercase(trim(s)))

val result = enthusiasticProcess("  hello  ")
println(result)  // HELLO!
```

**Better with composition**:

```kotlin
val process = ::trim then ::uppercase then ::addExclamation
val result = process("  hello  ")
println(result)  // HELLO!
```

---

## Function Composition

Combining functions to create new functions.

### Mathematical Foundation

In math: `(f ∘ g)(x) = f(g(x))`

```kotlin
// g(x) then f(result)
fun compose(f: (Int) -> Int, g: (Int) -> Int): (Int) -> Int {
    return { x -> f(g(x)) }
}

val double = { x: Int -> x * 2 }
val increment = { x: Int -> x + 1 }

// Compose: first increment, then double
val incrementThenDouble = compose(double, increment)

println(incrementThenDouble(5))  // (5 + 1) * 2 = 12
```

### Generic Composition

```kotlin
// Generic composition for any types
fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
    return { x -> f(g(x)) }
}

val trim: (String) -> String = { it.trim() }
val length: (String) -> Int = { it.length }

val trimAndLength = compose(length, trim)

println(trimAndLength("  hello  "))  // 5
```

### Infix Composition Operator

Make composition more readable with `infix`:

```kotlin
infix fun <A, B, C> ((B) -> C).compose(other: (A) -> B): (A) -> C {
    return { x -> this(other(x)) }
}

// Or "andThen" for more intuitive reading
infix fun <A, B, C> ((A) -> B).andThen(other: (B) -> C): (A) -> C {
    return { x -> other(this(x)) }
}

// Usage
val trim: (String) -> String = { it.trim() }
val uppercase: (String) -> String = { it.uppercase() }
val length: (String) -> Int = { it.length }

// Read as: trim, then uppercase, then get length
val process = trim andThen uppercase andThen length

println(process("  hello  "))  // 5
```

### Practical Example: Data Transformation Pipeline

```kotlin
// Individual transformations
val validateEmail: (String) -> String? = { email ->
    if (email.contains("@")) email else null
}

val normalizeEmail: (String) -> String = { email ->
    email.trim().lowercase()
}

val extractDomain: (String) -> String = { email ->
    email.substringAfter("@")
}

// Composition
infix fun <A, B, C> ((A) -> B?).thenIfNotNull(other: (B) -> C): (A) -> C? {
    return { x -> this(x)?.let(other) }
}

val processPipeline = validateEmail thenIfNotNull normalizeEmail

val email1 = processPipeline("  USER@EXAMPLE.COM  ")
println(email1)  // user@example.com

val email2 = processPipeline("invalid")
println(email2)  // null
```

---

## Currying

Transforming a function with multiple parameters into a sequence of functions, each taking a single parameter.

### Basic Currying

```kotlin
// Regular function
fun add(a: Int, b: Int): Int = a + b

// Curried version
fun curriedAdd(a: Int): (Int) -> Int {
    return { b -> a + b }
}

// Usage
val add5 = curriedAdd(5)
println(add5(3))   // 8
println(add5(10))  // 15

// Or in one line
println(curriedAdd(10)(5))  // 15
```

### Generic Currying Helper

```kotlin
fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C {
    return { a -> { b -> f(a, b) } }
}

// Usage
val add = { a: Int, b: Int -> a + b }
val curriedAdd = curry(add)

val add10 = curriedAdd(10)
println(add10(5))  // 15
```

### Three-Parameter Currying

```kotlin
fun <A, B, C, D> curry(f: (A, B, C) -> D): (A) -> (B) -> (C) -> D {
    return { a -> { b -> { c -> f(a, b, c) } } }
}

val multiply = { a: Int, b: Int, c: Int -> a * b * c }
val curriedMultiply = curry(multiply)

val multiplyBy2 = curriedMultiply(2)
val multiplyBy2And3 = multiplyBy2(3)
println(multiplyBy2And3(4))  // 24

// Or all at once
println(curriedMultiply(2)(3)(4))  // 24
```

### Practical Example: Configuration Builder

```kotlin
// Regular function with many parameters
fun sendEmail(
    to: String,
    subject: String,
    body: String,
    priority: String,
    attachments: List<String>
) {
    println("Sending email:")
    println("  To: $to")
    println("  Subject: $subject")
    println("  Body: $body")
    println("  Priority: $priority")
    println("  Attachments: $attachments")
}

// Curried version for reusability
fun emailSender(to: String) = { subject: String ->
    { body: String ->
        { priority: String ->
            { attachments: List<String> ->
                sendEmail(to, subject, body, priority, attachments)
            }
        }
    }
}

// Create specialized senders
val sendToAdmin = emailSender("admin@example.com")
val sendAlertToAdmin = sendToAdmin("ALERT")

// Use it
sendAlertToAdmin("System down")("HIGH")(emptyList())

// Or create even more specialized versions
val sendHighPriorityAlert = sendToAdmin("ALERT")("System issue")("HIGH")
sendHighPriorityAlert(listOf("log.txt"))
```

---

## Partial Application

Fixing some arguments of a function, creating a new function.

### Manual Partial Application

```kotlin
fun greet(greeting: String, name: String): String {
    return "$greeting, $name!"
}

// Partially apply the greeting
fun greetWith(greeting: String): (String) -> String {
    return { name -> greet(greeting, name) }
}

val sayHello = greetWith("Hello")
val sayGoodbye = greetWith("Goodbye")

println(sayHello("Alice"))     // Hello, Alice!
println(sayGoodbye("Bob"))     // Goodbye, Bob!
```

### Generic Partial Application Helper

```kotlin
fun <A, B, C> partial1(f: (A, B) -> C, a: A): (B) -> C {
    return { b -> f(a, b) }
}

fun <A, B, C> partial2(f: (A, B) -> C, b: B): (A) -> C {
    return { a -> f(a, b) }
}

// Usage
val multiply = { a: Int, b: Int -> a * b }

val double = partial1(multiply, 2)
println(double(5))  // 10

val multiplyBy10 = partial2(multiply, 10)
println(multiplyBy10(5))  // 50
```

### Practical Example: Database Queries

```kotlin
// Generic query function
fun query(
    database: String,
    table: String,
    columns: List<String>,
    where: String
): String {
    return "SELECT ${columns.joinToString()} FROM $database.$table WHERE $where"
}

// Partially apply database
fun queriesFor(database: String) = { table: String, columns: List<String>, where: String ->
    query(database, table, columns, where)
}

// Partially apply database and table
fun tableQueries(database: String, table: String) = { columns: List<String>, where: String ->
    query(database, table, columns, where)
}

// Usage
val prodQueries = queriesFor("production")
val userQuery = prodQueries("users", listOf("id", "name", "email"), "active = true")
println(userQuery)
// SELECT id, name, email FROM production.users WHERE active = true

val userTableQueries = tableQueries("production", "users")
val activeUsers = userTableQueries(listOf("*"), "active = true")
println(activeUsers)
// SELECT * FROM production.users WHERE active = true
```

---

## Extension Functions as Functional Tools

Extension functions enable functional-style APIs.

### Pipeline Operations

```kotlin
// Extension functions for string processing
fun String.trimAndLower() = this.trim().lowercase()
fun String.removeSpaces() = this.replace(" ", "")
fun String.addPrefix(prefix: String) = "$prefix$this"
fun String.addSuffix(suffix: String) = "$this$suffix"

// Usage: fluent pipeline
val result = "  Hello World  "
    .trimAndLower()
    .removeSpaces()
    .addPrefix("[")
    .addSuffix("]")

println(result)  // [helloworld]
```

### Collection Extensions

```kotlin
// Custom collection operations
fun <T> List<T>.second(): T? = this.getOrNull(1)
fun <T> List<T>.secondOrNull(): T? = this.getOrNull(1)

fun <T> List<T>.takeIfNotEmpty(): List<T>? =
    if (this.isNotEmpty()) this else null

fun <T> List<T>.splitAt(index: Int): Pair<List<T>, List<T>> =
    this.take(index) to this.drop(index)

// Usage
val numbers = listOf(1, 2, 3, 4, 5)

println(numbers.second())  // 2
val (left, right) = numbers.splitAt(2)
println("Left: $left, Right: $right")  // Left: [1, 2], Right: [3, 4, 5]
```

### Higher-Order Extension Functions

```kotlin
// Retry logic as extension
fun <T> (() -> T).retry(times: Int): T? {
    repeat(times) { attempt ->
        try {
            return this()
        } catch (e: Exception) {
            if (attempt == times - 1) throw e
            println("Attempt ${attempt + 1} failed, retrying...")
        }
    }
    return null
}

// Measure execution time
fun <T> (() -> T).measureTimeMillis(): Pair<T, Long> {
    val start = System.currentTimeMillis()
    val result = this()
    val elapsed = System.currentTimeMillis() - start
    return result to elapsed
}

// Usage
val (result, time) = {
    Thread.sleep(100)
    "Done"
}.measureTimeMillis()

println("Result: $result, Time: ${time}ms")
```

---

## Infix Functions

Make function calls read like natural language.

### Basic Infix

```kotlin
infix fun Int.times(str: String): String {
    return str.repeat(this)
}

println(3 times "Ha")  // HaHaHa

infix fun String.onto(list: MutableList<String>) {
    list.add(this)
}

val items = mutableListOf<String>()
"apple" onto items
"banana" onto items
println(items)  // [apple, banana]
```

### Building Readable DSLs

```kotlin
// Test assertions
infix fun <T> T.shouldBe(expected: T) {
    if (this != expected) {
        throw AssertionError("Expected $expected but got $this")
    }
}

infix fun String.shouldContain(substring: String) {
    if (!this.contains(substring)) {
        throw AssertionError("'$this' should contain '$substring'")
    }
}

// Usage (reads like English!)
val name = "Alice"
name shouldBe "Alice"
name shouldContain "ice"

val result = 2 + 2
result shouldBe 4
```

### Practical Example: Query DSL

```kotlin
data class Query(val table: String, val conditions: List<String> = emptyList())

infix fun String.from(table: String) = Query(table)

infix fun Query.where(condition: String) = this.copy(
    conditions = this.conditions + condition
)

infix fun Query.and(condition: String) = this.copy(
    conditions = this.conditions + condition
)

fun Query.build(): String {
    val whereCl= if (conditions.isNotEmpty()) {
        " WHERE ${conditions.joinToString(" AND ")}"
    } else ""
    return "SELECT $table FROM $table$whereClause"
}

// Usage: reads like SQL!
val query = "users" from "users_table" where "age > 18" and "active = true"
println(query.build())
// SELECT users FROM users_table WHERE age > 18 AND active = true
```

---

## Operator Overloading

Define how operators work with custom types.

### Arithmetic Operators

```kotlin
data class Vector(val x: Double, val y: Double) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
    operator fun minus(other: Vector) = Vector(x - other.x, y - other.y)
    operator fun times(scalar: Double) = Vector(x * scalar, y * scalar)

    fun length() = Math.sqrt(x * x + y * y)
}

val v1 = Vector(1.0, 2.0)
val v2 = Vector(3.0, 4.0)

val sum = v1 + v2
println("Sum: $sum")  // Vector(x=4.0, y=6.0)

val scaled = v1 * 2.0
println("Scaled: $scaled")  // Vector(x=2.0, y=4.0)
```

### Comparison Operators

```kotlin
data class Money(val amount: Double, val currency: String) {
    operator fun compareTo(other: Money): Int {
        require(currency == other.currency) { "Cannot compare different currencies" }
        return amount.compareTo(other.amount)
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add different currencies" }
        return Money(amount + other.amount, currency)
    }
}

val m1 = Money(100.0, "USD")
val m2 = Money(50.0, "USD")

println(m1 > m2)   // true
println(m1 + m2)   // Money(amount=150.0, currency=USD)
```

### Invoke Operator (Callable Objects)

```kotlin
class Multiplier(val factor: Int) {
    operator fun invoke(value: Int): Int = value * factor
}

val triple = Multiplier(3)
println(triple(10))  // 30
println(triple(5))   // 15

// Function-like object!
```

### Index Access Operator

```kotlin
class Grid(val width: Int, val height: Int) {
    private val data = Array(width * height) { 0 }

    operator fun get(x: Int, y: Int): Int {
        return data[y * width + x]
    }

    operator fun set(x: Int, y: Int, value: Int) {
        data[y * width + x] = value
    }
}

val grid = Grid(3, 3)
grid[1, 2] = 42
println(grid[1, 2])  // 42
```

---

## Building a Simple DSL

Combine everything to create a domain-specific language.

### HTML Builder DSL

```kotlin
@DslMarker
annotation class HtmlTagMarker

@HtmlTagMarker
abstract class Tag(val name: String) {
    val children = mutableListOf<Tag>()
    val attributes = mutableMapOf<String, String>()

    protected fun <T : Tag> initTag(tag: T, init: T.() -> Unit): T {
        tag.init()
        children.add(tag)
        return tag
    }

    fun render(): String {
        val attrs = if (attributes.isEmpty()) "" else {
            attributes.entries.joinToString(" ", " ") { "${it.key}=\"${it.value}\"" }
        }
        val content = children.joinToString("") { it.render() }
        return "<$name$attrs>$content</$name>"
    }
}

class HTML : Tag("html") {
    fun head(init: Head.() -> Unit) = initTag(Head(), init)
    fun body(init: Body.() -> Unit) = initTag(Body(), init)
}

class Head : Tag("head") {
    fun title(init: Title.() -> Unit) = initTag(Title(), init)
}

class Title : Tag("title") {
    operator fun String.unaryPlus() {
        children.add(Text(this))
    }
}

class Body : Tag("body") {
    fun h1(init: H1.() -> Unit) = initTag(H1(), init)
    fun p(init: P.() -> Unit) = initTag(P(), init)
}

class H1 : Tag("h1") {
    operator fun String.unaryPlus() {
        children.add(Text(this))
    }
}

class P : Tag("p") {
    operator fun String.unaryPlus() {
        children.add(Text(this))
    }
}

class Text(val content: String) : Tag("") {
    override fun render() = content
}

fun html(init: HTML.() -> Unit): HTML {
    val html = HTML()
    html.init()
    return html
}

// Usage: beautiful DSL!
val page = html {
    head {
        title { +"My Page" }
    }
    body {
        h1 { +"Welcome!" }
        p { +"This is a paragraph." }
        p { +"Another paragraph." }
    }
}

println(page.render())
// <html><head><title>My Page</title></head><body><h1>Welcome!</h1><p>This is a paragraph.</p><p>Another paragraph.</p></body></html>
```

---

## Exercise 1: Function Composition

**Goal**: Implement function composition operators.

**Task**: Create `andThen` and `compose` operators for functions.

```kotlin
// TODO: Implement these
infix fun <A, B, C> ((A) -> B).andThen(other: (B) -> C): (A) -> C {
    // Your code here
}

infix fun <A, B, C> ((B) -> C).compose(other: (A) -> B): (A) -> C {
    // Your code here
}

fun main() {
    val trim: (String) -> String = { it.trim() }
    val uppercase: (String) -> String = { it.uppercase() }
    val addExclamation: (String) -> String = { "$it!" }

    // TODO: Test both operators
}
```

---

## Solution 1: Function Composition

```kotlin
infix fun <A, B, C> ((A) -> B).andThen(other: (B) -> C): (A) -> C {
    return { x -> other(this(x)) }
}

infix fun <A, B, C> ((B) -> C).compose(other: (A) -> B): (A) -> C {
    return { x -> this(other(x)) }
}

fun main() {
    val trim: (String) -> String = { it.trim() }
    val uppercase: (String) -> String = { it.uppercase() }
    val addExclamation: (String) -> String = { "$it!" }

    // andThen: left to right
    val process1 = trim andThen uppercase andThen addExclamation
    println(process1("  hello  "))  // HELLO!

    // compose: right to left
    val process2 = addExclamation compose uppercase compose trim
    println(process2("  world  "))  // WORLD!

    // Practical example: data processing
    val validate: (String) -> String? = { if (it.isNotEmpty()) it else null }
    val normalize: (String) -> String = { it.trim().lowercase() }
    val hash: (String) -> Int = { it.hashCode() }

    val pipeline = normalize andThen hash
    println("Hash: ${pipeline("  HELLO  ")}")  // Hash of "hello"
}
```

**Explanation**:
- `andThen`: Read left-to-right (intuitive)
- `compose`: Mathematical notation (right-to-left)
- Both achieve the same result, different reading order

---

## Exercise 2: Currying Implementation

**Goal**: Implement a curry function for 2-parameter functions.

**Task**:

```kotlin
fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C {
    // TODO: Implement
}

fun main() {
    val add = { a: Int, b: Int -> a + b }
    val multiply = { a: Int, b: Int -> a * b }

    // TODO: Test currying
}
```

---

## Solution 2: Currying Implementation

```kotlin
fun <A, B, C> curry(f: (A, B) -> C): (A) -> (B) -> C {
    return { a -> { b -> f(a, b) } }
}

// Bonus: Uncurry
fun <A, B, C> uncurry(f: (A) -> (B) -> C): (A, B) -> C {
    return { a, b -> f(a)(b) }
}

fun main() {
    val add = { a: Int, b: Int -> a + b }
    val multiply = { a: Int, b: Int -> a * b }

    // Curry add
    val curriedAdd = curry(add)
    val add10 = curriedAdd(10)
    println(add10(5))   // 15
    println(add10(20))  // 30

    // Curry multiply
    val curriedMultiply = curry(multiply)
    val double = curriedMultiply(2)
    val triple = curriedMultiply(3)
    println(double(7))  // 14
    println(triple(7))  // 21

    // Practical: Specialized formatters
    val format = { prefix: String, value: String -> "$prefix: $value" }
    val curriedFormat = curry(format)

    val errorFormatter = curriedFormat("ERROR")
    val infoFormatter = curriedFormat("INFO")

    println(errorFormatter("Something went wrong"))  // ERROR: Something went wrong
    println(infoFormatter("Process started"))        // INFO: Process started

    // Uncurry example
    val uncurriedAdd = uncurry(curriedAdd)
    println(uncurriedAdd(5, 3))  // 8
}
```

**Explanation**:
- Currying transforms multi-parameter functions into chains
- Creates specialized versions by fixing parameters
- Useful for configuration and creating function families

---

## Exercise 3: DSL Builder

**Goal**: Create a simple DSL for building configurations.

**Task**:

```kotlin
// TODO: Implement a configuration DSL
class ServerConfig {
    var host: String = ""
    var port: Int = 0
    val routes = mutableListOf<Route>()

    fun route(path: String, init: Route.() -> Unit) {
        // TODO
    }
}

class Route(val path: String) {
    var method: String = "GET"
    var handler: String = ""
}

fun server(init: ServerConfig.() -> Unit): ServerConfig {
    // TODO
}

fun main() {
    // Should work like this:
    val config = server {
        host = "localhost"
        port = 8080
        route("/users") {
            method = "GET"
            handler = "listUsers"
        }
        route("/users") {
            method = "POST"
            handler = "createUser"
        }
    }
}
```

---

## Solution 3: DSL Builder

```kotlin
class ServerConfig {
    var host: String = ""
    var port: Int = 0
    val routes = mutableListOf<Route>()

    fun route(path: String, init: Route.() -> Unit) {
        val route = Route(path)
        route.init()
        routes.add(route)
    }

    override fun toString(): String {
        return """
            Server Configuration:
              Host: $host
              Port: $port
              Routes:
                ${routes.joinToString("\n    ") { it.toString() }}
        """.trimIndent()
    }
}

class Route(val path: String) {
    var method: String = "GET"
    var handler: String = ""

    override fun toString() = "$method $path -> $handler"
}

fun server(init: ServerConfig.() -> Unit): ServerConfig {
    val config = ServerConfig()
    config.init()
    return config
}

fun main() {
    val config = server {
        host = "localhost"
        port = 8080

        route("/users") {
            method = "GET"
            handler = "listUsers"
        }

        route("/users") {
            method = "POST"
            handler = "createUser"
        }

        route("/users/{id}") {
            method = "GET"
            handler = "getUser"
        }

        route("/users/{id}") {
            method = "PUT"
            handler = "updateUser"
        }

        route("/users/{id}") {
            method = "DELETE"
            handler = "deleteUser"
        }
    }

    println(config)
    /*
    Server Configuration:
      Host: localhost
      Port: 8080
      Routes:
        GET /users -> listUsers
        POST /users -> createUser
        GET /users/{id} -> getUser
        PUT /users/{id} -> updateUser
        DELETE /users/{id} -> deleteUser
    */
}
```

**Explanation**:
- DSL provides type-safe configuration
- Lambda with receiver (`init: ServerConfig.() -> Unit`) enables clean syntax
- Nested structures through builder pattern
- Reads almost like a configuration file!

---

## Checkpoint Quiz

### Question 1
What is function composition?

A) Writing functions inside other functions
B) Combining functions to create new functions where output of one becomes input of another
C) Making functions larger
D) Commenting functions

### Question 2
What is currying?

A) Converting a multi-parameter function into a sequence of single-parameter functions
B) Making functions run faster
C) A cooking technique
D) Error handling

### Question 3
What does the `infix` keyword do?

A) Makes functions run in the background
B) Allows calling functions without dot notation and parentheses (binary operation style)
C) Makes functions faster
D) Prevents function calls

### Question 4
What is operator overloading?

A) Using too many operators
B) Defining custom behavior for operators like +, -, *, / on custom types
C) A performance optimization
D) A deprecated feature

### Question 5
What is a DSL (Domain-Specific Language)?

A) A new programming language
B) An API designed to read like natural language for a specific domain
C) A debugging tool
D) A database query language

---

## Quiz Answers

**Question 1: B) Combining functions to create new functions where output of one becomes input of another**

```kotlin
val trim = { s: String -> s.trim() }
val uppercase = { s: String -> s.uppercase() }

// Compose: output of trim goes into uppercase
val trimAndUpper = { s: String -> uppercase(trim(s)) }

println(trimAndUpper("  hello  "))  // HELLO
```

Composition builds complex operations from simple parts.

---

**Question 2: A) Converting a multi-parameter function into a sequence of single-parameter functions**

```kotlin
// Normal function
fun add(a: Int, b: Int) = a + b

// Curried version
fun curriedAdd(a: Int) = { b: Int -> a + b }

val add5 = curriedAdd(5)
println(add5(3))  // 8
```

Currying enables partial application and function specialization.

---

**Question 3: B) Allows calling functions without dot notation and parentheses (binary operation style)**

```kotlin
infix fun Int.times(str: String) = str.repeat(this)

// Regular call
println(3.times("Ha"))

// Infix call
println(3 times "Ha")  // More readable!
```

Makes code read more naturally.

---

**Question 4: B) Defining custom behavior for operators like +, -, *, / on custom types**

```kotlin
data class Vector(val x: Int, val y: Int) {
    operator fun plus(other: Vector) = Vector(x + other.x, y + other.y)
}

val v1 = Vector(1, 2)
val v2 = Vector(3, 4)
val sum = v1 + v2  // Uses our custom plus operator
println(sum)  // Vector(x=4, y=6)
```

Enables intuitive syntax for custom types.

---

**Question 5: B) An API designed to read like natural language for a specific domain**

```kotlin
// DSL for HTML
html {
    head {
        title { +"My Page" }
    }
    body {
        h1 { +"Welcome" }
    }
}

// Reads like HTML structure!
```

DSLs make code expressive and domain-specific.

---

## What You've Learned

✅ Function composition (combining functions)
✅ Currying (transforming multi-parameter functions)
✅ Partial application (fixing some parameters)
✅ Extension functions as functional tools
✅ Infix functions for readable code
✅ Operator overloading for custom types
✅ Building domain-specific languages (DSLs)
✅ Advanced functional programming techniques

---

## Next Steps

In **Lesson 3.6: Part 3 Capstone - Data Processing Pipeline**, you'll:
- Build a complete functional programming project
- Process CSV data with functional operations
- Create reusable pipeline components
- Apply everything you've learned
- Build statistics and reporting features

Time to put it all together!

---

## Key Takeaways

**Function Composition**:
```kotlin
val process = trim andThen uppercase andThen addPrefix
```
Build complex operations from simple building blocks.

**Currying**:
```kotlin
val curriedAdd = { a: Int -> { b: Int -> a + b } }
val add10 = curriedAdd(10)
```
Create specialized functions from general ones.

**Infix & Operators**:
```kotlin
infix fun Int.times(str: String) = str.repeat(this)
3 times "Ha"  // HaHaHa
```
Make code read naturally.

**DSLs**:
```kotlin
server {
    host = "localhost"
    port = 8080
    route("/api") { ... }
}
```
Type-safe, readable configuration.

---

**Congratulations on completing Lesson 3.5!** 🎉

You've mastered advanced functional programming techniques! These concepts enable powerful abstractions and elegant APIs. Now you're ready to build real-world functional applications in the capstone project!

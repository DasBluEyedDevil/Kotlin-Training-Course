# Lesson 3.4: Scope Functions

**Estimated Time**: 65 minutes
**Difficulty**: Intermediate
**Prerequisites**: Lessons 3.1-3.3 (Functional programming, lambdas, collections)

---

## Topic Introduction

Scope functions are one of Kotlin's most distinctive features. They're small but incredibly powerful—enabling you to write cleaner, more expressive code.

At first glance, `let`, `run`, `with`, `apply`, and `also` might seem similar. But each has a specific purpose, and mastering them will make your code more idiomatic and elegant.

In this lesson, you'll learn:
- What scope functions are and why they exist
- The five scope functions: let, run, with, apply, also
- When to use each one
- The difference between `this` and `it` context
- Return value differences
- Chaining scope functions
- Real-world use cases

By the end, you'll write fluent, readable Kotlin code!

---

## The Concept: What Are Scope Functions?

Scope functions execute a block of code within the context of an object. They temporarily change the scope to work on that object.

### The Problem They Solve

**Without scope functions**:

```kotlin
val person = Person("Alice", 25)
person.name = person.name.uppercase()
person.age = person.age + 1
println(person)
val nameLength = person.name.length
```

**With scope functions**:

```kotlin
val person = Person("Alice", 25).apply {
    name = name.uppercase()
    age += 1
}
println(person)
val nameLength = person.name.length
```

Even better:

```kotlin
Person("Alice", 25)
    .apply {
        name = name.uppercase()
        age += 1
    }
    .also { println(it) }
    .name
    .length
```

**Benefits**:
- Less repetition (no `person.` everywhere)
- Clearer intent
- Chainable operations
- Scoped changes (visible what's being modified)

---

## The Five Scope Functions: Overview

| Function | Context | Return | Common Use |
|----------|---------|--------|------------|
| `let` | `it` | Lambda result | Null safety, transformations |
| `run` | `this` | Lambda result | Object configuration & compute result |
| `with` | `this` | Lambda result | Multiple operations on object |
| `apply` | `this` | Object itself | Object configuration |
| `also` | `it` | Object itself | Side effects (logging, validation) |

### Key Differences

**Context**: How you refer to the object
- `this`: Receiver (implicit, can omit)
- `it`: Parameter (explicit, must use `it`)

**Return value**:
- Lambda result: Returns what the block returns
- Object itself: Returns the original object (chainable)

---

## let: Transform or Process

`let` takes the object as `it` and returns the lambda result.

### Basic Usage

```kotlin
val name = "Alice"

val result = name.let {
    println("Name is: $it")
    it.uppercase()
}

println(result)  // ALICE
```

### Primary Use Case: Null Safety

```kotlin
var name: String? = "Alice"

// Without let
if (name != null) {
    println(name.length)
    println(name.uppercase())
}

// With let
name?.let {
    println(it.length)
    println(it.uppercase())
}

// Only executes if name is not null
```

### Transforming Nullable Values

```kotlin
val input: String? = "  Hello  "

val processed = input?.let {
    it.trim().uppercase()
} ?: "DEFAULT"

println(processed)  // HELLO

val nullInput: String? = null
val processedNull = nullInput?.let {
    it.trim().uppercase()
} ?: "DEFAULT"

println(processedNull)  // DEFAULT
```

### Chaining Transformations

```kotlin
data class Person(val name: String, val age: Int)

val person: Person? = Person("Alice", 25)

val description = person?.let { p ->
    "Name: ${p.name}"
}?.let { nameStr ->
    "$nameStr, Age: ${person?.age}"
}

println(description)
// Name: Alice, Age: 25
```

### Real-World Example: API Response Processing

```kotlin
data class ApiResponse(val data: String?, val error: String?)

fun processResponse(response: ApiResponse): String {
    return response.data?.let { data ->
        // Process successful response
        data.uppercase()
    } ?: response.error?.let { error ->
        // Handle error
        "Error: $error"
    } ?: "Unknown error"
}

val success = ApiResponse("hello", null)
println(processResponse(success))  // HELLO

val failure = ApiResponse(null, "Not found")
println(processResponse(failure))  // Error: Not found
```

---

## run: Execute and Return Result

`run` uses `this` as context and returns the lambda result.

### Basic Usage

```kotlin
val result = "Hello".run {
    // 'this' is the string
    println(this.length)
    this.uppercase()  // Return value
}

println(result)  // HELLO
```

### Object Configuration + Computation

```kotlin
data class Rectangle(var width: Int, var height: Int) {
    fun area() = width * height
}

val area = Rectangle(10, 5).run {
    // Configure
    width *= 2
    height *= 2
    // Compute and return
    area()
}

println(area)  // 200
```

### Multiple Operations, Single Result

```kotlin
val result = run {
    val a = 10
    val b = 20
    val c = 30
    a + b + c
}

println(result)  // 60
```

### Real-World Example: Complex Calculation

```kotlin
data class Order(
    val items: List<Item>,
    val discount: Double,
    val taxRate: Double
)

data class Item(val price: Double, val quantity: Int)

fun Order.calculateTotal() = run {
    val subtotal = items.sumOf { it.price * it.quantity }
    val afterDiscount = subtotal * (1 - discount)
    val withTax = afterDiscount * (1 + taxRate)
    withTax
}

val order = Order(
    items = listOf(
        Item(10.0, 2),
        Item(5.0, 3)
    ),
    discount = 0.1,
    taxRate = 0.08
)

println("Total: ${"%.2f".format(order.calculateTotal())}")
// Total: 30.02
```

---

## with: Non-Extension Version

`with` is not an extension function; you pass the object as parameter. Uses `this` context.

### Basic Usage

```kotlin
val person = Person("Alice", 25)

val description = with(person) {
    // 'this' is person
    "Name: $name, Age: $age"
}

println(description)
// Name: Alice, Age: 25
```

### Multiple Operations on Object

```kotlin
class StringBuilder {
    private val content = mutableListOf<String>()

    fun append(text: String) = content.add(text)
    fun build() = content.joinToString("")
}

val html = with(StringBuilder()) {
    append("<html>")
    append("<body>")
    append("<h1>Hello</h1>")
    append("</body>")
    append("</html>")
    build()
}

println(html)
// <html><body><h1>Hello</h1></body></html>
```

### When to Use with vs run

```kotlin
// Use 'with' when you have an object already
val person = Person("Alice", 25)
val info = with(person) {
    "$name is $age years old"
}

// Use 'run' for chaining or when creating object inline
val info2 = Person("Bob", 30).run {
    "$name is $age years old"
}
```

### Real-World Example: Configuration

```kotlin
data class DatabaseConfig(
    var host: String = "",
    var port: Int = 0,
    var username: String = "",
    var password: String = "",
    var database: String = ""
) {
    fun validate() = host.isNotEmpty() && username.isNotEmpty()
}

val config = DatabaseConfig()

val isValid = with(config) {
    host = "localhost"
    port = 5432
    username = "admin"
    password = "secret"
    database = "myapp"
    validate()
}

println("Config valid: $isValid")  // true
```

---

## apply: Configure and Return Object

`apply` uses `this` context and returns the object itself (great for chaining!).

### Basic Usage

```kotlin
data class Person(var name: String, var age: Int)

val person = Person("", 0).apply {
    name = "Alice"
    age = 25
}

println(person)  // Person(name=Alice, age=25)
```

### Object Initialization

```kotlin
class User {
    var name: String = ""
    var email: String = ""
    var age: Int = 0

    override fun toString() = "User(name=$name, email=$email, age=$age)"
}

val user = User().apply {
    name = "Alice"
    email = "alice@example.com"
    age = 25
}

println(user)
// User(name=Alice, email=alice@example.com, age=25)
```

### Builder Pattern

```kotlin
class StringBuilder {
    private val content = mutableListOf<String>()

    fun append(text: String) = apply { content.add(text) }
    fun appendLine(text: String) = apply { content.add("$text\n") }
    fun clear() = apply { content.clear() }
    fun build() = content.joinToString("")
}

val html = StringBuilder()
    .appendLine("<html>")
    .appendLine("<body>")
    .append("<h1>Hello</h1>")
    .appendLine("</body>")
    .appendLine("</html>")
    .build()

println(html)
```

### Real-World Example: Android View Configuration

```kotlin
// Simulated Android view
class TextView {
    var text: String = ""
    var textSize: Float = 14f
    var textColor: String = "black"

    override fun toString() = "TextView(text=$text, size=$textSize, color=$textColor)"
}

fun createTitleView() = TextView().apply {
    text = "Welcome!"
    textSize = 24f
    textColor = "blue"
}

val view = createTitleView()
println(view)
// TextView(text=Welcome!, size=24.0, color=blue)
```

---

## also: Side Effects, Return Object

`also` uses `it` context and returns the object itself.

### Basic Usage

```kotlin
val numbers = mutableListOf(1, 2, 3)
    .also { println("Initial list: $it") }
    .also { it.add(4) }
    .also { println("After adding: $it") }

println("Final: $numbers")
// Initial list: [1, 2, 3]
// After adding: [1, 2, 3, 4]
// Final: [1, 2, 3, 4]
```

### Debugging and Logging

```kotlin
fun processData(data: String): String {
    return data
        .trim()
        .also { println("After trim: '$it'") }
        .uppercase()
        .also { println("After uppercase: '$it'") }
        .replace(" ", "_")
        .also { println("After replace: '$it'") }
}

val result = processData("  hello world  ")
// After trim: 'hello world'
// After uppercase: 'HELLO WORLD'
// After replace: 'HELLO_WORLD'
```

### Validation with Side Effects

```kotlin
data class User(val name: String, val email: String, val age: Int)

fun validateUser(user: User): User {
    return user.also {
        require(it.name.isNotEmpty()) { "Name cannot be empty" }
        require(it.email.contains("@")) { "Invalid email" }
        require(it.age >= 18) { "Must be 18 or older" }
        println("User validated: ${it.name}")
    }
}

val user = validateUser(User("Alice", "alice@example.com", 25))
// User validated: Alice
```

### Real-World Example: File Operations

```kotlin
import java.io.File

fun processFile(path: String): List<String> {
    return File(path)
        .also { println("Reading file: ${it.absolutePath}") }
        .also { require(it.exists()) { "File not found" } }
        .readLines()
        .also { println("Read ${it.size} lines") }
        .filter { it.isNotEmpty() }
        .also { println("After filtering: ${it.size} non-empty lines") }
}
```

---

## this vs it: Context Objects

### Comparison

**`this` (receiver)**:
- Used by: `run`, `with`, `apply`
- Can be omitted (implicit)
- Feels like you "are" the object

**`it` (parameter)**:
- Used by: `let`, `also`
- Must be explicit
- Clearer distinction between outer and inner scope

### Examples

```kotlin
data class Person(var name: String)

val person = Person("Alice")

// 'this' context (apply)
person.apply {
    name = name.uppercase()  // 'this' is implicit
    // Could also write: this.name = this.name.uppercase()
}

// 'it' context (also)
person.also {
    it.name = it.name.lowercase()  // 'it' is explicit
}
```

### When to Use Which

```kotlin
// Use 'this' when configuring object
val user = User().apply {
    name = "Alice"  // Clean, no 'this.' needed
    email = "alice@example.com"
    age = 25
}

// Use 'it' when object needs clear reference
val processed = user.let {
    saveToDatabase(it)  // Clear what's being passed
    sendEmail(it)
    it
}
```

---

## Return Values: Lambda Result vs Object

### Lambda Result Functions: let, run, with

```kotlin
// let
val length = "Hello".let {
    it.length  // Returns Int
}

// run
val uppercase = "Hello".run {
    this.uppercase()  // Returns String
}

// with
val chars = with("Hello") {
    this.length  // Returns Int
}
```

### Object Functions: apply, also

```kotlin
// apply
val person = Person("Alice", 25).apply {
    age += 1
}  // Returns Person

// also
val list = mutableListOf(1, 2, 3).also {
    it.add(4)
}  // Returns MutableList
```

### Why It Matters for Chaining

```kotlin
// apply and also return object - chainable!
val person = Person("Alice", 25)
    .apply { age += 1 }
    .also { println("Created: $it") }
    .apply { name = name.uppercase() }

// let, run, with return result - chains break
val result = Person("Alice", 25)
    .run { age + 1 }  // Returns Int, can't call Person methods anymore
    // .apply { ... }  // ERROR: Int doesn't have apply with Person context
```

---

## Chaining Scope Functions

Combining scope functions creates fluent APIs.

### Example 1: Data Processing Pipeline

```kotlin
data class User(val name: String, val email: String, var validated: Boolean = false)

fun createUser(name: String, email: String): User {
    return User(name, email)
        .apply {
            // Configure object
            validated = email.contains("@") && name.isNotEmpty()
        }
        .also {
            // Side effect: log
            println("User created: ${it.name}")
        }
        .takeIf { it.validated }
        ?.also {
            // Only for valid users
            println("User validated successfully")
        } ?: throw IllegalArgumentException("Invalid user data")
}

val user = createUser("Alice", "alice@example.com")
// User created: Alice
// User validated successfully
```

### Example 2: Building Complex Objects

```kotlin
data class Report(
    var title: String = "",
    var author: String = "",
    val sections: MutableList<String> = mutableListOf(),
    var timestamp: Long = 0
)

fun generateReport(title: String, author: String): Report {
    return Report()
        .apply {
            this.title = title
            this.author = author
            timestamp = System.currentTimeMillis()
        }
        .also {
            println("Generating report: ${it.title}")
        }
        .apply {
            sections.add("Introduction")
            sections.add("Analysis")
            sections.add("Conclusion")
        }
        .also {
            println("Added ${it.sections.size} sections")
        }
}

val report = generateReport("Annual Report", "Alice")
// Generating report: Annual Report
// Added 3 sections
```

### Example 3: Conditional Processing

```kotlin
fun processOrder(orderId: Int): String {
    return fetchOrder(orderId)
        ?.let { order ->
            // Transform order
            order.apply {
                items = items.filter { it.inStock }
            }
        }
        ?.takeIf { it.items.isNotEmpty() }
        ?.also { validateOrder(it) }
        ?.run { "Order ${this.id} processed successfully" }
        ?: "Order not found or invalid"
}

data class Order(val id: Int, var items: List<Item>)
data class Item(val name: String, val inStock: Boolean)

fun fetchOrder(id: Int): Order? = Order(id, listOf(
    Item("Book", true),
    Item("Pen", false),
    Item("Notebook", true)
))

fun validateOrder(order: Order) {
    println("Validating order ${order.id}")
}
```

---

## Decision Matrix: Which Scope Function to Use?

### Flowchart

```
Need to transform/compute result?
├─ Yes → Returns lambda result
│  ├─ Have object already? → with
│  ├─ Need null safety? → let
│  └─ Creating/chaining? → run
│
└─ No → Returns object (chainable)
   ├─ Need configuration? → apply (this)
   └─ Need side effect? → also (it)
```

### Quick Reference

| Want to... | Use | Example |
|------------|-----|---------|
| Transform nullable value | `let` | `name?.let { it.uppercase() }` |
| Configure object | `apply` | `Person().apply { name = "Alice" }` |
| Log/debug without breaking chain | `also` | `.also { println(it) }` |
| Group operations, compute result | `run` / `with` | `person.run { age + 1 }` |
| Multiple calls on existing object | `with` | `with(config) { ... }` |

---

## Exercise 1: Refactor with Scope Functions

**Goal**: Refactor imperative code using scope functions.

**Task**: Rewrite this code using appropriate scope functions:

```kotlin
data class Email(
    var to: String = "",
    var subject: String = "",
    var body: String = "",
    var sent: Boolean = false
)

fun sendEmail() {
    val email = Email()
    email.to = "user@example.com"
    email.subject = "Welcome"
    email.body = "Welcome to our service!"

    println("Sending email to: ${email.to}")

    if (email.to.isNotEmpty() && email.subject.isNotEmpty()) {
        email.sent = true
        println("Email sent successfully")
    }
}
```

---

## Solution 1: Refactor with Scope Functions

```kotlin
data class Email(
    var to: String = "",
    var subject: String = "",
    var body: String = "",
    var sent: Boolean = false
)

fun sendEmailRefactored() {
    Email()
        .apply {
            // Configure email
            to = "user@example.com"
            subject = "Welcome"
            body = "Welcome to our service!"
        }
        .also {
            // Side effect: log
            println("Sending email to: ${it.to}")
        }
        .takeIf { it.to.isNotEmpty() && it.subject.isNotEmpty() }
        ?.apply {
            // Mark as sent
            sent = true
        }
        ?.also {
            // Side effect: confirm
            println("Email sent successfully")
        }
        ?: println("Email validation failed")
}

fun main() {
    sendEmailRefactored()
    // Sending email to: user@example.com
    // Email sent successfully
}
```

**Explanation**:
- `apply`: Configure the email object
- `also`: Log without breaking the chain
- `takeIf`: Conditional processing
- Chainable, readable, and expressive!

---

## Exercise 2: Null Safety with let

**Goal**: Use `let` for safe null handling.

**Task**: Process nullable user input safely:

```kotlin
fun processUserInput(input: String?): String {
    // TODO: Use let to safely process input
    // 1. Trim whitespace
    // 2. Convert to uppercase
    // 3. Return processed string or "NO INPUT" if null/empty
}

fun main() {
    println(processUserInput("  hello  "))  // Should print: HELLO
    println(processUserInput(null))         // Should print: NO INPUT
    println(processUserInput("   "))        // Should print: NO INPUT
}
```

---

## Solution 2: Null Safety with let

```kotlin
fun processUserInput(input: String?): String {
    return input
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?.let { it.uppercase() }
        ?: "NO INPUT"
}

// Alternative with more explicit let
fun processUserInputAlt(input: String?): String {
    return input?.let { rawInput ->
        rawInput.trim()
    }?.let { trimmed ->
        trimmed.takeIf { it.isNotEmpty() }
    }?.let { validated ->
        validated.uppercase()
    } ?: "NO INPUT"
}

fun main() {
    println(processUserInput("  hello  "))  // HELLO
    println(processUserInput(null))         // NO INPUT
    println(processUserInput("   "))        // NO INPUT

    println("\nAlternative version:")
    println(processUserInputAlt("  world  "))  // WORLD
    println(processUserInputAlt(null))         // NO INPUT
}
```

**Explanation**:
- `?.` safe call operator works with `let`
- `takeIf` filters out empty strings
- `let` chains transformations safely
- Elvis operator (`?:`) provides default

---

## Exercise 3: Builder Pattern with apply

**Goal**: Create a fluent builder using `apply`.

**Task**: Build an HTTP request configuration:

```kotlin
class HttpRequest {
    var url: String = ""
    var method: String = "GET"
    var headers: MutableMap<String, String> = mutableMapOf()
    var body: String? = null

    fun addHeader(key: String, value: String) {
        headers[key] = value
    }

    override fun toString(): String {
        return "HttpRequest(url=$url, method=$method, headers=$headers, body=$body)"
    }
}

fun main() {
    // TODO: Create POST request with headers using apply
}
```

---

## Solution 3: Builder Pattern with apply

```kotlin
class HttpRequest {
    var url: String = ""
    var method: String = "GET"
    var headers: MutableMap<String, String> = mutableMapOf()
    var body: String? = null

    fun addHeader(key: String, value: String) = apply {
        headers[key] = value
    }

    override fun toString(): String {
        return "HttpRequest(url=$url, method=$method, headers=$headers, body=$body)"
    }
}

fun main() {
    // Using apply for configuration
    val request = HttpRequest().apply {
        url = "https://api.example.com/users"
        method = "POST"
        body = """{"name": "Alice", "email": "alice@example.com"}"""
    }.apply {
        addHeader("Content-Type", "application/json")
        addHeader("Authorization", "Bearer token123")
    }

    println(request)
    // HttpRequest(url=https://api.example.com/users, method=POST,
    // headers={Content-Type=application/json, Authorization=Bearer token123},
    // body={"name": "Alice", "email": "alice@example.com"})

    // Alternative: chaining with fluent API
    val request2 = HttpRequest()
        .apply {
            url = "https://api.example.com/products"
            method = "PUT"
            body = """{"id": 1, "price": 99.99}"""
        }
        .addHeader("Content-Type", "application/json")
        .addHeader("Accept", "application/json")
        .also {
            println("\nCreated request: ${it.method} ${it.url}")
        }

    println(request2)
}
```

**Explanation**:
- `apply` configures the object and returns it
- Making `addHeader` return `this` with `apply` enables chaining
- `also` adds logging without breaking the chain
- Fluent, readable builder pattern

---

## Checkpoint Quiz

### Question 1
What's the main difference between `apply` and `also`?

A) They're the same
B) `apply` uses `this` context; `also` uses `it` context
C) `apply` is faster
D) `also` can't be chained

### Question 2
Which scope function should you use for null-safe transformations?

A) `apply`
B) `also`
C) `let`
D) `with`

### Question 3
What does `apply` return?

A) The lambda result
B) Unit
C) The object itself
D) A boolean

### Question 4
When should you use `with` vs `run`?

A) They're identical
B) `with` when you have an object; `run` for chaining or inline creation
C) `with` is deprecated
D) `run` only works with strings

### Question 5
What's the primary use case for `also`?

A) Configuration
B) Transformation
C) Side effects (logging, validation) without breaking the chain
D) Null safety

---

## Quiz Answers

**Question 1: B) `apply` uses `this` context; `also` uses `it` context**

```kotlin
val person = Person("Alice", 25)

// apply: 'this' context (implicit)
person.apply {
    name = name.uppercase()  // 'this' omitted
}

// also: 'it' context (explicit)
person.also {
    it.name = it.name.lowercase()
}
```

Both return the object, but context differs.

---

**Question 2: C) `let`**

```kotlin
val name: String? = "Alice"

// let with safe call
val result = name?.let {
    it.uppercase()
} ?: "NO NAME"

println(result)  // ALICE
```

`let` is perfect for nullable chains.

---

**Question 3: C) The object itself**

```kotlin
val person = Person("Alice", 25)
    .apply {
        age += 1
    }  // Returns Person

// Can chain because it returns the object
person
    .apply { name = name.uppercase() }
    .also { println(it) }
```

Returning the object enables chaining.

---

**Question 4: B) `with` when you have an object; `run` for chaining or inline creation**

```kotlin
// with: object already exists
val person = Person("Alice", 25)
val info = with(person) {
    "$name is $age"
}

// run: chaining or inline
val info2 = Person("Bob", 30).run {
    "$name is $age"
}
```

Functionally similar, but usage context differs.

---

**Question 5: C) Side effects (logging, validation) without breaking the chain**

```kotlin
val result = processData()
    .also { println("Step 1: $it") }
    .transform()
    .also { println("Step 2: $it") }
    .finalize()

// 'also' logs without changing the return value
```

Perfect for debugging and logging in chains.

---

## What You've Learned

✅ Five scope functions: let, run, with, apply, also
✅ Context differences: `this` vs `it`
✅ Return value differences: lambda result vs object
✅ When to use each scope function
✅ Chaining scope functions for fluent APIs
✅ Real-world use cases: null safety, configuration, logging
✅ Builder pattern with `apply`

---

## Next Steps

In **Lesson 3.5: Function Composition and Currying**, you'll explore:
- Composing functions to build complex operations
- Currying and partial application
- Extension functions as functional tools
- Infix functions for readable DSLs
- Operator overloading
- Building domain-specific languages (DSLs)

Get ready to take functional programming to the next level!

---

## Key Takeaways

**Scope Functions Summary**:

```kotlin
// let: nullable handling, transformation
name?.let { it.uppercase() }

// run: configure + compute result
person.run { age + 1 }

// with: multiple ops on existing object
with(config) { host = "localhost"; port = 8080 }

// apply: object configuration
Person().apply { name = "Alice"; age = 25 }

// also: side effects, logging
data.also { println(it) }
```

**Decision Tree**:
1. Need result from operation? → let, run, with
2. Need object for chaining? → apply, also
3. Null safety? → let
4. Configuration? → apply
5. Logging/side effects? → also

**Best Practices**:
- Don't overuse—sometimes simple code is clearer
- Choose based on intent, not just brevity
- Use meaningful names when using `it` isn't clear
- Chain thoughtfully—too many levels hurt readability

---

**Congratulations on completing Lesson 3.4!** 🎉

Scope functions are a hallmark of idiomatic Kotlin. Mastering them will make your code more elegant and expressive. Practice using them in your daily coding—they quickly become second nature!

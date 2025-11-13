# Lesson 3.2: Lambda Expressions and Anonymous Functions

**Estimated Time**: 65 minutes
**Difficulty**: Intermediate
**Prerequisites**: Lesson 3.1 (Introduction to Functional Programming)

---

## Topic Introduction

In the previous lesson, you learned the basics of lambda expressions. Now it's time to master them completely!

Lambda expressions are everywhere in modern Kotlin code. They power collection operations, make Android development cleaner, and enable elegant APIs. Understanding lambdas deeply will make you a more effective Kotlin developer.

In this lesson, you'll learn:
- All lambda syntax variations
- The `it` keyword and when to use it
- Trailing lambda syntax
- Anonymous functions
- Function references (::)
- Member references
- When to use each approach

By the end, you'll write idiomatic Kotlin code like a pro!

---

## Lambda Syntax Variations

Kotlin offers multiple ways to write lambdas, from verbose to ultra-concise.

### The Full Syntax Journey

Let's trace the evolution from most explicit to most concise:

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// 1. Most verbose: explicit types everywhere
val doubled1: List<Int> = numbers.map({ number: Int -> number * 2 })

// 2. Type inference: Kotlin infers parameter type
val doubled2 = numbers.map({ number -> number * 2 })

// 3. Trailing lambda: move lambda outside parentheses
val doubled3 = numbers.map() { number -> number * 2 }

// 4. Omit empty parentheses
val doubled4 = numbers.map { number -> number * 2 }

// 5. Use 'it' for single parameter
val doubled5 = numbers.map { it * 2 }

// All produce: [2, 4, 6, 8, 10]
```

### Syntax Breakdown

```kotlin
// Full anatomy
{ parameter1: Type1, parameter2: Type2 ->
    // function body
    returnValue
}

// Simplified with inference
{ parameter1, parameter2 ->
    returnValue
}

// Single parameter with 'it'
{ it.someProperty }
```

### Multi-Line Lambdas

```kotlin
val complexOperation = numbers.map { number ->
    println("Processing: $number")
    val doubled = number * 2
    val squared = doubled * doubled
    squared  // Last expression is the return value
}

println(complexOperation)  // [4, 16, 36, 64, 100]
```

**Key Rule**: The last expression in a lambda is automatically returned (no `return` keyword needed).

---

## The `it` Keyword

`it` is a shorthand for the single parameter in a lambda.

### When `it` Is Available

```kotlin
// ✅ Single parameter: can use 'it'
listOf(1, 2, 3).map { it * 2 }

// ❌ Multiple parameters: must name them
listOf(1, 2, 3).fold(0) { accumulator, number -> accumulator + number }

// ❌ No parameters: 'it' doesn't exist
repeat(3) { println("Hello") }  // No 'it' here
```

### `it` vs Named Parameters

```kotlin
val names = listOf("Alice", "Bob", "Charlie")

// Using 'it' (concise but less clear)
val lengths1 = names.map { it.length }

// Using named parameter (more readable)
val lengths2 = names.map { name -> name.length }

// Both produce: [5, 3, 7]
```

### When to Use `it`

**✅ Use `it` when**:
- The operation is simple and obvious
- The lambda is short (1-2 lines)
- Context makes the parameter clear

```kotlin
// Good: obvious what 'it' is
numbers.filter { it > 10 }
names.map { it.uppercase() }
prices.sum { it * 1.1 }  // Add 10% tax
```

**❌ Avoid `it` when**:
- The lambda is complex
- Multiple nested lambdas
- Parameter type isn't obvious

```kotlin
// Bad: unclear what 'it' refers to
users.filter { it.age > 18 && it.active && it.hasPermission("admin") }

// Better: use descriptive name
users.filter { user ->
    user.age > 18 && user.active && user.hasPermission("admin")
}

// Bad: nested 'it' conflicts
orders.map { order ->
    order.items.filter { it.price > 100 }  // Which 'it'?
}
```

### Nested Lambdas and `it`

```kotlin
data class Order(val id: Int, val items: List<Item>)
data class Item(val name: String, val price: Double)

val orders = listOf(
    Order(1, listOf(Item("Book", 15.0), Item("Laptop", 1200.0))),
    Order(2, listOf(Item("Phone", 800.0), Item("Case", 25.0)))
)

// ❌ Confusing: nested 'it'
val expensive = orders.map {
    it.items.filter { it.price > 100 }  // Both 'it'?!
}

// ✅ Clear: name parameters
val expensiveItems = orders.map { order ->
    order.items.filter { item -> item.price > 100 }
}

println(expensiveItems)
// [[Item(name=Laptop, price=1200.0)], [Item(name=Phone, price=800.0)]]
```

---

## Trailing Lambda Syntax

One of Kotlin's most elegant features!

### The Rule

**If a lambda is the last parameter, move it outside the parentheses.**

```kotlin
// Standard syntax
repeat(3, { println("Hello") })

// Trailing lambda syntax
repeat(3) { println("Hello") }

// If lambda is ONLY parameter, drop parentheses entirely
val numbers = listOf(1, 2, 3)
numbers.forEach({ println(it) })  // Verbose
numbers.forEach() { println(it) }  // Trailing
numbers.forEach { println(it) }    // Most concise
```

### Real-World Examples

```kotlin
// File operations
File("data.txt").readLines()
    .filter { it.isNotEmpty() }
    .map { it.trim() }
    .forEach { println(it) }

// UI event handlers (Android)
button.setOnClickListener { view ->
    println("Button clicked!")
}

// Database queries (Room/Exposed)
database.transaction {
    Users.insert {
        it[name] = "Alice"
        it[email] = "alice@example.com"
    }
}
```

### Multiple Parameters with Trailing Lambda

```kotlin
// Function with multiple parameters, lambda is last
fun processData(
    prefix: String,
    suffix: String,
    transform: (String) -> String
): String {
    return prefix + transform("data") + suffix
}

// Usage with trailing lambda
val result = processData("[", "]") { it.uppercase() }
println(result)  // [DATA]

// Without trailing lambda (less readable)
val result2 = processData("[", "]", { it.uppercase() })
```

---

## Anonymous Functions

An alternative to lambda expressions with different semantics.

### Anonymous Function Syntax

```kotlin
// Lambda
val lambda = { x: Int -> x * 2 }

// Anonymous function
val anonymousFunc = fun(x: Int): Int {
    return x * 2
}

// Both work the same
println(lambda(5))          // 10
println(anonymousFunc(5))   // 10
```

### Difference: Return Behavior

**The key difference**: `return` in lambdas vs anonymous functions.

```kotlin
fun demonstrateLambdaReturn() {
    val numbers = listOf(1, 2, 3, 4, 5)

    // Lambda: 'return' exits the outer function
    numbers.forEach {
        if (it == 3) return  // Returns from demonstrateLambdaReturn()
        println(it)
    }
    println("After forEach")  // This never executes!
}

demonstrateLambdaReturn()
// Output:
// 1
// 2
// (function exits here)

fun demonstrateAnonymousFunctionReturn() {
    val numbers = listOf(1, 2, 3, 4, 5)

    // Anonymous function: 'return' exits only the function
    numbers.forEach(fun(number: Int) {
        if (number == 3) return  // Returns only from this anonymous function
        println(number)
    })
    println("After forEach")  // This DOES execute!
}

demonstrateAnonymousFunctionReturn()
// Output:
// 1
// 2
// 4
// 5
// After forEach
```

### Labeled Returns in Lambdas

Alternative to anonymous functions:

```kotlin
fun demonstrateLabeledReturn() {
    val numbers = listOf(1, 2, 3, 4, 5)

    numbers.forEach { number ->
        if (number == 3) return@forEach  // Return from lambda only
        println(number)
    }
    println("After forEach")  // This executes!
}

demonstrateLabeledReturn()
// Output:
// 1
// 2
// 4
// 5
// After forEach
```

### When to Use Anonymous Functions

**Use anonymous functions when**:
- You need explicit return statements
- You want different return behavior
- The function body is complex with multiple returns

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// Complex validation with multiple returns
val isValid = numbers.any(fun(number: Int): Boolean {
    if (number < 0) return false
    if (number > 100) return false
    if (number % 2 != 0) return false
    return true
})
```

**Use lambdas when**:
- Simple, single-expression operations
- Following common Kotlin idioms
- Working with collection operations

---

## Function References

Referring to existing functions instead of creating new lambdas.

### Function Reference Syntax

Use `::` to reference a function:

```kotlin
fun double(x: Int): Int = x * 2

fun triple(x: Int): Int = x * 3

val numbers = listOf(1, 2, 3, 4, 5)

// Lambda
val doubled1 = numbers.map { x -> double(x) }

// Function reference (cleaner!)
val doubled2 = numbers.map(::double)

println(doubled2)  // [2, 4, 6, 8, 10]
```

### Top-Level Function References

```kotlin
fun isEven(n: Int): Boolean = n % 2 == 0

fun isPrime(n: Int): Boolean {
    if (n < 2) return false
    for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
        if (n % i == 0) return false
    }
    return true
}

val numbers = (1..20).toList()

val evens = numbers.filter(::isEven)
println("Evens: $evens")  // [2, 4, 6, 8, 10, 12, 14, 16, 18, 20]

val primes = numbers.filter(::isPrime)
println("Primes: $primes")  // [2, 3, 5, 7, 11, 13, 17, 19]
```

### Built-In Function References

```kotlin
val strings = listOf("  hello  ", "  world  ", "  kotlin  ")

// Method reference
val trimmed = strings.map(String::trim)
println(trimmed)  // [hello, world, kotlin]

// Property reference
val lengths = strings.map(String::length)
println(lengths)  // [9, 9, 10]
```

---

## Member References

References to class members (properties and methods).

### Instance Method References

```kotlin
data class Person(val name: String, val age: Int) {
    fun greet(): String = "Hi, I'm $name"

    fun isAdult(): Boolean = age >= 18
}

val people = listOf(
    Person("Alice", 25),
    Person("Bob", 17),
    Person("Charlie", 30)
)

// Method reference
val greetings = people.map(Person::greet)
println(greetings)  // [Hi, I'm Alice, Hi, I'm Bob, Hi, I'm Charlie]

val adults = people.filter(Person::isAdult)
println(adults)  // [Person(name=Alice, age=25), Person(name=Charlie, age=30)]
```

### Property References

```kotlin
data class Product(val name: String, val price: Double, val stock: Int)

val products = listOf(
    Product("Laptop", 1200.0, 5),
    Product("Mouse", 25.0, 50),
    Product("Keyboard", 75.0, 30)
)

// Property reference
val names = products.map(Product::name)
println(names)  // [Laptop, Mouse, Keyboard]

val prices = products.map(Product::price)
println(prices)  // [1200.0, 25.0, 75.0]

// Sort by property
val sortedByPrice = products.sortedBy(Product::price)
println(sortedByPrice)
// [Product(name=Mouse, price=25.0, stock=50), ...]
```

### Constructor References

```kotlin
data class User(val name: String, val email: String)

// Constructor reference
val names = listOf("Alice", "Bob", "Charlie")
val emails = listOf("alice@example.com", "bob@example.com", "charlie@example.com")

val users = names.zip(emails).map { (name, email) ->
    User(name, email)
}

// Or with constructor reference and destructuring
val usersData = listOf(
    listOf("Alice", "alice@example.com"),
    listOf("Bob", "bob@example.com")
)

// Can't use ::User directly with list, need to unpack
val users2 = usersData.map { User(it[0], it[1]) }

println(users2)
// [User(name=Alice, email=alice@example.com), User(name=Bob, email=bob@example.com)]
```

### Extension Function References

```kotlin
fun String.addExclamation(): String = "$this!"

fun Int.isEven(): Boolean = this % 2 == 0

val words = listOf("hello", "world", "kotlin")
val excited = words.map(String::addExclamation)
println(excited)  // [hello!, world!, kotlin!]

val numbers = listOf(1, 2, 3, 4, 5, 6)
val evens = numbers.filter(Int::isEven)
println(evens)  // [2, 4, 6]
```

---

## Choosing the Right Approach

When should you use each style?

### Decision Matrix

| Scenario | Best Choice | Example |
|----------|-------------|---------|
| Simple operation on single parameter | Lambda with `it` | `numbers.map { it * 2 }` |
| Complex operation or nested lambdas | Lambda with named parameter | `orders.map { order -> order.calculate() }` |
| Existing function matches signature | Function reference | `numbers.filter(::isEven)` |
| Need explicit returns | Anonymous function | `fun(x) { if(x < 0) return false; return true }` |
| Calling method on each element | Member reference | `people.map(Person::name)` |

### Examples of Each

```kotlin
// Lambda with 'it': simple operations
val doubled = numbers.map { it * 2 }
val filtered = numbers.filter { it > 10 }

// Lambda with named parameter: complex or nested
val processed = orders.map { order ->
    order.items.filter { item -> item.price > 100 }
}

// Function reference: existing function
fun isValid(s: String) = s.isNotEmpty() && s.length > 3
val valid = strings.filter(::isValid)

// Member reference: calling methods/properties
val names = people.map(Person::name)
val adults = people.filter(Person::isAdult)

// Anonymous function: explicit returns
val result = numbers.firstOrNull(fun(n): Boolean {
    if (n < 0) return false
    return n % 2 == 0
})
```

---

## Practical Examples

### Example 1: Data Processing Pipeline

```kotlin
data class Transaction(
    val id: Int,
    val amount: Double,
    val category: String,
    val description: String
)

fun processTransactions(transactions: List<Transaction>) {
    // Using various lambda styles
    transactions
        .filter { it.amount > 100 }  // Simple: use 'it'
        .filter(Transaction::isExpense)  // Member reference
        .map { transaction ->  // Named: complex operation
            transaction.copy(
                description = transaction.description.uppercase(),
                amount = transaction.amount * 1.1
            )
        }
        .sortedByDescending(Transaction::amount)  // Property reference
        .take(10)
        .forEach { println(it) }  // Simple: use 'it'
}

fun Transaction.isExpense() = category in listOf("food", "transport", "utilities")

// Usage
val transactions = listOf(
    Transaction(1, 150.0, "food", "Grocery shopping"),
    Transaction(2, 50.0, "entertainment", "Movie tickets"),
    Transaction(3, 200.0, "utilities", "Electric bill")
)

processTransactions(transactions)
```

### Example 2: Validation Framework

```kotlin
typealias Validator<T> = (T) -> ValidationResult

data class ValidationResult(val isValid: Boolean, val error: String? = null)

class FormValidator<T> {
    private val validators = mutableListOf<Validator<T>>()

    fun addRule(validator: Validator<T>) {
        validators.add(validator)
    }

    fun validate(value: T): List<String> {
        return validators
            .map { it(value) }  // Apply each validator
            .filter { !it.isValid }
            .mapNotNull { it.error }
    }
}

// Usage with different lambda styles
val emailValidator = FormValidator<String>().apply {
    // Lambda with descriptive name
    addRule { email ->
        if (email.contains("@"))
            ValidationResult(true)
        else
            ValidationResult(false, "Email must contain @")
    }

    // Lambda with 'it'
    addRule {
        if (it.length >= 5)
            ValidationResult(true)
        else
            ValidationResult(false, "Email too short")
    }

    // Function reference
    addRule(::validateEmailDomain)
}

fun validateEmailDomain(email: String): ValidationResult {
    val validDomains = listOf("gmail.com", "yahoo.com", "example.com")
    val domain = email.substringAfter("@")
    return if (domain in validDomains)
        ValidationResult(true)
    else
        ValidationResult(false, "Domain not allowed")
}

val errors = emailValidator.validate("test@unknown.com")
println("Validation errors: $errors")
// Output: Validation errors: [Domain not allowed]
```

### Example 3: Event System

```kotlin
class EventBus {
    private val handlers = mutableMapOf<String, MutableList<(Any) -> Unit>>()

    fun on(event: String, handler: (Any) -> Unit) {
        handlers.getOrPut(event) { mutableListOf() }.add(handler)
    }

    fun emit(event: String, data: Any) {
        handlers[event]?.forEach { it(data) }
    }
}

// Usage
val bus = EventBus()

// Lambda with named parameter
bus.on("user_login") { data ->
    val user = data as String
    println("User logged in: $user")
}

// Lambda with 'it'
bus.on("user_logout") {
    println("User logged out: $it")
}

// Function reference
fun handleError(error: Any) {
    println("Error occurred: $error")
}
bus.on("error", ::handleError)

// Emit events
bus.emit("user_login", "Alice")
bus.emit("user_logout", "Bob")
bus.emit("error", "Connection failed")
```

---

## Exercise 1: Lambda Style Converter

**Goal**: Convert between different lambda styles.

**Task**: Rewrite the following code using:
1. Function references where possible
2. Member references where possible
3. Simplified lambda syntax

```kotlin
data class Book(val title: String, val author: String, val pages: Int, val rating: Double)

fun isHighlyRated(book: Book): Boolean = book.rating >= 4.0

fun main() {
    val books = listOf(
        Book("1984", "George Orwell", 328, 4.5),
        Book("Brave New World", "Aldous Huxley", 268, 4.2),
        Book("The Hobbit", "J.R.R. Tolkien", 310, 4.7)
    )

    // TODO: Rewrite with better lambda styles
    val titles = books.map({ book -> book.title })
    val longBooks = books.filter({ book -> book.pages > 300 })
    val highlyRated = books.filter({ book -> isHighlyRated(book) })
    val authors = books.map({ book -> book.author })
}
```

---

## Solution 1: Lambda Style Converter

```kotlin
data class Book(val title: String, val author: String, val pages: Int, val rating: Double)

fun isHighlyRated(book: Book): Boolean = book.rating >= 4.0

fun main() {
    val books = listOf(
        Book("1984", "George Orwell", 328, 4.5),
        Book("Brave New World", "Aldous Huxley", 268, 4.2),
        Book("The Hobbit", "J.R.R. Tolkien", 310, 4.7)
    )

    // Original: books.map({ book -> book.title })
    // Improved: Property reference
    val titles = books.map(Book::title)
    println("Titles: $titles")
    // [1984, Brave New World, The Hobbit]

    // Original: books.filter({ book -> book.pages > 300 })
    // Improved: Lambda with 'it'
    val longBooks = books.filter { it.pages > 300 }
    println("Long books: ${longBooks.map { it.title }}")
    // [1984, The Hobbit]

    // Original: books.filter({ book -> isHighlyRated(book) })
    // Improved: Function reference
    val highlyRated = books.filter(::isHighlyRated)
    println("Highly rated: ${highlyRated.map { it.title }}")
    // [1984, Brave New World, The Hobbit]

    // Original: books.map({ book -> book.author })
    // Improved: Property reference
    val authors = books.map(Book::author)
    println("Authors: $authors")
    // [George Orwell, Aldous Huxley, J.R.R. Tolkien]
}
```

**Explanation**:
- Property references (`Book::title`) are cleanest for simple property access
- Function references (`::isHighlyRated`) work when calling existing functions
- Lambda with `it` is fine for simple operations like `it.pages > 300`

---

## Exercise 2: Nested Lambda Clarity

**Goal**: Improve nested lambda readability by using named parameters.

**Task**: Rewrite with clear, named parameters:

```kotlin
data class Order(val id: Int, val items: List<Item>)
data class Item(val name: String, val price: Double, val quantity: Int)

fun main() {
    val orders = listOf(
        Order(1, listOf(
            Item("Laptop", 1200.0, 1),
            Item("Mouse", 25.0, 2)
        )),
        Order(2, listOf(
            Item("Monitor", 300.0, 1),
            Item("Keyboard", 75.0, 1)
        ))
    )

    // TODO: Make this more readable
    val result = orders.map {
        it.items.filter { it.price > 50 }.map { it.name }
    }

    println(result)
}
```

---

## Solution 2: Nested Lambda Clarity

```kotlin
data class Order(val id: Int, val items: List<Item>)
data class Item(val name: String, val price: Double, val quantity: Int)

fun main() {
    val orders = listOf(
        Order(1, listOf(
            Item("Laptop", 1200.0, 1),
            Item("Mouse", 25.0, 2)
        )),
        Order(2, listOf(
            Item("Monitor", 300.0, 1),
            Item("Keyboard", 75.0, 1)
        ))
    )

    // Original (confusing):
    // val result = orders.map { it.items.filter { it.price > 50 }.map { it.name } }

    // Improved: Named parameters for clarity
    val expensiveItemNames = orders.map { order ->
        order.items
            .filter { item -> item.price > 50 }
            .map { item -> item.name }
    }

    println("Expensive items per order: $expensiveItemNames")
    // [[Laptop], [Monitor, Keyboard]]

    // Alternative: Extract helper function
    fun Order.getExpensiveItemNames(): List<String> {
        return items
            .filter { it.price > 50 }
            .map { it.name }
    }

    val expensiveItems2 = orders.map { it.getExpensiveItemNames() }
    println("Alternative result: $expensiveItems2")
    // [[Laptop], [Monitor, Keyboard]]

    // Or with extension and member reference
    val expensiveItems3 = orders.map(Order::getExpensiveItemNames)
    println("With member reference: $expensiveItems3")
    // [[Laptop], [Monitor, Keyboard]]
}
```

**Explanation**:
- Named parameters (`order`, `item`) eliminate confusion
- Breaking onto multiple lines improves readability
- Extracting helper functions can simplify complex chains
- Member references work great after extraction

---

## Exercise 3: Return Behavior

**Goal**: Understand the difference between lambda and anonymous function returns.

**Task**: Fix this code so it prints all numbers except 3:

```kotlin
fun printNumbersSkippingThree() {
    val numbers = listOf(1, 2, 3, 4, 5)

    numbers.forEach {
        if (it == 3) return  // Problem: this exits the entire function!
        println(it)
    }

    println("Done!")  // This never prints!
}

fun main() {
    printNumbersSkippingThree()
}
```

**Goal**: Fix it using:
1. Labeled return
2. Anonymous function

---

## Solution 3: Return Behavior

```kotlin
// Approach 1: Labeled return
fun printNumbersSkippingThreeLabeledReturn() {
    val numbers = listOf(1, 2, 3, 4, 5)

    numbers.forEach {
        if (it == 3) return@forEach  // Return from lambda only
        println(it)
    }

    println("Done!")  // This DOES print!
}

// Approach 2: Anonymous function
fun printNumbersSkippingThreeAnonymousFunction() {
    val numbers = listOf(1, 2, 3, 4, 5)

    numbers.forEach(fun(number) {
        if (number == 3) return  // Return from anonymous function only
        println(number)
    })

    println("Done!")  // This DOES print!
}

// Approach 3: Continue with different logic
fun printNumbersSkippingThreeFilter() {
    val numbers = listOf(1, 2, 3, 4, 5)

    numbers
        .filter { it != 3 }
        .forEach { println(it) }

    println("Done!")
}

fun main() {
    println("=== Labeled Return ===")
    printNumbersSkippingThreeLabeledReturn()
    // Output: 1, 2, 4, 5, Done!

    println("\n=== Anonymous Function ===")
    printNumbersSkippingThreeAnonymousFunction()
    // Output: 1, 2, 4, 5, Done!

    println("\n=== Filter Approach ===")
    printNumbersSkippingThreeFilter()
    // Output: 1, 2, 4, 5, Done!
}
```

**Explanation**:
- **Labeled return** (`return@forEach`): Returns from the lambda only
- **Anonymous function**: `return` naturally exits only that function
- **Filter approach**: Often the most idiomatic—avoid returns altogether
- Understanding return behavior prevents subtle bugs in functional code

---

## Checkpoint Quiz

### Question 1
What does the `it` keyword represent in a lambda expression?

A) The return value of the lambda
B) The single parameter when the lambda has exactly one parameter
C) The iterator in a loop
D) The lambda function itself

### Question 2
What is trailing lambda syntax?

A) A lambda that comes at the end of a file
B) Moving the lambda parameter outside parentheses when it's the last parameter
C) A lambda with multiple return statements
D) A deprecated lambda syntax

### Question 3
What's the key difference between lambda and anonymous function returns?

A) Lambdas can't use return
B) Anonymous functions are faster
C) `return` in lambda exits enclosing function; in anonymous function exits only that function
D) There is no difference

### Question 4
What does `String::length` represent?

A) A function that returns the length of "String"
B) A property reference to the length property of String
C) A way to create strings
D) An error—invalid syntax

### Question 5
When should you use named parameters instead of `it` in lambdas?

A) Always—named parameters are always better
B) Never—`it` is always clearer
C) When the lambda is complex, nested, or the parameter type isn't obvious
D) Only in anonymous functions

---

## Quiz Answers

**Question 1: B) The single parameter when the lambda has exactly one parameter**

```kotlin
// 'it' refers to the single parameter
listOf(1, 2, 3).map { it * 2 }  // 'it' is each number

// Multiple parameters: can't use 'it'
listOf(1, 2, 3).fold(0) { acc, n -> acc + n }  // Must name parameters
```

`it` is shorthand provided by Kotlin for single-parameter lambdas.

---

**Question 2: B) Moving the lambda parameter outside parentheses when it's the last parameter**

```kotlin
// Standard
repeat(3, { println("Hi") })

// Trailing lambda syntax
repeat(3) { println("Hi") }

// If lambda is only parameter, drop parentheses
listOf(1, 2, 3).forEach { println(it) }
```

This makes code more readable and is idiomatic Kotlin.

---

**Question 3: C) `return` in lambda exits enclosing function; in anonymous function exits only that function**

```kotlin
// Lambda: return exits outer function
fun example1() {
    listOf(1, 2, 3).forEach {
        if (it == 2) return  // Exits example1()
        println(it)
    }
    println("Done")  // Never executes
}

// Anonymous function: return exits only that function
fun example2() {
    listOf(1, 2, 3).forEach(fun(n) {
        if (n == 2) return  // Exits only the anonymous function
        println(n)
    })
    println("Done")  // This executes!
}
```

Understanding this prevents subtle bugs.

---

**Question 4: B) A property reference to the length property of String**

```kotlin
val strings = listOf("hi", "hello", "world")

// Using property reference
val lengths = strings.map(String::length)
// [2, 5, 5]

// Equivalent to
val lengths2 = strings.map { it.length }
```

`::` creates a reference to an existing member (property or function).

---

**Question 5: C) When the lambda is complex, nested, or the parameter type isn't obvious**

```kotlin
// ✅ Simple: 'it' is fine
numbers.filter { it > 10 }

// ❌ Complex: named parameter is clearer
users.filter { it.age > 18 && it.isActive && it.hasRole("admin") }
// Better:
users.filter { user -> user.age > 18 && user.isActive && user.hasRole("admin") }

// ❌ Nested: named parameters prevent confusion
orders.map { it.items.filter { it.price > 100 } }  // Which 'it'?
// Better:
orders.map { order -> order.items.filter { item -> item.price > 100 } }
```

Choose readability over brevity in complex scenarios.

---

## What You've Learned

✅ All lambda syntax variations (verbose to concise)
✅ The `it` keyword and when to use it
✅ Trailing lambda syntax for cleaner code
✅ Anonymous functions and return behavior
✅ Function references with `::`
✅ Member references (properties and methods)
✅ Labeled returns in lambdas
✅ How to choose the right approach for each situation

---

## Next Steps

In **Lesson 3.3: Collection Operations**, you'll master:
- Essential operations: map, filter, reduce
- Finding elements: find, first, last, any, all
- Grouping and partitioning data
- flatMap and flatten for nested structures
- Sequences for efficient lazy evaluation

Get ready to transform how you work with data!

---

## Key Takeaways

**Lambda Mastery**:
- Use `it` for simple operations
- Name parameters for clarity in complex cases
- Trailing lambda syntax is idiomatic Kotlin
- Understand return behavior to avoid bugs

**References**:
- Function references (`::functionName`) for existing functions
- Property references (`Class::property`) for property access
- Member references for methods and properties

**Best Practices**:
- Prioritize readability over brevity
- Use the simplest syntax that's still clear
- Extract complex lambdas to named functions
- Be consistent within your codebase

---

**Congratulations on completing Lesson 3.2!** 🎉

You now have deep knowledge of lambda expressions and anonymous functions. This mastery will serve you well throughout your Kotlin journey—lambdas are everywhere in modern Kotlin code!

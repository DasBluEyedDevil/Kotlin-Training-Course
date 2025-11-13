# Lesson 3.1: Introduction to Functional Programming

**Estimated Time**: 60 minutes
**Difficulty**: Intermediate
**Prerequisites**: Parts 1-2 (Kotlin fundamentals, OOP)

---

## Topic Introduction

Welcome to Part 3: Functional Programming! You've mastered Kotlin basics and object-oriented programming. Now it's time to explore a powerful programming paradigm that will transform how you write code.

Functional programming (FP) is not just about using functions—it's a different way of thinking about problems. Instead of telling the computer **what to do** step-by-step (imperative), you describe **what you want** (declarative). The result? Code that's shorter, clearer, and easier to test.

In this lesson, you'll learn:
- What functional programming really means
- First-class and higher-order functions
- Lambda expressions basics
- Function types in Kotlin
- How to pass functions as parameters

By the end, you'll write elegant, functional code that reads like English!

---

## The Concept: What Is Functional Programming?

### The Assembly Line Analogy

Imagine two approaches to making a pizza:

**Imperative Approach** (Traditional Programming):
```
1. Take dough → Put on counter
2. Take sauce → Pour on dough
3. Take cheese → Sprinkle on sauce
4. Take pepperoni → Place on cheese
5. Take pizza → Put in oven
6. Wait 15 minutes → Take pizza out
```

**Functional Approach**:
```
pizza = take(dough)
  .add(sauce)
  .add(cheese)
  .add(pepperoni)
  .bake(15)
```

The functional approach:
- Chains operations together
- Each step transforms data and passes it forward
- Reads more naturally
- Easier to understand at a glance

### Core Principles of Functional Programming

**1. Functions Are First-Class Citizens**

In FP, functions are values just like numbers or strings. You can:
- Store them in variables
- Pass them to other functions
- Return them from functions
- Create them on the fly

```kotlin
// Functions are values!
val greet = fun(name: String) = "Hello, $name!"
val result = greet("Alice")  // "Hello, Alice!"
```

**2. Higher-Order Functions**

Functions that take other functions as parameters or return functions:

```kotlin
// Takes a function as parameter
fun repeat(times: Int, action: () -> Unit) {
    for (i in 1..times) {
        action()
    }
}

repeat(3) { println("Hello!") }
// Output:
// Hello!
// Hello!
// Hello!
```

**3. Immutability**

Prefer values that don't change (immutable data):

```kotlin
// ❌ Imperative (mutating)
var total = 0
for (num in numbers) {
    total += num
}

// ✅ Functional (immutable)
val total = numbers.sum()
```

**4. Pure Functions**

Functions with no side effects—same input always gives same output:

```kotlin
// ✅ Pure function
fun add(a: Int, b: Int): Int = a + b

// ❌ Impure function (depends on external state)
var discount = 0.1
fun applyDiscount(price: Double): Double = price * (1 - discount)
```

---

## First-Class Functions

In Kotlin, functions are **first-class citizens**—they're treated like any other value.

### Assigning Functions to Variables

```kotlin
// Traditional function declaration
fun double(x: Int): Int {
    return x * 2
}

// Assigning function to variable
val doubleFunc = ::double  // Function reference

println(doubleFunc(5))  // 10
```

### Anonymous Functions

Functions without names:

```kotlin
// Anonymous function assigned to variable
val triple = fun(x: Int): Int {
    return x * 3
}

println(triple(4))  // 12
```

### Lambda Expressions (Preview)

Shorter syntax for anonymous functions:

```kotlin
// Lambda expression
val square = { x: Int -> x * x }

println(square(6))  // 36
```

### Why This Matters

```kotlin
// Store different math operations
val add = { a: Int, b: Int -> a + b }
val subtract = { a: Int, b: Int -> a - b }
val multiply = { a: Int, b: Int -> a * b }

// Use them interchangeably
fun calculate(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

println(calculate(10, 5, add))       // 15
println(calculate(10, 5, subtract))  // 5
println(calculate(10, 5, multiply))  // 50
```

---

## Higher-Order Functions

Functions that work with other functions.

### Taking Functions as Parameters

```kotlin
fun processNumber(x: Int, transformer: (Int) -> Int): Int {
    println("Processing $x...")
    return transformer(x)
}

// Use it with different transformations
val result1 = processNumber(5) { it * 2 }     // 10
val result2 = processNumber(5) { it * it }    // 25
val result3 = processNumber(5) { it + 100 }   // 105
```

### Real-World Example: Custom List Processing

```kotlin
fun customFilter(list: List<Int>, predicate: (Int) -> Boolean): List<Int> {
    val result = mutableListOf<Int>()
    for (item in list) {
        if (predicate(item)) {
            result.add(item)
        }
    }
    return result
}

val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Filter even numbers
val evens = customFilter(numbers) { it % 2 == 0 }
println(evens)  // [2, 4, 6, 8, 10]

// Filter numbers greater than 5
val bigNumbers = customFilter(numbers) { it > 5 }
println(bigNumbers)  // [6, 7, 8, 9, 10]
```

### Returning Functions

```kotlin
fun createMultiplier(factor: Int): (Int) -> Int {
    return { number -> number * factor }
}

val double = createMultiplier(2)
val triple = createMultiplier(3)
val tenfold = createMultiplier(10)

println(double(5))    // 10
println(triple(5))    // 15
println(tenfold(5))   // 50
```

---

## Lambda Expressions Basics

Lambdas are concise anonymous functions.

### Basic Lambda Syntax

```kotlin
// Full syntax
val sum = { a: Int, b: Int -> a + b }
//         { parameters -> body }

// Using the lambda
println(sum(3, 7))  // 10
```

### Lambda Structure

```
{ parameters -> body }
  ↓          ↓
  input      what to do with input
```

Examples:

```kotlin
// No parameters
val greet = { println("Hello!") }
greet()  // Hello!

// One parameter
val square = { x: Int -> x * x }
println(square(4))  // 16

// Multiple parameters
val concat = { a: String, b: String -> "$a $b" }
println(concat("Hello", "World"))  // Hello World

// Multiple statements
val complexOperation = { x: Int ->
    val doubled = x * 2
    val squared = doubled * doubled
    squared  // Last expression is returned
}
println(complexOperation(3))  // 36 (3 * 2 = 6, then 6 * 6 = 36)
```

### Type Inference

Kotlin often infers lambda parameter types:

```kotlin
// Explicit type
val numbers = listOf(1, 2, 3, 4, 5)
val doubled = numbers.map({ x: Int -> x * 2 })

// Type inferred (cleaner!)
val tripled = numbers.map({ x -> x * 3 })

// Even shorter with 'it' (single parameter)
val quadrupled = numbers.map({ it * 4 })

// Trailing lambda (move outside parentheses)
val quintupled = numbers.map { it * 5 }

println(quintupled)  // [5, 10, 15, 20, 25]
```

---

## Function Types

Every function has a type, just like variables.

### Basic Function Type Syntax

```kotlin
// Variable type: (ParameterTypes) -> ReturnType

val greet: (String) -> String = { name -> "Hello, $name!" }
//         ^^^^^^^^^^^^^^^^     function type

val add: (Int, Int) -> Int = { a, b -> a + b }
//       ^^^^^^^^^^^^^^^^^   function type

val printMessage: (String) -> Unit = { message -> println(message) }
//                ^^^^^^^^^^^^^^^^   function type (Unit = no return value)
```

### Function Type Components

```
(Int, String) -> Boolean
 ↓      ↓         ↓
 param types      return type
```

### Using Function Types in Declarations

```kotlin
// Function parameter with function type
fun applyOperation(x: Int, y: Int, operation: (Int, Int) -> Int): Int {
    return operation(x, y)
}

val result1 = applyOperation(10, 5, { a, b -> a + b })   // 15
val result2 = applyOperation(10, 5, { a, b -> a - b })   // 5
val result3 = applyOperation(10, 5, { a, b -> a * b })   // 50
```

### Nullable Function Types

```kotlin
var operation: ((Int, Int) -> Int)? = null

operation = { a, b -> a + b }

// Safe call with nullable function
val result = operation?.invoke(5, 3)  // 8

operation = null
val result2 = operation?.invoke(5, 3)  // null
```

---

## Passing Functions as Parameters

One of the most powerful FP techniques.

### Example 1: Retry Logic

```kotlin
fun <T> retry(times: Int, action: () -> T): T? {
    repeat(times) { attempt ->
        try {
            return action()
        } catch (e: Exception) {
            println("Attempt ${attempt + 1} failed: ${e.message}")
            if (attempt == times - 1) throw e
        }
    }
    return null
}

// Usage
fun unreliableNetworkCall(): String {
    if (Math.random() < 0.7) throw Exception("Network error")
    return "Success!"
}

val result = retry(3) { unreliableNetworkCall() }
```

### Example 2: Timing Function Execution

```kotlin
fun <T> measureTime(label: String, block: () -> T): T {
    val startTime = System.currentTimeMillis()
    val result = block()
    val endTime = System.currentTimeMillis()
    println("$label took ${endTime - startTime}ms")
    return result
}

// Usage
val sum = measureTime("Calculating sum") {
    (1..1_000_000).sum()
}
// Output: Calculating sum took 42ms
```

### Example 3: List Transformation

```kotlin
fun List<Int>.customMap(transform: (Int) -> Int): List<Int> {
    val result = mutableListOf<Int>()
    for (item in this) {
        result.add(transform(item))
    }
    return result
}

val numbers = listOf(1, 2, 3, 4, 5)

val doubled = numbers.customMap { it * 2 }
println(doubled)  // [2, 4, 6, 8, 10]

val squared = numbers.customMap { it * it }
println(squared)  // [1, 4, 9, 16, 25]
```

---

## Practical Examples: Real-World Use Cases

### Example 1: Form Validation

```kotlin
data class User(val name: String, val email: String, val age: Int)

typealias Validator<T> = (T) -> Boolean

fun <T> validate(value: T, validators: List<Validator<T>>): Boolean {
    return validators.all { it(value) }
}

val nameValidator: Validator<String> = { it.length >= 3 }
val emailValidator: Validator<String> = { it.contains("@") }
val ageValidator: Validator<Int> = { it >= 18 }

// Validate name
val validName = validate("John", listOf(nameValidator))
println("Name valid: $validName")  // true

// Validate email
val validEmail = validate("john@example.com", listOf(emailValidator))
println("Email valid: $validEmail")  // true

// Validate age
val validAge = validate(25, listOf(ageValidator))
println("Age valid: $validAge")  // true
```

### Example 2: Event Handling

```kotlin
class Button(val label: String) {
    private var clickHandler: (() -> Unit)? = null

    fun onClick(handler: () -> Unit) {
        clickHandler = handler
    }

    fun click() {
        println("Button '$label' clicked")
        clickHandler?.invoke()
    }
}

// Usage
val saveButton = Button("Save")
saveButton.onClick {
    println("Saving data...")
}

val cancelButton = Button("Cancel")
cancelButton.onClick {
    println("Operation cancelled")
}

saveButton.click()
// Output:
// Button 'Save' clicked
// Saving data...

cancelButton.click()
// Output:
// Button 'Cancel' clicked
// Operation cancelled
```

### Example 3: Strategy Pattern with Functions

```kotlin
class PriceCalculator {
    fun calculatePrice(
        basePrice: Double,
        quantity: Int,
        discountStrategy: (Double, Int) -> Double
    ): Double {
        return discountStrategy(basePrice, quantity)
    }
}

// Different discount strategies
val noDiscount = { price: Double, qty: Int -> price * qty }
val bulkDiscount = { price: Double, qty: Int ->
    if (qty >= 10) price * qty * 0.9 else price * qty
}
val loyaltyDiscount = { price: Double, qty: Int -> price * qty * 0.85 }

val calculator = PriceCalculator()

println(calculator.calculatePrice(100.0, 5, noDiscount))        // 500.0
println(calculator.calculatePrice(100.0, 15, bulkDiscount))     // 1350.0
println(calculator.calculatePrice(100.0, 5, loyaltyDiscount))   // 425.0
```

---

## Exercise 1: Function Calculator

**Goal**: Create a calculator that uses functions for operations.

**Requirements**:
1. Create a function `calculate` that takes two numbers and an operation function
2. Define operation functions for: add, subtract, multiply, divide
3. Use the calculator with different operations

**Starter Code**:
```kotlin
fun calculate(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    // TODO: Implement
}

fun main() {
    // TODO: Define operations and use calculator
}
```

---

## Solution 1: Function Calculator

```kotlin
fun calculate(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

fun main() {
    // Define operations as lambdas
    val add = { a: Int, b: Int -> a + b }
    val subtract = { a: Int, b: Int -> a - b }
    val multiply = { a: Int, b: Int -> a * b }
    val divide = { a: Int, b: Int -> if (b != 0) a / b else 0 }

    val x = 20
    val y = 4

    println("$x + $y = ${calculate(x, y, add)}")         // 24
    println("$x - $y = ${calculate(x, y, subtract)}")    // 16
    println("$x * $y = ${calculate(x, y, multiply)}")    // 80
    println("$x / $y = ${calculate(x, y, divide)}")      // 5

    // Can also use lambdas directly
    println("$x % $y = ${calculate(x, y) { a, b -> a % b }}")  // 0
}
```

**Explanation**:
- We define operation functions as lambda expressions
- Each lambda takes two Ints and returns an Int
- The `calculate` function is generic—it works with any operation
- We can pass pre-defined operations or create them inline

---

## Exercise 2: Custom List Filter

**Goal**: Build a reusable filter function for lists.

**Requirements**:
1. Create a function `filterList` that takes a list and a predicate function
2. The predicate determines which elements to keep
3. Test with different predicates (even numbers, > 10, etc.)

**Starter Code**:
```kotlin
fun filterList(list: List<Int>, predicate: (Int) -> Boolean): List<Int> {
    // TODO: Implement
}

fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    // TODO: Filter with different predicates
}
```

---

## Solution 2: Custom List Filter

```kotlin
fun filterList(list: List<Int>, predicate: (Int) -> Boolean): List<Int> {
    val result = mutableListOf<Int>()
    for (item in list) {
        if (predicate(item)) {
            result.add(item)
        }
    }
    return result
}

fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 15, 20, 25)

    // Filter even numbers
    val evens = filterList(numbers) { it % 2 == 0 }
    println("Even numbers: $evens")  // [2, 4, 6, 8, 10, 20]

    // Filter numbers greater than 10
    val bigNumbers = filterList(numbers) { it > 10 }
    println("Numbers > 10: $bigNumbers")  // [15, 20, 25]

    // Filter numbers divisible by 5
    val divisibleBy5 = filterList(numbers) { it % 5 == 0 }
    println("Divisible by 5: $divisibleBy5")  // [5, 10, 15, 20, 25]

    // Filter numbers in range 3..7
    val inRange = filterList(numbers) { it in 3..7 }
    println("In range 3-7: $inRange")  // [3, 4, 5, 6, 7]
}
```

**Explanation**:
- `filterList` iterates through the list
- For each item, it calls the predicate function
- If predicate returns true, item is included in result
- Different predicates give different filtered results

---

## Exercise 3: Function Builder

**Goal**: Create a function that returns different functions based on input.

**Requirements**:
1. Create `createGreeter` that takes a greeting style
2. Return appropriate greeting function
3. Styles: "formal", "casual", "enthusiastic"

**Starter Code**:
```kotlin
fun createGreeter(style: String): (String) -> String {
    // TODO: Return different greeting functions based on style
}

fun main() {
    // TODO: Test different greeting styles
}
```

---

## Solution 3: Function Builder

```kotlin
fun createGreeter(style: String): (String) -> String {
    return when (style) {
        "formal" -> { name -> "Good day, $name. How may I assist you?" }
        "casual" -> { name -> "Hey $name! What's up?" }
        "enthusiastic" -> { name -> "OH WOW! Hi $name!!! So great to see you!!!" }
        else -> { name -> "Hello, $name." }
    }
}

fun main() {
    val formalGreeter = createGreeter("formal")
    val casualGreeter = createGreeter("casual")
    val enthusiasticGreeter = createGreeter("enthusiastic")

    val person = "Alice"

    println(formalGreeter(person))
    // Output: Good day, Alice. How may I assist you?

    println(casualGreeter(person))
    // Output: Hey Alice! What's up?

    println(enthusiasticGreeter(person))
    // Output: OH WOW! Hi Alice!!! So great to see you!!!

    // Can also create and use immediately
    println(createGreeter("unknown")(person))
    // Output: Hello, Alice.
}
```

**Explanation**:
- `createGreeter` is a factory function that returns functions
- Based on style parameter, it returns different greeting implementations
- Each returned function has the same signature: `(String) -> String`
- This demonstrates functions returning functions—powerful abstraction!

---

## Checkpoint Quiz

Test your understanding of functional programming concepts!

### Question 1
What does it mean that functions are "first-class citizens" in Kotlin?

A) Functions must be declared before variables
B) Functions can be treated as values—stored in variables, passed as parameters, and returned from functions
C) Functions are more important than other code elements
D) Functions always execute first in a program

### Question 2
What is a higher-order function?

A) A function declared at the top of a file
B) A function with more parameters than usual
C) A function that takes another function as a parameter or returns a function
D) A function that runs faster than normal functions

### Question 3
What is the correct syntax for a lambda expression that doubles a number?

A) `lambda x -> x * 2`
B) `{ x -> x * 2 }`
C) `func(x) { x * 2 }`
D) `double(x) = x * 2`

### Question 4
What is the function type of: `{ a: Int, b: Int -> a + b }`?

A) `(Int) -> Int`
B) `(Int, Int) -> Unit`
C) `(Int, Int) -> Int`
D) `() -> Int`

### Question 5
What does the `it` keyword represent in a lambda?

A) The function itself
B) The single parameter when a lambda has exactly one parameter
C) The return value
D) The iteration count in a loop

---

## Quiz Answers

**Question 1: B) Functions can be treated as values—stored in variables, passed as parameters, and returned from functions**

First-class functions mean functions are treated like any other value in the language:

```kotlin
// Store in variable
val greet = { name: String -> "Hello, $name!" }

// Pass as parameter
fun execute(action: () -> Unit) = action()

// Return from function
fun getOperation() = { x: Int -> x * 2 }
```

This is fundamental to functional programming and enables powerful abstractions.

---

**Question 2: C) A function that takes another function as a parameter or returns a function**

Higher-order functions work with other functions:

```kotlin
// Takes function as parameter
fun applyTwice(x: Int, f: (Int) -> Int): Int {
    return f(f(x))
}

// Returns a function
fun createMultiplier(n: Int): (Int) -> Int {
    return { x -> x * n }
}

val result = applyTwice(5) { it * 2 }  // 20
val triple = createMultiplier(3)
```

This enables generic, reusable code patterns.

---

**Question 3: B) `{ x -> x * 2 }`**

Lambda syntax in Kotlin:

```kotlin
{ parameters -> body }

// Examples:
{ x -> x * 2 }              // One parameter
{ a, b -> a + b }           // Two parameters
{ it * 2 }                  // 'it' for single parameter
{ x: Int -> x * 2 }         // Explicit type
```

Curly braces delimit the lambda, arrow separates parameters from body.

---

**Question 4: C) `(Int, Int) -> Int`**

Function type syntax: `(ParameterTypes) -> ReturnType`

```kotlin
{ a: Int, b: Int -> a + b }
  ↓       ↓          ↓
  Int    Int        Int (return type)

Type: (Int, Int) -> Int
```

This describes a function taking two Ints and returning an Int.

---

**Question 5: B) The single parameter when a lambda has exactly one parameter**

`it` is shorthand for the single parameter:

```kotlin
// Explicit parameter
numbers.map({ x -> x * 2 })

// Using 'it'
numbers.map({ it * 2 })

// Even shorter
numbers.map { it * 2 }

// But with multiple parameters, must use names:
numbers.fold(0) { acc, n -> acc + n }  // Can't use 'it' here
```

Only works with single-parameter lambdas.

---

## What You've Learned

✅ Core principles of functional programming (first-class functions, immutability, pure functions)
✅ First-class functions—treating functions as values
✅ Higher-order functions—functions that work with other functions
✅ Lambda expression syntax and usage
✅ Function types and type signatures
✅ Passing functions as parameters
✅ Returning functions from functions
✅ Practical applications: validation, event handling, strategy pattern

---

## Next Steps

In **Lesson 3.2: Lambda Expressions and Anonymous Functions**, you'll master:
- Advanced lambda syntax variations
- The `it` keyword and trailing lambda syntax
- Anonymous functions vs lambdas
- Function references and member references
- When to use each approach

Get ready to write even more elegant functional code!

---

## Key Takeaways

**Functional Programming Benefits**:
- More concise code
- Easier to test (pure functions)
- Better composability
- Natural parallelization
- Reduced bugs from mutable state

**When to Use Functional Style**:
- ✅ Data transformations (map, filter, reduce)
- ✅ Event handling
- ✅ Configuration and customization
- ✅ Collections processing
- ❌ Performance-critical tight loops (sometimes)
- ❌ State machines with complex mutable state

**Remember**:
- Functions are values—treat them as such
- Higher-order functions enable powerful abstractions
- Lambdas make functional code concise
- Start thinking "what" instead of "how"

---

**Congratulations on completing Lesson 3.1!** 🎉

You've taken your first steps into functional programming. This paradigm will make your code more elegant and expressive. Keep practicing—functional thinking becomes natural with use!

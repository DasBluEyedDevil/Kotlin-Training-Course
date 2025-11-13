# Lesson 4.1: Lambda Expressions and Higher-Order Functions

## Functions as Values

Welcome to Part 4! Now we'll explore Kotlin's most powerful features.

**Analogy:** Think of a recipe card (function). What if you could:
- Pass a recipe card to someone else
- Store recipe cards in a box
- Create recipes on the fly

**Lambda expressions** let you treat functions like any other value!

---

## What is a Lambda?

A **lambda** is an anonymous function - a function without a name.

**Regular function:**
```kotlin
fun double(x: Int): Int {
    return x * 2
}
```

**Lambda:**
```kotlin
val double = { x: Int -> x * 2 }
```

---

## Lambda Syntax

```kotlin
val lambda = { parameters -> body }
```

**Examples:**

```kotlin
// No parameters
val greet = { println("Hello!") }

// One parameter
val square = { x: Int -> x * x }

// Multiple parameters
val add = { a: Int, b: Int -> a + b }

// With type declaration
val multiply: (Int, Int) -> Int = { a, b -> a * b }
```

---

## Using Lambdas

```kotlin
fun main() {
    val square = { x: Int -> x * x }

    println(square(5))  // 25
    println(square(10))  // 100
}
```

---

## Higher-Order Functions

Functions that take functions as parameters or return functions!

```kotlin
fun operateOnNumbers(a: Int, b: Int, operation: (Int, Int) -> Int): Int {
    return operation(a, b)
}

fun main() {
    val sum = operateOnNumbers(5, 3) { a, b -> a + b }
    val product = operateOnNumbers(5, 3) { a, b -> a * b }

    println("Sum: $sum")  // 8
    println("Product: $product")  // 15
}
```

---

## Collection Operations with Lambdas

### Map - Transform Each Element

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val doubled = numbers.map { it * 2 }
println(doubled)  // [2, 4, 6, 8, 10]
```

**`it`** is the implicit parameter name for single-parameter lambdas!

---

### Filter - Keep Only Matching Elements

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
val evens = numbers.filter { it % 2 == 0 }
println(evens)  // [2, 4, 6, 8, 10]
```

---

### ForEach - Perform Action on Each

```kotlin
val names = listOf("Alice", "Bob", "Charlie")
names.forEach { println("Hello, $it!") }
```

---

### Reduce - Combine into Single Value

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val sum = numbers.reduce { acc, num -> acc + num }
println(sum)  // 15
```

---

## Chaining Operations

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

val result = numbers
    .filter { it % 2 == 0 }  // Keep evens
    .map { it * it }  // Square them
    .filter { it > 10 }  // Keep > 10
    .sum()  // Sum them

println(result)  // 4 + 16 + 36 + 64 + 100 = 220
```

---

## Practical Examples

### Example 1: Custom Sorting

```kotlin
data class Person(val name: String, val age: Int)

fun main() {
    val people = listOf(
        Person("Alice", 30),
        Person("Bob", 25),
        Person("Charlie", 35)
    )

    val sortedByAge = people.sortedBy { it.age }
    val sortedByName = people.sortedBy { it.name }

    println(sortedByAge)
}
```

---

### Example 2: Custom Validation

```kotlin
fun validateInput(input: String, validator: (String) -> Boolean): Boolean {
    return validator(input)
}

fun main() {
    val email = "user@example.com"

    val isValidEmail = validateInput(email) { it.contains("@") && it.contains(".") }
    val isLongEnough = validateInput(email) { it.length >= 5 }

    println("Valid email: $isValidEmail")
    println("Long enough: $isLongEnough")
}
```

---

## Challenge: Build a Simple DSL

```kotlin
class Task(val name: String, var completed: Boolean = false)

class TodoList {
    private val tasks = mutableListOf<Task>()

    fun task(name: String) {
        tasks.add(Task(name))
    }

    fun complete(index: Int) {
        if (index in tasks.indices) {
            tasks[index].completed = true
        }
    }

    fun show() {
        tasks.forEachIndexed { index, task ->
            val status = if (task.completed) "✓" else "☐"
            println("$index. $status ${task.name}")
        }
    }
}

fun todoList(init: TodoList.() -> Unit): TodoList {
    val list = TodoList()
    list.init()
    return list
}

fun main() {
    val myTodos = todoList {
        task("Learn Kotlin")
        task("Build an app")
        task("Deploy to production")
    }

    myTodos.show()
    myTodos.complete(0)
    println("\nAfter completing first task:")
    myTodos.show()
}
```

---

## Recap

You now understand:

1. **Lambdas** = Anonymous functions
2. **`{ param -> body }`** = Lambda syntax
3. **`it`** = Implicit single parameter
4. **Higher-order functions** = Functions that take/return functions
5. **Collection operations** = map, filter, reduce, etc.
6. **Chaining** = Combine operations

---

## What's Next?

Next: **Extension Functions and Scope Functions** - Adding functions to existing classes!

**Key Takeaway:** Lambdas make code concise and expressive. They're everywhere in modern Kotlin!

Continue to the next lesson!

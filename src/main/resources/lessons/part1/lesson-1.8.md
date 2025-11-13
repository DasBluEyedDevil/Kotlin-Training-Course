# Lesson 1.8: Recipes with Ingredients (Functions with Parameters)

## Making Functions Flexible

In the last lesson, you learned to create functions - reusable blocks of code. But they were always the same, like a recipe that makes exactly one type of cookie.

What if you want to make chocolate chip cookies one day and sugar cookies the next? You'd need to give the recipe some **ingredients** to customize it!

**That's what parameters do for functions!**

---

## What are Parameters?

**Parameters** are inputs you give to a function to customize its behavior.

**Analogy:**
- **Function without parameters:** "Make a sandwich" (always the same sandwich)
- **Function with parameters:** "Make a sandwich with [bread type], [meat], and [cheese]" (customizable!)

---

## Creating a Function with Parameters

Let's start with a simple example:

```kotlin
fun greet(name: String) {
    println("Hello, $name!")
}

fun main() {
    greet("Alice")
    greet("Bob")
    greet("Charlie")
}
```

**Output:**
```
Hello, Alice!
Hello, Bob!
Hello, Charlie!
```

### Breaking It Down:

```kotlin
fun greet(name: String) {
    println("Hello, $name!")
}
```

- **`name: String`** = Parameter (an ingredient the function needs)
  - `name` = The parameter's name (like a variable)
  - `String` = The type of data expected (text)
- When you call `greet("Alice")`, the value `"Alice"` gets stored in `name`
- Inside the function, you can use `name` like any variable

---

## Calling Functions with Arguments

When you **call** a function and give it values, those values are called **arguments**.

```kotlin
greet("Alice")
```

- **`greet`** = Function name
- **`"Alice"`** = Argument (the actual value you're passing)
- The argument `"Alice"` becomes the parameter `name` inside the function

**Terminology:**
- **Parameter** = The placeholder when you *define* the function
- **Argument** = The actual value when you *call* the function

---

## Multiple Parameters

Functions can have multiple parameters!

```kotlin
fun introduce(name: String, age: Int) {
    println("My name is $name and I am $age years old.")
}

fun main() {
    introduce("Emma", 25)
    introduce("Liam", 30)
}
```

**Output:**
```
My name is Emma and I am 25 years old.
My name is Liam and I am 30 years old.
```

**Important:** The order matters!
- First argument (`"Emma"`) goes to first parameter (`name`)
- Second argument (`25`) goes to second parameter (`age`)

---

## Return Values: Getting Results Back

So far, our functions do things (print messages), but they don't **give anything back**. Let's change that!

**Analogy:** You give a recipe to a chef (parameters), the chef cooks it, and then **returns** the finished dish to you (return value).

### Functions That Return Values:

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}

fun main() {
    val result = add(5, 3)
    println("5 + 3 = $result")
}
```

**Output:**
```
5 + 3 = 8
```

### Breaking It Down:

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}
```

- **`a: Int, b: Int`** = Two parameters (both Ints)
- **`: Int`** (after the parentheses) = The **return type** (this function returns an Int)
- **`return a + b`** = Calculates the sum and **returns** it

When you call `add(5, 3)`, the function calculates `5 + 3 = 8` and **returns** that value. You can store it in a variable or use it directly.

---

## Return Type Explained

The return type tells you what kind of value the function will give back:

```kotlin
fun getGreeting(name: String): String {
    return "Hello, $name!"
}

fun calculateAge(birthYear: Int): Int {
    return 2024 - birthYear
}

fun isAdult(age: Int): Boolean {
    return age >= 18
}
```

- **`getGreeting()`** returns a **String**
- **`calculateAge()`** returns an **Int**
- **`isAdult()`** returns a **Boolean** (true/false)

---

## Using Return Values

Once a function returns a value, you can:

1. **Store it in a variable:**
   ```kotlin
   val sum = add(10, 20)
   ```

2. **Print it directly:**
   ```kotlin
   println(add(10, 20))
   ```

3. **Use it in calculations:**
   ```kotlin
   val total = add(5, 3) + add(2, 4)  // (5+3) + (2+4) = 14
   ```

---

## Interactive Coding Session

### Challenge 1: Personalized Greeting

Create a function `personalGreeting()` that takes two parameters:
- `name` (String)
- `timeOfDay` (String) - like "morning" or "evening"

It should print a message like: "Good morning, Alice!"

**Example:**
```kotlin
fun personalGreeting(name: String, timeOfDay: String) {
    println("Good $timeOfDay, $name!")
}

fun main() {
    personalGreeting("Alice", "morning")
    personalGreeting("Bob", "evening")
}
```

---

### Challenge 2: Area Calculator

Create a function `calculateArea()` that:
- Takes two parameters: `width` and `height` (both Int)
- Returns the area (width × height)
- Store the result in a variable and print it

**Example:**
```kotlin
fun calculateArea(width: Int, height: Int): Int {
    return width * height
}

fun main() {
    val area = calculateArea(5, 10)
    println("The area is $area")
}
```

---

### Challenge 3: Temperature Converter

Create a function `celsiusToFahrenheit()` that:
- Takes one parameter: `celsius` (Double)
- Converts it to Fahrenheit using the formula: `(celsius * 9/5) + 32`
- Returns the result as a Double

**Example:**
```kotlin
fun celsiusToFahrenheit(celsius: Double): Double {
    return (celsius * 9 / 5) + 32
}

fun main() {
    val tempC = 25.0
    val tempF = celsiusToFahrenheit(tempC)
    println("$tempC°C is $tempF°F")
}
```

---

## Default Parameters (Bonus!)

Kotlin has a cool feature: **default parameters**. You can give parameters default values!

```kotlin
fun greet(name: String = "Guest") {
    println("Hello, $name!")
}

fun main() {
    greet("Alice")  // Output: Hello, Alice!
    greet()         // Output: Hello, Guest!
}
```

If you don't provide an argument, it uses the default value!

---

## Common Mistakes

### Mistake 1: Wrong Number of Arguments

❌ **Wrong:**
```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}

fun main() {
    add(5)  // ERROR! Missing second argument
}
```

✅ **Correct:**
```kotlin
add(5, 3)  // Provide both arguments
```

---

### Mistake 2: Wrong Type of Argument

❌ **Wrong:**
```kotlin
fun greet(name: String) {
    println("Hello, $name")
}

fun main() {
    greet(123)  // ERROR! Expected String, got Int
}
```

✅ **Correct:**
```kotlin
greet("Alice")  // Correct type
```

---

### Mistake 3: Forgetting the Return Statement

❌ **Wrong:**
```kotlin
fun add(a: Int, b: Int): Int {
    a + b  // ERROR! Calculation happens, but nothing is returned
}
```

✅ **Correct:**
```kotlin
fun add(a: Int, b: Int): Int {
    return a + b  // Return the result
}
```

---

## Single-Expression Functions (Advanced Shortcut)

If a function is simple (just one expression), Kotlin has a shortcut:

**Normal way:**
```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}
```

**Shortcut:**
```kotlin
fun add(a: Int, b: Int): Int = a + b
```

Both do the same thing! The shortcut is cleaner for simple functions.

---

## Recap: What You've Learned

You now understand:

1. **Parameters** = Inputs you give to a function
2. **Arguments** = Actual values you pass when calling
3. **Return values** = The result a function gives back
4. **Return type** = Specified after the `()` with `: Type`
5. **`return`** keyword sends a value back
6. Functions can have multiple parameters
7. Default parameters are optional

---

## What's Next?

You've now mastered the fundamentals of Kotlin! In the final lesson of Part 1, you'll put **everything** together in a capstone project - building a real, interactive program from scratch!

**Key Takeaways:**
- Parameters customize function behavior
- Return values let functions give results back
- Order of arguments matters
- Match types: String to String, Int to Int
- Default parameters make arguments optional
- Use descriptive parameter names

---

Amazing progress! Mark this complete and get ready for the capstone project!

# Lesson 1.8: Functions with Parameters and Return Values

**Estimated Time**: 65 minutes

**Difficulty**: Beginner

---

## Topic Introduction

You've already learned the basics of functions—reusable blocks of code that help organize your program. But so far, your functions have been like vending machines that dispense the same item every time. What if you want to customize what you get?

In this lesson, you'll learn how to make your functions truly flexible and powerful by:
- **Passing data INTO functions** (parameters)
- **Getting data BACK from functions** (return values)
- **Creating reusable, customizable code blocks** that adapt to different situations

Think of it this way: A chef doesn't just make "a sandwich"—they take specific ingredients (parameters) and create a customized sandwich (return value) based on what you ordered. That's exactly what we're learning today!

By the end of this lesson, you'll be able to write functions that accept input, process it, and give you back exactly what you need.

---

## The Concept

### The Recipe Analogy

**Simple Functions** (what you know already):
```
Recipe: "Make Chocolate Chip Cookies"
Steps:
1. Use chocolate chips
2. Use butter
3. Mix and bake
Result: Always chocolate chip cookies
```

**Functions with Parameters** (what you're learning now):
```
Recipe: "Make Cookies with [TYPE], [SIZE], and [QUANTITY]"
Steps:
1. Use the specified [TYPE] of mix-ins
2. Make [SIZE] sized cookies
3. Bake [QUANTITY] cookies
Result: Customizable cookies!
```

**Real-World Examples**:
- **Coffee Shop**: `makeCoffee(size, type, milk)` → Takes your preferences, returns your custom coffee
- **ATM Machine**: `withdraw(accountNumber, amount)` → Takes account and amount, returns cash
- **Calculator**: `add(number1, number2)` → Takes two numbers, returns their sum

### Parameters vs Arguments

These terms are often confused, but they're different:

- **Parameter**: The placeholder variable in the function definition (like a recipe ingredient slot)
- **Argument**: The actual value you pass when calling the function (like the real ingredient)

```kotlin
fun greet(name: String) {  // 'name' is a PARAMETER
    println("Hello, $name!")
}

fun main() {
    greet("Alice")  // "Alice" is an ARGUMENT
}
```

Think of it like a form:
- **Parameter**: The blank field "Name: _______"
- **Argument**: What you write in that field "Name: Alice"

---

## Parameters: Giving Functions Input

### Single Parameter

The simplest case—one input to customize the function:

```kotlin
fun greet(name: String) {
    println("Hello, $name! Welcome to Kotlin!")
}

fun main() {
    greet("Alice")
    greet("Bob")
    greet("Charlie")
}
```

**Output**:
```
Hello, Alice! Welcome to Kotlin!
Hello, Bob! Welcome to Kotlin!
Hello, Charlie! Welcome to Kotlin!
```

**Breaking it down**:
```kotlin
fun greet(name: String) {
//        ^^^^  ^^^^^^
//        |     |
//        |     Type (must be String)
//        Name (can be used inside function like a variable)
```

---

### Multiple Parameters

Functions can accept multiple inputs:

```kotlin
fun introduce(name: String, age: Int, city: String) {
    println("My name is $name.")
    println("I am $age years old.")
    println("I live in $city.")
    println()
}

fun main() {
    introduce("Emma", 25, "New York")
    introduce("Liam", 30, "London")
    introduce("Sophia", 28, "Tokyo")
}
```

**Output**:
```
My name is Emma.
I am 25 years old.
I live in New York.

My name is Liam.
I am 30 years old.
I live in London.

My name is Sophia.
I am 28 years old.
I live in Tokyo.
```

**Important**: Order matters!
- First argument → first parameter
- Second argument → second parameter
- Third argument → third parameter

```kotlin
introduce("Emma", 25, "New York")  // ✅ Correct order
introduce(25, "Emma", "New York")  // ❌ Type error!
```

---

### Parameters with Different Types

You can mix and match any data types:

```kotlin
fun displayProduct(name: String, price: Double, inStock: Boolean, quantity: Int) {
    println("Product: $name")
    println("Price: $$price")
    println("Quantity: $quantity")
    println("Available: ${if (inStock) "Yes" else "No"}")
    println("---")
}

fun main() {
    displayProduct("Laptop", 999.99, true, 15)
    displayProduct("Mouse", 25.50, false, 0)
    displayProduct("Keyboard", 75.00, true, 8)
}
```

**Output**:
```
Product: Laptop
Price: $999.99
Quantity: 15
Available: Yes
---
Product: Mouse
Price: $25.5
Quantity: 0
Available: No
---
Product: Keyboard
Price: $75.0
Quantity: 8
Available: Yes
---
```

---

### Practical Example: Calculation Function

```kotlin
fun calculateTotalPrice(itemPrice: Double, quantity: Int, taxRate: Double) {
    val subtotal = itemPrice * quantity
    val tax = subtotal * taxRate
    val total = subtotal + tax

    println("Item Price: $$itemPrice")
    println("Quantity: $quantity")
    println("Subtotal: $${"%.2f".format(subtotal)}")
    println("Tax (${taxRate * 100}%): $${"%.2f".format(tax)}")
    println("Total: $${"%.2f".format(total)}")
}

fun main() {
    calculateTotalPrice(19.99, 3, 0.08)
}
```

**Output**:
```
Item Price: $19.99
Quantity: 3
Subtotal: $59.97
Tax (8.0%): $4.80
Total: $64.77
```

---

## Return Values: Getting Results Back

So far, our functions only **do** things (print output). But what if you want a function to **calculate** something and give you the result to use elsewhere?

**That's where return values come in!**

### The Return Statement

```kotlin
fun add(a: Int, b: Int): Int {
//                    ^^^
//                    Return type (this function returns an Int)
    return a + b
//  ^^^^^^
//  Return keyword - sends value back to caller
}

fun main() {
    val sum = add(5, 3)  // sum receives the returned value (8)
    println("5 + 3 = $sum")
}
```

**Output**:
```
5 + 3 = 8
```

**Anatomy of a Return Function**:
```kotlin
fun functionName(param1: Type1, param2: Type2): ReturnType {
//                                              ^^^^^^^^^^
//                                              What type of value this function returns
    // Do some work
    return value  // Must match ReturnType
}
```

---

### Return Types Explained

The return type tells you what kind of value the function will give back:

```kotlin
// Returns an Int
fun add(a: Int, b: Int): Int {
    return a + b
}

// Returns a String
fun getGreeting(name: String): String {
    return "Hello, $name!"
}

// Returns a Double
fun calculateAverage(num1: Int, num2: Int, num3: Int): Double {
    val sum = num1 + num2 + num3
    return sum / 3.0  // Divide by 3.0 to get a Double result
}

// Returns a Boolean
fun isAdult(age: Int): Boolean {
    return age >= 18
}

fun main() {
    val sum = add(10, 20)              // Int
    val greeting = getGreeting("Alice") // String
    val average = calculateAverage(80, 90, 85) // Double
    val adult = isAdult(25)            // Boolean

    println(sum)       // 30
    println(greeting)  // Hello, Alice!
    println(average)   // 85.0
    println(adult)     // true
}
```

---

### Using Return Values

Once a function returns a value, you can use it in many ways:

#### 1. Store in a Variable
```kotlin
fun square(n: Int): Int {
    return n * n
}

fun main() {
    val result = square(5)
    println("5 squared is $result")  // 5 squared is 25
}
```

#### 2. Use Directly in Expressions
```kotlin
fun double(n: Int): Int {
    return n * 2
}

fun main() {
    val total = double(5) + double(3)  // (5*2) + (3*2) = 16
    println("Total: $total")
}
```

#### 3. Print Directly
```kotlin
fun multiply(a: Int, b: Int): Int {
    return a * b
}

fun main() {
    println("3 × 7 = ${multiply(3, 7)}")  // 3 × 7 = 21
}
```

#### 4. Use in Conditions
```kotlin
fun isEven(n: Int): Boolean {
    return n % 2 == 0
}

fun main() {
    if (isEven(4)) {
        println("4 is even!")
    }
}
```

#### 5. Chain Function Calls
```kotlin
fun add(a: Int, b: Int): Int = a + b
fun multiply(a: Int, b: Int): Int = a * b

fun main() {
    // (3 + 5) * 2 = 16
    val result = multiply(add(3, 5), 2)
    println(result)  // 16
}
```

---

### Functions with Early Return

A function can have multiple return statements:

```kotlin
fun getGrade(score: Int): String {
    if (score >= 90) return "A"
    if (score >= 80) return "B"
    if (score >= 70) return "C"
    if (score >= 60) return "D"
    return "F"
}

fun main() {
    println("Score 95: ${getGrade(95)}")  // A
    println("Score 82: ${getGrade(82)}")  // B
    println("Score 55: ${getGrade(55)}")  // F
}
```

**How it works**:
- When a return is executed, the function immediately exits
- No code after the return runs
- Very useful for handling different cases

---

### Void Functions (Unit Type)

What about functions that don't return anything meaningful?

```kotlin
fun printWelcome(name: String): Unit {
    println("Welcome, $name!")
    // No return statement needed
}

// Unit can be omitted (it's the default)
fun printGoodbye(name: String) {
    println("Goodbye, $name!")
}

fun main() {
    printWelcome("Alice")  // Welcome, Alice!
    printGoodbye("Bob")    // Goodbye, Bob!
}
```

**Unit** is Kotlin's way of saying "this function doesn't return a useful value." It's like `void` in other languages, but in Kotlin, you usually just omit it.

---

## Single-Expression Functions

When a function is simple and returns a single expression, Kotlin has a shortcut:

### Traditional Way vs. Shortcut

```kotlin
// Traditional way (with curly braces and return)
fun add(a: Int, b: Int): Int {
    return a + b
}

// Single-expression way (with equals sign)
fun add(a: Int, b: Int): Int = a + b

// Even shorter (type inference)
fun add(a: Int, b: Int) = a + b
```

All three versions do exactly the same thing, but the single-expression version is more concise!

---

### More Single-Expression Examples

```kotlin
// Math operations
fun square(x: Int) = x * x
fun cube(x: Int) = x * x * x
fun double(x: Int) = x * 2

// Boolean checks
fun isEven(n: Int) = n % 2 == 0
fun isPositive(n: Int) = n > 0
fun isAdult(age: Int) = age >= 18

// String operations
fun greet(name: String) = "Hello, $name!"
fun shout(text: String) = text.uppercase() + "!"

// Conditional expressions
fun max(a: Int, b: Int) = if (a > b) a else b
fun min(a: Int, b: Int) = if (a < b) a else b
fun absoluteValue(n: Int) = if (n >= 0) n else -n

fun main() {
    println(square(5))           // 25
    println(isEven(4))           // true
    println(greet("Alice"))      // Hello, Alice!
    println(max(10, 20))         // 20
    println(absoluteValue(-7))   // 7
}
```

**When to use single-expression functions**:
- ✅ Function body is one simple expression
- ✅ Makes code more readable and concise
- ❌ Don't use if the logic is complex or needs multiple lines

---

## Default Parameters

Kotlin lets you provide default values for parameters:

```kotlin
fun greet(name: String, greeting: String = "Hello") {
    println("$greeting, $name!")
}

fun main() {
    greet("Alice")                    // Uses default: "Hello, Alice!"
    greet("Bob", "Good morning")      // Custom: "Good morning, Bob!"
    greet("Charlie", "Hey")           // Custom: "Hey, Charlie!"
}
```

**Output**:
```
Hello, Alice!
Good morning, Bob!
Hey, Charlie!
```

---

### Multiple Default Parameters

```kotlin
fun createUser(
    username: String,
    email: String = "no-email@example.com",
    age: Int = 18,
    isPremium: Boolean = false
) {
    println("Username: $username")
    println("Email: $email")
    println("Age: $age")
    println("Premium: $isPremium")
    println("---")
}

fun main() {
    // Only required parameter
    createUser("alice123")

    // Override some defaults
    createUser("bob456", "bob@example.com")

    // Override all
    createUser("charlie789", "charlie@example.com", 25, true)
}
```

**Output**:
```
Username: alice123
Email: no-email@example.com
Age: 18
Premium: false
---
Username: bob456
Email: bob@example.com
Age: 18
Premium: false
---
Username: charlie789
Email: charlie@example.com
Age: 25
Premium: true
---
```

---

### Named Arguments

You can specify parameter names when calling functions:

```kotlin
fun makeRecipe(dish: String, cookTime: Int, difficulty: String, serves: Int) {
    println("$dish - Serves $serves")
    println("Cooking time: $cookTime minutes")
    println("Difficulty: $difficulty")
    println()
}

fun main() {
    // Positional arguments (order matters)
    makeRecipe("Pizza", 30, "Easy", 4)

    // Named arguments (order doesn't matter!)
    makeRecipe(
        dish = "Pasta",
        serves = 2,
        difficulty = "Medium",
        cookTime = 20
    )

    // Mix of both
    makeRecipe("Cake", cookTime = 45, difficulty = "Hard", serves = 8)
}
```

**Benefits of named arguments**:
- Code is more readable
- Order doesn't matter
- Great when functions have many parameters
- Especially useful with default parameters

---

## Hands-On Exercises

### Exercise 1: Temperature Converter

**Goal**: Create a comprehensive temperature converter.

**Requirements**:
1. Create `celsiusToFahrenheit(celsius: Double): Double`
2. Create `fahrenheitToCelsius(fahrenheit: Double): Double`
3. Create `celsiusToKelvin(celsius: Double): Double`
4. Create `displayConversions(temp: Double, unit: String)` that shows all conversions
5. Test with different temperatures

**Formulas**:
- F = (C × 9/5) + 32
- C = (F - 32) × 5/9
- K = C + 273.15

**Try it yourself first, then check the solution!**

<details>
<summary>Click to see Solution</summary>

```kotlin
fun celsiusToFahrenheit(celsius: Double): Double {
    return (celsius * 9.0 / 5.0) + 32.0
}

fun fahrenheitToCelsius(fahrenheit: Double): Double {
    return (fahrenheit - 32.0) * 5.0 / 9.0
}

fun celsiusToKelvin(celsius: Double): Double {
    return celsius + 273.15
}

fun displayConversions(temp: Double, unit: String) {
    println("=== Temperature Converter ===")
    println("Input: $temp°$unit")
    println()

    when (unit.uppercase()) {
        "C" -> {
            val fahrenheit = celsiusToFahrenheit(temp)
            val kelvin = celsiusToKelvin(temp)
            println("Fahrenheit: ${"%.2f".format(fahrenheit)}°F")
            println("Kelvin: ${"%.2f".format(kelvin)}K")
        }
        "F" -> {
            val celsius = fahrenheitToCelsius(temp)
            val kelvin = celsiusToKelvin(celsius)
            println("Celsius: ${"%.2f".format(celsius)}°C")
            println("Kelvin: ${"%.2f".format(kelvin)}K")
        }
        else -> {
            println("Unknown unit. Use C or F.")
        }
    }
    println()
}

fun main() {
    displayConversions(25.0, "C")
    displayConversions(77.0, "F")
    displayConversions(0.0, "C")
}
```

**Output**:
```
=== Temperature Converter ===
Input: 25.0°C

Fahrenheit: 77.00°F
Kelvin: 298.15K

=== Temperature Converter ===
Input: 77.0°F

Celsius: 25.00°C
Kelvin: 298.15K

=== Temperature Converter ===
Input: 0.0°C

Fahrenheit: 32.00°F
Kelvin: 273.15K
```

</details>

---

### Exercise 2: Shopping Cart Calculator

**Goal**: Create a shopping cart calculator with tax and discounts.

**Requirements**:
1. Create `calculateSubtotal(price: Double, quantity: Int): Double`
2. Create `calculateTax(amount: Double, taxRate: Double = 0.08): Double`
3. Create `applyDiscount(amount: Double, discountPercent: Double = 0.0): Double`
4. Create `calculateTotal(price: Double, quantity: Int, taxRate: Double, discountPercent: Double): Double`
5. Create `displayReceipt(itemName: String, price: Double, quantity: Int, taxRate: Double, discountPercent: Double)`

<details>
<summary>Click to see Solution</summary>

```kotlin
fun calculateSubtotal(price: Double, quantity: Int): Double {
    return price * quantity
}

fun calculateTax(amount: Double, taxRate: Double = 0.08): Double {
    return amount * taxRate
}

fun applyDiscount(amount: Double, discountPercent: Double = 0.0): Double {
    val discount = amount * (discountPercent / 100.0)
    return amount - discount
}

fun calculateTotal(
    price: Double,
    quantity: Int,
    taxRate: Double = 0.08,
    discountPercent: Double = 0.0
): Double {
    val subtotal = calculateSubtotal(price, quantity)
    val afterDiscount = applyDiscount(subtotal, discountPercent)
    val tax = calculateTax(afterDiscount, taxRate)
    return afterDiscount + tax
}

fun displayReceipt(
    itemName: String,
    price: Double,
    quantity: Int,
    taxRate: Double = 0.08,
    discountPercent: Double = 0.0
) {
    val subtotal = calculateSubtotal(price, quantity)
    val discount = subtotal - applyDiscount(subtotal, discountPercent)
    val afterDiscount = applyDiscount(subtotal, discountPercent)
    val tax = calculateTax(afterDiscount, taxRate)
    val total = afterDiscount + tax

    println("========== RECEIPT ==========")
    println("Item: $itemName")
    println("Price: $${"%.2f".format(price)} × $quantity")
    println("---")
    println("Subtotal: $${"%.2f".format(subtotal)}")
    if (discountPercent > 0) {
        println("Discount ($discountPercent%): -$${"%.2f".format(discount)}")
        println("After Discount: $${"%.2f".format(afterDiscount)}")
    }
    println("Tax (${taxRate * 100}%): $${"%.2f".format(tax)}")
    println("---")
    println("TOTAL: $${"%.2f".format(total)}")
    println("=============================")
    println()
}

fun main() {
    // Regular purchase
    displayReceipt("Laptop", 999.99, 1)

    // Purchase with discount
    displayReceipt("Mouse", 25.00, 3, discountPercent = 10.0)

    // Custom tax rate and discount
    displayReceipt("Keyboard", 75.00, 2, taxRate = 0.10, discountPercent = 15.0)
}
```

</details>

---

### Exercise 3: Grade Calculator

**Goal**: Create a student grade calculator.

**Requirements**:
1. Create `calculateAverage(score1: Int, score2: Int, score3: Int): Double`
2. Create `getLetterGrade(average: Double): String`
3. Create `isPassing(grade: String): Boolean`
4. Create `displayGradeReport(name: String, score1: Int, score2: Int, score3: Int)`

**Grading Scale**:
- A: 90-100
- B: 80-89
- C: 70-79
- D: 60-69
- F: Below 60
- Passing: C or better

<details>
<summary>Click to see Solution</summary>

```kotlin
fun calculateAverage(score1: Int, score2: Int, score3: Int): Double {
    return (score1 + score2 + score3) / 3.0
}

fun getLetterGrade(average: Double): String {
    return when {
        average >= 90 -> "A"
        average >= 80 -> "B"
        average >= 70 -> "C"
        average >= 60 -> "D"
        else -> "F"
    }
}

fun isPassing(grade: String): Boolean {
    return grade in listOf("A", "B", "C")
}

fun displayGradeReport(name: String, score1: Int, score2: Int, score3: Int) {
    val average = calculateAverage(score1, score2, score3)
    val letterGrade = getLetterGrade(average)
    val passing = isPassing(letterGrade)

    println("╔════════════════════════════════╗")
    println("║       GRADE REPORT             ║")
    println("╚════════════════════════════════╝")
    println()
    println("Student: $name")
    println("---")
    println("Test 1: $score1")
    println("Test 2: $score2")
    println("Test 3: $score3")
    println("---")
    println("Average: ${"%.1f".format(average)}")
    println("Letter Grade: $letterGrade")
    println("Status: ${if (passing) "✓ PASSING" else "✗ FAILING"}")
    println()
}

fun main() {
    displayGradeReport("Alice", 92, 88, 95)
    displayGradeReport("Bob", 75, 70, 78)
    displayGradeReport("Charlie", 55, 60, 58)
}
```

**Output**:
```
╔════════════════════════════════╗
║       GRADE REPORT             ║
╚════════════════════════════════╝

Student: Alice
---
Test 1: 92
Test 2: 88
Test 3: 95
---
Average: 91.7
Letter Grade: A
Status: ✓ PASSING

╔════════════════════════════════╗
║       GRADE REPORT             ║
╚════════════════════════════════╝

Student: Bob
---
Test 1: 75
Test 2: 70
Test 3: 78
---
Average: 74.3
Letter Grade: C
Status: ✓ PASSING

╔════════════════════════════════╗
║       GRADE REPORT             ║
╚════════════════════════════════╝

Student: Charlie
---
Test 1: 55
Test 2: 60
Test 3: 58
---
Average: 57.7
Letter Grade: F
Status: ✗ FAILING
```

</details>

---

### Exercise 4: BMI Calculator

**Goal**: Create a Body Mass Index calculator with health recommendations.

**Requirements**:
1. Create `calculateBMI(weightKg: Double, heightM: Double): Double`
2. Create `getBMICategory(bmi: Double): String`
3. Create `getHealthAdvice(category: String): String`
4. Test with different values

**BMI Categories**:
- Underweight: < 18.5
- Normal: 18.5-24.9
- Overweight: 25-29.9
- Obese: ≥ 30

**Formula**: BMI = weight (kg) / height² (m)

<details>
<summary>Click to see Solution</summary>

```kotlin
fun calculateBMI(weightKg: Double, heightM: Double): Double {
    return weightKg / (heightM * heightM)
}

fun getBMICategory(bmi: Double): String {
    return when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal weight"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
    }
}

fun getHealthAdvice(category: String): String {
    return when (category) {
        "Underweight" -> "Consider consulting a nutritionist to gain weight healthily."
        "Normal weight" -> "Great! Maintain your current healthy lifestyle."
        "Overweight" -> "Consider a balanced diet and regular exercise."
        "Obese" -> "Consult a healthcare provider for a personalized health plan."
        else -> "Unknown category"
    }
}

fun displayBMIReport(name: String, weightKg: Double, heightM: Double) {
    val bmi = calculateBMI(weightKg, heightM)
    val category = getBMICategory(bmi)
    val advice = getHealthAdvice(category)

    println("═══════════════════════════════════════")
    println("         BMI HEALTH REPORT")
    println("═══════════════════════════════════════")
    println()
    println("Name: $name")
    println("Weight: ${weightKg}kg")
    println("Height: ${heightM}m")
    println()
    println("BMI: ${"%.1f".format(bmi)}")
    println("Category: $category")
    println()
    println("Health Advice:")
    println(advice)
    println()
    println("═══════════════════════════════════════")
    println()
}

fun main() {
    displayBMIReport("Alice", 65.0, 1.70)
    displayBMIReport("Bob", 95.0, 1.80)
    displayBMIReport("Charlie", 55.0, 1.75)
}
```

</details>

---

## Common Pitfalls and Best Practices

### Common Mistakes

#### Mistake 1: Wrong Number of Arguments

```kotlin
fun greet(name: String, age: Int) {
    println("Hello, $name! You are $age years old.")
}

fun main() {
    greet("Alice")  // ❌ Error: Missing argument for parameter 'age'
    greet("Alice", 25)  // ✅ Correct
}
```

---

#### Mistake 2: Wrong Argument Type

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}

fun main() {
    val result = add("5", "3")  // ❌ Error: Type mismatch
    val result = add(5, 3)      // ✅ Correct
}
```

---

#### Mistake 3: Wrong Argument Order

```kotlin
fun createProfile(name: String, age: Int) {
    println("$name is $age years old")
}

fun main() {
    createProfile(25, "Alice")  // ❌ Error: Type mismatch
    createProfile("Alice", 25)  // ✅ Correct

    // Or use named arguments (order doesn't matter)
    createProfile(age = 25, name = "Alice")  // ✅ Also correct
}
```

---

#### Mistake 4: Forgetting Return Statement

```kotlin
fun add(a: Int, b: Int): Int {
    val sum = a + b  // Calculated but not returned!
    // ❌ Error: A 'return' expression required in a function with a block body
}

// ✅ Correct
fun add(a: Int, b: Int): Int {
    return a + b
}

// ✅ Or use single-expression
fun add(a: Int, b: Int) = a + b
```

---

#### Mistake 5: Incorrect Return Type

```kotlin
fun divide(a: Int, b: Int): Int {
    return a / b.toDouble()  // ❌ Error: Type mismatch (returns Double, not Int)
}

// ✅ Correct - match return type
fun divide(a: Int, b: Int): Double {
    return a / b.toDouble()
}
```

---

### Best Practices

#### 1. Use Descriptive Parameter Names

```kotlin
// ❌ Bad
fun calc(a: Double, b: Int, c: Double): Double {
    return a * b * c
}

// ✅ Good
fun calculateTotalPrice(itemPrice: Double, quantity: Int, taxRate: Double): Double {
    return itemPrice * quantity * taxRate
}
```

---

#### 2. Keep Functions Focused (Single Responsibility)

```kotlin
// ❌ Bad - does too much
fun processOrder(name: String, amount: Double) {
    val tax = amount * 0.08
    val total = amount + tax
    println("Customer: $name")
    println("Total: $total")
    saveToDatabase(name, total)
    sendEmail(name)
}

// ✅ Good - separate concerns
fun calculateTotal(amount: Double, taxRate: Double = 0.08): Double {
    return amount + (amount * taxRate)
}

fun displayOrderSummary(name: String, total: Double) {
    println("Customer: $name")
    println("Total: $total")
}
```

---

#### 3. Use Default Parameters for Optional Values

```kotlin
// ✅ Good - sensible defaults
fun sendEmail(
    to: String,
    subject: String,
    body: String,
    priority: String = "Normal",
    attachFile: Boolean = false
) {
    // Implementation
}

fun main() {
    sendEmail("user@example.com", "Hello", "Message")  // Uses defaults
    sendEmail("user@example.com", "Urgent", "Message", priority = "High")
}
```

---

#### 4. Use Single-Expression Functions for Simple Logic

```kotlin
// ✅ Good for simple functions
fun isEven(n: Int) = n % 2 == 0
fun double(n: Int) = n * 2
fun max(a: Int, b: Int) = if (a > b) a else b

// ✅ Use regular functions for complex logic
fun calculateGrade(scores: List<Int>): String {
    val average = scores.sum() / scores.size.toDouble()
    val letterGrade = when {
        average >= 90 -> "A"
        average >= 80 -> "B"
        average >= 70 -> "C"
        else -> "F"
    }
    return letterGrade
}
```

---

#### 5. Validate Input Parameters

```kotlin
fun divide(a: Double, b: Double): Double {
    if (b == 0.0) {
        println("Error: Cannot divide by zero!")
        return 0.0
    }
    return a / b
}

fun createUser(name: String, age: Int) {
    if (name.isBlank()) {
        println("Error: Name cannot be empty!")
        return
    }
    if (age < 0 || age > 150) {
        println("Error: Invalid age!")
        return
    }
    println("User created: $name, age $age")
}
```

---

## Quick Quiz

Test your understanding!

### Question 1
What's the difference between a parameter and an argument?

A) They are the same thing
B) Parameter is in the function definition, argument is the actual value passed
C) Argument is in the function definition, parameter is the actual value passed
D) Parameters are for strings, arguments are for numbers

<details>
<summary>Show Answer</summary>

**Answer: B) Parameter is in the function definition, argument is the actual value passed**

Explanation:
```kotlin
fun greet(name: String) {  // 'name' is a PARAMETER
    println("Hello, $name")
}

greet("Alice")  // "Alice" is an ARGUMENT
```

Parameters are placeholders in the function signature. Arguments are the actual values you provide when calling the function.

</details>

---

### Question 2
What does this function return?

```kotlin
fun calculate(x: Int): Int {
    x * 2
}
```

A) 0
B) The value of x multiplied by 2
C) Nothing - it's an error
D) Unit

<details>
<summary>Show Answer</summary>

**Answer: C) Nothing - it's an error**

Explanation: The function has a return type of `Int` but no `return` statement. The calculation `x * 2` happens but the result is not returned.

**Correct version**:
```kotlin
// Option 1: Explicit return
fun calculate(x: Int): Int {
    return x * 2
}

// Option 2: Single-expression function
fun calculate(x: Int) = x * 2
```

</details>

---

### Question 3
Which of the following is a valid single-expression function?

A) `fun add(a: Int, b: Int): Int { a + b }`
B) `fun add(a: Int, b: Int) = a + b`
C) `fun add(a: Int, b: Int) => a + b`
D) `fun add(a: Int, b: Int) return a + b`

<details>
<summary>Show Answer</summary>

**Answer: B) `fun add(a: Int, b: Int) = a + b`**

Explanation: Single-expression functions use `=` instead of curly braces and don't need the `return` keyword.

```kotlin
// All equivalent:
fun add(a: Int, b: Int): Int {
    return a + b
}

fun add(a: Int, b: Int): Int = a + b

fun add(a: Int, b: Int) = a + b  // Most concise
```

</details>

---

### Question 4
What will this code output?

```kotlin
fun greet(name: String = "Guest", greeting: String = "Hello") {
    println("$greeting, $name!")
}

fun main() {
    greet()
}
```

A) Error: Missing arguments
B) Hello, Guest!
C) Guest, Hello!
D) Nothing

<details>
<summary>Show Answer</summary>

**Answer: B) Hello, Guest!**

Explanation: When a function has default parameters, you can call it without providing those arguments. The default values are used:
- `name` defaults to "Guest"
- `greeting` defaults to "Hello"

So `greet()` becomes `greet("Guest", "Hello")` which prints "Hello, Guest!"

</details>

---

## Summary

Congratulations! You've learned how to create powerful, flexible functions in Kotlin:

### Key Concepts Covered:

**Parameters**:
- Parameters are inputs that customize function behavior
- Can have multiple parameters of different types
- Order matters (unless using named arguments)

**Return Values**:
- Functions can return values using the `return` keyword
- Return type is specified after the parameter list: `: Type`
- Returned values can be stored, used in expressions, or passed to other functions

**Single-Expression Functions**:
- Use `=` instead of `{}` for simple functions
- More concise and readable for simple logic
- Syntax: `fun name(params) = expression`

**Default Parameters**:
- Provide default values for parameters
- Make parameters optional
- Syntax: `param: Type = defaultValue`

**Named Arguments**:
- Specify parameter names when calling functions
- Make code more readable
- Allow calling parameters in any order

**Best Practices**:
- Use descriptive parameter names
- Keep functions focused (single responsibility)
- Validate input parameters
- Use single-expression functions for simple logic
- Provide sensible default values

---

## What's Next?

You've now mastered all the fundamental building blocks of Kotlin programming! In the next lesson, **Lesson 1.9: Part 1 Capstone - Personal Profile Generator**, you'll put everything together:

- Variables and data types
- User input
- Functions with parameters
- Return values
- String templates
- Calculations

You'll build a complete, interactive program that showcases all your new skills!

---

**Congratulations on completing Lesson 1.8!**

You now know how to create flexible, reusable functions that are the foundation of organized, maintainable code. Functions with parameters and return values are essential tools in every programmer's toolkit.

Keep practicing, and get ready for the capstone project!

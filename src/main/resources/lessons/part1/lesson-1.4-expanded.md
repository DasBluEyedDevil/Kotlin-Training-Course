# Lesson 1.4: Functions & Basic Syntax

**Estimated Time**: 60 minutes

---

## Topic Introduction

Imagine you're writing a recipe book. Instead of writing "crack 3 eggs, beat them, add milk, stir" every single time you need beaten eggs, you create a recipe called "Make Beaten Eggs" and just reference it whenever needed.

**Functions** are exactly this in programming—reusable blocks of code that perform specific tasks. Instead of repeating the same code over and over, you write it once in a function and call it whenever you need it.

In this lesson, you'll learn how to create functions, pass data to them, get results back, and make your code more organized and maintainable.

---

## The Concept

### The Kitchen Helper Analogy

Think of functions as kitchen helpers with specific jobs:

**Chef's Kitchen (Your Program)**:
- **Dishwasher Helper**: You give them dirty dishes → They return clean dishes
- **Prep Helper**: You give them vegetables → They return chopped vegetables
- **Baking Helper**: You give them ingredients → They return a finished cake

**Programming Functions**:
```kotlin
fun washDishes(dirtyDishes: List<String>): List<String> {
    // Washing logic here
    return cleanDishes
}

fun chopVegetables(vegetables: List<String>): List<String> {
    // Chopping logic here
    return choppedVegetables
}

fun bakeCake(ingredients: List<String>): Cake {
    // Baking logic here
    return finishedCake
}
```

**Key Concepts**:
- **Input** (parameters): What you give the function
- **Processing**: What the function does
- **Output** (return value): What the function gives back

---

## Function Basics

### Function Declaration

```kotlin
fun greet() {
    println("Hello, World!")
}

fun main() {
    greet()  // Call the function
    greet()  // Call it again!
}
```

**Output**:
```
Hello, World!
Hello, World!
```

**Anatomy of a Function**:
```kotlin
fun functionName() {
    // Function body
}
```

- `fun` = keyword to declare a function
- `functionName` = what you call the function
- `()` = parameters go here (empty if none)
- `{}` = function body (code to execute)

---

## Parameters: Passing Data to Functions

### Single Parameter

```kotlin
fun greet(name: String) {
    println("Hello, $name!")
}

fun main() {
    greet("Alice")  // Hello, Alice!
    greet("Bob")    // Hello, Bob!
    greet("Carol")  // Hello, Carol!
}
```

**Parameter Structure**:
```kotlin
fun functionName(parameterName: Type) {
    // Use parameterName here
}
```

### Multiple Parameters

```kotlin
fun introduce(name: String, age: Int, city: String) {
    println("My name is $name, I'm $age years old, and I live in $city.")
}

fun main() {
    introduce("Alice", 25, "New York")
    // Output: My name is Alice, I'm 25 years old, and I live in New York.
}
```

### Parameters with Different Types

```kotlin
fun calculateTotal(price: Double, quantity: Int, taxRate: Double) {
    val subtotal = price * quantity
    val tax = subtotal * taxRate
    val total = subtotal + tax

    println("Subtotal: $$subtotal")
    println("Tax: $$tax")
    println("Total: $$total")
}

fun main() {
    calculateTotal(19.99, 3, 0.08)
}
```

**Output**:
```
Subtotal: $59.97
Tax: $4.7976
Total: $64.7676
```

---

## Return Values: Getting Data Back

### Basic Return

```kotlin
fun add(a: Int, b: Int): Int {
    return a + b
}

fun main() {
    val result = add(5, 3)
    println("5 + 3 = $result")  // 5 + 3 = 8
}
```

**Return Type Syntax**:
```kotlin
fun functionName(params): ReturnType {
    return value
}
```

### Multiple Return Statements

```kotlin
fun getGrade(score: Int): String {
    if (score >= 90) return "A"
    if (score >= 80) return "B"
    if (score >= 70) return "C"
    if (score >= 60) return "D"
    return "F"
}

fun main() {
    println("Score 85 = Grade ${getGrade(85)}")  // B
    println("Score 92 = Grade ${getGrade(92)}")  // A
    println("Score 58 = Grade ${getGrade(58)}")  // F
}
```

### Unit Return Type (No Return Value)

```kotlin
// These are equivalent:
fun sayHello(): Unit {
    println("Hello!")
}

fun sayGoodbye() {  // Unit is implicit if omitted
    println("Goodbye!")
}
```

`Unit` is like `void` in other languages—the function doesn't return a value.

---

## Single-Expression Functions

When a function returns a single expression, you can use shorthand:

### Long Form vs Short Form

```kotlin
// Long form
fun double(x: Int): Int {
    return x * 2
}

// Short form (single-expression)
fun double(x: Int): Int = x * 2

// Even shorter (type inference)
fun double(x: Int) = x * 2
```

### More Examples

```kotlin
fun square(x: Int) = x * x

fun isEven(n: Int) = n % 2 == 0

fun max(a: Int, b: Int) = if (a > b) a else b

fun getDiscount(isPremium: Boolean) = if (isPremium) 0.20 else 0.10

fun main() {
    println(square(5))        // 25
    println(isEven(7))        // false
    println(max(10, 20))      // 20
    println(getDiscount(true)) // 0.2
}
```

---

## Default Parameters

Provide default values for parameters:

```kotlin
fun greet(name: String, greeting: String = "Hello") {
    println("$greeting, $name!")
}

fun main() {
    greet("Alice")                  // Hello, Alice!
    greet("Bob", "Good morning")    // Good morning, Bob!
    greet("Carol", "Hi")            // Hi, Carol!
}
```

### Multiple Default Parameters

```kotlin
fun createUser(
    name: String,
    age: Int = 18,
    country: String = "USA",
    isPremium: Boolean = false
) {
    println("User: $name, Age: $age, Country: $country, Premium: $isPremium")
}

fun main() {
    createUser("Alice")
    // User: Alice, Age: 18, Country: USA, Premium: false

    createUser("Bob", 25)
    // User: Bob, Age: 25, Country: USA, Premium: false

    createUser("Carol", 30, "Canada", true)
    // User: Carol, Age: 30, Country: Canada, Premium: true
}
```

---

## Named Arguments

Call functions with parameter names for clarity:

```kotlin
fun sendEmail(to: String, subject: String, body: String) {
    println("To: $to")
    println("Subject: $subject")
    println("Body: $body")
}

fun main() {
    // Positional arguments
    sendEmail("alice@example.com", "Meeting", "Let's meet at 3pm")

    // Named arguments (any order!)
    sendEmail(
        subject = "Reminder",
        body = "Don't forget the meeting",
        to = "bob@example.com"
    )

    // Mix positional and named
    sendEmail("carol@example.com",
              subject = "Hello",
              body = "How are you?")
}
```

**Benefits of Named Arguments**:
- Code is more readable
- Order doesn't matter
- Especially useful with many parameters or default values

```kotlin
fun formatText(
    text: String,
    uppercase: Boolean = false,
    trim: Boolean = true,
    reverse: Boolean = false
) {
    var result = text
    if (trim) result = result.trim()
    if (uppercase) result = result.uppercase()
    if (reverse) result = result.reversed()
    println(result)
}

fun main() {
    formatText("  hello  ", uppercase = true, reverse = true)
    // Output: OLLEH
}
```

---

## Extension Functions

Add new functions to existing types without modifying their source code:

### Basic Extension Function

```kotlin
// Add a function to String type
fun String.shout(): String {
    return this.uppercase() + "!!!"
}

fun main() {
    val message = "hello"
    println(message.shout())  // HELLO!!!

    // Or directly:
    println("kotlin".shout())  // KOTLIN!!!
}
```

In extension functions, `this` refers to the object the function is called on.

### More Extension Examples

```kotlin
// Extension on Int
fun Int.isEven(): Boolean = this % 2 == 0

fun Int.square(): Int = this * this

// Extension on Double
fun Double.format(decimals: Int): String {
    return "%.${decimals}f".format(this)
}

// Extension on String
fun String.isPalindrome(): Boolean {
    val cleaned = this.lowercase().replace(" ", "")
    return cleaned == cleaned.reversed()
}

fun main() {
    println(4.isEven())        // true
    println(5.square())        // 25
    println(3.14159.format(2)) // 3.14

    println("racecar".isPalindrome())    // true
    println("A man a plan a canal Panama".isPalindrome())  // true
    println("kotlin".isPalindrome())     // false
}
```

### Why Extension Functions?

They make code more readable:

```kotlin
// Without extension
val doubled = multiplyBy2(number)
val formatted = formatAsCurrency(price)

// With extension
val doubled = number.double()
val formatted = price.asCurrency()
```

---

## Variable Number of Arguments (Vararg)

Accept any number of arguments:

```kotlin
fun sum(vararg numbers: Int): Int {
    var total = 0
    for (number in numbers) {
        total += number
    }
    return total
}

fun main() {
    println(sum(1, 2, 3))           // 6
    println(sum(10, 20, 30, 40))    // 100
    println(sum(5))                 // 5
    println(sum())                  // 0
}
```

### Practical Vararg Example

```kotlin
fun printAll(vararg messages: String) {
    for (message in messages) {
        println("- $message")
    }
}

fun main() {
    printAll("Apple", "Banana", "Cherry")
    // Output:
    // - Apple
    // - Banana
    // - Cherry
}
```

---

## Function Scope and Variables

### Local Variables

Variables inside functions are **local**—they only exist within that function:

```kotlin
fun calculate() {
    val result = 10 + 20  // Local to calculate()
    println(result)
}

fun main() {
    calculate()  // 30
    // println(result)  // ❌ Error: result not accessible here
}
```

### Function Parameters are Read-Only

```kotlin
fun modifyValue(number: Int) {
    // number = number + 1  // ❌ Error: Val cannot be reassigned
    val newNumber = number + 1  // ✅ Create new variable
    println(newNumber)
}
```

---

## Exercise 1: Temperature Converter Functions

**Goal**: Create a temperature converter with reusable functions.

**Requirements**:
1. Create `celsiusToFahrenheit(celsius: Double): Double` function
2. Create `celsiusToKelvin(celsius: Double): Double` function
3. Create `fahrenheitToCelsius(fahrenheit: Double): Double` function
4. In `main()`, ask user for temperature in Celsius and display all conversions

**Formulas**:
- F = (C × 9/5) + 32
- K = C + 273.15
- C = (F - 32) × 5/9

---

## Solution 1: Temperature Converter Functions

```kotlin
fun celsiusToFahrenheit(celsius: Double): Double {
    return (celsius * 9 / 5) + 32
}

fun celsiusToKelvin(celsius: Double): Double {
    return celsius + 273.15
}

fun fahrenheitToCelsius(fahrenheit: Double): Double {
    return (fahrenheit - 32) * 5 / 9
}

// Bonus: Extension functions for more readable code
fun Double.toFahrenheit() = (this * 9 / 5) + 32
fun Double.toKelvin() = this + 273.15

fun main() {
    println("=== Temperature Converter ===")
    println("Enter temperature in Celsius:")
    val celsius = readln().toDouble()

    val fahrenheit = celsiusToFahrenheit(celsius)
    val kelvin = celsiusToKelvin(celsius)

    println("\nResults:")
    println("$celsius°C = $fahrenheit°F = ${kelvin}K")

    // Using extension functions:
    println("\nUsing extension functions:")
    println("$celsius°C = ${celsius.toFahrenheit()}°F = ${celsius.toKelvin()}K")
}
```

**Sample Output**:
```
=== Temperature Converter ===
Enter temperature in Celsius:
25

Results:
25.0°C = 77.0°F = 298.15K
```

---

## Exercise 2: BMI Calculator with Functions

**Goal**: Create a BMI calculator using functions.

**Requirements**:
1. Create `calculateBMI(weight: Double, height: Double): Double` function
2. Create `getBMICategory(bmi: Double): String` function that returns:
   - "Underweight" if BMI < 18.5
   - "Normal weight" if BMI 18.5-24.9
   - "Overweight" if BMI 25-29.9
   - "Obese" if BMI ≥ 30
3. Create `displayBMIReport(name: String, bmi: Double, category: String)` function
4. In `main()`, get user input and display formatted report

**Formula**: BMI = weight (kg) / height² (m)

---

## Solution 2: BMI Calculator with Functions

```kotlin
fun calculateBMI(weight: Double, height: Double): Double {
    return weight / (height * height)
}

fun getBMICategory(bmi: Double): String {
    return when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal weight"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
    }
}

fun displayBMIReport(name: String, bmi: Double, category: String) {
    println("\n=== BMI Report for $name ===")
    println("BMI: %.2f".format(bmi))
    println("Category: $category")
    println("=" * 30)
}

fun main() {
    println("=== BMI Calculator ===")

    println("Enter your name:")
    val name = readln()

    println("Enter your weight (kg):")
    val weight = readln().toDouble()

    println("Enter your height (meters):")
    val height = readln().toDouble()

    val bmi = calculateBMI(weight, height)
    val category = getBMICategory(bmi)

    displayBMIReport(name, bmi, category)
}
```

**Sample Output**:
```
=== BMI Calculator ===
Enter your name:
Alice
Enter your weight (kg):
65
Enter your height (meters):
1.70

=== BMI Report for Alice ===
BMI: 22.49
Category: Normal weight
==============================
```

---

## Exercise 3: Simple Banking Functions

**Goal**: Create basic banking operations using functions.

**Requirements**:
1. Create `deposit(balance: Double, amount: Double): Double` function
2. Create `withdraw(balance: Double, amount: Double): Double` function
   - Only allow withdrawal if balance is sufficient
   - Return updated balance
3. Create `displayBalance(balance: Double)` function
4. In `main()`, create a simple menu system to deposit, withdraw, or check balance

---

## Solution 3: Simple Banking Functions

```kotlin
fun deposit(balance: Double, amount: Double): Double {
    if (amount <= 0) {
        println("Invalid deposit amount!")
        return balance
    }
    println("Deposited: $$amount")
    return balance + amount
}

fun withdraw(balance: Double, amount: Double): Double {
    if (amount <= 0) {
        println("Invalid withdrawal amount!")
        return balance
    }
    if (amount > balance) {
        println("Insufficient funds! Current balance: $$balance")
        return balance
    }
    println("Withdrawn: $$amount")
    return balance - amount
}

fun displayBalance(balance: Double) {
    println("Current Balance: $${"%.2f".format(balance)}")
}

fun main() {
    var balance = 1000.0

    println("=== Simple Banking System ===")
    displayBalance(balance)

    // Deposit
    println("\nDepositing $500...")
    balance = deposit(balance, 500.0)
    displayBalance(balance)

    // Withdraw
    println("\nWithdrawing $200...")
    balance = withdraw(balance, 200.0)
    displayBalance(balance)

    // Attempt overdraw
    println("\nAttempting to withdraw $2000...")
    balance = withdraw(balance, 2000.0)
    displayBalance(balance)
}
```

**Sample Output**:
```
=== Simple Banking System ===
Current Balance: $1000.00

Depositing $500...
Deposited: $500.0
Current Balance: $1500.00

Withdrawing $200...
Withdrawn: $200.0
Current Balance: $1300.00

Attempting to withdraw $2000...
Insufficient funds! Current balance: $1300.0
Current Balance: $1300.00
```

---

## Best Practices for Functions

### 1. Single Responsibility Principle

Each function should do ONE thing well:

```kotlin
// ❌ Bad - function does too much
fun processUser(name: String, age: Int) {
    val validated = validateAge(age)
    val formatted = formatName(name)
    saveToDatabase(formatted, validated)
    sendWelcomeEmail(formatted)
    logActivity(formatted)
}

// ✅ Good - separate functions for each responsibility
fun validateUser(name: String, age: Int): Boolean { /* ... */ }
fun saveUser(name: String, age: Int) { /* ... */ }
fun notifyUser(name: String) { /* ... */ }
```

### 2. Descriptive Function Names

```kotlin
// ❌ Bad
fun calc(a: Int, b: Int) = a + b
fun process(data: String) { /* ... */ }

// ✅ Good
fun calculateTotal(price: Int, quantity: Int) = price * quantity
fun validateEmailAddress(email: String) { /* ... */ }
```

### 3. Keep Functions Short

Aim for functions that fit on one screen (~20-30 lines max).

### 4. Avoid Side Effects When Possible

```kotlin
// ❌ Bad - modifies external state
var total = 0
fun addToTotal(amount: Int) {
    total += amount
}

// ✅ Good - returns new value
fun add(current: Int, amount: Int): Int {
    return current + amount
}
```

---

## Common Mistakes

### Mistake 1: Forgetting Return Type

```kotlin
// ❌ Error if you expect a return value but don't specify type
fun add(a: Int, b: Int) {
    a + b  // This doesn't return anything!
}

// ✅ Correct
fun add(a: Int, b: Int): Int {
    return a + b
}

// ✅ Or use single-expression
fun add(a: Int, b: Int) = a + b
```

### Mistake 2: Not Returning a Value

```kotlin
// ❌ Error
fun divide(a: Int, b: Int): Int {
    val result = a / b
    // Missing return statement!
}

// ✅ Correct
fun divide(a: Int, b: Int): Int {
    return a / b
}
```

### Mistake 3: Wrong Argument Order

```kotlin
fun createProfile(name: String, age: Int, city: String) { /* ... */ }

// ❌ Error - wrong order
createProfile(25, "Alice", "NYC")  // Type mismatch!

// ✅ Correct
createProfile("Alice", 25, "NYC")

// ✅ Better - use named arguments
createProfile(name = "Alice", age = 25, city = "NYC")
```

---

## Checkpoint Quiz

### Question 1
What keyword is used to declare a function in Kotlin?

A) function
B) def
C) fun
D) func

### Question 2
What is the return type of a function that doesn't return a value?

A) void
B) null
C) Unit
D) Nothing

### Question 3
Which is a valid single-expression function?

A) `fun double(x: Int) { x * 2 }`
B) `fun double(x: Int) = x * 2`
C) `fun double(x: Int) => x * 2`
D) `fun double(x: Int): x * 2`

### Question 4
What are named arguments used for?

A) Making code faster
B) Reducing memory usage
C) Improving code readability and allowing any parameter order
D) Required for all functions

### Question 5
In an extension function, what does `this` refer to?

A) The function itself
B) The class containing the function
C) The receiver object (the object the function is called on)
D) The return value

---

## Quiz Answers

**Question 1: C) fun**

Kotlin uses `fun` keyword to declare functions:

```kotlin
fun greet() {
    println("Hello!")
}
```

---

**Question 2: C) Unit**

`Unit` is Kotlin's type for "no meaningful return value":

```kotlin
fun sayHello(): Unit {  // Unit can be omitted
    println("Hello!")
}

// Equivalent:
fun sayHello() {
    println("Hello!")
}
```

---

**Question 3: B) `fun double(x: Int) = x * 2`**

Single-expression functions use `=` instead of curly braces:

```kotlin
// Traditional
fun double(x: Int): Int {
    return x * 2
}

// Single-expression (preferred for simple functions)
fun double(x: Int) = x * 2
```

---

**Question 4: C) Improving code readability and allowing any parameter order**

Named arguments make function calls clearer:

```kotlin
// Without named arguments - unclear
sendEmail("bob@example.com", "Meeting", "3pm today")

// With named arguments - clear
sendEmail(
    to = "bob@example.com",
    subject = "Meeting",
    body = "3pm today"
)
```

---

**Question 5: C) The receiver object (the object the function is called on)**

In extension functions, `this` is the object being extended:

```kotlin
fun String.shout(): String {
    return this.uppercase() + "!!!"
    //     ^^^^
    //     The String object
}

"hello".shout()  // this = "hello"
```

---

## What You've Learned

✅ How to declare and call functions
✅ Function parameters and return types
✅ Single-expression functions for concise code
✅ Default parameters and named arguments
✅ Extension functions to add functionality to existing types
✅ Vararg for variable number of arguments
✅ Function scope and local variables
✅ Best practices for writing clean, maintainable functions

---

## Next Steps

In **Lesson 1.5: Collections & Arrays**, you'll learn:
- Lists, sets, and maps for storing multiple values
- Array basics
- Common collection operations like filter, map, and forEach
- When to use each collection type

Get ready to work with groups of data efficiently!

---

**Congratulations on completing Lesson 1.4!**

You now know how to organize code into reusable, maintainable functions—a crucial skill for any programmer!

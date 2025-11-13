# Lesson 1.2: Variables, Data Types & Operators

**Estimated Time**: 55 minutes

---

## Topic Introduction

In the previous lesson, you wrote your first Kotlin programs and learned about `main()`, `println()`, and `readln()`. Now it's time to understand how to store and manipulate data—the core of all programming.

Imagine you're building a calculator app. You need to store numbers, perform operations on them, and display results. This lesson teaches you exactly how to do that with **variables**, **data types**, and **operators**.

---

## The Concept

### The Box Analogy

Think of variables like labeled boxes in a warehouse:

**Physical Warehouse**:
- **Box**: Container that holds something
- **Label**: Name on the box ("Books", "Toys", "Electronics")
- **Contents**: What's inside the box
- **Type**: What kind of things can go in (books only, toys only, etc.)

**Programming Warehouse**:
- **Variable**: Container that holds data
- **Name**: What you call the variable (`age`, `name`, `price`)
- **Value**: The data stored inside
- **Type**: What kind of data it can hold (numbers, text, true/false)

```kotlin
val age = 25        // Box labeled "age" contains number 25
val name = "Alice"  // Box labeled "name" contains text "Alice"
val isStudent = true  // Box labeled "isStudent" contains true/false
```

---

## Variables: val vs var

In Kotlin, you can create two kinds of variables:

### `val` - Immutable (Read-Only)

```kotlin
val age = 25
age = 26  // ❌ Error: Val cannot be reassigned
```

`val` stands for **value**. Once you put something in the box, you **cannot** change it.

**When to use**: Use `val` by default for values that won't change.

**Real-World Examples**:
```kotlin
val birthYear = 1995  // Birth year never changes
val pi = 3.14159      // Mathematical constant
val companyName = "TechCorp"  // Company name is fixed
```

### `var` - Mutable (Can Change)

```kotlin
var score = 0
score = 10  // ✅ OK
score = 20  // ✅ OK, can reassign
```

`var` stands for **variable**. You can change what's in the box anytime.

**When to use**: Use `var` only when the value needs to change.

**Real-World Examples**:
```kotlin
var playerScore = 0      // Score changes as game progresses
var accountBalance = 1000.0  // Balance changes with transactions
var isLoggedIn = false   // Login status changes
```

### Best Practice: Prefer `val` Over `var`

```kotlin
// ✅ Good - Using val by default
val name = "Bob"
val age = 30
var score = 0  // var only when needed

// ❌ Bad - Using var unnecessarily
var name = "Bob"  // Name won't change, should be val
var age = 30      // Age won't change (in one program), should be val
```

**Why prefer `val`?**
- Prevents accidental changes
- Makes code easier to understand (you know it won't change)
- Safer for multi-threaded programs (advanced topic)

---

## Data Types

Every variable has a **type** that determines what kind of data it can hold.

### Basic Data Types

| Type | Description | Example Values | Memory Size |
|------|-------------|----------------|-------------|
| `Int` | Whole numbers | -2,147,483,648 to 2,147,483,647 | 32 bits |
| `Long` | Large whole numbers | -9 quintillion to 9 quintillion | 64 bits |
| `Short` | Small whole numbers | -32,768 to 32,767 | 16 bits |
| `Byte` | Tiny whole numbers | -128 to 127 | 8 bits |
| `Double` | Decimal numbers | 3.14, -0.001, 1.5e10 | 64 bits |
| `Float` | Smaller decimals | 3.14f, 2.5f | 32 bits |
| `Boolean` | True or false | true, false | 1 bit |
| `Char` | Single character | 'A', 'z', '5', '@' | 16 bits |
| `String` | Text (sequence of characters) | "Hello", "Kotlin" | Variable |

### Examples of Each Type

```kotlin
// Integer types
val age: Int = 25
val population: Long = 7_800_000_000L  // L suffix for Long
val temperature: Short = -15
val statusCode: Byte = 127

// Floating-point types
val price: Double = 19.99
val exchangeRate: Float = 1.23f  // f suffix for Float

// Boolean
val isAvailable: Boolean = true
val hasDiscount: Boolean = false

// Character (single quotes!)
val grade: Char = 'A'
val symbol: Char = '@'

// String (double quotes!)
val name: String = "Alice"
val message: String = "Hello, World!"
```

**Note**: Underscores in numbers improve readability:
```kotlin
val million = 1_000_000  // Same as 1000000
val billion = 1_000_000_000L
```

---

## Type Inference

Kotlin is smart—it can figure out types automatically!

```kotlin
// Explicit type annotation
val age: Int = 25
val name: String = "Bob"

// Type inference (Kotlin figures it out)
val age = 25        // Kotlin knows it's Int
val name = "Bob"    // Kotlin knows it's String
val price = 19.99   // Kotlin knows it's Double
val isValid = true  // Kotlin knows it's Boolean
```

**When to use explicit types**:
- When the type isn't obvious
- For documentation/clarity
- Most of the time, let Kotlin infer!

```kotlin
// Inference is clear
val count = 10  // Obviously Int

// Explicit might help readability
val result: Boolean = checkStatus()  // Makes intent clear
```

---

## Type Safety and Type Checking

Kotlin is **strongly typed**—you can't mix types without converting:

```kotlin
val age = 25        // Int
val name = "Alice"  // String

// ❌ Error: Type mismatch
val result = age + name  // Can't add Int and String!

// ✅ Correct: Convert to same type
val result = age.toString() + name  // "25Alice"
val result2 = "$age years old"      // String interpolation: "25 years old"
```

**Check a variable's type**:
```kotlin
val number = 42

println(number is Int)     // true
println(number is String)  // false
println(number is Double)  // false
```

---

## Type Conversions

Convert between types explicitly:

### Number Conversions

```kotlin
val intNumber: Int = 42
val doubleNumber: Double = intNumber.toDouble()  // 42.0
val longNumber: Long = intNumber.toLong()        // 42L

val decimalNumber: Double = 3.14
val intFromDecimal: Int = decimalNumber.toInt()  // 3 (truncates decimal)

// String to number
val textNumber = "123"
val number = textNumber.toInt()  // 123

// Number to string
val age = 25
val ageText = age.toString()  // "25"
```

### Common Conversion Methods

| Method | From → To | Example |
|--------|-----------|---------|
| `toInt()` | Any number/String → Int | `"42".toInt()` → 42 |
| `toDouble()` | Any number/String → Double | `42.toDouble()` → 42.0 |
| `toLong()` | Any number/String → Long | `42.toLong()` → 42L |
| `toFloat()` | Any number/String → Float | `42.toFloat()` → 42.0f |
| `toString()` | Any type → String | `42.toString()` → "42" |
| `toBoolean()` | String → Boolean | `"true".toBoolean()` → true |

### Handling Conversion Errors

```kotlin
// ❌ This will crash if input isn't a valid number
val number = readln().toInt()  // User types "abc" → NumberFormatException

// ✅ Safe conversion with default value
val number = readln().toIntOrNull() ?: 0  // Returns 0 if conversion fails

// ✅ Safe conversion with error handling
val input = readln()
val number = input.toIntOrNull()

if (number != null) {
    println("Valid number: $number")
} else {
    println("Invalid input!")
}
```

---

## Operators

Operators perform operations on values.

### Arithmetic Operators

```kotlin
val a = 10
val b = 3

val sum = a + b       // 13 (addition)
val difference = a - b  // 7  (subtraction)
val product = a * b   // 30 (multiplication)
val quotient = a / b  // 3  (division - integer division!)
val remainder = a % b // 1  (modulus - remainder after division)
```

**Important**: Integer division truncates decimals:
```kotlin
val result1 = 10 / 3   // 3 (not 3.333...)
val result2 = 10.0 / 3  // 3.3333... (at least one Double)
val result3 = 10 / 3.0  // 3.3333... (at least one Double)
```

### Compound Assignment Operators

Shortcut operators that modify a variable:

```kotlin
var score = 10

score += 5   // Same as: score = score + 5  → score is now 15
score -= 3   // Same as: score = score - 3  → score is now 12
score *= 2   // Same as: score = score * 2  → score is now 24
score /= 4   // Same as: score = score / 4  → score is now 6
score %= 4   // Same as: score = score % 4  → score is now 2
```

### Increment and Decrement Operators

```kotlin
var count = 5

count++  // Increment by 1 → count is now 6 (same as count += 1)
count--  // Decrement by 1 → count is now 5 (same as count -= 1)
```

**Prefix vs Postfix**:
```kotlin
var x = 5
val a = x++  // a = 5, then x becomes 6 (use value, then increment)
val b = ++x  // x becomes 7, then b = 7 (increment, then use value)

// Most common usage:
for (i in 0..10) {
    println(i)
    // Could use i++, but not needed in Kotlin ranges
}
```

### Comparison Operators

Return `true` or `false`:

```kotlin
val a = 10
val b = 20

a == b   // false (equal to)
a != b   // true  (not equal to)
a > b    // false (greater than)
a < b    // true  (less than)
a >= b   // false (greater than or equal to)
a <= b   // true  (less than or equal to)
```

**String Comparison**:
```kotlin
val name1 = "Alice"
val name2 = "alice"

name1 == name2   // false (case-sensitive)
name1.equals(name2, ignoreCase = true)  // true
```

### Logical Operators

Combine boolean values:

```kotlin
val age = 25
val hasLicense = true

// AND (&&) - Both must be true
val canDrive = age >= 18 && hasLicense  // true

// OR (||) - At least one must be true
val canVote = age >= 18 || hasLicense  // true

// NOT (!) - Inverts boolean
val isChild = !(age >= 18)  // false
```

**Truth Tables**:

| A | B | A && B | A \|\| B | !A |
|---|---|--------|----------|-----|
| T | T | T      | T        | F   |
| T | F | F      | T        | F   |
| F | T | F      | T        | T   |
| F | F | F      | F        | T   |

**Short-Circuit Evaluation**:
```kotlin
val a = true
val b = false

// && stops if first is false
if (b && expensiveFunction()) {  // expensiveFunction() NOT called
    // ...
}

// || stops if first is true
if (a || expensiveFunction()) {  // expensiveFunction() NOT called
    // ...
}
```

---

## String Operations

### String Concatenation

```kotlin
val firstName = "John"
val lastName = "Doe"

// Using + operator
val fullName = firstName + " " + lastName  // "John Doe"

// Using string templates (preferred)
val fullName2 = "$firstName $lastName"  // "John Doe"

// Complex expressions with ${}
val age = 25
val message = "${firstName}'s age is ${age + 5}"  // "John's age is 30"
```

### String Properties and Methods

```kotlin
val text = "Hello, Kotlin!"

text.length           // 14 (number of characters)
text.uppercase()      // "HELLO, KOTLIN!"
text.lowercase()      // "hello, kotlin!"
text.reversed()       // "!niltoK ,olleH"
text.contains("Kotlin")  // true
text.startsWith("Hello")  // true
text.endsWith("!")    // true
text.replace("Kotlin", "World")  // "Hello, World!"

// Access individual characters
text[0]      // 'H' (first character)
text[7]      // 'K'
text.first() // 'H'
text.last()  // '!'
```

### Multi-line Strings

```kotlin
val poem = """
    Roses are red,
    Violets are blue,
    Kotlin is awesome,
    And so are you!
""".trimIndent()

println(poem)
```

---

## Exercise 1: Temperature Converter

**Goal**: Create a program that converts temperature from Celsius to Fahrenheit and Kelvin.

**Formula**:
- Fahrenheit = (Celsius × 9/5) + 32
- Kelvin = Celsius + 273.15

**Requirements**:
1. Ask user for temperature in Celsius
2. Calculate Fahrenheit and Kelvin
3. Display all three temperatures

**Expected Output**:
```
Enter temperature in Celsius:
25
25.0°C = 77.0°F = 298.15K
```

---

## Solution 1: Temperature Converter

```kotlin
fun main() {
    println("=== Temperature Converter ===")
    println("Enter temperature in Celsius:")

    val celsius = readln().toDouble()

    val fahrenheit = (celsius * 9 / 5) + 32
    val kelvin = celsius + 273.15

    println("$celsius°C = $fahrenheit°F = ${kelvin}K")
}
```

**Key Points**:
- We use `toDouble()` to allow decimal temperatures
- Formula uses decimal division (9 / 5 works because we're in Double context)
- String interpolation displays all values

---

## Exercise 2: Rectangle Calculator

**Goal**: Calculate the area and perimeter of a rectangle.

**Formulas**:
- Area = width × height
- Perimeter = 2 × (width + height)

**Requirements**:
1. Ask for width and height
2. Calculate area and perimeter
3. Display results with appropriate units

---

## Solution 2: Rectangle Calculator

```kotlin
fun main() {
    println("=== Rectangle Calculator ===")

    println("Enter width (meters):")
    val width = readln().toDouble()

    println("Enter height (meters):")
    val height = readln().toDouble()

    val area = width * height
    val perimeter = 2 * (width + height)

    println("\nResults:")
    println("Area: $area square meters")
    println("Perimeter: $perimeter meters")
}
```

---

## Exercise 3: Age Calculator

**Goal**: Calculate how many days, hours, and minutes old someone is.

**Requirements**:
1. Ask for age in years
2. Calculate approximate days (years × 365)
3. Calculate hours (days × 24)
4. Calculate minutes (hours × 60)

---

## Solution 3: Age Calculator

```kotlin
fun main() {
    println("=== Age Calculator ===")
    println("Enter your age in years:")

    val years = readln().toInt()

    val days = years * 365
    val hours = days * 24
    val minutes = hours * 60

    println("\nYou are approximately:")
    println("$days days old")
    println("$hours hours old")
    println("$minutes minutes old")
}
```

**Sample Output**:
```
=== Age Calculator ===
Enter your age in years:
25

You are approximately:
9125 days old
219000 hours old
13140000 minutes old
```

---

## Common Mistakes and How to Avoid Them

### Mistake 1: Integer Division Surprise

```kotlin
// ❌ Unexpected result
val average = (5 + 10 + 15) / 3  // 10 (not 10.0!)

// ✅ Force decimal division
val average = (5 + 10 + 15) / 3.0  // 10.0
// Or
val average = (5.0 + 10 + 15) / 3  // 10.0
```

### Mistake 2: Trying to Reassign val

```kotlin
// ❌ Error
val score = 100
score = 200  // Val cannot be reassigned

// ✅ Use var if you need to change it
var score = 100
score = 200  // OK
```

### Mistake 3: Type Mismatch

```kotlin
// ❌ Error
val age: Int = 25
val price: Double = age  // Type mismatch

// ✅ Convert explicitly
val price: Double = age.toDouble()  // 25.0
```

### Mistake 4: NumberFormatException

```kotlin
// ❌ Crashes if user types non-number
val number = readln().toInt()  // User types "hello" → crash!

// ✅ Safe conversion
val number = readln().toIntOrNull() ?: 0  // Returns 0 if invalid
```

---

## Checkpoint Quiz

### Question 1
What's the difference between `val` and `var`?

A) `val` is for numbers, `var` is for text
B) `val` cannot be reassigned, `var` can be reassigned
C) `val` is faster than `var`
D) There is no difference

### Question 2
What is the result of `10 / 3` in Kotlin?

A) 3.333...
B) 3.0
C) 3
D) Error

### Question 3
Which data type should you use to store `3.14159`?

A) Int
B) Float
C) Double
D) Decimal

### Question 4
What does `"Hello".length` return?

A) "Hello"
B) 5
C) true
D) Error

### Question 5
What is the result of `10 % 3`?

A) 3
B) 1
C) 0
D) 3.333...

---

## Quiz Answers

**Question 1: B) `val` cannot be reassigned, `var` can be reassigned**

```kotlin
val age = 25
age = 26  // ❌ Error: Val cannot be reassigned

var score = 100
score = 200  // ✅ OK: Var can be reassigned
```

`val` = immutable (read-only), `var` = mutable (changeable).

---

**Question 2: C) 3**

Integer division in Kotlin truncates the decimal part:

```kotlin
val result = 10 / 3  // 3 (integer division)

// To get decimal result, use Double:
val result = 10.0 / 3  // 3.3333...
val result = 10 / 3.0  // 3.3333...
```

---

**Question 3: C) Double**

`Double` is the default type for decimal numbers:

```kotlin
val pi = 3.14159  // Inferred as Double

// Float requires f suffix:
val pi: Float = 3.14159f
```

`Double` has higher precision (64 bits) than `Float` (32 bits).

---

**Question 4: B) 5**

`.length` is a property that returns the number of characters:

```kotlin
"Hello".length    // 5
"Kotlin".length   // 6
"".length         // 0
```

---

**Question 5: B) 1**

The `%` operator (modulus) returns the remainder after division:

```kotlin
10 % 3  // 1 (10 ÷ 3 = 3 remainder 1)
15 % 4  // 3 (15 ÷ 4 = 3 remainder 3)
20 % 5  // 0 (20 ÷ 5 = 4 remainder 0)
```

Useful for checking if a number is even: `number % 2 == 0`

---

## What You've Learned

✅ Difference between `val` (immutable) and `var` (mutable)
✅ Basic data types: Int, Double, Boolean, Char, String
✅ Type inference and type safety
✅ Type conversions (`toInt()`, `toDouble()`, etc.)
✅ Arithmetic operators: +, -, *, /, %
✅ Comparison operators: ==, !=, <, >, <=, >=
✅ Logical operators: &&, ||, !
✅ String operations and string interpolation
✅ Common mistakes and how to avoid them

---

## Next Steps

In **Lesson 1.3: Control Flow - Conditionals & Loops**, you'll learn:
- `if`-`else` statements for decision making
- `when` expressions (Kotlin's powerful switch)
- `for` loops for repetition
- `while` and `do-while` loops
- Breaking and continuing loops

Get ready to make your programs smart and responsive!

---

## Practice Challenges

Try these on your own:

1. **BMI Calculator**: Ask for height (meters) and weight (kg), calculate BMI = weight / (height²)

2. **Time Converter**: Convert hours to minutes and seconds

3. **Compound Interest**: Calculate final amount given principal, rate, and time

4. **Grade Calculator**: Average three test scores and display the result

---

**Great job completing Lesson 1.2!** 🎉

You now understand how to store and manipulate data—the foundation of all programming!

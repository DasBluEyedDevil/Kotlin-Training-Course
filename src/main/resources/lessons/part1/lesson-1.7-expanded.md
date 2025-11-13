# Lesson 1.7: Part 1 Capstone Project - CLI Calculator

**Estimated Time**: 90 minutes

---

## Project Introduction

Congratulations on making it to the capstone project! You've learned variables, control flow, functions, collections, and null safety. Now it's time to combine all these skills into a real, practical application.

In this project, you'll build a **Command-Line Calculator** that:
- Performs all basic arithmetic operations (+, -, *, /, %)
- Has a professional menu system
- Validates user input
- Handles errors gracefully
- Keeps a history of calculations
- Runs until the user chooses to exit

This is a complete, production-style application that demonstrates best practices and real-world programming patterns.

---

## Project Requirements

### Core Features

1. **Menu System**
   - Display clear menu options
   - Use when expression for menu selection
   - Loop until user exits

2. **Operations**
   - Addition
   - Subtraction
   - Multiplication
   - Division (with divide-by-zero check)
   - Modulus (remainder)

3. **Input Validation**
   - Handle invalid numbers
   - Handle invalid menu choices
   - Provide helpful error messages

4. **Calculation History**
   - Store past calculations
   - Display history on request
   - Clear history option

5. **Professional Polish**
   - Clear formatting
   - Helpful prompts
   - Graceful error handling

---

## Project Architecture

We'll structure our calculator with these components:

```
1. Data Models
   - Calculation (stores a single calculation)

2. Core Functions
   - add(), subtract(), multiply(), divide(), modulus()
   - formatResult()

3. UI Functions
   - displayMenu()
   - displayHistory()
   - clearHistory()

4. Input Functions
   - getNumber()
   - getMenuChoice()

5. Main Program
   - Main loop
   - Menu handling
   - Operation execution
```

---

## Step-by-Step Implementation

### Step 1: Data Model

First, let's create a data class to store calculations:

```kotlin
data class Calculation(
    val operation: String,
    val num1: Double,
    val num2: Double,
    val result: Double
) {
    override fun toString(): String {
        return "$num1 $operation $num2 = $result"
    }
}
```

**What this does**:
- Stores all information about a calculation
- Custom `toString()` for nice display
- Example: "10.0 + 5.0 = 15.0"

---

### Step 2: Core Calculation Functions

```kotlin
fun add(a: Double, b: Double): Double = a + b

fun subtract(a: Double, b: Double): Double = a - b

fun multiply(a: Double, b: Double): Double = a * b

fun divide(a: Double, b: Double): Double? {
    if (b == 0.0) {
        println("Error: Cannot divide by zero!")
        return null
    }
    return a / b
}

fun modulus(a: Double, b: Double): Double? {
    if (b == 0.0) {
        println("Error: Cannot calculate modulus with zero!")
        return null
    }
    return a % b
}
```

**Key Points**:
- Simple, focused functions (Single Responsibility)
- Division and modulus return `Double?` (nullable) for error handling
- Error messages provided at the point of failure

---

### Step 3: Input Validation Functions

```kotlin
fun getNumber(prompt: String): Double? {
    print(prompt)
    val input = readln()
    return input.toDoubleOrNull()
}

fun getMenuChoice(): Int? {
    print("Enter your choice: ")
    val input = readln()
    return input.toIntOrNull()
}
```

**Why nullable returns?**
- Safely handle invalid input
- Caller decides how to handle errors
- No crashes from bad input

---

### Step 4: UI Functions

```kotlin
fun displayMenu() {
    println("\n╔════════════════════════════════╗")
    println("║      KOTLIN CALCULATOR         ║")
    println("╠════════════════════════════════╣")
    println("║  1. Addition (+)               ║")
    println("║  2. Subtraction (-)            ║")
    println("║  3. Multiplication (*)         ║")
    println("║  4. Division (/)               ║")
    println("║  5. Modulus (%)                ║")
    println("║  6. View History               ║")
    println("║  7. Clear History              ║")
    println("║  8. Exit                       ║")
    println("╚════════════════════════════════╝")
}

fun displayHistory(history: List<Calculation>) {
    println("\n=== Calculation History ===")
    if (history.isEmpty()) {
        println("No calculations yet.")
    } else {
        history.forEachIndexed { index, calc ->
            println("${index + 1}. $calc")
        }
    }
}

fun displayResult(result: Double) {
    println("\nResult: ${"%.2f".format(result)}")
}
```

**Design choices**:
- Clean, professional-looking menu
- Box drawing for visual appeal
- Clear section headers
- Formatted output

---

### Step 5: Operation Handler

```kotlin
fun performOperation(
    operation: Int,
    history: MutableList<Calculation>
): Boolean {
    // Get numbers
    val num1 = getNumber("Enter first number: ")
    if (num1 == null) {
        println("Invalid number!")
        return true  // Continue running
    }

    val num2 = getNumber("Enter second number: ")
    if (num2 == null) {
        println("Invalid number!")
        return true
    }

    // Perform calculation
    val result: Double?
    val opSymbol: String

    when (operation) {
        1 -> {
            opSymbol = "+"
            result = add(num1, num2)
        }
        2 -> {
            opSymbol = "-"
            result = subtract(num1, num2)
        }
        3 -> {
            opSymbol = "*"
            result = multiply(num1, num2)
        }
        4 -> {
            opSymbol = "/"
            result = divide(num1, num2)
        }
        5 -> {
            opSymbol = "%"
            result = modulus(num1, num2)
        }
        else -> {
            println("Invalid operation!")
            return true
        }
    }

    // Display and store result
    if (result != null) {
        displayResult(result)
        history.add(Calculation(opSymbol, num1, num2, result))
    }

    return true  // Continue running
}
```

---

### Step 6: Main Program Loop

```kotlin
fun main() {
    val history = mutableListOf<Calculation>()
    var running = true

    println("Welcome to Kotlin Calculator!")

    while (running) {
        displayMenu()

        val choice = getMenuChoice()

        if (choice == null) {
            println("Invalid input! Please enter a number.")
            continue
        }

        when (choice) {
            in 1..5 -> {
                performOperation(choice, history)
            }
            6 -> {
                displayHistory(history)
            }
            7 -> {
                history.clear()
                println("History cleared!")
            }
            8 -> {
                println("\nThank you for using Kotlin Calculator!")
                println("Goodbye!")
                running = false
            }
            else -> {
                println("Invalid choice! Please select 1-8.")
            }
        }
    }
}
```

---

## Complete Solution

Here's the full calculator application:

```kotlin
// ========================================
// Data Models
// ========================================

data class Calculation(
    val operation: String,
    val num1: Double,
    val num2: Double,
    val result: Double
) {
    override fun toString(): String {
        return "$num1 $operation $num2 = $result"
    }
}

// ========================================
// Core Calculation Functions
// ========================================

fun add(a: Double, b: Double): Double = a + b

fun subtract(a: Double, b: Double): Double = a - b

fun multiply(a: Double, b: Double): Double = a * b

fun divide(a: Double, b: Double): Double? {
    if (b == 0.0) {
        println("Error: Cannot divide by zero!")
        return null
    }
    return a / b
}

fun modulus(a: Double, b: Double): Double? {
    if (b == 0.0) {
        println("Error: Cannot calculate modulus with zero!")
        return null
    }
    return a % b
}

// ========================================
// Input Functions
// ========================================

fun getNumber(prompt: String): Double? {
    print(prompt)
    val input = readln()
    return input.toDoubleOrNull()
}

fun getMenuChoice(): Int? {
    print("Enter your choice: ")
    val input = readln()
    return input.toIntOrNull()
}

// ========================================
// UI Functions
// ========================================

fun displayMenu() {
    println("\n╔════════════════════════════════╗")
    println("║      KOTLIN CALCULATOR         ║")
    println("╠════════════════════════════════╣")
    println("║  1. Addition (+)               ║")
    println("║  2. Subtraction (-)            ║")
    println("║  3. Multiplication (*)         ║")
    println("║  4. Division (/)               ║")
    println("║  5. Modulus (%)                ║")
    println("║  6. View History               ║")
    println("║  7. Clear History              ║")
    println("║  8. Exit                       ║")
    println("╚════════════════════════════════╝")
}

fun displayHistory(history: List<Calculation>) {
    println("\n=== Calculation History ===")
    if (history.isEmpty()) {
        println("No calculations yet.")
    } else {
        history.forEachIndexed { index, calc ->
            println("${index + 1}. $calc")
        }
    }
}

fun displayResult(result: Double) {
    println("\nResult: ${"%.2f".format(result)}")
}

// ========================================
// Operation Handler
// ========================================

fun performOperation(
    operation: Int,
    history: MutableList<Calculation>
): Boolean {
    val num1 = getNumber("Enter first number: ")
    if (num1 == null) {
        println("Invalid number!")
        return true
    }

    val num2 = getNumber("Enter second number: ")
    if (num2 == null) {
        println("Invalid number!")
        return true
    }

    val result: Double?
    val opSymbol: String

    when (operation) {
        1 -> {
            opSymbol = "+"
            result = add(num1, num2)
        }
        2 -> {
            opSymbol = "-"
            result = subtract(num1, num2)
        }
        3 -> {
            opSymbol = "*"
            result = multiply(num1, num2)
        }
        4 -> {
            opSymbol = "/"
            result = divide(num1, num2)
        }
        5 -> {
            opSymbol = "%"
            result = modulus(num1, num2)
        }
        else -> {
            println("Invalid operation!")
            return true
        }
    }

    if (result != null) {
        displayResult(result)
        history.add(Calculation(opSymbol, num1, num2, result))
    }

    return true
}

// ========================================
// Main Program
// ========================================

fun main() {
    val history = mutableListOf<Calculation>()
    var running = true

    println("Welcome to Kotlin Calculator!")

    while (running) {
        displayMenu()

        val choice = getMenuChoice()

        if (choice == null) {
            println("Invalid input! Please enter a number.")
            continue
        }

        when (choice) {
            in 1..5 -> {
                performOperation(choice, history)
            }
            6 -> {
                displayHistory(history)
            }
            7 -> {
                history.clear()
                println("History cleared!")
            }
            8 -> {
                println("\nThank you for using Kotlin Calculator!")
                println("Goodbye!")
                running = false
            }
            else -> {
                println("Invalid choice! Please select 1-8.")
            }
        }
    }
}
```

---

## Sample Output

```
Welcome to Kotlin Calculator!

╔════════════════════════════════╗
║      KOTLIN CALCULATOR         ║
╠════════════════════════════════╣
║  1. Addition (+)               ║
║  2. Subtraction (-)            ║
║  3. Multiplication (*)         ║
║  4. Division (/)               ║
║  5. Modulus (%)                ║
║  6. View History               ║
║  7. Clear History              ║
║  8. Exit                       ║
╚════════════════════════════════╝
Enter your choice: 1
Enter first number: 15
Enter second number: 7

Result: 22.00

╔════════════════════════════════╗
...
Enter your choice: 3
Enter first number: 8
Enter second number: 4

Result: 32.00

╔════════════════════════════════╗
...
Enter your choice: 6

=== Calculation History ===
1. 15.0 + 7.0 = 22.0
2. 8.0 * 4.0 = 32.0

╔════════════════════════════════╗
...
Enter your choice: 4
Enter first number: 10
Enter second number: 0
Error: Cannot divide by zero!

╔════════════════════════════════╗
...
Enter your choice: 8

Thank you for using Kotlin Calculator!
Goodbye!
```

---

## What You've Demonstrated

✅ **Variables**: Storing calculation history, user input, results
✅ **Data Types**: Int, Double, String, Boolean
✅ **Control Flow**: while loops, when expressions, if-else
✅ **Functions**: Organized, single-purpose functions
✅ **Collections**: MutableList for history
✅ **Null Safety**: Safe input handling, nullable return types
✅ **Error Handling**: Division by zero, invalid input
✅ **Code Organization**: Clean structure, readable code

---

## Extension Challenges

Ready for more? Try adding these features:

### Challenge 1: Scientific Operations

Add these operations:
- Power (x^y)
- Square root
- Absolute value

```kotlin
fun power(base: Double, exponent: Double): Double {
    return base.pow(exponent)
}

fun squareRoot(n: Double): Double? {
    if (n < 0) {
        println("Error: Cannot calculate square root of negative number!")
        return null
    }
    return sqrt(n)
}

fun absoluteValue(n: Double): Double = abs(n)
```

### Challenge 2: Memory Functions

Add calculator memory (M+, M-, MR, MC):

```kotlin
var memory: Double = 0.0

fun memoryAdd(value: Double) {
    memory += value
    println("Added to memory. Memory = $memory")
}

fun memorySubtract(value: Double) {
    memory -= value
    println("Subtracted from memory. Memory = $memory")
}

fun memoryRecall(): Double {
    println("Memory recalled: $memory")
    return memory
}

fun memoryClear() {
    memory = 0.0
    println("Memory cleared")
}
```

### Challenge 3: Save/Load History

Save history to a file:

```kotlin
fun saveHistory(history: List<Calculation>, filename: String) {
    File(filename).writeText(history.joinToString("\n"))
    println("History saved to $filename")
}

fun loadHistory(filename: String): List<String> {
    return if (File(filename).exists()) {
        File(filename).readLines()
    } else {
        emptyList()
    }
}
```

### Challenge 4: Expression Evaluator

Parse and evaluate expressions like "2 + 3 * 4":

```kotlin
fun evaluateExpression(expression: String): Double? {
    // Parse expression
    // Handle order of operations
    // Return result
}
```

### Challenge 5: Unit Converter

Add unit conversion:
- Temperature (C ↔ F ↔ K)
- Length (m ↔ ft ↔ in)
- Weight (kg ↔ lb)

### Challenge 6: Percentage Calculations

Add percentage operations:
- What is 15% of 200?
- What percentage is 30 of 150?
- Increase/decrease by percentage

---

## Code Quality Review

Let's review what makes this code high-quality:

### 1. Single Responsibility Principle

Each function does ONE thing:
```kotlin
fun add(a: Double, b: Double): Double = a + b  // Only adds
fun displayMenu()  // Only displays menu
fun getNumber(prompt: String): Double?  // Only gets number
```

### 2. Descriptive Names

Names clearly indicate purpose:
```kotlin
fun performOperation(...)  // Clear what it does
val history = mutableListOf<Calculation>()  // Clear what it stores
```

### 3. Error Handling

Graceful error handling throughout:
```kotlin
fun divide(a: Double, b: Double): Double? {
    if (b == 0.0) {
        println("Error: Cannot divide by zero!")
        return null  // Explicit error handling
    }
    return a / b
}
```

### 4. Null Safety

Proper use of nullable types:
```kotlin
val num1 = getNumber("Enter first number: ")
if (num1 == null) {
    println("Invalid number!")
    return true
}
// num1 is smart-cast to Double here
```

### 5. Code Organization

Clear sections and structure:
- Data models first
- Core functions
- UI functions
- Main program

### 6. User Experience

Professional, helpful interface:
- Clear menu
- Helpful error messages
- Confirmation messages
- Nice formatting

---

## Testing Your Calculator

Try these test cases:

**Basic Operations**:
- 10 + 5 = 15
- 20 - 8 = 12
- 6 * 7 = 42
- 100 / 4 = 25
- 17 % 5 = 2

**Edge Cases**:
- 10 / 0 → Error message
- abc (invalid input) → Error message
- -5 + 3 = -2 (negative numbers)
- 0.5 * 0.5 = 0.25 (decimals)

**User Flow**:
1. Perform several calculations
2. View history → See all calculations
3. Clear history
4. View history → "No calculations yet"
5. Exit → Goodbye message

---

## Congratulations!

You've built a complete, professional calculator application! This project demonstrates:

✅ Real-world application structure
✅ Professional error handling
✅ Clean, maintainable code
✅ All Part 1 concepts in practice
✅ User-friendly interface
✅ Production-ready quality

---

## What's Next?

You've completed **Part 1: Kotlin Fundamentals**! You now have a solid foundation in:
- Variables and data types
- Control flow
- Functions
- Collections
- Null safety

In **Part 2: Object-Oriented Programming**, you'll learn:
- Classes and objects
- Inheritance and interfaces
- Data classes
- Object declarations
- Companion objects
- And much more!

---

## Final Reflection

Take a moment to appreciate your progress:

**Lesson 1.1**: You wrote "Hello, World!"
**Lesson 1.7**: You built a complete calculator with error handling, history, and professional UI!

That's incredible growth! Keep building, keep learning, and most importantly—have fun with Kotlin!

---

**Congratulations on completing Part 1 of the Kotlin Training Course!**

You're well on your way to becoming a skilled Kotlin developer. The journey continues in Part 2!

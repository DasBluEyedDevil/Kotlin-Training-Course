# Lesson 2.1: Making Decisions (If Statements)

## Programs That Think

Welcome to Part 2! You've mastered the basics - now it's time to make your programs **intelligent**. They'll be able to make decisions and respond differently based on conditions!

**Analogy:** Think of a traffic light:
- **IF** the light is red → STOP
- **IF** the light is yellow → SLOW DOWN
- **IF** the light is green → GO

The light doesn't always do the same thing - it makes decisions based on its current state. Your programs can do the same!

---

## What is Conditional Logic?

**Conditional logic** means your program can choose different paths based on whether something is true or false.

**Real-world example:** A thermostat
```
IF temperature < 68°F
    Turn on heater
OTHERWISE
    Turn off heater
```

---

## The `if` Statement

In Kotlin, we use the **`if`** statement to make decisions:

```kotlin
fun main() {
    val temperature = 65

    if (temperature < 68) {
        println("It's cold! Turning on heater.")
    }
}
```

**Output:**
```
It's cold! Turning on heater.
```

### Breaking It Down:

```kotlin
if (temperature < 68) {
    // This code runs ONLY if the condition is true
}
```

- **`if`** = Keyword for conditional statement
- **`(temperature < 68)`** = The condition (a Boolean expression)
- **`{ }`** = Code block that runs if condition is true

---

## Comparison Operators

To compare values, use these operators:

| Operator | Meaning | Example | Result |
|----------|---------|---------|--------|
| `==` | Equal to | `5 == 5` | true |
| `!=` | Not equal to | `5 != 3` | true |
| `<` | Less than | `3 < 5` | true |
| `>` | Greater than | `5 > 3` | true |
| `<=` | Less than or equal | `5 <= 5` | true |
| `>=` | Greater than or equal | `5 >= 3` | true |

**Important:** Use `==` (two equals) for comparison, not `=` (assignment)!

---

## Examples

### Example 1: Age Check

```kotlin
fun main() {
    val age = 16

    if (age >= 18) {
        println("You are an adult.")
    }

    if (age < 18) {
        println("You are a minor.")
    }
}
```

**Output:**
```
You are a minor.
```

---

### Example 2: Password Check

```kotlin
fun main() {
    val password = "secret123"

    if (password == "secret123") {
        println("Access granted!")
    }

    if (password != "secret123") {
        println("Access denied!")
    }
}
```

**Output:**
```
Access granted!
```

---

## The `else` Clause

Instead of writing two separate `if` statements, use **`else`** for "otherwise":

```kotlin
fun main() {
    val age = 16

    if (age >= 18) {
        println("You are an adult.")
    } else {
        println("You are a minor.")
    }
}
```

**Much cleaner!**

**Analogy:**
- `if` = "If this is true, do this"
- `else` = "Otherwise, do that"

---

## The `else if` Chain

What if you have more than two options?

```kotlin
fun main() {
    val score = 85

    if (score >= 90) {
        println("Grade: A")
    } else if (score >= 80) {
        println("Grade: B")
    } else if (score >= 70) {
        println("Grade: C")
    } else if (score >= 60) {
        println("Grade: D")
    } else {
        println("Grade: F")
    }
}
```

**Output:**
```
Grade: B
```

**How it works:**
1. Check first condition (score >= 90)? No, skip
2. Check second condition (score >= 80)? Yes! Execute and stop
3. Don't check remaining conditions

---

## Boolean Variables in Conditions

Remember Boolean variables? They're perfect for `if` statements!

```kotlin
fun main() {
    val isRaining = true
    val hasUmbrella = false

    if (isRaining) {
        println("It's raining!")

        if (hasUmbrella) {
            println("Good thing you have an umbrella.")
        } else {
            println("You'll get wet!")
        }
    }
}
```

**Output:**
```
It's raining!
You'll get wet!
```

---

## Interactive Coding Session

### Challenge 1: Temperature Advisor

Write a program that:
1. Asks the user for the temperature
2. If temp >= 80, print "It's hot! Stay hydrated."
3. If temp < 80 but >= 60, print "Nice weather!"
4. Otherwise, print "It's cold! Bundle up."

**Example:**
```kotlin
fun main() {
    print("Enter temperature: ")
    val temp = readLine()!!.toInt()

    if (temp >= 80) {
        println("It's hot! Stay hydrated.")
    } else if (temp >= 60) {
        println("Nice weather!")
    } else {
        println("It's cold! Bundle up.")
    }
}
```

---

### Challenge 2: Even or Odd

Write a program that:
1. Asks for a number
2. Checks if it's even (divisible by 2)
3. Prints "Even" or "Odd"

**Hint:** Use the modulo operator `%` to check remainder:
- `number % 2 == 0` means even
- `number % 2 != 0` means odd

**Example:**
```kotlin
fun main() {
    print("Enter a number: ")
    val num = readLine()!!.toInt()

    if (num % 2 == 0) {
        println("$num is even")
    } else {
        println("$num is odd")
    }
}
```

---

### Challenge 3: Login System

Create a simple login system:
1. Ask for username and password
2. Check if username == "admin" AND password == "1234"
3. Print "Login successful" or "Login failed"

**Example:**
```kotlin
fun main() {
    print("Username: ")
    val username = readLine()

    print("Password: ")
    val password = readLine()

    if (username == "admin" && password == "1234") {
        println("Login successful!")
    } else {
        println("Login failed!")
    }
}
```

*(We'll learn about `&&` in the next lesson!)*

---

## Common Mistakes

### Mistake 1: Using `=` Instead of `==`

❌ **Wrong:**
```kotlin
if (age = 18) {  // ERROR! This tries to assign, not compare
    println("You are 18")
}
```

✅ **Correct:**
```kotlin
if (age == 18) {
    println("You are 18")
}
```

---

### Mistake 2: Forgetting Curly Braces

For single statements, braces are optional, but it's safer to always use them:

```kotlin
// Works but risky
if (age >= 18)
    println("Adult")

// Better - always use braces
if (age >= 18) {
    println("Adult")
}
```

---

### Mistake 3: Semicolons After Conditions

❌ **Wrong:**
```kotlin
if (age >= 18); {  // Semicolon breaks it!
    println("Adult")
}
```

✅ **Correct:**
```kotlin
if (age >= 18) {
    println("Adult")
}
```

---

## `if` as an Expression (Bonus!)

In Kotlin, `if` can return a value:

```kotlin
fun main() {
    val age = 20
    val status = if (age >= 18) "Adult" else "Minor"

    println("Status: $status")
}
```

**Output:**
```
Status: Adult
```

This is unique to Kotlin and very useful!

---

## Recap: What You've Learned

You now understand:

1. **Conditional logic** = Programs that make decisions
2. **`if` statements** = Execute code when a condition is true
3. **Comparison operators** = `==`, `!=`, `<`, `>`, `<=`, `>=`
4. **`else`** = The "otherwise" clause
5. **`else if`** = Multiple conditions in sequence
6. **`if` as an expression** = Can return values in Kotlin

---

## What's Next?

You can now make simple decisions. In the next lesson, we'll learn about **logical operators** (`&&`, `||`, `!`) to combine multiple conditions and make even smarter decisions!

**Key Takeaways:**
- `if` statements let programs make choices
- Use `==` for comparison, not `=`
- `else` handles the "otherwise" case
- Chain multiple conditions with `else if`
- Kotlin's `if` can return values

---

Great work! Mark this complete and continue to the next lesson!

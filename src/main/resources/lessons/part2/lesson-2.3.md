# Lesson 2.3: The When Expression (Better than If-Else)

## A Cleaner Way to Handle Multiple Choices

Imagine you're building a menu system with 10 options. Using `if-else if` chains gets messy fast! Kotlin has a better solution: **`when`**.

**Analogy:** Think of a restaurant menu:
- IF you order #1 → Burger
- IF you order #2 → Pizza
- IF you order #3 → Salad
- ...and so on

Instead of many `if` statements, `when` is like looking at a menu board - one clean list of all options!

---

## Basic `when` Expression

```kotlin
fun main() {
    val menuChoice = 2

    when (menuChoice) {
        1 -> println("You ordered a Burger")
        2 -> println("You ordered Pizza")
        3 -> println("You ordered a Salad")
        else -> println("Invalid choice")
    }
}
```

**Output:**
```
You ordered Pizza
```

### Breaking It Down:

- **`when (menuChoice)`** = Check this value
- **`1 ->` ** = If it's 1, do this
- **`else ->`** = Default case (like final `else` in if-else)

---

## Comparing to If-Else

### With If-Else (Verbose):

```kotlin
val grade = 'B'

if (grade == 'A') {
    println("Excellent!")
} else if (grade == 'B') {
    println("Good job!")
} else if (grade == 'C') {
    println("Satisfactory")
} else if (grade == 'D') {
    println("Needs improvement")
} else {
    println("Failed")
}
```

### With When (Clean!):

```kotlin
val grade = 'B'

when (grade) {
    'A' -> println("Excellent!")
    'B' -> println("Good job!")
    'C' -> println("Satisfactory")
    'D' -> println("Needs improvement")
    else -> println("Failed")
}
```

**Much cleaner and easier to read!**

---

## When with Multiple Values

You can match multiple values in one branch:

```kotlin
fun main() {
    val day = "Saturday"

    when (day) {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> {
            println("It's a weekday 😴")
        }
        "Saturday", "Sunday" -> {
            println("It's the weekend! 🎉")
        }
        else -> {
            println("Invalid day")
        }
    }
}
```

**Output:**
```
It's the weekend! 🎉
```

---

## When with Ranges

Check if a value falls within a range:

```kotlin
fun main() {
    val score = 85

    when (score) {
        in 90..100 -> println("Grade: A")
        in 80..89 -> println("Grade: B")
        in 70..79 -> println("Grade: C")
        in 60..69 -> println("Grade: D")
        else -> println("Grade: F")
    }
}
```

**Output:**
```
Grade: B
```

**`in 80..89`** checks if score is between 80 and 89 (inclusive).

---

## When with Conditions

You can use `when` without an argument for complex conditions:

```kotlin
fun main() {
    val age = 25
    val hasLicense = true

    when {
        age < 16 -> println("Too young to drive")
        age >= 16 && !hasLicense -> println("Need a license first")
        age >= 16 && hasLicense -> println("You can drive!")
        else -> println("Unknown case")
    }
}
```

**Output:**
```
You can drive!
```

This is like `if-else if` but cleaner!

---

## When as an Expression

Like `if`, `when` can return a value:

```kotlin
fun main() {
    val number = 7

    val description = when {
        number % 2 == 0 -> "even"
        number % 2 != 0 -> "odd"
        else -> "unknown"
    }

    println("$number is $description")
}
```

**Output:**
```
7 is odd
```

---

## When with Types (Preview)

You can even check types (we'll learn this more in OOP):

```kotlin
fun describe(obj: Any): String {
    return when (obj) {
        is String -> "It's a string: $obj"
        is Int -> "It's a number: $obj"
        is Boolean -> "It's a boolean: $obj"
        else -> "Unknown type"
    }
}
```

---

## Interactive Coding Session

### Challenge 1: Day of Week Planner

Create a program that takes a day number (1-7) and prints what you do that day:

```kotlin
fun main() {
    print("Enter day (1-7): ")
    val day = readLine()!!.toInt()

    when (day) {
        1 -> println("Monday: Work")
        2 -> println("Tuesday: Work")
        3 -> println("Wednesday: Work")
        4 -> println("Thursday: Work")
        5 -> println("Friday: Work")
        6 -> println("Saturday: Relax!")
        7 -> println("Sunday: Relax!")
        else -> println("Invalid day number")
    }
}
```

**Improve it** using multiple values:

```kotlin
when (day) {
    in 1..5 -> println("Weekday: Work")
    6, 7 -> println("Weekend: Relax!")
    else -> println("Invalid day")
}
```

---

### Challenge 2: Simple Calculator

Build a calculator using `when`:

```kotlin
fun main() {
    print("Enter first number: ")
    val num1 = readLine()!!.toDouble()

    print("Enter operator (+, -, *, /): ")
    val operator = readLine()!!

    print("Enter second number: ")
    val num2 = readLine()!!.toDouble()

    val result = when (operator) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "*" -> num1 * num2
        "/" -> if (num2 != 0.0) num1 / num2 else Double.NaN
        else -> {
            println("Invalid operator")
            Double.NaN
        }
    }

    if (!result.isNaN()) {
        println("Result: $result")
    }
}
```

---

### Challenge 3: Traffic Light Simulator

```kotlin
fun main() {
    print("Light color (red/yellow/green): ")
    val light = readLine()!!.lowercase()

    when (light) {
        "red" -> println("🛑 STOP")
        "yellow" -> println("⚠️ SLOW DOWN")
        "green" -> println("✅ GO")
        else -> println("Invalid color")
    }
}
```

---

## When vs If-Else: When to Use Which?

**Use `when` when:**
- Checking one variable against many values
- You have 3+ branches
- You want cleaner, more readable code

**Use `if-else` when:**
- Only 1-2 conditions
- Complex boolean expressions
- Conditions are unrelated

---

## Exhaustive When (Advanced)

When used as an expression, Kotlin ensures all cases are covered:

```kotlin
val grade = 'B'

val message = when (grade) {
    'A' -> "Excellent"
    'B' -> "Good"
    'C' -> "OK"
    // If you remove 'else', Kotlin shows error
    else -> "Unknown"
}
```

This prevents bugs!

---

## Recap: What You've Learned

You now understand:

1. **`when`** = Cleaner alternative to if-else chains
2. **Multiple values** = `1, 2, 3 -> ...`
3. **Ranges** = `in 1..10 -> ...`
4. **Conditions** = `when { condition -> ... }`
5. **As expression** = Returns values
6. **Exhaustive checking** = Kotlin ensures all cases covered

---

## What's Next?

You've mastered decision-making! Next, we'll learn about **loops** - making your program repeat tasks automatically. No more copy-pasting code 100 times!

**Key Takeaways:**
- `when` is more readable than long if-else chains
- Can match multiple values, ranges, or conditions
- Can return values like `if`
- Kotlin ensures you don't miss cases
- Use `when` for 3+ options, `if` for 1-2

---

Great progress! Mark complete and continue to loops!

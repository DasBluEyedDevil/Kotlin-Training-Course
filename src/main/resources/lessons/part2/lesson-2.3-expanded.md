# Lesson 2.3: The When Expression - Elegant Multi-Way Decisions

**Estimated Time**: 60 minutes
**Difficulty**: Beginner
**Prerequisites**: Lesson 2.1 (If statements), Lesson 2.2 (Logical operators)

---

## Topic Introduction

You've learned how to make decisions with `if-else` statements and combine conditions with logical operators. But what happens when you need to check many different possibilities? Imagine writing a program that converts day numbers to day names, or grades to letter marks. Using `if-else` chains becomes verbose and hard to read.

Enter Kotlin's `when` expression—an elegant, powerful alternative that makes multi-way decisions clean and expressive. Think of it as a sophisticated switchboard operator, efficiently routing your program to the right destination based on various conditions.

In this lesson, you'll learn:
- What the `when` expression is and when to use it
- How to match against specific values
- Using `when` with ranges and complex conditions
- The power of `when` as an expression
- Pattern matching and smart casts
- Best practices for clean, maintainable code

By the end, you'll be able to write elegant decision logic that's both powerful and easy to understand!

---

## The Concept: When as a Switchboard

### Real-World Analogy: The Hotel Concierge

Imagine a hotel concierge helping guests:

```
Guest: "What should I do on a rainy day?"
Concierge checks the weather:
  → Rain: "Visit the museum"
  → Snow: "Go skiing"
  → Sunny: "Beach day!"
  → Cloudy: "Perfect for hiking"
  → Otherwise: "Enjoy the hotel spa"
```

The concierge efficiently routes to one answer based on the weather. That's exactly what `when` does—it evaluates an expression once and routes to the matching branch.

### The if-else-if Problem

Let's see why we need `when`. Here's a day-of-week converter using if-else:

```kotlin
val dayNumber = 3
val dayName: String

if (dayNumber == 1) {
    dayName = "Monday"
} else if (dayNumber == 2) {
    dayName = "Tuesday"
} else if (dayNumber == 3) {
    dayName = "Wednesday"
} else if (dayNumber == 4) {
    dayName = "Thursday"
} else if (dayNumber == 5) {
    dayName = "Friday"
} else if (dayNumber == 6) {
    dayName = "Saturday"
} else if (dayNumber == 7) {
    dayName = "Sunday"
} else {
    dayName = "Invalid day"
}
```

This works, but it's:
- **Repetitive**: `dayNumber ==` appears 7 times
- **Verbose**: 19 lines for a simple mapping
- **Error-prone**: Easy to make mistakes in long chains

**The same logic with `when`:**

```kotlin
val dayNumber = 3
val dayName = when (dayNumber) {
    1 -> "Monday"
    2 -> "Tuesday"
    3 -> "Wednesday"
    4 -> "Thursday"
    5 -> "Friday"
    6 -> "Saturday"
    7 -> "Sunday"
    else -> "Invalid day"
}
```

Only 10 lines! Clean, readable, and elegant.

---

## Basic When Expression

### Syntax and Structure

```kotlin
when (expression) {
    value1 -> result1
    value2 -> result2
    value3 -> result3
    else -> defaultResult
}
```

**Parts:**
- `when` - Keyword starting the expression
- `(expression)` - The value to match against
- `value ->` - Match condition followed by arrow
- `result` - What to return/execute when matched
- `else` - Default case (like the final "otherwise")

### Your First When Expression

```kotlin
fun main() {
    val trafficLight = "Red"

    val action = when (trafficLight) {
        "Red" -> "Stop"
        "Yellow" -> "Slow down"
        "Green" -> "Go"
        else -> "Invalid light color"
    }

    println("Traffic light is $trafficLight: $action")
}
```

**Output:**
```
Traffic light is Red: Stop
```

**How it works:**
1. Evaluate the expression: `trafficLight` = "Red"
2. Check each branch from top to bottom
3. Find match: `"Red"` matches first branch
4. Return result: `"Stop"`
5. Assign to `action` variable
6. Skip remaining branches

---

## When with Multiple Values

You can match multiple values in one branch using commas:

```kotlin
fun main() {
    val month = "December"

    val season = when (month) {
        "December", "January", "February" -> "Winter"
        "March", "April", "May" -> "Spring"
        "June", "July", "August" -> "Summer"
        "September", "October", "November" -> "Fall"
        else -> "Unknown month"
    }

    println("$month is in $season")
}
```

**Output:**
```
December is in Winter
```

This is much cleaner than:
```kotlin
// Verbose alternative
if (month == "December" || month == "January" || month == "February") {
    season = "Winter"
}
// ... etc
```

### Practical Example: Weekend Checker

```kotlin
fun main() {
    val day = "Saturday"

    val dayType = when (day) {
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> "Weekday"
        "Saturday", "Sunday" -> "Weekend"
        else -> "Invalid day"
    }

    println("$day is a $dayType")

    if (dayType == "Weekend") {
        println("Time to relax!")
    } else {
        println("Time to work!")
    }
}
```

**Output:**
```
Saturday is a Weekend
Time to relax!
```

---

## When with Ranges

One of `when`'s superpowers is matching against ranges using the `in` keyword:

```kotlin
fun main() {
    val score = 85

    val grade = when (score) {
        in 90..100 -> "A"
        in 80..89 -> "B"
        in 70..79 -> "C"
        in 60..69 -> "D"
        in 0..59 -> "F"
        else -> "Invalid score"
    }

    println("Score: $score, Grade: $grade")
}
```

**Output:**
```
Score: 85, Grade: B
```

### How Ranges Work

**Range syntax:**
- `0..10` - Includes both 0 and 10 (closed range)
- `in range` - Checks if value is within the range

**Examples:**
```kotlin
val age = 25

when (age) {
    in 0..12 -> println("Child")
    in 13..19 -> println("Teenager")
    in 20..64 -> println("Adult")
    in 65..120 -> println("Senior")
    else -> println("Invalid age")
}
// Output: Adult
```

### Temperature Advisory System

```kotlin
fun main() {
    val temperature = 75

    val advice = when (temperature) {
        in Int.MIN_VALUE..32 -> "Freezing! Bundle up!"
        in 33..50 -> "Cold - wear a jacket"
        in 51..70 -> "Cool and comfortable"
        in 71..85 -> "Warm and pleasant"
        in 86..95 -> "Hot - stay hydrated"
        in 96..Int.MAX_VALUE -> "Extreme heat warning!"
        else -> "Invalid temperature"
    }

    println("Temperature: ${temperature}°F")
    println(advice)
}
```

**Output:**
```
Temperature: 75°F
Warm and pleasant
```

---

## When with Conditions (No Argument)

You can use `when` without an argument to write complex conditions:

```kotlin
fun main() {
    val age = 25
    val hasLicense = true
    val hasInsurance = true

    val canDrive = when {
        age < 16 -> false
        age >= 16 && hasLicense && hasInsurance -> true
        age >= 16 && hasLicense -> {
            println("Warning: No insurance!")
            true
        }
        else -> false
    }

    println("Can drive: $canDrive")
}
```

**Output:**
```
Can drive: true
```

**This form is perfect when:**
- Conditions are complex
- You're checking different variables
- Conditions don't follow a simple pattern

### Example: Shipping Cost Calculator

```kotlin
fun main() {
    val weight = 15.0  // pounds
    val distance = 500  // miles
    val isPrime = true

    val shippingCost = when {
        isPrime && weight < 20 -> 0.0  // Free for Prime under 20 lbs
        weight < 5 -> 5.99
        weight < 10 -> 9.99
        weight < 20 -> 14.99
        distance > 1000 -> weight * 2.0
        else -> weight * 1.5
    }

    println("Weight: $weight lbs, Distance: $distance miles")
    println("Shipping cost: $$shippingCost")
}
```

**Output:**
```
Weight: 15.0 lbs, Distance: 500 miles
Shipping cost: $0.0
```

---

## When as a Statement vs Expression

### When as Expression (Returns a Value)

```kotlin
// Must have else to ensure a value is always returned
val result = when (x) {
    1 -> "One"
    2 -> "Two"
    else -> "Other"
}
```

### When as Statement (Just Executes Code)

```kotlin
// No need for else when not returning a value
when (x) {
    1 -> println("One")
    2 -> println("Two")
}
// If x is 3, nothing happens
```

### Complete Example

```kotlin
fun main() {
    val userAction = "login"

    // When as statement - performs actions
    when (userAction) {
        "login" -> {
            println("Checking credentials...")
            println("Welcome back!")
        }
        "logout" -> {
            println("Saving session...")
            println("Goodbye!")
        }
        "register" -> {
            println("Creating new account...")
            println("Registration complete!")
        }
    }

    // When as expression - returns a value
    val message = when (userAction) {
        "login" -> "User logged in"
        "logout" -> "User logged out"
        "register" -> "New user registered"
        else -> "Unknown action"
    }

    println("Log entry: $message")
}
```

**Output:**
```
Checking credentials...
Welcome back!
Log entry: User logged in
```

---

## When with Type Checking (Smart Casts)

Kotlin's `when` can check types and automatically cast variables:

```kotlin
fun describeValue(value: Any): String {
    return when (value) {
        is String -> "Text: '$value' (length: ${value.length})"
        is Int -> "Number: $value (doubled: ${value * 2})"
        is Boolean -> "Boolean: $value (opposite: ${!value})"
        is List<*> -> "List with ${value.size} items"
        else -> "Unknown type: ${value::class.simpleName}"
    }
}

fun main() {
    println(describeValue("Hello"))
    println(describeValue(42))
    println(describeValue(true))
    println(describeValue(listOf(1, 2, 3)))
}
```

**Output:**
```
Text: 'Hello' (length: 5)
Number: 42 (doubled: 84)
Boolean: true (opposite: false)
List with 3 items
```

**Note:** After `is String`, Kotlin knows `value` is a String and lets you use `.length` without casting!

---

## Hands-On Exercises

### Exercise 1: Calculator

**Challenge:** Create a simple calculator using `when` that:
1. Takes two numbers and an operator (+, -, *, /)
2. Performs the operation
3. Returns the result

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val num1 = 10.0
    val num2 = 5.0
    val operator = "/"

    val result = when (operator) {
        "+" -> num1 + num2
        "-" -> num1 - num2
        "*" -> num1 * num2
        "/" -> if (num2 != 0.0) num1 / num2 else Double.NaN
        else -> Double.NaN
    }

    if (result.isNaN()) {
        println("Invalid operation")
    } else {
        println("$num1 $operator $num2 = $result")
    }
}
```

**Output:**
```
10.0 / 5.0 = 2.0
```

**Key concepts:**
- Using `when` for operator selection
- Handling division by zero
- Returning calculated values
</details>

---

### Exercise 2: Movie Rating System

**Challenge:** Create a movie rating system that converts numeric ratings to descriptions:
- 10: "Masterpiece"
- 8-9: "Excellent"
- 6-7: "Good"
- 4-5: "Average"
- 1-3: "Poor"
- 0: "Terrible"

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val rating = 8

    val description = when (rating) {
        10 -> "Masterpiece"
        in 8..9 -> "Excellent"
        in 6..7 -> "Good"
        in 4..5 -> "Average"
        in 1..3 -> "Poor"
        0 -> "Terrible"
        else -> "Invalid rating (must be 0-10)"
    }

    println("Rating: $rating/10 - $description")
}
```

**Output:**
```
Rating: 8/10 - Excellent
```
</details>

---

### Exercise 3: Password Strength Checker

**Challenge:** Create a password strength checker that evaluates based on length:
- Less than 6: "Weak"
- 6-8: "Medium"
- 9-12: "Strong"
- 13+: "Very Strong"

Also check if the password is a common password (use when without argument).

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val password = "MySecurePass123"
    val commonPasswords = listOf("password", "123456", "qwerty")

    val strength = when {
        password in commonPasswords -> "Weak (common password!)"
        password.length < 6 -> "Weak"
        password.length in 6..8 -> "Medium"
        password.length in 9..12 -> "Strong"
        password.length >= 13 -> "Very Strong"
        else -> "Invalid"
    }

    println("Password: $password")
    println("Strength: $strength")
}
```

**Output:**
```
Password: MySecurePass123
Strength: Very Strong
```

**Key concepts:**
- Using when without an argument
- Checking membership with `in`
- Combining multiple conditions
</details>

---

### Exercise 4: BMI Category Calculator

**Challenge:** Calculate BMI category:
- BMI < 18.5: "Underweight"
- BMI 18.5-24.9: "Normal weight"
- BMI 25.0-29.9: "Overweight"
- BMI ≥ 30.0: "Obese"

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val weight = 70.0  // kg
    val height = 1.75   // meters
    val bmi = weight / (height * height)

    val category = when {
        bmi < 18.5 -> "Underweight"
        bmi < 25.0 -> "Normal weight"
        bmi < 30.0 -> "Overweight"
        else -> "Obese"
    }

    println("BMI: %.1f".format(bmi))
    println("Category: $category")
}
```

**Output:**
```
BMI: 22.9
Category: Normal weight
```
</details>

---

## Common Pitfalls and Best Practices

### Pitfall 1: Missing else in Expressions

❌ **Error:**
```kotlin
val result = when (x) {
    1 -> "One"
    2 -> "Two"
    // No else - compiler error if used as expression!
}
```

✅ **Correct:**
```kotlin
val result = when (x) {
    1 -> "One"
    2 -> "Two"
    else -> "Other"
}
```

**Rule:** When used as an expression (returning a value), `else` is required unless the compiler can prove all cases are covered.

### Pitfall 2: Overlapping Ranges

❌ **Problem:**
```kotlin
when (score) {
    in 80..100 -> "Great"
    in 90..100 -> "Excellent"  // Never reached!
    else -> "Keep trying"
}
```

The second range is completely covered by the first. `when` executes the **first** matching branch.

✅ **Correct:**
```kotlin
when (score) {
    in 90..100 -> "Excellent"  // More specific first
    in 80..89 -> "Great"
    else -> "Keep trying"
}
```

### Pitfall 3: Forgetting Braces for Multiple Statements

❌ **Won't compile:**
```kotlin
when (x) {
    1 ->
        println("First line")
        println("Second line")  // Error!
}
```

✅ **Correct:**
```kotlin
when (x) {
    1 -> {
        println("First line")
        println("Second line")
    }
}
```

### Best Practice 1: Order Matters

Put the most specific cases first:

✅ **Good:**
```kotlin
when (value) {
    null -> "Null value"
    "" -> "Empty string"
    "test" -> "Test value"
    else -> "Other: $value"
}
```

### Best Practice 2: Use When for 3+ Options

- **2 options:** Use `if-else`
- **3+ options:** Use `when`

```kotlin
// 2 options - if-else is fine
val type = if (age >= 18) "Adult" else "Minor"

// 3+ options - when is better
val category = when (age) {
    in 0..12 -> "Child"
    in 13..19 -> "Teen"
    else -> "Adult"
}
```

### Best Practice 3: Exhaustive When

For enums and sealed classes, you can make `when` exhaustive without `else`:

```kotlin
enum class Direction { NORTH, SOUTH, EAST, WEST }

fun move(direction: Direction) = when (direction) {
    Direction.NORTH -> "Going north"
    Direction.SOUTH -> "Going south"
    Direction.EAST -> "Going east"
    Direction.WEST -> "Going west"
    // No else needed - all cases covered!
}
```

---

## Quick Quiz

**Question 1:** What will this print?
```kotlin
val x = 5
val result = when (x) {
    in 1..3 -> "Low"
    in 4..6 -> "Medium"
    in 7..10 -> "High"
    else -> "Unknown"
}
println(result)
```

<details>
<summary>Answer</summary>

**Output:** `Medium`

**Explanation:** `5` is in the range `4..6`, so "Medium" is returned.
</details>

---

**Question 2:** Is this valid code?
```kotlin
val day = 3
when (day) {
    1 -> println("Monday")
    2 -> println("Tuesday")
}
```

<details>
<summary>Answer</summary>

**Yes!** This is valid. When used as a **statement** (not returning a value), `else` is optional. If `day = 3`, nothing will print.
</details>

---

**Question 3:** What's wrong with this?
```kotlin
val grade = when (score) {
    in 0..100 -> "Valid"
    in 90..100 -> "A"
}
```

<details>
<summary>Answer</summary>

**Problem:** The second branch (`in 90..100`) will never execute because it's completely covered by the first branch (`in 0..100`). Always put more specific conditions first!

**Fixed:**
```kotlin
val grade = when (score) {
    in 90..100 -> "A"
    in 0..89 -> "Other"
    else -> "Invalid"
}
```
</details>

---

**Question 4:** Can you use `when` with strings?

<details>
<summary>Answer</summary>

**Yes!** `when` works with any type:

```kotlin
val fruit = "apple"
when (fruit) {
    "apple" -> println("Red or green")
    "banana" -> println("Yellow")
    else -> println("Unknown fruit")
}
```
</details>

---

## Summary

Congratulations! You've mastered Kotlin's `when` expression. Let's recap:

**Key Concepts:**
- **When expression** provides elegant multi-way decisions
- **Value matching** checks against specific values
- **Multiple values** can be matched with commas
- **Ranges** use `in` keyword for range checking
- **Conditions** can be complex with argument-less when
- **Type checking** with `is` and smart casts
- **Expression vs statement** - expressions need else

**When Syntax Patterns:**
```kotlin
// Basic value matching
when (x) {
    1 -> "One"
    2, 3 -> "Two or Three"
    else -> "Other"
}

// Range matching
when (score) {
    in 90..100 -> "A"
    in 80..89 -> "B"
    else -> "C or lower"
}

// Condition matching
when {
    x > 10 -> "Large"
    x > 5 -> "Medium"
    else -> "Small"
}
```

**Best Practices:**
- Use `when` for 3+ options
- Put specific cases before general ones
- Always include `else` for expressions
- Use braces for multi-statement branches
- Consider ranges for numeric values

---

## What's Next?

You can now make sophisticated decisions with `when`, but what about repeating tasks? What if you need to print "Hello" 100 times, or process every item in a list?

In **Lesson 2.4: Repeating Tasks - For Loops**, you'll learn:
- How to repeat code with for loops
- Iterating through ranges and collections
- Advanced loop techniques: step, downTo, until
- Practical applications of iteration

**Preview:**
```kotlin
for (i in 1..10) {
    println("Count: $i")
}

for (day in listOf("Mon", "Tue", "Wed")) {
    println("Today is $day")
}
```

---

**Excellent work! You've completed Lesson 2.3. Continue to master loops next!** 🎉

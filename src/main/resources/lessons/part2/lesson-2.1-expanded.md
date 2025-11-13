# Lesson 2.1: Making Decisions - If Statements and Conditional Logic

**Estimated Time**: 60 minutes
**Difficulty**: Beginner
**Prerequisites**: Part 1 (Kotlin fundamentals)

---

## Topic Introduction

Welcome to Part 2: Controlling the Flow! You've mastered Kotlin fundamentals—variables, data types, functions, and basic input/output. Now it's time to make your programs **intelligent** by teaching them to make decisions.

Up until now, your programs have executed line-by-line in a straight path, like following a recipe exactly. But real-world programs need to adapt and respond to different situations. Should you bring an umbrella? **If** it's raining, yes. **Otherwise**, no. That's conditional logic!

In this lesson, you'll learn:
- What conditional logic is and why it's essential
- How to use `if`, `else`, and `else if` statements
- Comparison operators (`==`, `!=`, `<`, `>`, `<=`, `>=`)
- How to make decisions based on Boolean conditions
- Kotlin's unique `if` expression feature
- Common patterns and best practices

By the end, you'll write programs that adapt their behavior based on conditions—the foundation of all intelligent software!

---

## The Concept: Conditional Logic

### Real-World Decision Making

Every day, you make countless decisions based on conditions:

```
IF temperature < 32°F
    Wear a heavy coat
OTHERWISE IF temperature < 60°F
    Wear a light jacket
OTHERWISE
    Wear a t-shirt
```

```
IF you have the key
    Open the door
OTHERWISE
    Ring the doorbell
```

```
IF account balance >= purchase price
    Complete purchase
OTHERWISE
    Show "insufficient funds" error
```

Your brain evaluates conditions and chooses different paths automatically. Programming lets computers do the same!

### The Traffic Light Analogy

Think of a traffic light controlling your program's flow:

- **Red light (condition false)**: Skip this block of code
- **Green light (condition true)**: Execute this block of code
- **Yellow light (else)**: Default path when others are false

Just as traffic lights control the flow of cars, conditional statements control the flow of code execution.

### What Makes a Condition?

A condition is any expression that evaluates to **true** or **false** (a Boolean value):

```kotlin
age >= 18           // true if age is 18 or more
temperature < 32    // true if temperature is less than 32
name == "Alice"     // true if name exactly equals "Alice"
isRaining           // already a Boolean variable
```

The program checks the condition and decides which code to execute based on the result.

---

## The Fundamentals: If Statements

### Basic If Statement

The simplest form of conditional logic is the **if statement**:

```kotlin
fun main() {
    val temperature = 95

    if (temperature > 90) {
        println("It's extremely hot! Stay hydrated.")
    }

    println("Have a great day!")
}
```

**Output:**
```
It's extremely hot! Stay hydrated.
Have a great day!
```

**How it works:**
1. Program evaluates `temperature > 90` → `95 > 90` → `true`
2. Because the condition is true, the code inside the braces `{ }` executes
3. Program continues to the next line after the if statement

**If the temperature was 85:**
```kotlin
val temperature = 85
if (temperature > 90) {
    println("It's extremely hot! Stay hydrated.")  // SKIPPED
}
println("Have a great day!")  // Still executes
```

**Output:**
```
Have a great day!
```

### Anatomy of an If Statement

```kotlin
if (condition) {
    // Code that runs only when condition is true
}
```

**Parts:**
- `if` - Keyword that starts the conditional statement
- `(condition)` - A Boolean expression that evaluates to true or false
- `{ }` - Code block containing statements to execute when true
- Indentation - Makes the code readable (best practice: 4 spaces)

### Multiple Independent If Statements

You can have multiple separate if statements:

```kotlin
fun main() {
    val score = 85

    if (score >= 90) {
        println("Excellent work!")
    }

    if (score >= 80) {
        println("Great job!")
    }

    if (score >= 70) {
        println("Good effort!")
    }
}
```

**Output:**
```
Great job!
Good effort!
```

**Important:** Each if statement is checked independently. If `score = 85`, both the second and third conditions are true, so both messages print.

---

## Comparison Operators

To create conditions, you need to compare values using **comparison operators**:

| Operator | Meaning | Example | Result |
|----------|---------|---------|--------|
| `==` | Equal to | `5 == 5` | `true` |
| `==` | Equal to | `5 == 3` | `false` |
| `!=` | Not equal to | `5 != 3` | `true` |
| `!=` | Not equal to | `5 != 5` | `false` |
| `<` | Less than | `3 < 5` | `true` |
| `<` | Less than | `5 < 3` | `false` |
| `>` | Greater than | `5 > 3` | `true` |
| `<=` | Less than or equal | `5 <= 5` | `true` |
| `>=` | Greater than or equal | `5 >= 3` | `true` |

### Common Comparison Examples

**Numeric comparisons:**
```kotlin
val age = 25
val minimumAge = 18

if (age >= minimumAge) {
    println("Access granted")
}
```

**String comparisons:**
```kotlin
val password = "secret123"

if (password == "secret123") {
    println("Login successful")
}

if (password != "admin") {
    println("You are not an administrator")
}
```

**Boolean comparisons:**
```kotlin
val isLoggedIn = true

if (isLoggedIn == true) {
    println("Welcome back!")
}

// Even better - Boolean variables don't need ==
if (isLoggedIn) {
    println("Welcome back!")
}
```

### Critical Mistake: = vs ==

**The #1 beginner mistake:**

❌ **WRONG:**
```kotlin
if (age = 18) {  // ERROR! This tries to ASSIGN 18 to age
    println("You are 18")
}
```

✅ **CORRECT:**
```kotlin
if (age == 18) {  // This COMPARES age to 18
    println("You are 18")
}
```

**Remember:**
- `=` is for **assignment** (storing a value)
- `==` is for **comparison** (checking equality)

---

## The Else Clause

Often you want to do one thing if a condition is true, and something **different** if it's false. That's where `else` comes in:

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

**Output:**
```
You are a minor.
```

**How it works:**
- If the condition (`age >= 18`) is **true**, execute the first block
- If the condition is **false**, execute the else block
- Exactly ONE of the two blocks will execute, never both

### The Either/Or Pattern

Think of if-else as a fork in the road:

```
          if (condition)
               /  \
             /      \
          true     false
           /          \
       {block1}    {block2}
           \          /
            \        /
              \    /
           (continue)
```

**Real-world example:**
```kotlin
fun main() {
    val hasKey = true

    if (hasKey) {
        println("Opening door with key")
    } else {
        println("Ringing doorbell")
    }

    println("Entering home")
}
```

**Output:**
```
Opening door with key
Entering home
```

---

## Else If: Multiple Conditions

What if you have more than two possibilities? Use **else if** to chain conditions:

```kotlin
fun main() {
    val score = 85

    if (score >= 90) {
        println("Grade: A - Excellent!")
    } else if (score >= 80) {
        println("Grade: B - Great work!")
    } else if (score >= 70) {
        println("Grade: C - Good job!")
    } else if (score >= 60) {
        println("Grade: D - Needs improvement")
    } else {
        println("Grade: F - Please see instructor")
    }
}
```

**Output:**
```
Grade: B - Great work!
```

### How Else If Works

The program checks conditions **in order** from top to bottom:

1. Check first condition (`score >= 90`) → `85 >= 90` → **false**, skip
2. Check second condition (`score >= 80`) → `85 >= 80` → **true**, execute and **STOP**
3. Don't check any remaining conditions

**Critical:** Once a condition is true, the rest are ignored. Order matters!

**Example showing order importance:**

❌ **WRONG ORDER:**
```kotlin
val score = 95

if (score >= 60) {
    println("Grade: D")  // This executes!
} else if (score >= 90) {
    println("Grade: A")  // Never reached
}
```
**Output:** `Grade: D` (Wrong! Should be A)

✅ **CORRECT ORDER:**
```kotlin
val score = 95

if (score >= 90) {
    println("Grade: A")  // This executes!
} else if (score >= 60) {
    println("Grade: D")  // Never reached (but that's okay)
}
```
**Output:** `Grade: A` (Correct!)

**Rule:** Put the most specific conditions first, most general conditions last.

---

## Nested If Statements

You can put if statements inside other if statements:

```kotlin
fun main() {
    val age = 25
    val hasLicense = true

    if (age >= 16) {
        println("You are old enough to drive")

        if (hasLicense) {
            println("You can drive legally!")
        } else {
            println("But you need a license first")
        }
    } else {
        println("You are too young to drive")
    }
}
```

**Output:**
```
You are old enough to drive
You can drive legally!
```

**How it works:**
1. Check outer condition (`age >= 16`) → true, enter outer block
2. Print "You are old enough to drive"
3. Check inner condition (`hasLicense`) → true, execute
4. Print "You can drive legally!"

**Nested if statement pattern:**
```kotlin
if (outerCondition) {
    // Outer block
    if (innerCondition) {
        // Inner block (only reached if BOTH conditions are true)
    }
}
```

**Alternative:** In the next lesson, you'll learn about **logical operators** (`&&`, `||`) which often eliminate the need for nesting.

---

## If as an Expression (Kotlin Special Feature!)

Here's something unique to Kotlin: `if` is not just a statement, it's an **expression** that can return a value!

**Traditional approach (statement):**
```kotlin
val message: String

if (age >= 18) {
    message = "Adult"
} else {
    message = "Minor"
}

println(message)
```

**Kotlin's expression approach:**
```kotlin
val message = if (age >= 18) "Adult" else "Minor"
println(message)
```

Both do the same thing, but the expression form is cleaner and more concise!

### More Expression Examples

**Example 1: Max of two numbers**
```kotlin
fun main() {
    val a = 10
    val b = 20
    val max = if (a > b) a else b

    println("Maximum: $max")  // Output: Maximum: 20
}
```

**Example 2: Fee calculation**
```kotlin
fun main() {
    val age = 12
    val fee = if (age < 18) 5 else 10

    println("Admission fee: $$fee")  // Output: Admission fee: $5
}
```

**Example 3: Multi-line expression blocks**
```kotlin
val result = if (score >= 60) {
    val bonus = 10
    score + bonus  // Last expression is returned
} else {
    score  // Last expression is returned
}
```

**Important:** When using if as an expression, you **must** have an else clause (the expression must always produce a value).

---

## Hands-On Practice

### Exercise 1: Temperature Advisor

**Challenge:** Write a program that:
1. Takes a temperature value
2. Prints different advice based on the temperature:
   - If temp >= 100: "Extreme heat warning! Stay indoors."
   - If temp >= 80: "It's hot! Stay hydrated."
   - If temp >= 60: "Nice weather!"
   - If temp < 60: "It's chilly! Bring a jacket."

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val temperature = 75

    if (temperature >= 100) {
        println("Extreme heat warning! Stay indoors.")
    } else if (temperature >= 80) {
        println("It's hot! Stay hydrated.")
    } else if (temperature >= 60) {
        println("Nice weather!")
    } else {
        println("It's chilly! Bring a jacket.")
    }
}
```

**Output:**
```
Nice weather!
```

**Key concepts:**
- Multiple conditions with else if
- Ordered from most specific to least specific
- Each temperature falls into exactly one category
</details>

---

### Exercise 2: Even or Odd Checker

**Challenge:** Write a program that:
1. Takes a number
2. Checks if it's even or odd
3. Prints the result

**Hint:** Use the modulo operator `%`. A number is even if `number % 2 == 0`.

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val number = 17

    if (number % 2 == 0) {
        println("$number is even")
    } else {
        println("$number is odd")
    }
}
```

**Output:**
```
17 is odd
```

**How it works:**
- `%` (modulo) gives the remainder after division
- `17 % 2` = 1 (remainder when dividing 17 by 2)
- `1 == 0` is false, so else block executes

**Even number example:**
- `18 % 2` = 0
- `0 == 0` is true, so if block executes
</details>

---

### Exercise 3: Login System

**Challenge:** Create a simple login system that:
1. Stores a correct username and password
2. Takes user input for username and password
3. Checks if both match
4. Prints "Login successful" or "Login failed"

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val correctUsername = "admin"
    val correctPassword = "pass123"

    print("Enter username: ")
    val username = readLine()!!

    print("Enter password: ")
    val password = readLine()!!

    if (username == correctUsername && password == correctPassword) {
        println("Login successful! Welcome, $username!")
    } else {
        println("Login failed! Invalid credentials.")
    }
}
```

**Sample run:**
```
Enter username: admin
Enter password: pass123
Login successful! Welcome, admin!
```

**Note:** We're using `&&` (AND operator) which you'll learn more about in the next lesson. For now, understand that both conditions must be true for the if block to execute.
</details>

---

### Exercise 4: Discount Calculator

**Challenge:** Write a program that:
1. Takes a purchase amount
2. Applies discounts based on the amount:
   - $100+: 20% discount
   - $50-$99: 10% discount
   - Under $50: No discount
3. Prints the final price

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val purchaseAmount = 75.0

    val discount = if (purchaseAmount >= 100) {
        0.20
    } else if (purchaseAmount >= 50) {
        0.10
    } else {
        0.0
    }

    val finalPrice = purchaseAmount * (1 - discount)

    println("Original price: $$purchaseAmount")
    println("Discount: ${discount * 100}%")
    println("Final price: $$finalPrice")
}
```

**Output:**
```
Original price: $75.0
Discount: 10.0%
Final price: $67.5
```

**Key concepts:**
- Using if as an expression to calculate the discount
- Storing the result in a variable
- Performing calculations with the result
</details>

---

## Common Pitfalls and Best Practices

### Pitfall 1: Missing Braces

While braces are optional for single statements, **always use them** for clarity:

⚠️ **Risky (works but confusing):**
```kotlin
if (age >= 18)
    println("Adult")
else
    println("Minor")
```

✅ **Better (clear and safe):**
```kotlin
if (age >= 18) {
    println("Adult")
} else {
    println("Minor")
}
```

### Pitfall 2: Semicolons After Conditions

❌ **WRONG:**
```kotlin
if (age >= 18); {  // Semicolon breaks the if statement!
    println("Adult")
}
```

This creates an empty if statement, and the code block always executes!

✅ **CORRECT:**
```kotlin
if (age >= 18) {
    println("Adult")
}
```

### Pitfall 3: Comparing Floating-Point Numbers with ==

Floating-point arithmetic can be imprecise:

❌ **Risky:**
```kotlin
val result = 0.1 + 0.2
if (result == 0.3) {  // Might be false due to floating-point precision!
    println("Equal")
}
```

✅ **Better:**
```kotlin
val result = 0.1 + 0.2
val epsilon = 0.0001
if (Math.abs(result - 0.3) < epsilon) {
    println("Approximately equal")
}
```

### Best Practice 1: Readable Conditions

Use descriptive variable names and comments for complex conditions:

❌ **Unclear:**
```kotlin
if (x > 0 && y < 100) {
    // ...
}
```

✅ **Clear:**
```kotlin
val hasValidAge = age > 0
val isBelowMaxAge = age < 100

if (hasValidAge && isBelowMaxAge) {
    // ...
}
```

### Best Practice 2: Positive Conditions

When possible, write conditions in positive form:

⚠️ **Harder to read:**
```kotlin
if (!isInvalid) {
    // Do something
}
```

✅ **Easier to read:**
```kotlin
if (isValid) {
    // Do something
}
```

---

## Quick Quiz

Test your understanding:

**Question 1:** What will this code print?
```kotlin
val score = 75
if (score >= 80) {
    println("Great!")
} else {
    println("Keep trying!")
}
```

<details>
<summary>Answer</summary>

**Output:** `Keep trying!`

**Explanation:** `75 >= 80` is false, so the else block executes.
</details>

---

**Question 2:** What's wrong with this code?
```kotlin
if (age = 18) {
    println("You are 18")
}
```

<details>
<summary>Answer</summary>

**Error:** Using `=` instead of `==`

`=` is assignment, `==` is comparison. Should be:
```kotlin
if (age == 18) {
    println("You are 18")
}
```
</details>

---

**Question 3:** What will this print if temperature = 85?
```kotlin
if (temperature > 90) {
    println("A")
}
if (temperature > 80) {
    println("B")
}
if (temperature > 70) {
    println("C")
}
```

<details>
<summary>Answer</summary>

**Output:**
```
B
C
```

**Explanation:** These are three separate if statements (not else if). Both `85 > 80` and `85 > 70` are true, so both B and C print.
</details>

---

**Question 4:** Is this valid Kotlin code?
```kotlin
val result = if (x > 0) "Positive" else "Non-positive"
```

<details>
<summary>Answer</summary>

**Yes!** This is valid. In Kotlin, `if` is an expression and can return a value. The result will be "Positive" if x > 0, otherwise "Non-positive".
</details>

---

## Advanced Bonus: When to Use If vs When

While you'll learn about `when` expressions in the next lesson, here's a preview of when to use each:

**Use if/else for:**
- Binary decisions (two outcomes)
- Range comparisons
- Simple conditions

**Use when (covered next lesson) for:**
- Multiple specific values
- Complex condition patterns
- More than 3-4 options

**Example - if is fine here:**
```kotlin
if (age < 18) {
    "Minor"
} else {
    "Adult"
}
```

**Example - when is better (preview):**
```kotlin
when (dayOfWeek) {
    1 -> "Monday"
    2 -> "Tuesday"
    3 -> "Wednesday"
    // ... cleaner than many else ifs
}
```

---

## Summary

Congratulations! You've mastered conditional logic with if statements. Let's recap:

**Key Concepts:**
- **Conditional logic** lets programs make decisions based on conditions
- **If statements** execute code blocks when conditions are true
- **Comparison operators** (`==`, `!=`, `<`, `>`, `<=`, `>=`) create conditions
- **Else** provides an alternative path when the condition is false
- **Else if** chains multiple conditions (checked top to bottom)
- **Nested if** statements check conditions within conditions
- **Kotlin's if expression** can return values (unique feature!)

**Common Patterns:**
```kotlin
// Simple if
if (condition) { /* code */ }

// If-else
if (condition) { /* code */ } else { /* code */ }

// If-else if chain
if (condition1) { /* code */ }
else if (condition2) { /* code */ }
else { /* code */ }

// If as expression
val result = if (condition) value1 else value2
```

**Best Practices:**
- Always use `==` for comparison, not `=`
- Use braces `{ }` even for single statements
- Order else-if conditions from specific to general
- Use descriptive variable names for complex conditions
- Prefer positive conditions over negative when possible

---

## What's Next?

You can now make basic decisions, but what if you need to combine multiple conditions? "IF it's raining AND I don't have an umbrella, THEN get wet!"

In the next lesson, you'll learn **logical operators** (`&&`, `||`, `!`) to combine and invert conditions, making your decision-making even more powerful!

**Preview:**
```kotlin
if (isRaining && !hasUmbrella) {
    println("You'll get wet!")
}

if (age < 13 || age > 65) {
    println("Discounted ticket")
}
```

---

**Great work! You've completed Lesson 2.1. Mark it complete and continue to Lesson 2.2!** 🎉

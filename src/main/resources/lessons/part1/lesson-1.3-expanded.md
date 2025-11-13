# Lesson 1.3: Control Flow - Conditionals & Loops

**Estimated Time**: 60 minutes

---

## Topic Introduction

So far, your programs execute line by line from top to bottom. But real programs need to make decisions ("if it's raining, bring an umbrella") and repeat tasks ("keep adding numbers until we reach 100").

This lesson teaches you **control flow**—how to make your programs smart and efficient with conditionals and loops.

---

## The Concept

### The GPS Analogy

Think of control flow like GPS navigation:

**Conditionals** (if/else): "IF there's traffic ahead, THEN take alternate route, ELSE continue on current road"

**Loops** (for/while): "WHILE you haven't reached destination, keep giving directions"

Your programs use the same logic!

---

## If-Else Statements

### Basic If Statement

```kotlin
val age = 18

if (age >= 18) {
    println("You can vote!")
}
```

**Structure**:
```
if (condition) {
    // Code runs if condition is true
}
```

### If-Else Statement

```kotlin
val age = 16

if (age >= 18) {
    println("You can vote!")
} else {
    println("You're too young to vote.")
}
```

### If-Else-If Chain

```kotlin
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
```

### If as an Expression

In Kotlin, `if` returns a value:

```kotlin
val age = 20
val status = if (age >= 18) "Adult" else "Minor"
println(status)  // "Adult"

// Multi-line
val message = if (age >= 18) {
    "You can vote"
} else {
    "You cannot vote yet"
}
```

---

## When Expression

Kotlin's `when` is like a powerful `switch` statement:

### Basic When

```kotlin
val dayNumber = 3

when (dayNumber) {
    1 -> println("Monday")
    2 -> println("Tuesday")
    3 -> println("Wednesday")
    4 -> println("Thursday")
    5 -> println("Friday")
    6 -> println("Saturday")
    7 -> println("Sunday")
    else -> println("Invalid day")
}
```

### When as Expression

```kotlin
val grade = 'B'

val description = when (grade) {
    'A' -> "Excellent"
    'B' -> "Good"
    'C' -> "Average"
    'D' -> "Below Average"
    'F' -> "Failing"
    else -> "Invalid grade"
}

println(description)  // "Good"
```

### When with Ranges

```kotlin
val score = 85

val grade = when (score) {
    in 90..100 -> "A"
    in 80..89 -> "B"
    in 70..79 -> "C"
    in 60..69 -> "D"
    else -> "F"
}
```

### When with Multiple Conditions

```kotlin
val number = 3

when (number) {
    1, 2, 3 -> println("Small number")
    4, 5, 6 -> println("Medium number")
    7, 8, 9 -> println("Large number")
    else -> println("Out of range")
}
```

### When with Boolean Conditions

```kotlin
val temperature = 25

when {
    temperature < 0 -> println("Freezing")
    temperature < 15 -> println("Cold")
    temperature < 25 -> println("Moderate")
    else -> println("Warm")
}
```

---

## Loops

### For Loop with Ranges

```kotlin
// Print 1 to 5
for (i in 1..5) {
    println(i)
}

// Print 1 to 10, step by 2
for (i in 1..10 step 2) {
    println(i)  // 1, 3, 5, 7, 9
}

// Count down from 10 to 1
for (i in 10 downTo 1) {
    println(i)
}

// Exclude last number
for (i in 1 until 5) {
    println(i)  // 1, 2, 3, 4 (excludes 5)
}
```

### While Loop

```kotlin
var count = 1

while (count <= 5) {
    println("Count: $count")
    count++
}
```

### Do-While Loop

Runs at least once:

```kotlin
var number = 10

do {
    println(number)
    number--
} while (number > 0)
```

### Break and Continue

```kotlin
// Break - exit loop early
for (i in 1..10) {
    if (i == 5) break
    println(i)  // 1, 2, 3, 4
}

// Continue - skip current iteration
for (i in 1..10) {
    if (i % 2 == 0) continue  // Skip even numbers
    println(i)  // 1, 3, 5, 7, 9
}
```

---

## Exercise 1: Number Guessing Game

Create a simple number guessing game.

**Expected Output**:
```
Guess a number between 1 and 10:
5
Too low!
7
Too high!
6
Correct!
```

---

## Solution 1

```kotlin
fun main() {
    val secretNumber = (1..10).random()
    var guess: Int

    do {
        println("Guess a number between 1 and 10:")
        guess = readln().toInt()

        when {
            guess < secretNumber -> println("Too low!")
            guess > secretNumber -> println("Too high!")
            else -> println("Correct!")
        }
    } while (guess != secretNumber)
}
```

---

## Checkpoint Quiz

### Question 1
What's the output of this code?
```kotlin
val x = 5
if (x > 10) println("A") else println("B")
```

A) A
B) B
C) Error
D) Nothing

### Question 2
What's the difference between `while` and `do-while`?

A) No difference
B) `do-while` runs at least once
C) `while` is faster
D) `do-while` can't use break

### Question 3
What does `1..5` represent?

A) Array with values 1 and 5
B) Range from 1 to 5 (inclusive)
C) Division: 1/5
D) Error

### Question 4
What does `break` do in a loop?

A) Skips current iteration
B) Exits the loop entirely
C) Pauses the loop
D) Restarts the loop

### Question 5
In a `when` expression, what is `else`?

A) Optional branch
B) Required catch-all branch
C) Error condition
D) Loop terminator

---

## Quiz Answers

**Question 1: B**
`x` is 5, which is not greater than 10, so the else branch executes printing "B".

**Question 2: B**
`do-while` executes the body first, then checks the condition, guaranteeing at least one execution.

**Question 3: B**
`1..5` creates a range including both 1 and 5: [1, 2, 3, 4, 5]

**Question 4: B**
`break` immediately exits the current loop.

**Question 5: B**
When used as an expression, `else` is required to ensure all cases are covered.

---

## What You've Learned

✅ If-else statements for decision making
✅ When expressions for multiple conditions
✅ For loops with ranges
✅ While and do-while loops
✅ Break and continue statements
✅ Using conditionals as expressions

---

## Next Steps

In **Lesson 1.4: Functions**, you'll learn to organize code into reusable blocks!

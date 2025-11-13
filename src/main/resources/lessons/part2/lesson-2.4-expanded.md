# Lesson 2.4: Repeating Tasks - For Loops and Iteration

**Estimated Time**: 60 minutes
**Difficulty**: Beginner
**Prerequisites**: Lesson 2.3 (When expressions)

---

## Topic Introduction

Imagine you need to send birthday invitations to 50 friends. Would you write 50 separate print statements? Of course not! You'd use a loop to repeat the same task with different values. That's the power of iteration—doing something multiple times without writing repetitive code.

In programming, we frequently need to:
- Process every item in a list
- Repeat an action a specific number of times
- Count through a sequence of numbers
- Iterate through collections of data

Kotlin's `for` loop makes all of this elegant and easy. Unlike many languages where loops can be complex and error-prone, Kotlin's for loop is designed to be safe, concise, and expressive.

In this lesson, you'll learn:
- What iteration means and why it's essential
- How to use for loops with ranges
- Iterating through collections and lists
- Working with indices
- Advanced loop techniques: step, downTo, until
- Best practices for clean, efficient loops

By the end, you'll be able to process data efficiently and write powerful, concise code!

---

## The Concept: Repetition in Programming

### Real-World Iteration

You perform iteration constantly in daily life:

**Making pancakes:**
```
FOR each pancake (1 to 10):
    1. Pour batter on griddle
    2. Wait for bubbles
    3. Flip pancake
    4. Cook other side
    5. Remove to plate
```

**Checking email:**
```
FOR each unread email:
    1. Open email
    2. Read content
    3. Decide: Reply, Archive, or Delete
    4. Mark as read
```

**Grading papers:**
```
FOR each student submission:
    1. Review work
    2. Calculate score
    3. Write feedback
    4. Record grade
```

In each case, you're **repeating the same steps** for different items. That's exactly what loops do in programming!

### The Manual vs Loop Comparison

**Without loops (manual repetition):**
```kotlin
println("Welcome, Alice!")
println("Welcome, Bob!")
println("Welcome, Charlie!")
println("Welcome, Diana!")
println("Welcome, Eve!")
// Imagine doing this for 100 names...
```

**With loops (automatic repetition):**
```kotlin
val names = listOf("Alice", "Bob", "Charlie", "Diana", "Eve")
for (name in names) {
    println("Welcome, $name!")
}
```

The loop version:
- Works for any number of names
- Less code to write and maintain
- Easy to modify (change the greeting in one place)
- No chance of typos from copying and pasting

---

## Basic For Loop with Ranges

### Your First For Loop

```kotlin
fun main() {
    for (i in 1..5) {
        println("Count: $i")
    }
}
```

**Output:**
```
Count: 1
Count: 2
Count: 3
Count: 4
Count: 5
```

**How it works:**
1. `for` - Keyword that starts the loop
2. `i` - Loop variable (can be any name)
3. `in` - Keyword meaning "within" or "through"
4. `1..5` - Range from 1 to 5 (inclusive)
5. Loop body executes once for each value in the range

### Anatomy of a For Loop

```kotlin
for (variable in collection) {
    // Code to repeat
    // variable changes each iteration
}
```

**Visual flow:**
```
Start
  ↓
for (i in 1..5)
  ↓
i = 1 → Execute body → Print "Count: 1"
  ↓
i = 2 → Execute body → Print "Count: 2"
  ↓
i = 3 → Execute body → Print "Count: 3"
  ↓
i = 4 → Execute body → Print "Count: 4"
  ↓
i = 5 → Execute body → Print "Count: 5"
  ↓
End (no more values)
```

### Practical Example: Countdown Timer

```kotlin
fun main() {
    println("Rocket launch countdown:")

    for (countdown in 10 downTo 1) {
        println("$countdown...")
        Thread.sleep(1000)  // Wait 1 second (1000 milliseconds)
    }

    println("🚀 BLAST OFF!")
}
```

**Output:**
```
Rocket launch countdown:
10...
9...
8...
...
1...
🚀 BLAST OFF!
```

---

## Understanding Ranges

Kotlin has several ways to create ranges:

### Inclusive Range (..)

```kotlin
for (i in 1..10) {
    print("$i ")
}
// Output: 1 2 3 4 5 6 7 8 9 10
```

Both 1 and 10 are **included**.

### Exclusive Range (until)

```kotlin
for (i in 1 until 10) {
    print("$i ")
}
// Output: 1 2 3 4 5 6 7 8 9
```

10 is **excluded** (stops before 10).

**Use case:** Perfect for array/list indices which start at 0:
```kotlin
val items = listOf("A", "B", "C")
for (i in 0 until items.size) {
    println("Item $i: ${items[i]}")
}
```

### Reverse Range (downTo)

```kotlin
for (i in 10 downTo 1) {
    print("$i ")
}
// Output: 10 9 8 7 6 5 4 3 2 1
```

Counts **backwards** from 10 to 1.

### Step Ranges (step)

```kotlin
for (i in 0..10 step 2) {
    print("$i ")
}
// Output: 0 2 4 6 8 10
```

Increments by 2 instead of 1 (counts even numbers).

**Combined example:**
```kotlin
for (i in 10 downTo 0 step 2) {
    print("$i ")
}
// Output: 10 8 6 4 2 0
```

### Range Quick Reference

```kotlin
1..10       // 1, 2, 3, ..., 10 (inclusive)
1 until 10  // 1, 2, 3, ..., 9 (exclusive end)
10 downTo 1 // 10, 9, 8, ..., 1 (reverse)
1..10 step 2 // 1, 3, 5, 7, 9 (every 2nd)
```

---

## Iterating Through Collections

### For Loop with Lists

```kotlin
fun main() {
    val fruits = listOf("Apple", "Banana", "Cherry", "Date")

    for (fruit in fruits) {
        println("I like $fruit")
    }
}
```

**Output:**
```
I like Apple
I like Banana
I like Cherry
I like Date
```

**How it works:** The loop variable `fruit` takes on each value in the list, one at a time.

### For Loop with Strings

Strings are collections of characters, so you can iterate through them:

```kotlin
fun main() {
    val word = "KOTLIN"

    for (letter in word) {
        println("Letter: $letter")
    }
}
```

**Output:**
```
Letter: K
Letter: O
Letter: T
Letter: L
Letter: I
Letter: N
```

### Practical Example: Shopping Cart Total

```kotlin
fun main() {
    val prices = listOf(29.99, 49.99, 19.99, 99.99, 15.99)
    var total = 0.0

    for (price in prices) {
        total += price
    }

    println("Shopping cart total: $$total")
}
```

**Output:**
```
Shopping cart total: $215.95
```

---

## Working with Indices

Sometimes you need both the **index** (position) and the **value**:

### Using indices Property

```kotlin
fun main() {
    val languages = listOf("Kotlin", "Python", "JavaScript", "Swift")

    for (i in languages.indices) {
        println("Language #${i + 1}: ${languages[i]}")
    }
}
```

**Output:**
```
Language #1: Kotlin
Language #2: Python
Language #3: JavaScript
Language #4: Swift
```

**Note:** `languages.indices` creates a range `0 until languages.size`.

### Using withIndex()

The elegant approach—get both index and value:

```kotlin
fun main() {
    val languages = listOf("Kotlin", "Python", "JavaScript", "Swift")

    for ((index, language) in languages.withIndex()) {
        println("Language #${index + 1}: $language")
    }
}
```

**Output:** (same as above)

**Bonus:** More readable and less error-prone!

### Practical Example: Leaderboard

```kotlin
fun main() {
    val players = listOf("Alice", "Bob", "Charlie", "Diana")
    val scores = listOf(950, 880, 920, 900)

    println("=== Game Leaderboard ===")

    for (i in players.indices) {
        val rank = i + 1
        println("#$rank - ${players[i]}: ${scores[i]} points")
    }
}
```

**Output:**
```
=== Game Leaderboard ===
#1 - Alice: 950 points
#2 - Bob: 880 points
#3 - Charlie: 920 points
#4 - Diana: 900 points
```

---

## Nested Loops

You can put loops inside other loops:

### Basic Nested Loop

```kotlin
fun main() {
    for (i in 1..3) {
        for (j in 1..3) {
            print("($i,$j) ")
        }
        println()  // New line after inner loop completes
    }
}
```

**Output:**
```
(1,1) (1,2) (1,3)
(2,1) (2,2) (2,3)
(3,1) (3,2) (3,3)
```

**How it works:**
- Outer loop runs 3 times (i = 1, 2, 3)
- For each outer iteration, inner loop runs 3 times (j = 1, 2, 3)
- Total: 3 × 3 = 9 iterations

### Practical Example: Multiplication Table

```kotlin
fun main() {
    println("Multiplication Table (1-5):")
    println()

    // Header row
    print("   ")
    for (i in 1..5) {
        print("%4d".format(i))
    }
    println()
    println("   " + "----".repeat(5))

    // Table rows
    for (i in 1..5) {
        print("%2d |".format(i))
        for (j in 1..5) {
            print("%4d".format(i * j))
        }
        println()
    }
}
```

**Output:**
```
Multiplication Table (1-5):

      1   2   3   4   5
   --------------------
 1 |   1   2   3   4   5
 2 |   2   4   6   8  10
 3 |   3   6   9  12  15
 4 |   4   8  12  16  20
 5 |   5  10  15  20  25
```

### Pattern Printing: Triangle

```kotlin
fun main() {
    val size = 5

    for (row in 1..size) {
        for (col in 1..row) {
            print("* ")
        }
        println()
    }
}
```

**Output:**
```
*
* *
* * *
* * * *
* * * * *
```

---

## Hands-On Exercises

### Exercise 1: Sum of Numbers

**Challenge:** Calculate the sum of all numbers from 1 to 100 using a for loop.

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    var sum = 0

    for (i in 1..100) {
        sum += i
    }

    println("Sum of 1 to 100: $sum")
}
```

**Output:**
```
Sum of 1 to 100: 5050
```

**Key concepts:**
- Using a range with for loop
- Accumulating values in a variable
- The `+=` compound operator

**Bonus - Math fact:** The formula is n(n+1)/2 = 100(101)/2 = 5050
</details>

---

### Exercise 2: FizzBuzz

**Challenge:** The classic FizzBuzz problem:
- Print numbers 1 to 30
- For multiples of 3, print "Fizz" instead
- For multiples of 5, print "Buzz" instead
- For multiples of both 3 and 5, print "FizzBuzz"

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    for (i in 1..30) {
        when {
            i % 15 == 0 -> println("FizzBuzz")
            i % 3 == 0 -> println("Fizz")
            i % 5 == 0 -> println("Buzz")
            else -> println(i)
        }
    }
}
```

**Output:**
```
1
2
Fizz
4
Buzz
Fizz
7
8
Fizz
Buzz
11
Fizz
13
14
FizzBuzz
...
```

**Key concepts:**
- Combining for loops with when expressions
- Using modulo operator for divisibility
- Order matters (check 15 before 3 or 5)
</details>

---

### Exercise 3: Reverse a String

**Challenge:** Write a program that reverses a string using a for loop.

**Example:** "KOTLIN" → "NILTOK"

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val original = "KOTLIN"
    var reversed = ""

    for (i in original.length - 1 downTo 0) {
        reversed += original[i]
    }

    println("Original: $original")
    println("Reversed: $reversed")
}
```

**Output:**
```
Original: KOTLIN
Reversed: NILTOK
```

**Alternative solution using indices:**
```kotlin
fun main() {
    val original = "KOTLIN"
    var reversed = ""

    for (char in original.reversed()) {
        reversed += char
    }

    println("Original: $original")
    println("Reversed: $reversed")
}
```

**Key concepts:**
- String indexing
- Reverse iteration with downTo
- String concatenation
</details>

---

### Exercise 4: Find Maximum Value

**Challenge:** Given a list of numbers, find the maximum value using a for loop.

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val numbers = listOf(45, 23, 67, 12, 89, 34, 56)
    var max = numbers[0]  // Start with first number

    for (number in numbers) {
        if (number > max) {
            max = number
        }
    }

    println("Numbers: $numbers")
    println("Maximum value: $max")
}
```

**Output:**
```
Numbers: [45, 23, 67, 12, 89, 34, 56]
Maximum value: 89
```

**Alternative using indices:**
```kotlin
fun main() {
    val numbers = listOf(45, 23, 67, 12, 89, 34, 56)
    var max = numbers[0]
    var maxIndex = 0

    for (i in numbers.indices) {
        if (numbers[i] > max) {
            max = numbers[i]
            maxIndex = i
        }
    }

    println("Maximum value: $max at index $maxIndex")
}
```

**Key concepts:**
- Tracking maximum value
- Comparing values in a loop
- Initializing with first element
</details>

---

## Common Pitfalls and Best Practices

### Pitfall 1: Off-By-One Errors

❌ **Common mistake:**
```kotlin
val items = listOf("A", "B", "C")
for (i in 1..items.size) {  // Bug! Goes from 1 to 3
    println(items[i])  // Crash! Index out of bounds
}
```

✅ **Correct:**
```kotlin
val items = listOf("A", "B", "C")
for (i in 0 until items.size) {  // 0 to 2
    println(items[i])
}

// Or better - use indices
for (i in items.indices) {
    println(items[i])
}

// Or best - iterate directly
for (item in items) {
    println(item)
}
```

### Pitfall 2: Modifying Collection While Iterating

❌ **Dangerous:**
```kotlin
val numbers = mutableListOf(1, 2, 3, 4, 5)
for (number in numbers) {
    if (number % 2 == 0) {
        numbers.remove(number)  // Can cause issues!
    }
}
```

✅ **Safe approach:**
```kotlin
val numbers = mutableListOf(1, 2, 3, 4, 5)
val toRemove = mutableListOf<Int>()

for (number in numbers) {
    if (number % 2 == 0) {
        toRemove.add(number)
    }
}

numbers.removeAll(toRemove)
```

Or use built-in functions:
```kotlin
val numbers = mutableListOf(1, 2, 3, 4, 5)
numbers.removeIf { it % 2 == 0 }
```

### Pitfall 3: Unnecessary Index Variables

⚠️ **Okay but verbose:**
```kotlin
val names = listOf("Alice", "Bob", "Charlie")
for (i in names.indices) {
    println(names[i])
}
```

✅ **Better:**
```kotlin
val names = listOf("Alice", "Bob", "Charlie")
for (name in names) {
    println(name)
}
```

**Rule:** Only use indices when you actually need them.

### Best Practice 1: Descriptive Variable Names

❌ **Unclear:**
```kotlin
for (x in myList) {
    println(x)
}
```

✅ **Clear:**
```kotlin
for (student in students) {
    println(student)
}
```

### Best Practice 2: Use Ranges Appropriately

```kotlin
// Counting up
for (i in 1..10) { }

// Counting down
for (i in 10 downTo 1) { }

// Skip values
for (i in 0..100 step 10) { }

// Exclusive end
for (i in 0 until list.size) { }
```

### Best Practice 3: Choose the Right Loop Type

```kotlin
// Need the value only? Iterate directly
for (fruit in fruits) { println(fruit) }

// Need index and value? Use withIndex()
for ((index, fruit) in fruits.withIndex()) {
    println("$index: $fruit")
}

// Need just the index? Use indices
for (i in fruits.indices) {
    println("Position $i")
}
```

---

## Quick Quiz

**Question 1:** What does this print?
```kotlin
for (i in 1..5 step 2) {
    print("$i ")
}
```

<details>
<summary>Answer</summary>

**Output:** `1 3 5`

**Explanation:** Starts at 1, increments by 2 each time, up to 5.
- First iteration: i = 1
- Second iteration: i = 3
- Third iteration: i = 5
- Stop (next would be 7, which is > 5)
</details>

---

**Question 2:** How many times does this loop run?
```kotlin
for (i in 0 until 10) {
    println(i)
}
```

<details>
<summary>Answer</summary>

**Answer:** 10 times (prints 0 through 9)

**Explanation:** `until` is exclusive of the end value. So `0 until 10` means 0, 1, 2, 3, 4, 5, 6, 7, 8, 9.
</details>

---

**Question 3:** What's the output?
```kotlin
val word = "Hi"
for (char in word) {
    print("$char ")
}
```

<details>
<summary>Answer</summary>

**Output:** `H i`

**Explanation:** Strings are iterable. The loop goes through each character: 'H' then 'i'.
</details>

---

**Question 4:** How do you loop backwards from 10 to 1?

<details>
<summary>Answer</summary>

```kotlin
for (i in 10 downTo 1) {
    println(i)
}
```

**Explanation:** Use `downTo` to create a reverse range.
</details>

---

## Summary

Congratulations! You've mastered for loops in Kotlin. Let's recap:

**Key Concepts:**
- **For loops** repeat code for each item in a collection or range
- **Ranges** define sequences: `1..10`, `1 until 10`, `10 downTo 1`
- **Step** allows custom increments: `0..100 step 5`
- **Collections** can be iterated directly or with indices
- **withIndex()** provides both index and value
- **Nested loops** enable multi-dimensional iteration

**For Loop Patterns:**
```kotlin
// Range iteration
for (i in 1..10) { }

// Collection iteration
for (item in collection) { }

// With index
for ((index, item) in collection.withIndex()) { }

// Using indices
for (i in collection.indices) { }

// Reverse
for (i in 10 downTo 1) { }

// With step
for (i in 0..100 step 10) { }
```

**Best Practices:**
- Iterate directly when you don't need indices
- Use `indices` or `until` to avoid off-by-one errors
- Use descriptive variable names
- Don't modify collections while iterating
- Choose the simplest loop form for your needs

---

## What's Next?

For loops are great when you know how many times to iterate, but what about situations where you need to repeat until a condition is met? What if you need to keep asking for valid input until the user gets it right?

In **Lesson 2.5: While Loops and Do-While**, you'll learn:
- While loops for condition-based repetition
- Do-while loops (execute at least once)
- Break and continue for loop control
- Infinite loops and how to guard against them

**Preview:**
```kotlin
var attempts = 0
while (attempts < 3) {
    println("Attempt ${attempts + 1}")
    attempts++
}

do {
    val input = readln()
} while (input != "quit")
```

---

**Fantastic progress! You've completed Lesson 2.4. Keep up the momentum!** 🎉

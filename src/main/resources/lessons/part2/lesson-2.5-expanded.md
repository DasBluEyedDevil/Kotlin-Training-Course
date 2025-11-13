# Lesson 2.5: While Loops and Do-While - Condition-Based Repetition

**Estimated Time**: 55 minutes
**Difficulty**: Beginner
**Prerequisites**: Lesson 2.4 (For loops)

---

## Topic Introduction

You've mastered for loops, which are perfect when you know exactly how many times to repeat something. But programming often requires a different kind of repetition—repeating until a condition is met, not a fixed number of times.

Think about real-life scenarios:
- Keep entering your password **until** it's correct
- Keep rolling dice **until** you get a six
- Keep asking for menu input **until** the user chooses "quit"
- Download data **while** there's more to download

These situations don't have a predetermined number of iterations—they continue based on a **condition**. That's where `while` and `do-while` loops shine!

In this lesson, you'll learn:
- The difference between while and do-while loops
- When to use each type of loop
- How to control loops with break and continue
- Avoiding infinite loops
- Common patterns and best practices

By the end, you'll know how to choose the right loop for any situation!

---

## The Concept: Condition-Based Repetition

### Real-World While Loops

You use condition-based repetition constantly:

**Making coffee:**
```
WHILE coffee isn't full:
    Add water to pot
    Check if full
```

**Waiting in line:**
```
WHILE people are ahead of me:
    Wait
    Check if my turn
```

**Learning to ride a bike:**
```
WHILE I keep falling:
    Get back on bike
    Try again
    Improve balance
```

The key difference from for loops: **You don't know beforehand how many times you'll repeat**. You repeat until a condition changes.

### For vs While: The Fundamental Difference

**Use FOR when:**
- You know the number of iterations upfront
- You're iterating through a collection
- You're counting within a specific range

```kotlin
// I know I want to print 1 to 10
for (i in 1..10) {
    println(i)
}
```

**Use WHILE when:**
- You repeat until a condition changes
- The number of iterations is unknown
- You're waiting for user input or external event

```kotlin
// I don't know when user will enter "quit"
var input = ""
while (input != "quit") {
    input = readln()
}
```

---

## The While Loop

### Basic While Loop Syntax

```kotlin
while (condition) {
    // Code to repeat
    // Must eventually make condition false!
}
```

**How it works:**
1. Check the condition
2. If true, execute the body
3. Return to step 1
4. If false, skip the body and continue

### Your First While Loop

```kotlin
fun main() {
    var count = 1

    while (count <= 5) {
        println("Count: $count")
        count++
    }

    println("Done!")
}
```

**Output:**
```
Count: 1
Count: 2
Count: 3
Count: 4
Count: 5
Done!
```

**Flow:**
```
count = 1
Check: 1 <= 5? TRUE → Print "Count: 1" → count = 2
Check: 2 <= 5? TRUE → Print "Count: 2" → count = 3
Check: 3 <= 5? TRUE → Print "Count: 3" → count = 4
Check: 4 <= 5? TRUE → Print "Count: 4" → count = 5
Check: 5 <= 5? TRUE → Print "Count: 5" → count = 6
Check: 6 <= 5? FALSE → Exit loop
Print "Done!"
```

### Practical Example: Password Validator

```kotlin
fun main() {
    val correctPassword = "kotlin123"
    var attempts = 0
    val maxAttempts = 3

    while (attempts < maxAttempts) {
        print("Enter password: ")
        val input = readln()

        if (input == correctPassword) {
            println("Access granted!")
            break  // Exit loop early
        } else {
            attempts++
            val remaining = maxAttempts - attempts
            if (remaining > 0) {
                println("Incorrect. $remaining attempts remaining.")
            }
        }
    }

    if (attempts >= maxAttempts) {
        println("Account locked. Too many failed attempts.")
    }
}
```

**Sample Run:**
```
Enter password: hello
Incorrect. 2 attempts remaining.
Enter password: world
Incorrect. 1 attempts remaining.
Enter password: kotlin123
Access granted!
```

---

## The Do-While Loop

### The Critical Difference

**While loop:** Check condition FIRST, then execute (may not execute at all)

**Do-while loop:** Execute FIRST, then check condition (executes at least once)

### Do-While Syntax

```kotlin
do {
    // Code to execute
    // Runs at least once!
} while (condition)
```

### Comparison Example

```kotlin
// While loop - may not execute
var x = 10
while (x < 5) {
    println("This never prints")
    x++
}

// Do-while loop - executes once
var y = 10
do {
    println("This prints once: $y")
    y++
} while (y < 5)
```

**Output:**
```
This prints once: 10
```

### When to Use Do-While

Perfect for situations where you **must** execute the code at least once:

**Menu systems:**
```kotlin
fun main() {
    var choice: String

    do {
        println("\n=== Main Menu ===")
        println("1. New Game")
        println("2. Load Game")
        println("3. Settings")
        println("4. Exit")
        print("Enter choice: ")

        choice = readln()

        when (choice) {
            "1" -> println("Starting new game...")
            "2" -> println("Loading game...")
            "3" -> println("Opening settings...")
            "4" -> println("Goodbye!")
            else -> println("Invalid choice. Try again.")
        }
    } while (choice != "4")
}
```

**Sample Run:**
```
=== Main Menu ===
1. New Game
2. Load Game
3. Settings
4. Exit
Enter choice: 1
Starting new game...

=== Main Menu ===
1. New Game
2. Load Game
3. Settings
4. Exit
Enter choice: 5
Invalid choice. Try again.

=== Main Menu ===
1. New Game
2. Load Game
3. Settings
4. Exit
Enter choice: 4
Goodbye!
```

### Input Validation Example

```kotlin
fun main() {
    var age: Int

    do {
        print("Enter your age (1-120): ")
        val input = readln()
        age = input.toIntOrNull() ?: -1

        if (age !in 1..120) {
            println("Invalid age. Please try again.")
        }
    } while (age !in 1..120)

    println("Age recorded: $age")
}
```

**Sample Run:**
```
Enter your age (1-120): 150
Invalid age. Please try again.
Enter your age (1-120): abc
Invalid age. Please try again.
Enter your age (1-120): 25
Age recorded: 25
```

---

## Break and Continue

### The break Statement

**Purpose:** Exit the loop immediately, even if the condition is still true.

```kotlin
fun main() {
    var number = 1

    while (number <= 10) {
        if (number == 6) {
            break  // Stop the loop completely
        }
        println(number)
        number++
    }

    println("Loop ended at $number")
}
```

**Output:**
```
1
2
3
4
5
Loop ended at 6
```

**Practical example: Search**
```kotlin
fun main() {
    val numbers = listOf(5, 12, 8, 3, 15, 7, 9)
    val target = 15
    var index = 0
    var found = false

    while (index < numbers.size) {
        if (numbers[index] == target) {
            println("Found $target at index $index")
            found = true
            break  // No need to continue searching
        }
        index++
    }

    if (!found) {
        println("$target not found")
    }
}
```

**Output:**
```
Found 15 at index 4
```

### The continue Statement

**Purpose:** Skip the rest of the current iteration and move to the next one.

```kotlin
fun main() {
    var number = 0

    while (number < 10) {
        number++

        if (number % 2 == 0) {
            continue  // Skip even numbers
        }

        println(number)
    }
}
```

**Output:**
```
1
3
5
7
9
```

**How it works:**
- When `number` is even, `continue` is executed
- Skip `println(number)`
- Jump back to the condition check
- Continue with next iteration

### Break vs Continue Comparison

```kotlin
fun main() {
    println("=== Break Example ===")
    for (i in 1..10) {
        if (i == 5) break
        print("$i ")
    }
    println("\n")

    println("=== Continue Example ===")
    for (i in 1..10) {
        if (i == 5) continue
        print("$i ")
    }
}
```

**Output:**
```
=== Break Example ===
1 2 3 4

=== Continue Example ===
1 2 3 4 6 7 8 9 10
```

---

## Infinite Loops and Guards

### What is an Infinite Loop?

An infinite loop is a loop that never ends because its condition never becomes false:

```kotlin
// ⚠️ DANGER: Infinite loop!
while (true) {
    println("This runs forever!")
}
```

**This will:**
- Run indefinitely
- Freeze your program
- Consume CPU and memory
- Require force-stopping

### Intentional Infinite Loops

Sometimes infinite loops are **intentional** and controlled with `break`:

```kotlin
fun main() {
    while (true) {
        print("Enter 'quit' to exit: ")
        val input = readln()

        if (input == "quit") {
            break  // This is our exit
        }

        println("You entered: $input")
    }

    println("Program ended")
}
```

This is safe because we have a guaranteed exit condition.

### Common Infinite Loop Mistakes

❌ **Mistake 1: Forgetting to update the condition**
```kotlin
var count = 0
while (count < 5) {
    println(count)
    // Oops! Forgot count++
}
```

❌ **Mistake 2: Wrong update direction**
```kotlin
var count = 10
while (count > 0) {
    println(count)
    count++  // Oops! Should be count--
}
```

❌ **Mistake 3: Condition that can't change**
```kotlin
val x = 5
while (x < 10) {  // x is val, can't change!
    println(x)
}
```

### Infinite Loop Guards

Always ask yourself:
1. **Does my condition eventually become false?**
2. **Do I update the variables in the condition?**
3. **Is there a guaranteed exit (break)?**

✅ **Safe pattern:**
```kotlin
var attempts = 0
val maxAttempts = 1000  // Safety limit

while (condition && attempts < maxAttempts) {
    // Loop body
    attempts++
}

if (attempts >= maxAttempts) {
    println("Warning: Loop limit reached")
}
```

---

## Hands-On Exercises

### Exercise 1: Number Guessing Game

**Challenge:** Create a number guessing game where:
1. Computer picks a random number 1-100
2. User keeps guessing until correct
3. Provide "higher" or "lower" hints
4. Count the number of guesses

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val secretNumber = (1..100).random()
    var guess: Int
    var attempts = 0

    println("I'm thinking of a number between 1 and 100.")

    do {
        print("Your guess: ")
        guess = readln().toIntOrNull() ?: 0
        attempts++

        when {
            guess < secretNumber -> println("Higher!")
            guess > secretNumber -> println("Lower!")
            else -> {
                println("Correct! You got it in $attempts attempts!")
            }
        }
    } while (guess != secretNumber)
}
```

**Sample Run:**
```
I'm thinking of a number between 1 and 100.
Your guess: 50
Higher!
Your guess: 75
Lower!
Your guess: 63
Higher!
Your guess: 69
Correct! You got it in 4 attempts!
```

**Key concepts:**
- Do-while ensures at least one guess
- Using random numbers
- Tracking attempts with a counter
</details>

---

### Exercise 2: Sum Until Zero

**Challenge:** Keep asking user for numbers and sum them. Stop when user enters 0.

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    var sum = 0
    var number: Int

    println("Enter numbers to sum (0 to stop):")

    do {
        print("Enter number: ")
        number = readln().toIntOrNull() ?: 0

        if (number != 0) {
            sum += number
            println("Current sum: $sum")
        }
    } while (number != 0)

    println("\nFinal sum: $sum")
}
```

**Sample Run:**
```
Enter numbers to sum (0 to stop):
Enter number: 10
Current sum: 10
Enter number: 20
Current sum: 30
Enter number: -5
Current sum: 25
Enter number: 0

Final sum: 25
```
</details>

---

### Exercise 3: Fibonacci Sequence

**Challenge:** Print Fibonacci numbers while they're less than 1000.

Fibonacci: Each number is the sum of the previous two (1, 1, 2, 3, 5, 8, 13...)

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    var a = 1
    var b = 1

    println("Fibonacci numbers less than 1000:")
    print("$a $b ")

    while (true) {
        val next = a + b

        if (next >= 1000) {
            break
        }

        print("$next ")

        a = b
        b = next
    }

    println("\n\nStopped at $b (next would be ${a + b})")
}
```

**Output:**
```
Fibonacci numbers less than 1000:
1 1 2 3 5 8 13 21 34 55 89 144 233 377 610 987

Stopped at 987 (next would be 1597)
```

**Key concepts:**
- While(true) with break for complex conditions
- Updating multiple variables
- Fibonacci algorithm
</details>

---

### Exercise 4: Print Even Numbers

**Challenge:** Print even numbers from 1 to 20 using a while loop and continue.

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    var number = 0

    println("Even numbers from 1 to 20:")

    while (number < 20) {
        number++

        if (number % 2 != 0) {
            continue  // Skip odd numbers
        }

        print("$number ")
    }
}
```

**Output:**
```
Even numbers from 1 to 20:
2 4 6 8 10 12 14 16 18 20
```

**Alternative without continue:**
```kotlin
fun main() {
    var number = 0

    println("Even numbers from 1 to 20:")

    while (number < 20) {
        number++

        if (number % 2 == 0) {
            print("$number ")
        }
    }
}
```
</details>

---

## Common Pitfalls and Best Practices

### Pitfall 1: Infinite Loops from Typos

❌ **Dangerous typo:**
```kotlin
var i = 0
while (i < 10) {
    println(i)
    // Typo: incrementing j instead of i
    j++  // i never changes!
}
```

✅ **Safe:**
```kotlin
var i = 0
while (i < 10) {
    println(i)
    i++  // Correct variable
}
```

### Pitfall 2: Off-by-One Errors

❌ **Subtle bug:**
```kotlin
var count = 1
while (count < 10) {  // Stops at 9, not 10
    println(count)
    count++
}
```

✅ **Correct:**
```kotlin
var count = 1
while (count <= 10) {  // Includes 10
    println(count)
    count++
}
```

### Pitfall 3: Not Validating Input

❌ **Crash risk:**
```kotlin
while (true) {
    val age = readln().toInt()  // Crashes on "abc"
    if (age > 0) break
}
```

✅ **Safe:**
```kotlin
while (true) {
    val age = readln().toIntOrNull()
    if (age != null && age > 0) break
    println("Invalid input. Try again.")
}
```

### Best Practice 1: Always Have an Exit

Every loop should have a clear, guaranteed exit condition:

```kotlin
// Good: Clear exit condition
var attempts = 0
while (attempts < maxAttempts) {
    // Do something
    attempts++
}

// Good: Break statement
while (true) {
    val input = readln()
    if (input == "quit") break
}
```

### Best Practice 2: Initialize Before Loop

```kotlin
// ✅ Good
var count = 0
while (count < 10) {
    println(count)
    count++
}

// ❌ Bad - count not initialized
while (count < 10) {  // Error: Unresolved reference
    println(count)
    count++
}
```

### Best Practice 3: Choose the Right Loop

```kotlin
// Use while when condition-based
var keepGoing = true
while (keepGoing) {
    val choice = readln()
    if (choice == "quit") keepGoing = false
}

// Use for when count-based
for (i in 1..10) {
    println(i)
}

// Use do-while when must execute once
do {
    showMenu()
    choice = readln()
} while (choice != "exit")
```

---

## Quick Quiz

**Question 1:** What's the output?
```kotlin
var x = 5
while (x > 0) {
    print("$x ")
    x--
}
```

<details>
<summary>Answer</summary>

**Output:** `5 4 3 2 1`

**Explanation:** Starts at 5, prints and decrements until x reaches 0 (loop stops when x is not > 0).
</details>

---

**Question 2:** How many times does this execute?
```kotlin
var x = 10
while (x < 5) {
    println(x)
    x++
}
```

<details>
<summary>Answer</summary>

**Answer:** 0 times

**Explanation:** The condition `10 < 5` is false from the start, so the loop body never executes.
</details>

---

**Question 3:** What's the difference between these?
```kotlin
// A
while (condition) {
    doSomething()
}

// B
do {
    doSomething()
} while (condition)
```

<details>
<summary>Answer</summary>

**Answer:**
- **A (while):** Checks condition FIRST. Might not execute at all.
- **B (do-while):** Executes FIRST, then checks. Always executes at least once.

**Example:**
```kotlin
var x = 10

while (x < 5) {
    println("A")  // Never prints
}

do {
    println("B")  // Prints once
} while (x < 5)
```
Output: `B`
</details>

---

**Question 4:** What does break do?

<details>
<summary>Answer</summary>

**Answer:** `break` immediately exits the loop, regardless of the condition.

**Example:**
```kotlin
while (true) {
    val input = readln()
    if (input == "quit") {
        break  // Exit the infinite loop
    }
    println("You said: $input")
}
```
</details>

---

## Summary

Congratulations! You've mastered condition-based loops. Let's recap:

**Key Concepts:**
- **While loops** repeat based on conditions, not counts
- **Do-while loops** execute at least once before checking
- **Break** exits the loop immediately
- **Continue** skips to the next iteration
- **Infinite loops** can be intentional with proper guards

**Loop Decision Guide:**
```kotlin
// Known iterations → for loop
for (i in 1..10) { }

// Unknown iterations, check first → while
while (condition) { }

// Unknown iterations, must run once → do-while
do { } while (condition)
```

**Control Flow:**
```kotlin
break     // Exit loop entirely
continue  // Skip to next iteration
```

**Best Practices:**
- Always ensure loops can exit
- Validate user input
- Initialize variables before loops
- Use meaningful variable names
- Guard against infinite loops

**Common Patterns:**
```kotlin
// Input validation
do {
    // Get input
} while (invalid)

// Menu systems
while (choice != "quit") {
    // Show menu
}

// Search until found
while (!found && index < size) {
    // Search logic
}
```

---

## What's Next?

You now have complete control over program flow—decisions and loops! But how do you store and work with multiple pieces of related data? What if you need to manage a shopping cart with many items, or a class roster with dozens of students?

In **Lesson 2.6: Lists - Storing Multiple Items**, you'll learn:
- Creating and using lists
- Mutable vs immutable lists
- Adding, removing, and accessing elements
- Powerful list operations: filter, map, and more
- Real-world list applications

**Preview:**
```kotlin
val fruits = listOf("Apple", "Banana", "Cherry")
val numbers = mutableListOf(1, 2, 3)
numbers.add(4)

val doubled = numbers.map { it * 2 }
val evens = numbers.filter { it % 2 == 0 }
```

---

**Outstanding work! You've completed Lesson 2.5. Lists await you next!** 🎉

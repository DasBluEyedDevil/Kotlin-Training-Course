# Lesson 2.5: While Loops and Do-While

## Loops with Unknown Iterations

With `for` loops, you know exactly how many times to repeat. But what if you don't?

**Analogy:** Pumping air into a tire
- **For loop:** "Pump exactly 50 times"
- **While loop:** "Keep pumping UNTIL the pressure reaches 35 PSI"

You don't know how many pumps that takes - you just check the condition each time!

---

## The `while` Loop

**`while`** repeats as long as a condition is true:

```kotlin
fun main() {
    var count = 1

    while (count <= 5) {
        println("Count: $count")
        count++
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

### Breaking It Down:

```kotlin
while (condition) {
    // Code to repeat
}
```

1. Check condition
2. If true, run code block
3. Go back to step 1
4. If false, exit loop

---

## While vs For

**Use `for` when:** You know the number of iterations
```kotlin
for (i in 1..10) {  // Exactly 10 times
    println(i)
}
```

**Use `while` when:** You repeat based on a condition
```kotlin
while (userWantsToContin

ue) {  // Unknown how many times
    // ...
}
```

---

## Example: User Input Loop

```kotlin
fun main() {
    var input = ""

    while (input != "quit") {
        print("Enter command (or 'quit' to exit): ")
        input = readLine()!!
        println("You entered: $input")
    }

    println("Goodbye!")
}
```

**This runs until the user types "quit"!**

---

## Infinite Loops (Be Careful!)

If the condition is always true, the loop never ends:

```kotlin
while (true) {
    println("This will print forever!")
    // DANGER: Infinite loop!
}
```

**Always ensure** your loop condition can eventually become false!

```kotlin
var count = 1
while (count <= 5) {
    println(count)
    // FORGOT to increment! Infinite loop!
}
```

✅ **Correct:**
```kotlin
var count = 1
while (count <= 5) {
    println(count)
    count++  // Don't forget this!
}
```

---

## The `do-while` Loop

**`do-while`** runs code FIRST, THEN checks condition:

```kotlin
fun main() {
    var count = 1

    do {
        println("Count: $count")
        count++
    } while (count <= 5)
}
```

**Key difference:** Guarantees at least ONE execution, even if condition starts false!

### Comparing While vs Do-While:

**While:**
```kotlin
var x = 10
while (x < 5) {
    println("This never prints!")
}
```

**Do-While:**
```kotlin
var x = 10
do {
    println("This prints once!")
} while (x < 5)
```

---

## Break and Continue

### `break` - Exit the Loop Early

```kotlin
fun main() {
    var count = 1

    while (count <= 10) {
        if (count == 5) {
            break  // Exit loop immediately!
        }
        println(count)
        count++
    }

    println("Loop ended")
}
```

**Output:**
```
1
2
3
4
Loop ended
```

---

### `continue` - Skip to Next Iteration

```kotlin
fun main() {
    for (i in 1..5) {
        if (i == 3) {
            continue  // Skip this iteration
        }
        println(i)
    }
}
```

**Output:**
```
1
2
4
5
```

*Notice: 3 is skipped!*

---

## Interactive Coding Session

### Challenge 1: Number Guessing Game

```kotlin
fun main() {
    val secret = 7
    var guess = 0

    while (guess != secret) {
        print("Guess the number (1-10): ")
        guess = readLine()!!.toInt()

        if (guess < secret) {
            println("Too low!")
        } else if (guess > secret) {
            println("Too high!")
        }
    }

    println("🎉 Correct! The number was $secret")
}
```

---

### Challenge 2: Password Retry

```kotlin
fun main() {
    val correctPassword = "secret123"
    var attempts = 0
    val maxAttempts = 3

    while (attempts < maxAttempts) {
        print("Enter password: ")
        val input = readLine()!!

        if (input == correctPassword) {
            println("✓ Access granted!")
            break
        } else {
            attempts++
            val remaining = maxAttempts - attempts
            if (remaining > 0) {
                println("✗ Wrong! $remaining attempts remaining")
            }
        }
    }

    if (attempts == maxAttempts) {
        println("🔒 Account locked")
    }
}
```

---

### Challenge 3: Sum Until Zero

```kotlin
fun main() {
    var sum = 0
    var input: Int

    println("Enter numbers (0 to stop):")

    do {
        print("> ")
        input = readLine()!!.toInt()
        sum += input
    } while (input != 0)

    println("Total sum: $sum")
}
```

---

## Choosing the Right Loop

| Loop Type | Use When |
|-----------|----------|
| **`for`** | Known iteration count, iterating ranges/collections |
| **`while`** | Condition-based, unknown iterations, check first |
| **`do-while`** | Need at least one execution, validate after |

---

## Common Patterns

### Pattern 1: Menu System

```kotlin
fun main() {
    var choice = 0

    while (choice != 4) {
        println("\n--- Menu ---")
        println("1. Option A")
        println("2. Option B")
        println("3. Option C")
        println("4. Exit")
        print("Choice: ")

        choice = readLine()!!.toInt()

        when (choice) {
            1 -> println("You chose A")
            2 -> println("You chose B")
            3 -> println("You chose C")
            4 -> println("Goodbye!")
            else -> println("Invalid choice")
        }
    }
}
```

---

### Pattern 2: Input Validation

```kotlin
fun main() {
    var age: Int

    do {
        print("Enter age (0-120): ")
        age = readLine()!!.toInt()

        if (age < 0 || age > 120) {
            println("Invalid! Try again.")
        }
    } while (age < 0 || age > 120)

    println("Age accepted: $age")
}
```

---

## Recap: What You've Learned

You now understand:

1. **`while` loop** = Repeat while condition is true
2. **`do-while` loop** = Run once, then repeat while true
3. **`break`** = Exit loop immediately
4. **`continue`** = Skip to next iteration
5. **Infinite loops** = Avoid them by ensuring condition changes
6. **When to use which loop** = Based on your needs

---

## What's Next?

You've mastered all loop types! Next, we'll learn about **Lists** - storing multiple items in a single variable. This combines perfectly with loops!

**Key Takeaways:**
- `while` checks condition first
- `do-while` executes first, then checks
- `break` exits the loop
- `continue` skips current iteration
- Always ensure your condition can become false
- Choose the right loop for the job

---

Great work! Continue to learn about collections!

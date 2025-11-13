# Lesson 2.4: Repeating Tasks (For Loops)

## Making Programs Repeat Themselves

Imagine you need to print "Hello" 100 times. Would you write `println("Hello")` 100 times? No way! That's what **loops** are for.

**Analogy:** Think of a washing machine cycle:
- Load clothes
- **REPEAT 5 times:** (Add water → Wash → Drain → Rinse)
- Dry
- Done!

The washing machine doesn't need separate instructions for each cycle - it **loops**!

---

## What is a Loop?

A **loop** is a programming structure that repeats a block of code multiple times automatically.

**Real-world uses:**
- Processing every item in a shopping cart
- Sending emails to 1000 users
- Drawing 50 stars on the screen
- Counting from 1 to 100

---

## The `for` Loop

The most common loop in Kotlin is the **`for` loop**:

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

### Breaking It Down:

```kotlin
for (i in 1..5) {
    // Code to repeat
}
```

- **`for`** = Keyword for loop
- **`i`** = Loop variable (the counter)
- **`in 1..5`** = Range from 1 to 5 (inclusive)
- **`{ }`** = Code block that repeats

**How it works:**
1. Set `i = 1`, run code block
2. Set `i = 2`, run code block
3. Set `i = 3`, run code block
4. ...continue until `i = 5`

---

## Ranges in Kotlin

**Ranges** define a sequence of numbers:

```kotlin
1..5      // 1, 2, 3, 4, 5 (inclusive)
1 until 5 // 1, 2, 3, 4 (exclusive of 5)
1..10 step 2  // 1, 3, 5, 7, 9 (skip by 2)
10 downTo 1   // 10, 9, 8, ..., 1 (reverse)
```

### Examples:

```kotlin
// Count to 10
for (i in 1..10) {
    println(i)
}

// Count to 9 (excluding 10)
for (i in 1 until 10) {
    println(i)
}

// Even numbers
for (i in 2..10 step 2) {
    println(i)  // 2, 4, 6, 8, 10
}

// Countdown
for (i in 10 downTo 1) {
    println(i)
}
println("Blast off! 🚀")
```

---

## Using the Loop Variable

The loop variable (`i`) is available inside the loop:

```kotlin
fun main() {
    for (i in 1..5) {
        val square = i * i
        println("$i squared is $square")
    }
}
```

**Output:**
```
1 squared is 1
2 squared is 4
3 squared is 9
4 squared is 16
5 squared is 25
```

---

## When You Don't Need the Variable

If you don't use the loop variable, use `_`:

```kotlin
fun main() {
    for (_ in 1..3) {
        println("Hello!")
    }
}
```

**Output:**
```
Hello!
Hello!
Hello!
```

---

## Nested Loops

Loops inside loops!

```kotlin
fun main() {
    for (row in 1..3) {
        for (col in 1..4) {
            print("* ")
        }
        println()  // New line after each row
    }
}
```

**Output:**
```
* * * *
* * * *
* * * *
```

---

## Interactive Coding Session

### Challenge 1: Multiplication Table

Print the multiplication table for a given number:

```kotlin
fun main() {
    print("Enter a number: ")
    val num = readLine()!!.toInt()

    println("Multiplication table for $num:")
    for (i in 1..10) {
        println("$num x $i = ${num * i}")
    }
}
```

**Example output:**
```
Multiplication table for 5:
5 x 1 = 5
5 x 2 = 10
...
5 x 10 = 50
```

---

### Challenge 2: Sum Calculator

Calculate the sum of numbers from 1 to N:

```kotlin
fun main() {
    print("Enter N: ")
    val n = readLine()!!.toInt()

    var sum = 0
    for (i in 1..n) {
        sum += i
    }

    println("Sum of 1 to $n = $sum")
}
```

---

### Challenge 3: Pattern Printer

Print a triangle pattern:

```kotlin
fun main() {
    for (i in 1..5) {
        for (j in 1..i) {
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

## The `repeat()` Function

For simple repetition, Kotlin has a shortcut:

```kotlin
fun main() {
    repeat(3) {
        println("Hello!")
    }
}
```

**Output:**
```
Hello!
Hello!
Hello!
```

With index:

```kotlin
repeat(5) { index ->
    println("Iteration: $index")
}
```

**Output:**
```
Iteration: 0
Iteration: 1
Iteration: 2
Iteration: 3
Iteration: 4
```

**Note:** Index starts at 0!

---

## Common Mistakes

### Mistake 1: Off-by-One Errors

```kotlin
// Want 10 iterations?
for (i in 0..10) {  // Actually 11 iterations (0-10)!
}

// Correct:
for (i in 0 until 10) {  // 10 iterations (0-9)
}
```

---

### Mistake 2: Modifying Range Variable

❌ **Wrong:**
```kotlin
for (i in 1..5) {
    i = i + 1  // ERROR! Can't modify loop variable
}
```

✅ **Use a separate variable if needed**

---

## Recap: What You've Learned

You now understand:

1. **`for` loops** = Repeat code a specific number of times
2. **Ranges** = `1..5`, `1 until 5`, `1..10 step 2`, `10 downTo 1`
3. **Loop variable** = Available inside the loop
4. **Nested loops** = Loops within loops
5. **`repeat()`** = Simple repetition function

---

## What's Next?

You've learned `for` loops for counting. Next, we'll learn **`while` loops** - for when you don't know how many times to repeat!

**Key Takeaways:**
- Loops avoid repetitive code
- `for` is great for known iteration counts
- Ranges define the sequence
- Use `step` for skipping, `downTo` for reverse
- Nested loops create patterns and grids

---

Excellent! Continue to the next lesson on while loops!

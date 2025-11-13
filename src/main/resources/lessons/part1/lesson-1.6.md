# Lesson 1.6: Talking Back to the Computer (Reading Input)

## Making Programs Interactive

So far, your programs have been one-way conversations - you write code, it prints output. Now let's make it **two-way**: your program will ask questions, and you'll provide answers!

---

## What is Input?

**Input** is information that comes **into** your program from the user (you!).

**Analogy:**
- **Output** = Computer talking to you (`println`)
- **Input** = You talking to the computer (typing answers)

Think of it like a conversation:
- **Computer:** "What's your name?"
- **You:** "Alex"
- **Computer:** "Nice to meet you, Alex!"

---

## The `readLine()` Function

In Kotlin, we use **`readLine()`** to get input from the user.

Here's how it works:

```kotlin
fun main() {
    println("What is your name?")
    val name = readLine()
    println("Hello, $name!")
}
```

**What happens:**
1. The program prints "What is your name?"
2. The program **waits** for you to type something and press Enter
3. Whatever you typed gets stored in the `name` variable
4. The program prints "Hello, [your name]!"

---

## Step-by-Step Breakdown

```kotlin
val name = readLine()
```

- **`readLine()`** = "Read a line of text that the user types"
- **`val name =`** = "Store that text in a variable called `name`"

**Analogy:** Think of `readLine()` as the computer's ears. It listens for what you type, and then puts it in a box (variable) for later use.

---

## Interactive Example

Let's create a simple greeting program:

```kotlin
fun main() {
    println("Hello! What's your name?")
    val userName = readLine()

    println("Nice to meet you, $userName!")
    println("How old are you?")
    val userAge = readLine()

    println("Wow, $userAge is a great age!")
}
```

**Sample interaction:**
```
Hello! What's your name?
> Jordan
Nice to meet you, Jordan!
How old are you?
> 25
Wow, 25 is a great age!
```

---

## Important: `readLine()` Returns a String

Whatever the user types is **always** treated as text (a `String`), even if they type numbers!

```kotlin
fun main() {
    println("Enter a number:")
    val input = readLine()  // This is a String!

    println("You entered: $input")
    println("Type: String")
}
```

If you type `42`, it's stored as `"42"` (text), not `42` (number).

---

## Converting Input to Numbers

What if you want to do math with user input? You need to **convert** the String to a number!

### Converting to Int:

```kotlin
fun main() {
    println("Enter your age:")
    val ageText = readLine()
    val age = ageText?.toInt()

    println("Next year you'll be ${age?.plus(1)}")
}
```

**What's happening:**
- `readLine()` gives you a String
- `.toInt()` converts it to an Int
- Now you can do math with it!

---

### The `?.` Symbol (Null Safety Preview)

You might notice `?.` in the code above. Here's a simplified explanation:

`readLine()` *might* return nothing (called `null`) if something goes wrong. The `?.` says: "Only do this if the value isn't null."

**For now**, just remember to use `?.toInt()` when converting input to numbers. We'll dive deeper into null safety later!

---

## Simpler Pattern (For Beginners)

For our exercises, here's a clean pattern you can use:

```kotlin
fun main() {
    print("Enter your age: ")
    val age = readLine()!!.toInt()

    println("You are $age years old")
}
```

The `!!` says: "I'm confident this will work!" (Use it carefully in real apps, but it's fine for learning.)

---

## Interactive Coding Session

### Challenge 1: Personalized Greeting

Write a program that:
1. Asks for the user's name
2. Asks for their favorite food
3. Prints a message using both pieces of information

**Example:**
```kotlin
fun main() {
    println("What is your name?")
    val name = readLine()

    println("What is your favorite food?")
    val food = readLine()

    println("Nice to meet you, $name! I also love $food!")
}
```

**Your turn!** Try it in the code editor!

---

### Challenge 2: Simple Calculator

Write a program that:
1. Asks the user for two numbers
2. Adds them together
3. Prints the result

**Example:**
```kotlin
fun main() {
    println("Enter the first number:")
    val num1 = readLine()!!.toInt()

    println("Enter the second number:")
    val num2 = readLine()!!.toInt()

    val sum = num1 + num2

    println("$num1 + $num2 = $sum")
}
```

---

### Challenge 3: Age Calculator

Write a program that:
1. Asks for the user's birth year
2. Calculates their age (assume current year is 2024)
3. Tells them how old they are

**Example:**
```kotlin
fun main() {
    println("What year were you born?")
    val birthYear = readLine()!!.toInt()

    val currentYear = 2024
    val age = currentYear - birthYear

    println("You are approximately $age years old!")
}
```

---

## Using `print()` Instead of `println()`

Notice in some examples we use `print()` instead of `println()`?

**Difference:**
- `println()` moves to the next line *after* printing
- `print()` stays on the same line

**With `println()`:**
```
Enter your name:
> Jordan
```

**With `print()`:**
```
Enter your name: Jordan
```

The input appears on the same line! This often looks cleaner.

---

## Common Mistakes

### Mistake 1: Forgetting to Store the Input

❌ **Wrong:**
```kotlin
println("What's your name?")
readLine()  // Input is read but not saved anywhere!
println("Hello, $name")  // ERROR! What is 'name'?
```

✅ **Correct:**
```kotlin
println("What's your name?")
val name = readLine()  // Save it in a variable!
println("Hello, $name")
```

---

### Mistake 2: Trying to Do Math with String Input

❌ **Wrong:**
```kotlin
println("Enter a number:")
val num = readLine()
println(num + 10)  // ERROR! Can't add Int to String
```

✅ **Correct:**
```kotlin
println("Enter a number:")
val num = readLine()!!.toInt()  // Convert to Int first
println(num + 10)
```

---

### Mistake 3: Forgetting `!!` or `?.` with toInt()

❌ **Wrong:**
```kotlin
val num = readLine().toInt()  // ERROR!
```

✅ **Correct:**
```kotlin
val num = readLine()!!.toInt()  // Add !! or ?.
```

---

## Recap: What You've Learned

You now know:

1. **`readLine()`** gets input from the user
2. Input is **always a String** initially
3. Use **`.toInt()`** to convert text to numbers
4. Use **`!!`** with `toInt()` (for now)
5. **Store input in a variable** to use it later
6. Programs can now have **two-way conversations**!

---

## What's Next?

You can now create interactive programs that get user input and respond! Next, we'll learn about **functions** - reusable blocks of code that make your programs organized and powerful!

**Key Takeaways:**
- `readLine()` reads user input as text
- Always store input in a variable
- Convert strings to numbers with `.toInt()`
- Use `print()` for same-line prompts
- Interactive programs are way more fun!

---

Awesome work! Mark this complete and move to the next lesson!

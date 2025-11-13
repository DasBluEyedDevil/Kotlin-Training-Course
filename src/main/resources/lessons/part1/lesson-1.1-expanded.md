# Lesson 1.1: Introduction to Kotlin & Development Setup

**Estimated Time**: 45 minutes

---

## Topic Introduction

Welcome to your journey into programming with Kotlin! Whether you've never written a line of code before or you're coming from another programming language, this course will teach you everything you need to know to become a confident Kotlin developer.

In this first lesson, you'll learn what programming really means, why Kotlin is an excellent choice, and how to set up your development environment. By the end, you'll write and run your very first Kotlin program!

---

## The Concept

### What is Programming?

Think of programming like writing a recipe for a robot chef:

**Cooking with a Human Chef**:
- "Add some salt" (they know what "some" means)
- "Cook until golden brown" (they recognize golden brown)
- "Stir occasionally" (they decide when "occasionally" is)

**Cooking with a Robot Chef** (Programming):
- "Add exactly 5 grams of salt"
- "Cook for 8 minutes at 180°C"
- "Stir every 2 minutes for 10 seconds"

Computers are like robot chefs—they need **exact, unambiguous instructions**. Programming is the art of writing these instructions in a language computers can understand.

### What is a Programming Language?

You speak English (or another human language). Computers speak in binary—millions of 1s and 0s. Programming languages are the bridge:

```
You (Human)  →  [Programming Language]  →  Computer (Binary)
"Print Hello"  →  [Kotlin Compiler]  →  10101001001...
```

**Kotlin** is our bridge language. It's designed to be:
- **Readable**: Looks almost like English
- **Precise**: No ambiguity for the computer
- **Safe**: Catches mistakes before they cause problems

---

## Why Kotlin?

### The Kotlin Story

Kotlin was created by JetBrains (makers of IntelliJ IDEA) in 2011 and officially released in 2016. In 2017, Google announced Kotlin as an official language for Android development. In 2019, Google declared Kotlin the **preferred language** for Android.

### Kotlin's Superpowers

**1. Modern & Concise**

Compare Java vs Kotlin for the same task:

```java
// Java (verbose)
public class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
```

```kotlin
// Kotlin (concise)
data class Person(var name: String, var age: Int)
```

**Same functionality, 90% less code!**

**2. Null Safety Built-In**

One of the most common programming errors is the "null pointer exception" (trying to use something that doesn't exist). Kotlin prevents this at compile-time:

```kotlin
var name: String = "Alice"
name = null  // ❌ Compiler error: "Null can not be a value of a non-null type String"

var nullableName: String? = "Bob"
nullableName = null  // ✅ OK, we explicitly said this can be null
```

**3. Multiplatform**

Write code once, run it everywhere:
- **Android**: Mobile apps
- **JVM**: Backend servers, desktop apps
- **JavaScript**: Web frontend
- **Native**: iOS apps, embedded systems

### Industry Adoption

Companies using Kotlin:
- **Google**: Android OS and apps
- **Netflix**: Mobile apps
- **Uber**: Internal tools
- **Pinterest**: Mobile apps
- **Trello**: Android app
- **Coursera**: Android app
- **Evernote**: Android app

**Job Market**: Over 50,000 Kotlin developer jobs posted in 2024 (Indeed, LinkedIn).

---

## Setting Up Your Development Environment

You have two options: online playground (quick start) or full IDE (professional setup).

### Option 1: Kotlin Playground (Beginner-Friendly)

**Best for**: Complete beginners, trying Kotlin quickly

**Steps**:
1. Open your web browser
2. Go to [play.kotlinlang.org](https://play.kotlinlang.org/)
3. You'll see a code editor with example code
4. That's it! No installation needed.

**Try it now**: Type this code in the playground:

```kotlin
fun main() {
    println("Hello, Kotlin!")
}
```

Click the green **Run** button. You should see:
```
Hello, Kotlin!
```

✅ **Pros**: Instant start, no installation, great for learning
❌ **Cons**: Limited features, requires internet

---

### Option 2: IntelliJ IDEA (Professional Setup)

**Best for**: Serious learning, building real projects

**Steps**:

1. **Download IntelliJ IDEA Community Edition** (Free)
   - Go to [jetbrains.com/idea/download](https://www.jetbrains.com/idea/download/)
   - Choose your operating system
   - Download the **Community Edition** (free, open-source)

2. **Install IntelliJ IDEA**
   - **Windows**: Run the `.exe` installer, follow prompts
   - **macOS**: Drag the app to Applications folder
   - **Linux**: Extract the archive, run `bin/idea.sh`

3. **Create Your First Kotlin Project**

   a. Open IntelliJ IDEA

   b. Click **New Project**

   c. Select **Kotlin** from the left menu

   d. Choose **JVM | Application**

   e. Project settings:
      - **Name**: MyFirstKotlinProject
      - **Location**: Choose where to save (e.g., Documents/KotlinProjects)
      - **Build system**: Gradle Kotlin (recommended)
      - **JDK**: 17 or higher (IntelliJ will download if needed)
      - **Gradle DSL**: Kotlin

   f. Click **Create**

4. **Create Your First Kotlin File**

   a. In the Project panel (left side), navigate to:
      ```
      src → main → kotlin
      ```

   b. Right-click on `kotlin` folder

   c. Select **New → Kotlin Class/File**

   d. Choose **File**

   e. Name it `Main` (creates `Main.kt`)

5. **Write Your First Program**

```kotlin
fun main() {
    println("Hello, World!")
    println("My name is [Your Name]")
    println("I'm learning Kotlin!")
}
```

6. **Run Your Program**
   - Click the green play button (▶) next to `fun main()`
   - Or press **Ctrl+Shift+F10** (Windows/Linux) or **Cmd+Shift+R** (macOS)

**Output**:
```
Hello, World!
My name is [Your Name]
I'm learning Kotlin!
```

🎉 **Congratulations! You just ran your first Kotlin program!**

---

## Understanding Your First Program

Let's break down what you just wrote:

```kotlin
fun main() {
    println("Hello, World!")
}
```

### Line-by-Line Breakdown

**`fun main()`**:
- `fun` = keyword that declares a **function** (a reusable block of code)
- `main` = the name of this function (special name: every program starts here)
- `()` = parentheses hold parameters (inputs to the function—none in this case)

The `main` function is the **entry point** of every Kotlin program. Think of it as the front door—when you run your program, the computer enters through `main()`.

**`{` and `}`**:
- Curly braces create a **code block**
- Everything inside the braces is part of the `main` function

**`println("Hello, World!")`**:
- `println` = a built-in function that **print**s a **line** of text
- `"Hello, World!"` = a **string** (text) to print
- `;` is optional in Kotlin (unlike Java)

**How It Works**:
```
1. Computer starts program
   ↓
2. Finds main() function
   ↓
3. Executes code inside { }
   ↓
4. Calls println() function
   ↓
5. Displays "Hello, World!" on screen
   ↓
6. Program ends
```

---

## How Kotlin Code Becomes a Running Program

This is what happens when you click "Run":

```
Your Code (Main.kt)
        ↓
   [Kotlin Compiler]
        ↓
   Bytecode (.class files)
        ↓
   [Java Virtual Machine (JVM)]
        ↓
   Running Program (Output)
```

**Step-by-Step**:

1. **You write code** in a `.kt` file (Kotlin source file)
2. **Kotlin Compiler** translates your code into **bytecode**
3. **Bytecode** is a language the JVM understands
4. **JVM** (Java Virtual Machine) runs the bytecode
5. **Output** appears on your screen

**Why JVM?**
- JVM is incredibly mature and optimized (30+ years old)
- Works on Windows, macOS, Linux, and more
- Kotlin leverages all of Java's ecosystem

---

## Your First Interactive Program

Let's make something more interesting—a program that talks back!

```kotlin
fun main() {
    println("=== Kotlin Greeter ===")
    println("What's your name?")

    val name = readln()  // Reads user input

    println("Hello, $name!")
    println("Welcome to Kotlin programming!")

    println("\nHow old are you?")
    val age = readln().toInt()  // Reads input and converts to number

    val yearsTo100 = 100 - age
    println("You have $yearsTo100 years until you're 100 years old!")
}
```

**Run this program** and interact with it:

```
=== Kotlin Greeter ===
What's your name?
Alice
Hello, Alice!
Welcome to Kotlin programming!

How old are you?
25
You have 75 years until you're 100 years old!
```

### New Concepts Introduced

**`readln()`**:
- Reads a line of text from user input
- Waits for user to type something and press Enter

**`val name = readln()`**:
- `val` = declares a **val**ue (a named container for data)
- `name` = the name of this container
- `=` = assigns the result of `readln()` to `name`

**`"Hello, $name!"`**:
- `$name` = **string interpolation** (inserting a variable's value into text)
- Dollar sign tells Kotlin: "Replace this with the value of `name`"

**`toInt()`**:
- Converts text to an integer (whole number)
- `"25".toInt()` becomes `25` (number)

---

## Exercise 1: Personalized Greeting

**Goal**: Create a program that asks for name, favorite color, and hobby, then prints a personalized message.

**Requirements**:
1. Ask for the user's name
2. Ask for their favorite color
3. Ask for their hobby
4. Print: "Hi [name]! Your favorite color is [color] and you love [hobby]!"

**Starter Code**:
```kotlin
fun main() {
    println("What's your name?")
    val name = readln()

    // TODO: Ask for favorite color

    // TODO: Ask for hobby

    // TODO: Print personalized message
}
```

**Expected Output**:
```
What's your name?
Bob
What's your favorite color?
Blue
What's your hobby?
Photography
Hi Bob! Your favorite color is Blue and you love Photography!
```

---

## Solution: Personalized Greeting

```kotlin
fun main() {
    println("=== Personal Profile ===")

    println("What's your name?")
    val name = readln()

    println("What's your favorite color?")
    val color = readln()

    println("What's your hobby?")
    val hobby = readln()

    println("\n--- Your Profile ---")
    println("Hi $name! Your favorite color is $color and you love $hobby!")
}
```

**Explanation**:
- We use `val` three times to store three pieces of user input
- String interpolation (`$name`, `$color`, `$hobby`) inserts values into our message
- `\n` creates a blank line for better formatting

---

## Exercise 2: Simple Calculator

**Goal**: Create a calculator that adds two numbers.

**Requirements**:
1. Ask for first number
2. Ask for second number
3. Add them together
4. Print the result

**Hint**: Use `readln().toInt()` to read numbers.

---

## Solution: Simple Calculator

```kotlin
fun main() {
    println("=== Simple Calculator ===")

    println("Enter first number:")
    val num1 = readln().toInt()

    println("Enter second number:")
    val num2 = readln().toInt()

    val sum = num1 + num2

    println("$num1 + $num2 = $sum")
}
```

**Sample Run**:
```
=== Simple Calculator ===
Enter first number:
15
Enter second number:
27
15 + 27 = 42
```

**What's Happening**:
1. We read two numbers from the user
2. We add them: `val sum = num1 + num2`
3. We print the result with string interpolation

---

## Programming Best Practices (Start Building Good Habits!)

### 1. Use Meaningful Names

```kotlin
// ❌ Bad
val x = 25
val y = 30
val z = x + y

// ✅ Good
val width = 25
val height = 30
val area = width * height
```

### 2. Add Comments

```kotlin
// This program calculates the area of a rectangle
fun main() {
    val width = 25   // Width in meters
    val height = 30  // Height in meters
    val area = width * height

    println("Area: $area square meters")
}
```

**Comment Types**:
- `// Single-line comment`
- `/* Multi-line
     comment */`

### 3. Use Blank Lines for Readability

```kotlin
// ❌ Cramped
fun main() {
    println("What's your name?")
    val name = readln()
    println("Hello, $name!")
}

// ✅ Readable
fun main() {
    println("What's your name?")
    val name = readln()

    println("Hello, $name!")
}
```

---

## Common Beginner Mistakes

### Mistake 1: Forgetting Quotes Around Text

```kotlin
// ❌ Error
println(Hello)  // Compiler error: Unresolved reference

// ✅ Correct
println("Hello")  // Text must be in quotes
```

### Mistake 2: Wrong Capitalization

```kotlin
// ❌ Error
fun Main() {  // Capital M
    Println("Hello")  // Capital P
}

// ✅ Correct
fun main() {  // Lowercase m
    println("Hello")  // Lowercase p
}
```

Kotlin is **case-sensitive**: `main` ≠ `Main`.

### Mistake 3: Missing Parentheses

```kotlin
// ❌ Error
fun main {  // Missing ()
    println("Hello")
}

// ✅ Correct
fun main() {  // Parentheses required
    println("Hello")
}
```

---

## Checkpoint Quiz

Test your understanding of this lesson!

### Question 1
What does the `main` function do?

A) Displays output to the screen
B) Reads input from the user
C) Serves as the entry point where the program starts
D) Calculates mathematical operations

### Question 2
What does `println()` do?

A) Reads a line of input
B) Prints a line of text to the console
C) Creates a new variable
D) Ends the program

### Question 3
What is string interpolation?

A) Inserting variable values into text using `$variableName`
B) Connecting multiple strings with `+`
C) Converting text to numbers
D) Reading user input

### Question 4
Which symbol is used for comments in Kotlin?

A) `#`
B) `--`
C) `//`
D) `/* */` (both C and D are correct)

### Question 5
What does `readln().toInt()` do?

A) Prints an integer
B) Creates a random number
C) Reads user input and converts it to an integer
D) Adds two numbers together

---

## Quiz Answers

**Question 1: C) Serves as the entry point where the program starts**

The `main()` function is special—every Kotlin program begins execution here. When you run your program, the computer looks for `fun main()` and starts executing the code inside its curly braces.

```kotlin
fun main() {  // ← Program starts HERE
    println("First line executed")
    println("Second line executed")
}  // ← Program ends HERE
```

---

**Question 2: B) Prints a line of text to the console**

`println()` stands for "print line." It displays text on the screen and moves to the next line.

```kotlin
println("Hello")  // Prints "Hello" and moves to next line
println("World")  // Prints "World" on new line
```

Output:
```
Hello
World
```

---

**Question 3: A) Inserting variable values into text using `$variableName`**

String interpolation lets you embed variables directly in strings:

```kotlin
val name = "Alice"
val age = 25
println("My name is $name and I'm $age years old")
// Output: My name is Alice and I'm 25 years old
```

The `$` tells Kotlin to insert the variable's value.

---

**Question 4: D) `/* */` (both C and D are correct)**

Kotlin supports two comment styles:

```kotlin
// Single-line comment

/*
 Multi-line
 comment
 */
```

Comments are ignored by the compiler—they're for human readers only.

---

**Question 5: C) Reads user input and converts it to an integer**

```kotlin
val age = readln().toInt()
```

This does two things:
1. `readln()` reads text from user: `"25"`
2. `.toInt()` converts text to number: `25`

Without `.toInt()`, you'd have text, not a number you can do math with.

---

## What You've Learned

✅ What programming is (precise instructions for computers)
✅ Why Kotlin is an excellent language to learn
✅ How to set up your development environment (playground or IntelliJ IDEA)
✅ How to write and run your first Kotlin program
✅ Understanding `fun main()`, `println()`, and `readln()`
✅ String interpolation with `$variableName`
✅ Converting text to numbers with `.toInt()`
✅ Best practices: meaningful names, comments, readability

---

## Next Steps

In **Lesson 1.2: Variables, Data Types & Operators**, you'll learn:
- Different types of data (numbers, text, true/false)
- How to store and manipulate data in variables
- Mathematical and logical operations
- Type conversions and type safety

Get ready to dive deeper into the building blocks of programming!

---

## Additional Resources

**Official Kotlin Documentation**:
- [Kotlin Basics](https://kotlinlang.org/docs/basic-syntax.html)
- [Kotlin Playground](https://play.kotlinlang.org/)

**Community**:
- [Kotlin Slack](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up)
- [r/Kotlin on Reddit](https://www.reddit.com/r/Kotlin/)

**Practice**:
- [Kotlin Koans](https://play.kotlinlang.org/koans/overview) - Interactive exercises

---

**Congratulations on completing Lesson 1.1!** 🎉

You've taken your first steps into the world of programming. Every expert programmer started exactly where you are now. Keep going!

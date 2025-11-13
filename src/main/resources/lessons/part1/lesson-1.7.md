# Lesson 1.7: Reusable Recipes (Functions - Part 1)

## The Power of Reusability

Imagine you bake cookies every weekend. Each time, you follow the same steps:
1. Mix ingredients
2. Form dough balls
3. Bake at 350°F for 12 minutes
4. Let cool

Instead of writing these steps down every single time, you could create a **recipe card** labeled "Chocolate Chip Cookies" and just follow that card whenever you want cookies.

**That's exactly what functions do in programming!**

---

## What is a Function?

A **function** is a named block of code that performs a specific task. Once you create it, you can use it over and over without rewriting the code.

**Analogy:**
- **Recipe** = Function (a set of instructions with a name)
- **Following the recipe** = Calling the function (executing those instructions)

---

## You've Already Used Functions!

Remember `println()` and `readLine()`? Those are **functions**!

- Someone wrote the code for `println()` once
- Now you can use it anytime by just writing `println("Hello")`
- You don't need to know *how* it works internally - you just use it!

---

## Creating Your First Function

Here's how you create a simple function in Kotlin:

```kotlin
fun sayHello() {
    println("Hello!")
    println("Welcome to Kotlin!")
}

fun main() {
    sayHello()
}
```

**Output:**
```
Hello!
Welcome to Kotlin!
```

### Breaking It Down:

1. **`fun`** = Keyword that means "I'm creating a function"
2. **`sayHello`** = The name of your function
3. **`()`** = Parentheses (we'll use these for parameters soon!)
4. **`{ }`** = Curly braces contain the function's code
5. **`sayHello()`** in `main()` = Calling (using) the function

---

## Why Use Functions?

### Without Functions (Repetitive):

```kotlin
fun main() {
    println("===== WELCOME =====")
    println("Hello, user!")
    println("===================")

    // ... some code ...

    println("===== WELCOME =====")
    println("Hello, user!")
    println("===================")

    // ... more code ...

    println("===== WELCOME =====")
    println("Hello, user!")
    println("===================")
}
```

If you want to change the welcome message, you have to change it in THREE places!

---

### With Functions (Smart!):

```kotlin
fun showWelcome() {
    println("===== WELCOME =====")
    println("Hello, user!")
    println("===================")
}

fun main() {
    showWelcome()
    // ... some code ...
    showWelcome()
    // ... more code ...
    showWelcome()
}
```

Now if you want to change the message, you change it **once** in the function, and it updates everywhere!

---

## Multiple Functions

You can create as many functions as you need:

```kotlin
fun greet() {
    println("Hello!")
}

fun sayGoodbye() {
    println("Goodbye!")
}

fun main() {
    greet()
    println("How are you?")
    sayGoodbye()
}
```

**Output:**
```
Hello!
How are you?
Goodbye!
```

---

## The `main()` Function

You might have noticed we always write `fun main() { }`. Now you understand why!

**`main()` is a special function:**
- Every Kotlin program *must* have a `main()` function
- It's the **starting point** of your program
- When you click "Run," Kotlin looks for `main()` and starts there

Think of `main()` as the front door of your house. The program enters through `main()` and then can go to other rooms (other functions).

---

## Function Names: Best Practices

Follow these naming conventions:

1. **Use descriptive verb-based names**
   - ✅ `printMenu()`, `calculateTotal()`, `showWelcome()`
   - ❌ `menu()`, `total()`, `welcome()`

2. **Use camelCase**
   - ✅ `sayHello()`, `calculateSum()`
   - ❌ `say_hello()`, `CALCULATESUM()`

3. **Make it clear what the function does**
   - ✅ `displayUserProfile()`
   - ❌ `doStuff()`

---

## Interactive Coding Session

### Challenge 1: Create a Banner Function

Write a function called `printBanner()` that prints a decorative banner. Then call it from `main()`.

**Example:**
```kotlin
fun printBanner() {
    println("************************")
    println("*   KOTLIN IS AWESOME  *")
    println("************************")
}

fun main() {
    printBanner()
}
```

---

### Challenge 2: Multiple Functions

Create three functions:
1. `greetUser()` - Prints a greeting
2. `showMenu()` - Prints a simple menu
3. `sayGoodbye()` - Prints a goodbye message

Call all three from `main()` in order.

**Example:**
```kotlin
fun greetUser() {
    println("Welcome to our app!")
}

fun showMenu() {
    println("Menu:")
    println("1. Start")
    println("2. Exit")
}

fun sayGoodbye() {
    println("Thanks for visiting!")
}

fun main() {
    greetUser()
    showMenu()
    sayGoodbye()
}
```

---

### Challenge 3: Repeated Tasks

Create a function called `drawLine()` that prints a horizontal line like `"--------------------"`.

Use it to create a formatted output like this:

```
--------------------
Welcome!
--------------------
This is my program
--------------------
Goodbye!
--------------------
```

**Example:**
```kotlin
fun drawLine() {
    println("--------------------")
}

fun main() {
    drawLine()
    println("Welcome!")
    drawLine()
    println("This is my program")
    drawLine()
    println("Goodbye!")
    drawLine()
}
```

---

## Functions Can Call Other Functions

Functions can call other functions!

```kotlin
fun printHeader() {
    println("===== MY PROGRAM =====")
}

fun printFooter() {
    println("===== END =====")
}

fun showPage() {
    printHeader()
    println("This is the content.")
    printFooter()
}

fun main() {
    showPage()
}
```

**Output:**
```
===== MY PROGRAM =====
This is the content.
===== END =====
```

Notice how `showPage()` calls both `printHeader()` and `printFooter()`!

---

## Common Mistakes

### Mistake 1: Forgetting to Call the Function

❌ **Wrong:**
```kotlin
fun sayHello() {
    println("Hello!")
}

fun main() {
    // Function was created but never called!
}
```

**Output:** Nothing prints!

✅ **Correct:**
```kotlin
fun sayHello() {
    println("Hello!")
}

fun main() {
    sayHello()  // Call it!
}
```

---

### Mistake 2: Calling a Function Before Defining It

❌ **Wrong:**
```kotlin
fun main() {
    greet()  // ERROR! What is greet()?
}

fun greet() {
    println("Hello!")
}
```

**In Kotlin, this actually works!** Kotlin reads the entire file before running, so function order doesn't matter. But for readability, many programmers put `main()` last.

---

### Mistake 3: Forgetting Parentheses

❌ **Wrong:**
```kotlin
fun main() {
    sayHello  // ERROR! This is not a function call
}
```

✅ **Correct:**
```kotlin
fun main() {
    sayHello()  // The () makes it a function call
}
```

---

## Recap: What You've Learned

You now understand:

1. **Functions** = Named, reusable blocks of code
2. Create functions with **`fun functionName() { }`**
3. **Call functions** by writing their name with parentheses: `functionName()`
4. **`main()`** is the entry point of every program
5. Functions make code **organized, reusable, and easier to change**
6. Functions can call other functions

---

## What's Next?

Right now, our functions are like recipes that always make the same dish. But what if you want to customize them? In the next lesson, we'll learn about **parameters** - giving your functions "ingredients" to work with!

**Key Takeaways:**
- Functions let you reuse code without rewriting it
- `fun` keyword creates a function
- Call functions with their name + `()`
- `main()` is where every program starts
- Use descriptive, verb-based names
- Functions make your code clean and organized

---

Excellent work! Mark this complete and continue to the next lesson!

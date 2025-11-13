# Lesson 1.4: Labeled Boxes (Variables - Part 1)

## Storing Information

So far, you've learned to make your program "speak" using `println()`. But what if you want your program to **remember** things? That's where **variables** come in!

---

## The Labeled Box Analogy

Imagine you're moving to a new house. You pack your belongings into boxes and label them:

- A box labeled **"Books"** contains your books
- A box labeled **"Kitchen Stuff"** contains pots and pans
- A box labeled **"Photos"** contains your photo albums

**Key idea:** Each box has:
1. A **label** (name) so you know what's inside
2. **Contents** (the actual stuff)

**Variables work exactly the same way!**

A **variable** is like a labeled box in your computer's memory:
- The **label** is the variable's name
- The **contents** are the value stored inside

---

## Creating Your First Variable

In Kotlin, you create a variable like this:

```kotlin
val age = 25
```

Let's break this down:

- **`val`** = "variable" - tells Kotlin you're creating a box
- **`age`** = The name (label) of your box
- **`=`** = "equals" - means "put this value in the box"
- **`25`** = The value (contents) you're storing

**In plain English:** "Create a box labeled 'age' and put the number 25 inside it."

---

## Using Variables

Once you've created a variable, you can use it anywhere in your code by referencing its name:

```kotlin
fun main() {
    val age = 25
    println(age)
}
```

**Output:**
```
25
```

When the computer sees `age`, it looks inside the box labeled "age" and uses whatever it finds (which is 25).

---

## Why Are Variables Useful?

### Example: Without Variables (Repetitive and Error-Prone)

```kotlin
fun main() {
    println("I am 25 years old")
    println("In 5 years, I will be 30 years old")
    println("Next year, I will be 26 years old")
}
```

If you need to change your age, you have to manually update it in **every** place. Tedious!

---

### Example: With Variables (Smart and Flexible)

```kotlin
fun main() {
    val age = 25
    println("I am " + age + " years old")
    println("In 5 years, I will be " + (age + 5) + " years old")
    println("Next year, I will be " + (age + 1) + " years old")
}
```

**Output:**
```
I am 25 years old
In 5 years, I will be 30 years old
Next year, I will be 26 years old
```

Now if you need to change your age, you only update it **once** (in the `val age = 25` line), and everything else updates automatically!

---

## Combining Variables with Text

Notice the `+` symbol? In Kotlin, you can **combine** (concatenate) text and variables using `+`:

```kotlin
fun main() {
    val name = "Helia"
    println("Hello, my name is " + name)
}
```

**Output:**
```
Hello, my name is Helia
```

---

## String Templates (A Better Way!)

Kotlin has an even nicer way to insert variables into text using **string templates**:

Instead of:
```kotlin
println("My name is " + name)
```

You can write:
```kotlin
println("My name is $name")
```

Just put a **`$`** before the variable name inside the quotes!

### Full Example:

```kotlin
fun main() {
    val name = "Alex"
    val age = 30

    println("Hello! My name is $name and I am $age years old.")
}
```

**Output:**
```
Hello! My name is Alex and I am 30 years old.
```

**Much cleaner, right?**

---

## `val` vs `var` - Permanent vs Changeable Boxes

In Kotlin, there are two types of variables:

### `val` - A Permanent Box (Immutable)

Once you put something in a `val` box, **you can't change it**.

```kotlin
fun main() {
    val age = 25
    age = 26  // ❌ ERROR! Can't change a val
}
```

**Analogy:** Think of it like writing in permanent marker. Once it's written, it's permanent.

---

### `var` - A Changeable Box (Mutable)

With `var`, you **can** change the contents later.

```kotlin
fun main() {
    var age = 25
    println(age)  // Output: 25

    age = 26  // ✅ OK! We can change a var
    println(age)  // Output: 26
}
```

**Analogy:** Think of it like a whiteboard. You can erase and write new content.

---

## When to Use `val` vs `var`?

**Best practice:** Use `val` by default!

- **`val`** for things that shouldn't change (names, birth years, constants)
- **`var`** only when you *know* the value will change (counters, scores, etc.)

**Why?** Using `val` makes your code safer and easier to understand. If something is `val`, you know it won't suddenly change somewhere else in your code.

---

## Naming Your Variables

You can name variables almost anything, but there are rules:

### Rules:

1. **Must start with a letter** (not a number)
   - ✅ `age`, `name1`
   - ❌ `1name`

2. **Can contain letters, numbers, and underscores**
   - ✅ `first_name`, `age2`, `userName`
   - ❌ `first-name` (no hyphens!)

3. **Cannot use special Kotlin keywords**
   - ❌ `val`, `fun`, `if` (these are reserved words)

4. **Case-sensitive**
   - `age`, `Age`, and `AGE` are three **different** variables

---

### Best Practices for Naming:

1. **Use descriptive names**
   - ✅ `userName` (clear what it is)
   - ❌ `x` (unclear)

2. **Use camelCase for multi-word names**
   - ✅ `firstName`, `userAge`
   - ❌ `first_name` (works but less common in Kotlin)

3. **Make it readable**
   - ✅ `numberOfStudents`
   - ❌ `numStdnts`

---

## Interactive Coding Session

Time to practice! Use the code playground below.

### Challenge 1: Create and Print Variables

Write a program that:
1. Creates a variable for your name
2. Creates a variable for your age
3. Creates a variable for your favorite food
4. Prints a sentence using all three variables

**Example:**
```kotlin
fun main() {
    val name = "Jordan"
    val age = 28
    val favoriteFood = "pizza"

    println("My name is $name, I am $age years old, and I love $favoriteFood!")
}
```

**Your turn!** Write your own version and run it!

---

### Challenge 2: Math with Variables

Write a program that:
1. Creates a variable for the current year (e.g., 2024)
2. Creates a variable for your birth year
3. Calculates and prints your age

**Example:**
```kotlin
fun main() {
    val currentYear = 2024
    val birthYear = 1996
    val age = currentYear - birthYear

    println("I was born in $birthYear, so I am $age years old.")
}
```

**Your turn!** Try this with your own birth year!

---

### Challenge 3: Changing Values with `var`

Write a program that:
1. Creates a `var` variable for a score, starting at 0
2. Prints the score
3. Adds 10 to the score
4. Prints the new score
5. Adds 5 more to the score
6. Prints the final score

**Example:**
```kotlin
fun main() {
    var score = 0
    println("Starting score: $score")

    score = score + 10
    println("After earning 10 points: $score")

    score = score + 5
    println("After earning 5 more points: $score")
}
```

**Your turn!** Try this and experiment with different numbers!

---

## Common Mistakes

### Mistake 1: Forgetting to Declare the Variable

❌ **Wrong:**
```kotlin
age = 25  // Where does this go? Computer is confused!
```

✅ **Correct:**
```kotlin
val age = 25  // Create the box first!
```

---

### Mistake 2: Trying to Change a `val`

❌ **Wrong:**
```kotlin
val age = 25
age = 26  // ERROR!
```

✅ **Correct:**
```kotlin
var age = 25  // Use var if you need to change it!
age = 26
```

---

### Mistake 3: Using a Variable Before Creating It

❌ **Wrong:**
```kotlin
println(name)  // ERROR! What is 'name'?
val name = "Alex"
```

✅ **Correct:**
```kotlin
val name = "Alex"
println(name)  // Create the box before using it!
```

---

## Recap: What You've Learned

You now understand:

1. **Variables** = Labeled boxes that store information
2. **`val`** = Permanent (immutable) variables
3. **`var`** = Changeable (mutable) variables
4. **Use `$variableName`** to insert variables into strings
5. **Naming rules** and best practices
6. **Use `val` by default**, `var` only when needed

---

## What's Next?

You know how to create boxes and put things in them. But what *kinds* of things can you store? In the next lesson, we'll explore **data types** - the different categories of information you can store (numbers, text, true/false, and more)!

**Key Takeaways:**
- Variables store information in labeled "boxes"
- `val` = can't change, `var` = can change
- Use descriptive names for variables
- String templates (`$name`) make code cleaner
- Create variables before you use them!

---

Excellent work! Mark this lesson complete and continue to the next one!

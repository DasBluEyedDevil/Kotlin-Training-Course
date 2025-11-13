# Lesson 1.5: Types of Contents (Basic Data Types)

## What Can You Put in the Box?

In the last lesson, you learned about variables - labeled boxes that store information. Now let's talk about what *kinds* of things you can put in those boxes!

---

## The Concept of Data Types

Imagine you're organizing a warehouse:

- **Some boxes hold books** (fragile, specific size)
- **Some boxes hold clothes** (soft, foldable)
- **Some boxes hold electronics** (require care, specific handling)

You wouldn't try to fold a TV like a shirt, right? Different things need different treatment.

**Computers work the same way!**

Different types of information need to be stored and handled differently. These categories are called **data types**.

---

## The Four Main Data Types (For Now)

In Kotlin, here are the most common types you'll use:

| Type | What It Stores | Example |
|------|----------------|---------|
| **Int** | Whole numbers (integers) | `42`, `-10`, `0` |
| **Double** | Decimal numbers (floating-point) | `3.14`, `-0.5`, `2.0` |
| **String** | Text (characters) | `"Hello"`, `"Kotlin"` |
| **Boolean** | True or False | `true`, `false` |

Let's explore each one!

---

## 1. Int - Whole Numbers

**Int** (short for "integer") stores whole numbers - no decimal points.

```kotlin
fun main() {
    val age = 25
    val temperature = -5
    val score = 0

    println("Age: $age")
    println("Temperature: $temperature")
    println("Score: $score")
}
```

**Output:**
```
Age: 25
Temperature: -5
Score: 0
```

**Use cases:**
- Ages (25, not 25.5)
- Counts (5 apples, not 5.2 apples)
- Scores in games (100 points, not 100.7)

---

## 2. Double - Decimal Numbers

**Double** stores numbers with decimal points (also called "floating-point" numbers).

```kotlin
fun main() {
    val price = 19.99
    val temperature = 98.6
    val pi = 3.14159

    println("Price: $price")
    println("Temperature: $temperature")
    println("Pi: $pi")
}
```

**Output:**
```
Price: 19.99
Temperature: 98.6
Pi: 3.14159
```

**Use cases:**
- Money ($19.99)
- Measurements (5.8 feet)
- Scientific calculations (3.14159...)

---

## 3. String - Text

**String** stores text - any sequence of characters.

```kotlin
fun main() {
    val name = "Helia"
    val greeting = "Hello, World!"
    val address = "123 Main Street"

    println(name)
    println(greeting)
    println(address)
}
```

**Output:**
```
Helia
Hello, World!
123 Main Street
```

**Important:** Strings must be wrapped in **quotes** `" "`.

**Use cases:**
- Names
- Messages
- Email addresses
- Any text!

---

## 4. Boolean - True or False

**Boolean** stores only two values: `true` or `false`.

```kotlin
fun main() {
    val isRaining = true
    val hasLicense = false
    val isAdult = true

    println("Is it raining? $isRaining")
    println("Do I have a license? $hasLicense")
    println("Am I an adult? $isAdult")
}
```

**Output:**
```
Is it raining? true
Do I have a license? false
Am I an adult? true
```

**Note:** `true` and `false` are keywords - no quotes needed!

**Use cases:**
- Yes/no questions
- On/off states
- Pass/fail conditions

---

## Kotlin's Smart Type Inference

Here's something cool: Kotlin is **smart**. It can usually figure out the type automatically!

You don't have to tell it:

```kotlin
fun main() {
    val age = 25            // Kotlin knows this is an Int
    val price = 19.99       // Kotlin knows this is a Double
    val name = "Alex"       // Kotlin knows this is a String
    val isHappy = true      // Kotlin knows this is a Boolean
}
```

Kotlin looks at the value you assigned and says, "Ah, 25 is a whole number, so `age` must be an Int!"

---

## Explicitly Declaring Types (Optional)

Sometimes you might want to be explicit. You can do this:

```kotlin
fun main() {
    val age: Int = 25
    val price: Double = 19.99
    val name: String = "Alex"
    val isHappy: Boolean = true
}
```

The `: Int`, `: Double`, etc., explicitly tell Kotlin what type you want.

**When to use this?**
- When you want to be extra clear
- When the type isn't obvious from the value
- Usually, you can skip it and let Kotlin figure it out!

---

## Type Safety - Kotlin's Superpower

Here's why types matter: **Kotlin won't let you mix incompatible types.**

### This is an error:

```kotlin
val age: Int = "Twenty-five"  // ❌ ERROR!
```

**Why?** You told Kotlin `age` is an `Int` (number), but you tried to put a `String` (text) in it. That's like trying to pour water into a box labeled "books" - doesn't make sense!

### This is also an error:

```kotlin
val price: Double = "19.99"  // ❌ ERROR!
```

Even though "19.99" looks like a number, the quotes make it a **String**, not a **Double**.

✅ **Correct:**
```kotlin
val price: Double = 19.99  // No quotes = actual number
```

---

## Math with Numbers

You can do math with `Int` and `Double`:

```kotlin
fun main() {
    val a = 10
    val b = 5

    println("Addition: " + (a + b))        // 15
    println("Subtraction: " + (a - b))     // 5
    println("Multiplication: " + (a * b))  // 50
    println("Division: " + (a / b))        // 2
}
```

**Note the parentheses!** `(a + b)` ensures the math happens before combining with the string.

---

## String Operations

You can combine (concatenate) strings with `+`:

```kotlin
fun main() {
    val firstName = "Ada"
    val lastName = "Lovelace"
    val fullName = firstName + " " + lastName

    println(fullName)  // Output: Ada Lovelace
}
```

Or use string templates (cleaner!):

```kotlin
fun main() {
    val firstName = "Ada"
    val lastName = "Lovelace"

    println("Full name: $firstName $lastName")
}
```

---

## Interactive Coding Session

Time to practice!

### Challenge 1: Create Variables of Different Types

Write a program that creates:
1. An `Int` for your age
2. A `Double` for your height (in feet, like 5.9)
3. A `String` for your favorite movie
4. A `Boolean` for whether you like pizza

Then print them all with descriptive labels.

**Example:**
```kotlin
fun main() {
    val age = 30
    val height = 5.8
    val favoriteMovie = "The Matrix"
    val likesPizza = true

    println("Age: $age")
    println("Height: $height feet")
    println("Favorite movie: $favoriteMovie")
    println("Likes pizza: $likesPizza")
}
```

**Your turn!**

---

### Challenge 2: Do Some Math

Write a program that:
1. Creates two `Int` variables with any numbers
2. Calculates their sum, difference, product, and quotient
3. Prints all results

**Example:**
```kotlin
fun main() {
    val num1 = 20
    val num2 = 4

    println("$num1 + $num2 = ${num1 + num2}")
    println("$num1 - $num2 = ${num1 - num2}")
    println("$num1 * $num2 = ${num1 * num2}")
    println("$num1 / $num2 = ${num1 / num2}")
}
```

**Note:** `${num1 + num2}` is how you do math inside string templates!

---

### Challenge 3: Build a Profile

Create a mini profile program:
1. Store your name, age, favorite color, and whether you're learning Kotlin (true!)
2. Print a nicely formatted profile

**Example:**
```kotlin
fun main() {
    val name = "Jordan"
    val age = 28
    val favoriteColor = "blue"
    val learningKotlin = true

    println("===== PROFILE =====")
    println("Name: $name")
    println("Age: $age")
    println("Favorite Color: $favoriteColor")
    println("Learning Kotlin: $learningKotlin")
    println("===================")
}
```

---

## Common Mistakes

### Mistake 1: Putting Quotes Around Numbers

❌ **Wrong:**
```kotlin
val age = "25"  // This is a String, not an Int!
```

✅ **Correct:**
```kotlin
val age = 25  // No quotes for numbers
```

---

### Mistake 2: Trying to Do Math with Strings

❌ **Wrong:**
```kotlin
val result = "10" + "5"  // Result is "105" (text), not 15!
```

✅ **Correct:**
```kotlin
val result = 10 + 5  // Result is 15 (number)
```

---

### Mistake 3: Forgetting Quotes for Strings

❌ **Wrong:**
```kotlin
val name = Helia  // ERROR! Kotlin thinks Helia is a variable
```

✅ **Correct:**
```kotlin
val name = "Helia"  // Text needs quotes
```

---

## Recap: What You've Learned

You now understand:

1. **Int** - Whole numbers (42, -10)
2. **Double** - Decimal numbers (3.14, 19.99)
3. **String** - Text ("Hello", "Kotlin")
4. **Boolean** - True or false (true, false)
5. **Type inference** - Kotlin guesses the type for you
6. **Type safety** - Kotlin prevents type mismatches
7. You can do math with numbers and combine strings

---

## What's Next?

You can now store information and understand what type it is. Next, we'll learn how to get information **from the user** - making your programs truly interactive!

**Key Takeaways:**
- Every value has a type (Int, Double, String, Boolean)
- Kotlin can infer types automatically
- Numbers don't use quotes, strings do
- Type safety prevents bugs
- You can do math with numbers and combine strings

---

Great progress! Click "Mark Complete" and continue to the next lesson!

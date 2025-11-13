# Lesson 2.2: Combining Conditions (Logical Operators)

## Making Complex Decisions

In the last lesson, you learned to check single conditions. But real life often requires checking **multiple** conditions at once!

**Real-world examples:**
- "You can drive IF you have a license **AND** you are 16 or older"
- "You get a discount IF you are a student **OR** a senior"
- "The alarm goes off IF it's NOT a weekend"

Let's learn how to express these in code!

---

## The Three Logical Operators

| Operator | Name | Meaning | Example |
|----------|------|---------|---------|
| `&&` | AND | Both must be true | `age >= 18 && hasLicense` |
| `||` | OR | At least one must be true | `isStudent || isSenior` |
| `!` | NOT | Opposite/negation | `!isWeekend` |

---

## The AND Operator (`&&`)

**`&&`** means "both conditions must be true"

**Truth table:**
| Condition A | Condition B | A && B |
|-------------|-------------|--------|
| true | true | **true** |
| true | false | false |
| false | true | false |
| false | false | false |

### Example: Can You Drive?

```kotlin
fun main() {
    val age = 17
    val hasLicense = true

    if (age >= 16 && hasLicense) {
        println("You can drive!")
    } else {
        println("You cannot drive yet.")
    }
}
```

**Output:**
```
You can drive!
```

Both conditions are true (17 >= 16 AND hasLicense is true), so the code runs.

---

### Example 2: Login System

```kotlin
fun main() {
    val username = "admin"
    val password = "secret"

    if (username == "admin" && password == "secret") {
        println("✓ Login successful!")
    } else {
        println("✗ Invalid credentials")
    }
}
```

Both must match for successful login!

---

## The OR Operator (`||`)

**`||`** means "at least one condition must be true"

**Truth table:**
| Condition A | Condition B | A \|\| B |
|-------------|-------------|----------|
| true | true | **true** |
| true | false | **true** |
| false | true | **true** |
| false | false | false |

### Example: Discount Eligibility

```kotlin
fun main() {
    val isStudent = false
    val isSenior = true
    val isMilitary = false

    if (isStudent || isSenior || isMilitary) {
        println("You qualify for a discount!")
    } else {
        println("Regular price applies.")
    }
}
```

**Output:**
```
You qualify for a discount!
```

Only ONE needs to be true (isSenior is true).

---

### Example 2: Weekend Check

```kotlin
fun main() {
    val day = "Saturday"

    if (day == "Saturday" || day == "Sunday") {
        println("It's the weekend! 🎉")
    } else {
        println("It's a weekday. 😴")
    }
}
```

---

## The NOT Operator (`!`)

**`!`** means "opposite" or "not"

**Truth table:**
| Condition | !Condition |
|-----------|------------|
| true | false |
| false | true |

### Example: Alarm System

```kotlin
fun main() {
    val isWeekend = false

    if (!isWeekend) {
        println("⏰ Alarm is ON - it's a weekday!")
    } else {
        println("😴 Sleep in - it's the weekend!")
    }
}
```

**Output:**
```
⏰ Alarm is ON - it's a weekday!
```

`!isWeekend` is true because isWeekend is false (NOT false = true).

---

### Example 2: Not Equal

```kotlin
fun main() {
    val status = "pending"

    if (status != "completed") {
        println("Task is not yet complete.")
    }
}
```

`!=` is like using `!` with `==`.

---

## Combining Multiple Operators

You can combine AND, OR, and NOT in complex expressions!

### Example: Can You Vote?

```kotlin
fun main() {
    val age = 19
    val isCitizen = true
    val isRegistered = true

    if (age >= 18 && isCitizen && isRegistered) {
        println("You can vote!")
    } else {
        println("You cannot vote.")
    }
}
```

**ALL three must be true.**

---

### Example 2: Emergency Contact

```kotlin
fun main() {
    val isEmergency = true
    val isDay = false
    val isNight = true

    if (isEmergency && (isDay || isNight)) {
        println("Calling emergency services!")
    }
}
```

**Parentheses group conditions:** Emergency AND (day OR night).

---

## Operator Precedence

Just like math (multiply before add), logical operators have order:

1. **`!`** (NOT) - highest priority
2. **`&&`** (AND)
3. **`||`** (OR) - lowest priority

**Example:**
```kotlin
val result = true || false && false
// Evaluates as: true || (false && false)
// Result: true
```

**Best practice:** Use parentheses to make it clear!

```kotlin
val result = true || (false && false)  // Much clearer!
```

---

## Interactive Coding Session

### Challenge 1: Movie Ticket Price

Write a program that determines ticket price:
- Age < 12 or age >= 65: $8 (child/senior discount)
- Age 12-64: $12 (regular price)

```kotlin
fun main() {
    print("Enter your age: ")
    val age = readLine()!!.toInt()

    if (age < 12 || age >= 65) {
        println("Ticket price: $8")
    } else {
        println("Ticket price: $12")
    }
}
```

---

### Challenge 2: Password Validator

Create a password validator that checks:
- Length >= 8 characters
- Contains at least one number

For now, just check length (we'll learn string functions later):

```kotlin
fun main() {
    print("Enter password: ")
    val password = readLine()!!

    if (password.length >= 8) {
        println("✓ Password is strong enough")
    } else {
        println("✗ Password too short (min 8 characters)")
    }
}
```

---

### Challenge 3: Can You Ride the Roller Coaster?

Requirements:
- Must be at least 48 inches tall
- Must be at least 8 years old OR accompanied by an adult

```kotlin
fun main() {
    print("Height in inches: ")
    val height = readLine()!!.toInt()

    print("Age: ")
    val age = readLine()!!.toInt()

    print("Accompanied by adult? (yes/no): ")
    val hasAdult = readLine() == "yes"

    if (height >= 48 && (age >= 8 || hasAdult)) {
        println("🎢 You can ride!")
    } else {
        println("❌ Sorry, you don't meet requirements")
    }
}
```

---

## Short-Circuit Evaluation

**Important:** `&&` and `||` use "short-circuit" logic:

### With `&&`:
If the first condition is FALSE, the second isn't even checked!

```kotlin
if (false && expensiveFunction()) {
    // expensiveFunction() never runs!
}
```

### With `||`:
If the first condition is TRUE, the second isn't checked!

```kotlin
if (true || expensiveFunction()) {
    // expensiveFunction() never runs!
}
```

**Why this matters:** Saves performance and prevents errors!

```kotlin
if (name != null && name.length > 0) {
    // Safe! name.length only checked if name isn't null
}
```

---

## Common Mistakes

### Mistake 1: Using Single `&` or `|`

❌ **Wrong:**
```kotlin
if (age >= 18 & hasLicense) {  // Single & is bitwise!
    // ...
}
```

✅ **Correct:**
```kotlin
if (age >= 18 && hasLicense) {  // Double &&
    // ...
}
```

---

### Mistake 2: Confusing AND with OR

Think carefully about what you need:

❌ **Wrong logic:**
```kotlin
// Want: "weekend is Saturday or Sunday"
if (day == "Saturday" && day == "Sunday") {  // Impossible!
    // day can't be both!
}
```

✅ **Correct:**
```kotlin
if (day == "Saturday" || day == "Sunday") {
    // Either one works!
}
```

---

### Mistake 3: Too Many Negations

❌ **Confusing:**
```kotlin
if (!(!(isActive))) {  // Too many !
    // ...
}
```

✅ **Clear:**
```kotlin
if (isActive) {
    // ...
}
```

---

## Recap: What You've Learned

You now understand:

1. **`&&` (AND)** = Both must be true
2. **`||` (OR)** = At least one must be true
3. **`!` (NOT)** = Opposite/negation
4. **Combining operators** for complex logic
5. **Parentheses** for grouping
6. **Short-circuit evaluation** for efficiency

---

## What's Next?

You can now make complex decisions with multiple conditions! Next, we'll learn about the **`when` expression** - Kotlin's powerful alternative to long `if-else` chains!

**Key Takeaways:**
- `&&` requires all conditions true
- `||` requires at least one true
- `!` negates/flips a condition
- Use parentheses for clarity
- Short-circuit evaluation is automatic
- Think carefully about AND vs OR logic

---

Excellent work! Continue to the next lesson to learn about `when` expressions!

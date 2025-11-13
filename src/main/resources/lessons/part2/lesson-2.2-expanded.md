# Lesson 2.2: Combining Conditions - Logical Operators

**Estimated Time**: 55 minutes
**Difficulty**: Beginner
**Prerequisites**: Lesson 2.1 (If statements)

---

## Topic Introduction

In the last lesson, you learned to make decisions with if statements and simple conditions. But real-world decisions often involve **multiple conditions** working together:

- "**IF** it's raining **AND** I don't have an umbrella, **THEN** I'll get wet"
- "**IF** you're under 13 **OR** over 65, **THEN** you get a discount"
- "**IF** the door is **NOT** locked, **THEN** you can enter"

Notice the words **AND**, **OR**, and **NOT**? These are **logical operators**, and they let you combine and modify conditions to create more sophisticated decision-making logic.

In this lesson, you'll learn:
- The three logical operators: AND (`&&`), OR (`||`), and NOT (`!`)
- How to combine multiple conditions
- Truth tables and how logical operators work
- Short-circuit evaluation for efficiency
- Common patterns and best practices
- How to simplify complex conditional logic

By the end, you'll write elegant code that handles complex real-world scenarios!

---

## The Concept: Logical Operators

### Real-World Logic

Think about these everyday decisions:

**AND logic (both must be true):**
```
To withdraw money from ATM:
- You must have your card AND
- You must know your PIN
(If either is missing, you can't withdraw)
```

**OR logic (at least one must be true):**
```
To enter the VIP lounge:
- You must be a premium member OR
- You must have a VIP ticket
(Either one gets you in)
```

**NOT logic (invert/flip the condition):**
```
IF the alarm is NOT set:
    You can leave without disabling it
```

Programming uses these exact same patterns!

### The Three Logical Operators

| Operator | Name | Symbol | Meaning |
|----------|------|--------|---------|
| AND | Logical AND | `&&` | Both conditions must be true |
| OR | Logical OR | `\|\|` | At least one condition must be true |
| NOT | Logical NOT | `!` | Inverts/flips the condition |

---

## The AND Operator (&&)

The AND operator (`&&`) returns `true` only when **BOTH** conditions are true.

### Truth Table for AND

| Condition A | Condition B | A && B |
|-------------|-------------|--------|
| true | true | **true** |
| true | false | false |
| false | true | false |
| false | false | false |

**Think of it as:** "This **AND** that" - you need **both**.

### Basic AND Example

```kotlin
fun main() {
    val hasTicket = true
    val hasID = true

    if (hasTicket && hasID) {
        println("Welcome to the concert!")
    } else {
        println("Sorry, you need both a ticket and ID")
    }
}
```

**Output:**
```
Welcome to the concert!
```

**What if hasID was false?**
```kotlin
val hasTicket = true
val hasID = false

if (hasTicket && hasID) {
    println("Welcome to the concert!")  // SKIPPED
} else {
    println("Sorry, you need both a ticket and ID")  // EXECUTES
}
```

**Output:**
```
Sorry, you need both a ticket and ID
```

### Real-World AND Examples

**Example 1: Age and license check**
```kotlin
fun main() {
    val age = 25
    val hasLicense = true

    if (age >= 16 && hasLicense) {
        println("You can drive legally!")
    } else {
        println("You cannot drive")
    }
}
```

**Example 2: Login validation**
```kotlin
fun main() {
    val username = "admin"
    val password = "secret123"

    if (username == "admin" && password == "secret123") {
        println("Login successful!")
    } else {
        println("Invalid username or password")
    }
}
```

**Example 3: Range check (value between two numbers)**
```kotlin
fun main() {
    val temperature = 72

    if (temperature >= 65 && temperature <= 75) {
        println("Perfect temperature!")
    } else {
        println("Too hot or too cold")
    }
}
```

### Chaining Multiple AND Conditions

You can chain more than two conditions:

```kotlin
fun main() {
    val hasPassport = true
    val hasVisa = true
    val hasTicket = true

    if (hasPassport && hasVisa && hasTicket) {
        println("You're ready for international travel!")
    } else {
        println("Missing required documents")
    }
}
```

All three conditions must be true for the message to print.

---

## The OR Operator (||)

The OR operator (`||`) returns `true` when **AT LEAST ONE** condition is true.

### Truth Table for OR

| Condition A | Condition B | A \|\| B |
|-------------|-------------|----------|
| true | true | **true** |
| true | false | **true** |
| false | true | **true** |
| false | false | false |

**Think of it as:** "This **OR** that" - you need **at least one**.

### Basic OR Example

```kotlin
fun main() {
    val isPremiumMember = false
    val hasVIPPass = true

    if (isPremiumMember || hasVIPPass) {
        println("Welcome to the VIP lounge!")
    } else {
        println("Standard access only")
    }
}
```

**Output:**
```
Welcome to the VIP lounge!
```

Even though `isPremiumMember` is false, `hasVIPPass` is true, so the condition succeeds!

### Real-World OR Examples

**Example 1: Weekend check**
```kotlin
fun main() {
    val day = "Saturday"

    if (day == "Saturday" || day == "Sunday") {
        println("It's the weekend! Relax!")
    } else {
        println("It's a weekday. Time to work!")
    }
}
```

**Example 2: Discount eligibility**
```kotlin
fun main() {
    val age = 70
    val isStudent = false

    if (age < 13 || age > 65 || isStudent) {
        println("You qualify for a discount!")
    } else {
        println("Regular price applies")
    }
}
```

**Output:**
```
You qualify for a discount!
```

The person is over 65, so they qualify (even though they're not a student).

**Example 3: Emergency access**
```kotlin
fun main() {
    val isAdmin = false
    val isEmergency = true

    if (isAdmin || isEmergency) {
        println("Access granted")
    } else {
        println("Access denied")
    }
}
```

---

## The NOT Operator (!)

The NOT operator (`!`) **inverts** (flips) a Boolean value.

### Truth Table for NOT

| Condition | !Condition |
|-----------|------------|
| true | **false** |
| false | **true** |

**Think of it as:** "The opposite of..."

### Basic NOT Example

```kotlin
fun main() {
    val isRaining = false

    if (!isRaining) {
        println("You don't need an umbrella!")
    }
}
```

**Output:**
```
You don't need an umbrella!
```

`isRaining` is false, so `!isRaining` becomes true, and the if block executes.

### Real-World NOT Examples

**Example 1: Checking if not equal**
```kotlin
fun main() {
    val status = "pending"

    if (!(status == "completed")) {
        println("Task is still in progress")
    }
}
```

**Note:** This is the same as `status != "completed"`. The `!=` operator is actually a shorthand for `!(... == ...)`.

**Example 2: Door lock check**
```kotlin
fun main() {
    val isDoorLocked = false

    if (!isDoorLocked) {
        println("You can open the door")
    } else {
        println("Door is locked, use your key")
    }
}
```

**Example 3: Inverting complex conditions**
```kotlin
fun main() {
    val hasPermission = true
    val isBanned = false

    if (hasPermission && !isBanned) {
        println("Access granted")
    }
}
```

**Output:**
```
Access granted
```

---

## Combining Logical Operators

You can combine AND, OR, and NOT in the same expression!

### Example: Comprehensive Access Control

```kotlin
fun main() {
    val age = 17
    val hasParentConsent = true
    val isVIP = false

    if ((age >= 18 || hasParentConsent) && !isVIP) {
        println("Standard access granted")
    }
}
```

**How it evaluates:**
1. `age >= 18` → `17 >= 18` → false
2. `hasParentConsent` → true
3. `false || true` → **true** (at least one is true)
4. `!isVIP` → `!false` → **true**
5. `true && true` → **true** (both parts are true)
6. Execute the if block

### Order of Operations (Precedence)

Just like math has PEMDAS, logical operators have precedence:

1. **`!` (NOT)** - Highest priority
2. **`&&` (AND)** - Medium priority
3. **`||` (OR)** - Lowest priority

**Example:**
```kotlin
val result = !false && true || false
```

**Evaluation order:**
1. `!false` → true (NOT first)
2. `true && true` → true (AND second)
3. `true || false` → true (OR last)

**Use parentheses for clarity:**
```kotlin
val result = ((!false) && true) || false  // Much clearer!
```

### Complex Real-World Example: Movie Ticket Eligibility

```kotlin
fun main() {
    val age = 16
    val hasParentConsent = true
    val isMatinee = false
    val isMember = true

    // Movie is R-rated, requires 17+ OR 13-16 with parent consent
    // Additionally, members get access to any showing, non-members only matinee
    val canWatch = (age >= 17 || (age >= 13 && hasParentConsent)) &&
                   (isMember || isMatinee)

    if (canWatch) {
        println("Enjoy the movie!")
    } else {
        println("Cannot watch this movie")
    }
}
```

**Breaking it down:**
- Age check: `age >= 17` is false, but `age >= 13 && hasParentConsent` is true → **passes**
- Showing access: `isMember` is true → **passes**
- Both conditions pass → **can watch!**

---

## Short-Circuit Evaluation

This is an important optimization that logical operators use:

### AND Short-Circuit

With `&&`, if the **first** condition is false, the second condition **isn't even checked** (because the result will be false regardless).

```kotlin
fun main() {
    val a = false
    val b = expensiveFunction()  // This would take 10 seconds

    if (a && b) {
        println("Both true")
    }
}
```

Since `a` is false, `b` is **never evaluated**! This saves time.

**Practical example:**
```kotlin
fun main() {
    val numbers = listOf<Int>()  // Empty list

    if (numbers.isNotEmpty() && numbers[0] > 10) {
        println("First element is greater than 10")
    }
}
```

If the list is empty, `numbers[0]` would crash the program! But short-circuit evaluation saves us—it never checks `numbers[0]` because `numbers.isNotEmpty()` is already false.

### OR Short-Circuit

With `||`, if the **first** condition is true, the second condition **isn't checked** (because the result will be true regardless).

```kotlin
fun main() {
    val isAdmin = true
    val hasSpecialPermission = expensiveCheck()  // Would take time

    if (isAdmin || hasSpecialPermission) {
        println("Access granted")
    }
}
```

Since `isAdmin` is true, `hasSpecialPermission` is **never checked**!

**Important:** Be careful with side effects! Don't put critical code in conditions that might not execute:

❌ **WRONG:**
```kotlin
if (isLoggedIn || performLogin()) {  // performLogin might not run!
    // ...
}
```

---

## Hands-On Practice

### Exercise 1: Age and Height Restriction

**Challenge:** An amusement park ride requires:
- Age >= 12 AND height >= 48 inches

Write a program that checks if someone can ride.

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val age = 14
    val heightInches = 50

    if (age >= 12 && heightInches >= 48) {
        println("You can ride the roller coaster!")
    } else {
        println("Sorry, you don't meet the requirements")
    }
}
```

**Output:**
```
You can ride the roller coaster!
```

**Both conditions must be true:**
- `14 >= 12` → true
- `50 >= 48` → true
- `true && true` → true
</details>

---

### Exercise 2: Weekend or Holiday

**Challenge:** Write a program that prints "Day off!" if it's either:
- Saturday OR Sunday OR a holiday

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val day = "Friday"
    val isHoliday = true

    if (day == "Saturday" || day == "Sunday" || isHoliday) {
        println("Day off!")
    } else {
        println("Work day")
    }
}
```

**Output:**
```
Day off!
```

**At least one condition is true:**
- `day == "Saturday"` → false
- `day == "Sunday"` → false
- `isHoliday` → true
- `false || false || true` → true
</details>

---

### Exercise 3: Password Validation

**Challenge:** Create a password validator that checks if a password is valid. A valid password must:
- Be at least 8 characters long AND
- NOT be "password123" (too common)

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val password = "mySecurePass"

    if (password.length >= 8 && password != "password123") {
        println("Password is valid")
    } else {
        println("Password is invalid")
    }
}
```

**Output:**
```
Password is valid
```

**Evaluation:**
- `password.length >= 8` → `12 >= 8` → true
- `password != "password123"` → true
- `true && true` → true
</details>

---

### Exercise 4: Temperature Alert System

**Challenge:** Write a program that alerts if temperature is dangerous:
- Below 32°F (freezing) OR above 100°F (heat danger)

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val temperature = 28

    if (temperature < 32 || temperature > 100) {
        println("⚠️ Temperature alert! Take precautions.")
    } else {
        println("Temperature is in safe range")
    }
}
```

**Output:**
```
⚠️ Temperature alert! Take precautions.
```

**Evaluation:**
- `28 < 32` → true
- `28 > 100` → false
- `true || false` → true
</details>

---

## Common Pitfalls and Best Practices

### Pitfall 1: Confusing && with ||

❌ **WRONG (wants AND but uses OR):**
```kotlin
val age = 10
val hasLicense = true

if (age >= 16 || hasLicense) {  // WRONG! This allows 10-year-old with license
    println("Can drive")
}
```

✅ **CORRECT:**
```kotlin
if (age >= 16 && hasLicense) {  // Both conditions required
    println("Can drive")
}
```

### Pitfall 2: Redundant Comparisons

❌ **Redundant:**
```kotlin
if (isLoggedIn == true) {  // Unnecessary comparison
    // ...
}
```

✅ **Clean:**
```kotlin
if (isLoggedIn) {  // Boolean already true/false
    // ...
}
```

❌ **Redundant:**
```kotlin
if (!isLoggedIn == true) {  // Confusing!
    // ...
}
```

✅ **Clean:**
```kotlin
if (!isLoggedIn) {
    // ...
}
```

### Pitfall 3: Complex Nested Conditions

❌ **Hard to read:**
```kotlin
if (a && b || c && !d && e || f) {
    // What does this even mean?
}
```

✅ **Use variables for clarity:**
```kotlin
val hasBasicAccess = a && b
val hasSpecialAccess = c && !d && e
val isVIP = f

if (hasBasicAccess || hasSpecialAccess || isVIP) {
    // Much clearer!
}
```

### Best Practice 1: Use Parentheses for Complex Logic

Make your intent crystal clear:

```kotlin
if ((age >= 18 || hasParentConsent) && !isBanned) {
    // Clear: (age check) AND (not banned)
}
```

### Best Practice 2: DeMorgan's Laws

Sometimes you can simplify logic using DeMorgan's Laws:

**DeMorgan's Law 1:**
```
!(A && B) equals (!A || !B)
```

**DeMorgan's Law 2:**
```
!(A || B) equals (!A && !B)
```

**Example:**
```kotlin
// These are equivalent:
if (!(isWeekend && isHoliday)) { /* ... */ }
if (!isWeekend || !isHoliday) { /* ... */ }
```

---

## Quick Quiz

**Question 1:** What will this code print?
```kotlin
val a = true
val b = false
if (a && b) {
    println("A")
} else {
    println("B")
}
```

<details>
<summary>Answer</summary>

**Output:** `B`

**Explanation:** `true && false` is false, so the else block executes.
</details>

---

**Question 2:** What will this code print?
```kotlin
val x = 5
val y = 10
if (x > 0 || y < 0) {
    println("Yes")
} else {
    println("No")
}
```

<details>
<summary>Answer</summary>

**Output:** `Yes`

**Explanation:**
- `x > 0` → `5 > 0` → true
- `y < 0` → `10 < 0` → false
- `true || false` → true

At least one condition is true, so "Yes" prints.
</details>

---

**Question 3:** Simplify this condition:
```kotlin
if (!(age < 18)) {
    println("Adult")
}
```

<details>
<summary>Answer</summary>

**Simplified:**
```kotlin
if (age >= 18) {
    println("Adult")
}
```

**Explanation:** "NOT less than 18" is the same as "greater than or equal to 18".
</details>

---

## Summary

Congratulations! You've mastered logical operators. Let's recap:

**Key Concepts:**
- **AND (`&&`)**: Both conditions must be true
- **OR (`||`)**: At least one condition must be true
- **NOT (`!`)**: Inverts/flips a Boolean value
- **Short-circuit evaluation**: Optimization that skips unnecessary checks
- **Precedence**: `!` → `&&` → `||` (use parentheses for clarity)

**Truth Tables:**
```
AND (&&)           OR (||)            NOT (!)
T && T = T         T || T = T         !T = F
T && F = F         T || F = T         !F = T
F && T = F         F || T = T
F && F = F         F || F = F
```

**Common Patterns:**
```kotlin
// Range check
if (x >= min && x <= max) { }

// Multiple options
if (option1 || option2 || option3) { }

// Exclusion check
if (condition && !exception) { }

// Complex logic
if ((condition1 || condition2) && !condition3) { }
```

**Best Practices:**
- Use parentheses to make complex conditions clear
- Extract complex logic into named Boolean variables
- Remember short-circuit evaluation for efficiency
- Avoid redundant comparisons with Boolean variables

---

## What's Next?

You can now combine multiple conditions, but what if you have many different cases to check? "If grade is A, print this. If B, print that. If C, print something else..."

In the next lesson, you'll learn the **when expression**—Kotlin's elegant way to handle multiple specific cases without writing long if-else chains!

**Preview:**
```kotlin
when (grade) {
    'A' -> println("Excellent!")
    'B' -> println("Great!")
    'C' -> println("Good!")
    else -> println("Keep trying!")
}
```

---

**Excellent progress! Mark this lesson complete and continue to Lesson 2.3!** 🎉

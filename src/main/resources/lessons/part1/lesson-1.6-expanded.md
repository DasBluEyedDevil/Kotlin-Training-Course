# Lesson 1.6: Null Safety & Safe Calls

**Estimated Time**: 55 minutes

---

## Topic Introduction

One of the most common bugs in programming is the dreaded **NullPointerException** (NPE)—trying to use something that doesn't exist. It's been called the "billion-dollar mistake" by its inventor, Tony Hoare.

Kotlin solves this problem with its **null safety** system. The compiler prevents most null-related crashes at compile-time, not runtime. This lesson teaches you how to safely work with values that might not exist.

---

## The Concept

### The Box Analogy

Think of variables as boxes that can hold values:

**Regular Box (Non-Nullable)**:
- Must always contain something
- Opening it always gives you a value
- Safe to use anytime

```kotlin
val name: String = "Alice"  // Box guaranteed to have a String
println(name.length)  // Safe!
```

**Special Box (Nullable)**:
- Might contain something, might be empty
- Must check before using
- Prevents surprises

```kotlin
val name: String? = null  // Box might be empty
// println(name.length)  // ❌ Compiler error: might be null!
println(name?.length)  // ✅ Safe: checks first
```

---

## Understanding Null

### What is null?

`null` represents **absence of a value**—nothing, empty, doesn't exist.

**Real-World Examples**:
- Phone number field when user hasn't provided one
- Middle name when person doesn't have one
- Search result when nothing matches
- User session when not logged in

### The Problem with Null (in other languages)

```java
// Java example - this crashes at runtime!
String name = null;
int length = name.length();  // NullPointerException!
```

**In Kotlin**: This doesn't compile! The compiler catches it.

---

## Nullable vs Non-Nullable Types

### Non-Nullable Types (Default)

```kotlin
var name: String = "Alice"
name = "Bob"  // ✅ OK
// name = null  // ❌ Compiler error: Null cannot be a value of a non-null type String
```

**By default, all types in Kotlin are non-nullable.**

### Nullable Types (Type?)

Add `?` to make a type nullable:

```kotlin
var name: String? = "Alice"
name = "Bob"   // ✅ OK
name = null    // ✅ OK (now null is allowed)
```

**Examples**:
```kotlin
val age: Int = 25       // Cannot be null
val age: Int? = null    // Can be null

val price: Double = 19.99  // Cannot be null
val price: Double? = null  // Can be null

val isActive: Boolean = true  // Cannot be null
val isActive: Boolean? = null // Can be null
```

---

## Safe Call Operator (?.)

The safe call operator `?.` safely accesses properties/methods on nullable objects.

### Basic Usage

```kotlin
val name: String? = "Alice"

// Without safe call - compiler error
// val length = name.length  // ❌ Error: name might be null

// With safe call - returns null if name is null
val length = name?.length  // ✅ OK: returns Int? (5 or null)

println(length)  // 5
```

### How it Works

```kotlin
val name: String? = null
val length = name?.length

// Equivalent to:
val length = if (name != null) name.length else null

println(length)  // null
```

**If the object is null, the entire expression returns null.**

### Chaining Safe Calls

```kotlin
data class Address(val street: String, val city: String)
data class Person(val name: String, val address: Address?)

val person: Person? = Person("Alice", null)

// Chain safe calls
val city = person?.address?.city

println(city)  // null
```

### Safe Calls with Methods

```kotlin
val text: String? = "  Hello  "

println(text?.trim())       // "Hello"
println(text?.uppercase())  // "HELLO"

val nullText: String? = null
println(nullText?.trim())   // null
```

---

## Elvis Operator (?:)

The Elvis operator `?:` provides a default value when something is null.

### Basic Usage

```kotlin
val name: String? = null
val displayName = name ?: "Unknown"

println(displayName)  // "Unknown"
```

**How it works**:
```
value ?: default
// If value is not null, use value
// If value is null, use default
```

### Real-World Examples

```kotlin
fun greet(name: String?) {
    val greeting = "Hello, ${name ?: "Guest"}!"
    println(greeting)
}

fun main() {
    greet("Alice")  // Hello, Alice!
    greet(null)     // Hello, Guest!
}
```

### Combining Safe Call and Elvis

```kotlin
val text: String? = null
val length = text?.length ?: 0

println(length)  // 0 (default value)

val text2: String? = "Hello"
val length2 = text2?.length ?: 0

println(length2)  // 5
```

### Elvis with Expressions

```kotlin
fun getDiscount(customerType: String?): Double {
    return when (customerType ?: "regular") {
        "premium" -> 0.20
        "gold" -> 0.15
        else -> 0.05
    }
}

fun main() {
    println(getDiscount("premium"))  // 0.2
    println(getDiscount(null))       // 0.05 (uses default "regular")
}
```

---

## Not-Null Assertion (!!)

The `!!` operator tells the compiler "I'm sure this isn't null!"

### When to Use (Rarely!)

```kotlin
val name: String? = "Alice"
val length = name!!.length  // "I guarantee name is not null!"

println(length)  // 5
```

### Danger: It Can Crash!

```kotlin
val name: String? = null
val length = name!!.length  // 💥 NullPointerException!
```

**When to use `!!`**:
- You're absolutely certain the value isn't null
- In test code
- Rarely in production code

**Better alternatives**:
```kotlin
// ❌ Risky
val length = name!!.length

// ✅ Better: Safe call with default
val length = name?.length ?: 0

// ✅ Better: Explicit null check
if (name != null) {
    val length = name.length
}
```

---

## Safe Casts (as?)

Cast to a type safely, returning null if the cast fails.

### Regular Cast (as)

```kotlin
val obj: Any = "Hello"
val str: String = obj as String  // ✅ OK

val num: Any = 42
// val str2: String = num as String  // 💥 ClassCastException!
```

### Safe Cast (as?)

```kotlin
val obj: Any = "Hello"
val str: String? = obj as? String  // "Hello"

val num: Any = 42
val str2: String? = num as? String  // null (safe!)

println(str)   // Hello
println(str2)  // null
```

### Practical Example

```kotlin
fun printLength(obj: Any) {
    val str = obj as? String
    println("Length: ${str?.length ?: "Not a string"}")
}

fun main() {
    printLength("Hello")     // Length: 5
    printLength(42)          // Length: Not a string
    printLength(listOf(1, 2)) // Length: Not a string
}
```

---

## The let Function

`let` executes a block of code only if the value is not null.

### Basic Usage

```kotlin
val name: String? = "Alice"

name?.let {
    println("Name is $it")
    println("Length is ${it.length}")
}

// Only runs if name is not null
```

### When Value is Null

```kotlin
val name: String? = null

name?.let {
    println("This won't print")
}

println("This will print")
```

### Practical Example

```kotlin
fun processUser(userId: String?) {
    userId?.let {
        println("Looking up user: $it")
        // Database lookup...
        println("User found!")
    } ?: println("No user ID provided")
}

fun main() {
    processUser("12345")
    // Looking up user: 12345
    // User found!

    processUser(null)
    // No user ID provided
}
```

### let with Return Value

```kotlin
val name: String? = "Alice"

val uppercaseName = name?.let {
    it.uppercase()
} ?: "UNKNOWN"

println(uppercaseName)  // ALICE
```

---

## Null Safety Patterns

### Pattern 1: Safe Call with Default

```kotlin
fun getDisplayName(name: String?): String {
    return name?.trim() ?: "Anonymous"
}
```

### Pattern 2: Explicit Null Check

```kotlin
fun processName(name: String?) {
    if (name != null) {
        // Inside this block, name is smart-cast to String
        println(name.length)
        println(name.uppercase())
    } else {
        println("Name is null")
    }
}
```

### Pattern 3: Early Return

```kotlin
fun validateAge(age: Int?): Boolean {
    if (age == null) return false

    // After null check, age is smart-cast to Int
    return age >= 18
}
```

### Pattern 4: let for Complex Logic

```kotlin
fun processOrder(orderId: String?) {
    orderId?.let { id ->
        println("Processing order: $id")
        // Multiple operations on id
        val order = findOrder(id)
        sendConfirmation(id)
        updateInventory(id)
    }
}
```

---

## Exercise 1: Safe User Profile Display

**Goal**: Create a user profile system that handles missing data safely.

**Requirements**:
1. Create a `User` data class with nullable fields: name, email, phone, address
2. Create `displayProfile(user: User?)` function that:
   - Shows all available information
   - Shows "Not provided" for missing fields
   - Shows "No user data" if user is null
3. Test with different combinations of null/non-null values

---

## Solution 1: Safe User Profile Display

```kotlin
data class User(
    val name: String?,
    val email: String?,
    val phone: String?,
    val address: String?
)

fun displayProfile(user: User?) {
    if (user == null) {
        println("No user data available")
        return
    }

    println("=== User Profile ===")
    println("Name: ${user.name ?: "Not provided"}")
    println("Email: ${user.email ?: "Not provided"}")
    println("Phone: ${user.phone ?: "Not provided"}")
    println("Address: ${user.address ?: "Not provided"}")
    println()
}

fun main() {
    // User with all information
    val user1 = User(
        name = "Alice Johnson",
        email = "alice@example.com",
        phone = "555-1234",
        address = "123 Main St"
    )
    displayProfile(user1)

    // User with partial information
    val user2 = User(
        name = "Bob Smith",
        email = "bob@example.com",
        phone = null,
        address = null
    )
    displayProfile(user2)

    // Null user
    displayProfile(null)
}
```

**Sample Output**:
```
=== User Profile ===
Name: Alice Johnson
Email: alice@example.com
Phone: 555-1234
Address: 123 Main St

=== User Profile ===
Name: Bob Smith
Email: bob@example.com
Phone: Not provided
Address: Not provided

No user data available
```

---

## Exercise 2: String Processor with Null Safety

**Goal**: Create safe string processing functions.

**Requirements**:
1. Create `safeLength(str: String?): Int` - returns length or 0
2. Create `safeUppercase(str: String?): String` - returns uppercase or "EMPTY"
3. Create `extractFirstWord(str: String?): String?` - returns first word or null
4. Create `processText(text: String?)` - displays all information using above functions

---

## Solution 2: String Processor with Null Safety

```kotlin
fun safeLength(str: String?): Int {
    return str?.length ?: 0
}

fun safeUppercase(str: String?): String {
    return str?.uppercase() ?: "EMPTY"
}

fun extractFirstWord(str: String?): String? {
    return str?.trim()?.split(" ")?.firstOrNull()
}

fun processText(text: String?) {
    println("\n=== Text Processing ===")
    println("Input: ${text ?: "null"}")
    println("Length: ${safeLength(text)}")
    println("Uppercase: ${safeUppercase(text)}")
    println("First word: ${extractFirstWord(text) ?: "none"}")

    // Using let for additional processing
    text?.let {
        if (it.isNotEmpty()) {
            println("Reversed: ${it.reversed()}")
            println("Word count: ${it.split(" ").size}")
        }
    }
}

fun main() {
    processText("Hello World from Kotlin")
    processText("   Kotlin   ")
    processText("")
    processText(null)
}
```

**Sample Output**:
```
=== Text Processing ===
Input: Hello World from Kotlin
Length: 24
Uppercase: HELLO WORLD FROM KOTLIN
First word: Hello
Reversed: niltoK morf dlroW olleH
Word count: 4

=== Text Processing ===
Input:    Kotlin
Length: 12
Uppercase:    KOTLIN
First word: Kotlin
Reversed:    niltoK
Word count: 1

=== Text Processing ===
Input:
Length: 0
Uppercase: EMPTY
First word: none

=== Text Processing ===
Input: null
Length: 0
Uppercase: EMPTY
First word: none
```

---

## Exercise 3: Safe Config Reader

**Goal**: Create a configuration reader that safely handles missing values.

**Requirements**:
1. Create a map to store configuration (String keys, nullable String values)
2. Create `getConfig(key: String, default: String): String` function
3. Create `getIntConfig(key: String, default: Int): Int` function
4. Create `getBoolConfig(key: String, default: Boolean): Boolean` function
5. Test with various keys and defaults

---

## Solution 3: Safe Config Reader

```kotlin
class ConfigReader(private val config: Map<String, String?>) {

    fun getConfig(key: String, default: String): String {
        return config[key] ?: default
    }

    fun getIntConfig(key: String, default: Int): Int {
        return config[key]?.toIntOrNull() ?: default
    }

    fun getBoolConfig(key: String, default: Boolean): Boolean {
        return config[key]?.toBoolean() ?: default
    }

    fun displayAllConfig() {
        println("\n=== Configuration ===")
        if (config.isEmpty()) {
            println("No configuration available")
            return
        }

        for ((key, value) in config) {
            println("$key = ${value ?: "null"}")
        }
    }
}

fun main() {
    val config = mapOf(
        "appName" to "MyApp",
        "version" to "1.0.0",
        "port" to "8080",
        "debug" to "true",
        "timeout" to null,
        "apiKey" to null
    )

    val reader = ConfigReader(config)
    reader.displayAllConfig()

    println("\n=== Reading Config ===")
    println("App Name: ${reader.getConfig("appName", "Unknown")}")
    println("Version: ${reader.getConfig("version", "1.0.0")}")
    println("Port: ${reader.getIntConfig("port", 3000)}")
    println("Debug: ${reader.getBoolConfig("debug", false)}")
    println("Timeout: ${reader.getIntConfig("timeout", 30)}")
    println("API Key: ${reader.getConfig("apiKey", "default-key")}")
    println("Missing: ${reader.getConfig("missing", "fallback")}")
}
```

**Sample Output**:
```
=== Configuration ===
appName = MyApp
version = 1.0.0
port = 8080
debug = true
timeout = null
apiKey = null

=== Reading Config ===
App Name: MyApp
Version: 1.0.0
Port: 8080
Debug: true
Timeout: 30
API Key: default-key
Missing: fallback
```

---

## Common Mistakes

### Mistake 1: Overusing !!

```kotlin
// ❌ Bad - risky
fun getLength(str: String?): Int {
    return str!!.length  // Can crash!
}

// ✅ Good - safe
fun getLength(str: String?): Int {
    return str?.length ?: 0
}
```

### Mistake 2: Forgetting to Handle Null

```kotlin
val name: String? = getName()

// ❌ Compiler error
// println(name.length)

// ✅ Correct
println(name?.length)
```

### Mistake 3: Unnecessary Null Checks

```kotlin
val name: String = "Alice"  // Non-nullable

// ❌ Unnecessary
if (name != null) {
    println(name.length)
}

// ✅ Just use it directly
println(name.length)
```

---

## Checkpoint Quiz

### Question 1
What does `String?` mean?

A) A String that might be null
B) An optional String parameter
C) A String or Integer
D) A String array

### Question 2
What does `name?.length` return if `name` is null?

A) 0
B) null
C) Error
D) Empty string

### Question 3
What does the Elvis operator `?:` do?

A) Checks if a value is null
B) Provides a default value when something is null
C) Asserts that a value is not null
D) Safely casts a value

### Question 4
When should you use `!!`?

A) Always, it's the safest option
B) Rarely, only when you're certain a value isn't null
C) For all nullable types
D) Never

### Question 5
What does `obj as? String` return if `obj` is not a String?

A) Error
B) null
C) Empty string
D) The original object

---

## Quiz Answers

**Question 1: A) A String that might be null**

The `?` makes a type nullable:

```kotlin
val name: String = "Alice"  // Cannot be null
val name: String? = null    // Can be null
```

---

**Question 2: B) null**

Safe call returns null if the receiver is null:

```kotlin
val name: String? = null
val length = name?.length  // null (not 0, not error)
```

---

**Question 3: B) Provides a default value when something is null**

```kotlin
val name: String? = null
val display = name ?: "Unknown"  // "Unknown"
```

---

**Question 4: B) Rarely, only when you're certain a value isn't null**

`!!` can cause crashes—use it sparingly:

```kotlin
// ❌ Risky
val length = text!!.length

// ✅ Better
val length = text?.length ?: 0
```

---

**Question 5: B) null**

Safe cast returns null on failure:

```kotlin
val num: Any = 42
val str = num as? String  // null (safe, no crash)
```

---

## What You've Learned

✅ Kotlin's null safety system prevents NullPointerExceptions
✅ Nullable types with `?`
✅ Safe call operator `?.` for safe access
✅ Elvis operator `?:` for default values
✅ Not-null assertion `!!` (use carefully!)
✅ Safe casts with `as?`
✅ The `let` function for null-safe operations
✅ Common patterns for handling null values

---

## Next Steps

In **Lesson 1.7: Part 1 Capstone Project - CLI Calculator**, you'll build:
- A complete command-line calculator
- Menu system with when expressions
- Input validation with null safety
- All arithmetic operations
- Loop until user exits

Time to apply everything you've learned!

---

**Congratulations on completing Lesson 1.6!**

You now understand one of Kotlin's most powerful features—null safety. This prevents countless bugs and makes your code more reliable!

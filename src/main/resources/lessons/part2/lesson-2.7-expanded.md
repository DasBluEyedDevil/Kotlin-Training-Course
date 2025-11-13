# Lesson 2.7: Maps and Part 2 Capstone Project

**Estimated Time**: 70 minutes
**Difficulty**: Beginner
**Prerequisites**: Lesson 2.6 (Lists)

---

## Topic Introduction

You've mastered lists—ordered collections accessed by numeric indices. But what if you need to look up data by something more meaningful than a number? What if you need to:

- Find a phone number by a person's name
- Look up a product price by its name
- Get a user's email by their username
- Translate a word from English to Spanish

You *could* use two parallel lists (one for keys, one for values), but that's clunky and error-prone. **Maps** solve this elegantly by storing **key-value pairs**—like a real-world dictionary where you look up a word (key) to find its definition (value).

In this lesson, you'll learn:
- What maps are and when to use them
- Creating immutable and mutable maps
- Accessing, adding, and removing entries
- Iterating through maps
- Common map operations
- **Part 2 Capstone Project**: Build a complete contact management system!

This is the final lesson of Part 2, so we'll finish strong with a comprehensive project that combines everything you've learned!

---

## The Concept: Key-Value Pairs

### Real-World Map Analogy

Think of a map like a **phone book** or **dictionary**:

```
Phone Book:
┌──────────────────────────┐
│ "Alice"  → "555-1234"    │
│ "Bob"    → "555-5678"    │
│ "Charlie"→ "555-9999"    │
└──────────────────────────┘
         ↑           ↑
       KEY        VALUE
```

**Properties:**
- **Keys are unique**: Can't have two "Alice" entries
- **Keys map to values**: Each key has exactly one value
- **Fast lookup**: Find value by key instantly
- **Unordered**: Entries aren't in a specific order (usually)

### List vs Map Comparison

**List (Index → Value):**
```kotlin
val colors = listOf("Red", "Green", "Blue")
println(colors[0])  // "Red"
println(colors[1])  // "Green"
```

**Map (Key → Value):**
```kotlin
val colorCodes = mapOf(
    "Red" to "#FF0000",
    "Green" to "#00FF00",
    "Blue" to "#0000FF"
)
println(colorCodes["Red"])  // "#FF0000"
```

**When to use maps:**
- ✅ Looking up by meaningful keys (name, ID, word)
- ✅ Need fast key-based access
- ✅ Associating related data (country → capital)

**When to use lists:**
- ✅ Ordered sequence of items
- ✅ Accessing by position
- ✅ Simple collection of values

---

## Creating Maps

### Immutable Maps (Read-Only)

Created with `mapOf()`:

```kotlin
fun main() {
    val capitals = mapOf(
        "USA" to "Washington D.C.",
        "France" to "Paris",
        "Japan" to "Tokyo",
        "Brazil" to "Brasília"
    )

    println(capitals)
    println("Size: ${capitals.size}")
}
```

**Output:**
```
{USA=Washington D.C., France=Paris, Japan=Tokyo, Brazil=Brasília}
Size: 4
```

**The `to` keyword** creates a Pair: `"USA" to "Washington D.C."` → `Pair("USA", "Washington D.C.")`

### Mutable Maps (Can Change)

Created with `mutableMapOf()`:

```kotlin
fun main() {
    val scores = mutableMapOf(
        "Alice" to 95,
        "Bob" to 87
    )

    println("Initial: $scores")

    // Add new entry
    scores["Charlie"] = 92
    println("After add: $scores")

    // Update existing
    scores["Alice"] = 98
    println("After update: $scores")

    // Remove entry
    scores.remove("Bob")
    println("After remove: $scores")
}
```

**Output:**
```
Initial: {Alice=95, Bob=87}
After add: {Alice=95, Bob=87, Charlie=92}
After update: {Alice=98, Bob=87, Charlie=92}
After remove: {Alice=98, Charlie=92}
```

### Empty Maps

```kotlin
// Empty immutable
val empty = mapOf<String, Int>()

// Empty mutable
val emptyMutable = mutableMapOf<String, String>()

// Or use emptyMap()
val alsoEmpty = emptyMap<Int, String>()
```

### Maps with Different Types

```kotlin
// String keys, Int values
val ages = mapOf("Alice" to 25, "Bob" to 30)

// Int keys, String values
val weekDays = mapOf(
    1 to "Monday",
    2 to "Tuesday",
    3 to "Wednesday"
)

// String keys, Any values (mixed)
val mixed = mapOf(
    "name" to "Alice",
    "age" to 25,
    "active" to true
)
```

---

## Accessing Map Values

### Basic Access

```kotlin
fun main() {
    val prices = mapOf(
        "Coffee" to 4.99,
        "Tea" to 3.99,
        "Sandwich" to 7.99
    )

    // Direct access with [] (returns null if not found)
    println(prices["Coffee"])      // 4.99
    println(prices["Pizza"])       // null

    // Safe access with get()
    println(prices.get("Tea"))     // 3.99

    // With default value
    println(prices.getOrDefault("Pizza", 0.0))  // 0.0
}
```

### Safe Access Patterns

```kotlin
fun main() {
    val contacts = mapOf(
        "Alice" to "alice@email.com",
        "Bob" to "bob@email.com"
    )

    // Nullable return
    val aliceEmail: String? = contacts["Alice"]
    println(aliceEmail)  // alice@email.com

    // With default
    val charlieEmail = contacts.getOrElse("Charlie") { "unknown@email.com" }
    println(charlieEmail)  // unknown@email.com

    // Check before accessing
    if ("Alice" in contacts) {
        println("Alice's email: ${contacts["Alice"]}")
    }
}
```

---

## Modifying Mutable Maps

### Adding and Updating

```kotlin
fun main() {
    val inventory = mutableMapOf(
        "Apples" to 50,
        "Bananas" to 30
    )

    // Add new entry
    inventory["Oranges"] = 40
    println(inventory)

    // Update existing (same syntax)
    inventory["Apples"] = 55
    println(inventory)

    // Add/update with put()
    inventory.put("Grapes", 25)
    println(inventory)

    // Add multiple entries
    inventory.putAll(mapOf("Mangoes" to 15, "Pears" to 20))
    println(inventory)
}
```

**Output:**
```
{Apples=50, Bananas=30, Oranges=40}
{Apples=55, Bananas=30, Oranges=40}
{Apples=55, Bananas=30, Oranges=40, Grapes=25}
{Apples=55, Bananas=30, Oranges=40, Grapes=25, Mangoes=15, Pears=20}
```

### Removing Entries

```kotlin
fun main() {
    val users = mutableMapOf(
        "alice" to "password123",
        "bob" to "secret456",
        "charlie" to "pass789"
    )

    // Remove by key
    users.remove("bob")
    println(users)

    // Remove and return value
    val removed = users.remove("alice")
    println("Removed: $removed")
    println(users)

    // Clear all
    users.clear()
    println("After clear: $users")
}
```

**Output:**
```
{alice=password123, charlie=pass789}
Removed: password123
{charlie=pass789}
After clear: {}
```

---

## Iterating Through Maps

### Iterate Over Entries

```kotlin
fun main() {
    val grades = mapOf(
        "Alice" to 95,
        "Bob" to 87,
        "Charlie" to 92
    )

    // Iterate over entries
    for (entry in grades) {
        println("${entry.key}: ${entry.value}")
    }

    // Or with destructuring
    for ((name, score) in grades) {
        println("$name scored $score")
    }
}
```

**Output:**
```
Alice: 95
Bob: 87
Charlie: 92
Alice scored 95
Bob scored 87
Charlie scored 92
```

### Iterate Over Keys or Values Only

```kotlin
fun main() {
    val capitals = mapOf(
        "USA" to "Washington D.C.",
        "France" to "Paris",
        "Japan" to "Tokyo"
    )

    // Just keys
    println("Countries:")
    for (country in capitals.keys) {
        println("- $country")
    }

    // Just values
    println("\nCapitals:")
    for (capital in capitals.values) {
        println("- $capital")
    }
}
```

**Output:**
```
Countries:
- USA
- France
- Japan

Capitals:
- Washington D.C.
- Paris
- Tokyo
```

---

## Common Map Operations

### Checking Contents

```kotlin
fun main() {
    val menu = mapOf(
        "Burger" to 9.99,
        "Pizza" to 12.99,
        "Salad" to 7.99
    )

    // Check if key exists
    println("Has Burger? ${"Burger" in menu}")  // true
    println("Has Tacos? ${menu.containsKey("Tacos")}")  // false

    // Check if value exists
    println("Has price 9.99? ${menu.containsValue(9.99)}")  // true

    // Check if empty
    println("Is empty? ${menu.isEmpty()}")  // false

    // Get size
    println("Menu items: ${menu.size}")  // 3
}
```

### Filtering Maps

```kotlin
fun main() {
    val products = mapOf(
        "Laptop" to 999.99,
        "Mouse" to 29.99,
        "Keyboard" to 79.99,
        "Monitor" to 299.99,
        "Cable" to 9.99
    )

    // Filter by value
    val expensive = products.filter { it.value > 100 }
    println("Expensive items: $expensive")

    // Filter by key
    val mProducts = products.filter { it.key.startsWith("M") }
    println("M products: $mProducts")

    // Filter and transform
    val discounted = products
        .filter { it.value > 50 }
        .mapValues { it.value * 0.9 }  // 10% discount
    println("Discounted: $discounted")
}
```

**Output:**
```
Expensive items: {Laptop=999.99, Monitor=299.99}
M products: {Mouse=29.99, Monitor=299.99}
Discounted: {Laptop=899.991, Keyboard=71.991, Monitor=269.991}
```

### Map Transformations

```kotlin
fun main() {
    val numbers = mapOf("one" to 1, "two" to 2, "three" to 3)

    // Transform values only
    val doubled = numbers.mapValues { it.value * 2 }
    println(doubled)  // {one=2, two=4, three=6}

    // Transform keys only
    val upperKeys = numbers.mapKeys { it.key.uppercase() }
    println(upperKeys)  // {ONE=1, TWO=2, THREE=3}

    // Convert to list of pairs
    val pairs = numbers.toList()
    println(pairs)  // [(one, 1), (two, 2), (three, 3)]
}
```

---

## Practical Examples

### Example 1: Grade Book

```kotlin
fun main() {
    val gradeBook = mutableMapOf<String, Int>()

    // Add students and grades
    gradeBook["Alice"] = 95
    gradeBook["Bob"] = 87
    gradeBook["Charlie"] = 92
    gradeBook["Diana"] = 78
    gradeBook["Eve"] = 88

    println("=== Grade Book ===")
    for ((student, grade) in gradeBook) {
        val letter = when (grade) {
            in 90..100 -> "A"
            in 80..89 -> "B"
            in 70..79 -> "C"
            else -> "F"
        }
        println("$student: $grade ($letter)")
    }

    // Statistics
    val average = gradeBook.values.average()
    val highest = gradeBook.maxByOrNull { it.value }
    val lowest = gradeBook.minByOrNull { it.value }

    println("\n=== Statistics ===")
    println("Class average: %.1f".format(average))
    println("Highest: ${highest?.key} with ${highest?.value}")
    println("Lowest: ${lowest?.key} with ${lowest?.value}")
}
```

**Output:**
```
=== Grade Book ===
Alice: 95 (A)
Bob: 87 (B)
Charlie: 92 (A)
Diana: 78 (C)
Eve: 88 (B)

=== Statistics ===
Class average: 88.0
Highest: Alice with 95
Lowest: Diana with 78
```

### Example 2: Inventory System

```kotlin
fun main() {
    val inventory = mutableMapOf(
        "Laptop" to 15,
        "Mouse" to 50,
        "Keyboard" to 30
    )

    println("=== Store Inventory ===")
    for ((item, quantity) in inventory) {
        val status = if (quantity < 20) "Low stock" else "In stock"
        println("$item: $quantity units ($status)")
    }

    // Restock low items
    println("\n=== Restocking Low Items ===")
    for ((item, quantity) in inventory) {
        if (quantity < 20) {
            val restock = 30
            inventory[item] = quantity + restock
            println("Restocked $item: $quantity → ${inventory[item]}")
        }
    }

    println("\n=== Updated Inventory ===")
    println(inventory)
}
```

**Output:**
```
=== Store Inventory ===
Laptop: 15 units (Low stock)
Mouse: 50 units (In stock)
Keyboard: 30 units (In stock)

=== Restocking Low Items ===
Restocked Laptop: 15 → 45

=== Updated Inventory ===
{Laptop=45, Mouse=50, Keyboard=30}
```

---

## Part 2 Capstone Project: Contact Management System

Now it's time to put everything together! You'll build a complete contact management system using all the concepts from Part 2.

### Project Requirements

Build a console application that manages contacts with these features:

1. **Add Contact**: Store name, phone, and email
2. **View All Contacts**: Display all contacts
3. **Search Contact**: Find by name
4. **Update Contact**: Modify phone or email
5. **Delete Contact**: Remove a contact
6. **Statistics**: Show total contacts, contacts with/without email
7. **Menu System**: User-friendly interface with loops

**Concepts used:**
- ✅ If statements (validation)
- ✅ When expressions (menu choices)
- ✅ For loops (displaying contacts)
- ✅ While/do-while loops (menu loop)
- ✅ Lists (managing multiple fields)
- ✅ Maps (storing contacts)

### Capstone Solution

<details>
<summary>Click to see complete solution</summary>

```kotlin
data class Contact(
    var phone: String,
    var email: String
)

fun main() {
    val contacts = mutableMapOf<String, Contact>()
    var choice: String

    println("╔═══════════════════════════════════╗")
    println("║  CONTACT MANAGEMENT SYSTEM v1.0   ║")
    println("╚═══════════════════════════════════╝")

    do {
        println("\n=== MAIN MENU ===")
        println("1. Add Contact")
        println("2. View All Contacts")
        println("3. Search Contact")
        println("4. Update Contact")
        println("5. Delete Contact")
        println("6. Statistics")
        println("7. Exit")
        print("\nEnter choice (1-7): ")

        choice = readln()

        when (choice) {
            "1" -> addContact(contacts)
            "2" -> viewAllContacts(contacts)
            "3" -> searchContact(contacts)
            "4" -> updateContact(contacts)
            "5" -> deleteContact(contacts)
            "6" -> showStatistics(contacts)
            "7" -> println("\n👋 Goodbye! Thanks for using Contact Manager!")
            else -> println("❌ Invalid choice. Please try again.")
        }
    } while (choice != "7")
}

fun addContact(contacts: MutableMap<String, Contact>) {
    println("\n=== ADD NEW CONTACT ===")

    print("Enter name: ")
    val name = readln()

    if (name.isBlank()) {
        println("❌ Name cannot be empty!")
        return
    }

    if (name in contacts) {
        println("❌ Contact '$name' already exists!")
        return
    }

    print("Enter phone: ")
    val phone = readln()

    print("Enter email (optional): ")
    val email = readln()

    contacts[name] = Contact(phone, email)
    println("✅ Contact '$name' added successfully!")
}

fun viewAllContacts(contacts: Map<String, Contact>) {
    if (contacts.isEmpty()) {
        println("\n📭 No contacts found.")
        return
    }

    println("\n=== ALL CONTACTS (${contacts.size}) ===")
    var index = 1

    for ((name, contact) in contacts) {
        println("\n[$index] $name")
        println("    📞 Phone: ${contact.phone}")
        if (contact.email.isNotBlank()) {
            println("    📧 Email: ${contact.email}")
        } else {
            println("    📧 Email: (not provided)")
        }
        index++
    }
}

fun searchContact(contacts: Map<String, Contact>) {
    println("\n=== SEARCH CONTACT ===")
    print("Enter name to search: ")
    val name = readln()

    val contact = contacts[name]

    if (contact != null) {
        println("\n✅ Contact found:")
        println("Name: $name")
        println("Phone: ${contact.phone}")
        println("Email: ${if (contact.email.isBlank()) "(not provided)" else contact.email}")
    } else {
        println("❌ Contact '$name' not found.")

        // Suggest similar names
        val similar = contacts.keys.filter { it.contains(name, ignoreCase = true) }
        if (similar.isNotEmpty()) {
            println("\nDid you mean:")
            for (suggestion in similar) {
                println("  - $suggestion")
            }
        }
    }
}

fun updateContact(contacts: MutableMap<String, Contact>) {
    println("\n=== UPDATE CONTACT ===")
    print("Enter name: ")
    val name = readln()

    val contact = contacts[name]

    if (contact == null) {
        println("❌ Contact '$name' not found.")
        return
    }

    println("\nCurrent details:")
    println("Phone: ${contact.phone}")
    println("Email: ${contact.email}")

    print("\nUpdate phone? (y/n): ")
    if (readln().lowercase() == "y") {
        print("Enter new phone: ")
        contact.phone = readln()
    }

    print("Update email? (y/n): ")
    if (readln().lowercase() == "y") {
        print("Enter new email: ")
        contact.email = readln()
    }

    println("✅ Contact '$name' updated successfully!")
}

fun deleteContact(contacts: MutableMap<String, Contact>) {
    println("\n=== DELETE CONTACT ===")
    print("Enter name: ")
    val name = readln()

    if (name !in contacts) {
        println("❌ Contact '$name' not found.")
        return
    }

    print("Are you sure you want to delete '$name'? (y/n): ")
    if (readln().lowercase() == "y") {
        contacts.remove(name)
        println("✅ Contact '$name' deleted successfully!")
    } else {
        println("❌ Deletion cancelled.")
    }
}

fun showStatistics(contacts: Map<String, Contact>) {
    println("\n=== STATISTICS ===")

    val total = contacts.size
    val withEmail = contacts.values.count { it.email.isNotBlank() }
    val withoutEmail = total - withEmail

    println("Total contacts: $total")
    println("Contacts with email: $withEmail")
    println("Contacts without email: $withoutEmail")

    if (total > 0) {
        val percentage = (withEmail.toDouble() / total * 100)
        println("Email coverage: %.1f%%".format(percentage))

        // Most common area code (first 3 digits of phone)
        val areaCodes = contacts.values
            .map { it.phone.take(3) }
            .groupingBy { it }
            .eachCount()

        if (areaCodes.isNotEmpty()) {
            val mostCommon = areaCodes.maxByOrNull { it.value }
            println("Most common area code: ${mostCommon?.key} (${mostCommon?.value} contacts)")
        }
    }
}
```

**Sample Run:**
```
╔═══════════════════════════════════╗
║  CONTACT MANAGEMENT SYSTEM v1.0   ║
╚═══════════════════════════════════╝

=== MAIN MENU ===
1. Add Contact
2. View All Contacts
3. Search Contact
4. Update Contact
5. Delete Contact
6. Statistics
7. Exit

Enter choice (1-7): 1

=== ADD NEW CONTACT ===
Enter name: Alice
Enter phone: 555-1234
Enter email (optional): alice@email.com
✅ Contact 'Alice' added successfully!

=== MAIN MENU ===
1. Add Contact
2. View All Contacts
3. Search Contact
4. Update Contact
5. Delete Contact
6. Statistics
7. Exit

Enter choice (1-7): 2

=== ALL CONTACTS (1) ===

[1] Alice
    📞 Phone: 555-1234
    📧 Email: alice@email.com

=== MAIN MENU ===
...
```

**Key features:**
- ✅ Data class for structured contact info
- ✅ Input validation
- ✅ Error handling
- ✅ User-friendly messages with emojis
- ✅ Confirmation for destructive actions
- ✅ Smart search with suggestions
- ✅ Comprehensive statistics
- ✅ Clean code organization with functions
</details>

### Challenge Extensions

Want to go further? Try adding:

1. **Export/Import**: Save contacts to a file
2. **Sorting**: View contacts alphabetically
3. **Groups**: Categorize contacts (family, work, friends)
4. **Favorites**: Mark important contacts
5. **Birthday tracking**: Store and remind birthdays
6. **Multiple phones**: Support home, work, mobile

---

## Common Pitfalls and Best Practices

### Pitfall 1: Modifying While Iterating

❌ **Dangerous:**
```kotlin
val map = mutableMapOf("a" to 1, "b" to 2, "c" to 3)
for ((key, value) in map) {
    if (value < 3) {
        map.remove(key)  // Can cause ConcurrentModificationException!
    }
}
```

✅ **Safe:**
```kotlin
val map = mutableMapOf("a" to 1, "b" to 2, "c" to 3)
val toRemove = map.filter { it.value < 3 }.keys
toRemove.forEach { map.remove(it) }
```

### Pitfall 2: Null Values from Missing Keys

❌ **Can crash:**
```kotlin
val ages = mapOf("Alice" to 25)
val age: Int = ages["Bob"]  // Error: Type mismatch (Int? can't be Int)
```

✅ **Safe:**
```kotlin
val ages = mapOf("Alice" to 25)
val age = ages["Bob"] ?: 0  // Use default value
// Or
val age = ages.getOrDefault("Bob", 0)
```

### Best Practice 1: Use Appropriate Map Type

```kotlin
// Immutable for fixed data
val monthDays = mapOf(
    "January" to 31,
    "February" to 28
    // ...
)

// Mutable for changing data
val cart = mutableMapOf<String, Int>()
```

### Best Practice 2: Descriptive Key Names

```kotlin
// ❌ Unclear
val m = mapOf(1 to "A", 2 to "B")

// ✅ Clear
val gradesByScore = mapOf(90 to "A", 80 to "B")
```

### Best Practice 3: Check Before Access

```kotlin
// ✅ Safe pattern
if ("Alice" in contacts) {
    val contact = contacts["Alice"]!!
    // Use contact
} else {
    println("Contact not found")
}
```

---

## Quick Quiz

**Question 1:** What's the output?
```kotlin
val map = mapOf("a" to 1, "b" to 2)
println(map["c"])
```

<details>
<summary>Answer</summary>

**Output:** `null`

**Explanation:** The key "c" doesn't exist, so accessing it returns null.
</details>

---

**Question 2:** How do you add to a mutable map?
```kotlin
val map = mutableMapOf("x" to 10)
// Add "y" with value 20
```

<details>
<summary>Answer</summary>

```kotlin
map["y"] = 20
// Or
map.put("y", 20)
```
</details>

---

**Question 3:** What's wrong here?
```kotlin
val map = mapOf("a" to 1)
map["b"] = 2
```

<details>
<summary>Answer</summary>

**Error:** `mapOf()` creates an **immutable** map. Can't add to it.

**Fix:**
```kotlin
val map = mutableMapOf("a" to 1)
map["b"] = 2  // Now it works!
```
</details>

---

**Question 4:** How do you iterate through keys and values?

<details>
<summary>Answer</summary>

```kotlin
val map = mapOf("a" to 1, "b" to 2)

// With destructuring (recommended)
for ((key, value) in map) {
    println("$key -> $value")
}

// Or with entry
for (entry in map) {
    println("${entry.key} -> ${entry.value}")
}
```
</details>

---

## Part 2 Summary

🎉 **Congratulations!** You've completed Part 2: Controlling the Flow!

**You've mastered:**

**Decision Making:**
- ✅ If/else statements for binary decisions
- ✅ Logical operators (&&, ||, !)
- ✅ When expressions for multi-way decisions

**Loops:**
- ✅ For loops for counted iteration
- ✅ While loops for condition-based repetition
- ✅ Do-while for "at least once" loops
- ✅ Break and continue for flow control

**Collections:**
- ✅ Lists for ordered data
- ✅ Maps for key-value associations
- ✅ Mutable vs immutable collections
- ✅ Collection operations (map, filter, etc.)

**You can now:**
- 🎯 Make complex decisions in your programs
- 🔄 Repeat tasks efficiently
- 📦 Store and manage collections of data
- 🏗️ Build complete, interactive applications

---

## What's Next?

In **Part 3: Functional Programming in Kotlin**, you'll level up with:
- Lambda expressions and higher-order functions
- Advanced collection operations
- Sequences for lazy evaluation
- Scope functions (let, apply, with, run, also)
- Function composition and chaining

**Preview:**
```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

numbers
    .filter { it % 2 == 0 }
    .map { it * it }
    .forEach { println(it) }

val result = listOf("apple", "banana", "cherry")
    .filter { it.length > 5 }
    .map { it.uppercase() }
    .joinToString(", ")
```

Get ready to write more expressive, concise, and powerful Kotlin code!

---

**🏆 Outstanding work completing Part 2! You're becoming a Kotlin developer!** 🎉

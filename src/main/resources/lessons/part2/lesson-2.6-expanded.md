# Lesson 2.6: Lists - Storing Multiple Items

**Estimated Time**: 65 minutes
**Difficulty**: Beginner
**Prerequisites**: Lesson 2.5 (While loops)

---

## Topic Introduction

So far, you've stored individual pieces of data in variables—a single name, one number, a single temperature. But real-world programs need to manage collections of related data: a shopping cart with multiple items, a class roster with dozens of students, a playlist with hundreds of songs.

Imagine creating a task manager app. Would you create separate variables for each task?

```kotlin
val task1 = "Buy groceries"
val task2 = "Call dentist"
val task3 = "Finish homework"
// ... task50?
```

This is impractical and impossible to maintain. **Lists** solve this problem elegantly by storing multiple items in a single, ordered collection.

In this lesson, you'll learn:
- What lists are and why they're essential
- Creating immutable and mutable lists
- Accessing, adding, and removing elements
- Essential list operations and functions
- Powerful functional programming with lists
- Best practices for working with collections

By the end, you'll be able to manage collections of data like a pro!

---

## The Concept: Lists as Containers

### Real-World List Analogy

Think of a list as a **numbered filing cabinet**:

```
Shopping List (Drawer):
┌──────────────────┐
│ 0: Milk          │  ← First item (index 0)
│ 1: Bread         │
│ 2: Eggs          │
│ 3: Cheese        │  ← Last item (index 3)
└──────────────────┘
```

**Properties of this cabinet:**
- **Ordered**: Items have specific positions (0, 1, 2, 3)
- **Indexed**: You can access any item by its position
- **Dynamic**: You can add or remove items (if mutable)
- **Homogeneous**: Usually stores items of the same type

### Why Use Lists?

**Without lists:**
```kotlin
val student1 = "Alice"
val student2 = "Bob"
val student3 = "Charlie"

// How do you loop through these?
// What if you get a 4th student?
```

**With lists:**
```kotlin
val students = listOf("Alice", "Bob", "Charlie")

// Easy to loop through
for (student in students) {
    println(student)
}

// Easy to add more (with mutableListOf)
```

Lists give you:
- ✅ Organization: Group related data
- ✅ Flexibility: Easily add/remove items
- ✅ Iteration: Loop through all items
- ✅ Built-in operations: Sort, filter, search, and more

---

## Creating Lists

### Immutable Lists (Read-Only)

Created with `listOf()`:

```kotlin
fun main() {
    val fruits = listOf("Apple", "Banana", "Cherry")

    println(fruits)  // [Apple, Banana, Cherry]
    println("Size: ${fruits.size}")  // Size: 3
}
```

**Immutable means:**
- ❌ Can't add items
- ❌ Can't remove items
- ❌ Can't change items
- ✅ Can read and iterate
- ✅ Thread-safe and predictable

**When to use:** When your collection won't change (days of the week, menu options, etc.)

### Mutable Lists (Can Change)

Created with `mutableListOf()`:

```kotlin
fun main() {
    val tasks = mutableListOf("Write code", "Test app")

    println("Initial: $tasks")

    tasks.add("Deploy")
    println("After add: $tasks")

    tasks.remove("Test app")
    println("After remove: $tasks")
}
```

**Output:**
```
Initial: [Write code, Test app]
After add: [Write code, Test app, Deploy]
After remove: [Write code, Deploy]
```

**When to use:** When your collection needs to change (shopping cart, todo list, etc.)

### Empty Lists

```kotlin
// Empty immutable list
val emptyList = listOf<String>()

// Empty mutable list
val emptyMutable = mutableListOf<Int>()

// Or use emptyList()
val alsoEmpty = emptyList<Double>()
```

### Lists with Type Inference

```kotlin
// Kotlin infers type from values
val numbers = listOf(1, 2, 3, 4, 5)  // List<Int>
val names = listOf("Alice", "Bob")    // List<String>
val mixed = listOf<Any>(1, "two", 3.0) // List<Any>

// Explicit type declaration
val scores: List<Int> = listOf(95, 87, 92)
```

---

## Accessing List Elements

### Indexing (Zero-Based)

Lists use **zero-based indexing**—the first element is at position 0:

```kotlin
fun main() {
    val colors = listOf("Red", "Green", "Blue", "Yellow")

    println(colors[0])  // Red (first)
    println(colors[1])  // Green
    println(colors[2])  // Blue
    println(colors[3])  // Yellow (last)
}
```

**Visual representation:**
```
Index:  0      1        2       3
Value: "Red" "Green" "Blue" "Yellow"
```

### Safe Access Methods

```kotlin
fun main() {
    val fruits = listOf("Apple", "Banana", "Cherry")

    // Direct access (throws error if out of bounds)
    println(fruits[0])  // Apple

    // Safe access with get()
    println(fruits.get(1))  // Banana

    // Safe access with getOrNull() (returns null if out of bounds)
    println(fruits.getOrNull(5))  // null (no error!)

    // Safe access with getOrElse()
    println(fruits.getOrElse(5) { "Not found" })  // Not found
}
```

### First, Last, and More

```kotlin
fun main() {
    val numbers = listOf(10, 20, 30, 40, 50)

    println("First: ${numbers.first()}")     // 10
    println("Last: ${numbers.last()}")       // 50
    println("Size: ${numbers.size}")         // 5
    println("Is empty: ${numbers.isEmpty()}") // false

    // Safe versions
    val empty = emptyList<Int>()
    println(empty.firstOrNull())  // null (no error)
    println(empty.lastOrNull())   // null
}
```

---

## Modifying Mutable Lists

### Adding Elements

```kotlin
fun main() {
    val cart = mutableListOf<String>()

    // Add at the end
    cart.add("Laptop")
    cart.add("Mouse")
    println(cart)  // [Laptop, Mouse]

    // Add at specific position
    cart.add(1, "Keyboard")
    println(cart)  // [Laptop, Keyboard, Mouse]

    // Add multiple items
    cart.addAll(listOf("Monitor", "Speakers"))
    println(cart)  // [Laptop, Keyboard, Mouse, Monitor, Speakers]
}
```

### Removing Elements

```kotlin
fun main() {
    val numbers = mutableListOf(10, 20, 30, 40, 50)

    // Remove by value
    numbers.remove(30)
    println(numbers)  // [10, 20, 40, 50]

    // Remove by index
    numbers.removeAt(0)
    println(numbers)  // [20, 40, 50]

    // Remove last
    numbers.removeLast()
    println(numbers)  // [20, 40]

    // Remove all
    numbers.clear()
    println(numbers)  // []
}
```

### Updating Elements

```kotlin
fun main() {
    val tasks = mutableListOf("Buy milk", "Call mom", "Study Kotlin")

    // Update by index
    tasks[0] = "Buy groceries"
    println(tasks)  // [Buy groceries, Call mom, Study Kotlin]

    // Update with set()
    tasks.set(1, "Video call mom")
    println(tasks)  // [Buy groceries, Video call mom, Study Kotlin]
}
```

---

## Common List Operations

### Checking Contents

```kotlin
fun main() {
    val fruits = listOf("Apple", "Banana", "Cherry", "Date")

    // Check if contains
    println("Apple" in fruits)        // true
    println(fruits.contains("Mango"))  // false

    // Check if contains all
    println(fruits.containsAll(listOf("Apple", "Date")))  // true

    // Count specific item
    val numbers = listOf(1, 2, 3, 2, 1, 2)
    println(numbers.count { it == 2 })  // 3
}
```

### Finding Elements

```kotlin
fun main() {
    val numbers = listOf(5, 12, 8, 3, 15, 7, 9)

    // Find first match
    val firstEven = numbers.find { it % 2 == 0 }
    println("First even: $firstEven")  // 12

    // Find last match
    val lastEven = numbers.findLast { it % 2 == 0 }
    println("Last even: $lastEven")  // 8

    // Find index
    val index = numbers.indexOf(15)
    println("15 is at index: $index")  // 4
}
```

### Sorting Lists

```kotlin
fun main() {
    val numbers = mutableListOf(5, 2, 8, 1, 9)

    // Sort in place (modifies original)
    numbers.sort()
    println("Sorted: $numbers")  // [1, 2, 5, 8, 9]

    // Reverse sort
    numbers.sortDescending()
    println("Descending: $numbers")  // [9, 8, 5, 2, 1]

    // Sorted (returns new list, original unchanged)
    val original = listOf(5, 2, 8, 1, 9)
    val sorted = original.sorted()
    println("Original: $original")  // [5, 2, 8, 1, 9]
    println("Sorted: $sorted")      // [1, 2, 5, 8, 9]
}
```

---

## Functional Operations on Lists

### Map (Transform Each Element)

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5)

    // Double each number
    val doubled = numbers.map { it * 2 }
    println(doubled)  // [2, 4, 6, 8, 10]

    // Convert to strings
    val strings = numbers.map { "Number $it" }
    println(strings)  // [Number 1, Number 2, ...]

    // Transform names to uppercase
    val names = listOf("alice", "bob", "charlie")
    val upper = names.map { it.uppercase() }
    println(upper)  // [ALICE, BOB, CHARLIE]
}
```

**Map pattern:**
```
Input:  [1, 2, 3, 4, 5]
         ↓  ↓  ↓  ↓  ↓
Transform each with: it * 2
         ↓  ↓  ↓  ↓  ↓
Output: [2, 4, 6, 8, 10]
```

### Filter (Keep Only Matching Items)

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // Keep only even numbers
    val evens = numbers.filter { it % 2 == 0 }
    println(evens)  // [2, 4, 6, 8, 10]

    // Keep numbers greater than 5
    val greaterThan5 = numbers.filter { it > 5 }
    println(greaterThan5)  // [6, 7, 8, 9, 10]

    // Filter strings by length
    val words = listOf("hi", "hello", "hey", "goodbye")
    val shortWords = words.filter { it.length <= 3 }
    println(shortWords)  // [hi, hey]
}
```

**Filter pattern:**
```
Input:  [1, 2, 3, 4, 5]
         ↓  ↓  ↓  ↓  ↓
Keep if: it % 2 == 0
         X  ✓  X  ✓  X
Output: [2, 4]
```

### Combining Map and Filter

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    // Get squares of even numbers
    val result = numbers
        .filter { it % 2 == 0 }  // [2, 4, 6, 8, 10]
        .map { it * it }         // [4, 16, 36, 64, 100]

    println(result)  // [4, 16, 36, 64, 100]
}
```

### Other Useful Operations

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5)

    // Sum
    println("Sum: ${numbers.sum()}")  // 15

    // Average
    println("Average: ${numbers.average()}")  // 3.0

    // Max and Min
    println("Max: ${numbers.maxOrNull()}")  // 5
    println("Min: ${numbers.minOrNull()}")  // 1

    // Any (at least one matches)
    println("Any > 3? ${numbers.any { it > 3 }}")  // true

    // All (all match)
    println("All > 0? ${numbers.all { it > 0 }}")  // true

    // None (none match)
    println("None < 0? ${numbers.none { it < 0 }}")  // true
}
```

---

## Hands-On Exercises

### Exercise 1: Shopping Cart Manager

**Challenge:** Create a shopping cart system that:
1. Starts with an empty mutable list
2. Allows adding items
3. Displays all items
4. Calculates total (assume each item costs $10)
5. Removes items

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val cart = mutableListOf<String>()

    // Add items
    cart.add("Laptop")
    cart.add("Mouse")
    cart.add("Keyboard")
    cart.add("Monitor")

    // Display cart
    println("=== Shopping Cart ===")
    for ((index, item) in cart.withIndex()) {
        println("${index + 1}. $item")
    }

    // Calculate total
    val itemPrice = 10.0
    val total = cart.size * itemPrice
    println("\nTotal items: ${cart.size}")
    println("Total cost: $$total")

    // Remove an item
    cart.remove("Mouse")
    println("\nAfter removing Mouse:")
    println(cart)
    println("New total: $${cart.size * itemPrice}")
}
```

**Output:**
```
=== Shopping Cart ===
1. Laptop
2. Mouse
3. Keyboard
4. Monitor

Total items: 4
Total cost: $40.0

After removing Mouse:
[Laptop, Keyboard, Monitor]
New total: $30.0
```
</details>

---

### Exercise 2: Grade Analyzer

**Challenge:** Given a list of test scores:
1. Calculate average
2. Find highest and lowest scores
3. Count how many passed (≥60)
4. Filter and display only passing grades

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val scores = listOf(85, 92, 78, 45, 88, 67, 95, 52, 73, 89)

    println("Test Scores: $scores")
    println()

    // Average
    val average = scores.average()
    println("Average: %.1f".format(average))

    // Highest and lowest
    val highest = scores.maxOrNull() ?: 0
    val lowest = scores.minOrNull() ?: 0
    println("Highest: $highest")
    println("Lowest: $lowest")

    // Count passing
    val passing = scores.filter { it >= 60 }
    println("\nPassing scores (≥60): ${passing.size}")
    println("Passing grades: $passing")

    // Count failing
    val failing = scores.filter { it < 60 }
    println("\nFailing scores (<60): ${failing.size}")
    println("Failing grades: $failing")
}
```

**Output:**
```
Test Scores: [85, 92, 78, 45, 88, 67, 95, 52, 73, 89]

Average: 76.4
Highest: 95
Lowest: 45

Passing scores (≥60): 8
Passing grades: [85, 92, 78, 88, 67, 95, 73, 89]

Failing scores (<60): 2
Failing grades: [45, 52]
```
</details>

---

### Exercise 3: Word Filter

**Challenge:** Create a program that:
1. Takes a list of words
2. Filters words longer than 5 characters
3. Converts them to uppercase
4. Sorts them alphabetically

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val words = listOf(
        "cat", "elephant", "dog", "butterfly",
        "ant", "giraffe", "bird", "hippopotamus"
    )

    println("Original words:")
    println(words)
    println()

    val result = words
        .filter { it.length > 5 }           // Keep long words
        .map { it.uppercase() }             // Convert to uppercase
        .sorted()                           // Sort alphabetically

    println("Filtered, uppercase, and sorted:")
    println(result)

    println("\nStep by step:")
    println("1. After filter: ${words.filter { it.length > 5 }}")
    println("2. After map: ${words.filter { it.length > 5 }.map { it.uppercase() }}")
    println("3. After sort: $result")
}
```

**Output:**
```
Original words:
[cat, elephant, dog, butterfly, ant, giraffe, bird, hippopotamus]

Filtered, uppercase, and sorted:
[BUTTERFLY, ELEPHANT, GIRAFFE, HIPPOPOTAMUS]

Step by step:
1. After filter: [elephant, butterfly, giraffe, hippopotamus]
2. After map: [ELEPHANT, BUTTERFLY, GIRAFFE, HIPPOPOTAMUS]
3. After sort: [BUTTERFLY, ELEPHANT, GIRAFFE, HIPPOPOTAMUS]
```
</details>

---

### Exercise 4: Number Statistics

**Challenge:** Create a statistics program that takes a list of numbers and displays:
1. Sum
2. Average
3. Numbers above average
4. Numbers below average
5. Median (middle value when sorted)

<details>
<summary>Click to see solution</summary>

```kotlin
fun main() {
    val numbers = listOf(23, 45, 12, 67, 34, 89, 15, 56, 78, 91)

    println("Numbers: $numbers")
    println()

    // Sum and average
    val sum = numbers.sum()
    val average = numbers.average()
    println("Sum: $sum")
    println("Average: %.1f".format(average))

    // Above and below average
    val aboveAvg = numbers.filter { it > average }
    val belowAvg = numbers.filter { it < average }

    println("\nAbove average (${aboveAvg.size}): $aboveAvg")
    println("Below average (${belowAvg.size}): $belowAvg")

    // Median
    val sorted = numbers.sorted()
    val median = if (sorted.size % 2 == 0) {
        (sorted[sorted.size / 2 - 1] + sorted[sorted.size / 2]) / 2.0
    } else {
        sorted[sorted.size / 2].toDouble()
    }

    println("\nSorted: $sorted")
    println("Median: $median")
}
```

**Output:**
```
Numbers: [23, 45, 12, 67, 34, 89, 15, 56, 78, 91]

Sum: 510
Average: 51.0

Above average (5): [67, 89, 56, 78, 91]
Below average (5): [23, 45, 12, 34, 15]

Sorted: [12, 15, 23, 34, 45, 56, 67, 78, 89, 91]
Median: 50.5
```
</details>

---

## Common Pitfalls and Best Practices

### Pitfall 1: Index Out of Bounds

❌ **Crash:**
```kotlin
val list = listOf(1, 2, 3)
println(list[5])  // Exception: Index out of bounds!
```

✅ **Safe:**
```kotlin
val list = listOf(1, 2, 3)
println(list.getOrNull(5))  // null (no crash)
println(list.getOrElse(5) { 0 })  // 0 (default value)
```

### Pitfall 2: Modifying Immutable Lists

❌ **Error:**
```kotlin
val list = listOf(1, 2, 3)
list.add(4)  // Error: Unresolved reference
```

✅ **Correct:**
```kotlin
val list = mutableListOf(1, 2, 3)
list.add(4)  // Works!
```

### Pitfall 3: Forgetting Lists Are Zero-Indexed

❌ **Confusion:**
```kotlin
val items = listOf("First", "Second", "Third")
println(items[1])  // "Second", not "First"!
```

✅ **Remember:**
```kotlin
val items = listOf("First", "Second", "Third")
println("Index 0: ${items[0]}")  // "First"
println("Index 1: ${items[1]}")  // "Second"
println("Index 2: ${items[2]}")  // "Third"
```

### Best Practice 1: Use val with Mutable Lists

```kotlin
// ✅ Good: val reference, mutable contents
val list = mutableListOf(1, 2, 3)
list.add(4)  // Can modify contents
// list = mutableListOf(5, 6)  // Can't reassign

// ❌ Avoid: var with mutable list (too much mutability)
var list2 = mutableListOf(1, 2, 3)
list2.add(4)  // Can modify
list2 = mutableListOf(5, 6)  // Can reassign (confusing!)
```

### Best Practice 2: Prefer Immutable When Possible

```kotlin
// ✅ Good: Won't change? Use listOf
val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

// Only use mutable when necessary
val shoppingCart = mutableListOf<String>()  // Will change
```

### Best Practice 3: Use Collection Functions

```kotlin
// ❌ Manual (verbose)
val numbers = listOf(1, 2, 3, 4, 5)
val evens = mutableListOf<Int>()
for (num in numbers) {
    if (num % 2 == 0) {
        evens.add(num)
    }
}

// ✅ Functional (concise)
val evens2 = numbers.filter { it % 2 == 0 }
```

---

## Quick Quiz

**Question 1:** What's the output?
```kotlin
val list = listOf("A", "B", "C")
println(list[0])
println(list.last())
```

<details>
<summary>Answer</summary>

**Output:**
```
A
C
```

**Explanation:** `list[0]` gets the first element, `last()` gets the last element.
</details>

---

**Question 2:** What's wrong with this code?
```kotlin
val numbers = listOf(1, 2, 3)
numbers.add(4)
```

<details>
<summary>Answer</summary>

**Error:** `listOf()` creates an **immutable** list. You can't add to it.

**Fix:** Use `mutableListOf()` instead:
```kotlin
val numbers = mutableListOf(1, 2, 3)
numbers.add(4)  // Now it works!
```
</details>

---

**Question 3:** What does this produce?
```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val result = numbers.filter { it > 2 }.map { it * 2 }
println(result)
```

<details>
<summary>Answer</summary>

**Output:** `[6, 8, 10]`

**Explanation:**
1. Filter keeps: `[3, 4, 5]` (values > 2)
2. Map doubles: `[6, 8, 10]`
</details>

---

**Question 4:** What's the size?
```kotlin
val list = mutableListOf(1, 2, 3)
list.add(4)
list.remove(2)
println(list.size)
```

<details>
<summary>Answer</summary>

**Output:** `3`

**Explanation:**
1. Start: `[1, 2, 3]` (size 3)
2. Add 4: `[1, 2, 3, 4]` (size 4)
3. Remove 2: `[1, 3, 4]` (size 3)
</details>

---

## Summary

Congratulations! You've mastered lists in Kotlin. Let's recap:

**Key Concepts:**
- **Lists** store multiple items in order
- **Immutable lists** (`listOf`) can't be changed
- **Mutable lists** (`mutableListOf`) can be modified
- **Zero-indexed**: First element is at index 0
- **Rich operations**: map, filter, sort, find, and more

**List Creation:**
```kotlin
val immutable = listOf(1, 2, 3)
val mutable = mutableListOf(1, 2, 3)
val empty = emptyList<String>()
```

**Common Operations:**
```kotlin
// Access
list[0], list.first(), list.last()

// Modify (mutable only)
list.add(item)
list.remove(item)
list.removeAt(index)

// Transform
list.map { }      // Transform each
list.filter { }   // Keep matching
list.sorted()     // Sort

// Aggregate
list.sum()
list.average()
list.maxOrNull()
```

**Best Practices:**
- Use immutable lists by default
- Prefer collection functions over manual loops
- Use safe access methods (getOrNull)
- Remember zero-based indexing
- Use val with mutable lists

---

## What's Next?

You can now store and manipulate lists of items, but what if you need to look up data by a key? Like finding a phone number by name, or a definition by word?

In **Lesson 2.7: Maps and Part 2 Capstone**, you'll learn:
- Maps for key-value pairs
- Creating and using maps
- Map operations and functions
- **Part 2 Capstone Project**: Combine everything you've learned!

**Preview:**
```kotlin
val phoneBook = mapOf(
    "Alice" to "555-1234",
    "Bob" to "555-5678"
)

println(phoneBook["Alice"])  // 555-1234
```

Get ready for the final lesson of Part 2 and an exciting capstone project!

---

**Amazing progress! You've completed Lesson 2.6. One more lesson to go!** 🎉

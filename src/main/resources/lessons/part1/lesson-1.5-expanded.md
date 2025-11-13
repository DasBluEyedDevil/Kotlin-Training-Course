# Lesson 1.5: Collections & Arrays

**Estimated Time**: 65 minutes

---

## Topic Introduction

So far, you've worked with individual values—one number, one string, one boolean. But real programs often need to work with **groups** of data: a list of students, a shopping cart of items, a phonebook of contacts.

This lesson teaches you how to store and manipulate collections of data using **Lists**, **Sets**, **Maps**, and **Arrays**—essential tools for any Kotlin programmer.

---

## The Concept

### The Container Analogy

Think of collections as different types of containers:

**List** = Playlist
- Ordered sequence of songs
- Can have duplicates (same song twice)
- You can access by position: "Play song #3"

**Set** = Unique Badge Collection
- No duplicates allowed
- Unordered (or natural order)
- Great for checking membership: "Do I have the gold badge?"

**Map** = Dictionary
- Key-value pairs
- Look up definitions by word
- Each key is unique: "What does 'hello' mean in Spanish?"

**Array** = Fixed-size parking lot
- Fixed number of spaces
- Direct access by position
- Size cannot change after creation

---

## Lists

Lists are ordered collections that can contain duplicates.

### Read-Only Lists (listOf)

```kotlin
val fruits = listOf("Apple", "Banana", "Cherry")

println(fruits)        // [Apple, Banana, Cherry]
println(fruits[0])     // Apple (first element)
println(fruits[1])     // Banana
println(fruits.size)   // 3
```

### Accessing List Elements

```kotlin
val numbers = listOf(10, 20, 30, 40, 50)

// By index
println(numbers[0])         // 10
println(numbers[2])         // 30

// First and last
println(numbers.first())    // 10
println(numbers.last())     // 50

// Get with default value
println(numbers.getOrNull(10))  // null (index out of bounds)
println(numbers.getOrElse(10) { 0 })  // 0 (default value)
```

### Mutable Lists (mutableListOf)

```kotlin
val shoppingCart = mutableListOf("Milk", "Bread")

// Add items
shoppingCart.add("Eggs")
shoppingCart.add("Butter")
println(shoppingCart)  // [Milk, Bread, Eggs, Butter]

// Add at specific position
shoppingCart.add(0, "Coffee")  // Add at beginning
println(shoppingCart)  // [Coffee, Milk, Bread, Eggs, Butter]

// Remove items
shoppingCart.remove("Bread")
println(shoppingCart)  // [Coffee, Milk, Eggs, Butter]

// Remove by index
shoppingCart.removeAt(0)
println(shoppingCart)  // [Milk, Eggs, Butter]

// Check if empty
println(shoppingCart.isEmpty())  // false
println(shoppingCart.isNotEmpty())  // true

// Clear all items
shoppingCart.clear()
println(shoppingCart)  // []
```

### List Operations

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// Check if contains
println(numbers.contains(3))     // true
println(3 in numbers)            // true (same thing)
println(10 in numbers)           // false

// Get index
println(numbers.indexOf(3))      // 2
println(numbers.indexOf(10))     // -1 (not found)

// Sublist
println(numbers.subList(1, 4))   // [2, 3, 4]

// Reverse
println(numbers.reversed())      // [5, 4, 3, 2, 1]

// Sort (returns new list)
val unsorted = listOf(5, 2, 8, 1, 9)
println(unsorted.sorted())       // [1, 2, 5, 8, 9]
println(unsorted.sortedDescending())  // [9, 8, 5, 2, 1]
```

---

## Sets

Sets are collections of **unique** elements (no duplicates).

### Read-Only Sets (setOf)

```kotlin
val uniqueNumbers = setOf(1, 2, 3, 2, 1)  // Duplicates removed
println(uniqueNumbers)  // [1, 2, 3]

val colors = setOf("Red", "Blue", "Green", "Red")
println(colors)  // [Red, Blue, Green]
println(colors.size)  // 3
```

### Mutable Sets (mutableSetOf)

```kotlin
val tags = mutableSetOf("kotlin", "programming")

// Add elements
tags.add("fun")
tags.add("kotlin")  // Already exists, won't add duplicate
println(tags)  // [kotlin, programming, fun]

// Remove elements
tags.remove("programming")
println(tags)  // [kotlin, fun]

// Check membership
println("kotlin" in tags)  // true
println("java" in tags)    // false
```

### Set Operations

```kotlin
val set1 = setOf(1, 2, 3, 4)
val set2 = setOf(3, 4, 5, 6)

// Union (combine, remove duplicates)
println(set1 union set2)        // [1, 2, 3, 4, 5, 6]

// Intersection (common elements)
println(set1 intersect set2)    // [3, 4]

// Difference (in first but not in second)
println(set1 subtract set2)     // [1, 2]
```

### When to Use Sets

Use sets when:
- You need unique elements
- Order doesn't matter
- You need fast membership checking

```kotlin
// Example: Track unique visitors
val visitors = mutableSetOf<String>()

visitors.add("Alice")
visitors.add("Bob")
visitors.add("Alice")  // Duplicate, ignored
visitors.add("Carol")

println("Unique visitors: ${visitors.size}")  // 3
```

---

## Maps

Maps store **key-value pairs** (like a dictionary).

### Read-Only Maps (mapOf)

```kotlin
val phoneBook = mapOf(
    "Alice" to "555-1234",
    "Bob" to "555-5678",
    "Carol" to "555-9012"
)

// Access by key
println(phoneBook["Alice"])  // 555-1234
println(phoneBook["Bob"])    // 555-5678

// Get with default
println(phoneBook.getOrDefault("Dave", "Unknown"))  // Unknown

// Check if key exists
println("Alice" in phoneBook)  // true
println("Dave" in phoneBook)   // false
```

### Mutable Maps (mutableMapOf)

```kotlin
val inventory = mutableMapOf(
    "Apples" to 50,
    "Bananas" to 30,
    "Oranges" to 25
)

// Add or update
inventory["Grapes"] = 40  // Add new
inventory["Apples"] = 55  // Update existing
println(inventory)

// Remove
inventory.remove("Bananas")
println(inventory)

// Get keys and values
println(inventory.keys)    // [Apples, Oranges, Grapes]
println(inventory.values)  // [55, 25, 40]
```

### Iterating Over Maps

```kotlin
val scores = mapOf(
    "Alice" to 95,
    "Bob" to 87,
    "Carol" to 92
)

// Iterate over entries
for ((name, score) in scores) {
    println("$name scored $score")
}

// Or
for (entry in scores) {
    println("${entry.key} scored ${entry.value}")
}
```

**Output**:
```
Alice scored 95
Bob scored 87
Carol scored 92
```

### Map Operations

```kotlin
val grades = mapOf("Math" to 95, "English" to 88, "Science" to 92)

println(grades.size)           // 3
println(grades.isEmpty())      // false
println(grades.containsKey("Math"))    // true
println(grades.containsValue(95))      // true

// Get all keys and values
println(grades.keys)    // [Math, English, Science]
println(grades.values)  // [95, 88, 92]
```

---

## Arrays

Arrays are **fixed-size** collections with indexed access.

### Creating Arrays

```kotlin
// Array of integers
val numbers = arrayOf(1, 2, 3, 4, 5)

// Array of strings
val names = arrayOf("Alice", "Bob", "Carol")

// Array with specific type
val doubles: Array<Double> = arrayOf(1.1, 2.2, 3.3)

// Array of specific size (all zeros)
val zeros = IntArray(5)  // [0, 0, 0, 0, 0]

// Array with initialization function
val squares = IntArray(5) { i -> i * i }  // [0, 1, 4, 9, 16]
```

### Accessing Array Elements

```kotlin
val fruits = arrayOf("Apple", "Banana", "Cherry")

println(fruits[0])     // Apple
println(fruits[1])     // Banana

// Modify elements
fruits[1] = "Blueberry"
println(fruits[1])     // Blueberry

// Size
println(fruits.size)   // 3
```

### Array vs List

```kotlin
// Array (fixed size, mutable elements)
val array = arrayOf(1, 2, 3)
array[0] = 10  // ✅ OK
// array.add(4)  // ❌ Error: Can't change size

// List (immutable)
val list = listOf(1, 2, 3)
// list[0] = 10  // ❌ Error: Can't modify
// list.add(4)   // ❌ Error: Can't add

// Mutable list (flexible)
val mutableList = mutableListOf(1, 2, 3)
mutableList[0] = 10  // ✅ OK
mutableList.add(4)   // ✅ OK
```

**When to use Arrays vs Lists**:
- **Arrays**: Performance-critical code, fixed size, interop with Java
- **Lists**: Most Kotlin code (more flexible, better API)

---

## Common Collection Operations

### forEach - Execute action for each element

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

numbers.forEach { number ->
    println(number * 2)
}

// Output: 2, 4, 6, 8, 10
```

### filter - Select elements matching a condition

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

val evenNumbers = numbers.filter { it % 2 == 0 }
println(evenNumbers)  // [2, 4, 6, 8, 10]

val greaterThanFive = numbers.filter { it > 5 }
println(greaterThanFive)  // [6, 7, 8, 9, 10]
```

### map - Transform each element

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

val doubled = numbers.map { it * 2 }
println(doubled)  // [2, 4, 6, 8, 10]

val squared = numbers.map { it * it }
println(squared)  // [1, 4, 9, 16, 25]

val lengths = listOf("a", "ab", "abc").map { it.length }
println(lengths)  // [1, 2, 3]
```

### Combining Operations

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Get squares of even numbers
val result = numbers
    .filter { it % 2 == 0 }  // [2, 4, 6, 8, 10]
    .map { it * it }          // [4, 16, 36, 64, 100]

println(result)  // [4, 16, 36, 64, 100]
```

### More Useful Operations

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

// sum
println(numbers.sum())  // 15

// average
println(numbers.average())  // 3.0

// max and min
println(numbers.max())  // 5
println(numbers.min())  // 1

// count
println(numbers.count { it > 3 })  // 2 (elements: 4, 5)

// any - check if any element matches
println(numbers.any { it > 4 })  // true

// all - check if all elements match
println(numbers.all { it > 0 })  // true

// none - check if no elements match
println(numbers.none { it < 0 })  // true

// find - get first matching element
println(numbers.find { it > 3 })  // 4

// take - get first n elements
println(numbers.take(3))  // [1, 2, 3]

// drop - skip first n elements
println(numbers.drop(2))  // [3, 4, 5]
```

---

## Exercise 1: Student Grade Manager

**Goal**: Create a program to manage student grades using a map.

**Requirements**:
1. Create a mutable map to store student names and grades
2. Add at least 5 students with their grades
3. Display all students and grades
4. Calculate and display the average grade
5. Display students who scored above 80
6. Display the highest and lowest grades

---

## Solution 1: Student Grade Manager

```kotlin
fun main() {
    val grades = mutableMapOf(
        "Alice" to 92,
        "Bob" to 78,
        "Carol" to 95,
        "Dave" to 88,
        "Eve" to 73
    )

    println("=== Student Grade Manager ===\n")

    // Display all students and grades
    println("All Students:")
    for ((name, grade) in grades) {
        println("  $name: $grade")
    }

    // Calculate average
    val average = grades.values.average()
    println("\nAverage Grade: ${"%.2f".format(average)}")

    // Students above 80
    println("\nStudents with grade > 80:")
    val topStudents = grades.filter { it.value > 80 }
    for ((name, grade) in topStudents) {
        println("  $name: $grade")
    }

    // Highest and lowest
    val highest = grades.maxBy { it.value }
    val lowest = grades.minBy { it.value }

    println("\nHighest Grade: ${highest.key} with ${highest.value}")
    println("Lowest Grade: ${lowest.key} with ${lowest.value}")
}
```

**Sample Output**:
```
=== Student Grade Manager ===

All Students:
  Alice: 92
  Bob: 78
  Carol: 95
  Dave: 88
  Eve: 73

Average Grade: 85.20

Students with grade > 80:
  Alice: 92
  Carol: 95
  Dave: 88

Highest Grade: Carol with 95
Lowest Grade: Eve with 73
```

---

## Exercise 2: Shopping Cart with Unique Items

**Goal**: Create a shopping cart that tracks items and quantities.

**Requirements**:
1. Use a mutable map where keys are item names and values are quantities
2. Create `addItem(cart, item, quantity)` function
3. Create `removeItem(cart, item)` function
4. Create `updateQuantity(cart, item, newQuantity)` function
5. Create `displayCart(cart)` function that shows all items
6. Calculate total number of items in cart

---

## Solution 2: Shopping Cart with Unique Items

```kotlin
fun addItem(cart: MutableMap<String, Int>, item: String, quantity: Int) {
    val currentQuantity = cart.getOrDefault(item, 0)
    cart[item] = currentQuantity + quantity
    println("Added $quantity x $item")
}

fun removeItem(cart: MutableMap<String, Int>, item: String) {
    if (cart.remove(item) != null) {
        println("Removed $item from cart")
    } else {
        println("$item not found in cart")
    }
}

fun updateQuantity(cart: MutableMap<String, Int>, item: String, newQuantity: Int) {
    if (item in cart) {
        cart[item] = newQuantity
        println("Updated $item quantity to $newQuantity")
    } else {
        println("$item not found in cart")
    }
}

fun displayCart(cart: Map<String, Int>) {
    if (cart.isEmpty()) {
        println("Cart is empty")
        return
    }

    println("\n=== Shopping Cart ===")
    for ((item, quantity) in cart) {
        println("  $item: $quantity")
    }

    val totalItems = cart.values.sum()
    println("Total items: $totalItems")
}

fun main() {
    val cart = mutableMapOf<String, Int>()

    addItem(cart, "Apple", 5)
    addItem(cart, "Banana", 3)
    addItem(cart, "Orange", 4)
    displayCart(cart)

    addItem(cart, "Apple", 2)  // Add more apples
    displayCart(cart)

    updateQuantity(cart, "Banana", 6)
    displayCart(cart)

    removeItem(cart, "Orange")
    displayCart(cart)
}
```

**Sample Output**:
```
Added 5 x Apple
Added 3 x Banana
Added 4 x Orange

=== Shopping Cart ===
  Apple: 5
  Banana: 3
  Orange: 4
Total items: 12

Added 2 x Apple

=== Shopping Cart ===
  Apple: 7
  Banana: 3
  Orange: 4
Total items: 14

Updated Banana quantity to 6

=== Shopping Cart ===
  Apple: 7
  Banana: 6
  Orange: 4
Total items: 17

Removed Orange from cart

=== Shopping Cart ===
  Apple: 7
  Banana: 6
Total items: 13
```

---

## Exercise 3: Word Frequency Counter

**Goal**: Count how many times each word appears in a sentence.

**Requirements**:
1. Take a sentence as input
2. Split it into words
3. Count frequency of each word (case-insensitive)
4. Display words and their counts
5. Show the most common word

---

## Solution 3: Word Frequency Counter

```kotlin
fun main() {
    println("Enter a sentence:")
    val sentence = readln()

    // Split into words and convert to lowercase
    val words = sentence.lowercase().split(" ")

    // Count word frequencies
    val wordCount = mutableMapOf<String, Int>()

    for (word in words) {
        val cleaned = word.replace(Regex("[^a-z]"), "")  // Remove punctuation
        if (cleaned.isNotEmpty()) {
            wordCount[cleaned] = wordCount.getOrDefault(cleaned, 0) + 1
        }
    }

    // Display results
    println("\n=== Word Frequency ===")
    for ((word, count) in wordCount.toList().sortedByDescending { it.second }) {
        println("$word: $count")
    }

    // Find most common word
    val mostCommon = wordCount.maxBy { it.value }
    println("\nMost common word: '${mostCommon.key}' (appears ${mostCommon.value} times)")

    println("\nTotal unique words: ${wordCount.size}")
    println("Total words: ${words.size}")
}
```

**Sample Run**:
```
Enter a sentence:
The quick brown fox jumps over the lazy dog. The fox is quick!

=== Word Frequency ===
the: 3
quick: 2
fox: 2
brown: 1
jumps: 1
over: 1
lazy: 1
dog: 1
is: 1

Most common word: 'the' (appears 3 times)

Total unique words: 9
Total words: 12
```

---

## Collection Type Selection Guide

| Collection | When to Use | Example Use Case |
|------------|-------------|------------------|
| **List** | Ordered elements, duplicates OK | Shopping cart items, playlist |
| **MutableList** | Need to add/remove elements | To-do list, dynamic data |
| **Set** | Unique elements only | User IDs, tags, categories |
| **MutableSet** | Unique elements, add/remove | Active users, visited URLs |
| **Map** | Key-value lookups | Phone book, inventory, settings |
| **MutableMap** | Need to update key-values | Cache, session data |
| **Array** | Fixed size, performance-critical | Low-level operations, Java interop |

---

## Common Mistakes

### Mistake 1: Modifying Read-Only Collections

```kotlin
// ❌ Error
val list = listOf(1, 2, 3)
list.add(4)  // Error: Unresolved reference

// ✅ Correct
val list = mutableListOf(1, 2, 3)
list.add(4)  // OK
```

### Mistake 2: Index Out of Bounds

```kotlin
val numbers = listOf(1, 2, 3)

// ❌ Crash
println(numbers[5])  // IndexOutOfBoundsException

// ✅ Safe
println(numbers.getOrNull(5))  // null
println(numbers.getOrElse(5) { 0 })  // 0
```

### Mistake 3: Forgetting Map Values are Nullable

```kotlin
val phoneBook = mapOf("Alice" to "555-1234")

// ❌ Potential null
val number = phoneBook["Bob"]  // Returns String?, not String!

// ✅ Handle null
val number = phoneBook["Bob"] ?: "Unknown"
val number2 = phoneBook.getOrDefault("Bob", "Unknown")
```

---

## Checkpoint Quiz

### Question 1
What's the difference between `listOf` and `mutableListOf`?

A) No difference
B) `listOf` is read-only, `mutableListOf` allows adding/removing elements
C) `listOf` is faster
D) `mutableListOf` can't contain duplicates

### Question 2
Which collection type should you use for unique elements?

A) List
B) Array
C) Set
D) Map

### Question 3
How do you access a value in a map?

A) `map.get(key)`
B) `map[key]`
C) Both A and B
D) `map.value(key)`

### Question 4
What does the `filter` function return?

A) A single element
B) A boolean
C) A new collection with elements matching the condition
D) The original collection modified

### Question 5
What is the result of `listOf(1, 2, 2, 3).toSet()`?

A) `[1, 2, 2, 3]`
B) `[1, 2, 3]`
C) Error
D) `[1, 3]`

---

## Quiz Answers

**Question 1: B) `listOf` is read-only, `mutableListOf` allows adding/removing elements**

```kotlin
val readOnly = listOf(1, 2, 3)
// readOnly.add(4)  // ❌ Error

val mutable = mutableListOf(1, 2, 3)
mutable.add(4)  // ✅ OK
```

---

**Question 2: C) Set**

Sets automatically remove duplicates:

```kotlin
val numbers = setOf(1, 2, 2, 3, 3, 3)
println(numbers)  // [1, 2, 3]
```

---

**Question 3: C) Both A and B**

Both syntaxes work:

```kotlin
val map = mapOf("name" to "Alice")

println(map.get("name"))  // Alice
println(map["name"])      // Alice (preferred)
```

---

**Question 4: C) A new collection with elements matching the condition**

`filter` returns a new collection; it doesn't modify the original:

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val evens = numbers.filter { it % 2 == 0 }

println(numbers)  // [1, 2, 3, 4, 5] (unchanged)
println(evens)    // [2, 4] (new list)
```

---

**Question 5: B) `[1, 2, 3]`**

Converting a list to a set removes duplicates:

```kotlin
val list = listOf(1, 2, 2, 3)
val set = list.toSet()
println(set)  // [1, 2, 3]
```

---

## What You've Learned

✅ Lists for ordered collections with duplicates
✅ Sets for unique elements
✅ Maps for key-value pairs
✅ Arrays for fixed-size collections
✅ Difference between read-only and mutable collections
✅ Common operations: forEach, filter, map
✅ When to use each collection type
✅ How to iterate and manipulate collections

---

## Next Steps

In **Lesson 1.6: Null Safety & Safe Calls**, you'll learn:
- Kotlin's null safety system
- Safe call operator (`?.`)
- Elvis operator (`?:`)
- Not-null assertion (`!!`)
- How to write crash-free code

Get ready to learn one of Kotlin's most powerful features!

---

**Congratulations on completing Lesson 1.5!**

You now know how to work with collections—essential for managing groups of data in real applications!

# Lesson 2.6: Lists - Storing Multiple Items

## Collections of Data

So far, each variable holds ONE value. But what if you need to store many related items - like a shopping list, a playlist, or student grades?

**Analogy:** Think of a todo list on paper:
- ☐ Buy milk
- ☐ Walk dog
- ☐ Study Kotlin

Instead of creating separate variables (`task1`, `task2`, `task3`...), you have ONE list with multiple items!

---

## What is a List?

A **List** is a collection that stores multiple items in order.

```kotlin
fun main() {
    val fruits = listOf("Apple", "Banana", "Cherry")
    println(fruits)
}
```

**Output:**
```
[Apple, Banana, Cherry]
```

---

## Creating Lists

### Read-Only List (Immutable):

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val names = listOf("Alice", "Bob", "Charlie")
val mixed = listOf(1, "Hello", 3.14, true)  // Can mix types (not recommended)
```

**`listOf()`** creates a list you can READ but not MODIFY.

---

### Mutable List (Changeable):

```kotlin
val groceries = mutableListOf("Milk", "Bread", "Eggs")
groceries.add("Butter")  // Add item
groceries.remove("Bread")  // Remove item
println(groceries)  // [Milk, Eggs, Butter]
```

**`mutableListOf()`** creates a list you can ADD to, REMOVE from, and MODIFY.

---

## Accessing List Elements

Lists are **indexed** starting from 0:

```
Index:  0        1         2
List:  ["Apple", "Banana", "Cherry"]
```

### Getting Elements:

```kotlin
fun main() {
    val fruits = listOf("Apple", "Banana", "Cherry")

    println(fruits[0])  // Apple (first item)
    println(fruits[1])  // Banana
    println(fruits[2])  // Cherry

    println(fruits.first())  // Apple
    println(fruits.last())   // Cherry
}
```

---

## List Properties

```kotlin
fun main() {
    val numbers = listOf(10, 20, 30, 40, 50)

    println("Size: ${numbers.size}")        // 5
    println("Empty? ${numbers.isEmpty()}")  // false
    println("Contains 30? ${numbers.contains(30)}")  // true
}
```

---

## Looping Through Lists

### With `for` Loop:

```kotlin
fun main() {
    val colors = listOf("Red", "Green", "Blue")

    for (color in colors) {
        println(color)
    }
}
```

**Output:**
```
Red
Green
Blue
```

---

### With Index:

```kotlin
fun main() {
    val colors = listOf("Red", "Green", "Blue")

    for (index in colors.indices) {
        println("$index: ${colors[index]}")
    }
}
```

**Output:**
```
0: Red
1: Green
2: Blue
```

---

### With `forEachIndexed`:

```kotlin
fun main() {
    val colors = listOf("Red", "Green", "Blue")

    colors.forEachIndexed { index, color ->
        println("$index: $color")
    }
}
```

---

## Modifying Mutable Lists

```kotlin
fun main() {
    val tasks = mutableListOf("Code", "Exercise", "Read")

    // Add items
    tasks.add("Sleep")
    tasks.add(0, "Wake up")  // Add at specific position

    // Remove items
    tasks.remove("Exercise")  // Remove by value
    tasks.removeAt(2)  // Remove at index 2

    // Change items
    tasks[0] = "Get up early"

    // Clear all
    // tasks.clear()

    println(tasks)
}
```

---

## List Operations

### Sorting:

```kotlin
fun main() {
    val numbers = listOf(5, 2, 8, 1, 9)

    val sorted = numbers.sorted()
    println(sorted)  // [1, 2, 5, 8, 9]

    val reversed = numbers.sortedDescending()
    println(reversed)  // [9, 8, 5, 2, 1]
}
```

---

### Filtering:

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    val evens = numbers.filter { it % 2 == 0 }
    println("Evens: $evens")  // [2, 4, 6, 8, 10]

    val greaterThanFive = numbers.filter { it > 5 }
    println("Greater than 5: $greaterThanFive")  // [6, 7, 8, 9, 10]
}
```

---

### Mapping:

```kotlin
fun main() {
    val numbers = listOf(1, 2, 3, 4, 5)

    val doubled = numbers.map { it * 2 }
    println(doubled)  // [2, 4, 6, 8, 10]

    val names = listOf("alice", "bob", "charlie")
    val capitalized = names.map { it.capitalize() }
    println(capitalized)  // [Alice, Bob, Charlie]
}
```

---

## Interactive Coding Session

### Challenge 1: Shopping List Manager

```kotlin
fun main() {
    val shoppingList = mutableListOf<String>()

    while (true) {
        println("\n--- Shopping List ---")
        if (shoppingList.isEmpty()) {
            println("(empty)")
        } else {
            shoppingList.forEachIndexed { index, item ->
                println("${index + 1}. $item")
            }
        }

        println("\n1. Add item")
        println("2. Remove item")
        println("3. Exit")
        print("Choice: ")

        when (readLine()!!.toInt()) {
            1 -> {
                print("Enter item: ")
                shoppingList.add(readLine()!!)
            }
            2 -> {
                print("Enter item number: ")
                val index = readLine()!!.toInt() - 1
                if (index in shoppingList.indices) {
                    shoppingList.removeAt(index)
                }
            }
            3 -> break
        }
    }
}
```

---

### Challenge 2: Grade Calculator

```kotlin
fun main() {
    val grades = mutableListOf<Int>()

    println("Enter grades (or -1 to finish):")
    while (true) {
        print("> ")
        val grade = readLine()!!.toInt()
        if (grade == -1) break
        grades.add(grade)
    }

    if (grades.isNotEmpty()) {
        val average = grades.average()
        val highest = grades.maxOrNull()
        val lowest = grades.minOrNull()

        println("\n--- Results ---")
        println("Count: ${grades.size}")
        println("Average: ${"%.2f".format(average)}")
        println("Highest: $highest")
        println("Lowest: $lowest")
    }
}
```

---

### Challenge 3: Filter and Sort

```kotlin
fun main() {
    val numbers = listOf(15, 3, 27, 8, 42, 11, 19, 5, 33)

    println("Original: $numbers")

    val sorted = numbers.sorted()
    println("Sorted: $sorted")

    val evens = numbers.filter { it % 2 == 0 }
    println("Evens: $evens")

    val greaterThan10 = numbers.filter { it > 10 }.sorted()
    println("Greater than 10 (sorted): $greaterThan10")
}
```

---

## List vs Array

**List:** More common in Kotlin, flexible
```kotlin
val list = listOf(1, 2, 3)
```

**Array:** Fixed size, better performance
```kotlin
val array = arrayOf(1, 2, 3)
```

**Use Lists** unless you have a specific reason to use Arrays!

---

## Recap: What You've Learned

You now understand:

1. **Lists** = Ordered collections of items
2. **`listOf()`** = Read-only list
3. **`mutableListOf()`** = Changeable list
4. **Indexing** = Starts at 0
5. **Looping** = Iterate through items
6. **Operations** = filter, map, sort, etc.

---

## What's Next?

Lists store items in order. But what if you want to store **key-value pairs** - like a dictionary? That's where **Maps** come in!

**Key Takeaways:**
- Lists store multiple items in order
- Index starts at 0
- Use `listOf()` for read-only, `mutableListOf()` for changeable
- Many built-in operations: filter, map, sort
- Perfect for storing related items
- Works great with loops!

---

Excellent! Continue to learn about Maps!

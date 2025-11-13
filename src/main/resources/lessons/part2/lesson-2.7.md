# Lesson 2.7: Maps - Key-Value Pairs

## Storing Data with Labels

Lists are great for ordered items, but what if you want to look things up by name instead of position?

**Analogy:** Think of a phone book:
- **List:** "Call the 5th person" (Who is that?)
- **Map:** "Call Alice" (Look up Alice's number)

A **Map** stores data as **key-value pairs** - like a dictionary!

---

## What is a Map?

A **Map** associates **keys** with **values**:

```kotlin
fun main() {
    val phoneBook = mapOf(
        "Alice" to "555-1234",
        "Bob" to "555-5678",
        "Charlie" to "555-9999"
    )

    println(phoneBook["Alice"])  // 555-1234
}
```

**Key:** "Alice"
**Value:** "555-1234"

---

## Creating Maps

### Read-Only Map:

```kotlin
val ages = mapOf(
    "Alice" to 25,
    "Bob" to 30,
    "Charlie" to 28
)
```

**`to`** creates a key-value pair (called a "Pair").

---

### Mutable Map:

```kotlin
val scores = mutableMapOf(
    "Alice" to 95,
    "Bob" to 87
)

scores["Charlie"] = 92  // Add new entry
scores["Alice"] = 98    // Update existing
scores.remove("Bob")    // Remove entry

println(scores)
```

---

## Accessing Map Values

```kotlin
fun main() {
    val capitals = mapOf(
        "France" to "Paris",
        "Japan" to "Tokyo",
        "USA" to "Washington D.C."
    )

    // Get value by key
    println(capitals["France"])  // Paris

    // Safe access (returns null if not found)
    println(capitals["Germany"])  // null

    // With default value
    println(capitals.getOrDefault("Germany", "Unknown"))  // Unknown
}
```

---

## Map Properties

```kotlin
fun main() {
    val inventory = mapOf(
        "apples" to 10,
        "bananas" to 5,
        "oranges" to 8
    )

    println("Size: ${inventory.size}")  // 3
    println("Empty? ${inventory.isEmpty()}")  // false
    println("Contains 'apples'? ${inventory.containsKey("apples")}")  // true
    println("Contains value 5? ${inventory.containsValue(5)}")  // true
}
```

---

## Looping Through Maps

### Loop Through Keys and Values:

```kotlin
fun main() {
    val grades = mapOf(
        "Math" to 90,
        "English" to 85,
        "Science" to 92
    )

    for ((subject, grade) in grades) {
        println("$subject: $grade")
    }
}
```

**Output:**
```
Math: 90
English: 85
Science: 92
```

---

### Loop Through Keys Only:

```kotlin
for (subject in grades.keys) {
    println(subject)
}
```

---

### Loop Through Values Only:

```kotlin
for (grade in grades.values) {
    println(grade)
}
```

---

## Modifying Mutable Maps

```kotlin
fun main() {
    val inventory = mutableMapOf(
        "apples" to 10,
        "bananas" to 5
    )

    // Add/update
    inventory["oranges"] = 8
    inventory["apples"] = 12  // Updates existing

    // Remove
    inventory.remove("bananas")

    // Put if absent
    inventory.putIfAbsent("grapes", 7)

    // Clear all
    // inventory.clear()

    println(inventory)
}
```

---

## Map Operations

### Get All Keys or Values:

```kotlin
fun main() {
    val scores = mapOf("Alice" to 95, "Bob" to 87, "Charlie" to 92)

    val players = scores.keys.toList()
    println("Players: $players")  // [Alice, Bob, Charlie]

    val allScores = scores.values.toList()
    println("Scores: $allScores")  // [95, 87, 92]
}
```

---

### Filter Maps:

```kotlin
fun main() {
    val scores = mapOf(
        "Alice" to 95,
        "Bob" to 75,
        "Charlie" to 92,
        "David" to 68
    )

    val passing = scores.filter { it.value >= 70 }
    println("Passing: $passing")  // {Alice=95, Bob=75, Charlie=92}
}
```

---

### Map Transformation:

```kotlin
fun main() {
    val prices = mapOf(
        "apple" to 1.50,
        "banana" to 0.75,
        "orange" to 1.25
    )

    val discounted = prices.mapValues { it.value * 0.9 }  // 10% off
    println(discounted)
}
```

---

## Interactive Coding Session

### Challenge 1: Student Grade Book

```kotlin
fun main() {
    val grades = mutableMapOf<String, Int>()

    while (true) {
        println("\n--- Grade Book ---")
        if (grades.isEmpty()) {
            println("(no students yet)")
        } else {
            for ((name, grade) in grades) {
                println("$name: $grade")
            }
        }

        println("\n1. Add/Update grade")
        println("2. Remove student")
        println("3. Find grade")
        println("4. Exit")
        print("Choice: ")

        when (readLine()!!.toInt()) {
            1 -> {
                print("Student name: ")
                val name = readLine()!!
                print("Grade: ")
                val grade = readLine()!!.toInt()
                grades[name] = grade
            }
            2 -> {
                print("Student name: ")
                grades.remove(readLine()!!)
            }
            3 -> {
                print("Student name: ")
                val name = readLine()!!
                val grade = grades[name]
                if (grade != null) {
                    println("$name's grade: $grade")
                } else {
                    println("Student not found")
                }
            }
            4 -> break
        }
    }
}
```

---

### Challenge 2: Word Counter

```kotlin
fun main() {
    print("Enter a sentence: ")
    val sentence = readLine()!!.lowercase()

    val words = sentence.split(" ")
    val wordCount = mutableMapOf<String, Int>()

    for (word in words) {
        wordCount[word] = wordCount.getOrDefault(word, 0) + 1
    }

    println("\nWord frequencies:")
    for ((word, count) in wordCount.toList().sortedByDescending { it.second }) {
        println("$word: $count")
    }
}
```

---

### Challenge 3: Simple Inventory System

```kotlin
fun main() {
    val inventory = mutableMapOf(
        "laptop" to 5,
        "mouse" to 20,
        "keyboard" to 15
    )

    println("Current inventory: $inventory")

    print("\nEnter item to restock: ")
    val item = readLine()!!
    print("Enter quantity to add: ")
    val quantity = readLine()!!.toInt()

    inventory[item] = inventory.getOrDefault(item, 0) + quantity

    println("\nUpdated inventory: $inventory")
}
```

---

## When to Use List vs Map

**Use List when:**
- Order matters
- You access by position/index
- You have a simple sequence
- Example: Task list, playlist, queue

**Use Map when:**
- You need quick lookup by name/ID
- Data has labels (keys)
- Order doesn't matter (or you can sort)
- Example: Phone book, dictionary, settings

---

## Nested Collections

You can combine lists and maps!

```kotlin
fun main() {
    // Map of lists
    val studentSubjects = mapOf(
        "Alice" to listOf("Math", "English", "Science"),
        "Bob" to listOf("Math", "Art", "History")
    )

    println(studentSubjects["Alice"])  // [Math, English, Science]

    // List of maps
    val users = listOf(
        mapOf("name" to "Alice", "age" to "25"),
        mapOf("name" to "Bob", "age" to "30")
    )
}
```

---

## Recap: What You've Learned

You now understand:

1. **Maps** = Key-value pairs (dictionary-like)
2. **`mapOf()`** = Read-only map
3. **`mutableMapOf()`** = Changeable map
4. **`to`** keyword = Creates pairs
5. **Access by key** = `map[key]`
6. **Looping** = Over keys, values, or both
7. **List vs Map** = When to use which

---

## Part 2 Complete! 🎉

Congratulations! You've completed Part 2 and learned:
- ✅ If/else statements
- ✅ Logical operators (&&, ||, !)
- ✅ When expressions
- ✅ For loops
- ✅ While/do-while loops
- ✅ Lists
- ✅ Maps

**You can now write programs that make decisions, repeat tasks, and work with collections of data!**

---

## What's Next?

In **Part 3**, you'll learn **Object-Oriented Programming** - creating your own custom types (classes) to model real-world things like Users, Products, and more!

**Key Takeaways:**
- Maps store key-value pairs
- Perfect for lookups by name/ID
- Use `mapOf()` or `mutableMapOf()`
- Can combine with lists for complex data
- Choose list vs map based on your needs

---

Amazing progress! Ready for Part 3? Mark complete and continue!

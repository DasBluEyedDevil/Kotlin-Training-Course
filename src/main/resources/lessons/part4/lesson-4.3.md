# Lesson 4.3: Collections Deep Dive and Sequences

## Mastering Kotlin Collections

Now that you understand lambdas, let's explore Kotlin's powerful collection operations in depth!

---

## Common Collection Operations

### map() - Transform Elements

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val squared = numbers.map { it * it }
println(squared)  // [1, 4, 9, 16, 25]

val names = listOf("alice", "bob", "charlie")
val uppercase = names.map { it.uppercase() }
println(uppercase)  // [ALICE, BOB, CHARLIE]
```

---

### filter() - Select Elements

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
val evens = numbers.filter { it % 2 == 0 }
val greaterThan5 = numbers.filter { it > 5 }

println(evens)  // [2, 4, 6, 8, 10]
println(greaterThan5)  // [6, 7, 8, 9, 10]
```

---

### find() and firstOrNull()

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val firstEven = numbers.find { it % 2 == 0 }
val firstGreaterThan10 = numbers.find { it > 10 }

println(firstEven)  // 2
println(firstGreaterThan10)  // null
```

---

### any(), all(), none()

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)

val hasEven = numbers.any { it % 2 == 0 }  // true
val allPositive = numbers.all { it > 0 }  // true
val noneNegative = numbers.none { it < 0 }  // true
```

---

### partition() - Split into Two

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6)
val (evens, odds) = numbers.partition { it % 2 == 0 }

println("Evens: $evens")  // [2, 4, 6]
println("Odds: $odds")  // [1, 3, 5]
```

---

### groupBy() - Group Elements

```kotlin
val words = listOf("apple", "apricot", "banana", "blueberry", "cherry")
val grouped = words.groupBy { it.first() }

println(grouped)
// {a=[apple, apricot], b=[banana, blueberry], c=[cherry]}
```

---

### associate() - Create Maps

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val squared = numbers.associate { it to it * it }

println(squared)
// {1=1, 2=4, 3=9, 4=16, 5=25}
```

---

### flatMap() - Flatten Nested Collections

```kotlin
val nested = listOf(
    listOf(1, 2, 3),
    listOf(4, 5),
    listOf(6, 7, 8, 9)
)

val flattened = nested.flatMap { it }
println(flattened)  // [1, 2, 3, 4, 5, 6, 7, 8, 9]

// More practical example
val words = listOf("Hello", "World")
val letters = words.flatMap { it.toList() }
println(letters)  // [H, e, l, l, o, W, o, r, l, d]
```

---

### zip() - Combine Two Collections

```kotlin
val names = listOf("Alice", "Bob", "Charlie")
val ages = listOf(25, 30, 35)

val people = names.zip(ages)
println(people)  // [(Alice, 25), (Bob, 30), (Charlie, 35)]

// With transformation
val descriptions = names.zip(ages) { name, age ->
    "$name is $age years old"
}
println(descriptions)
```

---

### windowed() - Sliding Windows

```kotlin
val numbers = listOf(1, 2, 3, 4, 5)
val windows = numbers.windowed(size = 3)

println(windows)
// [[1, 2, 3], [2, 3, 4], [3, 4, 5]]

// With step
val stepped = numbers.windowed(size = 3, step = 2)
println(stepped)
// [[1, 2, 3], [3, 4, 5]]
```

---

## Sequences - Lazy Evaluation

**Problem with collections:** Operations are eager - each creates a new list!

```kotlin
val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

// Creates 3 intermediate lists!
val result = numbers
    .filter { it % 2 == 0 }  // List 1
    .map { it * it }  // List 2
    .take(3)  // List 3

// Better: Use sequences (lazy evaluation)
val resultSeq = numbers.asSequence()
    .filter { it % 2 == 0 }
    .map { it * it }
    .take(3)
    .toList()
```

---

### When to Use Sequences

**Use sequences when:**
- Working with large collections
- Chaining many operations
- May not need all results (take, first, etc.)

```kotlin
// This is much more efficient with sequences
val result = (1..1_000_000).asSequence()
    .filter { it % 2 == 0 }
    .map { it * it }
    .take(10)
    .toList()
```

---

### Infinite Sequences

```kotlin
// Generate infinite sequence
val infiniteNumbers = generateSequence(1) { it + 1 }

// Take first 10
val first10 = infiniteNumbers.take(10).toList()
println(first10)  // [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

// Fibonacci sequence
val fibonacci = generateSequence(Pair(0, 1)) { (a, b) ->
    Pair(b, a + b)
}.map { it.first }

println(fibonacci.take(10).toList())
// [0, 1, 1, 2, 3, 5, 8, 13, 21, 34]
```

---

## Practical Examples

### Example 1: Data Processing Pipeline

```kotlin
data class Person(val name: String, val age: Int, val city: String)

val people = listOf(
    Person("Alice", 25, "New York"),
    Person("Bob", 30, "London"),
    Person("Charlie", 35, "New York"),
    Person("Diana", 28, "Paris"),
    Person("Eve", 32, "New York")
)

// Find average age of people in New York
val avgAgeNY = people
    .filter { it.city == "New York" }
    .map { it.age }
    .average()

println("Average age in New York: $avgAgeNY")

// Group by city
val byCity = people.groupBy { it.city }
println("\nPeople by city:")
byCity.forEach { (city, peopleInCity) ->
    println("$city: ${peopleInCity.map { it.name }}")
}
```

---

### Example 2: Word Frequency Counter

```kotlin
fun wordFrequency(text: String): Map<String, Int> {
    return text
        .lowercase()
        .split(Regex("\\W+"))
        .filter { it.isNotEmpty() }
        .groupingBy { it }
        .eachCount()
}

val text = "Hello world! Hello Kotlin. Kotlin is awesome!"
val frequencies = wordFrequency(text)

println("Word frequencies:")
frequencies.entries
    .sortedByDescending { it.value }
    .forEach { (word, count) ->
        println("$word: $count")
    }
```

---

### Example 3: Data Validation

```kotlin
data class User(val email: String, val age: Int, val name: String)

fun validateUsers(users: List<User>): List<String> {
    return users
        .filter { user ->
            user.email.contains("@") &&
            user.age >= 13 &&
            user.name.isNotBlank()
        }
        .map { it.email }
}

val users = listOf(
    User("alice@example.com", 25, "Alice"),
    User("invalid-email", 30, "Bob"),
    User("charlie@example.com", 10, "Charlie"),
    User("diana@example.com", 28, "")
)

val valid = validateUsers(users)
println("Valid users: $valid")
```

---

## Performance Tips

### 1. Use Sequences for Large Data

```kotlin
// Eager (creates intermediate lists)
val result1 = (1..1000000)
    .filter { it % 2 == 0 }
    .map { it * 2 }
    .take(10)

// Lazy (more efficient)
val result2 = (1..1000000).asSequence()
    .filter { it % 2 == 0 }
    .map { it * 2 }
    .take(10)
    .toList()
```

---

### 2. Use Inline Functions

Many collection operations are inline - no performance overhead!

```kotlin
inline fun <T> List<T>.customFilter(predicate: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (predicate(item)) {
            result.add(item)
        }
    }
    return result
}
```

---

### 3. Avoid Unnecessary Operations

```kotlin
// Bad: Multiple passes
val result = numbers
    .map { it * 2 }
    .filter { it > 10 }

// Better: Filter first (fewer elements to map)
val result = numbers
    .filter { it * 2 > 10 }
    .map { it * 2 }
```

---

## Challenge: Student Grade Analyzer

```kotlin
data class Student(val name: String, val grades: List<Int>)

val students = listOf(
    Student("Alice", listOf(85, 92, 88, 90)),
    Student("Bob", listOf(78, 85, 80, 82)),
    Student("Charlie", listOf(92, 95, 98, 94))
)

// 1. Calculate average for each student
val averages = students.associate {
    it.name to it.grades.average()
}

// 2. Find top student
val topStudent = students.maxByOrNull {
    it.grades.average()
}

// 3. Get all grades above 90
val excellentGrades = students
    .flatMap { it.grades }
    .filter { it >= 90 }

// 4. Students with any grade below 80
val needHelp = students.filter {
    it.grades.any { grade -> grade < 80 }
}

println("Averages: $averages")
println("Top student: ${topStudent?.name}")
println("Excellent grades: $excellentGrades")
println("Need help: ${needHelp.map { it.name }}")
```

---

## Recap

You now understand:

1. **map(), filter()** - Transform and select
2. **find(), any(), all()** - Search operations
3. **groupBy(), partition()** - Grouping
4. **flatMap(), zip()** - Combining
5. **Sequences** - Lazy evaluation for performance
6. **Practical patterns** - Real-world data processing

---

## What's Next?

Next: **Part 5 - Backend Development!** Time to build APIs!

**Key Takeaway:** Kotlin's collection operations make data processing elegant and efficient!

Continue to Part 5!

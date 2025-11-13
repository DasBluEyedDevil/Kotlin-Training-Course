# Lesson 4.1: Generics and Type Parameters

**Estimated Time**: 70 minutes
**Difficulty**: Advanced
**Prerequisites**: Parts 1-3 (Kotlin fundamentals, OOP, Functional Programming)

---

## Topic Introduction

Welcome to Part 4: Advanced Kotlin Features! You've mastered the fundamentals, object-oriented programming, and functional programming. Now it's time to explore the powerful features that make Kotlin a truly modern language.

Generics are one of the most important features in Kotlin. They allow you to write flexible, reusable code that works with different types while maintaining type safety. Without generics, you'd need to write the same code multiple times for different types or lose type safety by using `Any`.

In this lesson, you'll learn:
- Generic classes and functions
- Type parameters and constraints
- Variance: `in`, `out`, and invariant types
- Reified type parameters
- Star projections
- Generic constraints with `where`

By the end, you'll write type-safe, reusable code that works with any type!

---

## The Concept: Why Generics Matter

### The Problem Without Generics

Imagine you need to create a box that can hold different types of items:

```kotlin
// ❌ Without generics - need separate classes
class IntBox(val value: Int)
class StringBox(val value: String)
class PersonBox(val value: Person)

// ❌ Or lose type safety
class AnyBox(val value: Any)
val box = AnyBox("Hello")
val str: String = box.value as String  // Unsafe cast!
```

### The Solution: Generics

```kotlin
// ✅ With generics - one class, full type safety
class Box<T>(val value: T)

val intBox = Box(42)           // Box<Int>
val stringBox = Box("Hello")   // Box<String>
val personBox = Box(Person())  // Box<Person>

val str: String = stringBox.value  // Type-safe!
```

Generics let you write code once and use it with many types, while the compiler ensures everything is type-safe.

---

## Generic Classes

### Basic Generic Class

A generic class has type parameters in angle brackets:

```kotlin
class Container<T>(val item: T) {
    fun get(): T = item

    fun describe() {
        println("Container holds: $item")
    }
}

fun main() {
    val numberContainer = Container(42)
    println(numberContainer.get())  // 42

    val stringContainer = Container("Kotlin")
    println(stringContainer.get())  // Kotlin

    // Type inference works!
    val listContainer = Container(listOf(1, 2, 3))
    println(listContainer.get())  // [1, 2, 3]
}
```

### Multiple Type Parameters

Classes can have multiple type parameters:

```kotlin
class Pair<A, B>(val first: A, val second: B) {
    fun display() {
        println("First: $first, Second: $second")
    }

    fun swap(): Pair<B, A> = Pair(second, first)
}

fun main() {
    val pair = Pair("Alice", 25)  // Pair<String, Int>
    pair.display()  // First: Alice, Second: 25

    val swapped = pair.swap()  // Pair<Int, String>
    swapped.display()  // First: 25, Second: Alice
}
```

### Generic Collections

Kotlin's standard collections are generic:

```kotlin
fun main() {
    // List<T>
    val numbers: List<Int> = listOf(1, 2, 3)
    val words: List<String> = listOf("a", "b", "c")

    // Map<K, V>
    val ages: Map<String, Int> = mapOf(
        "Alice" to 25,
        "Bob" to 30
    )

    // Set<T>
    val uniqueNumbers: Set<Int> = setOf(1, 2, 2, 3)  // [1, 2, 3]
}
```

---

## Generic Functions

Functions can also be generic:

### Basic Generic Function

```kotlin
fun <T> printItem(item: T) {
    println("Item: $item")
}

fun <T> identity(value: T): T = value

fun main() {
    printItem(42)          // T = Int
    printItem("Hello")     // T = String
    printItem(listOf(1,2)) // T = List<Int>

    val num = identity(100)     // Int
    val str = identity("Kotlin") // String
}
```

### Generic Function with Type Inference

```kotlin
fun <T> createList(vararg items: T): List<T> {
    return items.toList()
}

fun main() {
    val numbers = createList(1, 2, 3, 4, 5)
    val words = createList("apple", "banana", "cherry")

    println(numbers)  // [1, 2, 3, 4, 5]
    println(words)    // [apple, banana, cherry]
}
```

### Generic Extension Functions

```kotlin
fun <T> T.toSingletonList(): List<T> {
    return listOf(this)
}

fun <T> List<T>.secondOrNull(): T? {
    return if (size >= 2) this[1] else null
}

fun main() {
    println(42.toSingletonList())  // [42]
    println("Hello".toSingletonList())  // [Hello]

    println(listOf(1, 2, 3).secondOrNull())  // 2
    println(listOf("a").secondOrNull())       // null
}
```

---

## Type Constraints

Type constraints restrict which types can be used with generics:

### Upper Bound Constraints

Use `:` to specify an upper bound:

```kotlin
// T must be a Number or its subtype
fun <T : Number> sum(a: T, b: T): Double {
    return a.toDouble() + b.toDouble()
}

fun main() {
    println(sum(10, 20))      // 30.0
    println(sum(5.5, 2.3))    // 7.8
    // println(sum("a", "b")) // ❌ Error: String is not a Number
}
```

### Comparable Constraint

```kotlin
fun <T : Comparable<T>> max(a: T, b: T): T {
    return if (a > b) a else b
}

fun main() {
    println(max(10, 20))           // 20
    println(max("apple", "banana")) // banana
    println(max(5.5, 2.3))         // 5.5
}
```

### Multiple Constraints with `where`

When you need multiple constraints, use `where`:

```kotlin
interface Drawable {
    fun draw()
}

class Shape(val name: String) : Drawable, Comparable<Shape> {
    override fun draw() {
        println("Drawing $name")
    }

    override fun compareTo(other: Shape): Int {
        return name.compareTo(other.name)
    }
}

fun <T> displayAndCompare(a: T, b: T) where T : Drawable, T : Comparable<T> {
    a.draw()
    b.draw()
    println("${if (a > b) "First" else "Second"} is greater")
}

fun main() {
    val circle = Shape("Circle")
    val square = Shape("Square")
    displayAndCompare(circle, square)
    // Drawing Circle
    // Drawing Square
    // Second is greater
}
```

---

## Variance: In, Out, and Invariant

Variance controls how generic types relate to each other based on their type parameters.

### The Problem: Invariance

By default, generic types are **invariant**:

```kotlin
open class Animal
class Dog : Animal()
class Cat : Animal()

class Box<T>(var item: T)

fun main() {
    val dogBox: Box<Dog> = Box(Dog())
    // val animalBox: Box<Animal> = dogBox  // ❌ Error!
    // Even though Dog is a subtype of Animal,
    // Box<Dog> is NOT a subtype of Box<Animal>
}
```

### Covariance: `out` Keyword

Use `out` when a type is only produced (output), never consumed:

```kotlin
class Producer<out T>(private val item: T) {
    fun produce(): T = item  // ✅ Only returns T
    // fun consume(item: T) {} // ❌ Can't accept T as parameter
}

fun main() {
    val dogProducer: Producer<Dog> = Producer(Dog())
    val animalProducer: Producer<Animal> = dogProducer  // ✅ Works!

    val animal: Animal = animalProducer.produce()
}
```

**Rule**: If a generic class only returns `T` (never accepts it), mark it `out T`.

### Contravariance: `in` Keyword

Use `in` when a type is only consumed (input), never produced:

```kotlin
interface Consumer<in T> {
    fun consume(item: T)     // ✅ Only accepts T
    // fun produce(): T {}   // ❌ Can't return T
}

class AnimalConsumer : Consumer<Animal> {
    override fun consume(item: Animal) {
        println("Consuming animal")
    }
}

fun main() {
    val animalConsumer: Consumer<Animal> = AnimalConsumer()
    val dogConsumer: Consumer<Dog> = animalConsumer  // ✅ Works!

    dogConsumer.consume(Dog())
}
```

**Rule**: If a generic class only accepts `T` (never returns it), mark it `in T`.

### Real-World Example: List vs MutableList

```kotlin
fun main() {
    // List<T> is covariant (out T)
    val dogs: List<Dog> = listOf(Dog(), Dog())
    val animals: List<Animal> = dogs  // ✅ Works!

    // MutableList<T> is invariant (can't be covariant or contravariant)
    val mutableDogs: MutableList<Dog> = mutableListOf(Dog())
    // val mutableAnimals: MutableList<Animal> = mutableDogs  // ❌ Error!
    // Why? Because MutableList both produces and consumes
}
```

### Variance Summary

| Variance | Keyword | Usage | Example |
|----------|---------|-------|---------|
| **Covariant** | `out T` | Type is only produced | `List<out T>`, `Producer<out T>` |
| **Contravariant** | `in T` | Type is only consumed | `Comparable<in T>`, `Consumer<in T>` |
| **Invariant** | `T` | Type is both produced and consumed | `MutableList<T>`, `Box<T>` |

---

## Use-Site Variance: Type Projections

You can specify variance at the use site instead of the declaration site:

```kotlin
class Box<T>(var item: T)

fun copyFrom(from: Box<out Animal>, to: Box<Animal>) {
    to.item = from.item  // ✅ Can read from 'from'
}

fun copyTo(from: Box<Animal>, to: Box<in Animal>) {
    to.item = from.item  // ✅ Can write to 'to'
}

fun main() {
    val dogBox = Box(Dog())
    val animalBox = Box<Animal>(Cat())

    copyFrom(dogBox, animalBox)  // ✅ Works with out projection
}
```

---

## Star Projections

Star projection `*` is used when you don't know or care about the type argument:

```kotlin
fun printList(list: List<*>) {
    for (item in list) {
        println(item)  // item is Any?
    }
}

fun main() {
    printList(listOf(1, 2, 3))
    printList(listOf("a", "b", "c"))

    // Star projection on mutable types
    val anyList: MutableList<*> = mutableListOf(1, 2, 3)
    // anyList.add(4)  // ❌ Error: can't add to MutableList<*>
    val item = anyList[0]  // ✅ Can read (as Any?)
}
```

**Rules for `List<*>`**:
- Equivalent to `List<out Any?>`
- You can read items (as `Any?`)
- For `MutableList<*>`: can't add items, can only read

---

## Reified Type Parameters

Normally, type information is erased at runtime. `reified` preserves it:

### The Problem: Type Erasure

```kotlin
fun <T> isInstance(value: Any): Boolean {
    // return value is T  // ❌ Error: Cannot check for instance of erased type
    return false
}
```

### The Solution: Reified

```kotlin
inline fun <reified T> isInstance(value: Any): Boolean {
    return value is T  // ✅ Works!
}

fun main() {
    println(isInstance<String>("Hello"))  // true
    println(isInstance<String>(42))       // false
    println(isInstance<Int>(42))          // true
}
```

### Reified with Class Checking

```kotlin
inline fun <reified T> createList(size: Int, creator: (Int) -> T): List<T> {
    return List(size) { creator(it) }
}

inline fun <reified T> printType(value: T) {
    println("Type: ${T::class.simpleName}, Value: $value")
}

fun main() {
    val numbers = createList(3) { it * 2 }
    println(numbers)  // [0, 2, 4]

    printType("Hello")  // Type: String, Value: Hello
    printType(42)       // Type: Int, Value: 42
}
```

### Reified with JSON Parsing (Practical Example)

```kotlin
import kotlin.reflect.KClass

// Simulated JSON parser
inline fun <reified T : Any> parseJson(json: String): T {
    println("Parsing JSON to ${T::class.simpleName}")
    // In real code, you'd use a JSON library
    return when (T::class) {
        String::class -> json as T
        Int::class -> json.toInt() as T
        else -> throw IllegalArgumentException("Unsupported type")
    }
}

fun main() {
    val str = parseJson<String>("\"Hello\"")
    val num = parseJson<Int>("42")

    println("String: $str")  // String: "Hello"
    println("Int: $num")     // Int: 42
}
```

**Requirements for `reified`**:
- Function must be `inline`
- Can use `is`, `as`, `::class` with type parameter
- Cannot be used in non-inline functions

---

## Generic Constraints with Where

Complex constraints often need the `where` clause:

```kotlin
interface Closeable {
    fun close()
}

interface Readable {
    fun read(): String
}

class DataFile : Closeable, Readable {
    override fun close() {
        println("Closing file")
    }

    override fun read(): String {
        return "File contents"
    }
}

fun <T> processResource(resource: T) where T : Closeable, T : Readable {
    val data = resource.read()
    println("Data: $data")
    resource.close()
}

fun main() {
    val file = DataFile()
    processResource(file)
    // Data: File contents
    // Closing file
}
```

### Multiple Constraints Example

```kotlin
fun <T> findMax(items: List<T>) where T : Comparable<T>, T : Number {
    val max = items.maxOrNull()
    max?.let {
        println("Max value: $it, Double value: ${it.toDouble()}")
    }
}

fun main() {
    findMax(listOf(1, 5, 3, 9, 2))
    // Max value: 9, Double value: 9.0

    findMax(listOf(1.5, 2.8, 0.9))
    // Max value: 2.8, Double value: 2.8
}
```

---

## Practical Examples

### Generic Repository Pattern

```kotlin
interface Entity {
    val id: Long
}

data class User(override val id: Long, val name: String) : Entity
data class Product(override val id: Long, val name: String, val price: Double) : Entity

class Repository<T : Entity> {
    private val items = mutableListOf<T>()

    fun add(item: T) {
        items.add(item)
    }

    fun findById(id: Long): T? {
        return items.find { it.id == id }
    }

    fun getAll(): List<T> {
        return items.toList()
    }

    fun remove(id: Long): Boolean {
        return items.removeIf { it.id == id }
    }
}

fun main() {
    val userRepo = Repository<User>()
    userRepo.add(User(1, "Alice"))
    userRepo.add(User(2, "Bob"))

    println(userRepo.findById(1))  // User(id=1, name=Alice)
    println(userRepo.getAll())     // [User(id=1, name=Alice), User(id=2, name=Bob)]

    val productRepo = Repository<Product>()
    productRepo.add(Product(1, "Laptop", 999.99))
    productRepo.add(Product(2, "Mouse", 29.99))

    println(productRepo.getAll())
}
```

### Generic Result Type

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()

    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> this
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
}

fun fetchUser(id: Int): Result<String> {
    return if (id > 0) {
        Result.Success("User $id")
    } else {
        Result.Error("Invalid user ID")
    }
}

fun main() {
    val result1 = fetchUser(42)
    println(result1.getOrNull())  // User 42

    val result2 = fetchUser(-1)
    println(result2.getOrNull())  // null

    val mapped = result1.map { it.uppercase() }
    println(mapped.getOrNull())  // USER 42
}
```

---

## Exercises

### Exercise 1: Generic Stack (Medium)

Create a generic `Stack<T>` class with push, pop, and peek operations.

**Requirements**:
- `push(item: T)` - add item to top
- `pop(): T?` - remove and return top item
- `peek(): T?` - return top item without removing
- `isEmpty(): Boolean` - check if stack is empty
- `size: Int` - number of items in stack

**Solution**:

```kotlin
class Stack<T> {
    private val items = mutableListOf<T>()

    fun push(item: T) {
        items.add(item)
    }

    fun pop(): T? {
        return if (items.isNotEmpty()) {
            items.removeAt(items.size - 1)
        } else {
            null
        }
    }

    fun peek(): T? {
        return items.lastOrNull()
    }

    fun isEmpty(): Boolean {
        return items.isEmpty()
    }

    val size: Int
        get() = items.size

    override fun toString(): String {
        return items.toString()
    }
}

fun main() {
    val stack = Stack<Int>()
    stack.push(1)
    stack.push(2)
    stack.push(3)

    println("Stack: $stack")        // Stack: [1, 2, 3]
    println("Size: ${stack.size}")  // Size: 3
    println("Peek: ${stack.peek()}") // Peek: 3
    println("Pop: ${stack.pop()}")   // Pop: 3
    println("Pop: ${stack.pop()}")   // Pop: 2
    println("Size: ${stack.size}")   // Size: 1

    val stringStack = Stack<String>()
    stringStack.push("Hello")
    stringStack.push("World")
    println(stringStack.pop())  // World
    println(stringStack.pop())  // Hello
    println(stringStack.pop())  // null
}
```

### Exercise 2: Generic Tree with Comparable (Hard)

Create a generic binary search tree that stores comparable items.

**Requirements**:
- `insert(value: T)` - add value to tree
- `contains(value: T): Boolean` - check if value exists
- `toSortedList(): List<T>` - return sorted list of all values

**Solution**:

```kotlin
class BinarySearchTree<T : Comparable<T>> {
    private var root: Node<T>? = null

    private class Node<T>(val value: T) {
        var left: Node<T>? = null
        var right: Node<T>? = null
    }

    fun insert(value: T) {
        root = insertRec(root, value)
    }

    private fun insertRec(node: Node<T>?, value: T): Node<T> {
        if (node == null) {
            return Node(value)
        }

        when {
            value < node.value -> node.left = insertRec(node.left, value)
            value > node.value -> node.right = insertRec(node.right, value)
        }

        return node
    }

    fun contains(value: T): Boolean {
        return containsRec(root, value)
    }

    private fun containsRec(node: Node<T>?, value: T): Boolean {
        if (node == null) return false

        return when {
            value == node.value -> true
            value < node.value -> containsRec(node.left, value)
            else -> containsRec(node.right, value)
        }
    }

    fun toSortedList(): List<T> {
        val result = mutableListOf<T>()
        inOrderTraversal(root, result)
        return result
    }

    private fun inOrderTraversal(node: Node<T>?, result: MutableList<T>) {
        if (node != null) {
            inOrderTraversal(node.left, result)
            result.add(node.value)
            inOrderTraversal(node.right, result)
        }
    }
}

fun main() {
    val tree = BinarySearchTree<Int>()
    tree.insert(5)
    tree.insert(3)
    tree.insert(7)
    tree.insert(1)
    tree.insert(9)

    println("Contains 3: ${tree.contains(3)}")  // true
    println("Contains 6: ${tree.contains(6)}")  // false
    println("Sorted: ${tree.toSortedList()}")   // [1, 3, 5, 7, 9]

    val stringTree = BinarySearchTree<String>()
    stringTree.insert("dog")
    stringTree.insert("cat")
    stringTree.insert("elephant")
    stringTree.insert("ant")

    println("Sorted: ${stringTree.toSortedList()}")
    // [ant, cat, dog, elephant]
}
```

### Exercise 3: Generic Cache with Constraints (Hard)

Create a generic cache that stores serializable items with expiration.

**Requirements**:
- Type must be serializable (toString/equals)
- `put(key: String, value: T, ttlSeconds: Int)` - store with expiration
- `get(key: String): T?` - retrieve if not expired
- `clear()` - remove all entries
- `size: Int` - number of valid entries

**Solution**:

```kotlin
import java.time.Instant

class Cache<T : Any> {
    private data class CacheEntry<T>(
        val value: T,
        val expiresAt: Long
    ) {
        fun isExpired(): Boolean {
            return System.currentTimeMillis() > expiresAt
        }
    }

    private val storage = mutableMapOf<String, CacheEntry<T>>()

    fun put(key: String, value: T, ttlSeconds: Int = 60) {
        val expiresAt = System.currentTimeMillis() + (ttlSeconds * 1000)
        storage[key] = CacheEntry(value, expiresAt)
        cleanupExpired()
    }

    fun get(key: String): T? {
        val entry = storage[key] ?: return null

        return if (entry.isExpired()) {
            storage.remove(key)
            null
        } else {
            entry.value
        }
    }

    fun clear() {
        storage.clear()
    }

    val size: Int
        get() {
            cleanupExpired()
            return storage.size
        }

    private fun cleanupExpired() {
        storage.entries.removeIf { it.value.isExpired() }
    }

    fun getAllKeys(): Set<String> {
        cleanupExpired()
        return storage.keys.toSet()
    }
}

fun main() {
    val cache = Cache<String>()

    cache.put("user1", "Alice", 2)
    cache.put("user2", "Bob", 5)

    println("Get user1: ${cache.get("user1")}")  // Alice
    println("Size: ${cache.size}")                // 2

    // Wait for expiration (in real code)
    Thread.sleep(2100)

    println("Get user1 after expiration: ${cache.get("user1")}")  // null
    println("Get user2: ${cache.get("user2")}")   // Bob
    println("Size: ${cache.size}")                // 1

    // Works with any type
    val numberCache = Cache<Int>()
    numberCache.put("count", 42, 10)
    println("Count: ${numberCache.get("count")}")  // 42

    cache.clear()
    println("Size after clear: ${cache.size}")  // 0
}
```

---

## Checkpoint Quiz

Test your understanding of generics!

### Question 1: Type Parameter Syntax

What does this function signature mean?
```kotlin
fun <T : Number> average(values: List<T>): Double
```

**A)** T can be any type
**B)** T must be Number or its subtype
**C)** T must be exactly Number
**D)** T can be Number or Any

**Answer**: **B** - The `: Number` constraint means T must be Number or any of its subtypes (Int, Double, Float, etc.)

---

### Question 2: Variance

Which statement is correct about variance?

**A)** `out` is used when a type is only consumed
**B)** `in` is used when a type is only produced
**C)** `out` makes a type covariant (producer)
**D)** Invariant types can be used as both covariant and contravariant

**Answer**: **C** - `out` makes a type covariant, meaning it can only be produced/returned, not consumed. `in` makes it contravariant (consumer).

---

### Question 3: Reified Type Parameters

What is required to use reified type parameters?

**A)** The function must be suspend
**B)** The function must be inline
**C)** The class must be open
**D)** The type must be nullable

**Answer**: **B** - Reified type parameters require the function to be `inline` so the compiler can substitute the actual type at call sites.

---

### Question 4: Star Projection

What can you do with a `MutableList<*>`?

**A)** Add and remove elements
**B)** Only add elements
**C)** Only read elements
**D)** Nothing at all

**Answer**: **C** - `MutableList<*>` can only read elements (as `Any?`). You cannot add elements because the compiler doesn't know the actual type.

---

### Question 5: Multiple Constraints

How do you specify multiple type constraints?

```kotlin
fun <T> process(item: T) where T : _____, T : _____
```

**A)** Separate with commas inside angle brackets
**B)** Use `where` clause with commas
**C)** Use multiple angle brackets
**D)** Not possible in Kotlin

**Answer**: **B** - Multiple constraints use the `where` clause: `fun <T> process(item: T) where T : Constraint1, T : Constraint2`

---

## Summary

Congratulations! You've mastered Kotlin generics. Here's what you learned:

✅ **Generic Classes and Functions** - Write reusable code for any type
✅ **Type Constraints** - Restrict types with upper bounds
✅ **Variance** - Understand `out` (covariant), `in` (contravariant), and invariant
✅ **Reified Type Parameters** - Preserve type information at runtime
✅ **Star Projections** - Work with unknown types safely
✅ **Generic Constraints** - Use `where` for multiple bounds

### Key Takeaways

1. **Generics provide type safety** without code duplication
2. **Use `out`** when you only return a type (producer)
3. **Use `in`** when you only accept a type (consumer)
4. **`reified` requires `inline`** but gives runtime type access
5. **Star projection `*`** is useful when the exact type doesn't matter

### Next Steps

In the next lesson, we'll dive into **Coroutines Fundamentals** - Kotlin's powerful approach to asynchronous programming. You'll learn how to write concurrent code that's easy to read and maintain!

---

**Practice Challenge**: Create a generic `Pool<T>` class that manages reusable objects (like database connections). Implement `acquire()` to get an object and `release(obj: T)` to return it to the pool.

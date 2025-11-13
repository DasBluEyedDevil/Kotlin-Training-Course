# Lesson 4.2: Extension Functions and Coroutines Intro

## Extension Functions - Adding Powers to Existing Classes

Ever wish you could add methods to classes you didn't create? You can!

```kotlin
fun String.isPalindrome(): Boolean {
    return this == this.reversed()
}

fun Int.isEven(): Boolean {
    return this % 2 == 0
}

fun List<Int>.average(): Double {
    return if (isEmpty()) 0.0 else sum().toDouble() / size
}

fun main() {
    println("racecar".isPalindrome())  // true
    println("hello".isPalindrome())  // false

    println(4.isEven())  // true
    println(7.isEven())  // false

    val numbers = listOf(10, 20, 30)
    println(numbers.average())  // 20.0
}
```

**You didn't modify String/Int/List - you extended them!**

---

## Scope Functions

Kotlin has special functions for working with objects:

### `let` - Execute Code and Return Result

```kotlin
val name: String? = "Alice"
name?.let {
    println("Hello, ${it.uppercase()}")
    it.length
}
```

---

### `apply` - Configure and Return Object

```kotlin
data class Person(var name: String = "", var age: Int = 0)

val person = Person().apply {
    name = "Alice"
    age = 30
}
```

---

### `run` - Execute and Return Last Expression

```kotlin
val result = "Hello".run {
    println(this)
    length
}
```

---

### `also` - Perform Side Effects

```kotlin
val numbers = mutableListOf(1, 2, 3)
    .also { println("Original: $it") }
    .apply { add(4) }
    .also { println("After add: $it") }
```

---

### `with` - Work with Object

```kotlin
val person = Person("Alice", 30)
with(person) {
    println("Name: $name")
    println("Age: $age")
}
```

---

## Introduction to Coroutines

**Coroutines** let you write asynchronous code that looks synchronous!

**Analogy:** While boiling water (long task), you can chop vegetables (other work). That's concurrency!

### Basic Example (Conceptual):

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch {
        delay(1000)
        println("Task 1 done!")
    }

    launch {
        delay(500)
        println("Task 2 done!")
    }

    println("Tasks started!")
}
```

**Output:**
```
Tasks started!
Task 2 done!
Task 1 done!
```

Tasks run concurrently!

---

## Async/Await Pattern

```kotlin
import kotlinx.coroutines.*

suspend fun fetchUser(): String {
    delay(1000)  // Simulate network call
    return "User Data"
}

suspend fun fetchPosts(): String {
    delay(1500)  // Simulate network call
    return "Posts Data"
}

fun main() = runBlocking {
    val userDeferred = async { fetchUser() }
    val postsDeferred = async { fetchPosts() }

    println("Fetching...")

    val user = userDeferred.await()
    val posts = postsDeferred.await()

    println("User: $user")
    println("Posts: $posts")
}
```

Both fetch **at the same time** - faster!

---

## Part 4 Complete! 🎉

You've learned advanced Kotlin:

✅ Lambda expressions
✅ Higher-order functions
✅ Collection operations
✅ Extension functions
✅ Scope functions
✅ Coroutines basics

**You now have professional-level Kotlin skills!**

---

## Part 4 Challenge: URL Shortener Logic

```kotlin
import kotlin.random.Random

data class ShortUrl(val original: String, val short: String, var clicks: Int = 0)

class UrlShortener {
    private val urls = mutableMapOf<String, ShortUrl>()

    fun shorten(url: String): String {
        val existing = urls.values.find { it.original == url }
        if (existing != null) return existing.short

        val shortCode = generateCode()
        urls[shortCode] = ShortUrl(url, shortCode)
        return shortCode
    }

    fun resolve(shortCode: String): String? {
        return urls[shortCode]?.also { it.clicks++ }?.original
    }

    fun stats(): List<ShortUrl> {
        return urls.values.sortedByDescending { it.clicks }
    }

    private fun generateCode(): String {
        val chars = "abcdefghijklmnopqrstuvwxyz0123456789"
        return (1..6)
            .map { chars[Random.nextInt(chars.length)] }
            .joinToString("")
    }
}

fun main() {
    val shortener = UrlShortener()

    val short1 = shortener.shorten("https://example.com/very/long/url")
    val short2 = shortener.shorten("https://example.com/another/long/url")

    println("Short URL 1: $short1")
    println("Short URL 2: $short2")

    shortener.resolve(short1)
    shortener.resolve(short1)
    shortener.resolve(short2)

    println("\n--- Stats ---")
    shortener.stats().forEach {
        println("${it.short}: ${it.clicks} clicks -> ${it.original}")
    }
}
```

---

## What's Next?

**Part 5: The Backend** - Build real APIs with Ktor!

Excellent work! Continue to Part 5!

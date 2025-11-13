# Lesson 4.2: Coroutines Fundamentals

**Estimated Time**: 75 minutes
**Difficulty**: Advanced
**Prerequisites**: Parts 1-3, Lesson 4.1 (Generics)

---

## Topic Introduction

Traditional programming is synchronous - your code waits for each operation to complete before moving to the next one. When dealing with slow operations like network requests, file I/O, or database queries, this leads to blocked threads and poor performance.

Coroutines are Kotlin's solution to asynchronous programming. They allow you to write asynchronous code that looks and behaves like synchronous code, making it much easier to understand and maintain.

In this lesson, you'll learn:
- What coroutines are and why they matter
- Suspend functions - the building blocks of coroutines
- Launching coroutines with `launch`, `async`, and `runBlocking`
- Coroutine scopes and contexts
- Job and Deferred for managing coroutines
- Basic patterns for async operations

By the end, you'll write efficient concurrent code that's as easy to read as sequential code!

---

## The Concept: Why Coroutines Matter

### The Problem: Blocking Code

```kotlin
fun fetchUserBlocking(userId: Int): String {
    Thread.sleep(1000)  // Simulates network delay
    return "User $userId"
}

fun main() {
    println("Fetching user 1...")
    val user1 = fetchUserBlocking(1)  // Blocks for 1 second
    println("Got $user1")

    println("Fetching user 2...")
    val user2 = fetchUserBlocking(2)  // Blocks for 1 second
    println("Got $user2")

    // Total time: 2+ seconds (sequential)
}
```

### Traditional Solution: Threads

```kotlin
fun main() {
    println("Fetching users...")

    thread {
        val user1 = fetchUserBlocking(1)
        println("Got $user1")
    }

    thread {
        val user2 = fetchUserBlocking(2)
        println("Got $user2")
    }

    Thread.sleep(1500)  // Wait for threads to complete
    // Problem: Threads are expensive, hard to manage
}
```

### The Coroutine Solution

```kotlin
import kotlinx.coroutines.*

suspend fun fetchUser(userId: Int): String {
    delay(1000)  // Non-blocking delay
    return "User $userId"
}

fun main() = runBlocking {
    println("Fetching users...")

    val user1 = async { fetchUser(1) }
    val user2 = async { fetchUser(2) }

    println("Got ${user1.await()}")
    println("Got ${user2.await()}")

    // Total time: ~1 second (concurrent)
}
```

**Key Differences**:
- Coroutines are lightweight (thousands can run on one thread)
- `delay()` doesn't block the thread
- Code looks sequential but runs concurrently
- Easy to manage and cancel

---

## Setting Up Coroutines

To use coroutines, add the dependency to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

Import the coroutines package:

```kotlin
import kotlinx.coroutines.*
```

---

## Suspend Functions

Suspend functions are the foundation of coroutines. They can be paused and resumed without blocking a thread.

### Basic Suspend Function

```kotlin
suspend fun doSomething() {
    delay(1000)  // Suspends for 1 second
    println("Done!")
}

// ❌ Can't call from regular function
fun regularFunction() {
    // doSomething()  // Error!
}

// ✅ Can call from another suspend function
suspend fun caller() {
    doSomething()  // Works!
}

// ✅ Can call from coroutine
fun main() = runBlocking {
    doSomething()  // Works!
}
```

### Suspend Functions Can Call Other Suspend Functions

```kotlin
suspend fun fetchData(): String {
    delay(1000)
    return "Data"
}

suspend fun processData(): String {
    val data = fetchData()  // Calls another suspend function
    delay(500)
    return "Processed: $data"
}

fun main() = runBlocking {
    val result = processData()
    println(result)  // Processed: Data
}
```

### Why Suspend?

The `suspend` keyword tells the compiler:
- This function may take time
- It can be paused and resumed
- It doesn't block the thread
- It can only be called from a coroutine or another suspend function

```kotlin
suspend fun example() {
    // Can call:
    delay(1000)           // ✅ Suspend function
    fetchData()           // ✅ Suspend function
    println("Hello")      // ✅ Regular function
    val x = 1 + 2         // ✅ Regular code

    // Thread.sleep(1000) // ⚠️ Works but blocks thread (avoid!)
}
```

---

## Coroutine Builders

Coroutine builders create and launch coroutines.

### `runBlocking` - Bridge to the Blocking World

`runBlocking` starts a coroutine and blocks the current thread until it completes:

```kotlin
fun main() = runBlocking {
    println("Start")
    delay(1000)
    println("End")
}

// Or explicitly:
fun main() {
    runBlocking {
        println("Inside coroutine")
        delay(1000)
    }
    println("After coroutine")
}
```

**When to use**: Main functions, tests. Avoid in production code (blocks thread).

### `launch` - Fire and Forget

`launch` starts a coroutine that runs in the background:

```kotlin
fun main() = runBlocking {
    println("Start")

    launch {
        delay(1000)
        println("Task 1 completed")
    }

    launch {
        delay(1500)
        println("Task 2 completed")
    }

    println("Launched tasks")
    delay(2000)  // Wait for tasks to complete
}
// Output:
// Start
// Launched tasks
// Task 1 completed (after 1s)
// Task 2 completed (after 1.5s)
```

**Returns**: `Job` - handle to manage the coroutine

```kotlin
fun main() = runBlocking {
    val job = launch {
        delay(1000)
        println("Task completed")
    }

    println("Waiting for job...")
    job.join()  // Wait for completion
    println("Job finished")
}
```

### `async` - Return a Result

`async` is like `launch` but returns a result:

```kotlin
fun main() = runBlocking {
    val deferred = async {
        delay(1000)
        42
    }

    println("Computing...")
    val result = deferred.await()  // Wait for result
    println("Result: $result")
}
```

**Returns**: `Deferred<T>` - a future result

### Concurrent Execution with `async`

```kotlin
suspend fun fetchUser(id: Int): String {
    delay(1000)
    return "User $id"
}

suspend fun fetchPosts(userId: Int): List<String> {
    delay(1000)
    return listOf("Post 1", "Post 2")
}

fun main() = runBlocking {
    val startTime = System.currentTimeMillis()

    // Sequential (slow)
    val user = fetchUser(1)
    val posts = fetchPosts(1)
    println("Sequential time: ${System.currentTimeMillis() - startTime}ms")
    // ~2000ms

    // Concurrent (fast)
    val startTime2 = System.currentTimeMillis()
    val userDeferred = async { fetchUser(1) }
    val postsDeferred = async { fetchPosts(1) }

    val user2 = userDeferred.await()
    val posts2 = postsDeferred.await()
    println("Concurrent time: ${System.currentTimeMillis() - startTime2}ms")
    // ~1000ms
}
```

---

## Coroutine Scope

Every coroutine runs inside a scope. Scopes define lifecycle and context.

### What is a Scope?

```kotlin
fun main() = runBlocking {  // This is a scope
    launch {                 // Runs in runBlocking's scope
        println("Task 1")
    }

    launch {                 // Also runs in runBlocking's scope
        println("Task 2")
    }
}
```

### Creating Custom Scopes

```kotlin
fun main() {
    val scope = CoroutineScope(Dispatchers.Default)

    scope.launch {
        delay(1000)
        println("Task 1")
    }

    scope.launch {
        delay(1500)
        println("Task 2")
    }

    Thread.sleep(2000)  // Wait for tasks
    scope.cancel()      // Cancel all coroutines in scope
}
```

### Structured Concurrency

Child coroutines are automatically cancelled when parent scope is cancelled:

```kotlin
fun main() = runBlocking {
    val parentJob = launch {
        launch {
            repeat(10) {
                delay(500)
                println("Child 1: $it")
            }
        }

        launch {
            repeat(10) {
                delay(500)
                println("Child 2: $it")
            }
        }
    }

    delay(1500)
    println("Cancelling parent")
    parentJob.cancel()  // Cancels all children too
    delay(1000)
}
// Output:
// Child 1: 0
// Child 2: 0
// Child 1: 1
// Child 2: 1
// Cancelling parent
```

---

## Coroutine Context

Every coroutine has a context that includes:
- **Job** - manages lifecycle
- **Dispatcher** - determines which thread(s) to use
- **CoroutineName** - for debugging
- **Exception handler** - handles errors

### Dispatchers

Dispatchers determine which thread pool a coroutine runs on:

```kotlin
fun main() = runBlocking {
    // Default dispatcher - CPU-intensive work
    launch(Dispatchers.Default) {
        println("Default: ${Thread.currentThread().name}")
    }

    // IO dispatcher - I/O operations (network, files)
    launch(Dispatchers.IO) {
        println("IO: ${Thread.currentThread().name}")
    }

    // Main dispatcher - UI updates (Android/Desktop)
    // launch(Dispatchers.Main) { ... }

    // Unconfined - runs on current thread
    launch(Dispatchers.Unconfined) {
        println("Unconfined: ${Thread.currentThread().name}")
    }

    delay(100)
}
```

**Common Dispatchers**:
- `Dispatchers.Default` - CPU-intensive tasks (sorting, calculations)
- `Dispatchers.IO` - I/O operations (network, database, files)
- `Dispatchers.Main` - UI updates (Android, JavaFX)
- `Dispatchers.Unconfined` - not confined to specific thread

### Switching Contexts with `withContext`

```kotlin
suspend fun fetchAndProcess() = withContext(Dispatchers.IO) {
    // Fetch data on IO dispatcher
    val data = fetchDataFromNetwork()

    withContext(Dispatchers.Default) {
        // Process on Default dispatcher
        processData(data)
    }
}

suspend fun fetchDataFromNetwork(): String {
    delay(1000)
    return "Network data"
}

suspend fun processData(data: String): String {
    delay(500)
    return "Processed: $data"
}

fun main() = runBlocking {
    val result = fetchAndProcess()
    println(result)
}
```

---

## Job - Managing Coroutine Lifecycle

A `Job` represents a coroutine and allows you to manage its lifecycle.

### Job Basics

```kotlin
fun main() = runBlocking {
    val job = launch {
        repeat(5) {
            delay(500)
            println("Working... $it")
        }
    }

    delay(1200)
    println("Cancelling job")
    job.cancel()      // Cancel the job
    job.join()        // Wait for cancellation to complete

    println("Job cancelled: ${job.isCancelled}")
    println("Job completed: ${job.isCompleted}")
}
```

### Job States

```kotlin
fun main() = runBlocking {
    val job = launch {
        delay(1000)
    }

    println("Active: ${job.isActive}")      // true
    println("Completed: ${job.isCompleted}")  // false

    job.join()

    println("Active: ${job.isActive}")      // false
    println("Completed: ${job.isCompleted}")  // true
}
```

### Cancellation is Cooperative

Coroutines must cooperate to be cancellable:

```kotlin
fun main() = runBlocking {
    val job = launch {
        repeat(10) { i ->
            println("Job: $i")
            delay(500)  // Suspension point - checks cancellation
        }
    }

    delay(1200)
    job.cancel()
}

// Non-cooperative (bad):
fun main() = runBlocking {
    val job = launch {
        var i = 0
        while (i < 10) {
            println("Job: $i")
            Thread.sleep(500)  // ❌ Doesn't check cancellation
            i++
        }
    }

    delay(1200)
    job.cancel()  // Won't stop the coroutine!
    job.join()
}
```

### Making Code Cancellable

```kotlin
import kotlinx.coroutines.isActive

fun main() = runBlocking {
    val job = launch {
        var i = 0
        while (isActive) {  // ✅ Check if still active
            println("Job: $i")
            Thread.sleep(500)
            i++
        }
        println("Cleaning up...")
    }

    delay(1200)
    job.cancel()
    job.join()
}
```

---

## Deferred - Async Results

`Deferred<T>` is a `Job` that returns a result.

### Basic Usage

```kotlin
fun main() = runBlocking {
    val deferred: Deferred<Int> = async {
        delay(1000)
        42
    }

    println("Computing...")
    val result = deferred.await()  // Suspends until result is ready
    println("Result: $result")
}
```

### Multiple Async Operations

```kotlin
suspend fun fetchUserName(id: Int): String {
    delay(1000)
    return "User$id"
}

suspend fun fetchUserAge(id: Int): Int {
    delay(1000)
    return 20 + id
}

fun main() = runBlocking {
    val name = async { fetchUserName(1) }
    val age = async { fetchUserAge(1) }

    println("User: ${name.await()}, Age: ${age.await()}")
    // Total time: ~1 second (concurrent)
}
```

### Error Handling with Deferred

```kotlin
fun main() = runBlocking {
    val deferred = async {
        delay(500)
        throw RuntimeException("Error!")
    }

    try {
        deferred.await()  // Exception thrown here
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
}
```

---

## Common Patterns

### Pattern 1: Parallel Decomposition

Execute multiple independent tasks concurrently:

```kotlin
suspend fun task1(): String {
    delay(1000)
    return "Result 1"
}

suspend fun task2(): String {
    delay(1000)
    return "Result 2"
}

suspend fun task3(): String {
    delay(1000)
    return "Result 3"
}

fun main() = runBlocking {
    val startTime = System.currentTimeMillis()

    val results = listOf(
        async { task1() },
        async { task2() },
        async { task3() }
    ).map { it.await() }

    println("Results: $results")
    println("Time: ${System.currentTimeMillis() - startTime}ms")
    // ~1000ms instead of 3000ms
}
```

### Pattern 2: Sequential with Suspending

```kotlin
suspend fun step1(): String {
    delay(1000)
    return "Step 1"
}

suspend fun step2(input: String): String {
    delay(1000)
    return "$input -> Step 2"
}

suspend fun step3(input: String): String {
    delay(1000)
    return "$input -> Step 3"
}

fun main() = runBlocking {
    val result1 = step1()
    val result2 = step2(result1)
    val result3 = step3(result2)

    println(result3)
    // Step 1 -> Step 2 -> Step 3
}
```

### Pattern 3: Timeout

```kotlin
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

fun main() = runBlocking {
    try {
        withTimeout(1500) {
            repeat(5) {
                delay(500)
                println("Working $it")
            }
        }
    } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
        println("Timeout!")
    }

    // Or with null on timeout
    val result = withTimeoutOrNull(1500) {
        delay(1000)
        "Success"
    }
    println(result)  // Success

    val result2 = withTimeoutOrNull(500) {
        delay(1000)
        "Success"
    }
    println(result2)  // null
}
```

### Pattern 4: Lazy Async

```kotlin
fun main() = runBlocking {
    val deferred = async(start = CoroutineStart.LAZY) {
        println("Computing...")
        delay(1000)
        42
    }

    println("Created async")
    delay(2000)
    println("Starting computation")
    val result = deferred.await()  // Starts computation here
    println("Result: $result")
}
```

---

## Exercises

### Exercise 1: Concurrent API Calls (Medium)

Simulate fetching data from multiple APIs concurrently.

**Requirements**:
- Create 3 suspend functions that simulate API calls (1-2 second delays)
- Fetch all data concurrently
- Print total time taken
- Handle potential errors

**Solution**:

```kotlin
import kotlinx.coroutines.*

suspend fun fetchWeather(): String {
    delay(1500)
    return "Weather: Sunny, 72°F"
}

suspend fun fetchNews(): String {
    delay(1000)
    return "News: Kotlin 2.0 released!"
}

suspend fun fetchStocks(): String {
    delay(2000)
    return "Stocks: GOOGL +2.5%"
}

fun main() = runBlocking {
    val startTime = System.currentTimeMillis()

    try {
        // Launch all requests concurrently
        val weather = async { fetchWeather() }
        val news = async { fetchNews() }
        val stocks = async { fetchStocks() }

        // Wait for all results
        println(weather.await())
        println(news.await())
        println(stocks.await())

        val totalTime = System.currentTimeMillis() - startTime
        println("\nTotal time: ${totalTime}ms")
        // ~2000ms (concurrent) vs ~4500ms (sequential)

    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
```

### Exercise 2: Progress Reporter (Medium)

Create a progress reporter that runs while a long task executes.

**Requirements**:
- Long-running task (5 seconds)
- Progress reporter updates every 500ms
- Stop progress when task completes
- Show final result

**Solution**:

```kotlin
import kotlinx.coroutines.*

suspend fun longRunningTask(): String {
    delay(5000)
    return "Task completed!"
}

fun main() = runBlocking {
    val progressJob = launch {
        var progress = 0
        while (isActive) {
            println("Progress: ${progress * 10}%")
            progress++
            delay(500)
        }
    }

    val result = longRunningTask()

    progressJob.cancel()  // Stop progress updates
    println("\n$result")
}

// Output:
// Progress: 0%
// Progress: 10%
// Progress: 20%
// ...
// Progress: 90%
// Task completed!
```

### Exercise 3: Retry Logic (Hard)

Implement retry logic for a failing operation.

**Requirements**:
- Suspend function that may fail
- Retry up to 3 times with exponential backoff
- Return result on success or throw after max retries
- Log each attempt

**Solution**:

```kotlin
import kotlinx.coroutines.*
import kotlin.random.Random

class RetryException(message: String) : Exception(message)

suspend fun unreliableOperation(): String {
    delay(500)

    // 70% chance of failure
    if (Random.nextInt(100) < 70) {
        throw RetryException("Operation failed")
    }

    return "Success!"
}

suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelay: Long = 100,
    maxDelay: Long = 2000,
    factor: Double = 2.0,
    operation: suspend () -> T
): T {
    var currentDelay = initialDelay

    repeat(maxRetries) { attempt ->
        try {
            println("Attempt ${attempt + 1}...")
            return operation()
        } catch (e: Exception) {
            println("Failed: ${e.message}")

            if (attempt == maxRetries - 1) {
                throw e
            }

            println("Retrying in ${currentDelay}ms...")
            delay(currentDelay)

            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
    }

    throw RetryException("Max retries exceeded")
}

fun main() = runBlocking {
    try {
        val result = retryWithBackoff {
            unreliableOperation()
        }
        println("\n$result")
    } catch (e: Exception) {
        println("\nGave up after max retries: ${e.message}")
    }
}

// Possible output:
// Attempt 1...
// Failed: Operation failed
// Retrying in 100ms...
// Attempt 2...
// Failed: Operation failed
// Retrying in 200ms...
// Attempt 3...
// Success!
```

---

## Checkpoint Quiz

### Question 1: Suspend Functions

What is true about suspend functions?

**A)** They always run on a background thread
**B)** They can only be called from coroutines or other suspend functions
**C)** They block the calling thread
**D)** They must always use delay()

**Answer**: **B** - Suspend functions can only be called from coroutines or other suspend functions. They don't necessarily run on background threads and don't block threads.

---

### Question 2: Coroutine Builders

What's the difference between `launch` and `async`?

**A)** `launch` returns a result, `async` doesn't
**B)** `launch` is for sequential code, `async` for concurrent
**C)** `launch` returns Job (no result), `async` returns Deferred (with result)
**D)** They are identical

**Answer**: **C** - `launch` returns a `Job` for fire-and-forget tasks, while `async` returns a `Deferred<T>` that can provide a result via `await()`.

---

### Question 3: Dispatchers

Which dispatcher should you use for network requests?

**A)** Dispatchers.Default
**B)** Dispatchers.Main
**C)** Dispatchers.IO
**D)** Dispatchers.Unconfined

**Answer**: **C** - `Dispatchers.IO` is optimized for I/O operations like network requests, file operations, and database queries.

---

### Question 4: Cancellation

Why doesn't this coroutine cancel properly?

```kotlin
val job = launch {
    while (true) {
        Thread.sleep(500)
        println("Working")
    }
}
job.cancel()
```

**A)** Missing job.join()
**B)** Thread.sleep doesn't check for cancellation
**C)** while(true) prevents cancellation
**D)** launch doesn't support cancellation

**Answer**: **B** - `Thread.sleep()` doesn't check for cancellation. Use `delay()` or check `isActive` in the loop.

---

### Question 5: Structured Concurrency

What happens when a parent coroutine is cancelled?

**A)** Child coroutines continue running
**B)** Only the parent is cancelled
**C)** All child coroutines are automatically cancelled
**D)** An exception is thrown

**Answer**: **C** - Structured concurrency ensures that when a parent coroutine is cancelled, all its children are automatically cancelled too.

---

## Summary

Congratulations! You've learned the fundamentals of Kotlin coroutines. Here's what you covered:

✅ **Suspend Functions** - Building blocks of coroutines
✅ **Coroutine Builders** - `launch`, `async`, `runBlocking`
✅ **Coroutine Scope** - Lifecycle and structured concurrency
✅ **Coroutine Context** - Jobs, dispatchers, and configuration
✅ **Job & Deferred** - Managing coroutines and results
✅ **Common Patterns** - Parallel execution, timeouts, retries

### Key Takeaways

1. **Suspend functions** don't block threads - they suspend and resume
2. **Use `launch`** for fire-and-forget tasks
3. **Use `async`** when you need a result
4. **`Dispatchers.IO`** for I/O, `Dispatchers.Default` for CPU work
5. **Cancellation is cooperative** - use `delay()` or check `isActive`
6. **Structured concurrency** automatically manages child coroutines

### Next Steps

In the next lesson, we'll dive into **Advanced Coroutines** - exploring Flows for reactive streams, channels for communication, exception handling, and advanced patterns!

---

**Practice Challenge**: Build a download manager that downloads multiple files concurrently, shows progress for each file, and allows cancelling individual downloads or all downloads at once.

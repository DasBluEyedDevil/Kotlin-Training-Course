# Lesson 4.3: Advanced Coroutines

**Estimated Time**: 75 minutes
**Difficulty**: Advanced
**Prerequisites**: Lesson 4.2 (Coroutines Fundamentals)

---

## Topic Introduction

Now that you understand coroutine basics, it's time to explore the advanced features that make coroutines truly powerful. These features enable you to build reactive systems, handle streams of data, communicate between coroutines, and gracefully handle errors in concurrent code.

In this lesson, you'll learn:
- Structured concurrency patterns
- Exception handling in coroutines
- Flows for reactive streams
- Channels for coroutine communication
- StateFlow and SharedFlow for state management
- `withContext` for context switching
- Advanced dispatchers and supervisors

By the end, you'll build production-ready concurrent applications!

---

## Structured Concurrency

Structured concurrency ensures coroutines have a clear lifecycle and don't leak.

### The Principle

Coroutines should:
1. Have a clear parent-child relationship
2. Be automatically cancelled when parent is cancelled
3. Complete or fail together as a unit

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    val job = launch {
        val child1 = launch {
            try {
                delay(Long.MAX_VALUE)
            } catch (e: CancellationException) {
                println("Child 1 cancelled")
            }
        }

        val child2 = launch {
            try {
                delay(Long.MAX_VALUE)
            } catch (e: CancellationException) {
                println("Child 2 cancelled")
            }
        }

        delay(500)
    }

    delay(200)
    job.cancel()
    delay(1000)
}
// Output:
// Child 1 cancelled
// Child 2 cancelled
```

### `coroutineScope` - Structured Concurrency Builder

`coroutineScope` creates a scope that completes only when all children complete:

```kotlin
suspend fun fetchAllData() = coroutineScope {
    val user = async { fetchUser() }
    val posts = async { fetchPosts() }
    val comments = async { fetchComments() }

    UserData(user.await(), posts.await(), comments.await())
}

data class UserData(val user: String, val posts: String, val comments: String)

suspend fun fetchUser() = delay(1000).let { "User" }
suspend fun fetchPosts() = delay(800).let { "Posts" }
suspend fun fetchComments() = delay(1200).let { "Comments" }

fun main() = runBlocking {
    val data = fetchAllData()
    println(data)
    // UserData(user=User, posts=Posts, comments=Comments)
}
```

If any child fails, all siblings are cancelled:

```kotlin
suspend fun fetchWithFailure() = coroutineScope {
    launch {
        delay(500)
        println("Task 1")
    }

    launch {
        delay(300)
        throw RuntimeException("Task 2 failed!")
    }

    launch {
        delay(700)
        println("Task 3")  // Never executes
    }
}

fun main() = runBlocking {
    try {
        fetchWithFailure()
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
}
// Output:
// Caught: Task 2 failed!
```

### `supervisorScope` - Independent Children

`supervisorScope` allows children to fail independently:

```kotlin
suspend fun fetchWithSupervision() = supervisorScope {
    launch {
        delay(500)
        println("Task 1 completed")
    }

    launch {
        delay(300)
        throw RuntimeException("Task 2 failed!")
    }

    launch {
        delay(700)
        println("Task 3 completed")  // Still executes
    }
}

fun main() = runBlocking {
    try {
        fetchWithSupervision()
        delay(1000)
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
}
// Output:
// Task 1 completed
// Task 3 completed
```

---

## Exception Handling in Coroutines

Exception handling in coroutines has special rules.

### Try-Catch in Coroutines

```kotlin
fun main() = runBlocking {
    val job = launch {
        try {
            delay(500)
            throw RuntimeException("Error!")
        } catch (e: Exception) {
            println("Caught in coroutine: ${e.message}")
        }
    }

    job.join()
}
```

### Try-Catch Outside Launch (Doesn't Work!)

```kotlin
fun main() = runBlocking {
    try {
        launch {  // Fire and forget!
            delay(500)
            throw RuntimeException("Error!")
        }
    } catch (e: Exception) {
        println("Never caught here!")  // Not reached
    }

    delay(1000)
}
// Crashes the program!
```

### Exception Handling with Async

```kotlin
fun main() = runBlocking {
    val deferred = async {
        delay(500)
        throw RuntimeException("Error in async!")
    }

    try {
        deferred.await()  // Exception thrown here
    } catch (e: Exception) {
        println("Caught: ${e.message}")
    }
}
```

### CoroutineExceptionHandler

Global exception handler for coroutines:

```kotlin
val handler = CoroutineExceptionHandler { _, exception ->
    println("Caught: ${exception.message}")
}

fun main() = runBlocking {
    val scope = CoroutineScope(Dispatchers.Default + handler)

    scope.launch {
        delay(500)
        throw RuntimeException("Error!")
    }

    delay(1000)
}
// Output: Caught: Error!
```

### SupervisorJob for Independent Failures

```kotlin
fun main() = runBlocking {
    val supervisor = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.Default + supervisor)

    val job1 = scope.launch {
        delay(500)
        println("Job 1 completed")
    }

    val job2 = scope.launch {
        delay(300)
        throw RuntimeException("Job 2 failed!")
    }

    val job3 = scope.launch {
        delay(700)
        println("Job 3 completed")
    }

    joinAll(job1, job2, job3)
    supervisor.cancel()
}
// Output:
// Job 1 completed
// Job 3 completed
```

---

## Flows - Reactive Streams

Flows represent asynchronous streams of values.

### Basic Flow

```kotlin
import kotlinx.coroutines.flow.*

fun simpleFlow(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(500)
        emit(i)  // Emit value
    }
}

fun main() = runBlocking {
    simpleFlow().collect { value ->
        println("Received: $value")
    }
}
// Output (0.5s delays):
// Received: 1
// Received: 2
// Received: 3
```

### Flow Builders

```kotlin
// flowOf - from values
val flowOfValues: Flow<Int> = flowOf(1, 2, 3, 4, 5)

// asFlow - from collections
val listFlow: Flow<Int> = listOf(1, 2, 3).asFlow()

// flow { } - custom
val customFlow: Flow<Int> = flow {
    emit(1)
    delay(100)
    emit(2)
}

fun main() = runBlocking {
    flowOfValues.collect { println(it) }
}
```

### Flow Operators

```kotlin
fun main() = runBlocking {
    // map
    (1..5).asFlow()
        .map { it * it }
        .collect { println(it) }  // 1, 4, 9, 16, 25

    // filter
    (1..10).asFlow()
        .filter { it % 2 == 0 }
        .collect { println(it) }  // 2, 4, 6, 8, 10

    // transform
    (1..3).asFlow()
        .transform { value ->
            emit("Start $value")
            delay(100)
            emit("End $value")
        }
        .collect { println(it) }

    // take
    (1..100).asFlow()
        .take(3)
        .collect { println(it) }  // 1, 2, 3
}
```

### Flow Context

Flows preserve the context of the collector:

```kotlin
fun simpleFlow(): Flow<Int> = flow {
    println("Flow started on ${Thread.currentThread().name}")
    for (i in 1..3) {
        delay(100)
        emit(i)
    }
}

fun main() = runBlocking {
    simpleFlow()
        .collect { value ->
            println("Collected $value on ${Thread.currentThread().name}")
        }
}
```

### `flowOn` - Change Flow Context

```kotlin
fun main() = runBlocking {
    (1..5).asFlow()
        .map { value ->
            println("Map on ${Thread.currentThread().name}")
            value * value
        }
        .flowOn(Dispatchers.Default)  // Upstream operators run on Default
        .collect { value ->
            println("Collect $value on ${Thread.currentThread().name}")
        }
}
```

### Buffer and Conflate

```kotlin
fun main() = runBlocking {
    // Without buffer (slow)
    val time1 = measureTimeMillis {
        (1..3).asFlow()
            .onEach { delay(100) }  // Emission delay
            .collect { value ->
                delay(300)  // Processing delay
                println(value)
            }
    }
    println("Time: $time1 ms")  // ~1200ms

    // With buffer (faster)
    val time2 = measureTimeMillis {
        (1..3).asFlow()
            .onEach { delay(100) }
            .buffer()  // Buffer emissions
            .collect { value ->
                delay(300)
                println(value)
            }
    }
    println("Time: $time2 ms")  // ~1000ms

    // Conflate - keep only latest
    (1..10).asFlow()
        .onEach { delay(100) }
        .conflate()  // Skip intermediate values
        .collect { value ->
            println("Processing $value")
            delay(300)
        }
}
```

### Combining Flows

```kotlin
fun main() = runBlocking {
    val nums = (1..3).asFlow()
    val strs = flowOf("one", "two", "three")

    // zip - combine corresponding values
    nums.zip(strs) { a, b -> "$a -> $b" }
        .collect { println(it) }
    // 1 -> one
    // 2 -> two
    // 3 -> three

    // combine - combine latest values
    nums.combine(strs) { a, b -> "$a and $b" }
        .collect { println(it) }
}
```

### Flow Completion

```kotlin
fun main() = runBlocking {
    (1..3).asFlow()
        .onEach { println("Emitting $it") }
        .onCompletion { println("Flow completed") }
        .collect { println("Collected $it") }

    // With exception handling
    flow {
        emit(1)
        throw RuntimeException("Error!")
    }
        .onCompletion { cause ->
            if (cause != null) {
                println("Completed with error: ${cause.message}")
            }
        }
        .catch { println("Caught: ${it.message}") }
        .collect()
}
```

---

## Channels - Communication Between Coroutines

Channels are hot streams for sending data between coroutines.

### Basic Channel

```kotlin
import kotlinx.coroutines.channels.*

fun main() = runBlocking {
    val channel = Channel<Int>()

    launch {
        for (x in 1..5) {
            channel.send(x)  // Send
            println("Sent $x")
        }
        channel.close()  // Close channel
    }

    for (y in channel) {  // Receive
        println("Received $y")
    }
}
```

### Producer-Consumer Pattern

```kotlin
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) {
        send(x++)
        delay(100)
    }
}

fun CoroutineScope.square(numbers: ReceiveChannel<Int>) = produce<Int> {
    for (x in numbers) {
        send(x * x)
    }
}

fun main() = runBlocking {
    val numbers = produceNumbers()
    val squares = square(numbers)

    repeat(5) {
        println(squares.receive())
    }

    coroutineContext.cancelChildren()
}
```

### Channel Buffering

```kotlin
fun main() = runBlocking {
    // Unbuffered (rendezvous)
    val unbuffered = Channel<Int>()

    // Buffered
    val buffered = Channel<Int>(capacity = 3)

    // Unlimited buffer
    val unlimited = Channel<Int>(Channel.UNLIMITED)

    // Conflated - keeps only latest
    val conflated = Channel<Int>(Channel.CONFLATED)

    launch {
        repeat(5) {
            buffered.send(it)
            println("Sent $it")
        }
    }

    delay(500)
    repeat(5) {
        println("Received ${buffered.receive()}")
    }
}
```

### Fan-out and Fan-in

```kotlin
// Fan-out - multiple consumers
fun CoroutineScope.produceNumbers() = produce<Int> {
    var x = 1
    while (true) {
        send(x++)
        delay(100)
    }
}

fun CoroutineScope.consumeNumbers(id: Int, channel: ReceiveChannel<Int>) = launch {
    for (msg in channel) {
        println("Consumer $id received $msg")
    }
}

fun main() = runBlocking {
    val producer = produceNumbers()

    repeat(3) {
        consumeNumbers(it + 1, producer)
    }

    delay(1000)
    producer.cancel()
}

// Fan-in - multiple producers
suspend fun sendString(channel: SendChannel<String>, s: String, time: Long) {
    while (true) {
        delay(time)
        channel.send(s)
    }
}

fun main2() = runBlocking {
    val channel = Channel<String>()

    launch { sendString(channel, "foo", 200) }
    launch { sendString(channel, "bar", 500) }

    repeat(10) {
        println(channel.receive())
    }

    coroutineContext.cancelChildren()
}
```

---

## StateFlow and SharedFlow

Hot flows that maintain state or broadcast values.

### StateFlow - State Holder

```kotlin
import kotlinx.coroutines.flow.*

class Counter {
    private val _count = MutableStateFlow(0)
    val count: StateFlow<Int> = _count

    fun increment() {
        _count.value++
    }

    fun decrement() {
        _count.value--
    }
}

fun main() = runBlocking {
    val counter = Counter()

    launch {
        counter.count.collect { value ->
            println("Counter: $value")
        }
    }

    delay(100)
    counter.increment()  // Counter: 1
    delay(100)
    counter.increment()  // Counter: 2
    delay(100)
    counter.decrement()  // Counter: 1

    delay(500)
}
```

### StateFlow Features

```kotlin
fun main() = runBlocking {
    val stateFlow = MutableStateFlow("Initial")

    // Always has a value
    println("Current: ${stateFlow.value}")

    launch {
        stateFlow.collect {
            println("Collected: $it")
        }
    }

    delay(100)
    stateFlow.value = "Updated"
    stateFlow.value = "Updated"  // Duplicate - not emitted
    stateFlow.value = "Final"

    delay(500)
}
// Output:
// Current: Initial
// Collected: Initial
// Collected: Updated
// Collected: Final
```

### SharedFlow - Event Broadcaster

```kotlin
class EventBus {
    private val _events = MutableSharedFlow<String>()
    val events: SharedFlow<String> = _events

    suspend fun publish(event: String) {
        _events.emit(event)
    }
}

fun main() = runBlocking {
    val eventBus = EventBus()

    // Multiple collectors
    launch {
        eventBus.events.collect {
            println("Collector 1: $it")
        }
    }

    launch {
        eventBus.events.collect {
            println("Collector 2: $it")
        }
    }

    delay(100)
    eventBus.publish("Event 1")
    delay(100)
    eventBus.publish("Event 2")

    delay(500)
}
// Both collectors receive all events
```

### SharedFlow with Replay

```kotlin
fun main() = runBlocking {
    val sharedFlow = MutableSharedFlow<Int>(replay = 2)

    sharedFlow.emit(1)
    sharedFlow.emit(2)
    sharedFlow.emit(3)

    // New collector gets last 2 values
    sharedFlow.collect {
        println("Received: $it")
    }
}
// Output:
// Received: 2
// Received: 3
```

---

## Advanced Context Switching

### `withContext` - Temporary Context Switch

```kotlin
suspend fun fetchData(): String = withContext(Dispatchers.IO) {
    // Runs on IO dispatcher
    delay(1000)
    "Data from IO"
}

suspend fun processData(data: String): String = withContext(Dispatchers.Default) {
    // Runs on Default dispatcher
    delay(500)
    data.uppercase()
}

fun main() = runBlocking {
    val data = fetchData()
    val result = processData(data)
    println(result)  // DATA FROM IO
}
```

### Context Elements

```kotlin
fun main() = runBlocking {
    val context = CoroutineName("MyCoroutine") + Dispatchers.Default

    launch(context) {
        println("Running in: ${coroutineContext[CoroutineName]?.name}")
        println("On thread: ${Thread.currentThread().name}")
    }

    delay(100)
}
```

---

## Exercises

### Exercise 1: Temperature Monitor with Flow (Medium)

Create a temperature monitoring system using Flow.

**Requirements**:
- Generate random temperatures every second
- Filter temperatures above 30°C
- Calculate running average
- Emit alerts for high temperatures

**Solution**:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.random.Random

fun temperatureFlow(): Flow<Double> = flow {
    while (true) {
        val temp = Random.nextDouble(15.0, 40.0)
        emit(temp)
        delay(1000)
    }
}

fun main() = runBlocking {
    var count = 0
    var sum = 0.0

    temperatureFlow()
        .onEach { temp ->
            println("Temperature: %.1f°C".format(temp))
        }
        .map { temp ->
            count++
            sum += temp
            Pair(temp, sum / count)
        }
        .filter { (temp, _) -> temp > 30.0 }
        .take(5)  // Stop after 5 high temps
        .collect { (temp, avg) ->
            println("⚠️ HIGH TEMP: %.1f°C (Avg: %.1f°C)".format(temp, avg))
        }
}
```

### Exercise 2: Download Manager with Channels (Hard)

Build a concurrent download manager using channels.

**Requirements**:
- Multiple download workers
- Task queue with channel
- Progress reporting
- Completion notification

**Solution**:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

data class DownloadTask(val id: Int, val url: String, val size: Int)
data class DownloadResult(val id: Int, val url: String, val success: Boolean)

suspend fun downloadFile(task: DownloadTask): DownloadResult {
    println("Downloading ${task.url}...")
    delay((task.size * 10).toLong())  // Simulate download
    return DownloadResult(task.id, task.url, true)
}

fun CoroutineScope.downloadWorker(
    id: Int,
    tasks: ReceiveChannel<DownloadTask>,
    results: SendChannel<DownloadResult>
) = launch {
    for (task in tasks) {
        println("Worker $id processing task ${task.id}")
        val result = downloadFile(task)
        results.send(result)
    }
    println("Worker $id finished")
}

fun main() = runBlocking {
    val tasks = Channel<DownloadTask>()
    val results = Channel<DownloadResult>()

    // Start 3 workers
    repeat(3) { workerId ->
        downloadWorker(workerId + 1, tasks, results)
    }

    // Send tasks
    launch {
        val downloads = listOf(
            DownloadTask(1, "file1.zip", 100),
            DownloadTask(2, "file2.zip", 50),
            DownloadTask(3, "file3.zip", 75),
            DownloadTask(4, "file4.zip", 120),
            DownloadTask(5, "file5.zip", 60)
        )

        downloads.forEach { tasks.send(it) }
        tasks.close()
    }

    // Collect results
    var completed = 0
    for (result in results) {
        completed++
        println("✅ Completed: ${result.url} (${completed}/5)")

        if (completed == 5) {
            results.close()
            break
        }
    }

    println("\nAll downloads completed!")
}
```

### Exercise 3: Real-Time Chat with StateFlow (Hard)

Create a simple chat system with StateFlow for state management.

**Requirements**:
- User state (online/offline)
- Message history
- Real-time updates
- Multiple observers

**Solution**:

```kotlin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class Message(val user: String, val text: String, val timestamp: Long)
data class ChatState(
    val users: Set<String>,
    val messages: List<Message>
)

class ChatRoom {
    private val _state = MutableStateFlow(ChatState(emptySet(), emptyList()))
    val state: StateFlow<ChatState> = _state

    fun userJoin(username: String) {
        _state.value = _state.value.copy(
            users = _state.value.users + username,
            messages = _state.value.messages + Message(
                "System",
                "$username joined",
                System.currentTimeMillis()
            )
        )
    }

    fun userLeave(username: String) {
        _state.value = _state.value.copy(
            users = _state.value.users - username,
            messages = _state.value.messages + Message(
                "System",
                "$username left",
                System.currentTimeMillis()
            )
        )
    }

    fun sendMessage(username: String, text: String) {
        _state.value = _state.value.copy(
            messages = _state.value.messages + Message(
                username,
                text,
                System.currentTimeMillis()
            )
        )
    }
}

fun main() = runBlocking {
    val chatRoom = ChatRoom()

    // Observer 1
    launch {
        chatRoom.state
            .map { it.users.size }
            .distinctUntilChanged()
            .collect { count ->
                println("👥 Users online: $count")
            }
    }

    // Observer 2
    launch {
        chatRoom.state
            .map { it.messages.lastOrNull() }
            .filterNotNull()
            .collect { msg ->
                println("💬 [${msg.user}]: ${msg.text}")
            }
    }

    delay(100)

    chatRoom.userJoin("Alice")
    delay(100)
    chatRoom.userJoin("Bob")
    delay(100)
    chatRoom.sendMessage("Alice", "Hello, Bob!")
    delay(100)
    chatRoom.sendMessage("Bob", "Hi, Alice!")
    delay(100)
    chatRoom.userLeave("Alice")

    delay(500)
}
```

---

## Checkpoint Quiz

### Question 1: Structured Concurrency

What happens in `coroutineScope` if one child fails?

**A)** Only that child is cancelled
**B)** All children are cancelled and exception is propagated
**C)** The exception is ignored
**D)** Other children continue running

**Answer**: **B** - In `coroutineScope`, if one child fails, all siblings are cancelled and the exception is propagated to the parent.

---

### Question 2: Flow vs Channel

What's the main difference between Flow and Channel?

**A)** Flow is hot, Channel is cold
**B)** Flow is cold (lazy), Channel is hot (active)
**C)** They are the same
**D)** Channel can't be cancelled

**Answer**: **B** - Flow is cold (starts on collection), while Channel is hot (actively sends/receives regardless of consumers).

---

### Question 3: StateFlow

What makes StateFlow special?

**A)** It's the fastest flow type
**B)** It always has a current value and conflates duplicates
**C)** It can only emit once
**D)** It doesn't support multiple collectors

**Answer**: **B** - StateFlow always has a current value (accessible via `.value`) and automatically conflates duplicate consecutive values.

---

### Question 4: Exception Handling

Why doesn't this catch the exception?

```kotlin
try {
    launch {
        throw Exception("Error")
    }
} catch (e: Exception) {
    println("Caught")
}
```

**A)** launch is not a suspend function
**B)** launch is fire-and-forget, exception happens async
**C)** Exception handling doesn't work in coroutines
**D)** Missing await()

**Answer**: **B** - `launch` returns immediately (fire-and-forget), so the exception happens asynchronously after the try-catch block.

---

### Question 5: flowOn

What does `flowOn` do?

**A)** Changes the dispatcher for downstream operators
**B)** Changes the dispatcher for upstream operators
**C)** Stops the flow
**D)** Buffers the flow

**Answer**: **B** - `flowOn` changes the dispatcher for upstream operators (everything before it in the chain).

---

## Summary

Congratulations! You've mastered advanced coroutines. Here's what you learned:

✅ **Structured Concurrency** - `coroutineScope` and `supervisorScope`
✅ **Exception Handling** - Try-catch patterns and exception handlers
✅ **Flows** - Reactive streams with operators and transformations
✅ **Channels** - Communication between coroutines
✅ **StateFlow/SharedFlow** - State management and event broadcasting
✅ **Context Switching** - `withContext` for dispatcher changes

### Key Takeaways

1. **Use `coroutineScope`** for related tasks that should fail together
2. **Use `supervisorScope`** for independent tasks
3. **Flows are cold** (start on collection), **Channels are hot**
4. **StateFlow** for state, **SharedFlow** for events
5. **Exception handling** in `launch` requires `CoroutineExceptionHandler`
6. **`flowOn`** changes dispatcher for upstream operators

### Next Steps

In the next lesson, we'll explore **Delegation and Lazy Initialization** - powerful patterns for delegating behavior and optimizing resource usage!

---

**Practice Challenge**: Build a stock price monitoring system that fetches prices from multiple sources using Flows, combines them, and alerts when prices cross thresholds using StateFlow.

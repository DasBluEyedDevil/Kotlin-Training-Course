# Lesson 3.2: Null Safety - Kotlin's Safety Net

## The Billion Dollar Mistake

Tony Hoare, inventor of null references, called them his "billion dollar mistake". Null pointer errors crash programs constantly!

**Kotlin's solution:** Build null safety directly into the language!

---

## What is Null?

**Null** means "nothing" or "absence of value."

**Analogy:** An empty box
- A box with a book inside: HAS a value
- An empty box: null (no value)

---

## Nullable vs Non-Nullable Types

In Kotlin, types are **non-nullable by default**:

```kotlin
var name: String = "Alice"
name = null  // ERROR! String can't be null
```

To allow null, add `?`:

```kotlin
var name: String? = "Alice"
name = null  // OK! String? can be null
```

**`String`** = Always has a value
**`String?`** = Might be null

---

## Safe Call Operator (`?.`)

Access properties safely:

```kotlin
fun main() {
    var name: String? = "Alice"
    println(name?.length)  // 5

    name = null
    println(name?.length)  // null (doesn't crash!)
}
```

**`?.`** only calls if not null, otherwise returns null.

---

## Elvis Operator (`?:`)

Provide a default value:

```kotlin
fun main() {
    var name: String? = null
    val length = name?.length ?: 0
    println("Length: $length")  // 0
}
```

**`?:`** means "if null, use this instead"

---

## Not-Null Assertion (`!!`)

Tell Kotlin "I'm sure this isn't null!":

```kotlin
fun main() {
    var name: String? = "Alice"
    println(name!!.length)  // 5
}
```

⚠️ **Warning:** If it IS null, your program crashes! Use carefully.

---

## Safe Casting (`as?`)

```kotlin
val obj: Any = "Hello"
val str: String? = obj as? String  // Safe cast
val num: Int? = obj as? Int  // null (can't cast String to Int)
```

---

## Let Function

Run code only if not null:

```kotlin
fun main() {
    val name: String? = "Alice"

    name?.let {
        println("Name is $it")
        println("Length is ${it.length}")
    }
}
```

If `name` is null, the block doesn't run!

---

## Real-World Example

```kotlin
class User(val name: String, val email: String?, val phone: String?) {

    fun displayContact() {
        println("Name: $name")

        email?.let {
            println("Email: $it")
        } ?: println("Email: Not provided")

        phone?.let {
            println("Phone: $it")
        } ?: println("Phone: Not provided")
    }

    fun getContactLength(): Int {
        return (email?.length ?: 0) + (phone?.length ?: 0)
    }
}

fun main() {
    val user1 = User("Alice", "alice@email.com", null)
    val user2 = User("Bob", null, "555-1234")

    user1.displayContact()
    println()
    user2.displayContact()
}
```

---

## Challenge: Safe User Input

```kotlin
fun main() {
    print("Enter your name (or press Enter to skip): ")
    val input: String? = readLine()

    val name = input?.takeIf { it.isNotBlank() } ?: "Guest"
    println("Hello, $name!")
}
```

---

## Recap

You now understand:

1. **Null** = Absence of value
2. **`Type?`** = Nullable type
3. **`?.`** = Safe call
4. **`?:`** = Elvis operator (default value)
5. **`!!`** = Not-null assertion (dangerous!)
6. **`let`** = Run code if not null

**Key Takeaway:** Kotlin's null safety prevents crashes before they happen!

---

Next: Inheritance and polymorphism!

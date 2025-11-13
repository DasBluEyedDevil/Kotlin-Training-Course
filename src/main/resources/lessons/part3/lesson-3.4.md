# Lesson 3.4: Data Classes and Part 3 Wrap-Up

## Data Classes - Classes Made Easy

Often you just want to store data. Kotlin has a shortcut:

```kotlin
data class User(
    val id: Int,
    val name: String,
    val email: String
)

fun main() {
    val user1 = User(1, "Alice", "alice@email.com")
    val user2 = User(1, "Alice", "alice@email.com")

    println(user1)  // Beautiful toString()
    println(user1 == user2)  // true (auto equals())

    val user3 = user1.copy(name = "Alicia")  // Easy copying
    println(user3)
}
```

**Data classes automatically provide:**
- `toString()` - Nice string representation
- `equals()` - Compare by value
- `hashCode()` - For use in collections
- `copy()` - Create copies with changes

---

## Sealed Classes - Limited Hierarchies

```kotlin
sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val message: String) : Result()
    object Loading : Result()
}

fun handle(result: Result) {
    when (result) {
        is Result.Success -> println("Data: ${result.data}")
        is Result.Error -> println("Error: ${result.message}")
        Result.Loading -> println("Loading...")
    }
    // No else needed - Kotlin knows all cases!
}
```

Perfect for representing states!

---

## Object Declarations - Singletons

```kotlin
object Database {
    var connectionCount = 0

    fun connect() {
        connectionCount++
        println("Connected! Total connections: $connectionCount")
    }
}

fun main() {
    Database.connect()
    Database.connect()
}
```

Only ONE instance ever exists!

---

## Companion Objects - Class-Level Members

```kotlin
class User(val name: String, val id: Int) {
    companion object {
        private var nextId = 1

        fun create(name: String): User {
            return User(name, nextId++)
        }
    }
}

fun main() {
    val user1 = User.create("Alice")
    val user2 = User.create("Bob")
    println("${user1.name}: ${user1.id}")
    println("${user2.name}: ${user2.id}")
}
```

---

## Part 3 Complete! 🎉

You've mastered Object-Oriented Programming:

✅ Classes and objects
✅ Properties and methods
✅ Null safety
✅ Inheritance and polymorphism
✅ Interfaces
✅ Data classes
✅ Sealed classes
✅ Object declarations

**You can now model real-world concepts with code!**

---

## Part 3 Capstone Challenge

Create a simple library management system:

```kotlin
data class Book(
    val isbn: String,
    val title: String,
    val author: String,
    var isAvailable: Boolean = true
)

class Library {
    private val books = mutableListOf<Book>()

    fun addBook(book: Book) {
        books.add(book)
        println("Added: ${book.title}")
    }

    fun checkOut(isbn: String): Boolean {
        val book = books.find { it.isbn == isbn && it.isAvailable }
        return if (book != null) {
            book.isAvailable = false
            println("Checked out: ${book.title}")
            true
        } else {
            println("Book not available")
            false
        }
    }

    fun returnBook(isbn: String) {
        books.find { it.isbn == isbn }?.let {
            it.isAvailable = true
            println("Returned: ${it.title}")
        }
    }

    fun listAvailable() {
        println("\n--- Available Books ---")
        books.filter { it.isAvailable }.forEach {
            println("${it.title} by ${it.author}")
        }
    }
}

fun main() {
    val library = Library()

    library.addBook(Book("123", "1984", "George Orwell"))
    library.addBook(Book("456", "Brave New World", "Aldous Huxley"))

    library.listAvailable()
    library.checkOut("123")
    library.listAvailable()
    library.returnBook("123")
    library.listAvailable()
}
```

---

## What's Next?

**Part 4: Advanced Kotlin** - Lambda functions, higher-order functions, coroutines, and more!

Great work! Continue to Part 4!

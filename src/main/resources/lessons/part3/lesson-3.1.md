# Lesson 3.1: Introduction to Classes and Objects

## Building Your Own Types

Welcome to Part 3! Until now, you've used built-in types (Int, String, List). Now you'll learn to create your **own custom types** to model real-world things!

**Analogy:** Think of a house:
- **Blueprint (Class):** The architectural plans showing how to build a house
- **Actual House (Object):** The real, physical house built from those plans

You can build many houses (objects) from one set of plans (class)!

---

## What is a Class?

A **class** is a blueprint for creating objects. It defines:
- **Properties:** What the object HAS (data)
- **Methods:** What the object CAN DO (functions)

**Example:** A Dog class might have:
- Properties: name, age, breed
- Methods: bark(), eat(), sleep()

---

## Creating Your First Class

```kotlin
class Dog {
    var name = ""
    var age = 0
    var breed = ""

    fun bark() {
        println("$name says: Woof!")
    }
}

fun main() {
    val myDog = Dog()
    myDog.name = "Buddy"
    myDog.age = 3
    myDog.breed = "Golden Retriever"

    println("My dog is ${myDog.name}, a ${myDog.age}-year-old ${myDog.breed}")
    myDog.bark()
}
```

**Output:**
```
My dog is Buddy, a 3-year-old Golden Retriever
Buddy says: Woof!
```

---

## Breaking It Down

```kotlin
class Dog {  // Class definition
    var name = ""  // Property
    var age = 0    // Property

    fun bark() {  // Method
        println("Woof!")
    }
}
```

```kotlin
val myDog = Dog()  // Create an object (instance)
myDog.name = "Buddy"  // Access property
myDog.bark()  // Call method
```

---

## Primary Constructor

Instead of setting properties after creation, use a constructor:

```kotlin
class Dog(var name: String, var age: Int, var breed: String) {
    fun bark() {
        println("$name says: Woof!")
    }
}

fun main() {
    val myDog = Dog("Buddy", 3, "Golden Retriever")
    myDog.bark()
}
```

**Much cleaner!**

---

## Multiple Objects from One Class

```kotlin
fun main() {
    val dog1 = Dog("Buddy", 3, "Golden Retriever")
    val dog2 = Dog("Max", 5, "Beagle")
    val dog3 = Dog("Luna", 2, "Husky")

    dog1.bark()
    dog2.bark()
    dog3.bark()
}
```

**Output:**
```
Buddy says: Woof!
Max says: Woof!
Luna says: Woof!
```

One blueprint, three different dogs!

---

## Methods with Parameters and Return Values

```kotlin
class Calculator {
    fun add(a: Int, b: Int): Int {
        return a + b
    }

    fun multiply(a: Int, b: Int): Int {
        return a * b
    }
}

fun main() {
    val calc = Calculator()
    println("5 + 3 = ${calc.add(5, 3)}")
    println("5 * 3 = ${calc.multiply(5, 3)}")
}
```

---

## `val` vs `var` Properties

```kotlin
class Person(val name: String, var age: Int) {
    // name is immutable (can't change)
    // age is mutable (can change)
}

fun main() {
    val person = Person("Alice", 25)

    // person.name = "Bob"  // ERROR! val can't be changed
    person.age = 26  // OK! var can be changed

    println("${person.name} is ${person.age}")
}
```

---

## Real-World Example: BankAccount

```kotlin
class BankAccount(val accountNumber: String, var balance: Double) {

    fun deposit(amount: Double) {
        if (amount > 0) {
            balance += amount
            println("Deposited $$amount. New balance: $$balance")
        }
    }

    fun withdraw(amount: Double) {
        if (amount > 0 && amount <= balance) {
            balance -= amount
            println("Withdrew $$amount. New balance: $$balance")
        } else {
            println("Insufficient funds!")
        }
    }

    fun displayBalance() {
        println("Account $accountNumber: $$balance")
    }
}

fun main() {
    val account = BankAccount("123456", 1000.0)

    account.displayBalance()
    account.deposit(500.0)
    account.withdraw(200.0)
    account.withdraw(2000.0)  // Not enough!
}
```

---

## Interactive Coding Session

### Challenge 1: Create a Book Class

```kotlin
class Book(val title: String, val author: String, var pages: Int) {

    fun info() {
        println("'$title' by $author ($pages pages)")
    }

    fun read(pagesRead: Int) {
        if (pagesRead <= pages) {
            pages -= pagesRead
            println("Read $pagesRead pages. $pages pages remaining.")
        }
    }
}

fun main() {
    val book = Book("1984", "George Orwell", 328)
    book.info()
    book.read(50)
    book.read(50)
}
```

---

### Challenge 2: Student Class

```kotlin
class Student(val name: String, val id: Int) {
    private val grades = mutableListOf<Int>()

    fun addGrade(grade: Int) {
        grades.add(grade)
        println("Added grade $grade for $name")
    }

    fun getAverage(): Double {
        return if (grades.isEmpty()) 0.0 else grades.average()
    }

    fun displayInfo() {
        println("Student: $name (ID: $id)")
        println("Grades: $grades")
        println("Average: ${"%.2f".format(getAverage())}")
    }
}

fun main() {
    val student = Student("Alice", 12345)
    student.addGrade(85)
    student.addGrade(90)
    student.addGrade(78)
    student.displayInfo()
}
```

---

## Recap: What You've Learned

You now understand:

1. **Classes** = Blueprints for objects
2. **Objects** = Instances created from classes
3. **Properties** = Data the object stores
4. **Methods** = Functions the object can perform
5. **Constructors** = Initialize objects
6. **val vs var** = Immutable vs mutable properties

---

## What's Next?

You've learned the basics of classes! Next, we'll explore **init blocks, default parameters, and data classes** to make your classes even more powerful!

**Key Takeaways:**
- Classes are blueprints, objects are instances
- Properties hold data, methods perform actions
- Use constructors to initialize objects
- One class can create many objects
- Classes model real-world things

---

Excellent! Continue to the next lesson!

# Lesson 2.1: Introduction to Object-Oriented Programming

**Estimated Time**: 60 minutes

---

## Topic Introduction

Welcome to Part 2 of the Kotlin Training Course! You've mastered the fundamentals—variables, control flow, functions, and collections. Now it's time to learn **Object-Oriented Programming (OOP)**, a paradigm that will transform how you design and structure your code.

OOP is more than just a programming technique—it's a way of thinking about problems. Instead of writing procedural code that executes step-by-step, you'll learn to model real-world entities as **objects** with their own data and behavior.

By the end of this lesson, you'll understand what OOP is, why it matters, and how to create your first classes and objects in Kotlin.

---

## The Concept

### What is Object-Oriented Programming?

**Object-Oriented Programming (OOP)** is a programming paradigm that organizes code around **objects**—self-contained units that combine data (properties) and behavior (methods).

**Real-World Analogy: A Car**

Think about a car in the real world:

**Properties (Data)**:
- Color: "Red"
- Brand: "Toyota"
- Model: "Camry"
- Current Speed: 0 mph
- Fuel Level: 100%

**Behaviors (Actions)**:
- Start engine
- Accelerate
- Brake
- Turn left/right
- Refuel

A car is an **object** with both data and functionality. OOP lets you model concepts like this in code!

### Why OOP Matters

**Before OOP (Procedural Programming)**:

```kotlin
// Scattered data
var carColor = "Red"
var carBrand = "Toyota"
var carSpeed = 0

// Scattered functions
fun accelerateCar() {
    carSpeed += 10
}

fun brakeCar() {
    carSpeed -= 10
}
```

**Problems**:
- Data and behavior are disconnected
- Hard to manage multiple cars
- No clear organization
- Prone to errors (which car are we accelerating?)

**With OOP**:

```kotlin
class Car(val color: String, val brand: String) {
    var speed = 0

    fun accelerate() {
        speed += 10
    }

    fun brake() {
        speed -= 10
    }
}

val myCar = Car("Red", "Toyota")
myCar.accelerate()
println(myCar.speed)  // 10
```

**Benefits**:
- ✅ Data and behavior are bundled together
- ✅ Easy to create multiple cars
- ✅ Clear organization and structure
- ✅ Safer and more maintainable

---

## Classes and Objects

### What is a Class?

A **class** is a blueprint or template for creating objects. It defines:
- **Properties**: What data the object holds
- **Methods**: What actions the object can perform

**Analogy**: A class is like a cookie cutter, and objects are the cookies.

```
Class (Blueprint)          Objects (Instances)
┌─────────────┐           ┌─────────────┐ ┌─────────────┐
│   Car       │           │  Car #1     │ │  Car #2     │
│             │ ─────────▶│  Red Toyota │ │  Blue Honda │
│ color       │           │  Speed: 0   │ │  Speed: 30  │
│ speed       │           └─────────────┘ └─────────────┘
│ accelerate()│
└─────────────┘
```

### Creating Your First Class

**Syntax**:

```kotlin
class ClassName {
    // Properties
    // Methods
}
```

**Example: Person Class**

```kotlin
class Person {
    var name: String = ""
    var age: Int = 0

    fun introduce() {
        println("Hi, I'm $name and I'm $age years old.")
    }
}

fun main() {
    // Create an object (instance) of Person
    val person1 = Person()
    person1.name = "Alice"
    person1.age = 25
    person1.introduce()  // Hi, I'm Alice and I'm 25 years old.

    // Create another object
    val person2 = Person()
    person2.name = "Bob"
    person2.age = 30
    person2.introduce()  // Hi, I'm Bob and I'm 30 years old.
}
```

**Key Points**:
- `class Person` defines the blueprint
- `Person()` creates a new instance (object)
- Each object has its own independent data
- `person1` and `person2` are separate objects

---

## Properties

**Properties** are variables that belong to a class. They define the state of an object.

**Two Types**:
- **`val`** (immutable): Cannot be changed after initialization
- **`var`** (mutable): Can be changed

```kotlin
class BankAccount {
    val accountNumber: String = "123456"  // Can't change
    var balance: Double = 0.0              // Can change

    fun deposit(amount: Double) {
        balance += amount
    }

    fun withdraw(amount: Double) {
        if (amount <= balance) {
            balance -= amount
        } else {
            println("Insufficient funds!")
        }
    }
}

fun main() {
    val account = BankAccount()
    println(account.balance)  // 0.0

    account.deposit(100.0)
    println(account.balance)  // 100.0

    account.withdraw(30.0)
    println(account.balance)  // 70.0

    // account.accountNumber = "999999"  // ❌ Error: Val cannot be reassigned
}
```

---

## Constructors

### Primary Constructor

A **constructor** is a special function that initializes an object when it's created. The **primary constructor** is defined in the class header.

**Without Constructor** (tedious):

```kotlin
val person = Person()
person.name = "Alice"
person.age = 25
```

**With Constructor** (clean):

```kotlin
class Person(val name: String, val age: Int) {
    fun introduce() {
        println("Hi, I'm $name and I'm $age years old.")
    }
}

fun main() {
    val person = Person("Alice", 25)
    person.introduce()  // Hi, I'm Alice and I'm 25 years old.
}
```

**Explanation**:
- `class Person(val name: String, val age: Int)` defines properties in the constructor
- `val` or `var` makes them properties (accessible throughout the class)
- Without `val`/`var`, they're just constructor parameters

**Constructor with Default Values**:

```kotlin
class Car(
    val brand: String,
    val model: String,
    val year: Int = 2024,  // Default value
    var mileage: Int = 0   // Default value
) {
    fun displayInfo() {
        println("$year $brand $model - $mileage miles")
    }
}

fun main() {
    val car1 = Car("Toyota", "Camry")  // Uses defaults
    car1.displayInfo()  // 2024 Toyota Camry - 0 miles

    val car2 = Car("Honda", "Civic", 2020, 15000)
    car2.displayInfo()  // 2020 Honda Civic - 15000 miles
}
```

### Init Block

The **`init` block** runs when an object is created. Use it for validation or setup logic.

```kotlin
class BankAccount(val accountNumber: String, initialBalance: Double) {
    var balance: Double = 0.0

    init {
        require(initialBalance >= 0) { "Initial balance cannot be negative" }
        balance = initialBalance
        println("Account $accountNumber created with balance $$balance")
    }
}

fun main() {
    val account = BankAccount("123456", 100.0)
    // Output: Account 123456 created with balance $100.0

    // val badAccount = BankAccount("999999", -50.0)  // ❌ Exception!
}
```

### Secondary Constructors

**Secondary constructors** provide alternative ways to create objects.

```kotlin
class Person(val name: String, val age: Int) {
    var email: String = ""

    // Secondary constructor
    constructor(name: String, age: Int, email: String) : this(name, age) {
        this.email = email
    }

    fun displayInfo() {
        println("Name: $name, Age: $age, Email: $email")
    }
}

fun main() {
    val person1 = Person("Alice", 25)
    person1.displayInfo()  // Name: Alice, Age: 25, Email:

    val person2 = Person("Bob", 30, "bob@example.com")
    person2.displayInfo()  // Name: Bob, Age: 30, Email: bob@example.com
}
```

**Note**: In modern Kotlin, **default parameters** are preferred over secondary constructors.

---

## Methods

**Methods** are functions that belong to a class. They define the behavior of an object.

```kotlin
class Calculator {
    fun add(a: Int, b: Int): Int {
        return a + b
    }

    fun subtract(a: Int, b: Int): Int {
        return a - b
    }

    fun multiply(a: Int, b: Int): Int {
        return a * b
    }

    fun divide(a: Int, b: Int): Double {
        require(b != 0) { "Cannot divide by zero" }
        return a.toDouble() / b
    }
}

fun main() {
    val calc = Calculator()

    println(calc.add(5, 3))        // 8
    println(calc.subtract(10, 4))  // 6
    println(calc.multiply(3, 7))   // 21
    println(calc.divide(15, 3))    // 5.0
}
```

---

## The `this` Keyword

**`this`** refers to the current instance of the class. Use it to:
1. Distinguish between properties and parameters with the same name
2. Reference the current object

```kotlin
class Person(name: String, age: Int) {
    var name: String = name
    var age: Int = age

    fun updateName(name: String) {
        this.name = name  // this.name is the property, name is the parameter
    }

    fun haveBirthday() {
        this.age++  // Optional: this.age++ is the same as age++
    }

    fun compareAge(otherPerson: Person): String {
        return when {
            this.age > otherPerson.age -> "$name is older than ${otherPerson.name}"
            this.age < otherPerson.age -> "$name is younger than ${otherPerson.name}"
            else -> "$name and ${otherPerson.name} are the same age"
        }
    }
}

fun main() {
    val alice = Person("Alice", 25)
    val bob = Person("Bob", 30)

    alice.updateName("Alicia")
    println(alice.name)  // Alicia

    println(alice.compareAge(bob))  // Alicia is younger than Bob
}
```

---

## Exercise 1: Create a Student Class

**Goal**: Create a `Student` class with properties and methods.

**Requirements**:
1. Properties: `name` (String), `studentId` (String), `grade` (Int, 0-100)
2. Method: `isPass()` returns true if grade >= 60, false otherwise
3. Method: `displayInfo()` prints student details
4. Create 3 students and test the methods

---

## Solution: Student Class

```kotlin
class Student(val name: String, val studentId: String, var grade: Int) {

    init {
        require(grade in 0..100) { "Grade must be between 0 and 100" }
    }

    fun isPass(): Boolean {
        return grade >= 60
    }

    fun displayInfo() {
        val status = if (isPass()) "PASS" else "FAIL"
        println("Student: $name (ID: $studentId)")
        println("Grade: $grade - $status")
    }
}

fun main() {
    val student1 = Student("Alice Johnson", "S001", 85)
    val student2 = Student("Bob Smith", "S002", 55)
    val student3 = Student("Carol Davis", "S003", 92)

    student1.displayInfo()
    println()
    student2.displayInfo()
    println()
    student3.displayInfo()
}
```

**Output**:
```
Student: Alice Johnson (ID: S001)
Grade: 85 - PASS

Student: Bob Smith (ID: S002)
Grade: 55 - FAIL

Student: Carol Davis (ID: S003)
Grade: 92 - PASS
```

---

## Exercise 2: Create a Rectangle Class

**Goal**: Create a `Rectangle` class that calculates area and perimeter.

**Requirements**:
1. Properties: `width` (Double), `height` (Double)
2. Method: `area()` returns width * height
3. Method: `perimeter()` returns 2 * (width + height)
4. Method: `isSquare()` returns true if width == height
5. Create rectangles and test all methods

---

## Solution: Rectangle Class

```kotlin
class Rectangle(val width: Double, val height: Double) {

    init {
        require(width > 0 && height > 0) { "Width and height must be positive" }
    }

    fun area(): Double {
        return width * height
    }

    fun perimeter(): Double {
        return 2 * (width + height)
    }

    fun isSquare(): Boolean {
        return width == height
    }

    fun displayInfo() {
        println("Rectangle: ${width} x ${height}")
        println("  Area: ${area()}")
        println("  Perimeter: ${perimeter()}")
        println("  Is Square: ${isSquare()}")
    }
}

fun main() {
    val rect1 = Rectangle(5.0, 10.0)
    val rect2 = Rectangle(7.0, 7.0)

    rect1.displayInfo()
    println()
    rect2.displayInfo()
}
```

**Output**:
```
Rectangle: 5.0 x 10.0
  Area: 50.0
  Perimeter: 30.0
  Is Square: false

Rectangle: 7.0 x 7.0
  Area: 49.0
  Perimeter: 28.0
  Is Square: true
```

---

## Exercise 3: Create a BankAccount Class

**Goal**: Build a complete bank account system.

**Requirements**:
1. Properties: `accountHolder` (String), `accountNumber` (String), `balance` (Double, private)
2. Method: `deposit(amount: Double)` adds to balance
3. Method: `withdraw(amount: Double)` subtracts from balance (check sufficient funds)
4. Method: `getBalance()` returns current balance
5. Method: `transfer(amount: Double, targetAccount: BankAccount)` transfers money
6. Create accounts and perform transactions

---

## Solution: BankAccount Class

```kotlin
class BankAccount(val accountHolder: String, val accountNumber: String) {
    private var balance: Double = 0.0

    fun deposit(amount: Double) {
        require(amount > 0) { "Deposit amount must be positive" }
        balance += amount
        println("Deposited $$amount. New balance: $$balance")
    }

    fun withdraw(amount: Double): Boolean {
        require(amount > 0) { "Withdrawal amount must be positive" }

        return if (amount <= balance) {
            balance -= amount
            println("Withdrew $$amount. New balance: $$balance")
            true
        } else {
            println("Insufficient funds! Balance: $$balance, Requested: $$amount")
            false
        }
    }

    fun getBalance(): Double {
        return balance
    }

    fun transfer(amount: Double, targetAccount: BankAccount): Boolean {
        println("\nTransferring $$amount from ${this.accountHolder} to ${targetAccount.accountHolder}")

        return if (withdraw(amount)) {
            targetAccount.deposit(amount)
            println("Transfer successful!")
            true
        } else {
            println("Transfer failed!")
            false
        }
    }

    fun displayInfo() {
        println("Account Holder: $accountHolder")
        println("Account Number: $accountNumber")
        println("Balance: $$balance")
    }
}

fun main() {
    val aliceAccount = BankAccount("Alice Johnson", "ACC001")
    val bobAccount = BankAccount("Bob Smith", "ACC002")

    // Alice deposits money
    aliceAccount.deposit(1000.0)
    println()

    // Alice withdraws money
    aliceAccount.withdraw(200.0)
    println()

    // Alice tries to withdraw more than balance
    aliceAccount.withdraw(1000.0)
    println()

    // Alice transfers to Bob
    aliceAccount.transfer(300.0, bobAccount)
    println()

    // Display final balances
    aliceAccount.displayInfo()
    println()
    bobAccount.displayInfo()
}
```

**Output**:
```
Deposited $1000.0. New balance: $1000.0

Withdrew $200.0. New balance: $800.0

Insufficient funds! Balance: $800.0, Requested: $1000.0

Transferring $300.0 from Alice Johnson to Bob Smith
Withdrew $300.0. New balance: $500.0
Deposited $300.0. New balance: $300.0
Transfer successful!

Account Holder: Alice Johnson
Account Number: ACC001
Balance: $500.0

Account Holder: Bob Smith
Account Number: ACC002
Balance: $300.0
```

---

## Checkpoint Quiz

### Question 1
What is a class in OOP?

A) A function that performs calculations
B) A blueprint or template for creating objects
C) A variable that stores data
D) A loop that iterates over collections

### Question 2
What is the difference between `val` and `var` for properties?

A) `val` is for integers, `var` is for strings
B) `val` is immutable (read-only), `var` is mutable (read-write)
C) `val` is for classes, `var` is for functions
D) There is no difference

### Question 3
What does the `this` keyword refer to?

A) The main function
B) The parent class
C) The current instance of the class
D) A static variable

### Question 4
What is a constructor?

A) A method that destroys objects
B) A special function that initializes objects when they're created
C) A variable that stores class data
D) A loop that creates multiple objects

### Question 5
Which of the following correctly creates an instance of a `Car` class?

A) `Car car = new Car()`
B) `val car = Car()`
C) `Car car()`
D) `new Car() as car`

---

## Quiz Answers

**Question 1: B) A blueprint or template for creating objects**

A class defines the structure (properties) and behavior (methods) that objects will have. It's like a blueprint for a house—the blueprint itself isn't a house, but you can build many houses from it.

```kotlin
class Car(val brand: String)  // Blueprint

val car1 = Car("Toyota")  // Object 1
val car2 = Car("Honda")   // Object 2
```

---

**Question 2: B) `val` is immutable (read-only), `var` is mutable (read-write)**

```kotlin
class Person(val name: String, var age: Int)

val person = Person("Alice", 25)
// person.name = "Bob"  // ❌ Error: Val cannot be reassigned
person.age = 26         // ✅ OK: Var can be changed
```

---

**Question 3: C) The current instance of the class**

`this` refers to the object itself. It's useful when you need to distinguish between properties and parameters with the same name.

```kotlin
class Person(name: String) {
    var name: String = name  // this.name (property) = name (parameter)

    fun greet() {
        println("I am ${this.name}")  // References this object's name
    }
}
```

---

**Question 4: B) A special function that initializes objects when they're created**

Constructors set up the initial state of an object.

```kotlin
class BankAccount(val accountNumber: String, initialBalance: Double) {
    var balance = initialBalance
}

val account = BankAccount("123456", 1000.0)  // Constructor called here
```

---

**Question 5: B) `val car = Car()`**

Kotlin doesn't use the `new` keyword like Java. You create objects by calling the class name with parentheses.

```kotlin
// ✅ Correct Kotlin syntax
val car = Car("Toyota")

// ❌ Wrong - Java syntax
// Car car = new Car("Toyota")
```

---

## What You've Learned

✅ What OOP is and why it's powerful
✅ How to define classes with properties and methods
✅ Creating objects (instances) from classes
✅ Using constructors (primary, init blocks, secondary)
✅ The difference between `val` and `var` properties
✅ The `this` keyword and when to use it
✅ Building practical classes (Student, Rectangle, BankAccount)

---

## Next Steps

In **Lesson 2.2: Properties and Initialization**, you'll learn:
- Custom getters and setters
- Late initialization with `lateinit`
- Lazy initialization for performance
- Backing fields for advanced property control
- Property delegation basics

You're building a strong OOP foundation! Keep going!

---

**Congratulations on completing Lesson 2.1!** 🎉

You've taken your first steps into Object-Oriented Programming. This is a fundamental shift in how you think about code—from procedures to objects that model the real world.

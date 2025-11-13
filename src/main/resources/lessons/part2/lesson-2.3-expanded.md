# Lesson 2.3: Inheritance and Polymorphism

**Estimated Time**: 70 minutes

---

## Topic Introduction

You've learned to create classes and manage properties. Now it's time to explore one of OOP's most powerful features: **inheritance**.

Inheritance allows you to create new classes based on existing ones, reusing and extending their functionality. Combined with **polymorphism**, you can write flexible, maintainable code that models complex real-world relationships.

Imagine you're building a system for different types of employees: managers, developers, and interns. They all share common attributes (name, ID, salary) but have unique behaviors. Inheritance lets you capture these commonalities and differences elegantly.

---

## The Concept

### What is Inheritance?

**Inheritance** is a mechanism where a new class (child/subclass) is based on an existing class (parent/superclass), inheriting its properties and methods.

**Real-World Analogy: Vehicle Hierarchy**

```
        Vehicle
       /   |   \
     Car  Bike  Truck
    /
  SportsCar
```

- **Vehicle** (parent): Has wheels, can move, has fuel
- **Car** (child): Inherits from Vehicle, adds doors and trunk
- **SportsCar** (grandchild): Inherits from Car, adds turbo boost

**Why Inheritance?**
- **Code Reuse**: Don't repeat common functionality
- **Logical Organization**: Model real-world relationships
- **Maintainability**: Change once, affect all subclasses
- **Polymorphism**: Treat different types uniformly

---

## Inheritance Basics

### The `open` Keyword

In Kotlin, classes are **final by default** (cannot be inherited). Use `open` to allow inheritance.

```kotlin
// ❌ Cannot inherit from this
class Animal {
    fun eat() = println("Eating...")
}

// ✅ Can inherit from this
open class Bird {
    open fun fly() = println("Flying...")
}
```

**Why are classes final by default?**
- Safety: Prevents unintended inheritance
- Performance: Compiler optimizations
- Design: Encourages composition over inheritance

### Creating a Subclass

Use a colon (`:`) to inherit from a superclass.

```kotlin
open class Animal(val name: String) {
    open fun makeSound() {
        println("Some generic animal sound")
    }

    fun sleep() {
        println("$name is sleeping...")
    }
}

class Dog(name: String) : Animal(name) {
    override fun makeSound() {
        println("$name says: Woof! Woof!")
    }

    fun fetch() {
        println("$name is fetching the ball!")
    }
}

class Cat(name: String) : Animal(name) {
    override fun makeSound() {
        println("$name says: Meow!")
    }

    fun scratch() {
        println("$name is scratching the furniture!")
    }
}

fun main() {
    val dog = Dog("Buddy")
    dog.makeSound()  // Buddy says: Woof! Woof!
    dog.sleep()      // Buddy is sleeping...
    dog.fetch()      // Buddy is fetching the ball!

    val cat = Cat("Whiskers")
    cat.makeSound()  // Whiskers says: Meow!
    cat.sleep()      // Whiskers is sleeping...
    cat.scratch()    // Whiskers is scratching the furniture!
}
```

**Key Points**:
- `Dog` and `Cat` inherit from `Animal`
- They inherit `sleep()` (can use it without redefining)
- They override `makeSound()` with their own implementation
- They add unique methods (`fetch()`, `scratch()`)

---

## Overriding Methods

To override a method from the superclass:
1. The superclass method must be marked `open`
2. Use the `override` keyword in the subclass

```kotlin
open class Shape {
    open fun draw() {
        println("Drawing a shape")
    }

    open fun area(): Double {
        return 0.0
    }
}

class Circle(val radius: Double) : Shape() {
    override fun draw() {
        println("Drawing a circle with radius $radius")
    }

    override fun area(): Double {
        return Math.PI * radius * radius
    }
}

class Rectangle(val width: Double, val height: Double) : Shape() {
    override fun draw() {
        println("Drawing a rectangle $width x $height")
    }

    override fun area(): Double {
        return width * height
    }
}

fun main() {
    val circle = Circle(5.0)
    circle.draw()  // Drawing a circle with radius 5.0
    println("Area: ${circle.area()}")  // Area: 78.53981633974483

    val rect = Rectangle(4.0, 6.0)
    rect.draw()  // Drawing a rectangle 4.0 x 6.0
    println("Area: ${rect.area()}")  // Area: 24.0
}
```

---

## The `super` Keyword

Use `super` to call the superclass's implementation.

```kotlin
open class Employee(val name: String, val salary: Double) {
    open fun displayInfo() {
        println("Employee: $name")
        println("Salary: $$salary")
    }

    open fun work() {
        println("$name is working...")
    }
}

class Manager(name: String, salary: Double, val teamSize: Int) : Employee(name, salary) {
    override fun displayInfo() {
        super.displayInfo()  // Call parent's implementation
        println("Team Size: $teamSize")
        println("Role: Manager")
    }

    override fun work() {
        println("$name is managing a team of $teamSize people")
    }

    fun conductMeeting() {
        println("$name is conducting a team meeting")
    }
}

class Developer(name: String, salary: Double, val programmingLanguage: String) : Employee(name, salary) {
    override fun displayInfo() {
        super.displayInfo()
        println("Language: $programmingLanguage")
        println("Role: Developer")
    }

    override fun work() {
        println("$name is coding in $programmingLanguage")
    }
}

fun main() {
    val manager = Manager("Alice", 120000.0, 5)
    manager.displayInfo()
    println()
    manager.work()
    manager.conductMeeting()

    println("\n---\n")

    val dev = Developer("Bob", 90000.0, "Kotlin")
    dev.displayInfo()
    println()
    dev.work()
}
```

**Output**:
```
Employee: Alice
Salary: $120000.0
Team Size: 5
Role: Manager

Alice is managing a team of 5 people
Alice is conducting a team meeting

---

Employee: Bob
Salary: $90000.0
Language: Kotlin
Role: Developer

Bob is coding in Kotlin
```

---

## Abstract Classes

**Abstract classes** are classes that cannot be instantiated directly. They serve as blueprints for subclasses.

Use abstract classes when:
- You want to provide a common base with some implemented methods
- You want to force subclasses to implement specific methods

```kotlin
abstract class Vehicle(val brand: String, val model: String) {
    var speed: Int = 0

    // Abstract method (no implementation)
    abstract fun start()

    // Abstract method
    abstract fun stop()

    // Concrete method (has implementation)
    fun accelerate(amount: Int) {
        speed += amount
        println("$brand $model accelerating to $speed km/h")
    }

    fun brake(amount: Int) {
        speed -= amount
        if (speed < 0) speed = 0
        println("$brand $model slowing down to $speed km/h")
    }
}

class Car(brand: String, model: String) : Vehicle(brand, model) {
    override fun start() {
        println("$brand $model: Turning key, engine starts")
    }

    override fun stop() {
        println("$brand $model: Turning off engine")
        speed = 0
    }
}

class ElectricBike(brand: String, model: String) : Vehicle(brand, model) {
    override fun start() {
        println("$brand $model: Pressing power button, motor starts silently")
    }

    override fun stop() {
        println("$brand $model: Releasing throttle, motor stops")
        speed = 0
    }
}

fun main() {
    // val vehicle = Vehicle("Generic", "Model")  // ❌ Cannot instantiate abstract class

    val car = Car("Toyota", "Camry")
    car.start()          // Toyota Camry: Turning key, engine starts
    car.accelerate(50)   // Toyota Camry accelerating to 50 km/h
    car.accelerate(30)   // Toyota Camry accelerating to 80 km/h
    car.brake(20)        // Toyota Camry slowing down to 60 km/h
    car.stop()           // Toyota Camry: Turning off engine

    println()

    val bike = ElectricBike("Tesla", "E-Bike Pro")
    bike.start()         // Tesla E-Bike Pro: Pressing power button, motor starts silently
    bike.accelerate(25)  // Tesla E-Bike Pro accelerating to 25 km/h
    bike.stop()          // Tesla E-Bike Pro: Releasing throttle, motor stops
}
```

---

## Polymorphism

**Polymorphism** means "many forms." It allows you to treat objects of different types through a common interface.

**Example: Animal Sounds**

```kotlin
open class Animal(val name: String) {
    open fun makeSound() {
        println("Some generic sound")
    }
}

class Dog(name: String) : Animal(name) {
    override fun makeSound() {
        println("$name: Woof! Woof!")
    }
}

class Cat(name: String) : Animal(name) {
    override fun makeSound() {
        println("$name: Meow!")
    }
}

class Cow(name: String) : Animal(name) {
    override fun makeSound() {
        println("$name: Moo!")
    }
}

fun makeAnimalSpeak(animal: Animal) {
    animal.makeSound()  // Polymorphism: calls the correct method based on actual type
}

fun main() {
    val animals: List<Animal> = listOf(
        Dog("Buddy"),
        Cat("Whiskers"),
        Cow("Bessie"),
        Dog("Max"),
        Cat("Fluffy")
    )

    // Polymorphism in action
    animals.forEach { animal ->
        makeAnimalSpeak(animal)
    }
}
```

**Output**:
```
Buddy: Woof! Woof!
Whiskers: Meow!
Bessie: Moo!
Max: Woof! Woof!
Fluffy: Meow!
```

**Key Point**: Even though `animals` is a list of `Animal`, each object calls its own specific `makeSound()` implementation!

---

## Type Checking and Casting

### Type Checking with `is`

```kotlin
fun describe(obj: Any) {
    when (obj) {
        is String -> println("String of length ${obj.length}")
        is Int -> println("Integer: $obj")
        is List<*> -> println("List with ${obj.size} items")
        is Dog -> println("Dog named ${obj.name}")
        else -> println("Unknown type")
    }
}
```

### Smart Casting

Kotlin automatically casts after type checking:

```kotlin
fun feedAnimal(animal: Animal) {
    if (animal is Dog) {
        // animal is automatically cast to Dog here
        animal.fetch()
    } else if (animal is Cat) {
        // animal is automatically cast to Cat here
        animal.scratch()
    }
}
```

### Explicit Casting

```kotlin
val animal: Animal = Dog("Buddy")

// Safe cast (returns null if cast fails)
val dog: Dog? = animal as? Dog
dog?.fetch()

// Unsafe cast (throws exception if cast fails)
val dog2: Dog = animal as Dog
dog2.fetch()
```

---

## Exercise 1: Employee Hierarchy

**Goal**: Create an employee management system with inheritance.

**Requirements**:
1. Abstract class `Employee` with properties: `name`, `id`, `baseSalary`
2. Abstract method: `calculateSalary(): Double`
3. Method: `displayInfo()`
4. Class `FullTimeEmployee` extends `Employee`:
   - Adds `bonus` property
   - Implements `calculateSalary()` as baseSalary + bonus
5. Class `Contractor` extends `Employee`:
   - Adds `hourlyRate` and `hoursWorked` properties
   - Implements `calculateSalary()` as hourlyRate * hoursWorked
6. Class `Intern` extends `Employee`:
   - Adds `stipend` property
   - Implements `calculateSalary()` as stipend (fixed amount)
7. Create a list of mixed employees and calculate total payroll

---

## Solution: Employee Hierarchy

```kotlin
abstract class Employee(val name: String, val id: String, val baseSalary: Double) {
    abstract fun calculateSalary(): Double

    open fun displayInfo() {
        println("ID: $id")
        println("Name: $name")
        println("Salary: $${calculateSalary()}")
    }
}

class FullTimeEmployee(
    name: String,
    id: String,
    baseSalary: Double,
    val bonus: Double
) : Employee(name, id, baseSalary) {

    override fun calculateSalary(): Double {
        return baseSalary + bonus
    }

    override fun displayInfo() {
        println("=== Full-Time Employee ===")
        super.displayInfo()
        println("Base Salary: $$baseSalary")
        println("Bonus: $$bonus")
    }
}

class Contractor(
    name: String,
    id: String,
    val hourlyRate: Double,
    val hoursWorked: Double
) : Employee(name, id, 0.0) {

    override fun calculateSalary(): Double {
        return hourlyRate * hoursWorked
    }

    override fun displayInfo() {
        println("=== Contractor ===")
        super.displayInfo()
        println("Hourly Rate: $$hourlyRate")
        println("Hours Worked: $hoursWorked")
    }
}

class Intern(
    name: String,
    id: String,
    val stipend: Double
) : Employee(name, id, 0.0) {

    override fun calculateSalary(): Double {
        return stipend
    }

    override fun displayInfo() {
        println("=== Intern ===")
        super.displayInfo()
        println("Monthly Stipend: $$stipend")
    }
}

fun main() {
    val employees: List<Employee> = listOf(
        FullTimeEmployee("Alice Johnson", "FT001", 80000.0, 10000.0),
        FullTimeEmployee("Bob Smith", "FT002", 75000.0, 8000.0),
        Contractor("Carol Davis", "CT001", 50.0, 160.0),
        Contractor("David Wilson", "CT002", 60.0, 120.0),
        Intern("Eve Brown", "IN001", 2000.0),
        Intern("Frank Miller", "IN002", 1800.0)
    )

    employees.forEach { employee ->
        employee.displayInfo()
        println()
    }

    val totalPayroll = employees.sumOf { it.calculateSalary() }
    println("=== Payroll Summary ===")
    println("Total Employees: ${employees.size}")
    println("Total Payroll: $$totalPayroll")
}
```

---

## Exercise 2: Shape Hierarchy

**Goal**: Create a comprehensive shape system.

**Requirements**:
1. Abstract class `Shape` with abstract methods: `area()`, `perimeter()`, `draw()`
2. Class `Circle` extends `Shape` with radius
3. Class `Rectangle` extends `Shape` with width and height
4. Class `Triangle` extends `Shape` with three sides
5. Create a function that prints total area of all shapes

---

## Solution: Shape Hierarchy

```kotlin
import kotlin.math.sqrt

abstract class Shape(val color: String) {
    abstract fun area(): Double
    abstract fun perimeter(): Double
    abstract fun draw()

    fun displayInfo() {
        println("Color: $color")
        println("Area: ${String.format("%.2f", area())}")
        println("Perimeter: ${String.format("%.2f", perimeter())}")
    }
}

class Circle(color: String, val radius: Double) : Shape(color) {
    override fun area(): Double = Math.PI * radius * radius

    override fun perimeter(): Double = 2 * Math.PI * radius

    override fun draw() {
        println("⭕ Drawing a $color circle with radius $radius")
    }
}

class Rectangle(color: String, val width: Double, val height: Double) : Shape(color) {
    override fun area(): Double = width * height

    override fun perimeter(): Double = 2 * (width + height)

    override fun draw() {
        println("▭ Drawing a $color rectangle ${width}x${height}")
    }
}

class Triangle(color: String, val side1: Double, val side2: Double, val side3: Double) : Shape(color) {

    init {
        require(isValid()) { "Invalid triangle: sides don't satisfy triangle inequality" }
    }

    private fun isValid(): Boolean {
        return side1 + side2 > side3 && side1 + side3 > side2 && side2 + side3 > side1
    }

    override fun area(): Double {
        // Heron's formula
        val s = perimeter() / 2
        return sqrt(s * (s - side1) * (s - side2) * (s - side3))
    }

    override fun perimeter(): Double = side1 + side2 + side3

    override fun draw() {
        println("△ Drawing a $color triangle with sides $side1, $side2, $side3")
    }
}

fun printTotalArea(shapes: List<Shape>) {
    val total = shapes.sumOf { it.area() }
    println("Total area of all shapes: ${String.format("%.2f", total)}")
}

fun main() {
    val shapes: List<Shape> = listOf(
        Circle("Red", 5.0),
        Rectangle("Blue", 4.0, 6.0),
        Triangle("Green", 3.0, 4.0, 5.0),
        Circle("Yellow", 3.0),
        Rectangle("Purple", 10.0, 2.0)
    )

    shapes.forEach { shape ->
        shape.draw()
        shape.displayInfo()
        println()
    }

    printTotalArea(shapes)
}
```

---

## Exercise 3: Bank Account Hierarchy

**Goal**: Build different types of bank accounts with shared and unique features.

**Requirements**:
1. Open class `BankAccount` with `accountNumber`, `holder`, `balance`
2. Methods: `deposit()`, `withdraw()`, `displayBalance()`
3. Class `SavingsAccount` extends `BankAccount`:
   - Adds `interestRate` property
   - Method `applyInterest()`
   - Withdrawal limit of 3 times per month
4. Class `CheckingAccount` extends `BankAccount`:
   - Adds `overdraftLimit` property
   - Can withdraw beyond balance up to overdraft limit
5. Test all account types

---

## Solution: Bank Account Hierarchy

```kotlin
open class BankAccount(val accountNumber: String, val holder: String) {
    protected var balance: Double = 0.0

    open fun deposit(amount: Double) {
        require(amount > 0) { "Deposit amount must be positive" }
        balance += amount
        println("Deposited $$amount. New balance: $$balance")
    }

    open fun withdraw(amount: Double): Boolean {
        require(amount > 0) { "Withdrawal amount must be positive" }

        return if (amount <= balance) {
            balance -= amount
            println("Withdrew $$amount. New balance: $$balance")
            true
        } else {
            println("Insufficient funds! Balance: $$balance")
            false
        }
    }

    fun displayBalance() {
        println("Account: $accountNumber ($holder)")
        println("Balance: $$balance")
    }
}

class SavingsAccount(
    accountNumber: String,
    holder: String,
    val interestRate: Double
) : BankAccount(accountNumber, holder) {

    private var withdrawalsThisMonth = 0
    private val maxWithdrawals = 3

    override fun withdraw(amount: Double): Boolean {
        if (withdrawalsThisMonth >= maxWithdrawals) {
            println("Withdrawal limit reached! Maximum $maxWithdrawals withdrawals per month.")
            return false
        }

        val success = super.withdraw(amount)
        if (success) {
            withdrawalsThisMonth++
            println("Withdrawals remaining this month: ${maxWithdrawals - withdrawalsThisMonth}")
        }
        return success
    }

    fun applyInterest() {
        val interest = balance * interestRate / 100
        balance += interest
        println("Interest applied: $$interest. New balance: $$balance")
    }

    fun resetMonthlyWithdrawals() {
        withdrawalsThisMonth = 0
        println("Monthly withdrawal limit reset")
    }
}

class CheckingAccount(
    accountNumber: String,
    holder: String,
    val overdraftLimit: Double
) : BankAccount(accountNumber, holder) {

    override fun withdraw(amount: Double): Boolean {
        require(amount > 0) { "Withdrawal amount must be positive" }

        val availableFunds = balance + overdraftLimit

        return if (amount <= availableFunds) {
            balance -= amount
            println("Withdrew $$amount. New balance: $$balance")
            if (balance < 0) {
                println("⚠️ Account overdrawn by $${-balance}")
            }
            true
        } else {
            println("Exceeds overdraft limit! Available: $$availableFunds")
            false
        }
    }
}

fun main() {
    println("=== Savings Account ===")
    val savings = SavingsAccount("SAV001", "Alice Johnson", 2.5)
    savings.deposit(1000.0)
    savings.applyInterest()
    savings.withdraw(100.0)
    savings.withdraw(100.0)
    savings.withdraw(100.0)
    savings.withdraw(100.0)  // Should fail (limit reached)
    savings.displayBalance()

    println("\n=== Checking Account ===")
    val checking = CheckingAccount("CHK001", "Bob Smith", 500.0)
    checking.deposit(1000.0)
    checking.withdraw(1200.0)  // Uses overdraft
    checking.withdraw(400.0)   // Should fail (exceeds overdraft limit)
    checking.displayBalance()
}
```

---

## Checkpoint Quiz

### Question 1
What keyword is required to allow a class to be inherited?

A) `extend`
B) `open`
C) `inherit`
D) `abstract`

### Question 2
What is polymorphism?

A) Creating multiple classes
B) The ability to treat objects of different types through a common interface
C) Overriding methods
D) Using multiple inheritance

### Question 3
When should you use an abstract class?

A) When you never want instances of that class
B) When you want to provide a common base with some implemented methods
C) When you want to force subclasses to implement specific methods
D) Both B and C

### Question 4
What does the `super` keyword do?

A) Creates a new superclass
B) Calls the subclass's implementation
C) Calls the superclass's implementation
D) Deletes the superclass

### Question 5
What is smart casting in Kotlin?

A) Converting strings to integers
B) Automatic type casting after a type check with `is`
C) Casting to any type
D) A compiler optimization

---

## Quiz Answers

**Question 1: B) `open`**

Kotlin classes are final by default. Use `open` to allow inheritance.

```kotlin
open class Animal { }  // ✅ Can inherit
class Dog : Animal()   // ✅ Works

class Plant { }        // ❌ Final by default
// class Tree : Plant() // ❌ Error
```

---

**Question 2: B) The ability to treat objects of different types through a common interface**

Polymorphism lets you write code that works with a superclass but automatically uses the correct subclass implementation.

```kotlin
fun makeSound(animal: Animal) {
    animal.makeSound()  // Calls Dog, Cat, or Cow's version
}
```

---

**Question 3: D) Both B and C**

Abstract classes provide partial implementation (some methods implemented, some abstract) and force subclasses to implement abstract methods.

```kotlin
abstract class Shape {
    abstract fun area(): Double  // Must implement
    fun display() { }           // Already implemented
}
```

---

**Question 4: C) Calls the superclass's implementation**

Use `super` to access the parent class's methods or properties.

```kotlin
override fun displayInfo() {
    super.displayInfo()  // Call parent's version first
    println("Additional info")
}
```

---

**Question 5: B) Automatic type casting after a type check with `is`**

After checking a type with `is`, Kotlin automatically casts the variable.

```kotlin
if (animal is Dog) {
    animal.fetch()  // No explicit cast needed!
}
```

---

## What You've Learned

✅ Inheritance basics with `open` and `:` syntax
✅ Overriding methods with `override`
✅ Using `super` to call parent implementations
✅ Abstract classes for shared functionality
✅ Polymorphism for flexible code
✅ Type checking with `is` and smart casting

---

## Next Steps

In **Lesson 2.4: Interfaces and Abstract Classes**, you'll learn:
- Defining and implementing interfaces
- Multiple interface implementation
- Default interface methods
- When to use interfaces vs abstract classes
- Real-world design patterns

You're mastering inheritance! Keep building on this foundation!

---

**Congratulations on completing Lesson 2.3!** 🎉

Inheritance and polymorphism are cornerstones of OOP. You now have the tools to create flexible, maintainable class hierarchies!

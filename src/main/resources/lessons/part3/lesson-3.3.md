# Lesson 3.3: Inheritance and Polymorphism

## Building on Existing Classes

**Analogy:** Think of vehicle types:
- All vehicles have wheels and can move
- Cars are vehicles WITH doors and steering wheel
- Motorcycles are vehicles WITH handlebars

**Inheritance** lets you create specialized versions of existing classes!

---

## Basic Inheritance

```kotlin
open class Vehicle(val brand: String, val year: Int) {
    fun start() {
        println("$brand is starting...")
    }

    open fun describe() {
        println("$brand vehicle from $year")
    }
}

class Car(brand: String, year: Int, val doors: Int) : Vehicle(brand, year) {
    override fun describe() {
        println("$brand car from $year with $doors doors")
    }
}

fun main() {
    val car = Car("Toyota", 2023, 4)
    car.start()  // Inherited
    car.describe()  // Overridden
}
```

- **`open`** = Can be inherited/overridden
- **`: Vehicle(...)`** = Inherits from Vehicle
- **`override`** = Replaces parent method

---

## Abstract Classes

Can't be instantiated directly:

```kotlin
abstract class Shape {
    abstract fun area(): Double
    abstract fun perimeter(): Double
}

class Rectangle(val width: Double, val height: Double) : Shape() {
    override fun area() = width * height
    override fun perimeter() = 2 * (width + height)
}

class Circle(val radius: Double) : Shape() {
    override fun area() = Math.PI * radius * radius
    override fun perimeter() = 2 * Math.PI * radius
}
```

---

## Interfaces

Define contracts that classes must implement:

```kotlin
interface Drivable {
    fun drive()
    fun stop()
}

interface Electric {
    fun charge()
}

class TeslaCar : Drivable, Electric {
    override fun drive() {
        println("Driving silently...")
    }

    override fun stop() {
        println("Stopping...")
    }

    override fun charge() {
        println("Charging battery...")
    }
}
```

---

## Polymorphism

```kotlin
fun main() {
    val shapes: List<Shape> = listOf(
        Rectangle(5.0, 3.0),
        Circle(4.0),
        Rectangle(2.0, 2.0)
    )

    for (shape in shapes) {
        println("Area: ${shape.area()}")
    }
}
```

Same method call, different behavior!

---

## Recap

1. **Inheritance** = Reuse code from parent classes
2. **`open`** = Allow inheritance/override
3. **`abstract`** = Must be inherited
4. **Interfaces** = Define contracts
5. **Polymorphism** = Many forms, one interface

Next: Data classes and sealed classes!

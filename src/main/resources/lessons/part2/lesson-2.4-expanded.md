# Lesson 2.4: Interfaces and Abstract Classes

**Estimated Time**: 65 minutes

---

## Topic Introduction

You've learned about inheritance and abstract classes. Now let's explore **interfaces**—one of OOP's most powerful tools for designing flexible, maintainable systems.

An **interface** defines a contract: "Any class that implements me must provide these capabilities." Unlike abstract classes (which you can only inherit from one), a class can implement multiple interfaces, enabling composition of behaviors.

This lesson will teach you:
- How to define and implement interfaces
- The difference between interfaces and abstract classes
- When to use each
- Default interface methods
- Real-world design patterns

---

## The Concept

### What is an Interface?

An **interface** is a contract that defines what a class can do, without specifying how it does it.

**Real-World Analogy: Power Outlets**

A power outlet is an interface:
- **Contract**: "I provide electricity through these two/three holes"
- **Devices** (implementations): Phone chargers, laptops, lamps all plug into the same outlet
- **Different implementations**: Each device uses the electricity differently, but all follow the outlet interface

```
  Interface: PowerSource
       ↓
  ┌───────────────────────────┐
  │ fun provideElectricity()  │
  └───────────────────────────┘
           ↓         ↓         ↓
     PhoneCharger  Laptop   Lamp
```

### Why Interfaces?

**Problems interfaces solve**:
1. **Multiple inheritance**: A class can implement multiple interfaces
2. **Loose coupling**: Code depends on contracts, not implementations
3. **Testability**: Easy to create mock implementations for testing
4. **Flexibility**: Swap implementations without changing client code

---

## Defining Interfaces

**Syntax**:

```kotlin
interface InterfaceName {
    fun methodName()  // Abstract by default
    val propertyName: Type  // Must be overridden
}
```

**Example: Simple Interface**

```kotlin
interface Drawable {
    fun draw()
}

class Circle : Drawable {
    override fun draw() {
        println("Drawing a circle")
    }
}

class Square : Drawable {
    override fun draw() {
        println("Drawing a square")
    }
}

fun main() {
    val shapes: List<Drawable> = listOf(Circle(), Square())

    shapes.forEach { shape ->
        shape.draw()
    }
}
```

**Output**:
```
Drawing a circle
Drawing a square
```

---

## Implementing Multiple Interfaces

Unlike classes (single inheritance), you can implement multiple interfaces!

```kotlin
interface Flyable {
    fun fly()
}

interface Swimmable {
    fun swim()
}

interface Walkable {
    fun walk()
}

class Duck : Flyable, Swimmable, Walkable {
    override fun fly() {
        println("Duck is flying")
    }

    override fun swim() {
        println("Duck is swimming")
    }

    override fun walk() {
        println("Duck is walking")
    }
}

class Fish : Swimmable {
    override fun swim() {
        println("Fish is swimming")
    }
}

class Bird : Flyable, Walkable {
    override fun fly() {
        println("Bird is flying")
    }

    override fun walk() {
        println("Bird is walking")
    }
}

fun main() {
    val duck = Duck()
    duck.fly()
    duck.swim()
    duck.walk()

    println()

    val fish = Fish()
    fish.swim()

    println()

    val bird = Bird()
    bird.fly()
    bird.walk()
}
```

**Output**:
```
Duck is flying
Duck is swimming
Duck is walking

Fish is swimming

Bird is flying
Bird is walking
```

---

## Interface Properties

Interfaces can declare properties, but they can't have backing fields.

```kotlin
interface Vehicle {
    val maxSpeed: Int  // Must be overridden
    val type: String
        get() = "Generic Vehicle"  // Can provide default

    fun start()
    fun stop()
}

class Car(override val maxSpeed: Int) : Vehicle {
    override val type: String
        get() = "Car"

    override fun start() {
        println("Car starting with key")
    }

    override fun stop() {
        println("Car stopping")
    }
}

class Motorcycle(override val maxSpeed: Int) : Vehicle {
    override val type: String = "Motorcycle"  // Can also initialize directly

    override fun start() {
        println("Motorcycle starting with button")
    }

    override fun stop() {
        println("Motorcycle stopping")
    }
}

fun main() {
    val car = Car(180)
    println("${car.type} - Max Speed: ${car.maxSpeed} km/h")
    car.start()

    val bike = Motorcycle(220)
    println("${bike.type} - Max Speed: ${bike.maxSpeed} km/h")
    bike.start()
}
```

---

## Default Interface Methods

Kotlin interfaces can have default implementations (unlike Java pre-8):

```kotlin
interface Logger {
    fun log(message: String) {
        println("[LOG] $message")  // Default implementation
    }

    fun error(message: String) {
        println("[ERROR] $message")  // Default implementation
    }

    fun debug(message: String)  // Must be implemented
}

class ConsoleLogger : Logger {
    override fun debug(message: String) {
        println("[DEBUG] $message")
    }
    // log() and error() use default implementations
}

class FileLogger : Logger {
    override fun log(message: String) {
        println("[FILE LOG] Writing to file: $message")
    }

    override fun error(message: String) {
        println("[FILE ERROR] Writing error to file: $message")
    }

    override fun debug(message: String) {
        println("[FILE DEBUG] Writing debug to file: $message")
    }
}

fun main() {
    val console = ConsoleLogger()
    console.log("Application started")
    console.error("Connection failed")
    console.debug("Variable value: 42")

    println()

    val file = FileLogger()
    file.log("Application started")
    file.error("Connection failed")
    file.debug("Variable value: 42")
}
```

---

## Abstract Classes vs Interfaces

### When to Use Abstract Classes

Use **abstract classes** when:
- You have shared **state** (properties with backing fields)
- You want to provide **common implementation** for subclasses
- You have a clear "is-a" relationship
- You need **constructors with parameters**

```kotlin
abstract class Animal(val name: String, var age: Int) {
    var energy: Int = 100  // State with backing field

    abstract fun makeSound()

    fun eat() {  // Common implementation
        energy += 20
        println("$name is eating. Energy: $energy")
    }

    fun sleep() {  // Common implementation
        energy = 100
        println("$name is sleeping. Energy restored!")
    }
}

class Dog(name: String, age: Int) : Animal(name, age) {
    override fun makeSound() {
        println("$name says: Woof!")
    }
}
```

### When to Use Interfaces

Use **interfaces** when:
- You want to define **capabilities** or **behaviors**
- You need **multiple inheritance** of type
- You don't need shared state
- You want loose coupling

```kotlin
interface Flyable {
    fun fly()
}

interface Swimmable {
    fun swim()
}

// A class can implement multiple interfaces
class Duck : Flyable, Swimmable {
    override fun fly() = println("Duck flying")
    override fun swim() = println("Duck swimming")
}
```

### Comparison Table

| Feature | Abstract Class | Interface |
|---------|---------------|-----------|
| State (backing fields) | ✅ Yes | ❌ No |
| Constructor | ✅ Yes | ❌ No |
| Multiple inheritance | ❌ No (single only) | ✅ Yes (multiple) |
| Default implementations | ✅ Yes | ✅ Yes (since Kotlin 1.0) |
| Access modifiers | ✅ Yes (public, protected, private) | ✅ Limited (public only) |
| When to use | "is-a" relationship | "can-do" capability |

---

## Real-World Example: E-Commerce System

```kotlin
// Interface for payment processing
interface PaymentProcessor {
    fun processPayment(amount: Double): Boolean
    fun refund(transactionId: String): Boolean

    fun validatePayment(amount: Double): Boolean {
        return amount > 0  // Default implementation
    }
}

// Interface for notification
interface Notifiable {
    fun sendNotification(message: String)
}

// Credit card payment
class CreditCardProcessor : PaymentProcessor {
    override fun processPayment(amount: Double): Boolean {
        if (!validatePayment(amount)) return false
        println("Processing credit card payment: $$amount")
        println("Payment successful!")
        return true
    }

    override fun refund(transactionId: String): Boolean {
        println("Refunding transaction: $transactionId")
        return true
    }
}

// PayPal payment
class PayPalProcessor : PaymentProcessor, Notifiable {
    override fun processPayment(amount: Double): Boolean {
        if (!validatePayment(amount)) return false
        println("Processing PayPal payment: $$amount")
        sendNotification("Payment processed via PayPal")
        return true
    }

    override fun refund(transactionId: String): Boolean {
        println("Refunding PayPal transaction: $transactionId")
        sendNotification("Refund processed")
        return true
    }

    override fun sendNotification(message: String) {
        println("📧 Email sent: $message")
    }
}

// Bitcoin payment
class BitcoinProcessor : PaymentProcessor, Notifiable {
    override fun processPayment(amount: Double): Boolean {
        if (!validatePayment(amount)) return false
        println("Processing Bitcoin payment: $$amount")
        println("Waiting for blockchain confirmation...")
        sendNotification("Bitcoin payment received")
        return true
    }

    override fun refund(transactionId: String): Boolean {
        println("Bitcoin refunds take 24-48 hours")
        return false
    }

    override fun sendNotification(message: String) {
        println("📱 Push notification: $message")
    }
}

fun checkout(processor: PaymentProcessor, amount: Double) {
    println("\n=== Checkout ===")
    val success = processor.processPayment(amount)

    if (success) {
        println("Order confirmed!")
    } else {
        println("Payment failed!")
    }
}

fun main() {
    val creditCard = CreditCardProcessor()
    val paypal = PayPalProcessor()
    val bitcoin = BitcoinProcessor()

    checkout(creditCard, 99.99)
    checkout(paypal, 149.99)
    checkout(bitcoin, 299.99)
}
```

---

## Exercise 1: Media Player System

**Goal**: Create a flexible media player system using interfaces.

**Requirements**:
1. Interface `Playable` with methods: `play()`, `pause()`, `stop()`
2. Interface `Downloadable` with method: `download()`
3. Class `Song` implements `Playable` and `Downloadable`
4. Class `Podcast` implements `Playable` and `Downloadable`
5. Class `LiveStream` implements only `Playable` (can't download)
6. Create a playlist that can hold any `Playable` item

---

## Solution: Media Player System

```kotlin
interface Playable {
    val title: String
    var isPlaying: Boolean

    fun play() {
        isPlaying = true
        println("▶️  Playing: $title")
    }

    fun pause() {
        isPlaying = false
        println("⏸️  Paused: $title")
    }

    fun stop() {
        isPlaying = false
        println("⏹️  Stopped: $title")
    }
}

interface Downloadable {
    val sizeInMB: Double

    fun download() {
        println("⬇️  Downloading... ($sizeInMB MB)")
        println("✅ Download complete!")
    }
}

class Song(
    override val title: String,
    val artist: String,
    override val sizeInMB: Double
) : Playable, Downloadable {
    override var isPlaying: Boolean = false

    override fun play() {
        println("🎵 Song")
        super.play()
        println("   Artist: $artist")
    }
}

class Podcast(
    override val title: String,
    val host: String,
    val episode: Int,
    override val sizeInMB: Double
) : Playable, Downloadable {
    override var isPlaying: Boolean = false

    override fun play() {
        println("🎙️  Podcast")
        super.play()
        println("   Host: $host, Episode: $episode")
    }
}

class LiveStream(
    override val title: String,
    val streamer: String
) : Playable {
    override var isPlaying: Boolean = false

    override fun play() {
        println("📡 Live Stream")
        super.play()
        println("   Streamer: $streamer")
    }
}

class MediaPlayer {
    private val playlist = mutableListOf<Playable>()
    private var currentIndex = 0

    fun addToPlaylist(item: Playable) {
        playlist.add(item)
        println("Added to playlist: ${item.title}")
    }

    fun playAll() {
        println("\n=== Playing All ===")
        playlist.forEach { it.play() }
    }

    fun downloadAll() {
        println("\n=== Downloading All (if possible) ===")
        playlist.forEach { item ->
            if (item is Downloadable) {
                item.download()
            } else {
                println("⚠️  ${item.title} cannot be downloaded (live stream)")
            }
        }
    }
}

fun main() {
    val player = MediaPlayer()

    val song1 = Song("Bohemian Rhapsody", "Queen", 5.8)
    val song2 = Song("Imagine", "John Lennon", 3.2)
    val podcast = Podcast("Tech Talk Daily", "Jane Doe", 42, 25.5)
    val stream = LiveStream("Gaming Night", "ProGamer123")

    player.addToPlaylist(song1)
    player.addToPlaylist(song2)
    player.addToPlaylist(podcast)
    player.addToPlaylist(stream)

    player.playAll()
    player.downloadAll()
}
```

---

## Exercise 2: Smart Home System

**Goal**: Create a smart home system with different device types.

**Requirements**:
1. Interface `SmartDevice` with properties: `name`, `isOn`, methods: `turnOn()`, `turnOff()`
2. Interface `Schedulable` with method: `schedule(time: String)`
3. Interface `VoiceControllable` with method: `respondToVoice(command: String)`
4. Class `SmartLight` implements all three interfaces
5. Class `SmartThermostat` implements `SmartDevice` and `Schedulable`
6. Class `SmartSpeaker` implements `SmartDevice` and `VoiceControllable`
7. Create a home controller that manages all devices

---

## Solution: Smart Home System

```kotlin
interface SmartDevice {
    val name: String
    var isOn: Boolean

    fun turnOn() {
        isOn = true
        println("✅ $name is now ON")
    }

    fun turnOff() {
        isOn = false
        println("❌ $name is now OFF")
    }

    fun getStatus(): String {
        return "$name: ${if (isOn) "ON" else "OFF"}"
    }
}

interface Schedulable {
    fun schedule(time: String)
}

interface VoiceControllable {
    fun respondToVoice(command: String)
}

class SmartLight(
    override val name: String,
    var brightness: Int = 100
) : SmartDevice, Schedulable, VoiceControllable {
    override var isOn: Boolean = false

    fun setBrightness(level: Int) {
        require(level in 0..100) { "Brightness must be 0-100" }
        brightness = level
        println("💡 $name brightness set to $level%")
    }

    override fun schedule(time: String) {
        println("⏰ $name scheduled to turn on at $time")
    }

    override fun respondToVoice(command: String) {
        when {
            "on" in command.lowercase() -> turnOn()
            "off" in command.lowercase() -> turnOff()
            "brightness" in command.lowercase() -> {
                val level = command.filter { it.isDigit() }.toIntOrNull() ?: 50
                setBrightness(level)
            }
            else -> println("🔊 $name: Command not understood")
        }
    }
}

class SmartThermostat(
    override val name: String,
    var temperature: Int = 72
) : SmartDevice, Schedulable {
    override var isOn: Boolean = false

    fun setTemperature(temp: Int) {
        require(temp in 60..85) { "Temperature must be 60-85°F" }
        temperature = temp
        println("🌡️  $name temperature set to $temp°F")
    }

    override fun schedule(time: String) {
        println("⏰ $name scheduled to set temperature at $time")
    }
}

class SmartSpeaker(
    override val name: String,
    var volume: Int = 50
) : SmartDevice, VoiceControllable {
    override var isOn: Boolean = false

    fun setVolume(level: Int) {
        require(level in 0..100) { "Volume must be 0-100" }
        volume = level
        println("🔊 $name volume set to $level")
    }

    override fun respondToVoice(command: String) {
        when {
            "play music" in command.lowercase() -> {
                if (isOn) println("🎵 Playing music...")
                else println("❌ Turn me on first!")
            }
            "volume" in command.lowercase() -> {
                val level = command.filter { it.isDigit() }.toIntOrNull() ?: 50
                setVolume(level)
            }
            else -> println("🔊 $name: I can play music or adjust volume")
        }
    }
}

class HomeController {
    private val devices = mutableListOf<SmartDevice>()

    fun addDevice(device: SmartDevice) {
        devices.add(device)
        println("➕ Added ${device.name} to home system")
    }

    fun turnAllOn() {
        println("\n=== Turning All Devices ON ===")
        devices.forEach { it.turnOn() }
    }

    fun turnAllOff() {
        println("\n=== Turning All Devices OFF ===")
        devices.forEach { it.turnOff() }
    }

    fun showStatus() {
        println("\n=== Home Status ===")
        devices.forEach { device ->
            println(device.getStatus())
        }
    }

    fun scheduleAll(time: String) {
        println("\n=== Scheduling Devices ===")
        devices.forEach { device ->
            if (device is Schedulable) {
                device.schedule(time)
            }
        }
    }

    fun voiceCommand(command: String) {
        println("\n=== Voice Command: '$command' ===")
        devices.forEach { device ->
            if (device is VoiceControllable) {
                device.respondToVoice(command)
            }
        }
    }
}

fun main() {
    val home = HomeController()

    val livingRoomLight = SmartLight("Living Room Light")
    val bedroomLight = SmartLight("Bedroom Light")
    val thermostat = SmartThermostat("Main Thermostat")
    val speaker = SmartSpeaker("Kitchen Speaker")

    home.addDevice(livingRoomLight)
    home.addDevice(bedroomLight)
    home.addDevice(thermostat)
    home.addDevice(speaker)

    home.turnAllOn()
    home.showStatus()

    home.scheduleAll("7:00 AM")

    home.voiceCommand("turn on")
    home.voiceCommand("set brightness to 75")
    home.voiceCommand("play music")

    home.turnAllOff()
    home.showStatus()
}
```

---

## Exercise 3: Plugin System

**Goal**: Create an extensible plugin system.

**Requirements**:
1. Interface `Plugin` with properties: `name`, `version`, methods: `initialize()`, `execute()`, `shutdown()`
2. Interface `Configurable` with method: `configure(settings: Map<String, String>)`
3. Create 3 different plugin types
4. Create a `PluginManager` that loads and manages plugins

---

## Solution: Plugin System

```kotlin
interface Plugin {
    val name: String
    val version: String

    fun initialize()
    fun execute()
    fun shutdown()
}

interface Configurable {
    fun configure(settings: Map<String, String>)
}

class LoggerPlugin : Plugin, Configurable {
    override val name = "Logger"
    override val version = "1.0.0"
    private var logLevel = "INFO"

    override fun initialize() {
        println("[$name] Initializing logger plugin...")
    }

    override fun execute() {
        println("[$name] Logging at level: $logLevel")
        println("[$name] Log entry: Application running smoothly")
    }

    override fun shutdown() {
        println("[$name] Shutting down logger...")
    }

    override fun configure(settings: Map<String, String>) {
        logLevel = settings["logLevel"] ?: "INFO"
        println("[$name] Configured with log level: $logLevel")
    }
}

class DatabasePlugin : Plugin, Configurable {
    override val name = "Database"
    override val version = "2.1.0"
    private var connectionString = ""

    override fun initialize() {
        println("[$name] Connecting to database...")
    }

    override fun execute() {
        println("[$name] Querying database at: $connectionString")
        println("[$name] Query result: 42 records found")
    }

    override fun shutdown() {
        println("[$name] Closing database connection...")
    }

    override fun configure(settings: Map<String, String>) {
        connectionString = settings["connectionString"] ?: "localhost:5432"
        println("[$name] Configured to connect to: $connectionString")
    }
}

class CachePlugin : Plugin {
    override val name = "Cache"
    override val version = "1.5.2"
    private val cache = mutableMapOf<String, String>()

    override fun initialize() {
        println("[$name] Initializing cache system...")
    }

    override fun execute() {
        cache["user:1"] = "Alice"
        cache["user:2"] = "Bob"
        println("[$name] Cache populated with ${cache.size} items")
    }

    override fun shutdown() {
        cache.clear()
        println("[$name] Cache cleared and shutdown")
    }
}

class PluginManager {
    private val plugins = mutableListOf<Plugin>()

    fun registerPlugin(plugin: Plugin) {
        plugins.add(plugin)
        println("\n✅ Registered plugin: ${plugin.name} v${plugin.version}")
    }

    fun configurePlugin(pluginName: String, settings: Map<String, String>) {
        val plugin = plugins.find { it.name == pluginName }
        if (plugin is Configurable) {
            plugin.configure(settings)
        } else {
            println("⚠️  Plugin '$pluginName' is not configurable")
        }
    }

    fun initializeAll() {
        println("\n=== Initializing All Plugins ===")
        plugins.forEach { it.initialize() }
    }

    fun executeAll() {
        println("\n=== Executing All Plugins ===")
        plugins.forEach { it.execute() }
    }

    fun shutdownAll() {
        println("\n=== Shutting Down All Plugins ===")
        plugins.forEach { it.shutdown() }
    }

    fun listPlugins() {
        println("\n=== Installed Plugins ===")
        plugins.forEach { plugin ->
            val configurable = if (plugin is Configurable) "(Configurable)" else ""
            println("${plugin.name} v${plugin.version} $configurable")
        }
    }
}

fun main() {
    val manager = PluginManager()

    // Register plugins
    val logger = LoggerPlugin()
    val database = DatabasePlugin()
    val cache = CachePlugin()

    manager.registerPlugin(logger)
    manager.registerPlugin(database)
    manager.registerPlugin(cache)

    manager.listPlugins()

    // Configure
    manager.configurePlugin("Logger", mapOf("logLevel" to "DEBUG"))
    manager.configurePlugin("Database", mapOf("connectionString" to "prod-db.example.com:5432"))

    // Run lifecycle
    manager.initializeAll()
    manager.executeAll()
    manager.shutdownAll()
}
```

---

## Checkpoint Quiz

### Question 1
What is the main difference between an interface and an abstract class?

A) Interfaces can't have methods
B) A class can implement multiple interfaces but inherit from only one abstract class
C) Abstract classes are faster
D) There is no difference

### Question 2
Can interfaces have properties with backing fields?

A) Yes, always
B) No, never
C) Only if marked `open`
D) Only if they're `lateinit`

### Question 3
Can interface methods have default implementations in Kotlin?

A) No, never
B) Yes, always
C) Yes, but not in Java
D) Yes, since Kotlin 1.0

### Question 4
When should you use an interface instead of an abstract class?

A) When you need constructors
B) When you need to define capabilities without shared state
C) When you need multiple inheritance of type
D) Both B and C

### Question 5
What's required for a class property declared in an interface?

A) It must have a backing field
B) It must be overridden by implementing classes (unless it has a default getter)
C) It must be mutable
D) It must be private

---

## Quiz Answers

**Question 1: B) A class can implement multiple interfaces but inherit from only one abstract class**

This is one of the key differences and a major reason to use interfaces.

```kotlin
interface Flyable { }
interface Swimmable { }

class Duck : Flyable, Swimmable { }  // ✅ Multiple interfaces

abstract class Animal { }
abstract class Vehicle { }
// class FlyingCar : Animal, Vehicle { }  // ❌ Can't inherit from two classes
```

---

**Question 2: B) No, never**

Interfaces can't have backing fields. Properties must either be abstract or have custom getters.

```kotlin
interface Example {
    val x: Int  // ✅ Abstract (must override)
    val y: Int
        get() = 42  // ✅ Custom getter
    // val z: Int = 10  // ❌ Backing field not allowed
}
```

---

**Question 3: D) Yes, since Kotlin 1.0**

Kotlin interfaces can have default method implementations from the start.

```kotlin
interface Logger {
    fun log(msg: String) {
        println("[LOG] $msg")  // ✅ Default implementation
    }
}
```

---

**Question 4: D) Both B and C**

Use interfaces when you want to define capabilities ("can-do") without shared state, and when you need multiple inheritance.

```kotlin
interface Printable { fun print() }
interface Scannable { fun scan() }

class AllInOnePrinter : Printable, Scannable {
    override fun print() { }
    override fun scan() { }
}
```

---

**Question 5: B) It must be overridden by implementing classes (unless it has a default getter)**

Interface properties without default getters must be overridden.

```kotlin
interface Vehicle {
    val speed: Int  // Must override
    val type: String
        get() = "Generic"  // Has default, override optional
}
```

---

## What You've Learned

✅ Defining and implementing interfaces
✅ Multiple interface implementation
✅ Interface properties (without backing fields)
✅ Default interface methods
✅ Interfaces vs abstract classes
✅ Real-world design patterns with interfaces

---

## Next Steps

In **Lesson 2.5: Data Classes and Sealed Classes**, you'll learn:
- Data classes for holding data
- Automatic `equals()`, `hashCode()`, `toString()`, `copy()`
- Destructuring declarations
- Sealed classes for restricted hierarchies
- When to use each special class type

You're building a complete OOP toolkit!

---

**Congratulations on completing Lesson 2.4!** 🎉

Interfaces are essential for designing flexible, maintainable systems. You now understand when to use interfaces vs abstract classes!

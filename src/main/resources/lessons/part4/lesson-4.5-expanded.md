# Lesson 4.5: Annotations and Reflection

**Estimated Time**: 70 minutes
**Difficulty**: Advanced
**Prerequisites**: Parts 1-3, Lesson 4.1 (Generics)

---

## Topic Introduction

Annotations and reflection are powerful metaprogramming tools that allow you to write code that examines and modifies other code at runtime. Annotations provide metadata about your code, while reflection lets you inspect and manipulate classes, functions, and properties dynamically.

These features are essential for building frameworks, libraries, serialization systems, dependency injection containers, and testing frameworks.

In this lesson, you'll learn:
- Built-in annotations (`@JvmName`, `@JvmStatic`, `@Deprecated`, etc.)
- Creating custom annotations
- Annotation targets and retention
- Reflection basics with `KClass`, `KFunction`, `KProperty`
- Inspecting classes and members at runtime
- Practical use cases and patterns

By the end, you'll build systems that adapt dynamically at runtime!

---

## The Concept: Metadata and Introspection

### Why Annotations?

Annotations attach metadata to code elements:

```kotlin
@Deprecated("Use newFunction() instead", ReplaceWith("newFunction()"))
fun oldFunction() {
    println("Old way")
}

fun newFunction() {
    println("New way")
}
```

### Why Reflection?

Reflection lets you inspect code structure at runtime:

```kotlin
data class User(val name: String, val age: Int)

fun main() {
    val user = User("Alice", 25)
    val kClass = user::class

    println("Class: ${kClass.simpleName}")
    println("Properties:")
    kClass.memberProperties.forEach { prop ->
        println("  ${prop.name} = ${prop.get(user)}")
    }
}
// Output:
// Class: User
// Properties:
//   age = 25
//   name = Alice
```

---

## Built-in Annotations

Kotlin provides several useful annotations.

### @Deprecated

Mark code as deprecated with migration hints:

```kotlin
@Deprecated(
    message = "Use calculateTotal() instead",
    replaceWith = ReplaceWith("calculateTotal(items)"),
    level = DeprecationLevel.WARNING
)
fun calculate(items: List<Int>): Int {
    return items.sum()
}

fun calculateTotal(items: List<Int>): Int {
    return items.sum()
}

fun main() {
    val items = listOf(1, 2, 3)
    calculate(items)  // ⚠️ Warning in IDE
    calculateTotal(items)  // ✅ No warning
}
```

**Deprecation Levels**:
- `WARNING` - shows warning (default)
- `ERROR` - compilation error
- `HIDDEN` - not visible to code

### @Suppress

Suppress compiler warnings:

```kotlin
@Suppress("UNCHECKED_CAST", "UNUSED_PARAMETER")
fun example(param: Any): String {
    val unused = "not used"
    return param as String
}
```

### JVM Interoperability Annotations

#### @JvmName

Change the JVM name of a function:

```kotlin
@JvmName("calculateSum")
fun sum(numbers: List<Int>): Int {
    return numbers.sum()
}

// In Java: calculateSum(list)
```

#### @JvmStatic

Generate static method for companion object:

```kotlin
class Utils {
    companion object {
        @JvmStatic
        fun format(text: String): String {
            return text.uppercase()
        }

        fun helper() = "Helper"
    }
}

// In Java:
// Utils.format("hello")  // ✅ Works (static)
// Utils.helper()         // ❌ Doesn't work (not static)
// Utils.Companion.helper()  // ✅ Works
```

#### @JvmField

Expose property as public field (no getter/setter):

```kotlin
class Config {
    @JvmField
    var timeout: Int = 5000

    var retries: Int = 3
}

// In Java:
// config.timeout (direct field access)
// config.getRetries() (getter method)
```

#### @JvmOverloads

Generate overloaded methods for default parameters:

```kotlin
class Greeter {
    @JvmOverloads
    fun greet(name: String, greeting: String = "Hello", punctuation: String = "!") {
        println("$greeting, $name$punctuation")
    }
}

// Generates in Java:
// greet(String name)
// greet(String name, String greeting)
// greet(String name, String greeting, String punctuation)
```

### @Throws

Declare checked exceptions (for Java interop):

```kotlin
import java.io.IOException

@Throws(IOException::class)
fun readFile(path: String): String {
    throw IOException("File not found")
}

// In Java, this is a checked exception
```

---

## Creating Custom Annotations

### Basic Annotation

```kotlin
annotation class Important

@Important
fun criticalFunction() {
    println("This is important!")
}

@Important
class CriticalClass
```

### Annotations with Parameters

```kotlin
annotation class Author(
    val name: String,
    val date: String
)

@Author(name = "Alice", date = "2024-01-15")
fun featureFunction() {
    println("Feature implementation")
}
```

### Annotation with Multiple Parameters

```kotlin
annotation class Route(
    val path: String,
    val method: String = "GET",
    val requiresAuth: Boolean = false
)

@Route("/users", method = "GET", requiresAuth = true)
fun getUsers() {
    println("Fetching users")
}

@Route("/users", method = "POST")
fun createUser() {
    println("Creating user")
}
```

---

## Annotation Targets

Specify where an annotation can be used:

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Audited

@Target(AnnotationTarget.PROPERTY)
annotation class Required

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class NotBlank

@Audited  // ✅ OK on class
class Service {
    @Required  // ✅ OK on property
    val name: String = ""

    @Audited  // ✅ OK on function
    fun process(@NotBlank input: String) {  // ✅ OK on parameter
        println(input)
    }
}
```

**Common Targets**:
- `CLASS` - classes, interfaces, objects
- `FUNCTION` - functions
- `PROPERTY` - properties
- `FIELD` - backing fields
- `VALUE_PARAMETER` - function parameters
- `CONSTRUCTOR` - constructors
- `EXPRESSION` - expressions
- `FILE` - file

### Use-Site Targets

Specify exactly which part to annotate:

```kotlin
class Example(
    @field:Required val name: String,  // Annotate the backing field
    @get:Required val age: Int,        // Annotate the getter
    @param:NotBlank val email: String  // Annotate constructor parameter
)
```

---

## Annotation Retention

Control when annotations are available:

```kotlin
@Retention(AnnotationRetention.SOURCE)
annotation class CompileTimeOnly

@Retention(AnnotationRetention.BINARY)
annotation class InBinary

@Retention(AnnotationRetention.RUNTIME)
annotation class InRuntime
```

**Retention Policies**:
- `SOURCE` - discarded after compilation (e.g., `@Suppress`)
- `BINARY` - stored in binary but not available via reflection
- `RUNTIME` - available at runtime via reflection (default)

---

## Reflection Basics

Reflection allows inspecting and manipulating code at runtime.

### Getting Class References

```kotlin
// From instance
val user = User("Alice", 25)
val kClass1 = user::class

// From class
val kClass2 = User::class

// From Java class
val javaClass = User::class.java
val kClass3 = javaClass.kotlin

println(kClass1.simpleName)  // User
println(kClass1.qualifiedName)  // com.example.User
```

### KClass - Class Metadata

```kotlin
import kotlin.reflect.full.*

data class Person(val name: String, val age: Int) {
    fun greet() = "Hello, I'm $name"
}

fun main() {
    val kClass = Person::class

    println("Simple name: ${kClass.simpleName}")
    println("Qualified name: ${kClass.qualifiedName}")
    println("Is data class: ${kClass.isData}")
    println("Is final: ${kClass.isFinal}")

    println("\nConstructors:")
    kClass.constructors.forEach { constructor ->
        println("  Parameters: ${constructor.parameters.map { it.name }}")
    }

    println("\nMember properties:")
    kClass.memberProperties.forEach { prop ->
        println("  ${prop.name}: ${prop.returnType}")
    }

    println("\nMember functions:")
    kClass.memberFunctions.forEach { func ->
        println("  ${func.name}")
    }
}
```

### KProperty - Property Reflection

```kotlin
import kotlin.reflect.full.*

class Settings {
    var theme: String = "light"
    var fontSize: Int = 14
    val isModified: Boolean = false
}

fun main() {
    val settings = Settings()
    val kClass = Settings::class

    kClass.memberProperties.forEach { prop ->
        println("Property: ${prop.name}")
        println("  Type: ${prop.returnType}")
        println("  Is mutable: ${prop is kotlin.reflect.KMutableProperty<*>}")

        // Get value
        val value = prop.get(settings)
        println("  Value: $value")

        // Set value (if mutable)
        if (prop is kotlin.reflect.KMutableProperty<*>) {
            when (prop.name) {
                "theme" -> prop.setter.call(settings, "dark")
                "fontSize" -> prop.setter.call(settings, 16)
            }
        }
    }

    println("\nAfter modification:")
    println("Theme: ${settings.theme}")
    println("Font size: ${settings.fontSize}")
}
```

### KFunction - Function Reflection

```kotlin
import kotlin.reflect.full.*

class Calculator {
    fun add(a: Int, b: Int): Int = a + b

    fun multiply(a: Int, b: Int, c: Int = 1): Int = a * b * c
}

fun main() {
    val calc = Calculator()
    val kClass = Calculator::class

    val addFunction = kClass.memberFunctions.find { it.name == "add" }!!

    println("Function: ${addFunction.name}")
    println("Parameters: ${addFunction.parameters.map { it.name }}")
    println("Return type: ${addFunction.returnType}")

    // Call function
    val result = addFunction.call(calc, 5, 3)
    println("Result: $result")  // 8

    // Call with named parameters
    val multiplyFunction = kClass.memberFunctions.find { it.name == "multiply" }!!
    val result2 = multiplyFunction.callBy(
        mapOf(
            multiplyFunction.parameters[0] to calc,  // instance
            multiplyFunction.parameters[1] to 2,      // a
            multiplyFunction.parameters[2] to 3       // b (c uses default)
        )
    )
    println("Multiply result: $result2")  // 6
}
```

---

## Reading Annotations at Runtime

```kotlin
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Validate(val min: Int = 0, val max: Int = 100)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Entity(val tableName: String)

@Entity(tableName = "users")
data class User(
    val name: String,

    @Validate(min = 18, max = 120)
    val age: Int
)

fun main() {
    val kClass = User::class

    // Read class annotation
    val entityAnnotation = kClass.annotations.find { it is Entity } as? Entity
    println("Table name: ${entityAnnotation?.tableName}")

    // Read property annotations
    kClass.memberProperties.forEach { prop ->
        val validateAnnotation = prop.annotations.find { it is Validate } as? Validate
        if (validateAnnotation != null) {
            println("${prop.name}: min=${validateAnnotation.min}, max=${validateAnnotation.max}")
        }
    }
}
```

### Finding Annotated Members

```kotlin
import kotlin.reflect.full.*

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Test

class TestSuite {
    @Test
    fun test1() = println("Running test 1")

    @Test
    fun test2() = println("Running test 2")

    fun helper() = println("Helper function")
}

fun main() {
    val testSuite = TestSuite()
    val kClass = TestSuite::class

    val testFunctions = kClass.memberFunctions.filter { function ->
        function.annotations.any { it is Test }
    }

    println("Running ${testFunctions.size} tests:")
    testFunctions.forEach { function ->
        function.call(testSuite)
    }
}
// Output:
// Running 2 tests:
// Running test 1
// Running test 2
```

---

## Practical Use Cases

### Use Case 1: Simple Validation Framework

```kotlin
import kotlin.reflect.full.*

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Min(val value: Int)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Max(val value: Int)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class NotBlank

data class UserRegistration(
    @NotBlank
    val username: String,

    @NotBlank
    val email: String,

    @Min(18) @Max(120)
    val age: Int,

    @Min(8)
    val passwordLength: Int
)

object Validator {
    fun validate(obj: Any): List<String> {
        val errors = mutableListOf<String>()
        val kClass = obj::class

        kClass.memberProperties.forEach { prop ->
            val value = prop.get(obj)

            // Check @NotBlank
            if (prop.annotations.any { it is NotBlank }) {
                if (value is String && value.isBlank()) {
                    errors.add("${prop.name} cannot be blank")
                }
            }

            // Check @Min
            prop.annotations.filterIsInstance<Min>().forEach { min ->
                if (value is Int && value < min.value) {
                    errors.add("${prop.name} must be at least ${min.value}")
                }
            }

            // Check @Max
            prop.annotations.filterIsInstance<Max>().forEach { max ->
                if (value is Int && value > max.value) {
                    errors.add("${prop.name} must be at most ${max.value}")
                }
            }
        }

        return errors
    }
}

fun main() {
    val validUser = UserRegistration("alice", "alice@example.com", 25, 10)
    println("Valid user errors: ${Validator.validate(validUser)}")

    val invalidUser = UserRegistration("", "email@example.com", 15, 6)
    println("Invalid user errors:")
    Validator.validate(invalidUser).forEach { println("  - $it") }
}
```

### Use Case 2: Simple Serialization

```kotlin
import kotlin.reflect.full.*

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class JsonField(val name: String)

data class Product(
    @JsonField("product_id")
    val id: Int,

    @JsonField("product_name")
    val name: String,

    val price: Double  // Uses property name
)

object SimpleJsonSerializer {
    fun toJson(obj: Any): String {
        val kClass = obj::class
        val properties = kClass.memberProperties

        val fields = properties.map { prop ->
            val jsonName = prop.annotations
                .filterIsInstance<JsonField>()
                .firstOrNull()?.name
                ?: prop.name

            val value = prop.get(obj)
            val jsonValue = when (value) {
                is String -> "\"$value\""
                else -> value.toString()
            }

            "\"$jsonName\": $jsonValue"
        }

        return "{ ${fields.joinToString(", ")} }"
    }
}

fun main() {
    val product = Product(1, "Laptop", 999.99)
    val json = SimpleJsonSerializer.toJson(product)
    println(json)
    // { "product_id": 1, "product_name": "Laptop", "price": 999.99 }
}
```

### Use Case 3: Dependency Injection Container

```kotlin
import kotlin.reflect.full.*

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Inject

class Database {
    fun query(sql: String) = "Result for: $sql"
}

class UserRepository {
    @Inject
    lateinit var database: Database

    fun findUser(id: Int): String {
        return database.query("SELECT * FROM users WHERE id = $id")
    }
}

class Container {
    private val instances = mutableMapOf<kotlin.reflect.KClass<*>, Any>()

    fun <T : Any> register(kClass: kotlin.reflect.KClass<T>, instance: T) {
        instances[kClass] = instance
    }

    fun <T : Any> get(kClass: kotlin.reflect.KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return instances[kClass] as T
    }

    fun <T : Any> inject(obj: T) {
        val kClass = obj::class

        kClass.memberProperties.forEach { prop ->
            if (prop.annotations.any { it is Inject }) {
                if (prop is kotlin.reflect.KMutableProperty<*>) {
                    val dependency = instances[prop.returnType.classifier as kotlin.reflect.KClass<*>]
                    if (dependency != null) {
                        prop.setter.call(obj, dependency)
                    }
                }
            }
        }
    }
}

fun main() {
    val container = Container()
    container.register(Database::class, Database())

    val repository = UserRepository()
    container.inject(repository)

    println(repository.findUser(1))
    // Result for: SELECT * FROM users WHERE id = 1
}
```

---

## Exercises

### Exercise 1: Test Runner (Medium)

Create a simple test runner using annotations.

**Requirements**:
- `@Test` for test methods
- `@BeforeEach` for setup
- `@AfterEach` for cleanup
- Run all tests and report results

**Solution**:

```kotlin
import kotlin.reflect.full.*

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Test

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class BeforeEach

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AfterEach

class TestRunner {
    fun run(testClass: Any) {
        val kClass = testClass::class

        val beforeEach = kClass.memberFunctions.find { it.annotations.any { a -> a is BeforeEach } }
        val afterEach = kClass.memberFunctions.find { it.annotations.any { a -> a is AfterEach } }
        val tests = kClass.memberFunctions.filter { it.annotations.any { a -> a is Test } }

        var passed = 0
        var failed = 0

        println("Running ${tests.size} tests:\n")

        tests.forEach { test ->
            try {
                beforeEach?.call(testClass)
                test.call(testClass)
                afterEach?.call(testClass)

                println("✅ ${test.name} - PASSED")
                passed++
            } catch (e: Exception) {
                println("❌ ${test.name} - FAILED: ${e.message}")
                failed++
            }
        }

        println("\n$passed passed, $failed failed")
    }
}

class CalculatorTests {
    private var calculator: Calculator? = null

    @BeforeEach
    fun setup() {
        calculator = Calculator()
        println("  [Setup]")
    }

    @AfterEach
    fun cleanup() {
        calculator = null
        println("  [Cleanup]")
    }

    @Test
    fun testAdd() {
        val result = calculator!!.add(2, 3)
        if (result != 5) throw AssertionError("Expected 5, got $result")
    }

    @Test
    fun testMultiply() {
        val result = calculator!!.multiply(2, 3)
        if (result != 6) throw AssertionError("Expected 6, got $result")
    }

    @Test
    fun testFailing() {
        throw AssertionError("This test always fails")
    }
}

class Calculator {
    fun add(a: Int, b: Int) = a + b
    fun multiply(a: Int, b: Int) = a * b
}

fun main() {
    val runner = TestRunner()
    runner.run(CalculatorTests())
}
```

### Exercise 2: Query Builder (Hard)

Create a query builder using annotations and reflection.

**Requirements**:
- `@Table` for table name
- `@Column` for column mapping
- Generate SELECT, INSERT queries

**Solution**:

```kotlin
import kotlin.reflect.full.*

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Table(val name: String)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(val name: String)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PrimaryKey

@Table("users")
data class User(
    @PrimaryKey
    @Column("user_id")
    val id: Int,

    @Column("user_name")
    val name: String,

    @Column("user_email")
    val email: String
)

object QueryBuilder {
    fun <T : Any> selectAll(kClass: kotlin.reflect.KClass<T>): String {
        val table = kClass.annotations.filterIsInstance<Table>().first().name

        val columns = kClass.memberProperties.map { prop ->
            prop.annotations.filterIsInstance<Column>().firstOrNull()?.name ?: prop.name
        }

        return "SELECT ${columns.joinToString(", ")} FROM $table"
    }

    fun <T : Any> selectById(kClass: kotlin.reflect.KClass<T>, id: Any): String {
        val table = kClass.annotations.filterIsInstance<Table>().first().name

        val pkProp = kClass.memberProperties.find { prop ->
            prop.annotations.any { it is PrimaryKey }
        }!!

        val pkColumn = pkProp.annotations.filterIsInstance<Column>().first().name

        return "SELECT * FROM $table WHERE $pkColumn = $id"
    }

    fun insert(obj: Any): String {
        val kClass = obj::class
        val table = kClass.annotations.filterIsInstance<Table>().first().name

        val columns = mutableListOf<String>()
        val values = mutableListOf<String>()

        kClass.memberProperties.forEach { prop ->
            val columnName = prop.annotations.filterIsInstance<Column>().firstOrNull()?.name
                ?: prop.name

            val value = prop.get(obj)
            val valueStr = when (value) {
                is String -> "'$value'"
                else -> value.toString()
            }

            columns.add(columnName)
            values.add(valueStr)
        }

        return "INSERT INTO $table (${columns.joinToString(", ")}) VALUES (${values.joinToString(", ")})"
    }
}

fun main() {
    println(QueryBuilder.selectAll(User::class))
    // SELECT user_id, user_name, user_email FROM users

    println(QueryBuilder.selectById(User::class, 1))
    // SELECT * FROM users WHERE user_id = 1

    val user = User(1, "Alice", "alice@example.com")
    println(QueryBuilder.insert(user))
    // INSERT INTO users (user_id, user_name, user_email) VALUES (1, 'Alice', 'alice@example.com')
}
```

### Exercise 3: Object Mapper (Hard)

Create an object mapper that converts between objects and maps.

**Requirements**:
- Convert object to Map<String, Any?>
- Convert Map<String, Any?> to object
- Support custom field names
- Handle nested objects

**Solution**:

```kotlin
import kotlin.reflect.full.*
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Field(val name: String = "")

data class Address(
    @Field("street_name")
    val street: String,

    val city: String
)

data class Person(
    @Field("full_name")
    val name: String,

    val age: Int,

    val address: Address
)

object ObjectMapper {
    fun toMap(obj: Any): Map<String, Any?> {
        val kClass = obj::class
        val map = mutableMapOf<String, Any?>()

        kClass.memberProperties.forEach { prop ->
            val fieldName = prop.annotations.filterIsInstance<Field>().firstOrNull()?.name?.takeIf { it.isNotEmpty() }
                ?: prop.name

            val value = prop.get(obj)

            map[fieldName] = when {
                value == null -> null
                isPrimitive(value) -> value
                else -> toMap(value)  // Nested object
            }
        }

        return map
    }

    fun <T : Any> fromMap(map: Map<String, Any?>, kClass: KClass<T>): T {
        val constructor = kClass.constructors.first()
        val args = constructor.parameters.associateWith { param ->
            val prop = kClass.memberProperties.find { it.name == param.name }

            val fieldName = prop?.annotations?.filterIsInstance<Field>()?.firstOrNull()?.name?.takeIf { it.isNotEmpty() }
                ?: param.name

            val value = map[fieldName]

            when {
                value == null -> null
                param.type.classifier == String::class -> value.toString()
                param.type.classifier == Int::class -> (value as? Number)?.toInt()
                else -> {
                    // Nested object
                    @Suppress("UNCHECKED_CAST")
                    fromMap(value as Map<String, Any?>, param.type.classifier as KClass<Any>)
                }
            }
        }

        return constructor.callBy(args)
    }

    private fun isPrimitive(value: Any): Boolean {
        return value is String || value is Number || value is Boolean
    }
}

fun main() {
    val person = Person(
        name = "Alice",
        age = 30,
        address = Address("123 Main St", "Springfield")
    )

    val map = ObjectMapper.toMap(person)
    println("To Map:")
    println(map)
    // {full_name=Alice, age=30, address={street_name=123 Main St, city=Springfield}}

    val restored = ObjectMapper.fromMap(map, Person::class)
    println("\nFrom Map:")
    println(restored)
    // Person(name=Alice, age=30, address=Address(street=123 Main St, city=Springfield))
}
```

---

## Checkpoint Quiz

### Question 1: Annotation Retention

What does `@Retention(AnnotationRetention.RUNTIME)` mean?

**A)** Annotation is discarded after compilation
**B)** Annotation is available at runtime via reflection
**C)** Annotation only works at compile time
**D)** Annotation is stored in source code only

**Answer**: **B** - `RUNTIME` retention makes annotations available at runtime for reflection.

---

### Question 2: KClass

How do you get a KClass reference from an instance?

**A)** `instance.class`
**B)** `instance::class`
**C)** `instance.getClass()`
**D)** `classOf(instance)`

**Answer**: **B** - Use `instance::class` to get KClass from an instance.

---

### Question 3: @JvmStatic

What does `@JvmStatic` do?

**A)** Makes a property immutable
**B)** Generates a static method for Java interop
**C)** Prevents inheritance
**D)** Makes a class final

**Answer**: **B** - `@JvmStatic` generates a static method in the companion object for Java interoperability.

---

### Question 4: Reflection Performance

What's a disadvantage of reflection?

**A)** It's type-safe
**B)** It's slower than direct access
**C)** It can't access private members
**D)** It only works with data classes

**Answer**: **B** - Reflection is slower than direct access because it involves runtime type checking and dynamic invocation.

---

### Question 5: Annotation Targets

Which target allows annotating a property's backing field?

**A)** `@field:`
**B)** `@property:`
**C)** `@get:`
**D)** `@param:`

**Answer**: **A** - Use `@field:` to annotate the backing field of a property.

---

## Summary

Congratulations! You've mastered annotations and reflection in Kotlin. Here's what you learned:

✅ **Built-in Annotations** - `@Deprecated`, `@JvmStatic`, `@JvmOverloads`, etc.
✅ **Custom Annotations** - Creating annotations with parameters
✅ **Annotation Targets** - Controlling where annotations can be used
✅ **Retention Policies** - SOURCE, BINARY, RUNTIME
✅ **Reflection** - `KClass`, `KFunction`, `KProperty`
✅ **Practical Uses** - Validation, serialization, dependency injection

### Key Takeaways

1. **Annotations** provide metadata for code elements
2. **`@Retention(RUNTIME)`** needed for reflection access
3. **`@Target`** controls where annotations apply
4. **Reflection** enables dynamic code inspection
5. **Use sparingly** - reflection has performance overhead

### Next Steps

In the next lesson, we'll explore **DSLs and Type-Safe Builders** - creating beautiful, type-safe domain-specific languages in Kotlin!

---

**Practice Challenge**: Build a configuration validator that reads annotations and validates configuration objects, generating detailed error reports with field names and constraints.

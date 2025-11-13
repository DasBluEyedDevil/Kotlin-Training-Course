# Lesson 2.7: Part 2 Capstone - Library Management System

**Estimated Time**: 3-4 hours

---

## Project Overview

Congratulations on completing all the lessons in Part 2! You've learned the fundamentals of Object-Oriented Programming in Kotlin:

- ✅ Classes, objects, properties, and methods
- ✅ Constructors and initialization
- ✅ Inheritance and polymorphism
- ✅ Interfaces and abstract classes
- ✅ Data classes and sealed classes
- ✅ Object declarations and companion objects

Now it's time to put it all together in a **comprehensive capstone project**: a **Library Management System**.

This project will challenge you to apply all OOP concepts in a real-world scenario where you manage books, members, loans, and library operations.

---

## The Project: LibraryHub

**LibraryHub** is a complete library management system that allows:
- Managing different types of books (physical and digital)
- Registering library members
- Borrowing and returning books
- Reserving books that are currently unavailable
- Searching and filtering books
- Tracking loan history
- Managing late fees

---

## Requirements

### 1. Book Management

**Abstract Class: `Book`**
- Properties: `isbn`, `title`, `author`, `publishYear`, `status`
- Abstract method: `getDisplayInfo()`
- Method: `isAvailable()`

**Classes**:
- `PhysicalBook` extends `Book`
  - Additional properties: `shelfLocation`, `condition` (New, Good, Fair, Poor)
  - Implements `getDisplayInfo()`

- `DigitalBook` extends `Book`
  - Additional properties: `fileSize` (MB), `format` (PDF, EPUB, MOBI)
  - Method: `download()`
  - Implements `getDisplayInfo()`

**Book Status** (Sealed Class):
- `Available`
- `Borrowed(memberId, dueDate)`
- `Reserved(memberId)`
- `Maintenance`

### 2. Member Management

**Data Class: `Member`**
- Properties: `memberId`, `name`, `email`, `membershipType`, `joinDate`
- Method: `canBorrow()` - checks if member can borrow more books

**Membership Types** (Enum):
- `BASIC` - Can borrow 3 books
- `PREMIUM` - Can borrow 5 books
- `STUDENT` - Can borrow 4 books with discounted fees

### 3. Loan System

**Data Class: `Loan`**
- Properties: `loanId`, `book`, `member`, `borrowDate`, `dueDate`, `returnDate`, `lateFee`
- Method: `isOverdue()` - checks if loan is past due date
- Method: `calculateLateFee()` - calculates fee based on days overdue

**Interface: `Borrowable`**
- Methods: `borrow(member)`, `returnBook()`

**Interface: `Reservable`**
- Methods: `reserve(member)`, `cancelReservation()`

### 4. Library Manager

**Object: `Library`**
- Manages all books, members, and loans
- Methods:
  - `addBook(book)`
  - `removeBook(isbn)`
  - `registerMember(member)`
  - `borrowBook(isbn, memberId)`
  - `returnBook(isbn)`
  - `reserveBook(isbn, memberId)`
  - `searchBooks(query)` - search by title or author
  - `getOverdueLoans()`
  - `getMemberHistory(memberId)`
  - `printStatistics()`

---

## Step-by-Step Implementation

### Phase 1: Book Status and Types (30 minutes)

Let's start by defining our book status and types:

```kotlin
// BookStatus.kt
sealed class BookStatus {
    object Available : BookStatus()
    data class Borrowed(val memberId: String, val dueDate: String) : BookStatus()
    data class Reserved(val memberId: String) : BookStatus()
    object Maintenance : BookStatus()

    fun getDescription(): String = when (this) {
        is Available -> "Available"
        is Borrowed -> "Borrowed by $memberId, due $dueDate"
        is Reserved -> "Reserved by $memberId"
        is Maintenance -> "Under maintenance"
    }
}

// BookCondition.kt
enum class BookCondition {
    NEW, GOOD, FAIR, POOR
}

// FileFormat.kt
enum class FileFormat(val extension: String) {
    PDF("pdf"),
    EPUB("epub"),
    MOBI("mobi")
}

// MembershipType.kt
enum class MembershipType(val maxBooks: Int, val lateFeePerDay: Double) {
    BASIC(3, 1.0),
    PREMIUM(5, 0.5),
    STUDENT(4, 0.75)
}
```

### Phase 2: Book Classes (45 minutes)

```kotlin
// Book.kt
abstract class Book(
    val isbn: String,
    val title: String,
    val author: String,
    val publishYear: Int
) {
    var status: BookStatus = BookStatus.Available

    abstract fun getDisplayInfo(): String

    fun isAvailable(): Boolean = status is BookStatus.Available

    fun markAsBorrowed(memberId: String, dueDate: String) {
        status = BookStatus.Borrowed(memberId, dueDate)
    }

    fun markAsReturned() {
        status = BookStatus.Available
    }

    fun reserve(memberId: String) {
        if (status is BookStatus.Borrowed) {
            status = BookStatus.Reserved(memberId)
        }
    }

    override fun toString(): String = getDisplayInfo()
}

// PhysicalBook.kt
class PhysicalBook(
    isbn: String,
    title: String,
    author: String,
    publishYear: Int,
    val shelfLocation: String,
    var condition: BookCondition = BookCondition.GOOD
) : Book(isbn, title, author, publishYear) {

    override fun getDisplayInfo(): String {
        return """
            📚 Physical Book
            ISBN: $isbn
            Title: $title
            Author: $author
            Year: $publishYear
            Location: $shelfLocation
            Condition: $condition
            Status: ${status.getDescription()}
        """.trimIndent()
    }
}

// DigitalBook.kt
class DigitalBook(
    isbn: String,
    title: String,
    author: String,
    publishYear: Int,
    val fileSize: Double,
    val format: FileFormat
) : Book(isbn, title, author, publishYear) {

    override fun getDisplayInfo(): String {
        return """
            💾 Digital Book
            ISBN: $isbn
            Title: $title
            Author: $author
            Year: $publishYear
            File Size: ${fileSize}MB
            Format: ${format.extension}
            Status: ${status.getDescription()}
        """.trimIndent()
    }

    fun download() {
        println("📥 Downloading $title (${fileSize}MB, ${format.extension})...")
        println("✅ Download complete!")
    }
}
```

### Phase 3: Member and Loan (30 minutes)

```kotlin
// Member.kt
data class Member(
    val memberId: String,
    val name: String,
    val email: String,
    val membershipType: MembershipType,
    val joinDate: String
) {
    private var currentBorrowedCount = 0

    fun canBorrow(): Boolean {
        return currentBorrowedCount < membershipType.maxBooks
    }

    fun incrementBorrowCount() {
        currentBorrowedCount++
    }

    fun decrementBorrowCount() {
        if (currentBorrowedCount > 0) currentBorrowedCount--
    }

    fun getBorrowedCount() = currentBorrowedCount

    fun display() {
        println("""
            👤 Member: $name
            ID: $memberId
            Email: $email
            Membership: $membershipType
            Books Borrowed: $currentBorrowedCount/${membershipType.maxBooks}
            Joined: $joinDate
        """.trimIndent())
    }
}

// Loan.kt
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Loan(
    val loanId: String,
    val book: Book,
    val member: Member,
    val borrowDate: LocalDate,
    val dueDate: LocalDate,
    var returnDate: LocalDate? = null
) {
    fun isOverdue(): Boolean {
        val checkDate = returnDate ?: LocalDate.now()
        return checkDate.isAfter(dueDate)
    }

    fun calculateLateFee(): Double {
        if (!isOverdue()) return 0.0

        val checkDate = returnDate ?: LocalDate.now()
        val daysOverdue = ChronoUnit.DAYS.between(dueDate, checkDate)
        return daysOverdue * member.membershipType.lateFeePerDay
    }

    fun getDaysUntilDue(): Long {
        return ChronoUnit.DAYS.between(LocalDate.now(), dueDate)
    }

    fun display() {
        val status = if (returnDate != null) "Returned" else "Active"
        val fee = if (isOverdue()) " | Late Fee: $${String.format("%.2f", calculateLateFee())}" else ""

        println("""
            📋 Loan $loanId [$status]
            Book: ${book.title}
            Member: ${member.name}
            Borrowed: $borrowDate
            Due: $dueDate
            Returned: ${returnDate ?: "Not yet"}$fee
        """.trimIndent())
    }
}
```

### Phase 4: Library Manager (60 minutes)

```kotlin
// Library.kt
import java.time.LocalDate

object Library {
    private val books = mutableMapOf<String, Book>()
    private val members = mutableMapOf<String, Member>()
    private val loans = mutableListOf<Loan>()
    private var nextLoanId = 1

    init {
        println("🏛️  LibraryHub System Initialized")
    }

    // Book Management
    fun addBook(book: Book) {
        if (books.containsKey(book.isbn)) {
            println("❌ Book with ISBN ${book.isbn} already exists")
            return
        }
        books[book.isbn] = book
        println("✅ Added: ${book.title}")
    }

    fun removeBook(isbn: String): Boolean {
        val book = books.remove(isbn)
        return if (book != null) {
            println("🗑️  Removed: ${book.title}")
            true
        } else {
            println("❌ Book not found: $isbn")
            false
        }
    }

    fun getBook(isbn: String): Book? = books[isbn]

    // Member Management
    fun registerMember(member: Member) {
        if (members.containsKey(member.memberId)) {
            println("❌ Member with ID ${member.memberId} already exists")
            return
        }
        members[member.memberId] = member
        println("✅ Registered: ${member.name}")
    }

    fun getMember(memberId: String): Member? = members[memberId]

    // Borrowing System
    fun borrowBook(isbn: String, memberId: String): Boolean {
        val book = books[isbn]
        val member = members[memberId]

        if (book == null) {
            println("❌ Book not found: $isbn")
            return false
        }

        if (member == null) {
            println("❌ Member not found: $memberId")
            return false
        }

        if (!book.isAvailable()) {
            println("❌ Book is not available: ${book.title}")
            return false
        }

        if (!member.canBorrow()) {
            println("❌ ${member.name} has reached the borrowing limit")
            return false
        }

        val borrowDate = LocalDate.now()
        val dueDate = borrowDate.plusWeeks(2)  // 2-week loan period

        book.markAsBorrowed(memberId, dueDate.toString())
        member.incrementBorrowCount()

        val loan = Loan(
            loanId = "LOAN-${String.format("%04d", nextLoanId++)}",
            book = book,
            member = member,
            borrowDate = borrowDate,
            dueDate = dueDate
        )
        loans.add(loan)

        println("✅ ${member.name} borrowed '${book.title}'")
        println("   Due date: $dueDate")
        return true
    }

    fun returnBook(isbn: String): Boolean {
        val book = books[isbn]

        if (book == null) {
            println("❌ Book not found: $isbn")
            return false
        }

        val activeLoan = loans.find { it.book.isbn == isbn && it.returnDate == null }

        if (activeLoan == null) {
            println("❌ No active loan found for: ${book.title}")
            return false
        }

        activeLoan.returnDate = LocalDate.now()
        book.markAsReturned()
        activeLoan.member.decrementBorrowCount()

        println("✅ ${activeLoan.member.name} returned '${book.title}'")

        if (activeLoan.isOverdue()) {
            val fee = activeLoan.calculateLateFee()
            println("⚠️  Book was overdue! Late fee: $${String.format("%.2f", fee)}")
        }

        return true
    }

    // Search and Filter
    fun searchBooks(query: String): List<Book> {
        val lowerQuery = query.lowercase()
        return books.values.filter {
            it.title.lowercase().contains(lowerQuery) ||
            it.author.lowercase().contains(lowerQuery)
        }
    }

    fun getAvailableBooks(): List<Book> {
        return books.values.filter { it.isAvailable() }
    }

    fun getBorrowedBooks(): List<Book> {
        return books.values.filter { !it.isAvailable() }
    }

    // Loan Management
    fun getOverdueLoans(): List<Loan> {
        return loans.filter { it.returnDate == null && it.isOverdue() }
    }

    fun getMemberHistory(memberId: String): List<Loan> {
        return loans.filter { it.member.memberId == memberId }
    }

    fun getActiveLoans(): List<Loan> {
        return loans.filter { it.returnDate == null }
    }

    // Statistics
    fun printStatistics() {
        println("\n" + "=".repeat(50))
        println("📊 LibraryHub Statistics")
        println("=".repeat(50))
        println("Total Books: ${books.size}")
        println("  - Available: ${getAvailableBooks().size}")
        println("  - Borrowed: ${getBorrowedBooks().size}")
        println("  - Physical: ${books.values.count { it is PhysicalBook }}")
        println("  - Digital: ${books.values.count { it is DigitalBook }}")
        println()
        println("Total Members: ${members.size}")
        members.values.groupBy { it.membershipType }.forEach { (type, memberList) ->
            println("  - $type: ${memberList.size}")
        }
        println()
        println("Total Loans: ${loans.size}")
        println("  - Active: ${getActiveLoans().size}")
        println("  - Overdue: ${getOverdueLoans().size}")
        println("  - Completed: ${loans.count { it.returnDate != null }}")
        println("=".repeat(50) + "\n")
    }

    fun displayAllBooks() {
        println("\n📚 All Books")
        println("=".repeat(50))
        if (books.isEmpty()) {
            println("No books in library")
        } else {
            books.values.forEachIndexed { index, book ->
                println("\n${index + 1}. ${book.title}")
                println("   Author: ${book.author}")
                println("   ISBN: ${book.isbn}")
                println("   Status: ${book.status.getDescription()}")
            }
        }
        println("=".repeat(50) + "\n")
    }

    fun displayAllMembers() {
        println("\n👥 All Members")
        println("=".repeat(50))
        if (members.isEmpty()) {
            println("No registered members")
        } else {
            members.values.forEachIndexed { index, member ->
                println("\n${index + 1}. ${member.name}")
                println("   ID: ${member.memberId}")
                println("   Type: ${member.membershipType}")
                println("   Books: ${member.getBorrowedCount()}/${member.membershipType.maxBooks}")
            }
        }
        println("=".repeat(50) + "\n")
    }

    fun displayOverdueLoans() {
        val overdueLoans = getOverdueLoans()
        println("\n⚠️  Overdue Loans")
        println("=".repeat(50))
        if (overdueLoans.isEmpty()) {
            println("No overdue loans")
        } else {
            overdueLoans.forEach { loan ->
                println("\n${loan.member.name} - ${loan.book.title}")
                println("Due: ${loan.dueDate}")
                println("Late Fee: $${String.format("%.2f", loan.calculateLateFee())}")
            }
        }
        println("=".repeat(50) + "\n")
    }
}
```

### Phase 5: Main Application (30 minutes)

```kotlin
// Main.kt
import java.time.LocalDate

fun main() {
    println("╔════════════════════════════════════════╗")
    println("║     Welcome to LibraryHub System      ║")
    println("╚════════════════════════════════════════╝\n")

    // Initialize library with books
    println("📚 Adding books to library...")
    Library.addBook(PhysicalBook(
        isbn = "978-0-13-468599-1",
        title = "Effective Java",
        author = "Joshua Bloch",
        publishYear = 2018,
        shelfLocation = "A1-15",
        condition = BookCondition.GOOD
    ))

    Library.addBook(PhysicalBook(
        isbn = "978-0-13-597764-5",
        title = "Clean Code",
        author = "Robert C. Martin",
        publishYear = 2008,
        shelfLocation = "A1-20",
        condition = BookCondition.FAIR
    ))

    Library.addBook(DigitalBook(
        isbn = "978-1-61729-655-2",
        title = "Kotlin in Action",
        author = "Dmitry Jemerov",
        publishYear = 2017,
        fileSize = 15.5,
        format = FileFormat.PDF
    ))

    Library.addBook(DigitalBook(
        isbn = "978-1-78899-367-8",
        title = "Programming Kotlin",
        author = "Venkat Subramaniam",
        publishYear = 2019,
        fileSize = 12.3,
        format = FileFormat.EPUB
    ))

    Library.addBook(PhysicalBook(
        isbn = "978-0-13-490733-2",
        title = "Design Patterns",
        author = "Gang of Four",
        publishYear = 1994,
        shelfLocation = "B2-10",
        condition = BookCondition.GOOD
    ))

    // Register members
    println("\n👥 Registering members...")
    Library.registerMember(Member(
        memberId = "M001",
        name = "Alice Johnson",
        email = "alice@example.com",
        membershipType = MembershipType.PREMIUM,
        joinDate = LocalDate.now().minusMonths(6).toString()
    ))

    Library.registerMember(Member(
        memberId = "M002",
        name = "Bob Smith",
        email = "bob@example.com",
        membershipType = MembershipType.BASIC,
        joinDate = LocalDate.now().minusMonths(3).toString()
    ))

    Library.registerMember(Member(
        memberId = "M003",
        name = "Carol Davis",
        email = "carol@example.com",
        membershipType = MembershipType.STUDENT,
        joinDate = LocalDate.now().minusWeeks(2).toString()
    ))

    // Display initial state
    Library.printStatistics()
    Library.displayAllBooks()
    Library.displayAllMembers()

    // Simulate borrowing
    println("\n" + "=".repeat(50))
    println("📖 Borrowing Operations")
    println("=".repeat(50))

    Library.borrowBook("978-0-13-468599-1", "M001")  // Alice borrows Effective Java
    Library.borrowBook("978-1-61729-655-2", "M001")  // Alice borrows Kotlin in Action
    Library.borrowBook("978-0-13-597764-5", "M002")  // Bob borrows Clean Code
    Library.borrowBook("978-1-78899-367-8", "M003")  // Carol borrows Programming Kotlin

    // Try to borrow unavailable book
    println()
    Library.borrowBook("978-0-13-468599-1", "M002")  // Should fail - already borrowed

    // Display updated state
    Library.printStatistics()

    // Search functionality
    println("\n" + "=".repeat(50))
    println("🔍 Search Results for 'Kotlin'")
    println("=".repeat(50))
    val kotlinBooks = Library.searchBooks("Kotlin")
    kotlinBooks.forEach { book ->
        println("\n${book.title} by ${book.author}")
        println("Status: ${book.status.getDescription()}")
    }

    // Return books
    println("\n" + "=".repeat(50))
    println("📥 Return Operations")
    println("=".repeat(50))

    Library.returnBook("978-0-13-468599-1")  // Alice returns Effective Java
    Library.returnBook("978-1-78899-367-8")  // Carol returns Programming Kotlin

    // Display active loans
    println("\n📋 Active Loans:")
    Library.getActiveLoans().forEach { it.display() }

    // Final statistics
    Library.printStatistics()

    // Member history
    println("\n" + "=".repeat(50))
    println("📜 Alice's Borrowing History")
    println("=".repeat(50))
    val aliceHistory = Library.getMemberHistory("M001")
    aliceHistory.forEach { it.display() }

    println("\n╔════════════════════════════════════════╗")
    println("║   Thank you for using LibraryHub!     ║")
    println("╚════════════════════════════════════════╝")
}
```

---

## Complete Solution

The complete solution integrates all the pieces above. Here's what you should have:

**File Structure**:
```
LibraryHub/
├── BookStatus.kt
├── BookCondition.kt
├── FileFormat.kt
├── MembershipType.kt
├── Book.kt
├── PhysicalBook.kt
├── DigitalBook.kt
├── Member.kt
├── Loan.kt
├── Library.kt
└── Main.kt
```

---

## Testing Your Solution

Run the main function and verify:

1. ✅ Books are added successfully
2. ✅ Members are registered
3. ✅ Borrowing works correctly
4. ✅ Can't borrow unavailable books
5. ✅ Can't exceed borrowing limits
6. ✅ Return functionality works
7. ✅ Search finds correct books
8. ✅ Statistics are accurate
9. ✅ Member history is tracked

---

## Extension Challenges

Once you have the basic system working, try these enhancements:

### Challenge 1: Reservation System (+⭐)

Add ability to reserve books that are currently borrowed:

```kotlin
fun reserveBook(isbn: String, memberId: String): Boolean {
    // Implement reservation logic
    // Book should automatically be available to reserver when returned
}
```

### Challenge 2: Fine Payment System (+⭐⭐)

Add payment tracking:

```kotlin
data class Payment(
    val paymentId: String,
    val member: Member,
    val amount: Double,
    val paymentDate: LocalDate,
    val description: String
)

// Add to Library object
fun recordPayment(payment: Payment)
fun getMemberBalance(memberId: String): Double
```

### Challenge 3: Book Categories (+⭐)

Add categories/genres to books:

```kotlin
enum class BookCategory {
    FICTION, NON_FICTION, SCIENCE, TECHNOLOGY,
    BIOGRAPHY, HISTORY, CHILDREN, REFERENCE
}

// Add category property to Book
// Implement filtering by category
```

### Challenge 4: Review System (+⭐⭐)

Allow members to review books:

```kotlin
data class Review(
    val member: Member,
    val book: Book,
    val rating: Int,  // 1-5 stars
    val comment: String,
    val reviewDate: LocalDate
)

// Add reviews to Library object
// Calculate average rating per book
```

### Challenge 5: Save/Load System (+⭐⭐⭐)

Persist data to files:

```kotlin
fun saveToFile(filename: String)
fun loadFromFile(filename: String)

// Use JSON or serialization
// Save all books, members, loans
```

---

## Evaluation Checklist

Before considering your project complete, ensure:

- [ ] All classes are properly defined with correct properties
- [ ] Inheritance hierarchy is implemented (Book → PhysicalBook/DigitalBook)
- [ ] Sealed classes are used for BookStatus
- [ ] Enums are defined for BookCondition, FileFormat, MembershipType
- [ ] Data classes are used where appropriate (Member, Loan)
- [ ] Object declaration is used for Library singleton
- [ ] All interfaces are implemented correctly
- [ ] Borrowing logic validates availability and member limits
- [ ] Return logic updates all states correctly
- [ ] Search functionality works
- [ ] Statistics are accurate
- [ ] Late fee calculation is correct
- [ ] Code is well-organized and readable
- [ ] No duplicate code (DRY principle)
- [ ] Meaningful variable and function names

---

## Learning Outcomes

By completing this capstone project, you have:

✅ **Applied OOP principles** in a real-world scenario
✅ **Designed a class hierarchy** with inheritance and polymorphism
✅ **Used interfaces** to define contracts
✅ **Leveraged sealed classes** for type-safe state management
✅ **Created data classes** for domain models
✅ **Implemented a singleton** for centralized management
✅ **Managed relationships** between objects
✅ **Handled business logic** with validation
✅ **Built a complete system** from requirements to implementation

---

## What's Next?

Congratulations on completing Part 2: Object-Oriented Programming! 🎉

**In Part 3: Functional Programming**, you'll learn:
- Lambda expressions and higher-order functions
- Collection operations (map, filter, reduce)
- Function types and function composition
- Scope functions (let, apply, run, also, with)
- Sequences for lazy evaluation

**In Part 4: Advanced Kotlin**, you'll learn:
- Generics and variance
- Delegation pattern
- DSL creation
- Coroutines basics
- Extension functions

---

## Tips for Success

### Design Principles Applied

**1. Single Responsibility Principle**
- Each class has one clear purpose
- `Book` manages book data, `Library` manages operations

**2. Open/Closed Principle**
- `Book` is open for extension (PhysicalBook, DigitalBook)
- Closed for modification (base behavior is stable)

**3. Liskov Substitution Principle**
- `PhysicalBook` and `DigitalBook` can be used anywhere `Book` is expected

**4. Interface Segregation**
- Small, focused interfaces (Borrowable, Reservable)

**5. Dependency Inversion**
- Code depends on abstractions (Book, not specific types)

---

## Reflection Questions

After completing the project, consider:

1. Why did we use an abstract class for `Book` instead of an interface?
2. When would you use a data class vs a regular class?
3. Why is `Library` an object instead of a regular class?
4. How does sealed classes make the status system safer?
5. What are the benefits of using enums for fixed sets of values?

---

## Resources

**Kotlin Documentation**:
- [Classes and Objects](https://kotlinlang.org/docs/classes.html)
- [Inheritance](https://kotlinlang.org/docs/inheritance.html)
- [Data Classes](https://kotlinlang.org/docs/data-classes.html)
- [Sealed Classes](https://kotlinlang.org/docs/sealed-classes.html)
- [Object Declarations](https://kotlinlang.org/docs/object-declarations.html)

**Design Patterns**:
- [Singleton Pattern](https://refactoring.guru/design-patterns/singleton)
- [Factory Pattern](https://refactoring.guru/design-patterns/factory-method)

---

**🎉 Congratulations on completing Part 2: Object-Oriented Programming! 🎉**

You've built a complete, real-world application using OOP principles. This is a major milestone in your Kotlin journey!

Your Library Management System demonstrates:
- Strong understanding of OOP concepts
- Ability to model real-world domains
- Clean code organization
- Practical problem-solving skills

Take a moment to celebrate this achievement, then get ready for Part 3: Functional Programming! 🚀

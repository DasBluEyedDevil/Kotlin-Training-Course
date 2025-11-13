# Lesson 5.7: Database Operations with Exposed - Part 2 (CRUD & Transactions)

**Estimated Time**: 45 minutes
**Difficulty**: Intermediate
**Prerequisites**: Lesson 5.6 (Database fundamentals, INSERT, SELECT)

---

## 📖 Topic Introduction

In the previous lesson, you learned to INSERT and SELECT data. Now it's time to complete the CRUD operations: **UPDATE** and **DELETE**.

But that's not all! You'll also learn:
- Complex WHERE clauses with multiple conditions
- Table relationships (foreign keys)
- JOIN queries to combine data from multiple tables
- Batch operations for performance
- Database migrations

By the end, you'll be able to build complex, production-ready database schemas!

---

## 💡 The Concept: Completing CRUD

### The Four Pillars of Data Management

**C**reate - INSERT ✅ (learned in 5.6)
**R**ead - SELECT ✅ (learned in 5.6)
**U**pdate - UPDATE 📝 (this lesson)
**D**elete - DELETE 🗑️ (this lesson)

### Real-World Analogy

Think of your database like a filing cabinet:

- **INSERT**: Add a new document to a folder
- **SELECT**: Find and read documents
- **UPDATE**: Take out a document, cross out old info, write new info
- **DELETE**: Remove and shred a document

---

## 🔄 UPDATE Operations

### Basic Update

```kotlin
object BookDao {
    fun update(id: Int, title: String?, author: String?, year: Int?): Boolean {
        return transaction {
            val updateCount = Books.update({ Books.id eq id }) {
                title?.let { newTitle -> it[Books.title] = newTitle }
                author?.let { newAuthor -> it[Books.author] = newAuthor }
                year?.let { newYear -> it[Books.year] = newYear }
            }
            updateCount > 0  // Returns true if at least one row was updated
        }
    }
}
```

**Understanding the syntax:**

```kotlin
Books.update({ Books.id eq id }) {
    it[Books.title] = "New Title"
    it[Books.author] = "New Author"
}
```

- **update({ condition })**: WHERE clause
- **it[column] = value**: SET clause
- Returns the number of rows updated

**Behind the scenes SQL:**
```sql
UPDATE books
SET title = 'New Title', author = 'New Author'
WHERE id = 1;
```

### Conditional Updates

```kotlin
// Update all books by a specific author
fun updateAuthorName(oldName: String, newName: String): Int = transaction {
    Books.update({ Books.author eq oldName }) {
        it[author] = newName
    }
}

// Increase year by 1 for all books before 1950
fun adjustOldBookYears(): Int = transaction {
    Books.update({ Books.year less 1950 }) {
        it[year] = year + 1
    }
}

// Update with multiple conditions
fun markBooksAsClassic(): Int = transaction {
    Books.update({
        (Books.year less 1960) and (Books.author eq "George Orwell")
    }) {
        it[title] = SqlExpressionBuilder.concat("[Classic] ", title)
    }
}
```

### Partial Updates (Only Changed Fields)

```kotlin
data class UpdateBookRequest(
    val title: String? = null,
    val author: String? = null,
    val year: Int? = null,
    val isbn: String? = null
)

fun partialUpdate(id: Int, request: UpdateBookRequest): Boolean = transaction {
    // Build update dynamically based on what's provided
    val updateCount = Books.update({ Books.id eq id }) {
        request.title?.let { newTitle -> it[Books.title] = newTitle }
        request.author?.let { newAuthor -> it[Books.author] = newAuthor }
        request.year?.let { newYear -> it[Books.year] = newYear }
        request.isbn?.let { newIsbn -> it[Books.isbn] = newIsbn }
    }
    updateCount > 0
}
```

**This is powerful for PATCH endpoints** where clients only send changed fields!

---

## 🗑️ DELETE Operations

### Basic Delete

```kotlin
object BookDao {
    fun delete(id: Int): Boolean = transaction {
        Books.deleteWhere { Books.id eq id } > 0
    }
}
```

**SQL equivalent:**
```sql
DELETE FROM books WHERE id = 1;
```

### Conditional Deletes

```kotlin
// Delete all books by an author
fun deleteByAuthor(author: String): Int = transaction {
    Books.deleteWhere { Books.author eq author }
}

// Delete old books
fun deleteBooksBefore(year: Int): Int = transaction {
    Books.deleteWhere { Books.year less year }
}

// Delete with multiple conditions
fun deleteUnpopularOldBooks(year: Int, maxRating: Double): Int = transaction {
    Books.deleteWhere {
        (Books.year less year) and (Books.rating less maxRating)
    }
}
```

### Delete All (Dangerous!)

```kotlin
// Delete all records (use with caution!)
fun deleteAll(): Int = transaction {
    Books.deleteAll()
}
```

⚠️ **Warning**: Always use WHERE clauses unless you really want to delete everything!

---

## 🔍 Complex WHERE Clauses

### Comparison Operators

```kotlin
// Exposed DSL operators
Books.year eq 1949          // =
Books.year neq 1949         // !=
Books.year greater 1940     // >
Books.year greaterEq 1940   // >=
Books.year less 1950        // <
Books.year lessEq 1950      // <=
```

### Logical Operators

```kotlin
// AND
Books.selectAll().where {
    (Books.year greaterEq 1940) and (Books.year lessEq 1950)
}

// OR
Books.selectAll().where {
    (Books.author eq "Orwell") or (Books.author eq "Huxley")
}

// NOT
Books.selectAll().where {
    not(Books.year eq 1984)
}

// Complex combinations
Books.selectAll().where {
    ((Books.year greaterEq 1940) and (Books.year lessEq 1950)) or
    (Books.author eq "George Orwell")
}
```

### String Operations

```kotlin
// LIKE - pattern matching
Books.selectAll().where {
    Books.title like "%Brave%"  // Contains "Brave"
}

Books.selectAll().where {
    Books.author like "George%"  // Starts with "George"
}

// Case-insensitive (database-dependent)
Books.selectAll().where {
    Books.title.lowerCase() like "%brave%".lowerCase()
}
```

### IN Operator

```kotlin
// Multiple values
val authors = listOf("Orwell", "Huxley", "Bradbury")
Books.selectAll().where {
    Books.author inList authors
}

// NOT IN
Books.selectAll().where {
    Books.author notInList authors
}
```

### NULL Checks

```kotlin
// IS NULL
Books.selectAll().where {
    Books.isbn.isNull()
}

// IS NOT NULL
Books.selectAll().where {
    Books.isbn.isNotNull()
}
```

---

## 🔗 Table Relationships: Foreign Keys

### One-to-Many Relationship Example

Let's model books and reviews (one book can have many reviews):

```kotlin
// Books table (already exists)
object Books : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 255)
    val author = varchar("author", 255)
    val year = integer("year")

    override val primaryKey = PrimaryKey(id)
}

// Reviews table (new)
object Reviews : Table() {
    val id = integer("id").autoIncrement()
    val bookId = integer("book_id").references(Books.id)  // Foreign key!
    val reviewerName = varchar("reviewer_name", 100)
    val rating = integer("rating")  // 1-5
    val comment = text("comment")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}
```

**Key concept:**
```kotlin
val bookId = integer("book_id").references(Books.id)
```
- Creates a **foreign key** linking `Reviews.bookId` to `Books.id`
- Ensures referential integrity (can't review a non-existent book)

### Creating Tables with Relationships

```kotlin
// In DatabaseFactory.init()
transaction(database) {
    SchemaUtils.create(Books, Reviews)  // Order matters!
}
```

**Important**: Create parent table (Books) before child table (Reviews).

---

## 🔀 JOIN Queries

### Inner Join

Get books with their reviews:

```kotlin
object ReviewDao {
    fun getBooksWithReviews(): List<BookWithReviews> = transaction {
        // Join Books and Reviews
        (Books innerJoin Reviews)
            .selectAll()
            .groupBy { it[Books.id] }
            .map { (bookId, rows) ->
                val firstRow = rows.first()
                BookWithReviews(
                    book = Book(
                        id = firstRow[Books.id],
                        title = firstRow[Books.title],
                        author = firstRow[Books.author],
                        year = firstRow[Books.year]
                    ),
                    reviews = rows.map { row ->
                        Review(
                            id = row[Reviews.id],
                            bookId = row[Reviews.bookId],
                            reviewerName = row[Reviews.reviewerName],
                            rating = row[Reviews.rating],
                            comment = row[Reviews.comment],
                            createdAt = row[Reviews.createdAt].toString()
                        )
                    }
                )
            }
    }
}
```

**SQL equivalent:**
```sql
SELECT * FROM books
INNER JOIN reviews ON books.id = reviews.book_id;
```

### Left Join

Get all books, even those without reviews:

```kotlin
fun getAllBooksWithOptionalReviews(): List<BookWithReviews> = transaction {
    (Books leftJoin Reviews)
        .selectAll()
        .groupBy { it[Books.id] }
        .map { (bookId, rows) ->
            val firstRow = rows.first()
            BookWithReviews(
                book = Book(
                    id = firstRow[Books.id],
                    title = firstRow[Books.title],
                    author = firstRow[Books.author],
                    year = firstRow[Books.year]
                ),
                reviews = rows.mapNotNull { row ->
                    // Check if review exists (leftJoin might have NULLs)
                    row.getOrNull(Reviews.id)?.let {
                        Review(
                            id = row[Reviews.id],
                            bookId = row[Reviews.bookId],
                            reviewerName = row[Reviews.reviewerName],
                            rating = row[Reviews.rating],
                            comment = row[Reviews.comment],
                            createdAt = row[Reviews.createdAt].toString()
                        )
                    }
                }
            )
        }
}
```

### Simplified: Get Reviews for Specific Book

```kotlin
fun getReviewsForBook(bookId: Int): List<Review> = transaction {
    Reviews.selectAll()
        .where { Reviews.bookId eq bookId }
        .map { rowToReview(it) }
}

// Or with aggregation
fun getAverageRating(bookId: Int): Double? = transaction {
    Reviews.select(Reviews.rating.avg())
        .where { Reviews.bookId eq bookId }
        .singleOrNull()
        ?.get(Reviews.rating.avg())
}
```

---

## 📦 Batch Operations

### Batch Insert

Inserting many records efficiently:

```kotlin
fun insertBatch(books: List<CreateBookRequest>): List<Int> = transaction {
    val ids = mutableListOf<Int>()

    Books.batchInsert(books) { book ->
        this[Books.title] = book.title
        this[Books.author] = book.author
        this[Books.year] = book.year
        this[Books.isbn] = book.isbn
    }.forEach { resultRow ->
        ids.add(resultRow[Books.id])
    }

    ids
}
```

**Why batch operations?**
- ✅ Much faster for large datasets
- ✅ Single database round-trip
- ✅ Better transaction handling

### Batch Update

```kotlin
fun updateBatch(updates: Map<Int, String>): Unit = transaction {
    updates.forEach { (id, newTitle) ->
        Books.update({ Books.id eq id }) {
            it[title] = newTitle
        }
    }
}
```

---

## 💻 Complete Example: Book Review System

Let's build a complete system with relationships:

### Models

```kotlin
@Serializable
data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val year: Int,
    val isbn: String? = null,
    val averageRating: Double? = null,
    val reviewCount: Int = 0
)

@Serializable
data class Review(
    val id: Int,
    val bookId: Int,
    val reviewerName: String,
    val rating: Int,
    val comment: String,
    val createdAt: String
)

@Serializable
data class BookWithReviews(
    val book: Book,
    val reviews: List<Review>
)

@Serializable
data class CreateReviewRequest(
    val reviewerName: String,
    val rating: Int,
    val comment: String
)
```

### Enhanced BookDao with Statistics

```kotlin
object BookDao {
    // ... existing methods ...

    fun getWithStats(id: Int): Book? = transaction {
        val bookRow = Books.selectAll()
            .where { Books.id eq id }
            .singleOrNull() ?: return@transaction null

        // Get review statistics
        val stats = Reviews.select(
            Reviews.rating.avg(),
            Reviews.id.count()
        ).where { Reviews.bookId eq id }
            .singleOrNull()

        Book(
            id = bookRow[Books.id],
            title = bookRow[Books.title],
            author = bookRow[Books.author],
            year = bookRow[Books.year],
            isbn = bookRow[Books.isbn],
            averageRating = stats?.get(Reviews.rating.avg()),
            reviewCount = stats?.get(Reviews.id.count())?.toInt() ?: 0
        )
    }
}
```

### ReviewDao

```kotlin
object ReviewDao {
    fun insert(bookId: Int, request: CreateReviewRequest): Int = transaction {
        Reviews.insert {
            it[Reviews.bookId] = bookId
            it[reviewerName] = request.reviewerName
            it[rating] = request.rating
            it[comment] = request.comment
            it[createdAt] = LocalDateTime.now()
        }[Reviews.id]
    }

    fun getByBookId(bookId: Int): List<Review> = transaction {
        Reviews.selectAll()
            .where { Reviews.bookId eq bookId }
            .orderBy(Reviews.createdAt, SortOrder.DESC)
            .map { rowToReview(it) }
    }

    fun delete(id: Int, bookId: Int): Boolean = transaction {
        Reviews.deleteWhere {
            (Reviews.id eq id) and (Reviews.bookId eq bookId)
        } > 0
    }

    private fun rowToReview(row: ResultRow): Review {
        return Review(
            id = row[Reviews.id],
            bookId = row[Reviews.bookId],
            reviewerName = row[Reviews.reviewerName],
            rating = row[Reviews.rating],
            comment = row[Reviews.comment],
            createdAt = row[Reviews.createdAt].toString()
        )
    }
}
```

### Routes

```kotlin
fun Route.reviewRoutes() {
    route("/api/books/{bookId}/reviews") {
        // Get all reviews for a book
        get {
            val bookId = call.parameters["bookId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val reviews = ReviewDao.getByBookId(bookId)
            call.respond(ApiResponse(success = true, data = reviews))
        }

        // Create review
        post {
            val bookId = call.parameters["bookId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            // Check if book exists
            if (BookDao.getById(bookId) == null) {
                call.respond(
                    HttpStatusCode.NotFound,
                    ApiResponse<Review>(
                        success = false,
                        message = "Book not found"
                    )
                )
                return@post
            }

            val request = call.receive<CreateReviewRequest>()

            // Validate rating
            if (request.rating !in 1..5) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Review>(
                        success = false,
                        message = "Rating must be between 1 and 5"
                    )
                )
                return@post
            }

            val reviewId = ReviewDao.insert(bookId, request)
            val review = ReviewDao.getById(reviewId)

            call.respond(
                HttpStatusCode.Created,
                ApiResponse(success = true, data = review)
            )
        }

        // Delete review
        delete("/{reviewId}") {
            val bookId = call.parameters["bookId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val reviewId = call.parameters["reviewId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val deleted = ReviewDao.delete(reviewId, bookId)

            if (deleted) {
                call.respond(
                    HttpStatusCode.OK,
                    ApiResponse<Unit>(
                        success = true,
                        message = "Review deleted"
                    )
                )
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
```

### Testing

```bash
# Create a review
curl -X POST http://localhost:8080/api/books/1/reviews \
  -H "Content-Type: application/json" \
  -d '{
    "reviewerName": "Alice",
    "rating": 5,
    "comment": "Absolutely brilliant dystopian novel!"
  }'

# Get all reviews for a book
curl http://localhost:8080/api/books/1/reviews

# Get book with statistics
curl http://localhost:8080/api/books/1

# Delete a review
curl -X DELETE http://localhost:8080/api/books/1/reviews/1
```

---

## 🎯 Exercise: Comment System

Add comments on reviews (nested relationship):

### Requirements

1. Create a **Comments** table:
   - id, reviewId (foreign key to Reviews), commenterName, text, createdAt

2. Implement **CommentDao**:
   - insert, getByReviewId, delete

3. Add routes:
   - POST `/api/books/{bookId}/reviews/{reviewId}/comments`
   - GET `/api/books/{bookId}/reviews/{reviewId}/comments`
   - DELETE `/api/books/{bookId}/reviews/{reviewId}/comments/{commentId}`

---

## ✅ Solution & Explanation

```kotlin
// Table definition
object Comments : Table() {
    val id = integer("id").autoIncrement()
    val reviewId = integer("review_id").references(Reviews.id)
    val commenterName = varchar("commenter_name", 100)
    val text = text("text")
    val createdAt = datetime("created_at")

    override val primaryKey = PrimaryKey(id)
}

// Model
@Serializable
data class Comment(
    val id: Int,
    val reviewId: Int,
    val commenterName: String,
    val text: String,
    val createdAt: String
)

@Serializable
data class CreateCommentRequest(
    val commenterName: String,
    val text: String
)

// DAO
object CommentDao {
    fun insert(reviewId: Int, request: CreateCommentRequest): Int = transaction {
        Comments.insert {
            it[Comments.reviewId] = reviewId
            it[commenterName] = request.commenterName
            it[text] = request.text
            it[createdAt] = LocalDateTime.now()
        }[Comments.id]
    }

    fun getByReviewId(reviewId: Int): List<Comment> = transaction {
        Comments.selectAll()
            .where { Comments.reviewId eq reviewId }
            .orderBy(Comments.createdAt)
            .map { rowToComment(it) }
    }

    fun delete(id: Int, reviewId: Int): Boolean = transaction {
        Comments.deleteWhere {
            (Comments.id eq id) and (Comments.reviewId eq reviewId)
        } > 0
    }

    private fun rowToComment(row: ResultRow): Comment {
        return Comment(
            id = row[Comments.id],
            reviewId = row[Comments.reviewId],
            commenterName = row[Comments.commenterName],
            text = row[Comments.text],
            createdAt = row[Comments.createdAt].toString()
        )
    }
}

// Routes
fun Route.commentRoutes() {
    route("/api/books/{bookId}/reviews/{reviewId}/comments") {
        get {
            val reviewId = call.parameters["reviewId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val comments = CommentDao.getByReviewId(reviewId)
            call.respond(ApiResponse(success = true, data = comments))
        }

        post {
            val reviewId = call.parameters["reviewId"]?.toIntOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            val request = call.receive<CreateCommentRequest>()
            val commentId = CommentDao.insert(reviewId, request)

            call.respond(HttpStatusCode.Created)
        }

        delete("/{commentId}") {
            val reviewId = call.parameters["reviewId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val commentId = call.parameters["commentId"]?.toIntOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val deleted = CommentDao.delete(commentId, reviewId)
            if (deleted) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

// Update DatabaseFactory
transaction(database) {
    SchemaUtils.create(Books, Reviews, Comments)
}
```

---

## 📝 Lesson Checkpoint Quiz

### Question 1
What does the following code do?
```kotlin
Books.update({ Books.year less 1950 }) { it[year] = year + 1 }
```

A) Updates all books by incrementing their year by 1
B) Updates only books published before 1950, incrementing their year by 1
C) Deletes books from before 1950
D) Selects books from before 1950

---

### Question 2
What's the difference between `innerJoin` and `leftJoin`?

A) There is no difference
B) innerJoin only returns rows where both tables have matches; leftJoin returns all rows from the left table
C) leftJoin is faster
D) innerJoin supports more tables

---

### Question 3
Why use batch operations instead of individual inserts in a loop?

A) They look better in code
B) They're required by Exposed
C) They're much faster and use fewer database connections
D) They provide better error messages

---

## 🎯 Why This Matters

You now have **complete control** over your database! These operations form the backbone of every backend application.

### What You've Mastered

✅ **UPDATE**: Modify existing records
✅ **DELETE**: Remove records safely
✅ **Complex queries**: Multiple conditions, string matching, NULL checks
✅ **Relationships**: Foreign keys and referential integrity
✅ **JOINs**: Combine data from multiple tables
✅ **Batch operations**: Efficient bulk operations
✅ **Nested resources**: Books → Reviews → Comments

### Real-World Applications

- **E-commerce**: Products → Reviews → Questions
- **Social media**: Posts → Comments → Reactions
- **Forums**: Threads → Posts → Replies
- **Blogs**: Articles → Comments → Likes

---

## 📚 Key Takeaways

✅ **UPDATE** modifies records: `Books.update({ condition }) { it[column] = value }`
✅ **DELETE** removes records: `Books.deleteWhere { condition }`
✅ **Foreign keys** link tables: `.references(OtherTable.id)`
✅ **JOINs** combine tables: `Books innerJoin Reviews`
✅ **Batch operations** improve performance for bulk operations
✅ **Transactions** ensure data consistency

---

## 🔜 Next Steps

In **Lesson 5.8**, you'll learn:
- The Repository Pattern (organizing database code)
- Dependency Injection basics
- Service layer architecture
- Separating concerns
- Making code testable

---

## ✏️ Quiz Answer Key

**Question 1**: **B) Updates only books published before 1950, incrementing their year by 1**

Explanation: The WHERE clause `{ Books.year less 1950 }` filters to only books before 1950, then `year + 1` increments each one.

---

**Question 2**: **B) innerJoin only returns rows where both tables have matches; leftJoin returns all rows from the left table**

Explanation: INNER JOIN requires matches in both tables. LEFT JOIN returns all left table rows, with NULL for unmatched right table columns.

---

**Question 3**: **C) They're much faster and use fewer database connections**

Explanation: Batch operations send multiple records in a single database round-trip, dramatically improving performance compared to individual operations in a loop.

---

**Congratulations!** You now have complete CRUD mastery with Exposed! 🎉

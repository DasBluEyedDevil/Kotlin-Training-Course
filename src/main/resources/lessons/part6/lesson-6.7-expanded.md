# Lesson 6.7: Local Data Storage

**Estimated Time**: 75 minutes

---

## Introduction

Apps need to store data locally for offline access, caching, and user preferences. Room provides a powerful, type-safe database layer over SQLite, while DataStore handles preferences elegantly.

In this lesson, you'll master:
- ✅ Room database setup and configuration
- ✅ Entity definitions with relationships
- ✅ DAOs (Data Access Objects) for queries
- ✅ Repository pattern with Room
- ✅ Flows for reactive data updates
- ✅ DataStore for preferences
- ✅ Combining local and remote data

---

## Setup Dependencies

Add in `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.ksp)  // Kotlin Symbol Processing for Room
}

dependencies {
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
}
```

In `gradle/libs.versions.toml`:

```toml
[versions]
room = "2.6.1"
ksp = "2.0.21-1.0.27"
datastore = "1.1.1"

[libraries]
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-datastore-preferences = { group = "androidx.datastore", name = "datastore-preferences", version.ref = "datastore" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

---

## Room Database

### Entity (Table)

```kotlin
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val isCompleted: Boolean = false,
    val dueDate: Long? = null,  // Timestamp
    val priority: Priority = Priority.MEDIUM,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Priority {
    LOW, MEDIUM, HIGH
}
```

### Type Converters

```kotlin
import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPriority(value: Priority): String {
        return value.name
    }

    @TypeConverter
    fun toPriority(value: String): Priority {
        return enumValueOf(value)
    }
}
```

### DAO (Data Access Object)

```kotlin
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTask(taskId: Int): Flow<Task?>

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted")
    fun getTasksByStatus(isCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE priority = :priority")
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Insert
    suspend fun insertTasks(tasks: List<Task>)

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Int)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int, isCompleted: Boolean)
}
```

### Database

```kotlin
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [Task::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()  // For development only!
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
```

---

## Repository with Room

```kotlin
class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<Task>> = taskDao.getAllTasks()

    fun getTask(taskId: Int): Flow<Task?> = taskDao.getTask(taskId)

    fun getActiveTasks(): Flow<List<Task>> = taskDao.getTasksByStatus(false)

    fun getCompletedTasks(): Flow<List<Task>> = taskDao.getTasksByStatus(true)

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun toggleTaskStatus(taskId: Int, isCompleted: Boolean) {
        taskDao.updateTaskStatus(taskId, isCompleted)
    }
}
```

---

## ViewModel with Room

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksViewModel(
    private val repository: TaskRepository
) : ViewModel() {

    val allTasks: StateFlow<List<Task>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(title: String, description: String, priority: Priority) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                priority = priority
            )
            repository.insertTask(task)
        }
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}
```

---

## UI with Room Data

```kotlin
@Composable
fun TasksScreen(viewModel: TasksViewModel) {
    val tasks by viewModel.allTasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { innerPadding ->
        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("No tasks yet. Add one!")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { viewModel.toggleTask(task) },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddTaskDialog(
            onDismiss = { showDialog = false },
            onAdd = { title, description, priority ->
                viewModel.addTask(title, description, priority)
                showDialog = false
            }
        )
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else null
                )
                Text(
                    task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
```

---

## Relationships

### One-to-Many

```kotlin
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val color: String
)

@Entity(
    tableName = "tasks_with_category",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskWithCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val categoryId: Int
)

// Query with relationship
data class CategoryWithTasks(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val tasks: List<TaskWithCategory>
)

@Dao
interface CategoryDao {
    @Transaction
    @Query("SELECT * FROM categories")
    fun getCategoriesWithTasks(): Flow<List<CategoryWithTasks>>
}
```

---

## DataStore for Preferences

```kotlin
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_KEY = stringPreferencesKey("theme")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val USERNAME = stringPreferencesKey("username")
    }

    val theme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_KEY] ?: "system"
        }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_KEY] = theme
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USERNAME] = username
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit { it.clear() }
    }
}
```

---

## Migration

```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE tasks ADD COLUMN categoryId INTEGER")
    }
}

val database = Room.databaseBuilder(
    context,
    AppDatabase::class.java,
    "app_database"
)
    .addMigrations(MIGRATION_1_2)
    .build()
```

---

## Exercise 1: Notes App with Room

Create a notes app:
- Add, edit, delete notes
- Search notes
- Persist to Room database

---

## Solution 1

```kotlin
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<Note>>

    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)
}

class NotesViewModel(private val noteDao: NoteDao) : ViewModel() {
    val allNotes: StateFlow<List<Note>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addNote(title: String, content: String) {
        viewModelScope.launch {
            noteDao.insertNote(Note(title = title, content = content))
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            noteDao.updateNote(note)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }
}
```

---

## Exercise 2: Favorites with DataStore

Implement favorites functionality:
- Save favorite item IDs
- Load favorites on app start
- Toggle favorite status

---

## Solution 2

```kotlin
import androidx.datastore.preferences.core.stringSetPreferencesKey

class FavoritesRepository(private val context: Context) {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorites")

    val favorites: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }

    suspend fun addFavorite(itemId: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.add(itemId)
            preferences[FAVORITES_KEY] = currentFavorites
        }
    }

    suspend fun removeFavorite(itemId: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            currentFavorites.remove(itemId)
            preferences[FAVORITES_KEY] = currentFavorites
        }
    }

    suspend fun toggleFavorite(itemId: String) {
        context.dataStore.edit { preferences ->
            val currentFavorites = preferences[FAVORITES_KEY]?.toMutableSet() ?: mutableSetOf()
            if (currentFavorites.contains(itemId)) {
                currentFavorites.remove(itemId)
            } else {
                currentFavorites.add(itemId)
            }
            preferences[FAVORITES_KEY] = currentFavorites
        }
    }
}
```

---

## Exercise 3: Offline-First App

Combine Room + Retrofit:
- Fetch data from API
- Cache in Room
- Show cached data while loading
- Update cache when new data arrives

---

## Solution 3

```kotlin
class OfflineFirstRepository(
    private val apiService: ApiService,
    private val userDao: UserDao
) {
    fun getUsers(): Flow<Result<List<User>>> = flow {
        // Emit cached data first
        emit(Result.Loading)

        val cachedUsers = userDao.getAllUsers().first()
        if (cachedUsers.isNotEmpty()) {
            emit(Result.Success(cachedUsers))
        }

        // Fetch from network
        try {
            val remoteUsers = apiService.getUsers()

            // Update cache
            userDao.deleteAll()
            userDao.insertAll(remoteUsers)

            // Emit fresh data
            emit(Result.Success(remoteUsers))
        } catch (e: Exception) {
            // If network fails and we have cache, keep showing cached data
            if (cachedUsers.isNotEmpty()) {
                emit(Result.Success(cachedUsers))
            } else {
                emit(Result.Error(e.message ?: "Unknown error"))
            }
        }
    }
}
```

---

## Why This Matters

**Statistics**:
- **80%** of app usage happens offline or on slow networks
- Apps with local storage have **5x** better retention
- Users expect instant data (not "Loading...")

---

## Checkpoint Quiz

### Question 1
What is Room?

A) Image loading library
B) SQLite database wrapper with type safety
C) Network library
D) UI component

### Question 2
What does `Flow<List<Task>>` provide?

A) One-time data fetch
B) Reactive updates when database changes
C) Faster queries
D) Automatic caching

### Question 3
When should you use DataStore instead of Room?

A) For large datasets
B) For simple key-value preferences
C) For complex queries
D) For images

### Question 4
What does `@PrimaryKey(autoGenerate = true)` do?

A) Makes field required
B) Generates unique ID automatically
C) Enables caching
D) Creates index

### Question 5
What is an offline-first strategy?

A) Never use network
B) Show cached data immediately, update from network
C) Only load data once
D) Disable network features

---

## Quiz Answers

**Question 1: B** - Room is a type-safe SQLite wrapper
**Question 2: B** - Flow provides reactive, automatic updates
**Question 3: B** - DataStore for preferences, Room for structured data
**Question 4: B** - Auto-generates incrementing IDs
**Question 5: B** - Show cache first, then update from network

---

## What You've Learned

✅ Room database setup and entities
✅ DAOs for type-safe queries
✅ Repository pattern with Room
✅ Flows for reactive data
✅ Entity relationships (one-to-many)
✅ DataStore for preferences
✅ Database migrations
✅ Offline-first architecture

---

## Next Steps

In **Lesson 6.8: MVVM Architecture**, you'll learn:
- MVVM pattern in depth
- ViewModel lifecycle
- LiveData vs StateFlow
- Dependency injection with Hilt
- Clean architecture layers
- Testing ViewModels

Get ready to structure professional apps!

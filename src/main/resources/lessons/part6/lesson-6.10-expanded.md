# Lesson 6.10: Part 6 Capstone - Task Manager App

**Estimated Time**: 4-6 hours

---

## Project Overview

Congratulations on completing Part 6! You've learned Android development from fundamentals to advanced concepts.

Now it's time to build a **complete, production-ready Task Manager App** that integrates everything you've learned:
- ✅ Jetpack Compose UI
- ✅ Material Design 3
- ✅ MVVM architecture
- ✅ Room database for local storage
- ✅ Navigation between screens
- ✅ State management
- ✅ Animations and gestures
- ✅ Dependency injection with Hilt

---

## The Project: TaskMaster

**TaskMaster** is a comprehensive task management app where users can:
- Create, edit, and delete tasks
- Organize tasks by categories
- Set priorities (Low, Medium, High)
- Add due dates
- Mark tasks as complete
- Filter and search tasks
- View statistics

---

## Features

### Core Features

1. **Task Management**
   - Create new tasks with title, description, due date, priority
   - Edit existing tasks
   - Delete tasks (swipe to dismiss)
   - Mark tasks as complete/incomplete

2. **Categories**
   - Predefined categories (Work, Personal, Shopping, Health)
   - Color-coded categories
   - Filter tasks by category

3. **Priorities**
   - Low, Medium, High
   - Visual indicators (colors, icons)
   - Sort by priority

4. **Due Dates**
   - Set due date with date picker
   - Overdue indicator
   - Sort by due date

5. **Filters & Search**
   - All tasks, Active, Completed
   - Search by title/description
   - Filter by category and priority

6. **Statistics**
   - Total tasks
   - Completed percentage
   - Tasks by category
   - Tasks by priority

---

## Project Structure

```
app/src/main/java/com/example/taskmaster/
├── data/
│   ├── local/
│   │   ├── TaskDatabase.kt
│   │   ├── TaskDao.kt
│   │   └── TaskEntity.kt
│   ├── repository/
│   │   └── TaskRepository.kt
│   └── model/
│       ├── Task.kt
│       ├── Category.kt
│       └── Priority.kt
├── di/
│   └── AppModule.kt
├── ui/
│   ├── theme/
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── components/
│   │   ├── TaskItem.kt
│   │   ├── CategoryChip.kt
│   │   └── PriorityBadge.kt
│   ├── screens/
│   │   ├── home/
│   │   │   ├── HomeScreen.kt
│   │   │   └── HomeViewModel.kt
│   │   ├── addEdit/
│   │   │   ├── AddEditScreen.kt
│   │   │   └── AddEditViewModel.kt
│   │   └── statistics/
│   │       ├── StatisticsScreen.kt
│   │       └── StatisticsViewModel.kt
│   └── navigation/
│       └── NavGraph.kt
└── MainActivity.kt
```

---

## Complete Implementation

### 1. Data Models

```kotlin
// data/model/Priority.kt
package com.example.taskmaster.data.model

import androidx.compose.ui.graphics.Color

enum class Priority(val displayName: String, val color: Color) {
    LOW("Low", Color(0xFF4CAF50)),
    MEDIUM("Medium", Color(0xFFFFC107)),
    HIGH("High", Color(0xFFF44336))
}

// data/model/Category.kt
enum class Category(val displayName: String, val color: Color, val icon: String) {
    WORK("Work", Color(0xFF2196F3), "💼"),
    PERSONAL("Personal", Color(0xFF9C27B0), "👤"),
    SHOPPING("Shopping", Color(0xFFFF5722), "🛒"),
    HEALTH("Health", Color(0xFF4CAF50), "❤️")
}

// data/model/Task.kt
data class Task(
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val category: Category,
    val priority: Priority,
    val dueDate: Long? = null,  // Timestamp
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
```

### 2. Database Layer

```kotlin
// data/local/TaskEntity.kt
package com.example.taskmaster.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.example.taskmaster.data.model.Category
import com.example.taskmaster.data.model.Priority
import com.example.taskmaster.data.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val category: Category,
    val priority: Priority,
    val dueDate: Long?,
    val isCompleted: Boolean,
    val createdAt: Long,
    val completedAt: Long?
)

// Converters
class Converters {
    @TypeConverter
    fun fromCategory(value: Category): String = value.name

    @TypeConverter
    fun toCategory(value: String): Category = Category.valueOf(value)

    @TypeConverter
    fun fromPriority(value: Priority): String = value.name

    @TypeConverter
    fun toPriority(value: String): Priority = Priority.valueOf(value)
}

// Extension functions
fun TaskEntity.toTask() = Task(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority,
    dueDate = dueDate,
    isCompleted = isCompleted,
    createdAt = createdAt,
    completedAt = completedAt
)

fun Task.toEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    priority = priority,
    dueDate = dueDate,
    isCompleted = isCompleted,
    createdAt = createdAt,
    completedAt = completedAt
)

// data/local/TaskDao.kt
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTask(taskId: Int): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted ORDER BY createdAt DESC")
    fun getTasksByStatus(isCompleted: Boolean): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY createdAt DESC")
    fun getTasksByCategory(category: Category): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY createdAt DESC")
    fun getTasksByPriority(priority: Priority): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE isCompleted = 1")
    fun getCompletedTaskCount(): Flow<Int>
}

// data/local/TaskDatabase.kt
@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
```

### 3. Repository

```kotlin
// data/repository/TaskRepository.kt
package com.example.taskmaster.data.repository

import com.example.taskmaster.data.local.TaskDao
import com.example.taskmaster.data.local.toEntity
import com.example.taskmaster.data.local.toTask
import com.example.taskmaster.data.model.Category
import com.example.taskmaster.data.model.Priority
import com.example.taskmaster.data.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { entities -> entities.map { it.toTask() } }

    fun getTask(taskId: Int): Flow<Task?> =
        taskDao.getTask(taskId).map { it?.toTask() }

    fun getActiveTasks(): Flow<List<Task>> =
        taskDao.getTasksByStatus(false).map { entities -> entities.map { it.toTask() } }

    fun getCompletedTasks(): Flow<List<Task>> =
        taskDao.getTasksByStatus(true).map { entities -> entities.map { it.toTask() } }

    fun getTasksByCategory(category: Category): Flow<List<Task>> =
        taskDao.getTasksByCategory(category).map { entities -> entities.map { it.toTask() } }

    fun getTasksByPriority(priority: Priority): Flow<List<Task>> =
        taskDao.getTasksByPriority(priority).map { entities -> entities.map { it.toTask() } }

    fun searchTasks(query: String): Flow<List<Task>> =
        taskDao.searchTasks(query).map { entities -> entities.map { it.toTask() } }

    fun getTaskCount(): Flow<Int> = taskDao.getTaskCount()

    fun getCompletedTaskCount(): Flow<Int> = taskDao.getCompletedTaskCount()

    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task.toEntity())
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    suspend fun toggleTaskCompletion(task: Task) {
        val updated = task.copy(
            isCompleted = !task.isCompleted,
            completedAt = if (!task.isCompleted) System.currentTimeMillis() else null
        )
        updateTask(updated)
    }
}
```

### 4. Dependency Injection

```kotlin
// di/AppModule.kt
package com.example.taskmaster.di

import android.content.Context
import androidx.room.Room
import com.example.taskmaster.data.local.TaskDao
import com.example.taskmaster.data.local.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(
        @ApplicationContext context: Context
    ): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "task_database"
        ).build()
    }

    @Provides
    fun provideTaskDao(database: TaskDatabase): TaskDao {
        return database.taskDao()
    }
}
```

### 5. Home Screen

```kotlin
// ui/screens/home/HomeViewModel.kt
package com.example.taskmaster.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.data.model.Category
import com.example.taskmaster.data.model.Priority
import com.example.taskmaster.data.model.Task
import com.example.taskmaster.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TaskFilter { ALL, ACTIVE, COMPLETED }

data class HomeUiState(
    val tasks: List<Task> = emptyList(),
    val filter: TaskFilter = TaskFilter.ALL,
    val selectedCategory: Category? = null,
    val selectedPriority: Priority? = null,
    val searchQuery: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(TaskFilter.ALL)
    private val _selectedCategory = MutableStateFlow<Category?>(null)
    private val _selectedPriority = MutableStateFlow<Priority?>(null)
    private val _searchQuery = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        _filter,
        _selectedCategory,
        _selectedPriority,
        _searchQuery
    ) { filter, category, priority, query ->
        HomeUiState(
            filter = filter,
            selectedCategory = category,
            selectedPriority = priority,
            searchQuery = query
        )
    }.flatMapLatest { state ->
        getFilteredTasks(state).map { tasks ->
            state.copy(tasks = tasks)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    private fun getFilteredTasks(state: HomeUiState): Flow<List<Task>> {
        var flow = when (state.filter) {
            TaskFilter.ALL -> repository.getAllTasks()
            TaskFilter.ACTIVE -> repository.getActiveTasks()
            TaskFilter.COMPLETED -> repository.getCompletedTasks()
        }

        return flow.map { tasks ->
            var filtered = tasks

            // Filter by category
            state.selectedCategory?.let { category ->
                filtered = filtered.filter { it.category == category }
            }

            // Filter by priority
            state.selectedPriority?.let { priority ->
                filtered = filtered.filter { it.priority == priority }
            }

            // Search
            if (state.searchQuery.isNotBlank()) {
                filtered = filtered.filter {
                    it.title.contains(state.searchQuery, ignoreCase = true) ||
                    it.description.contains(state.searchQuery, ignoreCase = true)
                }
            }

            filtered
        }
    }

    fun setFilter(filter: TaskFilter) {
        _filter.value = filter
    }

    fun setCategory(category: Category?) {
        _selectedCategory.value = category
    }

    fun setPriority(priority: Priority?) {
        _selectedPriority.value = priority
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompletion(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}

// ui/screens/home/HomeScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToAddTask: () -> Unit,
    onNavigateToEditTask: (Int) -> Unit,
    onNavigateToStatistics: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TaskMaster") },
                actions = {
                    IconButton(onClick = onNavigateToStatistics) {
                        Icon(Icons.Default.BarChart, contentDescription = "Statistics")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAddTask) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search tasks...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )

            // Filter chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.filter == TaskFilter.ALL,
                        onClick = { viewModel.setFilter(TaskFilter.ALL) },
                        label = { Text("All") }
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filter == TaskFilter.ACTIVE,
                        onClick = { viewModel.setFilter(TaskFilter.ACTIVE) },
                        label = { Text("Active") }
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filter == TaskFilter.COMPLETED,
                        onClick = { viewModel.setFilter(TaskFilter.COMPLETED) },
                        label = { Text("Completed") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category filters
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Category.values()) { category ->
                    FilterChip(
                        selected = uiState.selectedCategory == category,
                        onClick = {
                            viewModel.setCategory(
                                if (uiState.selectedCategory == category) null else category
                            )
                        },
                        label = { Text("${category.icon} ${category.displayName}") }
                    )
                }
            }

            // Task list
            if (uiState.tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tasks found", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onClick = { onNavigateToEditTask(task.id) },
                            onDelete = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}

// ui/components/TaskItem.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(
                containerColor = if (task.isCompleted) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surface
                }
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleComplete() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = if (task.isCompleted) {
                            TextDecoration.LineThrough
                        } else null
                    )

                    if (task.description.isNotBlank()) {
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Category chip
                        AssistChip(
                            onClick = { },
                            label = { Text(task.category.icon, fontSize = 12.sp) },
                            modifier = Modifier.height(24.dp)
                        )

                        // Priority badge
                        Surface(
                            color = task.priority.color,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                task.priority.displayName,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }

                        // Due date
                        task.dueDate?.let { dueDate ->
                            val isOverdue = dueDate < System.currentTimeMillis() && !task.isCompleted
                            Text(
                                text = formatDate(dueDate),
                                fontSize = 12.sp,
                                color = if (isOverdue) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
```

### 6. Add/Edit Screen

```kotlin
// ui/screens/addEdit/AddEditViewModel.kt
@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Int = savedStateHandle.get<Int>("taskId") ?: 0

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        if (taskId > 0) {
            viewModelScope.launch {
                repository.getTask(taskId).collectLatest { task ->
                    task?.let {
                        _uiState.value = AddEditUiState(
                            title = it.title,
                            description = it.description,
                            category = it.category,
                            priority = it.priority,
                            dueDate = it.dueDate,
                            isEdit = true
                        )
                    }
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun updateCategory(category: Category) {
        _uiState.value = _uiState.value.copy(category = category)
    }

    fun updatePriority(priority: Priority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }

    fun updateDueDate(dueDate: Long?) {
        _uiState.value = _uiState.value.copy(dueDate = dueDate)
    }

    fun saveTask(onComplete: () -> Unit) {
        val state = _uiState.value

        if (state.title.isBlank()) return

        viewModelScope.launch {
            val task = Task(
                id = if (state.isEdit) taskId else 0,
                title = state.title,
                description = state.description,
                category = state.category,
                priority = state.priority,
                dueDate = state.dueDate
            )

            if (state.isEdit) {
                repository.updateTask(task)
            } else {
                repository.insertTask(task)
            }

            onComplete()
        }
    }
}

data class AddEditUiState(
    val title: String = "",
    val description: String = "",
    val category: Category = Category.PERSONAL,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long? = null,
    val isEdit: Boolean = false
)

// ui/screens/addEdit/AddEditScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEdit) "Edit Task" else "New Task") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveTask(onNavigateBack) },
                        enabled = uiState.title.isNotBlank()
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title
            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.updateTitle(it) },
                label = { Text("Title *") },
                modifier = Modifier.fillMaxWidth()
            )

            // Description
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Category
            Text("Category", style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Category.values()) { category ->
                    FilterChip(
                        selected = uiState.category == category,
                        onClick = { viewModel.updateCategory(category) },
                        label = { Text("${category.icon} ${category.displayName}") }
                    )
                }
            }

            // Priority
            Text("Priority", style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Priority.values().forEach { priority ->
                    FilterChip(
                        selected = uiState.priority == priority,
                        onClick = { viewModel.updatePriority(priority) },
                        label = { Text(priority.displayName) }
                    )
                }
            }

            // Due Date (simplified - use DatePicker in production)
            Button(onClick = {
                // Show date picker
            }) {
                Text(
                    if (uiState.dueDate != null) {
                        "Due: ${formatDate(uiState.dueDate!!)}"
                    } else {
                        "Set Due Date"
                    }
                )
            }
        }
    }
}
```

### 7. Statistics Screen

```kotlin
// ui/screens/statistics/StatisticsScreen.kt
@Composable
fun StatisticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary cards
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(
                    title = "Total",
                    value = stats.totalTasks.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Completed",
                    value = stats.completedTasks.toString(),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Active",
                    value = stats.activeTasks.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            // Completion percentage
            LinearProgressIndicator(
                progress = stats.completionPercentage,
                modifier = Modifier.fillMaxWidth()
            )
            Text("${(stats.completionPercentage * 100).toInt()}% Completed")

            // By category
            Text("By Category", style = MaterialTheme.typography.titleMedium)
            stats.byCategory.forEach { (category, count) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${category.icon} ${category.displayName}")
                    Text("$count")
                }
            }
        }
    }
}

@Composable
fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}
```

---

## Extension Challenges

After completing the base project, try these advanced features:

1. **Notifications**
   - Remind user of upcoming due dates
   - Use WorkManager for scheduling

2. **Themes**
   - Light/Dark mode toggle
   - Custom color schemes

3. **Cloud Sync**
   - Firebase integration
   - Sync across devices

4. **Widgets**
   - Home screen widget showing tasks
   - Glance API

5. **Collaboration**
   - Share tasks with others
   - Real-time updates

---

## Testing

```kotlin
@Test
fun `adding task should insert to database`() = runTest {
    val task = Task(
        title = "Test Task",
        category = Category.WORK,
        priority = Priority.HIGH
    )

    repository.insertTask(task)

    val tasks = repository.getAllTasks().first()
    assertTrue(tasks.any { it.title == "Test Task" })
}
```

---

## What You've Learned

✅ Building a complete Android app from scratch
✅ MVVM architecture in practice
✅ Room database for local storage
✅ Navigation between multiple screens
✅ Material Design 3 implementation
✅ State management at scale
✅ Dependency injection with Hilt
✅ Animations and gestures
✅ Production-ready code structure

---

## Congratulations!

You've completed **Part 6: Android Development with Kotlin**!

You can now:
- Build modern Android apps with Jetpack Compose
- Implement MVVM architecture
- Manage local data with Room
- Create beautiful UIs with Material Design 3
- Handle navigation and state management
- Add animations and gestures
- Structure code for maintainability

**Next Steps**:
- Publish your app to Google Play
- Learn advanced topics (WorkManager, Notifications, Services)
- Explore Kotlin Multiplatform
- Contribute to open-source Android projects
- Build your portfolio with real apps

**Keep building, keep learning!** 🚀

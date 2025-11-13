# Lesson 6.6: Networking and APIs

**Estimated Time**: 75 minutes

---

## Introduction

Modern apps rely on network data from REST APIs, whether it's social media posts, weather data, or e-commerce products. Android apps must fetch, parse, and display this data efficiently.

In this lesson, you'll master:
- ✅ Retrofit setup for REST APIs
- ✅ Kotlin Serialization for JSON parsing
- ✅ Coroutines for async network calls
- ✅ Error handling and retry logic
- ✅ Loading states and UI feedback
- ✅ Image loading with Coil

---

## Setup Dependencies

Add in `build.gradle.kts`:

```kotlin
dependencies {
    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.okhttp.logging)

    // Kotlin Serialization
    implementation(libs.kotlinx.serialization.json)

    // Coil for images
    implementation(libs.coil.compose)
}
```

In `gradle/libs.versions.toml`:

```toml
[versions]
retrofit = "2.11.0"
okhttp = "4.12.0"
kotlinxSerialization = "1.7.3"
coil = "2.7.0"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
retrofit-kotlin-serialization = { group = "com.squareup.retrofit2", name = "converter-kotlinx-serialization", version.ref = "retrofit" }
okhttp-logging = { group = "com.squareup.okhttp3", name = "logging-interceptor", version.ref = "okhttp" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }
coil-compose = { group = "io.coil-kt", name = "coil-compose", version.ref = "coil" }

[plugins]
kotlinx-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

Enable serialization plugin in `build.gradle.kts`:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)  // Add this
}
```

Add internet permission in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## Kotlin Serialization

### Data Models

```kotlin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String,
    @SerialName("avatar_url")  // Map JSON field to Kotlin property
    val avatarUrl: String? = null
)

@Serializable
data class Post(
    val id: Int,
    val title: String,
    val body: String,
    @SerialName("user_id")
    val userId: Int
)

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null
)
```

---

## Retrofit Setup

### API Service Interface

```kotlin
import retrofit2.http.*

interface ApiService {
    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: Int): User

    @GET("posts")
    suspend fun getPosts(@Query("userId") userId: Int? = null): List<Post>

    @POST("users")
    suspend fun createUser(@Body user: CreateUserRequest): User

    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: UpdateUserRequest
    ): User

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int)
}

@Serializable
data class CreateUserRequest(
    val name: String,
    val email: String
)

@Serializable
data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null
)
```

### Retrofit Instance

```kotlin
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val json = Json {
        ignoreUnknownKeys = true  // Ignore JSON fields not in data class
        coerceInputValues = true  // Convert null to default values
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
```

---

## Repository Pattern

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class UserRepository(private val apiService: ApiService) {
    suspend fun getUsers(): Result<List<User>> {
        return try {
            val users = apiService.getUsers()
            Result.Success(users)
        } catch (e: Exception) {
            Result.Error("Failed to fetch users: ${e.message}", e)
        }
    }

    suspend fun getUser(userId: Int): Result<User> {
        return try {
            val user = apiService.getUser(userId)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("Failed to fetch user: ${e.message}", e)
        }
    }

    suspend fun createUser(name: String, email: String): Result<User> {
        return try {
            val request = CreateUserRequest(name, email)
            val user = apiService.createUser(request)
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error("Failed to create user: ${e.message}", e)
        }
    }
}
```

---

## ViewModel with Network Calls

```kotlin
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UsersUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class UsersViewModel(
    private val repository: UserRepository = UserRepository(RetrofitClient.apiService)
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            when (val result = repository.getUsers()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        users = result.data,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                is Result.Loading -> {
                    // Already handled above
                }
            }
        }
    }

    fun retry() {
        loadUsers()
    }
}
```

---

## UI with Loading States

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun UsersScreen(
    viewModel: UsersViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                ErrorScreen(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.retry() }
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.users) { user ->
                        UserCard(user = user)
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
fun UserCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(user.name, style = MaterialTheme.typography.titleMedium)
                Text(user.email, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
```

---

## Image Loading with Coil

```kotlin
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

@Composable
fun UserAvatar(url: String?, size: Dp = 48.dp) {
    AsyncImage(
        model = url,
        contentDescription = "User avatar",
        modifier = Modifier
            .size(size)
            .clip(CircleShape),
        placeholder = painterResource(R.drawable.ic_placeholder),
        error = painterResource(R.drawable.ic_error)
    )
}

// Usage
@Composable
fun UserCard(user: User) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(url = user.avatarUrl)

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(user.name, style = MaterialTheme.typography.titleMedium)
                Text(user.email, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
```

---

## Advanced: Pagination

```kotlin
class PaginatedViewModel : ViewModel() {
    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20

    fun loadMore() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val newPosts = apiService.getPosts(
                    page = currentPage,
                    limit = pageSize
                )

                _posts.value = _posts.value + newPosts
                currentPage++
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@Composable
fun PaginatedList(viewModel: PaginatedViewModel = viewModel()) {
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && lastVisibleIndex >= posts.size - 5) {
                    viewModel.loadMore()
                }
            }
    }

    LazyColumn(state = listState) {
        items(posts) { post ->
            PostCard(post)
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
```

---

## Exercise 1: Fetch and Display Users

Create a screen that fetches users from JSONPlaceholder API:
- Display list of users
- Show loading spinner
- Handle errors with retry button

---

## Solution 1

```kotlin
// Already covered in main content - see UsersScreen implementation above
```

---

## Exercise 2: Search Functionality

Add search to filter users:
- Search input field
- Filter users by name
- Debounce search input

---

## Solution 2

```kotlin
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.delay

data class SearchUiState(
    val allUsers: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SearchViewModel(
    private val repository: UserRepository = UserRepository(RetrofitClient.apiService)
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        loadUsers()

        // Debounced search
        viewModelScope.launch {
            searchQuery
                .debounce(300)  // Wait 300ms after user stops typing
                .collect { query ->
                    filterUsers(query)
                }
        }
    }

    private fun loadUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            when (val result = repository.getUsers()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        allUsers = result.data,
                        filteredUsers = result.data,
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
                else -> {}
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchQuery.value = query
    }

    private fun filterUsers(query: String) {
        val filtered = if (query.isEmpty()) {
            _uiState.value.allUsers
        } else {
            _uiState.value.allUsers.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.email.contains(query, ignoreCase = true)
            }
        }

        _uiState.value = _uiState.value.copy(filteredUsers = filtered)
    }
}

@Composable
fun SearchUsersScreen(viewModel: SearchViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Search field
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onSearchQueryChange(it) },
            label = { Text("Search users") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Results
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.filteredUsers) { user ->
                    UserCard(user)
                }

                if (uiState.filteredUsers.isEmpty() && uiState.searchQuery.isNotEmpty()) {
                    item {
                        Text(
                            "No users found",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}
```

---

## Exercise 3: Post Details with Comments

Create a post details screen:
- Fetch post by ID
- Load and display comments
- Pull to refresh

---

## Solution 3

```kotlin
@Serializable
data class Comment(
    val id: Int,
    val postId: Int,
    val name: String,
    val email: String,
    val body: String
)

interface ApiService {
    // ... previous methods

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") postId: Int): Post

    @GET("posts/{id}/comments")
    suspend fun getComments(@Path("id") postId: Int): List<Comment>
}

data class PostDetailsUiState(
    val post: Post? = null,
    val comments: List<Comment> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PostDetailsViewModel(
    private val postId: Int,
    private val apiService: ApiService = RetrofitClient.apiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostDetailsUiState())
    val uiState: StateFlow<PostDetailsUiState> = _uiState.asStateFlow()

    init {
        loadPost()
    }

    fun loadPost() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val post = apiService.getPost(postId)
                val comments = apiService.getComments(postId)

                _uiState.value = _uiState.value.copy(
                    post = post,
                    comments = comments,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load post: ${e.message}"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    postId: Int,
    onBack: () -> Unit,
    viewModel: PostDetailsViewModel = remember { PostDetailsViewModel(postId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Post Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            if (uiState.isLoading && uiState.post == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                ErrorScreen(
                    message = uiState.errorMessage!!,
                    onRetry = { viewModel.loadPost() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        uiState.post?.let { post ->
                            Text(
                                post.title,
                                style = MaterialTheme.typography.headlineMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                post.body,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                "Comments (${uiState.comments.size})",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    items(uiState.comments) { comment ->
                        CommentCard(comment)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CommentCard(comment: Comment) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(comment.name, style = MaterialTheme.typography.titleSmall)
            Text(
                comment.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

---

## Why This Matters

**Statistics**:
- **90%** of apps use network data
- Apps with fast loading are **3x** more likely to be used daily
- Good error handling reduces support tickets by **60%**

---

## Checkpoint Quiz

### Question 1
What is Retrofit used for?

A) Image loading
B) Making HTTP API calls
C) Database access
D) UI rendering

### Question 2
Why use `suspend` functions for API calls?

A) They're faster
B) They run on background thread via coroutines
C) They're required by Retrofit
D) They use less memory

### Question 3
What does `@SerialName` do?

A) Serializes data
B) Maps JSON field names to Kotlin property names
C) Creates network request
D) Caches responses

### Question 4
When should you show a loading spinner?

A) Never
B) While fetching data from network
C) Only on first launch
D) After data loads

### Question 5
What is debouncing in search?

A) Canceling previous requests
B) Waiting before executing search (avoid searching on every keystroke)
C) Caching search results
D) Validating input

---

## Quiz Answers

**Question 1: B** - Retrofit is an HTTP client for making API calls
**Question 2: B** - `suspend` enables coroutines for async/background execution
**Question 3: B** - Maps JSON `"user_name"` to Kotlin `userName`
**Question 4: B** - Show loading state during network operations
**Question 5: B** - Delay search execution until user stops typing (e.g., 300ms)

---

## What You've Learned

✅ Setting up Retrofit for REST APIs
✅ Kotlin Serialization for JSON parsing
✅ Repository pattern for data access
✅ Coroutines for async network calls
✅ Error handling and retry logic
✅ Loading states in UI
✅ Image loading with Coil
✅ Pagination and search

---

## Next Steps

In **Lesson 6.7: Local Data Storage**, you'll learn:
- Room database setup
- Entity definitions and DAOs
- Repository pattern with Room
- Flows for reactive data
- DataStore for preferences
- Combining local and remote data

Get ready to persist data locally!

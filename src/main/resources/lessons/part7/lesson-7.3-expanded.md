# Lesson 7.3: Performance Optimization

**Estimated Time**: 85 minutes

---

## Introduction

"Premature optimization is the root of all evil" - Donald Knuth

But **measured, strategic optimization** is the difference between a slow app that users delete and a fast app they love.

In this lesson, you'll master performance optimization for Kotlin applications:
- ✅ Profiling tools to identify bottlenecks
- ✅ Memory management and leak detection
- ✅ Coroutine performance optimization
- ✅ Jetpack Compose recomposition optimization
- ✅ Database query optimization
- ✅ Network performance best practices

By the end, you'll know how to build blazing-fast applications that delight users.

---

## The Golden Rule of Optimization

### Measure First, Optimize Second

**Wrong Approach**:
```kotlin
// "I think this is slow, let me optimize it"
fun processData() {
    // Spend hours optimizing
}
```

**Right Approach**:
```kotlin
// 1. Measure with profiler
// 2. Find actual bottleneck (it's not where you think!)
// 3. Optimize the bottleneck
// 4. Measure again to verify improvement
```

**Why This Matters**:
- 90% of execution time is spent in 10% of code
- Optimizing the wrong code = wasted time
- Profilers show you the **actual** bottlenecks

---

## Profiling Tools

### Android Studio Profiler

**CPU Profiler**:
```
Run → Profile → CPU
```

Shows:
- Which functions take the most time
- Call stack and flame graphs
- Thread activity

**Example Output**:
```
MainActivity.onCreate() - 2.3s
├─ loadData() - 1.8s (⚠️ BOTTLENECK)
│  └─ database.query() - 1.5s
└─ setupUI() - 0.5s
   └─ inflate() - 0.3s
```

**Memory Profiler**:
```
Run → Profile → Memory
```

Shows:
- Memory allocation over time
- Heap dumps
- Memory leaks

**Network Profiler**:
```
Run → Profile → Network
```

Shows:
- Request/response times
- Payload sizes
- Connection duration

### Ktor Server Profiling

**Add Timing Plugin**:
```kotlin
// build.gradle.kts
dependencies {
    implementation("io.ktor:ktor-server-call-logging:2.3.7")
}

// Application.kt
install(CallLogging) {
    level = Level.INFO
    format { call ->
        val duration = call.processingTimeMillis()
        "${call.request.httpMethod.value} ${call.request.path()} - ${duration}ms"
    }
}
```

**Output**:
```
GET /api/users - 45ms
GET /api/products - 850ms ⚠️ SLOW!
POST /api/orders - 120ms
```

---

## Memory Management

### Detecting Memory Leaks

**Common Leak: Activity Reference in ViewModel**:

❌ **Bad**:
```kotlin
class MyViewModel(private val activity: Activity) : ViewModel() {
    // LEAK! Activity can't be garbage collected
    fun doSomething() {
        activity.findViewById(R.id.button)
    }
}
```

✅ **Good**:
```kotlin
class MyViewModel : ViewModel() {
    // No activity reference - safe!
    private val _data = MutableStateFlow<String>("")
    val data: StateFlow<String> = _data.asStateFlow()
}
```

**Common Leak: Coroutine Not Cancelled**:

❌ **Bad**:
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LEAK! Coroutine continues after activity destroyed
        GlobalScope.launch {
            while (true) {
                delay(1000)
                updateUI() // Crashes if activity destroyed
            }
        }
    }
}
```

✅ **Good**:
```kotlin
class MainActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scope.launch {
            while (true) {
                delay(1000)
                updateUI()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel() // Clean up!
    }
}
```

**Better: Use lifecycleScope**:
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // Automatically cancelled when activity destroyed
            while (true) {
                delay(1000)
                updateUI()
            }
        }
    }
}
```

### Memory Leak Detection with LeakCanary

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.13")
}
```

LeakCanary automatically detects leaks and shows:
- What object leaked
- Reference path keeping it alive
- Suggested fix

---

## Coroutine Performance

### Dispatcher Selection

**Wrong Dispatcher = Poor Performance**:

❌ **Bad**:
```kotlin
// Running heavy computation on Main thread
lifecycleScope.launch(Dispatchers.Main) {
    val result = processMillionItems() // FREEZES UI!
    updateUI(result)
}
```

✅ **Good**:
```kotlin
lifecycleScope.launch {
    // Heavy work on background thread
    val result = withContext(Dispatchers.Default) {
        processMillionItems()
    }
    // Update UI on Main thread
    updateUI(result)
}
```

**Dispatcher Guide**:
```kotlin
// CPU-intensive work (calculations, parsing)
withContext(Dispatchers.Default) {
    parseJSON(largeFile)
}

// I/O operations (network, database, files)
withContext(Dispatchers.IO) {
    database.query()
    api.fetch()
    file.readText()
}

// UI updates (always!)
withContext(Dispatchers.Main) {
    textView.text = "Updated"
}
```

### Avoiding Excessive Coroutine Creation

❌ **Bad** (Creates 1000 coroutines):
```kotlin
fun processItems(items: List<Item>) {
    items.forEach { item ->
        scope.launch {
            process(item)
        }
    }
}
```

✅ **Good** (Single coroutine):
```kotlin
fun processItems(items: List<Item>) {
    scope.launch(Dispatchers.Default) {
        items.forEach { item ->
            process(item)
        }
    }
}
```

✅ **Better** (Parallel processing with limit):
```kotlin
suspend fun processItems(items: List<Item>) {
    items.chunked(10).forEach { chunk ->
        coroutineScope {
            chunk.map { item ->
                async(Dispatchers.Default) {
                    process(item)
                }
            }.awaitAll()
        }
    }
}
```

### Flow Performance

**Cold vs Hot Flows**:

❌ **Bad** (Network call on every collect):
```kotlin
fun getUsers(): Flow<List<User>> = flow {
    val users = api.getUsers() // Called every time!
    emit(users)
}

// Each collector makes a new API call
viewModel.users.collect { }
viewModel.users.collect { } // Another API call!
```

✅ **Good** (SharedFlow - single source):
```kotlin
class UserRepository {
    private val _users = MutableSharedFlow<List<User>>(replay = 1)
    val users: SharedFlow<List<User>> = _users.asSharedFlow()

    suspend fun fetchUsers() {
        val users = api.getUsers()
        _users.emit(users)
    }
}

// All collectors share the same data
repository.users.collect { } // No new API call
repository.users.collect { } // No new API call
```

**Debouncing Search**:

❌ **Bad** (API call on every keystroke):
```kotlin
searchField.onTextChanged { query ->
    viewModel.search(query) // API call!
}
```

✅ **Good** (Debounce 300ms):
```kotlin
searchField.textAsFlow()
    .debounce(300)
    .distinctUntilChanged()
    .collectLatest { query ->
        viewModel.search(query)
    }
```

---

## Jetpack Compose Optimization

### Recomposition Basics

**What is Recomposition?**

When state changes, Compose re-runs composables to update UI.

**Problem**: Unnecessary recompositions = poor performance

**Example**:
```kotlin
@Composable
fun CounterScreen() {
    var count by remember { mutableStateOf(0) }

    Column {
        Text("Count: $count") // Recomposes when count changes ✅
        Button(onClick = { count++ }) { Text("Increment") }

        ExpensiveComponent() // ⚠️ Also recomposes! (Unnecessary)
    }
}

@Composable
fun ExpensiveComponent() {
    // Expensive calculation runs on every recomposition
    val result = remember { heavyCalculation() }
    Text("Result: $result")
}
```

### Optimization 1: Stable Parameters

❌ **Bad** (Recomposes unnecessarily):
```kotlin
@Composable
fun UserList(viewModel: UserViewModel) {
    val users = viewModel.users.collectAsState()

    LazyColumn {
        items(users.value) { user ->
            // Recomposes all items when any state in viewModel changes
            UserCard(user, viewModel)
        }
    }
}
```

✅ **Good** (Only necessary recompositions):
```kotlin
@Composable
fun UserList(
    users: List<User>,
    onUserClick: (User) -> Unit
) {
    LazyColumn {
        items(users, key = { it.id }) { user ->
            // Only recomposes when this user changes
            UserCard(user, onClick = { onUserClick(user) })
        }
    }
}
```

### Optimization 2: derivedStateOf

❌ **Bad** (Recalculates on every recomposition):
```kotlin
@Composable
fun ProductList(products: List<Product>) {
    val expensiveProducts = products.filter { it.price > 1000 } // ⚠️

    LazyColumn {
        items(expensiveProducts) { product ->
            ProductCard(product)
        }
    }
}
```

✅ **Good** (Only recalculates when products change):
```kotlin
@Composable
fun ProductList(products: List<Product>) {
    val expensiveProducts by remember {
        derivedStateOf {
            products.filter { it.price > 1000 }
        }
    }

    LazyColumn {
        items(expensiveProducts) { product ->
            ProductCard(product)
        }
    }
}
```

### Optimization 3: LazyColumn Keys

❌ **Bad** (Entire list recomposes):
```kotlin
LazyColumn {
    items(users) { user ->
        UserCard(user)
    }
}
```

✅ **Good** (Only changed items recompose):
```kotlin
LazyColumn {
    items(users, key = { it.id }) { user ->
        UserCard(user)
    }
}
```

### Optimization 4: Immutable Collections

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable:0.3.7")
}
```

✅ **Good** (Compose knows it's immutable):
```kotlin
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun UserList(users: ImmutableList<User>) {
    // Compose can skip recomposition if users reference unchanged
    LazyColumn {
        items(users) { user ->
            UserCard(user)
        }
    }
}
```

### Measuring Recompositions

```kotlin
@Composable
fun LogCompositions(tag: String) {
    val ref = remember { Ref(0) }
    SideEffect {
        ref.value++
        Log.d("Recomposition", "$tag recomposed ${ref.value} times")
    }
}

class Ref(var value: Int)

@Composable
fun MyScreen() {
    LogCompositions("MyScreen")

    // Your content
}
```

---

## Database Optimization

### Query Optimization

❌ **Bad** (N+1 queries):
```kotlin
// Gets all users
val users = userDao.getAll() // 1 query

// For each user, get their orders
users.forEach { user ->
    val orders = orderDao.getByUserId(user.id) // N queries!
    println("${user.name} has ${orders.size} orders")
}
// Total: 1 + N queries
```

✅ **Good** (Single query with JOIN):
```kotlin
@Query("""
    SELECT users.*, COUNT(orders.id) as order_count
    FROM users
    LEFT JOIN orders ON users.id = orders.user_id
    GROUP BY users.id
""")
fun getUsersWithOrderCount(): List<UserWithOrderCount>

// Single query!
val results = userDao.getUsersWithOrderCount()
```

### Indexing

❌ **Bad** (Full table scan):
```kotlin
@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // Frequently queried, but no index!
    val price: Double
)

@Query("SELECT * FROM products WHERE category = :category")
fun getByCategory(category: String): List<Product>
// Scans entire table!
```

✅ **Good** (Indexed):
```kotlin
@Entity(
    tableName = "products",
    indices = [Index(value = ["category"])] // ⚡ Fast lookup
)
data class Product(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val price: Double
)

@Query("SELECT * FROM products WHERE category = :category")
fun getByCategory(category: String): List<Product>
// Uses index - much faster!
```

### Pagination

❌ **Bad** (Load all 10,000 products):
```kotlin
@Query("SELECT * FROM products")
fun getAllProducts(): List<Product> // OOM for large datasets!
```

✅ **Good** (Paging):
```kotlin
@Query("SELECT * FROM products ORDER BY name")
fun getProducts(): PagingSource<Int, Product>

// Usage
val pager = Pager(
    config = PagingConfig(pageSize = 20),
    pagingSourceFactory = { productDao.getProducts() }
)

val products: Flow<PagingData<Product>> = pager.flow
```

### Batch Operations

❌ **Bad** (Individual inserts):
```kotlin
products.forEach { product ->
    database.productDao().insert(product) // Slow!
}
```

✅ **Good** (Batch insert):
```kotlin
@Insert
suspend fun insertAll(products: List<Product>)

// Single transaction - much faster
database.productDao().insertAll(products)
```

---

## Network Optimization

### Response Caching

**HTTP Caching with OkHttp**:
```kotlin
val cacheSize = 10 * 1024 * 1024 // 10 MB
val cache = Cache(context.cacheDir, cacheSize.toLong())

val client = OkHttpClient.Builder()
    .cache(cache)
    .build()

val retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("https://api.example.com")
    .build()
```

**Cache Headers**:
```kotlin
interface ApiService {
    @Headers("Cache-Control: max-age=3600") // Cache for 1 hour
    @GET("products")
    suspend fun getProducts(): List<Product>
}
```

### Compression

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val request = chain.request().newBuilder()
            .header("Accept-Encoding", "gzip")
            .build()
        chain.proceed(request)
    }
    .build()
```

### Request Coalescing

❌ **Bad** (Multiple identical requests):
```kotlin
// Screen 1
viewModel1.loadProducts()

// Screen 2 (at same time)
viewModel2.loadProducts()

// Both make API calls!
```

✅ **Good** (Share single request):
```kotlin
class ProductRepository {
    private var productsDeferred: Deferred<List<Product>>? = null

    suspend fun getProducts(): List<Product> {
        return productsDeferred?.await() ?: run {
            val deferred = scope.async {
                api.getProducts()
            }
            productsDeferred = deferred
            deferred.await().also {
                productsDeferred = null
            }
        }
    }
}

// Both calls wait for same request
viewModel1.loadProducts() // Makes API call
viewModel2.loadProducts() // Uses same request!
```

### Prefetching

```kotlin
class ProductRepository {
    private val cache = mutableMapOf<String, Product>()

    suspend fun prefetchProducts(ids: List<String>) {
        val uncachedIds = ids.filter { it !in cache }
        if (uncachedIds.isEmpty()) return

        val products = api.getProductsBatch(uncachedIds)
        products.forEach { cache[it.id] = it }
    }

    suspend fun getProduct(id: String): Product {
        return cache[id] ?: api.getProduct(id).also {
            cache[id] = it
        }
    }
}

// Usage
repository.prefetchProducts(listOf("1", "2", "3"))
// Later...
val product = repository.getProduct("1") // Instant! (cached)
```

---

## Exercise 1: Optimize a Slow Screen

You have a slow user list screen. Profile and optimize it.

### Initial Code (Slow)

```kotlin
@Composable
fun UserListScreen(viewModel: UserViewModel) {
    val users = viewModel.users.collectAsState()
    val searchQuery = viewModel.searchQuery.collectAsState()

    Column {
        SearchBar(
            query = searchQuery.value,
            onQueryChange = { viewModel.updateSearchQuery(it) }
        )

        LazyColumn {
            items(users.value) { user ->
                UserCard(
                    user = user,
                    isOnline = viewModel.isUserOnline(user.id),
                    messageCount = viewModel.getUnreadCount(user.id),
                    onClick = { viewModel.selectUser(user) }
                )
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    isOnline: Boolean,
    messageCount: Int,
    onClick: () -> Unit
) {
    // Heavy image loading
    val avatar = loadImageFromNetwork(user.avatarUrl)

    Card(onClick = onClick) {
        Row {
            Image(bitmap = avatar, contentDescription = null)
            Column {
                Text(user.name)
                Text("Unread: $messageCount")
                if (isOnline) {
                    OnlineBadge()
                }
            }
        }
    }
}
```

### Performance Issues

1. ⚠️ Entire list recomposes when search query changes
2. ⚠️ No keys in LazyColumn
3. ⚠️ `isUserOnline()` and `getUnreadCount()` called on every recomposition
4. ⚠️ Images loaded from network on every recomposition
5. ⚠️ ViewModel passed to composable (unstable parameter)

---

## Solution 1

```kotlin
@Stable
data class UserUiState(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val isOnline: Boolean,
    val unreadCount: Int
)

@Composable
fun UserListScreen(viewModel: UserViewModel) {
    val users by viewModel.usersUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column {
        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::updateSearchQuery
        )

        // derivedStateOf - only recalculate when users or query changes
        val filteredUsers by remember {
            derivedStateOf {
                if (searchQuery.isBlank()) {
                    users
                } else {
                    users.filter { it.name.contains(searchQuery, ignoreCase = true) }
                }
            }
        }

        UserList(
            users = filteredUsers,
            onUserClick = viewModel::selectUser
        )
    }
}

@Composable
fun UserList(
    users: List<UserUiState>,
    onUserClick: (String) -> Unit
) {
    LazyColumn {
        items(
            items = users,
            key = { it.id } // ✅ Stable keys
        ) { user ->
            UserCard(
                user = user,
                onClick = { onUserClick(user.id) }
            )
        }
    }
}

@Composable
fun UserCard(
    user: UserUiState,
    onClick: () -> Unit
) {
    Card(onClick = onClick) {
        Row {
            // ✅ Coil handles caching
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Column {
                Text(user.name)
                Text("Unread: ${user.unreadCount}")

                if (user.isOnline) {
                    OnlineBadge()
                }
            }
        }
    }
}

class UserViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Pre-compute UI state in ViewModel
    val usersUiState: StateFlow<List<UserUiState>> = combine(
        userRepository.users,
        onlineStatusRepository.onlineUsers,
        messageRepository.unreadCounts
    ) { users, onlineIds, unreadCounts ->
        users.map { user ->
            UserUiState(
                id = user.id,
                name = user.name,
                avatarUrl = user.avatarUrl,
                isOnline = user.id in onlineIds,
                unreadCount = unreadCounts[user.id] ?: 0
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectUser(userId: String) {
        // Handle selection
    }
}
```

**Improvements**:
1. ✅ Stable parameters (`UserUiState`, lambda references)
2. ✅ Keys in LazyColumn
3. ✅ UI state pre-computed in ViewModel
4. ✅ Image loading with Coil (handles caching)
5. ✅ `derivedStateOf` for filtering
6. ✅ No ViewModel passed to composables

---

## Exercise 2: Optimize Database Queries

Optimize this slow order fetching code.

### Initial Code (Slow)

```kotlin
@Dao
interface OrderDao {
    @Query("SELECT * FROM orders")
    fun getAllOrders(): List<Order>

    @Query("SELECT * FROM orders WHERE user_id = :userId")
    fun getOrdersByUser(userId: String): List<Order>
}

// Usage
fun displayUserOrders(userId: String) {
    val orders = orderDao.getOrdersByUser(userId)

    orders.forEach { order ->
        val user = userDao.getById(order.userId) // N+1 query!
        val items = orderItemDao.getByOrderId(order.id) // N+1 query!

        println("Order ${order.id} by ${user.name}: ${items.size} items")
    }
}
```

---

## Solution 2

```kotlin
// 1. Add indexes
@Entity(
    tableName = "orders",
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["created_at"])
    ]
)
data class Order(
    @PrimaryKey val id: String,
    val userId: String,
    val totalAmount: Double,
    val status: String,
    val createdAt: Long
)

// 2. Create joined data class
data class OrderWithDetails(
    @Embedded val order: Order,

    @Relation(
        parentColumn = "user_id",
        entityColumn = "id"
    )
    val user: User,

    @Relation(
        parentColumn = "id",
        entityColumn = "order_id"
    )
    val items: List<OrderItem>
)

// 3. Single query with JOIN
@Dao
interface OrderDao {
    @Transaction
    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC")
    fun getOrdersWithDetails(userId: String): List<OrderWithDetails>

    // For pagination
    @Transaction
    @Query("SELECT * FROM orders WHERE user_id = :userId ORDER BY created_at DESC")
    fun getOrdersWithDetailsPaged(userId: String): PagingSource<Int, OrderWithDetails>
}

// Usage
fun displayUserOrders(userId: String) {
    val ordersWithDetails = orderDao.getOrdersWithDetails(userId) // Single query!

    ordersWithDetails.forEach { orderDetail ->
        println("Order ${orderDetail.order.id} by ${orderDetail.user.name}: ${orderDetail.items.size} items")
    }
}

// For large datasets, use paging
fun getOrdersPaged(userId: String): Flow<PagingData<OrderWithDetails>> {
    return Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { orderDao.getOrdersWithDetailsPaged(userId) }
    ).flow
}
```

---

## Exercise 3: Optimize Network Calls

Create an optimized image loading repository with caching and prefetching.

---

## Solution 3

```kotlin
class ImageRepository(
    private val api: ImageApi,
    private val diskCache: DiskLruCache,
    private val memoryCache: LruCache<String, Bitmap>
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // In-flight requests to avoid duplicates
    private val loadingImages = mutableMapOf<String, Deferred<Bitmap>>()

    suspend fun loadImage(url: String): Bitmap? {
        // 1. Check memory cache (fastest)
        memoryCache.get(url)?.let { return it }

        // 2. Check disk cache
        diskCache.get(url)?.let { bytes ->
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            memoryCache.put(url, bitmap)
            return bitmap
        }

        // 3. Coalesce network requests
        return loadingImages[url]?.await() ?: run {
            val deferred = scope.async {
                downloadAndCache(url)
            }
            loadingImages[url] = deferred

            try {
                deferred.await().also {
                    loadingImages.remove(url)
                }
            } catch (e: Exception) {
                loadingImages.remove(url)
                null
            }
        }
    }

    private suspend fun downloadAndCache(url: String): Bitmap {
        val bytes = api.downloadImage(url)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // Cache in memory
        memoryCache.put(url, bitmap)

        // Cache on disk
        diskCache.put(url, bytes)

        return bitmap
    }

    fun prefetch(urls: List<String>) {
        scope.launch {
            urls.forEach { url ->
                if (url !in memoryCache && url !in diskCache) {
                    try {
                        loadImage(url)
                    } catch (e: Exception) {
                        // Ignore prefetch errors
                    }
                }
            }
        }
    }

    fun clearCache() {
        memoryCache.evictAll()
        diskCache.delete()
    }
}

// Usage
class ProductListViewModel(private val imageRepo: ImageRepository) : ViewModel() {
    fun loadProducts(products: List<Product>) {
        // Prefetch images for visible products
        val imageUrls = products.take(10).map { it.imageUrl }
        imageRepo.prefetch(imageUrls)
    }
}
```

---

## Why This Matters

### Real-World Impact

**Performance Statistics**:
- 53% of users abandon apps that take > 3 seconds to load
- 1-second delay = 7% reduction in conversions
- Google ranks faster sites higher in search

**Business Impact**:
- **Amazon**: 100ms faster = 1% more revenue
- **Pinterest**: 40% reduction in wait time = 15% more signups
- **Shopify**: Faster stores convert 1.2x better

**Career Impact**:
- Performance optimization is a senior-level skill
- Companies pay 20-30% more for engineers who can optimize

---

## Checkpoint Quiz

### Question 1
What's the first step in performance optimization?

A) Rewrite everything in C
B) Profile to find bottlenecks
C) Optimize all loops
D) Remove all logging

### Question 2
Which Dispatcher should you use for heavy calculations?

A) Dispatchers.Main
B) Dispatchers.IO
C) Dispatchers.Default
D) Dispatchers.Unconfined

### Question 3
How do you prevent unnecessary Compose recompositions?

A) Use var instead of mutableStateOf
B) Use stable parameters and keys in LazyColumn
C) Disable recomposition in settings
D) Recomposition can't be prevented

### Question 4
What's the N+1 query problem?

A) A query that returns N+1 rows
B) Making N additional queries in a loop
C) A query with N+1 joins
D) A query error code

### Question 5
What does `derivedStateOf` do?

A) Creates a new state
B) Only recalculates when dependencies change
C) Derives state from database
D) Deletes old state

---

## Quiz Answers

**Question 1: B) Profile to find bottlenecks**

Always measure first:
1. Profile with Android Studio Profiler
2. Find the actual bottleneck
3. Optimize that specific code
4. Measure again to verify

90% of time is in 10% of code - find that 10%!

---

**Question 2: C) Dispatchers.Default**

```kotlin
// Heavy CPU work
withContext(Dispatchers.Default) {
    parseJSON()
    calculatePi()
}

// I/O operations
withContext(Dispatchers.IO) {
    api.fetch()
    database.query()
}

// UI updates
withContext(Dispatchers.Main) {
    textView.text = "Done"
}
```

---

**Question 3: B) Use stable parameters and keys in LazyColumn**

```kotlin
// Stable parameters
@Composable
fun UserCard(user: User, onClick: (String) -> Unit)

// Keys in LazyColumn
LazyColumn {
    items(users, key = { it.id }) { user ->
        UserCard(user, onClick)
    }
}
```

---

**Question 4: B) Making N additional queries in a loop**

```kotlin
// Bad: 1 + N queries
val users = getUsers() // 1
users.forEach { user ->
    val orders = getOrders(user.id) // N
}

// Good: 1 query with JOIN
val usersWithOrders = getUsersWithOrders()
```

---

**Question 5: B) Only recalculates when dependencies change**

```kotlin
val filteredItems by remember {
    derivedStateOf {
        items.filter { it.price > 100 }
    }
}
// Only recalculates when 'items' changes
```

---

## What You've Learned

✅ The golden rule: measure first, optimize second
✅ Using Android Studio Profiler to find bottlenecks
✅ Memory leak detection and prevention
✅ Coroutine performance optimization (dispatchers, flow)
✅ Jetpack Compose recomposition optimization
✅ Database optimization (indexing, joins, paging)
✅ Network optimization (caching, compression, prefetching)
✅ Practical optimization exercises

---

## Next Steps

In **Lesson 7.4: Security Best Practices**, you'll learn:
- Secure coding practices
- Input validation and sanitization
- Encryption and hashing
- API security (OAuth 2.0, JWT best practices)
- Android security (KeyStore, ProGuard)
- Common vulnerabilities (OWASP Top 10)

Fast apps are great, but secure apps are essential!

---

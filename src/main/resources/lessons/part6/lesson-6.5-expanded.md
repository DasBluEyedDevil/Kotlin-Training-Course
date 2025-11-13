# Lesson 6.5: Navigation

**Estimated Time**: 75 minutes

---

## Introduction

Multi-screen navigation is essential for modern apps. Users expect smooth transitions between screens, deep linking support, and logical app flow.

**Jetpack Navigation for Compose** provides a type-safe, declarative way to handle navigation with full integration into Compose.

In this lesson, you'll master:
- ✅ Navigation component setup
- ✅ NavHost and NavController
- ✅ Route definitions and navigation
- ✅ Passing arguments between screens
- ✅ Bottom navigation bars
- ✅ Navigation drawer
- ✅ Deep linking

---

## Setup

Add navigation dependency in `build.gradle.kts`:

```kotlin
dependencies {
    implementation(libs.androidx.navigation.compose)
}
```

In `gradle/libs.versions.toml`:

```toml
[versions]
navigation = "2.8.4"

[libraries]
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigation" }
```

---

## Basic Navigation

### NavController

**NavController** manages navigation between screens:

```kotlin
import androidx.navigation.compose.rememberNavController

@Composable
fun MyApp() {
    val navController = rememberNavController()

    // Use navController to navigate
}
```

### NavHost

**NavHost** defines navigation graph (screens and routes):

```kotlin
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(onNavigateToProfile = {
                navController.navigate("profile")
            })
        }

        composable("profile") {
            ProfileScreen(onNavigateBack = {
                navController.popBackStack()
            })
        }
    }
}
```

### Screen Composables

```kotlin
@Composable
fun HomeScreen(onNavigateToProfile: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Home Screen", style = MaterialTheme.typography.headlineLarge)

        Button(onClick = onNavigateToProfile) {
            Text("Go to Profile")
        }
    }
}

@Composable
fun ProfileScreen(onNavigateBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Profile Screen", style = MaterialTheme.typography.headlineLarge)

        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}
```

---

## Navigation with Arguments

### Passing Simple Arguments

```kotlin
NavHost(navController = navController, startDestination = "home") {
    composable("home") {
        HomeScreen(onNavigateToDetails = { userId ->
            navController.navigate("details/$userId")
        })
    }

    composable(
        route = "details/{userId}",
        arguments = listOf(navArgument("userId") { type = NavType.IntType })
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
        DetailsScreen(userId = userId)
    }
}

@Composable
fun DetailsScreen(userId: Int) {
    Text("User ID: $userId")
}
```

### Optional Arguments

```kotlin
composable(
    route = "profile?name={name}&age={age}",
    arguments = listOf(
        navArgument("name") {
            type = NavType.StringType
            defaultValue = "Guest"
        },
        navArgument("age") {
            type = NavType.IntType
            defaultValue = 0
        }
    )
) { backStackEntry ->
    val name = backStackEntry.arguments?.getString("name") ?: "Guest"
    val age = backStackEntry.arguments?.getInt("age") ?: 0

    ProfileScreen(name = name, age = age)
}

// Navigate with all args
navController.navigate("profile?name=Alice&age=25")

// Navigate with default args
navController.navigate("profile")
```

### Type-Safe Navigation (Recommended)

```kotlin
// Define routes
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Profile : Screen("profile")
    data class Details(val userId: Int) : Screen("details/$userId") {
        companion object {
            const val route = "details/{userId}"
        }
    }
}

// Navigation graph
NavHost(navController = navController, startDestination = Screen.Home.route) {
    composable(Screen.Home.route) {
        HomeScreen(onNavigateToDetails = { userId ->
            navController.navigate(Screen.Details(userId).route)
        })
    }

    composable(
        route = Screen.Details.route,
        arguments = listOf(navArgument("userId") { type = NavType.IntType })
    ) { backStackEntry ->
        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
        DetailsScreen(userId = userId)
    }
}
```

---

## Bottom Navigation

### Setup

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Search : BottomNavItem("search", Icons.Default.Search, "Search")
    object Profile : BottomNavItem("profile", Icons.Default.Person, "Profile")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Search.route) { SearchScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }
    }
}
```

---

## Navigation Drawer

```kotlin
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import kotlinx.coroutines.launch

sealed class DrawerItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : DrawerItem("home", Icons.Default.Home, "Home")
    object Settings : DrawerItem("settings", Icons.Default.Settings, "Settings")
    object About : DrawerItem("about", Icons.Default.Info, "About")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppWithDrawer() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val drawerItems = listOf(
        DrawerItem.Home,
        DrawerItem.Settings,
        DrawerItem.About
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "My App",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium
                )

                HorizontalDivider()

                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.icon, contentDescription = null) },
                        label = { Text(item.label) },
                        selected = false,
                        onClick = {
                            navController.navigate(item.route)
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("My App") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = DrawerItem.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(DrawerItem.Home.route) { HomeScreen() }
                composable(DrawerItem.Settings.route) { SettingsScreen() }
                composable(DrawerItem.About.route) { AboutScreen() }
            }
        }
    }
}
```

---

## Nested Navigation

```kotlin
// Main navigation graph
NavHost(navController = mainNavController, startDestination = "main") {
    // Auth flow
    navigation(startDestination = "login", route = "auth") {
        composable("login") { LoginScreen() }
        composable("register") { RegisterScreen() }
    }

    // Main app flow
    navigation(startDestination = "home", route = "main") {
        composable("home") { HomeScreen() }
        composable("profile") { ProfileScreen() }

        // Nested settings flow
        navigation(startDestination = "settings_main", route = "settings") {
            composable("settings_main") { SettingsScreen() }
            composable("settings_account") { AccountSettingsScreen() }
            composable("settings_privacy") { PrivacySettingsScreen() }
        }
    }
}
```

---

## Deep Linking

### Setup in Manifest

```xml
<!-- AndroidManifest.xml -->
<activity
    android:name=".MainActivity"
    android:exported="true">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>

    <!-- Deep link -->
    <intent-filter>
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.DEFAULT" />
        <category android:name="android.intent.category.BROWSABLE" />
        <data
            android:scheme="myapp"
            android:host="profile" />
    </intent-filter>
</activity>
```

### Deep Link in NavGraph

```kotlin
composable(
    route = "profile/{userId}",
    arguments = listOf(navArgument("userId") { type = NavType.IntType }),
    deepLinks = listOf(navDeepLink { uriPattern = "myapp://profile/{userId}" })
) { backStackEntry ->
    val userId = backStackEntry.arguments?.getInt("userId") ?: 0
    ProfileScreen(userId = userId)
}

// Users can open: myapp://profile/123
// App navigates directly to ProfileScreen with userId=123
```

---

## Exercise 1: Multi-Screen App

Create an app with 3 screens:
1. **Home**: List of products, click to see details
2. **Details**: Show product info, navigate back
3. **Cart**: Show selected items

### Requirements
- Bottom navigation (Home, Cart)
- Pass product ID to details
- Back button on details screen

---

## Solution 1

```kotlin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

data class Product(val id: Int, val name: String, val price: Double)

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Cart : Screen("cart")
    data class Details(val productId: Int) : Screen("details/$productId") {
        companion object {
            const val route = "details/{productId}"
        }
    }
}

@Composable
fun ShoppingApp() {
    val navController = rememberNavController()
    val cart = remember { mutableStateListOf<Product>() }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, cartCount = cart.size)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onProductClick = { productId ->
                        navController.navigate(Screen.Details(productId).route)
                    }
                )
            }

            composable(
                route = Screen.Details.route,
                arguments = listOf(navArgument("productId") { type = NavType.IntType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                DetailsScreen(
                    productId = productId,
                    onAddToCart = { product -> cart.add(product) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    items = cart,
                    onRemove = { product -> cart.remove(product) }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, cartCount: Int) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        )

        NavigationBarItem(
            icon = {
                BadgedBox(badge = {
                    if (cartCount > 0) {
                        Badge { Text("$cartCount") }
                    }
                }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                }
            },
            label = { Text("Cart") },
            selected = currentRoute == Screen.Cart.route,
            onClick = {
                navController.navigate(Screen.Cart.route) {
                    popUpTo(Screen.Home.route)
                }
            }
        )
    }
}

@Composable
fun HomeScreen(onProductClick: (Int) -> Unit) {
    val products = remember {
        listOf(
            Product(1, "Laptop", 999.99),
            Product(2, "Mouse", 29.99),
            Product(3, "Keyboard", 79.99),
            Product(4, "Monitor", 299.99)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(products) { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onProductClick(product.id) }
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(product.name, style = MaterialTheme.typography.titleMedium)
                        Text("$${product.price}", color = MaterialTheme.colorScheme.primary)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    productId: Int,
    onAddToCart: (Product) -> Unit,
    onBack: () -> Unit
) {
    val product = remember {
        listOf(
            Product(1, "Laptop", 999.99),
            Product(2, "Mouse", 29.99),
            Product(3, "Keyboard", 79.99),
            Product(4, "Monitor", 299.99)
        ).find { it.id == productId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .padding(16.dp)
        ) {
            product?.let { p ->
                Text(p.name, style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Price: $${p.price}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = { onAddToCart(p) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.AddShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to Cart")
                }
            }
        }
    }
}

@Composable
fun CartScreen(items: List<Product>, onRemove: (Product) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Cart (${items.size} items)", style = MaterialTheme.typography.headlineMedium)

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Cart is empty")
            }
        } else {
            val total = items.sumOf { it.price }

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { product ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(product.name)
                                Text("$${product.price}", color = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onRemove(product) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove")
                            }
                        }
                    }
                }
            }

            HorizontalDivider()
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", style = MaterialTheme.typography.titleLarge)
                Text("$${String.format("%.2f", total)}", style = MaterialTheme.typography.titleLarge)
            }
            Button(onClick = { }, modifier = Modifier.fillMaxWidth()) {
                Text("Checkout")
            }
        }
    }
}
```

---

## Exercise 2: Settings with Nested Navigation

Create a settings screen with nested navigation:
- Main settings (General, Account, Privacy)
- Each opens a sub-screen
- Back navigation works correctly

---

## Solution 2

```kotlin
sealed class SettingsScreen(val route: String, val title: String) {
    object Main : SettingsScreen("settings_main", "Settings")
    object General : SettingsScreen("settings_general", "General")
    object Account : SettingsScreen("settings_account", "Account")
    object Privacy : SettingsScreen("settings_privacy", "Privacy")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsApp() {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val title = when (currentRoute) {
                SettingsScreen.General.route -> SettingsScreen.General.title
                SettingsScreen.Account.route -> SettingsScreen.Account.title
                SettingsScreen.Privacy.route -> SettingsScreen.Privacy.title
                else -> SettingsScreen.Main.title
            }

            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (currentRoute != SettingsScreen.Main.route) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SettingsScreen.Main.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(SettingsScreen.Main.route) {
                SettingsMainScreen(onNavigate = { route ->
                    navController.navigate(route)
                })
            }

            composable(SettingsScreen.General.route) {
                GeneralSettingsScreen()
            }

            composable(SettingsScreen.Account.route) {
                AccountSettingsScreen()
            }

            composable(SettingsScreen.Privacy.route) {
                PrivacySettingsScreen()
            }
        }
    }
}

@Composable
fun SettingsMainScreen(onNavigate: (String) -> Unit) {
    LazyColumn {
        item {
            SettingsItem(
                title = "General",
                subtitle = "App preferences",
                icon = Icons.Default.Settings,
                onClick = { onNavigate(SettingsScreen.General.route) }
            )
        }
        item {
            SettingsItem(
                title = "Account",
                subtitle = "Manage your account",
                icon = Icons.Default.Person,
                onClick = { onNavigate(SettingsScreen.Account.route) }
            )
        }
        item {
            SettingsItem(
                title = "Privacy",
                subtitle = "Privacy and security",
                icon = Icons.Default.Lock,
                onClick = { onNavigate(SettingsScreen.Privacy.route) }
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
}

@Composable
fun GeneralSettingsScreen() {
    var darkMode by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        SwitchSetting("Dark Mode", darkMode) { darkMode = it }
        SwitchSetting("Notifications", notifications) { notifications = it }
    }
}

@Composable
fun AccountSettingsScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Email: user@example.com")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { }) {
            Text("Change Password")
        }
    }
}

@Composable
fun PrivacySettingsScreen() {
    var analytics by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        SwitchSetting("Share Analytics", analytics) { analytics = it }
    }
}

@Composable
fun SwitchSetting(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
```

---

## Exercise 3: Tab Navigation

Create a tabbed interface:
- 3 tabs: Feed, Discover, Profile
- Use TabRow
- Content changes based on selected tab

---

## Solution 3

```kotlin
@Composable
fun TabbedApp() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Feed", "Discover", "Profile")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTab) {
            0 -> FeedScreen()
            1 -> DiscoverScreen()
            2 -> ProfileScreen()
        }
    }
}

@Composable
fun FeedScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text("Feed Content", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun DiscoverScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text("Discover Content", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Text("Profile Content", style = MaterialTheme.typography.headlineMedium)
    }
}
```

---

## Why This Matters

**User Expectations**:
- 📱 Smooth transitions between screens
- ↩️ Back button works correctly
- 🔗 Deep links open correct screens
- 💾 State preserved during navigation

**Statistics**:
- Apps with poor navigation have **75% higher** uninstall rates
- Users abandon apps if they can't find features within **3 taps**
- Deep linking increases engagement by **2x**

---

## Checkpoint Quiz

### Question 1
What is NavController responsible for?

A) Creating screens
B) Managing navigation between destinations
C) Displaying UI
D) Handling user input

### Question 2
How do you pass arguments between screens?

A) Global variables
B) Route parameters like "details/{id}"
C) Shared preferences
D) Broadcast receivers

### Question 3
What does `popBackStack()` do?

A) Deletes all screens
B) Navigates back to previous screen
C) Opens a dialog
D) Saves navigation state

### Question 4
What's the benefit of type-safe navigation with sealed classes?

A) Faster performance
B) Compile-time safety and autocomplete
C) Smaller app size
D) Better animations

### Question 5
When should you use nested navigation?

A) For all navigation
B) For grouping related screens (auth flow, settings)
C) Never
D) Only for deep linking

---

## Quiz Answers

**Question 1: B** - NavController manages navigation state and transitions
**Question 2: B** - Use route parameters: `"details/{id}"`, access with `navArgument`
**Question 3: B** - Navigates back, removes current screen from stack
**Question 4: B** - Compile-time checks prevent typos, IDE autocomplete
**Question 5: B** - Group related screens logically (auth, settings, onboarding)

---

## What You've Learned

✅ Setting up Navigation Compose
✅ NavController and NavHost basics
✅ Passing arguments between screens
✅ Bottom navigation bars
✅ Navigation drawer
✅ Nested navigation graphs
✅ Deep linking support
✅ Type-safe navigation patterns

---

## Next Steps

In **Lesson 6.6: Networking and APIs**, you'll learn:
- Retrofit setup for API calls
- Kotlin serialization
- Coroutines for async networking
- Error handling
- Loading states
- Image loading with Coil

Get ready to connect your app to the internet!

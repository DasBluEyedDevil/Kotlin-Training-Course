# Lesson 6.3: Layouts and UI Design

**Estimated Time**: 75 minutes

---

## Introduction

Beautiful UI is crucial for app success. Users judge apps within **milliseconds** - if your UI looks outdated or confusing, users uninstall.

In this lesson, you'll master:
- ✅ Advanced layout composables (Box, LazyColumn, LazyRow)
- ✅ Arrangement and alignment strategies
- ✅ Spacer for spacing control
- ✅ Material Design 3 components
- ✅ Theming: colors, typography, shapes
- ✅ Building complex, professional UIs

---

## Advanced Layout Composables

### Box (Stacking/Overlapping)

`Box` stacks children on top of each other (like `FrameLayout` in XML):

```kotlin
@Composable
fun BoxExample() {
    Box(
        modifier = Modifier.size(200.dp)
    ) {
        // Children stack from bottom to top
        Image(
            painter = painterResource(R.drawable.photo),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize()
        )

        Text(
            "Overlay Text",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
```

**Alignment options**:
```kotlin
Box {
    Text("Top Start", Modifier.align(Alignment.TopStart))
    Text("Top Center", Modifier.align(Alignment.TopCenter))
    Text("Top End", Modifier.align(Alignment.TopEnd))

    Text("Center Start", Modifier.align(Alignment.CenterStart))
    Text("Center", Modifier.align(Alignment.Center))
    Text("Center End", Modifier.align(Alignment.CenterEnd))

    Text("Bottom Start", Modifier.align(Alignment.BottomStart))
    Text("Bottom Center", Modifier.align(Alignment.BottomCenter))
    Text("Bottom End", Modifier.align(Alignment.BottomEnd))
}
```

### LazyColumn (Efficient Vertical List)

`LazyColumn` is like `RecyclerView` - only renders visible items:

```kotlin
@Composable
fun LazyColumnExample() {
    LazyColumn {
        // Static items
        item {
            Text("Header", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        // Multiple items from list
        items(100) { index ->
            Text("Item $index", modifier = Modifier.padding(16.dp))
        }

        // From a data list
        items(listOf("Apple", "Banana", "Orange")) { fruit ->
            Text(fruit)
        }
    }
}
```

**With custom data**:

```kotlin
data class User(val id: Int, val name: String, val email: String)

@Composable
fun UserList(users: List<User>) {
    LazyColumn {
        items(users) { user ->
            UserCard(user)
        }
    }
}

@Composable
fun UserCard(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Icon(Icons.Default.Person, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(user.name, fontWeight = FontWeight.Bold)
            Text(user.email, fontSize = 14.sp, color = Color.Gray)
        }
    }
}
```

**Key with items for better performance**:

```kotlin
LazyColumn {
    items(
        items = users,
        key = { user -> user.id }  // Helps Compose track items efficiently
    ) { user ->
        UserCard(user)
    }
}
```

### LazyRow (Efficient Horizontal List)

Same as `LazyColumn` but horizontal:

```kotlin
@Composable
fun CategoryChips() {
    val categories = listOf("All", "Electronics", "Fashion", "Home", "Sports")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = false,
                onClick = { },
                label = { Text(category) }
            )
        }
    }
}
```

### LazyVerticalGrid (Grid Layout)

Display items in a grid:

```kotlin
@Composable
fun PhotoGrid(photos: List<Int>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),  // 3 columns
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos) { photoRes ->
            Image(
                painter = painterResource(photoRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)  // Square items
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
```

**Grid column options**:
```kotlin
GridCells.Fixed(3)              // Exactly 3 columns
GridCells.Adaptive(120.dp)      // As many columns as fit (min 120dp each)
GridCells.FixedSize(120.dp)     // Fixed column width
```

---

## Arrangement and Alignment

### Column Arrangement

Control vertical spacing:

```kotlin
@Composable
fun ColumnArrangements() {
    // Space children evenly
    Column(verticalArrangement = Arrangement.SpaceEvenly) {
        Text("First")
        Text("Second")
        Text("Third")
    }

    // Space between children
    Column(verticalArrangement = Arrangement.SpaceBetween) {
        Text("Top")
        Text("Middle")
        Text("Bottom")
    }

    // Space around children
    Column(verticalArrangement = Arrangement.SpaceAround) {
        Text("Item 1")
        Text("Item 2")
    }

    // Fixed spacing
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Item 1")
        Text("Item 2")
    }

    // Align to top/center/bottom
    Column(verticalArrangement = Arrangement.Top) { }
    Column(verticalArrangement = Arrangement.Center) { }
    Column(verticalArrangement = Arrangement.Bottom) { }
}
```

### Column Alignment

Control horizontal alignment of children:

```kotlin
@Composable
fun ColumnAlignments() {
    Column(horizontalAlignment = Alignment.Start) {
        Text("Left aligned")
        Text("Also left")
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Centered")
        Text("Also centered")
    }

    Column(horizontalAlignment = Alignment.End) {
        Text("Right aligned")
        Text("Also right")
    }
}
```

### Row Arrangement and Alignment

```kotlin
@Composable
fun RowLayouts() {
    // Horizontal arrangement
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Left")
        Text("Right")
    }

    // Vertical alignment
    Row(verticalAlignment = Alignment.Top) { }
    Row(verticalAlignment = Alignment.CenterVertically) { }
    Row(verticalAlignment = Alignment.Bottom) { }
}
```

---

## Spacer

Create fixed spacing between composables:

```kotlin
@Composable
fun SpacerExamples() {
    Column {
        Text("First")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Second")
    }

    Row {
        Text("Left")
        Spacer(modifier = Modifier.width(24.dp))
        Text("Right")
    }

    // Fill available space
    Row(modifier = Modifier.fillMaxWidth()) {
        Text("Left")
        Spacer(modifier = Modifier.weight(1f))  // Takes all remaining space
        Text("Right")
    }
}
```

---

## Material Design 3 Components

### Cards

```kotlin
@Composable
fun CardExamples() {
    // Filled card (default)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Card Title", style = MaterialTheme.typography.titleLarge)
            Text("Card content goes here")
        }
    }

    // Outlined card
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Outlined Card", modifier = Modifier.padding(16.dp))
    }

    // Elevated card
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Elevated Card", modifier = Modifier.padding(16.dp))
    }
}
```

**Clickable cards**:

```kotlin
@Composable
fun ClickableCard() {
    Card(
        onClick = { /* Handle click */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Click me!", modifier = Modifier.padding(16.dp))
    }
}
```

### Surface

Material surface with elevation and color:

```kotlin
@Composable
fun SurfaceExample() {
    Surface(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Surface Content")
        }
    }
}
```

### Divider

Visual separator:

```kotlin
@Composable
fun DividerExample() {
    Column {
        Text("Above divider")
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Below divider")

        // Vertical divider
        Row {
            Text("Left")
            VerticalDivider(modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 8.dp)
            )
            Text("Right")
        }

        // Custom divider
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Red
        )
    }
}
```

### Chips

```kotlin
@Composable
fun ChipExamples() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Assist chip
        AssistChip(
            onClick = { },
            label = { Text("Assist Chip") },
            leadingIcon = {
                Icon(Icons.Default.Settings, contentDescription = null)
            }
        )

        // Filter chip
        var selected by remember { mutableStateOf(false) }
        FilterChip(
            selected = selected,
            onClick = { selected = !selected },
            label = { Text("Filter Chip") },
            leadingIcon = if (selected) {
                {
                    Icon(
                        Icons.Default.Done,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            } else null
        )

        // Input chip
        InputChip(
            selected = false,
            onClick = { },
            label = { Text("Input Chip") },
            trailingIcon = {
                Icon(Icons.Default.Close, contentDescription = "Remove")
            }
        )

        // Suggestion chip
        SuggestionChip(
            onClick = { },
            label = { Text("Suggestion") }
        )
    }
}
```

### TextField

```kotlin
@Composable
fun TextFieldExamples() {
    var text by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Filled text field
        TextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Label") },
            placeholder = { Text("Placeholder") }
        )

        // Outlined text field
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(onClick = { text = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            supportingText = { Text("Enter your email address") },
            isError = text.isNotEmpty() && !text.contains("@"),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        // Password field
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            )
        )
    }
}
```

### Checkbox, Switch, RadioButton

```kotlin
@Composable
fun SelectionControls() {
    Column {
        // Checkbox
        var checked by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = it }
            )
            Text("Agree to terms")
        }

        // Switch
        var switchState by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Enable notifications")
            Spacer(Modifier.weight(1f))
            Switch(
                checked = switchState,
                onCheckedChange = { switchState = it }
            )
        }

        // Radio buttons
        var selectedOption by remember { mutableStateOf("Option 1") }
        val options = listOf("Option 1", "Option 2", "Option 3")

        Column {
            options.forEach { option ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = selectedOption == option,
                        onClick = { selectedOption = option }
                    )
                    Text(option)
                }
            }
        }
    }
}
```

### Slider

```kotlin
@Composable
fun SliderExample() {
    var sliderValue by remember { mutableStateOf(50f) }

    Column {
        Text("Volume: ${sliderValue.toInt()}%")

        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..100f,
            steps = 10  // Creates 11 discrete values (0, 10, 20, ..., 100)
        )

        // Range slider
        var rangeValues by remember { mutableStateOf(20f..80f) }
        Text("Range: ${rangeValues.start.toInt()} - ${rangeValues.endInclusive.toInt()}")

        RangeSlider(
            value = rangeValues,
            onValueChange = { rangeValues = it },
            valueRange = 0f..100f
        )
    }
}
```

---

## Material Design 3 Theming

### Color Scheme

Material 3 uses a **dynamic color system**:

```kotlin
// ui/theme/Color.kt
package com.example.app.ui.theme

import androidx.compose.ui.graphics.Color

// Light theme colors
val md_theme_light_primary = Color(0xFF6750A4)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFEADDFF)
val md_theme_light_onPrimaryContainer = Color(0xFF21005D)
val md_theme_light_secondary = Color(0xFF625B71)
val md_theme_light_background = Color(0xFFFFFBFE)
val md_theme_light_surface = Color(0xFFFFFBFE)
val md_theme_light_error = Color(0xFFB3261E)

// Dark theme colors
val md_theme_dark_primary = Color(0xFFD0BCFF)
val md_theme_dark_onPrimary = Color(0xFF381E72)
val md_theme_dark_primaryContainer = Color(0xFF4F378B)
val md_theme_dark_onPrimaryContainer = Color(0xFFEADDFF)
val md_theme_dark_secondary = Color(0xFFCCC2DC)
val md_theme_dark_background = Color(0xFF1C1B1F)
val md_theme_dark_surface = Color(0xFF1C1B1F)
val md_theme_dark_error = Color(0xFFF2B8B5)

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    background = md_theme_light_background,
    surface = md_theme_light_surface,
    error = md_theme_light_error
)

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    background = md_theme_dark_background,
    surface = md_theme_dark_surface,
    error = md_theme_dark_error
)
```

### Theme Setup

```kotlin
// ui/theme/Theme.kt
package com.example.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,  // Android 12+
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

### Typography

```kotlin
// ui/theme/Type.kt
package com.example.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### Using Theme

```kotlin
@Composable
fun ThemedContent() {
    // Access theme colors
    val backgroundColor = MaterialTheme.colorScheme.background
    val primaryColor = MaterialTheme.colorScheme.primary
    val textColor = MaterialTheme.colorScheme.onBackground

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Use theme typography
        Text(
            "Headline",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            "Body text",
            style = MaterialTheme.typography.bodyMedium
        )

        // Components automatically use theme colors
        Button(onClick = { }) {
            Text("Themed Button")  // Uses primary color
        }
    }
}
```

---

## Exercise 1: Product Card

Create a product card with:
- Product image at top
- Product name (bold, large)
- Price (primary color)
- Short description
- "Add to Cart" button
- Material 3 card with elevation

---

## Solution 1

```kotlin
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ProductCard(
    name: String,
    price: Double,
    description: String,
    imageRes: Int,
    onAddToCart: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Product image
            Image(
                painter = painterResource(imageRes),
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Product name
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Price
                Text(
                    text = "$${"%.2f".format(price)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Add to Cart button
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.AddShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to Cart")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductCardPreview() {
    MaterialTheme {
        ProductCard(
            name = "Wireless Headphones",
            price = 129.99,
            description = "Premium noise-cancelling headphones with 30-hour battery life.",
            imageRes = R.drawable.ic_launcher_foreground,
            onAddToCart = { }
        )
    }
}
```

---

## Exercise 2: Settings Screen

Create a settings screen with:
- Section headers
- Toggle switches for notifications
- Navigation items (Profile, Privacy, About)
- Dividers between sections

---

## Solution 2

```kotlin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Notifications section
        SettingsSectionHeader("Notifications")

        var pushNotifications by remember { mutableStateOf(true) }
        SettingsToggle(
            title = "Push Notifications",
            subtitle = "Receive push notifications",
            checked = pushNotifications,
            onCheckedChange = { pushNotifications = it },
            icon = Icons.Default.Notifications
        )

        var emailNotifications by remember { mutableStateOf(false) }
        SettingsToggle(
            title = "Email Notifications",
            subtitle = "Receive email updates",
            checked = emailNotifications,
            onCheckedChange = { emailNotifications = it },
            icon = Icons.Default.Email
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Account section
        SettingsSectionHeader("Account")

        SettingsItem(
            title = "Profile",
            subtitle = "Edit your profile information",
            icon = Icons.Default.Person,
            onClick = { }
        )

        SettingsItem(
            title = "Privacy",
            subtitle = "Manage your privacy settings",
            icon = Icons.Default.Lock,
            onClick = { }
        )

        SettingsItem(
            title = "About",
            subtitle = "App version and information",
            icon = Icons.Default.Info,
            onClick = { }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun SettingsToggle(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen()
    }
}
```

---

## Exercise 3: Photo Gallery Grid

Create a photo gallery with:
- 3-column grid layout
- Square images
- Rounded corners
- Spacing between items

---

## Solution 3

```kotlin
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class Photo(val id: Int, val resourceId: Int)

@Composable
fun PhotoGallery(photos: List<Photo>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(photos, key = { it.id }) { photo ->
            Image(
                painter = painterResource(photo.resourceId),
                contentDescription = "Photo ${photo.id}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)  // Square
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotoGalleryPreview() {
    val samplePhotos = List(12) { index ->
        Photo(
            id = index,
            resourceId = R.drawable.ic_launcher_foreground
        )
    }

    PhotoGallery(photos = samplePhotos)
}
```

---

## Why This Matters

### Real-World Impact

**User Statistics**:
- **94%** of first impressions are design-related
- **88%** of users won't return after a bad experience
- **75%** judge credibility based on design
- Apps with good UI have **3x** higher retention

**Business Impact**:
- Well-designed apps get **5x more downloads**
- Higher ratings (4.5+ stars) increase installs by **300%**
- Better UI reduces support requests by **40%**

**Material Design 3 Benefits**:
- ✅ **Consistent**: Familiar patterns across apps
- ✅ **Accessible**: Built-in accessibility features
- ✅ **Adaptive**: Dynamic colors on Android 12+
- ✅ **Modern**: Fresh, contemporary look

---

## Checkpoint Quiz

### Question 1
What's the main difference between `Column` and `LazyColumn`?

A) Column is faster
B) LazyColumn only renders visible items (efficient for long lists)
C) Column supports more composables
D) LazyColumn is deprecated

### Question 2
Which modifier creates spacing between items in a Column?

A) `Modifier.spacing(16.dp)`
B) `Arrangement.spacedBy(16.dp)`
C) `Modifier.gap(16.dp)`
D) `Spacer(16.dp)`

### Question 3
What does `GridCells.Adaptive(120.dp)` do?

A) Creates exactly 120 columns
B) Creates as many columns as fit (each min 120dp wide)
C) Makes each cell 120dp tall
D) Limits grid to 120 items

### Question 4
Why use `sp` for text size instead of `dp`?

A) sp is smaller
B) sp looks better
C) sp scales with user's font size preference (accessibility)
D) sp is required by Material Design

### Question 5
What is Material Design 3's dynamic color feature?

A) Colors change randomly
B) Colors adapt based on user's wallpaper (Android 12+)
C) Colors animate automatically
D) Developers can't customize colors

---

## Quiz Answers

**Question 1: B) LazyColumn only renders visible items (efficient for long lists)**

```kotlin
// Column: All items rendered immediately (use for small lists)
Column {
    items.forEach { item ->  // All 1000 items rendered!
        ItemCard(item)
    }
}

// LazyColumn: Only visible items rendered (use for long lists)
LazyColumn {
    items(items) { item ->  // Only ~10 visible items rendered
        ItemCard(item)
    }
}
```

**Performance**:
- Column with 1000 items: Slow, high memory usage
- LazyColumn with 1000 items: Fast, low memory (like RecyclerView)

---

**Question 2: B) `Arrangement.spacedBy(16.dp)`**

```kotlin
Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text("First")
    Text("Second")   // 16.dp above
    Text("Third")    // 16.dp above
}

// Alternative: Manual spacers
Column {
    Text("First")
    Spacer(modifier = Modifier.height(16.dp))
    Text("Second")
    Spacer(modifier = Modifier.height(16.dp))
    Text("Third")
}
```

---

**Question 3: B) Creates as many columns as fit (each min 120dp wide)**

```kotlin
// Fixed: Exactly 3 columns
LazyVerticalGrid(columns = GridCells.Fixed(3)) { }

// Adaptive: As many as fit (each min 120dp)
// Screen 360dp wide → 3 columns (120dp each)
// Screen 800dp wide → 6 columns (133dp each)
LazyVerticalGrid(columns = GridCells.Adaptive(120.dp)) { }
```

**Benefits**:
- Responsive: Adapts to screen size
- Works on phones, tablets, foldables

---

**Question 4: C) sp scales with user's font size preference (accessibility)**

```kotlin
// ✅ Correct: Text size in sp
Text(
    "Hello",
    fontSize = 16.sp  // Scales with accessibility settings
)

// ❌ Wrong: Text size in dp
Text(
    "Hello",
    fontSize = 16.dp  // Ignores user preference, accessibility issue
)
```

**Accessibility**:
- Users can increase font size in Settings
- `sp` respects this preference
- `dp` does not

**Use `dp` for**: padding, margins, component sizes
**Use `sp` for**: text size only

---

**Question 5: B) Colors adapt based on user's wallpaper (Android 12+)**

Material Design 3's dynamic color extracts colors from the user's wallpaper:

```kotlin
@Composable
fun AppTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            // Extract colors from wallpaper
            if (darkTheme) {
                dynamicDarkColorScheme(LocalContext.current)
            } else {
                dynamicLightColorScheme(LocalContext.current)
            }
        }
        else -> {
            // Fallback to static colors
            if (darkTheme) DarkColorScheme else LightColorScheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, content = content)
}
```

**Benefits**:
- Personalized: Each user gets unique colors
- Cohesive: Matches system UI
- Fresh: Changes with wallpaper

---

## What You've Learned

✅ Advanced layouts: Box, LazyColumn, LazyRow, LazyVerticalGrid
✅ Arrangement and alignment options for precise positioning
✅ Spacer for controlling spacing
✅ Material Design 3 components: Cards, Chips, TextFields, Sliders
✅ Selection controls: Checkbox, Switch, RadioButton
✅ Theming system: ColorScheme, Typography, Shapes
✅ Dynamic colors on Android 12+
✅ Building complex, professional UIs with Material Design 3

---

## Next Steps

In **Lesson 6.4: State Management**, you'll master:
- Deep dive into state and recomposition
- `remember` vs `rememberSaveable`
- State hoisting patterns
- ViewModel integration
- Managing complex state
- Best practices for state management

Get ready to build truly interactive, data-driven apps!

# Lesson 6.2: Introduction to Jetpack Compose

**Estimated Time**: 70 minutes

---

## Introduction

Welcome to the world of **Jetpack Compose** - Google's modern toolkit for building native Android UIs!

Traditional Android development required XML layouts, `findViewById()` calls, and manual view updates. Compose changes everything with a **declarative** approach: you describe what the UI should look like, and Compose handles the rest.

In this lesson, you'll learn:
- ✅ What Jetpack Compose is and why it's revolutionary
- ✅ How to write composable functions
- ✅ Using Preview annotations for instant feedback
- ✅ Basic UI components (Text, Button, Image, Column, Row)
- ✅ Modifiers for styling and layout
- ✅ Basic state management with `remember`

---

## What is Jetpack Compose?

### Declarative UI Framework

**Imperative (Old Way)**:
```kotlin
// Tell Android HOW to build UI step by step
val textView = findViewById<TextView>(R.id.title)
textView.text = "Hello"
textView.textSize = 20f
textView.setTextColor(Color.BLUE)

val button = findViewById<Button>(R.id.button)
button.setOnClickListener {
    textView.text = "Clicked!"
}
```

**Declarative (Compose Way)**:
```kotlin
// Describe WHAT the UI should look like
@Composable
fun MyScreen() {
    var text by remember { mutableStateOf("Hello") }

    Column {
        Text(
            text = text,
            fontSize = 20.sp,
            color = Color.Blue
        )
        Button(onClick = { text = "Clicked!" }) {
            Text("Click Me")
        }
    }
}
```

**Benefits**:
- ✅ Less code (40% reduction)
- ✅ Easier to read and maintain
- ✅ No manual view updates (automatic recomposition)
- ✅ Type-safe (compiler catches errors)

### Compose vs XML Comparison

| Feature           | XML + Views        | Jetpack Compose       |
|-------------------|--------------------|-----------------------|
| **Language**      | XML + Kotlin       | Kotlin only           |
| **Lines of Code** | ~100 lines         | ~60 lines (40% less)  |
| **Type Safety**   | No (IDs are ints)  | Yes (full type safety)|
| **Preview**       | Limited            | Real-time, interactive|
| **Reusability**   | Difficult          | Easy (functions)      |
| **State Updates** | Manual             | Automatic             |

---

## Composable Functions

### The @Composable Annotation

A **composable function** is a regular Kotlin function annotated with `@Composable`:

```kotlin
@Composable
fun Greeting(name: String) {
    Text("Hello, $name!")
}
```

**Rules**:
1. Must be annotated with `@Composable`
2. Can only be called from other `@Composable` functions
3. Can emit UI elements
4. Can call other `@Composable` functions

### Basic Composable

```kotlin
@Composable
fun SimpleText() {
    Text("Hello World")
}

@Composable
fun GreetingCard(name: String) {
    Text("Welcome, $name!")
}

@Composable
fun CompleteScreen() {
    SimpleText()  // OK: Called from @Composable
    GreetingCard("Alice")  // OK: Called from @Composable
}

fun regularFunction() {
    SimpleText()  // ERROR: Can't call @Composable from regular function
}
```

### Composable Naming Convention

**Convention**: Use **PascalCase** (same as classes):

```kotlin
@Composable
fun UserProfile() { }  // ✅ Good

@Composable
fun userProfile() { }  // ❌ Bad (should be PascalCase)
```

**Why?**
- Composables represent UI components (like classes)
- Distinguishes them from regular functions
- Follows official Compose style guide

---

## Preview Annotations

### @Preview Basics

The `@Preview` annotation lets you see composables without running the app:

```kotlin
@Composable
fun WelcomeMessage() {
    Text("Welcome to Compose!")
}

@Preview(showBackground = true)
@Composable
fun WelcomeMessagePreview() {
    WelcomeMessage()
}
```

Click the **Preview** tab (right side of editor) to see the UI instantly.

### Preview Parameters

```kotlin
@Preview(
    name = "Light Mode",           // Name in preview list
    showBackground = true,          // Show white background
    backgroundColor = 0xFFFFFFFF,   // Background color (ARGB hex)
    widthDp = 320,                  // Width in density-independent pixels
    heightDp = 640,                 // Height in dp
    fontScale = 1.5f,               // Text scaling (accessibility)
    showSystemUi = true             // Show status bar and nav bar
)
@Composable
fun CustomPreview() {
    Text("Custom Preview Settings")
}
```

### Multiple Previews

Preview the same composable in different scenarios:

```kotlin
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Large Text", showBackground = true, fontScale = 2f)
@Preview(name = "Small Screen", widthDp = 360, heightDp = 640)
@Composable
fun MultiPreview() {
    WelcomeMessage()
}
```

### Interactive Preview

Click the **Interactive Mode** button in preview to:
- Click buttons
- Type in text fields
- Test interactions without running the app

---

## Basic UI Components

### Text

Display text on screen:

```kotlin
@Composable
fun TextExamples() {
    Column {
        // Simple text
        Text("Hello World")

        // With custom styling
        Text(
            text = "Styled Text",
            fontSize = 24.sp,              // Scaled pixels (respects user font size)
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            fontStyle = FontStyle.Italic
        )

        // With Material theme
        Text(
            text = "Material Text",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
```

**Units**:
- `sp` (scaled pixels): For text size (respects accessibility settings)
- `dp` (density-independent pixels): For sizes, padding, margins

### Button

Interactive button with click handling:

```kotlin
@Composable
fun ButtonExample() {
    Button(onClick = { /* Handle click */ }) {
        Text("Click Me")
    }
}
```

**Button variations**:

```kotlin
@Composable
fun ButtonVariations() {
    Column {
        // Standard button
        Button(onClick = { }) {
            Text("Standard Button")
        }

        // Outlined button
        OutlinedButton(onClick = { }) {
            Text("Outlined Button")
        }

        // Text button (no background)
        TextButton(onClick = { }) {
            Text("Text Button")
        }

        // Disabled button
        Button(
            onClick = { },
            enabled = false
        ) {
            Text("Disabled")
        }

        // Custom colors
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Red Button")
        }
    }
}
```

### Image

Display images from resources or URLs:

```kotlin
@Composable
fun ImageExample() {
    // From drawable resources
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_foreground),
        contentDescription = "App logo"
    )

    // From vector drawable
    Image(
        imageVector = Icons.Default.Favorite,
        contentDescription = "Favorite icon"
    )

    // With content scale
    Image(
        painter = painterResource(id = R.drawable.photo),
        contentDescription = "Photo",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(200.dp)
    )
}
```

**Content Scales**:
- `ContentScale.Fit`: Fit entire image (may have empty space)
- `ContentScale.Crop`: Fill entire area (may crop image)
- `ContentScale.FillWidth`: Fill width, maintain aspect ratio
- `ContentScale.FillHeight`: Fill height, maintain aspect ratio

### Icon

Material icons for common UI elements:

```kotlin
@Composable
fun IconExamples() {
    Row {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home"
        )

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Favorite",
            tint = Color.Red
        )

        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            modifier = Modifier.size(48.dp)
        )
    }
}
```

**Popular icons**:
- `Icons.Default.Home`
- `Icons.Default.Settings`
- `Icons.Default.Favorite`
- `Icons.Default.Search`
- `Icons.Default.Menu`
- `Icons.Default.Person`
- `Icons.Default.ShoppingCart`

---

## Layout Composables

### Column (Vertical Stack)

Arrange children vertically:

```kotlin
@Composable
fun ColumnExample() {
    Column {
        Text("First")
        Text("Second")
        Text("Third")
    }
}
```

Result:
```
First
Second
Third
```

### Row (Horizontal Stack)

Arrange children horizontally:

```kotlin
@Composable
fun RowExample() {
    Row {
        Text("Left")
        Text("Center")
        Text("Right")
    }
}
```

Result:
```
Left Center Right
```

### Nested Layouts

Combine `Column` and `Row`:

```kotlin
@Composable
fun ProfileCard() {
    Column {
        Text("John Doe", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Row {
            Icon(Icons.Default.Email, contentDescription = "Email")
            Text("john@example.com")
        }

        Row {
            Icon(Icons.Default.Phone, contentDescription = "Phone")
            Text("+1 234 567 8900")
        }
    }
}
```

---

## Modifiers

### What are Modifiers?

**Modifiers** customize the appearance and behavior of composables:
- Size (width, height)
- Padding and margins
- Background colors
- Click handling
- Alignment

### Basic Modifiers

```kotlin
@Composable
fun ModifierExamples() {
    Column {
        // Size
        Text(
            "Fixed Size",
            modifier = Modifier.size(200.dp)
        )

        // Width and height separately
        Text(
            "Custom Dimensions",
            modifier = Modifier
                .width(300.dp)
                .height(100.dp)
        )

        // Fill available width
        Text(
            "Full Width",
            modifier = Modifier.fillMaxWidth()
        )

        // Fill available height
        Text(
            "Full Height",
            modifier = Modifier.fillMaxHeight()
        )

        // Fill entire screen
        Text(
            "Fill Everything",
            modifier = Modifier.fillMaxSize()
        )
    }
}
```

### Padding

```kotlin
@Composable
fun PaddingExamples() {
    Column {
        // Uniform padding (all sides)
        Text(
            "Padded Text",
            modifier = Modifier.padding(16.dp)
        )

        // Specific sides
        Text(
            "Custom Padding",
            modifier = Modifier.padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 8.dp
            )
        )

        // Horizontal and vertical
        Text(
            "Symmetric Padding",
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        )
    }
}
```

### Background and Border

```kotlin
@Composable
fun BackgroundExamples() {
    Column {
        // Solid background
        Text(
            "Blue Background",
            modifier = Modifier.background(Color.Blue)
        )

        // Rounded corners
        Text(
            "Rounded",
            modifier = Modifier
                .background(
                    color = Color.LightGray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        )

        // Border
        Text(
            "With Border",
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = Color.Red,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(16.dp)
        )
    }
}
```

### Clickable

```kotlin
@Composable
fun ClickableExamples() {
    Column {
        // Clickable text
        Text(
            "Click me!",
            modifier = Modifier.clickable {
                // Handle click
                println("Text clicked!")
            }
        )

        // Clickable with ripple effect
        Text(
            "Ripple Effect",
            modifier = Modifier
                .clickable(
                    onClick = { },
                    indication = rememberRipple(),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(16.dp)
        )
    }
}
```

### Modifier Chaining

Order matters! Modifiers are applied sequentially:

```kotlin
@Composable
fun ModifierOrder() {
    // Padding INSIDE background
    Text(
        "Padding Inside",
        modifier = Modifier
            .background(Color.Blue)
            .padding(16.dp)  // Blue extends to edges, text has padding
    )

    // Padding OUTSIDE background
    Text(
        "Padding Outside",
        modifier = Modifier
            .padding(16.dp)  // Gap around blue background
            .background(Color.Blue)
    )
}
```

---

## State Management Basics

### What is State?

**State** is any value that can change over time and affects the UI.

Examples:
- Text field input
- Counter value
- Checkbox checked/unchecked
- List of items

### remember and mutableStateOf

```kotlin
@Composable
fun Counter() {
    // State: value that can change
    var count by remember { mutableStateOf(0) }

    Column {
        Text("Count: $count")
        Button(onClick = { count++ }) {
            Text("Increment")
        }
    }
}
```

**How it works**:
1. `mutableStateOf(0)` creates state with initial value `0`
2. `remember { }` preserves state across recompositions
3. `by` delegates property access (requires `import androidx.compose.runtime.getValue` and `setValue`)
4. When `count` changes, Compose automatically recomposes (rebuilds) the UI

### Without Delegation

```kotlin
@Composable
fun CounterExplicit() {
    val count = remember { mutableStateOf(0) }

    Column {
        Text("Count: ${count.value}")  // Access with .value
        Button(onClick = { count.value++ }) {
            Text("Increment")
        }
    }
}
```

### Multiple State Variables

```kotlin
@Composable
fun LoginForm() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Column {
        Text("Email: $email")
        Text("Password: $password")
        Text("Remember: $rememberMe")

        Button(onClick = {
            // Use state values
            println("Login: $email / $password")
        }) {
            Text("Login")
        }
    }
}
```

---

## Putting It All Together

### Profile Card Example

```kotlin
@Composable
fun ProfileCard(name: String, role: String, imageRes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile image
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Profile info
        Column {
            Text(
                text = name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = role,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileCardPreview() {
    ProfileCard(
        name = "Alice Johnson",
        role = "Software Engineer",
        imageRes = R.drawable.ic_launcher_foreground
    )
}
```

### Interactive Counter App

```kotlin
@Composable
fun CounterApp() {
    var count by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Count: $count",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { count-- }) {
                Text("-")
            }

            Button(onClick = { count = 0 }) {
                Text("Reset")
            }

            Button(onClick = { count++ }) {
                Text("+")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CounterAppPreview() {
    CounterApp()
}
```

---

## Exercise 1: Build a Business Card

Create a digital business card with:
- Your name (large, bold)
- Your title (smaller, gray)
- Email address with icon
- Phone number with icon
- Rounded corners and background color

### Requirements

```kotlin
@Composable
fun BusinessCard() {
    // Your implementation here
}
```

---

## Solution 1

```kotlin
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BusinessCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF1976D2),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Name
        Text(
            text = "Alice Johnson",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = "Android Developer",
            fontSize = 18.sp,
            color = Color(0xFFB3E5FC)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "alice@example.com",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Phone
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Phone",
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "+1 (555) 123-4567",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BusinessCardPreview() {
    BusinessCard()
}
```

---

## Exercise 2: Interactive Like Button

Create a like button that:
- Shows a heart icon
- Toggles between outlined and filled when clicked
- Changes color (gray → red)
- Shows like count that increments/decrements

### Requirements

```kotlin
@Composable
fun LikeButton() {
    // Your implementation here
}
```

---

## Solution 2

```kotlin
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LikeButton() {
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(42) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(16.dp)
    ) {
        IconButton(onClick = {
            isLiked = !isLiked
            likeCount = if (isLiked) likeCount + 1 else likeCount - 1
        }) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isLiked) "Unlike" else "Like",
                tint = if (isLiked) Color.Red else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$likeCount",
            fontSize = 18.sp,
            color = if (isLiked) Color.Red else Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LikeButtonPreview() {
    LikeButton()
}
```

---

## Exercise 3: User List

Create a list of 3 user profiles using the `ProfileCard` composable:

### Requirements

```kotlin
@Composable
fun UserList() {
    // Display 3 ProfileCards vertically
}
```

---

## Solution 3

```kotlin
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun UserList() {
    Column {
        ProfileCard(
            name = "Alice Johnson",
            role = "Android Developer",
            imageRes = R.drawable.ic_launcher_foreground
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileCard(
            name = "Bob Smith",
            role = "Product Manager",
            imageRes = R.drawable.ic_launcher_foreground
        )

        Spacer(modifier = Modifier.height(8.dp))

        ProfileCard(
            name = "Carol Williams",
            role = "UX Designer",
            imageRes = R.drawable.ic_launcher_foreground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserListPreview() {
    UserList()
}
```

---

## Why This Matters

### Real-World Impact

**Companies Using Jetpack Compose**:
- **Google**: Gmail, Google Play Store, Google Drive
- **Twitter**: Android app rebuilt with Compose
- **Airbnb**: Migrating to Compose
- **Square**: Cash App using Compose

**Benefits in Production**:
- ✅ 40% less code → faster development
- ✅ Fewer bugs (type safety, automatic state management)
- ✅ Better performance (smart recomposition)
- ✅ Easier to test (composables are functions)
- ✅ Modern, maintainable codebase

**Industry Trends**:
- Compose is now the **recommended** way to build Android UIs
- All new Google apps use Compose
- Strong community support and growing ecosystem
- Multiplatform: Compose for Desktop, Web, iOS (experimental)

---

## Checkpoint Quiz

### Question 1
What does the `@Composable` annotation do?

A) Makes a function run faster
B) Marks a function that can emit UI elements
C) Automatically creates previews
D) Enables state management

### Question 2
What is the purpose of `remember { mutableStateOf(0) }`?

A) Improves performance by caching values
B) Creates state that persists across recompositions
C) Makes the variable immutable
D) Enables preview mode

### Question 3
How do you make a Text composable clickable?

A) Add `onClick` parameter to Text
B) Wrap it in a Button
C) Use the `.clickable()` modifier
D) Use the `@Clickable` annotation

### Question 4
What's the difference between `dp` and `sp`?

A) They're the same thing
B) `dp` for sizes/padding, `sp` for text (respects accessibility)
C) `sp` is larger than `dp`
D) `dp` only works on tablets

### Question 5
What happens when state changes in a composable?

A) The entire app restarts
B) The composable automatically recomposes (rebuilds)
C) Nothing, you must manually update UI
D) The state is lost

---

## Quiz Answers

**Question 1: B) Marks a function that can emit UI elements**

The `@Composable` annotation tells the Compose compiler:
- This function describes UI
- It can call other `@Composable` functions
- It can only be called from composable context

```kotlin
@Composable
fun MyUI() {
    Text("Hello")  // OK: Text is @Composable
}

fun regular() {
    Text("Hello")  // ERROR: Can't call @Composable from here
}
```

---

**Question 2: B) Creates state that persists across recompositions**

Without `remember`, state is lost on every recomposition:

```kotlin
// ❌ Wrong: count resets to 0 on every recomposition
@Composable
fun Counter() {
    var count = 0  // Lost on recomposition!
    Button(onClick = { count++ }) { Text("$count") }
}

// ✅ Correct: count persists
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }  // Preserved!
    Button(onClick = { count++ }) { Text("$count") }
}
```

---

**Question 3: C) Use the `.clickable()` modifier**

```kotlin
Text(
    "Click me!",
    modifier = Modifier.clickable {
        // Handle click
    }
)
```

Alternative: Wrap in a `Button`, but that adds button styling.

---

**Question 4: B) `dp` for sizes/padding, `sp` for text (respects accessibility)**

- **`dp`** (density-independent pixels): Fixed size, same on all devices
  - Use for: padding, margins, component sizes
- **`sp`** (scalable pixels): Scales with user's font size preference
  - Use for: text size only
  - Respects accessibility settings

```kotlin
Text(
    "Hello",
    fontSize = 16.sp,  // ✅ Correct: scales with user preference
    modifier = Modifier.padding(16.dp)  // ✅ Correct: fixed padding
)
```

---

**Question 5: B) The composable automatically recomposes (rebuilds)**

Compose tracks state reads and automatically recomposes when state changes:

```kotlin
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }

    // When count changes:
    // 1. Compose detects the change
    // 2. Automatically calls Counter() again
    // 3. UI updates with new count value

    Text("Count: $count")  // Auto-updates when count changes!
    Button(onClick = { count++ }) { Text("+") }
}
```

**Smart Recomposition**: Only the composables that read the changed state are recomposed, not the entire UI.

---

## What You've Learned

✅ What Jetpack Compose is and its benefits over XML layouts
✅ How to write composable functions with `@Composable`
✅ Using `@Preview` for instant UI feedback
✅ Basic components: Text, Button, Image, Icon
✅ Layout composables: Column, Row
✅ Modifiers for styling (size, padding, background, clickable)
✅ State management basics with `remember` and `mutableStateOf`
✅ Building interactive UIs that respond to user input

---

## Next Steps

In **Lesson 6.3: Layouts and UI Design**, you'll master:
- Advanced layouts: Box, LazyColumn, LazyRow
- Arrangement and alignment options
- Material Design 3 components
- Theming: colors, typography, shapes
- Building complex, scrollable UIs
- Responsive layouts

Get ready to build professional-looking Android apps!

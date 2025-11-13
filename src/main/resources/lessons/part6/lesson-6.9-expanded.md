# Lesson 6.9: Advanced UI & Animations

**Estimated Time**: 75 minutes

---

## Introduction

Animations make apps feel alive and responsive. They guide user attention, provide feedback, and create delightful experiences. Jetpack Compose makes animations simple and declarative.

In this lesson, you'll master:
- ✅ Animation APIs overview
- ✅ Simple value animations (animateDpAsState, animateColorAsState)
- ✅ AnimatedVisibility for enter/exit animations
- ✅ Transitions for complex state changes
- ✅ Infinite and repeating animations
- ✅ Gestures and touch handling
- ✅ Canvas for custom drawing

---

## Animation Basics

### animate*AsState

Animate a single value when it changes:

```kotlin
@Composable
fun AnimatedBox() {
    var isExpanded by remember { mutableStateOf(false) }

    // Animate size
    val size by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else 100.dp,
        label = "size"
    )

    Box(
        modifier = Modifier
            .size(size)
            .background(Color.Blue)
            .clickable { isExpanded = !isExpanded }
    )
}
```

### Multiple Property Animations

```kotlin
@Composable
fun AnimatedCard() {
    var isExpanded by remember { mutableStateOf(false) }

    val size by animateDpAsState(if (isExpanded) 300.dp else 150.dp)
    val cornerRadius by animateDpAsState(if (isExpanded) 24.dp else 8.dp)
    val elevation by animateDpAsState(if (isExpanded) 12.dp else 4.dp)
    val backgroundColor by animateColorAsState(
        if (isExpanded) Color.Green else Color.Blue
    )

    Card(
        modifier = Modifier
            .size(size)
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(if (isExpanded) "Tap to shrink" else "Tap to expand")
        }
    }
}
```

### Animation Specs

Control animation duration and easing:

```kotlin
val size by animateDpAsState(
    targetValue = if (isExpanded) 200.dp else 100.dp,
    animationSpec = tween(
        durationMillis = 500,
        easing = FastOutSlowInEasing
    )
)

// Spring animation (bouncy)
val offset by animateDpAsState(
    targetValue = if (isVisible) 0.dp else 100.dp,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
)

// Repeatable animation
val alpha by animateFloatAsState(
    targetValue = if (isHighlighted) 1f else 0.3f,
    animationSpec = repeatable(
        iterations = 3,
        animation = tween(durationMillis = 300),
        repeatMode = RepeatMode.Reverse
    )
)
```

---

## AnimatedVisibility

### Enter and Exit Animations

```kotlin
@Composable
fun AnimatedVisibilityExample() {
    var isVisible by remember { mutableStateOf(true) }

    Column {
        Button(onClick = { isVisible = !isVisible }) {
            Text(if (isVisible) "Hide" else "Show")
        }

        AnimatedVisibility(visible = isVisible) {
            Card(modifier = Modifier.padding(16.dp)) {
                Text("This card animates in and out", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
```

### Custom Enter/Exit Transitions

```kotlin
@Composable
fun CustomTransitions() {
    var isVisible by remember { mutableStateOf(false) }

    Button(onClick = { isVisible = !isVisible }) {
        Text("Toggle")
    }

    // Fade + Slide
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(),
        exit = fadeOut() + slideOutVertically()
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text("Animated Content", modifier = Modifier.padding(16.dp))
        }
    }

    // Scale + Fade
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(64.dp))
    }

    // Expand/Collapse
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        Text("Expanding text", modifier = Modifier.padding(16.dp))
    }
}
```

### Animated Content

Animate content changes:

```kotlin
@Composable
fun AnimatedCounter() {
    var count by remember { mutableStateOf(0) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                if (targetState > initialState) {
                    // Counting up
                    slideInVertically { -it } + fadeIn() togetherWith
                            slideOutVertically { it } + fadeOut()
                } else {
                    // Counting down
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                }
            },
            label = "count"
        ) { targetCount ->
            Text(
                text = "$targetCount",
                style = MaterialTheme.typography.displayLarge
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { count-- }) { Text("-") }
            Button(onClick = { count++ }) { Text("+") }
        }
    }
}
```

---

## Transitions

### updateTransition

Coordinate multiple animations:

```kotlin
enum class BoxState { Small, Large }

@Composable
fun TransitionExample() {
    var currentState by remember { mutableStateOf(BoxState.Small) }

    val transition = updateTransition(targetState = currentState, label = "box")

    val size by transition.animateDp(label = "size") { state ->
        when (state) {
            BoxState.Small -> 100.dp
            BoxState.Large -> 200.dp
        }
    }

    val color by transition.animateColor(label = "color") { state ->
        when (state) {
            BoxState.Small -> Color.Blue
            BoxState.Large -> Color.Red
        }
    }

    val cornerRadius by transition.animateDp(label = "cornerRadius") { state ->
        when (state) {
            BoxState.Small -> 8.dp
            BoxState.Large -> 50.dp
        }
    }

    Box(
        modifier = Modifier
            .size(size)
            .background(color, RoundedCornerShape(cornerRadius))
            .clickable {
                currentState = if (currentState == BoxState.Small) {
                    BoxState.Large
                } else {
                    BoxState.Small
                }
            }
    )
}
```

---

## Infinite Animations

```kotlin
@Composable
fun LoadingSpinner() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Icon(
        Icons.Default.Refresh,
        contentDescription = "Loading",
        modifier = Modifier
            .size(48.dp)
            .rotate(rotation)
    )
}

@Composable
fun PulsingHeart() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Icon(
        Icons.Default.Favorite,
        contentDescription = "Heart",
        tint = Color.Red,
        modifier = Modifier
            .size(48.dp)
            .scale(scale)
    )
}
```

---

## Gestures

### Clickable with Ripple

```kotlin
@Composable
fun ClickableBox() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clickable(
                onClick = { /* Handle click */ },
                indication = rememberRipple(bounded = true),
                interactionSource = remember { MutableInteractionSource() }
            )
            .background(Color.Blue)
    )
}
```

### Draggable

```kotlin
@Composable
fun DraggableBox() {
    var offsetX by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .size(100.dp)
            .background(Color.Blue)
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    offsetX += delta
                }
            )
    )
}
```

### Swipe to Dismiss

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissExample(item: String, onDismiss: () -> Unit) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
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
                    .background(Color.Red),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(item, modifier = Modifier.padding(16.dp))
        }
    }
}
```

### Pointer Input (Advanced)

```kotlin
@Composable
fun DoubleTapExample() {
    var isLiked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(200.dp)
            .background(if (isLiked) Color.Red else Color.Gray)
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        isLiked = !isLiked
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.Default.Favorite,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(64.dp)
        )
    }
}
```

---

## Canvas Drawing

### Basic Shapes

```kotlin
@Composable
fun CanvasExample() {
    Canvas(modifier = Modifier.size(200.dp)) {
        // Circle
        drawCircle(
            color = Color.Blue,
            radius = 50.dp.toPx(),
            center = Offset(100.dp.toPx(), 100.dp.toPx())
        )

        // Rectangle
        drawRect(
            color = Color.Red,
            topLeft = Offset(150.dp.toPx(), 150.dp.toPx()),
            size = Size(50.dp.toPx(), 50.dp.toPx())
        )

        // Line
        drawLine(
            color = Color.Green,
            start = Offset(0f, 0f),
            end = Offset(200.dp.toPx(), 200.dp.toPx()),
            strokeWidth = 5.dp.toPx()
        )
    }
}
```

### Custom Progress Indicator

```kotlin
@Composable
fun CircularProgressBar(
    progress: Float,  // 0f to 1f
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(120.dp)) {
        val strokeWidth = 12.dp.toPx()

        // Background circle
        drawCircle(
            color = Color.LightGray,
            radius = size.minDimension / 2 - strokeWidth / 2,
            style = Stroke(width = strokeWidth)
        )

        // Progress arc
        drawArc(
            color = Color.Blue,
            startAngle = -90f,
            sweepAngle = 360f * progress,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            size = Size(
                width = size.minDimension - strokeWidth,
                height = size.minDimension - strokeWidth
            ),
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
        )
    }
}

@Composable
fun ProgressDemo() {
    var progress by remember { mutableStateOf(0f) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressBar(progress = progress)

        Slider(
            value = progress,
            onValueChange = { progress = it },
            modifier = Modifier.padding(16.dp)
        )

        Text("${(progress * 100).toInt()}%")
    }
}
```

---

## Exercise 1: Animated Like Button

Create a like button:
- Heart icon
- Scale animation when clicked
- Color change (gray → red)
- Particle effect (bonus)

---

## Solution 1

```kotlin
@Composable
fun AnimatedLikeButton() {
    var isLiked by remember { mutableStateOf(false) }
    var animationTrigger by remember { mutableStateOf(0) }

    val scale by animateFloatAsState(
        targetValue = if (animationTrigger > 0) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = {
            if (animationTrigger > 0) {
                animationTrigger = 0
            }
        }
    )

    IconButton(
        onClick = {
            isLiked = !isLiked
            animationTrigger++
        }
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Like",
            tint = if (isLiked) Color.Red else Color.Gray,
            modifier = Modifier
                .size(32.dp)
                .scale(scale)
        )
    }
}
```

---

## Exercise 2: Loading Skeleton

Create a shimmer loading effect:
- Animated gradient
- Placeholder cards
- Smooth animation

---

## Solution 2

```kotlin
@Composable
fun ShimmerEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")

    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset(offset - 300f, offset - 300f),
        end = Offset(offset, offset)
    )

    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        repeat(3) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    // Avatar placeholder
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(brush)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Title placeholder
                        Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )

                        // Subtitle placeholder
                        Box(
                            modifier = Modifier
                                .width(150.dp)
                                .height(14.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(brush)
                        )
                    }
                }
            }
        }
    }
}
```

---

## Exercise 3: Pull to Refresh

Implement pull-to-refresh:
- Drag gesture
- Loading indicator
- Smooth animation

---

## Solution 3

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullToRefreshExample() {
    var isRefreshing by remember { mutableStateOf(false) }
    var items by remember { mutableStateOf(List(20) { "Item $it" }) }
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(2000)  // Simulate network call
            items = List(20) { "Item ${it + items.size}" }
            isRefreshing = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pullToRefresh(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = { isRefreshing = true }
                )
        ) {
            items(items) { item ->
                Text(
                    item,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }

        if (pullRefreshState.isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
```

---

## Why This Matters

**User Experience Impact**:
- Animations increase engagement by **20%**
- Users perceive animated apps as **faster** and more polished
- Proper feedback reduces perceived wait time by **30%**

**Best Practices**:
- ✅ Keep animations under 300ms (feel instant)
- ✅ Use spring animations for natural feel
- ✅ Provide feedback for all user actions
- ✅ Don't overuse animations (distracting)

---

## Checkpoint Quiz

### Question 1
What does `animateDpAsState` do?

A) Creates a static value
B) Animates a Dp value when target changes
C) Only works for colors
D) Requires manual triggering

### Question 2
How do you create an infinite animation?

A) Set duration to infinity
B) Use `rememberInfiniteTransition`
C) Call animation repeatedly
D) Not possible

### Question 3
What is `AnimatedVisibility` used for?

A) Making views transparent
B) Animating enter/exit of composables
C) Checking if animation is running
D) Debugging animations

### Question 4
Which gesture detector is built into Compose?

A) Only click
B) Click, drag, swipe, and custom gestures
C) No gestures supported
D) Only swipe

### Question 5
What can Canvas be used for?

A) Only images
B) Custom drawings (shapes, paths, gradients)
C) Only text
D) Cannot draw anything

---

## Quiz Answers

**Question 1: B** - Animates Dp when target value changes
**Question 2: B** - Use `rememberInfiniteTransition` with `infiniteRepeatable`
**Question 3: B** - Handles enter/exit animations automatically
**Question 4: B** - Full gesture support (tap, drag, swipe, custom)
**Question 5: B** - Draw custom shapes, paths, gradients, etc.

---

## What You've Learned

✅ Simple value animations with animate*AsState
✅ AnimatedVisibility for enter/exit transitions
✅ Complex transitions with updateTransition
✅ Infinite animations for loaders
✅ Gesture handling (click, drag, swipe)
✅ Custom drawing with Canvas
✅ Performance considerations
✅ UX best practices

---

## Next Steps

In **Lesson 6.10: Part 6 Capstone - Task Manager App**, you'll build:
- Complete Android app from scratch
- All concepts integrated (MVVM, Room, Navigation, Animations)
- Task CRUD operations
- Categories, priorities, due dates
- Material Design 3 UI
- Fully functional production-ready app

Time to put everything together!

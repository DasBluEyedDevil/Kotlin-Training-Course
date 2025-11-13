# Lesson 6.2: Advanced Frontend - State, Routing, and Forms

## Building Production-Ready UIs

Now let's explore advanced React patterns in Kotlin!

---

## Advanced State Management

### useState with Complex Objects

```kotlin
data class FormState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val errors: Map<String, String> = emptyMap()
)

val LoginForm = FC<Props> {
    var formState by useState(FormState())

    fun updateEmail(email: String) {
        formState = formState.copy(email = email)
    }

    fun updatePassword(password: String) {
        formState = formState.copy(password = password)
    }

    fun validate(): Boolean {
        val errors = mutableMapOf<String, String>()

        if (!formState.email.contains("@")) {
            errors["email"] = "Invalid email"
        }

        if (formState.password.length < 8) {
            errors["password"] = "Password too short"
        }

        formState = formState.copy(errors = errors)
        return errors.isEmpty()
    }

    div {
        input {
            type = InputType.email
            value = formState.email
            placeholder = "Email"
            onChange = { event ->
                updateEmail(event.target.value)
            }
        }

        formState.errors["email"]?.let { error ->
            div {
                css { color = Color.red }
                +error
            }
        }

        input {
            type = InputType.password
            value = formState.password
            placeholder = "Password"
            onChange = { event ->
                updatePassword(event.target.value)
            }
        }

        formState.errors["password"]?.let { error ->
            div {
                css { color = Color.red }
                +error
            }
        }

        button {
            +"Login"
            onClick = {
                if (validate()) {
                    console.log("Form valid, submitting...")
                }
            }
        }
    }
}
```

---

### useEffect - Side Effects

```kotlin
val UserProfile = FC<Props> {
    var user by useState<User?>(null)
    var loading by useState(true)
    var error by useState<String?>(null)

    useEffectOnce {
        MainScope().launch {
            try {
                val response = window.fetch("/api/user/profile").await()
                if (response.ok) {
                    user = response.json().await().unsafeCast<User>()
                } else {
                    error = "Failed to load user"
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    div {
        when {
            loading -> p { +"Loading..." }
            error != null -> p {
                css { color = Color.red }
                +"Error: $error"
            }
            user != null -> div {
                h2 { +"Welcome, ${user!!.name}!" }
                p { +"Email: ${user!!.email}" }
            }
        }
    }
}
```

---

### useEffect with Dependencies

```kotlin
val SearchComponent = FC<Props> {
    var query by useState("")
    var results by useState(listOf<SearchResult>())
    var loading by useState(false)

    useEffect(query) {
        if (query.length < 3) {
            results = emptyList()
            return@useEffect
        }

        loading = true
        val job = MainScope().launch {
            delay(300) // Debounce
            try {
                val response = window.fetch("/api/search?q=$query").await()
                results = response.json().await().unsafeCast<Array<SearchResult>>().toList()
            } finally {
                loading = false
            }
        }

        cleanup {
            job.cancel()
        }
    }

    div {
        input {
            type = InputType.text
            value = query
            placeholder = "Search..."
            onChange = { event ->
                query = event.target.value
            }
        }

        if (loading) {
            p { +"Searching..." }
        }

        ul {
            results.forEach { result ->
                li {
                    key = result.id
                    +result.title
                }
            }
        }
    }
}
```

---

## Custom Hooks

```kotlin
fun useLocalStorage(key: String, initialValue: String): Pair<String, (String) -> Unit> {
    val (storedValue, setStoredValue) = useState(
        localStorage.getItem(key) ?: initialValue
    )

    val setValue = { value: String ->
        setStoredValue(value)
        localStorage.setItem(key, value)
    }

    return storedValue to setValue
}

// Usage
val Settings = FC<Props> {
    val (theme, setTheme) = useLocalStorage("theme", "light")

    button {
        +"Toggle Theme"
        onClick = {
            setTheme(if (theme == "light") "dark" else "light")
        }
    }

    div {
        +"Current theme: $theme"
    }
}
```

---

### useFetch Hook

```kotlin
data class FetchState<T>(
    val data: T? = null,
    val loading: Boolean = false,
    val error: String? = null
)

fun <T> useFetch(url: String): FetchState<T> {
    var state by useState(FetchState<T>(loading = true))

    useEffectOnce {
        MainScope().launch {
            try {
                val response = window.fetch(url).await()
                if (response.ok) {
                    val data = response.json().await().unsafeCast<T>()
                    state = FetchState(data = data)
                } else {
                    state = FetchState(error = "HTTP ${response.status}")
                }
            } catch (e: Exception) {
                state = FetchState(error = e.message)
            }
        }
    }

    return state
}

// Usage
val UserList = FC<Props> {
    val usersState = useFetch<Array<User>>("/api/users")

    div {
        when {
            usersState.loading -> p { +"Loading..." }
            usersState.error != null -> p { +"Error: ${usersState.error}" }
            usersState.data != null -> {
                usersState.data!!.forEach { user ->
                    div {
                        key = user.id.toString()
                        +"${user.name} - ${user.email}"
                    }
                }
            }
        }
    }
}
```

---

## Component Composition

### Higher-Order Components

```kotlin
fun <P : Props> withLoading(
    component: FC<P>,
    isLoading: Boolean
): FC<P> = FC { props ->
    if (isLoading) {
        div {
            css {
                textAlign = TextAlign.center
                padding = 20.px
            }
            +"Loading..."
        }
    } else {
        component(props)
    }
}

// Usage
val UserProfile = FC<Props> {
    // component code
}

val UserProfileWithLoading = withLoading(UserProfile, isLoading = true)
```

---

### Render Props Pattern

```kotlin
external interface DataLoaderProps : Props {
    var url: String
    var render: (data: dynamic) -> ReactNode
}

val DataLoader = FC<DataLoaderProps> { props ->
    var data by useState<dynamic>(null)
    var loading by useState(true)

    useEffectOnce {
        MainScope().launch {
            val response = window.fetch(props.url).await()
            data = response.json().await()
            loading = false
        }
    }

    div {
        if (loading) {
            +"Loading..."
        } else {
            child(props.render(data))
        }
    }
}

// Usage
DataLoader {
    url = "/api/users"
    render = { data ->
        ul {
            val users = data.unsafeCast<Array<User>>()
            users.forEach { user ->
                li { +user.name }
            }
        }
    }
}
```

---

## Form Handling

### Controlled Components

```kotlin
data class ContactForm(
    val name: String = "",
    val email: String = "",
    val message: String = ""
)

val ContactFormComponent = FC<Props> {
    var form by useState(ContactForm())
    var submitted by useState(false)
    var errors by useState(mapOf<String, String>())

    fun validateForm(): Boolean {
        val newErrors = mutableMapOf<String, String>()

        if (form.name.isBlank()) {
            newErrors["name"] = "Name is required"
        }

        if (!form.email.contains("@")) {
            newErrors["email"] = "Invalid email"
        }

        if (form.message.length < 10) {
            newErrors["message"] = "Message too short"
        }

        errors = newErrors
        return newErrors.isEmpty()
    }

    fun handleSubmit() {
        if (validateForm()) {
            MainScope().launch {
                window.fetch("/api/contact", RequestInit(
                    method = "POST",
                    headers = json("Content-Type" to "application/json"),
                    body = JSON.stringify(form)
                )).await()
                submitted = true
            }
        }
    }

    if (submitted) {
        div {
            css {
                backgroundColor = Color("#4CAF50")
                color = Color.white
                padding = 20.px
            }
            +"Thank you! Your message has been sent."
        }
    } else {
        form {
            onSubmit = { event ->
                event.preventDefault()
                handleSubmit()
            }

            div {
                label {
                    htmlFor = "name"
                    +"Name:"
                }
                input {
                    id = "name"
                    type = InputType.text
                    value = form.name
                    onChange = { event ->
                        form = form.copy(name = event.target.value)
                    }
                }
                errors["name"]?.let { error ->
                    span {
                        css { color = Color.red }
                        +error
                    }
                }
            }

            div {
                label {
                    htmlFor = "email"
                    +"Email:"
                }
                input {
                    id = "email"
                    type = InputType.email
                    value = form.email
                    onChange = { event ->
                        form = form.copy(email = event.target.value)
                    }
                }
                errors["email"]?.let { error ->
                    span {
                        css { color = Color.red }
                        +error
                    }
                }
            }

            div {
                label {
                    htmlFor = "message"
                    +"Message:"
                }
                textarea {
                    id = "message"
                    value = form.message
                    onChange = { event ->
                        form = form.copy(message = event.target.value)
                    }
                }
                errors["message"]?.let { error ->
                    span {
                        css { color = Color.red }
                        +error
                    }
                }
            }

            button {
                type = ButtonType.submit
                +"Send Message"
            }
        }
    }
}
```

---

## Lists and Keys

### Efficient List Rendering

```kotlin
data class TodoItem(val id: Int, val text: String, val completed: Boolean)

val TodoList = FC<Props> {
    var todos by useState(listOf(
        TodoItem(1, "Learn Kotlin", false),
        TodoItem(2, "Build app", false),
        TodoItem(3, "Deploy", false)
    ))

    fun toggleTodo(id: Int) {
        todos = todos.map { todo ->
            if (todo.id == id) {
                todo.copy(completed = !todo.completed)
            } else {
                todo
            }
        }
    }

    fun deleteTodo(id: Int) {
        todos = todos.filter { it.id != id }
    }

    ul {
        css {
            listStyleType = None.none
            padding = 0.px
        }

        todos.forEach { todo ->
            li {
                key = todo.id.toString() // Important for performance!

                css {
                    padding = 10.px
                    marginBottom = 5.px
                    backgroundColor = if (todo.completed) {
                        Color("#d3d3d3")
                    } else {
                        Color("#f9f9f9")
                    }
                    display = Display.flex
                    justifyContent = JustifyContent.spaceBetween
                }

                span {
                    css {
                        textDecoration = if (todo.completed) {
                            TextDecoration.lineThrough
                        } else {
                            None.none
                        }
                    }
                    +todo.text
                }

                div {
                    button {
                        +"Toggle"
                        onClick = { toggleTodo(todo.id) }
                    }

                    button {
                        css {
                            marginLeft = 5.px
                            backgroundColor = Color.red
                            color = Color.white
                        }
                        +"Delete"
                        onClick = { deleteTodo(todo.id) }
                    }
                }
            }
        }
    }
}
```

---

## Recap

You now understand:

1. **Advanced useState** - Complex state management
2. **useEffect** - Side effects and lifecycle
3. **Custom hooks** - Reusable logic
4. **Component composition** - Building complex UIs
5. **Form handling** - Validation and submission
6. **List rendering** - Keys and performance

---

## What's Next?

Final lesson: **Complete Full-Stack Integration!**

**Key Takeaway:** Advanced React patterns make complex UIs manageable!

Continue to Part 7!

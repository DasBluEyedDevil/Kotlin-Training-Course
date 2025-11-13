# Kotlin Training Course

**From Zero to Full-Stack: An Interactive Desktop Learning Platform**

A comprehensive, interactive desktop application for learning Kotlin programming from absolute beginner to full-stack developer. Built with JavaFX and powered by an embedded Kotlin execution engine.

---

## 🌟 Features

- **Interactive Code Playground**: Write and execute Kotlin code directly in the application
- **45+ Coding Challenges**: Hands-on exercises with starter code, hints, and solutions
- **110+ Knowledge Quizzes**: Multiple-choice and true/false questions with explanations
- **Progressive Curriculum**: 7 parts covering fundamentals to full-stack development
- **Concept-First Learning**: Real-world analogies before technical jargon
- **Progress Tracking**: Automatic saving of lesson completion and progress
- **Beautiful UI**: Modern JavaFX interface with syntax highlighting
- **Offline Capable**: All lessons and execution happen locally
- **Beginner-Friendly**: Designed for people with zero programming experience

---

## 📚 Course Structure

### Part 1: The Absolute Basics ✅ (9 lessons)
- Introduction to Kotlin & development setup
- Variables, data types, and operators
- Control flow basics (if/when/loops fundamentals)
- Functions and basic syntax
- Collections and arrays
- Null safety and safe calls
- CLI Calculator capstone
- Functions with parameters and return values
- **Final Capstone: Personal Profile Generator**

### Part 2: Controlling the Flow ✅ (7 lessons)
- If statements and conditional logic
- Logical operators (AND, OR, NOT)
- When expressions for elegant multi-way decisions
- For loops and iteration
- While loops and do-while
- Lists - storing and manipulating collections
- Maps - key-value pairs
- **Capstone: Contact Management System**

### Part 3: Object-Oriented Programming ✅ (7 lessons)
- Introduction to OOP concepts
- Properties and initialization
- Inheritance and polymorphism
- Interfaces and abstract classes
- Data classes and sealed classes
- Object declarations and companion objects
- **Capstone: Library Management System**

### Part 4: Advanced Kotlin ✅ (13 lessons)
- Introduction to functional programming
- Lambda expressions and anonymous functions
- Collection operations (map, filter, reduce)
- Scope functions (let, apply, run, with, also)
- Function composition and currying
- Functional capstone: Data processing pipeline
- Generics and type parameters
- Coroutines fundamentals
- Advanced coroutines (Flow, channels, etc.)
- Delegation and lazy initialization
- Annotations and reflection
- DSLs and type-safe builders
- **Capstone: Task Scheduler with Coroutines**

### Part 5: Backend Development with Ktor ✅ (15 lessons)
- Introduction to backend development & HTTP fundamentals
- Setting up your first Ktor project
- Routing fundamentals - building endpoints
- Request parameters (path, query, body)
- JSON serialization with kotlinx.serialization
- Database fundamentals with Exposed (setup & queries)
- Database operations (CRUD & transactions)
- Repository pattern - organizing your data layer
- Request validation and error handling
- Authentication - user registration & password hashing
- Authentication - login & JWT tokens
- Authentication - protecting routes with JWT
- Dependency injection with Koin
- Testing your API
- **Capstone: Complete Task Management API**

### Part 6: Android Development ✅ (10 lessons)
- Android fundamentals & setup
- Introduction to Jetpack Compose
- Layouts and UI design with Material Design 3
- State management in Compose
- Navigation component
- Networking with Retrofit
- Local data storage with Room
- MVVM architecture pattern
- Advanced UI & animations
- **Capstone: Task Manager Mobile App**

### Part 7: Professional Development & Deployment ✅ (8 lessons)
- Kotlin Multiplatform (KMP) basics
- Testing strategies (unit, integration, UI testing)
- Performance optimization and profiling
- Security best practices (OWASP Top 10)
- CI/CD and DevOps with GitHub Actions
- Cloud deployment (AWS, Heroku, GCP)
- Monitoring and analytics
- **Final Capstone: Full-Stack E-Commerce Platform**

---

## 📊 Total Course Content

- **69 comprehensive lessons** across 7 parts (9 + 7 + 7 + 13 + 15 + 10 + 8)
- **100+ hands-on exercises** with detailed solutions
- **80+ quiz questions** with explanations
- **10+ capstone projects** integrating all concepts
- **~70-80 hours of estimated learning time**
- **~120,000 lines of educational content**

### Interactive Learning Components:
- ✅ **Hands-On Exercises**: 3-4 per lesson with step-by-step solutions
- ✅ **Quick Quizzes**: 3-4 questions per lesson with detailed explanations
- ✅ **Coding Challenges**: Part 1 (7), Part 2 (8), Part 3 (7), Part 4 (7), Part 5 (6), Part 6 (6), Part 7 (4)
- ✅ **Knowledge Quizzes**: Part 1 (25Q), Part 2 (22Q), Part 3 (18Q), Part 4 (11Q), Part 5 (14Q), Part 6 (10Q), Part 7 (10Q)
- ✅ **Capstone Projects**: One per part (10 total) building real-world applications

---

## 🚀 Quick Start

### Prerequisites

- **Java 17 or higher** installed on your system
- **Maven 3.6+** (for building from source)

### Running the Application

#### Option 1: Using Maven (Recommended for Development)

```bash
# Clone the repository
git clone <repository-url>
cd Kotlin-Training-Course

# Run the application
mvn clean javafx:run
```

#### Option 2: Build and Run JAR

```bash
# Build the application
mvn clean package

# Run the JAR (requires JavaFX runtime)
java --module-path /path/to/javafx-sdk/lib \
     --add-modules javafx.controls,javafx.fxml,javafx.web \
     -jar target/kotlin-training-course-1.0.0.jar
```

#### Option 3: Using the JavaFX Maven Plugin

```bash
mvn clean javafx:jlink
./target/image/bin/launcher
```

---

## 🛠️ Building from Source

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Kotlin-Training-Course
```

### 2. Build with Maven

```bash
mvn clean install
```

### 3. Run Tests (Coming Soon)

```bash
mvn test
```

---

## 📦 Project Structure

```
Kotlin-Training-Course/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/kotlincourse/
│       │       ├── KotlinCourseApp.java       # Main application entry
│       │       ├── ui/                         # JavaFX UI components
│       │       │   ├── MainWindow.java
│       │       │   ├── LessonViewer.java
│       │       │   ├── CodeEditor.java
│       │       │   └── ProgressSidebar.java
│       │       ├── core/                       # Business logic
│       │       │   ├── LessonManager.java
│       │       │   ├── ProgressTracker.java
│       │       │   ├── CodeExecutor.java
│       │       │   └── ChallengeValidator.java
│       │       └── model/                      # Data models
│       │           ├── Lesson.java
│       │           ├── CodeChallenge.java
│       │           └── UserProgress.java
│       └── resources/
│           ├── lessons/                        # Lesson content (69 lessons)
│           │   ├── part1/ (9 lessons)
│           │   ├── part2/ (7 lessons)
│           │   ├── part3/ (7 lessons)
│           │   ├── part4/ (13 lessons)
│           │   ├── part5/ (15 lessons)
│           │   ├── part6/ (10 lessons)
│           │   └── part7/ (8 lessons)
│           ├── challenges/                     # Interactive coding challenges
│           │   ├── part1-challenges.json      # 7 challenges
│           │   ├── part2-challenges.json      # 8 challenges
│           │   ├── part3-challenges.json      # 7 challenges
│           │   ├── part4-challenges.json      # 7 challenges
│           │   ├── part5-challenges.json      # 6 challenges
│           │   ├── part6-challenges.json      # 6 challenges
│           │   └── part7-challenges.json      # 4 challenges
│           ├── quizzes/                        # Knowledge assessment quizzes
│           │   ├── part1-quiz.json            # 25 questions
│           │   ├── part2-quiz.json            # 22 questions
│           │   ├── part3-quiz.json            # 18 questions
│           │   ├── part4-quiz.json            # 11 questions
│           │   ├── part5-quiz.json            # 14 questions
│           │   ├── part6-quiz.json            # 10 questions
│           │   └── part7-quiz.json            # 10 questions
│           └── styles/                         # CSS stylesheets
│               ├── application.css
│               └── lesson-content.css
├── pom.xml                                     # Maven configuration
└── README.md                                   # This file
```

---

## 💾 Progress Tracking

User progress is automatically saved to:

```
~/.kotlin-course/user-progress.json
```

This includes:
- Completed lessons
- Time spent learning
- Challenge completion status
- Last accessed date

To reset progress, use the application menu: **File → Reset Progress**

---

## 🎨 Technology Stack

| Component | Technology |
|-----------|------------|
| **Language** | Java 17 |
| **UI Framework** | JavaFX 20 |
| **Code Execution** | Kotlin Compiler Embedded (1.9.20) |
| **Markdown Rendering** | CommonMark |
| **Code Editor** | RichTextFX |
| **JSON Processing** | Gson |
| **Build Tool** | Maven |

---

## 🧑‍💻 Development

### Running in Development Mode

```bash
mvn clean javafx:run
```

### Adding New Lessons

1. Create a markdown file in `src/main/resources/lessons/partX/`
2. Follow the naming convention: `lesson-X.Y.md`
3. Update `LessonManager.java` to include the new lesson
4. Rebuild and test

### Customizing the UI

- Modify `src/main/resources/styles/application.css` for styling
- Update UI components in `src/main/java/com/kotlincourse/ui/`

---

## 🐛 Troubleshooting

### "Kotlin script engine not available"

**Solution:** Ensure all Kotlin dependencies are in the classpath:
```bash
mvn clean install
mvn dependency:tree | grep kotlin
```

### JavaFX not found

**Solution:** Ensure you're using Java 17+ and JavaFX is configured:
```bash
java --version
mvn javafx:run
```

### Progress not saving

**Solution:** Check permissions for `~/.kotlin-course/` directory:
```bash
ls -la ~/.kotlin-course/
chmod 755 ~/.kotlin-course/
```

---

## 🤝 Contributing

Contributions are welcome! Areas for contribution:

- [x] ~~Additional lessons (Parts 2-7)~~ **COMPLETED - All 69 comprehensive lessons created**
- [x] ~~More coding challenges~~ **COMPLETED - 45+ challenges + 100+ in-lesson exercises**
- [x] ~~Knowledge quizzes~~ **COMPLETED - 110+ quiz questions + 200+ in-lesson quizzes**
- [ ] Automated challenge validation system
- [ ] UI/UX improvements
- [ ] Video content creation
- [ ] Bug fixes
- [ ] Documentation improvements
- [ ] Translations to other languages

### Contribution Guidelines

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-lesson`)
3. Commit your changes (`git commit -m 'Add new lesson on coroutines'`)
4. Push to the branch (`git push origin feature/new-lesson`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🙏 Acknowledgments

- **Kotlin Team** for creating an amazing language
- **JavaFX Community** for the UI framework
- **OpenJFX** for maintaining JavaFX
- **CommonMark** for markdown parsing
- **RichTextFX** for the code editor component

---

## 📧 Contact & Support

- **Issues:** Please report bugs via GitHub Issues
- **Discussions:** Use GitHub Discussions for questions and ideas
- **Email:** [Your contact email]

---

## 🗺️ Roadmap

### Completed ✅
- [x] Part 1: Absolute Basics (9 comprehensive lessons)
- [x] Part 2: Controlling the Flow (7 comprehensive lessons)
- [x] Part 3: Object-Oriented Programming (7 comprehensive lessons)
- [x] Part 4: Advanced Kotlin (13 comprehensive lessons - Functional + Advanced merged)
- [x] Part 5: Backend with Ktor (15 comprehensive lessons)
- [x] Part 6: Android Development with Jetpack Compose (10 comprehensive lessons)
- [x] Part 7: Professional Development & Deployment (8 comprehensive lessons)
- [x] 69 total comprehensive lessons (~70-80 hours of content)
- [x] 100+ hands-on exercises with detailed solutions
- [x] 45+ Interactive coding challenges across all parts
- [x] 110+ Knowledge assessment questions with explanations
- [x] 10+ capstone projects integrating all concepts
- [x] Progress tracking and persistence

### Future Enhancements 🚀
- [ ] Automated challenge validation with unit tests
- [ ] Certificate generation upon course completion
- [ ] Community lesson sharing platform
- [ ] Mobile app version (Android)
- [ ] Video content integration
- [ ] Live coding sessions recorder
- [ ] Multi-language support

---

## ⭐ Star This Project

If you find this useful, please consider giving it a star on GitHub! It helps others discover the project.

---

**Happy Learning! 🚀**

*Built with ❤️ for aspiring Kotlin developers*

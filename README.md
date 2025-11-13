# Kotlin Training Course

**From Zero to Full-Stack: An Interactive Desktop Learning Platform**

A comprehensive, interactive desktop application for learning Kotlin programming from absolute beginner to full-stack developer. Built with JavaFX and powered by an embedded Kotlin execution engine.

---

## 🌟 Features

- **Interactive Code Playground**: Write and execute Kotlin code directly in the application
- **Progressive Curriculum**: 7 parts covering fundamentals to full-stack development
- **Concept-First Learning**: Real-world analogies before technical jargon
- **Progress Tracking**: Automatic saving of lesson completion and progress
- **Beautiful UI**: Modern JavaFX interface with syntax highlighting
- **Offline Capable**: All lessons and execution happen locally
- **Beginner-Friendly**: Designed for people with zero programming experience

---

## 📚 Course Structure

### Part 1: The Absolute Basics ✅ (Complete!)
- What is programming?
- Your first Kotlin code
- Variables, data types, and functions
- User input and output
- **9 comprehensive lessons**

### Part 2-7: Coming Soon
- Control flow (if/when, loops)
- Object-oriented programming
- Advanced Kotlin features
- Backend development with Ktor
- Frontend development with Kotlin/JS
- Full-stack capstone project

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
│           ├── lessons/                        # Lesson content
│           │   └── part1/
│           │       ├── lesson-1.1.md
│           │       └── ... (1.2-1.9)
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

- [ ] Additional lessons (Parts 2-7)
- [ ] More coding challenges
- [ ] UI/UX improvements
- [ ] Bug fixes
- [ ] Documentation improvements
- [ ] Translations

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

- [x] Part 1: Absolute Basics (9 lessons)
- [ ] Part 2: Control Flow
- [ ] Part 3: Object-Oriented Programming
- [ ] Part 4: Advanced Kotlin
- [ ] Part 5: Backend with Ktor
- [ ] Part 6: Frontend with Kotlin/JS
- [ ] Part 7: Full-Stack Capstone
- [ ] Interactive coding challenges with automated validation
- [ ] Community lesson sharing platform
- [ ] Mobile app version (Android)

---

## ⭐ Star This Project

If you find this useful, please consider giving it a star on GitHub! It helps others discover the project.

---

**Happy Learning! 🚀**

*Built with ❤️ for aspiring Kotlin developers*

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
- What is programming?
- Your first Kotlin code
- Variables, data types, and functions
- User input and output
- **Capstone: Personal Profile Generator**

### Part 2: Controlling the Flow ✅ (7 lessons)
- If/else statements and logical operators
- When expressions
- For loops, while loops
- Collections: Lists and Maps
- **Complete with interactive challenges**

### Part 3: Object-Oriented Programming ✅ (4 lessons)
- Classes and objects
- Null safety
- Inheritance and polymorphism
- Data classes and sealed classes
- **Capstone: Library Management System**

### Part 4: Advanced Kotlin ✅ (3 lessons)
- Lambda expressions and higher-order functions
- Extension functions and coroutines
- Collections deep dive and sequences
- **Interactive challenges with lambdas and functional programming**

### Part 5: Backend Development ✅ (3 lessons)
- REST APIs with Ktor
- Database integration with Exposed
- Testing, validation, and error handling
- **Complete TODO API implementation**

### Part 6: Frontend Development ✅ (2 lessons)
- React Kotlin and Kotlin/JS basics
- Advanced state management, routing, and forms
- Custom hooks and component patterns
- **Full interactive UI components**

### Part 7: Full-Stack Capstone ✅ (1 lesson)
- Complete task management system
- Backend + Frontend integration
- Authentication flow
- **Production-ready application**

---

## 📊 Total Course Content

- **29 comprehensive lessons** across 7 parts
- **45+ interactive coding challenges** with solutions and hints
- **110+ assessment questions** (quizzes with explanations)
- **Multiple capstone projects** (full applications)
- **~15-20 hours of estimated learning time**

### Interactive Learning Components:
- ✅ **Coding Challenges**: Part 1 (7), Part 2 (8), Part 3 (7), Part 4 (7), Part 5 (6), Part 6 (6), Part 7 (4)
- ✅ **Knowledge Quizzes**: Part 1 (25Q), Part 2 (22Q), Part 3 (18Q), Part 4 (11Q), Part 5 (14Q), Part 6 (10Q), Part 7 (10Q)

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
│           ├── lessons/                        # Lesson content (29 lessons)
│           │   ├── part1/ (9 lessons)
│           │   ├── part2/ (7 lessons)
│           │   ├── part3/ (4 lessons)
│           │   ├── part4/ (3 lessons)
│           │   ├── part5/ (3 lessons)
│           │   ├── part6/ (2 lessons)
│           │   └── part7/ (1 lesson)
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

- [x] ~~Additional lessons (Parts 2-7)~~ **COMPLETED - All 29 lessons created**
- [x] ~~More coding challenges~~ **COMPLETED - 45+ challenges across all parts**
- [x] ~~Knowledge quizzes~~ **COMPLETED - 110+ quiz questions**
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
- [x] Part 1: Absolute Basics (9 lessons)
- [x] Part 2: Control Flow (7 lessons)
- [x] Part 3: Object-Oriented Programming (4 lessons)
- [x] Part 4: Advanced Kotlin (3 lessons)
- [x] Part 5: Backend with Ktor (3 lessons)
- [x] Part 6: Frontend with Kotlin/JS (2 lessons)
- [x] Part 7: Full-Stack Capstone (1 lesson)
- [x] 45+ Interactive coding challenges across all parts
- [x] 110+ Knowledge assessment questions with explanations
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

package com.kotlincourse.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kotlincourse.model.Lesson;
import com.kotlincourse.model.CodeChallenge;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages loading and accessing lessons from resources
 */
public class LessonManager {
    private List<Lesson> allLessons;
    private Map<String, Lesson> lessonMap;
    private Map<String, String> lessonContent;
    private Gson gson;

    public LessonManager() {
        this.allLessons = new ArrayList<>();
        this.lessonMap = new HashMap<>();
        this.lessonContent = new HashMap<>();
        this.gson = new Gson();
        loadCourseStructure();
    }

    /**
     * Loads the course structure from configuration
     */
    private void loadCourseStructure() {
        // For now, we'll create the structure programmatically
        // Later, this can be loaded from course-structure.json

        createPart1Lessons();
        createPart2Lessons();
        createPart3Lessons();
        createPart4Lessons();
        createPart5Lessons();
        createPart6Lessons();
        createPart7Lessons();
    }

    /**
     * Creates Part 1 lesson structure
     */
    private void createPart1Lessons() {
        addLesson(new Lesson("1.1", "Introduction to Kotlin & Development Setup", 1, 1,
                "lessons/part1/lesson-1.1-expanded.md", 60));

        addLesson(new Lesson("1.2", "Variables, Data Types & Operators", 1, 2,
                "lessons/part1/lesson-1.2-expanded.md", 60));

        addLesson(new Lesson("1.3", "Control Flow - Conditionals & Loops", 1, 3,
                "lessons/part1/lesson-1.3-expanded.md", 60));

        addLesson(new Lesson("1.4", "Functions & Basic Syntax", 1, 4,
                "lessons/part1/lesson-1.4-expanded.md", 60));

        addLesson(new Lesson("1.5", "Collections & Arrays", 1, 5,
                "lessons/part1/lesson-1.5-expanded.md", 60));

        addLesson(new Lesson("1.6", "Null Safety & Safe Calls", 1, 6,
                "lessons/part1/lesson-1.6-expanded.md", 60));

        addLesson(new Lesson("1.7", "Part 1 Capstone Project - CLI Calculator", 1, 7,
                "lessons/part1/lesson-1.7-expanded.md", 60));

        addLesson(new Lesson("1.8", "Functions with Parameters and Return Values", 1, 8,
                "lessons/part1/lesson-1.8-expanded.md", 70));

        addLesson(new Lesson("1.9", "Part 1 Capstone - Personal Profile Generator", 1, 9,
                "lessons/part1/lesson-1.9-expanded.md", 70));
    }

    /**
     * Adds a lesson to the manager
     */
    private void addLesson(Lesson lesson) {
        allLessons.add(lesson);
        lessonMap.put(lesson.getId(), lesson);
    }

    /**
     * Gets all lessons
     */
    public List<Lesson> getAllLessons() {
        return new ArrayList<>(allLessons);
    }

    /**
     * Gets lessons by part number
     */
    public List<Lesson> getLessonsByPart(int partNumber) {
        return allLessons.stream()
                .filter(lesson -> lesson.getPartNumber() == partNumber)
                .sorted(Comparator.comparingInt(Lesson::getLessonNumber))
                .collect(Collectors.toList());
    }

    /**
     * Gets a specific lesson by ID
     */
    public Lesson getLesson(String lessonId) {
        return lessonMap.get(lessonId);
    }

    /**
     * Loads lesson content (markdown) from resources
     */
    public String getLessonContent(String lessonId) {
        if (lessonContent.containsKey(lessonId)) {
            return lessonContent.get(lessonId);
        }

        Lesson lesson = lessonMap.get(lessonId);
        if (lesson == null) {
            return "Lesson not found: " + lessonId;
        }

        try {
            String content = loadResourceFile(lesson.getMarkdownFile());
            lessonContent.put(lessonId, content);
            return content;
        } catch (IOException e) {
            return "Error loading lesson content: " + e.getMessage();
        }
    }

    /**
     * Loads a resource file as string
     */
    private String loadResourceFile(String resourcePath) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    /**
     * Gets the next lesson after the given lesson ID
     */
    public Lesson getNextLesson(String currentLessonId) {
        Lesson currentLesson = lessonMap.get(currentLessonId);
        if (currentLesson == null) {
            return null;
        }

        // Find the next lesson in the same part
        List<Lesson> partLessons = getLessonsByPart(currentLesson.getPartNumber());
        int currentIndex = partLessons.indexOf(currentLesson);

        if (currentIndex >= 0 && currentIndex < partLessons.size() - 1) {
            return partLessons.get(currentIndex + 1);
        }

        // If last lesson in part, get first lesson of next part
        List<Lesson> nextPartLessons = getLessonsByPart(currentLesson.getPartNumber() + 1);
        if (!nextPartLessons.isEmpty()) {
            return nextPartLessons.get(0);
        }

        return null;
    }

    /**
     * Gets the previous lesson before the given lesson ID
     */
    public Lesson getPreviousLesson(String currentLessonId) {
        Lesson currentLesson = lessonMap.get(currentLessonId);
        if (currentLesson == null) {
            return null;
        }

        // Find the previous lesson in the same part
        List<Lesson> partLessons = getLessonsByPart(currentLesson.getPartNumber());
        int currentIndex = partLessons.indexOf(currentLesson);

        if (currentIndex > 0) {
            return partLessons.get(currentIndex - 1);
        }

        // If first lesson in part, get last lesson of previous part
        if (currentLesson.getPartNumber() > 1) {
            List<Lesson> prevPartLessons = getLessonsByPart(currentLesson.getPartNumber() - 1);
            if (!prevPartLessons.isEmpty()) {
                return prevPartLessons.get(prevPartLessons.size() - 1);
            }
        }

        return null;
    }

    /**
     * Gets total number of lessons in the course
     */
    public int getTotalLessonCount() {
        return allLessons.size();
    }

    /**
     * Gets list of unique part numbers
     */
    public List<Integer> getPartNumbers() {
        return allLessons.stream()
                .map(Lesson::getPartNumber)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Creates Part 2 lesson structure
     */
    private void createPart2Lessons() {
        addLesson(new Lesson("2.1", "Making Decisions - If Statements and Conditional Logic", 2, 1,
                "lessons/part2/lesson-2.1-expanded.md", 60));

        addLesson(new Lesson("2.2", "Combining Conditions - Logical Operators", 2, 2,
                "lessons/part2/lesson-2.2-expanded.md", 60));

        addLesson(new Lesson("2.3", "The When Expression - Elegant Multi-Way Decisions", 2, 3,
                "lessons/part2/lesson-2.3-expanded.md", 60));

        addLesson(new Lesson("2.4", "Repeating Tasks - For Loops and Iteration", 2, 4,
                "lessons/part2/lesson-2.4-expanded.md", 60));

        addLesson(new Lesson("2.5", "While Loops and Do-While - Condition-Based Repetition", 2, 5,
                "lessons/part2/lesson-2.5-expanded.md", 60));

        addLesson(new Lesson("2.6", "Lists - Storing Multiple Items", 2, 6,
                "lessons/part2/lesson-2.6-expanded.md", 60));

        addLesson(new Lesson("2.7", "Maps and Part 2 Capstone Project", 2, 7,
                "lessons/part2/lesson-2.7-expanded.md", 70));
    }

    /**
     * Creates Part 3 lesson structure
     */
    private void createPart3Lessons() {
        addLesson(new Lesson("3.1", "Introduction to Object-Oriented Programming", 3, 1,
                "lessons/part3/lesson-3.1-expanded.md", 60));

        addLesson(new Lesson("3.2", "Properties and Initialization", 3, 2,
                "lessons/part3/lesson-3.2-expanded.md", 65));

        addLesson(new Lesson("3.3", "Inheritance and Polymorphism", 3, 3,
                "lessons/part3/lesson-3.3-expanded.md", 65));

        addLesson(new Lesson("3.4", "Interfaces and Abstract Classes", 3, 4,
                "lessons/part3/lesson-3.4-expanded.md", 70));

        addLesson(new Lesson("3.5", "Data Classes and Sealed Classes", 3, 5,
                "lessons/part3/lesson-3.5-expanded.md", 65));

        addLesson(new Lesson("3.6", "Object Declarations and Companion Objects", 3, 6,
                "lessons/part3/lesson-3.6-expanded.md", 65));

        addLesson(new Lesson("3.7", "Part 2 Capstone - Library Management System", 3, 7,
                "lessons/part3/lesson-3.7-expanded.md", 75));
    }

    /**
     * Creates Part 4 lesson structure
     */
    private void createPart4Lessons() {
        addLesson(new Lesson("4.1", "Introduction to Functional Programming", 4, 1,
                "lessons/part4/lesson-4.1-expanded.md", 65));

        addLesson(new Lesson("4.2", "Lambda Expressions and Anonymous Functions", 4, 2,
                "lessons/part4/lesson-4.2-expanded.md", 70));

        addLesson(new Lesson("4.3", "Collection Operations", 4, 3,
                "lessons/part4/lesson-4.3-expanded.md", 70));

        addLesson(new Lesson("4.4", "Scope Functions", 4, 4,
                "lessons/part4/lesson-4.4-expanded.md", 65));

        addLesson(new Lesson("4.5", "Function Composition and Currying", 4, 5,
                "lessons/part4/lesson-4.5-expanded.md", 65));

        addLesson(new Lesson("4.6", "Part 3 Capstone - Data Processing Pipeline", 4, 6,
                "lessons/part4/lesson-4.6-expanded.md", 70));

        addLesson(new Lesson("4.7", "Generics and Type Parameters", 4, 7,
                "lessons/part4/lesson-4.7-expanded.md", 65));

        addLesson(new Lesson("4.8", "Coroutines Fundamentals", 4, 8,
                "lessons/part4/lesson-4.8-expanded.md", 65));

        addLesson(new Lesson("4.9", "Advanced Coroutines", 4, 9,
                "lessons/part4/lesson-4.9-expanded.md", 65));

        addLesson(new Lesson("4.10", "Delegation and Lazy Initialization", 4, 10,
                "lessons/part4/lesson-4.10-expanded.md", 65));

        addLesson(new Lesson("4.11", "Annotations and Reflection", 4, 11,
                "lessons/part4/lesson-4.11-expanded.md", 70));

        addLesson(new Lesson("4.12", "DSLs and Type-Safe Builders", 4, 12,
                "lessons/part4/lesson-4.12-expanded.md", 70));

        addLesson(new Lesson("4.13", "Part 4 Capstone - Task Scheduler with Coroutines", 4, 13,
                "lessons/part4/lesson-4.13-expanded.md", 75));
    }

    /**
     * Creates Part 5 lesson structure
     */
    private void createPart5Lessons() {
        addLesson(new Lesson("5.1", "Introduction to Backend Development & HTTP Fundamentals", 5, 1,
                "lessons/part5/lesson-5.1-expanded.md", 60));

        addLesson(new Lesson("5.2", "Setting Up Your First Ktor Project", 5, 2,
                "lessons/part5/lesson-5.2-expanded.md", 65));

        addLesson(new Lesson("5.3", "Routing Fundamentals - Building Your First Endpoints", 5, 3,
                "lessons/part5/lesson-5.3-expanded.md", 65));

        addLesson(new Lesson("5.4", "Request Parameters - Path, Query, and Body", 5, 4,
                "lessons/part5/lesson-5.4-expanded.md", 65));

        addLesson(new Lesson("5.5", "JSON Serialization with kotlinx.serialization", 5, 5,
                "lessons/part5/lesson-5.5-expanded.md", 65));

        addLesson(new Lesson("5.6", "Database Fundamentals with Exposed - Part 1 (Setup & Queries)", 5, 6,
                "lessons/part5/lesson-5.6-expanded.md", 65));

        addLesson(new Lesson("5.7", "Database Operations with Exposed - Part 2 (CRUD & Transactions)", 5, 7,
                "lessons/part5/lesson-5.7-expanded.md", 65));

        addLesson(new Lesson("5.8", "The Repository Pattern - Organizing Your Data Layer", 5, 8,
                "lessons/part5/lesson-5.8-expanded.md", 70));

        addLesson(new Lesson("5.9", "Request Validation & Error Handling", 5, 9,
                "lessons/part5/lesson-5.9-expanded.md", 75));

        addLesson(new Lesson("5.10", "Authentication - User Registration & Password Hashing", 5, 10,
                "lessons/part5/lesson-5.10-expanded.md", 75));

        addLesson(new Lesson("5.11", "Authentication - Login & JWT Tokens", 5, 11,
                "lessons/part5/lesson-5.11-expanded.md", 75));

        addLesson(new Lesson("5.12", "Authentication - Protecting Routes with JWT", 5, 12,
                "lessons/part5/lesson-5.12-expanded.md", 75));

        addLesson(new Lesson("5.13", "Dependency Injection with Koin", 5, 13,
                "lessons/part5/lesson-5.13-expanded.md", 70));

        addLesson(new Lesson("5.14", "Testing Your API", 5, 14,
                "lessons/part5/lesson-5.14-expanded.md", 75));

        addLesson(new Lesson("5.15", "Part 5 Capstone Project - Task Management API", 5, 15,
                "lessons/part5/lesson-5.15-expanded.md", 80));
    }

    /**
     * Creates Part 6 lesson structure
     */
    private void createPart6Lessons() {
        addLesson(new Lesson("6.1", "Android Fundamentals & Setup", 6, 1,
                "lessons/part6/lesson-6.1-expanded.md", 70));

        addLesson(new Lesson("6.2", "Introduction to Jetpack Compose", 6, 2,
                "lessons/part6/lesson-6.2-expanded.md", 70));

        addLesson(new Lesson("6.3", "Layouts and UI Design", 6, 3,
                "lessons/part6/lesson-6.3-expanded.md", 75));

        addLesson(new Lesson("6.4", "State Management", 6, 4,
                "lessons/part6/lesson-6.4-expanded.md", 70));

        addLesson(new Lesson("6.5", "Navigation", 6, 5,
                "lessons/part6/lesson-6.5-expanded.md", 70));

        addLesson(new Lesson("6.6", "Networking and APIs", 6, 6,
                "lessons/part6/lesson-6.6-expanded.md", 65));

        addLesson(new Lesson("6.7", "Local Data Storage", 6, 7,
                "lessons/part6/lesson-6.7-expanded.md", 60));

        addLesson(new Lesson("6.8", "MVVM Architecture", 6, 8,
                "lessons/part6/lesson-6.8-expanded.md", 60));

        addLesson(new Lesson("6.9", "Advanced UI & Animations", 6, 9,
                "lessons/part6/lesson-6.9-expanded.md", 60));

        addLesson(new Lesson("6.10", "Part 6 Capstone - Task Manager App", 6, 10,
                "lessons/part6/lesson-6.10-expanded.md", 80));
    }

    /**
     * Creates Part 7 lesson structure
     */
    private void createPart7Lessons() {
        addLesson(new Lesson("7.1", "Kotlin Multiplatform (KMP) Basics", 7, 1,
                "lessons/part7/lesson-7.1-expanded.md", 75));

        addLesson(new Lesson("7.2", "Testing Strategies", 7, 2,
                "lessons/part7/lesson-7.2-expanded.md", 75));

        addLesson(new Lesson("7.3", "Performance Optimization", 7, 3,
                "lessons/part7/lesson-7.3-expanded.md", 70));

        addLesson(new Lesson("7.4", "Security Best Practices", 7, 4,
                "lessons/part7/lesson-7.4-expanded.md", 75));

        addLesson(new Lesson("7.5", "CI/CD and DevOps", 7, 5,
                "lessons/part7/lesson-7.5-expanded.md", 75));

        addLesson(new Lesson("7.6", "Cloud Deployment", 7, 6,
                "lessons/part7/lesson-7.6-expanded.md", 70));

        addLesson(new Lesson("7.7", "Monitoring and Analytics", 7, 7,
                "lessons/part7/lesson-7.7-expanded.md", 75));

        addLesson(new Lesson("7.8", "Final Capstone - Full Stack E-Commerce Platform", 7, 8,
                "lessons/part7/lesson-7.8-expanded.md", 90));
    }
}

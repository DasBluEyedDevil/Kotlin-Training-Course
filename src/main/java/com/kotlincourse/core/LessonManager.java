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
        addLesson(new Lesson("1.1", "What is Programming?", 1, 1,
                "lessons/part1/lesson-1.1.md", 15));

        addLesson(new Lesson("1.2", "Your First Playground", 1, 2,
                "lessons/part1/lesson-1.2.md", 10));

        addLesson(new Lesson("1.3", "Talking to the Computer", 1, 3,
                "lessons/part1/lesson-1.3.md", 15));

        addLesson(new Lesson("1.4", "Labeled Boxes (Variables - Part 1)", 1, 4,
                "lessons/part1/lesson-1.4.md", 20));

        addLesson(new Lesson("1.5", "Types of Contents (Basic Data Types)", 1, 5,
                "lessons/part1/lesson-1.5.md", 25));

        addLesson(new Lesson("1.6", "Talking Back to the Computer", 1, 6,
                "lessons/part1/lesson-1.6.md", 20));

        addLesson(new Lesson("1.7", "Reusable Recipes (Functions - Part 1)", 1, 7,
                "lessons/part1/lesson-1.7.md", 25));

        addLesson(new Lesson("1.8", "Recipes with Ingredients", 1, 8,
                "lessons/part1/lesson-1.8.md", 30));

        addLesson(new Lesson("1.9", "Part 1 Capstone Project", 1, 9,
                "lessons/part1/lesson-1.9.md", 45));
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
        addLesson(new Lesson("2.1", "Making Decisions (If Statements)", 2, 1,
                "lessons/part2/lesson-2.1.md", 25));

        addLesson(new Lesson("2.2", "Combining Conditions (Logical Operators)", 2, 2,
                "lessons/part2/lesson-2.2.md", 20));

        addLesson(new Lesson("2.3", "The When Expression", 2, 3,
                "lessons/part2/lesson-2.3.md", 20));

        addLesson(new Lesson("2.4", "Repeating Tasks (For Loops)", 2, 4,
                "lessons/part2/lesson-2.4.md", 25));

        addLesson(new Lesson("2.5", "While Loops and Do-While", 2, 5,
                "lessons/part2/lesson-2.5.md", 20));

        addLesson(new Lesson("2.6", "Lists - Storing Multiple Items", 2, 6,
                "lessons/part2/lesson-2.6.md", 30));

        addLesson(new Lesson("2.7", "Maps - Key-Value Pairs", 2, 7,
                "lessons/part2/lesson-2.7.md", 25));
    }

    /**
     * Creates Part 3 lesson structure
     */
    private void createPart3Lessons() {
        addLesson(new Lesson("3.1", "Introduction to Classes and Objects", 3, 1,
                "lessons/part3/lesson-3.1.md", 35));

        addLesson(new Lesson("3.2", "Null Safety - Kotlin's Safety Net", 3, 2,
                "lessons/part3/lesson-3.2.md", 25));

        addLesson(new Lesson("3.3", "Inheritance and Polymorphism", 3, 3,
                "lessons/part3/lesson-3.3.md", 30));

        addLesson(new Lesson("3.4", "Data Classes and Part 3 Wrap-Up", 3, 4,
                "lessons/part3/lesson-3.4.md", 30));
    }

    /**
     * Creates Part 4 lesson structure
     */
    private void createPart4Lessons() {
        addLesson(new Lesson("4.1", "Lambda Expressions and Higher-Order Functions", 4, 1,
                "lessons/part4/lesson-4.1.md", 40));

        addLesson(new Lesson("4.2", "Extension Functions and Coroutines Intro", 4, 2,
                "lessons/part4/lesson-4.2.md", 35));
    }

    /**
     * Creates Part 5 lesson structure
     */
    private void createPart5Lessons() {
        addLesson(new Lesson("5.1", "Introduction to Backend Development with Ktor", 5, 1,
                "lessons/part5/lesson-5.1.md", 45));

        addLesson(new Lesson("5.2", "Databases and Authentication", 5, 2,
                "lessons/part5/lesson-5.2.md", 40));
    }

    /**
     * Creates Part 6 lesson structure
     */
    private void createPart6Lessons() {
        addLesson(new Lesson("6.1", "Introduction to Frontend Development with Kotlin/JS", 6, 1,
                "lessons/part6/lesson-6.1.md", 45));
    }

    /**
     * Creates Part 7 lesson structure
     */
    private void createPart7Lessons() {
        addLesson(new Lesson("7.1", "Full-Stack Capstone Project", 7, 1,
                "lessons/part7/lesson-7.1.md", 60));
    }
}

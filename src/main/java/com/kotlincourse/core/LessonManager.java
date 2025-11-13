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
}

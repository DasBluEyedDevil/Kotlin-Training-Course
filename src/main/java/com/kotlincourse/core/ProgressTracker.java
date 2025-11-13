package com.kotlincourse.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.kotlincourse.model.UserProgress;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manages saving and loading user progress
 */
public class ProgressTracker {
    private static final String PROGRESS_DIR = ".kotlin-course";
    private static final String PROGRESS_FILE = "user-progress.json";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private UserProgress userProgress;
    private Gson gson;
    private Path progressFilePath;

    public ProgressTracker() {
        initializeGson();
        initializeProgressFile();
        loadProgress();
    }

    private void initializeGson() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) ->
                                context.serialize(src.format(DATE_FORMATTER)))
                .registerTypeAdapter(LocalDateTime.class,
                        (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                                LocalDateTime.parse(json.getAsString(), DATE_FORMATTER))
                .create();
    }

    private void initializeProgressFile() {
        try {
            String userHome = System.getProperty("user.home");
            Path progressDir = Paths.get(userHome, PROGRESS_DIR);

            if (!Files.exists(progressDir)) {
                Files.createDirectories(progressDir);
            }

            progressFilePath = progressDir.resolve(PROGRESS_FILE);

        } catch (IOException e) {
            System.err.println("Error initializing progress file: " + e.getMessage());
            // Fallback to current directory
            progressFilePath = Paths.get(PROGRESS_FILE);
        }
    }

    /**
     * Loads user progress from disk
     */
    public void loadProgress() {
        try {
            if (Files.exists(progressFilePath)) {
                String json = Files.readString(progressFilePath);
                userProgress = gson.fromJson(json, UserProgress.class);
                System.out.println("Progress loaded from: " + progressFilePath);
            } else {
                userProgress = new UserProgress();
                System.out.println("No existing progress found. Starting fresh.");
            }
        } catch (IOException e) {
            System.err.println("Error loading progress: " + e.getMessage());
            userProgress = new UserProgress();
        }
    }

    /**
     * Saves user progress to disk
     */
    public void saveProgress() {
        try {
            userProgress.setLastAccessed(LocalDateTime.now());
            String json = gson.toJson(userProgress);
            Files.writeString(progressFilePath, json);
            System.out.println("Progress saved to: " + progressFilePath);
        } catch (IOException e) {
            System.err.println("Error saving progress: " + e.getMessage());
        }
    }

    /**
     * Marks a lesson as started
     */
    public void markLessonStarted(String lessonId) {
        userProgress.markLessonStarted(lessonId);
        saveProgress();
    }

    /**
     * Marks a lesson as completed
     */
    public void markLessonCompleted(String lessonId) {
        userProgress.markLessonComplete(lessonId);
        saveProgress();
    }

    /**
     * Marks a challenge as completed
     */
    public void markChallengeCompleted(String challengeId, boolean passed, int attempts) {
        userProgress.markChallengeComplete(challengeId, passed, attempts);
        saveProgress();
    }

    /**
     * Checks if a lesson is completed
     */
    public boolean isLessonCompleted(String lessonId) {
        return userProgress.isLessonCompleted(lessonId);
    }

    /**
     * Gets the current user progress
     */
    public UserProgress getUserProgress() {
        return userProgress;
    }

    /**
     * Calculates overall course completion percentage
     */
    public double getOverallProgress(int totalLessons) {
        if (totalLessons == 0) {
            return 0.0;
        }

        long completedCount = userProgress.getLessonProgress().values().stream()
                .filter(UserProgress.LessonProgress::isCompleted)
                .count();

        return (completedCount * 100.0) / totalLessons;
    }

    /**
     * Resets all progress (for testing or starting over)
     */
    public void resetProgress() {
        userProgress = new UserProgress();
        saveProgress();
    }
}

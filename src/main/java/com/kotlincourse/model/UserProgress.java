package com.kotlincourse.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tracks user progress through the course
 */
public class UserProgress {
    private Map<String, LessonProgress> lessonProgress;
    private Map<String, ChallengeProgress> challengeProgress;
    private LocalDateTime lastAccessed;
    private int totalMinutesSpent;

    public UserProgress() {
        this.lessonProgress = new HashMap<>();
        this.challengeProgress = new HashMap<>();
        this.lastAccessed = LocalDateTime.now();
        this.totalMinutesSpent = 0;
    }

    // Getters and Setters
    public Map<String, LessonProgress> getLessonProgress() {
        return lessonProgress;
    }

    public void setLessonProgress(Map<String, LessonProgress> lessonProgress) {
        this.lessonProgress = lessonProgress;
    }

    public Map<String, ChallengeProgress> getChallengeProgress() {
        return challengeProgress;
    }

    public void setChallengeProgress(Map<String, ChallengeProgress> challengeProgress) {
        this.challengeProgress = challengeProgress;
    }

    public LocalDateTime getLastAccessed() {
        return lastAccessed;
    }

    public void setLastAccessed(LocalDateTime lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public int getTotalMinutesSpent() {
        return totalMinutesSpent;
    }

    public void setTotalMinutesSpent(int totalMinutesSpent) {
        this.totalMinutesSpent = totalMinutesSpent;
    }

    // Helper methods
    public void markLessonComplete(String lessonId) {
        LessonProgress progress = lessonProgress.getOrDefault(lessonId, new LessonProgress(lessonId));
        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        lessonProgress.put(lessonId, progress);
    }

    public void markLessonStarted(String lessonId) {
        LessonProgress progress = lessonProgress.getOrDefault(lessonId, new LessonProgress(lessonId));
        if (progress.getStartedAt() == null) {
            progress.setStartedAt(LocalDateTime.now());
        }
        lessonProgress.put(lessonId, progress);
    }

    public boolean isLessonCompleted(String lessonId) {
        return lessonProgress.containsKey(lessonId) && lessonProgress.get(lessonId).isCompleted();
    }

    public void markChallengeComplete(String challengeId, boolean passed, int attempts) {
        ChallengeProgress progress = new ChallengeProgress(challengeId);
        progress.setCompleted(true);
        progress.setPassed(passed);
        progress.setAttempts(attempts);
        progress.setCompletedAt(LocalDateTime.now());
        challengeProgress.put(challengeId, progress);
    }

    /**
     * Tracks progress for a single lesson
     */
    public static class LessonProgress {
        private String lessonId;
        private boolean completed;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private int minutesSpent;

        public LessonProgress(String lessonId) {
            this.lessonId = lessonId;
            this.completed = false;
            this.minutesSpent = 0;
        }

        // Getters and Setters
        public String getLessonId() {
            return lessonId;
        }

        public void setLessonId(String lessonId) {
            this.lessonId = lessonId;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public LocalDateTime getStartedAt() {
            return startedAt;
        }

        public void setStartedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
        }

        public LocalDateTime getCompletedAt() {
            return completedAt;
        }

        public void setCompletedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
        }

        public int getMinutesSpent() {
            return minutesSpent;
        }

        public void setMinutesSpent(int minutesSpent) {
            this.minutesSpent = minutesSpent;
        }
    }

    /**
     * Tracks progress for a single challenge
     */
    public static class ChallengeProgress {
        private String challengeId;
        private boolean completed;
        private boolean passed;
        private int attempts;
        private LocalDateTime completedAt;

        public ChallengeProgress(String challengeId) {
            this.challengeId = challengeId;
            this.completed = false;
            this.passed = false;
            this.attempts = 0;
        }

        // Getters and Setters
        public String getChallengeId() {
            return challengeId;
        }

        public void setChallengeId(String challengeId) {
            this.challengeId = challengeId;
        }

        public boolean isCompleted() {
            return completed;
        }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public int getAttempts() {
            return attempts;
        }

        public void setAttempts(int attempts) {
            this.attempts = attempts;
        }

        public LocalDateTime getCompletedAt() {
            return completedAt;
        }

        public void setCompletedAt(LocalDateTime completedAt) {
            this.completedAt = completedAt;
        }
    }
}

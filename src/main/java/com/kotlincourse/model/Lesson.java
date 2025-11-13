package com.kotlincourse.model;

import java.util.List;

/**
 * Represents a single lesson in the course
 */
public class Lesson {
    private String id;
    private String title;
    private int partNumber;
    private int lessonNumber;
    private String markdownFile;
    private List<CodeChallenge> challenges;
    private int estimatedMinutes;

    public Lesson(String id, String title, int partNumber, int lessonNumber,
                  String markdownFile, int estimatedMinutes) {
        this.id = id;
        this.title = title;
        this.partNumber = partNumber;
        this.lessonNumber = lessonNumber;
        this.markdownFile = markdownFile;
        this.estimatedMinutes = estimatedMinutes;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    public int getLessonNumber() {
        return lessonNumber;
    }

    public void setLessonNumber(int lessonNumber) {
        this.lessonNumber = lessonNumber;
    }

    public String getMarkdownFile() {
        return markdownFile;
    }

    public void setMarkdownFile(String markdownFile) {
        this.markdownFile = markdownFile;
    }

    public List<CodeChallenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<CodeChallenge> challenges) {
        this.challenges = challenges;
    }

    public int getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(int estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getDisplayName() {
        return String.format("Lesson %d.%d: %s", partNumber, lessonNumber, title);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}

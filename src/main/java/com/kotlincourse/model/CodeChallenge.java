package com.kotlincourse.model;

import java.util.List;
import java.util.Map;

/**
 * Represents a coding challenge within a lesson
 */
public class CodeChallenge {
    private String id;
    private String lessonId;
    private String title;
    private String description;
    private String starterCode;
    private String solution;
    private List<TestCase> testCases;
    private List<String> hints;
    private int difficultyLevel; // 1-5

    public CodeChallenge(String id, String lessonId, String title, String description,
                         String starterCode, String solution, int difficultyLevel) {
        this.id = id;
        this.lessonId = lessonId;
        this.title = title;
        this.description = description;
        this.starterCode = starterCode;
        this.solution = solution;
        this.difficultyLevel = difficultyLevel;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStarterCode() {
        return starterCode;
    }

    public void setStarterCode(String starterCode) {
        this.starterCode = starterCode;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public List<String> getHints() {
        return hints;
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    /**
     * Represents a test case for validating code challenge solutions
     */
    public static class TestCase {
        private String input;
        private String expectedOutput;
        private String description;

        public TestCase(String input, String expectedOutput, String description) {
            this.input = input;
            this.expectedOutput = expectedOutput;
            this.description = description;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }

        public void setExpectedOutput(String expectedOutput) {
            this.expectedOutput = expectedOutput;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

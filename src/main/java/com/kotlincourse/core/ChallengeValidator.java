package com.kotlincourse.core;

import com.kotlincourse.model.CodeChallenge;
import com.kotlincourse.core.CodeExecutor.ExecutionResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates code challenge solutions
 */
public class ChallengeValidator {
    private CodeExecutor codeExecutor;

    public ChallengeValidator(CodeExecutor codeExecutor) {
        this.codeExecutor = codeExecutor;
    }

    /**
     * Validates a code solution against all test cases
     *
     * @param challenge The challenge to validate against
     * @param userCode  The user's code solution
     * @return ValidationResult containing pass/fail status and details
     */
    public ValidationResult validate(CodeChallenge challenge, String userCode) {
        ValidationResult result = new ValidationResult();
        result.setChallengeId(challenge.getId());

        if (challenge.getTestCases() == null || challenge.getTestCases().isEmpty()) {
            // Simple execution check if no test cases
            ExecutionResult execResult = codeExecutor.execute(userCode);
            result.setPassed(execResult.isSuccess());
            result.setMessage(execResult.isSuccess() ?
                    "Code executed successfully!" :
                    "Code failed to execute: " + execResult.getError());
            return result;
        }

        // Run each test case
        List<TestCaseResult> testResults = new ArrayList<>();
        int passedCount = 0;

        for (CodeChallenge.TestCase testCase : challenge.getTestCases()) {
            TestCaseResult tcResult = runTestCase(userCode, testCase);
            testResults.add(tcResult);

            if (tcResult.isPassed()) {
                passedCount++;
            }
        }

        result.setTestCaseResults(testResults);
        result.setPassed(passedCount == challenge.getTestCases().size());
        result.setMessage(String.format("Passed %d/%d test cases",
                passedCount, challenge.getTestCases().size()));

        return result;
    }

    /**
     * Runs a single test case
     */
    private TestCaseResult runTestCase(String userCode, CodeChallenge.TestCase testCase) {
        TestCaseResult result = new TestCaseResult();
        result.setDescription(testCase.getDescription());
        result.setExpectedOutput(testCase.getExpectedOutput());

        try {
            // For now, simple execution - in a real implementation,
            // we would inject the input and capture output
            ExecutionResult execResult = codeExecutor.execute(userCode);

            if (!execResult.isSuccess()) {
                result.setPassed(false);
                result.setActualOutput(execResult.getError());
                result.setMessage("Execution failed");
                return result;
            }

            String actualOutput = execResult.getOutput().trim();
            String expectedOutput = testCase.getExpectedOutput().trim();

            boolean passed = actualOutput.equals(expectedOutput) ||
                    actualOutput.contains(expectedOutput);

            result.setPassed(passed);
            result.setActualOutput(actualOutput);
            result.setMessage(passed ? "Test passed!" : "Output mismatch");

        } catch (Exception e) {
            result.setPassed(false);
            result.setActualOutput("");
            result.setMessage("Error: " + e.getMessage());
        }

        return result;
    }

    /**
     * Result of challenge validation
     */
    public static class ValidationResult {
        private String challengeId;
        private boolean passed;
        private String message;
        private List<TestCaseResult> testCaseResults;

        public ValidationResult() {
            this.passed = false;
            this.message = "";
            this.testCaseResults = new ArrayList<>();
        }

        // Getters and Setters
        public String getChallengeId() {
            return challengeId;
        }

        public void setChallengeId(String challengeId) {
            this.challengeId = challengeId;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<TestCaseResult> getTestCaseResults() {
            return testCaseResults;
        }

        public void setTestCaseResults(List<TestCaseResult> testCaseResults) {
            this.testCaseResults = testCaseResults;
        }

        public String getFormattedResults() {
            StringBuilder sb = new StringBuilder();
            sb.append(passed ? "✓ Challenge Passed!\n\n" : "✗ Challenge Failed\n\n");
            sb.append(message).append("\n\n");

            if (!testCaseResults.isEmpty()) {
                sb.append("Test Case Results:\n");
                for (int i = 0; i < testCaseResults.size(); i++) {
                    TestCaseResult tcr = testCaseResults.get(i);
                    sb.append(String.format("  %d. %s %s\n",
                            i + 1,
                            tcr.isPassed() ? "✓" : "✗",
                            tcr.getDescription()));

                    if (!tcr.isPassed()) {
                        sb.append(String.format("     Expected: %s\n", tcr.getExpectedOutput()));
                        sb.append(String.format("     Got: %s\n", tcr.getActualOutput()));
                    }
                }
            }

            return sb.toString();
        }
    }

    /**
     * Result of a single test case
     */
    public static class TestCaseResult {
        private String description;
        private boolean passed;
        private String expectedOutput;
        private String actualOutput;
        private String message;

        // Getters and Setters
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isPassed() {
            return passed;
        }

        public void setPassed(boolean passed) {
            this.passed = passed;
        }

        public String getExpectedOutput() {
            return expectedOutput;
        }

        public void setExpectedOutput(String expectedOutput) {
            this.expectedOutput = expectedOutput;
        }

        public String getActualOutput() {
            return actualOutput;
        }

        public void setActualOutput(String actualOutput) {
            this.actualOutput = actualOutput;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

package com.kotlincourse.core;

import javax.script.*;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Executes Kotlin code using the embedded Kotlin compiler
 */
public class CodeExecutor {
    private ScriptEngine kotlinEngine;
    private ScriptEngineManager manager;

    public CodeExecutor() {
        initializeEngine();
    }

    private void initializeEngine() {
        try {
            manager = new ScriptEngineManager();
            kotlinEngine = manager.getEngineByExtension("kts");

            if (kotlinEngine == null) {
                System.err.println("WARNING: Kotlin script engine not available. Using fallback mode.");
            }
        } catch (Exception e) {
            System.err.println("Error initializing Kotlin engine: " + e.getMessage());
        }
    }

    /**
     * Executes Kotlin code and returns the result
     *
     * @param code The Kotlin code to execute
     * @return ExecutionResult containing output, errors, and success status
     */
    public ExecutionResult execute(String code) {
        ExecutionResult result = new ExecutionResult();

        if (kotlinEngine == null) {
            result.setSuccess(false);
            result.setError("Kotlin execution engine not available. Please ensure Kotlin is properly configured.");
            return result;
        }

        try {
            // Capture output
            Writer outputWriter = new StringWriter();
            Writer errorWriter = new StringWriter();

            ScriptContext context = new SimpleScriptContext();
            context.setWriter(outputWriter);
            context.setErrorWriter(errorWriter);
            kotlinEngine.setContext(context);

            // Execute the code
            Object evalResult = kotlinEngine.eval(code);

            // Collect output
            String output = outputWriter.toString();
            String error = errorWriter.toString();

            result.setSuccess(true);
            result.setOutput(output);

            if (evalResult != null && !evalResult.toString().equals("kotlin.Unit")) {
                result.setOutput(result.getOutput() + "\nResult: " + evalResult.toString());
            }

            if (!error.isEmpty()) {
                result.setError(error);
            }

        } catch (ScriptException e) {
            result.setSuccess(false);
            result.setError("Compilation/Runtime Error:\n" + e.getMessage());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setError("Unexpected Error:\n" + e.getMessage());
        }

        return result;
    }

    /**
     * Validates code against expected output (for challenges)
     *
     * @param code The code to execute
     * @param expectedOutput The expected output
     * @return true if output matches expected
     */
    public boolean validate(String code, String expectedOutput) {
        ExecutionResult result = execute(code);

        if (!result.isSuccess()) {
            return false;
        }

        String actualOutput = result.getOutput().trim();
        String expected = expectedOutput.trim();

        return actualOutput.equals(expected) || actualOutput.contains(expected);
    }

    /**
     * Result of code execution
     */
    public static class ExecutionResult {
        private boolean success;
        private String output;
        private String error;
        private long executionTimeMs;

        public ExecutionResult() {
            this.success = false;
            this.output = "";
            this.error = "";
            this.executionTimeMs = 0;
        }

        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public long getExecutionTimeMs() {
            return executionTimeMs;
        }

        public void setExecutionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
        }

        public String getFormattedResult() {
            StringBuilder sb = new StringBuilder();

            if (success) {
                sb.append("✓ Execution Successful\n\n");
                if (!output.isEmpty()) {
                    sb.append("Output:\n").append(output);
                }
            } else {
                sb.append("✗ Execution Failed\n\n");
                if (!error.isEmpty()) {
                    sb.append(error);
                }
            }

            return sb.toString();
        }
    }
}

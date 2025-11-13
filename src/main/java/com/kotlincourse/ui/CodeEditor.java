package com.kotlincourse.ui;

import com.kotlincourse.core.CodeExecutor;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

/**
 * Interactive code editor with execution capabilities
 */
public class CodeEditor {
    private BorderPane root;
    private CodeArea codeArea;
    private TextArea outputArea;
    private CodeExecutor codeExecutor;

    public CodeEditor(CodeExecutor codeExecutor) {
        this.codeExecutor = codeExecutor;
        initializeUI();
    }

    private void initializeUI() {
        root = new BorderPane();
        root.getStyleClass().add("code-editor");

        // Top: Toolbar
        HBox toolbar = createToolbar();
        root.setTop(toolbar);

        // Center: Split pane with code editor and output
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.HORIZONTAL);
        splitPane.setDividerPositions(0.6);

        // Left: Code editor
        VBox editorPane = createEditorPane();

        // Right: Output area
        VBox outputPane = createOutputPane();

        splitPane.getItems().addAll(editorPane, outputPane);
        root.setCenter(splitPane);
    }

    /**
     * Creates the toolbar with action buttons
     */
    private HBox createToolbar() {
        HBox toolbar = new HBox(10);
        toolbar.setPadding(new Insets(10));
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("editor-toolbar");

        Label title = new Label("Interactive Code Playground");
        title.getStyleClass().add("toolbar-title");

        Button runButton = new Button("▶ Run Code");
        runButton.getStyleClass().addAll("toolbar-button", "run-button");
        runButton.setOnAction(e -> runCode());

        Button clearButton = new Button("Clear");
        clearButton.getStyleClass().add("toolbar-button");
        clearButton.setOnAction(e -> clearEditor());

        Button resetButton = new Button("Reset Output");
        resetButton.getStyleClass().add("toolbar-button");
        resetButton.setOnAction(e -> clearOutput());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        toolbar.getChildren().addAll(title, spacer, clearButton, resetButton, runButton);

        return toolbar;
    }

    /**
     * Creates the code editor pane
     */
    private VBox createEditorPane() {
        VBox editorPane = new VBox(5);
        editorPane.setPadding(new Insets(10));

        Label label = new Label("Kotlin Code:");
        label.getStyleClass().add("editor-label");

        codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace; -fx-font-size: 14px;");
        codeArea.getStyleClass().add("code-area");

        // Set default starter code
        codeArea.replaceText("""
                fun main() {
                    println("Hello, Kotlin!")
                }
                """);

        VBox.setVgrow(codeArea, Priority.ALWAYS);

        editorPane.getChildren().addAll(label, codeArea);

        return editorPane;
    }

    /**
     * Creates the output pane
     */
    private VBox createOutputPane() {
        VBox outputPane = new VBox(5);
        outputPane.setPadding(new Insets(10));

        Label label = new Label("Output:");
        label.getStyleClass().add("editor-label");

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-font-family: 'Consolas', 'Monaco', monospace; -fx-font-size: 12px;");
        outputArea.getStyleClass().add("output-area");

        VBox.setVgrow(outputArea, Priority.ALWAYS);

        outputPane.getChildren().addAll(label, outputArea);

        return outputPane;
    }

    /**
     * Executes the code in the editor
     */
    private void runCode() {
        String code = codeArea.getText();

        if (code.trim().isEmpty()) {
            outputArea.setText("No code to execute. Please write some Kotlin code first.");
            return;
        }

        outputArea.setText("Executing...\n");

        // Run in background thread to avoid blocking UI
        new Thread(() -> {
            try {
                CodeExecutor.ExecutionResult result = codeExecutor.execute(code);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    outputArea.setText(result.getFormattedResult());
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    outputArea.setText("Error executing code:\n" + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Clears the code editor
     */
    private void clearEditor() {
        codeArea.clear();
    }

    /**
     * Clears the output area
     */
    private void clearOutput() {
        outputArea.clear();
    }

    /**
     * Sets the code in the editor
     */
    public void setCode(String code) {
        codeArea.replaceText(code);
    }

    /**
     * Gets the current code from the editor
     */
    public String getCode() {
        return codeArea.getText();
    }

    public Parent getRoot() {
        return root;
    }
}

package com.kotlincourse.ui;

import com.kotlincourse.core.LessonManager;
import com.kotlincourse.core.ProgressTracker;
import com.kotlincourse.model.Lesson;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.web.WebView;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.util.function.Consumer;

/**
 * Displays lesson content with navigation controls
 */
public class LessonViewer {
    private BorderPane root;
    private WebView contentView;
    private Label lessonTitle;
    private Lesson currentLesson;

    private LessonManager lessonManager;
    private ProgressTracker progressTracker;

    private Consumer<String> onNextLesson;
    private Consumer<String> onPreviousLesson;
    private Consumer<String> onMarkComplete;

    private Parser markdownParser;
    private HtmlRenderer htmlRenderer;

    public LessonViewer(LessonManager lessonManager, ProgressTracker progressTracker) {
        this.lessonManager = lessonManager;
        this.progressTracker = progressTracker;
        this.markdownParser = Parser.builder().build();
        this.htmlRenderer = HtmlRenderer.builder().build();
        initializeUI();
    }

    private void initializeUI() {
        root = new BorderPane();
        root.getStyleClass().add("lesson-viewer");
        root.setPadding(new Insets(10));

        // Top: Lesson title and progress
        VBox header = createHeader();
        root.setTop(header);

        // Center: Lesson content
        contentView = new WebView();
        contentView.getEngine().setUserStyleSheetLocation(
                getClass().getResource("/styles/lesson-content.css").toExternalForm()
        );

        ScrollPane scrollPane = new ScrollPane(contentView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        root.setCenter(scrollPane);

        // Bottom: Navigation buttons
        HBox navigationBar = createNavigationBar();
        root.setBottom(navigationBar);
    }

    /**
     * Creates the header with lesson title
     */
    private VBox createHeader() {
        VBox header = new VBox(5);
        header.setPadding(new Insets(0, 0, 10, 0));

        lessonTitle = new Label("Select a lesson to begin");
        lessonTitle.getStyleClass().add("lesson-title");

        header.getChildren().add(lessonTitle);
        return header;
    }

    /**
     * Creates the navigation bar with buttons
     */
    private HBox createNavigationBar() {
        HBox navBar = new HBox(10);
        navBar.setPadding(new Insets(10, 0, 0, 0));
        navBar.setAlignment(Pos.CENTER);

        Button prevButton = new Button("← Previous");
        prevButton.getStyleClass().add("nav-button");
        prevButton.setOnAction(e -> {
            if (currentLesson != null && onPreviousLesson != null) {
                onPreviousLesson.accept(currentLesson.getId());
            }
        });

        Button completeButton = new Button("Mark Complete");
        completeButton.getStyleClass().addAll("nav-button", "complete-button");
        completeButton.setOnAction(e -> {
            if (currentLesson != null && onMarkComplete != null) {
                onMarkComplete.accept(currentLesson.getId());
            }
        });

        Button nextButton = new Button("Next →");
        nextButton.getStyleClass().add("nav-button");
        nextButton.setOnAction(e -> {
            if (currentLesson != null && onNextLesson != null) {
                onNextLesson.accept(currentLesson.getId());
            }
        });

        Region spacer1 = new Region();
        Region spacer2 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        navBar.getChildren().addAll(prevButton, spacer1, completeButton, spacer2, nextButton);

        return navBar;
    }

    /**
     * Displays a lesson
     */
    public void displayLesson(Lesson lesson) {
        if (lesson == null) return;

        this.currentLesson = lesson;

        // Update title
        lessonTitle.setText(lesson.getDisplayName());

        // Load and render markdown content
        String markdownContent = lessonManager.getLessonContent(lesson.getId());
        String htmlContent = renderMarkdown(markdownContent);

        // Display in WebView
        contentView.getEngine().loadContent(htmlContent);
    }

    /**
     * Renders markdown to HTML
     */
    private String renderMarkdown(String markdown) {
        var document = markdownParser.parse(markdown);
        String bodyHtml = htmlRenderer.render(document);

        // Wrap in full HTML document with styling
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            line-height: 1.6;
                            color: #333;
                            max-width: 800px;
                            margin: 0 auto;
                            padding: 20px;
                            background-color: #ffffff;
                        }
                        h1, h2, h3 {
                            color: #2c3e50;
                            margin-top: 24px;
                        }
                        h1 {
                            border-bottom: 3px solid #3498db;
                            padding-bottom: 10px;
                        }
                        h2 {
                            border-bottom: 2px solid #95a5a6;
                            padding-bottom: 8px;
                        }
                        code {
                            background-color: #f4f4f4;
                            padding: 2px 6px;
                            border-radius: 3px;
                            font-family: 'Consolas', 'Monaco', monospace;
                            font-size: 0.9em;
                        }
                        pre {
                            background-color: #2c3e50;
                            color: #ecf0f1;
                            padding: 15px;
                            border-radius: 5px;
                            overflow-x: auto;
                        }
                        pre code {
                            background-color: transparent;
                            color: #ecf0f1;
                            padding: 0;
                        }
                        blockquote {
                            border-left: 4px solid #3498db;
                            margin-left: 0;
                            padding-left: 20px;
                            color: #555;
                            font-style: italic;
                        }
                        ul, ol {
                            margin-left: 20px;
                        }
                        li {
                            margin-bottom: 8px;
                        }
                        strong {
                            color: #2980b9;
                        }
                        .emoji {
                            font-size: 1.2em;
                        }
                    </style>
                </head>
                <body>
                """ + bodyHtml + """
                </body>
                </html>
                """;
    }

    // Setters for callbacks
    public void setOnNextLesson(Consumer<String> callback) {
        this.onNextLesson = callback;
    }

    public void setOnPreviousLesson(Consumer<String> callback) {
        this.onPreviousLesson = callback;
    }

    public void setOnMarkComplete(Consumer<String> callback) {
        this.onMarkComplete = callback;
    }

    public Parent getRoot() {
        return root;
    }
}

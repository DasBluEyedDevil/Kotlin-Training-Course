package com.kotlincourse.ui;

import com.kotlincourse.core.*;
import com.kotlincourse.model.Lesson;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * Main application window containing all UI components
 */
public class MainWindow {
    private BorderPane root;
    private ProgressSidebar progressSidebar;
    private LessonViewer lessonViewer;
    private CodeEditor codeEditor;

    private LessonManager lessonManager;
    private ProgressTracker progressTracker;
    private CodeExecutor codeExecutor;

    public MainWindow() {
        initializeCore();
        initializeUI();
        loadFirstLesson();
    }

    /**
     * Initializes core business logic components
     */
    private void initializeCore() {
        lessonManager = new LessonManager();
        progressTracker = new ProgressTracker();
        codeExecutor = new CodeExecutor();
    }

    /**
     * Initializes the user interface
     */
    private void initializeUI() {
        root = new BorderPane();
        root.getStyleClass().add("main-window");

        // Create top menu bar
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);

        // Create left sidebar (progress/navigation)
        progressSidebar = new ProgressSidebar(lessonManager, progressTracker);
        progressSidebar.setOnLessonSelected(this::onLessonSelected);
        root.setLeft(progressSidebar.getRoot());

        // Create center content area with split pane
        SplitPane centerPane = createCenterPane();
        root.setCenter(centerPane);

        // Create bottom status bar
        HBox statusBar = createStatusBar();
        root.setBottom(statusBar);
    }

    /**
     * Creates the menu bar
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem resetProgress = new MenuItem("Reset Progress");
        resetProgress.setOnAction(e -> resetProgress());
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(resetProgress, new SeparatorMenuItem(), exit);

        // View Menu
        Menu viewMenu = new Menu("View");
        MenuItem zoomIn = new MenuItem("Zoom In");
        MenuItem zoomOut = new MenuItem("Zoom Out");
        MenuItem resetZoom = new MenuItem("Reset Zoom");
        viewMenu.getItems().addAll(zoomIn, zoomOut, resetZoom);

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        about.setOnAction(e -> showAboutDialog());
        MenuItem guide = new MenuItem("Getting Started");
        helpMenu.getItems().addAll(guide, new SeparatorMenuItem(), about);

        menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);
        return menuBar;
    }

    /**
     * Creates the center pane with lesson viewer and code editor
     */
    private SplitPane createCenterPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.setDividerPositions(0.6);

        // Top: Lesson content viewer
        lessonViewer = new LessonViewer(lessonManager, progressTracker);
        lessonViewer.setOnNextLesson(this::onNextLesson);
        lessonViewer.setOnPreviousLesson(this::onPreviousLesson);
        lessonViewer.setOnMarkComplete(this::onMarkComplete);

        // Bottom: Interactive code editor
        codeEditor = new CodeEditor(codeExecutor);

        splitPane.getItems().addAll(lessonViewer.getRoot(), codeEditor.getRoot());

        return splitPane;
    }

    /**
     * Creates the status bar
     */
    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.getStyleClass().add("status-bar");

        Label statusLabel = new Label("Ready");
        statusLabel.getStyleClass().add("status-label");

        Label progressLabel = new Label(
                String.format("Progress: %.1f%%",
                        progressTracker.getOverallProgress(lessonManager.getTotalLessonCount()))
        );
        progressLabel.getStyleClass().add("progress-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        statusBar.getChildren().addAll(statusLabel, spacer, progressLabel);

        return statusBar;
    }

    /**
     * Loads the first lesson on startup
     */
    private void loadFirstLesson() {
        Lesson firstLesson = lessonManager.getLesson("1.1");
        if (firstLesson != null) {
            loadLesson(firstLesson);
        }
    }

    /**
     * Loads and displays a lesson
     */
    private void loadLesson(Lesson lesson) {
        if (lesson == null) return;

        lessonViewer.displayLesson(lesson);
        progressTracker.markLessonStarted(lesson.getId());
        progressSidebar.refresh();
        updateStatusBar();
    }

    /**
     * Event handler for lesson selection from sidebar
     */
    private void onLessonSelected(Lesson lesson) {
        loadLesson(lesson);
    }

    /**
     * Event handler for next lesson button
     */
    private void onNextLesson(String currentLessonId) {
        Lesson nextLesson = lessonManager.getNextLesson(currentLessonId);
        if (nextLesson != null) {
            loadLesson(nextLesson);
        } else {
            showAlert("End of Course", "Congratulations! You've reached the end of the available lessons.");
        }
    }

    /**
     * Event handler for previous lesson button
     */
    private void onPreviousLesson(String currentLessonId) {
        Lesson prevLesson = lessonManager.getPreviousLesson(currentLessonId);
        if (prevLesson != null) {
            loadLesson(prevLesson);
        }
    }

    /**
     * Event handler for marking lesson complete
     */
    private void onMarkComplete(String lessonId) {
        progressTracker.markLessonCompleted(lessonId);
        progressSidebar.refresh();
        updateStatusBar();
        showAlert("Lesson Complete", "Great job! Lesson marked as complete.");
    }

    /**
     * Updates the status bar
     */
    private void updateStatusBar() {
        if (root.getBottom() instanceof HBox) {
            HBox statusBar = (HBox) root.getBottom();
            if (statusBar.getChildren().size() > 1 && statusBar.getChildren().get(1) instanceof Label) {
                Label progressLabel = (Label) statusBar.getChildren().get(1);
                progressLabel.setText(
                        String.format("Progress: %.1f%%",
                                progressTracker.getOverallProgress(lessonManager.getTotalLessonCount()))
                );
            }
        }
    }

    /**
     * Resets all progress
     */
    private void resetProgress() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Reset Progress");
        alert.setHeaderText("Are you sure you want to reset all progress?");
        alert.setContentText("This action cannot be undone.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                progressTracker.resetProgress();
                progressSidebar.refresh();
                updateStatusBar();
            }
        });
    }

    /**
     * Shows the about dialog
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Kotlin Training Course");
        alert.setHeaderText("Kotlin Training Course v1.0");
        alert.setContentText(
                "An interactive desktop application for learning Kotlin\n" +
                        "from absolute zero to full-stack development.\n\n" +
                        "Built with JavaFX and powered by Kotlin.\n\n" +
                        "© 2024 - Educational Project"
        );
        alert.showAndWait();
    }

    /**
     * Shows a simple alert dialog
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Gets the root node
     */
    public Parent getRoot() {
        return root;
    }
}

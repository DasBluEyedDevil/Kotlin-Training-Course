package com.kotlincourse.ui;

import com.kotlincourse.core.LessonManager;
import com.kotlincourse.core.ProgressTracker;
import com.kotlincourse.model.Lesson;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/**
 * Sidebar showing course structure and progress
 */
public class ProgressSidebar {
    private VBox root;
    private TreeView<LessonTreeItem> lessonTree;
    private LessonManager lessonManager;
    private ProgressTracker progressTracker;
    private Consumer<Lesson> onLessonSelected;

    public ProgressSidebar(LessonManager lessonManager, ProgressTracker progressTracker) {
        this.lessonManager = lessonManager;
        this.progressTracker = progressTracker;
        initializeUI();
    }

    private void initializeUI() {
        root = new VBox(10);
        root.setPadding(new Insets(10));
        root.getStyleClass().add("progress-sidebar");
        root.setPrefWidth(300);
        root.setMinWidth(250);
        root.setMaxWidth(400);

        // Title
        Label title = new Label("Course Content");
        title.getStyleClass().add("sidebar-title");

        // Lesson tree view
        lessonTree = createLessonTree();

        root.getChildren().addAll(title, lessonTree);
    }

    /**
     * Creates the lesson tree view
     */
    private TreeView<LessonTreeItem> createLessonTree() {
        TreeItem<LessonTreeItem> rootItem = new TreeItem<>(
                new LessonTreeItem("Kotlin Course", null, LessonTreeItemType.ROOT)
        );
        rootItem.setExpanded(true);

        // Get all part numbers
        List<Integer> partNumbers = lessonManager.getPartNumbers();

        for (int partNumber : partNumbers) {
            TreeItem<LessonTreeItem> partItem = new TreeItem<>(
                    new LessonTreeItem("Part " + partNumber + ": The Absolute Basics", null, LessonTreeItemType.PART)
            );
            partItem.setExpanded(true);

            // Add lessons for this part
            List<Lesson> partLessons = lessonManager.getLessonsByPart(partNumber);
            for (Lesson lesson : partLessons) {
                TreeItem<LessonTreeItem> lessonItem = new TreeItem<>(
                        new LessonTreeItem(lesson.getTitle(), lesson, LessonTreeItemType.LESSON)
                );
                partItem.getChildren().add(lessonItem);
            }

            rootItem.getChildren().add(partItem);
        }

        TreeView<LessonTreeItem> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(false);
        treeView.getStyleClass().add("lesson-tree");

        // Custom cell factory to show completion status
        treeView.setCellFactory(tv -> new TreeCell<LessonTreeItem>() {
            @Override
            protected void updateItem(LessonTreeItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getDisplayText());

                    // Add completion indicator
                    if (item.getType() == LessonTreeItemType.LESSON && item.getLesson() != null) {
                        boolean completed = progressTracker.isLessonCompleted(item.getLesson().getId());
                        if (completed) {
                            setText("✓ " + item.getDisplayText());
                            setStyle("-fx-text-fill: #4CAF50;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            }
        });

        // Handle lesson selection
        treeView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.getValue().getType() == LessonTreeItemType.LESSON) {
                Lesson lesson = newVal.getValue().getLesson();
                if (lesson != null && onLessonSelected != null) {
                    onLessonSelected.accept(lesson);
                }
            }
        });

        VBox.setVgrow(treeView, javafx.scene.layout.Priority.ALWAYS);
        return treeView;
    }

    /**
     * Refreshes the tree view to show updated progress
     */
    public void refresh() {
        lessonTree.refresh();
    }

    /**
     * Sets the callback for when a lesson is selected
     */
    public void setOnLessonSelected(Consumer<Lesson> callback) {
        this.onLessonSelected = callback;
    }

    public Parent getRoot() {
        return root;
    }

    /**
     * Represents an item in the lesson tree
     */
    private static class LessonTreeItem {
        private String displayText;
        private Lesson lesson;
        private LessonTreeItemType type;

        public LessonTreeItem(String displayText, Lesson lesson, LessonTreeItemType type) {
            this.displayText = displayText;
            this.lesson = lesson;
            this.type = type;
        }

        public String getDisplayText() {
            return displayText;
        }

        public Lesson getLesson() {
            return lesson;
        }

        public LessonTreeItemType getType() {
            return type;
        }

        @Override
        public String toString() {
            return displayText;
        }
    }

    /**
     * Type of tree item
     */
    private enum LessonTreeItemType {
        ROOT, PART, LESSON
    }
}

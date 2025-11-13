package com.kotlincourse;

import com.kotlincourse.ui.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main JavaFX Application Entry Point
 */
public class KotlinCourseApp extends Application {

    private static final String APP_TITLE = "Kotlin Training Course - From Zero to Full-Stack";
    private static final int WINDOW_WIDTH = 1400;
    private static final int WINDOW_HEIGHT = 900;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create main window
            MainWindow mainWindow = new MainWindow();
            Scene scene = new Scene(mainWindow.getRoot(), WINDOW_WIDTH, WINDOW_HEIGHT);

            // Load CSS stylesheet
            String css = getClass().getResource("/styles/application.css").toExternalForm();
            scene.getStylesheets().add(css);

            // Configure primary stage
            primaryStage.setTitle(APP_TITLE);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);

            // Set application icon (if available)
            try {
                Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                // Icon not available, continue without it
            }

            // Show the window
            primaryStage.show();

            System.out.println("Kotlin Course Application started successfully!");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error starting application: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        System.out.println("Application closing...");
        // Cleanup resources if needed
    }

    public static void main(String[] args) {
        launch(args);
    }
}

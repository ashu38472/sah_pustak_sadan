package ashu.sah.SahPustakSadan.Front_end.Stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SceneManager {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * -- SETTER --
     *  Set the primary stage that will be used for scene switching
     * <p>
     *
     * -- GETTER --
     *  Get the current stage
     *
     */
    @Getter
    @Setter
    private Stage primaryStage;

    // Stage state preservation
    private static class StageState {
        double x, y, width, height;
        boolean maximized, iconified, fullScreen;
        boolean alwaysOnTop, resizable;

        StageState(Stage stage) {
            this.x = stage.getX();
            this.y = stage.getY();
            this.width = stage.getWidth();
            this.height = stage.getHeight();
            this.maximized = stage.isMaximized();
            this.iconified = stage.isIconified();
            this.fullScreen = stage.isFullScreen();
            this.alwaysOnTop = stage.isAlwaysOnTop();
            this.resizable = stage.isResizable();
        }

        void applyTo(Stage stage) {
            if (!maximized && !fullScreen) {
                stage.setX(x);
                stage.setY(y);
                stage.setWidth(width);
                stage.setHeight(height);
            }
            stage.setMaximized(maximized);
            stage.setIconified(iconified);
            stage.setFullScreen(fullScreen);
            stage.setAlwaysOnTop(alwaysOnTop);
            stage.setResizable(resizable);
        }
    }

    /**
     * Switch to a new scene by loading the FXML file while preserving stage state
     * @param fxmlPath The path to the FXML file (e.g., "classpath:/scenes/dashboard.fxml")
     * @param title The title for the new scene (optional, can be null)
     * @param width The width of the new scene (optional, 0 to keep current width)
     * @param height The height of the new scene (optional, 0 to keep current height)
     * @param preserveState Whether to preserve the current window state (maximized, position, etc.)
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchScene(String fxmlPath, String title, double width, double height, boolean preserveState) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set. Call setPrimaryStage() first.");
        }

        // Capture current stage state if preserving
        StageState currentState = preserveState ? new StageState(primaryStage) : null;

        Resource resource = resourceLoader.getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
        fxmlLoader.setControllerFactory(applicationContext::getBean);

        Parent root = fxmlLoader.load();

        Scene scene;
        if (width > 0 && height > 0 && !preserveState) {
            // Only set specific dimensions if not preserving state
            scene = new Scene(root, width, height);
        } else if (preserveState && currentState != null) {
            // Use current dimensions when preserving state
            scene = new Scene(root, currentState.width, currentState.height);
        } else {
            // Use default dimensions from FXML
            scene = new Scene(root);
        }

        primaryStage.setScene(scene);

        // Apply preserved state if requested
        if (preserveState && currentState != null) {
            // Use Platform.runLater to ensure the scene is fully loaded before applying state
            javafx.application.Platform.runLater(() -> currentState.applyTo(primaryStage));
        }

        if (title != null && !title.trim().isEmpty()) {
            primaryStage.setTitle(title);
        }

        primaryStage.show();
    }

    /**
     * Switch to a new scene with default dimensions and preserve current stage state
     * @param fxmlPath The path to the FXML file
     * @param title The title for the new scene
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchScene(String fxmlPath, String title) throws IOException {
        switchScene(fxmlPath, title, 0, 0, true);
    }

    /**
     * Switch to a new scene keeping the current title, dimensions, and stage state
     * @param fxmlPath The path to the FXML file
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchScene(String fxmlPath) throws IOException {
        switchScene(fxmlPath, null, 0, 0, true);
    }

    /**
     * Switch to a new scene with specific dimensions (will not preserve state)
     * @param fxmlPath The path to the FXML file
     * @param title The title for the new scene
     * @param width The width of the new scene
     * @param height The height of the new scene
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchSceneWithDimensions(String fxmlPath, String title, double width, double height) throws IOException {
        switchScene(fxmlPath, title, width, height, false);
    }

    /**
     * Switch to a new scene and reset to default state (not maximized, centered)
     * @param fxmlPath The path to the FXML file
     * @param title The title for the new scene
     * @param width The width of the new scene
     * @param height The height of the new scene
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchSceneAndReset(String fxmlPath, String title, double width, double height) throws IOException {
        switchScene(fxmlPath, title, width, height, false);

        // Reset stage to centered position
        javafx.application.Platform.runLater(() -> {
            primaryStage.setMaximized(false);
            primaryStage.setFullScreen(false);
            primaryStage.centerOnScreen();
        });
    }

    /**
     * Maximize the current stage
     */
    public void maximizeStage() {
        if (primaryStage != null) {
            primaryStage.setMaximized(true);
        }
    }

    /**
     * Minimize the current stage
     */
    public void minimizeStage() {
        if (primaryStage != null) {
            primaryStage.setIconified(true);
        }
    }

    /**
     * Toggle fullscreen mode
     */
    public void toggleFullScreen() {
        if (primaryStage != null) {
            primaryStage.setFullScreen(!primaryStage.isFullScreen());
        }
    }

    /**
     * Center the stage on screen
     */
    public void centerStage() {
        if (primaryStage != null) {
            primaryStage.centerOnScreen();
        }
    }

    /**
     * Get current stage state information
     * @return String with current stage state details
     */
    public String getStageStateInfo() {
        if (primaryStage == null) {
            return "No stage available";
        }

        return String.format("Stage State - X: %.0f, Y: %.0f, Width: %.0f, Height: %.0f, " +
                        "Maximized: %s, Minimized: %s, FullScreen: %s",
                primaryStage.getX(), primaryStage.getY(),
                primaryStage.getWidth(), primaryStage.getHeight(),
                primaryStage.isMaximized(), primaryStage.isIconified(),
                primaryStage.isFullScreen());
    }

    /**
     * Close the application
     */
    public void closeApplication() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }
}
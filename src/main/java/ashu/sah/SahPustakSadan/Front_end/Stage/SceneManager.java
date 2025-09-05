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
import java.util.function.Consumer;

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

    // -------------------------------------------------------------------------
    // ViewTuple: holds both controller and root
    // -------------------------------------------------------------------------
    public static class ViewTuple<T> {
        private final T controller;
        private final Parent view;

        public ViewTuple(T controller, Parent view) {
            this.controller = controller;
            this.view = view;
        }

        public T getController() { return controller; }
        public Parent getView() { return view; }
    }

    // -------------------------------------------------------------------------
    // FXML Loading Methods
    // -------------------------------------------------------------------------

    public <T> ViewTuple<T> loadViewWithRoot(String fxmlPath, Consumer<FXMLLoader> configurer) throws IOException {
        Resource resource = resourceLoader.getResource(fxmlPath);
        FXMLLoader loader = new FXMLLoader(resource.getURL());
        loader.setControllerFactory(applicationContext::getBean);

        if (configurer != null) {
            configurer.accept(loader);
        }

        Parent root = loader.load();
        T controller = loader.getController();
        return new ViewTuple<>(controller, root);
    }

    public <T> ViewTuple<T> loadViewWithRoot(String fxmlPath) throws IOException {
        return loadViewWithRoot(fxmlPath, null);
    }

    /**
     * Load controller only (if you don’t need the root).
     */
    @SuppressWarnings("unchecked")
    public <T> T loadView(String fxmlPath) throws IOException {
        return (T) loadViewWithRoot(fxmlPath).getController();
    }


    /**
     * Load root only (if you don’t need the controller).
     */
    public Parent loadRoot(String fxmlPath) throws IOException {
        return loadViewWithRoot(fxmlPath).getView();
    }

    // -------------------------------------------------------------------------
    // Scene Switching Methods
    // -------------------------------------------------------------------------

    public void switchScene(String fxmlPath, String title, double width, double height, boolean preserveState) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set. Call setPrimaryStage() first.");
        }

        Parent root = loadRoot(fxmlPath);
        Scene scene = (width > 0 && height > 0) ? new Scene(root, width, height) : new Scene(root);

        primaryStage.setScene(scene);
        if (title != null && !title.trim().isEmpty()) {
            primaryStage.setTitle(title);
        }
        primaryStage.show();
    }

    public void switchScene(String fxmlPath, String title) throws IOException {
        switchScene(fxmlPath, title, 0, 0, true);
    }

    public void switchScene(String fxmlPath) throws IOException {
        switchScene(fxmlPath, null, 0, 0, true);
    }

    public void switchSceneWithDimensions(String fxmlPath, String title, double width, double height) throws IOException {
        switchScene(fxmlPath, title, width, height, false);
    }

    public void switchSceneAndReset(String fxmlPath, String title, double width, double height) throws IOException {
        switchScene(fxmlPath, title, width, height, false);

        // Reset stage to centered position
        javafx.application.Platform.runLater(() -> {
            primaryStage.setMaximized(false);
            primaryStage.setFullScreen(false);
            primaryStage.centerOnScreen();
        });
    }

    // -------------------------------------------------------------------------
    // Stage Utility Methods
    // -------------------------------------------------------------------------

    public void maximizeStage() {
        if (primaryStage != null) {
            primaryStage.setMaximized(true);
        }
    }

    public void minimizeStage() {
        if (primaryStage != null) {
            primaryStage.setIconified(true);
        }
    }

    public void toggleFullScreen() {
        if (primaryStage != null) {
            primaryStage.setFullScreen(!primaryStage.isFullScreen());
        }
    }

    public void centerStage() {
        if (primaryStage != null) {
            primaryStage.centerOnScreen();
        }
    }

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

    public void closeApplication() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }
}

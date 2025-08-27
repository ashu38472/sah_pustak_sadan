package ashu.sah.SahPustakSadan.Front_end.Service;

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

    /**
     * Switch to a new scene by loading the FXML file
     * @param fxmlPath The path to the FXML file (e.g., "classpath:/scenes/dashboard.fxml")
     * @param title The title for the new scene (optional, can be null)
     * @param width The width of the new scene (optional, 0 to keep current width)
     * @param height The height of the new scene (optional, 0 to keep current height)
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchScene(String fxmlPath, String title, double width, double height) throws IOException {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage not set. Call setPrimaryStage() first.");
        }

        Resource resource = resourceLoader.getResource(fxmlPath);
        FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
        fxmlLoader.setControllerFactory(applicationContext::getBean);

        Parent root = fxmlLoader.load();

        Scene scene;
        if (width > 0 && height > 0) {
            scene = new Scene(root, width, height);
        } else {
            scene = new Scene(root);
        }

        primaryStage.setScene(scene);

        if (title != null && !title.trim().isEmpty()) {
            primaryStage.setTitle(title);
        }

        primaryStage.show();
    }

    /**
     * Switch to a new scene with default dimensions
     * @param fxmlPath The path to the FXML file
     * @param title The title for the new scene
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchScene(String fxmlPath, String title) throws IOException {
        switchScene(fxmlPath, title, 0, 0);
    }

    /**
     * Switch to a new scene keeping the current title and dimensions
     * @param fxmlPath The path to the FXML file
     * @throws IOException if the FXML file cannot be loaded
     */
    public void switchScene(String fxmlPath) throws IOException {
        switchScene(fxmlPath, null, 0, 0);
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
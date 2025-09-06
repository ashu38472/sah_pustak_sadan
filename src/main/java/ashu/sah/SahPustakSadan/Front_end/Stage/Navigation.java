package ashu.sah.SahPustakSadan.Front_end.Stage;

import ashu.sah.SahPustakSadan.Front_end.Controller.Dashboard.DashboardController;
import ashu.sah.SahPustakSadan.Front_end.Controller.Product.ProductController;
import ashu.sah.SahPustakSadan.Front_end.Controller.Product.ProductFormController;
import ashu.sah.SahPustakSadan.Front_end.Types.ProductDTO;
import ashu.sah.SahPustakSadan.Service.UserSession;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Component
public class Navigation {

    @Autowired private SceneManager sceneManager;
    @Autowired private UserSession userSession;

    private final Map<String, CachedScene<?>> sceneCache = new HashMap<>();
    private BorderPane rootPane;
    private Node currentContent;

    // Scene IDs
    private static final String PRODUCT_LIST = "product_list";
    private static final String PRODUCT_FORM = "product_form";
    private static final String DASHBOARD = "dashboard";

    // -------------------------------------------------------------------------
    // Initialization
    // -------------------------------------------------------------------------
    public void init(BorderPane rootPane) {
        this.rootPane = rootPane;
    }

    public void enterApp() {
        try {
            clearAllCache(); // fresh start after login
            sceneManager.switchScene("classpath:/scenes/app.fxml", "Sah Pustak Sadan");
        } catch (IOException e) {
            log.error("Failed to enter app", e);
        }
    }

    // -------------------------------------------------------------------------
    // Cached Scene wrapper
    // -------------------------------------------------------------------------
    private static class CachedScene<T> {
        private final T controller;
        private final Parent view;
        private final String fxmlPath;
        private long lastAccessed;

        public CachedScene(T controller, Parent view, String fxmlPath) {
            this.controller = controller;
            this.view = view;
            this.fxmlPath = fxmlPath;
            this.lastAccessed = System.currentTimeMillis();
        }

        public T getController() {
            this.lastAccessed = System.currentTimeMillis();
            return controller;
        }

        public Parent getView() {
            this.lastAccessed = System.currentTimeMillis();
            return view;
        }
    }

    private <T> SceneManager.ViewTuple<T> getOrLoadScene(String sceneId, String fxmlPath) {
        try {
            @SuppressWarnings("unchecked")
            CachedScene<T> cached = (CachedScene<T>) sceneCache.get(sceneId);

            if (cached != null) {
                log.debug("Using cached scene: {}", sceneId);
                return new SceneManager.ViewTuple<>(cached.getController(), cached.getView());
            }

            log.debug("Loading new scene: {}", sceneId);
            SceneManager.ViewTuple<T> tuple = sceneManager.loadViewWithRoot(fxmlPath);

            if (tuple != null) {
                sceneCache.put(sceneId, new CachedScene<>(tuple.getController(), tuple.getView(), fxmlPath));
            }
            return tuple;
        } catch (Exception e) {
            log.error("Failed to load scene: {}", sceneId, e);
            return null;
        }
    }

    // -------------------------------------------------------------------------
    // Main content setter
    // -------------------------------------------------------------------------
    public void setMainContent(Node content) {
        System.out.println("set scene - "+ (rootPane ==null)+" current scene - "+ (currentContent != null));

        if (rootPane == null) {
            throw new IllegalStateException("Navigation rootPane not initialized");
        }

        if (currentContent != null) {
            rootPane.getChildren().remove(currentContent);
        }

        rootPane.setCenter(content);
        currentContent = content;

        if (content instanceof Region region) {
            region.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.80));
        }
    }

    // -------------------------------------------------------------------------
    // Navigation methods
    // -------------------------------------------------------------------------
    public void navigateToProductList() {
        SceneManager.ViewTuple<ProductController> tuple =
                getOrLoadScene(PRODUCT_LIST, "classpath:/scenes/product/product.fxml");

        if (tuple != null) {
            tuple.getController().refreshDataIfNeeded();
            setMainContent(tuple.getView());
        }
    }

    public void navigateToProductForm(Consumer<ProductDTO> onSaveCallback) {
        ProductFormController controller = loadFreshProductForm();
        if (controller != null) {
            controller.setOnSaveCallback(onSaveCallback);
        }
    }

    public void navigateToProductFormWithData(ProductDTO product, Consumer<ProductDTO> onSaveCallback) {
        ProductFormController controller = loadFreshProductForm();
        if (controller != null) {
            controller.setProduct(product);
            controller.setOnSaveCallback(onSaveCallback);
        }
    }

    private ProductFormController loadFreshProductForm() {
        try {
            sceneCache.remove(PRODUCT_FORM); // always load fresh form
            SceneManager.ViewTuple<ProductFormController> tuple =
                    sceneManager.loadViewWithRoot("classpath:/scenes/product/product-form.fxml");
            if (tuple != null) {
                setMainContent(tuple.getView());
                return tuple.getController();
            }
        } catch (Exception e) {
            log.error("Failed to load product form", e);
        }
        return null;
    }

    public void navigateToDashboard() {
        SceneManager.ViewTuple<DashboardController> tuple =
                getOrLoadScene(DASHBOARD, "classpath:/scenes/dashboard/dashboard.fxml");
        if (tuple != null) {
            tuple.getController().refreshData();
            setMainContent(tuple.getView());
        }
    }

    // -------------------------------------------------------------------------
    // Logout
    // -------------------------------------------------------------------------
    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be redirected to the login screen.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userSession.logout();
                sceneManager.switchScene("classpath:/scenes/login.fxml", "Login - Sah Pustak Sadan");
            } catch (IOException e) {
                log.error("Error during logout", e);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Cache management
    // -------------------------------------------------------------------------
    public void clearScene(String sceneId) {
        sceneCache.remove(sceneId);
        log.debug("Cleared scene from cache: {}", sceneId);
    }

    public void clearAllCache() {
        sceneCache.clear();
        log.debug("Cleared all scene cache");
    }
}

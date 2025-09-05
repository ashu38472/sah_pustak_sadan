package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.Front_end.Controller.Dashboard.DashboardController;
import ashu.sah.SahPustakSadan.Front_end.Controller.Product.ProductController;
import ashu.sah.SahPustakSadan.Front_end.Stage.SceneManager;
import ashu.sah.SahPustakSadan.Service.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class AppController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebarContainer;
    @FXML private Label statusLabel;
    @FXML private Label dateTimeLabel;

    @Autowired private SceneManager sceneManager;
    @Autowired private UserSession userSession;
    @Autowired private SidebarController sidebarController;

    // Getter methods for accessing controllers from other components
    // Controllers for different sections
    @Getter
    private DashboardController dashboardController;
    @Getter
    private ProductController productController;

    // Current loaded content
    private Node currentContent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupResponsiveLayout();
        setupSidebar();
        startClock();
        loadDashboard(); // Load dashboard by default
        sidebarController.setActiveButton("dashboard");
        statusLabel.setText("Application loaded successfully");
    }

    private void setupResponsiveLayout() {
        sidebarContainer.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.20));
        sidebarContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        sidebarContainer.setMaxHeight(Double.MAX_VALUE);
    }

    private void setupSidebar() {
        VBox sidebar = sidebarController.createSidebar(rootPane, this::handleNavigation);
        sidebarContainer.getChildren().setAll(sidebar.getChildren());
    }

    private void handleNavigation(String buttonId) {
        switch (buttonId) {
            case "dashboard" -> handleDashboard();
            case "invoice" -> handleInvoice();
            case "product" -> handleProduct();
            case "priceCalc" -> handlePriceCalculator();
            case "profile" -> handleProfile();
            case "logout" -> handleLogout();
            case "reportBug" -> handleReportBug();
        }
    }

    private void handleDashboard() {
        sidebarController.setActiveButton("dashboard");
        loadDashboard();
        statusLabel.setText("Dashboard loaded");
    }

    private void handleInvoice() {
        sidebarController.setActiveButton("invoice");
        try {
            statusLabel.setText("Opening invoice management...");
            showComingSoonAlert("Invoice Management");
        } catch (Exception e) {
            showErrorAlert("Error opening invoice screen", e.getMessage());
        }
    }

    private void handleProduct() {
        sidebarController.setActiveButton("product");
        loadProductManagement();
    }

    private void handlePriceCalculator() {
        sidebarController.setActiveButton("priceCalc");
        try {
            statusLabel.setText("Opening price calculator...");
            showComingSoonAlert("Price Calculator");
        } catch (Exception e) {
            showErrorAlert("Error opening price calculator screen", e.getMessage());
        }
    }

    private void handleProfile() {
        sidebarController.setActiveButton("profile");
        try {
            statusLabel.setText("Opening profile...");
            showComingSoonAlert("User Profile");
        } catch (Exception e) {
            showErrorAlert("Error opening profile screen", e.getMessage());
        }
    }

    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be redirected to the login screen.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userSession.logout();
                sceneManager.switchScene("file:src/main/resources/scenes/login.fxml", "Login - Sah Pustak Sadan");
            } catch (IOException e) {
                showErrorAlert("Error during logout", "Could not return to login screen.");
            }
        }
    }

    @FXML
    private void handleReportBug() {
        try {
            statusLabel.setText("Opening bug report...");
            showComingSoonAlert("Bug Report System");
        } catch (Exception e) {
            showErrorAlert("Error opening bug report", e.getMessage());
        }
    }

    private void loadDashboard() {
        try {
            statusLabel.setText("Loading dashboard...");

            SceneManager.ViewTuple<DashboardController> tuple =
                    sceneManager.loadViewWithRoot("classpath:/scenes/dashboard/dashboard.fxml");

            dashboardController = tuple.getController();
            setMainContent(tuple.getView());

            statusLabel.setText("Dashboard loaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error loading dashboard", e.getMessage());
            statusLabel.setText("Failed to load dashboard");
        }
    }

    private void loadProductManagement() {
        try {
            statusLabel.setText("Loading product management...");

            SceneManager.ViewTuple<ProductController> tuple =
                    sceneManager.loadViewWithRoot("classpath:/scenes/product/product.fxml");

            productController = tuple.getController();
            setMainContent(tuple.getView());

            statusLabel.setText("Product management loaded successfully");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error loading product management", e.getMessage());
            statusLabel.setText("Failed to load product management");
        }
    }

    private void setMainContent(Node content) {
        if (currentContent != null) {
            rootPane.getChildren().remove(currentContent);
        }

        rootPane.setCenter(content);
        currentContent = content;

        // Ensure proper sizing
        if (content instanceof Region) {
            Region region = (Region) content;
            region.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.80));
        }
    }

    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            dateTimeLabel.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void showComingSoonAlert(String feature) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Coming Soon");
        alert.setHeaderText(feature + " - Coming Soon!");
        alert.setContentText("This feature is under development and will be available soon.");
        alert.showAndWait();
        statusLabel.setText("Ready");
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        statusLabel.setText("Error occurred");
    }

}
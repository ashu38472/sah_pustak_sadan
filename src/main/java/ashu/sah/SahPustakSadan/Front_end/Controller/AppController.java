package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.Front_end.Stage.Navigation;
import ashu.sah.SahPustakSadan.Service.UserSession;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class AppController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebarContainer;
    @FXML private Label statusLabel;
    @FXML private Label dateTimeLabel;

    @Autowired private UserSession userSession;
    @Autowired private SidebarController sidebarController;
    @Autowired private Navigation navigation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        navigation.init(rootPane);

        setupResponsiveLayout();
        setupSidebar();
        startClock();

        handleDashboard(); // Load dashboard by default
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
            case "product" -> handleProduct();
            case "invoice" -> showComingSoonAlert("Invoice Management");
            case "priceCalc" -> showComingSoonAlert("Price Calculator");
            case "profile" -> showComingSoonAlert("User Profile");
            case "logout" -> navigation.logout();
            case "reportBug" -> showComingSoonAlert("Bug Report System");
        }
    }

    private void handleDashboard() {
        try {
            navigation.navigateToDashboard();
            sidebarController.setActiveButton("dashboard");
            statusLabel.setText("Dashboard loaded Successfully");
        } catch (Exception e) {
            showErrorAlert("Navigation Error", "Could not load dashboard");
        }
    }

    private void handleProduct() {
        try {
            navigation.navigateToProductList();
            sidebarController.setActiveButton("product");
            statusLabel.setText("Products loaded Successfully");
        } catch (Exception e) {
            showErrorAlert("Navigation Error", "Could not load product");
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

    public void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
        statusLabel.setText("Error occurred");
    }

    public void updateStatus(String status) {
        statusLabel.setText(status);
    }
}

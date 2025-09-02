package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.Front_end.Stage.SceneManager;
import ashu.sah.SahPustakSadan.Service.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class AppController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebarContainer;
    @FXML private VBox mainContentContainer;
    @FXML private HBox statsContainer;
    @FXML private TableView<?> recentActivitiesTable;
    @FXML private Label statusLabel;
    @FXML private Label dateTimeLabel;

    @Autowired private SceneManager sceneManager;
    @Autowired private UserSession userSession;
    @Autowired private SidebarController sidebarController;

    // Stats text fields
    private Text todaySalesText;
    private Text totalProductsText;
    private Text lowStockText;
    private Text totalCustomersText;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupResponsiveLayout();

        // Use SidebarController to create the sidebar
        VBox sidebar = sidebarController.createSidebar(rootPane, this::handleNavigation);
        sidebarContainer.getChildren().setAll(sidebar.getChildren());

        createStatsCards();
        startClock();
        loadDashboardData();
        sidebarController.setActiveButton("dashboard");
        statusLabel.setText("Dashboard loaded successfully");
    }

    private void setupResponsiveLayout() {
        sidebarContainer.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.20));
        sidebarContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        sidebarContainer.setMaxHeight(Double.MAX_VALUE);

        mainContentContainer.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.80));
    }

    private void handleNavigation(String buttonId) {
        switch (buttonId) {
            case "dashboard" -> handleDashboard();
            case "invoice" -> handleInvoice();
            case "productStock" -> handleProductStock();
            case "priceCalc" -> handlePriceCalculator();
            case "profile" -> handleProfile();
            case "logout" -> handleLogout();
            case "reportBug" -> handleReportBug();
        }
    }

    private void createStatsCards() {
        statsContainer.getChildren().clear();

        // Today's Sales
        VBox salesCard = createStatCard("fas-dollar-sign", "Today's Sales", "₹0.00");
        todaySalesText = (Text) salesCard.getChildren().get(2);

        // Total Products
        VBox productsCard = createStatCard("fas-shopping-cart", "Total Products", "0");
        totalProductsText = (Text) productsCard.getChildren().get(2);

        // Low Stock Items
        VBox lowStockCard = createStatCard("fas-exclamation-triangle", "Low Stock Items", "0");
        lowStockText = (Text) lowStockCard.getChildren().get(2);

        // Total Customers
        VBox customersCard = createStatCard("fas-users", "Total Customers", "0");
        totalCustomersText = (Text) customersCard.getChildren().get(2);

        statsContainer.getChildren().addAll(salesCard, productsCard, lowStockCard, customersCard);

        // Make stats container responsive
        for (javafx.scene.Node card : statsContainer.getChildren()) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }
    }

    private VBox createStatCard(String iconName, String label, String value) {
        VBox card = new VBox();
        card.getStyleClass().add("stat_card");
        card.setSpacing(10);

        // Icon
        FontIcon icon = new FontIcon(iconName);
        icon.setIconSize(32);
        icon.getStyleClass().add("stat_icon");

        // Label
        Text labelText = new Text(label);
        labelText.getStyleClass().add("stat_label");

        // Value
        Text valueText = new Text(value);
        valueText.getStyleClass().add("stat_value");

        card.getChildren().addAll(icon, labelText, valueText);
        return card;
    }

    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            dateTimeLabel.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void loadDashboardData() {
        // TODO: Load actual data from your services
        // For now, setting some sample data
        todaySalesText.setText("₹2,450.00");
        totalProductsText.setText("156");
        lowStockText.setText("8");
        totalCustomersText.setText("45");
    }

    // Button handlers
    private void handleDashboard() {
        sidebarController.setActiveButton("dashboard");
        statusLabel.setText("Dashboard selected");
    }

    private void handleInvoice() {
        sidebarController.setActiveButton("invoice");
        try {
            statusLabel.setText("Opening invoice...");
            showComingSoonAlert("Invoice Management");
        } catch (Exception e) {
            showErrorAlert("Error opening invoice screen", e.getMessage());
        }
    }

    private void handleProductStock() {
        sidebarController.setActiveButton("productStock");
        try {
            statusLabel.setText("Opening product stock...");
            showComingSoonAlert("Product Stock Management");
        } catch (Exception e) {
            showErrorAlert("Error opening product stock screen", e.getMessage());
        }
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
                sceneManager.switchScene("classpath:/scenes/login.fxml", "Login - Sah Pustak Sadan");
            } catch (IOException e) {
                showErrorAlert("Error during logout", "Could not return to login screen.");
                e.printStackTrace();
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

    // Public methods for external access
    public void refreshSidebar() {
        sidebarController.refreshSidebar();
    }

    public void setActiveTab(String tabId) {
        sidebarController.setActiveButton(tabId);
    }

    // FXML action handlers (if needed for backward compatibility)
    public void handleDashboard(ActionEvent event) { handleDashboard(); }
    public void handleInvoice(ActionEvent event) { handleInvoice(); }
    public void handleProductStock(ActionEvent event) { handleProductStock(); }
    public void handlePriceCalculator(ActionEvent event) { handlePriceCalculator(); }
    public void handleProfile(ActionEvent event) { handleProfile(); }
    public void handleReportBug(ActionEvent event) { handleReportBug(); }

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
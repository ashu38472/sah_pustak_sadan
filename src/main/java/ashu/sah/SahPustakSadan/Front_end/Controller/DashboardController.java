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
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class DashboardController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebarContainer;
    @FXML private VBox logoContainer;
    @FXML private VBox buttonsContainer;
    @FXML private VBox reportBugContainer;
    @FXML private VBox mainContentContainer;
    @FXML private HBox statsContainer;
    @FXML private Text appNameLabel;
    @FXML private TableView<?> recentActivitiesTable;
    @FXML private Label statusLabel;
    @FXML private Label dateTimeLabel;

    @Autowired private SceneManager sceneManager;
    @Autowired private UserSession userSession;

    // Stats text fields
    private Text todaySalesText;
    private Text totalProductsText;
    private Text lowStockText;
    private Text totalCustomersText;

    // Button references
    private final Map<String, Button> sidebarButtons = new HashMap<>();

    // Button configuration
    private final List<ButtonConfig> buttonConfigs = Arrays.asList(
            new ButtonConfig("dashboard", "Dashboard", "fas-home", this::handleDashboard, true),
            new ButtonConfig("invoice", "Invoice", "fas-file-invoice", this::handleInvoice,
                    () -> userSession.canAccessInvoice()),
            new ButtonConfig("productStock", "Product Stock", "fas-clipboard-list", this::handleProductStock,
                    () -> userSession.canAccessProductStock()),
            new ButtonConfig("priceCalc", "Price Calculator", "fas-calculator", this::handlePriceCalculator,
                    () -> userSession.canAccessPriceCalculator()),
            new ButtonConfig("profile", "Profile", "far-user-circle", this::handleProfile,
                    () -> userSession.canAccessProfile()),
            new ButtonConfig("logout", "Logout", "fas-sign-out-alt", this::handleLogout, true)
    );

    private static class ButtonConfig {
        final String id;
        final String text;
        final String icon;
        final Runnable action;
        final java.util.function.Supplier<Boolean> visibilityCheck;

        ButtonConfig(String id, String text, String icon, Runnable action, boolean alwaysVisible) {
            this(id, text, icon, action, () -> alwaysVisible);
        }

        ButtonConfig(String id, String text, String icon, Runnable action,
                     java.util.function.Supplier<Boolean> visibilityCheck) {
            this.id = id;
            this.text = text;
            this.icon = icon;
            this.action = action;
            this.visibilityCheck = visibilityCheck;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupResponsiveLayout();
        loadAppName();
        createSidebarButtons();
        createStatsCards();
        startClock();
        loadDashboardData();
        setActiveButton("dashboard");
        statusLabel.setText("Dashboard loaded successfully");
    }

    private void setupResponsiveLayout() {
        // Make sidebar responsive - 30% width with 10px padding
        sidebarContainer.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.20));
        sidebarContainer.setPrefHeight(Region.USE_COMPUTED_SIZE);
        sidebarContainer.setMaxHeight(Double.MAX_VALUE);

        // Main content takes remaining space
        mainContentContainer.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.80));

        // Sidebar internal spacing and sizing
        VBox.setVgrow(buttonsContainer, Priority.ALWAYS);
        VBox.setVgrow(reportBugContainer, Priority.NEVER);

        // Logo container sizing
        logoContainer.setPrefHeight(80);
        logoContainer.setMaxHeight(80);
        logoContainer.setMinHeight(80);
    }

    private void loadAppName() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
                String appName = properties.getProperty("spring.application.name", "Sah Pustak Sadan");
                appNameLabel.setText(appName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSidebarButtons() {
        buttonsContainer.getChildren().clear();
        sidebarButtons.clear();

        // Add spacing at the top
        Region topSpacer = new Region();
        topSpacer.setPrefHeight(20);
        buttonsContainer.getChildren().add(topSpacer);

        for (ButtonConfig config : buttonConfigs) {
            if (config.visibilityCheck.get()) {
                Button button = createSidebarButton(config);
                sidebarButtons.put(config.id, button);
                buttonsContainer.getChildren().add(button);

                // Add some spacing between buttons
                Region spacer = new Region();
                spacer.setPrefHeight(5);
                buttonsContainer.getChildren().add(spacer);
            }
        }

        // Add flexible space to push report bug section to bottom
        Region flexibleSpace = new Region();
        VBox.setVgrow(flexibleSpace, Priority.ALWAYS);
        buttonsContainer.getChildren().add(flexibleSpace);
    }

    private Button createSidebarButton(ButtonConfig config) {
        Button button = new Button(config.text);
        button.setId(config.id + "Btn");

        // Create icon
        FontIcon icon = new FontIcon(config.icon);
        icon.setIconSize(20);
        button.setGraphic(icon);

        // Styling
        button.getStyleClass().add("sidebar_btn");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);

        // Set action
        button.setOnAction(e -> config.action.run());

        return button;
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

    private void setActiveButton(String buttonId) {
        // Remove active class from all buttons
        sidebarButtons.values().forEach(button ->
                button.getStyleClass().remove("sidebar_btn_active"));

        // Add active class to specified button
        Button activeButton = sidebarButtons.get(buttonId);
        if (activeButton != null) {
            activeButton.getStyleClass().add("sidebar_btn_active");
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
        setActiveButton("dashboard");
        statusLabel.setText("Dashboard selected");
    }

    private void handleInvoice() {
        setActiveButton("invoice");
        try {
            statusLabel.setText("Opening invoice...");
            showComingSoonAlert("Invoice Management");
        } catch (Exception e) {
            showErrorAlert("Error opening invoice screen", e.getMessage());
        }
    }

    private void handleProductStock() {
        setActiveButton("productStock");
        try {
            statusLabel.setText("Opening product stock...");
            showComingSoonAlert("Product Stock Management");
        } catch (Exception e) {
            showErrorAlert("Error opening product stock screen", e.getMessage());
        }
    }

    private void handlePriceCalculator() {
        setActiveButton("priceCalc");
        try {
            statusLabel.setText("Opening price calculator...");
            showComingSoonAlert("Price Calculator");
        } catch (Exception e) {
            showErrorAlert("Error opening price calculator screen", e.getMessage());
        }
    }

    private void handleProfile() {
        setActiveButton("profile");
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
                sceneManager.switchScene("classpath:/scenes/login.fxml", "Login - Sah Pustak Sadan", 835, 560);
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

    // Public methods for navigation (called from FXML if needed)
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

    // Method to refresh sidebar based on user permissions
    public void refreshSidebar() {
        createSidebarButtons();
    }
}
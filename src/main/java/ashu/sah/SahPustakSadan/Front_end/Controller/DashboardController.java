package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.Front_end.Service.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class DashboardController implements Initializable {

    @FXML
    private Button newSaleBtn;
    @FXML
    private Button viewInventoryBtn;
    @FXML
    private Button addProductBtn;
    @FXML
    private Button reportsBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Button logoutBtn;

    @FXML
    private Text todaySalesText;
    @FXML
    private Text totalProductsText;
    @FXML
    private Text lowStockText;
    @FXML
    private Text totalCustomersText;

    @FXML
    private TableView<?> recentActivitiesTable;
    @FXML
    private Label statusLabel;
    @FXML
    private Label dateTimeLabel;

    @Autowired
    private SceneManager sceneManager;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Start the clock
        startClock();

        // Load dashboard data
        loadDashboardData();

        statusLabel.setText("Dashboard loaded successfully");
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

    @FXML
    private void handleNewSale() {
        try {
            statusLabel.setText("Opening new sale...");
            // TODO: Create and switch to sales screen
            // sceneManager.switchScene("classpath:/scenes/sale.fxml", "New Sale - Sah Pustak Sadan");
            showComingSoonAlert("New Sale");
        } catch (Exception e) {
            showErrorAlert("Error opening new sale screen", e.getMessage());
        }
    }

    @FXML
    private void handleViewInventory() {
        try {
            statusLabel.setText("Opening inventory...");
            // TODO: Create and switch to inventory screen
            // sceneManager.switchScene("classpath:/scenes/inventory.fxml", "Inventory - Sah Pustak Sadan");
            showComingSoonAlert("Inventory Management");
        } catch (Exception e) {
            showErrorAlert("Error opening inventory screen", e.getMessage());
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            statusLabel.setText("Opening add product...");
            // TODO: Create and switch to add product screen
            // sceneManager.switchScene("classpath:/scenes/add-product.fxml", "Add Product - Sah Pustak Sadan");
            showComingSoonAlert("Add Product");
        } catch (Exception e) {
            showErrorAlert("Error opening add product screen", e.getMessage());
        }
    }

    @FXML
    private void handleReports() {
        try {
            statusLabel.setText("Opening reports...");
            // TODO: Create and switch to reports screen
            // sceneManager.switchScene("classpath:/scenes/reports.fxml", "Reports - Sah Pustak Sadan");
            showComingSoonAlert("Reports");
        } catch (Exception e) {
            showErrorAlert("Error opening reports screen", e.getMessage());
        }
    }

    @FXML
    private void handleSettings() {
        try {
            statusLabel.setText("Opening settings...");
            // TODO: Create and switch to settings screen
            // sceneManager.switchScene("classpath:/scenes/settings.fxml", "Settings - Sah Pustak Sadan");
            showComingSoonAlert("Settings");
        } catch (Exception e) {
            showErrorAlert("Error opening settings screen", e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Are you sure you want to logout?");
        alert.setContentText("You will be redirected to the login screen.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                sceneManager.switchScene("classpath:/scenes/login.fxml", "Login - Sah Pustak Sadan", 835, 560);
            } catch (IOException e) {
                showErrorAlert("Error during logout", "Could not return to login screen.");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("The application will close.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
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
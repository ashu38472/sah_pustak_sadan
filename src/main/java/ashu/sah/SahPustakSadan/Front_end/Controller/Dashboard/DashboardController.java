package ashu.sah.SahPustakSadan.Front_end.Controller.Dashboard;

import ashu.sah.SahPustakSadan.Front_end.Stage.SceneManager;
import ashu.sah.SahPustakSadan.Model.Product;
import ashu.sah.SahPustakSadan.Model.Transaction;
import ashu.sah.SahPustakSadan.Service.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class DashboardController implements Initializable {

    @FXML private VBox mainContentContainer;
    @FXML private HBox statsContainer;
    @FXML private HBox quickActionsContainer;
    @FXML private TableView<ActivityRowData> recentActivitiesTable;
    @FXML private TableColumn<ActivityRowData, String> timeColumn;
    @FXML private TableColumn<ActivityRowData, String> typeColumn;
    @FXML private TableColumn<ActivityRowData, String> descriptionColumn;
    @FXML private TableColumn<ActivityRowData, String> amountColumn;
    @FXML private TableColumn<ActivityRowData, String> userColumn;

    @FXML private VBox lowStockSection;
    @FXML private TableView<LowStockRowData> lowStockTable;
    @FXML private TableColumn<LowStockRowData, String> productNameColumn;
    @FXML private TableColumn<LowStockRowData, String> categoryColumn;
    @FXML private TableColumn<LowStockRowData, Integer> currentStockColumn;
    @FXML private TableColumn<LowStockRowData, Integer> minStockColumn;
    @FXML private TableColumn<LowStockRowData, Void> stockActionColumn;
    @FXML private Label lowStockCountLabel;

    @FXML private Button createInvoiceBtn;
    @FXML private Button addProductBtn;
    @FXML private Button viewInventoryBtn;
    @FXML private Button addCustomerBtn;
    @FXML private Button refreshActivitiesBtn;

    @Autowired private SceneManager sceneManager;
    @Autowired private UserSession userSession;
//    @Autowired private ProductService productService;
//    @Autowired private TransactionService transactionService;

    // Stats text fields
    private Text todaySalesText;
    private Text totalProductsText;
    private Text lowStockText;
    private Text totalCustomersText;

    private final ObservableList<ActivityRowData> activitiesData = FXCollections.observableArrayList();
    private final ObservableList<LowStockRowData> lowStockData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupStatsCards();
        setupRecentActivitiesTable();
        setupLowStockTable();
        loadDashboardData();
    }

    private void setupStatsCards() {
        statsContainer.getChildren().clear();

        // Today's Sales
        VBox salesCard = createStatCard("fas-dollar-sign", "Today's Sales", "₹0.00", "#28a745");
        todaySalesText = (Text) salesCard.getChildren().get(2);

        // Total Products
        VBox productsCard = createStatCard("fas-shopping-cart", "Total Products", "0", "#007bff");
        totalProductsText = (Text) productsCard.getChildren().get(2);

        // Low Stock Items
        VBox lowStockCard = createStatCard("fas-exclamation-triangle", "Low Stock Items", "0", "#ffc107");
        lowStockText = (Text) lowStockCard.getChildren().get(2);

        // Total Customers
        VBox customersCard = createStatCard("fas-users", "Total Customers", "0", "#17a2b8");
        totalCustomersText = (Text) customersCard.getChildren().get(2);

        statsContainer.getChildren().addAll(salesCard, productsCard, lowStockCard, customersCard);

        // Make stats container responsive
        for (javafx.scene.Node card : statsContainer.getChildren()) {
            HBox.setHgrow(card, Priority.ALWAYS);
        }
    }

    private VBox createStatCard(String iconName, String label, String value, String accentColor) {
        VBox card = new VBox();
        card.getStyleClass().add("stat_card");
        card.setSpacing(10);
        card.setStyle("-fx-border-left-color: " + accentColor + "; -fx-border-left-width: 4px;");

        // Icon
        FontIcon icon = new FontIcon(iconName);
        icon.setIconSize(32);
        icon.getStyleClass().add("stat_icon");
        icon.setStyle("-fx-icon-color: " + accentColor + ";");

        // Label
        Text labelText = new Text(label);
        labelText.getStyleClass().add("stat_label");

        // Value
        Text valueText = new Text(value);
        valueText.getStyleClass().add("stat_value");

        card.getChildren().addAll(icon, labelText, valueText);
        return card;
    }

    private void setupRecentActivitiesTable() {
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user"));

        // Format time column
        timeColumn.setCellFactory(column -> new TableCell<ActivityRowData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: #666;");
                }
            }
        });

        // Format type column with colored badges
        typeColumn.setCellFactory(column -> new TableCell<ActivityRowData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    String color = switch (item.toLowerCase()) {
                        case "sale" -> "-fx-background-color: #d4edda; -fx-text-fill: #155724;";
                        case "purchase" -> "-fx-background-color: #d1ecf1; -fx-text-fill: #0c5460;";
                        case "return" -> "-fx-background-color: #f8d7da; -fx-text-fill: #721c24;";
                        default -> "-fx-background-color: #e2e3e5; -fx-text-fill: #383d41;";
                    };
                    setStyle(color + " -fx-background-radius: 4px; -fx-padding: 2 8 2 8;");
                }
            }
        });

        recentActivitiesTable.setItems(activitiesData);
    }

    private void setupLowStockTable() {
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        currentStockColumn.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        minStockColumn.setCellValueFactory(new PropertyValueFactory<>("minStock"));

        // Add action column with "Reorder" button
        stockActionColumn.setCellFactory(new Callback<TableColumn<LowStockRowData, Void>, TableCell<LowStockRowData, Void>>() {
            @Override
            public TableCell<LowStockRowData, Void> call(TableColumn<LowStockRowData, Void> param) {
                return new TableCell<LowStockRowData, Void>() {
                    private final Button reorderBtn = new Button("Reorder");

                    {
                        reorderBtn.getStyleClass().add("reorder_btn");
                        reorderBtn.setOnAction(event -> {
                            LowStockRowData data = getTableView().getItems().get(getIndex());
                            handleReorderProduct(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(reorderBtn);
                        }
                    }
                };
            }
        });

        lowStockTable.setItems(lowStockData);
    }

    private void loadDashboardData() {
        Task<Void> loadDataTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Load stats data
                loadStatsData();

                // Load recent activities
                loadRecentActivities();

                // Load low stock items
                loadLowStockItems();

                return null;
            }
        };

        loadDataTask.setOnSucceeded(e -> {
            // Data loaded successfully
        });

        loadDataTask.setOnFailed(e -> {
            showErrorAlert("Error loading dashboard data", loadDataTask.getException().getMessage());
        });

        Thread thread = new Thread(loadDataTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadStatsData() {
        try {
            // TODO: Replace with actual service calls
            javafx.application.Platform.runLater(() -> {
                todaySalesText.setText("₹12,450.00");
                totalProductsText.setText("156");
                lowStockText.setText("8");
                totalCustomersText.setText("45");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadRecentActivities() {
        try {
            // TODO: Replace with actual transaction service call
            javafx.application.Platform.runLater(() -> {
                activitiesData.clear();
                // Sample data
                activitiesData.addAll(
                        new ActivityRowData("10:30 AM", "Sale", "Invoice #INV-001 - Customer ABC", "₹2,500.00", "Admin"),
                        new ActivityRowData("09:15 AM", "Purchase", "Purchase Order #PO-001 from Supplier XYZ", "₹5,000.00", "Manager"),
                        new ActivityRowData("08:45 AM", "Sale", "Invoice #INV-002 - Customer DEF", "₹1,200.00", "Staff"),
                        new ActivityRowData("Yesterday", "Return", "Return processed for Invoice #INV-001", "₹500.00", "Admin")
                );
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLowStockItems() {
        try {
            // TODO: Replace with actual inventory service call
            javafx.application.Platform.runLater(() -> {
                lowStockData.clear();
                // Sample data
                lowStockData.addAll(
                        new LowStockRowData("Notebook A4", "Stationery", 5, 10),
                        new LowStockRowData("Pen Blue", "Stationery", 2, 20),
                        new LowStockRowData("Pencil HB", "Stationery", 8, 15)
                );
                lowStockCountLabel.setText(String.valueOf(lowStockData.size()));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Button handlers
    @FXML
    private void handleCreateInvoice() {
        try {
            sceneManager.switchScene("classpath:/scenes/invoice.fxml", "Create Invoice - Sah Pustak Sadan");
        } catch (IOException e) {
            showErrorAlert("Error", "Could not open invoice screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddProduct() {
        try {
            sceneManager.switchScene("classpath:/scenes/product/product.fxml", "Product Management - Sah Pustak Sadan");
        } catch (IOException e) {
            showErrorAlert("Error", "Could not open product screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewInventory() {
        try {
            sceneManager.switchScene("classpath:/scenes/inventory.fxml", "Inventory Management - Sah Pustak Sadan");
        } catch (IOException e) {
            showErrorAlert("Error", "Could not open inventory screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddCustomer() {
        try {
            sceneManager.switchScene("classpath:/scenes/customer/customer.fxml", "Customer Management - Sah Pustak Sadan");
        } catch (IOException e) {
            showErrorAlert("Error", "Could not open customer screen: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshActivities() {
        loadRecentActivities();
        showSuccessAlert("Refreshed", "Activities refreshed successfully.");
    }

    private void handleReorderProduct(LowStockRowData data) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reorder Product");
        alert.setHeaderText("Reorder: " + data.getProductName());
        alert.setContentText("Reorder functionality will be implemented soon.");
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data classes for table rows
    public static class ActivityRowData {
        private final String time;
        private final String type;
        private final String description;
        private final String amount;
        private final String user;

        public ActivityRowData(String time, String type, String description, String amount, String user) {
            this.time = time;
            this.type = type;
            this.description = description;
            this.amount = amount;
            this.user = user;
        }

        public String getTime() { return time; }
        public String getType() { return type; }
        public String getDescription() { return description; }
        public String getAmount() { return amount; }
        public String getUser() { return user; }
    }

    public static class LowStockRowData {
        private final String productName;
        private final String category;
        private final Integer currentStock;
        private final Integer minStock;

        public LowStockRowData(String productName, String category, Integer currentStock, Integer minStock) {
            this.productName = productName;
            this.category = category;
            this.currentStock = currentStock;
            this.minStock = minStock;
        }

        public String getProductName() { return productName; }
        public String getCategory() { return category; }
        public Integer getCurrentStock() { return currentStock; }
        public Integer getMinStock() { return minStock; }
    }
}
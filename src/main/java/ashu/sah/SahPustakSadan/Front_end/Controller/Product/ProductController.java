package ashu.sah.SahPustakSadan.Front_end.Controller.Product;

import ashu.sah.SahPustakSadan.Front_end.Stage.SceneManager;
import ashu.sah.SahPustakSadan.Model.Category;
import ashu.sah.SahPustakSadan.Model.Product;
//import ashu.sah.SahPustakSadan.Service.CategoryService;
//import ashu.sah.SahPustakSadan.Service.ProductService;
import ashu.sah.SahPustakSadan.Service.UserSession;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

@Component
public class ProductController implements Initializable {

    @FXML private VBox mainContentContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<ProductRowData> productsTable;
    @FXML private TableColumn<ProductRowData, String> codeColumn;
    @FXML private TableColumn<ProductRowData, String> nameColumn;
    @FXML private TableColumn<ProductRowData, String> categoryColumn;
    @FXML private TableColumn<ProductRowData, String> unitColumn;
    @FXML private TableColumn<ProductRowData, Double> basePriceColumn;
    @FXML private TableColumn<ProductRowData, Double> costPriceColumn;
    @FXML private TableColumn<ProductRowData, Integer> stockColumn;
    @FXML private TableColumn<ProductRowData, Integer> minStockColumn;
    @FXML private TableColumn<ProductRowData, String> statusColumn;
    @FXML private TableColumn<ProductRowData, Void> actionsColumn;
    @FXML private Label productCountLabel;
    @FXML private Label statusLabel;
    @FXML private Button addProductBtn;
    @FXML private Button refreshBtn;
    @FXML private Button clearFiltersBtn;

    @Autowired private SceneManager sceneManager;
    @Autowired private UserSession userSession;
//    @Autowired private ProductService productService;
//    @Autowired private CategoryService categoryService;

    private final ObservableList<ProductRowData> allProducts = FXCollections.observableArrayList();
    private FilteredList<ProductRowData> filteredProducts;
    private List<Category> categories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusFilter.setItems(FXCollections.observableArrayList("All Status", "Active", "Inactive"));
        statusFilter.setValue("All Status");
        setupTable();
        setupFilters();
        loadData();
    }

    private void setupTable() {
        // Setup table columns
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        basePriceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        costPriceColumn.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        minStockColumn.setCellValueFactory(new PropertyValueFactory<>("minStockLevel"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Format price columns
        basePriceColumn.setCellFactory(column -> new TableCell<ProductRowData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", item));
                }
            }
        });

        costPriceColumn.setCellFactory(column -> new TableCell<ProductRowData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("₹%.2f", item));
                }
            }
        });

        // Format status column with colored badges
        statusColumn.setCellFactory(column -> new TableCell<ProductRowData, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Active".equals(item)) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; " +
                                "-fx-background-radius: 4px; -fx-padding: 2 8 2 8;");
                    } else {
                        setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; " +
                                "-fx-background-radius: 4px; -fx-padding: 2 8 2 8;");
                    }
                }
            }
        });

        // Format stock column with warning colors for low stock
        stockColumn.setCellFactory(column -> new TableCell<ProductRowData, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    ProductRowData rowData = getTableView().getItems().get(getIndex());
                    if (item <= rowData.getMinStockLevel()) {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #28a745;");
                    }
                }
            }
        });

        // Setup actions column
        setupActionsColumn();

        // Setup filtered list
        filteredProducts = new FilteredList<>(allProducts);
        productsTable.setItems(filteredProducts);
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(new Callback<TableColumn<ProductRowData, Void>, TableCell<ProductRowData, Void>>() {
            @Override
            public TableCell<ProductRowData, Void> call(TableColumn<ProductRowData, Void> param) {
                return new TableCell<ProductRowData, Void>() {
                    private final HBox actionsBox = new HBox(5);
                    private final Button viewBtn = new Button();
                    private final Button editBtn = new Button();
                    private final Button deleteBtn = new Button();

                    {
                        // Setup buttons
                        viewBtn.setGraphic(new FontIcon("fas-eye"));
                        viewBtn.getStyleClass().addAll("action_btn", "view_btn");
                        viewBtn.setTooltip(new Tooltip("View Details"));

                        editBtn.setGraphic(new FontIcon("fas-edit"));
                        editBtn.getStyleClass().addAll("action_btn", "edit_btn");
                        editBtn.setTooltip(new Tooltip("Edit Product"));

                        deleteBtn.setGraphic(new FontIcon("fas-trash"));
                        deleteBtn.getStyleClass().addAll("action_btn", "delete_btn");
                        deleteBtn.setTooltip(new Tooltip("Delete Product"));

                        actionsBox.getChildren().addAll(viewBtn, editBtn, deleteBtn);

                        // Set button actions
                        viewBtn.setOnAction(event -> {
                            ProductRowData data = getTableView().getItems().get(getIndex());
                            handleViewProduct(data);
                        });

                        editBtn.setOnAction(event -> {
                            ProductRowData data = getTableView().getItems().get(getIndex());
                            handleEditProduct(data);
                        });

                        deleteBtn.setOnAction(event -> {
                            ProductRowData data = getTableView().getItems().get(getIndex());
                            handleDeleteProduct(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(actionsBox);
                        }
                    }
                };
            }
        });
    }

    private void setupFilters() {
        // Setup search filter
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyFilters();
        });

        // Setup category filter
        categoryFilter.setOnAction(e -> applyFilters());
        statusFilter.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        filteredProducts.setPredicate(createFilterPredicate());
        updateProductCount();
    }

    private Predicate<ProductRowData> createFilterPredicate() {
        return product -> {
            // Search filter
            String searchText = searchField.getText();
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                if (!(product.getName().toLowerCase().contains(lowerCaseFilter) ||
                        product.getCode().toLowerCase().contains(lowerCaseFilter) ||
                        product.getCategoryName().toLowerCase().contains(lowerCaseFilter))) {
                    return false;
                }
            }

            // Category filter
            String selectedCategory = categoryFilter.getValue();
            if (selectedCategory != null && !selectedCategory.isEmpty() &&
                    !selectedCategory.equals("All Categories")) {
                if (!product.getCategoryName().equals(selectedCategory)) {
                    return false;
                }
            }

            // Status filter
            String selectedStatus = statusFilter.getValue();
            if (selectedStatus != null && !selectedStatus.isEmpty()) {
                boolean isActive = "true".equals(selectedStatus);
                if ((isActive && !"Active".equals(product.getStatus())) ||
                        (!isActive && "Active".equals(product.getStatus()))) {
                    return false;
                }
            }

            return true;
        };
    }

    private void loadData() {
        statusLabel.setText("Loading products...");

        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // Load categories for filter
                loadCategories();

                // Load products
                loadProducts();

                return null;
            }
        };

        loadTask.setOnSucceeded(e -> {
            statusLabel.setText("Products loaded successfully");
            updateProductCount();
        });

        loadTask.setOnFailed(e -> {
            statusLabel.setText("Failed to load products");
            showErrorAlert("Error", "Failed to load products: " + loadTask.getException().getMessage());
        });

        Thread thread = new Thread(loadTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void loadCategories() {
        try {
            // TODO: Replace with actual service call
            // categories = categoryService.findAllActive();

            Platform.runLater(() -> {
                ObservableList<String> categoryNames = FXCollections.observableArrayList();
                categoryNames.add("All Categories");
                // Sample categories
                categoryNames.addAll("Stationery", "Books", "Electronics", "Accessories");
                categoryFilter.setItems(categoryNames);
                categoryFilter.setValue("All Categories");
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        try {
            Platform.runLater(() -> {
                allProducts.clear();
                // Sample product data
                allProducts.addAll(
                        new ProductRowData(1L, "NB001", "Notebook A4", "Stationery", "Piece", 25.0, 20.0, 50, 10, "Active"),
                        new ProductRowData(2L, "PEN001", "Pen Blue", "Stationery", "Piece", 5.0, 3.0, 15, 20, "Active"),
                        new ProductRowData(3L, "BK001", "Mathematics Book", "Books", "Piece", 150.0, 120.0, 25, 5, "Active"),
                        new ProductRowData(4L, "CAL001", "Calculator", "Electronics", "Piece", 250.0, 200.0, 8, 5, "Inactive"),
                        new ProductRowData(5L, "PEN002", "Pencil HB", "Stationery", "Piece", 3.0, 2.0, 5, 15, "Active")
                );
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateProductCount() {
        Platform.runLater(() -> {
            int filteredCount = filteredProducts.size();
            int totalCount = allProducts.size();
            productCountLabel.setText(String.format("(%d of %d products)", filteredCount, totalCount));
        });
    }

    // Event handlers
    @FXML
    private void handleAddProduct() {
        openProductDialog(null);
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleCategoryFilter() {
        applyFilters();
    }

    @FXML
    private void handleStatusFilter() {
        applyFilters();
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        categoryFilter.setValue("All Categories");
        statusFilter.setValue(null);
        applyFilters();
    }

    private void handleViewProduct(ProductRowData product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Details");
        alert.setHeaderText("Product: " + product.getName());

        String content = String.format(
                "Code: %s\nCategory: %s\nUnit: %s\nBase Price: ₹%.2f\nCost Price: ₹%.2f\nCurrent Stock: %d\nMin Stock: %d\nStatus: %s",
                product.getCode(), product.getCategoryName(), product.getUnit(),
                product.getBasePrice(), product.getCostPrice(),
                product.getCurrentStock(), product.getMinStockLevel(), product.getStatus()
        );

        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleEditProduct(ProductRowData product) {
        openProductDialog(product);
    }

    private void handleDeleteProduct(ProductRowData product) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Product");
        confirmation.setHeaderText("Are you sure you want to delete this product?");
        confirmation.setContentText(String.format("Product: %s (%s)\nThis action cannot be undone.",
                product.getName(), product.getCode()));

        ButtonType deleteButton = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmation.getButtonTypes().setAll(deleteButton, cancelButton);

        // Style the delete button as dangerous
        confirmation.getDialogPane().lookupButton(deleteButton).getStyleClass().add("danger_btn");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == deleteButton) {
            performDeleteProduct(product);
        }
    }

    private void performDeleteProduct(ProductRowData product) {
        Task<Boolean> deleteTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                // TODO: Call actual service to delete product
                // return productService.deleteProduct(product.getId());

                // Simulate deletion
                Thread.sleep(1000);
                return true;
            }
        };

        deleteTask.setOnSucceeded(e -> {
            if (deleteTask.getValue()) {
                allProducts.remove(product);
                updateProductCount();
                statusLabel.setText("Product deleted successfully");
                showSuccessAlert("Success", "Product deleted successfully");
            } else {
                showErrorAlert("Error", "Failed to delete product");
            }
        });

        deleteTask.setOnFailed(e -> {
            showErrorAlert("Error", "Failed to delete product: " + deleteTask.getException().getMessage());
        });

        statusLabel.setText("Deleting product...");
        Thread thread = new Thread(deleteTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void openProductDialog(ProductRowData product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/product-dialog.fxml"));
            Parent root = loader.load();

//            ProductDialogController controller = loader.getController();
//            controller.setCategories(categories);
//
//            if (product != null) {
//                controller.setProduct(product);
//            }
//
//            Stage dialogStage = new Stage();
//            dialogStage.setTitle(product == null ? "Add Product" : "Edit Product");
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//            dialogStage.initOwner(productsTable.getScene().getWindow());
//            dialogStage.setScene(new Scene(root));
//            dialogStage.setResizable(false);
//
//            controller.setDialogStage(dialogStage);
//            controller.setOnProductSaved(this::onProductSaved);

//            dialogStage.showAndWait();

        } catch (IOException e) {
            showErrorAlert("Error", "Could not open product dialog: " + e.getMessage());
        }
    }

    private void onProductSaved(ProductRowData savedProduct) {
        if (savedProduct.getId() == null || savedProduct.getId() == 0) {
            // New product
            savedProduct.setId((long) (allProducts.size() + 1));
            allProducts.add(savedProduct);
        } else {
            // Update existing product
            for (int i = 0; i < allProducts.size(); i++) {
                if (allProducts.get(i).getId().equals(savedProduct.getId())) {
                    allProducts.set(i, savedProduct);
                    break;
                }
            }
        }
        updateProductCount();
        statusLabel.setText("Product saved successfully");
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

    // Data class for table rows
    public static class ProductRowData {
        private Long id;
        private String code;
        private String name;
        private String categoryName;
        private String unit;
        private Double basePrice;
        private Double costPrice;
        private Integer currentStock;
        private Integer minStockLevel;
        private String status;

        public ProductRowData(Long id, String code, String name, String categoryName, String unit,
                              Double basePrice, Double costPrice, Integer currentStock,
                              Integer minStockLevel, String status) {
            this.id = id;
            this.code = code;
            this.name = name;
            this.categoryName = categoryName;
            this.unit = unit;
            this.basePrice = basePrice;
            this.costPrice = costPrice;
            this.currentStock = currentStock;
            this.minStockLevel = minStockLevel;
            this.status = status;
        }

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }

        public Double getBasePrice() { return basePrice; }
        public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }

        public Double getCostPrice() { return costPrice; }
        public void setCostPrice(Double costPrice) { this.costPrice = costPrice; }

        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

        public Integer getMinStockLevel() { return minStockLevel; }
        public void setMinStockLevel(Integer minStockLevel) { this.minStockLevel = minStockLevel; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
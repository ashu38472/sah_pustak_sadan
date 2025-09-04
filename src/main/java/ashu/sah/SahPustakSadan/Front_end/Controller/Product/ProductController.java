package ashu.sah.SahPustakSadan.Front_end.Controller.Product;

import ashu.sah.SahPustakSadan.APIController.product.ProductAPIController;
import ashu.sah.SahPustakSadan.Front_end.Stage.SceneManager;
import ashu.sah.SahPustakSadan.Front_end.Types.ProductDTO;
import ashu.sah.SahPustakSadan.Model.Category;
import ashu.sah.SahPustakSadan.Service.UserSession;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
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
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class ProductController implements Initializable {

    @FXML private VBox mainContentContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<ProductDTO> productsTable;
    @FXML private TableColumn<ProductDTO, String> codeColumn;
    @FXML private TableColumn<ProductDTO, String> nameColumn;
    @FXML private TableColumn<ProductDTO, String> categoryColumn;
    @FXML private TableColumn<ProductDTO, String> unitColumn;
    @FXML private TableColumn<ProductDTO, Double> basePriceColumn;
    @FXML private TableColumn<ProductDTO, Double> costPriceColumn;
    @FXML private TableColumn<ProductDTO, Integer> stockColumn;
    @FXML private TableColumn<ProductDTO, Integer> minStockColumn;
    @FXML private TableColumn<ProductDTO, String> statusColumn;
    @FXML private TableColumn<ProductDTO, Void> actionsColumn;
    @FXML private Label productCountLabel;
    @FXML private Label statusLabel;

    @Autowired private SceneManager sceneManager;
    @Autowired private UserSession userSession;
    @Autowired private ProductAPIController productAPIController;

    private final ObservableList<ProductDTO> allProducts = FXCollections.observableArrayList();
    private FilteredList<ProductDTO> filteredProducts;
    private List<Category> categories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        statusFilter.setItems(FXCollections.observableArrayList("All Status", "Active", "Inactive"));
        statusFilter.setValue("All Status");
        setupTable();
        setupFilters();
        loadData();
    }

    // ---------------- Setup Table ----------------
    private void setupTable() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        basePriceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        costPriceColumn.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        minStockColumn.setCellValueFactory(new PropertyValueFactory<>("minStockLevel"));

        statusColumn.setCellValueFactory(cellData -> {
            ProductDTO product = cellData.getValue();
            return new SimpleStringProperty(product.getIsActive() ? "Active" : "Inactive");
        });

        setupPriceColumn(basePriceColumn);
        setupPriceColumn(costPriceColumn);
        setupStatusColumn();
        setupStockColumn();
        setupActionsColumn();

        filteredProducts = new FilteredList<>(allProducts);
        productsTable.setItems(filteredProducts);
    }

    private void setupPriceColumn(TableColumn<ProductDTO, Double> column) {
        column.setCellFactory(col -> new TableCell<ProductDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("₹%.2f", item));
            }
        });
    }

    private void setupStatusColumn() {
        statusColumn.setCellFactory(col -> new TableCell<ProductDTO, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Active".equals(item)) {
                        setStyle("-fx-background-color: #d4edda; -fx-text-fill: #155724; "
                                + "-fx-background-radius: 4px; -fx-padding: 2 8 2 8;");
                    } else {
                        setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; "
                                + "-fx-background-radius: 4px; -fx-padding: 2 8 2 8;");
                    }
                }
            }
        });
    }

    private void setupStockColumn() {
        stockColumn.setCellFactory(col -> new TableCell<ProductDTO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    ProductDTO product = getTableView().getItems().get(getIndex());
                    if (product.getCurrentStock() != null && product.getMinStockLevel() != null
                            && product.getCurrentStock() <= product.getMinStockLevel()) {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #28a745;");
                    }
                }
            }
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<ProductDTO, Void>() {
            private final HBox actionsBox = new HBox(5);
            private final Button viewBtn = new Button();
            private final Button editBtn = new Button();
            private final Button deleteBtn = new Button();

            {
                viewBtn.setGraphic(new FontIcon("fas-eye"));
                editBtn.setGraphic(new FontIcon("fas-edit"));
                deleteBtn.setGraphic(new FontIcon("fas-trash"));
                actionsBox.getChildren().addAll(viewBtn, editBtn, deleteBtn);

                viewBtn.setOnAction(e -> handleViewProduct(getTableView().getItems().get(getIndex())));
                editBtn.setOnAction(e -> handleEditProduct(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> handleDeleteProduct(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionsBox);
            }
        });
    }

    // ---------------- Filters ----------------
    private void setupFilters() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        categoryFilter.setOnAction(e -> applyFilters());
        statusFilter.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        filteredProducts.setPredicate(createFilterPredicate());
        updateProductCount();
    }

    private Predicate<ProductDTO> createFilterPredicate() {
        return product -> {
            String searchText = searchField.getText();
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                if (!(product.getName().toLowerCase().contains(lowerCaseFilter) ||
                        product.getCode().toLowerCase().contains(lowerCaseFilter) ||
                        (product.getCategoryName() != null && product.getCategoryName().toLowerCase().contains(lowerCaseFilter)))) {
                    return false;
                }
            }

            String selectedCategory = categoryFilter.getValue();
            if (selectedCategory != null && !selectedCategory.equals("All Categories")) {
                if (!Objects.equals(product.getCategoryName(), selectedCategory)) return false;
            }

            String selectedStatus = statusFilter.getValue();
            if (selectedStatus != null && !selectedStatus.equals("All Status")) {
                if ((product.getIsActive() ? "Active" : "Inactive").equals(selectedStatus) == false) return false;
            }

            return true;
        };
    }

    // ---------------- Data Loading ----------------
    private void loadData() {
        statusLabel.setText("Loading products...");
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() {
                loadProducts();
                return null;
            }
        };

        loadTask.setOnSucceeded(e -> {
            statusLabel.setText("Products loaded successfully");
            updateProductCount();
        });
        loadTask.setOnFailed(e -> statusLabel.setText("Failed to load products"));

        new Thread(loadTask).start();
    }

    private void loadProducts() {
        try {
            List<Map<String, Object>> productMaps = productAPIController.getProducts();
            List<ProductDTO> dtos = productMaps.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            Platform.runLater(() -> {
                allProducts.clear();
                allProducts.addAll(dtos);
                updateCategoryFilter();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ProductDTO mapToDTO(Map<String, Object> map) {
        ProductDTO dto = new ProductDTO();
        dto.setId((Long) map.get("id"));
        dto.setCode((String) map.get("code"));
        dto.setName((String) map.get("name"));
        dto.setDescription((String) map.get("description"));
        dto.setBarcode((String) map.get("barcode"));
        dto.setUnit((String) map.get("unit"));
        dto.setBasePrice((Double) map.get("basePrice"));
        dto.setCostPrice((Double) map.get("costPrice"));
        dto.setIsActive((Boolean) map.get("isActive"));
        dto.setCategoryId((Long) map.get("categoryId"));
        dto.setCategoryName((String) map.get("categoryName"));
        return dto;
    }

    private void updateCategoryFilter() {
        ObservableList<String> categoryNames = FXCollections.observableArrayList("All Categories");
        allProducts.stream()
                .map(ProductDTO::getCategoryName)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .forEach(categoryNames::add);

        String currentSelection = categoryFilter.getValue();
        categoryFilter.setItems(categoryNames);
        categoryFilter.setValue(categoryNames.contains(currentSelection) ? currentSelection : "All Categories");
    }

    private void updateProductCount() {
        Platform.runLater(() -> {
            productCountLabel.setText(String.format("(%d of %d products)", filteredProducts.size(), allProducts.size()));
        });
    }

    // ---------------- Actions ----------------
    private void handleViewProduct(ProductDTO product) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Product Details");
        alert.setHeaderText("Product: " + product.getName());
        alert.setContentText("Code: " + product.getCode() +
                "\nCategory: " + product.getCategoryName() +
                "\nUnit: " + product.getUnit() +
                "\nBase Price: ₹" + product.getBasePrice() +
                "\nCost Price: ₹" + product.getCostPrice() +
                "\nStock: " + product.getCurrentStock() +
                "\nMin Stock: " + product.getMinStockLevel() +
                "\nStatus: " + (product.getIsActive() ? "Active" : "Inactive") +
                "\nDescription: " + product.getDescription());
        alert.showAndWait();
    }

    private void handleEditProduct(ProductDTO product) {
        openProductDialog(product);
    }

    private void handleDeleteProduct(ProductDTO product) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Are you sure you want to delete this product?");
        confirm.setContentText(product.getName() + " will be marked as inactive.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = productAPIController.deleteProduct(product.getId());
            if (success) {
                product.setIsActive(false); // update local DTO
                productsTable.refresh(); // refresh table to update status column
                updateProductCount();
            } else {
                showErrorAlert("Delete Failed", "Could not delete product.");
            }
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleAddProduct(ActionEvent event) {
        openProductDialog(null); // Passing null signals it's a new product
    }


    private void openProductDialog(ProductDTO product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/scenes/product-dialog.fxml"));
            Parent root = loader.load();
            ProductDialogController controller = loader.getController();
            controller.setCategories(categories);
            controller.setProduct(product);
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(productsTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            controller.setDialogStage(dialogStage);
            controller.setOnProductSaved(this::onProductSaved);
            dialogStage.showAndWait();
        } catch (IOException e) {
            showErrorAlert("Error", "Could not open product dialog: " + e.getMessage());
        }
    }

    private void onProductSaved(ProductDTO savedProduct) {
        // If product already exists in the list, replace it
        Optional<ProductDTO> existing = allProducts.stream()
                .filter(p -> p.getId().equals(savedProduct.getId()))
                .findFirst();

        if (existing.isPresent()) {
            int index = allProducts.indexOf(existing.get());
            allProducts.set(index, savedProduct);
        } else {
            allProducts.add(savedProduct);
        }

        // Refresh filters and table
        updateCategoryFilter();
        applyFilters();

        // Show success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Product saved successfully!");
        alert.showAndWait();
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        applyFilters();
    }

    @FXML
    private void handleCategoryFilter(ActionEvent event) {
        applyFilters();
    }

    @FXML
    private void handleStatusFilter(ActionEvent event) {
        applyFilters();
    }

    @FXML
    private void handleClearFilters(ActionEvent event) {
        searchField.clear();
        categoryFilter.setValue("All Categories");
        statusFilter.setValue("All Status");
        applyFilters();
    }

}

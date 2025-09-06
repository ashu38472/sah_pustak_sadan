package ashu.sah.SahPustakSadan.Front_end.Controller.Product;

import ashu.sah.SahPustakSadan.APIController.product.ProductAPIController;
import ashu.sah.SahPustakSadan.Front_end.Stage.Navigation;
import ashu.sah.SahPustakSadan.Front_end.Types.ProductDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Slf4j
@Component
public class ProductController implements Initializable {

    @FXML private TableView<ProductDTO> productsTable;
    @FXML private TableColumn<ProductDTO, String> codeColumn;
    @FXML private TableColumn<ProductDTO, String> nameColumn;
    @FXML private TableColumn<ProductDTO, String> descriptionColumn;
    @FXML private TableColumn<ProductDTO, String> unitColumn;
    @FXML private TableColumn<ProductDTO, Double> basePriceColumn;
    @FXML private TableColumn<ProductDTO, Double> costPriceColumn;
    @FXML private TableColumn<ProductDTO, Double> profitMarginColumn;
    @FXML private TableColumn<ProductDTO, String> categoryColumn;
    @FXML private TableColumn<ProductDTO, Boolean> isActiveColumn;
    @FXML private VBox mainContentContainer;
    @FXML private Label productCountLabel;
    @FXML private Label statusLabel;

    @FXML private Button addProductBtn;
    @FXML private Button refreshBtn;
    @FXML private Button clearFiltersBtn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<String> statusFilter;

    @FXML private TableColumn<ProductDTO, Integer> stockColumn;
    @FXML private TableColumn<ProductDTO, Integer> minStockColumn;
    @FXML private TableColumn<ProductDTO, String> statusColumn;
    @FXML private TableColumn<ProductDTO, Void> actionsColumn;

    private final ObservableList<ProductDTO> productData = FXCollections.observableArrayList();

    @Autowired private ProductAPIController productAPIController;
    @Autowired private Navigation navigation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTable();
        loadProducts();
    }

    private void setupTable() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        unitColumn.setCellValueFactory(new PropertyValueFactory<>("unit"));
        basePriceColumn.setCellValueFactory(new PropertyValueFactory<>("basePrice"));
        costPriceColumn.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        profitMarginColumn.setCellValueFactory(new PropertyValueFactory<>("profitMargin"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        isActiveColumn.setCellValueFactory(new PropertyValueFactory<>("isActive"));

        productsTable.setItems(productData);
    }

    private void loadProducts() {
        productData.clear();
        List<ProductDTO> products = productAPIController.getProducts();
        productData.addAll(products);
        log.info("Loaded {} products into table", products.size());
    }

    @FXML
    private void handleAddProduct() {
        navigation.navigateToProductForm(this::onProductSaved);
    }

    @FXML
    private void handleEditProduct() {
        ProductDTO selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            navigation.navigateToProductFormWithData(selected, this::onProductSaved);
        } else {
            showAlert("No Selection", "Please select a product to edit.");
        }
    }

    @FXML
    private void handleDeleteProduct() {
        ProductDTO selected = productsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean deleted = productAPIController.deleteProduct(selected.getId());
            if (deleted) {
                productData.remove(selected);
                log.info("Deleted product {}", selected.getCode());
            } else {
                showAlert("Delete Failed", "Could not delete product.");
            }
        } else {
            showAlert("No Selection", "Please select a product to delete.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadProducts();
        statusLabel.setText("Data refreshed");
    }

    @FXML
    private void handleSearch(KeyEvent event) {
        String query = searchField.getText().trim();
        List<ProductDTO> results = query.isEmpty()
                ? productAPIController.getProducts()
                : productAPIController.searchProducts(query);
        productData.setAll(results);
        updateProductCount();
    }

    @FXML
    private void handleCategoryFilter() {
        String selectedCategory = categoryFilter.getValue();
        // TODO: call API with category filter
        log.info("Filter by category: {}", selectedCategory);
    }

    @FXML
    private void handleStatusFilter() {
        String selectedStatus = statusFilter.getValue();
        // TODO: call API with status filter
        log.info("Filter by status: {}", selectedStatus);
    }

    @FXML
    private void handleClearFilters() {
        searchField.clear();
        categoryFilter.getSelectionModel().clearSelection();
        statusFilter.getSelectionModel().clearSelection();
        loadProducts();
        statusLabel.setText("Filters cleared");
    }

    /** Unified save handler for add/edit */
    private void onProductSaved(ProductDTO savedProduct) {
        int index = -1;
        for (int i = 0; i < productData.size(); i++) {
            if (productData.get(i).getId().equals(savedProduct.getId())) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            productData.set(index, savedProduct);
        } else {
            productData.add(savedProduct);
        }
        productsTable.refresh();
        log.info("Product {} saved/updated", savedProduct.getCode());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Called by Navigation when revisiting list */
    public void refreshDataIfNeeded() {
        loadProducts();
    }


    // --- Helpers ---
    private void updateProductCount() {
        productCountLabel.setText("(" + productData.size() + " products)");
    }
}

package ashu.sah.SahPustakSadan.Front_end.Controller.Product;

import ashu.sah.SahPustakSadan.Model.Category;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Component
public class ProductDialogController implements Initializable {

    @FXML private Text dialogTitle;
    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> unitCombo;
    @FXML private TextField barcodeField;
    @FXML private TextField costPriceField;
    @FXML private TextField basePriceField;
    @FXML private TextField taxPercentageField;
    @FXML private Label profitMarginLabel;
    @FXML private TextField minStockField;
    @FXML private TextField initialStockField;
    @FXML private CheckBox activeCheckBox;
    @FXML private TextArea descriptionArea;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    @Setter
    private Stage dialogStage;
    private ProductController.ProductRowData product;
    @Setter
    private Consumer<ProductController.ProductRowData> onProductSaved;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupFieldListeners();
        setupValidation();
    }

    private void setupFieldListeners() {
        // Add listeners for price calculation
        costPriceField.textProperty().addListener((obs, oldVal, newVal) -> calculateProfitMargin());
        basePriceField.textProperty().addListener((obs, oldVal, newVal) -> calculateProfitMargin());

        // Numeric field validation
        setupNumericField(costPriceField);
        setupNumericField(basePriceField);
        setupNumericField(taxPercentageField);
        setupIntegerField(minStockField);
        setupIntegerField(initialStockField);
    }

    private void setupNumericField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                field.setText(oldValue);
            }
        });
    }

    private void setupIntegerField(TextField field) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(oldValue);
            }
        });
    }

    private void setupValidation() {
        // Add visual validation indicators
        codeField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateField(codeField, !codeField.getText().trim().isEmpty());
        });

        nameField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateField(nameField, !nameField.getText().trim().isEmpty());
        });

        costPriceField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateField(costPriceField, isValidPrice(costPriceField.getText()));
        });

        basePriceField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) validateField(basePriceField, isValidPrice(basePriceField.getText()));
        });
    }

    private void validateField(TextField field, boolean isValid) {
        if (isValid) {
            field.getStyleClass().remove("field_error");
            field.getStyleClass().add("field_valid");
        } else {
            field.getStyleClass().remove("field_valid");
            field.getStyleClass().add("field_error");
        }
    }

    private boolean isValidPrice(String text) {
        try {
            double price = Double.parseDouble(text);
            return price >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void calculateProfitMargin() {
        try {
            double costPrice = Double.parseDouble(costPriceField.getText().isEmpty() ? "0" : costPriceField.getText());
            double basePrice = Double.parseDouble(basePriceField.getText().isEmpty() ? "0" : basePriceField.getText());

            if (costPrice > 0) {
                double profit = basePrice - costPrice;
                double profitPercentage = (profit / costPrice) * 100;

                Platform.runLater(() -> {
                    profitMarginLabel.setText(String.format("₹%.2f (%.1f%%)", profit, profitPercentage));

                    // Color coding for profit margin
                    if (profitPercentage < 10) {
                        profitMarginLabel.setStyle("-fx-text-fill: #dc3545;"); // Red for low profit
                    } else if (profitPercentage < 25) {
                        profitMarginLabel.setStyle("-fx-text-fill: #ffc107;"); // Yellow for moderate profit
                    } else {
                        profitMarginLabel.setStyle("-fx-text-fill: #28a745;"); // Green for good profit
                    }
                });
            } else {
                Platform.runLater(() -> {
                    profitMarginLabel.setText("₹0.00 (0%)");
                    profitMarginLabel.setStyle("-fx-text-fill: #6c757d;");
                });
            }
        } catch (NumberFormatException e) {
            Platform.runLater(() -> {
                profitMarginLabel.setText("₹0.00 (0%)");
                profitMarginLabel.setStyle("-fx-text-fill: #6c757d;");
            });
        }
    }

    public void setCategories(List<Category> categories) {

        // TODO: Replace with actual category names from database
        categoryCombo.setItems(FXCollections.observableArrayList(
                "Stationery", "Books", "Electronics", "Accessories", "Office Supplies"
        ));
    }

    public void setProduct(ProductController.ProductRowData product) {
        this.product = product;
        this.isEditMode = true;

        dialogTitle.setText("Edit Product");

        // Populate fields with product data
        codeField.setText(product.getCode());
        nameField.setText(product.getName());
        categoryCombo.setValue(product.getCategoryName());
        unitCombo.setValue(product.getUnit());
        costPriceField.setText(String.valueOf(product.getCostPrice()));
        basePriceField.setText(String.valueOf(product.getBasePrice()));
        minStockField.setText(String.valueOf(product.getMinStockLevel()));
        initialStockField.setText(String.valueOf(product.getCurrentStock()));
        activeCheckBox.setSelected("Active".equals(product.getStatus()));

        // Disable code field for edit mode
        codeField.setDisable(true);

        // Calculate initial profit margin
        calculateProfitMargin();
    }

    @FXML
    private void handleSave() {
        if (validateForm()) {
            ProductController.ProductRowData savedProduct = collectFormData();

            if (onProductSaved != null) {
                onProductSaved.accept(savedProduct);
            }

            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean validateForm() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        // Validate required fields
        if (codeField.getText().trim().isEmpty()) {
            validateField(codeField, false);
            errors.append("• Product code is required\n");
            isValid = false;
        }

        if (nameField.getText().trim().isEmpty()) {
            validateField(nameField, false);
            errors.append("• Product name is required\n");
            isValid = false;
        }

        if (categoryCombo.getValue() == null || categoryCombo.getValue().isEmpty()) {
            errors.append("• Category is required\n");
            isValid = false;
        }

        if (unitCombo.getValue() == null || unitCombo.getValue().isEmpty()) {
            errors.append("• Unit is required\n");
            isValid = false;
        }

        // Validate numeric fields
        if (!isValidPrice(costPriceField.getText())) {
            validateField(costPriceField, false);
            errors.append("• Cost price must be a valid number\n");
            isValid = false;
        }

        if (!isValidPrice(basePriceField.getText())) {
            validateField(basePriceField, false);
            errors.append("• Base price must be a valid number\n");
            isValid = false;
        }

        try {
            int minStock = Integer.parseInt(minStockField.getText());
            if (minStock < 0) {
                errors.append("• Minimum stock level cannot be negative\n");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errors.append("• Minimum stock level must be a valid number\n");
            isValid = false;
        }

        try {
            int initialStock = Integer.parseInt(initialStockField.getText());
            if (initialStock < 0) {
                errors.append("• Initial stock cannot be negative\n");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errors.append("• Initial stock must be a valid number\n");
            isValid = false;
        }

        // Validate business rules
        try {
            double costPrice = Double.parseDouble(costPriceField.getText());
            double basePrice = Double.parseDouble(basePriceField.getText());

            if (basePrice < costPrice) {
                errors.append("• Base price should be greater than or equal to cost price\n");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            // Already handled above
        }

        if (!isValid) {
            showValidationErrorAlert(errors.toString());
        }

        return isValid;
    }

    private ProductController.ProductRowData collectFormData() {
        Long id = isEditMode ? product.getId() : null;
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String categoryName = categoryCombo.getValue();
        String unit = unitCombo.getValue();
        Double costPrice = Double.parseDouble(costPriceField.getText());
        Double basePrice = Double.parseDouble(basePriceField.getText());
        Integer minStock = Integer.parseInt(minStockField.getText());
        Integer currentStock = Integer.parseInt(initialStockField.getText());
        String status = activeCheckBox.isSelected() ? "Active" : "Inactive";

        return new ProductController.ProductRowData(
                id, code, name, categoryName, unit, basePrice, costPrice,
                currentStock, minStock, status
        );
    }

    private void showValidationErrorAlert(String errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please fix the following errors:");
        alert.setContentText(errors);
        alert.showAndWait();
    }
}
package ashu.sah.SahPustakSadan.Front_end.Controller.Product;

import ashu.sah.SahPustakSadan.Front_end.Types.ProductDTO;
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

    @Setter private Stage dialogStage;
    private ProductDTO product;
    @Setter private Consumer<ProductDTO> onProductSaved;
    private boolean isEditMode = false;

    private List<Category> categories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupFieldListeners();
        setupValidation();
    }

    // ---------------- Field Listeners ----------------
    private void setupFieldListeners() {
        costPriceField.textProperty().addListener((obs, o, n) -> calculateProfitMargin());
        basePriceField.textProperty().addListener((obs, o, n) -> calculateProfitMargin());

        setupNumericField(costPriceField);
        setupNumericField(basePriceField);
        setupNumericField(taxPercentageField);
        setupIntegerField(minStockField);
        setupIntegerField(initialStockField);
    }

    private void setupNumericField(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                field.setText(oldVal);
            }
        });
    }

    private void setupIntegerField(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                field.setText(oldVal);
            }
        });
    }

    // ---------------- Validation ----------------
    private void setupValidation() {
        codeField.focusedProperty().addListener((obs, o, n) -> {
            if (!n) validateField(codeField, !codeField.getText().trim().isEmpty());
        });
        nameField.focusedProperty().addListener((obs, o, n) -> {
            if (!n) validateField(nameField, !nameField.getText().trim().isEmpty());
        });
        costPriceField.focusedProperty().addListener((obs, o, n) -> {
            if (!n) validateField(costPriceField, isValidPrice(costPriceField.getText()));
        });
        basePriceField.focusedProperty().addListener((obs, o, n) -> {
            if (!n) validateField(basePriceField, isValidPrice(basePriceField.getText()));
        });
    }

    private void validateField(TextField field, boolean isValid) {
        field.getStyleClass().removeAll("field_error", "field_valid");
        field.getStyleClass().add(isValid ? "field_valid" : "field_error");
    }

    private boolean isValidPrice(String text) {
        try {
            return Double.parseDouble(text) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ---------------- Profit Margin ----------------
    private void calculateProfitMargin() {
        try {
            double costPrice = parseDouble(costPriceField.getText());
            double basePrice = parseDouble(basePriceField.getText());

            if (costPrice > 0) {
                double profit = basePrice - costPrice;
                double profitPct = (profit / costPrice) * 100;

                Platform.runLater(() -> {
                    profitMarginLabel.setText(String.format("₹%.2f (%.1f%%)", profit, profitPct));
                    if (profitPct < 10) profitMarginLabel.setStyle("-fx-text-fill: #dc3545;");
                    else if (profitPct < 25) profitMarginLabel.setStyle("-fx-text-fill: #ffc107;");
                    else profitMarginLabel.setStyle("-fx-text-fill: #28a745;");
                });
            } else {
                resetProfitMargin();
            }
        } catch (NumberFormatException e) {
            resetProfitMargin();
        }
    }

    private void resetProfitMargin() {
        Platform.runLater(() -> {
            profitMarginLabel.setText("₹0.00 (0%)");
            profitMarginLabel.setStyle("-fx-text-fill: #6c757d;");
        });
    }

    private double parseDouble(String val) {
        return val == null || val.isBlank() ? 0 : Double.parseDouble(val);
    }

    // ---------------- Categories ----------------
    public void setCategories(List<Category> categories) {
        this.categories = categories;
        categoryCombo.setItems(FXCollections.observableArrayList(
                categories.stream().map(Category::getName).toList()
        ));
    }

    // ---------------- Populate For Edit ----------------
    public void setProduct(ProductDTO product) {
        this.product = product;
        this.isEditMode = true;

        dialogTitle.setText("Edit Product");

        codeField.setText(product.getCode());
        nameField.setText(product.getName());
        categoryCombo.setValue(product.getCategoryName());
        unitCombo.setValue(product.getUnit());
        barcodeField.setText(product.getBarcode());
        descriptionArea.setText(product.getDescription());
        costPriceField.setText(String.valueOf(product.getCostPrice()));
        basePriceField.setText(String.valueOf(product.getBasePrice()));
        minStockField.setText(String.valueOf(product.getMinStockLevel()));
        initialStockField.setText(String.valueOf(product.getCurrentStock()));
        activeCheckBox.setSelected(Boolean.TRUE.equals(product.getIsActive()));

        codeField.setDisable(true); // cannot edit code
        calculateProfitMargin();
    }

    // ---------------- Save / Cancel ----------------
    @FXML
    private void handleSave() {
        if (validateForm()) {
            ProductDTO savedProduct = collectFormData();
            if (onProductSaved != null) onProductSaved.accept(savedProduct);
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    // ---------------- Form Validation ----------------
    private boolean validateForm() {
        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

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
        if (!isValidPrice(costPriceField.getText())) {
            validateField(costPriceField, false);
            errors.append("• Cost price must be valid\n");
            isValid = false;
        }
        if (!isValidPrice(basePriceField.getText())) {
            validateField(basePriceField, false);
            errors.append("• Base price must be valid\n");
            isValid = false;
        }

        try {
            int minStock = Integer.parseInt(minStockField.getText());
            if (minStock < 0) {
                errors.append("• Minimum stock cannot be negative\n");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errors.append("• Minimum stock must be valid\n");
            isValid = false;
        }

        try {
            int initialStock = Integer.parseInt(initialStockField.getText());
            if (initialStock < 0) {
                errors.append("• Initial stock cannot be negative\n");
                isValid = false;
            }
        } catch (NumberFormatException e) {
            errors.append("• Initial stock must be valid\n");
            isValid = false;
        }

        try {
            double cost = parseDouble(costPriceField.getText());
            double base = parseDouble(basePriceField.getText());
            if (base < cost) {
                errors.append("• Base price must be ≥ cost price\n");
                isValid = false;
            }
        } catch (NumberFormatException ignored) {}

        if (!isValid) {
            showValidationErrorAlert(errors.toString());
        }
        return isValid;
    }

    private void showValidationErrorAlert(String errors) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please fix the following:");
        alert.setContentText(errors);
        alert.showAndWait();
    }

    // ---------------- Collect Form Data ----------------
    private ProductDTO collectFormData() {
        ProductDTO dto = new ProductDTO();
        if (isEditMode) dto.setId(product.getId());
        dto.setCode(codeField.getText().trim());
        dto.setName(nameField.getText().trim());
        dto.setCategoryName(categoryCombo.getValue());
        dto.setUnit(unitCombo.getValue());
        dto.setBasePrice(Double.parseDouble(basePriceField.getText()));
        dto.setCostPrice(Double.parseDouble(costPriceField.getText()));
        dto.setCurrentStock(Integer.parseInt(initialStockField.getText()));
        dto.setMinStockLevel(Integer.parseInt(minStockField.getText()));
        dto.setIsActive(activeCheckBox.isSelected());
        dto.setDescription(descriptionArea.getText());
        dto.setBarcode(barcodeField.getText());
        return dto;
    }

}

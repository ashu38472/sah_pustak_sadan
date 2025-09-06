package ashu.sah.SahPustakSadan.Front_end.Controller.Product;

import ashu.sah.SahPustakSadan.APIController.product.ProductAPIController;
import ashu.sah.SahPustakSadan.Front_end.Types.ProductDTO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Slf4j
@Component
public class ProductFormController implements Initializable {

    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField barcodeField;
    @FXML private TextField unitField;
    @FXML private TextField basePriceField;
    @FXML private TextField costPriceField;
    @FXML private CheckBox activeCheckBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    @Autowired private ProductAPIController productAPIController;

    private ProductDTO editingProduct;
    private Consumer<ProductDTO> onSaveCallback; // ✅ callback to parent

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        saveButton.setOnAction(event -> handleSave());
        cancelButton.setOnAction(event -> handleCancel());
    }

    /** Called when editing an existing product */
    public void setProduct(ProductDTO product) {
        this.editingProduct = product;

        codeField.setText(product.getCode());
        nameField.setText(product.getName());
        descriptionField.setText(product.getDescription());
        barcodeField.setText(product.getBarcode());
        unitField.setText(product.getUnit());
        basePriceField.setText(product.getBasePrice() != null ? product.getBasePrice().toString() : "");
        costPriceField.setText(product.getCostPrice() != null ? product.getCostPrice().toString() : "");
        activeCheckBox.setSelected(Boolean.TRUE.equals(product.getIsActive()));
    }

    /** Parent can set a callback to handle product save */
    public void setOnSaveCallback(Consumer<ProductDTO> callback) {
        this.onSaveCallback = callback;
    }

    private void handleSave() {
        if (!validateFields()) {
            return;
        }

        try {
            ProductDTO productDTO = new ProductDTO();
            if (editingProduct != null) {
                productDTO.setId(editingProduct.getId()); // preserve ID when editing
            }
            productDTO.setCode(codeField.getText().trim());
            productDTO.setName(nameField.getText().trim());
            productDTO.setDescription(descriptionField.getText().trim());
            productDTO.setBarcode(barcodeField.getText().trim());
            productDTO.setUnit(unitField.getText().trim());
            productDTO.setBasePrice(parseDouble(basePriceField.getText()));
            productDTO.setCostPrice(parseDouble(costPriceField.getText()));
            productDTO.setIsActive(activeCheckBox.isSelected());

            boolean success = (editingProduct == null)
                    ? productAPIController.createProduct(productDTO)
                    : productAPIController.updateProduct(productDTO.getId(), productDTO);

            if (success) {
                log.info("Product {} successfully {}", productDTO.getCode(), editingProduct == null ? "created" : "updated");

                // ✅ Let parent decide what to do after save
                if (onSaveCallback != null) {
                    onSaveCallback.accept(productDTO);
                }
            } else {
                showErrorAlert("Save Failed", "Could not save product. Please try again.");
            }
        } catch (Exception e) {
            log.error("Error saving product", e);
            showErrorAlert("Error", "An unexpected error occurred while saving the product.");
        }
    }

    private void handleCancel() {
        log.info("Product form cancelled");
        // ✅ No navigation here — parent can handle switching views
    }

    private boolean validateFields() {
        if (codeField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "Code is required.");
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            showErrorAlert("Validation Error", "Name is required.");
            return false;
        }
        if (!isDouble(basePriceField.getText())) {
            showErrorAlert("Validation Error", "Base price must be a valid number.");
            return false;
        }
        if (!isDouble(costPriceField.getText())) {
            showErrorAlert("Validation Error", "Cost price must be a valid number.");
            return false;
        }
        return true;
    }

    private boolean isDouble(String value) {
        if (value == null || value.trim().isEmpty()) return false;
        try {
            Double.parseDouble(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private Double parseDouble(String value) {
        return (value == null || value.trim().isEmpty()) ? null : Double.parseDouble(value.trim());
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

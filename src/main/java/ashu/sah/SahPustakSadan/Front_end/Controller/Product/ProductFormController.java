package ashu.sah.SahPustakSadan.Front_end.Controller.Product;

import ashu.sah.SahPustakSadan.APIController.product.ProductAPIController;
import ashu.sah.SahPustakSadan.Front_end.Types.ProductDTO;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

@Slf4j
@Component
public class ProductFormController implements Initializable {

    @FXML public Label taxPercentageLable;
    // Header Elements
    @FXML private BorderPane scene;
    @FXML private Button backButton;
    @FXML private Text formTitle;
    @FXML private Label statusIndicator;
    @FXML private Label breadcrumbCurrent;

    // Basic Information
    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private ComboBox<String> unitCombo;
    @FXML private TextField barcodeField;
    @FXML private CheckBox activeCheckBox;

    // Validation Labels
    @FXML private Label codeValidation;
    @FXML private Label nameValidation;
    @FXML private Label categoryValidation;
    @FXML private Label unitValidation;

    // Pricing Information
    @FXML private TextField costPriceField;
    @FXML private TextField basePriceField;
    @FXML private TextField taxPercentageField;
    @FXML private Label profitMarginLabel;
    @FXML private Label profitIndicator;

    // Pricing Validation
    @FXML private Label costValidation;
    @FXML private Label baseValidation;

    // Inventory Information
    @FXML private TextField minStockField;
    @FXML private TextField initialStockField;
    @FXML private Label minStockValidation;
    @FXML private Label stockValidation;

    // Description
    @FXML private TextArea descriptionArea;
    @FXML private Label characterCount;

    // Action Elements
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private HBox progressContainer;
    @FXML private ProgressIndicator saveProgress;
    @FXML private Label progressLabel;
    @FXML private Label requiredFieldsLabel;

    @FXML private StackPane navIcon;
    @FXML private StackPane infoIcon;
    @FXML private StackPane pricingIcon;
    @FXML private StackPane inventoryIcon;
    @FXML private StackPane descriptionIcon;

    @Autowired private ProductAPIController productAPIController;

    private ProductDTO editingProduct;
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private final DecimalFormat percentFormat = new DecimalFormat("#0.0");

    @Setter
    private Consumer<ProductDTO> onSaveCallback;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        log.info("Initializing ProductFormController");

        setupUI();
        setupIcons();
        setupEventHandlers();
        setupValidation();
        setupAnimations();
    }

    private void setupUI() {
        // Initialize ComboBoxes
        unitCombo.setItems(FXCollections.observableArrayList(
                "Piece", "Kg", "Gram", "Liter", "ML", "Meter", "CM", "Box", "Pack", "Dozen"
        ));

        categoryCombo.setItems(FXCollections.observableArrayList(
                "Electronics", "Clothing", "Food & Beverages", "Books", "Home & Garden",
                "Sports", "Toys", "Beauty", "Automotive", "Office Supplies"
        ));

        taxPercentageLable.setText("%");

        // Setup initial UI state
        statusIndicator.setText("Draft");
        statusIndicator.getStyleClass().add("status-draft");

        progressContainer.setVisible(false);
        clearValidationMessages();

        // Setup character counter for description
        descriptionArea.textProperty().addListener((obs, oldText, newText) -> {
            int length = newText != null ? newText.length() : 0;
            characterCount.setText(length + " / 500");

            if (length > 500) {
                characterCount.getStyleClass().add("error-text");
            } else {
                characterCount.getStyleClass().remove("error-text");
            }
        });
    }

    private void setupIcons() {
        // Navigation bar icon
        FontIcon nav = new FontIcon(FontAwesomeSolid.BOOK);
        nav.setIconSize(20);
        navIcon.getChildren().add(nav);

        // Info card icon
        FontIcon info = new FontIcon(FontAwesomeSolid.INFO_CIRCLE);
        info.setIconSize(24);
        infoIcon.getChildren().add(info);

        // Pricing card icon
        FontIcon pricing = new FontIcon(FontAwesomeSolid.MONEY_BILL);
        pricing.setIconSize(24);
        pricingIcon.getChildren().add(pricing);

        // Inventory card icon
        FontIcon inventory = new FontIcon(FontAwesomeSolid.CUBES);
        inventory.setIconSize(24);
        inventoryIcon.getChildren().add(inventory);

        // Description card icon
        FontIcon description = new FontIcon(FontAwesomeSolid.FILE_ALT);
        description.setIconSize(24);
        descriptionIcon.getChildren().add(description);
    }

    private void setupEventHandlers() {
        // Navigation handlers
        backButton.setOnAction(e -> handleBack());
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());

        // Auto-calculation handlers
        costPriceField.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePriceField(costPriceField, costValidation);
            updateProfitMargin();
        });

        basePriceField.textProperty().addListener((obs, oldVal, newVal) -> {
            validatePriceField(basePriceField, baseValidation);
            updateProfitMargin();
        });

        taxPercentageField.textProperty().addListener((obs, oldVal, newVal) -> {
            validateTaxField();
            updateProfitMargin();
        });

        // Real-time validation
        codeField.textProperty().addListener((obs, oldVal, newVal) ->
                validateRequiredField(codeField, codeValidation, "Product code is required"));

        nameField.textProperty().addListener((obs, oldVal, newVal) ->
                validateRequiredField(nameField, nameValidation, "Product name is required"));

        unitCombo.valueProperty().addListener((obs, oldVal, newVal) ->
                validateComboField(unitCombo, unitValidation, "Unit selection is required"));

        categoryCombo.valueProperty().addListener((obs, oldVal, newVal) ->
                validateComboField(categoryCombo, categoryValidation, "Category selection is required"));

        // Stock validation
        minStockField.textProperty().addListener((obs, oldVal, newVal) ->
                validateIntegerField(minStockField, minStockValidation, "Minimum stock must be a valid number"));

        initialStockField.textProperty().addListener((obs, oldVal, newVal) ->
                validateIntegerField(initialStockField, stockValidation, "Initial stock must be a valid number"));

        // Format number fields on focus lost
        setupNumberFieldFormatting();
    }

    private void setupNumberFieldFormatting() {
        costPriceField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) formatPriceField(costPriceField);
        });

        basePriceField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) formatPriceField(basePriceField);
        });

        taxPercentageField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) formatPercentageField(taxPercentageField);
        });
    }

    private void setupValidation() {
        // Setup validation styling
        setupFieldValidationStyling(codeField);
        setupFieldValidationStyling(nameField);
        setupFieldValidationStyling(costPriceField);
        setupFieldValidationStyling(basePriceField);
        setupFieldValidationStyling(minStockField);
        setupFieldValidationStyling(initialStockField);
    }

    private void setupFieldValidationStyling(TextField field) {
        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (hasValidationError(field)) {
                if (!field.getStyleClass().contains("error-field")) {
                    field.getStyleClass().add("error-field");
                }
            } else {
                field.getStyleClass().remove("error-field");
            }
        });
    }

    private boolean hasValidationError(TextField field) {
        // Check if the field has any validation errors based on associated validation label
        if (field == codeField && !codeValidation.getText().isEmpty()) return true;
        if (field == nameField && !nameValidation.getText().isEmpty()) return true;
        if (field == costPriceField && !costValidation.getText().isEmpty()) return true;
        if (field == basePriceField && !baseValidation.getText().isEmpty()) return true;
        if (field == minStockField && !minStockValidation.getText().isEmpty()) return true;
        if (field == initialStockField && !stockValidation.getText().isEmpty()) return true;
        return false;
    }

    private void setupAnimations() {
        // Add subtle entrance animations
        Timeline fadeIn = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(formTitle.opacityProperty(), 0)),
                new KeyFrame(Duration.millis(300), new KeyValue(formTitle.opacityProperty(), 1))
        );
        fadeIn.play();
    }

    public void setProduct(ProductDTO product) {
        this.editingProduct = product;

        if (product.getId() != null) {
            formTitle.setText("Edit Product");
            breadcrumbCurrent.setText("Edit Product");
            statusIndicator.setText("Editing");
            statusIndicator.getStyleClass().removeAll("status-draft", "status-active");
            statusIndicator.getStyleClass().add("status-editing");
        }

        // Populate form fields
        populateFormFields(product);
        updateProfitMargin();

        log.info("Product form loaded for {}", product.getId() != null ? "editing" : "creation");
    }

    private void populateFormFields(ProductDTO product) {
        Platform.runLater(() -> {
            codeField.setText(product.getCode());
            nameField.setText(product.getName());
            descriptionArea.setText(product.getDescription());
            barcodeField.setText(product.getBarcode());
            unitCombo.setValue(product.getUnit());
            categoryCombo.setValue(product.getCategoryName());

            if (product.getBasePrice() != null) {
                basePriceField.setText(product.getBasePrice().toString());
            }
            if (product.getCostPrice() != null) {
                costPriceField.setText(product.getCostPrice().toString());
            }
            if (product.getTaxPercentage() != null) {
                taxPercentageField.setText(product.getTaxPercentage().toString());
            }
            if (product.getMinStockLevel() != null) {
                minStockField.setText(product.getMinStockLevel().toString());
            }
            if (product.getCurrentStock() != null) {
                initialStockField.setText(product.getCurrentStock().toString());
            }

            activeCheckBox.setSelected(Boolean.TRUE.equals(product.getIsActive()));
        });
    }

    @FXML
    private void handleSave() {
        log.info("Save button clicked");

        if (!validateForm() || !validateBusinessRules()) {
            showValidationSummary();
            return;
        }

        // Show progress
        setUIState(true);

        // Create background task for saving
        Task<Boolean> saveTask = createSaveTask();

        saveTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                setUIState(false);
                if (saveTask.getValue()) {
                    showSuccessMessage();
                    if (onSaveCallback != null) {
                        onSaveCallback.accept(createProductDTO());
                    }
                } else {
                    showErrorMessage("Failed to save product. Please try again.");
                }
            });
        });

        saveTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                setUIState(false);
                log.error("Save task failed", saveTask.getException());
                showErrorMessage("An unexpected error occurred while saving.");
            });
        });

        new Thread(saveTask).start();
    }

    private Task<Boolean> createSaveTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                updateProgress(-1, -1); // Indeterminate progress
                updateMessage("Saving product...");

                try {
                    ProductDTO productDTO = createProductDTO();

                    boolean success = (editingProduct == null)
                            ? productAPIController.createProduct(productDTO)
                            : productAPIController.updateProduct(productDTO.getId(), productDTO);

                    if (success) {
                        updateMessage("Product saved successfully!");
                        Thread.sleep(500); // Brief pause to show success message
                    }

                    return success;
                } catch (Exception e) {
                    log.error("Error in save task", e);
                    throw e;
                }
            }
        };
    }

    private ProductDTO createProductDTO() {
        ProductDTO productDTO = new ProductDTO();

        if (editingProduct != null) {
            productDTO.setId(editingProduct.getId());
        }

        productDTO.setCode(codeField.getText().trim());
        productDTO.setName(nameField.getText().trim());
        productDTO.setDescription(descriptionArea.getText().trim());
        productDTO.setBarcode(barcodeField.getText().trim());
        productDTO.setUnit(unitCombo.getValue());
        productDTO.setCategoryName(categoryCombo.getValue());
        productDTO.setBasePrice(parseDouble(basePriceField.getText()));
        productDTO.setCostPrice(parseDouble(costPriceField.getText()));
        productDTO.setTaxPercentage(parseDouble(taxPercentageField.getText()));
        productDTO.setMinStockLevel(parseInteger(minStockField.getText()));
        productDTO.setCurrentStock(parseInteger(initialStockField.getText()));
        productDTO.setIsActive(activeCheckBox.isSelected());

        return productDTO;
    }

    private void setUIState(boolean loading) {
        saveButton.setDisable(loading);
        cancelButton.setDisable(loading);
        progressContainer.setVisible(loading);

        if (loading) {
            saveProgress.setVisible(true);
            progressLabel.setVisible(true);
            progressLabel.setText("Saving...");
        } else {
            saveProgress.setVisible(false);
            progressLabel.setVisible(false);
        }
    }

    @FXML
    private void handleCancel() {
        log.info("Cancel button clicked");

        if (hasUnsavedChanges()) {
            showUnsavedChangesDialog();
        } else {
            if (onSaveCallback != null) {
                onSaveCallback.accept(null);
            }
        }
    }

    @FXML
    private void handleBack() {
        log.info("Back button clicked");
        handleCancel(); // Same logic as cancel
    }

    private void updateProfitMargin() {
        try {
            Double cost = parseDouble(costPriceField.getText());
            Double base = parseDouble(basePriceField.getText());

            if (cost != null && base != null && cost > 0 && base > 0) {
                double profit = base - cost;
                double marginPercent = (profit / cost) * 100;

                String profitText = "₹" + currencyFormat.format(profit) +
                        " (" + percentFormat.format(marginPercent) + "%)";
                profitMarginLabel.setText(profitText);

                // Update profit indicator
                if (marginPercent > 0) {
                    profitIndicator.setText("+" + percentFormat.format(marginPercent) + "%");
                    profitIndicator.getStyleClass().removeAll("profit-negative", "profit-zero");
                    profitIndicator.getStyleClass().add("profit-positive");
                } else if (marginPercent < 0) {
                    profitIndicator.setText(percentFormat.format(marginPercent) + "%");
                    profitIndicator.getStyleClass().removeAll("profit-positive", "profit-zero");
                    profitIndicator.getStyleClass().add("profit-negative");
                } else {
                    profitIndicator.setText("0%");
                    profitIndicator.getStyleClass().removeAll("profit-positive", "profit-negative");
                    profitIndicator.getStyleClass().add("profit-zero");
                }
            } else {
                profitMarginLabel.setText("₹0.00 (0%)");
                profitIndicator.setText("0%");
                profitIndicator.getStyleClass().removeAll("profit-positive", "profit-negative");
                profitIndicator.getStyleClass().add("profit-zero");
            }
        } catch (Exception e) {
            profitMarginLabel.setText("₹0.00 (0%)");
            profitIndicator.setText("0%");
        }
    }

    // Validation Methods
    private boolean validateForm() {
        boolean isValid = true;

        clearValidationMessages();

        isValid &= validateRequiredField(codeField, codeValidation, "Product code is required");
        isValid &= validateRequiredField(nameField, nameValidation, "Product name is required");
        isValid &= validateComboField(unitCombo, unitValidation, "Unit selection is required");
        isValid &= validatePriceField(costPriceField, costValidation);
        isValid &= validatePriceField(basePriceField, baseValidation);
        isValid &= validateIntegerField(minStockField, minStockValidation, "Minimum stock must be a valid number");

        return isValid;
    }

    private boolean validateRequiredField(TextField field, Label validationLabel, String message) {
        if (field.getText() == null || field.getText().trim().isEmpty()) {
            showFieldError(validationLabel, message);
            return false;
        }
        clearFieldError(validationLabel);
        return true;
    }

    private boolean validateComboField(ComboBox<String> combo, Label validationLabel, String message) {
        if (combo.getValue() == null || combo.getValue().trim().isEmpty()) {
            showFieldError(validationLabel, message);
            return false;
        }
        clearFieldError(validationLabel);
        return true;
    }

    private boolean validatePriceField(TextField field, Label validationLabel) {
        String text = field.getText();
        if (text == null || text.trim().isEmpty()) {
            showFieldError(validationLabel, "Price is required");
            return false;
        }

        try {
            double value = Double.parseDouble(text.trim());
            if (value < 0) {
                showFieldError(validationLabel, "Price cannot be negative");
                return false;
            }
        } catch (NumberFormatException e) {
            showFieldError(validationLabel, "Please enter a valid price");
            return false;
        }

        clearFieldError(validationLabel);
        return true;
    }

    private boolean validateIntegerField(TextField field, Label validationLabel, String message) {
        String text = field.getText();
        if (text != null && !text.trim().isEmpty()) {
            try {
                int value = Integer.parseInt(text.trim());
                if (value < 0) {
                    showFieldError(validationLabel, "Value cannot be negative");
                    return false;
                }
            } catch (NumberFormatException e) {
                showFieldError(validationLabel, message);
                return false;
            }
        }
        clearFieldError(validationLabel);
        return true;
    }

    private void validateTaxField() {
        String text = taxPercentageField.getText();
        if (text != null && !text.trim().isEmpty()) {
            try {
                double value = Double.parseDouble(text.trim());
                if (value < 0 || value > 100) {
                    // Could add validation for tax range if needed
                }
            } catch (NumberFormatException e) {
                // Handle invalid tax percentage
            }
        }
    }

    private void showFieldError(Label validationLabel, String message) {
        validationLabel.setText(message);
        validationLabel.setVisible(true);
    }

    private void clearFieldError(Label validationLabel) {
        validationLabel.setText("");
        validationLabel.setVisible(false);
    }

    private void clearValidationMessages() {
        Label[] validationLabels = {
                codeValidation, nameValidation, categoryValidation, unitValidation,
                costValidation, baseValidation, minStockValidation, stockValidation
        };

        for (Label label : validationLabels) {
            if (label != null) {
                clearFieldError(label);
            }
        }
    }

    // Formatting Methods
    private void formatPriceField(TextField field) {
        try {
            Double value = parseDouble(field.getText());
            if (value != null) {
                field.setText(currencyFormat.format(value));
            }
        } catch (Exception e) {
            // Keep original text if formatting fails
        }
    }

    private void formatPercentageField(TextField field) {
        try {
            Double value = parseDouble(field.getText());
            if (value != null) {
                field.setText(percentFormat.format(value));
            }
        } catch (Exception e) {
            // Keep original text if formatting fails
        }
    }

    // Utility Methods
    private Double parseDouble(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            // Remove currency symbols and commas for parsing
            String cleanValue = value.trim().replaceAll("[^0-9.]", "");
            return Double.parseDouble(cleanValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseInteger(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean hasUnsavedChanges() {
        if (editingProduct == null) {
            return !codeField.getText().trim().isEmpty() ||
                    !nameField.getText().trim().isEmpty() ||
                    !descriptionArea.getText().trim().isEmpty();
        }

        return !codeField.getText().trim().equals(editingProduct.getCode()) ||
                !nameField.getText().trim().equals(editingProduct.getName()) ||
                !descriptionArea.getText().trim().equals(editingProduct.getDescription()) ||
                !barcodeField.getText().trim().equals(editingProduct.getBarcode()) ||
                !unitCombo.getValue().equals(editingProduct.getUnit()) ||
                !categoryCombo.getValue().equals(editingProduct.getCategoryName()) ||
                !Objects.equals(parseDouble(costPriceField.getText()), editingProduct.getCostPrice()) ||
                !Objects.equals(parseDouble(basePriceField.getText()), editingProduct.getBasePrice()) ||
                !Objects.equals(parseDouble(taxPercentageField.getText()), editingProduct.getTaxPercentage()) ||
                !Objects.equals(parseInteger(minStockField.getText()), editingProduct.getMinStockLevel()) ||
                !Objects.equals(parseInteger(initialStockField.getText()), editingProduct.getCurrentStock()) ||
                activeCheckBox.isSelected() != Boolean.TRUE.equals(editingProduct.getIsActive());
    }


    // Dialog Methods
    private void showValidationSummary() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Please fix the following issues:");
        alert.setContentText("Check the highlighted fields and correct any errors before saving.");
        alert.showAndWait();
    }

    private void showUnsavedChangesDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Unsaved Changes");
        alert.setHeaderText("You have unsaved changes.");
        alert.setContentText("Do you want to discard your changes?");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                if (onSaveCallback != null) {
                    onSaveCallback.accept(null);
                }
            }
        });
    }

    private void showSuccessMessage() {
        // Create a subtle success notification
        statusIndicator.setText("Saved");
        statusIndicator.getStyleClass().removeAll("status-draft", "status-editing");
        statusIndicator.getStyleClass().add("status-saved");

        // Auto-hide success message after 3 seconds
        Timeline hideSuccess = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> {
                    statusIndicator.setText(editingProduct != null ? "Active" : "Draft");
                    statusIndicator.getStyleClass().remove("status-saved");
                    statusIndicator.getStyleClass().add(editingProduct != null ? "status-active" : "status-draft");
                })
        );
        hideSuccess.play();

        log.info("Product {} successfully {}",
                codeField.getText(),
                editingProduct == null ? "created" : "updated");
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Save Error");
        alert.setHeaderText("Failed to save product");
        alert.setContentText(message);

        // Style the alert dialog
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/styles/modern-alerts.css").toExternalForm()
        );

        alert.showAndWait();
        log.error("Save error: {}", message);
    }

    // Public methods for external interaction
    public void focusFirstField() {
        Platform.runLater(() -> codeField.requestFocus());
    }

    public void resetForm() {
        Platform.runLater(() -> {
            // Clear all fields
            codeField.clear();
            nameField.clear();
            descriptionArea.clear();
            barcodeField.clear();
            unitCombo.setValue(null);
            categoryCombo.setValue(null);
            costPriceField.clear();
            basePriceField.clear();
            taxPercentageField.setText("0.00");
            minStockField.setText("0");
            initialStockField.setText("0");
            activeCheckBox.setSelected(true);

            // Clear validations
            clearValidationMessages();

            // Reset UI state
            formTitle.setText("Add New Product");
            breadcrumbCurrent.setText("Add Product");
            statusIndicator.setText("Draft");
            statusIndicator.getStyleClass().removeAll("status-editing", "status-saved", "status-active");
            statusIndicator.getStyleClass().add("status-draft");

            updateProfitMargin();
            editingProduct = null;

            log.info("Form reset to initial state");
        });
    }

    public boolean isDirty() {
        return hasUnsavedChanges();
    }

    // Animation helper methods
    private void animateFieldError(TextField field) {
        // Subtle shake animation for validation errors
        Timeline shake = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(field.translateXProperty(), 0)),
                new KeyFrame(Duration.millis(50), new KeyValue(field.translateXProperty(), -3)),
                new KeyFrame(Duration.millis(100), new KeyValue(field.translateXProperty(), 3)),
                new KeyFrame(Duration.millis(150), new KeyValue(field.translateXProperty(), -3)),
                new KeyFrame(Duration.millis(200), new KeyValue(field.translateXProperty(), 0))
        );
        shake.play();
    }

    private void animateSuccessSave() {
        // Pulse animation for successful save
        ScaleTransition pulse = new ScaleTransition(Duration.millis(150), saveButton);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.play();
    }

    // Accessibility methods
    public void setAccessibilityMode(boolean enabled) {
        if (enabled) {
            // Enhance for screen readers and keyboard navigation
            codeField.setAccessibleText("Product code input field, required");
            nameField.setAccessibleText("Product name input field, required");
            costPriceField.setAccessibleText("Cost price input field, required, enter amount in rupees");
            basePriceField.setAccessibleText("Selling price input field, required, enter amount in rupees");

            // Add more accessible descriptions
            unitCombo.setAccessibleText("Unit of measurement selection, required");
            categoryCombo.setAccessibleText("Product category selection");
            activeCheckBox.setAccessibleText("Product active status checkbox");

            log.info("Accessibility mode enabled");
        }
    }

    // Data validation methods for complex business rules
    private boolean validateBusinessRules() {
        boolean isValid = true;

        // Business rule: Selling price should be higher than cost price
        Double cost = parseDouble(costPriceField.getText());
        Double selling = parseDouble(basePriceField.getText());

        if (cost != null && selling != null && selling <= cost) {
            showFieldError(baseValidation, "Selling price should be higher than cost price");
            isValid = false;
        }

        // Business rule: Product code should be unique (would need API call to verify)
        String code = codeField.getText().trim();
        if (!code.isEmpty() && code.length() < 3) {
            showFieldError(codeValidation, "Product code should be at least 3 characters long");
            isValid = false;
        }

        // Business rule: Minimum stock should be reasonable
        Integer minStock = parseInteger(minStockField.getText());
        if (minStock != null && minStock > 10000) {
            showFieldError(minStockValidation, "Minimum stock level seems unusually high");
            isValid = false;
        }

        return isValid;
    }

    // Auto-save functionality (optional)
    private Timeline autoSaveTimeline;

    public void enableAutoSave(boolean enabled) {
        if (enabled && autoSaveTimeline == null) {
            autoSaveTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(30), e -> autoSaveDraft())
            );
            autoSaveTimeline.setCycleCount(Timeline.INDEFINITE);
            autoSaveTimeline.play();
            log.info("Auto-save enabled (every 30 seconds)");
        } else if (!enabled && autoSaveTimeline != null) {
            autoSaveTimeline.stop();
            autoSaveTimeline = null;
            log.info("Auto-save disabled");
        }
    }

    private void autoSaveDraft() {
        if (hasUnsavedChanges() && validateForm()) {
            // Save as draft - implementation would depend on your backend API
            log.debug("Auto-saving draft...");
            // This would typically save to a drafts table or local storage
        }
    }

    // Cleanup method
    public void cleanup() {
        if (autoSaveTimeline != null) {
            autoSaveTimeline.stop();
        }
        log.info("ProductFormController cleanup completed");
    }
}
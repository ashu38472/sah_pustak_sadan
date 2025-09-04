package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.Service.UserSession;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

@Component
public class SidebarController {

    @Autowired
    private UserSession userSession;

    private VBox sidebarContainer;
    private VBox buttonsContainer;
    private final Map<String, Button> sidebarButtons = new HashMap<>();
    private String activeButtonId = "dashboard";
    private Consumer<String> navigationHandler;

    // Button configuration
    private final List<ButtonConfig> buttonConfigs = Arrays.asList(
            new ButtonConfig("dashboard", "Dashboard", "fas-home", true),
            new ButtonConfig("product", "Product", "fas-clipboard-list", true),
            new ButtonConfig("invoice", "Invoice", "fas-file-invoice",
                    () -> userSession.canAccessInvoice()),
            new ButtonConfig("priceCalc", "Price Calculator", "fas-calculator",
                    () -> userSession.canAccessPriceCalculator()),
            new ButtonConfig("profile", "Profile", "far-user-circle",
                    () -> userSession.canAccessProfile()),
            new ButtonConfig("logout", "Logout", "fas-sign-out-alt", true)
    );

    private static class ButtonConfig {
        final String id;
        final String text;
        final String icon;
        final java.util.function.Supplier<Boolean> visibilityCheck;

        ButtonConfig(String id, String text, String icon, boolean alwaysVisible) {
            this(id, text, icon, () -> alwaysVisible);
        }

        ButtonConfig(String id, String text, String icon,
                     java.util.function.Supplier<Boolean> visibilityCheck) {
            this.id = id;
            this.text = text;
            this.icon = icon;
            this.visibilityCheck = visibilityCheck;
        }
    }

    public VBox createSidebar(BorderPane rootPane, Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;

        sidebarContainer = new VBox();
        sidebarContainer.getStyleClass().add("sidebar_container");

        createLogoSection();
        createButtonsSection();
        createReportBugSection();

        return sidebarContainer;
    }

    private void createLogoSection() {
        // Logo container in horizontal layout
        HBox logoContainer = new HBox();
        logoContainer.getStyleClass().add("sidebar_logo_container");
        logoContainer.setSpacing(10);
        logoContainer.setPrefHeight(80);
        logoContainer.setMaxHeight(80);
        logoContainer.setMinHeight(80);

        // Store icon
        FontIcon storeIcon = new FontIcon("fas-store-alt");
        storeIcon.setIconSize(30);

        // App name
        Text appNameLabel = new Text(loadAppName());
        appNameLabel.getStyleClass().add("sidebar_logo_text");

        logoContainer.getChildren().addAll(storeIcon, appNameLabel);
        sidebarContainer.getChildren().add(logoContainer);
    }

    private void createButtonsSection() {
        buttonsContainer = new VBox();
        buttonsContainer.getStyleClass().add("sidebar_buttons_container");
        buttonsContainer.setSpacing(8);

        // Add top spacing
        Region topSpacer = new Region();
        topSpacer.setPrefHeight(20);
        buttonsContainer.getChildren().add(topSpacer);

        // Create buttons based on permissions
        createSidebarButtons();

        // Add flexible space to push report bug section to bottom
        Region flexibleSpace = new Region();
        VBox.setVgrow(flexibleSpace, Priority.ALWAYS);
        buttonsContainer.getChildren().add(flexibleSpace);

        VBox.setVgrow(buttonsContainer, Priority.ALWAYS);
        sidebarContainer.getChildren().add(buttonsContainer);
    }

    private void createSidebarButtons() {
        // Clear existing buttons but keep spacers
        buttonsContainer.getChildren().removeIf(node -> node instanceof Button);
        sidebarButtons.clear();

        for (ButtonConfig config : buttonConfigs) {
            if (config.visibilityCheck.get()) {
                Button button = createSidebarButton(config);
                sidebarButtons.put(config.id, button);

                // Insert button before the flexible space (last element)
                int insertIndex = buttonsContainer.getChildren().size() - 1;
                buttonsContainer.getChildren().add(insertIndex, button);
            }
        }

        setActiveButton(activeButtonId);
    }

    private Button createSidebarButton(ButtonConfig config) {
        Button button = new Button();
        button.setId(config.id + "Btn");

        // Create horizontal layout for icon and text
        HBox buttonContent = new HBox();
        buttonContent.setSpacing(12);
        buttonContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        // Create icon
        FontIcon icon = new FontIcon(config.icon);
        icon.setIconSize(20);
        icon.getStyleClass().add("sidebar_btn_icon");

        // Create text
        Text text = new Text(config.text);
        text.getStyleClass().add("sidebar_btn_text");

        buttonContent.getChildren().addAll(icon, text);
        button.setGraphic(buttonContent);

        // Styling
        button.getStyleClass().add("sidebar_btn");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(45);

        // Set action
        button.setOnAction(e -> {
            setActiveButton(config.id);
            if (navigationHandler != null) {
                navigationHandler.accept(config.id);
            }
        });

        return button;
    }

    private void createReportBugSection() {
        VBox reportBugContainer = new VBox();
        reportBugContainer.getStyleClass().add("report_bug_container");
        reportBugContainer.setSpacing(8);

        Text reportText = new Text("Report Bugs?");
        reportText.getStyleClass().add("report_bug_text");

        Label reportLabel = new Label("Use this to report any error or suggestion");
        reportLabel.getStyleClass().add("report_bug_label");
        reportLabel.setWrapText(true);

        Button reportBtn = new Button();
        HBox reportBtnContent = new HBox();
        reportBtnContent.setSpacing(12);
        reportBtnContent.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        FontIcon bugIcon = new FontIcon("fas-bug");
        bugIcon.setIconSize(20);
        bugIcon.getStyleClass().add("sidebar_btn_icon");

        Text reportBtnText = new Text("Report");
        reportBtnText.getStyleClass().add("sidebar_btn_text");

        reportBtnContent.getChildren().addAll(bugIcon, reportBtnText);
        reportBtn.setGraphic(reportBtnContent);
        reportBtn.getStyleClass().add("sidebar_btn");
        reportBtn.setMaxWidth(Double.MAX_VALUE);
        reportBtn.setOnAction(e -> {
            if (navigationHandler != null) {
                navigationHandler.accept("reportBug");
            }
        });

        reportBugContainer.getChildren().addAll(reportText, reportLabel, reportBtn);
        sidebarContainer.getChildren().add(reportBugContainer);
    }

    public void setActiveButton(String buttonId) {
        this.activeButtonId = buttonId;

        // Remove active class from all buttons
        sidebarButtons.values().forEach(button ->
                button.getStyleClass().remove("sidebar_btn_active"));

        // Add active class to specified button
        Button activeButton = sidebarButtons.get(buttonId);
        if (activeButton != null) {
            activeButton.getStyleClass().add("sidebar_btn_active");
        }
    }

    public String getActiveButton() {
        return activeButtonId;
    }

    public void refreshSidebar() {
        createSidebarButtons();
    }

    private String loadAppName() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
                return properties.getProperty("spring.application.name", "Sah Pustak Sadan");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Sah Pustak Sadan";
    }
}
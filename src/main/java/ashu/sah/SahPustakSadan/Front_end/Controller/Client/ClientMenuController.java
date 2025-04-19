package ashu.sah.SahPustakSadan.Front_end.Controller.Client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ClientMenuController {

    @FXML private Button dashboard_btn;
    @FXML private Button invoice_btn;
    @FXML private Button product_stock_btn;
    @FXML private Button price_calc_btn;
    @FXML private Button profile_btn;
    @FXML private Button logout_btn;
    @FXML private Button report_btn;

    @FXML
    public void initialize() {
        dashboard_btn.setOnAction(event -> openDashboard());
        invoice_btn.setOnAction(event -> openInvoice());
        product_stock_btn.setOnAction(event -> openProductStock());
        price_calc_btn.setOnAction(event -> openPriceCalculator());
        profile_btn.setOnAction(event -> openProfile());
        logout_btn.setOnAction(event -> logout());
        report_btn.setOnAction(event -> reportBug());
    }

    private void openDashboard() {
        System.out.println("Opening Dashboard...");
    }

    private void openInvoice() {
        System.out.println("Opening Invoice...");
    }

    private void openProductStock() {
        System.out.println("Opening Product Stock...");
    }

    private void openPriceCalculator() {
        System.out.println("Opening Price Calculator...");
    }

    private void openProfile() {
        System.out.println("Opening Profile...");
    }

    private void logout() {
        System.out.println("Logging out...");
    }

    private void reportBug() {
        System.out.println("Opening Bug Report...");
    }
}


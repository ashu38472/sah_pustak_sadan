package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.APIController.LoginController;
import ashu.sah.SahPustakSadan.Front_end.Stage.SceneManager;
import ashu.sah.SahPustakSadan.Service.UserSession;
import ashu.sah.SahPustakSadan.enums.UserRole;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LoginFormController implements Initializable {
    @FXML
    public TextField username;
    @FXML
    public PasswordField password;
    @FXML
    public Button login_btn;
    @FXML
    public Label error;

    @Autowired
    private LoginController loginController;

    @Autowired
    private SceneManager sceneManager;

    @Autowired
    private UserSession userSession;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(event -> handleLogin());

        // Add Enter key support
        username.setOnAction(event -> handleLogin());
        password.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String email = username.getText().trim();
        String pass = password.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            boolean isAuthenticated = loginController.authenticate(email, pass);

            if (isAuthenticated) {
                // Hide error message
                error.setVisible(false);

                // TODO: Get actual user details from your login service
                // For now, using demo data - replace with actual user data from database
                UserRole role = determineUserRole(email); // You should get this from your database
                String userId = "USER_" + System.currentTimeMillis(); // Generate or get from DB
                String userName = getUserNameFromEmail(email); // Get from database

                // Set up user session
                userSession.login(userId, email, userName, role);

                // Switch to dashboard scene
                sceneManager.switchScene("classpath:/scenes/app.fxml", "Dashboard - Sah Pustak Sadan");

            } else {
                showError("Invalid username or password.");
            }
        } catch (IOException e) {
            showError("Error loading dashboard. Please try again.");
            e.printStackTrace();
        } catch (Exception e) {
            showError("Login failed. Please try again.");
            e.printStackTrace();
        }
    }

    // TODO: Replace this with actual database lookup
    private UserRole determineUserRole(String email) {
        // Demo logic - replace with actual database lookup
        if (email.contains("admin")) {
            return UserRole.ADMIN;
        } else if (email.contains("manager")) {
            return UserRole.MANAGER;
        } else if (email.contains("staff")) {
            return UserRole.STAFF;
        } else {
            return UserRole.STAFF; // Default role
        }
    }

    // TODO: Replace this with actual database lookup
    private String getUserNameFromEmail(String email) {
        // Demo logic - replace with actual database lookup
        String name = email.split("@")[0];
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    private void showError(String message) {
        error.setText(message);
        error.setVisible(true);
        error.setStyle("-fx-text-fill: red;");
    }
}
package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.Controller.LoginController;
import ashu.sah.SahPustakSadan.Front_end.Service.SceneManager;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String email = username.getText();
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

                // Switch to dashboard scene
                sceneManager.switchScene("classpath:/scenes/dashboard.fxml", "Dashboard - Sah Pustak Sadan");

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

    private void showError(String message) {
        error.setText(message);
        error.setVisible(true);
        error.setStyle("-fx-text-fill: red;");
    }
}
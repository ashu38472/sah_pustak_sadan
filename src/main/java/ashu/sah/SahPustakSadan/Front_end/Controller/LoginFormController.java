package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.APIController.LoginController;
import ashu.sah.SahPustakSadan.Front_end.Stage.Navigation;
import ashu.sah.SahPustakSadan.Service.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Component
public class LoginFormController implements Initializable {

    @FXML public Label error;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    @Autowired
    private LoginController loginController;
    @Autowired private UserSession userSession;
    @Autowired private Navigation navigation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showErrorAlert("Validation Error", "Username and password must not be empty.");
            return;
        }

        try {
            boolean authenticated = loginController.authenticate(username, password);
            if (authenticated) {
                userSession.login(username);
                log.info("User '{}' logged in successfully", username);

                navigation.enterApp();
            } else {
                showErrorAlert("Authentication Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            log.error("Login error", e);
            showErrorAlert("Login Error", "An error occurred during login. Please try again.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

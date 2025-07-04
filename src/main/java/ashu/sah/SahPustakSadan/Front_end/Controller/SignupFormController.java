package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.Controller.SignUpController;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class SignupFormController implements Initializable {

    @FXML
    public TextField nameField;
    @FXML
    public TextField emailField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField confirmPasswordField;
    @FXML
    public Button registerButton;
    @FXML
    public Label error;

    @Autowired
    private SignUpController signUpController;
    @Autowired
    private LoginViewController loginViewController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        registerButton.setOnAction(e -> registerUser());
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            error.setText("Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            error.setText("Passwords do not match.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            error.setText("Invalid email format.");
            return;
        }

        if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")) {
            error.setText("Password must be at least 6 characters,\ncontain upper & lower case letters and a digit.");
            return;
        }

        boolean success = signUpController.register(name, email, password);
        if (success) {
            error.setStyle("-fx-text-fill: green;");
            error.setText("Registration successful! Please login.");
            clearForm();
            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(e -> loginViewController.handleSwitchAuthMode());
            delay.play();
        } else {
            error.setStyle("-fx-text-fill: red;");
            error.setText("User with this email already exists.");
        }
    }

    private void clearForm() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }
}

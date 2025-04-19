package ashu.sah.SahPustakSadan.Front_end.Controller;

import ashu.sah.SahPustakSadan.Controller.LoginController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class LoginFormController implements Initializable {
    @FXML
    public ChoiceBox<String> acc_selector;
    @FXML
    public TextField username;
    @FXML
    public PasswordField password; // Use PasswordField for password input
    @FXML
    public Button login_btn;
    @FXML
    public Label error;

    @Autowired
    private LoginController loginController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_btn.setOnAction(event -> handleLogin());
    }
    private void handleLogin() {
        String email = username.getText();
        String pass = password.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            error.setText("Please fill in all fields.");
            error.setVisible(true);
            return;
        }

        System.out.println(loginController.authenticate(email,pass));
    }
}

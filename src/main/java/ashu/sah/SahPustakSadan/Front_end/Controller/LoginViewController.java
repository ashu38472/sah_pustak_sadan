package ashu.sah.SahPustakSadan.Front_end.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

@Component
public class LoginViewController implements Initializable {
    @FXML
    public Label heading;
    @FXML
    private VBox logoContainer;

    @FXML
    private VBox loginFieldContainer;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Text appNameLabel;

    private Parent loginForm;
    private Parent signupForm;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Value("classpath:/scenes/login_form.fxml")
    private Resource loginResource;

    @Autowired
    @Value("classpath:/scenes/signup_form.fxml")
    private Resource signupResource;

    @FXML
    private Text switchMessage;

    @FXML
    private Hyperlink switchButton;

    private boolean showingLogin = true;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loginForm = loadForm(loginResource);
            signupForm = loadForm(signupResource);
            loginFieldContainer.getChildren().add(1, loginForm);
            heading.setText("Sign In");
            switchMessage.setText("Don't have an account?");
            switchButton.setText("Sign Up");
            showingLogin = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Bind container sizes to root pane size
        if (rootPane != null && logoContainer != null) {
            logoContainer.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.4)); // 40% of parent width
        }
        if (rootPane != null && loginFieldContainer != null) {
            loginFieldContainer.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.6)); // 60% of parent width
        }

        // Load the app name from application.properties
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            if (input != null) {
                properties.load(input);
                String appName = properties.getProperty("spring.application.name", "Sah Pustak Sadan");
                appNameLabel.setText(appName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Parent loadForm(Resource resource) throws IOException {
        FXMLLoader loader = new FXMLLoader(resource.getURL());
        loader.setControllerFactory(applicationContext::getBean);
        return loader.load();
    }

    @FXML
    public void handleSwitchAuthMode() {
        if (showingLogin) {
            loginFieldContainer.getChildren().set(1, signupForm);
            heading.setText("Sign Up");
            switchMessage.setText("Already have an account?");
            switchButton.setText("Login");
        } else {
            loginFieldContainer.getChildren().set(1, loginForm);
            heading.setText("Sign In");
            switchMessage.setText("Don't have an account?");
            switchButton.setText("Sign Up");
        }
        showingLogin = !showingLogin;
    }
}

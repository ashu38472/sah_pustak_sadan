package ashu.sah.SahPustakSadan.Front_end;

import ashu.sah.SahPustakSadan.Front_end.IndexApplication.StageReadyEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class StageInitializer implements ApplicationListener<StageReadyEvent> {
    @Value("classpath:/scenes/login.fxml")
    private Resource loginResource;
    private final String applicationTitle;
    private final String applicationIcon;
    private final ApplicationContext applicationContext;

    public StageInitializer(@Value("${spring.application.name}") String applicationTitle, ApplicationContext applicationContext, @Value("${spring.application.icon_path}") String applicationIcon) {
        this.applicationContext = applicationContext;
        this.applicationTitle = applicationTitle;
        this.applicationIcon = applicationIcon;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(loginResource.getURL());
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            Parent parent = fxmlLoader.load();
            Stage stage = event.getStage();
            Scene scene = new Scene(parent, 835, 560);
            stage.setScene(scene);
            stage.setTitle(applicationTitle);
            InputStream iconStream = getClass().getResourceAsStream(applicationIcon);
            if (iconStream != null) {
                stage.getIcons().add(new Image(iconStream));
            } else {
                System.err.println("Icon not found: " + applicationIcon);
            }            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

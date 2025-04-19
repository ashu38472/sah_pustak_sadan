package ashu.sah.SahPustakSadan.Front_end;

import ashu.sah.SahPustakSadan.SahPustakSadanApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;

public class IndexApplication extends Application {
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(SahPustakSadanApplication.class).web(org.springframework.boot.WebApplicationType.SERVLET).run();}

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }

    @Override
    public void start(Stage stage) {

        applicationContext.publishEvent(new StageReadyEvent(stage));
//        Pane rootNode = loader.load();
//
//        MenuBar menuBar=createMenu();
//        rootNode.getChildren().addAll(menuBar);
    }

    static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return (Stage) getSource();
        }
    }
}

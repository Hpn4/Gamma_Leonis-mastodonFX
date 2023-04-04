package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.presentation.AccountContentController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.MainWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private MainWindowController mainController;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("window.fxml"));

        Parent p = fxmlLoader.load();
        mainController = fxmlLoader.getController();

        Scene scene = new Scene(p, 1200, 700);
        stage.setTitle("Gamma Leonis Mastodon Client");
        stage.setScene(scene);

        requestShowAccount(null);

        stage.show();
    }

    public void requestShowAccount(String account) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("account_content.fxml"));
        Pane p = fxmlLoader.load();

        AccountContentController controller = fxmlLoader.getController();
        controller.setAccount(account);
        controller.setApplication(this);

        mainController.setCenter(p);
    }

    public static void main(String[] args) {
        launch();
    }
}
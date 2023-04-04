package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.presentation.AbstractController;
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

    private static MainApplication instance;

    public static MainApplication getInstance() {
        return instance;
    }

    private MainWindowController mainController;

    // Window
    private Window<AccountContentController> accountsWindow;

    private API api;

    public static void main(String[] args) {
        launch();
    }

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

        instance = this;
    }

    public void requestShowAccount(String account) {
        try {
            if (accountsWindow == null)
                accountsWindow = load("account_content.fxml");

            accountsWindow.controller.setAccount(account);

            mainController.setCenter(accountsWindow.ui);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Window<E extends AbstractController> {
        E controller;
        Pane ui;
    }

    private <E extends AbstractController> Window<E> load(String url) throws IOException {
        Window<E> ref = new Window<E>();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(url));
        ref.ui = fxmlLoader.load();
        ref.controller = fxmlLoader.getController();
        ref.controller.setApplication(this);
        ref.controller.setAPI(api);

        return ref;
    }
}
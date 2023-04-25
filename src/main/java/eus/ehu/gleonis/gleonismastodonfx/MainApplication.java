package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Tag;
import eus.ehu.gleonis.gleonismastodonfx.db.DBManager;
import eus.ehu.gleonis.gleonismastodonfx.presentation.AbstractController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.AccountContentController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.LoginController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.MainWindowController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.TagsScrollableContent;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.TootsScrollableContent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static MainApplication instance;

    private MainWindowController mainController;

    private API api;

    private DBManager dbManager;

    // Cached Window
    private Window<AccountContentController> accountsWindow;

    private Window<MainWindowController> mainWindow;

    private Window<LoginController> loginWindow;

    private Stage stage;

    public static MainApplication getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        // Initialize API, DB and other variables
        api = new API();
        dbManager = new DBManager();
        instance = this;
        this.stage = stage;

        // Request the first screen, login if there is no access token or main window if there is one
        if (api.isUserConnected()) {
            api.setupUser(dbManager);
            requestMainScreen();
        }else
            requestLoginScreen();

        stage.setTitle("Gamma Leonis Mastodon Client");

        stage.show();
    }

    public void requestLoginScreen() {
        try {
            if (loginWindow == null)
                loginWindow = load("loginWindow.fxml");

            loginWindow.controller.init();

            // When possible, we just change the root element of the Scene instead of creating a new one
            // In fact creating a new Scene instance is performance heavy
            if (stage.getScene() == null)
                stage.setScene(new Scene(loginWindow.ui));
            else {
                stage.getScene().setRoot(loginWindow.ui);
                stage.setWidth(700);
                stage.setHeight(500);
                stage.centerOnScreen();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestMainScreen() {
        try {
            if (mainWindow == null)
                mainWindow = load("window.fxml");

            mainController = mainWindow.controller;
            mainController.init();

            if (stage.getScene() == null)
                stage.setScene(new Scene(mainWindow.ui, 1400, 750));
            else {
                stage.getScene().setRoot(mainWindow.ui);
                stage.setWidth(1400);
                stage.setHeight(750);
                stage.centerOnScreen();
            }

            requestShowAccount(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void requestShowToots(ListStream<Status> toots, int itemPerPage) {
        TootsScrollableContent tootsScrollableContent = new TootsScrollableContent(toots, itemPerPage);

        mainController.setCenter(tootsScrollableContent);
    }

    public void requestShowTags(ListStream<Tag> tags, int itemPerPage) {
        TagsScrollableContent tagsScrollableContent = new TagsScrollableContent(tags, itemPerPage);

        mainController.setCenter(tagsScrollableContent);
    }

    public API getAPI() {
        return api;
    }

    private <E extends AbstractController> Window<E> load(String url) throws IOException {
        Window<E> ref = new Window<>();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(url));
        ref.ui = fxmlLoader.load();
        ref.controller = fxmlLoader.getController();
        ref.controller.setApplication(this);
        ref.controller.setDBManager(dbManager);
        ref.controller.setAPI(api);

        return ref;
    }

    private static class Window<E extends AbstractController> {
        E controller;
        Pane ui;
    }
}
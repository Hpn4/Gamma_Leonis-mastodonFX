package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Visibility;
import eus.ehu.gleonis.gleonismastodonfx.db.DBManager;
import eus.ehu.gleonis.gleonismastodonfx.presentation.*;
import eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane.AccountRPController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane.SearchRPController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane.TrendingRPController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.TootsScrollableContent;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApplication extends Application {

    private static final Logger logger = LogManager.getLogger("Main");
    private static MainApplication instance;
    private final ResourceBundle translation = ResourceBundle.getBundle("translation", Locale.getDefault());

    private API api;

    private DBManager dbManager;

    private Stage stage;

    private MainWindowController mainController;

    // Cached Window
    private Window<AccountRPController> accountsWindow;
    private Window<SearchRPController> searchWindow;
    private Window<TrendingRPController> trendingWindow;

    private Window<MainWindowController> mainWindow;
    private Window<LoginWindowController> loginWindow;
    private Window<ConfigWindowController> configWindow;

    public static MainApplication getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        long start = System.currentTimeMillis();
        logger.info("Start Gamma Leonis Mastodon Client");

        // Initialize API, DB and other variables
        api = new API();
        dbManager = new DBManager();
        instance = this;
        this.stage = stage;

        // If there is no config file, we launch UI to fill it
        if(api.isConfigFileEmpty())
            requestConfigFileScreen();
        else {
            api.initAPI();
            dbManager.initDB();

            // Request the first screen, login if there is no access token or main window if there is one
            if (api.isUserConnected()) {
                api.setupUser(dbManager);
                requestMainScreen();
            } else
                requestLoginScreen();
        }

        stage.setTitle("Gamma Leonis Mastodon Client");

        stage.show();

        // When the window is closed, we close the DB connection
        stage.setOnCloseRequest(event -> {
            logger.info("Close Gamma Leonis Mastodon Client");
            dbManager.closeDb();
            api.closeAPI(); // Close stream
        });

        logger.debug("Gamma Leonis Mastodon Client started in {} ms", System.currentTimeMillis() - start);
    }

    private void requestConfigFileScreen() {
        try {
            logger.info("No config file found, switch to config file screen");

            if (configWindow == null)
                configWindow = load("configFile_window.fxml");

            // When possible, we just change the root element of the Scene instead of creating a new one
            // In fact creating a new Scene instance is performance heavy
            if (stage.getScene() == null)
                stage.setScene(new Scene(configWindow.ui));
            else {
                stage.getScene().setRoot(configWindow.ui);
                stage.setWidth(700);
                stage.setHeight(500);
                stage.centerOnScreen();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestLoginScreen() {
        try {
            logger.debug("Switch to login screen");

            if (loginWindow == null)
                loginWindow = load("login_window.fxml");

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
            logger.debug("Switch to main screen");

            if (mainWindow == null)
                mainWindow = load("main_window.fxml");

            if (stage.getScene() == null)
                stage.setScene(new Scene(mainWindow.ui, 1400, 750));
            else {
                stage.getScene().setRoot(mainWindow.ui);
                stage.setWidth(1400);
                stage.setHeight(750);
                stage.centerOnScreen();
            }

            mainController = mainWindow.controller;
            mainController.init(stage.getScene());

            requestShowAccount(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestShowAccount(String account) {
        try {
            logger.debug("Switch to account screen");

            if (accountsWindow == null)
                accountsWindow = load("account_root-pane.fxml");

            accountsWindow.controller.setAccount(account);

            mainController.setCenter(accountsWindow.ui);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestShowSendToot(String tootContent, Visibility visibility, String inResponseTo) {
        mainController.gainTootFocus(tootContent, visibility, inResponseTo);
    }

    public void requestSearch(String query) {
        if(query == null || query.isEmpty())
            return;

        try {
            logger.debug("Switch to search screen");

            if (searchWindow == null)
                searchWindow = load("search_root-pane.fxml");

            searchWindow.controller.doSearch(query);

            mainController.setCenter(searchWindow.ui);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestShowTrending() {
        try {
            logger.debug("Switch to trending screen");

            if (trendingWindow == null)
                trendingWindow = load("trending_root-pane.fxml");

            trendingWindow.controller.refreshTrending();

            mainController.setCenter(trendingWindow.ui);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestShowStreamToots(ListStream<Status> toots){
        logger.debug("Switch to scrollable toots screen");

        TootsScrollableContent tootsScrollableContent = new TootsScrollableContent(toots, 0);

        mainController.setCenter(tootsScrollableContent, false);
    }

    public void requestShowToots(ListStream<Status> toots, int itemPerPage) {
        logger.debug("Switch to scrollable toots screen");

        TootsScrollableContent tootsScrollableContent = new TootsScrollableContent(toots, itemPerPage);

        mainController.setCenter(tootsScrollableContent);
    }

    public API getAPI() {
        return api;
    }

    private <E extends AbstractController> Window<E> load(String url) throws IOException {
        Window<E> ref = new Window<>();

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(url), translation);
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
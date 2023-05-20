package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Context;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Visibility;
import eus.ehu.gleonis.gleonismastodonfx.db.DBManager;
import eus.ehu.gleonis.gleonismastodonfx.presentation.AbstractController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.ConfigWindowController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.LoginWindowController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.MainWindowController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.comp.MediaPlayerNode;
import eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane.AccountRPController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane.SearchRPController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane.TrendingRPController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.*;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class MainApplication extends Application {

    private static final Logger logger = LogManager.getLogger("Main");

    private static MainApplication instance;

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
        if (api.isConfigFileEmpty())
            requestConfigFileScreen();
        else {
            api.initAPI();
            dbManager.initDB();

            // Request the first screen, login if there is no access token or main window if there is one
            if (api.isUserConnected(dbManager)) {
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
        configWindow = load("configFile_window.fxml", "config file", configWindow);

        setupScene(700, 500, configWindow.ui);
    }

    public void requestLoginScreen() {
        MediaPlayerNode.freeAndStopMedias();
        loginWindow = load("login_window.fxml", "login", loginWindow);

        loginWindow.controller.init();

        setupScene(700, 500, loginWindow.ui);
    }

    public void requestMainScreen() {
        MediaPlayerNode.freeAndStopMedias();
        mainWindow = load("main_window.fxml", "main", mainWindow);
        setupScene(1400, 750, mainWindow.ui);

        mainController = mainWindow.controller;
        mainController.init(stage.getScene());

        requestShowAccount(null);
    }

    private void setupScene(int width, int height, Pane root) {
        if (stage.getScene() == null)
            stage.setScene(new Scene(root, width, height));
        else {
            stage.getScene().setRoot(root);
            stage.setWidth(width);
            stage.setHeight(height);
            stage.centerOnScreen();
        }
    }

    public void requestShowAccount(String account) {
        mainController.uncheckCategory();
        MediaPlayerNode.freeAndStopMedias();
        accountsWindow = load("account_root-pane.fxml", "account", accountsWindow);

        accountsWindow.controller.setAccount(account);
        mainController.setCenter(accountsWindow.ui);
    }

    public void requestShowSendToot(String tootContent, Visibility visibility, String inResponseTo) {
        mainController.gainTootFocus(tootContent, visibility, inResponseTo);
    }

    public void requestSearch(String query) {
        mainController.uncheckCategory();
        MediaPlayerNode.freeAndStopMedias();
        if (query == null || query.isEmpty())
            return;

        searchWindow = load("search_root-pane.fxml", "search", searchWindow);

        searchWindow.controller.doSearch(query);
        mainController.setCenter(searchWindow.ui);
    }

    public void requestShowTrending() {
        MediaPlayerNode.freeAndStopMedias();
        trendingWindow = load("trending_root-pane.fxml", "trending", trendingWindow);

        trendingWindow.controller.refreshTrending();
        mainController.setCenter(trendingWindow.ui);
    }

    public void requestShowStreamToots(ListStream<Status> toots) {
        MediaPlayerNode.freeAndStopMedias();
        logger.debug("Switch to scrollable toots screen");

        TootsScrollableContent tootsScrollableContent = new TootsScrollableContent(toots, 0);

        mainController.setCenter(tootsScrollableContent, false);
    }

    public void requestShowToots(ListStream<Status> toots, int itemPerPage, boolean clearSelection) {
        if (clearSelection)
            mainController.uncheckCategory();
        requestScrollableContent(new TootsScrollableContent(toots, itemPerPage), "toots");
    }

    public void requestShowConversation() {
        requestScrollableContent(new ConversationScrollableContent(api.getConversations(10), 10), "conversations");
    }

    public void requestShowNotification() {
        requestScrollableContent(new NotificationScrollableContent(api.getNotifications(10), 10), "notifications");
    }

    public void requestShowTootContext(Status status) {
        mainController.uncheckCategory();
        Context context = api.getStatusContext(status.getId());
        if (context == null) {
            logger.error("Unable to get toot context");
            return;
        }

        MediaPlayerNode.freeAndStopMedias();
        logger.debug("Switch to toot context screen");

        ContextScrollableContent tootContextContent = new ContextScrollableContent(status, context);

        mainController.setCenter(tootContextContent);
    }

    private void requestScrollableContent(AbstractScrollableContent<?> content, String title) {
        MediaPlayerNode.freeAndStopMedias();
        logger.debug("Switch to " + title + " screen");

        mainController.setCenter(content);
    }

    public API getAPI() {
        return api;
    }

    public Stage getStage() {
        return stage;
    }

    private <E extends AbstractController> Window<E> load(String url, String logTitle, Window<E> window) {
        logger.debug("Switch to " + logTitle + " screen");
        if (window != null)
            return window;

        Window<E> ref = new Window<>();

        try {
            FXMLLoader fxmlLoader = Utils.loadAndTranslate(MainApplication.class.getResource(url));
            ref.ui = fxmlLoader.load();
            ref.controller = fxmlLoader.getController();
            ref.controller.setApplication(this);
            ref.controller.setDBManager(dbManager);
            ref.controller.setAPI(api);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ref;
    }

    private static class Window<E extends AbstractController> {
        E controller;
        Pane ui;
    }
}
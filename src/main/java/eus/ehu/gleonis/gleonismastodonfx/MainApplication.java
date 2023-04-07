package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Tag;
import eus.ehu.gleonis.gleonismastodonfx.presentation.AbstractController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.AccountContentController;
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
    // Window
    private Window<AccountContentController> accountsWindow;
    private API api;

    public static MainApplication getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        api = new API();
        Window<MainWindowController> mainWindow = load("window.fxml");

        mainController = mainWindow.controller;
        mainController.init();

        Scene scene = new Scene(mainWindow.ui, 1200, 700);
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
        ref.controller.setAPI(api);

        return ref;
    }

    private static class Window<E extends AbstractController> {
        E controller;
        Pane ui;
    }
}
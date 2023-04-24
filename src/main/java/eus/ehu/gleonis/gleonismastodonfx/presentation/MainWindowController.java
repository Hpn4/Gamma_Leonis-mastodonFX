package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;

public class MainWindowController extends AbstractController {

    @FXML
    private Circle accountImage;

    @FXML
    private Label accountUsername;

    @FXML
    private Label accountWebfinger;

    @FXML
    private BorderPane sceneContent;

    // We don't use initilize because we need to wait for the API to be set
    public void init() {
        Account account = api.verifyCredentials();

        accountUsername.setText(account.getUsername());
        accountWebfinger.setText("@" + account.getAcct());

        account.getAvatarCachedImage().setImage(accountImage);

        accountImage.setOnMouseClicked(e -> getApplication().requestShowAccount(null));
    }


    public void setCenter(Node pane) {
        BorderPane.setAlignment(pane, Pos.TOP_CENTER);

        sceneContent.setCenter(pane);
    }

    @FXML
    void onBookmarksClick() {
        getApplication().requestShowToots(api.getBookmarks(10), 10);
    }

    @FXML
    void onDirectMsgClick() {

    }

    @FXML
    void onFavouritesClick() {
        getApplication().requestShowToots(api.getFavourites(10), 10);
    }

    @FXML
    void onGlobalTLClick() {

    }

    @FXML
    void onHomeClick() {
        getApplication().requestShowToots(api.getHomeTimeline(10), 10);
    }

    @FXML
    void onListsClick() {

    }

    @FXML
    void onLocalTLClick() {

    }

    @FXML
    void onNotificationsClick() {

    }

    @FXML
    void onSettingsClick() {
        getApplication().requestLoginScreen();
    }

    @FXML
    void onTrendingClick() {
        getApplication().requestShowTags(api.getTrendingTags(10), 10);
    }
}

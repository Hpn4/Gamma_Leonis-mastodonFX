package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AccountContentController {

    private final API api = new API();

    private Account account;

    @FXML
    private ImageView accountAvatar;

    @FXML
    private ImageView accountBanner;

    @FXML
    private Label statusesCount;

    @FXML
    private Label followersCount;

    @FXML
    private Label followingsCount;

    @FXML
    private ScrollPane itemsScrollPane;

    @FXML
    private VBox itemBox;

    private ObservableList<Account> followingsList;

    private ObservableList<Account> followersList;

    private ObservableList<Status> tootsList;


    @FXML
    void onFollowersClick() {
        showFollowers();
    }

    @FXML
    void onFollowingsClick() {
        showFollowings();
    }

    @FXML
    void onTootsClick() {
        showToots();
    }

    @FXML
    public void initialize() {
        account = api.verifyCredentials();

        statusesCount.setText(String.valueOf(account.getStatusesCount()));
        followersCount.setText(String.valueOf(account.getFollowersCount()));
        followingsCount.setText(String.valueOf(account.getFollowingCount()));

        accountBanner.setImage(new Image(account.getHeader(), true));
        accountAvatar.setImage(new Image(account.getAvatar(), true));

        itemBox.prefWidthProperty().bind(itemsScrollPane.widthProperty());
    }


    private void updateFollowers() {
        followersList = api.getAccountFollowers(account.getId(), 20).getElement();
    }

    private void updateFollowings() {
        followingsList = api.getAccountFollowing(account.getId(), 20).getElement();
    }

    private void updateToots() {
        tootsList = api.getAccountStatuses(account.getId(), 20).getElement();
    }


    private void showFollowers() {
        updateFollowers();

        Utils.mapByValue(followersList, itemBox.getChildren(), p -> {
            Pane pane = new AccountItemCell(p, false).getAccountItem();
            pane.prefWidthProperty().bind(itemsScrollPane.widthProperty().subtract(20));

            return pane;
        });
    }

    private void showFollowings() {
        updateFollowings();

        Utils.mapByValue(followingsList, itemBox.getChildren(), p -> {
            Pane pane = new AccountItemCell(p, true).getAccountItem();
            pane.prefWidthProperty().bind(itemsScrollPane.widthProperty().subtract(20));

            return pane;
        });
    }

    private void showToots() {
        updateToots();

        Utils.mapByValue(tootsList, itemBox.getChildren(), p -> {
            Pane pane = new TootsItemCell(p, api).getGraphic();
            pane.prefWidthProperty().bind(itemsScrollPane.widthProperty().subtract(20));

            return pane;
        });
    }
}

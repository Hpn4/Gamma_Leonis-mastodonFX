package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private ListView<Status> tootsListView;

    @FXML
    private ListView<Account> accountListView;

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
    public void initialize(){
        account = api.verifyCredentials();

        statusesCount.setText(String.valueOf(account.getStatusesCount()));
        followersCount.setText(String.valueOf(account.getFollowersCount()));
        followingsCount.setText(String.valueOf(account.getFollowingCount()));

        accountBanner.setImage(new Image(account.getHeader(), true));
        accountAvatar.setImage(new Image(account.getAvatar(), true));
    }


    private void updateFollowers() {
        followersList = api.getAccountFollowers(account.getId(), 20).getElement();
    }
    private void updateFollowings() {
        followingsList = api.getAccountFollowing(account.getId(), 20).getElement();
    }
    private void updateToots() {tootsList = api.getAccountStatuses(account.getId(), 20).getElement();}


    private void showFollowers()  {

        updateFollowers();
        System.out.println(followersList.size());

        accountListView.toFront();

        if (accountListView != null) {
            accountListView.setItems(followersList);
            accountListView.setCellFactory(param -> new AccountItemCell(false));
        }
    }

    private void showFollowings() {

        updateFollowings();

        accountListView.toFront();

        if (accountListView != null) {
            accountListView.setItems(followingsList);
            accountListView.setCellFactory(param -> new AccountItemCell(true));
        }
    }

    private void showToots() {

        updateToots();

        tootsListView.toFront();

        if (tootsListView == null) {
            tootsListView.setItems(tootsList);
            tootsListView.setCellFactory(param -> new TootsItemCell());
        }
    }
}

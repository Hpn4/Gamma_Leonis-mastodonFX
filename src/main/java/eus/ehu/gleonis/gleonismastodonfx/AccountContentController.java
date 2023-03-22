package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class AccountContentController {

    private final API api = new API();

    private Account account;

    @FXML
    private ListView<Status> tootsListView;

    @FXML
    private ListView<Account> accountListView;

    private ObservableList<Account> followingsList;
    private ObservableList<Account> followersList;

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
    }


    private void updateFollowers() {
        followersList = api.getAccountFollowers(account.getId(), 20).getElement();
    }
    private void updateFollowings() {
        followingsList = api.getAccountFollowing(account.getId(), 20).getElement();
    }


    private void showFollowers() {

        updateFollowers();

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

        tootsListView.toFront();
        // TODO
    }
}

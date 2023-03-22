package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;

public class AccountContentController {

    private API api = new API();

    private Account account;

    @FXML
    private ListView<?> tootsListView;

    @FXML
    private ListView<?> followersListView;

    @FXML
    private ListView<?> followingListView;

    @FXML
    private StackPane listViewStack;

    @FXML
    private Button followersButton;

    @FXML
    private Button followingButton;

    @FXML
    private ListView<Account> listView;

    @FXML
    private Button tootsButton;

    @FXML
    void onFollowersClick(ActionEvent event) {
        showFollowers();
    }

    @FXML
    void onFollowingsClick(ActionEvent event) {
        // showFollowings();
    }

    @FXML
    void onTootsClick(ActionEvent event) {
        // showToots();
    }

    @FXML
    void initialize() {

        account = api.verifyCredentials();



    }

    public void showFollowers() {

        ObservableList<Account> followers = api.getAccountFollowers(account.getId(), 20).getElement();

        if (listView != null) {
            listView.setItems(followers);
            listView.setCellFactory(param -> {
                    var cell = new AccountItemCell();
                    return cell;
            });
        }
    }

}

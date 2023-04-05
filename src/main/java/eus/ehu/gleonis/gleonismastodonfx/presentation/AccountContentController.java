package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.AccountScrollableContent;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.TootsScrollableContent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class AccountContentController extends AbstractController {

    private final API api = new API();

    @FXML
    private BorderPane rootBorderPane;

    private Account account;

    @FXML
    private ToggleGroup accountDataSelection;

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

    public void setAccount(String acc) {
        account = acc == null ? api.verifyCredentials() : api.getAccount(acc);

        statusesCount.setText(String.valueOf(account.getStatusesCount()));
        followersCount.setText(String.valueOf(account.getFollowersCount()));
        followingsCount.setText(String.valueOf(account.getFollowingCount()));

        accountBanner.setImage(new Image(account.getHeader(), true));
        //accountBanner.fitWidthProperty().bind(rootBorderPane.widthProperty());
        //accountBanner.setPreserveRatio(false);

        accountAvatar.setImage(new Image(account.getAvatar(), true));

        // Reset UI when switching to other accounts
        rootBorderPane.setCenter(null);

        if(accountDataSelection.getSelectedToggle() != null)
            accountDataSelection.getSelectedToggle().setSelected(false);
    }

    private void showFollowers() {
        ListStream<Account> accountListStream = api.getAccountFollowers(account.getId(), 20);
        AccountScrollableContent accountScrollableContent = new AccountScrollableContent(accountListStream, 10, false);

        accountScrollableContent.addToBorderPane(rootBorderPane);
    }

    private void showFollowings() {
        ListStream<Account> accountListStream = api.getAccountFollowing(account.getId(), 20);
        AccountScrollableContent accountScrollableContent = new AccountScrollableContent(accountListStream, 10, true);

        accountScrollableContent.addToBorderPane(rootBorderPane);
    }

    private void showToots() {
        ListStream<Status> tootsListStream = api.getAccountStatuses(account.getId(), 20);
        TootsScrollableContent tootsScrollableContent = new TootsScrollableContent(tootsListStream, 10, api);

        tootsScrollableContent.addToBorderPane(rootBorderPane);
    }
}

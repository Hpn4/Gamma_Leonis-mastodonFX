package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Relationship;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.AccountScrollableContent;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.TootsScrollableContent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AccountContentController extends AbstractController {

    private static final Logger logger = LogManager.getLogger("AccountContentController");

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
    private Button accountRelationButton;

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

    private void unfollowAccountButton() {
        accountRelationButton.setText("Unfollow");
        accountRelationButton.setOnAction(e -> {
            api.unfollowAccount(account.getId());
            followAccountButton();
        });
    }

    private void followAccountButton() {
        accountRelationButton.setText("Follow");
        accountRelationButton.setOnAction(e -> {
            api.followAccount(account.getId());
            unfollowAccountButton();
        });
    }

    public void setAccount(String acc) {
        account = acc == null ? api.verifyCredentials() : api.getAccount(acc);

        statusesCount.setText(String.valueOf(account.getStatusesCount()));
        followersCount.setText(String.valueOf(account.getFollowersCount()));
        followingsCount.setText(String.valueOf(account.getFollowingCount()));

        accountBanner.setImage(new Image(account.getHeader(), true));
        accountBanner.fitWidthProperty().bind(rootBorderPane.widthProperty());

        accountAvatar.setImage(new Image(account.getAvatar(), true));

        // Reset UI when switching to other accounts
        rootBorderPane.setCenter(null);

        // get relationship with this account
        if (acc != null) {
            Relationship relationship = api.getRelationships(account.getId()).getElement().get(0);
            if (relationship.isFollowing())
                unfollowAccountButton();
            else
                followAccountButton();

        }

        accountRelationButton.setVisible(acc != null); // For our account we disable this button

        if (accountDataSelection.getSelectedToggle() != null)
            accountDataSelection.getSelectedToggle().setSelected(false);
    }

    private void showFollowers() {
        long t = System.currentTimeMillis();
        ListStream<Account> accountListStream = api.getAccountFollowers(account.getId(), 20);
        AccountScrollableContent accountScrollableContent = new AccountScrollableContent(accountListStream, 10, true);

        accountScrollableContent.addToBorderPane(rootBorderPane);
        logger.info("Time to display followers: " + (System.currentTimeMillis() - t));
    }

    private void showFollowings() {
        long t = System.currentTimeMillis();
        ListStream<Account> accountListStream = api.getAccountFollowing(account.getId(), 20);
        AccountScrollableContent accountScrollableContent = new AccountScrollableContent(accountListStream, 10, false);

        accountScrollableContent.addToBorderPane(rootBorderPane);
        logger.info("Time to display followings: " + (System.currentTimeMillis() - t));
    }

    private void showToots() {
        long t = System.currentTimeMillis();
        ListStream<Status> tootsListStream = api.getAccountStatuses(account.getId(), 10);
        TootsScrollableContent tootsScrollableContent = new TootsScrollableContent(tootsListStream, 10);

        tootsScrollableContent.addToBorderPane(rootBorderPane);
        logger.info("Time to display toots: " + (System.currentTimeMillis() - t));
    }
}

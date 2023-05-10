package eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Relationship;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class AccountRPController extends AbstractPanelSwitchController {

    private Account account;

    @FXML
    private BorderPane rootBorderPane;

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

    public void setAccount(String acc) {
        super.init(rootBorderPane, accountDataSelection, 10);

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

    private void unfollowAccountButton() {
        accountRelationButton.setText(Utils.getTranslation("account.unfollow_tooltips"));
        accountRelationButton.setOnAction(e -> {
            api.unfollowAccount(account.getId());
            followAccountButton(); // Switch button to follow
        });
    }

    private void followAccountButton() {
        accountRelationButton.setText(Utils.getTranslation("account.follow"));
        accountRelationButton.setOnAction(e -> {
            api.followAccount(account.getId());
            unfollowAccountButton(); // Switch button to unfollow
        });
    }

    @FXML
    void onFollowersClick() {
        showScrollableAccounts(api.getAccountFollowers(account.getId(), 20), true);
    }

    @FXML
    void onFollowingsClick() {
        showScrollableAccounts(api.getAccountFollowing(account.getId(), 20), false);
    }

    @FXML
    void onTootsClick() {
        showScrollableToots(api.getAccountStatuses(account.getId(), 10));
    }
}

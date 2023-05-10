package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Relationship;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.List;

public class AccountItem extends AbstractItem<Account> {

    private boolean removeFromFollowers;

    private FXMLLoader loader;

    @FXML
    private ImageView accountAvatar;
    @FXML
    private HBox accountItem;
    @FXML
    private Label accountName;
    @FXML
    private Label accountWebfinger;
    @FXML
    private Button followButton;

    @FXML
    private Button unfollowButton;

    public AccountItem(AccountScrollableContent asc, Account account, boolean followersPanel) {
        super(asc);
        elem = account;

        setupUI(account, followersPanel);
    }

    @FXML
    void onFollowClick() {
        api.followAccount(elem.getId());
        followButton.setVisible(false);
    }

    @FXML
    void onUnfollowClick() {
        if (removeFromFollowers) {
            api.removeAccountFromFollowers(elem.getId());
            deleteItemFromUI();
        } else {
            api.unfollowAccount(elem.getId());
            followButton.setVisible(true);
        }
    }

    private void setupUI(Account account, boolean followersPanel) {
        if (account == null)
            return;

        if (loader == null) {
            loader = new FXMLLoader(MainApplication.class.getResource("account_item.fxml"));
            loader.setController(this);

            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // We are interested in only the first since we send 1 ID
        List<Relationship> relations = api.getRelationships(account.getId()).getElement();
        if (relations.size() == 1) {
            Relationship relation = relations.get(0);

            // Followers: remove from followers (always) & add to followings (if not already)
            // Following: unfollow
            removeFromFollowers = followersPanel && relation.isFollowedBy();
            if (removeFromFollowers)
                unfollowButton.setTooltip(new Tooltip("Remove from followers"));
            else {
                unfollowButton.setTooltip(new Tooltip("Unfollow"));
                unfollowButton.setVisible(relation.isFollowing());
            }

            followButton.setVisible(!relation.isFollowing()); // Show follow button only if we are not following this account
        } else {
            followButton.setVisible(true);
            unfollowButton.setVisible(false);
        }

        accountName.setText(account.getUsername());
        accountWebfinger.setText("@" + account.getAcct());

        Image avatar = new Image(account.getAvatar(), true);
        accountAvatar.setImage(avatar);

        accountItem.setOnMouseClicked(e -> MainApplication.getInstance().requestShowAccount(account.getId()));
    }

    public HBox getParent() {
        return accountItem;
    }
}

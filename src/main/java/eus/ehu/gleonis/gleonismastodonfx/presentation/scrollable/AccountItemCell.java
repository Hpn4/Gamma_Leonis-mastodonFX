package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class AccountItemCell {

    private final boolean isFollowings;

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

    public AccountItemCell(Account account, boolean isFollowings) {
        super();

        this.isFollowings = isFollowings;
        updateItem(account);
    }

    @FXML
    void onFollowClick() {
        System.out.println("Following " + accountName.getText());
        //TODO
    }

    @FXML
    void onUnfollowClick() {
        System.out.println("Unfollowing " + accountName.getText());
        //TODO
    }

    protected void updateItem(Account account) {
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

        if (isFollowings)
            followButton.setVisible(false);

        accountName.setText(account.getUsername());
        accountWebfinger.setText("@" + account.getAcct());

        Image avatar = new Image(account.getAvatar(), true);
        accountAvatar.setImage(avatar);

        accountItem.setOnMouseClicked(e -> MainApplication.getInstance().requestShowAccount(account.getId()));
    }

    public HBox getAccountItem() {
        return accountItem;
    }
}

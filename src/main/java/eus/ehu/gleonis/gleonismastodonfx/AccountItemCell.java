package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class AccountItemCell extends ListCell<Account> {

    public AccountItemCell(boolean isFollowings) {
        super();

        this.isFollowings = isFollowings;

    }

    private FXMLLoader loader;

    private final boolean isFollowings;

    @FXML
    private ImageView accountAvatar;

    @FXML
    private BorderPane accountItem;

    @FXML
    private Label accountName;

    @FXML
    private Label accountWebfinger;

    @FXML
    private Button followButton;

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

    @Override
    protected void updateItem(Account account, boolean empty) {
        super.updateItem(account, empty);

        if (empty || account == null) {
            setText(null);
            setGraphic(null);

            return;
        }

        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("account_item.fxml"));
            loader.setController(this);

            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isFollowings) {
            followButton.setVisible(false);
        }

        accountName.setText(account.getUsername());
        accountWebfinger.setText(account.getAcct());

        Image avatar = new Image(account.getAvatar(), true);
        accountAvatar.setImage(avatar);

        accountItem.prefWidthProperty().bind(getListView().widthProperty().subtract(40));

        setText(null);
        setGraphic(accountItem);
    }
}

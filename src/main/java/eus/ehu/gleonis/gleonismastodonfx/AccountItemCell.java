package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import javafx.scene.control.ListCell;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class AccountItemCell extends ListCell<Account> {

        @FXML
        private BorderPane accountItem;

        @FXML
        private Label accountName;

        @FXML
        private ImageView accountProfilePicture;

        @FXML
        private Label accountWebfinger;

        @FXML
        private Button followButton;

        @FXML
        private Button unfollowButton;

        @Override
        protected void updateItem(Account account, boolean empty) {
                super.updateItem(account, empty);

                if (empty || account == null) {
                        setText(null);
                        setGraphic(null);
                } else {
                        setText(null);
                        setGraphic(accountItem);
                        accountName.setText(account.getDisplayName());
                        accountWebfinger.setText(account.getAcct());
                }
        }
}

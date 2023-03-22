package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class AccountItemCell extends ListCell<Account> {

        private FXMLLoader loader;

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

                        return;
                }

                if (loader == null){
                        loader = new FXMLLoader(getClass().getResource("account_item.fxml"));
                        loader.setController(this);

                        try {
                                loader.load();
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }

                accountName.setText(account.getUsername());
                accountWebfinger.setText(account.getAcct());

                setText(null);
                setGraphic(accountItem);
        }
}

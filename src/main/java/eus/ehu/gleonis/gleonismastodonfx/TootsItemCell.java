package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

public class TootsItemCell extends ListCell<Status> {
    private FXMLLoader loader;

    @FXML
    private BorderPane messageBorder;
    @FXML
    private ImageView accountProfilePicture;

    @FXML
    private Label dateMessageLabel;

    @FXML
    private Label userLabel;

    @FXML
    private WebView userMessageLabel;

    @FXML
    private Label webfingerLabel;

    @Override
    protected void updateItem(Status status, boolean empty) {
        super.updateItem(status, empty);

        //First, we check for the empty status
        if (empty || status == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        //Then, we check if the loader is null
        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("toots.fxml"));
            loader.setController(this);

            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //We have the loader, so we can set the graphic

            userLabel.setText(status.getAccount().getDisplayName());
            webfingerLabel.setText(status.getAccount().getAcct());
            userMessageLabel.getEngine().loadContent(status.getContent());
            dateMessageLabel.setText(status.getCreated_at());
            accountProfilePicture.setImage(new Image(status.getAccount().getAvatar()));

            setText(null);
            setGraphic(messageBorder);

        }
    }
}

package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class TootsItemCell extends ListCell<Status> {
    @FXML
    private BorderPane messageBorder;
    @FXML
    private ImageView accountProfilePicture;

    @FXML
    private Label dateMessageLabel;

    @FXML
    private Label userLabel;

    @FXML
    private Label userMessageLabel;

    @FXML
    private Label webfingerLabel;

    @Override
    protected void updateItem(Status status, boolean empty) {
        super.updateItem(status, empty);

        if (empty || status == null) {
            setText(null);
            setGraphic(null);
        } else {
            setText(null);
            setGraphic(messageBorder);
            userLabel.setText(status.getAccount().getDisplayName());
            webfingerLabel.setText(status.getAccount().getAcct());
            userMessageLabel.setText(status.getContent());
            dateMessageLabel.setText(status.getCreated_at());
        }
    }
}

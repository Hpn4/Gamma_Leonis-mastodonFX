package eus.ehu.gleonis.gleonismastodonfx;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.utils.HTMLView;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;


public class TootsItemCell extends ListCell<Status> {

    public TootsItemCell() {
        super();
    }

    private FXMLLoader loader;

    @FXML
    private BorderPane messageBorder;

    @FXML
    private ImageView accountProfilePicture;

    @FXML
    private ImageView accountReblogAvatar;

    @FXML
    private Label dateMessageLabel;

    @FXML
    private Label userLabel;

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

        ///Then, we check if the loader is null
        if (loader == null) {
            loader = new FXMLLoader(getClass().getResource("toots.fxml"));
            loader.setController(this);

            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            //We have the loader, so we can set the graphic

            Status finalStatus = status;
            if(status.getReblog() != null) {
                finalStatus = status.getReblog();
                accountReblogAvatar.setImage(new Image(status.getAccount().getAvatar(), true));
            }

            userLabel.setText(finalStatus.getAccount().getDisplayName());
            webfingerLabel.setText(finalStatus.getAccount().getAcct());

            HTMLView htmlView = new HTMLView(finalStatus.getContent());

            messageBorder.setCenter(htmlView);

            dateMessageLabel.setText(Utils.getDateString(status.getCreated_at()));
            accountProfilePicture.setImage(new Image(finalStatus.getAccount().getAvatar(), true));

            messageBorder.prefWidthProperty().bind(getListView().widthProperty().subtract(30));
            messageBorder.prefHeightProperty().bind(htmlView.heightProperty().map(e -> Math.max((Double) e, 100.0)));

            setText(null);
            setGraphic(messageBorder);
        }
}

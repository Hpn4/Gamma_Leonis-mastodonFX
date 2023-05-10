package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Conversation;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Visibility;
import eus.ehu.gleonis.gleonismastodonfx.utils.HTMLView;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;


public class ConversationItem extends AbstractItem<Conversation> {

    @FXML
    private BorderPane messageBorder;

    @FXML
    private Rectangle accountProfilePicture;

    @FXML
    private Label dateMessageLabel;

    @FXML
    private Label userLabel;

    @FXML
    private Label webfingerLabel;


    // Toots interaction menu
    @FXML
    private MenuItem readConversationMenuItem;

    public ConversationItem() {
        super(null);
    }

    public void init(Conversation conv) {
        elem = conv;

        updateItem(elem);
    }

    @FXML
    void onReplyClick() {
        MainApplication.getInstance().requestShowSendToot("@" + elem.getLastStatus().getAccount().getAcct(), Visibility.DIRECT, elem.getId());
    }

    @FXML
    void onViewConvClick() {
        MainApplication.getInstance().requestShowTootContext(elem.getLastStatus());
    }

    @FXML
    void onDeleteConvClick() {
        api.removeConversation(elem.getId());
        deleteItemFromUI();
    }

    @FXML
    void onReadConvClick() {
        api.markedConversationAsRead(elem.getId());
    }

    protected void updateItem(Conversation conv) {
        Status status = conv.getLastStatus();

        accountProfilePicture.setHeight(40);
        accountProfilePicture.setWidth(40);
        status.getAccount().getAvatarCachedImage().setImage(accountProfilePicture);

        accountProfilePicture.setOnMouseClicked(e ->
                MainApplication.getInstance().requestShowAccount(status.getAccount().getId()));

        userLabel.setText(status.getAccount().getDisplayName());
        webfingerLabel.setText(status.getAccount().getAcct());
        dateMessageLabel.setText(Utils.getDateString(status.getCreated_at()));

        HTMLView htmlView = new HTMLView(status, status.getContent(), false);
        htmlView.setPadding(new Insets(10));

        messageBorder.setCenter(htmlView);

        System.out.println(conv.isUnread());
        readConversationMenuItem.setVisible(!conv.isUnread());

        // Layout constraints
        messageBorder.prefHeightProperty().bind(
                htmlView.heightProperty().map(e -> 30 + Math.max((Double) e, 40))
        );

        messageBorder.setOnMouseClicked(e -> onViewConvClick());
    }

    public BorderPane getParent() {
        return messageBorder;
    }
}

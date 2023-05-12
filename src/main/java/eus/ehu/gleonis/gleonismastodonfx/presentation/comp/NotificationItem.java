package eus.ehu.gleonis.gleonismastodonfx.presentation.comp;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Notification;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.NotificationType;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.NotificationScrollableContent;
import eus.ehu.gleonis.gleonismastodonfx.utils.CachedImage;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.HashMap;

public class NotificationItem extends AbstractItem<Notification> {

    private record TypeData(String translation, String css) {
    }

    private static final HashMap<NotificationType, TypeData> notifDatas;

    static {
        notifDatas = new HashMap<>();

        notifDatas.put(NotificationType.MENTION, new TypeData("notif.mention", "type-mention"));
        notifDatas.put(NotificationType.STATUS, new TypeData("notif.status", "type-default"));
        notifDatas.put(NotificationType.REBLOG, new TypeData("notif.reblog", "type-reblog"));
        notifDatas.put(NotificationType.FOLLOW, new TypeData("notif.follow", "type-follow"));
        notifDatas.put(NotificationType.FOLLOW_REQUEST, new TypeData("notif.follow_request", "type-follow"));
        notifDatas.put(NotificationType.FAVOURITE, new TypeData("notif.favourite", "type-favourite"));
        notifDatas.put(NotificationType.POLL, new TypeData("notif.poll", "type-default"));
        notifDatas.put(NotificationType.UPDATE, new TypeData("notif.update", "type-default"));
        notifDatas.put(NotificationType.SIGN_UP, new TypeData("notif.sign_up", "type-default"));
        notifDatas.put(NotificationType.REPORT, new TypeData("notif.report", "type-default"));
        notifDatas.put(NotificationType.UNKNOWN, new TypeData("notif.unknown", "type-default"));
    }

    private FXMLLoader loader;

    @FXML
    private GridPane rootPane;

    @FXML
    private Label notifDateLabel;

    @FXML
    private Label accountName;

    @FXML
    private Label accountWebfinger;

    @FXML
    private Rectangle notifAccountImg;

    @FXML
    private Label notifNameLabel;

    @FXML
    private Button notifTypeIcon;

    @FXML
    private Text viewTootText;

    public NotificationItem(NotificationScrollableContent nsc, Notification notif) {
        super(nsc);
        elem = notif;

        setupUI(elem);
    }

    private void setupUI(Notification notif) {
        if (loader == null) {
            loader = Utils.loadAndTranslate(MainApplication.class.getResource("notification_item.fxml"));
            loader.setController(this);

            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // We can show the button to display toots only if the notification has a toot
        viewTootText.setVisible(notif.getStatus() != null);

        Account account = notif.getAccount();

        accountName.setText(account.getUsername());
        accountWebfinger.setText("@" + account.getAcct());

        CachedImage accountAvatar = new CachedImage(null, account.getAvatar());
        accountAvatar.setImage(notifAccountImg);

        // Account redirection for the avatar of the account, the name and the webfinger
        Utils.redirectToAccount(account.getId(), notifAccountImg, accountName, accountWebfinger);

        // Date
        notifDateLabel.setText(Utils.getDateString(notif.getCreatedAt()));

        // Type of the notification
        TypeData typeData = notifDatas.get(notif.getType());

        String text = Utils.getTranslation(typeData.translation()).formatted(account.getUsername());

        notifNameLabel.setText(text);

        notifTypeIcon.getStyleClass().set(notifTypeIcon.getStyleClass().size() - 1, typeData.css());
    }

    @FXML
    void onDeleteNotifClick() {
        api.dismissNotification(elem.getId());
        deleteItemFromUI();
    }

    @FXML
    void onViewTootClick() {
        MainApplication.getInstance().requestShowTootContext(elem.getStatus());
    }

    public GridPane getParent() {
        return rootPane;
    }
}

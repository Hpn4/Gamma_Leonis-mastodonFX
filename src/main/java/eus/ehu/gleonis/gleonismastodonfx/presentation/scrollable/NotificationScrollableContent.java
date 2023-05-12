package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Notification;
import eus.ehu.gleonis.gleonismastodonfx.presentation.comp.NotificationItem;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class NotificationScrollableContent extends AbstractScrollableContent<Notification> {

    public NotificationScrollableContent(ListStream<Notification> itemsStream, int itemsPerPage) {
        super(itemsStream, itemsPerPage);

        setupContextMenu();
        setupListPropertyListener();
    }

    private void setupContextMenu() {
        API api = MainApplication.getInstance().getAPI();

        ContextMenu contextMenu = new ContextMenu();

        MenuItem clearAll = new MenuItem(Utils.getTranslation("notif.clear_all"));
        clearAll.setOnAction(event -> api.clearNotifications());

        MenuItem refresh = new MenuItem(Utils.getTranslation("notif.refresh"));
        refresh.setOnAction(event -> MainApplication.getInstance().requestShowNotification());

        contextMenu.getItems().addAll(clearAll, refresh);

        contentBox.setOnContextMenuRequested(event -> contextMenu.show(contentBox, event.getScreenX(), event.getScreenY()));
        contextMenu.setHideOnEscape(true);
        contentBox.setOnMouseClicked(event -> contextMenu.hide());
    }

    private void setupListPropertyListener() {
        Utils.mapByValue(itemsList, contentBox.getChildren(), p -> {
            Pane pane = new NotificationItem(this, p).getParent();
            pane.prefWidthProperty().bind(widthProperty().subtract(30));
            VBox.setMargin(pane, new Insets(10));

            reduceSpeed();

            return pane;
        });
    }
}

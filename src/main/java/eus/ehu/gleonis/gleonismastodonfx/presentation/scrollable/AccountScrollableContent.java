package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.scene.layout.Pane;

public class AccountScrollableContent extends AbstractScrollableContent<Account> {

    public AccountScrollableContent(ListStream<Account> itemsStream, int itemsPerPage, boolean following) {
        super(itemsStream, itemsPerPage);

        setupListPropertyListener(following);
    }

    private void setupListPropertyListener(boolean following) {
        Utils.mapByValue(itemsList, contentBox.getChildren(), p -> {
            Pane pane = new AccountItemCell(p, following).getAccountItem();
            pane.prefWidthProperty().bind(widthProperty().subtract(20));

            return pane;
        });
    }
}
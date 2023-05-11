package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Conversation;
import eus.ehu.gleonis.gleonismastodonfx.presentation.comp.ConversationItem;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class ConversationScrollableContent extends AbstractScrollableContent<Conversation> {

    private static final FXMLLoader fxml = Utils.loadAndTranslate(MainApplication.class.getResource("conversation_item.fxml"));

    public ConversationScrollableContent(ListStream<Conversation> itemsStream, int itemsPerPage) {
        super(itemsStream, itemsPerPage);

        setupListPropertyListener();
    }

    private void setupListPropertyListener() {
        Utils.mapByValue(itemsList, contentBox.getChildren(), conv -> {
            fxml.setRoot(null);
            fxml.setController(null);
            try {
                fxml.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Setup controller
            ConversationItem convItem = fxml.getController();
            convItem.init(conv);
            convItem.setScrollableContent(this);

            // Small adjustment for the width
            Pane pane = convItem.getParent();
            pane.prefWidthProperty().bind(widthProperty().subtract(20));

            // Reset the loader (i need to do it twice, i don't know why but sometimes stat are not cleared)
            fxml.setRoot(null);
            fxml.setController(null);

            reduceSpeed();

            return pane;
        });
    }
}

package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Tag;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.scene.layout.Pane;

public class TagsScrollableContent extends AbstractScrollableContent<Tag> {
    public TagsScrollableContent(ListStream<Tag> itemsStream, int itemsPerPage) {
        super(itemsStream, itemsPerPage);

        setupListPropertyListener();
    }

    private void setupListPropertyListener() {
        Utils.mapByValue(itemsList, contentBox.getChildren(), tag -> {
            HashTagItem tagItem = new HashTagItem(tag);

            Pane pane = tagItem.getHashtagItem();
            pane.prefWidthProperty().bind(widthProperty().subtract(20));

            return pane;
        });
    }
}

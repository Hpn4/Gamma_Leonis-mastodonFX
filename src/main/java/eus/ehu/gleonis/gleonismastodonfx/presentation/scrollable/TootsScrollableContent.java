package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.scene.layout.Pane;

public class TootsScrollableContent extends AbstractScrollableContent<Status> {
    public TootsScrollableContent(ListStream<Status> itemsStream, int itemsPerPage) {
        super(itemsStream, itemsPerPage);

        setupListPropertyListener();
    }

    private void setupListPropertyListener() {
        Utils.mapByValue(itemsList, contentBox.getChildren(), status -> {
            TootsItemCell toot = new TootsItemCell(status);

            Pane pane = toot.getGraphic();
            pane.prefWidthProperty().bind(widthProperty().subtract(20));

            return pane;
        });
    }
}

package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class TootsScrollableContent extends AbstractScrollableContent<Status> {

    private static final FXMLLoader fxml = new FXMLLoader(MainApplication.class.getResource("toot_item.fxml"));

    public TootsScrollableContent(ListStream<Status> itemsStream, int itemsPerPage) {
        super(itemsStream, itemsPerPage);

        setupListPropertyListener();
    }

    private void setupListPropertyListener() {
        Utils.mapByValue(itemsList, contentBox.getChildren(), status -> {
            fxml.setRoot(null);
            fxml.setController(null);
            try {
                fxml.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            TootsItemCell toot = fxml.getController();

            toot.init(status);

            Pane pane = toot.getGraphic();
            pane.prefWidthProperty().bind(widthProperty().subtract(20));

            fxml.setRoot(null);
            fxml.setController(null);

            reduceSpeed();

            return pane;
        });
    }
}

package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class TootsScrollableContent extends AbstractScrollableContent<Status> {

    private static final FXMLLoader fxml = new FXMLLoader(MainApplication.class.getResource("toot_item.fxml"), ResourceBundle.getBundle("translation", Locale.getDefault()));

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

            // Setup controller
            TootItem toot = fxml.getController();
            toot.init(status);
            toot.setScrollableContent(this);

            // Small adjustment for the width
            Pane pane = toot.getParent();
            pane.prefWidthProperty().bind(widthProperty().subtract(20));

            // Reset the loader (i need to do it twice, i don't know why but sometimes stat are not cleared)
            fxml.setRoot(null);
            fxml.setController(null);

            reduceSpeed();

            return pane;
        });
    }
}

package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Context;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ContextScrollableContent extends ScrollPane {

    private static final FXMLLoader fxml = new FXMLLoader(MainApplication.class.getResource("toot_item.fxml"));

    protected final VBox contentBox;

    private float scrollSpeed = 0.3f;

    public ContextScrollableContent(Status current, Context context) {
        super();

        contentBox = new VBox();

        for(Status ancestor : context.getAncestors())
            loadSingleToot(ancestor, "context", false);

        loadSingleToot(current, "context-selected", true);

        for(Status descendant : context.getDescendants())
            loadSingleToot(descendant, "context", false);

        setFitToWidth(true);
        setFitToHeight(true);

        setContent(contentBox);
        contentBox.getStyleClass().add("vbox-items");

        setHbarPolicy(ScrollBarPolicy.NEVER);

        setupScrollBar();
    }

    private void loadSingleToot(Status status, String style, boolean context) {
        fxml.setRoot(null);
        fxml.setController(null);
        try {
            fxml.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Setup controller
        TootItem toot = fxml.getController();
        toot.init(status, context);
        toot.setScrollableContent(null);

        // Small adjustment for the width
        Pane pane = toot.getParent();
        pane.prefWidthProperty().bind(widthProperty().subtract(20));
        pane.getStyleClass().add(style);

        // Reset the loader (i need to do it twice, i don't know why but sometimes stat are not cleared)
        fxml.setRoot(null);
        fxml.setController(null);

        reduceSpeed();

        contentBox.getChildren().add(pane);
    }

    private void setupScrollBar() {
        addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaY() != 0) {
                event.consume(); //prevent the default scrolling

                double pos = getVvalue();
                pos += event.getDeltaY() < 0 ? scrollSpeed : -scrollSpeed;

                pos = pos < 0 ? 0 : pos; //prevent to be negative
                pos = Math.min(pos, getVmax()); //prevent to be over max

                setVvalue(pos);
            }
        });
    }

    private void reduceSpeed() {
        scrollSpeed -= scrollSpeed / 13.5f; //TODO improve this pure magic number
    }
}

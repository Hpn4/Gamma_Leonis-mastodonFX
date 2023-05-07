package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public abstract class AbstractScrollableContent<E> extends ScrollPane {

    protected final VBox contentBox;

    private final ListStream<E> itemsStream;

    private final int itemsPerPage;

    protected ObservableList<E> itemsList;

    private float scrollSpeed = 0.3f;

    public AbstractScrollableContent(ListStream<E> itemsStream, int itemsPerPage) {
        super();

        contentBox = new VBox();

        this.itemsStream = itemsStream;
        itemsList = itemsStream.getElement();
        this.itemsPerPage = itemsPerPage;

        setFitToWidth(true);
        setFitToHeight(true);

        setContent(contentBox);
        contentBox.getStyleClass().add("vbox-items");

        setHbarPolicy(ScrollBarPolicy.NEVER);

        setupScrollBar();
    }

    private void setupScrollBar() {
        vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                Platform.runLater(() -> itemsStream.getNextElements(itemsPerPage));
            }
        });

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

    public void deleteItem(E toDelete) {
        itemsList.remove(toDelete);
        layout();
    }

    public void reduceSpeed() {
        scrollSpeed -= scrollSpeed / 13.5f; //TODO improve this pure magic number
    }
}

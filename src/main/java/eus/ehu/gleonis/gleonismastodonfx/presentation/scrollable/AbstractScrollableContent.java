package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class AbstractScrollableContent<E> extends ScrollPane {

    protected final VBox contentBox;

    protected ObservableList<E> itemsList;

    private final ListStream<E> itemsStream;

    private final int itemsPerPage;

    public AbstractScrollableContent(ListStream<E> itemsStream, int itemsPerPage) {
        super();

        contentBox = new VBox();

        this.itemsStream = itemsStream;
        itemsList = itemsStream.getElement();
        this.itemsPerPage = itemsPerPage;

        setFitToWidth(true);
        setFitToHeight(true);

        setContent(contentBox);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.getStyleClass().add("vbox-items");

        setHbarPolicy(ScrollBarPolicy.NEVER);
        contentBox.prefWidthProperty().bind(widthProperty());

        setupScrollBar();
    }

    private void setupScrollBar() {
        vvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == 1.0) {
                Platform.runLater(() -> {
                    List<E> nextElements = itemsStream.getNextElements(itemsPerPage);
                    if (nextElements != null)
                        itemsList.addAll(nextElements);
                });
            }
        });
    }

    public void addToBorderPane(BorderPane borderPane) {
        borderPane.setCenter(this);
        BorderPane.setAlignment(this, Pos.TOP_CENTER);
    }
}

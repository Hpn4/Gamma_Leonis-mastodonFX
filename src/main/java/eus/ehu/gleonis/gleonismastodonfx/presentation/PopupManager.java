package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;

public class PopupManager {

    private final static MainApplication instance;

    private final static HashMap<Integer, Popup> popups;

    private static Integer counter;

    private final static String css;

    static {
        instance = MainApplication.getInstance();
        counter = 0;
        popups = new HashMap<>();

        URL url = MainApplication.class.getResource("css/popup.css");
        css = url != null ? url.toExternalForm() : null;
    }

    public static int showProgressPopup(String msg, ReadOnlyDoubleProperty progressProperty) {
        VBox vBox = new VBox(10);
        vBox.getStyleClass().add("popup");
        vBox.getStylesheets().add(css);
        vBox.getChildren().add(new Label(msg));

        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0.5);
        progressBar.setMinWidth(300);
        progressBar.progressProperty().bind(progressProperty);

        vBox.getChildren().add(progressBar);

        Popup popup = new Popup();
        popup.getContent().setAll(vBox);

        Stage stage = instance.getStage();
        popup.show(stage, stage.getX() + 10, stage.getY() + stage.getHeight() - 73);

        popups.put(++counter, popup);

        return counter;
    }

    public static void hideProgressPopup(Integer counter) {
        popups.remove(counter).hide();
    }
}

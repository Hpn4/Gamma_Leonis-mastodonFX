package eus.ehu.gleonis.gleonismastodonfx.presentation;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class MainWindowController {

    @FXML
    private Circle accountImage;

    @FXML
    private Label accountUsername;

    @FXML
    private Label accountWebfinger;

    @FXML
    private ToggleGroup menuChoice;

    @FXML
    private BorderPane sceneContent;


    public void setCenter(Pane pane) {
        BorderPane.setAlignment(pane, Pos.TOP_CENTER);

        sceneContent.setCenter(pane);
    }
}

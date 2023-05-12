package eus.ehu.gleonis.gleonismastodonfx.presentation.comp;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class MediaAttachmentViewer extends FlowPane {

    private final ScrollPane parent;

    public MediaAttachmentViewer(ScrollPane parent) {
        super();

        this.parent = parent;
        parent.setContent(this);

        setHgap(5);
        setVgap(5);
    }

    public void clearMedia() {
        this.getChildren().clear();
    }

    public void addMedia(File file, List<File> mediaList) {
        StackPane mediaPane = new StackPane();
        mediaPane.setPrefSize(100, 100);
        mediaPane.setStyle("-fx-background-color: black;");

        // Check whether the file is an image or not
        boolean image = false;
        try {
            image = Files.probeContentType(file.toPath()).startsWith("image");
        } catch (IOException ignore) {
        }

        if (image) {
            Image img = new Image(file.toURI().toString(), true);
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(100);
            imgView.setPreserveRatio(true);

            mediaPane.getChildren().add(imgView);
        } else {
            Label name = new Label(file.getName());
            name.setTextAlignment(TextAlignment.CENTER);
            name.setStyle("-fx-text-fill: #CCCCCC;");
            name.setWrapText(true);

            mediaPane.getChildren().add(name);
        }

        mediaPane.setOnMouseClicked(event -> {
            mediaList.remove(file);
            getChildren().remove(mediaPane);
            if (getChildren().size() == 0) {
                parent.setPrefHeight(0);
            }
        });

        parent.setPrefHeight(110);

        getChildren().add(mediaPane);
    }
}

package eus.ehu.gleonis.gleonismastodonfx.presentation.comp;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.MediaAttachment;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.MediaAttachmentType;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class VideoPlayerNode extends BorderPane {

    private final MediaPlayerNode player;

    private final boolean gif;

    public VideoPlayerNode(MediaAttachment media) {
        super();
        setStyle("-fx-background-color: black");

        gif = media.getType() == MediaAttachmentType.GIFV;
        player = new MediaPlayerNode(media, true, this::reloadMediaView);

        setBottom(player);

        setupVideoPane();
    }

    private void setupVideoPane() {
        if(player == null)
            return;

        setBottom(player);
    }

    private void reloadMediaView(MediaPlayer mediaPlayer) {
        if(gif)
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        getChildren().clear();

        MediaView video = new MediaView(mediaPlayer);
        video.setOnError(e -> player.reloadMediaPlayer());
        video.fitHeightProperty().bind(widthProperty().map(w -> w.doubleValue() * 9 / 16)); // 16:9 aspect ratio

        setCenter(video);
        setupVideoPane();
    }
}

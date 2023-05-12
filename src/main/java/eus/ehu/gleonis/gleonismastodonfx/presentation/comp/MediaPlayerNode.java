package eus.ehu.gleonis.gleonismastodonfx.presentation.comp;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.MediaAttachment;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MediaPlayerNode extends GridPane {

    private final static List<MediaPlayer> loadedMedias = new ArrayList<>();
    private final Media media;
    private final Slider volumeSlider;
    private final Button playButton;
    private final Label timeLabel;
    private MediaPlayer mediaPlayer;
    private Consumer<MediaPlayer> onLoaded;

    public MediaPlayerNode(MediaAttachment mediaAttachment, boolean video) {
        this(mediaAttachment, video, null);
    }

    public MediaPlayerNode(MediaAttachment mediaAttachment, boolean video, Consumer<MediaPlayer> onLoaded) {
        super();

        setOnLoaded(onLoaded);

        getStylesheets().add(MainApplication.class.getResource("css/audio_player.css").toExternalForm());
        getStyleClass().add(video ? "video-player" : "audio-player");

        double duration = mediaAttachment.getDuration();

        // All UI elements
        media = new Media(mediaAttachment.getUrl());
        media.setOnError(this::reloadMediaPlayer);
        volumeSlider = new Slider(0, duration, 0);
        playButton = new Button("");
        timeLabel = new Label("00:00:00");

        reloadMediaPlayer();

        // Setup play button
        playButton.getStyleClass().add("play-button");
        playButton.setOnAction(event -> {
            if (mediaPlayer.getError() != null || mediaPlayer.getStatus() == MediaPlayer.Status.HALTED)
                reloadMediaPlayer();

            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING)
                mediaPlayer.pause();
            else
                onPlay();
        });

        // Setup grid pane
        setHgap(10);

        getColumnConstraints().addAll(new ColumnConstraints(30), new ColumnConstraints(60), new ColumnConstraints(), new ColumnConstraints(60));

        addColumn(0, playButton);
        addColumn(1, timeLabel);
        addColumn(2, volumeSlider);
        addColumn(3, new Label(formatTime(duration)));

        GridPane.setHgrow(volumeSlider, javafx.scene.layout.Priority.ALWAYS);
    }

    public static void freeAndStopMedias() {
        loadedMedias.forEach(e -> {
            e.stop();
            e.dispose();
        });
        loadedMedias.clear();
    }

    public void reloadMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            loadedMedias.remove(mediaPlayer);
        }

        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnError(this::reloadMediaPlayer);
        loadedMedias.add(mediaPlayer);

        if (onLoaded != null)
            onLoaded.accept(mediaPlayer);

        mediaPlayer.setOnPaused(() -> playButton.getStyleClass().set(1, "play-button"));

        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double time = newValue.toSeconds();
            volumeSlider.setValue(time);
            timeLabel.setText(formatTime(time));
        });

        volumeSlider.setOnMousePressed(event -> mediaPlayer.pause());

        volumeSlider.setOnMouseReleased(event -> {
            if (mediaPlayer.getError() != null)
                reloadMediaPlayer();

            mediaPlayer.seek(new Duration(volumeSlider.getValue() * 1000));
            onPlay();
        });
    }

    private void onPlay() {
        // Pause others media players
        loadedMedias.stream().filter(e -> e != mediaPlayer).forEach(MediaPlayer::pause);

        mediaPlayer.play();
        playButton.getStyleClass().set(1, "pause-button");
    }

    private String formatTime(double time) {
        int seconds = (int) time % 60;
        int minutes = (int) time / 60;
        int hours = minutes / 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public void setOnLoaded(Consumer<MediaPlayer> onLoaded) {
        this.onLoaded = onLoaded;
    }
}

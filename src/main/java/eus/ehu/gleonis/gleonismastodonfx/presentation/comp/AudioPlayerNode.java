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

public class AudioPlayerNode extends GridPane {

    private MediaPlayer mediaPlayer;

    private final Label timeLabel;

    public AudioPlayerNode(MediaAttachment mediaAttachment) {
        super();

        getStylesheets().add(MainApplication.class.getResource("css/audio_player.css").toExternalForm());
        getStyleClass().add("audio-player");

        double duration = mediaAttachment.getDuration();

        // All UI elements
        Media media = new Media(mediaAttachment.getUrl());
        Slider volumeSlider = new Slider(0, duration, 0);
        Button playButton = new Button("");
        timeLabel = new Label("00:00:00");

        mediaPlayer = new MediaPlayer(media);
        setupMediaPlayer(volumeSlider, playButton);

        // Setup play button
        playButton.getStyleClass().add("play-button");
        playButton.setOnAction(event -> {
            if (mediaPlayer.getError() != null) {
                mediaPlayer = new MediaPlayer(media);
                setupMediaPlayer(volumeSlider, playButton);
            }

            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playButton.getStyleClass().set(1, "play-button");
            } else {
                mediaPlayer.play();
                playButton.getStyleClass().set(1, "pause-button");
            }
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

    private String formatTime(double time) {
        int seconds = (int) time % 60;
        int minutes = (int) time / 60;
        int hours = minutes / 60;
        minutes = minutes % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void setupMediaPlayer(Slider volumeSlider, Button playButton) {
        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            double time = newValue.toSeconds();
            volumeSlider.setValue(time);
            timeLabel.setText(formatTime(time));
        });

        volumeSlider.setOnMousePressed(event -> mediaPlayer.pause());

        volumeSlider.setOnMouseReleased(event -> {
            if (mediaPlayer.getError() != null) {
                mediaPlayer = new MediaPlayer(mediaPlayer.getMedia());
                setupMediaPlayer(volumeSlider, playButton);
            }

            mediaPlayer.seek(new Duration(volumeSlider.getValue() * 1000));
            mediaPlayer.play();
            playButton.getStyleClass().set(1, "pause-button");
        });

    }
}

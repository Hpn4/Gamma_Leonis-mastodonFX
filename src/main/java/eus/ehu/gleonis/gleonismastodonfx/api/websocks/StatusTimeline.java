package eus.ehu.gleonis.gleonismastodonfx.api.websocks;

import com.google.gson.Gson;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import javafx.application.Platform;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class StatusTimeline {

    private static final Logger logger = LogManager.getLogger("StatusTimeline");

    private final Gson gson;

    private WebSocket webSocket;

    private StatusListener onNewStatus;

    private StatusListener onStatusDeleted;

    private StatusListener onStatusUpdated;


    public StatusTimeline(Gson gson) {
        this.gson = gson;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public void closeStream() {
        if (webSocket != null) {
            // 1001: Go away. Ref:https://datatracker.ietf.org/doc/html/rfc6455#section-7.4
            webSocket.close(1001, "Close");
            webSocket = null;
        }
    }

    public WebSocketListener buildWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                logger.debug("Websockets closed");
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                logger.error("Error on WebSocket", t);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                super.onMessage(webSocket, text);
                onReceive(text);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
                super.onMessage(webSocket, bytes);
                onReceive(bytes.toString());
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                super.onOpen(webSocket, response);
                logger.debug("Websockets open");
            }

            private void onReceive(String text) {
                StreamEvent event = gson.fromJson(text, StreamEvent.class);

                switch (event.event) {
                    case "update" -> {
                        if (onNewStatus != null)
                            Platform.runLater(() -> onNewStatus.onStatus(gson.fromJson(event.payload, Status.class)));
                    }
                    case "delete" -> {
                        if (onStatusDeleted != null)
                            Platform.runLater(() -> onStatusDeleted.onStatus(gson.fromJson(event.payload, Status.class)));
                    }
                    case "status.update" -> {
                        if (onStatusUpdated != null)
                            Platform.runLater(() -> onStatusUpdated.onStatus(gson.fromJson(event.payload, Status.class)));
                    }
                    default -> {
                    }
                }
            }
        };
    }

    public void setOnNewStatus(StatusListener onNewStatus) {
        this.onNewStatus = onNewStatus;
    }

    public void setOnStatusDeleted(StatusListener onStatusDeleted) {
        this.onStatusDeleted = onStatusDeleted;
    }

    public void setOnStatusUpdated(StatusListener onStatusUpdated) {
        this.onStatusUpdated = onStatusUpdated;
    }
}

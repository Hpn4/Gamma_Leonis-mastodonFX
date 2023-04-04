package eus.ehu.gleonis.gleonismastodonfx.api.websocks;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class StatusTimeline {

    private StatusListener onNewStatus;

    private StatusListener onStatusDeleted;

    private StatusListener onStatusUpdated;

    public StatusTimeline() {
    }

    public WebSocketListener buildWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
            }

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
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

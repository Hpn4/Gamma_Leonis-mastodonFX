package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

import java.util.Map;

public class MediaAttachment {

    String id;

    MediaAttachmentType type;

    String url;

    String remote_url;

    String description;

    String blurhash;

    Map<?, ?> meta;

    public String getId() {
        return id;
    }

    public MediaAttachmentType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public String getRemote_url() {
        return remote_url;
    }

    public String getDescription() {
        return description;
    }

    public String getBlurhash() {
        return blurhash;
    }

    private double getMetaDouble(String key) {
        if (meta.containsKey(key))
            return (double) meta.get(key);

        if (meta.containsKey("original")) {
            Map<?, ?> original = (Map<?, ?>) meta.get("original");

            if (original.containsKey(key))
                return (double) original.get(key);
        }

        return 0;
    }

    public int getWidth() {
        return (int) getMetaDouble("width");
    }

    public int getHeight() {
        return (int) getMetaDouble("height");
    }

    public double getDuration() {
        return getMetaDouble("duration");
    }

    public void setId(String id) {
        this.id = id;
    }
}

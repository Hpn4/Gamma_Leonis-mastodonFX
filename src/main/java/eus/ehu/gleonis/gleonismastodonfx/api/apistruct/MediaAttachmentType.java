package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public enum MediaAttachmentType {

    UNKNOWN("unknown"),
    IMAGE("image"),
    GIFV("gifv"),
    VIDEO("video"),
    AUDIO("audio");

    private final String type;

    MediaAttachmentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public enum StatusVisibility {
    PUBLIC("public"),
    UNLISTED("unlisted"),
    PRIVATE("private"),
    DIRECT("direct");

    private final String value;

    StatusVisibility(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public enum Visibility {
    PUBLIC("public"),
    UNLISTED("unlisted"),
    PRIVATE("private"),
    DIRECT("direct"),

    UNKNOWN("unknown");

    final String visibility;

    Visibility(String visibility) {
        this.visibility = visibility;
    }

    public String getVisibility() {
        return visibility;
    }
}

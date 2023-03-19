package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public enum NotificationType {

    MENTION("mention"), // You were mentioned in a status
    STATUS("status"), // Someone you follow posted a status
    REBLOG("reblog"), // One of your statuses was boosted
    FOLLOW("follow"), // Someone followed you

    FOLLOW_REQUEST("follow_request"), // Someone requested to follow you
    FAVOURITE("favourite"), // Someone favourited one of your statuses

    POLL("poll"), // A poll you voted in has ended
    UPDATE("update"), // A status you interacted with has been updated

    SIGN_UP("admin.sign_up"), // A new user has signed up for the instance

    REPORT("admin.report"); // A status has been reported

    String type;

    NotificationType(String type) {
        this.type = type;
    }

    public boolean isReport() {
        return this == REPORT;
    }

    public boolean hasStatusAttached() {
        return this == MENTION || this == STATUS || this == REBLOG || this == FAVOURITE || this == POLL || this == UPDATE;
    }
}

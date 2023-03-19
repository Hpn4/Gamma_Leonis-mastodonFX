package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public class Notification implements Identifiable {

    String id;

    NotificationType type;

    String created_at;

    Account account;

    Status status;

    public String getId() {
        return id;
    }

    public NotificationType getType() {
        return type;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public Account getAccount() {
        return account;
    }

    public Status getStatus() {
        return status;
    }
}

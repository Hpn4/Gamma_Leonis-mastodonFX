package eus.ehu.gleonis.gleonismastodonfx.db;

public class DBAccount {

    private String id;

    private String username;

    private String avatarUrl;

    private String last_access;

    private String token;

    public DBAccount(String id, String username, String avatarUrl, String last_access, String token) {
        this.id = id;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.last_access = last_access;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getLast_access() {
        return last_access;
    }

    public String getToken() {
        return token;
    }
}

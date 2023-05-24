package eus.ehu.gleonis.gleonismastodonfx.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"DBAccount\"")
public class DBAccount {

    @Id
    private String id;

    private String appDomain; //TODO set this as a foreign key

    private String username;

    private String avatarUrl;

    private String last_access;

    private String token;

    public DBAccount() {
    }

    public DBAccount(String id, String appDomain, String username, String avatarUrl, String last_access, String token) {
        this.id = id;
        this.appDomain = appDomain;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.last_access = last_access;
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppDomain() {
        return appDomain;
    }

    public void setAppDomain(String appDomain) {
        this.appDomain = appDomain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLast_access() {
        return last_access;
    }

    public void setLast_access(String last_access) {
        this.last_access = last_access;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

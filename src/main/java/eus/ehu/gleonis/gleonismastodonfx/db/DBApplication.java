package eus.ehu.gleonis.gleonismastodonfx.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "\"DBApplication\"")
public class DBApplication {

    @Id
    private String domain;

    private String id;

    private String secret;

    public DBApplication() {}

    public DBApplication(String domain, String id, String secret) {
        this.domain = domain;
        this.id = id;
        this.secret = secret;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}

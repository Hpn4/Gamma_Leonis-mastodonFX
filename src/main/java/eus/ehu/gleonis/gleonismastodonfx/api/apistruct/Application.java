package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public class Application {

    String client_id;

    String client_secret;

    public Application(String client_id, String client_secret) {
        this.client_id = client_id;
        this.client_secret = client_secret;
    }

    public String getClientId() {
        return client_id;
    }

    public String getClientSecret() {
        return client_secret;
    }

    public String toString() {
        return "Application{" +
                "client_id='" + client_id + '\'' +
                ", client_secret='" + client_secret + '\'' +
                '}';
    }
}

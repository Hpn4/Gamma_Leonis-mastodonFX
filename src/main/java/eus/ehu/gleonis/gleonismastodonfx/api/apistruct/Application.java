package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public class Application {

    String name;
    String website;
    String vapid_key;
    String client_id;
    String client_secret;

    public String getName() {
        return name;
    }

    public String getWebsite() {
        return website;
    }

    public String getVapidKey() {
        return vapid_key;
    }

    public String getClientId() {
        return client_id;
    }

    public String getClientSecret() {
        return client_secret;
    }

    public String toString() {
        return "Application{" +
                "name='" + name + '\'' +
                ", website='" + website + '\'' +
                ", vapid_key='" + vapid_key + '\'' +
                ", client_id='" + client_id + '\'' +
                ", client_secret='" + client_secret + '\'' +
                '}';
    }
}

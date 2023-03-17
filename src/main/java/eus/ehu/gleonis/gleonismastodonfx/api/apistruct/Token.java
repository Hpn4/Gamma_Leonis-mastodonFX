package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public class Token {

    String access_token;

    String scope;

    String created_at;

    public String getAccessToken() {
        return access_token;
    }

    public String toString() {
        return "Token{" +
                "access_token='" + access_token + '\'' +
                ", scope='" + scope + '\'' +
                ", created_at=" + created_at +
                '}';
    }
}

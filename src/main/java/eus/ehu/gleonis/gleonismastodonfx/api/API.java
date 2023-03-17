package eus.ehu.gleonis.gleonismastodonfx.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import eus.ehu.gleonis.gleonismastodonfx.api.adapter.MediaAttachmentTypeDeserializer;
import eus.ehu.gleonis.gleonismastodonfx.api.adapter.VisibilityDeserializer;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.*;
import okhttp3.*;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class API {

    private final Gson gson;
    private String token;

    public API() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Visibility.class, new VisibilityDeserializer());
        builder.registerTypeAdapter(MediaAttachmentType.class, new MediaAttachmentTypeDeserializer());

        gson = builder.create();
    }


    //*******************************************************************
    // Utilitary methods for request building and execution.
    //
    // *******************************************************************
    private String request(String endpoint) {
        return request(endpoint, null);
    }

    private String request(String endpoint, RequestBody body) {
        String result = "";

        OkHttpClient client = new OkHttpClient();

        Request.Builder builder = new Request.Builder()
                .url("https://mastodon.social/" + endpoint);

        if (token != null)
            builder.addHeader("Authorization", "Bearer " + token);

        if (body != null)
            builder.post(body);
        else
            builder.get();

        Request request = builder.build();

        try {
            Response response = client.newCall(request).execute();

            if (response.code() == 200)
                result = response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


    //*******************************************************************
    // OAuth methods to authorize user, get and revoke tokens.
    //
    // The autizeUser method opens the browser to the authorization page of mastodon
    // then the user will connect to mastodon and allow or not the application to
    // access to his account. If he accept, he will receive a code.
    //
    // This code then needs to be passed to the function getToken().
    // It will return a Token object with the access token and other information.
    // The access token is then used to setup the API with setupAPI().
    // *******************************************************************
    public void authorizeUser(Application app) {
        String uri = "https://mastodon.social/oauth/authorize?response_type=code&client_id=" +
                app.getClientId() + "&redirect_uri=urn:ietf:wg:oauth:2.0:oob&scope=read+write+push+follow";

        try {
            Desktop.getDesktop().browse(new URI(uri));
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Token getToken(Application app, String code) {
        RequestBody body = new FormBody.Builder()
                .add("client_id", app.getClientId())
                .add("client_secret", app.getClientSecret())
                .add("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("scope", "read write push follow")
                .build();

        String req = request("oauth/token", body);

        return gson.fromJson(req, Token.class);
    }

    public void revokeToken(Application app, Token token) {
        RequestBody body = new FormBody.Builder()
                .add("client_id", app.getClientId())
                .add("client_secret", app.getClientSecret())
                .add("token", token.getAccessToken())
                .build();

        request("oauth/revoke", body);
    }

    public void setupAPI(Token token) {
        this.token = token.getAccessToken();
    }


    //*******************************************************************
    // Account methods
    //
    // *******************************************************************
    public Account getAccount(String id) {
        String req = request("api/v1/accounts/" + id);

        return gson.fromJson(req, Account.class);
    }

    public List<Status> getAccountStatuses(Account account) {
        return getAccountStatuses(account.getId());
    }

    public List<Status> getAccountStatuses(String id) {
        String req = request("api/v1/accounts/" + id + "/statuses");

        JsonArray jsonArray = gson.fromJson(req, JsonArray.class);
        Type statusListType = new TypeToken<List<Status>>() {}.getType();

        return gson.fromJson(jsonArray, statusListType);
    }
}

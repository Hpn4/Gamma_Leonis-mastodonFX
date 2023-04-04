package eus.ehu.gleonis.gleonismastodonfx.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import eus.ehu.gleonis.gleonismastodonfx.PropertiesManager;
import eus.ehu.gleonis.gleonismastodonfx.api.adapter.MediaAttachmentTypeDeserializer;
import eus.ehu.gleonis.gleonismastodonfx.api.adapter.NotificationTypeDeserializer;
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

    private final OkHttpClient client;

    private String token;

    private APIError error;

    public API() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Visibility.class, new VisibilityDeserializer());
        builder.registerTypeAdapter(MediaAttachmentType.class, new MediaAttachmentTypeDeserializer());
        builder.registerTypeAdapter(NotificationType.class, new NotificationTypeDeserializer());

        gson = builder.create();
        client = new OkHttpClient();

        token = PropertiesManager.getInstance().getToken();
    }

    public boolean errorOccurred() {
        return error != null;
    }

    public APIError getError() {
        return error;
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

        return postSingle("oauth/token", Token.class, body);
    }

    public void revokeToken(Application app, Token token) {
        RequestBody body = new FormBody.Builder()
                .add("client_id", app.getClientId())
                .add("client_secret", app.getClientSecret())
                .add("token", token.getAccessToken())
                .build();

        request("oauth/revoke", "POST", body);
    }

    public void setupToken(Token token) {
        this.token = token.getAccessToken();
    }


    //*******************************************************************
    // Account methods:
    // - getAccount
    // - getAccountStatuses: return all statuses posted by the account
    // - getAccountFollowers: return all followers of the account
    // - getAccountFollowing: return all accounts followed by the account
    // - followAccount: follow a given account. Parameters are: id of the account to follow, receiveReblogs and notify
    // - unfollowAccount: unfollow a given account.
    // - blockAccount: block a given account.
    // - unblockAccount: unblock a given account.
    // - muteAccount: mute a given account.
    // - unmuteAccount: unmute a given account.
    // - getBookmarks: return all bookmarks of the account.
    // - getFavourites: return all favourites of the account.
    // - getFollowRequests: return all follow requests of the account.
    // - authorizeFollowRequest: authorize a follow request.
    // - rejectFollowRequest: reject a follow request.
    // - getSuggestions: return all suggestions of the account.
    // - removeSuggestion: remove a suggestion.
    //
    // *******************************************************************

    /**
     * @return The account of the connected user
     */
    public Account verifyCredentials() {
        return getSingle("api/v1/accounts/verify_credentials", Account.class);
    }

    /**
     * @param id The id of the account to get
     * @return The account with the given id
     */
    public Account getAccount(String id) {
        return getSingle("api/v1/accounts/" + id, Account.class);
    }

    public ListStream<Status> getAccountStatuses(String id, int limit) {
        return getStream("api/v1/accounts/" + id + "/statuses", limit, Status.class);
    }

    public ListStream<Status> getBookmarks(int limit) {
        return getStream("api/v1/bookmarks", limit, Status.class);
    }

    public ListStream<Status> getFavourites(int limit) {
        return getStream("api/v1/favourites", limit, Status.class);
    }

    public ListStream<Account> getAccountFollowers(String id, int limit) {
        return getStream("api/v1/accounts/" + id + "/followers", limit, Account.class);
    }

    public ListStream<Account> getAccountFollowing(String id, int limit) {
        return getStream("api/v1/accounts/" + id + "/following", limit, Account.class);
    }

    public ListStream<Relationship> getRelationships(String... ids) {
        StringBuilder baseUrl = new StringBuilder("api/v1/accounts/relationships?");
        baseUrl.append("id=").append(ids[0]);
        for (String id : ids)
            baseUrl.append("&id=").append(id);

        return getStream(baseUrl.toString(), 0, Relationship.class);
    }

    public Relationship followAccount(String id) {
        return followAccount(id, true, false);
    }

    public Relationship followAccount(String id, boolean receiveReblogs, boolean notify) {
        RequestBody body = new FormBody.Builder()
                .add("reblogs", receiveReblogs ? "true" : "false")
                .add("notify", notify ? "true" : "false")
                .build();

        return postSingle("api/v1/accounts/" + id + "/follow", Relationship.class, body);
    }

    public Relationship unfollowAccount(String id) {
        return postSingle("api/v1/accounts/" + id + "/unfollow", Relationship.class);
    }

    public Relationship removeAccountFromFollowers(String id) {
        return postSingle("api/v1/accounts/" + id + "/remove_from_followers", Relationship.class);
    }

    public Relationship blockAccount(String id) {
        return postSingle("api/v1/accounts/" + id + "/block", Relationship.class);
    }

    public Relationship unblockAccount(String id) {
        return postSingle("api/v1/accounts/" + id + "/unblock", Relationship.class);
    }

    public Relationship muteAccount(String id) {
        return muteAccount(id, true, 0);
    }

    public Relationship muteAccount(String id, boolean muteNotifications, int duration) {
        RequestBody body = new FormBody.Builder()
                .add("notifications", muteNotifications ? "true" : "false")
                .add("duration", String.valueOf(duration))
                .build();

        return postSingle("api/v1/accounts/" + id + "/mute", Relationship.class, body);
    }

    public Relationship unmuteAccount(String id) {
        return postSingle("api/v1/accounts/" + id + "/unmute", Relationship.class);
    }

    //*******************************************************************
    // Follow request methods and follow suggestion
    //
    // *******************************************************************
    public ListStream<Account> getFollowRequests(int limit) {
        return getStream("api/v1/follow_requests", limit, Account.class);
    }

    public Relationship authorizeFollowRequest(String id) {
        return postSingle("api/v1/follow_requests/" + id + "/authorize", Relationship.class);
    }

    public Relationship rejectFollowRequest(String id) {
        return postSingle("api/v1/follow_requests/" + id + "/reject", Relationship.class);
    }

    public ListStream<Suggestion> getSuggestions() {
        return getStream("api/v1/suggestions", 40, Suggestion.class);
    }

    public void removeSuggestion(String id) {
        deleteRequest("api/v1/suggestions/" + id);
    }

    //*******************************************************************
    // Statuses methods
    //
    // Remaining one to implement but not very useful:
    // - getStatusContext
    // - getStatusCard
    // - muteConversation and unmuteConversation
    // - pinStatus and unpinStatus
    // - editStatus
    // - viewEditHistory
    // - viewStatusSource
    // *******************************************************************
    public Status postStatus(String status, String inReplyToId, String mediaIds, boolean sensitive, String spoilerText, String visibility) {
        RequestBody body = new FormBody.Builder()
                .add("status", status)
                .add("in_reply_to_id", inReplyToId)
                .add("media_ids", mediaIds)
                .add("sensitive", sensitive ? "true" : "false")
                .add("spoiler_text", spoilerText)
                .add("visibility", visibility)
                .build();

        return postSingle("api/v1/statuses", Status.class, body);
    }

    public Status getStatus(String id) {
        return getSingle("api/v1/statuses/" + id, Status.class);
    }

    public Status deleteStatus(String id) {
        return deleteSingle("api/v1/statuses/" + id, Status.class);
    }

    public ListStream<Account> getStatusRebloggedBy(String id, int limit) {
        return getStream("api/v1/statuses/" + id + "/reblogged_by", limit, Account.class);
    }

    public ListStream<Account> getStatusFavouritedBy(String id, int limit) {
        return getStream("api/v1/statuses/" + id + "/favourited_by", limit, Account.class);
    }

    public Status favouriteStatus(String id) {
        return postSingle("api/v1/statuses/" + id + "/favourite", Status.class);
    }

    public Status unfavouriteStatus(String id) {
        return postSingle("api/v1/statuses/" + id + "/unfavourite", Status.class);
    }

    public Status reblogStatus(String id) {
        return postSingle("api/v1/statuses/" + id + "/reblog", Status.class);
    }

    public Status unreblogStatus(String id) {
        return postSingle("api/v1/statuses/" + id + "/unreblog", Status.class);
    }

    public Status bookmarkStatus(String id) {
        return postSingle("api/v1/statuses/" + id + "/bookmark", Status.class);
    }

    public Status unbookmarkStatus(String id) {
        return postSingle("api/v1/statuses/" + id + "/unbookmark", Status.class);
    }


    //*******************************************************************
    // Timelines methods:
    // - getPublicTimelines
    // - getHashTagTimelines: all status that have the given hashtag (without the #)
    // - getHomeTimeline
    //
    // *******************************************************************
    public ListStream<Status> getPublicTimelines(int limit, boolean onlyLocal, boolean onlyRemote) {
        return getStream("api/v1/timelines/public?local=" + onlyLocal + "&remote=" + onlyRemote, limit, Status.class);
    }

    public ListStream<Status> getHashTagTimelines(String tag, int limit) {
        return getStream("api/v1/timelines/tag/" + tag, limit, Status.class);
    }

    public ListStream<Status> getHomeTimeline(int limit) {
        return getStream("api/v1/timelines/home", limit, Status.class);
    }

    //*******************************************************************
    // Conversations methods:
    // - getConversations
    // - removeConversation
    // - markedConversationAsRead
    //
    // *******************************************************************
    public ListStream<Conversation> getConversations(int limit) {
        return getStream("api/v1/conversations", limit, Conversation.class);
    }

    public void removeConversation(String id) {
        deleteRequest("api/v1/conversations/" + id);
    }

    public Conversation markedConversationAsRead(String id) {
        return postSingle("api/v1/conversations/" + id + "/read", Conversation.class);
    }

    //*******************************************************************
    // Notification methods:
    //
    // *******************************************************************
    public ListStream<Notification> getNotifications(int limit) {
        return getStream("api/v1/notifications", limit, Notification.class);
    }

    public Notification getNotification(String id) {
        return getSingle("api/v1/notifications/" + id, Notification.class);
    }

    public void clearNotifications() {
        postSingle("api/v1/notifications/clear", Notification.class);
    }

    public void dismissNotification(String id) {
        postSingle("api/v1/notifications/" + id + "/dismiss", Notification.class);
    }


    //*******************************************************************
    // Web sockets
    //
    // *******************************************************************


    //*******************************************************************
    // Stream Utils methods
    //
    // *******************************************************************
    protected <E> List<E> updateStream(String baseUrl, int limit, Class<E> objClass, ListStream<E> listStream) {
        String url = baseUrl + (baseUrl.contains("?") ? "&" : "?") + "limit=" + limit;

        RequestResult requestResult = getRequest(url);
        listStream.parsePaginationLink(requestResult.getPaginationLink());
        if (errorOccurred())
            return null;

        JsonArray jsonArray = gson.fromJson(requestResult.getResponse(), JsonArray.class);
        Type statusList = TypeToken.getParameterized(List.class, objClass).getType();

        return gson.fromJson(jsonArray, statusList);
    }

    private <E> ListStream<E> getStream(String baseUrl, int limit, Class<E> objClass) {
        String url = baseUrl + (baseUrl.contains("?") ? "&" : "?") + "limit=" + limit;

        RequestResult requestResult = getRequest(url);
        if (errorOccurred())
            return null;

        JsonArray jsonArray = gson.fromJson(requestResult.getResponse(), JsonArray.class);
        Type statusList = TypeToken.getParameterized(List.class, objClass).getType();

        List<E> list = gson.fromJson(jsonArray, statusList);

        return new ListStream<>(this, baseUrl, requestResult.getPaginationLink(), list);
    }

    private <E> E getSingle(String url, Class<E> objClass) {
        RequestResult requestResult = getRequest(url);
        if (errorOccurred())
            return null;

        return gson.fromJson(requestResult.getResponse(), objClass);
    }

    private <E> E deleteSingle(String url, Class<E> objClass) {
        RequestResult requestResult = deleteRequest(url);
        if (errorOccurred())
            return null;

        return gson.fromJson(requestResult.getResponse(), objClass);
    }

    private <E> E postSingle(String url, Class<E> objClass) {
        return postSingle(url, objClass, new FormBody.Builder().build());
    }

    private <E> E postSingle(String url, Class<E> objClass, RequestBody body) {
        RequestResult requestResult = request(url, "POST", body);
        if (errorOccurred())
            return null;

        String req = requestResult.getResponse();

        return gson.fromJson(req, objClass);
    }

    private <E> E putSingle(String url, Class<E> objClass, RequestBody body) {
        RequestResult requestResult = request(url, "PUT", body);
        if (errorOccurred())
            return null;

        String req = requestResult.getResponse();

        return gson.fromJson(req, objClass);
    }

    //*******************************************************************
    // Utilitary methods for request building and execution.
    //
    // *******************************************************************
    private RequestResult getRequest(String endpoint) {
        return request(endpoint, "GET", null);
    }

    private RequestResult deleteRequest(String endpoint) {
        return request(endpoint, "DELETE", null);
    }

    private RequestResult request(String url, String method, RequestBody body) {
        error = null;
        RequestResult requestResult;

        Request request = buildRequest(url, method, body);

        try {
            Response response = client.newCall(request).execute();

            String result = response.body() != null ? response.body().string() : null;

            if (!response.isSuccessful())
                error = new APIError(response.code(), result);

            requestResult = new RequestResult(result, response.code(), response.header("Link"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return requestResult;
    }

    private Request buildRequest(String url, String method, RequestBody body) {
        Request.Builder builder = new Request.Builder().url("https://mastodon.social/" + url);

        if (token != null)
            builder.addHeader("Authorization", "Bearer " + token);

        return builder.method(method, body).build();
    }
}

package eus.ehu.gleonis.gleonismastodonfx.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import eus.ehu.gleonis.gleonismastodonfx.api.adapter.MediaAttachmentTypeDeserializer;
import eus.ehu.gleonis.gleonismastodonfx.api.adapter.NotificationTypeDeserializer;
import eus.ehu.gleonis.gleonismastodonfx.api.adapter.VisibilityDeserializer;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.*;
import eus.ehu.gleonis.gleonismastodonfx.db.DBAccount;
import eus.ehu.gleonis.gleonismastodonfx.db.IDBManager;
import eus.ehu.gleonis.gleonismastodonfx.utils.PropertiesManager;
import javafx.collections.ObservableList;
import okhttp3.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class API {

    private final PropertiesManager propertiesManager;

    private final Gson gson;

    private final OkHttpClient client;

    private final Application application;

    private String token;

    private APIError error;

    public API() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Visibility.class, new VisibilityDeserializer());
        builder.registerTypeAdapter(MediaAttachmentType.class, new MediaAttachmentTypeDeserializer());
        builder.registerTypeAdapter(NotificationType.class, new NotificationTypeDeserializer());

        gson = builder.create();
        client = new OkHttpClient();

        propertiesManager = PropertiesManager.getInstance();
        if (propertiesManager.getClientID().isEmpty() || propertiesManager.getClientSecret().isEmpty())
            throw new RuntimeException("Client ID and Client Secret are not set in config.properties file.");

        application = new Application(propertiesManager.getClientID(), propertiesManager.getClientSecret());
        token = null;
    }

    public boolean errorOccurred() {
        return error != null;
    }

    public APIError getError() {
        return error;
    }

    public boolean isUser(Account account) {
        return Objects.equals(account.getId(), propertiesManager.getDbUser());
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
    public String getAuthorizedUserURL() {
        return "https://mastodon.social/oauth/authorize?response_type=code&client_id=" +
                application.getClientId() + "&redirect_uri=urn:ietf:wg:oauth:2.0:oob&scope=read+write+push+follow";
    }

    public Token getToken(String code) {
        RequestBody body = new FormBody.Builder()
                .add("client_id", application.getClientId())
                .add("client_secret", application.getClientSecret())
                .add("redirect_uri", "urn:ietf:wg:oauth:2.0:oob")
                .add("grant_type", "authorization_code")
                .add("code", code)
                .add("scope", "read write push follow")
                .build();

        return postSingle("oauth/token", Token.class, body);
    }

    public void revokeToken(Token token) {
        RequestBody body = new FormBody.Builder()
                .add("client_id", application.getClientId())
                .add("client_secret", application.getClientSecret())
                .add("token", token.getAccessToken())
                .build();

        request("oauth/revoke", "POST", body);
    }

    public boolean isUserConnected() {
        return !propertiesManager.getDbUser().isEmpty();
    }

    public void setupUser(IDBManager db) {
        DBAccount dbAccount = db.getLoggedAccount();

        token = dbAccount.getToken();
        Account account = verifyCredentials();

        db.updateAccount(account);
        propertiesManager.setDbUser(dbAccount.getId());
    }

    public boolean addNewUser(IDBManager db, Token token) {
        this.token = token.getAccessToken();
        Account account = getSingle("api/v1/accounts/verify_credentials", Account.class);
        if (account == null)
            throw new RuntimeException("Error while getting account information.");

        propertiesManager.setDbUser(account.getId());
        return db.insertAccount(account, token.getAccessToken());
    }

    public void switchUser(String token) {
        this.token = token;
        Account account = verifyCredentials();

        propertiesManager.setDbUser(account.getId());
    }

    public void setToken(String token) {
        this.token = token;
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
    public Status repplyToStatus(String statusId, String content, String spoilerText, MediaAttachment... medias) {
        return postStatus(content, StatusVisibility.DIRECT, statusId, spoilerText, medias);
    }

    /**
     * Post a new status
     *
     * @param status      The content of the status
     * @param visibility  The visibility: public (visible by everyone),
     *                    private (followers only),
     *                    direct (only for mentionned user) and
     *                    unlisted (visible by everyone but not in the public timeline)
     * @param inReplyToId Id of the toots to reply to or null if none
     * @param spoilerText Text to be displayed as a warning before the status or null if none
     * @param medias      Medias to attach to the status
     *
     * @return The posted status
     */
    public Status postStatus(String status, StatusVisibility visibility, String inReplyToId, String spoilerText, MediaAttachment... medias) {
        FormBody.Builder form = new FormBody.Builder()
                .add("status", status)
                .add("visibility", visibility.getValue());

        if (inReplyToId != null)
            form.add("in_reply_to_id", inReplyToId);

        if (spoilerText != null && !spoilerText.isEmpty())
            form.add("sensitive", "true")
                    .add("spoiler_text", spoilerText);

        for (int i = 0; i < medias.length; ++i)
            form.add("media_ids[" + i + "]", medias[i].getId());

        return postSingle("api/v1/statuses", Status.class, form.build());
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
    // Trending methods
    //
    // *******************************************************************
    public ListStream<Tag> getTrendingTags(int limit) {
        return getStream("api/v1/trends/tags", limit, Tag.class);
    }

    public ListStream<Status> getTrendingStatuses(int limit) {
        return getStream("api/v1/trends/statuses", limit, Status.class);
    }

    //*******************************************************************
    // Search methods
    //
    // *******************************************************************
    public ListStream<Account> searchAccounts(String query, int limit) {
        query = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String baseUrl = "api/v2/search?q=" + query + "&resolve=true&type=accounts";

        return getSearchStream(baseUrl, limit, Search::getAccounts);
    }

    public ListStream<Status> searchToots(String query, int limit) {
        String baseUrl = "api/v2/search?q=" + query + "&resolve=true&type=statuses";

        return getSearchStream(baseUrl, limit, Search::getStatuses);
    }

    public ListStream<Tag> searchTags(String query, int limit) {
        String baseUrl = "api/v2/search?q=" + query + "&resolve=true&type=hashtags";

        return getSearchStream(baseUrl, limit, Search::getHashtags);
    }

    private <T> ListStream<T> getSearchStream(String baseUrl, int limit, SearchList<T> testList) {
        RequestResult requestResult = getRequest(baseUrl + "&limit=" + limit);
        if (errorOccurred())
            return null;

        Search searchRes = readSingleFromJson(requestResult.response(), Search.class);

        ListStream<T> stream = new ListStream<>(this, baseUrl, requestResult.paginationLink(), limit);
        stream.setSearchList(testList);
        stream.getElement().addAll(testList.apply(searchRes));

        return stream;
    }

    protected <T> void updateSearchStream(String baseUrl, ListStream<T> stream, SearchList<T> testList, int limit) {
        String url = baseUrl + (baseUrl.contains("?") ? "&" : "?") + "limit=" + limit;
        RequestResult requestResult = getRequest(url);
        if (errorOccurred())
            return;

        Search searchRes = readSingleFromJson(requestResult.response(), Search.class);

        stream.parsePaginationLink(requestResult.paginationLink());
        stream.getElement().addAll(testList.apply(searchRes));
    }

    //*******************************************************************
    // HashTag methods
    //
    // *******************************************************************
    public Tag getHashTagInfo(String tag) {
        return getSingle("api/v1/tags/" + tag, Tag.class);
    }

    public Tag followHashTag(String tag) {
        return postSingle("api/v1/tags/" + tag + "/follow", Tag.class);
    }

    public Tag unfollowHashTag(String tag) {
        return postSingle("api/v1/tags/" + tag + "/unfollow", Tag.class);
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
    // Stream Utils methods
    //
    // *******************************************************************
    protected <E> void updateStream(String baseUrl, int limit, Class<E> objClass, ListStream<E> listStream) {
        String url = baseUrl + (baseUrl.contains("?") ? "&" : "?") + "limit=" + limit;

        RequestResult requestResult = getRequest(url);
        listStream.parsePaginationLink(requestResult.paginationLink());
        if (errorOccurred())
            return;

        readArraysFromJson(requestResult.response(), objClass, listStream.getElement());
    }


    //*******************************************************************
    // Web sockets
    //
    // *******************************************************************

    private <E> ListStream<E> getStream(String baseUrl, int limit, Class<E> objClass) {
        String url = baseUrl + (baseUrl.contains("?") ? "&" : "?") + "limit=" + limit;

        RequestResult requestResult = getRequest(url);
        if (errorOccurred())
            return null;

        ListStream<E> listStream = new ListStream<>(this, baseUrl, requestResult.paginationLink(), limit);

        readArraysFromJson(requestResult.response(), objClass, listStream.getElement());

        return listStream;
    }

    private <E> E getSingle(String url, Class<E> objClass) {
        RequestResult requestResult = getRequest(url);
        if (errorOccurred())
            return null;

        return readSingleFromJson(requestResult.response(), objClass);
    }

    private <E> E deleteSingle(String url, Class<E> objClass) {
        RequestResult requestResult = deleteRequest(url);
        if (errorOccurred())
            return null;

        return readSingleFromJson(requestResult.response(), objClass);
    }

    private <E> E postSingle(String url, Class<E> objClass) {
        return postSingle(url, objClass, new FormBody.Builder().build());
    }

    private <E> E postSingle(String url, Class<E> objClass, RequestBody body) {
        RequestResult requestResult = request(url, "POST", body);
        if (errorOccurred())
            return null;

        return readSingleFromJson(requestResult.response(), objClass);
    }

    private <E> E putSingle(String url, Class<E> objClass, RequestBody body) {
        RequestResult requestResult = request(url, "PUT", body);
        if (errorOccurred())
            return null;

        return readSingleFromJson(requestResult.response(), objClass);
    }

    private <E> void readArraysFromJson(Reader r, Class<E> objClass, ObservableList<E> list) {
        try (JsonReader reader = new JsonReader(new BufferedReader(r))) {
            reader.beginArray();

            while (reader.hasNext())
                list.add(gson.fromJson(reader, objClass));

            reader.endArray();
        } catch (IOException e) {
            error = new APIError(0, e.getMessage());
        }
    }

    private <E> E readSingleFromJson(Reader r, Class<E> objClass) {
        E element = null;
        try (JsonReader reader = new JsonReader(new BufferedReader(r))) {
            element = gson.fromJson(reader, objClass);
        } catch (IOException e) {
            error = new APIError(0, e.getMessage());
        }

        return element;
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
            // We do not close it since we need to read the response. And when we will read it, it will be closed.
            Response response = client.newCall(request).execute();
            Reader result = response.body() != null ? response.body().charStream() : null;

            if (!response.isSuccessful())
                error = new APIError(response.code(), response.body().string());

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

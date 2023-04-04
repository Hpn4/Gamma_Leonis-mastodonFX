package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

import java.util.List;

public class Status implements Identifiable {

    String id;

    String created_at;

    // Owner of the status
    Account account;

    String content;

    Visibility visibility;

    boolean sensitive;

    String spoiler_text;

    List<MediaAttachment> media_attachments;

    int reblogs_count;

    int favourites_count;

    int replies_count;

    String in_reply_to_id;

    String in_reply_to_account_id;

    Status reblog;

    // Need to add poll, filter result

    String language;

    boolean favourited;

    boolean reblogged;

    boolean bookmarked;

    List<StatusMention> mentions;

    @Override
    public String getId() {
        return id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Account getAccount() {
        return account;
    }

    public String getContent() {
        return content;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public String getSpoiler_text() {
        return spoiler_text;
    }

    public List<MediaAttachment> getMedia_attachments() {
        return media_attachments;
    }

    public int getReblogs_count() {
        return reblogs_count;
    }

    public int getFavourites_count() {
        return favourites_count;
    }

    public int getReplies_count() {
        return replies_count;
    }

    public String getIn_reply_to_id() {
        return in_reply_to_id;
    }

    public String getIn_reply_to_account_id() {
        return in_reply_to_account_id;
    }

    public Status getReblog() {
        return reblog;
    }

    public String getLanguage() {
        return language;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public boolean isReblogged() {
        return reblogged;
    }

    public boolean isBookmarked() {
        return bookmarked;
    }

    public List<StatusMention> getMentions() {return mentions; }

}

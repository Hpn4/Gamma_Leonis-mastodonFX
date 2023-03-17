package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public class Account {

    String id;

    boolean locked;

    boolean bot;

    String username;

    String acct;

    String display_name;

    String created_at;

    String note;

    String avatar;

    String header;

    int statuses_count;

    int followers_count;

    int following_count;

    public Account() {
    }

    @Override
    public String toString()
    {
        return "Account{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", bot=" + bot +
                ", created_at='" + created_at + '\'' +
                ", statuses_count=" + statuses_count +
                ", followers_count=" + followers_count +
                ", following_count=" + following_count +
                '}';
    }

    public String getId() {
        return id;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isBot() {
        return bot;
    }

    public String getUsername() {
        return username;
    }

    public String getAcct() {
        return acct;
    }

    public String getDisplayName() {
        return display_name;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getNote() {
        return note;
    }

    /**
     * Returns the URL of the avatar image. The image may be a png or a gif
     *
     * @return the URL of the avatar image.
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * Returns the URL of the header image. The image may be a png or a gif
     *
     * @return the URL of the header image.
     */
    public String getHeader() {
        return header;
    }

    public int getStatusesCount() {
        return statuses_count;
    }

    public int getFollowersCount() {
        return followers_count;
    }

    public int getFollowingCount() {
        return following_count;
    }
}

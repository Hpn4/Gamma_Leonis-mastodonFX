package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

public class Relationship {

    String id;

    boolean following;

    boolean followed_by;

    boolean blocking;

    boolean blocked_by;

    boolean muting;

    String note;

    public String getId() {
        return id;
    }

    /**
     *
     * @return If we follow the account
     */
    public boolean isFollowing() {
        return following;
    }

    /**
     *
     * @return If the account follow us
     */
    public boolean isFollowedBy() {
        return followed_by;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public boolean isBlockedBy() {
        return blocked_by;
    }

    public boolean isMuting() {
        return muting;
    }

    public String getNote() {
        return note;
    }

}

package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

import java.util.List;

public class Search {

    List<Account> accounts;

    List<Status> statuses;

    List<Tag> hashtags;

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public List<Tag> getHashtags() {
        return hashtags;
    }
}

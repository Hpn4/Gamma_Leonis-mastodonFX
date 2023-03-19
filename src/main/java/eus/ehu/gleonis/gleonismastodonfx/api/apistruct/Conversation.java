package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

import java.util.List;

public class Conversation implements Identifiable {

    String id;

    boolean unread;

    List<Account> accounts;

    Status last_status;

    public String getId() {
        return id;
    }

    public boolean isUnread() {
        return unread;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Status getLastStatus() {
        return last_status;
    }
}

package eus.ehu.gleonis.gleonismastodonfx.db;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;

import java.util.List;

public interface IDBManager {

    void initDB();

    // Add a new account in the database
    // returns true if no problem, false if the account already exists
    DBAccount insertAccount(Account account, String token);

    DBAccount insertAccount(String id, String username, String avatar, String token);

    boolean deleteAccount(String id);

    // Get the currently logged in account or null if no account is logged in
    DBAccount getLoggedAccount();

    // Get all the accounts in the database
    List<DBAccount> getAccounts();

    // Update the last access of the account and the avatar url if changed
    void updateAccount(Account ac);
}

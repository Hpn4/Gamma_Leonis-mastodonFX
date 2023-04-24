package eus.ehu.gleonis.gleonismastodonfx.db;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;

import java.util.List;

public interface IDBManager {

    // Add a new account in the database
    void insertAccount(Account account, String token);

    // Get the currently logged in account or null if no account is logged in
    DBAccount getLoggedAccount();

    // Get all the accounts in the database
    List<DBAccount> getAccounts();

    // Update the last access of the account and the avatar url if changed
    void updateAccount(Account ac);
}

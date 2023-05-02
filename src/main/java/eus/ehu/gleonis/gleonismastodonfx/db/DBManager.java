package eus.ehu.gleonis.gleonismastodonfx.db;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.utils.PropertiesManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBManager implements IDBManager {

    private static final Logger logger = LogManager.getLogger("DBManager");

    private final PropertiesManager propManager;

    private Connection conn;

    private final String dbpath;

    public DBManager() {
        propManager = PropertiesManager.getInstance();
        dbpath = propManager.getDbPath();

        if (dbpath == null || dbpath.isEmpty()) {
            logger.fatal("Database path not found in properties file");
            throw new RuntimeException("Database path not found in properties file");
        }
    }

    private void open() {
        try {
            String url = "jdbc:sqlite:" + dbpath;
            conn = DriverManager.getConnection(url);

            logger.info("Database connection established");
        } catch (Exception e) {
            logger.error("Cannot connect to database server " + e, e);
        }
    }

    private void close() {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException e) {
                logger.error("Cannot close the connection to database server " + e, e);
                e.printStackTrace();
            }

        logger.info("Database connection closed");
    }


    @Override
    public boolean insertAccount(Account account, String token) {
        open();

        String sql = "INSERT INTO accounts (id, username, avatar, last_access, token) VALUES(?,?,?,?,?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, account.getId());
            pstmt.setString(2, account.getUsername());
            pstmt.setString(3, account.getAvatar());
            pstmt.setString(4, LocalDate.now().toString());
            pstmt.setString(5, token);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            close();
            return false;
        }

        close();
        return true;
    }

    @Override
    public DBAccount getLoggedAccount() {
        if (propManager.getDbUser().isEmpty())
            return null;

        open();

        DBAccount account = null;

        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, propManager.getDbUser());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                account = new DBAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("avatar"),
                        rs.getString("last_access"),
                        rs.getString("token")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        close();

        return account;
    }

    @Override
    public List<DBAccount> getAccounts() {
        open();

        List<DBAccount> accounts = new ArrayList<>();

        String sql = "SELECT * FROM accounts";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                DBAccount account = new DBAccount(
                        rs.getString("id"),
                        rs.getString("username"),
                        rs.getString("avatar"),
                        rs.getString("last_access"),
                        rs.getString("token")
                );

                accounts.add(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        close();

        return accounts;
    }

    @Override
    public void updateAccount(Account ac) {
        open();

        String sql = "UPDATE accounts SET username = ?, avatar = ?, last_access = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ac.getUsername());
            pstmt.setString(2, ac.getAvatar());
            pstmt.setString(3, LocalDate.now().toString());
            pstmt.setString(4, ac.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        close();
    }
}

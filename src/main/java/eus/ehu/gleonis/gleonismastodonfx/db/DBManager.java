package eus.ehu.gleonis.gleonismastodonfx.db;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.utils.PropertiesManager;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.exception.ConstraintViolationException;

import java.time.LocalDate;
import java.util.List;

public class DBManager implements IDBManager {

    private static final Logger logger = LogManager.getLogger("DBManager");

    private PropertiesManager propManager;

    private EntityManager dbConnector;

    private EntityManagerFactory emf;

    public DBManager() {
    }

    public void initDB() {
        long start = System.currentTimeMillis();
        propManager = PropertiesManager.getInstance();

        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            emf = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }

        dbConnector = emf.createEntityManager();

        logger.info("DataBase opened");
        logger.debug("Time to open DB: " + (System.currentTimeMillis() - start) + "ms");
    }

    public DBAccount insertAccount(Account account, String token) {
        return insertAccount(account.getId(), account.getUsername(), account.getAvatar(), token);
    }

    public DBAccount insertAccount(String id, String username, String avatar, String token) {
        try {
            dbConnector.getTransaction().begin();
            DBAccount dbAccount = new DBAccount(id, username, avatar, LocalDate.now().toString(), token);
            dbConnector.persist(dbAccount);
            dbConnector.getTransaction().commit();

            logger.debug("Account inserted");

            return dbAccount;
        }catch (EntityExistsException | ConstraintViolationException e) {
            dbConnector.getTransaction().rollback();

            return null;
        }
    }

    public boolean deleteAccount(String id) {
        dbConnector.getTransaction().begin();

        int amount = dbConnector.createQuery("DELETE FROM DBAccount a WHERE a.id = ?1")
                .setParameter(1, id)
                .executeUpdate();

        dbConnector.getTransaction().commit();

        return amount == 1;
    }

    public boolean deleteAccount(DBAccount account) {
        dbConnector.getTransaction().begin();
        dbConnector.remove(account);
        dbConnector.getTransaction().commit();

        return true;
    }


    public List<DBAccount> getAccounts() {
        List<DBAccount> accounts = dbConnector.createQuery("SELECT a FROM DBAccount a", DBAccount.class).getResultList();
        logger.debug(accounts.size() + " Accounts retrieved");

        return accounts;
    }

    public DBAccount getLoggedAccount() {
        String dbUser = propManager.getDbUser();

        if (dbUser.isEmpty())
            return null;

        DBAccount account = dbConnector.getReference(DBAccount.class, dbUser);

        if (account == null) {
            logger.error("Account not found");
            return null;
        }

        return account;
    }

    public void updateAccount(Account ac) {
        dbConnector.getTransaction().begin();

        DBAccount dbAccount = dbConnector.getReference(DBAccount.class, ac.getId());
        dbAccount.setAvatarUrl(ac.getAvatar());
        dbAccount.setUsername(ac.getUsername());
        dbAccount.setLast_access(LocalDate.now().toString());

        dbConnector.getTransaction().commit();

        logger.debug("Account updated");
    }


    // later we will add further operations here
    public void closeDb() {
        dbConnector.close();
        logger.info("DataBase closed");
    }

}

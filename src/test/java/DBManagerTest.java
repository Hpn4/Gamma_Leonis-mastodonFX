import eus.ehu.gleonis.gleonismastodonfx.db.DBAccount;
import eus.ehu.gleonis.gleonismastodonfx.db.DBManager;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DBManagerTest {

    DBManager dbm;

    @BeforeEach
    public void setUp() {
        dbm = new DBManager();
        dbm.initDB();

    }

    @AfterEach
    public void close() {
        dbm.closeDb();
    }

    @Test
    public void testInsertAccount() {
        int firstSize = dbm.getAccounts().size();

        int inserted = dbm.insertAccount("1", "Hpn4", "outer_wilds", "1122") != null ? 1 : 0;
        inserted += dbm.insertAccount("2", "Hey", "echoes_of_the_eye", "3311") != null ? 1 : 0;
        inserted += dbm.insertAccount("3", "Hpn4", "outer_wilds", "1122") != null ? 1 : 0;
        inserted += dbm.insertAccount("4", "Hey", "echoes_of_the_eye", "3311") != null ? 1 : 0;

        int secondSize = dbm.getAccounts().size();

        Assertions.assertEquals(secondSize - firstSize, inserted);
    }

    @Test
    public void testDeleteAccounts() {
        for (DBAccount acc : dbm.getAccounts())
            dbm.deleteAccount(acc.getId());

        Assertions.assertEquals(dbm.getAccounts().size(), 0);
    }

    @Test
    public void testDeleteNotExistent() {
        for (DBAccount acc : dbm.getAccounts())
            dbm.deleteAccount(acc.getId());

        DBAccount acc = dbm.insertAccount("1", "Hpn4", "outer_wilds", "1122");

        if (acc != null)
            Assertions.assertTrue(dbm.deleteAccount("1"));
        else
            Assertions.assertFalse(dbm.deleteAccount("1"));
    }
}

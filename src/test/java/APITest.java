import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class APITest {

    API api;

    Account us;

    @BeforeAll
    public void setUp() {
        api = new API();
        // Inert your token here if you want to execute test
        api.setToken("");

        us = api.verifyCredentials();
    }

    @Test
    public void testFavorites() {
        // Get a random status
        String idS = api.getAccountStatuses(us.getId(), 1).getElement().get(0).getId();

        api.favouriteStatus(idS);
        Assertions.assertTrue(api.getStatus(idS).isFavourited());
        api.unfavouriteStatus(idS);
        Assertions.assertFalse(api.getStatus(idS).isFavourited());
    }

    @Test
    public void testBookmarks() {
        // Get a random status
        String idS = api.getAccountStatuses(us.getId(), 1).getElement().get(0).getId();

        api.bookmarkStatus(idS);
        Assertions.assertTrue(api.getStatus(idS).isBookmarked());
        api.unbookmarkStatus(idS);
        Assertions.assertFalse(api.getStatus(idS).isBookmarked());
    }
}

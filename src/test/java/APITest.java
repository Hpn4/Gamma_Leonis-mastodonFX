import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Relationship;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class APITest {

    API api;

    Account us;

    @BeforeAll
    public void setUp() {
        api = new API();

        //api.setToken(""); //TODO: Insert your token here in order to test the API

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

    @Test
    public void testFollow() {
        String idA = "1"; // Account of the creator of mastodon

        Relationship r = api.followAccount(idA);
        Assertions.assertTrue(r.isFollowing());
        Assertions.assertTrue(api.getRelationships(idA).getElement().get(0).isFollowing());

        r = api.unfollowAccount(idA);
        Assertions.assertFalse(r.isFollowing());
        Assertions.assertFalse(api.getRelationships(idA).getElement().get(0).isFollowing());
    }

    @Test
    public void testBlock() {
        String idA = "1"; // Account of the creator of mastodon

        Relationship r = api.blockAccount(idA);
        Assertions.assertTrue(r.isBlocking());
        Assertions.assertTrue(api.getRelationships(idA).getElement().get(0).isBlocking());

        r = api.unblockAccount(idA);
        Assertions.assertFalse(r.isBlocking());
        Assertions.assertFalse(api.getRelationships(idA).getElement().get(0).isBlocking());
    }

    @Test
    public void testMute() {
        String idA = "1"; // Account of the creator of mastodon

        Relationship r = api.muteAccount(idA);
        Assertions.assertTrue(r.isMuting());
        Assertions.assertTrue(api.getRelationships(idA).getElement().get(0).isMuting());

        r = api.unmuteAccount(idA);
        Assertions.assertFalse(r.isMuting());
        Assertions.assertFalse(api.getRelationships(idA).getElement().get(0).isMuting());
    }
}

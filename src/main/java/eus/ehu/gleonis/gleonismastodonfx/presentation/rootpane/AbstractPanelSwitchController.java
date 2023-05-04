package eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane;

import eus.ehu.gleonis.gleonismastodonfx.api.ListStream;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Tag;
import eus.ehu.gleonis.gleonismastodonfx.presentation.AbstractController;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.AccountScrollableContent;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.TagsScrollableContent;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.TootsScrollableContent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Skin;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AbstractPanelSwitchController extends AbstractController {

    private static final Logger logger = LogManager.getLogger("AbstractPanelSwitchController");

    private BorderPane rootPane;

    private int elementsPerPage;

    protected void init(BorderPane rootBorderPane, ToggleGroup tg, int elementsPerPage) {
        rootPane = rootBorderPane;
        this.elementsPerPage = elementsPerPage;

        setupRadioButtonsUIFromTG(tg);
    }


    // This function have for only purposes to set the little blue triangle at the center of the radio button
    private void setupRadioButtonUI(RadioButton radioButton) {
        // CSS properties are stored in the skin of the radio button. But we need to wait for the skin to be created
        ChangeListener<Skin<?>> skinChangeListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue) {
                radioButton.skinProperty().removeListener(this);
                radioButton.lookup(".dot").translateXProperty().bind(radioButton.widthProperty().divide(2));
            }
        };

        radioButton.skinProperty().addListener(skinChangeListener);
    }

    private void setupRadioButtonsUIFromTG(ToggleGroup tg) {
        for (Toggle t : tg.getToggles())
            setupRadioButtonUI((RadioButton) t);
    }

    protected void showScrollableAccounts(ListStream<Account> accounts, boolean followersPane) {
        if(accounts == null)
            return;

        long t = System.currentTimeMillis();

        AccountScrollableContent accountScrollableContent = new AccountScrollableContent(accounts, elementsPerPage, followersPane);
        rootPane.setCenter(accountScrollableContent);

        logger.info("Time to display accounts: " + (System.currentTimeMillis() - t));
    }

    protected void showScrollableToots(ListStream<Status> toots) {
        if(toots == null)
            return;

        long t = System.currentTimeMillis();

        TootsScrollableContent tootsScrollableContent = new TootsScrollableContent(toots, elementsPerPage);
        rootPane.setCenter(tootsScrollableContent);

        logger.info("Time to display toots: " + (System.currentTimeMillis() - t));
    }

    protected void showScrollableTags(ListStream<Tag> tags) {
        if(tags == null)
            return;

        long t = System.currentTimeMillis();

        TagsScrollableContent tagsScrollableContent = new TagsScrollableContent(tags, elementsPerPage);
        rootPane.setCenter(tagsScrollableContent);

        logger.info("Time to display tags: " + (System.currentTimeMillis() - t));
    }
}

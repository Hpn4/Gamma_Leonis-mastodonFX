package eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

public class TrendingRPController extends AbstractPanelSwitchController {

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private ToggleGroup trendingTypeSelection;

    public void refreshTrending() {
        super.init(rootBorderPane, trendingTypeSelection, 10);

        trendingTypeSelection.selectToggle(null);

        // Show tags by default
        ((RadioButton) trendingTypeSelection.getToggles().get(0)).fire();
    }

    @FXML
    void onTootsTypeClick() {
        showScrollableToots(api.getTrendingStatuses(10));
    }

    @FXML
    void onHashtagsTypeClick() {
        showScrollableTags(api.getTrendingTags(10));
    }

}

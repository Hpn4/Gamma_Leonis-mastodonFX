package eus.ehu.gleonis.gleonismastodonfx.presentation.rootpane;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;

public class SearchRPController extends AbstractPanelSwitchController {

    @FXML
    private BorderPane rootBorderPane;

    @FXML
    private ToggleGroup searchingTypeSelection;

    private String query;

    public void doSearch(String query) {
        super.init(rootBorderPane, searchingTypeSelection, 10);

        searchingTypeSelection.selectToggle(null);

        // This part will choose will pane to render first depending on the query.
        // If the user type a @, accounts will be shown.
        // If the user type a #, tags will be shown.
        int smartQueryIndex = 1;

        if(query.charAt(0) == '#')
            smartQueryIndex = 0;
        else if(query.charAt(0) == '@')
            smartQueryIndex = 2;

        this.query = smartQueryIndex != 1 ? query.substring(1) : query;

        // Show accounts by default
        ((RadioButton) searchingTypeSelection.getToggles().get(smartQueryIndex)).fire();
    }

    @FXML
    void onAccountsTypeClick() {
        showScrollableAccounts(api.searchAccounts(query, 11), false);
    }

    @FXML
    void onTootsTypeClick() {
        showScrollableToots(api.searchToots(query, 10));
    }

    @FXML
    void onHashtagsTypeClick() {
        showScrollableTags(api.searchTags(query, 10));
    }

}

package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Tag;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class HashTagItem {

    private final API api;

    private final Tag hashtag;

    @FXML
    private GridPane hashtagItem;

    @FXML
    private Label nameLabel;

    @FXML
    private Label usesLabel;

    public HashTagItem(String hashtagName) {
        api = MainApplication.getInstance().getAPI();
        hashtag = api.getHashTagInfo(hashtagName);

        updateItem(hashtag);
    }

    public HashTagItem(Tag hashtag) {
        api = MainApplication.getInstance().getAPI();
        this.hashtag = hashtag;

        updateItem(hashtag);
    }

    @FXML
    void onItemClick() {
        MainApplication.getInstance().requestShowToots(api.getHashTagTimelines(hashtag.getName(), 10), 10);
    }

    protected void updateItem(Tag hashtag) {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("hashtag_item.fxml"));
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        nameLabel.setText("#" + hashtag.getName());

        int uses = 0, accounts = 0;

        uses += hashtag.getHistory().get(0).getUses();
        uses += hashtag.getHistory().get(1).getUses();

        accounts += hashtag.getHistory().get(0).getAccounts();
        accounts += hashtag.getHistory().get(1).getAccounts();

        usesLabel.setText(uses + " uses by " + accounts + " people in the past 2 days");
    }

    public GridPane getHashtagItem() {
        return hashtagItem;
    }
}

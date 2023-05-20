package eus.ehu.gleonis.gleonismastodonfx.presentation.comp;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Tag;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.TagHistory;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.TagsScrollableContent;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.List;

public class HashTagItem extends AbstractItem<Tag> {

    @FXML
    private GridPane hashtagItem;

    @FXML
    private Label nameLabel;

    @FXML
    private Label usesLabel;

    @FXML
    private AreaChart<String, Number> frequenceCharts;

    public HashTagItem(TagsScrollableContent tsc, Tag hashtag) {
        super(tsc);
        elem = hashtag;

        updateItem(elem);
    }

    @FXML
    void onItemClick() {
        MainApplication.getInstance().requestShowToots(api.getHashTagTimelines(elem.getName(), 10), 10, true);
    }

    protected void updateItem(Tag hashtag) {
        FXMLLoader loader = Utils.loadAndTranslate(MainApplication.class.getResource("hashtag_item.fxml"));
        loader.setController(this);

        try {
            loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        nameLabel.setText("#" + hashtag.getName());

        int uses = 0, accounts = 0;
        List<TagHistory> historyList = hashtag.getHistory();

        uses += historyList.get(0).getUses();
        uses += historyList.get(1).getUses();

        accounts += historyList.get(0).getAccounts();
        accounts += historyList.get(1).getAccounts();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (TagHistory tagHistory : historyList)
            series.getData().add(new XYChart.Data<>(tagHistory.getDay(), tagHistory.getUses()));

        frequenceCharts.getData().add(series);

        usesLabel.setText(Utils.getTranslation("hash.uses").formatted(uses, accounts));
    }

    public GridPane getParent() {
        return hashtagItem;
    }
}

package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.utils.HTMLView;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;


public class TootsItemCell{

    private API api;

    private FXMLLoader loader;

    @FXML
    private BorderPane messageBorder;

    @FXML
    private Rectangle accountProfilePicture;

    @FXML
    private Rectangle accountReblogAvatar;

    @FXML
    private Label dateMessageLabel;

    @FXML
    private Label userLabel;

    @FXML
    private Label webfingerLabel;


    // Toots interaction menu
    @FXML
    private GridPane interactionPanel;

    @FXML
    private Label repliesCount;

    @FXML
    private Label reblogsCount;

    @FXML
    private Label favouritesCount;

    @FXML
    private Button reblogButton;

    @FXML
    private Button favouriteButton;

    @FXML
    private Button bookmarkButton;

    private Status status;

    public TootsItemCell(Status status, API api) {
        super();
        this.api = api;
        this.status = status;

        updateItem(status);
    }

    @FXML
    void onReplyClick() {
        //TODO
    }

    @FXML
    void onReblogClick() {
        if (status.isReblogged())
            status = api.unreblogStatus(status.getId());
        else
            status = api.reblogStatus(status.getId());

        updateItem(status);
    }

    @FXML
    void onFavouriteClick() {
        if (status.isFavourited())
            status = api.unfavouriteStatus(status.getId());
        else
            status = api.favouriteStatus(status.getId());

        updateItem(status);
    }

    @FXML
    void onBookmarkClick() {
        if (status.isBookmarked())
            status = api.unbookmarkStatus(status.getId());
        else
            status = api.bookmarkStatus(status.getId());

        updateItem(status);
    }

    @FXML
    void onMoreOptClick() {
        //TODO
    }

    protected void updateItem(Status status) {
        //First, we check for the empty status
        if (status == null) {
            return;
        }

        ///Then, we check if the loader is null
        if (loader == null) {
            loader = new FXMLLoader(MainApplication.class.getResource("toots.fxml"));
            loader.setController(this);

            try {
                loader.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
            interactionPanel.setVisible(false);
        }

        //We have the loader, so we can set the graphic
        Status finalStatus = status;
        if (status.getReblog() != null) {
            finalStatus = status.getReblog();

            status.getAccount().getAvatarCachedImage().setImage(accountReblogAvatar);
            accountReblogAvatar.setVisible(true);
            accountProfilePicture.setHeight(35);
            accountProfilePicture.setWidth(35);
        } else {
            accountReblogAvatar.setVisible(false);
            accountProfilePicture.setHeight(40);
            accountProfilePicture.setWidth(40);
        }

        userLabel.setText(finalStatus.getAccount().getDisplayName());
        webfingerLabel.setText(finalStatus.getAccount().getAcct());

        HTMLView htmlView = new HTMLView(finalStatus, finalStatus.getContent());
        htmlView.setPadding(new Insets(10));

        messageBorder.setCenter(htmlView);

        dateMessageLabel.setText(Utils.getDateString(status.getCreated_at()));

        finalStatus.getAccount().getAvatarCachedImage().setImage(accountProfilePicture);

        // Setup labels for interaction menu
        repliesCount.setText(String.valueOf(finalStatus.getReplies_count()));
        reblogsCount.setText(String.valueOf(finalStatus.getReblogs_count()));
        favouritesCount.setText(String.valueOf(finalStatus.getFavourites_count()));

        setupButtonColor(reblogButton, finalStatus.isReblogged(), "rebloged-toots-button");
        setupButtonColor(favouriteButton, finalStatus.isFavourited(), "favourited-toots-button");
        setupButtonColor(bookmarkButton, finalStatus.isBookmarked(), "bookmarked-toots-button");

        // Layout constraints
        messageBorder.prefHeightProperty().bind(htmlView.heightProperty().map(e -> 40 + Math.max((Double) e, 60.0)));

        messageBorder.setOnMouseEntered(e -> interactionPanel.setVisible(true));
        messageBorder.setOnMouseExited(e -> interactionPanel.setVisible(false));
    }

    private void setupButtonColor(Button button, boolean setColor, String cssClassColor) {
        if (setColor) {
            button.getStyleClass().remove("interaction-status-button");
            button.getStyleClass().add(cssClassColor);
            button.applyCss();
        } else {
            button.getStyleClass().remove(cssClassColor);
            button.getStyleClass().add("interaction-status-button");
            button.applyCss();
        }
    }

    public BorderPane getGraphic() {
        return messageBorder;
    }
}

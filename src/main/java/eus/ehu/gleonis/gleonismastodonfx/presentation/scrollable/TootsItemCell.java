package eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.MediaAttachment;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.MediaAttachmentType;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.utils.CachedImage;
import eus.ehu.gleonis.gleonismastodonfx.utils.HTMLView;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;


public class TootsItemCell {

    private API api;

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

    @FXML
    private Label sensitiveContentLabel;

    @FXML
    private VBox mediasPane;

    private Status status;

    public void init(Status status) {
        api = MainApplication.getInstance().getAPI();
        this.status = status;

        updateItem(status, false);
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

        updateItem(status, true);
    }

    @FXML
    void onFavouriteClick() {
        if (status.isFavourited())
            status = api.unfavouriteStatus(status.getId());
        else
            status = api.favouriteStatus(status.getId());

        updateItem(status, true);
    }

    @FXML
    void onBookmarkClick() {
        if (status.isBookmarked())
            status = api.unbookmarkStatus(status.getId());
        else
            status = api.bookmarkStatus(status.getId());

        updateItem(status, true);
    }

    @FXML
    void onMoreOptClick() {
        //TODO
    }

    protected void updateItem(Status status, boolean onlyInteractionPanel) {

        if (onlyInteractionPanel) {
            setupInteractionPanel(status.getReblog() != null ? status.getReblog() : status);
            return;
        }

        //We have the loader, so we can set the graphic
        final Status finalStatus;
        if (status.getReblog() != null) {
            finalStatus = status.getReblog();

            status.getAccount().getAvatarCachedImage().setImage(accountReblogAvatar);
            accountReblogAvatar.setOnMouseClicked(e -> MainApplication.getInstance().requestShowAccount(status.getAccount().getId()));

            accountReblogAvatar.setVisible(true);
            accountProfilePicture.setHeight(35);
            accountProfilePicture.setWidth(35);
        } else {
            finalStatus = status;
            accountReblogAvatar.setVisible(false);
            accountProfilePicture.setHeight(40);
            accountProfilePicture.setWidth(40);
        }

        finalStatus.getAccount().getAvatarCachedImage().setImage(accountProfilePicture);

        accountProfilePicture.setOnMouseClicked(e -> MainApplication.getInstance().requestShowAccount(finalStatus.getAccount().getId()));

        userLabel.setText(finalStatus.getAccount().getDisplayName());
        webfingerLabel.setText(finalStatus.getAccount().getAcct());

        HTMLView htmlView = new HTMLView(finalStatus, finalStatus.getContent());
        htmlView.setPadding(new Insets(10));

        if (finalStatus.isSensitive()) {
            sensitiveContentLabel.setText(finalStatus.getSpoiler_text());
            sensitiveContentLabel.setOnMouseClicked(e -> {
                messageBorder.setCenter(htmlView);
                sensitiveContentLabel.setVisible(false);
            });
        } else {
            sensitiveContentLabel.setVisible(true);
            messageBorder.setCenter(htmlView);
        }

        // Layout constraints
        messageBorder.prefHeightProperty().bind(
                htmlView.heightProperty()
                        .add(mediasPane.heightProperty())
                        .map(e -> {
                            double sens = sensitiveContentLabel.isVisible() ?
                                    sensitiveContentLabel.heightProperty().get() : 0;
                            return 30 + Math.max((Double) e + sens * 4, 40.0);
                        })
        );

        setupInteractionPanel(finalStatus);
        setupMediaAttachments(finalStatus);
    }

    private void setupMediaAttachments(Status status) {
        for (MediaAttachment media : status.getMedia_attachments())
            if (media.getType() == MediaAttachmentType.IMAGE) {
                CachedImage cachedImage = new CachedImage(media);
                ImageView imageView = new ImageView();
                imageView.fitWidthProperty().bind(messageBorder.widthProperty().divide(2));
                imageView.setPreserveRatio(true);

                cachedImage.setImage(imageView);
                mediasPane.getChildren().add(imageView);
                VBox.setMargin(imageView, new Insets(5));
            }

        mediasPane.setAlignment(javafx.geometry.Pos.CENTER);
    }

    private void setupInteractionPanel(Status status) {
        interactionPanel.setVisible(false);

        // Setup labels for interaction menu
        repliesCount.setText(String.valueOf(status.getReplies_count()));
        reblogsCount.setText(String.valueOf(status.getReblogs_count()));
        favouritesCount.setText(String.valueOf(status.getFavourites_count()));

        dateMessageLabel.setText(Utils.getDateString(status.getCreated_at()));

        setupButtonColor(reblogButton, status.isReblogged(), "rebloged-toots-button");
        setupButtonColor(favouriteButton, status.isFavourited(), "favourited-toots-button");
        setupButtonColor(bookmarkButton, status.isBookmarked(), "bookmarked-toots-button");

        // Layout constraints
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

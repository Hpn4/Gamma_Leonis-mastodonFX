package eus.ehu.gleonis.gleonismastodonfx.presentation.comp;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.MediaAttachment;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.MediaAttachmentType;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Visibility;
import eus.ehu.gleonis.gleonismastodonfx.utils.CachedImage;
import eus.ehu.gleonis.gleonismastodonfx.utils.HTMLView;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;


public class TootItem extends AbstractItem<Status> {

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

    @FXML
    private MenuButton moreMenuButton;

    @FXML
    private MenuItem deleteMenuItem;

    private boolean context;

    public TootItem() {
        super(null);
    }

    public void init(Status status, boolean context) {
        elem = status;
        this.context = context;

        updateItem(elem, false);
    }

    @FXML
    void onReplyClick() {
        MainApplication.getInstance().requestShowSendToot("@" + elem.getAccount().getAcct(), Visibility.DIRECT, elem.getId());
    }

    @FXML
    void onReblogClick() {
        elem = elem.isReblogged() ? api.unreblogStatus(elem.getId()) : api.reblogStatus(elem.getId());

        updateItem(elem, true);
    }

    @FXML
    void onFavouriteClick() {
        elem = elem.isFavourited() ? api.unfavouriteStatus(elem.getId()) : api.favouriteStatus(elem.getId());

        updateItem(elem, true);
    }

    @FXML
    void onBookmarkClick() {
        elem = elem.isBookmarked() ? api.unbookmarkStatus(elem.getId()) : api.bookmarkStatus(elem.getId());

        updateItem(elem, true);
    }

    @FXML
    void onDeleteTootClick() {
        api.deleteStatus(elem.getId());
        deleteItemFromUI();
    }

    @FXML
    void onMentionMenuClick() {
        MainApplication.getInstance().requestShowSendToot("@" + elem.getAccount().getAcct() + " ", Visibility.PUBLIC, null);
    }

    @FXML
    void onPrivateMentionMenuClick() {
        MainApplication.getInstance().requestShowSendToot("@" + elem.getAccount().getAcct() + " ", Visibility.DIRECT, null);
    }

    @FXML
    void onContextClick() {
        MainApplication.getInstance().requestShowTootContext(elem);
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

        // Redirection to the account by clicking on the avatar, the name and the webfinger
        Utils.redirectToAccount(finalStatus.getAccount().getId(), accountProfilePicture, userLabel, webfingerLabel);

        userLabel.setText(finalStatus.getAccount().getDisplayName());
        webfingerLabel.setText(finalStatus.getAccount().getAcct());

        HTMLView htmlView = new HTMLView(finalStatus, finalStatus.getContent(), context);
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

                            // The spoiler label has a bigger size since it's bold and higher police, so it's take
                            // more place than just a single line of text in a normal toot
                            double minSize = sensitiveContentLabel.isVisible() ? 80 : 40;
                            return 30 + Math.max((Double) e + sens * 4, minSize);
                        })
        );

        deleteMenuItem.setVisible(api.isUser(status.getAccount()));
        dateMessageLabel.setText(Utils.getDateString(status.getCreated_at()));

        setupInteractionPanel(finalStatus);
        interactionPanel.setVisible(false);
        setupMediaAttachments(finalStatus, finalStatus.isSensitive());

        moreMenuButton.showingProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                messageBorder.setOnMouseExited(null);
                interactionPanel.setVisible(true);
            } else {
                interactionPanel.setVisible(false);
                messageBorder.setOnMouseExited(e -> interactionPanel.setVisible(false));
            }
        });
    }

    private void setupMediaAttachments(Status status, boolean sensitive) {
        for (MediaAttachment media : status.getMedia_attachments()) {
            MediaAttachmentType type = media.getType();

            if (type == MediaAttachmentType.IMAGE) {
                CachedImage cachedImage = new CachedImage(media);
                ImageView imageView = new ImageView();
                imageView.fitWidthProperty().bind(messageBorder.widthProperty().divide(2));
                // imageView.fitHeightProperty().bind(messageBorder.widthProperty().divide(2).map(w -> w.doubleValue() * 9 / 16)); // 16:9 aspect ratio
                imageView.setPreserveRatio(true);

                // Enable ability to disable/activate image spoiler
                imageView.setOnMouseClicked(e -> cachedImage.switchImage(imageView, media.getWidth(), media.getHeight()));

                if (sensitive)
                    cachedImage.setBlurHashedImg(imageView, media.getWidth(), media.getHeight());
                else
                    cachedImage.setImage(imageView);

                mediasPane.getChildren().add(imageView);
                VBox.setMargin(imageView, new Insets(5));
            } else if (type != MediaAttachmentType.UNKNOWN) {
                Pane mediaPane = type == MediaAttachmentType.AUDIO ?
                                new MediaPlayerNode(media, false) : new VideoPlayerNode(media);

                mediasPane.getChildren().add(mediaPane);
                mediaPane.maxWidthProperty().bind(messageBorder.widthProperty().divide(2));
                VBox.setMargin(mediaPane, new Insets(10));
            }
        }

        mediasPane.setAlignment(Pos.CENTER);
    }

    private void setupInteractionPanel(Status status) {
        // Setup labels for interaction menu
        repliesCount.setText(String.valueOf(status.getReplies_count()));
        reblogsCount.setText(String.valueOf(status.getReblogs_count()));
        favouritesCount.setText(String.valueOf(status.getFavourites_count()));

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

    public BorderPane getParent() {
        return messageBorder;
    }
}

package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Visibility;
import eus.ehu.gleonis.gleonismastodonfx.presentation.comp.MediaAttachmentViewer;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController extends AbstractController {

    private final TootSendData tootSendData = new TootSendData();

    @FXML
    private ToggleGroup menuChoice;

    @FXML
    private Circle accountImage;
    @FXML
    private Label accountUsername;
    @FXML
    private Label accountWebfinger;
    @FXML
    private StackPane sceneContent;
    @FXML
    private TextField searchTextField;
    // Send toot UI part
    @FXML
    private VBox sendTootVBox;
    @FXML
    private GridPane fakeSendGridPane;
    @FXML
    private TextField fakeSendTextField;
    @FXML
    private TextField spoilerTextField;
    @FXML
    private GridPane realSendTootGridPane;
    @FXML
    private TextArea tootContentTextArea;
    @FXML
    private CheckBox visibilityCheckbox;
    @FXML
    private Text charCountText;
    @FXML
    private ScrollPane mediaAttachmentScroll;
    private MediaAttachmentViewer mediaAttachmentViewer;
    private FileChooser fileChooser;
    private boolean sendTootPanelVisible;

    // We don't use initilize because we need to wait for the API to be set
    public void init(Scene scene) {
        // Setup account information (webfinger, username and icon)
        Account account = api.verifyCredentials();

        accountUsername.setText(account.getUsername());
        accountWebfinger.setText("@" + account.getAcct());

        account.getAvatarCachedImage().setImage(accountImage);

        // Redirect to the account of the user by clicking on his avatar, username or webfinger.
        Utils.redirectToAccount(null, accountImage, accountUsername, accountWebfinger);

        // Setup listener for textField to search when enter is pressed
        searchTextField.setOnAction(e -> onSearchClick());

        setupSendTootUI(scene);
    }

    private void setupSendTootUI(Scene scene) {
        // When we click on the fake text field, display the real panel to send toot
        sendTootPanelVisible = false;
        fakeSendTextField.setOnMouseClicked(e -> {
            if (sendTootPanelVisible)
                return;

            gainTootFocus(tootContentTextArea.getText(), Visibility.UNKNOWN, tootSendData.inResponseTo);
        });

        // Hide the real panel to send toot when the user click outside
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (sendTootPanelVisible && !inHierarchyOf(e.getPickResult().getIntersectedNode(), sendTootVBox))
                loseTootFocus();
        });

        // Bind remaining char count

        charCountText.textProperty().bind(tootContentTextArea.textProperty().length().asString().concat("/500"));

        // Setup file chooser
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // TODO add all others extensions supported by mastodon
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp", "*.mp4", "*.webm", "*.mp3", "*.ogg", "*.wav", "*.flac", "*.opus", "*.aac", "*.m4a", "*.oga", "*.wma"),
                new FileChooser.ExtensionFilter("Image", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"),
                new FileChooser.ExtensionFilter("Video", "*.mp4", "*.webm"),
                new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.ogg", "*.wav", "*.flac", "*.opus", "*.aac", "*.m4a", "*.oga", "*.wma")
        );

        // Setup media attachment viewer
        mediaAttachmentViewer = new MediaAttachmentViewer(mediaAttachmentScroll);
        mediaAttachmentScroll.prefWidthProperty().bind(tootContentTextArea.widthProperty().subtract(80));

        resetToot();

        sendTootVBox.getChildren().clear();
        sendTootVBox.getChildren().add(fakeSendGridPane);
    }

    public void setCenter(Region pane) {
        setCenter(pane, true);
    }

    public void uncheckCategory() {
        if (menuChoice.getSelectedToggle() != null)
            menuChoice.getSelectedToggle().setSelected(false);
    }

    public void setCenter(Region pane, boolean closeStream) {
        BorderPane.setAlignment(pane, Pos.TOP_CENTER);

        if (closeStream)
            api.closeStream();

        // We add some paddings so that the send toot panel doesn't hide the content
        //pane.setPadding(new Insets(0, 0, 50, 0));
        StackPane.setMargin(pane, new Insets(0, 0, 50, 0));

        sceneContent.getChildren().clear();
        sceneContent.getChildren().add(pane);
        sceneContent.getChildren().add(sendTootVBox);
    }

    private boolean inHierarchyOf(Node n, Node h) {
        while (n != null) {
            if (n == h)
                return true;

            n = n.getParent();
        }

        return false;
    }

    public void gainTootFocus(String content, Visibility visibility, String inResponseTo) {
        sendTootPanelVisible = true;

        tootSendData.inResponseTo = inResponseTo;

        if (visibility != Visibility.UNKNOWN)
            visibilityCheckbox.setSelected(visibility == Visibility.PRIVATE);

        tootContentTextArea.setText(content);
        sendTootVBox.getChildren().set(0, realSendTootGridPane);

        tootContentTextArea.requestFocus();
    }

    private void loseTootFocus() {
        sendTootPanelVisible = false;

        sendTootVBox.getChildren().clear();
        sendTootVBox.getChildren().add(fakeSendGridPane);
        sceneContent.requestFocus();
    }

    private void resetToot() {
        tootSendData.inResponseTo = null;
        visibilityCheckbox.setSelected(false); // Select: private, Unselect: public
        tootSendData.mediaAttachments.clear();
        mediaAttachmentViewer.clearMedia();
    }

    @FXML
    void onWarningContentClick(ActionEvent event) {
        boolean selected = ((CheckBox) event.getSource()).isSelected();
        if (selected) {
            sendTootVBox.getChildren().add(0, spoilerTextField);
            spoilerTextField.requestFocus();
        } else {
            sendTootVBox.getChildren().remove(0);
            tootContentTextArea.requestFocus();
        }
    }

    @FXML
    void onMediAttachmentClick() {
        List<File> files = fileChooser.showOpenMultipleDialog(getApplication().getStage());

        if (files == null)
            return;

        for (File file : files)
            mediaAttachmentViewer.addMedia(file, tootSendData.mediaAttachments);

        tootSendData.mediaAttachments.addAll(files);
    }

    @FXML
    void onSendToot() {
        String content = tootContentTextArea.getText();

        if (content.isEmpty() && tootSendData.mediaAttachments.isEmpty())
            return;

        Visibility visibility = visibilityCheckbox.isSelected() ? Visibility.PRIVATE : Visibility.PUBLIC;

        Task<Status> task = api.postStatus(content,
                visibility,
                tootSendData.inResponseTo,
                spoilerTextField.getText(),
                tootSendData.mediaAttachments.toArray(File[]::new));

        Integer i = PopupManager.showProgressPopup("Sending Toot...", task.progressProperty());

        task.setOnSucceeded(e -> {
            resetToot();
            loseTootFocus();
            PopupManager.hideProgressPopup(i);
        });

        System.out.println("Toot sent don't freeze the UI");
    }

    @FXML
    void onSearchClick() {
        getApplication().requestSearch(searchTextField.getText());
    }

    @FXML
    void onNotificationsClick() {
        getApplication().requestShowNotification();
    }

    @FXML
    void onSettingsClick() {
        getApplication().requestLoginScreen();
    }

    //*******************************************************************
    // Timelines:
    // - Home
    // - Local
    // - Global/Federated
    // - Trendings
    //
    // *******************************************************************
    @FXML
    void onHomeClick() {
        getApplication().requestShowToots(api.getHomeTimeline(10), 10, false);
    }

    @FXML
    void onLocalTLClick() {
        getApplication().requestShowStreamToots(api.getLocalTimelines());
    }

    @FXML
    void onGlobalTLClick() {
        getApplication().requestShowStreamToots(api.getFederatedTimeline());
    }

    @FXML
    void onTrendingClick() {
        getApplication().requestShowTrending();
    }

    //*******************************************************************
    // Personal categories:
    // - Direct messages
    // - Favourites
    // - Bookmarks
    // - Lists
    //
    // *******************************************************************
    @FXML
    void onDirectMsgClick() {
        getApplication().requestShowConversation();
    }

    @FXML
    void onFavouritesClick() {
        getApplication().requestShowToots(api.getFavourites(10), 10, false);
    }

    @FXML
    void onBookmarksClick() {
        getApplication().requestShowToots(api.getBookmarks(10), 10, false);
    }

    @FXML
    void onListsClick() {

    }

    private static class TootSendData {
        public String inResponseTo;

        public List<File> mediaAttachments;

        public TootSendData() {
            mediaAttachments = new ArrayList<>();
        }
    }
}

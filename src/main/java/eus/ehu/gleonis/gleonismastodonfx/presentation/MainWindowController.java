package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Account;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.StatusVisibility;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

public class MainWindowController extends AbstractController {

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

    private boolean sendTootPanelVisible;

    private StatusVisibility tootVisibility;

    private String inResponseTo;

    // We don't use initilize because we need to wait for the API to be set
    public void init(Scene scene) {
        // Setup account information (webfinger, username and icon)
        Account account = api.verifyCredentials();

        accountUsername.setText(account.getUsername());
        accountWebfinger.setText("@" + account.getAcct());

        account.getAvatarCachedImage().setImage(accountImage);

        accountImage.setOnMouseClicked(e -> getApplication().requestShowAccount(null));

        // Setup listener for textField to search when enter is pressed
        searchTextField.setOnAction(e -> onSearchClick());

        // Display the real panel to send toot.
        fakeSendTextField.setOnMouseClicked(e -> {
            if (sendTootPanelVisible)
                return;

            gainTootFocus("", StatusVisibility.PUBLIC, null);
        });

        // Hide or show the real panel to send toot when the user click in the region of the panel.
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (sendTootPanelVisible && !inHierarchyOf(e.getPickResult().getIntersectedNode(), sendTootVBox))
                loseTootFocus();
        });

        sendTootPanelVisible = false;

        sendTootVBox.getChildren().clear();
        sendTootVBox.getChildren().add(fakeSendGridPane);
    }


    public void setCenter(Node pane) {
        BorderPane.setAlignment(pane, Pos.TOP_CENTER);

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

    public void gainTootFocus(String content, StatusVisibility visibility, String inResponseTo) {
        sendTootPanelVisible = true;
        this.inResponseTo = inResponseTo;
        tootContentTextArea.setText(content);
        sendTootVBox.getChildren().set(0, realSendTootGridPane);
        tootVisibility = visibility;
        tootContentTextArea.requestFocus();
        updateSendTootVisibilityCheckbox();
    }

    private void updateSendTootVisibilityCheckbox() {
        if (tootVisibility == StatusVisibility.PUBLIC) {
            visibilityCheckbox.getStyleClass().set(2, "toot-visibility-public");
            visibilityCheckbox.setSelected(true);
        } else {
            visibilityCheckbox.getStyleClass().set(2, "toot-visibility-direct");
            visibilityCheckbox.setSelected(false);
        }
    }

    private void loseTootFocus() {
        sendTootPanelVisible = false;
        inResponseTo = null;
        tootVisibility = StatusVisibility.PUBLIC;
        sendTootVBox.getChildren().clear();
        sendTootVBox.getChildren().add(fakeSendGridPane);
        sceneContent.requestFocus();
    }

    @FXML
    void onVisibilityClick(ActionEvent event) {
        boolean selected = ((CheckBox) event.getSource()).isSelected();
        if (selected) {
            tootVisibility = StatusVisibility.PUBLIC;
        } else {
            tootVisibility = StatusVisibility.PRIVATE;
        }

        updateSendTootVisibilityCheckbox();
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
    void onSendToot() {
        String content = tootContentTextArea.getText();

        if (content.isEmpty())
            return;

        api.postStatus(content, tootVisibility, inResponseTo, spoilerTextField.getText());
        loseTootFocus();
    }

    @FXML
    void onBookmarksClick() {
        getApplication().requestShowToots(api.getBookmarks(10), 10);
    }

    @FXML
    void onDirectMsgClick() {

    }

    @FXML
    void onFavouritesClick() {
        getApplication().requestShowToots(api.getFavourites(10), 10);
    }

    @FXML
    void onGlobalTLClick() {

    }

    @FXML
    void onHomeClick() {
        getApplication().requestShowToots(api.getHomeTimeline(10), 10);
    }

    @FXML
    void onListsClick() {

    }

    @FXML
    void onLocalTLClick() {

    }

    @FXML
    void onSearchClick() {
        getApplication().requestSearch(searchTextField.getText());
    }

    @FXML
    void onNotificationsClick() {

    }

    @FXML
    void onSettingsClick() {
        getApplication().requestLoginScreen();
    }

    @FXML
    void onTrendingClick() {
        getApplication().requestShowTrending();
    }
}

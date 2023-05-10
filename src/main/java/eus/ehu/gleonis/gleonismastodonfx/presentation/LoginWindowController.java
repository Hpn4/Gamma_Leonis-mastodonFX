package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Token;
import eus.ehu.gleonis.gleonismastodonfx.db.DBAccount;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.List;

public class LoginWindowController extends AbstractController {

    @FXML
    private Button authorizeButton;

    @FXML
    private TextField codeTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private VBox accountsList;

    public void init() {
        // Clear all the element except the first one
        Node node = accountsList.getChildren().get(0);
        accountsList.getChildren().clear();
        accountsList.getChildren().add(node);

        List<DBAccount> accounts = dbManager.getAccounts();

        // "Hardcoded" pane to improve performance
        // (in fact FXML is very very very slow du to reflection, so for this kind of little things it's better to use Java code).
        for (DBAccount account : accounts) {
            HBox accountBox = new HBox();
            accountBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            accountBox.setPadding(new Insets(10));
            accountBox.setSpacing(5);
            accountBox.getStyleClass().add("account-box");

            // Avatar image
            ImageView avatar = new ImageView(new Image(account.getAvatarUrl()));
            avatar.setFitHeight(50);
            avatar.setFitWidth(50);
            accountBox.getChildren().add(avatar);

            // Username label
            Label username = new Label(account.getUsername());
            username.setTextFill(javafx.scene.paint.Color.WHITE);
            accountBox.getChildren().add(username);

            // Last access label
            Label lastAccess = new Label(LocalDate.parse(account.getLast_access()).toString());
            accountBox.getChildren().add(lastAccess);

            accountBox.setOnMouseClicked(event -> {
                if(api.switchUser(account.getToken()))
                    getApplication().requestMainScreen();
                else {
                    dbManager.deleteAccount(account);
                    init();
                    showError("The token has expired or have changed, you need to authorize again.");
                }
            });

            accountsList.getChildren().add(accountBox);
        }
    }

    @FXML
    void onAuthorizeClick() {
        getApplication().getHostServices().showDocument(api.getAuthorizedUserURL());

        authorizeButton.setDisable(true);
        errorLabel.setVisible(false);

        loginButton.setDisable(false);
    }

    private void showError(String label) {
        errorLabel.setVisible(true);
        errorLabel.setText(label);

        authorizeButton.setDisable(false);
        authorizeButton.setText("Generate new authorization");
    }

    @FXML
    void onLoginClick() {
        Token token = api.getToken(codeTextField.getText());

        if (token == null)
            showError("Error getting token. Please try again.");
        else if (!api.addNewUser(dbManager, token))
            showError("Error adding new user. Please try again.");
        else
            getApplication().requestMainScreen();
    }

}

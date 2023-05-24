package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Token;
import eus.ehu.gleonis.gleonismastodonfx.db.DBAccount;
import eus.ehu.gleonis.gleonismastodonfx.db.DBApplication;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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
    private ComboBox<String> appComboBox;

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
        codeTextField.clear();

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
                if (api.setupAPIWithUser(dbManager, account))
                    getApplication().requestMainScreen();
                else {
                    dbManager.deleteAccount(account);
                    init();
                    showError("login.error_expired", true);
                }
            });

            accountsList.getChildren().add(accountBox);
        }

        setupComboBox();
    }

    private void setupComboBox() {
        ObservableList<String> items = FXCollections.observableArrayList();
        String addNew = Utils.getTranslation("login.new_application");

        // Add all applications from the DB
        for (DBApplication app: dbManager.getApplications())
            items.add(app.getDomain());

        items.add(addNew);

        appComboBox.setItems(items);
        appComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldV, newV) -> {
            if(newV == null)
                return;

            if(newV.equals(addNew)) // The last element is to create a new application
                getApplication().requestConfigFileScreen();
            else
                api.useApplication(dbManager.getApplicationByDomain(newV));
        });
    }

    @FXML
    void onAuthorizeClick() {
        if(appComboBox.getSelectionModel().getSelectedItem() == null) {
            showError("login.error_no_app", false);
            return;
        }

        getApplication().getHostServices().showDocument(api.getAuthorizedUserURL());

        authorizeButton.setDisable(true);
        errorLabel.setVisible(false);

        loginButton.setDisable(false);
    }

    private void showError(String key, boolean regen) {
        errorLabel.setVisible(true);
        errorLabel.setText(Utils.getTranslation(key));

        if(regen) {
            authorizeButton.setDisable(false);
            authorizeButton.setText(Utils.getTranslation("login.generate_new"));
        }
    }

    @FXML
    void onLoginClick() {
        if(appComboBox.getSelectionModel().getSelectedItem() == null) {
            showError("login.error_no_app", false);
            return;
        }

        Token token = api.getToken(codeTextField.getText());

        if (token == null)
            showError("login.error_get_token", true);
        else if (!api.addNewUser(dbManager, token))
            showError("login.error_add_user", true);
        else
            getApplication().requestMainScreen();

    }
}

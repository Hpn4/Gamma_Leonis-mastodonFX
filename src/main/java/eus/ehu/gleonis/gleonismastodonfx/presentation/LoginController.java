package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Token;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class LoginController extends AbstractController {

    @FXML
    private Button authorizeButton;

    @FXML
    private TextField codeTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    void onAuthorizeClick() {
        api.authorizeUser();

        authorizeButton.setDisable(true);
        errorLabel.setVisible(false);

        loginButton.setDisable(false);
    }

    @FXML
    void onLoginClick() {
        Token token = api.getToken(codeTextField.getText());

        if (token == null) {
            errorLabel.setVisible(true);
            errorLabel.setText("Error getting token. Please try again.");

            authorizeButton.setDisable(false);
            authorizeButton.setText("Generate new authorization");
        } else {
            api.setupToken(token);
            getApplication().requestMainScreen();
        }
    }

}

package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.utils.PropertiesManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ConfigWindowController extends AbstractController {

    @FXML
    private TextField keyTextField;

    @FXML
    private TextField secretTextField;

    @FXML
    private Label errorLabel;

    @FXML
    void onConfigureClick() {
        if(keyTextField.getText().isEmpty() || secretTextField.getText().isEmpty()) {
            errorLabel.setVisible(true);
            return;
        }

        PropertiesManager.getInstance().setApp(keyTextField.getText(), secretTextField.getText());
        api.initAPI();
        dbManager.initDB();

        getApplication().requestLoginScreen();
    }

}

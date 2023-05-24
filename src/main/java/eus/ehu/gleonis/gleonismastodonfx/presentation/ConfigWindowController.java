package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Application;
import eus.ehu.gleonis.gleonismastodonfx.utils.Utils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ConfigWindowController extends AbstractController {

    @FXML
    private TextField domainTextField;

    @FXML
    private TextField keyTextField;

    @FXML
    private TextField secretTextField;

    @FXML
    private Label errorLabel;

    public void init() {
        domainTextField.clear();
        keyTextField.clear();
        secretTextField.clear();
    }

    @FXML
    void onConfigureClick() {
        if (keyTextField.getText().isEmpty() || secretTextField.getText().isEmpty() || domainTextField.getText().isEmpty()) {
            setErrorMsg("config.error_label");
            return;
        }

        if(dbManager.getApplicationByDomain(domainTextField.getText()) != null) {
            setErrorMsg("config.error_domain");
            return;
        }

        if (!api.createAppAndUse(dbManager, domainTextField.getText(), keyTextField.getText(), secretTextField.getText())) {
            setErrorMsg("config.error_domain");
            return;
        }

        getApplication().requestLoginScreen();
    }

    @FXML
    void onAutoGenerateClick() {
        if(domainTextField.getText().isEmpty()) {
            setErrorMsg("config.error_no_domain");
            return;
        }

        if(dbManager.getApplicationByDomain(domainTextField.getText()) != null) {
            setErrorMsg("config.error_domain");
            return;
        }

        Application app = api.createApplication(domainTextField.getText(), "GLMastodonFX");

        keyTextField.setText(app.getClientId());
        secretTextField.setText(app.getClientSecret());

        onConfigureClick();
    }

    private void setErrorMsg(String key) {
        errorLabel.setText(Utils.getTranslation(key));
        errorLabel.setVisible(true);
    }

}

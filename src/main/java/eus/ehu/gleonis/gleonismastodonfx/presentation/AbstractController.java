package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;

public abstract class AbstractController {

    private MainApplication application;

    public void setApplication(MainApplication application) {
        this.application = application;
    }

    public MainApplication getApplication() {
        return application;
    }
}

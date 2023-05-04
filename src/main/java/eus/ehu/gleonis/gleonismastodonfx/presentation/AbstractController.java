package eus.ehu.gleonis.gleonismastodonfx.presentation;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.db.IDBManager;

public abstract class AbstractController {

    private MainApplication application;

    protected IDBManager dbManager;

    protected API api;

    public void setApplication(MainApplication application) {
        this.application = application;
    }

    public MainApplication getApplication() {
        return application;
    }

    public void setAPI(API api) {
        this.api = api;
    }

    public void setDBManager(IDBManager dbManager) {
        this.dbManager = dbManager;
    }
}

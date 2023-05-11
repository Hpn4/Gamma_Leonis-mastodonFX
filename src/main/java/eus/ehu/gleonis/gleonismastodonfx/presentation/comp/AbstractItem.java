package eus.ehu.gleonis.gleonismastodonfx.presentation.comp;

import eus.ehu.gleonis.gleonismastodonfx.MainApplication;
import eus.ehu.gleonis.gleonismastodonfx.api.API;
import eus.ehu.gleonis.gleonismastodonfx.presentation.scrollable.AbstractScrollableContent;
import javafx.scene.Node;

public abstract class AbstractItem<E> {

    protected API api;

    protected E elem;

    private AbstractScrollableContent<E> parent;

    public AbstractItem(AbstractScrollableContent<E> parent) {
        api = MainApplication.getInstance().getAPI();
        this.parent = parent;
    }

    protected void deleteItemFromUI() {
        if (parent != null)
            parent.deleteItem(elem);
    }

    public void setScrollableContent(AbstractScrollableContent<E> parent) {
        this.parent = parent;
    }

    public abstract Node getParent();
}

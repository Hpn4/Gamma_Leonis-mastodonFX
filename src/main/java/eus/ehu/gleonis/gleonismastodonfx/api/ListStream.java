package eus.ehu.gleonis.gleonismastodonfx.api;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Identifiable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ListStream<E extends Identifiable> {

    private final API api;

    private final String query;

    private final ObservableList<E> list;


    public ListStream(API api, String query, List<E> list) {
        this.api = api;
        this.query = query;
        this.list = FXCollections.observableList(list);
    }

    public ObservableList<E> getElement() {
        return list;
    }

    public List<E> getNextElements(int limit)
    {
        //List<E> nextList = (List<E extends Identifiable>) api.getList(query, limit, list.get(list.size() - 1).getId(), list.get(0).getClass());
        //list.addAll(nextList);

        //return nextList;
        return null;
    }
}

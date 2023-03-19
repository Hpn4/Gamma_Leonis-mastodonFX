package eus.ehu.gleonis.gleonismastodonfx.api;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Identifiable;

import java.util.List;

public class ListStream<E extends Identifiable> {

    private API api;

    private String query;

    private List<E> list;


    public ListStream(API api, String query, List<E> list) {
        this.api = api;
        this.query = query;
        this.list = list;
    }

    public List<E> getElement() {
        return list;
    }

    public List<E> getNextElements(int limit)
    {
        List<E> nextList = api.getList(query, limit, list.get(list.size() - 1).getId());
        list.addAll(nextList);

        return nextList;
    }
}

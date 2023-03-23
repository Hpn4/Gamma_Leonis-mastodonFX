package eus.ehu.gleonis.gleonismastodonfx.api;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ListStream<E> {

    private final API api;

    private final ObservableList<E> list;

    private final String baseUrl;

    private String nextQuery;


    public ListStream(API api, String baseUrl, String link, List<E> list) {
        this.api = api;
        this.list = FXCollections.observableList(list);
        this.baseUrl = baseUrl;

        parsePaginationLink(link);
    }

    protected void parsePaginationLink(String link) {
        if (link == null || !link.contains(",")) // No header or no next link (no links or only prev link)
            nextQuery = null;
        else
        {
            String[] links = link.split(",");
            String nextLink = links[0].contains("next") ? links[0] : links[1];

            String maxId = nextLink.substring(nextLink.indexOf("max_id="), nextLink.indexOf(">"));

            nextQuery = baseUrl + "?" + maxId;
            System.err.println("Debug warning in ListStream#parsePaginationLink. Next query: " + nextQuery);
        }
    }

    public ObservableList<E> getElement() {
        return list;
    }

    public boolean hasNext() {
        return nextQuery != null;
    }

    public List<E> getNextElements(int limit) {
        if (!hasNext()) {
            System.err.println("Debug warning in ListStream#getNextElements: No pagination link");
            return null;
        }

        List<E> nextList = api.updateStream(nextQuery, limit, (Class<E>) list.get(0).getClass(), this);
        list.addAll(nextList);

        return nextList;
    }
}

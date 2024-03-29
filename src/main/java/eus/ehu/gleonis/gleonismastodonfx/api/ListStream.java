package eus.ehu.gleonis.gleonismastodonfx.api;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ListStream<E> {

    private final API api;

    private final ObservableList<E> list;

    private final String baseUrl;

    // When they are no pagination link, this variable will be used for pagination
    private int offset;

    private final boolean supportOffset;

    private SearchList<E> searchList; // Used only for search stream

    private String nextQuery;


    public ListStream(API api, String baseUrl, String link, int limit, boolean supportOffset) {
        this.api = api;
        this.list = FXCollections.observableArrayList();
        this.baseUrl = baseUrl;
        this.supportOffset = supportOffset;
        offset = limit;

        parsePaginationLink(link);
    }

    protected void parsePaginationLink(String link) {
        if(baseUrl == null)
            return;
        if (supportOffset) // No header or no next link (no links or only prev link)
            nextQuery = baseUrl + (baseUrl.contains("?") ? "&" : "?") + "offset=" + offset;
        else if(link != null && link.contains("next")){
            String[] links = link.split(",");
            String nextLink = links[0].contains("next") ? links[0] : links[1];

            String maxId = nextLink.substring(nextLink.indexOf("max_id="), nextLink.indexOf(">"));

            nextQuery = baseUrl + (baseUrl.contains("?") ? "&" : "?") + maxId;
        }
    }

    public ObservableList<E> getElement() {
        return list;
    }

    public void setSearchList(SearchList<E> searchList) {
        this.searchList = searchList;
    }

    public boolean hasNext() {
        return nextQuery != null;
    }

    public void getNextElements(int limit) {
        if (!hasNext())
            return;

        offset += limit;

        if (searchList != null)
            api.updateSearchStream(nextQuery, this, searchList, limit);
        else
            api.updateStream(nextQuery, limit, (Class<E>) list.get(0).getClass(), this);
    }
}

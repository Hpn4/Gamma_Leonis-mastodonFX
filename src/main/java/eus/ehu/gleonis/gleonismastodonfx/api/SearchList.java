package eus.ehu.gleonis.gleonismastodonfx.api;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Search;

import java.util.List;

@FunctionalInterface
public interface SearchList<T> {

    List<T> apply(Search search);
}

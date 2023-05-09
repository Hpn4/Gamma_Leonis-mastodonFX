package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

import java.util.List;

public class Context {

    List<Status> ancestors;

    List<Status> descendants;

    public List<Status> getAncestors() {
        return ancestors;
    }

    public List<Status> getDescendants() {
        return descendants;
    }
}

package eus.ehu.gleonis.gleonismastodonfx.api.apistruct;

import java.util.ArrayList;

public class Tag {

    private String name;

    private ArrayList<TagHistory> history;

    public Tag() {
    }

    public String getName() {
        return name;
    }

    public ArrayList<TagHistory> getHistory() {
        return history;
    }
}

package eus.ehu.gleonis.gleonismastodonfx.api.websocks;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Status;

@FunctionalInterface
public interface StatusListener {

    void onStatus(Status status);
}

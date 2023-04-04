package eus.ehu.gleonis.gleonismastodonfx.api.websocks;

import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Notification;

@FunctionalInterface
public interface NotificationListener {

    void onNotification(Notification notification);
}

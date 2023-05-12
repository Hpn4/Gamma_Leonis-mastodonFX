package eus.ehu.gleonis.gleonismastodonfx.api.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.NotificationType;

import java.lang.reflect.Type;

public class NotificationTypeDeserializer implements JsonDeserializer<NotificationType> {
    @Override
    public NotificationType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        for (NotificationType notificationType: NotificationType.values())
            if (notificationType.getType().equals(json.getAsString()))
                return notificationType;

        return NotificationType.UNKNOWN;
    }
}

package eus.ehu.gleonis.gleonismastodonfx.api.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.MediaAttachmentType;

import java.lang.reflect.Type;

public class MediaAttachmentTypeDeserializer implements JsonDeserializer<MediaAttachmentType> {
    @Override
    public MediaAttachmentType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return MediaAttachmentType.valueOf(json.getAsString().toUpperCase());
    }
}

package eus.ehu.gleonis.gleonismastodonfx.api.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import eus.ehu.gleonis.gleonismastodonfx.api.apistruct.Visibility;

import java.lang.reflect.Type;

public class VisibilityDeserializer implements JsonDeserializer<Visibility> {
    @Override
    public Visibility deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Visibility.valueOf(json.getAsString().toUpperCase());
    }
}

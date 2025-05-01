package com.apt.collaborative_editor.Model.Util;

import com.google.gson.*;
import com.google.gson.JsonParseException;
import org.springframework.stereotype.Component;
import java.lang.reflect.Type;

import com.apt.collaborative_editor.Model.CRDT.Operations.CRDTOperation;
import com.apt.collaborative_editor.Model.CRDT.Operations.DeleteOperation;
import com.apt.collaborative_editor.Model.CRDT.Operations.InsertOperation;

@Component
public class CRDTSerializer {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(CRDTOperation.class, new OperationAdapter())
            .create();

    public String serialize(CRDTOperation operation) {
        return gson.toJson(operation);
    }

    public CRDTOperation deserialize(String json) throws JsonParseException {
        try {
            return gson.fromJson(json, CRDTOperation.class);
        } catch (JsonSyntaxException e) {
            throw new JsonParseException("Invalid JSON: " + e.getMessage());
        }
    }

    private static class OperationAdapter implements JsonSerializer<CRDTOperation>,
            JsonDeserializer<CRDTOperation> {
        @Override
        public JsonElement serialize(CRDTOperation src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.addProperty("type", src.getClass().getSimpleName());
            obj.add("data", context.serialize(src));
            return obj;
        }

        @Override
        public CRDTOperation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            JsonObject obj = json.getAsJsonObject();
            String type = obj.get("type").getAsString();
            JsonElement data = obj.get("data");

            switch (type) {
                case "InsertOperation":
                    return context.deserialize(data, InsertOperation.class);
                case "DeleteOperation":
                    return context.deserialize(data, DeleteOperation.class);
                default:
                    throw new JsonParseException("Unknown operation type: " + type);
            }
        }
    }
}
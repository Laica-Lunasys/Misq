package net.synchthia.misq.gate;

import com.google.gson.*;
import net.synchthia.misq.location.Range;
import net.synchthia.misq.location.StaticBlockLocation;

import java.lang.reflect.Type;
import java.util.UUID;

public class GateAdapter implements JsonSerializer<Gate>, JsonDeserializer<Gate> {
    @Override
    public JsonElement serialize(Gate gate, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.add("name", new JsonPrimitive(gate.getName()));
        obj.add("destination", new JsonPrimitive(gate.getDestination()));
        obj.add("worldUID", context.serialize(gate.getWorldUID(), UUID.class));
        obj.add("gateArea", context.serialize(gate.getGateArea(), Range.class));
        obj.add("signLocation", context.serialize(gate.getSignLocation(), StaticBlockLocation.class));
        return obj;
    }

    @Override
    public Gate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String name = obj.get("name").getAsString();
        String destination = obj.get("destination").getAsString();
        UUID worldUID = UUID.fromString(obj.get("worldUID").getAsString());
        Range gateArea = context.deserialize(obj.get("gateArea"), Range.class);
        StaticBlockLocation signLocation = context.deserialize(obj.get("signLocation"), StaticBlockLocation.class);

        return new Gate(name, destination, worldUID, gateArea, signLocation);
    }
}

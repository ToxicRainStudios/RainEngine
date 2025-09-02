package com.toxicrain.rainengine.core.json.gamestate;

import java.util.HashMap;
import java.util.Map;

/**
 * GameState represents a dynamic set of fields for saving/loading game state.
 */
public class GameState {

    private final Map<String, Object> fields = new HashMap<>();

    public void setField(String key, Object value) {
        fields.put(key, value);
    }

    public Object getField(String key) {
        return fields.get(key);
    }

    public <T> T getField(String key, Class<T> type) {
        Object value = fields.get(key);
        if (type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public Map<String, Object> getAllFields() {
        return fields;
    }

    public void setAllFields(Map<String, Object> newFields) {
        fields.clear();
        fields.putAll(newFields);
    }
}

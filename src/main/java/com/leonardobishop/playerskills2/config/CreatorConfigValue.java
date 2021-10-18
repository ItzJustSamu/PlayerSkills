package com.leonardobishop.playerskills2.config;

/**
 * Used to generate default configurations and for the in game editor.
 */
public class CreatorConfigValue {

    private final String key;
    private final Object def;
    private Object value;

    public CreatorConfigValue(String key, Object value, Object def) {
        this.key = key;
        this.value = value;
        this.def = def;
    }

    public CreatorConfigValue(String key, Object value) {
        this.key = key;
        this.value = value;
        this.def = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getDefault() {
        return def;
    }
}

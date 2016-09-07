package com.tendersaucer.joe.level.script;

import com.badlogic.gdx.maps.MapProperties;

/**
 * Created by Alex on 8/14/2016.
 */
public class TiledScriptDefinition extends ScriptDefinition {

    private final MapProperties properties;

    public TiledScriptDefinition(String name, String type, MapProperties properties) {
        super(name, type);

        this.properties = properties;
    }

    @Override
    public Object getProperty(String key) {
        return properties.get(key);
    }
}

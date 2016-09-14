package com.tendersaucer.joe.level.script;

import com.tendersaucer.joe.util.Definition;

/**
 * Interface for scripts
 * <p/>
 * Created by Alex on 4/9/2016.
 */
public abstract class ScriptDefinition extends Definition {

    protected final String id;
    protected final String type;

    public ScriptDefinition(String id, String type) {
        super();

        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}

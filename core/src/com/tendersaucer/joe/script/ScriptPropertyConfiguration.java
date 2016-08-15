package com.tendersaucer.joe.script;

import com.tendersaucer.joe.util.PropertyConfiguration;

/**
 * Created by Alex on 4/24/2016.
 */
public final class ScriptPropertyConfiguration extends PropertyConfiguration {

    private static final ScriptPropertyConfiguration instance = new ScriptPropertyConfiguration();

    private ScriptPropertyConfiguration() {
        super("script.json", "scripts", "script");
    }

    public static ScriptPropertyConfiguration getInstance() {
        return instance;
    }
}

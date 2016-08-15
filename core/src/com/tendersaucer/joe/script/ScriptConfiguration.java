package com.tendersaucer.joe.script;

import com.tendersaucer.joe.util.Configuration;

/**
 * Created by Alex on 4/24/2016.
 */
public final class ScriptConfiguration extends Configuration {

    private static final ScriptConfiguration instance = new ScriptConfiguration();

    private ScriptConfiguration() {
        super("script.json", "scripts", "script");
    }

    public static ScriptConfiguration getInstance() {
        return instance;
    }
}

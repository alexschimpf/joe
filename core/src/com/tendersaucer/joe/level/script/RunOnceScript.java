package com.tendersaucer.joe.level.script;

import com.badlogic.gdx.Gdx;

/**
 * A script that only executes once
 * <p/>
 * Created by Alex on 4/24/2016.
 */
public abstract class RunOnceScript extends Script {

    private float elapsed;

    // If -1, the script is not run until set active.
    private final float delay;

    protected RunOnceScript(ScriptDefinition definition) {
        super(definition);

        delay = definition.getFloatProperty("delay");
    }

    @Override
    public boolean update() {
        if (delay >= 0) {
            elapsed += Gdx.graphics.getDeltaTime() * 1000;
            if (elapsed >= delay) {
                tick();
                return true;
            }
        }

        return false;
    }
}

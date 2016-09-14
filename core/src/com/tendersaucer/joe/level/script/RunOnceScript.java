package com.tendersaucer.joe.level.script;

import com.badlogic.gdx.Gdx;

/**
 * A script that only executes once
 * <p/>
 * Created by Alex on 4/24/2016.
 */
public abstract class RunOnceScript extends Script {

    private Float elapsed;
    private final float delay;

    protected RunOnceScript(ScriptDefinition definition) {
        super(definition);

        delay = definition.getFloatProperty("delay");
    }

    @Override
    public boolean update() {
        if (delay > 0) {
            if (elapsed == null) {
                elapsed = 0f;
                return false;
            } else if (elapsed > delay) {
                tick();
            } else {
                return false;
            }
        } else {
            elapsed += Gdx.graphics.getDeltaTime() * 1000;
            tick();
        }

        return true;
    }
}

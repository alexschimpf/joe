package com.tendersaucer.joe.script;

import com.badlogic.gdx.utils.TimeUtils;

/**
 * A script that only executes once
 * <p/>
 * Created by Alex on 4/24/2016.
 */
public abstract class RunOnceScript extends Script {

    private Long startTime;
    private final float delay;

    protected RunOnceScript(ScriptDefinition definition) {
        super(definition);

        delay = definition.getFloatProperty("delay");
    }

    @Override
    public boolean update() {
        if (delay > 0) {
            if (startTime == null) {
                startTime = TimeUtils.millis();
                return false;
            } else if (TimeUtils.timeSinceMillis(startTime) > delay) {
                tick();
            } else {
                return false;
            }
        } else {
            tick();
        }

        return true;
    }
}

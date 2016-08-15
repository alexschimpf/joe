package com.tendersaucer.joe.script;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Timer;

/**
 * A script that only executes once
 * <p/>
 * Created by Alex on 4/24/2016.
 */
public abstract class RunOnce extends Script {

    private boolean isScheduled;
    private final float delay;

    protected RunOnce(ScriptDefinition definition) {
        super(definition);

        delay = definition.getFloatProperty("delay");
        isScheduled = false;
    }

    @Override
    public final boolean update() {
        if (isDone) {
            return true;
        }

        if (delay > 0) {
            if (!isScheduled) {
                isScheduled = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                tick();
                                setDone();
                            }
                        });
                    }
                }, delay / 1000);
            }
        } else {
            tick();
            return true;
        }

        return false;
    }
}

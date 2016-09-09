package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.Gdx;
import com.tendersaucer.joe.IRender;
import com.tendersaucer.joe.IUpdate;

/**
 * Created by Alex on 9/9/2016.
 */
public abstract class Tween implements IUpdate {

    public enum State {
        ACTIVE, PAUSED, STOPPED, DONE
    }

    protected float elapsed;
    protected State state;
    protected Float interval;
    protected IRender target;

    public Tween(IRender target) {
        this.target = target;
        this.interval = null;

        elapsed = 0;
        state = State.STOPPED;
    }

    public Tween(IRender target, Float interval) {
        this.target = target;
        this.interval = interval;

        elapsed = 0;
        state = State.STOPPED;
    }

    @Override
    public boolean update() {
        if (state == State.STOPPED) {
            elapsed = 0;
            return false;
        }
        if (state == State.PAUSED) {
            return false;
        }

        if (interval != null) {
            elapsed += Gdx.graphics.getDeltaTime();
            if (elapsed > interval) {
                setState(State.DONE);
            }
        }

        return state == State.DONE;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void onDone() {
    }
}

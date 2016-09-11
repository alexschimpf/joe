package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.Gdx;
import com.tendersaucer.joe.IUpdate;
import com.tendersaucer.joe.level.entity.RenderedEntity;

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
    protected RenderedEntity target;

    public Tween() {
        this.interval = null;

        elapsed = 0;
        state = State.STOPPED;
    }

    public Tween(Float interval) {
        this.interval = interval;

        elapsed = 0;
        state = State.STOPPED;
    }

    public static ParallelTween parallel(Tween... tweens) {
        return new ParallelTween(tweens);
    }

    public static SequenceTween sequence(Tween... tweens) {
        return new SequenceTween(tweens);
    }

//    public static AlphaTween alpha(float start, float end, float duration) {
//
//    }

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

    /**
     * This should be called in renderedEntity.addTween
     * @param target
     */
    public void setTarget(RenderedEntity target) {
        this.target = target;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void onDone() {
    }
}

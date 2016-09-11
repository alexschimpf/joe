package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.tendersaucer.joe.IUpdate;
import com.tendersaucer.joe.util.ConversionUtils;

/**
 * Created by Alex on 9/9/2016.
 */
public abstract class Tween implements IUpdate {

    public enum State {
        INACTIVE, ACTIVE, DONE
    }

    protected float elapsed;
    protected State state;
    protected Float interval;
    protected ITweenable target;

    public Tween() {
        this.interval = null;

        elapsed = 0;
        state = State.INACTIVE;
    }

    public Tween(Float interval) {
        this.interval = interval;

        elapsed = 0;
        state = State.INACTIVE;
    }

    public static float lerp(float start, float end, float progress) {
        float min = start < end ? start : end;
        float max = start > end ? start : end;
        return MathUtils.clamp(MathUtils.lerp(start, end, progress), min, max);
    }

    public static LoopTween loop(Tween tween) {
        return new LoopTween(tween);
    }

    public static LoopTween loop(Tween tween, Integer numLoops) {
        return new LoopTween(tween, numLoops);
    }

    public static ParallelTween parallel(Tween... tweens) {
        return new ParallelTween(tweens);
    }

    public static SequenceTween sequence(Tween... tweens) {
        return new SequenceTween(tweens);
    }

    public static AlphaTween alpha(float startAlpha, float endAlpha, float interval) {
        return new AlphaTween(startAlpha, endAlpha, interval);
    }

    public static ColorTween color(Color startColor, Color endColor, float interval) {
        return new ColorTween(startColor, endColor, interval);
    }

    public static SizeTween size(float startWidth, float startHeight, float endWidth, float endHeight,
                                 float interval) {
        return new SizeTween(startWidth, startHeight, endWidth, endHeight, interval);
    }

    @Override
    public boolean update() {
        if (state == State.INACTIVE) {
            return false;
        }

        if (interval != null) {
            elapsed += ConversionUtils.s2ms(Gdx.graphics.getDeltaTime());
            if (elapsed > interval) {
                setState(State.DONE);
            }
        }

        tick();

        return state == State.DONE;
    }

    /**
     * This should be called in renderedEntity.addTween
     * @param target
     */
    public void setTarget(ITweenable target) {
        this.target = target;
    }

    public Tween setState(State state) {
        this.state = state;

        return this;
    }

    public State getState() {
        return state;
    }

    public void reset() {
        elapsed = 0;
        state = State.INACTIVE;
    }

    public void onDone() {
    }

    protected abstract void tick();
}

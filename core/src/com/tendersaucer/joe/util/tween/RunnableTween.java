package com.tendersaucer.joe.util.tween;

/**
 * Can be used as a MainThread-based alternative to the Timer
 * <p/>
 * Created by Alex on 9/12/2016.
 */
public class RunnableTween extends Tween {

    protected final ITweenRunnable runnable;

    protected RunnableTween(ITweenRunnable runnable, float interval) {
        super(interval);

        this.runnable = runnable;
    }

    @Override
    public void tick() {
        runnable.update(elapsed, interval, elapsed / interval);
    }
}

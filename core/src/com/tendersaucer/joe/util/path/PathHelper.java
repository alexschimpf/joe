package com.tendersaucer.joe.util.path;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by alexschimpf on 8/25/16.
 */
public abstract class PathHelper {

    protected static final float MIN_SPEED = 0.0001f;

    protected boolean loops;
    protected final Vector2 velocity;

    public PathHelper() {
        this(false);
    }

    public PathHelper(boolean loops) {
        this.loops = loops;
        velocity = new Vector2();
    }

    /**
     * Returns the velocity at a point in time, given path and total trip duration
     * @param duration total path duration in ms
     * @param age progress through path in ms
     * @return velocity in distance/ms
     */
    public abstract Vector2 getVelocity(float duration, float age);
}

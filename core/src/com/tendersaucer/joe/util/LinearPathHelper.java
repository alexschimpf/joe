package com.tendersaucer.joe.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Determines velocity at a point in time, given path and total trip duration
 *
 * Created by Alex on 5/5/2016.
 */
public final class LinearPathHelper extends PathHelper {

    private final Array<Vector2> legs;
    private float totalDistance;

    public LinearPathHelper() {
        this(false);
    }

    public LinearPathHelper(boolean loops) {
        super(loops);

        legs = new Array<Vector2>();
    }

    public LinearPathHelper(Array<Vector2> legs) {
        this();

        setPath(legs);
    }

    public LinearPathHelper(Array<Vector2> legs, boolean loops) {
        this(loops);

        setPath(legs);
    }

    @Override
    public Vector2 getVelocity(float duration, float age) {
        float speed = totalDistance / duration;
        float distanceCovered = speed * age;
        if (loops) {
            distanceCovered = distanceCovered % totalDistance;
        }

        for (int i = 0; i < legs.size; i++) {
            Vector2 leg = legs.get(i);
            distanceCovered -= leg.len();
            if (distanceCovered < 0) {
                float theta = leg.angleRad();
                velocity.set(MathUtils.cos(theta), MathUtils.sin(theta)).scl(speed);
                if (Math.abs(velocity.x) < MIN_SPEED) {
                    velocity.x = 0;
                }
                if (Math.abs(velocity.y) < MIN_SPEED) {
                    velocity.y = 0;
                }

                break;
            }
        }

        return velocity;
    }

    /**
     * NOTE: Each vector represents a "leg" (i.e. (dx, dy)) of the path.
     * @param legs
     */
    public void setPath(Array<Vector2> legs) {
        this.legs.clear();
        this.legs.add(new Vector2(0, 0));
        this.legs.addAll(legs);

        totalDistance = 0;
        for (Vector2 leg : legs) {
            totalDistance += leg.len();
        }
    }
}

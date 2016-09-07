package com.tendersaucer.joe.util.path;

import com.badlogic.gdx.math.Vector2;

/**
 * TODO: Need to refactor this hierarchy first.
 *
 * Created by Alex on 8/7/2016.
 */
public final class CirclePathHelper extends PathHelper {

    private float radius;

    public CirclePathHelper() {
        super();
    }

    public CirclePathHelper(float radius) {
        super();

        this.radius = radius;
    }

    public CirclePathHelper(float radius, boolean loops) {
        super(loops);

        this.radius = radius;
    }

    @Override
    public Vector2 getVelocity(float duration, float age) {
//        float circumference = MathUtils.PI * (float)Math.pow(radius, 2);
//        float angularSpeed =
//        float distanceCovered = speed * age;
//        if (loops) {
//            distanceCovered = distanceCovered % totalDistance;
//        }
//
//        return velocity;

        return null;
    }
}

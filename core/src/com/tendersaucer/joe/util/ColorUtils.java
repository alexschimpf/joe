package com.tendersaucer.joe.util;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Alex on 8/25/2016.
 */
public final class ColorUtils {

    private ColorUtils() {
    }

    public static double dist(Color x, Color y) {
        return Math.pow(x.r - y.r, 2) + Math.pow(x.g - y.g, 2) + Math.pow(x.b - y.b, 2);
    }

    public static void shade(Color color, float amount) {
        color.mul(amount).clamp();
    }
}
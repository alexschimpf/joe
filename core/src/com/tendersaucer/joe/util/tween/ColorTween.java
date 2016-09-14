package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by Alex on 9/9/2016.
 */
public class ColorTween extends Tween {

    protected final Color startColor;
    protected final Color endColor;

    protected ColorTween(Color startColor, Color endColor, float interval) {
        super(interval);

        this.startColor = new Color(startColor);
        this.endColor = new Color(endColor);
    }

    @Override
    public void tick() {
        float r = lerp(startColor.r, endColor.r, elapsed / interval);
        float g = lerp(startColor.g, endColor.g, elapsed / interval);
        float b = lerp(startColor.b, endColor.b, elapsed / interval);
        float a = lerp(startColor.a, endColor.a, elapsed / interval);
        target.getSprite().setColor(r, g, b, a);
    }
}

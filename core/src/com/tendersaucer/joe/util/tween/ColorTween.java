package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Alex on 9/9/2016.
 */
public class ColorTween extends Tween {

    protected Color startColor;
    protected Color endColor;

    protected ColorTween(Color startColor, Color endColor, float interval) {
        super(interval);

        this.startColor = new Color(startColor);
        this.endColor = new Color(endColor);
    }

    @Override
    public void tick() {
        float r = Tween.lerp(startColor.r, endColor.r, elapsed / interval);
        float g = Tween.lerp(startColor.g, endColor.g, elapsed / interval);
        float b = Tween.lerp(startColor.b, endColor.b, elapsed / interval);
        float a = Tween.lerp(startColor.a, endColor.a, elapsed / interval);
        target.getSprite().setColor(r, g, b, a);
    }
}

package com.tendersaucer.joe.parallax;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.tendersaucer.joe.IRender;

/**
 * Layer in ParallaxBackground
 * <p/>
 * Created by Alex on 4/9/2016.
 */
public abstract class ParallaxLayer implements IRender {

    protected final float parallaxRatio;
    protected final Vector2 topLeft;

    public ParallaxLayer(float parallaxRatio) {
        this.parallaxRatio = parallaxRatio;
        topLeft = new Vector2();
    }

    public abstract float getWidth();

    public abstract float getHeight();

    public abstract void setColor(Color color);

    public void setTopLeft(float left, float top) {
        topLeft.set(left, top);
    }

    public float getParallaxRatio() {
        return parallaxRatio;
    }
}

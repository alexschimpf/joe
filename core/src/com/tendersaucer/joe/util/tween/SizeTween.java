package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Alex on 9/9/2016.
 */
public class SizeTween extends Tween {

    protected float startWidth;
    protected float startHeight;
    protected float endWidth;
    protected float endHeight;

    protected SizeTween(float startWidth, float startHeight, float endWidth, float endHeight,
                     float interval) {
        super(interval);

        this.startWidth = startWidth;
        this.startHeight = startHeight;
        this.endWidth = endWidth;
        this.endHeight = endHeight;
    }

    @Override
    public void tick() {
        float width = Tween.lerp(startWidth, endWidth, elapsed / interval);
        float height = Tween.lerp(startHeight, endHeight, elapsed / interval);
        target.getSprite().setSize(width, height);
    }
}

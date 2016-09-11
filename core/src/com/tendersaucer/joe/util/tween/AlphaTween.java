package com.tendersaucer.joe.util.tween;

/**
 * Created by Alex on 9/9/2016.
 */
public class AlphaTween extends Tween {

    protected float startAlpha;
    protected float endAlpha;

    protected AlphaTween(float startAlpha, float endAlpha, float interval) {
        super(interval);

        this.startAlpha = startAlpha;
        this.endAlpha = endAlpha;
    }

    @Override
    public void tick() {
        target.getSprite().setAlpha(lerp(startAlpha, endAlpha, elapsed / interval));
    }
}

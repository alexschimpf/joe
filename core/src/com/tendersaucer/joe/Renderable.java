package com.tendersaucer.joe;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tendersaucer.joe.util.tween.Tween;

/**
 * Created by Alex on 9/10/2016.
 */
public abstract class Renderable implements IRender {

    protected Tween tween;

    public void render(SpriteBatch spriteBatch) {
        if (tween != null && tween.update()) {
            tween.onDone();
        }
    }

    public void setTween(Tween tween) {
        this.tween = tween;
    }

    public Tween getTween() {
        return tween;
    }
}

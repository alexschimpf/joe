package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.IRender;

import java.util.Iterator;

/**
 * Implements the tween management for ITweenable
 *
 * Created by Alex on 9/10/2016.
 */
public abstract class Tweenable implements ITweenable, IRender {

    protected Array<Tween> tweens;

    public Tweenable(Tween... tweens) {
        if (tweens.length > 0) {
            this.tweens = new Array<Tween>(tweens);
        }
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        if (tweens != null) {
            Iterator<Tween> tweensIter = tweens.iterator();
            while (tweensIter.hasNext()) {
                Tween tween = tweensIter.next();
                if (tween.update()) {
                    tween.onDone();
                    tweensIter.remove();
                }
            }
        }
    }

    public Tween getTween(int i) {
        return tweens.get(i);
    }

    public Array<Tween> getTweens() {
        return tweens;
    }

    public Tweenable addTween(Tween tween) {
        if (tweens == null) {
            tweens = new Array<Tween>();
        }

        tween.setTarget(this);
        tweens.add(tween);

        return this;
    }

    public Tweenable removeTween(Tween tween) {
        if (tweens != null) {
            tweens.removeValue(tween, true);
        }

        return this;
    }

    public void clearTweens() {
        if (tweens != null) {
            tweens.clear();
        }
    }
}

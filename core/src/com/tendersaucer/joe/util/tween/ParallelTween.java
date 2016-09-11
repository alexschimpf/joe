package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * Created by Alex on 9/9/2016.
 */
public class ParallelTween extends Tween {

    protected Array<Tween> tweens;

    protected ParallelTween(Tween... tweens) {
        super();

        this.tweens = new Array<Tween>(tweens);
    }

    protected ParallelTween(Float interval) {
        super(interval);
    }

    @Override
    public boolean update() {
        Iterator<Tween> tweensIter = tweens.iterator();
        while (tweensIter.hasNext()) {
            Tween tween = tweensIter.next();
            if (tween.update()) {
                tween.onDone();
            }
        }

        return super.update();
    }
}

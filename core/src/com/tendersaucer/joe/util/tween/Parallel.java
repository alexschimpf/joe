package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.IRender;

import java.util.Iterator;

/**
 * Created by Alex on 9/9/2016.
 */
public class Parallel extends Tween {

    protected Array<Tween> tweens;

    public Parallel(IRender target, Tween... tweens) {
        super(target);

        this.tweens = new Array<Tween>(tweens);
    }

    private Parallel(IRender target, Float interval) {
        super(target, interval);
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

package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.IRender;

/**
 * Created by Alex on 9/9/2016.
 */
public class Sequence extends Tween {

    protected Array<Tween> tweens;

    public Sequence(IRender target, Tween... tweens) {
        super(target);

        this.tweens = new Array<Tween>(tweens);
    }

    private Sequence(IRender target, Float interval) {
        super(target, interval);
    }

    @Override
    public boolean update() {
        if (tweens.size > 0) {
            Tween tween = tweens.first();
            if (tween.update()) {
                tween.onDone();
                tweens.removeIndex(0);
            }
        }

        return super.update();
    }
}

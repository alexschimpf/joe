package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.level.entity.RenderedEntity;

/**
 * Created by Alex on 9/9/2016.
 */
public class SequenceTween extends Tween {

    protected Array<Tween> tweens;

    protected SequenceTween(Tween... tweens) {
        super();

        this.tweens = new Array<Tween>(tweens);
    }

    protected SequenceTween(RenderedEntity target, Float interval) {
        super(interval);
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

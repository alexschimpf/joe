package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.level.entity.RenderedEntity;

/**
 * Created by Alex on 9/9/2016.
 */
public class SequenceTween extends Tween {

    protected int index;
    protected Array<Tween> tweens;

    protected SequenceTween(Tween... tweens) {
        super();

        index = 0;
        this.tweens = new Array<Tween>(tweens);
        for (Tween tween : tweens) {
            tween.setState(State.ACTIVE);
        }
    }

    @Override
    public void tick() {
        if (index >= tweens.size) {
            state = State.DONE;
        } else {
            Tween tween = tweens.get(index);
            if (tween.update()) {
                tween.onDone();
                index++;
            }
        }
    }

    @Override
    public void setTarget(RenderedEntity target) {
        super.setTarget(target);

        for (Tween tween : tweens) {
            tween.setTarget(target);
        }
    }

    @Override
    public void reset() {
        super.reset();

        index = 0;
        for (Tween tween : tweens) {
            tween.reset();
            tween.setState(State.ACTIVE);
        }
    }
}

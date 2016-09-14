package com.tendersaucer.joe.util.tween;

import com.badlogic.gdx.utils.Array;

/**
 * Created by Alex on 9/9/2016.
 */
public class ParallelTween extends Tween {

    protected int numDone;
    protected final Array<Tween> tweens;

    protected ParallelTween(Tween... tweens) {
        super();

        numDone = 0;
        this.tweens = new Array<Tween>(tweens);
        for (Tween tween : tweens) {
            tween.setState(State.ACTIVE);
        }
    }

    @Override
    public void tick() {
        for (Tween tween : tweens) {
            if (tween.getState() == State.ACTIVE && tween.update()) {
                tween.onDone();
                numDone++;
            }
        }

        if (numDone >= tweens.size) {
            state = State.DONE;
        }
    }

    @Override
    public void setTarget(ITweenable target) {
        super.setTarget(target);

        for (Tween tween : tweens) {
            tween.setTarget(target);
        }
    }

    @Override
    public void reset() {
        super.reset();

        numDone = 0;
        for (Tween tween : tweens) {
            tween.reset();
            tween.setState(State.ACTIVE);
        }
    }
}

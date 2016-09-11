package com.tendersaucer.joe.util.tween;

/**
 * Created by Alex on 9/10/2016.
 */
public class LoopTween extends Tween {

    protected int loopNum;
    protected final Tween tween;
    protected final Integer numLoops;

    protected LoopTween(Tween tween) {
        this(tween, null);
    }

    protected LoopTween(Tween tween, Integer numLoops) {
        super();

        this.tween = tween;
        this.numLoops = numLoops;
        this.loopNum = 0;

        tween.setState(State.ACTIVE);
    }

    @Override
    public void tick() {
        if (tween.update()) {
            if (numLoops == null) {
                reset();
                state = State.ACTIVE;
            } else {
                loopNum++;
                if (loopNum >= numLoops) {
                    state = State.DONE;
                } else {
                    tween.reset();
                    tween.setState(State.ACTIVE);
                }
            }
        }
    }

    @Override
    public void setTarget(ITweenable target) {
        super.setTarget(target);

        tween.setTarget(target);
    }

    @Override
    public void reset() {
        super.reset();

        loopNum = 0;
        tween.reset();
        tween.setState(State.ACTIVE);
    }
}

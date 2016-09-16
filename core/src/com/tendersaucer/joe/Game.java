package com.tendersaucer.joe;

import com.tendersaucer.joe.screen.Driver;

/**
 * Game entry point
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class Game extends com.badlogic.gdx.Game {

    public static Game instance;

    public enum State {

        WAIT_FOR_INPUT, RUNNING, PAUSED, LEVEL_COMPLETE, ITERATION_COMPLETE, SHUTDOWN
    }

    @Override
    public void create() {
        instance = this;
        setScreen(new Driver(this));
    }
}

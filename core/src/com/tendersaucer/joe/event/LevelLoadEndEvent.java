package com.tendersaucer.joe.event;

import com.tendersaucer.joe.level.ILevelLoadable;

/**
 * Created by Alex on 5/5/2016.
 */
public class LevelLoadEndEvent extends Event<com.tendersaucer.joe.event.listeners.ILevelLoadEndListener> {

    private final ILevelLoadable loadable;

    public LevelLoadEndEvent(ILevelLoadable loadable) {
        this.loadable = loadable;
    }

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.ILevelLoadEndListener listener) {
        listener.onLevelLoadEnd(loadable);
    }
}

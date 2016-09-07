package com.tendersaucer.joe.event;

import com.tendersaucer.joe.level.ILevelLoadable;

/**
 * Created by Alex on 5/5/2016.
 */
public class LevelLoadBeginEvent extends Event<com.tendersaucer.joe.event.listeners.ILevelLoadBeginListener> {

    private ILevelLoadable loadable;

    public LevelLoadBeginEvent(ILevelLoadable loadable) {
        this.loadable = loadable;
    }

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.ILevelLoadBeginListener listener) {
        listener.onLevelLoadBegin(loadable);
    }
}

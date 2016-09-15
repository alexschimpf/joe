package com.tendersaucer.joe.event;

import com.tendersaucer.joe.event.listeners.ILevelLoadEndListener;
import com.tendersaucer.joe.event.listeners.INextLevelReadyListener;
import com.tendersaucer.joe.level.ILevelLoadable;
import com.tendersaucer.joe.level.Level;

/**
 * Created by alexschimpf on 9/15/16.
 */
public class NextLevelReadyEvent extends Event<INextLevelReadyListener> {

    private final Level nextLevel;
    private final ILevelLoadable loadable;

    public NextLevelReadyEvent(Level nextLevel, ILevelLoadable loadable) {
        this.nextLevel = nextLevel;
        this.loadable = loadable;
    }

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.INextLevelReadyListener listener) {
        listener.onNextLevelReady(nextLevel, loadable);
    }
}

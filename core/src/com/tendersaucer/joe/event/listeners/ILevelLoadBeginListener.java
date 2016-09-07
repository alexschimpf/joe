package com.tendersaucer.joe.event.listeners;

import com.tendersaucer.joe.level.ILevelLoadable;

/**
 * Interface for listening to level load begin events
 * <p/>
 * Created by Alex on 4/23/2016.
 */
public interface ILevelLoadBeginListener {

    /**
     * Fired by Level before any loading occurs
     */
    void onLevelLoadBegin(ILevelLoadable loadable);
}

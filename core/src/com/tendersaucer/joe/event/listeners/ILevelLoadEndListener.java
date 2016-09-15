package com.tendersaucer.joe.event.listeners;

import com.tendersaucer.joe.level.ILevelLoadable;

/**
 * Interface for listening to level load end events
 * <p/>
 * Created by Alex on 4/23/2016.
 */
public interface ILevelLoadEndListener {

    /**
     * Fired by Level after loading is complete
     * @param loadable
     */
    void onLevelLoadEnd(ILevelLoadable loadable);
}

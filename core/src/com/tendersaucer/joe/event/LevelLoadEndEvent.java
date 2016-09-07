package com.tendersaucer.joe.event;

/**
 * Created by Alex on 5/5/2016.
 */
public class LevelLoadEndEvent extends Event<com.tendersaucer.joe.event.listeners.ILevelLoadEndListener> {

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.ILevelLoadEndListener listener) {
        listener.onLevelLoadEnd();
    }
}

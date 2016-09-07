package com.tendersaucer.joe.event;

import com.tendersaucer.joe.Game;

/**
 * Created by Alex on 7/22/2016.
 */
public class GameStateChangeEvent extends Event<com.tendersaucer.joe.event.listeners.IGameStateChangeListener> {

    private Game.State oldEvent;
    private Game.State newEvent;

    public GameStateChangeEvent(Game.State oldEvent, Game.State newEvent) {
        this.oldEvent = oldEvent;
        this.newEvent = newEvent;
    }

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.IGameStateChangeListener listener) {
        listener.onGameStateChange(oldEvent, newEvent);
    }
}

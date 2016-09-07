package com.tendersaucer.joe.event;

import com.tendersaucer.joe.GameState;

/**
 * Created by Alex on 7/22/2016.
 */
public class GameStateChangeEvent extends Event<com.tendersaucer.joe.event.listeners.IGameStateChangeListener> {

    private GameState oldEvent;
    private GameState newEvent;

    public GameStateChangeEvent(GameState oldEvent, GameState newEvent) {
        this.oldEvent = oldEvent;
        this.newEvent = newEvent;
    }

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.IGameStateChangeListener listener) {
        listener.onGameStateChange(oldEvent, newEvent);
    }
}

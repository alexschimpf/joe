package com.tendersaucer.joe.event;

import com.tendersaucer.joe.Game;
import com.tendersaucer.joe.event.listeners.IGameStateChangeListener;

/**
 * Created by Alex on 7/22/2016.
 */
public class GameStateChangeEvent extends Event<IGameStateChangeListener> {

    private final Game.State oldEvent;
    private final Game.State newEvent;

    public GameStateChangeEvent(Game.State oldEvent, Game.State newEvent) {
        this.oldEvent = oldEvent;
        this.newEvent = newEvent;
    }

    @Override
    public void notify(IGameStateChangeListener listener) {
        listener.onGameStateChange(oldEvent, newEvent);
    }
}

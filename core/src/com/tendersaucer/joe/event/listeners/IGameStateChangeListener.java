package com.tendersaucer.joe.event.listeners;

import com.tendersaucer.joe.Game;

/**
 * Created by Alex on 7/22/2016.
 */
public interface IGameStateChangeListener {

    void onGameStateChange(Game.State oldEvent, Game.State newEvent);
}

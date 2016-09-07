package com.tendersaucer.joe.event.listeners;

import com.tendersaucer.joe.GameState;

/**
 * Created by Alex on 7/22/2016.
 */
public interface IGameStateChangeListener {

    void onGameStateChange(GameState oldEvent, GameState newEvent);
}

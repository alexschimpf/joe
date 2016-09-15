package com.tendersaucer.joe.event.listeners;

import com.tendersaucer.joe.level.ILevelLoadable;
import com.tendersaucer.joe.level.Level;

/**
 * Created by alexschimpf on 9/15/16.
 */
public interface INextLevelReadyListener {

    void onNextLevelReady(Level nextLevel, ILevelLoadable loadable);
}

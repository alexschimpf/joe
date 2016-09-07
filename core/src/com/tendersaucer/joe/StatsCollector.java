package com.tendersaucer.joe;

import com.badlogic.gdx.utils.TimeUtils;
import com.tendersaucer.joe.event.listeners.IGameStateChangeListener;

/**
 * Created by Alex on 7/27/2016.
 */
public final class StatsCollector implements IGameStateChangeListener {

    private static final StatsCollector instance = new StatsCollector();

    private Long runStartTime;
    private DAO dao;

    private StatsCollector() {
        dao = DAO.getInstance();
    }

    public static StatsCollector getInstance() {
        return instance;
    }

    @Override
    public void onGameStateChange(Game.State oldEvent, Game.State newEvent) {
        if (isRunBegin(newEvent)) {
            runStartTime = TimeUtils.millis();
        }
        if (isRunEnd(oldEvent, newEvent) && runStartTime != null) {
            if (!isLevelEnd(newEvent)) {
                dao.increment(DAO.RUN_ID_KEY);
            }

            long duration = TimeUtils.timeSinceMillis(runStartTime);
            dao.add(DAO.TOTAL_TIME_KEY, duration);
            runStartTime = null;
        }
        if (isLevelFirstRun(oldEvent, newEvent)) {
            dao.remove(DAO.RUN_ID_KEY);
            dao.remove(DAO.TOTAL_TIME_KEY);
        }
        if (isLevelEnd(newEvent)) {
            int levelId = (int)dao.getLong(DAO.LEVEL_ID_KEY, 0);
            if (levelId >= Globals.NUM_LEVELS - 1) {
                dao.remove(DAO.LEVEL_ID_KEY);
                dao.increment(DAO.ITERATION_ID_KEY);
            } else {
                dao.increment(DAO.LEVEL_ID_KEY);
            }
        }
    }

    private boolean isRunBegin(Game.State newEvent) {
        return newEvent == Game.State.RUNNING;
    }

    private boolean isRunEnd(Game.State oldEvent, Game.State newEvent) {
        return (oldEvent == Game.State.RUNNING && newEvent == Game.State.WAIT_FOR_INPUT) ||
                isLevelEnd(newEvent);
    }

    private boolean isLevelFirstRun(Game.State oldEvent, Game.State newEvent) {
        return oldEvent == Game.State.LEVEL_COMPLETE && newEvent == Game.State.WAIT_FOR_INPUT;
    }

    private boolean isLevelEnd(Game.State newEvent) {
        return newEvent == Game.State.LEVEL_COMPLETE;
    }
}

package com.tendersaucer.joe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import java.util.Map;

/**
 * Slow-write, fast-read DAO for StatsCollector
 * <p/>
 * Created by Alex on 7/27/2016.
 */
public final class DAO {

    public static final String IS_NEW_KEY = "is_new";
    public static final String IS_AUDIO_ENABLED = "is_audio_enabled";
    public static final String ITERATION_ID_KEY = "iteration_id";
    public static final String LEVEL_ID_KEY = "level_id";
    public static final String RUN_ID_KEY = "run_id";
    public static final String TOTAL_TIME_KEY = "total_time";
    public static final String LEVEL_ORDER_KEY = "level_order";
    private static final String PREFERENCES_NAME = "joe";
    private static final DAO INSTANCE = new DAO();

    private Map<String, ?> preferencesCache;
    private final Preferences preferences;

    private DAO() {
        preferences = Gdx.app.getPreferences(PREFERENCES_NAME);
        loadFromPreferences();

        if (Globals.CLEAR_PREFERENCES) {
            clear();
        }
    }

    public static DAO getInstance() {
        return INSTANCE;
    }

    public boolean containsKey(String key) {
        return preferencesCache.containsKey(key);
    }

    public long getLong(String key, long defaultValue) {
        if (!preferencesCache.containsKey(key)) {
            return defaultValue;
        }

        return Long.parseLong(preferencesCache.get(key).toString());
    }

    public int getInteger(String key, int defaultValue) {
        if (!preferencesCache.containsKey(key)) {
            return defaultValue;
        }

        return Integer.parseInt(preferencesCache.get(key).toString());
    }

    public String getString(String key, String defaultValue) {
        if (!preferencesCache.containsKey(key)) {
            return defaultValue;
        }

        return preferencesCache.get(key).toString();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        if (!preferencesCache.containsKey(key)) {
            return defaultValue;
        }

        return Boolean.parseBoolean(preferencesCache.get(key).toString());
    }

    public void putBoolean(String key, boolean value) {
        preferences.putBoolean(key, value);
        preferences.flush();
        loadFromPreferences();
    }

    public void putLong(String key, long value) {
        preferences.putLong(key, value);
        preferences.flush();
        loadFromPreferences();
    }

    public void putString(String key, String value) {
        preferences.putString(key, value);
        preferences.flush();
        loadFromPreferences();
    }

    public void putInteger(String key, int value) {
        preferences.putInteger(key, value);
        preferences.flush();
        loadFromPreferences();
    }

    public void increment(String key) {
        add(key, 1);
    }

    public void add(String key, long amount) {
        if (containsKey(key)) {
            long curr = getLong(key, 0);
            preferences.putLong(key, curr + amount);
        } else {
            preferences.putLong(key, amount);
        }

        preferences.flush();
        loadFromPreferences();
    }

    public void remove(String key) {
        if (containsKey(key)) {
            preferences.remove(key);
        }

        preferences.flush();
        loadFromPreferences();
    }

    public void clear() {
        preferences.clear();
        preferences.flush();
        preferencesCache.clear();
    }

    private void loadFromPreferences() {
        preferencesCache = preferences.get();
    }
}

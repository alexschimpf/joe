package com.tendersaucer.joe;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.tendersaucer.joe.event.EventManager;
import com.tendersaucer.joe.event.GameStateChangeEvent;

/**
 * Game global variables
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class Globals {

    public static final int NUM_LEVELS = 2;
    public static final int LOG_LEVEL = Application.LOG_DEBUG;
    public static boolean FULLSCREEN_MODE = true;
    public static boolean DEBUG_PHYSICS = false;
    public static boolean PRINT_DEBUG_INFO = false;
    public static boolean CUSTOM_CAMERA_MODE = false;
    public static boolean CLEAR_PREFERENCES = true;
    public static final Color ON_COLOR = new Color(0, 0.7f, 0, 1);
    public static final Color OFF_COLOR = new Color(0.7f, 0, 0, 1);
    private static GameState gameState;

    private Globals() {
        gameState = GameState.RUNNING;
    }

    public static boolean isMobile() {
        return isAndroid() || isIOS();
    }

    public static boolean isAndroid() {
        return Gdx.app.getType().equals(ApplicationType.Android);
    }

    public static boolean isDesktop() {
        return Gdx.app.getType().equals(ApplicationType.Desktop);
    }

    public static boolean isIOS() {
        return Gdx.app.getType().equals(ApplicationType.iOS);
    }

    public static void  setGameState(GameState gameState) {
        GameState oldGameState = Globals.gameState;
        Gdx.app.debug("Globals", "Game state changed from '" + oldGameState + "' to '" + gameState + "'");

        Globals.gameState = gameState;
        EventManager.getInstance().notify(new GameStateChangeEvent(oldGameState, gameState));
    }

    public static GameState getGameState() {
        return gameState;
    }
}

package com.tendersaucer.joe;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.tendersaucer.joe.event.EventManager;
import com.tendersaucer.joe.event.GameStateChangeEvent;
import com.tendersaucer.joe.util.RandomUtils;

/**
 * Game global variables
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class Globals {

    private static Game.State gameState;
    public static final int NUM_LEVELS = 6;
    public static final Integer START_LEVEL = 4; // null = disabled
    public static boolean REPEAT_START_LEVEL = true;
    public static boolean FULLSCREEN_MODE = true;
    public static boolean DEBUG_PHYSICS = false;
    public static boolean PRINT_DEBUG_INFO = false;
    public static boolean CUSTOM_CAMERA_MODE = false;
    public static boolean CLEAR_PREFERENCES = true;
    public static boolean PACK_TEXTURES = true;
    public static final int LOG_LEVEL = Application.LOG_DEBUG;

    private Globals() {
    }

    public static boolean isDesktop() {
        return Gdx.app.getType() == ApplicationType.Desktop;
    }

    public static boolean isAndroid() {
        return Gdx.app.getType() == ApplicationType.Android;
    }

    public static boolean isIOS() {
        return Gdx.app.getType() == ApplicationType.iOS;
    }

    public static boolean isMobile() {
        return isAndroid() || isIOS();
    }

    public static void  setGameState(Game.State gameState) {
        Game.State oldGameState = Globals.gameState;
        Gdx.app.debug("Globals", "Game state changed from '" + oldGameState + "' to '" + gameState + "'");

        Globals.gameState = gameState;
        EventManager.getInstance().notify(new GameStateChangeEvent(oldGameState, gameState));
    }

    public static Game.State getGameState() {
        return gameState;
    }

    public static String getLevelTileMapName(int levelId) {
        String[] levelOrder = DAO.getInstance().getString(DAO.LEVEL_ORDER_KEY, "").split(",");
        return levelOrder[levelId];
    }

    public static void setRandomLevelOrder() {
        String[] temp = new String[Globals.NUM_LEVELS - 2];
        for (int i = 2; i < Globals.NUM_LEVELS; i++){
            temp[i - 2] = String.valueOf(i);
        }

        if (START_LEVEL == null) {
            RandomUtils.shuffle(temp);
        }

        String levelOrderCSV = "0,1";
        for (int i = 0; i < temp.length; i++) {
            levelOrderCSV += "," + temp[i];
        }
        DAO.getInstance().putString(DAO.LEVEL_ORDER_KEY, levelOrderCSV);
    }
}

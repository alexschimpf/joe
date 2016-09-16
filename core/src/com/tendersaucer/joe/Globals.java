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

    public static final int NUM_LEVELS = 6;
    public static final Integer START_LEVEL = null; // null = disabled
    public static final boolean REPEAT_START_LEVEL = false;
    public static final boolean CLEAR_PREFERENCES = true;
    public static final boolean PACK_TEXTURES = true;
    public static final int LOG_LEVEL = Application.LOG_DEBUG;

    private static Game.State gameState;
    public static boolean fullScreenMode = false;
    public static boolean debugPhysics = false;
    public static boolean printDebugInfo = false;
    public static boolean customCameraInfo = false;

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

    public static void setGameState(Game.State gameState) {
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
        for (int i = 2; i < Globals.NUM_LEVELS; i++) {
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

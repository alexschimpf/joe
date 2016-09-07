package com.tendersaucer.joe.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.tendersaucer.joe.DAO;
import com.tendersaucer.joe.Game;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.IUpdate;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.level.entity.Player;
import com.tendersaucer.joe.level.Level;

/**
 * Game input listener
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class InputListener extends com.badlogic.gdx.scenes.scene2d.InputListener implements IUpdate {

    private boolean mainMenuOnBack;

    public InputListener(boolean mainMenuOnBack) {
        this.mainMenuOnBack = mainMenuOnBack;
    }

    @Override
    public boolean update() {
        if (Globals.getGameState() != Game.State.RUNNING) {
            return false;
        }

        MainCamera camera = MainCamera.getInstance();
        Player player = Level.getInstance().getPlayer();
        if (player != null) {
            boolean isCameraFlipped = camera.isFlipped();
            float multiplier = isCameraFlipped ? -1 : 1;
            if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
                if (Globals.CUSTOM_CAMERA_MODE && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
                    camera.move(camera.getTileSize() / 2 * multiplier, 0);
                    camera.setPlayerFocus(false);
                } else if (isCameraFlipped){
                    player.moveLeft();
                } else {
                    player.moveRight();
                }
            } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
                if (Globals.CUSTOM_CAMERA_MODE && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
                    camera.move(-camera.getTileSize() / 2 * multiplier, 0);
                    camera.setPlayerFocus(false);
                } else if (isCameraFlipped){
                    player.moveRight();
                } else {
                    player.moveLeft();
                }
            } else if (Globals.CUSTOM_CAMERA_MODE && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) {
                if (Gdx.input.isKeyPressed(Keys.UP)) {
                    camera.move(0, -camera.getTileSize() / 2 * multiplier);
                    camera.setPlayerFocus(false);
                } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
                    camera.move(0, camera.getTileSize() / 2  * multiplier);
                }
            } else if (!Globals.isAndroid()) {
                player.stopHorizontalMove();
            }

            if (Gdx.input.isKeyPressed(Keys.Z)) {
                MainCamera.getInstance().getRawCamera().zoom += 0.1f;
            }
            if (Gdx.input.isKeyPressed(Keys.X)) {
                MainCamera.getInstance().getRawCamera().zoom -= 0.1f;
            }
        }

        return false;
    }

    @Override
    public boolean keyDown(InputEvent event, int keyCode) {
        Game.State gameState = Globals.getGameState();
        if (gameState == Game.State.RUNNING || gameState == Game.State.WAIT_FOR_INPUT) {
            if (keyCode == Keys.A) {
                Player player = Level.getInstance().getPlayer();
                if (player != null) {
                    player.jump();
                }
            }
        }

        switch (keyCode) {
            case Keys.BACK:
                if (mainMenuOnBack) {
                    // TODO: Game.instance.setScreen(new MainMenu(Game.instance));
                }
                break;
            case Keys.ESCAPE:
                Gdx.app.exit();
                break;
            case Keys.D:
                Globals.DEBUG_PHYSICS = !Globals.DEBUG_PHYSICS;
                break;
            case Keys.C:
                DAO.getInstance().clear();
                break;
            case Keys.P:
                Globals.PRINT_DEBUG_INFO = !Globals.PRINT_DEBUG_INFO;
                break;
            case Keys.E:
                Globals.CUSTOM_CAMERA_MODE = !Globals.CUSTOM_CAMERA_MODE;
                MainCamera.getInstance().setPlayerFocus(true);
                break;
            case Keys.Q:
                MainCamera.getInstance().takeScreenshot();
                break;
            case Keys.ENTER:
                Level.getInstance().loadNext();
                break;
            default:
                if (Level.getInstance().getPlayer() != null &&
                        Globals.getGameState() == Game.State.WAIT_FOR_INPUT) {
                    Globals.setGameState(Game.State.RUNNING);
                }

                return false;
        }

        return true;
    }

    @Override
    public boolean keyUp(InputEvent event, int keyCode) {
        if (Globals.getGameState() != Game.State.RUNNING ) {
            return false;
        }

        if (keyCode == Keys.A) {
            Player player = Level.getInstance().getPlayer();
            if (player != null) {
                player.stopJump();
            }
        }

        return true;
    }
}

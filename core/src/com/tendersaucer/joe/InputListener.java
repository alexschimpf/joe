package com.tendersaucer.joe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.tendersaucer.joe.entity.Player;
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
        if (Globals.getGameState() != GameState.RUNNING) {
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
        }

        return false;
    }

    @Override
    public boolean keyDown(InputEvent event, int keyCode) {
        if (Globals.getGameState() == GameState.RUNNING) {
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
            case Keys.Z:
                MainCamera.getInstance().getRawCamera().zoom += 0.1f;
                break;
            case Keys.X:
                MainCamera.getInstance().getRawCamera().zoom -= 0.1f;
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
            default:
                if (Globals.getGameState() != GameState.RUNNING) {
                    Globals.setGameState(GameState.RUNNING);
                }

                return false;
        }

        return true;
    }

    @Override
    public boolean keyUp(InputEvent event, int keyCode) {
        if (Globals.getGameState() != GameState.RUNNING ) {
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

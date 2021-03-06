package com.tendersaucer.joe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.tendersaucer.joe.event.listeners.IGameStateChangeListener;
import com.tendersaucer.joe.level.Level;
import com.tendersaucer.joe.level.entity.Player;
import com.tendersaucer.joe.util.path.LinearPathHelper;

import java.util.UUID;

/**
 * Main game camera
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class MainCamera implements IUpdate, IGameStateChangeListener {

    private static final int TILES_PER_SCREEN_WIDTH = 18;
    private static final MainCamera INSTANCE = new MainCamera();
    private static final int TILE_SIZE_PIXELS = 64;
    private static final int BASE_VIEWPORT_WIDTH = 50; // 50m is small enough for Box2D to handle
    private static final int BASE_VIEWPORT_HEIGHT = 50;
    private static final float PATH_DURATION = 20000;

    private boolean playerFocus;
    private Float preLoopElapsed;
    private Float pathElapsed;
    private float pathWidth;
    private final Array<Vector2> path;
    private final LinearPathHelper pathHelper;
    private final OrthographicCamera rawCamera;

    private MainCamera() {
        rawCamera = new OrthographicCamera();
        rawCamera.setToOrtho(true, BASE_VIEWPORT_WIDTH, BASE_VIEWPORT_HEIGHT);
        rawCamera.zoom += 0.3f; // TODO: Hack

        playerFocus = true;
        path = new Array<Vector2>();
        pathHelper = new LinearPathHelper(true);
    }

    public static MainCamera getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean update() {
        float deltaMs = Gdx.graphics.getDeltaTime() * 1000;
        if (preLoopElapsed != null) {
            preLoopElapsed += deltaMs;
            float preLoopDuration = PATH_DURATION / 8;
            if (preLoopElapsed < preLoopDuration) {
                float speed = (pathWidth / 2) / preLoopDuration;
                rawCamera.translate(0, -speed * deltaMs);
            } else {
                preLoopElapsed = 0f;
                pathElapsed = 0f;
            }
        } else if (pathElapsed != null) {
            pathElapsed += deltaMs;
            Vector2 velocity = pathHelper.getVelocity(PATH_DURATION, pathElapsed);
            rawCamera.translate(velocity.x * deltaMs, velocity.y * deltaMs);
        } else if (playerFocus) {
            Player player = Level.getInstance().getPlayer();
            if (player != null) {
                rawCamera.position.set(player.getCenterX(), player.getCenterY(), 0);
            }
        }

        rawCamera.update();

        return false;
    }

    @Override
    public void onGameStateChange(Game.State oldEvent, Game.State newEvent) {
        preLoopElapsed = newEvent == Game.State.LEVEL_COMPLETE ? 0f : null;
        if (oldEvent != newEvent && newEvent == Game.State.WAIT_FOR_INPUT) {
            pathElapsed = null;
            setPath();
        }
    }

    public void takeScreenshot() {
        byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight(), true);
        Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(),
                Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
        BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
        PixmapIO.writePNG(Gdx.files.external(UUID.randomUUID().toString() + ".png"), pixmap);
        pixmap.dispose();
    }

    public OrthographicCamera getRawCamera() {
        return rawCamera;
    }

    public void setPosition(float centerX, float centerY) {
        rawCamera.position.x = centerX;
        rawCamera.position.y = centerY;
    }

    public void move(float dx, float dy) {
        rawCamera.position.x += dx;
        rawCamera.position.y += dy;
    }

    public void resizeViewport(float width, float height) {
        rawCamera.viewportHeight = (rawCamera.viewportWidth / width) * height;
        rawCamera.position.y = rawCamera.viewportHeight / 2;
        rawCamera.update();
    }

    public float getViewportWidth() {
        return rawCamera.viewportWidth;
    }

    public float getViewportHeight() {
        return rawCamera.viewportHeight;
    }

    public void flipHorizontally() {
        rawCamera.position.z *= -1;
        rawCamera.direction.z *= -1;
    }

    public float getTileMapScale() {
        float viewportTileSize = getViewportWidth() / TILES_PER_SCREEN_WIDTH;
        return viewportTileSize / TILE_SIZE_PIXELS;
    }

    public boolean isFlipped() {
        return rawCamera.direction.z < 0;
    }

    public float getCenterX() {
        return rawCamera.position.x;
    }

    public float getCenterY() {
        return rawCamera.position.y;
    }

    public float getLeft() {
        return isFlipped() ? getCenterX() + (getViewportWidth() / 2) : getCenterX() - (getViewportWidth() / 2);
    }

    public float getTop() {
        return getCenterY() - (getViewportHeight() / 2);
    }

    public float getRight() {
        return isFlipped() ? getCenterX() - (getViewportWidth() / 2) : getCenterX() + (getViewportWidth() / 2);
    }

    public float getBottom() {
        return getCenterY() + (getViewportHeight() / 2);
    }

    public float getTileSize() {
        return getViewportWidth() / TILES_PER_SCREEN_WIDTH;
    }

    public void setPlayerFocus(boolean playerFocus) {
        this.playerFocus = playerFocus;
    }

    private void setPath() {
        path.clear();

        float w = pathWidth = getViewportWidth() * 0.2f;
        path.add(new Vector2(-w / 2, 0));
        path.add(new Vector2(0, w));
        path.add(new Vector2(w, 0));
        path.add(new Vector2(0, -w));
        path.add(new Vector2(-w / 2, 0));
        pathHelper.setPath(path);
    }
}


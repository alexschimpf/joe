package com.tendersaucer.joe.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.tendersaucer.joe.AssetManager;
import com.tendersaucer.joe.DAO;
import com.tendersaucer.joe.GameState;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.StatisticsListener;
import com.tendersaucer.joe.event.EventManager;
import com.tendersaucer.joe.event.GameStateChangeEvent;
import com.tendersaucer.joe.event.LevelLoadBeginEvent;
import com.tendersaucer.joe.event.NewUserEvent;
import com.tendersaucer.joe.level.Level;
import com.tendersaucer.joe.particle.ParticleEffectManager;
import com.tendersaucer.joe.util.Vector2Pool;

/**
 * Main update and render logic
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class Driver implements Screen {

    public static SpriteBatch spriteBatch;

    private final Matrix4 debugMatrix;
    private final Box2DDebugRenderer debugRenderer;
    private final Game game;

    public Driver(Game game) {
        this.game = game;
        debugMatrix = new Matrix4();
        debugRenderer = new Box2DDebugRenderer();

        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {
        AssetManager.getInstance().load();
        ParticleEffectManager.getInstance().loadDefinitions();

        EventManager eventManager = EventManager.getInstance();
        eventManager.listen(LevelLoadBeginEvent.class, Canvas.getInstance());
        eventManager.listen(LevelLoadBeginEvent.class, ParticleEffectManager.getInstance());
        eventManager.listen(GameStateChangeEvent.class, HUD.getInstance());
        eventManager.listen(GameStateChangeEvent.class, StatisticsListener.getInstance());
        eventManager.listen(GameStateChangeEvent.class, MainCamera.getInstance());

        DAO dao = DAO.getInstance();
        long iterationId = dao.getIterationId();
        int levelId = (int)dao.getLevelId();
        Level.getInstance().load(iterationId, levelId);

        if (dao.isNew()) {
            eventManager.notify(new NewUserEvent());
            dao.putBoolean(DAO.IS_NEW_KEY, false);
        }
    }

    @Override
    public void render(float delta) {
        if (Globals.PRINT_DEBUG_INFO) {
            Gdx.app.debug("debug", "FPS: " + Gdx.graphics.getFramesPerSecond());
            Gdx.app.debug("debug", "V2Pool #Free: " + Vector2Pool.getInstance().getFree());
            Gdx.app.debug("debug", "V3Pool #Free: " + Vector2Pool.getInstance().getFree());
            Gdx.app.debug("debug", "Heap size (MB): " + Gdx.app.getJavaHeap() / 1000000.0f);
        }

        update();
        render();
    }

    @Override
    public void resize(int width, int height) {
        MainCamera.getInstance().resizeViewport(width, height);
        HUD.getInstance().resize(width, height);
    }

    @Override
    public void pause() {
        Globals.setGameState(GameState.PAUSED);
    }

    @Override
    public void resume() {
        Level.getInstance().replay(); // then state becomes WAIT_FOR_INPUT
    }

    @Override
    public void hide() {
        Globals.setGameState(GameState.PAUSED);
    }

    @Override
    public void dispose() {
        Globals.setGameState(GameState.SHUTDOWN);
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    private void update() {
        MainCamera.getInstance().update();
        HUD.getInstance().update();
        Level.getInstance().update();
        ParticleEffectManager.getInstance().update();
    }

    private void render() {
        if (Globals.PRINT_DEBUG_INFO) {
            Gdx.app.debug("debug", "FPS: " + Gdx.graphics.getFramesPerSecond());
            Gdx.app.debug("debug", "V2Pool #Free: " + Vector2Pool.getInstance().getFree());
            Gdx.app.debug("debug", "V3Pool #Free: " + Vector2Pool.getInstance().getFree());
            Gdx.app.debug("debug", "Heap size (MB): " + Gdx.app.getJavaHeap() / 1000000.0f);
        }

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        OrthographicCamera camera = MainCamera.getInstance().getRawCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin(); {
            Canvas.getInstance().render(spriteBatch);
        }
        spriteBatch.end();

        if (Globals.DEBUG_PHYSICS) {
            debugMatrix.set(camera.combined);
            debugRenderer.render(Level.getInstance().getPhysicsWorld(), debugMatrix);
        }

        HUD.getInstance().render(spriteBatch);
    }
}

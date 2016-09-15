package com.tendersaucer.joe.level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.tendersaucer.joe.ColorScheme;
import com.tendersaucer.joe.DAO;
import com.tendersaucer.joe.Game;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.IDisposable;
import com.tendersaucer.joe.IUpdate;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.parallax.ParallaxBackground;
import com.tendersaucer.joe.level.entity.Entity;
import com.tendersaucer.joe.level.entity.EntityDefinition;
import com.tendersaucer.joe.level.entity.Player;
import com.tendersaucer.joe.level.entity.RenderedEntity;
import com.tendersaucer.joe.event.EventManager;
import com.tendersaucer.joe.event.LevelLoadBeginEvent;
import com.tendersaucer.joe.event.LevelLoadEndEvent;
import com.tendersaucer.joe.Canvas;
import com.tendersaucer.joe.IRender;
import com.tendersaucer.joe.level.script.Script;
import com.tendersaucer.joe.level.script.ScriptDefinition;
import com.tendersaucer.joe.util.box2d.FixtureBodyDefinition;
import com.tendersaucer.joe.util.exception.InvalidConfigException;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * A single level
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class Level implements IUpdate, IDisposable {

    public static final float DEFAULT_GRAVITY = 50;
    private static final Level INSTANCE = new Level();

    private int id;
    private long iterationId;
    private Player player;
    private World physicsWorld;
    private Vector2 respawnPosition;
    private ParallaxBackground background;
    private final Map<String, Entity> entityMap;
    private final Map<String, Script> scriptMap;

    private Level() {
        entityMap = new ConcurrentHashMap<String, Entity>();
        scriptMap = new ConcurrentHashMap<String, Script>();

        respawnPosition = new Vector2();

        World.setVelocityThreshold(0.5f);
        physicsWorld = new World(new Vector2(0, DEFAULT_GRAVITY), true);
        physicsWorld.setContactListener(CollisionListener.getInstance());
    }

    public static Level getInstance() {
        return INSTANCE;
    }

    @Override
    public void dispose() {
        for (Entity entity : entityMap.values()) {
            entity.dispose();
        }
        for (Script script : scriptMap.values()) {
            script.dispose();
        }
    }

    @Override
    public boolean update() {
        physicsWorld.step(1 / 45.0f, 5, 5);

        Iterator<String> entityIdIter = entityMap.keySet().iterator();
        while (entityIdIter.hasNext()) {
            String id = entityIdIter.next();
            Entity entity = entityMap.get(id);
            if (entity != null && entity.update()) {
                if (Entity.isPlayer(entity)) {
                    player = null;
                }

                entityIdIter.remove();
                entity.dispose();
            }
        }

        Iterator<String> scriptIdIter = scriptMap.keySet().iterator();
        while (scriptIdIter.hasNext()) {
            String id = scriptIdIter.next();
            Script script = scriptMap.get(id);
            if (script != null && script.update()) {
                scriptIdIter.remove();
                script.dispose();
            }
        }

        return false;
    }

    // TODO: More color after each iteration
    public void load(long iterationId, int levelId) {
        try {
            // Seems to be preventing concurrency issue.
            TimeUnit.MILLISECONDS.sleep(5);
        } catch (InterruptedException e) {
            // TODO:
        }

        id = levelId;
        this.iterationId = iterationId;

        ColorScheme.getInstance().reset();
        TiledMapLevelLoadable loadable = new TiledMapLevelLoadable(levelId);
        EventManager.getInstance().notify(new LevelLoadBeginEvent(loadable));

        id = loadable.getId();
        respawnPosition.set(loadable.getRespawnPosition());

        background = loadable.getBackground();
        Canvas.getInstance().addToLayer(0, background);

        // Add non-entity/background canvas objects.
        Map<IRender, Integer> canvasMap = loadable.getCanvasMap();
        for (IRender object : canvasMap.keySet()) {
            int layer = canvasMap.get(object);
            Canvas.getInstance().addToLayer(layer, object);
        }

        dispose();
        clearPhysicsWorld();
        entityMap.clear();
        scriptMap.clear();

        loadEntities(loadable);
        loadFreeBodies(loadable);
        loadScripts(loadable);

        boolean isCameraFlipped = MainCamera.getInstance().isFlipped();
        if ((iterationId % 2 == 0 && isCameraFlipped) ||
                (iterationId % 2 == 1 && !isCameraFlipped)) {
            MainCamera.getInstance().flipHorizontally();
        }

        EventManager.getInstance().notify(new LevelLoadEndEvent());
        Globals.setGameState(Game.State.WAIT_FOR_INPUT);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                getPlayer().setActive(true);
                getPlayer().setVisible(true);
            }
        }, DAO.getInstance().getBoolean(DAO.IS_NEW_KEY, true) ? 0 : 0.2f);
    }

    public void loadNext() {
        if (Globals.REPEAT_START_LEVEL) {
            replay();
        }

        if (id >= Globals.NUM_LEVELS - 1) {
            id = 0;
            iterationId++;
        } else {
            id++;
        }

        load(iterationId, id);
    }

    public void replay() {
        load(iterationId, id);
    }

    public World getPhysicsWorld() {
        return physicsWorld;
    }

    public Array<Body> getBodies() {
        Array<Body> bodies = new Array<Body>();
        physicsWorld.getBodies(bodies);

        return bodies;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean hasPlayer() {
        return player != null;
    }

    public ParallaxBackground getBackground() {
        return background;
    }

    public void addEntity(Entity entity) {
        String id = entity.getId();
        entityMap.put(id, entity);
    }

    public void addScript(Script script) {
        scriptMap.put(script.getId(), script);
    }

    public Entity getEntity(String id) {
        return entityMap.get(id);
    }

    public Script getScript(String id) {
        return scriptMap.get(id);
    }

    public boolean hasEntity(String id) {
        return entityMap.containsKey(id);
    }

    public boolean hasScript(String id) {
        return scriptMap.containsKey(id);
    }

    public String getAvailableEntityId() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (hasEntity(id));

        return id;
    }

    public String getAvailableScriptId() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (hasScript(id));

        return id;
    }

    public int getId() {
        return id;
    }

    public long getIterationId() {
        return iterationId;
    }

    public Vector2 getRespawnPosition() {
        return respawnPosition;
    }

    private void loadEntities(ILevelLoadable loadable) {
        for (EntityDefinition entityDefinition : loadable.getEntityDefinitions()) {
            String id = entityDefinition.getId();
            if (id != null && entityMap.containsKey(id)) {
                throw new InvalidConfigException("Duplicate entity id: " + id);
            }

            Entity entity = Entity.build(entityDefinition);
            if (Entity.isPlayer(entity)) {
                player = (Player)entity;
                player.setActive(false);
                player.setVisible(false);
            }
            if (entity instanceof RenderedEntity) {
                ((RenderedEntity)entity).addToCanvas();
            }

            addEntity(entity);
        }
    }

    private void loadFreeBodies(ILevelLoadable loadable) {
        for (FixtureBodyDefinition fixtureBodyDef : loadable.getFreeBodyDefinitions()) {
            Body body = physicsWorld.createBody(fixtureBodyDef.bodyDef);
            body.createFixture(fixtureBodyDef.fixtureDef);

            fixtureBodyDef.fixtureDef.shape.dispose();
        }
    }

    private void loadScripts(ILevelLoadable loadable) {
        for (ScriptDefinition definition : loadable.getScriptDefinitions()) {
            String id = definition.getId();

            if (id != null && scriptMap.containsKey(id)) {
                throw new InvalidConfigException("Duplicate script id: " + id);
            }

            addScript(Script.build(definition));
        }
    }

    private void clearPhysicsWorld() {
        Iterator<Body> bodiesIter = getBodies().iterator();
        while (bodiesIter.hasNext()) {
            Body body = bodiesIter.next();
            physicsWorld.destroyBody(body);

            bodiesIter.remove();
        }
    }
}

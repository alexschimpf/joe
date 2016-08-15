package com.tendersaucer.joe.level;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.GameState;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.IUpdate;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.entity.Entity;
import com.tendersaucer.joe.entity.EntityDefinition;
import com.tendersaucer.joe.entity.Player;
import com.tendersaucer.joe.entity.RenderedEntity;
import com.tendersaucer.joe.event.EventManager;
import com.tendersaucer.joe.event.LevelLoadBeginEvent;
import com.tendersaucer.joe.event.LevelLoadEndEvent;
import com.tendersaucer.joe.gen.EntityConstants;
import com.tendersaucer.joe.screen.Canvas;
import com.tendersaucer.joe.screen.IRender;
import com.tendersaucer.joe.script.Script;
import com.tendersaucer.joe.util.FixtureBodyDefinition;
import com.tendersaucer.joe.util.InvalidConfigException;

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
public final class Level implements IUpdate {

    public static final float DEFAULT_GRAVITY = 50;
    private static final Level instance = new Level();

    private int id;
    private long iterationId;
    private Player player;
    private World physicsWorld;
    private Vector2 respawnPosition;
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
        return instance;
    }

    @Override
    public boolean update() {
        physicsWorld.step(1 / 45.0f, 5, 5);

        Iterator<String> entityIdIter = entityMap.keySet().iterator();
        while (entityIdIter.hasNext()) {
            String id = entityIdIter.next();
            Entity entity = entityMap.get(id);
            if (entity != null && entity.update()) {
                if (id.equals(EntityConstants.PLAYER)) {
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
                entityIdIter.remove();
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

        TiledMapLevelLoadable loadable = new TiledMapLevelLoadable(levelId);
        EventManager.getInstance().notify(new LevelLoadBeginEvent(loadable));

        id = loadable.getId();
        respawnPosition.set(loadable.getRespawnPosition());

        clearPhysicsWorld();
        Canvas.getInstance().addToLayer(0, loadable.getBackground());

        // Add non-entity/background canvas objects.
        Map<IRender, Integer> canvasMap = loadable.getCanvasMap();
        for (IRender object : canvasMap.keySet()) {
            int layer = canvasMap.get(object);
            Canvas.getInstance().addToLayer(layer, object);
        }

        entityMap.clear();
        loadEntities(loadable);
        loadFreeBodies(loadable);

        EventManager.getInstance().notify(new LevelLoadEndEvent());
        Globals.setGameState(GameState.WAIT_FOR_INPUT);

        boolean isCameraFlipped = MainCamera.getInstance().isFlipped();
        if ((iterationId % 2 == 0 && isCameraFlipped) ||
                (iterationId % 2 == 1 && !isCameraFlipped)) {
            MainCamera.getInstance().flipHorizontally();
        }
    }

    public void loadNext() {
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

    public void addEntity(Entity entity) {
        String id = entity.getId();
        entityMap.put(id, entity);

        if (id.equals(EntityConstants.PLAYER)) {
            player = (Player)entity;
        }
    }

    public Entity getEntity(String id) {
        return entityMap.get(id);
    }

    public boolean hasEntity(String id) {
        return entityMap.containsKey(id);
    }

    public String getAvailableId() {
        String id;
        do {
            id = UUID.randomUUID().toString();
        } while (hasEntity(id));

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

    public String getAvailableEntityId() {
        String id;
        do {
            id = String.valueOf(MathUtils.random(0, Integer.MAX_VALUE));
        } while (entityMap.containsKey(id));

        return id;
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

    private void clearPhysicsWorld() {
        Iterator<Body> bodiesIter = getBodies().iterator();
        while (bodiesIter.hasNext()) {
            Body body = bodiesIter.next();
            physicsWorld.destroyBody(body);

            bodiesIter.remove();
        }
    }
}

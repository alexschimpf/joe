package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.anim.AnimatedSprite;
import com.tendersaucer.joe.anim.AnimatedSpriteSystem;
import com.tendersaucer.joe.level.Level;

/**
 * User-controlled player
 * Created by Alex on 4/8/2016.
 */
public final class Player extends RenderedEntity {

    public enum Direction {
        LEFT, RIGHT
    }

    public enum InteractionType {
        SIMPLE
    }

    private static final String JUMP_ANIMATION_ID = "jump";
    private static final String MOVE_ANIMATION_ID = "move";
    public static final float MOVE_SPEED = 1800;
    public static final float JUMP_IMPULSE = -160;
    public static final short COLLISION_MASK = 0x0002;
    private static final float JUMP_ANIMATION_DURATION = 400;
    private static final float MOVE_ANIMATION_DURATION = 200;
    private static final float MAX_FALL_TILES = 15.5f; // won't have to worry about rounding

    private int numFootContacts;
    private Direction direction;
    private float jumpStartY;

    private Player(EntityDefinition def) {
        super(def);

        numFootContacts = 0;
        direction = Direction.RIGHT;
    }

    @Override
    public void init() {
        super.init();

        // To ensure foot contact, thus allowing jumping
        body.applyForceToCenter(0, 0.0001f, true);
    }

    @Override
    protected void tick() {
        super.tick();

        if (numFootContacts > 0) {
            jumpStartY = getCenterY();
        }

        float maxFallHeight = MAX_FALL_TILES * MainCamera.getInstance().getTileSize();
        if (getCenterY() - jumpStartY > maxFallHeight) {
            respawn();
        }

        ((AnimatedSpriteSystem)sprite).update();
    }

    @Override
    protected Sprite createSprite(EntityDefinition definition) {
        AnimatedSpriteSystem animationSystem = new AnimatedSpriteSystem("player_default");
        animationSystem.setSize(getWidth(), getHeight());
        animationSystem.add(JUMP_ANIMATION_ID, new AnimatedSprite("player_jump", JUMP_ANIMATION_DURATION));
        animationSystem.add(MOVE_ANIMATION_ID, new AnimatedSprite("player_move", MOVE_ANIMATION_DURATION));

        return animationSystem;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        sprite.setFlip(isFacingLeft(), true);
        super.render(spriteBatch);
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        handleFootContact(contact, true);

        if (entity instanceof GroovySpinningThingy) {
            setDone();
        }
    }

    @Override
    public void onEndContact(Contact contact, Entity entity) {
        handleFootContact(contact, false);
    }

    @Override
    protected Body createBody(EntityDefinition definition) {
        BodyDef bodyDef = definition.getBodyDef();
        bodyDef.position.set(definition.getCenter());
        Body body = Level.getInstance().getPhysicsWorld().createBody(bodyDef);

        FixtureDef fixtureDef = createFixtureDef();
        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();

        body.setBullet(true);
        attachFootSensor(body, definition.getSize().x);

        Filter filter = new Filter();
        filter.categoryBits = 0x0001;
        filter.maskBits = ~Player.COLLISION_MASK;
        body.getFixtureList().get(0).setFilterData(filter);

        return body;
    }

    public void jump() {
        if (!isJumping() && isActive() && isVisible()) {
            //AssetManager.getInstance().getSound("jump").play();
            body.applyLinearImpulse(0, JUMP_IMPULSE, getCenterX(),
                    getCenterY(), true);

            AnimatedSpriteSystem animationSystem = (AnimatedSpriteSystem)sprite;
            if (!animationSystem.isCurrent(JUMP_ANIMATION_ID)) {
                animationSystem.switchTo(JUMP_ANIMATION_ID, AnimatedSprite.State.PLAYING, true);
            }

            jumpStartY = getCenterY();
        }
    }

    public void land() {
    }

    public void stopJump() {
        if (getLinearVelocity().y < 0) {
            setLinearVelocity(getLinearVelocity().x, 0.02f);
        }

        AnimatedSpriteSystem animationSystem = (AnimatedSpriteSystem)sprite;
        if (animationSystem.isCurrent(JUMP_ANIMATION_ID)) {
            animationSystem.switchToDefault();
        }
    }

    public void moveLeft() {
        move(Direction.LEFT);
    }

    public void moveRight() {
        move(Direction.RIGHT);
    }

    private void move(Direction direction) {
        if (!isActive() || !isVisible()) {
            return;
        }

        setDirection(direction);

        float vx = Gdx.graphics.getDeltaTime() * Player.MOVE_SPEED * (isFacingLeft() ? -1 : 1);
        setLinearVelocity(vx, getLinearVelocity().y);

        AnimatedSpriteSystem animationSystem = (AnimatedSpriteSystem)sprite;
        if (!animationSystem.isCurrent(MOVE_ANIMATION_ID) && !isJumping()) {
            animationSystem.switchTo(MOVE_ANIMATION_ID, AnimatedSprite.State.PLAYING);
        } else if (numFootContacts == 0 && animationSystem.isCurrent(MOVE_ANIMATION_ID)) {
            // If moving straight off a surface, without jumping.
            animationSystem.switchToDefault();
        }
    }

    public void stopHorizontalMove() {
        setLinearVelocity(0, getLinearVelocity().y);

        AnimatedSpriteSystem animationSystem = (AnimatedSpriteSystem)sprite;
        if (animationSystem.isCurrent(MOVE_ANIMATION_ID)) {
            animationSystem.switchToDefault();
        }
    }

    public boolean isFacingLeft() {
        return direction == Direction.LEFT;
    }

    public boolean isFacingRight() {
        return direction == Direction.RIGHT;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    private void respawn() {
        Level.getInstance().replay();
    }

    private void attachFootSensor(Body body, float width) {
        PolygonShape shape = new PolygonShape();
        Vector2 localBottom = body.getLocalPoint(new Vector2(getCenterX(), getBottom()));
        shape.setAsBox(width * 0.47f, 0.12f, localBottom, 0);
        Fixture fixture = body.createFixture(shape, 0);
        fixture.setSensor(true);

        shape.dispose();
    }

    private void handleFootContact(Contact contact, boolean onBeginContact) {
        Fixture footSensor = getFootSensor();
        if (contact.getFixtureA() == footSensor || contact.getFixtureB() == footSensor) {
            Fixture otherFixture =
                    (footSensor == contact.getFixtureA()) ? contact.getFixtureB() : contact.getFixtureA();
            if (!otherFixture.isSensor()) {
                if (onBeginContact) {
                    if (numFootContacts == 0) {
                        land();
                    }

                    numFootContacts++;
                } else {
                    numFootContacts--;
                }

                numFootContacts = Math.max(0, numFootContacts);
            }
        }
    }

    private boolean isJumping() {
        // May have not left the ground yet but is still in the process of jumping
        return numFootContacts == 0 || ((AnimatedSpriteSystem)sprite).isPlaying(JUMP_ANIMATION_ID);
    }

    private Fixture getFootSensor() {
        return body.getFixtureList().get(1);
    }

    private FixtureDef createFixtureDef() {
        PolygonShape shape = new PolygonShape();
//        shape.set(new Vector2[]{
//                new Vector2(1.3f, -1.29f),
//                new Vector2(1f, -1.3f),
//                new Vector2(-1f, -1.3f),
//                new Vector2(-1.3f, -1.29f),
//                new Vector2(-1.3f, 1.29f),
//                new Vector2(-1f, 1.3f),
//                new Vector2(1f, 1.3f),
//                new Vector2(1.3f, 1.29f)
//        });
        shape.setAsBox(getWidth() * 0.49f, getHeight() * 0.459375f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.71f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0;

        return fixtureDef;
    }
}
package com.tendersaucer.joe.level;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.tendersaucer.joe.level.entity.Entity;
import com.tendersaucer.joe.util.box2d.BodyData;

/**
 * Game collision listener
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class CollisionListener implements ContactListener {

    private static final CollisionListener INSTANCE = new CollisionListener();

    private CollisionListener() {

    }

    public static CollisionListener getInstance() {
        return INSTANCE;
    }

    @Override
    public void beginContact(Contact contact) {
        onContact(contact, true);
    }

    @Override
    public void endContact(Contact contact) {
        onContact(contact, false);

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void onContact(Contact contact, boolean beginContact) {
        Entity entityA = getEntity(contact.getFixtureA());
        Entity entityB = getEntity(contact.getFixtureB());
        setEntityContact(contact, entityA, entityB, beginContact);
    }

    private void setEntityContact(Contact contact, Entity entityA, Entity entityB,
                                  boolean beginContact) {
        if (entityA != null) {
            if (beginContact) {
                entityA.onBeginContact(contact, entityB);
            } else {
                entityA.onEndContact(contact, entityB);
            }
        }

        if (entityB != null) {
            if (beginContact) {
                entityB.onBeginContact(contact, entityA);
            } else {
                entityB.onEndContact(contact, entityA);
            }
        }
    }

    private Entity getEntity(Fixture fixture) {
        Body body = fixture.getBody();
        BodyData bodyData = (BodyData)body.getUserData();
        if (bodyData == null) {
            return null;
        }

        return bodyData.entity;
    }
}

package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Contact;
import com.tendersaucer.joe.level.Level;

/**
 * Each platform is numbered (via name) between 0 and 5.
 *
 * There can only be <= 6 platforms in a system (one is dedicated to each color)
 *     - This coloring will happen via a script
 *     - Use ColorScheme.getInstance().getColors()
 *
 * Each platform stores its next move(s) within the system
 *     e.g. Platform 1 has moves "2,3,2,0"
 *
 * Each platform also has an "interaction" interval
 *     - When one's activated, the player has X ms to interact
 *       with it before it "resets" the system
 *     - That can be accomplished easily because the start level is always "simon_0"
 *       and everything else is "simon_X", where 0 < X < 6
 *
 * Created by Alex on 9/19/2016.
 */
public final class SimonPlatform extends RenderedEntity {

    private static final String ID_PREFIX = "simon_";

    private int state;
    private boolean isCurrent;
    private float elapsed;
    private final float interval;
    private final Color startColor;
    private final String[] moves;

    public SimonPlatform(EntityDefinition definition) {
        super(definition);

        interval = definition.getFloatProperty("interval");
        moves = definition.getStringArrayProperty("moves", ",");
        state = isStartPlatform() ? 1 : 0;

        startColor = new Color();
    }

    @Override
    public void tick() {
        if (isCurrent) {
            elapsed += Gdx.graphics.getDeltaTime() * 1000f;
            if (elapsed > interval) {
                resetSystem();
            }
        }
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        if (isCurrent) {
            next();
        }
    }

    public void reset() {
        elapsed = 0;
        state = isStartPlatform() ? 1 : 0;
        sprite.setColor(startColor);
    }

    public void setCurrent(boolean isCurrent) {
        this.isCurrent = isCurrent;

        // TODO: Clear tweens, reset animation, and add new one.
    }

    public void setStartColor(Color startColor) {
        this.startColor.set(startColor);
    }

    private void next() {
        setCurrent(false);

        if (state < moves.length - 1) {
            String id = ID_PREFIX + moves[state];
            SimonPlatform platform = (SimonPlatform)Level.getInstance().getEntity(id);
            platform.setCurrent(true);
        } else {
            // TODO: Activate script to make GroovySpinningThingy reachable.
        }
    }

    private void resetSystem() {
        for (int i = 0; i < 6; i++) {
            String id = ID_PREFIX + i;
            SimonPlatform platform = (SimonPlatform)Level.getInstance().getEntity(id);
            platform.reset();
        }
    }

    private boolean isStartPlatform() {
        return getId().equals(ID_PREFIX);
    }
}

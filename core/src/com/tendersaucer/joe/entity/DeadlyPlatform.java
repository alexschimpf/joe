package com.tendersaucer.joe.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.TimeUtils;
import com.tendersaucer.joe.level.Level;

/**
 * Created by Alex on 9/4/2016.
 */
public final class DeadlyPlatform extends RenderedEntity {

    private boolean isPlayerTouching;
    private long touchStartTime;
    private final float duration;

    private DeadlyPlatform(EntityDefinition definition) {
        super(definition);

        duration = definition.getFloatProperty("duration");
        isPlayerTouching = false;

        if (duration == 0) {
            sprite.setColor(new Color(Color.RED));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (isPlayerTouching) {
            float x = 1;
            if (duration != 0) {
                float timeSinceOnStart = TimeUtils.millis() - touchStartTime;
                x = MathUtils.clamp(timeSinceOnStart / duration, 0, 1);
                sprite.setColor(1, 1 - x, 1 - x, 1);
            }

            if (x >= 1) {
                Level.getInstance().replay();
            }
        }
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        if(Entity.isPlayer(entity)) {
            isPlayerTouching = true;
            touchStartTime = TimeUtils.millis();
        }
    }

    @Override
    public void onEndContact(Contact contact, Entity entity) {
        if(Entity.isPlayer(entity)) {
            isPlayerTouching = false;
            sprite.setColor(1, 1, 1, 1);
        }
    }
}

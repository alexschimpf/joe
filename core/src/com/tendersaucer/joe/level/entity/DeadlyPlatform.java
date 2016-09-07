package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.TimeUtils;
import com.tendersaucer.joe.ColorScheme;
import com.tendersaucer.joe.animation.AnimatedSprite;
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
            sprite.setColor(ColorScheme.getInstance().getSecondaryColor(ColorScheme.ReturnType.SHARED));
        } else {
            sprite.setColor(ColorScheme.getInstance().getPrimaryColor(ColorScheme.ReturnType.SHARED));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (isPlayerTouching) {
            float age = 1;
            if (duration != 0) {
                float timeSinceOnStart = TimeUtils.millis() - touchStartTime;
                age = MathUtils.clamp(timeSinceOnStart / duration, 0, 1);
            }

            if (age >= 1) {
                Level.getInstance().replay();
            }
        }

        ((AnimatedSprite)sprite).update();
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        if(Entity.isPlayer(entity)) {
            isPlayerTouching = true;
            touchStartTime = TimeUtils.millis();
            ((AnimatedSprite)sprite).play();
        }
    }

    @Override
    public void onEndContact(Contact contact, Entity entity) {
        if(duration != 0 && Entity.isPlayer(entity)) {
            isPlayerTouching = false;
            ((AnimatedSprite)sprite).stop(true);
        }
    }

    @Override
    protected Sprite createSprite(EntityDefinition definition) {
        float duration = definition.getFloatProperty("duration");

        AnimatedSprite sprite;
        if (duration == 0) {
            String textureName = definition.getStringProperty("texture");
            sprite = new AnimatedSprite(textureName, 200, null, AnimatedSprite.State.PLAYING);
        } else {
            sprite = new AnimatedSprite("deadly-1x1", duration);
        }

        sprite.setSize(getWidth(), getHeight());

        return sprite;
    }
}

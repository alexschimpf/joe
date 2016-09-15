package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Contact;
import com.tendersaucer.joe.ColorScheme;
import com.tendersaucer.joe.anim.AnimatedSprite;
import com.tendersaucer.joe.level.Level;

/**
 * Created by Alex on 9/4/2016.
 */
public final class DeadlyPlatform extends RenderedEntity {

    private boolean isPlayerTouching;

    private DeadlyPlatform(EntityDefinition definition) {
        super(definition);

        isPlayerTouching = false;
        sprite.setColor(ColorScheme.getInstance().getSecondaryColor(ColorScheme.ReturnType.SHARED));
    }

    @Override
    protected void tick() {
        super.tick();

        if (isPlayerTouching) {
            Level.getInstance().replay();
        }

        ((AnimatedSprite)sprite).update();
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        if (Entity.isPlayer(entity)) {
            isPlayerTouching = true;
            ((AnimatedSprite)sprite).play();
        }
    }

    @Override
    protected Sprite createSprite(EntityDefinition definition) {
        String textureName = definition.getStringProperty("texture");
        AnimatedSprite sprite = new AnimatedSprite(textureName, 200, null, AnimatedSprite.State.PLAYING);
        sprite.setSize(getWidth(), getHeight());

        return sprite;
    }
}

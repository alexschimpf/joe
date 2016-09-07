package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.tendersaucer.joe.Canvas;
import com.tendersaucer.joe.IRender;

/**
 * Created by Alex on 5/9/2016.
 */
public abstract class RenderedEntity extends Entity implements IRender {

    protected Sprite sprite; // subclasses can add more, if necessary

    protected RenderedEntity(EntityDefinition definition) {
        super(definition);

        sprite = createSprite(definition);
        sprite.setColor(definition.getColorProperty("color"));
    }

    @Override
    public void dispose() {
        super.dispose();

        removeFromCanvas();
    }

    @Override
    protected void tick() {
        super.tick();

        sprite.setPosition(getLeft(), getTop());
        sprite.setOrigin(getWidth() / 2, getHeight() / 2);
        sprite.setRotation(MathUtils.radiansToDegrees * body.getAngle());
    }

    protected Sprite createSprite(EntityDefinition definition) {
        Sprite sprite = new Sprite(definition.getTextureRegion());
        sprite.setSize(getWidth(), getHeight());

        return sprite;
    }

    public void render(SpriteBatch spriteBatch) {
        sprite.draw(spriteBatch);
    }

    public void addToCanvas() {
        Canvas.getInstance().addToLayer(definition.getLayer(), this);
    }

    public void removeFromCanvas() {
        Canvas.getInstance().remove(this);
    }

    public Sprite getSprite() {
        return sprite;
    }
}

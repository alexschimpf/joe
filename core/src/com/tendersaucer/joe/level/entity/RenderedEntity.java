package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.Canvas;
import com.tendersaucer.joe.IRender;
import com.tendersaucer.joe.util.tween.ITweenable;
import com.tendersaucer.joe.util.tween.Tween;

import java.util.Iterator;

/**
 * Created by Alex on 5/9/2016.
 */
public abstract class RenderedEntity extends Entity implements IRender, ITweenable {

    protected boolean isVisible;
    protected Sprite sprite; // subclasses can add more, if necessary
    protected Array<Tween> tweens;

    protected RenderedEntity(EntityDefinition definition) {
        super(definition);

        sprite = createSprite(definition);
        sprite.setColor(definition.getColorProperty("color"));
        isVisible = definition.getBooleanProperty("is_visible");
    }

    @Override
    public void dispose() {
        super.dispose();

        removeFromCanvas();
    }

    public RenderedEntity addTween(Tween tween) {
        if (tweens == null) {
            tweens = new Array<Tween>();
        }

        tween.setTarget(this);
        tweens.add(tween);

        return this;
    }

    public RenderedEntity removeTween(Tween tween) {
        if (tweens != null) {
            tweens.removeValue(tween, true);
        }

        return this;
    }

    public void clearTweens() {
        if (tweens != null) {
            tweens.clear();
        }
    }

    public void render(SpriteBatch spriteBatch) {
        if (isVisible()) {
            sprite.draw(spriteBatch);
        }

        if (tweens != null) {
            Iterator<Tween> tweensIter = tweens.iterator();
            while (tweensIter.hasNext()) {
                Tween tween = tweensIter.next();
                if (tween.update()) {
                    tween.onDone();
                    tweensIter.remove();
                }
            }
        }
    }

    public void addToCanvas() {
        Canvas.getInstance().addToLayer(definition.getLayer(), this);
    }

    public void removeFromCanvas() {
        Canvas.getInstance().remove(this);
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isVisible() {
        return isVisible && sprite.getColor().a > 0;
    }

    @Override
    public Sprite getSprite() {
        return sprite;
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
}

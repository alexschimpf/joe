package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.tendersaucer.joe.Canvas;
import com.tendersaucer.joe.ColorScheme;
import com.tendersaucer.joe.Game;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.particle.ParticleEffect;
import com.tendersaucer.joe.particle.ParticleEffectManager;
import com.tendersaucer.joe.util.pool.Vector2Pool;
import com.tendersaucer.joe.util.tween.SequenceTween;
import com.tendersaucer.joe.util.tween.Tween;

/**
 * Created by Alex on 5/31/2016.
 */
public class GroovySpinningThingy extends RenderedEntity {

    public static final String ID = "groovy_spinning_thingy";

    private boolean obtained;

    private GroovySpinningThingy(EntityDefinition def) {
        super(def);

        id = ID;
        obtained = false;
        body.setAngularVelocity(1.5f);
        sprite.setColor(new Color(1, 0, 0, 0.8f));

        body.getFixtureList().get(0).setSensor(true);
    }

    @Override
    public void init() {
        super.init();

        Color[] colors = ColorScheme.getInstance().getColors();
        Tween[] colorTweens = new Tween[colors.length];
        for (int i = 0; i < colorTweens.length - 1; i++) {
            colorTweens[i] = Tween.color(colors[i], colors[i + 1], 1200f / colorTweens.length);
        }
        colorTweens[colorTweens.length - 1] = Tween.color(colors[colors.length - 1], colors[0],
                1200f / colorTweens.length);
        SequenceTween colorSequence = Tween.sequence(colorTweens);

        float fromWidth = getWidth() * 0.9f, toWidth = getWidth() * 1.1f;
        float fromHeight = getHeight() * 0.9f, toHeight = getHeight() * 1.1f;
        addTween(Tween.loop(
                Tween.parallel(
                        colorSequence,
                        Tween.sequence(
                                Tween.size(fromWidth, fromHeight, toWidth, toHeight, 600f),
                                Tween.size(toWidth, toHeight, fromWidth, fromHeight, 600f)
                        )
                )
        ).setState(Tween.State.ACTIVE));
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        if (!obtained && Entity.isPlayer(entity)) {
            obtained = true;

            setDone();
            beginParticleEffect();

            Globals.setGameState(Game.State.LEVEL_COMPLETE);
        }
    }

    private void beginParticleEffect() {
        for (int layer = 1; layer < Canvas.NUM_LAYERS; layer += 2) {
            Vector2Pool vector2Pool = Vector2Pool.getInstance();
            Vector2 sizeRange = vector2Pool.obtain(getWidth() * 0.75f, getWidth() * 3);
            Vector2 position = vector2Pool.obtain(getLeft(), getBottom() - (sizeRange.y / 2));
            ParticleEffect effect =
                    ParticleEffectManager.getInstance().buildParticleEffect("level_complete");
            Color color = ColorScheme.getInstance().getSecondaryColor(ColorScheme.ReturnType.NEW);
            effect.setRedRange(color.r, 1);
            effect.setGreenRange(color.g, 1);
            effect.setBlueRange(color.b, 1);
            effect.setAlphaRange(color.a, color.a);

            ParticleEffectManager.getInstance().beginParticleEffect(effect, position, sizeRange, layer);
            vector2Pool.free(position);
            vector2Pool.free(sizeRange);
        }
    }
}
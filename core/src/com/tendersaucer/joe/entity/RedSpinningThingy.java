package com.tendersaucer.joe.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.utils.Timer;
import com.tendersaucer.joe.ColorScheme;
import com.tendersaucer.joe.GameState;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.gen.ParticleConstants;
import com.tendersaucer.joe.particle.ParticleEffect;
import com.tendersaucer.joe.particle.ParticleEffectManager;
import com.tendersaucer.joe.screen.Canvas;
import com.tendersaucer.joe.util.Vector2Pool;

/**
 * Created by Alex on 5/31/2016.
 */
public class RedSpinningThingy extends RenderedEntity {

    public static final String ID = "red_spinning_thingy";
    public static final Color ON_COLOR = new Color(1, 0, 0, 0.8f);
    public static final Color OFF_COLOR = new Color(0.7f, 0, 0, 0.8f);

    private boolean obtained;
    private Timer timer;

    private RedSpinningThingy(EntityDefinition def) {
        super(def);

        id = ID;
        obtained = false;
        body.setAngularVelocity(1.5f);
        sprite.setColor(ON_COLOR);

        body.getFixtureList().get(0).setSensor(true);
    }

    @Override
    public void init() {
        super.init();

        timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (sprite.getColor().equals(ON_COLOR)) {
                    sprite.setColor(OFF_COLOR);
                } else  {
                    sprite.setColor(ON_COLOR);
                }
            }
        }, 0, 0.25f);
    }

    @Override
    public void dispose() {
        super.dispose();

        timer.clear();
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        if (!obtained && Entity.isPlayer(entity)) {
            obtained = true;
            setDone();
            beginParticleEffect();
            Globals.setGameState(GameState.LEVEL_COMPLETE);
        }
    }

    private void beginParticleEffect() {
        for (int layer = 1; layer < Canvas.NUM_LAYERS; layer += 2) {
            Vector2Pool vector2Pool = Vector2Pool.getInstance();
            Vector2 sizeRange = vector2Pool.obtain(-getWidth() * 2, getWidth() * 2);
            Vector2 position = vector2Pool.obtain(getLeft(), getBottom() - (sizeRange.y / 2));
            ParticleEffect effect =
                    ParticleEffectManager.getInstance().buildParticleEffect(ParticleConstants.LEVEL_COMPLETE);
            Color color = ColorScheme.getInstance().getSecondaryColor(ColorScheme.ReturnType.NEW);
            effect.setRedRange(color.r, color.r);
            effect.setGreenRange(color.g, color.g);
            effect.setBlueRange(color.b, color.b);
            effect.setAlphaRange(color.a, color.a);

            ParticleEffectManager.getInstance().beginParticleEffect(effect, position, sizeRange, layer);
            vector2Pool.free(position);
            vector2Pool.free(sizeRange);
        }
    }
}
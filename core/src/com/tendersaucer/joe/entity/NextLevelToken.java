package com.tendersaucer.joe.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.tendersaucer.joe.GameState;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.gen.ParticleConstants;
import com.tendersaucer.joe.particle.ParticleEffect;
import com.tendersaucer.joe.particle.ParticleEffectManager;
import com.tendersaucer.joe.util.Vector2Pool;

/**
 * Created by Alex on 5/31/2016.
 */
public class NextLevelToken extends RenderedEntity {

    public static final String ID = "next_level_token";

    private boolean obtained;

    private NextLevelToken(EntityDefinition def) {
        super(def);

        id = ID;
        obtained = false;
        body.setAngularVelocity(1.5f);
        sprite.setColor(Color.RED);

        body.getFixtureList().get(0).setSensor(true);
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        if (!obtained && Entity.isPlayer(entity)) {
            obtained = true;

            Gdx.app.debug("NextLevelToken", "Next level token obtained...");
            setDone();
            beginParticleEffect();

            Globals.setGameState(GameState.LEVEL_COMPLETE);
        }
    }

    private void beginParticleEffect() {
        Vector2Pool vector2Pool = Vector2Pool.getInstance();
        Vector2 sizeRange = vector2Pool.obtain(-getWidth() * 2, getWidth() * 2);
        Vector2 position = vector2Pool.obtain(getLeft(), getBottom() - (sizeRange.y / 2));
        ParticleEffect effect =
                ParticleEffectManager.getInstance().buildParticleEffect(ParticleConstants.LEVEL_COMPLETE);
        ParticleEffectManager.getInstance().beginParticleEffect(effect, position, sizeRange, 9);
        vector2Pool.free(position);
        vector2Pool.free(sizeRange);
    }
}
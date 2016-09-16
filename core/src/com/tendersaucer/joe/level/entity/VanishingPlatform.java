package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Timer;
import com.tendersaucer.joe.Canvas;
import com.tendersaucer.joe.anim.AnimatedSprite;
import com.tendersaucer.joe.level.Level;

import java.util.UUID;

/**
 * Created by Alex on 8/14/2016.
 */
public class VanishingPlatform extends RenderedEntity {

    private boolean isReappearWait;
    private boolean isVanishing;
    private float vanishElapsed;
    private final float vanishDuration;
    private final float reappearDelay;

    private VanishingPlatform(EntityDefinition def) {
        super(def);

        vanishDuration = def.getFloatProperty("vanish_duration");
        reappearDelay = def.getFloatProperty("reappear_delay");
        isVanishing = false;
        isReappearWait = false;
        vanishElapsed = 0;
    }

    @Override
    protected void tick() {
        super.tick();

        if (isVanishing) {
            vanishElapsed += Gdx.graphics.getDeltaTime() * 1000;
            if (vanishElapsed > vanishDuration) {
                isVanishing = false;
                scheduleReappear();
                setDone();
            }
        }

        Player player = Level.getInstance().getPlayer();
        if (isReappearWait && player != null && !player.overlaps(this)) {
            setReappearWait(false);
        }

        ((AnimatedSprite)sprite).update();
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        if (!isVanishing && !isReappearWait && Entity.isPlayer(entity)) {
            vanish();
        }
    }

    public void setReappearWait(boolean isReappearWait) {
        this.isReappearWait = isReappearWait;

        Fixture fixture = body.getFixtureList().get(0);
        fixture.setSensor(isReappearWait);
        sprite.setAlpha(isReappearWait ? 0 : 1);
    }

    @Override
    protected Sprite createSprite(EntityDefinition definition) {
        AnimatedSprite sprite = new AnimatedSprite(definition.getStringProperty("texture"),
                definition.getFloatProperty("vanish_duration"));
        sprite.setSize(getWidth(), getHeight());

        return sprite;
    }

    private void scheduleReappear() {
        final int oldLevelId = Level.getInstance().getId();
        new Timer().scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        if (oldLevelId != Level.getInstance().getId()) {
                            return;
                        }

                        TiledEntityDefinition offspringDefinition =
                                new TiledEntityDefinition(UUID.randomUUID().toString(), (TiledEntityDefinition)definition);
                        VanishingPlatform offspring = (VanishingPlatform)Entity.build(offspringDefinition);
                        offspring.setReappearWait(true);
                        Level.getInstance().addEntity(offspring);
                        Canvas.getInstance().addToLayer(offspringDefinition.getLayer(), offspring);
                    }
                });
            }
        }, reappearDelay / 1000);
    }

    public void vanish() {
        isVanishing = true;
        ((AnimatedSprite)sprite).play();
    }
}

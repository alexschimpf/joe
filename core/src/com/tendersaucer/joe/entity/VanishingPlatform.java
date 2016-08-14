package com.tendersaucer.joe.entity;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.tendersaucer.joe.level.Level;
import com.tendersaucer.joe.screen.Canvas;

import java.util.UUID;

/**
 * Created by Alex on 8/14/2016.
 */
public class VanishingPlatform extends RenderedEntity {

    private boolean isReappearWait;
    private boolean isVanishing;
    private long vanishStartTime;
    private final float vanishDuration;
    private final float reappearDelay;

    public VanishingPlatform(EntityDefinition def) {
        super(def);

        vanishDuration = def.getFloatProperty("vanish_duration");
        reappearDelay = def.getFloatProperty("reappear_delay");
        isVanishing = false;
        isReappearWait = false;
    }

    @Override
    public void tick() {
        super.tick();

        if(isVanishing) {
            float timeSinceDisappearStart = TimeUtils.millis() - vanishStartTime;
            sprite.setAlpha(1 - (timeSinceDisappearStart / vanishDuration));

            if(timeSinceDisappearStart > vanishDuration) {
                isVanishing = false;
                scheduleReappear();
                setDone();
            }
        }

        if(isReappearWait && !Level.getInstance().getPlayer().overlaps(this)) {
            setReappearWait(false);
        }
    }

    @Override
    public void onBeginContact(Contact contact, Entity entity) {
        if(!isVanishing && !isReappearWait && Entity.isPlayer(entity)) {
            vanish();
        }
    }

    public void setReappearWait(boolean isReappearWait) {
        this.isReappearWait = isReappearWait;

        System.out.println("isReappearWait=" + isReappearWait);
        Fixture fixture = body.getFixtureList().get(0);
        fixture.setSensor(isReappearWait);
        sprite.setAlpha(isReappearWait ? 0 : 1);
    }

    private void scheduleReappear() {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                TiledEntityDefinition offspringDefinition =
                        new TiledEntityDefinition(UUID.randomUUID().toString(), (TiledEntityDefinition)definition);
                VanishingPlatform offspring = (VanishingPlatform)Entity.build(offspringDefinition);
                offspring.setReappearWait(true);
                Level.getInstance().addEntity(offspring);
                Canvas.getInstance().addToLayer(offspringDefinition.getLayer(), offspring);
            }
        }, reappearDelay / 1000);
    }

    public void vanish() {
        isVanishing = true;
        vanishStartTime = TimeUtils.millis();
    }
}

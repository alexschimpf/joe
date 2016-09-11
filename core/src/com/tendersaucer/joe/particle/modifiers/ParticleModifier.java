package com.tendersaucer.joe.particle.modifiers;

import com.badlogic.gdx.utils.JsonValue;
import com.tendersaucer.joe.particle.Particle;

/**
 * Created by Alex on 4/30/2016.
 */
public abstract class ParticleModifier {

    public ParticleModifier(JsonValue root) {
        load(root);
    }

    public abstract void modify(Particle particle);

    protected abstract void load(JsonValue json);
}

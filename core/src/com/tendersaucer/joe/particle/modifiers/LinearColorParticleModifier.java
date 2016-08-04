package com.tendersaucer.joe.particle.modifiers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonValue;
import com.tendersaucer.joe.particle.Particle;
import com.tendersaucer.joe.util.ConversionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 4/30/2016.
 */
public class LinearColorParticleModifier extends ParticleModifier {

    protected Color startColor;
    protected Color endColor;
    protected Map<Particle, Color> particleStartColorMap;

    public LinearColorParticleModifier(JsonValue json) {
        super(json);

        particleStartColorMap = new HashMap<Particle, Color>();
    }

    @Override
    public void modify(Particle particle) {
        if (!particleStartColorMap.containsKey(particle)) {
            particleStartColorMap.put(particle, particle.getColor());
        }
        startColor = particleStartColorMap.get(particle);

        float ageToLifeRatio = particle.getAgeToLifeRatio();
        float r = interpolate(startColor.r, endColor.r, ageToLifeRatio);
        float g = interpolate(startColor.g, endColor.g, ageToLifeRatio);
        float b = interpolate(startColor.b, endColor.b, ageToLifeRatio);
        float a = interpolate(startColor.a, endColor.a, ageToLifeRatio);
        particle.setColor(r, g, b, a);
    }

    @Override
    protected void load(JsonValue json) {
        if (json.has("start")) {
            startColor = ConversionUtils.toColor(json.get("start"));
        }

        endColor = ConversionUtils.toColor(json.get("end"));
    }
}

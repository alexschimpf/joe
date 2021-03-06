package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.util.path.LinearPathHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alex on 8/20/2016.
 */
public class MovingPlatform extends RenderedEntity {

    protected static final Pattern pattern = Pattern.compile("\\((.+?),(.+?)\\)");

    protected Float elapsed;
    protected final LinearPathHelper pathHelper;
    protected final float duration;

    protected MovingPlatform(EntityDefinition definition) {
        super(definition);

        duration = definition.getFloatProperty("duration");

        Array<Vector2> path = new Array<Vector2>();
        String[] vectorStrings = definition.getStringArrayProperty("path", ":");
        for (String vectorString : vectorStrings) {
            Matcher matcher = pattern.matcher(vectorString);
            if (matcher.matches()) {
                float dx = Float.parseFloat(matcher.group(1));
                float dy = Float.parseFloat(matcher.group(2));
                float tileSize = MainCamera.getInstance().getTileSize();
                path.add(new Vector2(dx * tileSize, dy * tileSize));
            }
        }

        pathHelper = new LinearPathHelper(path, true);
    }

    @Override
    protected void tick() {
        super.tick();

        if (elapsed == null) {
            elapsed = 0f;
            return;
        }

        elapsed += Gdx.graphics.getDeltaTime() * 1000;
        setLinearVelocity(pathHelper.getVelocity(duration, elapsed).scl(1000));
    }
}

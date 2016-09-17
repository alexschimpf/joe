package com.tendersaucer.joe.level.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.tendersaucer.joe.AssetManager;
import com.tendersaucer.joe.util.StringUtils;
import com.tendersaucer.joe.util.tiled.TiledUtils;

import java.util.Iterator;

/**
 * Entity definition also based on Tiled map objects and properties
 * <p/>
 * Created by Alex on 4/12/2016.
 */
public final class TiledEntityDefinition extends EntityDefinition {

    private final int layer;
    private final MapProperties properties;
    private final MapObject bodySkeleton;
    private final FixtureDef fixtureDef;
    private final TextureRegion textureRegion;

    public TiledEntityDefinition(String name, String type, int layer, BodyDef bodyDef,
                                 MapObject bodySkeleton, MapProperties properties,
                                 TextureRegion textureRegion) {
        super(name, type, bodyDef);

        this.layer = layer;
        this.properties = properties;
        this.bodySkeleton = bodySkeleton;
        fixtureDef = TiledUtils.getFixtureDefFromBodySkeleton(bodySkeleton);

        String textureKey = properties.get("texture").toString();
        if (StringUtils.isEmpty(textureKey)) {
            this.textureRegion = textureRegion;
        } else {
            this.textureRegion = AssetManager.getInstance().getTextureRegion(textureKey);
        }

        // This is kind of hacky...
        Iterator<String> keys = properties.getKeys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = properties.get(key);
            if (value.toString().toLowerCase().equals("null")) {
                properties.put(key, null);
            }
        }
    }

    public TiledEntityDefinition(String name, TiledEntityDefinition other) {
        this(name, other.type, other.layer, other.bodyDef, other.bodySkeleton, other.properties,
                other.textureRegion);
    }

    @Override
    public Object getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    @Override
    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }

    @Override
    public int getLayer() {
        return layer;
    }
}

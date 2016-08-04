package com.tendersaucer.joe.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.tendersaucer.joe.util.TiledUtils;

/**
 * Entity definition also based on Tiled map objects and properties
 * <p/>
 * Created by Alex on 4/12/2016.
 */
public final class TiledEntityDefinition extends EntityDefinition {

    private final int layer;
    private final MapProperties properties;
    private final FixtureDef fixtureDef;
    private final TextureRegion textureRegion;

    public TiledEntityDefinition(String name, String type, int layer, BodyDef bodyDef,
                                 MapObject bodySkeleton, MapProperties properties,
                                 TextureRegion textureRegion) {
        super(name, type, bodyDef);

        this.layer = layer;
        this.properties = properties;
        this.textureRegion = textureRegion;
        fixtureDef = TiledUtils.getFixtureDefFromBodySkeleton(bodySkeleton);
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

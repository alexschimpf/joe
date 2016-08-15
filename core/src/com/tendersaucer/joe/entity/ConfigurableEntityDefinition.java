package com.tendersaucer.joe.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * Created by Alex on 5/21/2016.
 */
public abstract class ConfigurableEntityDefinition extends EntityDefinition {

    private int layer;
    private FixtureDef fixtureDef;
    private TextureRegion textureRegion;

    public ConfigurableEntityDefinition(String id, String type, BodyDef bodyDef) {
        super(id, type, bodyDef);
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

    public void setLayer(int layer) {
        this.layer = layer;
    }

    public void setFixtureDef(FixtureDef fixtureDef) {
        this.fixtureDef = fixtureDef;
    }

    public void setTextureRegion(TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
    }
}

package com.tendersaucer.joe.entity;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.util.Definition;

/**
 * Abstract entity definition
 * <p/>
 * Created by Alex on 4/13/2016.
 */
public abstract class EntityDefinition extends Definition {

    protected static final String WIDTH_PROP = "width";
    protected static final String HEIGHT_PROP = "height";

    protected final String id;
    protected final String type;
    protected final BodyDef bodyDef;
    protected final Vector2 size;

    public EntityDefinition(String id, String type, BodyDef bodyDef) {
        super();

        this.id = id;
        this.type = type;
        this.bodyDef = bodyDef;

        size = new Vector2();
    }

    public abstract TextureRegion getTextureRegion();

    public abstract FixtureDef getFixtureDef();

    public abstract int getLayer();

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public Vector2 getSize() {
        float unitScale = MainCamera.getInstance().getTileMapScale();
        float width = getFloatProperty(WIDTH_PROP) * unitScale;
        float height = getFloatProperty(HEIGHT_PROP) * unitScale;

        return size.set(width, height);
    }

    public Vector2 getCenter() {
        return bodyDef.position;
    }

    public BodyDef.BodyType getBodyType() {
        return bodyDef.type;
    }
}

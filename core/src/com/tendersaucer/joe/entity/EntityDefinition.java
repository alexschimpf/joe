package com.tendersaucer.joe.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.tendersaucer.joe.MainCamera;

/**
 * Abstract entity definition
 * <p/>
 * Created by Alex on 4/13/2016.
 */
public abstract class EntityDefinition {

    protected static final String WIDTH_PROP = "width";
    protected static final String HEIGHT_PROP = "height";

    protected final String id;
    protected final String type;
    protected final BodyDef bodyDef;
    protected final Vector2 size;

    public EntityDefinition(String id, String type, BodyDef bodyDef) {
        this.id = id;
        this.type = type;
        this.bodyDef = bodyDef;

        size = new Vector2();
    }

    public abstract Object getProperty(String key);

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

    public boolean propertyExists(String key) {
        return getStringProperty(key) != null;
    }

    public boolean isPropertyEmpty(String key) {
        return !propertyExists(key) || getStringProperty(key).equals("");
    }

    public String getStringProperty(String key) {
        return getProperty(key).toString();
    }

    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getStringProperty(key));
    }

    public int getIntProperty(String key) {
        return Integer.parseInt(getStringProperty(key));
    }

    public float getFloatProperty(String key) {
        return Float.parseFloat(getStringProperty(key));
    }

    public Color getColorProperty(String key) {
        float[] rgba = getFloatArrayProperty(key, ",");
        return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    public Vector2 getVector2Property(String key) {
        if (getStringProperty(key).equals("")) {
            return new Vector2();
        }

        float[] vals = getFloatArrayProperty(key, ",");
        return new Vector2(vals[0], vals[1]);
    }

    public boolean[] getBooleanArrayProperty(String key, String delim) {
        String full = getStringProperty(key);
        if (full.equals("")) {
            return new boolean[0];
        }

        String[] strArr = full.split(delim);
        boolean[] booleanArr = new boolean[strArr.length];

        int i = 0;
        for (String elem : strArr) {
            booleanArr[i++] = Boolean.parseBoolean(elem);
        }

        return booleanArr;
    }

    public int[] getIntArrayProperty(String key, String delim) {
        String full = getStringProperty(key);
        if (full.equals("")) {
            return new int[0];
        }

        String[] strArr = full.split(delim);
        int[] intArr = new int[strArr.length];

        int i = 0;
        for (String elem : strArr) {
            intArr[i++] = Integer.parseInt(elem);
        }

        return intArr;
    }

    public float[] getFloatArrayProperty(String key, String delim) {
        String full = getStringProperty(key);
        if (full.equals("")) {
            return new float[0];
        }

        String[] strArr = full.split(delim);
        float[] floatArr = new float[strArr.length];

        int i = 0;
        for (String elem : strArr) {
            floatArr[i++] = Float.parseFloat(elem);
        }

        return floatArr;
    }

    public String[] getStringArrayProperty(String key, String delim) {
        String full = getStringProperty(key);
        if (full.equals("")) {
            return new String[0];
        }

        return full.split(delim);
    }
}

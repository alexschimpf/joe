package com.tendersaucer.joe;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.event.listeners.ILevelLoadBeginListener;
import com.tendersaucer.joe.level.ILevelLoadable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Canvas, composed of layers of IRender objects
 * <p/>
 * Created by Alex on 4/10/2016.
 */
public final class Canvas implements IRender, ILevelLoadBeginListener {

    public static final int NUM_LAYERS = 10;
    private static final Canvas INSTANCE = new Canvas();

    private final Map<IRender, Integer> objectLayerMap;
    private final Array<LinkedHashMap<IRender, Boolean>> layers; // 0 = Background, 10 = Foreground

    private Canvas() {
        objectLayerMap = new ConcurrentHashMap<IRender, Integer>();
        layers = new Array<LinkedHashMap<IRender, Boolean>>();
        for (int i = 0; i < NUM_LAYERS; i++) {
            layers.add(new LinkedHashMap<IRender, Boolean>());
        }
    }

    public static Canvas getInstance() {
        return INSTANCE;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        for (int i = 0; i < layers.size; i++) {
            LinkedHashMap<IRender, Boolean> layer = layers.get(i);
            for (IRender object : layer.keySet()) {
                object.render(spriteBatch);
            }
        }
    }

    @Override
    public void onLevelLoadBegin(ILevelLoadable levelLoadable) {
        clearLayers();
    }

    public void clearLayers() {
        for (int i = 0; i < NUM_LAYERS; i++) {
            layers.get(i).clear();
        }

        objectLayerMap.clear();
    }

    public void clearLayer(int layer) {
        checkLayer(layer);
        layers.get(layer).clear();

        Iterator objectLayerMapIter = objectLayerMap.keySet().iterator();
        while (objectLayerMapIter.hasNext()) {
            IRender object = (IRender)objectLayerMapIter.next();
            int objectLayer = objectLayerMap.get(object);
            if (objectLayer == layer) {
                objectLayerMapIter.remove();
            }
        }
    }

    public void addToLayer(int layer, IRender object) {
        checkLayer(layer);
        layers.get(layer).put(object, true);
        objectLayerMap.put(object, layer);
    }

    public void remove(IRender object) {
        Integer layer = objectLayerMap.get(object);
        if (layer != null) {
            layers.get(layer).remove(object);
            objectLayerMap.remove(object);
        }
    }

    private void checkLayer(int layer) {
        if (layer < 0 || layer > NUM_LAYERS - 1) {
            throw new IndexOutOfBoundsException("Layer " + layer + " is out of bounds");
        }
    }
}


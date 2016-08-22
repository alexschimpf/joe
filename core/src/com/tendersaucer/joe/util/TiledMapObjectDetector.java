package com.tendersaucer.joe.util;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Alex on 8/21/2016.
 */
public final class TiledMapObjectDetector {

    private final TiledMapTileLayer layer;

    public TiledMapObjectDetector(TiledMapTileLayer layer) {
        this.layer = layer;
    }

    /**
     * Runs connected components algorithm on raw tiled map layer
     * @return groups of connected (non-empty) cells
     */
    public Array<Array<TiledMapTileLayer.Cell>> findObjects() {
        Array<Array<TiledMapTileLayer.Cell>> objects =
                new Array<Array<TiledMapTileLayer.Cell>>();

        // TODO

        return objects;
    }
}

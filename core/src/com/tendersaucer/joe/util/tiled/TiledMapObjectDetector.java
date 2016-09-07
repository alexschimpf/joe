package com.tendersaucer.joe.util.tiled;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 8/21/2016.
 */
public final class TiledMapObjectDetector {

    private final TiledMapTileLayer layer;
    private final Map<Vector2, Boolean> unvisited;

    public TiledMapObjectDetector(TiledMapTileLayer layer) {
        this.layer = layer;

        unvisited = new HashMap<Vector2, Boolean>();
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) {
                    setVisited(new Vector2(x, y), false);
                }
            }
        }
    }

    /**
     * Runs connected components algorithm on raw tiled map layer
     * @return groups of connected (non-empty) cells
     */
    public Array<Array<TiledMapTileLayer.Cell>> findObjects() {
        Array<Array<TiledMapTileLayer.Cell>> objects =
                new Array<Array<TiledMapTileLayer.Cell>>();

        while (!unvisited.isEmpty()) {
            Vector2 unvisitedPos = getUnvisited();
            Array<TiledMapTileLayer.Cell> object = DFS((int)unvisitedPos.x, (int)unvisitedPos.y);
            objects.add(object);
        }

        return objects;
    }

    private Array<TiledMapTileLayer.Cell> DFS(int x, int y) {
        if (layer.getCell(x, y) == null) {
            return null;
        }

        Array<TiledMapTileLayer.Cell> object = new Array<TiledMapTileLayer.Cell>();
        Array<Vector2> stack = new Array<Vector2>();
        stack.add(new Vector2(x, y));
        while (stack.size > 0) {
            Vector2 curr = stack.pop();
            if (!isVisited(curr)) {
                object.add(layer.getCell((int)curr.x, (int)curr.y));
                setVisited(curr, true);
                for (Vector2 neighbor : getNeighbors(curr)) {
                    stack.add(neighbor);
                }
            }
        }

        return object;
    }

    private Array<Vector2> getNeighbors(Vector2 pos) {
        Array<Vector2> neighbors = new Array<Vector2>();
        if (layer.getCell((int)pos.x - 1, (int)pos.y) != null) {
            neighbors.add(new Vector2(pos.x - 1, pos.y));
        }
        if (layer.getCell((int)pos.x + 1, (int)pos.y) != null) {
            neighbors.add(new Vector2(pos.x + 1, pos.y));
        }
        if (layer.getCell((int)pos.x, (int)pos.y - 1) != null) {
            neighbors.add(new Vector2(pos.x, pos.y - 1));
        }
        if (layer.getCell((int)pos.x, (int)pos.y + 1) != null) {
            neighbors.add(new Vector2(pos.x, pos.y + 1));
        }

        return neighbors;
    }

    private void setVisited(Vector2 pos, boolean visited) {
        if (visited) {
            unvisited.remove(pos);
        } else {
            unvisited.put(pos, true);
        }
    }

    private boolean isVisited(Vector2 pos) {
        return !unvisited.containsKey(pos);
    }

    private Vector2 getUnvisited() {
        return unvisited.keySet().iterator().next();
    }
}

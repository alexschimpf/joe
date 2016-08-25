package com.tendersaucer.joe.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.ColorScheme;
import com.tendersaucer.joe.screen.IRender;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper around TiledMapLayer
 * <p/>
 * Created by Alex on 4/12/2016.
 */
public final class MapLayerWrapper implements IRender {

    private final MapLayer rawLayer;
    private final ColoredOrthogonalTiledMapRenderer renderer;

    public MapLayerWrapper(ColoredOrthogonalTiledMapRenderer renderer, MapLayer rawLayer) {
        this.renderer = renderer;

        if (rawLayer instanceof TiledMapTileLayer) {
            correctCells((TiledMapTileLayer)rawLayer);
        }
        this.rawLayer = rawLayer;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        renderer.renderTileLayer((TiledMapTileLayer)rawLayer);
    }

    public MapProperties getProperties() {
        return rawLayer.getProperties();
    }

    public String getName() {
        return rawLayer.getName();
    }

    public MapObjects getObjects() {
        return rawLayer.getObjects();
    }

    private void correctCells(TiledMapTileLayer rawLayer) {
        Array<Array<TiledMapTileLayer.Cell>> objects = null;
        if (rawLayer.getObjects().getCount() == 0) {
            TiledMapObjectDetector detector = new TiledMapObjectDetector(rawLayer);
            objects = detector.findObjects();
        }

        if (objects != null) {
            ColorScheme.ColorType layerColorType = null;
            MapProperties properties = rawLayer.getProperties();
            if (properties.containsKey("color_type")) {
                String colorType = properties.get("color_type").toString();
                layerColorType = ColorScheme.ColorType.valueOf(colorType.toUpperCase());
            }

            Color objectColor;
            Map<TiledMapTileLayer.Cell, Color> cellColorMap = new HashMap<TiledMapTileLayer.Cell, Color>();
            for (Array<TiledMapTileLayer.Cell> object : objects) {
                if (layerColorType == ColorScheme.ColorType.PRIMARY) {
                    objectColor = ColorScheme.getInstance().getShadedPrimaryColor();
                } else {
                    objectColor = ColorScheme.getInstance().getShadedSecondaryColor();
                }

                for (TiledMapTileLayer.Cell cell : object) {
                    cellColorMap.put(cell, objectColor);
                }
            }
            renderer.addCellColorMap(cellColorMap);
        }

        for (int col = 0; col < rawLayer.getWidth(); col++) {
            for (int row = 0; row < rawLayer.getHeight(); row++) {
                TiledMapTileLayer.Cell cell = rawLayer.getCell(col, row);
                if (cell != null) {
                    cell.setFlipVertically(true);
                }
            }
        }
    }
}

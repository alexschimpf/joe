package com.tendersaucer.joe.parallax;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.IRender;

/**
 * Simple parallax background
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public class ParallaxBackground implements IRender {

    protected final Vector2 cachedCameraPosition;
    protected final Matrix4 cachedCameraMatrix;
    protected final Array<ParallaxLayer> layers;

    public ParallaxBackground() {
        cachedCameraPosition = new Vector2();
        cachedCameraMatrix = new Matrix4();
        layers = new Array<ParallaxLayer>();
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        for (ParallaxLayer layer : layers) {
            adjustCamera(spriteBatch, layer.getParallaxRatio());

            // Reposition camera based on layer's parallax ratio.
            // Determine start/end positions for layer rendering, since each layer repeats.
            MainCamera camera = MainCamera.getInstance();
            float width = layer.getWidth();
            float height = layer.getHeight();
            if (camera.isFlipped()) {
                // left = 100, right = 0, w/h = 50
                float startLeft = (MathUtils.floor(camera.getLeft() / width) + 1) * width;
                float startTop = (MathUtils.floor(camera.getTop() / height) - 2) * height;
                int numXRepeats = MathUtils.ceil(Math.abs(startLeft + camera.getRight()) / width) + 3;
                int numYRepeats = MathUtils.ceil(Math.abs(startTop - camera.getBottom()) / height) + 2;
                float endLeft = startLeft - (numXRepeats * width);
                float endTop = startTop + (numYRepeats * height);
                for (float left = startLeft; left > endLeft; left -= width) {
                    for (float top = startTop; top < endTop; top += height) {
                        layer.setTopLeft(left, top);
                        layer.render(spriteBatch);
                    }
                }
            } else {
                float startLeft = (MathUtils.floor(camera.getLeft() / width) - 2) * width;
                float startTop = (MathUtils.floor(camera.getTop() / height) - 2) * height;
                int numXRepeats = MathUtils.ceil(Math.abs(startLeft - camera.getRight()) / width) + 2;
                int numYRepeats = MathUtils.ceil(Math.abs(startTop - camera.getBottom()) / height) + 2;
                float endLeft = startLeft + (numXRepeats * width);
                float endTop = startTop + (numYRepeats * height);
                for (float left = startLeft; left < endLeft; left += width) {
                    for (float top = startTop; top < endTop; top += height) {
                        layer.setTopLeft(left, top);
                        layer.render(spriteBatch);
                    }
                }
            }

            resetCamera(spriteBatch);
        }
    }

    public void addLayer(ParallaxLayer layer) {
        layers.add(layer);
    }

    public void setColor(Color color) {
        for (ParallaxLayer layer : layers) {
            layer.setColor(color);
        }
    }

    private void adjustCamera(SpriteBatch spriteBatch, float parallaxRatio) {
        OrthographicCamera camera = MainCamera.getInstance().getRawCamera();
        cachedCameraMatrix.set(camera.combined);
        cachedCameraPosition.set(camera.position.x, camera.position.y);
        camera.position.set(cachedCameraPosition.x * parallaxRatio,
                cachedCameraPosition.y * parallaxRatio, camera.position.z);
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);
    }

    private void resetCamera(SpriteBatch spriteBatch) {
        OrthographicCamera camera = MainCamera.getInstance().getRawCamera();
        camera.combined.set(cachedCameraMatrix);
        camera.position.set(cachedCameraPosition.x, cachedCameraPosition.y, camera.position.z);
        camera.update();

        spriteBatch.setProjectionMatrix(camera.combined);
    }
}

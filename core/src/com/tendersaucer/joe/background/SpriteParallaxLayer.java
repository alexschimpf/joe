package com.tendersaucer.joe.background;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tendersaucer.joe.AssetManager;
import com.tendersaucer.joe.ColorScheme;

/**
 * Parallax layer from single texture
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public class SpriteParallaxLayer extends ParallaxLayer {

    private static int WIDTH = 100;
    private static int HEIGHT = 50;

    protected final Sprite sprite;

    public SpriteParallaxLayer(float parallaxRatio, String textureName) {
        super(parallaxRatio);

        sprite = AssetManager.getInstance().getSprite(textureName);
        sprite.setSize(WIDTH, HEIGHT);
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        sprite.setColor(ColorScheme.getInstance().getSecondaryColor(ColorScheme.ReturnType.SHARED));
        sprite.setPosition(topLeft.x, topLeft.y);
        sprite.draw(spriteBatch);
    }

    @Override
    public float getWidth() {
        return sprite.getWidth();
    }

    @Override
    public float getHeight() {
        return sprite.getHeight();
    }

    @Override
    public void setColor(Color color) {
        sprite.setColor(color);
    }
}

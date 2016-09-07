package com.tendersaucer.joe;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Interface for rendered objects
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public interface IRender {

    /**
     * Renders the object, using a single spriteBatch
     *
     * @param spriteBatch
     */
    void render(SpriteBatch spriteBatch);
}

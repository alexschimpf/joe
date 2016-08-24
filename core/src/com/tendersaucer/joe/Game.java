package com.tendersaucer.joe;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.tendersaucer.joe.screen.MainMenu;

import java.util.UUID;

/**
 * Game entry point
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class Game extends com.badlogic.gdx.Game {

    public static Game instance;

    @Override
    public void create() {
        instance = this;
        setScreen(new MainMenu(this));
    }

}

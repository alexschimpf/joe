package com.tendersaucer.joe;

import com.tendersaucer.joe.screen.MainMenu;

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

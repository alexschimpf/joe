package com.tendersaucer.joe.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.tendersaucer.joe.Game;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.ParticleEffectViewer;

public class DesktopLauncher {

    private enum RunMode {
        GAME, PARTICLE_EFFECT_VIEWER
    }

    private static final RunMode RUN_MODE = RunMode.GAME;

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.vSyncEnabled = true;
        config.fullscreen = Globals.FULLSCREEN_MODE;
        config.resizable = false;
        config.title = "joe";

        // TODO: How to get native resolution?
        if (Globals.FULLSCREEN_MODE) {
            config.width = 3200;
            config.height = 1800;
        } else {
            config.width = 1280;
            config.height = 720;
        }

        switch (RUN_MODE) {
            case GAME:
                new LwjglApplication(new Game(), config);
                break;
            case PARTICLE_EFFECT_VIEWER:
                new LwjglApplication(new ParticleEffectViewer(), config);
                break;
        }
    }
}

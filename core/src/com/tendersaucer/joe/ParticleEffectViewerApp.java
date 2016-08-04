package com.tendersaucer.joe;

import com.tendersaucer.joe.screen.ParticleEffectViewer;

/**
 * Created by Alex on 5/26/2016.
 */
public class ParticleEffectViewerApp extends com.badlogic.gdx.Game {

    @Override
    public void create() {
        setScreen(new ParticleEffectViewer());
    }
}

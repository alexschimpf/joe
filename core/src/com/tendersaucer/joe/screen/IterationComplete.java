package com.tendersaucer.joe.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.joe.AssetManager;
import com.tendersaucer.joe.ColorScheme;
import com.tendersaucer.joe.Game;
import com.tendersaucer.joe.ui.InputListener;

/**
 * Created by alexschimpf on 9/16/16.
 */
public final class IterationComplete implements Screen {

    private Game game;
    private final Skin skin;
    private final Stage stage;
    private final FreeTypeFontGenerator fontGenerator;

    public IterationComplete(Game game) {
        this.game = game;

        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
    }

    @Override
    public void show() {
        stage.addListener(new InputListener(false));
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(false);

        createUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void createUI() {
        createReplayButton();
    }

    private void createReplayButton() {
        FreeTypeFontGenerator.FreeTypeFontParameter fontParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParam.size = Gdx.graphics.getWidth() / 8;

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontGenerator.generateFont(fontParam);
        style.fontColor = Color.WHITE;

        final TextButton playButton = new TextButton("REPLAY?", skin);
        playButton.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        playButton.setPosition(0, 0);
        playButton.setStyle(style);
        playButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new Driver(game));
            }
        });

        stage.addActor(playButton);
    }
}

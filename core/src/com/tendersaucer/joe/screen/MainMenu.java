package com.tendersaucer.joe.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.joe.AssetManager;
import com.tendersaucer.joe.DAO;
import com.tendersaucer.joe.InputListener;

/**
 * Created by Alex on 8/3/2016.
 */
public class MainMenu implements Screen {

    private Game game;
    private FreeTypeFontGenerator fontGenerator;
    private Skin skin;
    private Stage stage;
    private Image loadingBackground;
    private Label loadingText;
    private Label byLine;
    private TextButton audioButton;

    public MainMenu(Game game) {
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

        AssetManager.getInstance().load();
        createUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
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
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float playButtonHeight = screenWidth / 10f;
        FreeTypeFontGenerator.FreeTypeFontParameter playParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        playParameter.size = (int)playButtonHeight * 2;
        playParameter.spaceX = (int)screenWidth / 100;
        TextButton.TextButtonStyle playButtonStyle = new TextButton.TextButtonStyle();
        playButtonStyle.font = fontGenerator.generateFont(playParameter);
        playButtonStyle.fontColor = Color.BLACK;
        playButtonStyle.downFontColor = Color.WHITE;

        final TextButton playButton = new TextButton("PLAY", skin);
        playButton.setSize(screenWidth, playButtonHeight);
        playButton.setPosition(0, (screenHeight - playButton.getPrefHeight()) * 0.4f);
        playButton.setStyle(playButtonStyle);
        playButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                loadingBackground.setVisible(true);
                loadingText.setVisible(true);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new Driver(game));
            }
        });
        stage.addActor(playButton);

        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = Gdx.graphics.getWidth() / 30;
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = fontGenerator.generateFont(fontParameter);
        style.fontColor = Color.BLACK;

        byLine = new Label("by.alex.schimpf", skin);
        byLine.setStyle(style);
        float margin = screenHeight / 50;
        byLine.setPosition(screenWidth - byLine.getPrefWidth() - margin, margin);
        stage.addActor(byLine);

        audioButton = new TextButton("audio", skin);
        TextButton.TextButtonStyle audioButtonStyle = new TextButton.TextButtonStyle(playButtonStyle);
        audioButtonStyle.font = fontGenerator.generateFont(fontParameter);
        audioButtonStyle.fontColor = DAO.getInstance().getBoolean(DAO.IS_AUDIO_ENABLED, true) ? Color.GREEN : Color.RED;
        audioButton.setStyle(audioButtonStyle);
        audioButton.setPosition(screenWidth - audioButton.getPrefWidth() - margin,
                screenHeight - audioButton.getPrefHeight() - margin);
        audioButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean wasEnabled = DAO.getInstance().getBoolean(DAO.IS_AUDIO_ENABLED, true);
                DAO.getInstance().putBoolean(DAO.IS_AUDIO_ENABLED, !wasEnabled );
                boolean isEnabled = DAO.getInstance().getBoolean(DAO.IS_AUDIO_ENABLED, true);
                audioButton.getStyle().fontColor = isEnabled ? Color.GREEN : Color.RED;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            }
        });
        stage.addActor(audioButton);

        loadingBackground = new Image(AssetManager.getInstance().getTextureRegion("default"));
        loadingBackground.setPosition(0, 0);
        loadingBackground.setSize(screenWidth, screenHeight);
        loadingBackground.setColor(1, 1, 1, 1);
        loadingBackground.setVisible(false);
        stage.addActor(loadingBackground);

        loadingText = new Label("loading", skin);
        loadingText.setStyle(style);
        loadingText.setVisible(false);
        loadingText.setPosition((screenWidth - loadingText.getPrefWidth()) / 2,
                (screenHeight - byLine.getPrefHeight()) / 2);
        stage.addActor(loadingText);
    }
}

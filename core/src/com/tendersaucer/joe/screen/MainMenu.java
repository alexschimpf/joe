package com.tendersaucer.joe.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.joe.AssetManager;
import com.tendersaucer.joe.DAO;
import com.tendersaucer.joe.InputListener;

/**
 * Created by Alex on 8/3/2016.
 */
public class MainMenu implements Screen {

    private static final Color offColor = new Color(0, 0.7f, 0, 1);
    private static final Color onColor = new Color(0.7f, 0, 0, 1);

    private Game game;
    private FreeTypeFontGenerator fontGenerator;
    private Skin skin;
    private Stage stage;
    private Label loadingLabel;

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
        float titleY = createTitleLabel();
        createPlayButton(titleY);
        createAudioButton(titleY);
        createLoadingLabel();
    }

    private float createTitleLabel() {
        int height = (int)(Gdx.graphics.getWidth() * 0.15f);
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = height;

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = fontGenerator.generateFont(fontParam);
        style.fontColor = Color.BLACK;

        Label title = new Label("joe", skin);
        title.setStyle(style);
        title.setSize(Gdx.graphics.getWidth(), height);
        title.setPosition(0, (Gdx.graphics.getHeight() - height) / 2);
        stage.addActor(title);

        return title.getY();
    }

    private void createPlayButton(float titleY) {
        int height = (int)(Gdx.graphics.getWidth() * 0.05f);
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = height;

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontGenerator.generateFont(fontParam);
        style.fontColor = Color.BLACK;
        style.downFontColor = Color.WHITE;

        float padding = Gdx.graphics.getHeight() * 0.01f;
        final TextButton playButton = new TextButton("PLAY", skin);
        playButton.setSize(Gdx.graphics.getWidth(), height);
        playButton.setPosition(0, titleY - height - padding);
        playButton.setStyle(style);
        playButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                loadingLabel.setVisible(true);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new Driver(game));
            }
        });

        stage.addActor(playButton);
    }

    private void createAudioButton(float titleY) {
        int height = (int)(Gdx.graphics.getWidth() * 0.05f);
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = height;

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontGenerator.generateFont(fontParam);
        style.downFontColor = Color.WHITE;
        style.fontColor = DAO.getInstance().getBoolean(DAO.IS_AUDIO_ENABLED, true) ? onColor : offColor;

        float padding = Gdx.graphics.getHeight() * 0.01f;
        final TextButton audioButton = new TextButton("audio", skin);
        audioButton.setStyle(style);
        audioButton.setPosition(Gdx.graphics.getWidth() - audioButton.getPrefWidth() - padding, titleY + padding);
        audioButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean wasEnabled = DAO.getInstance().getBoolean(DAO.IS_AUDIO_ENABLED, true);
                DAO.getInstance().putBoolean(DAO.IS_AUDIO_ENABLED, !wasEnabled );
                boolean isEnabled = DAO.getInstance().getBoolean(DAO.IS_AUDIO_ENABLED, true);
                audioButton.getStyle().fontColor = isEnabled ? Color.GREEN : Color.RED;
                return true;
            }
        });

        stage.addActor(audioButton);
    }

    private void createLoadingLabel() {
        int height = (int)(Gdx.graphics.getWidth() * 0.15f);
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = height;

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = fontGenerator.generateFont(fontParam);
        style.fontColor = Color.BLACK;
        loadingLabel = new Label("LOADING", skin);
        loadingLabel.setStyle(style);
        loadingLabel.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        loadingLabel.setAlignment(Align.center);
        loadingLabel.setPosition(0, 0);
        loadingLabel.setVisible(false);
        stage.addActor(loadingLabel);
    }
}

package com.tendersaucer.joe.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.joe.AssetManager;
import com.tendersaucer.joe.ColorScheme;
import com.tendersaucer.joe.InputListener;

/**
 * Created by Alex on 8/3/2016.
 */
public class MainMenu implements Screen {

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
        ColorScheme.getInstance().reset();

        createUI();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
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
        Image background = new Image();
        background.setDrawable(new TextureRegionDrawable(AssetManager.getInstance().getTextureRegion("main_menu_background")));
        background.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        background.setPosition(0, 0);
        stage.addActor(background);

        createPlayButton();
        createAudioButton();
        createLoadingLabel();
    }

    private void createPlayButton() {
        float correction = (int)(Gdx.graphics.getWidth() * 0.1f);
        int height = (int)(Gdx.graphics.getWidth() * 0.25f);
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = height;

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = fontGenerator.generateFont(fontParam);
        style.fontColor = ColorScheme.getInstance().getBackgroundColor();

        final TextButton playButton = new TextButton("JOE", skin);
        playButton.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight() + correction);
        playButton.setPosition(0, 0);
        playButton.setStyle(style);
        playButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                stage.clear();
                stage.addActor(loadingLabel);
                loadingLabel.setVisible(true);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new Driver(game));
            }
        });

        stage.addActor(playButton);

        height = (int)(Gdx.graphics.getWidth() * 0.05f);
        fontParam = new FreeTypeFontParameter();
        fontParam.size = height;

        Label.LabelStyle style2 = new Label.LabelStyle();
        style2.font = fontGenerator.generateFont(fontParam);
        style2.fontColor = ColorScheme.getInstance().getBackgroundColor();

        final Label footer = new Label("(touch anywhere to begin)", skin);
        footer.setAlignment(Align.center);
        footer.setSize(Gdx.graphics.getWidth(), height);
        footer.setPosition(0, (Gdx.graphics.getHeight() / 2) - (height * 3));
        footer.setStyle(style2);
        footer.setTouchable(Touchable.disabled);

        stage.addActor(footer);
    }

    private void createLoadingLabel() {
        int height = (int)(Gdx.graphics.getWidth() * 0.1f);
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = height;
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = fontGenerator.generateFont(fontParam);
        style.fontColor = ColorScheme.getInstance().getBackgroundColor();
        loadingLabel = new Label("LOADING", skin);
        loadingLabel.setStyle(style);
        loadingLabel.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        loadingLabel.setAlignment(Align.center);
        loadingLabel.setPosition(0, 0);
        loadingLabel.setVisible(false);
        stage.addActor(loadingLabel);
    }

    private void createAudioButton() {
//        int height = (int)(Gdx.graphics.getWidth() * 0.05f);
//        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
//        fontParam.size = height;
//
//        Label.LabelStyle style = new Label.LabelStyle();
//        style.font = fontGenerator.generateFont(fontParam);
//        style.fontColor = DAO.getInstance().getBoolean(DAO.IS_AUDIO_ENABLED, true) ? Color.RED : Color.GREEN;
//        final Label audioButton = new Label("AUDIO?", skin);
//        audioButton.setStyle(style);
//        audioButton.setSize(Gdx.graphics.getWidth() * 0.95f, height);
//        audioButton.setPosition(0, Gdx.graphics.getHeight() - height - (Gdx.graphics.getWidth() * 0.025f));
//        audioButton.setAlignment(Align.right);
//        audioButton.addListener(new ClickListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                boolean wasEnabled = DAO.getInstance().getBoolean(DAO.IS_AUDIO_ENABLED, true);
//                DAO.getInstance().putBoolean(DAO.IS_AUDIO_ENABLED, !wasEnabled );
//                boolean isEnabled = DAO.getInstance().getBoolean(DAO.IS_AUDIO_ENABLED, true);
//                audioButton.getStyle().fontColor = isEnabled ? Color.RED : Color.GREEN;
//                return true;
//            }
//        });
//
//        stage.addActor(audioButton);
    }
}
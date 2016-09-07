package com.tendersaucer.joe.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.joe.*;
import com.tendersaucer.joe.particle.ParticleEffectManager;
import com.tendersaucer.joe.util.ConversionUtils;
import com.tendersaucer.joe.util.pool.Vector2Pool;

/**
 * Created by Alex on 4/30/2016.
 */
public class ParticleEffectViewer implements Screen {

    private final Skin skin;
    private final InputListener inputListener;
    private final SpriteBatch spriteBatch;
    private Stage stage;
    private BitmapFont font;

    public ParticleEffectViewer() {
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        spriteBatch = new SpriteBatch();
        inputListener = new InputListener();
    }

    @Override
    public void show() {
        AssetManager.getInstance().load();
        ParticleEffectManager.getInstance().loadDefinitions();
        loadFont();

        setStage();
        createDropdown();
    }

    @Override
    public void render(float delta) {
        update();
        render();
    }

    @Override
    public void resize(int width, int height) {
        MainCamera.getInstance().resizeViewport(width, height);
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

    private void update() {
        MainCamera.getInstance().update();
        ParticleEffectManager.getInstance().update();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
    }

    private void render() {
        if (Globals.PRINT_DEBUG_INFO) {
            Gdx.app.debug("debug", "FPS: " + Gdx.graphics.getFramesPerSecond());
            Gdx.app.debug("debug", "V2Pool #Free: " + Vector2Pool.getInstance().getFree());
            Gdx.app.debug("debug", "V3Pool #Free: " + Vector2Pool.getInstance().getFree());
            Gdx.app.debug("debug", "Heap size (MB): " + Gdx.app.getJavaHeap() / 1000000.0f);
        }

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.graphics.setTitle("ParticleEffectViewer (x" + inputListener.getSizeScale() + ")");

        OrthographicCamera camera = MainCamera.getInstance().getRawCamera();
        spriteBatch.setProjectionMatrix(camera.combined);

        spriteBatch.begin();
        {
            com.tendersaucer.joe.Canvas.getInstance().render(spriteBatch);
        }
        spriteBatch.end();

        stage.draw();
    }

    private void setStage() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        stage.addListener(inputListener);
        Gdx.input.setInputProcessor(stage);
    }

    private void createDropdown() {
        final float screenWidth = Gdx.graphics.getWidth();
        final float screenHeight = Gdx.graphics.getHeight();

        final SelectBox<String> dropdown = new SelectBox<String>(skin);
        dropdown.setSize(screenWidth, screenHeight / 10);
        dropdown.setPosition(0, screenHeight - dropdown.getHeight());

        dropdown.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                inputListener.setSelectedEffectType(dropdown.getSelected());
            }
        });

        // Load dropdown.
        Array<String> effectTypes = ParticleEffectManager.getInstance().getParticleEffectTypes();
        dropdown.setItems(effectTypes);
        dropdown.getStyle().font = font;

        stage.addActor(dropdown);
    }

    private void loadFont() {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal("font/font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter param =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        param.size = Gdx.graphics.getHeight() / 16;
        param.color = Color.WHITE;
        font = generator.generateFont(param);
        generator.dispose();
    }

    private static final class InputListener extends com.badlogic.gdx.scenes.scene2d.InputListener {

        private float sizeScale = 1;
        private String selectedEffectType;

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            if (selectedEffectType != null) {
                if (y > .8 * Gdx.graphics.getHeight()) {
                    return true;
                }

                Vector2 position = ConversionUtils.toWorldCoords(x, y);
                MainCamera camera = MainCamera.getInstance();
                float minSize = (camera.getViewportWidth() / 30) * sizeScale;
                float maxSize = (camera.getViewportHeight() / 20) * sizeScale;
                Vector2 sizeRange = Vector2Pool.getInstance().obtain(minSize, maxSize);
                ParticleEffectManager.getInstance().beginParticleEffect(selectedEffectType,
                        position, sizeRange, 5);
                Vector2Pool.getInstance().free(sizeRange);
            }

            return true;
        }

        @Override
        public boolean keyDown(InputEvent event, int keyCode) {
            switch (keyCode) {
                case Keys.ESCAPE:
                    Gdx.app.exit();
                    break;
                case Keys.Z:
                    sizeScale = Math.max(0.1f, sizeScale - 0.1f);
                    break;
                case Keys.X:
                    sizeScale += 0.1f;
                    break;
                case Keys.C:
                    ParticleEffectManager.getInstance().clearLiveEffects();
                    break;
                case Keys.D:
                    Globals.PRINT_DEBUG_INFO = !Globals.PRINT_DEBUG_INFO;
                    break;
            }

            return true;
        }

        public float getSizeScale() {
            return sizeScale;
        }

        public void setSelectedEffectType(String selectedEffectType) {
            this.selectedEffectType = selectedEffectType;
        }
    }
}

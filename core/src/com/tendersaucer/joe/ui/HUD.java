package com.tendersaucer.joe.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.joe.AssetManager;
import com.tendersaucer.joe.DAO;
import com.tendersaucer.joe.Game;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.IRender;
import com.tendersaucer.joe.IUpdate;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.event.listeners.IGameStateChangeListener;
import com.tendersaucer.joe.event.listeners.INewUserListener;
import com.tendersaucer.joe.level.Level;
import com.tendersaucer.joe.level.entity.Player;

/**
 * Game heads up display
 *
 * /                \
 * / /          \ \   \
 * |                  |
 * /                  /
 * |      ___\ \| | / /
 * |      /          \
 * |      |           \
 * /       |      _    |
 * |       |       \   |
 * |       |       _\ /|    DO NOT LOOK AT THIS CLASS!
 * |      __\     <_o)\o-   THE IMPLEMENTATION IS __UNFORGIVABLE__.
 * |     |             \    IT IS CURRENTLY ONE HUGE HACK.
 * \    ||             \    MAYBE ONE DAY I'LL BE BRAVE ENOUGH TO REFACTOR.
 * |   |__          _  \    /
 * |   |           (*___)  /
 * |   |       _     |    /
 * |   |    //_______/
 * |  /       | UUUUU__
 * \|        \_nnnnnn_\-\
 * |       ____________/
 * |      /
 * |_____/
 *
 * http://www.chris.com/ascii/index.php?art=cartoons/beavis%20and%20Butt-head
 *
 * Created by Alex on 4/8/2016.
 */
public final class HUD implements IUpdate, IRender, IGameStateChangeListener, INewUserListener {

    private static final HUD INSTANCE = new HUD();
    public static final String WAITING_FOR_INPUT_MESSAGE = "- WAITING FOR INPUT -";
    private Color MOBILE_BUTTON_DOWN_COLOR = new Color(0.45f, 0.45f, 0.45f, 0.5f);
    private Color MOBILE_BUTTON_UP_COLOR = new Color(0.65f, 0.65f, 0.65f, 0.5f);
    private static final String[] TUTORIAL_MESSAGES = new String[] {
            "Hi, I'm Joe.",
            "Now follow my commands!!!",
            "To make me move left/right, slide your finger here.",
            "To make me jump, touch here.",
            "If you hold longer, I'll jump higher.",
            "Your mission?",
            "GUIDE ME TO THE GROOVY SPINNING THINGIES!"
    };

    // Tutorial
    private int tutorialPosition;
    private Label tutorialLabel;
    private Image tutorialNextButton;

    // Level Complete
    private Image levelCompleteBackground;
    private Label levelSummaryLabel;
    private TextButton nextButton;

    // Mobile
    private Button moveButton;
    private Button jumpButton;
    private Integer movePointer;

    // Main
    private Image infoBackground;
    private Label infoLabel;
    private Label progressLabel;
    private Image flashImage;
    private final Stage stage;
    private final InputListener inputListener;
    private final FreeTypeFontGenerator fontGenerator;
    private final Skin skin;

    private HUD() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        stage.addListener(inputListener = new InputListener(true));
        Gdx.input.setInputProcessor(stage);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        createFlashImage();
        createProgressLabel();
        createInfoLabel();
        createLevelCompleteUI();
        hideLevelComplete();

        if (Globals.isMobile()) {
            createMobileButtons();
        }
    }

    public static HUD getInstance() {
        return INSTANCE;
    }

    @Override
    public void render(SpriteBatch spriteBatch) {
        stage.draw();
    }

    @Override
    public boolean update() {
        if (Globals.isDesktop()) {
            inputListener.update();
        } else {
            checkMobileButtons();
        }

        Level level = Level.getInstance();
        boolean isTutorial = DAO.getInstance().getBoolean(DAO.IS_NEW_KEY, true);
        if (!isTutorial) {
            long iterationId = level.getIterationId();
            int levelId = level.getId();
            long runId = DAO.getInstance().getLong(DAO.RUN_ID_KEY, 0);
            progressLabel.setText(iterationId + "." + levelId + "." + runId);
        }

        if (isTutorial) {
            infoLabel.setText("TUTORIAL");
        } else {
            infoLabel.setText(WAITING_FOR_INPUT_MESSAGE);
            if (Globals.getGameState() != Game.State.WAIT_FOR_INPUT && infoLabel.getActions().size == 0) {
                infoLabel.setVisible(false);
                infoBackground.setVisible(false);
            }
        }

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float margin = screenHeight / 50;
        float labelHeight = progressLabel.getPrefHeight();
        progressLabel.setPosition(margin, screenHeight - (labelHeight / 2) - margin);

        float labelWidth = infoLabel.getPrefWidth();
        labelHeight = infoLabel.getPrefHeight();
        infoBackground.setSize(Gdx.graphics.getWidth(), infoLabel.getPrefHeight() + (margin * 2));
        infoLabel.setSize(Gdx.graphics.getWidth(), labelHeight);
        infoLabel.setPosition((screenWidth - labelWidth) / 2, screenHeight - labelHeight - margin);
        infoBackground.setPosition(0, infoLabel.getY() - margin);

        levelSummaryLabel.setPosition(margin, margin + (labelHeight / 2));

        if (tutorialLabel != null) {
            labelWidth = tutorialLabel.getWidth();
            tutorialLabel.setPosition((screenWidth - labelWidth) / 2, 0.6f * screenHeight);

            if (Globals.isMobile()) {
                tutorialLabel.setText(TUTORIAL_MESSAGES[tutorialPosition]);
            } else {
                tutorialLabel.setText(TUTORIAL_MESSAGES[tutorialPosition]);
            }
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        // Hack to keep infoLabel from flashing during tutorial.
        if (isTutorial) {
            infoLabel.getStyle().fontColor.a = 1;
            infoLabel.getColor().a = 1;
        }

        return false;
    }

    @Override
    public void onGameStateChange(Game.State oldEvent, Game.State newEvent) {
        if (newEvent == Game.State.LEVEL_COMPLETE) {
            Gdx.app.debug("HUD", "Showing level completion dialog...");

            // TODO: Hacky way to ensure statistics have been updated first.
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    showLevelComplete();
                }
            });
        }

        if (oldEvent == Game.State.RUNNING && newEvent == Game.State.WAIT_FOR_INPUT) {
            doFlash();
        }

        if (oldEvent == Game.State.WAIT_FOR_INPUT) {
            infoLabel.clearActions();
            infoBackground.clearActions();
            infoLabel.getColor().a = 1;
            infoBackground.getColor().a = 1;
            infoLabel.setVisible(true);
            infoBackground.setVisible(true);
            infoLabel.setText(WAITING_FOR_INPUT_MESSAGE);

            boolean isTutorial = DAO.getInstance().getBoolean(DAO.IS_NEW_KEY, true);
            if (!isTutorial) {
                infoLabel.addAction(Actions.alpha(0, 0.3f));
                infoBackground.addAction(Actions.alpha(0, 0.3f));
            }
        }

        if (newEvent == Game.State.WAIT_FOR_INPUT) {
            nextButton.getStyle().fontColor = Color.WHITE;
            nextButton.getStyle().downFontColor = new Color(0.8f, 0.8f, 0.8f, 1);
            infoLabel.clearActions();
            infoBackground.clearActions();
            infoLabel.getColor().a = 0;
            infoBackground.getColor().a = 0;
            infoLabel.setVisible(true);
            infoBackground.setVisible(true);
            infoLabel.setText(WAITING_FOR_INPUT_MESSAGE);
            infoLabel.addAction(Actions.forever(Actions.sequence(Actions.alpha(0, 0.25f), Actions.alpha(1, 0.25f))));
            infoBackground.addAction(Actions.alpha(1, 0.6f));
        } else if (newEvent == Game.State.ITERATION_COMPLETE) {
            //Game.instance.setScreen(new IterationComplete(Game.instance));
        }
    }

    @Override
    public void onNewUser() {
        tutorialPosition = 0;
        createTutorialUI();

        tutorialLabel.setText(TUTORIAL_MESSAGES[0]);
        tutorialLabel.setVisible(true);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    public void addActor(Actor actor) {
        stage.addActor(actor);
    }

    public Skin getSkin() {
        return skin;
    }

    public void doFlash() {
        flashImage.clearActions();
        flashImage.getColor().a = 0;
        flashImage.setVisible(true);
        flashImage.addAction(Actions.sequence(
                Actions.alpha(1, 0.4f), Actions.alpha(0, 0.4f), Actions.visible(false)));
    }

    private void createFlashImage() {
        TextureRegion tr = AssetManager.getInstance().getTextureRegion("default");
        flashImage = new Image(tr);
        flashImage.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        flashImage.setPosition(0, 0);
        flashImage.setVisible(false);
        flashImage.setColor(new Color(0, 0, 0, 0));

        stage.addActor(flashImage);
    }

    private void createProgressLabel() {
        progressLabel = new Label("", skin);

        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = Gdx.graphics.getWidth() / 30;
        LabelStyle style = new LabelStyle();
        style.font = fontGenerator.generateFont(fontParameter);
        style.fontColor = Color.WHITE;
        progressLabel.setStyle(style);

        stage.addActor(progressLabel);
    }

    private void createInfoLabel() {
        infoBackground = new Image();
        TextureRegionDrawable image =
                new TextureRegionDrawable(AssetManager.getInstance().getTextureRegion("default"));
        infoBackground.setDrawable(image.tint(new Color(0, 0, 0, 0.6f)));
        infoBackground.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getWidth() / 20);
        stage.addActor(infoBackground);

        infoLabel = new Label("", skin);
        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = Gdx.graphics.getWidth() / 30;
        LabelStyle style = new LabelStyle();
        style.font = fontGenerator.generateFont(fontParameter);
        style.fontColor = Color.WHITE;
        infoLabel.setStyle(style);
        infoLabel.setWidth(Gdx.graphics.getWidth());
        stage.addActor(infoLabel);
    }

    /**
     * ********************************************************************************************
     * Level Complete
     * ********************************************************************************************
     */

    private void createLevelCompleteUI() {
        if (Level.getInstance().getPlayer() != null) {
            Level.getInstance().getPlayer().setDone();
        }

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        levelCompleteBackground = new Image(AssetManager.getInstance().getTextureRegion("default"));
        levelCompleteBackground.setPosition(0, 0);
        levelCompleteBackground.setSize(screenWidth, screenHeight);
        levelCompleteBackground.setColor(0, 0, 0, 0.5f);
        stage.addActor(levelCompleteBackground);

        int nextButtonHeight = (int)(Gdx.graphics.getWidth() * 0.2f);
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = nextButtonHeight;
        TextButton.TextButtonStyle nextButtonStyle = new TextButton.TextButtonStyle();
        nextButtonStyle.font = fontGenerator.generateFont(fontParam);
        nextButtonStyle.fontColor = Color.WHITE;
        nextButtonStyle.downFontColor = new Color(0.8f, 0.8f, 0.8f, 1);
        nextButton = new TextButton("\nNEXT", skin);
        nextButton.setSize(screenWidth, nextButtonHeight);
        nextButton.setPosition(0, screenHeight / 2);
        nextButton.setStyle(nextButtonStyle);
        nextButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return nextButton.isVisible();
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (!nextButton.isVisible()) {
                    return;
                }

                hideLevelComplete();
//                if (Level.getInstance().getId() == Globals.NUM_LEVELS - 1) {
//                    Globals.setGameState(Game.State.ITERATION_COMPLETE);
//                } else {
                    Level.getInstance().loadNext();
//                }
            }
        });
        stage.addActor(nextButton);

        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = Gdx.graphics.getWidth() / 30;
        LabelStyle style = new LabelStyle();
        style.font = fontGenerator.generateFont(fontParameter);
        style.fontColor = Color.WHITE;
        levelSummaryLabel = new Label("", skin);
        levelSummaryLabel.setStyle(style);
        stage.addActor(levelSummaryLabel);
    }

    private String getTimeTakenDisplay(long durationMs) {
        long durationSeconds = durationMs / 1000;
        String unitDisplay = null;
        if (durationSeconds < 60) {
            unitDisplay = durationSeconds + " SECONDS";
        } else if (durationSeconds >= 60) {
            long minutes = durationSeconds / 60;
            long seconds = durationSeconds - (minutes * 60);
            String minutesDisplay = minutes > 1 ? "MINUTES" : "MINUTE";
            String secondsDisplay = seconds > 1 ? "SECONDS" : "SECOND";
            unitDisplay = minutes + " " + minutesDisplay;
            if (seconds > 0) {
                unitDisplay += " " + seconds + " " + secondsDisplay;
            }
        }

        return unitDisplay;
    }

    public void hideLevelComplete() {
        levelCompleteBackground.setVisible(false);
        nextButton.setVisible(false);
        levelSummaryLabel.setText("");
        levelSummaryLabel.setVisible(false);

        if (!Globals.isDesktop() && moveButton != null && jumpButton != null) {
            moveButton.setDisabled(false);
            moveButton.setVisible(true);
            jumpButton.setDisabled(false);
            jumpButton.setVisible(true);
        }
    }

    private void showLevelComplete() {
        levelCompleteBackground.setVisible(true);
        nextButton.getColor().a = 0;
        nextButton.addAction(Actions.alpha(1, 1.5f));
        nextButton.setVisible(true);

        long duration = DAO.getInstance().getLong(DAO.TOTAL_TIME_KEY, 0);
        levelSummaryLabel.setText("TIME TAKEN: " + getTimeTakenDisplay(duration));
        levelSummaryLabel.setVisible(true);

        if (!Globals.isDesktop() && moveButton != null && jumpButton != null) {
            moveButton.setDisabled(true);
            moveButton.setVisible(false);
            jumpButton.setDisabled(true);
            jumpButton.setVisible(false);
        }
    }

    /**
     * ********************************************************************************************
     * Tutorial
     * ********************************************************************************************
     */

    private void createTutorialUI() {
        final int screenWidth = Gdx.graphics.getWidth();
        final int screenHeight = Gdx.graphics.getHeight();

        tutorialLabel = new Label("", skin);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = screenWidth / 40;
        parameter.size = screenWidth / 40;
        BitmapFont font = fontGenerator.generateFont(parameter);
        LabelStyle labelStyle = new LabelStyle(font, Color.WHITE);
        tutorialLabel.setStyle(labelStyle);
        tutorialLabel.setAlignment(Align.center);
        tutorialLabel.setWrap(true);
        tutorialLabel.setSize(screenWidth / 2, screenHeight / 5);
        tutorialLabel.setVisible(false);
        stage.addActor(tutorialLabel);

        float size = Gdx.graphics.getWidth() / 20;
        TextureRegion tr = AssetManager.getInstance().getTextureRegion("arrow");
        tr.flip(false, true);
        tutorialNextButton = new Image(tr);
        tutorialNextButton.setSize(size * 2, size * 2);
        tutorialNextButton.setOrigin(Align.center);
        tutorialNextButton.setPosition(screenWidth - (size * 2.1f), screenHeight - (size * 1.55f));
        tutorialNextButton.setVisible(true);
        tutorialNextButton.setRotation(90);
        tutorialNextButton.setColor(Color.WHITE);
        tutorialNextButton.addAction(Actions.forever(
                Actions.sequence(
                    Actions.moveBy(-size / 2, 0, 0.5f),
                    Actions.moveBy(size / 2, 0, 0.5f)
                )
        ));
        tutorialNextButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                doFlash();

                int lastPos = TUTORIAL_MESSAGES.length - 1;
                if (tutorialPosition == lastPos) {
                    tutorialLabel.setVisible(false);
                    tutorialNextButton.remove();
                    DAO.getInstance().putBoolean(DAO.IS_NEW_KEY, false);
                    Level.getInstance().getScript("tutorial_end_script").setActive(true);
                } else {
                    tutorialPosition++;
                    if (Globals.isMobile()) {
                        if (tutorialPosition == 2) {
                            moveButton.addAction(Actions.color(Color.RED, 1.5f));
                        } else if (tutorialPosition == 3) {
                            moveButton.addAction(Actions.color(Color.WHITE, 1.5f));
                            jumpButton.addAction(Actions.color(Color.RED, 1.5f));
                        } else if (tutorialPosition == 5) {
                            jumpButton.addAction(Actions.color(Color.WHITE, 1.5f));
                        }
                    }
                }

                return true;
            }
        });
        stage.addActor(tutorialNextButton);
    }

    /**
     * ********************************************************************************************
     * Mobile
     * ********************************************************************************************
     */

    private void createMobileButtons() {
        createMobileMoveButton();
        createMobileJumpButton();
    }

    private void createMobileMoveButton() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        moveButton = new Button(skin);
        moveButton.getStyle().down = new TextureRegionDrawable(AssetManager.getInstance().getTextureRegion("default")).tint(MOBILE_BUTTON_DOWN_COLOR);
        moveButton.getStyle().up = new TextureRegionDrawable(AssetManager.getInstance().getTextureRegion("default")).tint(MOBILE_BUTTON_UP_COLOR);
        moveButton.setSize(screenWidth * 0.35f, screenHeight * 0.2f);
        moveButton.setPosition(screenWidth / 32, screenHeight / 32f);
        moveButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean isTutorial = DAO.getInstance().getBoolean(DAO.IS_NEW_KEY, true);
                if (Globals.getGameState() == Game.State.WAIT_FOR_INPUT) {
                    Globals.setGameState(Game.State.RUNNING);
                }

                if (Globals.getGameState() == Game.State.RUNNING) {
                    movePointer = pointer;
                }
                return true;
            }
        });

        stage.addActor(moveButton);
    }

    private void createMobileJumpButton() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        jumpButton = new Button(skin);
        jumpButton.getStyle().down = new TextureRegionDrawable(AssetManager.getInstance().getTextureRegion("default")).tint(MOBILE_BUTTON_DOWN_COLOR);
        jumpButton.getStyle().up = new TextureRegionDrawable(AssetManager.getInstance().getTextureRegion("default")).tint(MOBILE_BUTTON_UP_COLOR);
        jumpButton.setSize(screenWidth / 5f, screenHeight / 5f);
        float buttonWidth = jumpButton.getWidth();
        jumpButton.setPosition(((31.0f / 32.0f) * screenWidth) - buttonWidth, screenHeight / 32f);
        jumpButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (Globals.getGameState() == Game.State.WAIT_FOR_INPUT) {
                    Globals.setGameState(Game.State.RUNNING);
                }

                if (Globals.getGameState() == Game.State.RUNNING) {
                    Level.getInstance().getPlayer().jump();
                }

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Globals.getGameState() == Game.State.RUNNING) {
                    Level.getInstance().getPlayer().stopJump();
                }
            }
        });

        stage.addActor(jumpButton);
    }

    private void checkMobileButtons() {
        if (movePointer == null || Globals.getGameState() != Game.State.RUNNING) {
            return;
        }

        float moveCenterX = moveButton.getX() + (moveButton.getWidth() / 2);
        float x = Gdx.input.getX(movePointer);
        Player player = Level.getInstance().getPlayer();
        if (moveButton.isPressed()) {
            MainCamera camera = MainCamera.getInstance();
            if (x < moveCenterX) {
                if (camera.isFlipped()) {
                    player.moveRight();
                } else {
                    player.moveLeft();
                }
            } else {
                if (camera.isFlipped()) {
                    player.moveLeft();
                } else {
                    player.moveRight();
                }
            }
        } else {
            player.stopHorizontalMove();
        }
    }
}

package com.tendersaucer.joe.screen;

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
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.joe.AssetManager;
import com.tendersaucer.joe.ColorScheme;
import com.tendersaucer.joe.DAO;
import com.tendersaucer.joe.GameState;
import com.tendersaucer.joe.Globals;
import com.tendersaucer.joe.IUpdate;
import com.tendersaucer.joe.InputListener;
import com.tendersaucer.joe.MainCamera;
import com.tendersaucer.joe.entity.Player;
import com.tendersaucer.joe.event.IGameStateChangeListener;
import com.tendersaucer.joe.event.INewUserEventListener;
import com.tendersaucer.joe.level.Level;

/**
 * Game heads up display
 *
 *
 /                \
 / /          \ \   \
 |                  |
 /                  /
 |      ___\ \| | / /
 |      /          \
 |      |           \
 /       |      _    |
 |       |       \   |
 |       |       _\ /|    DO NOT LOOK AT THIS CLASS!
 |      __\     <_o)\o-   THE IMPLEMENTATION IS __UNFORGIVABLE__.
 |     |             \    IT IS CURRENTLY ONE HUGE HACK.
 \    ||             \    MAYBE ONE DAY I'LL BE BRAVE ENOUGH TO REFACTOR.
 |   |__          _  \    /
 |   |           (*___)  /
 |   |       _     |    /
 |   |    //_______/
 |  /       | UUUUU__
 \|        \_nnnnnn_\-\
 |       ____________/
 |      /
 |_____/
 *
 * http://www.chris.com/ascii/index.php?art=cartoons/beavis%20and%20Butt-head
 *
 * Created by Alex on 4/8/2016.
 */
public final class HUD implements IUpdate, IRender, IGameStateChangeListener, INewUserEventListener {

    private static final HUD instance = new HUD();
    private Color MOBILE_BUTTON_DOWN_COLOR = new Color(0.45f, 0.45f, 0.45f, 0.5f);
    private Color MOBILE_BUTTON_UP_COLOR = new Color(0.65f, 0.65f, 0.65f, 0.5f);
    private static final String[] MOBILE_TUTORIAL_MESSAGES = new String[] {
        "Hi, I'm Joe.",
        "Now... mindlessly follow my commands.",
        "To make me move left/right, slide your finger here.",
        "To make me jump, touch here.",
        "If you hold longer, I'll jump higher.",
        "Your mission?",
        "GUIDE ME TO THE RED SPINNING THINGIES!"
    };
    private static final String[] DESKTOP_TUTORIAL_MESSAGES = new String[] {
        "Hi, I'm Joe.",
        "Now... mindlessly follow my commands.",
        "To make me move left/right, use the arrow keys.",
        "To make me jump, press A.",
        "If you hold longer, I'll jump higher.",
        "Your mission?",
        "GUIDE ME TO THE RED SPINNING THINGIES!"
    };

    private int tutorialPosition;
    private boolean isTutorialNextButtonPressed;
    private Label tutorialLabel;
    private Image tutorialHelperArrow;
    private Image tutorialNextButton;
    private Timer tutorialFlashTimer;
    private Stage stage;
    private InputListener inputListener;
    private Image levelCompleteBackground;
    private Label levelSummaryLabel;
    private TextButton nextButton;
    private Label progressLabel;
    private Image infoBackground;
    private Label infoLabel;
    private Button moveButton;
    private Button jumpButton;
    private Integer movePointer;
    private FreeTypeFontGenerator fontGenerator;
    private Image flashImage;
    private Long flashStartTime;
    private Skin skin;

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

        if (DAO.getInstance().getBoolean(DAO.IS_NEW_KEY, true)) {
            tutorialPosition = 0;
            createTutorialUI();
        }

        isTutorialNextButtonPressed = false;
    }

    public static HUD getInstance() {
        return instance;
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

        if (flashImage.isVisible() && flashStartTime != null) {
            updateFlash();
        }

        Level level = Level.getInstance();
        boolean isTutorial = DAO.getInstance().getBoolean(DAO.IS_NEW_KEY, true);
        if (!isTutorial) {
            long iterationId = level.getIterationId();
            int levelId = level.getId();
            long runId = DAO.getInstance().getLong(DAO.RUN_ID_KEY, 0);
            progressLabel.setText(iterationId + "." + levelId + "." + runId);
        }

        infoLabel.setColor(ColorScheme.getInstance().getSecondaryColor(ColorScheme.ReturnType.SHARED));
        if (isTutorial) {
            infoLabel.setText("TUTORIAL");
        } else {
            infoLabel.setText("WAITING FOR INPUT");
            if (Globals.getGameState() != GameState.WAIT_FOR_INPUT && infoLabel.getActions().size == 0) {
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
                tutorialLabel.setText(MOBILE_TUTORIAL_MESSAGES[tutorialPosition]);
            } else {
                tutorialLabel.setText(DESKTOP_TUTORIAL_MESSAGES[tutorialPosition]);
            }
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));

        return false;
    }

    @Override
    public void onGameStateChange(GameState oldEvent, GameState newEvent) {
        if (newEvent == GameState.LEVEL_COMPLETE) {
            Gdx.app.debug("HUD", "Showing level completion dialog...");

            // TODO: Hacky way to ensure statistics have been updated first.
            Gdx.app.postRunnable(new Runnable() {
                @Override
                public void run() {
                    showLevelComplete();
                }
            });
        }

        if (oldEvent == GameState.RUNNING && newEvent == GameState.WAIT_FOR_INPUT) {
            setFlash(true);
        }

        if (oldEvent == GameState.WAIT_FOR_INPUT) {
            infoLabel.clearActions();
            infoBackground.clearActions();
            infoLabel.getColor().a = 1;
            infoBackground.getColor().a = 1;
            infoLabel.setVisible(true);
            infoBackground.setVisible(true);
            infoLabel.setText("WAITING FOR INPUT");
            infoLabel.addAction(Actions.alpha(0, 0.6f));
            infoBackground.addAction(Actions.alpha(0, 0.6f));
        }

        if (newEvent == GameState.WAIT_FOR_INPUT) {
            nextButton.getStyle().fontColor = Color.WHITE;
            nextButton.getStyle().downFontColor = new Color(0.8f, 0.8f, 0.8f, 1);
            infoLabel.clearActions();
            infoBackground.clearActions();
            infoLabel.getColor().a = 0;
            infoBackground.getColor().a = 0;
            infoLabel.setVisible(true);
            infoBackground.setVisible(true);
            infoLabel.setText("WAITING FOR INPUT");
            infoLabel.addAction(Actions.alpha(1, 0.6f));
            infoBackground.addAction(Actions.alpha(1, 0.6f));
        }
    }

    @Override
    public void onNewUser() {
        if (Globals.isMobile()) {
            tutorialLabel.setText(MOBILE_TUTORIAL_MESSAGES[0]);
        } else {
            tutorialLabel.setText(DESKTOP_TUTORIAL_MESSAGES[0]);
        }

        tutorialLabel.setVisible(true);
        stage.removeListener(inputListener);
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

    public void setFlash(boolean flash) {
        if (flashImage.isVisible() != flash) {
            flashImage.setVisible(flash);
            flashStartTime = flash ? TimeUtils.millis() : null;
        }
    }

    private void updateFlash() {
        Color c = flashImage.getColor();
        float age = TimeUtils.timeSinceMillis(flashStartTime);
        if (age < 200) {
            c.a = age / 200;
            flashImage.setColor(c);
        } else if (age < 400){
            age -= 200;
            c.a = 1 - (age / 200);
            flashImage.setColor(c);
        } else {
            setFlash(false);
            flashStartTime = null;
        }
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
        TextureRegionDrawable image = new TextureRegionDrawable(AssetManager.getInstance().getTextureRegion("default"));
        infoBackground.setDrawable(image.tint(new Color(0, 0, 0, 0.3f)));
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
        if (Level.getInstance().getPlayer() != null){
            Level.getInstance().getPlayer().setDone();
        }

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        levelCompleteBackground = new Image(AssetManager.getInstance().getTextureRegion("default"));
        levelCompleteBackground.setPosition(0, 0);
        levelCompleteBackground.setSize(screenWidth, screenHeight);
        levelCompleteBackground.setColor(0, 0, 0, 0.3f);
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
                Level.getInstance().loadNext();
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

    private void hideLevelComplete() {
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
        nextButton.addAction(Actions.alpha(1, 2f));
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

        final float padding = screenHeight * 0.01f;
        float size = screenWidth * 0.1f;
        TextureRegion tr = AssetManager.getInstance().getTextureRegion("arrow");
        tr.flip(false, true);
        tutorialHelperArrow = new Image(tr);
        tutorialHelperArrow.setSize(size, size);
        tutorialHelperArrow.setOrigin(Align.center);
        tutorialHelperArrow.setVisible(false);
        tutorialHelperArrow.setColor(Color.WHITE);
        tutorialNextButton = new Image(tr);
        tutorialNextButton.setSize(size * 2, size * 2);
        tutorialNextButton.setOrigin(Align.center);
        tutorialNextButton.setPosition(screenWidth - (size * 2.1f), screenHeight - (size * 1.55f));
        tutorialNextButton.setVisible(true);
        tutorialNextButton.setRotation(90);
        tutorialNextButton.setColor(Color.WHITE);
        tutorialNextButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (tutorialLabel.isVisible()) {
                    isTutorialNextButtonPressed = true;
                    Color color = tutorialNextButton.getColor();
                    tutorialNextButton.setColor(color.r, color.g, color.b, 1);
                    tutorialNextButton.setColor(new Color(0.8f, 0.8f, 0.8f, 1));
                    return true;
                }

                return false;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                setFlash(true);

                isTutorialNextButtonPressed = false;
                int lastPos = Globals.isMobile() ? MOBILE_TUTORIAL_MESSAGES.length - 1 :
                        DESKTOP_TUTORIAL_MESSAGES.length - 1;
                tutorialNextButton.setColor(Color.WHITE);
                if (tutorialPosition == lastPos) {
                    tutorialLabel.setVisible(false);
                    tutorialHelperArrow.setVisible(false);
                    tutorialNextButton.setVisible(false);
                    stage.addListener(inputListener);
                    DAO.getInstance().putBoolean(DAO.IS_NEW_KEY, false);
                    tutorialFlashTimer.clear();
                } else {
                    tutorialPosition++;
                    if (Globals.isMobile()) {
                        float size = tutorialHelperArrow.getWidth();
                        if (tutorialPosition == 2) {
                            float moveButtonCenterX = moveButton.getX() + (moveButton.getWidth() / 2);
                            tutorialHelperArrow.setPosition(moveButtonCenterX - (size / 2), moveButton.getTop() + padding);
                            tutorialHelperArrow.setVisible(true);
                        } else if (tutorialPosition == 3) {
                            float jumpButtonCenterX = jumpButton.getX() + (jumpButton.getWidth() / 2);
                            tutorialHelperArrow.setPosition(jumpButtonCenterX - (size / 2), jumpButton.getTop() + padding);
                        } else if (tutorialPosition == 5) {
                            tutorialHelperArrow.setVisible(false);
                        }
                    }
                }
            }
        });
        stage.addActor(tutorialNextButton);
        stage.addActor(tutorialHelperArrow);

        tutorialFlashTimer = new Timer();
        tutorialFlashTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                Color curr = tutorialNextButton.getColor();
                tutorialNextButton.setColor(curr.r, curr.g, curr.b,
                        curr.a == 0 || isTutorialNextButtonPressed ? 1 : 0);
            }
        }, 0, 0.25f);
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
                if (!isTutorial && Globals.getGameState() == GameState.WAIT_FOR_INPUT) {
                    Globals.setGameState(GameState.RUNNING);
                }

                if(Globals.getGameState() == GameState.RUNNING) {
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
                boolean isTutorial = DAO.getInstance().getBoolean(DAO.IS_NEW_KEY, true);
                if (!isTutorial && Globals.getGameState() == GameState.WAIT_FOR_INPUT) {
                    Globals.setGameState(GameState.RUNNING);
                }

                if(Globals.getGameState() == GameState.RUNNING) {
                    Level.getInstance().getPlayer().jump();
                }

                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if(Globals.getGameState() == GameState.RUNNING) {
                    Level.getInstance().getPlayer().stopJump();
                }
            }
        });

        stage.addActor(jumpButton);
    }

    private void checkMobileButtons() {
        if(movePointer == null || Globals.getGameState() != GameState.RUNNING) {
            return;
        }

        float moveCenterX = moveButton.getX() + (moveButton.getWidth() / 2);
        float x = Gdx.input.getX(movePointer);
        Player player = Level.getInstance().getPlayer();
        boolean isTutorial = DAO.getInstance().getBoolean(DAO.IS_NEW_KEY, true);
        if(moveButton.isPressed() && !isTutorial) {
            MainCamera camera = MainCamera.getInstance();
            if(x < moveCenterX) {
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

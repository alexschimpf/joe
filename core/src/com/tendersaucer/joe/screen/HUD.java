package com.tendersaucer.joe.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.tendersaucer.joe.AssetManager;
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

import java.util.concurrent.TimeUnit;

/**
 * Game heads up display
 * TODO: MAKE THIS NOT SO HACKY!!!
 * Created by Alex on 4/8/2016.
 */
public final class HUD implements IUpdate, IRender, IGameStateChangeListener, INewUserEventListener {

    private static final HUD instance = new HUD();
    private static final float BUTTON_ALPHA = 0.5f;
    private static final String[] MOBILE_TUTORIAL_MESSAGES = new String[] {
        "Hi, I'm Joe.",
        "Now... mindlessly follow my commands.",
        "To make me move left/right, slide your finger here...",
        "To make me jump, touch here...",
        "If you hold longer, I'll jump higher...",
        "IMPORTANT: There will be NO instructions! Figure it out yourself!!!",
        "Anyway, just get me to the end of each level."
    };
    private static final String[] DESKTOP_TUTORIAL_MESSAGES = new String[] {
            "Hi, I'm Joe.",
            "Now... mindlessly follow my commands.",
            "To make me move left/right, use the arrow keys...",
            "To make me jump, press A...",
            "If you hold longer, I'll jump higher...",
            "IMPORTANT: There will be NO instructions! Figure it out yourself!!!",
            "... Just get me to the end of each level."
    };

    private int tutorialPosition;
    private Label tutorialLabel;
    private Image tutorialNextButton;
    private Stage stage;
    private InputListener inputListener;
    private Image levelCompleteBackground;
    private Label levelSummaryLabel;
    private TextButton nextButton;
    private Label progressLabel;
    private Button moveButton;
    private Button jumpButton;
    private Integer movePointer;
    private Image tutorialArrow;
    private FreeTypeFontGenerator fontGenerator;
    private Skin skin;

    private HUD() {
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        inputListener = new InputListener(true);
        stage.addListener(inputListener);
        Gdx.input.setInputProcessor(stage);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        createProgressLabel();
        createLevelCompleteUI();
        hideLevelComplete();

        if (Globals.isMobile()) {
            createMobileButtons();
        }

        tutorialPosition = 0;
        createTutorialUI();
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

        Level level = Level.getInstance();
        long iterationId = level.getIterationId();
        int levelId = level.getId();
        long runId = DAO.getInstance().getLong(DAO.RUN_ID_KEY, 0);
        progressLabel.setText(iterationId + "." + levelId + "." + runId);

        float labelHeight = progressLabel.getPrefHeight();
        float screenHeight = Gdx.graphics.getHeight();
        float margin = screenHeight / 50;
        progressLabel.setPosition(margin, screenHeight - (labelHeight / 2) - margin);
        levelSummaryLabel.setPosition(margin, margin + (labelHeight / 2));

        float labelWidth = tutorialLabel.getWidth();
        int screenWidth = Gdx.graphics.getWidth();
        tutorialLabel.setPosition((screenWidth - labelWidth) / 2, 0.6f * screenHeight);

        if (Globals.isMobile()) {
            tutorialLabel.setText(MOBILE_TUTORIAL_MESSAGES[tutorialPosition]);
        } else {
            tutorialLabel.setText(DESKTOP_TUTORIAL_MESSAGES[tutorialPosition]);
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
    }

    @Override
    public void onNewUser() {
        if (Globals.isMobile()) {
            tutorialLabel.setText(MOBILE_TUTORIAL_MESSAGES[0]);
        } else {
            tutorialLabel.setText(DESKTOP_TUTORIAL_MESSAGES[0]);
        }

        tutorialLabel.setVisible(true);
        tutorialNextButton.setVisible(true);
        stage.removeListener(inputListener);
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height);
    }

    private void hideLevelComplete() {
        levelCompleteBackground.setVisible(false);
        nextButton.setDisabled(true);
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
        nextButton.setDisabled(false);
        nextButton.setVisible(true);

        long duration = DAO.getInstance().getLong(DAO.TOTAL_TIME_KEY, 0);
        levelSummaryLabel.setText(String.format("TIME TAKEN: %s SECONDS",
                TimeUnit.MILLISECONDS.toSeconds(duration)));
        levelSummaryLabel.setVisible(true);

        if (!Globals.isDesktop() && moveButton != null && jumpButton != null) {
            moveButton.setDisabled(true);
            moveButton.setVisible(false);
            jumpButton.setDisabled(true);
            jumpButton.setVisible(false);
        }
    }

    private void createProgressLabel() {
        progressLabel = new Label("", skin);

        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = Gdx.graphics.getWidth() / 30;
        LabelStyle style = new LabelStyle();
        style.font = fontGenerator.generateFont(fontParameter);
        style.fontColor = Color.BLACK;
        progressLabel.setStyle(style);

        stage.addActor(progressLabel);
    }

    private void createLevelCompleteUI() {
        if (Level.getInstance().getPlayer() != null){
            Level.getInstance().getPlayer().setDone();
        }

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        levelCompleteBackground = new Image(AssetManager.getInstance().getTextureRegion("default"));
        levelCompleteBackground.setPosition(0, 0);
        levelCompleteBackground.setSize(screenWidth, screenHeight);
        levelCompleteBackground.setColor(0.7f, 0.7f, 0.7f, 0.8f);
        stage.addActor(levelCompleteBackground);

        float nextButtonHeight = screenWidth / 4;
        FreeTypeFontParameter nextParameter = new FreeTypeFontParameter();
        nextParameter.size = (int)nextButtonHeight;
        nextParameter.borderWidth = nextParameter.size / 50;
        nextParameter.spaceX = (int)screenWidth / 100;
        TextButtonStyle nextButtonStyle = new TextButtonStyle();
        nextButtonStyle.font = fontGenerator.generateFont(nextParameter);
        nextButtonStyle.fontColor = Color.BLACK;
        nextButton = new TextButton("\nNEXT", skin);
        nextButton.setSize(screenWidth, nextButtonHeight);
        nextButton.setPosition(0, screenHeight / 2);
        nextButton.setStyle(nextButtonStyle);
        nextButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                nextButton.getStyle().fontColor = Color.WHITE;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                hideLevelComplete();
                nextButton.getStyle().fontColor = Color.BLACK;
                Level.getInstance().loadNext();
            }
        });
        stage.addActor(nextButton);

        FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
        fontParameter.size = Gdx.graphics.getWidth() / 30;
        LabelStyle style = new LabelStyle();
        style.font = fontGenerator.generateFont(fontParameter);
        style.fontColor = Color.BLACK;
        levelSummaryLabel = new Label("", skin);
        levelSummaryLabel.setStyle(style);
        stage.addActor(levelSummaryLabel);
    }

    private void createMobileButtons() {
        createMobileMoveButton();
        createMobileJumpButton();
    }

    private void createMobileMoveButton() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        moveButton = new Button(skin);
        moveButton.setColor(1, 1, 1, BUTTON_ALPHA);
        moveButton.setSize(screenWidth / 3f, screenHeight / 5f);
        moveButton.setPosition(screenWidth / 32, screenHeight / 32f);
        moveButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean isTutorial = tutorialLabel.isVisible();
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
        jumpButton.setColor(1, 1, 1, BUTTON_ALPHA);
        jumpButton.setSize(screenWidth / 5f, screenHeight / 5f);
        float buttonWidth = jumpButton.getWidth();
        jumpButton.setPosition(((31.0f / 32.0f) * screenWidth) - buttonWidth, screenHeight / 32f);
        jumpButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                boolean isTutorial = tutorialLabel.isVisible();
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

    private void createTutorialUI() {
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        tutorialLabel = new Label("", skin);
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        parameter.size = screenWidth / 50;
        BitmapFont font = fontGenerator.generateFont(parameter);
        LabelStyle labelStyle = new LabelStyle(font, Color.BLACK);
        tutorialLabel.setStyle(labelStyle);
        tutorialLabel.setAlignment(Align.center);
        tutorialLabel.setWrap(true);
        tutorialLabel.setSize(screenWidth / 2, screenHeight / 5);
        tutorialLabel.setVisible(false);
        stage.addActor(tutorialLabel);

        final float padding = screenHeight * 0.01f;
        float size = screenWidth * 0.08f;
        TextureRegion tr = AssetManager.getInstance().getTextureRegion("arrow");
        tr.flip(false, true);
        tutorialNextButton = new Image(tr);
        tutorialNextButton.setSize(size, size);
        tutorialNextButton.setPosition(screenWidth - tutorialNextButton.getWidth() - padding, screenHeight - tutorialNextButton.getHeight());
        tutorialNextButton.setRotation(90);
        tutorialNextButton.setVisible(false);
        tutorialNextButton.setColor(Color.BLACK);
        tutorialNextButton.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
           @Override
           public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
               if (!tutorialLabel.isVisible()) {
                   return false;
               }

               tutorialNextButton.setColor(Color.LIGHT_GRAY);
               int lastPos = Globals.isMobile() ? MOBILE_TUTORIAL_MESSAGES.length - 1 :
                       DESKTOP_TUTORIAL_MESSAGES.length - 1;
               if (tutorialPosition == lastPos) {
                   tutorialLabel.setVisible(false);
                   tutorialNextButton.setVisible(false);
                   stage.addListener(inputListener);
                   DAO.getInstance().putBoolean(DAO.IS_NEW_KEY, false);
               } else {
                   tutorialPosition++;
                   if (tutorialPosition == 5) {
                       tutorialLabel.getStyle().fontColor = Color.RED;
                   } else {
                       tutorialLabel.getStyle().fontColor = Color.BLACK;
                   }

                   if (Globals.isMobile()) {
                       if (tutorialPosition == 2) {
                           float moveButtonCenterX = moveButton.getX() + (moveButton.getWidth() / 2);
                           tutorialArrow.setPosition((moveButtonCenterX - (tutorialArrow.getWidth()) / 2), moveButton.getTop() + padding);
                           tutorialArrow.setVisible(true);
                       } else if (tutorialPosition == 3) {
                           float jumpButtonCenterX = jumpButton.getX() + (jumpButton.getWidth() / 2);
                           tutorialArrow.setPosition((jumpButtonCenterX - (tutorialArrow.getWidth()) / 2), jumpButton.getTop() + padding);
                       } else if (tutorialPosition == 5) {
                           tutorialArrow.setVisible(false);
                       }
                   }
               }

               return true;
           }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                tutorialNextButton.setColor(Color.BLACK);
            }
        });

        stage.addActor(tutorialNextButton);

        if (Globals.isMobile()) {
            tutorialArrow = new Image(tr);
            tutorialArrow.setSize(size, size);
            tutorialArrow.setVisible(false);
            tutorialArrow.setColor(Color.BLACK);
            stage.addActor(tutorialArrow);
        }
    }

    private void checkMobileButtons() {
        if(movePointer == null || Globals.getGameState() != GameState.RUNNING) {
            return;
        }

        float moveCenterX = moveButton.getX() + (moveButton.getWidth() / 2);
        float x = Gdx.input.getX(movePointer);
        Player player = Level.getInstance().getPlayer();
        boolean isTutorial = tutorialLabel.isVisible();
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

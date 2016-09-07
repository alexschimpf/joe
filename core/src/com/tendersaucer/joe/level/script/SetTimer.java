package com.tendersaucer.joe.level.script;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.tendersaucer.joe.Game;
import com.tendersaucer.joe.event.EventManager;
import com.tendersaucer.joe.event.GameStateChangeEvent;
import com.tendersaucer.joe.event.listeners.IGameStateChangeListener;
import com.tendersaucer.joe.ui.HUD;

/**
 * Created by Alex on 8/16/2016.
 */
public final class SetTimer extends Script implements IGameStateChangeListener {

    private Long lastUpdated;
    private final int duration; // seconds
    private final Label timerLabel;
    private final FreeTypeFontGenerator fontGenerator;

    protected SetTimer(ScriptDefinition definition) {
        super(definition);

        duration = definition.getIntProperty("duration");

        // TODO: Create a global font generator!
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        timerLabel = createTimerLabel();

        setActive(false);
    }

    @Override
    protected void tick() {
        if (lastUpdated == null) {
            lastUpdated = TimeUtils.millis();
            return;
        }

        if (TimeUtils.timeSinceMillis(lastUpdated) >= 1000) {
            lastUpdated = TimeUtils.millis();
            int secondsLeft = Integer.parseInt(timerLabel.getText().toString()) - 1;
            if (secondsLeft <= 0) {
                timerLabel.setText("0");
                //HUD.getInstance().setFlash(true);
            } else {
                timerLabel.setText(String.valueOf(secondsLeft));
            }
        }
    }

    @Override
    public void init() {
        super.init();

        EventManager.getInstance().listen(GameStateChangeEvent.class, this);
    }

    @Override
    public void dispose() {
        super.dispose();

        timerLabel.remove();
        EventManager.getInstance().mute(GameStateChangeEvent.class, this);
    }

    @Override
    public void onGameStateChange(Game.State oldEvent, Game.State newEvent) {
        if (newEvent == Game.State.RUNNING) {
            setActive(true);
        } else if (newEvent == Game.State.LEVEL_COMPLETE) {
            timerLabel.remove();
        }
    }

    private Label createTimerLabel() {
        int height = (int)(Gdx.graphics.getWidth() * 0.05f);
        FreeTypeFontParameter fontParam = new FreeTypeFontParameter();
        fontParam.size = height;

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = fontGenerator.generateFont(fontParam);
        style.fontColor = Color.WHITE;

        final Label timerLabel = new Label(String.valueOf(duration), HUD.getInstance().getSkin());
        timerLabel.setStyle(style);
        timerLabel.setSize(Gdx.graphics.getWidth(), height);
        timerLabel.setPosition(0, Gdx.graphics.getHeight() - height - (Gdx.graphics.getWidth() * 0.025f));
        timerLabel.setAlignment(Align.center);
        timerLabel.setText(String.valueOf(duration));
        HUD.getInstance().addActor(timerLabel);

        return timerLabel;
    }
}

package com.tendersaucer.joe.level.script;

import com.badlogic.gdx.Gdx;
import com.tendersaucer.joe.IDisposable;
import com.tendersaucer.joe.IUpdate;
import com.tendersaucer.joe.level.Level;
import com.tendersaucer.joe.util.StringUtils;

import java.lang.reflect.Constructor;

/**
 * A runnable script
 * <p/>
 * Created by Alex on 4/24/2016.
 */
public abstract class Script implements IUpdate, IDisposable {

    private static final String CLASS_PATH = "com.tendersaucer.joe.level.script.";

    public enum State {
        ACTIVE, INACTIVE, DONE
    }

    protected State state;
    private final float delay; // If -1, the script is not run until set active.
    private float elapsed;
    protected final String type;
    protected final String id;
    protected final ScriptDefinition definition;

    protected Script(ScriptDefinition definition) {
        this.definition = definition;
        this.type = definition.getType();

        id = getOrCreateId();
        state = definition.getBooleanProperty("is_active") ? State.ACTIVE : State.INACTIVE;
        delay = definition.getFloatProperty("delay");
    }

    public static Script build(ScriptDefinition scriptDef) {
        Script script = null;
        try {
            String scriptType = scriptDef.getType();
            String className = CLASS_PATH + ScriptPropertyConfiguration.getInstance().getClassName(scriptType);
            Class<?> c = Class.forName(className);
            Constructor<?> constructor = c.getDeclaredConstructor(ScriptDefinition.class);
            constructor.setAccessible(true);
            script = (Script)constructor.newInstance(scriptDef);
            script.init();
        } catch (Exception e) {
            String scriptInfo = "type=" + scriptDef.getType() + ", id=" + scriptDef.getId();
            Gdx.app.log("script", "Error building script (" + scriptInfo + ")");
            Gdx.app.log("script", e.toString());
        }

        return script;
    }

    public void init() {
    }

    public static Script buildScript(ScriptDefinition scriptDef) {
        Script script = null;
        try {
            String scriptType = scriptDef.getType();
            String className = ScriptPropertyConfiguration.getInstance().getClassName(scriptType);
            Class<?> c = Class.forName(className);
            Constructor<?> constructor = c.getDeclaredConstructor(ScriptDefinition.class);
            constructor.setAccessible(true);
            script = (Script)constructor.newInstance(scriptDef);
        } catch (Exception e) {
            String scriptInfo = "type=" + scriptDef.getType() + ", id=" + scriptDef.getId();
            Gdx.app.log("script", "Error building script (" + scriptInfo + ")");
            Gdx.app.log("script", e.toString());
        }

        return script;
    }

    @Override
    public boolean update() {
        if (isActive()) {
            if (delay >= 0) {
                elapsed += Gdx.graphics.getDeltaTime() * 1000;
                if (elapsed >= delay) {
                    tick();
                }
            } else {
                tick();
            }
        }

        return isDone();
    }

    @Override
    public void dispose() {
    }

    public State getState() {
        return state;
    }

    public boolean isActive() {
        return state == State.ACTIVE;
    }

    public void setActive(boolean active) {
        state = active ? State.ACTIVE : State.INACTIVE;
    }

    public boolean isDone() {
        return state == State.DONE;
    }

    public void setDone() {
        state = State.DONE;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    protected abstract void tick();

    private String getOrCreateId() {
        String id = definition.getId();
        if (StringUtils.isEmpty(id)) {
            id = Level.getInstance().getAvailableScriptId();
        }

        return id;
    }
}

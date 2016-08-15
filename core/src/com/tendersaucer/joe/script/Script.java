package com.tendersaucer.joe.script;

import com.badlogic.gdx.Gdx;
import com.tendersaucer.joe.IDisposable;
import com.tendersaucer.joe.IUpdate;
import com.tendersaucer.joe.level.Level;

import java.lang.reflect.Constructor;

/**
 * A runnable script
 *
 * Created by Alex on 4/24/2016.
 */
public abstract class Script implements IUpdate, IDisposable {

    private static final String CLASS_PATH = "com.tendersaucer.joe.script.";

    public enum State {
        ACTIVE, INACTIVE, DONE
    }

    protected State state;
    protected String type;
    protected String id;
    protected ScriptDefinition definition;

    protected Script(ScriptDefinition definition) {
        this.definition = definition;
        this.type = definition.getType();

        id = getOrCreateId();
        state = State.ACTIVE;
    }

    public static Script build(ScriptDefinition scriptDef) {
        Script script = null;
        try {
            String scriptType = scriptDef.getType();
            String className = CLASS_PATH + ScriptConfiguration.getInstance().getClassName(scriptType);
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
            String className = ScriptConfiguration.getInstance().getClassName(scriptType);
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
        tick();
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
        if (id == null || id.equals("")) {
            id = Level.getInstance().getAvailableId();
        }

        return id;
    }
}

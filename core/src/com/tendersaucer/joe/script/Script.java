package com.tendersaucer.joe.script;

import com.badlogic.gdx.Gdx;
import com.tendersaucer.joe.IDisposable;
import com.tendersaucer.joe.IUpdate;

import java.lang.reflect.Constructor;

/**
 * A runnable script
 * <p/>
 * Created by Alex on 4/24/2016.
 */
public abstract class Script implements IUpdate, IDisposable {

    protected boolean isDone;

    protected Script(ScriptDefinition def) {
        isDone = false;
    }

    public void init() {
    }

    public static Script buildScript(ScriptDefinition scriptDef) {
        Script script = null;
        try {
            String scriptType = scriptDef.getType();
            String className = ScriptConfig.getInstance().getClassName(scriptType);
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
        return isDone;
    }

    @Override
    public void dispose() {
    }

    public void setDone() {
        isDone = true;
    }

    protected abstract void tick();
}

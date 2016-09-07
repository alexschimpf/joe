package com.tendersaucer.joe.event;

import com.tendersaucer.joe.level.script.Script;

/**
 * Created by Alex on 5/5/2016.
 */
public final class ScriptDoneEvent extends Event<com.tendersaucer.joe.event.listeners.IScriptDoneListener> {

    private Script script;

    public ScriptDoneEvent(Script script) {
        this.script = script;
    }

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.IScriptDoneListener listener) {
        listener.onScriptDone(script);
    }
}

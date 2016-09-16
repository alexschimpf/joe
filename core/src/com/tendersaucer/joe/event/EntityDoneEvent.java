package com.tendersaucer.joe.event;

import com.tendersaucer.joe.event.listeners.IEntityDoneListener;
import com.tendersaucer.joe.level.entity.Entity;

/**
 * Created by Alex on 5/5/2016.
 */
public final class EntityDoneEvent extends Event<IEntityDoneListener> {

    private final Entity entity;

    public EntityDoneEvent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void notify(IEntityDoneListener listener) {
        listener.onEntityDone(entity);
    }
}

package com.tendersaucer.joe.event;

import com.tendersaucer.joe.level.entity.Entity;

/**
 * Created by Alex on 5/5/2016.
 */
public final class EntityDoneEvent extends Event<com.tendersaucer.joe.event.listeners.IEntityDoneListener> {

    private Entity entity;

    public EntityDoneEvent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void notify(com.tendersaucer.joe.event.listeners.IEntityDoneListener listener) {
        listener.onEntityDone(entity);
    }
}

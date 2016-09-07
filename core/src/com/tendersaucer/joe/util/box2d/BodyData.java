package com.tendersaucer.joe.util.box2d;

import com.tendersaucer.joe.level.entity.Entity;

/**
 * Object metadata to be attached to a body
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class BodyData {

    public final Entity entity;

    public BodyData(Entity entity) {
        this.entity = entity;
    }
}

package com.tendersaucer.joe.level.entity;

import com.tendersaucer.joe.util.PropertyConfiguration;

/**
 * Created by Alex on 5/9/2016.
 */
public final class EntityPropertyConfiguration extends PropertyConfiguration {

    private static final EntityPropertyConfiguration instance = new EntityPropertyConfiguration();

    private EntityPropertyConfiguration() {
        super("entity.json", "entities", "entity");
    }

    public static EntityPropertyConfiguration getInstance() {
        return instance;
    }
}

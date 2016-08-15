package com.tendersaucer.joe.entity;

import com.tendersaucer.joe.util.Configuration;

/**
 * Created by Alex on 5/9/2016.
 */
public final class EntityConfiguration extends Configuration {

    private static final EntityConfiguration instance = new EntityConfiguration();

    private EntityConfiguration() {
        super("entity.json", "entities", "entity");
    }

    public static EntityConfiguration getInstance() {
        return instance;
    }
}

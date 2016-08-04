package com.tendersaucer.joe.entity;

import com.badlogic.gdx.maps.MapProperties;
import com.tendersaucer.joe.entity.EntityConfig.EntityProperties;
import com.tendersaucer.joe.util.InvalidConfigException;

import java.util.Iterator;

/**
 * Validates entity properties from loadable against config
 * <p/>
 * Created by Alex on 4/8/2016.
 */
public final class TiledEntityPropertyValidator {

    private TiledEntityPropertyValidator() {
    }

    public static void validateAndProcess(String type, MapProperties properties) {
        EntityProperties entityProperties = EntityConfig.getInstance().getEntityProperties(type);
        if (entityProperties == null) {
            throw new InvalidConfigException(type + ": EntityProperties not set for type");
        }

        // Check that all required properties are set.
        for (String requiredProperty : entityProperties.getRequiredProperties()) {
            if (!properties.containsKey(requiredProperty)) {
                throw new InvalidConfigException(type + ": Required entity property '" + requiredProperty + "' missing");
            }
        }

        // Check that all properties are valid.
        Iterator<String> propertiesIter = properties.getKeys();
        while (propertiesIter.hasNext()) {
            String property = propertiesIter.next();
            if (property.equals("gid")) {
                continue;
            }

            if (!entityProperties.propertyExists(property)) {
                throw new InvalidConfigException(type + ": Entity property '" + property + "' is not valid");
            }
        }

        // Fill in missing optional properties with defaults.
        for (String optionalProperty : entityProperties.getOptionalProperties()) {
            String defaultVal = entityProperties.getPropertyDefaultVal(optionalProperty);
            if (!properties.containsKey(optionalProperty)) {
                properties.put(optionalProperty, defaultVal);
            }
        }
    }
}
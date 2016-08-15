package com.tendersaucer.joe.util;

import com.badlogic.gdx.maps.MapProperties;

import java.util.Iterator;

/**
 * Created by Alex on 8/14/2016.
 */
public abstract class PropertyValidator {

    protected PropertyValidator() {
    }

    public static void validateAndProcess(Configuration configuration, String type,
                                          MapProperties mapProperties) {
        Configuration.Properties properties = configuration.getProperties(type);
        if (properties == null) {
            throw new InvalidConfigException(type + ": Properties not set for type");
        }

        // Check that all required properties are set.
        for (String requiredProperty : properties.getRequiredProperties()) {
            if (!mapProperties.containsKey(requiredProperty)) {
                throw new InvalidConfigException(type + ": Required property '" + requiredProperty + "' missing");
            }
        }

        // Check that all properties are valid.
        Iterator<String> propertiesIter = mapProperties.getKeys();
        while (propertiesIter.hasNext()) {
            String property = propertiesIter.next();
            if (property.equals("gid")) {
                continue;
            }

            if (!properties.propertyExists(property)) {
                throw new InvalidConfigException(type + ": Property '" + property + "' is not valid");
            }
        }

        // Fill in missing optional properties with defaults.
        for (String optionalProperty : properties.getOptionalProperties()) {
            String defaultVal = properties.getPropertyDefaultVal(optionalProperty);
            if (!mapProperties.containsKey(optionalProperty)) {
                mapProperties.put(optionalProperty, defaultVal);
            }
        }
    }
}

package com.tendersaucer.joe.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alex on 8/14/2016.
 */
public abstract class PropertyConfiguration {

    private final String filename;
    private final String root1;
    private final String root2;
    private final Map<String, Properties> typePropertiesMap;
    private final Map<String, String> typeClassMap;

    protected PropertyConfiguration(String filename, String root1, String root2) {
        this.filename = filename;
        this.root1 = root1;
        this.root2 = root2;

        typePropertiesMap = new ConcurrentHashMap<String, Properties>();
        typeClassMap = new ConcurrentHashMap<String, String>();

        JsonReader jsonReader = new JsonReader();
        parseConfiguration(jsonReader.parse(Gdx.files.internal(filename)));
    }

    public String getClassName(String type) {
        return typeClassMap.get(type);
    }

    public Properties getProperties(String type) {
        return typePropertiesMap.get(type);
    }

    private void parseConfiguration(JsonValue root) {
        parseConfiguration(null, root.get(root1).get(root2));
    }

    private void parseConfiguration(String parentType, JsonValue root) {
        String type = root.name;
        if (type.equals("")) {
            throw new com.tendersaucer.joe.util.exception.InvalidConfigException(filename, "type", "null");
        }

        Properties parentProperties = null;
        if (parentType != null) {
            parentProperties = typePropertiesMap.get(parentType);
        }

        Properties properties = new Properties(parentProperties);
        addCustomProperties(properties, root);
        typePropertiesMap.put(type, properties);

        if (root.has("children")) {
            for (JsonValue child : root.get("children")) {
                parseConfiguration(type, child);
            }
        }
    }

    // Add custom properties set per object (which may override global properties).
    private void addCustomProperties(Properties properties, JsonValue root) {
        String type = root.name;
        String className = root.getString("class");
        if (className.equals("")) {
            throw new com.tendersaucer.joe.util.exception.InvalidConfigException(filename, "class", "null");
        }

        typeClassMap.put(type, className);

        // Object type may not even have custom properties.
        if (!root.hasChild("properties")) {
            return;
        }

        // Required
        if (root.get("properties").hasChild("required")) {
            for (JsonValue requiredProperty : root.get("properties").get("required")) {
                // If it was an optional global property but is now required, remove it as optional.
                String name = requiredProperty.asString();
                if (properties.propertyExists(name) && !properties.isPropertyRequired(name)) {
                    properties.removeOptionalProperty(name);
                }

                properties.addRequiredProperty(name);
            }
        }

        // Optional
        if (root.get("properties").hasChild("optional")) {
            for (JsonValue optionalProperty : root.get("properties").get("optional")) {
                // If it was a required global property but is now optional, remove it as required.
                String name = optionalProperty.getString("name");
                if (properties.propertyExists(name) && properties.isPropertyRequired(name)) {
                    properties.removeRequiredProperty(name);
                }

                properties.addOptionalProperty(name, optionalProperty.getString("default"));
            }
        }
    }

    public final class Properties {

        private final Map<String, Boolean> requiredProperties;
        private final Map<String, String> propertyDefaultValMap;

        public Properties(Properties other) {
            requiredProperties = new HashMap<String, Boolean>();
            propertyDefaultValMap = new HashMap<String, String>();

            if (other != null) {
                for (String name : other.getRequiredProperties()) {
                    addRequiredProperty(name);
                }
                for (String name : other.getOptionalProperties()) {
                    addOptionalProperty(name, other.getPropertyDefaultVal(name));
                }
            }
        }

        public boolean propertyExists(String name) {
            return requiredProperties.containsKey(name) || propertyDefaultValMap.containsKey(name);
        }

        public boolean isPropertyRequired(String name) {
            if (requiredProperties.containsKey(name)) {
                return true;
            } else if (propertyDefaultValMap.containsKey(name)) {
                return false;
            } else {
                throw new com.tendersaucer.joe.util.exception.InvalidConfigException(filename, "name", name);
            }
        }

        public Set<String> getRequiredProperties() {
            return requiredProperties.keySet();
        }

        public Set<String> getOptionalProperties() {
            return propertyDefaultValMap.keySet();
        }

        public void addRequiredProperty(String name) {
            requiredProperties.put(name, true);
        }

        public void addOptionalProperty(String name, String defaultVal) {
            propertyDefaultValMap.put(name, defaultVal);
        }

        public void removeRequiredProperty(String name) {
            requiredProperties.remove(name);
        }

        public void removeOptionalProperty(String name) {
            propertyDefaultValMap.remove(name);
        }

        public String getPropertyDefaultVal(String name) {
            return propertyDefaultValMap.get(name);
        }
    }
}

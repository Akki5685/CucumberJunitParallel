package com.example.utils;

import java.util.Properties;

public class PropertyManager {

    private static Properties cucumberProperties;

    static {
        // Load cucumber.properties once
        cucumberProperties = new Properties();
        try {
            cucumberProperties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("cucumber.properties"));
        } catch (Exception e) {
            // Initialize with empty properties if file not found
            cucumberProperties = new Properties();
        }
    }

    /**
     * Gets a property value from system properties, environment variables, or cucumber.properties
     */
    public static String getProperty(String key, String defaultValue) {
        // First check system properties
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }

        // Then check environment variables (converted to uppercase with dots replaced by underscores)
        String envKey = key.toUpperCase().replace('.', '_');
        value = System.getenv(envKey);
        if (value != null) {
            return value;
        }

        // Finally check cucumber.properties
        value = cucumberProperties.getProperty(key);
        if (value != null) {
            return value;
        }

        return defaultValue;
    }

    /**
     * Gets a property value without a default
     */
    public static String getProperty(String key) {
        return getProperty(key, null);
    }
}
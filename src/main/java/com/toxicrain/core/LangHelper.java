package com.toxicrain.core;

import java.util.Locale;
import java.util.ResourceBundle;

public class LangHelper {
    private ResourceBundle resourceBundle;

    /**
     * @param baseName The name of the Resource Bundle to load
     * @param locale The locale to use.
     */
    public LangHelper(String baseName, Locale locale) {
        loadResourceBundle(baseName, locale);
    }

    /**
     * @param baseName The name of the Resource Bundle to load
     * @param locale The locale to use.
     */
    private void loadResourceBundle(String baseName, Locale locale) {
        try {
            resourceBundle = ResourceBundle.getBundle(baseName, locale);
        } catch (java.util.MissingResourceException e) {
            Logger.printERROR("Resource bundle not found: " + e.getMessage());
            resourceBundle = ResourceBundle.getBundle(baseName); // Fallback to default
        }
    }

    /**
     * Get a message from a key
     *
     * @param key the key to get
     *
     * @return the key translated into the current language
     */
    public String get(String key) {
        return resourceBundle.getString(key);
    }

    /**
     * Change the Locale at runtime
     */
    public void changeLocale(Locale locale) {
        loadResourceBundle(resourceBundle.getBaseBundleName(), locale);
    }
}


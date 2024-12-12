package com.toxicrain.core;

import java.util.Locale;
import java.util.ResourceBundle;

public class LangHelper {
    private ResourceBundle resourceBundle;
    private final String baseName;

    /**
     * Constructs a LangHelper with the specified resource bundle base name and locale.
     *
     * @param baseName The name of the Resource Bundle to load.
     * @param locale   The locale to use.
     */
    public LangHelper(String baseName, Locale locale) {
        this.baseName = baseName;
        loadResourceBundle(baseName, locale);
    }

    /**
     * Loads the Resource Bundle for the given base name and locale.
     *
     * @param baseName The name of the Resource Bundle to load.
     * @param locale   The locale to use.
     */
    private void loadResourceBundle(String baseName, Locale locale) {
        try {
            resourceBundle = ResourceBundle.getBundle(baseName, locale);
        } catch (java.util.MissingResourceException e) {
            resourceBundle = ResourceBundle.getBundle(baseName); // Fallback to default
        }
    }

    /**
     * Retrieves a message for the given key from the resource bundle.
     *
     * @param key The key to retrieve the message for.
     * @return The message corresponding to the key, or a default message if the key is not found.
     */
    public String get(String key) {
        try {
            return resourceBundle.getString(key);
        } catch (java.util.MissingResourceException e) {
            return "Missing key: " + key; // Default message if key is not found
        }
    }

    /**
     * Changes the locale at runtime by reloading the resource bundle.
     *
     * @param locale The new locale to set.
     */
    public void changeLocale(Locale locale) {
        loadResourceBundle(baseName, locale);
    }
}

package com.toxicrain.rainengine.core;

/**
 * The Constants class provides a convenient place to hold constants.
 * This class should not be used for any other purpose.
 *
 * All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 */
public class Constants {
    /**Engine's version*/
    public static final String engineVersion = "1.0.0";
    public static final String engineMakers = "Toxic Rain Studios";
    public static final String credits = "Stavj and notdeadpool456: Making the textures";

    /**Z level for NPC's, allows the player to be draw over them*/
    public static final float NPC_ZLEVEL = 1.01f;

    public static final float PROJECTILE_ZLEVEL = 1.02f;

    public static class FileConstants {
        private static final String BASE_PATH = "resources";

        public static final String PALETTE_DEFAULT_PATH = BASE_PATH + "/json/palette.json";
        public static final String PALETTE_CUSTOM_PATH = BASE_PATH + "/custom/palette.json";

        public static final String SETTINGS_PATH = BASE_PATH + "/json/settings.json";

        public static final String GAMEINFO_PATH = BASE_PATH + "/json/gameinfo.json";

        public static final String MAP_PATH = BASE_PATH + "/json/";

        public static final String IMAGES_PATH = BASE_PATH + "/images";

        public static final String KEYBINDS_PATH = BASE_PATH + "/json/keybinds.json";

    }
}
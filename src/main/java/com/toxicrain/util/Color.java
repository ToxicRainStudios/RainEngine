package com.toxicrain.util;

import static java.util.Arrays.stream;

/**
 * The Color enum provides basic color tools
 *
 * @author strubium
 */
public enum Color {
    ORANGE(1.000f, 0.647f, 0.000f),
    BROWN(0.647f, 0.165f, 0.165f),
    PINK(1.000f, 0.753f, 0.796f),
    GOLD(1.000f, 0.843f, 0.000f),
    LIGHT_GRAY(0.827f, 0.827f, 0.827f),
    DARK_GRAY(0.663f, 0.663f, 0.663f),
    LIGHT_BLUE(0.678f, 0.847f, 0.902f),
    LIGHT_GREEN(0.564f, 0.933f, 0.564f),
    LIGHT_CYAN(0.878f, 1.000f, 1.000f),
    LIGHT_YELLOW(1.000f, 1.000f, 0.878f),
    LIGHT_PINK(1.000f, 0.714f, 0.757f),
    DARK_RED(0.545f, 0.000f, 0.000f),
    DARK_ORANGE(1.000f, 0.549f, 0.000f),
    BLANK(-1f, -1f, -1f),
    BLACK(0.000f, 0.000f, 0.000f),
    DARK_GREEN(0.000f, 0.392f, 0.000f),
    DARK_CYAN(0.000f, 0.545f, 0.545f),
    DARK_BLUE(0.000f, 0.000f, 0.545f),
    WHITE(1.000f, 1.000f, 1.000f),
    RED(1.000f, 0.000f, 0.000f),
    LIME(0.000f, 1.000f, 0.000f),
    BLUE(0.000f, 0.000f, 1.000f),
    YELLOW(1.000f, 1.000f, 0.000f),
    CYAN_AQUA(0.000f, 1.000f, 1.000f),
    MAGENTA(1.000f, 0.000f, 1.000f),
    SILVER(0.753f, 0.753f, 0.753f),
    GRAY(0.502f, 0.502f, 0.502f),
    MAROON(0.502f, 0.000f, 0.000f),
    OLIVE(0.502f, 0.502f, 0.000f),
    GREEN(0.000f, 0.502f, 0.000f),
    PURPLE(0.502f, 0.000f, 0.502f),
    TEAL(0.000f, 0.502f, 0.502f),
    VIOLET(0.933f, 0.510f, 0.933f),
    NAVY(0.000f, 0.000f, 0.502f),

    // Light levels 0-20
    LIGHT_LEVEL_0(0.08f, 0.08f, 0.08f), // Dark
    LIGHT_LEVEL_1(0.1f, 0.1f, 0.1f),    // Very dark grey
    LIGHT_LEVEL_2(0.15f, 0.15f, 0.15f), // Dark grey
    LIGHT_LEVEL_3(0.2f, 0.2f, 0.2f),    // Grey
    LIGHT_LEVEL_4(0.25f, 0.25f, 0.25f), // Light grey
    LIGHT_LEVEL_5(0.3f, 0.3f, 0.3f),    // Slightly lighter grey
    LIGHT_LEVEL_6(0.35f, 0.35f, 0.35f), // Medium grey
    LIGHT_LEVEL_7(0.4f, 0.4f, 0.4f),    // Light grey
    LIGHT_LEVEL_8(0.45f, 0.45f, 0.45f), // Very light grey
    LIGHT_LEVEL_9(0.5f, 0.5f, 0.5f),    // Medium light grey
    LIGHT_LEVEL_10(0.55f, 0.55f, 0.55f),// Light gray
    LIGHT_LEVEL_11(0.6f, 0.6f, 0.6f),   // Slightly lighter gray
    LIGHT_LEVEL_12(0.65f, 0.65f, 0.65f),// Very light gray
    LIGHT_LEVEL_13(0.7f, 0.7f, 0.7f),   // Almost white
    LIGHT_LEVEL_14(0.75f, 0.75f, 0.75f),// Near white
    LIGHT_LEVEL_15(0.8f, 0.8f, 0.8f),   // Lightest gray
    LIGHT_LEVEL_16(0.85f, 0.85f, 0.85f),// Very light gray
    LIGHT_LEVEL_17(0.9f, 0.9f, 0.9f),   // Almost white
    LIGHT_LEVEL_18(0.95f, 0.95f, 0.95f),// Very near white
    LIGHT_LEVEL_19(0.99f, 0.99f, 0.99f),   // Basically White
    LIGHT_LEVEL_20(1.0f, 1.0f, 1.0f);   // White

    private final float red;
    private final float green;
    private final float blue;

    public static Color from(String colorName) {
        return stream(Color.values())
                .filter(color -> colorName.toLowerCase().contains(color.name().toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Make a new color
     */
    Color(float red, float green, float blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    /**
     * Averages two colors together
     *
     * @param color1 the first color
     * @param color2 the second color
     * @return a float array representing the averaged color
     */
    public static float[] average(Color color1, Color color2) {
        float avgRed = (color1.red + color2.red) / 2.0f;
        float avgGreen = (color1.green + color2.green) / 2.0f;
        float avgBlue = (color1.blue + color2.blue) / 2.0f;

        return new float[]{avgRed, avgGreen, avgBlue};
    }


    public static Color findByRGB(float red, float green, float blue) {
        for (Color color : Color.values()) {
            if (color.red == red && color.green == green && color.blue == blue) {
                return color;
            }
        }
        return null;
    }


    public static float[] toFloatArray(float alpha, Color color) {
        return new float[]{color.red, color.green, color.blue, alpha};
    }
    public static float[] toFloatArray(Color color) {
        return new float[]{color.red, color.green, color.blue, 1.0f};
    }
}


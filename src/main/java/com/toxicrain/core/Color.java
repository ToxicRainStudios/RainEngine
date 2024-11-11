package com.toxicrain.core;

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
    NAVY(0.000f, 0.000f, 0.502f);

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


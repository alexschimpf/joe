package com.tendersaucer.joe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.tendersaucer.joe.util.ColorUtils;
import com.tendersaucer.joe.util.RandomUtils;

/**
 * Created by Alex on 8/21/2016.
 */
public final class ColorScheme {

    private static final ColorScheme INSTANCE = new ColorScheme();
    private static final float MIN_SHADE_BRIGHTNESS = 0.98f;
    private static final float MAX_SHADE_BRIGHTNESS = 1.02f;

    private static final Color BLUE = Color.valueOf("69B4FFFF");
    private static final Color GREEN = Color.valueOf("B4FF69FF");
    private static final Color ORANGE = Color.valueOf("FFB469FF");
    private static final Color TEAL = Color.valueOf("69FFB4FF");
    private static final Color PURPLE = Color.valueOf("B469FFFF");
    private static final Color PINK = Color.valueOf("FF69B4FF");
    private static final Color[][] COLOR_SCHEMES = new Color[][] {
            new Color[] { BLUE, ORANGE },
            new Color[] { BLUE, PINK },
            new Color[] { GREEN, PURPLE },
            new Color[] { GREEN, ORANGE },
            new Color[] { GREEN, PINK },
            new Color[] { TEAL, PURPLE },
            new Color[] { TEAL, ORANGE },
            new Color[] { TEAL, PINK },
    };

    public enum ColorType {
        PRIMARY, SECONDARY
    }

    public enum ReturnType {
        SHARED, NEW
    }

    private Color primaryColor;
    private Color secondaryColor;
    private Color backgroundColor;

    private ColorScheme() {
    }

    public static ColorScheme getInstance() {
        return INSTANCE;
    }

    public void reset() {
        RandomUtils.shuffle(COLOR_SCHEMES);
        Color[] scheme = RandomUtils.pickFrom(COLOR_SCHEMES);
        RandomUtils.shuffle(scheme);
        primaryColor = new Color(scheme[0]);
        secondaryColor = new Color(scheme[1]);
        backgroundColor = new Color(0.1f, 0.1f, 0.1f, 1);

        // TODO: This is just for convenience. Remove later.
        ColorUtils.shade(primaryColor, 1);
        ColorUtils.shade(secondaryColor, 1);
        ColorUtils.shade(backgroundColor, 1);
    }

    public Color[] getColors() {
        return new Color[] { BLUE, ORANGE, GREEN, PURPLE, TEAL, PINK };
    }

    public Color getPrimaryColor(ReturnType returnType) {
        if (returnType == ReturnType.SHARED) {
            return primaryColor;
        }

        return new Color(primaryColor);
    }

    public Color getSecondaryColor(ReturnType returnType) {
        if (returnType == ReturnType.SHARED) {
            return secondaryColor;
        }

        return new Color(secondaryColor);
    }

    public Color getShadedPrimaryColor() {
        Color color = getPrimaryColor(ReturnType.NEW);
        ColorUtils.shade(color, MathUtils.random(MIN_SHADE_BRIGHTNESS, MAX_SHADE_BRIGHTNESS));

        return color;
    }

    public Color getShadedSecondaryColor() {
        Color color = getSecondaryColor(ReturnType.NEW);
        ColorUtils.shade(color, MathUtils.random(MIN_SHADE_BRIGHTNESS, MAX_SHADE_BRIGHTNESS));

        return color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }
}

package com.tendersaucer.joe;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.tendersaucer.joe.util.ColorUtils;
import com.tendersaucer.joe.util.RandomUtils;

/**
 * Created by Alex on 8/21/2016.
 */
public final class ColorScheme {

    private static final ColorScheme instance = new ColorScheme();
    private static final float MIN_SHADE_BRIGHTNESS = 0.98f;
    private static final float MAX_SHADE_BRIGHTNESS = 1.02f;

    public enum ColorType {
        PRIMARY, SECONDARY
    }

    public enum ReturnType {
        SHARED, NEW
    }

    private Color[] colorBank = new Color[] {
        Color.valueOf("69B4FFFF"), // blue
        Color.valueOf("B4FF69FF"), // green
        Color.valueOf("FFB469FF"), // orange
        Color.valueOf("69FFB4FF"), // teal
        Color.valueOf("B469FFFF"), // purple
        Color.valueOf("FFFF69FF"), // yellow
    };
    private Color primaryColor;
    private Color secondaryColor;
    private Color backgroundColor;

    private ColorScheme() {
    }

    public static ColorScheme getInstance() {
        return instance;
    }

    public void reset() {
        RandomUtils.shuffle(colorBank);
        primaryColor = new Color(colorBank[0]);
        secondaryColor = new Color(colorBank[1]);
        backgroundColor = new Color(0.2f, 0.2f, 0.2f, 1);

        // TODO: This is just for convenience. Remove later.
        ColorUtils.shade(primaryColor, 1);
        ColorUtils.shade(secondaryColor, 1);
        ColorUtils.shade(backgroundColor, 1);
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

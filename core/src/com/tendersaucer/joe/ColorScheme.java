package com.tendersaucer.joe;

import com.badlogic.gdx.graphics.Color;
import com.tendersaucer.joe.util.ColorUtils;
import com.tendersaucer.joe.util.RandomUtils;

/**
 * Created by Alex on 8/21/2016.
 */
public final class ColorScheme {

    private static final ColorScheme instance = new ColorScheme();
    private static final float MIN_SHADE_BRIGHTNESS = 0.98f;
    private static final float MAX_SHADE_BRIGHTNESS = 1.02f;
    private static final float BRIGHTNESS_CORRECTION = 1.1f;

    public enum ColorType {
        PRIMARY, SECONDARY
    }

    public enum ReturnType {
        SHARED, NEW
    }

    // http://paletton.com/#uid=70f0s0kllllaFw0g0qFqFg0w0aF
    private Color[][] colorSchemes = new Color[][] {
            new Color[] {
                    new Color(255 / 255.0f, 179 / 255.0f, 179 / 255.0f, 1),
                    new Color(124 / 255.0f, 177 / 255.0f, 171 / 255.0f, 1),
                    new Color(213 / 255.0f, 240 / 255.0f, 168 / 255.0f, 1)
            },
            new Color[] {
                    new Color(255 / 255.0f, 255 / 255.0f, 179 / 255.0f, 1),
                    new Color(210 / 255.0f, 147 / 255.0f, 187 / 255.0f, 1),
                    new Color(153 / 255.0f, 140 / 255.0f, 191 / 255.0f, 1)
            }
    };
    private Color tertiaryColor;
    private Color secondaryColor;
    private Color primaryColor;

    private ColorScheme() {
    }

    public static ColorScheme getInstance() {
        return instance;
    }

    public void reset() {
        // TODO: Is MathUtils.random legit?
        RandomUtils.shuffle(colorSchemes);
        Color[] colorScheme = RandomUtils.pickFrom(colorSchemes);
        RandomUtils.shuffle(colorScheme);
        primaryColor = new Color(colorScheme[0]);
        secondaryColor = new Color(colorScheme[1]);
        tertiaryColor = new Color(colorScheme[2]);

        // TODO: Remove this.
        ColorUtils.shade(primaryColor, BRIGHTNESS_CORRECTION);
        ColorUtils.shade(secondaryColor, BRIGHTNESS_CORRECTION + ((BRIGHTNESS_CORRECTION - 1) * 2));
        ColorUtils.shade(tertiaryColor, BRIGHTNESS_CORRECTION);
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

    /**
     * As of now, this is only allowed for level-complete particles.
     * @param returnType
     * @return
     */
    public Color getTertiaryColor(ReturnType returnType) {
        if (returnType == ReturnType.SHARED) {
            return tertiaryColor;
        }

        return new Color(tertiaryColor);
    }

    public Color getShadedPrimaryColor() {
        Color color = getPrimaryColor(ReturnType.NEW);
        ColorUtils.shade(color, RandomUtils.pickFromRange(MIN_SHADE_BRIGHTNESS, MAX_SHADE_BRIGHTNESS));

        return color;
    }

    public Color getShadedSecondaryColor() {
        Color color = getSecondaryColor(ReturnType.NEW);
        ColorUtils.shade(color, RandomUtils.pickFromRange(MIN_SHADE_BRIGHTNESS, MAX_SHADE_BRIGHTNESS));

        return color;
    }

    public Color getShadedTertiaryColor() {
        Color color = getTertiaryColor(ReturnType.NEW);
        ColorUtils.shade(color, RandomUtils.pickFromRange(MIN_SHADE_BRIGHTNESS, MAX_SHADE_BRIGHTNESS));

        return color;
    }
}

package com.tendersaucer.joe;

import com.badlogic.gdx.graphics.Color;
import com.tendersaucer.joe.util.ColorUtils;
import com.tendersaucer.joe.util.RandomUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 8/21/2016.
 */
public final class ColorScheme {

    private static final ColorScheme instance = new ColorScheme();
    private static final float MIN_SHADE_BRIGHTNESS = 0.91f;
    private static final float MAX_SHADE_BRIGHTNESS = 1.01f;

    public enum ColorType {
        PRIMARY, SECONDARY
    }

    public enum ReturnType {
        SHARED, NEW
    }

    // http://paletton.com/#uid=70f0s0kllllaFw0g0qFqFg0w0aF
    private static Color[][] colorSchemes = new Color[][] {
            new Color[] {
                    new Color(255 / 255.0f, 179 / 255.0f, 179 / 255.0f, 1),
                    new Color(124 / 255.0f, 177 / 255.0f, 171 / 255.0f, 1),
                    new Color(213 / 255.0f, 240 / 255.0f, 168 / 255.0f, 1)
            },
            new Color[] {
                    new Color(255 / 255.0f, 255 / 255.0f, 179 / 255.0f, 1),
                    new Color(210 / 255.0f, 147 / 255.0f, 187 / 255.0f, 1),
                    new Color(153 / 255.0f, 140 / 255.0f, 191 / 255.0f, 1)
            },
            new Color[] {
                    new Color(137 / 255.0f, 152 / 255.0f, 187 / 255.0f, 1),
                    new Color(255 / 255.0f, 241 / 255.0f, 179 / 255.0f, 1),
                    new Color(255 / 255.0f, 215 / 255.0f, 179 / 255.0f, 1)
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
        List<Color[]> colorBankList = Arrays.asList(colorSchemes);
        Collections.shuffle(colorBankList);
        colorSchemes = new Color[colorSchemes.length][colorSchemes[0].length];
        colorBankList.toArray(colorSchemes);

        Color[] colorScheme = colorSchemes[RandomUtils.pickIndex(colorSchemes)];
        primaryColor = colorScheme[RandomUtils.pickIndex(colorScheme)];
        secondaryColor = primaryColor;
        while (secondaryColor == primaryColor) {
            secondaryColor = colorScheme[RandomUtils.pickIndex(colorScheme)];
        }

        for (int i = 0; i <= 2; i++) {
            if (primaryColor != colorScheme[i] && secondaryColor != colorScheme[i]) {
                tertiaryColor = colorScheme[i];
                break;
            }
        }
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

    public ColorType isShadeOf(Color color) {
        return ColorUtils.dist(color, primaryColor) < ColorUtils.dist(color, secondaryColor) ?
                ColorType.PRIMARY : ColorType.SECONDARY;
    }


}

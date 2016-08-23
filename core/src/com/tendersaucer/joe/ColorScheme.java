package com.tendersaucer.joe;

import com.badlogic.gdx.graphics.Color;
import com.tendersaucer.joe.util.RandomUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Alex on 8/21/2016.
 */
public final class ColorScheme {

    private static final ColorScheme instance = new ColorScheme();
    private static final float MIN_SHADE_BRIGHTNESS = 0.95f;
    private static final float MAX_SHADE_BRIGHTNESS = 1.05f;

    public enum ColorType {
        PRIMARY, SECONDARY
    }

    public enum ReturnType {
        SHARED, NEW
    }

    // http://paletton.com/#uid=70f0s0kllllaFw0g0qFqFg0w0aF
    private static Color[] colorBank = new Color[] {
            new Color(121 / 255.0f, 173 / 255.0f, 173 / 255.0f, 1),
            new Color(255 / 255.0f, 229 / 255.0f, 179 / 255.0f, 1),
            new Color(255 / 255.0f, 183 / 255.0f, 179 / 255.0f, 1)
    };
    private Color secondaryColor;
    private Color primaryColor;

    private ColorScheme() {
    }

    public static ColorScheme getInstance() {
        return instance;
    }

    public void reset() {
        // TODO: Is MathUtils.random legit?
        List<Color> colorBankList = Arrays.asList(colorBank);
        Collections.shuffle(colorBankList);
        colorBank = new Color[colorBank.length];
        colorBankList.toArray(colorBank);

        primaryColor = colorBank[RandomUtils.pickIndex(colorBank)];
        secondaryColor = primaryColor;
        while (secondaryColor == primaryColor) {
            secondaryColor = colorBank[RandomUtils.pickIndex(colorBank)];
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

    public Color getShadedPrimaryColor() {
        Color color = getPrimaryColor(ReturnType.NEW);
        color.mul(RandomUtils.pickFromRange(MIN_SHADE_BRIGHTNESS, MAX_SHADE_BRIGHTNESS)).clamp();

        return color;
    }

    public Color getShadedSecondaryColor() {
        Color color = getSecondaryColor(ReturnType.NEW);
        color.mul(RandomUtils.pickFromRange(MIN_SHADE_BRIGHTNESS, MAX_SHADE_BRIGHTNESS)).clamp();

        return color;
    }

    public ColorType isShadeOf(Color color) {
        return dist(color, primaryColor) < dist(color, secondaryColor) ?
                ColorType.PRIMARY : ColorType.SECONDARY;
    }

    private double dist(Color x, Color y) {
        return Math.pow(x.r - y.r, 2) + Math.pow(x.g - y.g, 2) + Math.pow(x.b - y.b, 2);
    }
}

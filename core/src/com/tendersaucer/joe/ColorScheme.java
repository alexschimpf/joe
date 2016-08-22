package com.tendersaucer.joe;

import com.badlogic.gdx.graphics.Color;
import com.tendersaucer.joe.event.ILevelLoadBeginListener;
import com.tendersaucer.joe.level.ILevelLoadable;
import com.tendersaucer.joe.util.RandomUtils;

/**
 * Created by Alex on 8/21/2016.
 */
public final class ColorScheme implements ILevelLoadBeginListener {

    private static final ColorScheme instance = new ColorScheme();

    // Tetrad complimentary colors
    private static final Color[] COLOR_BANK = new Color[] {
        new Color(), new Color(), new Color(), new Color()
    };

    public enum ColorType {
        PRIMARY, SECONDARY
    }

    public enum ReturnType {
        SHARED, NEW
    }

    private Color secondaryColor;
    private Color primaryColor;

    private ColorScheme() {
    }

    public static ColorScheme getInstance() {
        return instance;
    }

    @Override
    public void onLevelLoadBegin(ILevelLoadable loadable) {
        primaryColor = COLOR_BANK[RandomUtils.pickIndex(COLOR_BANK)];
        secondaryColor = primaryColor;
        while (secondaryColor == primaryColor) {
            secondaryColor = COLOR_BANK[RandomUtils.pickIndex(COLOR_BANK)];
        }
    }

    public Color getPrimaryColor(ReturnType returnType) {
        if (returnType == ReturnType.SHARED) {
            return primaryColor;
        } else if (returnType == ReturnType.NEW) {
            return new Color(primaryColor);
        }

        return null;
    }

    public Color getSecondaryColor(ReturnType returnType) {
        if (returnType == ReturnType.SHARED) {
            return secondaryColor;
        } else if (returnType == ReturnType.NEW) {
            return new Color(secondaryColor);
        }

        return null;
    }

    public ColorType isShadeOf(Color color) {
        return dist(color, primaryColor) < dist(color, secondaryColor) ?
                ColorType.PRIMARY : ColorType.SECONDARY;
    }

    private double dist(Color x, Color y) {
        return Math.pow(x.r - y.r, 2) + Math.pow(x.g - y.g, 2) + Math.pow(x.b - y.b, 2);
    }
}

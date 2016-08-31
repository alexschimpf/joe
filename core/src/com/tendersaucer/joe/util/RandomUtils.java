package com.tendersaucer.joe.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.List;

/**
 * Utility functions for randomness
 * <p/>
 * Created by Alex on 4/28/2016.
 */
public final class RandomUtils {

    public static <T> T pick(T a, T b) {
        return MathUtils.random() < 0.5f ? a : b;
    }

    public static <T> T pickFrom(T... t) {
        return t[MathUtils.random(0, t.length - 1)];
    }

    public static <T> T pickFrom(List<T> t) {
        return t.get(MathUtils.random(0, t.size() - 1));
    }

    public static <T> T pickFrom(Array<T> t) {
        return t.get(MathUtils.random(0, t.size - 1));
    }

    /**
     * Returns random number from range
     * @param a inclusive
     * @param b EXCLUSIVE
     * @return
     */
    public static float pickFromRange(float a, float b) {
        return MathUtils.random(a, b);
    }

    /**
     * Returns random number from range
     * @param a inclusive
     * @param b inclusive
     * @return
     */
    public static int pickFromRange(int a, int b) {
        return MathUtils.random(a, b);
    }

    /**
     * Returns random number from range
     * @param range - x = inclusive, y = EXCLUSIVE
     * @return
     */
    public static float pickFromRange(Vector2 range) {
        return pickFromRange(range.x, range.y);
    }

    public static float pickFromSplitRange(Vector2 range, float split) {
        return pickFromSplitRange(range.x, range.y, split);
    }

    public static float pickFromSplitRange(float a, float b, float split) {
        if (split == 0) {
            return pickFromRange(a, b);
        }

        split = Math.abs(split);
        return pick(pickFromRange(a, -split), pickFromRange(split, b));
    }

    public static <T> void shuffle(T[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int swapIndex = RandomUtils.pickFromRange(0, i);
            T temp = array[i];
            array[i] = array[swapIndex];
            array[swapIndex] = temp;
        }
    }
}

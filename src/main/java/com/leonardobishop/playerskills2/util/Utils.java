package com.leonardobishop.playerskills2.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public final class Utils {
    private static final DecimalFormat percentageFormat = new DecimalFormat("#.#");

    static {
        percentageFormat.setRoundingMode(RoundingMode.HALF_UP);
    }

    private Utils() {
        // EMPTY
    }

    public static DecimalFormat getPercentageFormat() {
        return percentageFormat;
    }
}

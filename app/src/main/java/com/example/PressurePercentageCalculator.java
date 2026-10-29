package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * PressurePercentageCalculator calculates the exact pressure value for every
 * percentage from 1% through 100% in 1% increments.
 *
 * Formula:
 * Percentage pressure = Maximum gauge pressure * Percentage / 100
 */
public final class PressurePercentageCalculator {

    public static class PercentageItem {
        private final int percentage;
        private final double pressureValue;
        private final String formattedPressure;
        private final String unit;

        public PercentageItem(int percentage, double pressureValue, String formattedPressure, String unit) {
            this.percentage = percentage;
            this.pressureValue = pressureValue;
            this.formattedPressure = formattedPressure;
            this.unit = unit;
        }

        public int getPercentage() {
            return percentage;
        }

        public double getPressureValue() {
            return pressureValue;
        }

        public String getFormattedPressure() {
            return formattedPressure;
        }

        public String getUnit() {
            return unit;
        }

        public String getDisplayText() {
            return formattedPressure + " " + unit;
        }
    }

    private PressurePercentageCalculator() {
        // Utility class
    }

    /**
     * Calculates pressure for a specific percentage.
     */
    public static double calculatePressure(double maxPressure, int percentage) {
        if (maxPressure < 0 || percentage < 0) {
            return 0.0;
        }
        return (maxPressure * percentage) / 100.0;
    }

    /**
     * Generates all 100 percentages from 1% to 100% in increments of exactly 1%.
     */
    public static List<PercentageItem> calculateAll(double maxPressure, String unit) {
        List<PercentageItem> items = new ArrayList<>(100);
        for (int p = 1; p <= 100; p++) {
            double pressure = calculatePressure(maxPressure, p);
            String formatted = PressureConverter.formatValue(pressure);
            items.add(new PercentageItem(p, pressure, formatted, unit));
        }
        return items;
    }
}

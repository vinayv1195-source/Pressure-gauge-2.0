package com.example;

import java.util.ArrayList;
import java.util.List;

/**
 * GaugeDivisionCalculator calculates:
 * - Pressure per division = Maximum pressure / Number of divisions
 * - Scale graduation breakdown for each division mark from 1 to N.
 */
public final class GaugeDivisionCalculator {

    public static class DivisionItem {
        private final int divisionNumber;
        private final double pressureAtMark;
        private final String formattedPressure;
        private final String unit;

        public DivisionItem(int divisionNumber, double pressureAtMark, String formattedPressure, String unit) {
            this.divisionNumber = divisionNumber;
            this.pressureAtMark = pressureAtMark;
            this.formattedPressure = formattedPressure;
            this.unit = unit;
        }

        public int getDivisionNumber() {
            return divisionNumber;
        }

        public double getPressureAtMark() {
            return pressureAtMark;
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

    private GaugeDivisionCalculator() {
        // Utility class
    }

    /**
     * Calculates the step size / pressure per division.
     * Returns 0.0 if divisions <= 0 or maxPressure < 0 to avoid division by zero.
     */
    public static double calculatePressurePerDivision(double maxPressure, int divisions) {
        if (divisions <= 0 || maxPressure < 0) {
            return 0.0;
        }
        return maxPressure / divisions;
    }

    /**
     * Calculates the pressure at every division mark from 1 to divisions.
     */
    public static List<DivisionItem> calculateDivisionMarks(double maxPressure, int divisions, String unit) {
        List<DivisionItem> list = new ArrayList<>();
        if (divisions <= 0 || maxPressure < 0) {
            return list;
        }
        double step = calculatePressurePerDivision(maxPressure, divisions);
        for (int i = 1; i <= divisions; i++) {
            double markPressure = step * i;
            String formatted = PressureConverter.formatValue(markPressure);
            list.add(new DivisionItem(i, markPressure, formatted, unit));
        }
        return list;
    }
}

package com.example;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * PressureConverter handles bidirectional conversions between:
 * - kg/cm²
 * - bar
 * - PSI
 * - MPa
 *
 * Exact conversion factors specified:
 * - 1 kg/cm² = 0.980665 bar
 * - 1 kg/cm² = 14.2233 PSI
 * - 1 kg/cm² = 0.0980665 MPa
 */
public final class PressureConverter {

    public static final String UNIT_KG_CM2 = "kg/cm²";
    public static final String UNIT_BAR = "bar";
    public static final String UNIT_PSI = "PSI";
    public static final String UNIT_MPA = "MPa";

    public static final double BAR_PER_KG_CM2 = 0.980665;
    public static final double PSI_PER_KG_CM2 = 14.2233;
    public static final double MPA_PER_KG_CM2 = 0.0980665;

    private PressureConverter() {
        // Utility class
    }

    /**
     * Converts a given pressure value in a specified unit to base unit (kg/cm²).
     */
    public static double toKgCm2(double value, String unit) {
        if (unit == null) return value;
        switch (unit) {
            case UNIT_KG_CM2:
                return value;
            case UNIT_BAR:
                return value / BAR_PER_KG_CM2;
            case UNIT_PSI:
                return value / PSI_PER_KG_CM2;
            case UNIT_MPA:
                return value / MPA_PER_KG_CM2;
            default:
                return value;
        }
    }

    /**
     * Converts a value in base unit (kg/cm²) to the target unit.
     */
    public static double fromKgCm2(double kgCm2Value, String targetUnit) {
        if (targetUnit == null) return kgCm2Value;
        switch (targetUnit) {
            case UNIT_KG_CM2:
                return kgCm2Value;
            case UNIT_BAR:
                return kgCm2Value * BAR_PER_KG_CM2;
            case UNIT_PSI:
                return kgCm2Value * PSI_PER_KG_CM2;
            case UNIT_MPA:
                return kgCm2Value * MPA_PER_KG_CM2;
            default:
                return kgCm2Value;
        }
    }

    /**
     * Bidirectional conversion from one unit to another.
     */
    public static double convert(double value, String fromUnit, String toUnit) {
        if (fromUnit == null || toUnit == null || fromUnit.equals(toUnit)) {
            return value;
        }
        double baseValue = toKgCm2(value, fromUnit);
        return fromKgCm2(baseValue, toUnit);
    }

    /**
     * Formats numerical pressure value, avoiding unnecessary trailing zeros
     * while retaining precision up to 5 decimals.
     */
    public static String formatValue(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "0";
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#,##0.#####", symbols);
        df.setDecimalSeparatorAlwaysShown(false);
        return df.format(value);
    }

    /**
     * Formats numerical pressure value with custom max decimal places.
     */
    public static String formatValue(double value, int maxDecimals) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "0";
        }
        StringBuilder pattern = new StringBuilder("#,##0.");
        for (int i = 0; i < maxDecimals; i++) {
            pattern.append("#");
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat(pattern.toString(), symbols);
        df.setDecimalSeparatorAlwaysShown(false);
        return df.format(value);
    }
}

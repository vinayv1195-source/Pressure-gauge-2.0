package com.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {34})
public class PressureGaugeTest {

    @Test
    public void testAppName() {
        Context context = ApplicationProvider.getApplicationContext();
        String appName = context.getString(R.string.app_name);
        assertEquals("Pressure Gauge Calculator", appName);
    }

    @Test
    public void testPressureConversions() {
        // 1 kg/cm² = 0.980665 bar
        double barVal = PressureConverter.convert(1.0, PressureConverter.UNIT_KG_CM2, PressureConverter.UNIT_BAR);
        assertEquals(0.980665, barVal, 0.00001);

        // 1 kg/cm² = 14.2233 PSI
        double psiVal = PressureConverter.convert(1.0, PressureConverter.UNIT_KG_CM2, PressureConverter.UNIT_PSI);
        assertEquals(14.2233, psiVal, 0.001);

        // 1 kg/cm² = 0.0980665 MPa
        double mpaVal = PressureConverter.convert(1.0, PressureConverter.UNIT_KG_CM2, PressureConverter.UNIT_MPA);
        assertEquals(0.0980665, mpaVal, 0.00001);

        // Bidirectional test: 98.0665 bar to kg/cm² should be 100
        double backToKg = PressureConverter.convert(98.0665, PressureConverter.UNIT_BAR, PressureConverter.UNIT_KG_CM2);
        assertEquals(100.0, backToKg, 0.001);
    }

    @Test
    public void testFullPercentageCalculator1To100() {
        List<PressurePercentageCalculator.PercentageItem> list =
                PressurePercentageCalculator.calculateAll(100.0, PressureConverter.UNIT_KG_CM2);

        // Must display every percentage from 1% to 100% without skipping
        assertEquals(100, list.size());
        assertEquals(1, list.get(0).getPercentage());
        assertEquals(1.0, list.get(0).getPressureValue(), 0.001);

        assertEquals(50, list.get(49).getPercentage());
        assertEquals(50.0, list.get(49).getPressureValue(), 0.001);

        assertEquals(100, list.get(99).getPercentage());
        assertEquals(100.0, list.get(99).getPressureValue(), 0.001);

        // Example from prompt: 60 kg/cm²
        List<PressurePercentageCalculator.PercentageItem> list60 =
                PressurePercentageCalculator.calculateAll(60.0, PressureConverter.UNIT_KG_CM2);
        assertEquals(0.6, list60.get(0).getPressureValue(), 0.001);  // 1% = 0.6
        assertEquals(1.2, list60.get(1).getPressureValue(), 0.001);  // 2% = 1.2
        assertEquals(3.0, list60.get(4).getPressureValue(), 0.001);  // 5% = 3.0
        assertEquals(6.0, list60.get(9).getPressureValue(), 0.001);  // 10% = 6.0
        assertEquals(30.0, list60.get(49).getPressureValue(), 0.001); // 50% = 30.0
        assertEquals(60.0, list60.get(99).getPressureValue(), 0.001); // 100% = 60.0
    }

    @Test
    public void testDivisionCalculator() {
        // Step size for 100 / 10
        double step = GaugeDivisionCalculator.calculatePressurePerDivision(100.0, 10);
        assertEquals(10.0, step, 0.001);

        List<GaugeDivisionCalculator.DivisionItem> marks =
                GaugeDivisionCalculator.calculateDivisionMarks(100.0, 10, PressureConverter.UNIT_KG_CM2);
        assertEquals(10, marks.size());
        assertEquals(10.0, marks.get(0).getPressureAtMark(), 0.001);
        assertEquals(100.0, marks.get(9).getPressureAtMark(), 0.001);

        // Zero division protection
        double zeroStep = GaugeDivisionCalculator.calculatePressurePerDivision(100.0, 0);
        assertEquals(0.0, zeroStep, 0.0001);
    }
}

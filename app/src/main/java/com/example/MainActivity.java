package com.example;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.databinding.ActivityMainBinding;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String currentUnit = PressureConverter.UNIT_KG_CM2;
    private double currentPressure = 100.0;
    private int selectedPercentage = 50;

    private PercentageAdapter percentageAdapter;
    private DivisionAdapter divisionAdapter;
    private boolean isUpdatingFromSync = false;
    private boolean isDivisionMarksVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupAdapters();
        setupUnitButtons();
        setupInputWatchers();
        setupActionButtons();
        setupPercentageQuickJumps();
        setupDivisionCalculator();

        // Initial calculation with default values
        calculateAndRefreshAll();
    }

    private void setupAdapters() {
        // Setup Percentage RecyclerView
        percentageAdapter = new PercentageAdapter();
        binding.rvPercentages.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPercentages.setAdapter(percentageAdapter);
        binding.rvPercentages.setHasFixedSize(true);

        percentageAdapter.setOnPercentageSelectedListener(item -> {
            selectedPercentage = item.getPercentage();
            updatePercentageBanner(item.getPercentage(), item.getPressureValue(), item.getUnit());
        });

        // Setup Division Marks RecyclerView
        divisionAdapter = new DivisionAdapter();
        binding.rvDivisionMarks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDivisionMarks.setAdapter(divisionAdapter);
    }

    private void setupUnitButtons() {
        binding.btnUnitKgCm2.setOnClickListener(v -> selectUnit(PressureConverter.UNIT_KG_CM2));
        binding.btnUnitBar.setOnClickListener(v -> selectUnit(PressureConverter.UNIT_BAR));
        binding.btnUnitPsi.setOnClickListener(v -> selectUnit(PressureConverter.UNIT_PSI));
        binding.btnUnitMpa.setOnClickListener(v -> selectUnit(PressureConverter.UNIT_MPA));
    }

    private void selectUnit(String unit) {
        if (currentUnit.equals(unit)) return;
        currentUnit = unit;
        updateUnitButtonStyles();
        calculateAndRefreshAll();
    }

    private void updateUnitButtonStyles() {
        setUnitButtonStyle(binding.btnUnitKgCm2, currentUnit.equals(PressureConverter.UNIT_KG_CM2));
        setUnitButtonStyle(binding.btnUnitBar, currentUnit.equals(PressureConverter.UNIT_BAR));
        setUnitButtonStyle(binding.btnUnitPsi, currentUnit.equals(PressureConverter.UNIT_PSI));
        setUnitButtonStyle(binding.btnUnitMpa, currentUnit.equals(PressureConverter.UNIT_MPA));
    }

    private void setUnitButtonStyle(Button button, boolean isSelected) {
        if (isSelected) {
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_unit_button_selected));
            button.setTextColor(ContextCompat.getColor(this, R.color.unit_selected_text));
        } else {
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_unit_button_unselected));
            button.setTextColor(ContextCompat.getColor(this, R.color.unit_unselected_text));
        }
    }

    private void setupInputWatchers() {
        binding.etPressureInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdatingFromSync) return;
                parsePressureAndCalculate();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void parsePressureAndCalculate() {
        String inputStr = binding.etPressureInput.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            currentPressure = 0.0;
            binding.tvInputError.setVisibility(View.GONE);
            updateConversionCards(0.0);
            refreshPercentageList(0.0);
            updateDivisionCalculatorValues(0.0);
            return;
        }

        try {
            double value = Double.parseDouble(inputStr);
            if (value < 0) {
                binding.tvInputError.setText(R.string.error_negative_value);
                binding.tvInputError.setVisibility(View.VISIBLE);
                currentPressure = 0.0;
            } else {
                binding.tvInputError.setVisibility(View.GONE);
                currentPressure = value;
            }
            updateConversionCards(currentPressure);
            refreshPercentageList(currentPressure);
            updateDivisionCalculatorValues(currentPressure);
        } catch (NumberFormatException e) {
            binding.tvInputError.setText(R.string.error_invalid_input);
            binding.tvInputError.setVisibility(View.VISIBLE);
        }
    }

    private void updateConversionCards(double value) {
        // Convert to base kg/cm² first, then to all target units
        double valKgCm2 = PressureConverter.convert(value, currentUnit, PressureConverter.UNIT_KG_CM2);
        double valBar = PressureConverter.convert(value, currentUnit, PressureConverter.UNIT_BAR);
        double valPsi = PressureConverter.convert(value, currentUnit, PressureConverter.UNIT_PSI);
        double valMpa = PressureConverter.convert(value, currentUnit, PressureConverter.UNIT_MPA);

        binding.tvResultKgCm2.setText(PressureConverter.formatValue(valKgCm2));
        binding.tvResultBar.setText(PressureConverter.formatValue(valBar));
        binding.tvResultPsi.setText(PressureConverter.formatValue(valPsi));
        binding.tvResultMpa.setText(PressureConverter.formatValue(valMpa));

        // Highlight the card that matches the currently selected base unit
        highlightResultCard(binding.cardResultKgCm2, binding.tvResultKgCm2, currentUnit.equals(PressureConverter.UNIT_KG_CM2));
        highlightResultCard(binding.cardResultBar, binding.tvResultBar, currentUnit.equals(PressureConverter.UNIT_BAR));
        highlightResultCard(binding.cardResultPsi, binding.tvResultPsi, currentUnit.equals(PressureConverter.UNIT_PSI));
        highlightResultCard(binding.cardResultMpa, binding.tvResultMpa, currentUnit.equals(PressureConverter.UNIT_MPA));
    }

    private void highlightResultCard(View cardView, android.widget.TextView tvValue, boolean isBaseUnit) {
        if (isBaseUnit) {
            cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_card_highlight));
            tvValue.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        } else {
            cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_card_industrial));
            tvValue.setTextColor(ContextCompat.getColor(this, R.color.text_blue_result));
        }
    }

    private void refreshPercentageList(double maxPressure) {
        List<PressurePercentageCalculator.PercentageItem> items =
                PressurePercentageCalculator.calculateAll(maxPressure, currentUnit);
        percentageAdapter.setItems(items);
        percentageAdapter.setSelectedPercentage(selectedPercentage);

        double selectedPressure = PressurePercentageCalculator.calculatePressure(maxPressure, selectedPercentage);
        updatePercentageBanner(selectedPercentage, selectedPressure, currentUnit);
    }

    private void updatePercentageBanner(int percent, double pressure, String unit) {
        binding.tvBannerPercent.setText(percent + "%");
        binding.tvBannerPressureValue.setText(PressureConverter.formatValue(pressure) + " " + unit);
        binding.tvBannerFormula.setText(PressureConverter.formatValue(currentPressure) + " × " + percent + " ÷ 100");
    }

    private void setupPercentageQuickJumps() {
        binding.btnQuickJump.setOnClickListener(v -> {
            String query = binding.etSearchPercent.getText().toString().trim();
            if (!query.isEmpty()) {
                try {
                    int p = Integer.parseInt(query);
                    if (p >= 1 && p <= 100) {
                        jumpToPercentage(p);
                    } else {
                        Toast.makeText(this, "Enter percentage between 1 and 100", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException ignored) {}
            }
        });

        binding.chip10.setOnClickListener(v -> jumpToPercentage(10));
        binding.chip25.setOnClickListener(v -> jumpToPercentage(25));
        binding.chip33.setOnClickListener(v -> jumpToPercentage(33));
        binding.chip50.setOnClickListener(v -> jumpToPercentage(50));
        binding.chip75.setOnClickListener(v -> jumpToPercentage(75));
        binding.chip100.setOnClickListener(v -> jumpToPercentage(100));
    }

    private void jumpToPercentage(int percent) {
        selectedPercentage = percent;
        percentageAdapter.setSelectedPercentage(percent);

        int pos = percentageAdapter.findPositionForPercentage(percent);
        if (pos != -1) {
            binding.rvPercentages.smoothScrollToPosition(pos);
        }

        double pressure = PressurePercentageCalculator.calculatePressure(currentPressure, percent);
        updatePercentageBanner(percent, pressure, currentUnit);
    }

    private void setupDivisionCalculator() {
        binding.etDivMaxPressure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateDivisions();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etDivisionsCount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateDivisions();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.btnPresetDiv5.setOnClickListener(v -> setDivisionPreset(5));
        binding.btnPresetDiv10.setOnClickListener(v -> setDivisionPreset(10));
        binding.btnPresetDiv20.setOnClickListener(v -> setDivisionPreset(20));
        binding.btnPresetDiv50.setOnClickListener(v -> setDivisionPreset(50));
        binding.btnPresetDiv100.setOnClickListener(v -> setDivisionPreset(100));

        binding.btnToggleDivisions.setOnClickListener(v -> {
            isDivisionMarksVisible = !isDivisionMarksVisible;
            if (isDivisionMarksVisible) {
                binding.rvDivisionMarks.setVisibility(View.VISIBLE);
                binding.btnToggleDivisions.setText(R.string.hide_divisions);
            } else {
                binding.rvDivisionMarks.setVisibility(View.GONE);
                binding.btnToggleDivisions.setText(R.string.show_all_divisions);
            }
        });
    }

    private void setDivisionPreset(int divisions) {
        binding.etDivisionsCount.setText(String.valueOf(divisions));
    }

    private void updateDivisionCalculatorValues(double maxPressure) {
        isUpdatingFromSync = true;
        binding.etDivMaxPressure.setText(PressureConverter.formatValue(maxPressure));
        isUpdatingFromSync = false;
        calculateDivisions();
    }

    private void calculateDivisions() {
        String pStr = binding.etDivMaxPressure.getText().toString().trim();
        String dStr = binding.etDivisionsCount.getText().toString().trim();

        double maxP = 0.0;
        int divs = 1;

        if (!pStr.isEmpty() && !pStr.equals(".")) {
            try {
                maxP = Double.parseDouble(pStr);
                if (maxP < 0) maxP = 0.0;
            } catch (NumberFormatException ignored) {}
        }

        if (!dStr.isEmpty()) {
            try {
                divs = Integer.parseInt(dStr);
                if (divs <= 0) divs = 1;
            } catch (NumberFormatException ignored) {}
        }

        double step = GaugeDivisionCalculator.calculatePressurePerDivision(maxP, divs);
        binding.tvPressurePerDivision.setText(PressureConverter.formatValue(step) + " " + currentUnit + " / division");
        binding.tvDivisionFormulaDesc.setText("Formula: " + PressureConverter.formatValue(maxP) + " ÷ " + divs + " = " + PressureConverter.formatValue(step));

        List<GaugeDivisionCalculator.DivisionItem> marks =
                GaugeDivisionCalculator.calculateDivisionMarks(maxP, divs, currentUnit);
        divisionAdapter.setItems(marks);
    }

    private void setupActionButtons() {
        binding.btnClear.setOnClickListener(v -> {
            isUpdatingFromSync = true;
            binding.etPressureInput.setText("");
            binding.etDivMaxPressure.setText("");
            binding.etSearchPercent.setText("");
            isUpdatingFromSync = false;

            currentPressure = 0.0;
            parsePressureAndCalculate();
            Toast.makeText(this, "Inputs cleared", Toast.LENGTH_SHORT).show();
        });

        binding.btnReset.setOnClickListener(v -> {
            isUpdatingFromSync = true;
            currentUnit = PressureConverter.UNIT_KG_CM2;
            currentPressure = 100.0;
            selectedPercentage = 50;

            binding.etPressureInput.setText("100");
            binding.etDivMaxPressure.setText("100");
            binding.etDivisionsCount.setText("10");
            binding.etSearchPercent.setText("");
            isUpdatingFromSync = false;

            updateUnitButtonStyles();
            calculateAndRefreshAll();
            Toast.makeText(this, "Reset to standard 100 kg/cm²", Toast.LENGTH_SHORT).show();
        });
    }

    private void calculateAndRefreshAll() {
        updateUnitButtonStyles();
        parsePressureAndCalculate();
        calculateDivisions();
    }
}

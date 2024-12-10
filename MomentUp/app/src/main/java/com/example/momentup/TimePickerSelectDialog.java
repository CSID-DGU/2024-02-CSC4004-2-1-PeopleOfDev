package com.example.momentup;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;

public class TimePickerSelectDialog extends Dialog {
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private NumberPicker amPmPicker;
    private TextView selectedTimeText;
    private Button cancelButton;
    private Button confirmButton;
    private View.OnClickListener confirmClickListener;

    public TimePickerSelectDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_time_picker);

        // Initialize views
        initViews();
        setupPickers();
        setupButtons();
    }

    private void initViews() {
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        amPmPicker = findViewById(R.id.amPmPicker);
        selectedTimeText = findViewById(R.id.selected_time_text);
        cancelButton = findViewById(R.id.cancel_button);
        confirmButton = findViewById(R.id.confirm_button);
    }

    private void setupPickers() {
        // Set up hour picker (1-12)
        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(12);
        hourPicker.setValue(5); // Default value

        // Set up minute picker (0-59)
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(30); // Default value

        // Set up AM/PM picker
        String[] amPmValues = new String[]{"AM", "PM"};
        amPmPicker.setMinValue(0);
        amPmPicker.setMaxValue(1);
        amPmPicker.setDisplayedValues(amPmValues);

        NumberPicker.OnValueChangeListener valueChangeListener = (picker, oldVal, newVal) -> updateTimeDisplay();

        hourPicker.setOnValueChangedListener(valueChangeListener);
        minutePicker.setOnValueChangedListener(valueChangeListener);
        amPmPicker.setOnValueChangedListener(valueChangeListener);
    }

    private void setupButtons() {
        cancelButton.setOnClickListener(v -> dismiss());

        // 기본 confirm 리스너 설정
        confirmButton.setOnClickListener(v -> {
            if (confirmClickListener != null) {
                confirmClickListener.onClick(v);
            }
        });
    }

    private void updateTimeDisplay() {
        String hour = String.format("%02d", hourPicker.getValue());
        String minute = String.format("%02d", minutePicker.getValue());
        String amPm = amPmPicker.getValue() == 0 ? "AM" : "PM";
        selectedTimeText.setText(hour + ":" + minute + " " + amPm);
    }

    public void setConfirmClickListener(View.OnClickListener listener) {
        this.confirmClickListener = listener;
        if (confirmButton != null) {
            confirmButton.setOnClickListener(listener);
        }
    }

    public int getHour() {
        return hourPicker.getValue();
    }

    public int getMinute() {
        return minutePicker.getValue();
    }

    public boolean isAm() {
        return amPmPicker.getValue() == 0;
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.copyFrom(window.getAttributes());
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }
}
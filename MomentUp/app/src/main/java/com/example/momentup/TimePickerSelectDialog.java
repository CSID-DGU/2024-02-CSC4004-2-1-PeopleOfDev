package com.example.momentup;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

public class TimePickerSelectDialog extends Dialog {

    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private Button confirmButton, cancelButton;
    private OnTimeSelectedListener listener;

    public TimePickerSelectDialog(Context context, OnTimeSelectedListener listener) {
        super(context);
        setContentView(R.layout.dialog_time_picker_select);
        this.listener = listener;
        hourPicker = findViewById(R.id.hourPicker);
        minutePicker = findViewById(R.id.minutePicker);
        confirmButton = findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_button);

        // 默认值
        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(24);
        hourPicker.setValue(8);

        minutePicker.setValue(30);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int hour = hourPicker.getValue();
                    int minute = minutePicker.getValue();
                    listener.onTimeSelected(hour, minute);
                }
                dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public interface OnTimeSelectedListener {
        void onTimeSelected(int hour, int minute);
    }
}

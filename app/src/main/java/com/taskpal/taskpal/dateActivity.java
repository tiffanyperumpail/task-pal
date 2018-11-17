package com.taskpal.taskpal;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;

public class dateActivity extends AppCompatActivity {

    private Button doneButton;
    private Button swapButton;
    private boolean date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        doneButton = findViewById(R.id.doneButton);
        swapButton = findViewById(R.id.swapButton);
        date = true;

        final DatePicker datePicker = findViewById(R.id.datePicker);
        final TimePicker timePicker = findViewById(R.id.timePicker);

        swapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (date) {
                    datePicker.setVisibility(View.INVISIBLE);
                    timePicker.setVisibility(View.VISIBLE);
                    swapButton.setText(R.string.date_button_text);
                } else {
                    datePicker.setVisibility(View.VISIBLE);
                    timePicker.setVisibility(View.INVISIBLE);
                    swapButton.setText(R.string.time_button_text);
                }
                date = !date;
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();

                Intent intent = new Intent();
                intent.putExtra("day", format(day));
                intent.putExtra("month", format(month));
                intent.putExtra("year", Integer.toString(year));
                intent.putExtra("hour", format(hour));
                intent.putExtra("min", format(min));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    private String format(int data) {
        String raw = Integer.toString(data);
        if (raw.length() < 2)
            raw = "0" + raw;
        return raw;
    }
}

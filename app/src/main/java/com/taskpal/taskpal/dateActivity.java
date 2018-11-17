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
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

public class dateActivity extends AppCompatActivity {

    private Button doneButton;
    private Button swapButton;
    private boolean date;
    private int completionTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        doneButton = findViewById(R.id.doneButton);
        swapButton = findViewById(R.id.swapButton);
        date = true;
        Intent intent = getIntent();
        String comp = intent.getStringExtra("completion");
        if (comp == null || comp.equals("")) {
            completionTime = 120;
        } else {
            completionTime = Integer.parseInt(comp);
        }
        String day = intent.getStringExtra("day");
        String month = intent.getStringExtra("month");
        String year = intent.getStringExtra("year");
        String hour = intent.getStringExtra("hour");
        String min = intent.getStringExtra("min");

        final DatePicker datePicker = findViewById(R.id.datePicker);
        final TimePicker timePicker = findViewById(R.id.timePicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        if (day != null && !day.equals("")) {
            datePicker.updateDate(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day));
            timePicker.setCurrentHour(Integer.parseInt(hour));
            timePicker.setCurrentMinute(Integer.parseInt(min));
        }

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
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                int hour = timePicker.getCurrentHour();
                int min = timePicker.getCurrentMinute();

                final Calendar c1 = Calendar.getInstance();
                c1.add(Calendar.MINUTE, completionTime);
                final Calendar c2 = Calendar.getInstance();
                c2.set(Calendar.MONTH, month-1);
                c2.set(Calendar.DAY_OF_MONTH, day);
                c2.set(Calendar.YEAR, year);
                c2.set(Calendar.HOUR_OF_DAY, hour);
                c2.set(Calendar.MINUTE, min);

                long time1 = c1.getTimeInMillis();
                long time2 = c2.getTimeInMillis();
                if (time1 > time2) {
                    Toast.makeText(getApplicationContext(), getString(R.string.invalid_date), Toast.LENGTH_SHORT).show();
                    return;
                }

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

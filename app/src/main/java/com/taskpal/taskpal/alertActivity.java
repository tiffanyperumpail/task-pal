package com.taskpal.taskpal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;

public class alertActivity extends AppCompatActivity {

    private RadioButton fifteen_mins_Button;
    private RadioButton thirty_mins_Button;
    private RadioButton hour_Button;
    private RadioButton two_hours_Button;
    private RadioButton day_before_Button;
    private RadioButton two_day_before_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        fifteen_mins_Button = findViewById(R.id.fifteen_mins_Button);
        thirty_mins_Button = findViewById(R.id.thirty_mins_Button);
        hour_Button = findViewById(R.id.hour_Button);
        two_hours_Button = findViewById(R.id.two_hours_button);
        day_before_Button = findViewById(R.id.day_before_Button);
        two_day_before_Button = findViewById(R.id.two_day_before_Button2);
    }
}
